package org.trivia.game.utils;

import org.trivia.game.model.Trivia;

public class TestHelper {
    public static Trivia buildTrivia() {
        return Trivia.builder().triviaId(1).answerAttempts(0)
                .question("What is the capital of England?")
                .possibleAnswers(new String[]{"London", "Manchester", "Birmingham"})
                .correctAnswer("London")
                .build();
    }
}
