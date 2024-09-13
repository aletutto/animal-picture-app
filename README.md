# Animal Picture App
The *Animal Picture App* provides a small REST API, which can fetch from external APIs random pictures of different animals, and save them into a database. Besides that it can show the latest fetched animal picture.

# How to run the Animal Picture App
Prerequisite: [Docker](https://docs.docker.com/get-started/get-docker/) installed
1. Simply run `docker run -p 8080:8080 -d --name animal-picture-app -t aletutto/animal-picture-app:latest`.
2. Enjoy! There is a Swagger-UI available at [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html) to play around with the API.

# API
| HTTP Method | Path            | Description                                                                                                 |
|-------------|-----------------|-------------------------------------------------------------------------------------------------------------|
| POST        | /animals        | Fetches random pictures of animals by an provided animal type from external APIs, and save them into the DB |
| GET         | /animals/latest | Get the latest animal picture saved in DB                                                                   |

For a more detailed overview on the API, checkout the [Swagger-UI](http://localhost:8080/swagger-ui/index.html).

# Tech Stack
- Spring Boot
- H2 In Memory database
- OpenAPI & Swagger 
- Docker

# Possible next steps
- **Swagger-UI documentation:** In order to not blow up the code of the controller, I didn't document the API very much through Swagger or OpenAPI. Especially, but not limited to, should the following things be documented more precisely:
  - Further description of endpoints including all parameters
  - HTTP Status codes of a response,
  - Used schemas
  - Example data
- **More Test Cases:** Currently there are only very basic test cases implemented. They should be extended with more detailed test cases, especially on the service level which includes the business logic.
- **Persistent DB:** Currently the in memory database H2 is used for saving the data. That means whenever the docker container is stopped, the data will be deleted. The H2 in memory database was taken for simplicity, but should be exchanged with a persistent one, for example [PostgreSQL](https://www.postgresql.org) or [MySQL](https://www.mysql.com/de/). This could be done by adding another docker container, and combining them through docker-compose. For sure, they would need to be in the same docker network, so that the Animal Picture App can communicate with the database, and a docker volume would need to be defined in order to save the data persistently.