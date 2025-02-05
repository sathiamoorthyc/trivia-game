# This is a simple spring-boot project that implements the below requirements
## This project uses the following tech-stack
- Maven
- h2 in mem DB
- lombok
- junit
- cucumber

TRIVIA GAME
Build a REST API with following 2 endpoints:
POST /trivia/start
PUT /trivia/reply
Above endpoints will do the following:
POST /trivia/start
This endpoint will call following public 3rd party API to get a random trivia question:
GET https://opentdb.com/api.php?amount=1

I.e:

        {
          "response_code": 0,
          "results": [{
            "category": "Sports",
            "type": "multiple",
            "difficulty": "medium",
            "question": "Which soccer team won the Copa America 2015 Championship ?",
            "correct_answer": "Chile",
            "incorrect_answers": ["Argentina", "Brazil", "Paraguay"]
          }]
        }

It will create a record in DB with data from above endpoint response. Record in the database will have the following structure:

        Trivia table:
        - triviaId (autogenerated). I.e.: 1
        - question. I.e.: "Which soccer team won the Copa America 2015 Championship ?"
        - correct_answer: "Chile"
        - answerAttempts: 0

Endpoint response will have following format: (possible answer should be showed in random possitions)

        Status: 200 Success
        Response body:
 
        {
          "triviaId": 1,
          "question": "Which soccer team won the Copa America 2015 Championship ?",
          "possibleAnwers": ["Chile", "Argentina", "Brazil", "Paraguay"] 
        }

where triviaId is the autogenerated triviaId in the database.
PUT /trivia/reply/1

           Request Body
           {
             "answer": "Chile"
           }

This endpoint will retrieve record from database given the triviaId received as input parameter.
It will compare the answer received in the request body against the one in the db.
a. If both are the same (no case sensitive), it will remove record from database and will return:

            Status: 200 Success
 
            Body response:
            {
              "result": "right!"
            }

b. If they are different (no case sensitive), it will increase answerAttempts in database and will reply:

            Status: 400 Bad Request
            Body response:
            {
              "result": "wrong!"
            }

c. If number of attempts in database is 3, it will reply:

            Status: 403 Forbidden
            Body response:
            {
              "result": "Max attempts reached!"
            }

Extra (not required, just if you get bored with above one)
Add error handling for any exception you consider should be catched.
I.e: If record is not found in DB for the PUT /trivia/reply&triviaId={id} endpoint, then it should reply with:

               Status: 404 Not Found
               Body response:
               {
                 "result": "No such question!"
               }