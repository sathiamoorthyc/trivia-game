package org.trivia.game.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.trivia.game.model.SubmitAnswerRequestResponse;
import org.trivia.game.model.TriviaDto;
import org.trivia.game.service.TriviaService;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/trivia")

public class GameController {

    @Autowired
    private TriviaService triviaService;

    @PostMapping(value = "/start")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TriviaDto> start(){
        return new ResponseEntity<>(triviaService.startGame(), HttpStatus.OK);
    }

    @PutMapping(value = "/reply/{triviaId}", consumes = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SubmitAnswerRequestResponse> submitAnswer(@PathVariable int triviaId, @RequestBody SubmitAnswerRequestResponse submitAnswerRequest) throws BadRequestException {
        SubmitAnswerRequestResponse response = triviaService.submitAnswer(triviaId, submitAnswerRequest);
        return new ResponseEntity<>(response, getAppropriateResponseStatus(response.getResult()));
    }

    private HttpStatus getAppropriateResponseStatus(String result){
        return switch (result) {
            case "right!" -> HttpStatus.OK;
            case "wrong!" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.FORBIDDEN;
        };
    }
}
