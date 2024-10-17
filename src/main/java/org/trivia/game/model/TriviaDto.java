package org.trivia.game.model;

public record TriviaDto(Integer triviaId, String question, String[] possibleAnswers) {
}
