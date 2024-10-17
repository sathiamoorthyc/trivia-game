Feature: play trivia quiz game after max attempt reached

  Scenario: Client-3 plays by replying back to the question posted /trivia/start
    When the client-3 answers the question by calling '/trivia/reply/' after max-attempt reached
    Then verify the response status is 403 forbidden

