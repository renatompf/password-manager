# password-manager

## Description:
This is the backend application of a password-manager when you are able to manage not only all your passwords
and also notes. You can also generate your own random password if you want and check the strength of a password.

**ALL YOUR PASSWORDS AND NOTES ARE ENCRYPTED BEFORE BEING STORED IN THE DATABASE, NOT ONLY TO GUARANTEE THE SECURITY OF IT 
BUT ALSO TO ENSURE THE PRIVACY OF THE USER**

This project was made in [JAVA 17](https://www.java.com/en/), using [Spring Security](https://spring.io/projects/spring-security)
to manage the security and [Spring Data JPA](https://spring.io/projects/spring-data-jpa) and [PostgreSQL](https://www.postgresql.org/) to handle all the data.

## How to run it:

To run the entire project you must have [Docker](https://www.docker.com/) installed in you system.
After you installed Docker, in case you didn't just run the command bellow in the root folder:

```shell
make run
```

To run tests, you can simply make:
```shell
make tests
```

When you want to stop it just make:

```shell
make stop
```

## Endpoints:

**WHEN USING THE ENDPOINTS DON'T FORGET TO USE THE JWT TOKEN GENERATED (AS A BEARER) IN THE AUTHENTICATION ENDPOINTS**

### 

#### Registration
* POST request
```shell
localhost:8080/auth/register
```
#### Body example:

```json
{
    "firstName": "Renato",
    "lastName": "Freire",
    "email": "renato@github.com",
    "password": "password"
}
```

--------------------------------------------

#### Login
* POST request

```shell
localhost:8080/auth/login
```

#### Body example:

```json
{
    "email": "renato@github.com",
    "password": "password"
}
```
--------------------------------------------

#### Refresh Token
* POST request

```shell
localhost:8080/auth/refresh-token
```

--------------------------------------------

#### Logout
* POST request

```shell
localhost:8080/auth/logout
```

--------------------------------------------

### Generate Random Password:
**(THIS API DOES NOT NEED AUTHENTICATION)**
* POST Request

```shell
localhost:8080/password-generator
```
##### Body example:

```json
{
  "length": 16,
  "useLower": true,
  "useUpper": true,
  "useNumber": true,
  "useSpecials": true
}

```

--------------------------------------------

### Evaluate your password's strength:
**(THIS API DOES NOT NEED AUTHENTICATION)**
* POST Request

```shell
localhost:8080/password-strength
```
##### Body example:

```json
{
  "password": "(dtz%YJx4€B5@15@H8Vxc7@eAUz77]7{Y13)h"
}

```

--------------------------------------------

### Logins

#### Get all your logins

* GET Request

```shell
localhost:8080/logins
```
--------------------------------------------

### Create a new logins

* POST request

```shell
localhost:8080/logins
```

#### Body example:
```json
{
    "name": "GITHUB LOGIN",
    "username": "exampleUsername",
    "password": "examplePassword",
    "url": "https://www.github.com",
    "description": "Credentials for personal github account"
}
```


--------------------------------------------

### Get a specific login:
* GET request

```shell
localhost:8080/logins/{loginId}
```

--------------------------------------------

### Update a specific login:
* PUT request

```shell
localhost:8080/logins/{loginId}
```

#### Body example:
```json
{
  "name": "GITHUB LOGIN",
  "username": "exampleUsername2",
  "password": "examplePassword2",
  "url": "https://www.github.com",
  "description": "Credentials for personal github account"
}

```

--------------------------------------------

### Delete a specific login:
* Delete request

```shell
localhost:8080/logins/{loginId}
```


--------------------------------------------


## Notes

### Get all your notes

* GET Request

```shell
localhost:8080/notes
```
--------------------------------------------

### Create a new note

* POST request

```shell
localhost:8080/notes
```

#### Body example:
```json
{
  "name": "Example Note",
  "description": "This is an example note."
}

```
--------------------------------------------

### Get a specific note:
* GET request

```shell
localhost:8080/notes/{noteId}
```

--------------------------------------------

### Update a specific note:
* PUT request

```shell
localhost:8080/notes/{noteId}
```

#### Body example:
```json
{
  "name": "Update Example Note",
  "description": "This is an updated example note."
}

```

--------------------------------------------

### Delete a specific password:
* Delete request

```shell
localhost:8080/notes/{noteId}
```

-------------------------------------------

## License

MIT License

* http://www.opensource.org/licenses/mit-license.php