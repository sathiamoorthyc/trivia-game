Feature: play trivia quiz game submitting incorrect answer

  Scenario: Client 2 plays by replying back to the question posted /trivia/start
    When the client-2 answers the question incorrectly by calling '/trivia/reply/'
    Then verify the response status is 400 bad request

