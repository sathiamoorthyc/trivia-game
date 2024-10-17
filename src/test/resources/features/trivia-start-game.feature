Feature: start the trivia quiz game

  Scenario: client makes call to POST /trivia/start
    When the client calls '/trivia/start'
    Then the client receives status code of 200
    Then the trivia object is not null

