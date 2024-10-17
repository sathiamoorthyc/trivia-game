Feature: play trivia quiz game

  Scenario: Client plays by replying back to the question posted /trivia/start
    When the client answers the question by calling '/trivia/reply/'
    Then verify the response status is 200

