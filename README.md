Code created as an exercise. It is a HTTP-based microservice implemented in Scala/Akka.

###Build and Execution
The project can be built, tested and executed using SBT:
* `sbt compile` to compile
* `sbt test` to test
* `sbt run` to execute

It's also possible to have all the project and dependencies in one executable jar, by using the assembly plugin:
* `sbt assembly`

###Usage
The service should accept a POST request containing an expression to be calculated in JSON format and return the result or a validation error.

Example request:

    curl -H "Content-Type: application/json" \
         -X POST \
         -d '{"expression":"(1-1)*2+3*(1-3+4)+10/2' \
         http://localhost:5555/evaluate
Expected response:

    {"result": 11}