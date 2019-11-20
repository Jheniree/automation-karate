Feature: Test User API

#POST SCENARIOS

  Scenario: Testing POST User endpoint with request body
    Given url 'http://127.0.0.1:8900/user'
    And request { "email": "jheni@test.com", "name": "Jheni" }
    When method POST
    Then status 201
    And match $ contains { name : "Jheni" }

  Scenario: Testing POST User endpoint with wrong email on body
    Given url 'http://127.0.0.1:8900/user'
    And request { "email": "jhenitest.com", "name": "Jheni" }
    When method POST
    Then status 500
    And match $ contains { message : "malformed email" }

  Scenario: Testing POST User endpoint with empty name on body
    Given url 'http://127.0.0.1:8900/user'
    And request { "email": "jheni@test.com", "name": "" }
    When method POST
    Then status 409
    And match $ == "Check fields"

#GET SCENARIOS

  Scenario: Testing GET User endpoint by Id
    Given url 'http://127.0.0.1:8900/user?id=pepe@pepe.pe1-0.1'
    When method GET
    Then status 200
    And match $ contains { name : "pepe" }

  Scenario: Testing GET User endpoint by wrong Id
    Given url 'http://127.0.0.1:8900/user?id=pepe@pepe.pe1-0.1WRONG'
    When method GET
    Then status 404
    And match $ == "User not found"

  Scenario: Testing GET User endpoint by wrong Id
    Given url 'http://127.0.0.1:8900/user/id'
    When method GET
    Then status 404
    And match $ contains { message : "No message available" }

  Scenario: Testing GET User endpoint by wrong Id
    Given url 'http://127.0.0.1:8900/user/all'
    When method GET
    Then status 200
    And match $ == '##[_ > 0]'
    And match each $ contains { email : "#notnull" }
