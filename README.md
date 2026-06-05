# Controla Preju API 💰

Essa API é uma solução robusta 
para gestão financeira, permitindo o controle de contas, cartões de crédito, receitas, despesas e transferências de forma organizada e segura. Desenvolvida com Java/Spring.

## 🚀 Funcionalidades Principais

- **Autenticação e Segurança:**
  - Login com JWT (JSON Web Token) e Refresh Token.
  - Recuperação de senha e confirmação de e-mail.
  - Rate Limiting para proteção contra ataques de força bruta.
- **Gestão de Contas:**
  - Suporte a múltiplos tipos de contas (Corrente, Poupança, Investimento, etc.).
  - Controle de saldo total.
- **Transações Financeiras:**
  - Registro de Receitas e Despesas com categorização.
  - Transferências entre contas do usuário.
  - Agendamento de transações.
- **Cartão de Crédito e Faturas:**
  - Gestão de cartões de crédito e limites.
  - Geração automática de faturas.
  - Pagamento de faturas integrado ao saldo das contas.
- **Relatórios e Histórico:**
  - Histórico detalhado de transações.

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- **Framework:** [Spring Boot 3.5.6](https://spring.io/projects/spring-boot)
- **Banco de Dados:** [PostgreSQL](https://www.postgresql.org/)
- **Migrações:** [Flyway](https://flywaydb.org/)
- **Segurança:** Spring Security & [Auth0 Java JWT](https://github.com/auth0/java-jwt)
- **Documentação:** [SpringDoc OpenAPI (Swagger)](https://springdoc.org/)
- **Resiliência:** [Bucket4j](https://bucket4j.com/) (Rate Limiting)
- **Email:** Spring Mail
- **Utilitários:** Lombok, Jakarta Validation

## 🛠️ Infraestrutura & DevOps

- **Containerização:** Uso de **Docker** e **Docker Compose** para garantir um ambiente de desenvolvimento consistente e simplificar o processo de deploy.
- **Testes Automatizados:** Testes unitários para validação das regras de negócio, assegurando a confiabilidade da aplicação.
- **Integração Contínua (CI/CD):** Pipeline configurado via **GitHub Actions** que automatiza o build e a execução dos testes a cada atualização no código.

## 📂 Estrutura do Projeto

```text
src/main/java/api/controla_preju/
├── config/         # Configurações globais (Segurança, MVC, Rate Limit)
├── controllers/    # Endpoints da API
├── dtos/           # Objetos de transferência de dados (Forms e Views)
├── entities/       # Entidades JPA (Modelo de dados)
├── exceptions/     # Tratamento global de erros
├── repositories/   # Interfaces de acesso ao banco de dados
├── schedulers/     # Tarefas agendadas
├── security/       # Filtros e lógica de autenticação
└── services/       # Regras de negócio da aplicação
```

## 📄 Licença

Este projeto é proprietário. Todos os direitos reservados. O código é disponibilizado publicamente apenas para fins de visualização e portfólio. Não é permitida a cópia, modificação ou distribuição para qualquer finalidade sem autorização prévia.
