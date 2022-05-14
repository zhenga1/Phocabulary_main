# Server Architecture

## Exposed APIs

### Account API

The Account API endpoints are utilities for performing certain actions with accounts.

#### Register Endpoint: `POST /accounts/register`

The Register Endpoint allows a user to register an account.

The endpoint expects to receive a `Registrar` object.

##### The `Registrar` object

| Field | Type | Description |
| ----- | ---- | ----------- |
| email | string | the email address associated with the registrar |
| username | string | the username of the registrar |
| password | string | salted hash of the password |
| salt | string | the salt for hashing previously |

#### Login Endpoint: `POST /accounts/login`

The Login Endpoint allows a user to login.

The endpoint expects to receieve a `Credentials` object.

##### The `Credentials` object

| Field | Type | Description |
| ----- | ---- | ----------- |
| username | string | the username of the user |
| password | string | the password of the user |
