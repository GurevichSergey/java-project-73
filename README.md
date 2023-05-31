### Hexlet tests and linter status:
[![Actions Status](https://github.com/GurevichSergey/java-project-73/workflows/hexlet-check/badge.svg)](https://github.com/GurevichSergey/java-project-73/actions)
[![Actions Status](https://github.com/GurevichSergey/java-project-73/workflows/my-check/badge.svg)](https://github.com/GurevichSergey/java-project-73/actions)
[![Maintainability](https://api.codeclimate.com/v1/badges/e518efc4db665e65ecee/maintainability)](https://codeclimate.com/github/GurevichSergey/java-project-73/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/e518efc4db665e65ecee/test_coverage)](https://codeclimate.com/github/GurevichSergey/java-project-73/test_coverage)

## Менеджер задач

[Task Manager](java-project-73-production-92a3.up.railway.app/) - система управления задачами, подобная http://www.redmine.org/. Она позволяет ставить задачи, назначать исполнителей и менять их статусы. Для работы с системой требуется регистрация и аутентификация.


[API documentation](https://java-project-73-production-92a3.up.railway.app/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config) - документация для описания REST API

### Технологии

   * Java 17
   * Spring Boot, WVC, Data
   * Swagger, Lombok
   * Gradle
   * Liquibase
   * Spring Security, JWT

### Разработка

Для локального запуска необходимо установить:

   * JDK 17
   * Gradle 7.4
   * Make
   
## Локальный запуск приложения
```
make start
```

## Тестирование
```
make test
```
