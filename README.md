# User API

REST API built with Java 21, Maven and Spring Boot.

## Requirements

- JDK 21
- Maven 3.6.3 or later

## Run

```bash
mvn spring-boot:run
```

The API runs at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Endpoints

```text
GET    /users
GET    /users?sortedBy=email|id|name|phone|tax_id|created_at
GET    /users?filter=field operator value
POST   /users
PATCH  /users/{id}
DELETE /users/{id}
POST   /login
```

## Filter Operators

```text
co = contains
eq = equals
sw = starts with
ew = ends with
```

Example:

```text
/users?filter=name%20co%20user
```

## Login

`tax_id` is used as username.

```json
{
  "tax_id": "BERR980202XXX",
  "password": "password2"
}
```

## Notes

- Users are stored in memory.
- Passwords are encrypted with AES-256.
- Passwords are not returned in response bodies.
- `created_at` uses the Madagascar timezone.
- The date format is `dd-MM-yyyy HH:mm`.
- `tax_id` must be unique and follow RFC format.
- Phone numbers must contain 10 digits and may include a country code.

## Tests

```bash
mvn test
```

## Postman

Import the following collection into Postman:

```text
postman/User-API.postman_collection.json
```

Run `Get all users` or `Create user` first to initialize the `userId`
collection variable used by the PATCH and DELETE requests.
