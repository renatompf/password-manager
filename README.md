# password-manager

## Description:
This is the backend application of a password-manager when you are able to manage not only all your passwords
and also notes. You can also generate your own random password if you want.

This project was made in [JAVA 17](https://www.java.com/en/), using [Spring Security](https://spring.io/projects/spring-security) 
to manage the security and [Spring Data JPA](https://spring.io/projects/spring-data-jpa) and [PostgreSQL](https://www.postgresql.org/) to handle all the data.

## How to run it:

To run the entire project you must have [Docker]() installed in you system.
After you installed Docker, in case you didn't just run the command bellow in the root folder:

```shell
make run
```

When you want to stop it just make:


```shell
make stop
```

## Endpoints:

### Authenticate

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

### Generate Random Password:
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

### Passwords

#### Get all your passwords

* GET Request

```shell
localhost:8080/passwords
```
--------------------------------------------

### Create a new password

* POST request

```shell
localhost:8080/passwords
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

### Get a specific password:
* GET request

```shell
localhost:8080/passwords/{passwordId}
```

--------------------------------------------

### Update a specific password:
* PUT request

```shell
localhost:8080/passwords/{passwordId}
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

### Delete a specific password:
* Delete request

```shell
localhost:8080/passwords/{passwordId}
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
--------------------------------------------

## Coming Soon:
For futures updates one thing to add is the fact that no password will be saved in the system without being encrypted 
for user safety.
