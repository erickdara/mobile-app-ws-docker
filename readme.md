# Microservice API Documentation

## Overview

This document describes the functionalities of the microservice, focusing on the REST API endpoints provided for
interacting with the service. The microservice is designed to manage user data, including personal information and
addresses associated with each user.

### Base URL

All API requests are made to the base URL `http://localhost:8080/mobile-app-ws`. Ensure that the microservice is running
on this address before attempting to make requests.

## API Endpoints

### Create User

- **URL**: `/users`
- **Method**: `POST`
- **Description**: Creates a new user with the provided information, including their addresses. This endpoint accepts a
  JSON object containing the user's first name, last name, email, password, and an array of address objects.
- **Headers**:
    - `Content-Type`: `application/json`
    - `Accept`: `application/json`
- **Request Body Example**:

```json
{
  "firstName": "Pedro",
  "lastName": "Marmol",
  "email": "pedro2@gmail.com",
  "password": "123456",
  "addresses": [
    {
      "city": "Bogota",
      "country": "Colombia",
      "streetName": "456 Street Name",
      "postalCode": "ADADSSS",
      "type": "shipping"
    },
    {
      "city": "Bogota",
      "country": "Colombia",
      "streetName": "789 Street Name",
      "postalCode": "ertert",
      "type": "billing"
    }
  ]
}
```

### Login Endpoint

### Description

The login endpoint is used to authenticate users in the microservice. Upon successful authentication, it returns a JWT
token which can be used to access all remaining endpoints that require authentication.

### Login User

- **URL**: `/users/login`
- **Method**: `POST`
- **Description**: To authenticate, send a POST request with the following JSON structure in the body:
- **Headers**:
    - `Content-Type`: `application/json`
    - `Accept`: `application/json`
- **Request Body Example**:

```json
{
  "email": "user@example.com",
  "password": "yourPassword"
}
```
