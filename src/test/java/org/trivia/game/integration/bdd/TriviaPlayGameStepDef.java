package org.trivia.game.integration.bdd;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
@ExtendWith(value={SpringExtension.class, MockitoExtension.class})
public class TriviaPlayGameStepDef {

    @LocalServerPort
    String port;
    ResponseEntity<SubmitAnswerRequestResponse> submitAnswerResponse;

    @Autowired
    TriviaRepository triviaRepository;

    @When("the client answers the question by calling {string}")
    public void theClientAnswersTheQuestionByCallingTriviaReplyTriviaId(String endPoint) {

        String url = "http://localhost:"+ port + endPoint + "/" + 1;

        Optional<Trivia> optionalTrivia = Optional.of(TestHelper.buildTrivia());

        // When
        when(triviaRepository.findById(anyInt())).thenReturn(optionalTrivia);
        Trivia trivia = optionalTrivia.get();

        SubmitAnswerRequestResponse request = SubmitAnswerRequestResponse.builder().answer(trivia.getCorrectAnswer()).build();
        HttpEntity<SubmitAnswerRequestResponse> requestResponseHttpEntity = new HttpEntity<>(request);
        submitAnswerResponse = new RestTemplate().exchange(url, HttpMethod.PUT, requestResponseHttpEntity, SubmitAnswerRequestResponse.class);

    }


    @Then("verify the response status is {int}")
    public void verifyTheResponseStatusIs(int expected) {
        Assertions.assertNotNull(submitAnswerResponse);
        Assertions.assertNotNull(submitAnswerResponse.getStatusCode());
        assertThat("status code is" + expected,
                submitAnswerResponse.getStatusCode().value() == expected);
    }

}
