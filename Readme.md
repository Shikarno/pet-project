# Spring Boot REST API with Swagger Documentation

This project is a Spring Boot REST API application that provides endpoints to retrieve user repositories and their branches from Version Control Systems (VCS).

## Table of Contents

1. [Introduction](#introduction)
2. [Features](#features)
3. [Technologies Used](#technologies-used)
4. [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
5. [Usage](#usage)
6. [Swagger Documentation](#swagger-documentation)
7. [Exception Handling](#exception-handling)
8. [Accessing the Application](#accessing-application)

## Introduction

This project provides a Spring Boot REST API application that allows users to retrieve repositories and branches from VCS by providing a username and VCS type. It includes Swagger documentation for API reference, making it easy for developers to understand and interact with the API.

## Features

- Spring Boot REST API
- Swagger documentation for API reference
- Exception handling for error responses

## Technologies Used

- Java
- Spring Boot
- Spring Webflux
- Swagger (OpenAPI)
- Lombok
- Reactor (for reactive programming)

## Getting Started

Follow the instructions below to get started with running and using the application.

### Prerequisites

Before running the application, make sure you have the following installed:

- Java Development Kit (JDK)
- Maven
- Docker (optional, for Docker deployment)

### Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/Shikarno/pet-project.git
    cd pet-project
    ```

2. Build the application:

    ```bash
    mvn clean package
    ```

3. Run the application:

    ```bash
    java -jar target/spring-boot-rest-api.jar
    ```

## Usage

Once the application is running, you can access the endpoints using tools like cURL, Postman, or through the Swagger UI.

## Swagger Documentation

The Swagger documentation for the API is available at `/swagger-ui.html` endpoint when the application is running. You can use the Swagger UI to explore and test the API endpoints interactively.

## Exception Handling

The application includes exception handling for error responses. It returns appropriate HTTP status codes along with error messages in case of errors such as user not found or internal server error.

## Accessing the Application

The application is hosted on AWS and can be accessed at the following URL:

[http://3.81.87.73:8080](http://3.81.87.73:8080)

Endpoint url - [http://3.81.87.73:8080/v1/api/github/{userName}/repositories](http://3.81.87.73:8080/v1/api/github/{userName}/repositories)

You can use this URL to interact with the API endpoints.
