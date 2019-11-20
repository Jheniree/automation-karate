Feature: Test Booking API

#POST SCENARIOS

  Scenario: Testing POST Booking endpoint with request body
    Given url 'http://127.0.0.1:8900/booking'
    And request { "date": "2019-11-19", "destination": "MAD", "id": "pepe@pepe.pe1-0.1", "origin": "FRA" }
    When method POST
    Then status 201
    And match $ contains { idUser : "#notnull" }

  Scenario: Testing POST Booking endpoint with wrong IATA destination on request body
    Given url 'http://127.0.0.1:8900/booking'
    And request { "date": "2019-11-19", "destination": "madrid", "id": "pepe@pepe.pe1-0.1", "origin": "FRA" }
    When method POST
    Then status 409
    And match $ == "Origin or Destination is not a IATA code (Three Uppercase Letters)"

  Scenario: Testing POST Booking endpoint with wrong IATA origin on request body
    Given url 'http://127.0.0.1:8900/booking'
    And request { "date": "2019-11-19", "destination": "MAD", "id": "pepe@pepe.pe1-0.1", "origin": "frankfurt" }
    When method POST
    Then status 409
    And match $ == "Origin or Destination is not a IATA code (Three Uppercase Letters)"

  Scenario: Testing POST Booking endpoint with wrong date format on request body
    Given url 'http://127.0.0.1:8900/booking'
    And request { "date": "2019-22-33", "destination": "MAD", "id": "pepe@pepe.pe1-0.1", "origin": "FRA" }
    When method POST
    Then status 400
    And match $ == "Date format not valid"

  Scenario: Testing POST Booking endpoint with wrong id on request body
    Given url 'http://127.0.0.1:8900/booking'
    And request { "date": "2019-11-18", "destination": "MAD", "id": "WRONG_pepe@pepe.pe1-0.1.WRONG_ID", "origin": "FRA" }
    When method POST
    Then status 500
    And match $ contains { error : "Internal Server Error" }

#---------------------------------------------------------------------------------------------------------------------------------------------



#GET SCENARIOS

  Scenario: Testing GET Booking endpoint by Id
    Given url 'http://127.0.0.1:8900/booking?id=pepe@pepe.pe1-0.1'
    When method GET
    Then status 200
    And match $[0] contains { idUser : "#notnull" }

  Scenario: Testing GET Booking endpoint by Date
    Given url 'http://127.0.0.1:8900/booking?date=2019-11-19'
    When method GET
    Then status 200
    And match $[0] contains { idUser : "#notnull" }

  Scenario: Testing GET Booking endpoint by Id & Date
    Given url 'http://127.0.0.1:8900/booking?id=pepe@pepe.pe1-0.1&date=2019-11-19'
    When method GET
    Then status 200
    And match $[0] contains { idUser : "#notnull" }

  Scenario: Testing GET Booking endpoint by Wrong Date
    Given url 'http://127.0.0.1:8900/booking?date=2019-33-99'
    When method GET
    Then status 500
    And match $ contains { message : "Format date not valid" }

  Scenario: Testing GET Booking endpoint by Wrong path
    Given url 'http://127.0.0.1:8900/booking/date=2019-33-99'
    When method GET
    Then status 404
    And match $ contains { error : "Not Found" }