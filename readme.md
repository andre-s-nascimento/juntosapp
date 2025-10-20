# JuntosApp

Aplicação Spring Boot para gerenciamento de usuários com autenticação JWT, seguindo conceitos de **Clean Code** e **Domain-Driven Design (DDD)**.

---

## Funcionalidades

### 1. Autenticação e Autorização

- Login de usuários via email e senha.
- Geração de **JWT** para autenticação de requisições futuras.
- Proteção de endpoints REST (ex: `/api/users`) para usuários logados.
- **Tecnologias/Conceitos**:
  - Spring Security
  - BCryptPasswordEncoder
  - Clean Code: separação clara entre `AuthService`, `UserService` e `UserController`
  - DDD: autenticação na camada de aplicação (`AuthService`), isolada da persistência

---

### 2. Gerenciamento de Usuários

- Criação de usuários e administradores via API.
- Listagem de usuários ou busca por ID/email.
- Persistência em **H2** (memória) para ambiente de desenvolvimento.
- **Tecnologias/Conceitos**:
  - DDD:
    - `User` como entidade de domínio
    - `Role` como enum de valor do domínio
    - `UserRepository` como interface de repositório do domínio
  - Spring Boot: Controllers, Services, Repositories
  - Clean Code: métodos claros (`registerUser`, `registerAdmin`, `listAll`), adapter pattern (`UserRepositoryJpaAdapter`)

---

### 3. Separação de Camadas

- Divisão em camadas:
  1. **Domain** – entidades e regras de negócio (`User`, `Role`)
  2. **Application** – serviços que executam operações (`UserService`, `AuthService`)
  3. **Infrastructure** – acesso a banco (`UserEntity`, `UserJpaRepository`, Adapter)
  4. **Interface** – API REST (`UserController`)
- **Conceitos aplicados**:
  - DDD: separação clara de domínio e infraestrutura
  - Clean Architecture: dependência das camadas vai de fora para dentro (domínio não conhece Spring/JPA)

---

### 4. Inicialização de Dados

- Criação de usuários iniciais na inicialização da aplicação (`DataInitializer`).
- Evita dependência de `data.sql`.
- Garante que o `role` seja corretamente definido.
- **Conceitos aplicados**:
  - Spring Boot: `CommandLineRunner`
  - DDD / Clean Code: usa `UserRepository` do domínio, sem manipular `UserEntity` diretamente

---

### 5. JWT e Roles

- Geração de tokens JWT baseados em email e role do usuário.
- Controle de acesso baseado em **roles** (`ADMIN` / `USER`).
- **Conceitos aplicados**:
  - Spring Security: autenticação via token Bearer
  - DDD: `Role` como valor do domínio
  - Clean Code: separação de responsabilidades (`JwtService` e `AuthService`)

---

### 6. Tratamento de Exceções (planejado)

- Redução de stacktraces nos logs.
- Retorno de mensagens amigáveis (ex: "Usuário não encontrado", "Senha incorreta").
- **Conceitos aplicados**:
  - Spring `@ControllerAdvice` para captura global de exceções
  - Clean Code: centralização do tratamento de erros
  - DDD: camada de aplicação lança exceções, camada de interface converte para HTTP

---

### 7. Boas práticas aplicadas

- Separation of Concerns (SoC): cada classe tem responsabilidade clara.
- Dependency Injection: injeção via construtor.
- Encapsulamento: atributos privados, métodos de acesso (`getters/setters`).
- Adapter Pattern: `UserRepositoryJpaAdapter` desacopla JPA do domínio.
- Imutabilidade parcial: IDs gerados no construtor e não alterados posteriormente.

---

## Estrutura do Projeto

```bash
src/main/java/net/ab79/juntos/juntosapp/
│
├─ JuntosappApplication.java # Classe principal do Spring Boot
│
├─ admin
│ └─ interfaces/controller
│    └─ AdminController.java # Endpoints administrativos
│
├─ auth
│ ├─ application
│ │ ├─ AuthService.java # Lógica de autenticação
│ │ └─ JwtService.java # Serviço JWT
│ ├─ infrastructure
│ │ └─ JwtAuthenticationFilter.java # Filtro de autenticação JWT
│ └─ interfaces
│ └─ AuthController.java # Endpoints de login
│
├─ config
│ ├─ DataInitializer.java # Popula dados iniciais (usuários/admin)
│ └─ SecurityConfig.java # Configuração do Spring Security
│
├─ shared
│ └─ GlobalExceptionHandler.java # Tratamento global de exceções
│
└─ users
├─ application/service
│ └─ UserService.java # Regras de negócio do usuário
├─ domain/model
│ ├─ Role.java # Enum de papéis
│ └─ User.java # Modelo de domínio
├─ domain/repository
│ └─ UserRepository.java # Interface do repositório
├─ infrastructure/entity
│ └─ UserEntity.java # Entidade JPA
└─ infrastructure/repository
├─ UserJpaRepository.java # JpaRepository padrão
└─ UserRepositoryJpaAdapter.java # Adapter para Domain Model
└─ interfaces/controller
└─ UserController.java # Endpoints CRUD de usuários
```

---

### Tecnologias

- Spring Boot 3
- Spring Security
- Spring Data JPA
- H2 Database (dev)
- JWT
- Java 17+
- Maven

---

### Conceitos aplicados

- **DDD (Domain-Driven Design)**: separação de domínio, aplicação, infraestrutura e interface
- **Clean Code**: métodos claros, classes pequenas, responsabilidades únicas
- **Design Patterns**: Adapter, Dependency Injection
- **Boas práticas Spring**: CommandLineRunner, @ControllerAdvice, @Service, @Repository
