# Entity Manager Service (EMS)

This repository contains the backend microservice for managing the entities (such as startups, users, team members, etc.) in the 
Digi-Dojo project.

The first version of this microservice can be found [here](https://github.com/Digi-Dojo/MService-StartupsAndUsers).

## Architecture

The architecture is built on Java using Spring Boot. It follows a hybrid architecture, handling both sync and async requests.

* The synchronous requests are based on Spring Boot Rest Controller.
* The asynchronous requests are based on Apache Kafka.

The package structure is divided mainly into application and domain, following a clean architecture approach.

## Requirements

- Java 17 or higher
- Gradle
- Docker
- Docker Compose

## Getting Started

1. Clone the repository of the shared model:

```
git clone git@github.com:Digi-Dojo/DigiDojoSharedModel.git
cd DigiDojoSharedModel
```

2. (Optional) Follow the instructions in the package's `README.md` file to build it.

3. Clone the repository:

```
git clone git@github.com:Digi-Dojo/EntityManagerService.git
cd EntityManagerService
```

4. Create a `.env` file based on `.env.sample` file sample to configure connections

5. Build and run your containers:
```
docker-compose build
docker-compose up
```

The server will be running on `localhost:8200`.

6. When you are done, drop the containers:
```
docker-compose down
```

## Testing

To run tests, execute the following command in the project root:

```
gradle test
```

This will run all test classes in the `src/test/java` directory.

## Running locally

If you don't want to run the server in a docker container, you can:

1. Export the `.env` file running `export $(cat .env | xargs) && export DB_PORT=3333 && export DB_HOST=localhost` in the root folder.

2. Run the docker container related to the database:

```
docker-compose up ems_postgres
```

3. Build the project:

```
gradle clean build
```

4. Run the server:

```
gradle bootRun
```

5. After closing the server, turn off the database:

```
docker-compose down
```














