# Ranking

Service to manage players in a tournament

## 🧐 Features

Here're some of the project's best features:

*   Add new player in the tournament
*   Edit score of a player
*   Get the full ranking of the tournament
*   Get the data about a player
*   Delete all players

## 🛠️ Installation Steps

### Prerequisites
You need to have jdk-17, docker and docker-compose installed.


### Steps

1. In docker/mongo repository you can customize the user and password in docker-compose.yml then run</p>

``` shell
docker-compose up -d
```

2. Rename the .env.example and customize the environment variables

3. Install the env variable

``` shell
source .env
```

4. In the root folder launch the application

``` shell
./gradlew run
```

5. Check it works by loading the swagger ui

``` 
http://localhost:8080/swagger
```

## 💻 Built with

Technologies used in the project:

* Kotlin
* Ktor
* Koin
* JUnit
* MongoDB
* TestContainers
* OpenAPI
* Liquibase
* MockK
* ArchUnit
* Detekt