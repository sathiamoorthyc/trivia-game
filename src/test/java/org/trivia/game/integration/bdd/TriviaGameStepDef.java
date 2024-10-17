package org.trivia.game.integration.bdd;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.trivia.game.model.Trivia;
import org.trivia.game.model.TriviaDto;
import org.trivia.game.repository.TriviaRepository;
import org.trivia.game.utils.TestHelper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TriviaGameStepDef {

    @LocalServerPort
    String port;
    @Autowired
    TriviaRepository triviaRepository;
    ResponseEntity<TriviaDto> responseEntity;

    @When("the client calls {string}")
    public void theClientCallsTriviaStart(String endPoint) {
        String url = "http://localhost:"+ port + endPoint;

        Trivia trivia = TestHelper.buildTrivia();
        when(triviaRepository.save(any())).thenReturn(trivia);
        responseEntity = new RestTemplate().exchange(url, HttpMethod.POST, null, TriviaDto.class);
    }

    @Then("the client receives status code of {int}")
    public void theClientReceivesStatusCodeOf(int expected) {
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getStatusCode());
        assertThat("status code is" + expected,
                responseEntity.getStatusCode().value() == expected);
    }

    @Then("the trivia object is not null")
    public void theTriviaObjectIsNotNull() {
        Assertions.assertNotNull(responseEntity);
        TriviaDto triviaDto = responseEntity.getBody();
        Assertions.assertNotNull(triviaDto);
        Assertions.assertNotNull(triviaDto.triviaId());
        Assertions.assertNotNull(triviaDto.question());
        Assertions.assertNotNull(triviaDto.possibleAnswers());
    }
}
