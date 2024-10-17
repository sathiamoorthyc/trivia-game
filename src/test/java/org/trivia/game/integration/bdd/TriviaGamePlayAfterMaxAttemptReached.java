package org.trivia.game.integration.bdd;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.trivia.game.model.SubmitAnswerRequestResponse;
import org.trivia.game.model.Trivia;
import org.trivia.game.model.TriviaDto;
import org.trivia.game.repository.TriviaRepository;
import org.trivia.game.utils.TestHelper;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class TriviaGamePlayAfterMaxAttemptReached {
    @LocalServerPort
    String port;
    ResponseEntity<TriviaDto> responseEntity;
    HttpClientErrorException exception;
    @Autowired
    TriviaRepository triviaRepository;

    @When("the client-3 answers the question by calling {string} after max-attempt reached")
    public void theClientAnswersTheQuestionByCallingTriviaReplyTriviaId(String endPoint) {
        String url = "http://localhost:"+ port + endPoint + "/" + 1;
        Trivia trivia = TestHelper.buildTrivia();
        trivia.setAnswerAttempts(3);
        Optional<Trivia> optionalTrivia = Optional.of(trivia);

        // When
        when(triviaRepository.findById(anyInt())).thenReturn(optionalTrivia);
        SubmitAnswerRequestResponse request = SubmitAnswerRequestResponse.builder().answer(trivia.getCorrectAnswer()).build();
        HttpEntity<SubmitAnswerRequestResponse> requestResponseHttpEntity = new HttpEntity<>(request);

        exception = Assertions.assertThrows(HttpClientErrorException.class, () -> {
            new RestTemplate().exchange(url, HttpMethod.PUT, requestResponseHttpEntity, SubmitAnswerRequestResponse.class);
        }, "HttpClientErrorException error was expected");

    }

    @Then("verify the response status is {int} forbidden")
    public void verifyTheResponseStatusIs(int expected) {
        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getStatusCode());
        assertThat("status code is" + expected,
                exception.getStatusCode().value() == expected);
        Assert.assertEquals("403 : \"{\"answer\":null,\"result\":\"Max attempts reached!\"}\"", exception.getMessage());
    }
}
