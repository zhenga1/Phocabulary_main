# Server Architecture

## Exposed APIs

### Account API

The Account API endpoints are utilities for performing certain actions with accounts.

#### Register Endpoint: `POST /accounts/register`

The Register Endpoint allows a user to register an account.

The endpoint expects to receive a `Registrar` object

##### The `Registrar` object

| Field | Type | Description |
| ----- | ---- | ----------- |
| email | string | the email address associated with the registrar |
| username | string | the username of the registrar |
| password | string | salted hash of the password |

