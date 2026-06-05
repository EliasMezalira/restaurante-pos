# restaurante-pos

API de ponto de venda para restaurante, construida com Quarkus, PostgreSQL, Hibernate ORM Panache e Liquibase.

## Front-end

O front-end da aplicacao e desenvolvido em Angular 21.

Repositorio: [github.com/daniel-abrahao/app-restaurante-front](https://github.com/daniel-abrahao/app-restaurante-front)


## Pre-requisitos

- Docker e Docker Compose
- JDK configurado no `JAVA_HOME`
- Maven Wrapper do projeto (`mvnw` / `mvnw.cmd`)

## Rodar o projeto localmente

Para subir o ambiente local completo, primeiro gere o pacote da aplicacao:

```shell
./mvnw package
```

Depois, suba os containers:

```shell
docker compose up -d
```

Esse comando sobe o banco de dados e o `adminer`. O servico `db` cria um PostgreSQL local com as credenciais usadas pela aplicacao:

- Banco: `restaurante_pos`
- Usuario: `restaurante_user`
- Senha: `restaurante_pass`
- Porta: `5432`

Se quiser subir somente o banco, use:

```shell
docker compose up -d db
```

## Rodar o projeto em desenvolvimento

Depois que o banco estiver saudavel, rode a aplicacao em modo desenvolvimento.

No Windows:

```shell
.\mvnw.cmd quarkus:dev
```

No Linux/macOS:

```shell
./mvnw quarkus:dev
```

A API ficara disponivel em:

```text
http://localhost:8080
```

O Quarkus Dev UI fica disponivel em:

```text
http://localhost:8080/q/dev/
```

## Swagger UI e OpenAPI

A documentacao da API fica disponivel pelo Swagger UI em:

```text
http://localhost:8080/q/swagger-ui
```

O documento OpenAPI em JSON/YAML fica disponivel em:

```text
http://localhost:8080/q/openapi
```

O projeto usa a extensao `quarkus-smallrye-openapi`. A API esta mapeada com metadados no `application.properties` e com anotacoes OpenAPI no controller de usuario.

## Docker Compose completo

O arquivo `docker-compose.yml` tambem possui o servico `adminer`. Atencao: o Adminer esta configurado na porta `8080`, que e a mesma porta da aplicacao Quarkus. Para desenvolver a API localmente, prefira subir apenas o banco com `docker compose up -d db`.

## Banco de dados e migrations

As migrations Liquibase rodam automaticamente ao iniciar a aplicacao:

```properties
quarkus.liquibase.migrate-at-start=true
```

O changelog principal esta em:

```text
src/main/resources/db/db.changelog-master.sql
```

As tabelas sao criadas a partir de:

```text
src/main/resources/db/migrations/create-database.sql
```

## Endpoints

Login de usuario:

```http
POST /usuario/login
Content-Type: application/json
```

Exemplo de corpo:

```json
{
  "user": "admin",
  "password": "admin"
}
```

## Comandos uteis

Rodar testes:

```shell
.\mvnw.cmd test
```

Gerar pacote da aplicacao:

```shell
.\mvnw.cmd package
```

Parar os containers:

```shell
docker compose down
```
