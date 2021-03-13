
# How to start postgres via docker 
```
docker-compose -f docker-compose.yml up --no-start
docker-compose -f docker-compose.yml start
```

## How to setup environment

provide correct connection url

```
spring:
  datasource:
    ...
    url: jdbc:postgresql://<host>:<port>/bookstorage
```
