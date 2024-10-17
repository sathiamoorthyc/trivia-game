package org.trivia.game.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.trivia.game.model.SubmitAnswerRequestResponse;
import org.trivia.game.model.Trivia;
import org.trivia.game.model.TriviaDto;
import org.trivia.game.repository.TriviaRepository;

import java.util.Optional;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TriviaGameIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    TriviaRepository triviaRepository;

    @Test
    public void startGame() throws Exception {
        // Given
        Trivia trivia = buildTrivia();

        // When
        when(triviaRepository.save(any())).thenReturn(trivia);

        // Then
        JsonPathResultMatchers jsonIdPath = jsonPath("$.triviaId");
        mockMvc.perform(post("/trivia/start")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonIdPath.exists())
                .andExpect(jsonPath("$.question").exists())
                .andExpect(jsonPath("$.possibleAnswers").exists());

       }

    @Test
    public void submitAnswer() throws Exception {

        // Given
        Optional<Trivia> optionalTrivia = Optional.of(buildTrivia());

        // When
        when(triviaRepository.findById(anyInt())).thenReturn(optionalTrivia);

        Trivia trivia = optionalTrivia.get();

        SubmitAnswerRequestResponse request = SubmitAnswerRequestResponse.builder().answer(trivia.getCorrectAnswer()).build();

        mockMvc.perform(put("/trivia/reply/" + trivia.getTriviaId())
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").doesNotExist())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result", is("right!")));
    }


    /**
     * The below test case runs without any mocks.
     * This is commented, because 'TriviaRepository' is mocked to keep other tests simple.
     * If you want to test without mock, replace the 'MockBean' annotation with 'Autowired for 'TriviaRepository' bean
     */
    //@Test
    public void submitAnswer_Without_Mock() throws Exception {

        JsonPathResultMatchers jsonIdPath = jsonPath("$.triviaId");
        MvcResult mvcResult = mockMvc.perform(post("/trivia/start")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonIdPath.exists())
                .andExpect(jsonPath("$.question").exists())
                .andExpect(jsonPath("$.possibleAnswers").exists())
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        TriviaDto triviaDto = objectMapper.readValue(responseContent, TriviaDto.class);
        Integer triviaId = triviaDto.triviaId();
        Optional<Trivia> optionalTrivia = triviaRepository.findById(triviaId);

        if(optionalTrivia.isPresent()) {
            Trivia trivia = optionalTrivia.get();

            SubmitAnswerRequestResponse request = SubmitAnswerRequestResponse.builder().answer(trivia.getCorrectAnswer()).build();

            mockMvc.perform(put("/trivia/reply/" + triviaId)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(APPLICATION_JSON)
                            .accept(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.answer").doesNotExist())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result", is("right!")));
        }
    }

    @Test
    public void submit_IncorrectAnswer() throws Exception {

       // Given
        Optional<Trivia> optionalTrivia = Optional.of(buildTrivia());

        SubmitAnswerRequestResponse request = SubmitAnswerRequestResponse.builder().answer("Manchester").build();

        // When
        when(triviaRepository.findById(anyInt())).thenReturn(optionalTrivia);

        // Then
        mockMvc.perform(put("/trivia/reply/" + 1)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.answer").doesNotExist())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result", is("wrong!")));
    }

    private static Trivia buildTrivia() {
        return Trivia.builder().triviaId(1).answerAttempts(0)
                .question("What is the capital of England?")
                .possibleAnswers(new String[]{"London", "Manchester", "Birmingham"})
                .correctAnswer("London")
                .build();
    }

    @Test
    public void submitAnswer_Beyond_Max_Attempt() throws Exception {

        // Given
        Optional<Trivia> optionalTrivia = Optional.of(Trivia.builder().triviaId(1).answerAttempts(3)
                .question("What is the capital of England?")
                .possibleAnswers(new String[]{"London", "Manchester", "Birmingham"})
                .correctAnswer("London")
                .build());

        SubmitAnswerRequestResponse request = SubmitAnswerRequestResponse.builder().answer("London").build();

        // When
        when(triviaRepository.findById(anyInt())).thenReturn(optionalTrivia);

        // Then
        mockMvc.perform(put("/trivia/reply/" + 1)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.answer").doesNotExist())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result", is("Max attempts reached!")));
    }
}
