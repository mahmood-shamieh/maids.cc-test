
# Spring Boot Application Documentation - Maids.cc Test

This project is a test implementation developed by Engineer Mahmood Shamieh. It follows a structured architecture to handle requests and maintain a clean, organized backend for Spring Boot applications.

----------

## Project Structure

The application consists of the following layers:

-   Filter Layer: Manages request filtering and security.

-   Controller Layer: Handles incoming requests.

-   DTO (Data Transfer Object) Layer: Defines structured data transfer between layers.

-   Service Layer: Processes business logic.

-   Repository Layer: Manages database interactions.

-   Model Layer: Contains entity definitions.

-   Exception Layer: Manages custom exceptions.

-   Validation Layer: Provides validation for data inputs.

-   Config Layer: Configures settings like authentication and password encoding.

-   Aspects Layer: Contains cross-cutting concerns, such as logging.

-   Utils Layer: Houses utility classes and methods.


----------

## Request Processing Flow

1.  Configuration Layer: Configures which requests pass through the filter, sets up the password encoder type, and defines the authentication manager.

2.  Filter Layer: Implements security using a JWT filter, with JWTUtility for token management.

3.  Controller Layer: Accepts requests and validates input data using DTO objects.

4.  DTO Validation: Uses validation groups to check DTO attributes before processing.

5.  Service Layer: Executes business logic, handles custom exceptions, and performs data operations using the Repository layer.

6.  Repository Layer: Executes CRUD operations on the database.

7.  Response Handling: Service layer returns a DTO object as the response.


----------

## Dependencies

This application relies on the following dependencies:

-   spring-boot-starter-data-jpa

-   spring-boot-starter-cache

-   io.jsonwebtoken:jjwt

-   javax.xml.bind:jaxb-api

-   org.slf4j:slf4j-api

-   org.mockito:mockito-core

-   com.fasterxml.jackson.core:jackson-databind

-   spring-boot-starter-security

-   spring-boot-starter-validation

-   spring-boot-starter-web

-   org.projectlombok:lombok

-   com.mysql:mysql-connector-j


----------

## Running the Application

### Server Setup

1.  Ensure a MySQL server is installed and running.

2.  Update the MySQL port, username, and password in application.properties (located in the resources directory).

3.  The database named maids_cc will be created automatically on the first application run—manual creation is not needed.

4.  Verify port 8080 is available or update it in application.properties.


### Testing the Application

Run the application to ensure all components are functioning correctly.

### Postman Setup

1.  Import the provided Postman collection (endpoints.json) from the project repository.

2.  Import the environment file (environment.json) into Postman.

3.  Update the server URL and port in the Postman environment.


----------

## API Endpoints

### Books:


#### 1. Retrieve a list of all books

-   Method: GET

-   Endpoint: http://{{serverUrl}}:{{port}}/api/books

-   Authorization: Bearer Token ({{adminToken}})


----------

#### 2. Retrieve a specific book

-   Method: GET

-   Endpoint: http://{{serverUrl}}:{{port}}/api/books/{bookId}

-   Authorization: Bearer Token ({{adminToken}})

-   Parameters: bookId (Path Parameter, e.g., /api/books/2)


----------

#### 3. Delete a specific book

-   Method: DELETE

-   Endpoint: http://{{serverUrl}}:{{port}}/api/books/{bookId}

-   Authorization: Bearer Token ({{adminToken}})

-   Parameters: bookId (Path Parameter, e.g., /api/books/4)


----------

#### 4. Add a new book

-   Method: POST

-   Endpoint: http://{{serverUrl}}:{{port}}/api/books

-   Authorization: Bearer Token ({{adminToken}})


Request Body:

    {
    
    "author": 10,
    
    "title": "Book Title",
    
    "isbn": 11,
    
    "publicationYear": 2020,
    
    "available": 1
    
    }

----------

#### 5. Update an existing book

-   Method: PUT

-   Endpoint: http://{{serverUrl}}:{{port}}/api/books/{bookId}

-   Authorization: Bearer Token ({{adminToken}})

-   Parameters: bookId (Path Parameter, e.g., /api/books/1)


Request Body:

    {
    
    "author": 10,
    
    "title": "Updated Book Title",
    
    "isbn": 1,
    
    "publicationYear": 2020,
    
    "available": 1
    
    }

----------



### Patrons:


#### 1.Retrieve a List of All Patrons

-   Method: GET.

-   Endpoint: http://{{serverUrl}}:{{port}}/api/patrons.

-   Authorization: Bearer token ({{adminToken}}).


----------

#### 2.Retrieve a Specific Patron

-   Method: GET.

-   Endpoint: http://{{serverUrl}}:{{port}}/api/patrons/{id}.

-   Authorization: Bearer token ({{adminToken}}).

-   Parameters: id - Patron ID.


----------

#### 3.Delete a Specific Patron

-   Method: DELETE.

-   Endpoint: http://{{serverUrl}}:{{port}}/api/patrons/{id}.

-   Authorization: Bearer token ({{adminToken}}).

-   Parameters: id - Patron ID.


----------

#### 4.Authenticate Patron

-   Method: POST.

-   Endpoint: http://{{serverUrl}}:{{port}}/api/patrons/authenticate.

-   Body: JSON


Request Body:

    {
    
    "email": "mahmood@mahmood.mahmood11",
    
    "password": "11111111"
    
    }

----------

#### 5.Add a New Patron

-   Method: POST.

-   Endpoint: http://{{serverUrl}}:{{port}}/api/patrons.

-   Body: JSON.


Request Body:

    {
    
    "name": 10,
    
    "email": "mahmood@mahmood.mahmood11",
    
    "password": "11111111"
    
    }

----------

#### 6.Update an Existing Patron

-   Method: PUT.

-   Endpoint: http://{{serverUrl}}:{{port}}/api/patrons/{id}.

-   Authorization: Bearer token ({{adminToken}}).

-   Parameters: id - Patron ID.

-   Body: JSON


Request Body:

    {
    
    "name": 10,
    
    "email": "mahmood@mahmood.mahmood",
    
    "password": "123456789"
    
    }

----------




### BorrowingRecords:


#### 1. Borrow a Book


-   Method: POST.

-   Endpoint: /api/borrow/{bookId}/patron/{patronId}.

-   Authorization: Bearer token ({{adminToken}}).


----------

#### 2. Return a Borrowed Book


-   Method: PUT.

-   Endpoint: http://{{serverUrl}}:{{port}}/api/return/{bookId}/patron/{patronId}.

-   Authorization: Bearer token ({{adminToken}})

# Notes
All of Postman files (Collection, Environment) are provided at the same repository