package org.trivia.game.service;

import org.apache.coyote.BadRequestException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.trivia.game.model.SubmitAnswerRequestResponse;
import org.trivia.game.model.Trivia;
import org.trivia.game.model.TriviaDto;
import org.trivia.game.repository.TriviaRepository;
import org.trivia.game.utils.TestHelper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.trivia.game.service.TriviaService.OPEN_TDB_URL;

@ExtendWith(MockitoExtension.class)
class TriviaServiceTest {

    @Mock
    private TriviaRepository triviaRepository;

    @InjectMocks
    private TriviaService triviaService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void startGame(){

        // Given
        String openTDbResponse = """
                {
                          "response_code": 0,
                          "results": [{
                            "category": "Sports",
                            "type": "multiple",
                            "difficulty": "medium",
                            "question": "What is the capital of England?",
                            "correct_answer": "London",
                            "incorrect_answers": ["Manchester", "Birmingham", "London"]
                          }]
                        }""";

        // When
        when(restTemplate.getForEntity(OPEN_TDB_URL, String.class)).thenReturn(new ResponseEntity<>(openTDbResponse, HttpStatus.OK));
        Trivia expectedTrivia = TestHelper.buildTrivia();
        when(triviaRepository.save(any())).thenReturn(expectedTrivia);

        TriviaDto actualTriviaDto = triviaService.startGame();

        // Then
        assertNotNull(actualTriviaDto);
        assertEquals(expectedTrivia.getQuestion(), actualTriviaDto.question());
        assertEquals(expectedTrivia.getPossibleAnswers(), actualTriviaDto.possibleAnswers());

    }

    @Test
    public void submitCorrectAnswer_with_incorrect_id(){
        // Given
        int triviaId = 1;
        SubmitAnswerRequestResponse requestResponse = SubmitAnswerRequestResponse.builder().answer("London").build();

        // When
        when(triviaRepository.findById(triviaId)).thenReturn(Optional.empty());
        BadRequestException exception = Assertions.assertThrows(BadRequestException.class, () -> {
            triviaService.submitAnswer(triviaId, requestResponse);
        }, "BadRequestException error was expected");

        // Then
        assertNotNull(exception);
        assertEquals("No such question!", exception.getMessage());
    }

    @Test
    public void submitCorrectAnswer() throws BadRequestException {
        // Given
        int triviaId = 1;
        SubmitAnswerRequestResponse requestResponse = SubmitAnswerRequestResponse.builder().answer("London").build();

        // When
        when(triviaRepository.findById(triviaId)).thenReturn(Optional.ofNullable(TestHelper.buildTrivia()));
        SubmitAnswerRequestResponse response = triviaService.submitAnswer(triviaId, requestResponse);

        // Then
        assertNotNull(response);
        String result = response.getResult();
        assertNotNull(result);
        assertEquals("right!", result);
    }

    @Test
    public void submitWrongAnswer() throws BadRequestException {
        // Given
        int triviaId = 1;
        SubmitAnswerRequestResponse requestResponse = SubmitAnswerRequestResponse.builder().answer("Manchester").build();

        // When
        when(triviaRepository.findById(triviaId)).thenReturn(Optional.ofNullable(TestHelper.buildTrivia()));
        SubmitAnswerRequestResponse response = triviaService.submitAnswer(triviaId, requestResponse);

        // Then
        assertNotNull(response);
        String result = response.getResult();
        assertNotNull(result);
        assertEquals("wrong!", result);
    }

    @Test
    public void submitAnswerAfterMaxAttempt() throws BadRequestException {
        // Given
        int triviaId = 1;
        SubmitAnswerRequestResponse requestResponse = SubmitAnswerRequestResponse.builder().answer("London").build();

        // When
        Trivia trivia = TestHelper.buildTrivia();
        trivia.setAnswerAttempts(3);
        when(triviaRepository.findById(triviaId)).thenReturn(Optional.of(trivia));
        SubmitAnswerRequestResponse response = triviaService.submitAnswer(triviaId, requestResponse);

        // Then
        assertNotNull(response);
        String result = response.getResult();
        assertNotNull(result);
        assertEquals("Max attempts reached!", result);
    }

}