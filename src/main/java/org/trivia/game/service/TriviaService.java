package org.trivia.game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.trivia.game.model.SubmitAnswerRequestResponse;
import org.trivia.game.model.Trivia;
import org.trivia.game.model.TriviaDto;
import org.trivia.game.openTDb.OpenTDbResponse;
import org.trivia.game.openTDb.OpenTDbResult;
import org.trivia.game.repository.TriviaRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TriviaService {

    /**
     * The below constants can be moved to external properties
     */
    public static final String OPEN_TDB_URL = "https://opentdb.com/api.php?amount=1";
    private static final int MAX_ATTEMPT_ALLOWED = 3;

    @Autowired
    private TriviaRepository triviaRepository;

    @Autowired
    private RestTemplate restTemplate;

    public TriviaDto startGame(){
        OpenTDbResponse openTDbResponse = callExternalApi();
        Trivia trivia = convertOpenTDbResponseToTrivia(openTDbResponse);

        Trivia savedTrivia = triviaRepository.save(trivia);
        return convertToTriviaDto(savedTrivia);
    }

    public SubmitAnswerRequestResponse submitAnswer(int triviaId, SubmitAnswerRequestResponse submitAnswerRequest){
        Optional<Trivia> optionalTrivia = triviaRepository.findById(triviaId);
        if(optionalTrivia.isPresent()){
            Trivia retrievedTrivia = optionalTrivia.get();

            if(retrievedTrivia.getAnswerAttempts() < MAX_ATTEMPT_ALLOWED) {
                String correctAnswer = retrievedTrivia.getCorrectAnswer();
                String submittedAnswer = submitAnswerRequest.getAnswer();

                // If correct answer submitted
                if (submittedAnswer.equalsIgnoreCase(correctAnswer)) {
                    incrementTriviaAttempt(retrievedTrivia);
                    return SubmitAnswerRequestResponse.builder().result("right!").build();
                }

                incrementTriviaAttempt(retrievedTrivia);
                return SubmitAnswerRequestResponse.builder().result("wrong!").build();
            }else{ // If max attempt reached
                return SubmitAnswerRequestResponse.builder().result("Max attempts reached!").build();
            }
        }else{
            throw new IllegalArgumentException("Invalid Trivia id [" + triviaId + "] submitted");
        }
    }

    private void incrementTriviaAttempt(Trivia trivia){
        trivia.setAnswerAttempts(trivia.getAnswerAttempts() + 1);
        triviaRepository.save(trivia);
    }

    private OpenTDbResponse callExternalApi(){
        ResponseEntity<String> forEntity = restTemplate.getForEntity(OPEN_TDB_URL, String.class);
        String response = forEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        OpenTDbResponse openTDbResponse;
        try {
            assert response != null;
            openTDbResponse = objectMapper.readValue(response, OpenTDbResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return openTDbResponse;
    }

    private Trivia convertOpenTDbResponseToTrivia(OpenTDbResponse openTDbResponse){
        Optional<OpenTDbResult> openTDbResult = Arrays.stream(openTDbResponse.getResults()).findAny();
        if(openTDbResult.isPresent()){
            OpenTDbResult result = openTDbResult.get();
            List<String> possibleAnswers = new ArrayList<>(Arrays.asList(result.getIncorrect_answers()));
            possibleAnswers.add(result.getCorrect_answer());
            return Trivia.builder().question(result.getQuestion())
                    .correctAnswer(result.getCorrect_answer())
                    .answerAttempts(0)
                    .possibleAnswers(possibleAnswers.toArray(String[]::new)).build();
        }else{
            throw new IllegalArgumentException("No questions retrieved from OpenTDb, please try again");
        }
    }

    private TriviaDto convertToTriviaDto(Trivia trivia){
        return new TriviaDto(trivia.getTriviaId(), trivia.getQuestion(), trivia.getPossibleAnswers());
    }
}
