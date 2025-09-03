# API de Gerenciamento de Doações Locais

Esta é uma API RESTful desenvolvida com Spring Boot para gerenciar doações locais, permitindo que usuários cadastrem doações e outros usuários solicitem essas doações.

## 🚀 Funcionalidades

### Autenticação
- **POST** `/api/auth/register` - Cadastro de novo usuário
- **POST** `/api/auth/login` - Login e obtenção do token JWT
- **GET** `/api/auth/me` - Obter dados do usuário logado

### Usuários
- **GET** `/api/users/profile` - Obter perfil do usuário
- **PUT** `/api/users/profile` - Atualizar perfil do usuário

### Doações
- **GET** `/api/donations` - Listar doações (com filtros)
- **POST** `/api/donations` - Criar nova doação
- **GET** `/api/donations/{id}` - Obter detalhes da doação
- **PUT** `/api/donations/{id}` - Atualizar doação
- **DELETE** `/api/donations/{id}` - Deletar doação
- **GET** `/api/donations/my` - Listar minhas doações
- **GET** `/api/donations/categories` - Listar categorias disponíveis
- **GET** `/api/donations/cities` - Listar cidades disponíveis

### Matches (Solicitações)
- **POST** `/api/matches` - Solicitar doação
- **GET** `/api/matches/my` - Minhas solicitações
- **GET** `/api/matches/received` - Solicitações recebidas para minhas doações
- **PUT** `/api/matches/{id}/status` - Atualizar status da solicitação
- **GET** `/api/matches/donation/{donationId}` - Listar solicitações de uma doação

## 🛠️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Security** (Autenticação JWT)
- **Spring Data JPA** (Persistência)
- **H2 Database** (Banco em memória para desenvolvimento)
- **MySQL** (Banco para produção)
- **SpringDoc OpenAPI** (Documentação Swagger)
- **ModelMapper** (Mapeamento de DTOs)
- **Maven** (Gerenciamento de dependências)

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior

## 🔧 Como executar

1. **Clone o repositório**
   ```bash
   git clone <repository-url>
   cd donation-api
   ```

2. **Execute a aplicação**
   ```bash
   mvn spring-boot:run
   ```

3. **Acesse a aplicação**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Console H2: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:testdb`
     - Username: `sa`
     - Password: `password`

## 📖 Documentação da API

A documentação completa da API está disponível através do Swagger UI em:
http://localhost:8080/swagger-ui.html

### Autenticação

A API utiliza autenticação JWT. Para acessar endpoints protegidos:

1. Faça login através do endpoint `/api/auth/login`
2. Use o token retornado no cabeçalho `Authorization: Bearer {token}`

### Exemplo de uso

1. **Cadastrar usuário:**
   ```json
   POST /api/auth/register
   {
     "name": "João Silva",
     "email": "joao@email.com",
     "password": "senha123",
     "phone": "11999999999",
     "city": "São Paulo",
     "state": "SP"
   }
   ```

2. **Fazer login:**
   ```json
   POST /api/auth/login
   {
     "email": "joao@email.com",
     "password": "senha123"
   }
   ```

3. **Criar doação:**
   ```json
   POST /api/donations
   Authorization: Bearer {token}
   {
     "title": "Roupas de inverno",
     "description": "Casacos e blusas em bom estado",
     "category": "Roupas",
     "condition": "Usado - Bom estado",
     "quantity": 5,
     "city": "São Paulo",
     "state": "SP"
   }
   ```

## 🗄️ Banco de Dados

### Para Desenvolvimento
- Utiliza H2 Database em memória
- Dados são perdidos ao reiniciar a aplicação
- Console disponível em: http://localhost:8080/h2-console

### Para Produção
- Configure MySQL no `application.yml`
- Altere as propriedades de datasource:
  ```yaml
  spring:
    datasource:
      url: jdbc:mysql://localhost:3306/donation_db
      username: seu_usuario
      password: sua_senha
      driver-class-name: com.mysql.cj.jdbc.Driver
    jpa:
      database-platform: org.hibernate.dialect.MySQLDialect
      hibernate:
        ddl-auto: update
  ```

## 🔒 Segurança

- Senhas são criptografadas com BCrypt
- Autenticação via JWT com expiração de 24 horas
- Endpoints públicos: registro, login e listagem de doações
- Endpoints protegidos requerem token JWT válido

## 📊 Status das Doações

- **AVAILABLE**: Disponível para solicitação
- **RESERVED**: Reservada (solicitação aprovada)
- **COMPLETED**: Doação concluída
- **EXPIRED**: Doação expirada
- **CANCELLED**: Doação cancelada

## 📝 Status dos Matches

- **PENDING**: Aguardando resposta do doador
- **APPROVED**: Solicitação aprovada pelo doador
- **REJECTED**: Solicitação rejeitada pelo doador
- **IN_PROGRESS**: Doação em andamento
- **COMPLETED**: Doação concluída
- **CANCELLED**: Solicitação cancelada

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👥 Equipe

- Desenvolvido para o Projeto Integrador - SENAC

## 📞 Suporte

Para dúvidas ou suporte, entre em contato através do email: contato@doacoes.com.br
