# Reactive-Spring-Boot-webFlux 
## Indytskyi Artem 
---
### Stack of technologies :smiley:

- `Spring Boot 2.7.8`
- `WebFlux` 
- `Swagger UI`
- `MongoDb reactive`
- `Eureka`
- `Reactor Test`
- `Gateway`

You will like those projects!

---
### General info :musical_note:
This test project is based on showing the possibilities and types of reactive style and programming itself. Covering all its parts.

It includes microservices:
1. Discovery service (Eureka)
2. Config service (stores all service configurations )
3. Api-gateway
4. Movies-info service
5. Movies-reviews service
6. Movies service
---
### How to start service locally :construction_worker:

There are literally a few things you need to do to get the services up and running.

#### Docker composr

In order to start the project you will need to run the docker containers. 

For a guide for usage with Docker, [checkout the docs](https://github.com/maildev/maildev/blob/master/docs/docker.md).

1. Open your project in terminal.

2. Start docker-compose-mongo
```
docker-compose -f docker-compose-mongo.yml up
````


3. Start discovery-movie-service

4. Start movies-config-service

5. Start movies-api-gateway

6. Start movies-info-service

7. Start movies-reviews-service

8. Start movies-service

Relax and test :sunglasses:

---
### Usage :star:

#### To test the microservice:
- [Open the Swagger](http://localhost:8080/swagger-ui/index.html#/) - port on which you run the application

### IMPOTRANT :fire: :fire:
- To test streaming, you can't run the query through postman, you just need to type this command in the console to do it.
```
curl -i http://localhost:8080/v1/reviews/stream
```

---
### THANKS :heart:
---
