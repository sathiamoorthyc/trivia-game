package org.trivia.game.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.trivia.game.model.SubmitAnswerRequestResponse;
import org.trivia.game.model.TriviaDto;
import org.trivia.game.service.TriviaService;

import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@SpringBootTest(classes = {GameController.class})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@EnableWebMvc
public class GameControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TriviaService triviaService;

    @Test
    public void shouldGetRandomQuestion() throws Exception {

        // Given
        int triviaId = 1;
        String[] possibleAnswers = {"Chile", "Argentina", "Brazil", "Paraguay"};
        String question = "Which soccer team won the Copa America 2015 Championship ?";
        TriviaDto triviaDto = new TriviaDto(triviaId, question,possibleAnswers);

        // When
        when(triviaService.startGame()).thenReturn(triviaDto);

        // Then
        mockMvc.perform(post("/trivia/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.triviaId").exists())
                .andExpect(jsonPath("$.question").exists())
                .andExpect(jsonPath("$.possibleAnswers").exists())
                .andExpect(jsonPath("$.triviaId", is(triviaId)))
                .andExpect(jsonPath("$.question", is(question)))
                .andExpect(jsonPath("$.possibleAnswers", contains(possibleAnswers)));
    }

    @Test
    public void shouldSubmitAnswer_with_correct_answer() throws Exception {
        // Given
        int triviaId = 1;
        String request = """ 
                             {
                             "answer": "Chile"
                             }
                           """;

        SubmitAnswerRequestResponse response = SubmitAnswerRequestResponse.builder()
                .result("right!")
                .build();

        // When
        when(triviaService.submitAnswer(anyInt(), any())).thenReturn(response);

        // Then
        mockMvc.perform(put("/trivia/reply/"+ triviaId )
                        .content(request)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").doesNotExist())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result", is("right!")));
    }

    @Test
    public void shouldSubmitAnswer_with_wrong_answer() throws Exception {
        // Given
        int triviaId = 1;
        String request = """ 
                             {
                             "answer": "Chile"
                             }
                           """;

        SubmitAnswerRequestResponse response = SubmitAnswerRequestResponse.builder()
                .result("wrong!")
                .build();

        // When
        when(triviaService.submitAnswer(anyInt(), any())).thenReturn(response);

        // Then
        mockMvc.perform(put("/trivia/reply/"+ triviaId )
                        .content(request)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.answer").doesNotExist())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result", is("wrong!")));
    }

}
