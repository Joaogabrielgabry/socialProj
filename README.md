# socialProj
CRUD Para usuarios e listas com sistema de segurança JWT para dois diferentes usuarios.

Este projeto é uma aplicação desenvolvida com Spring Boot que implementa um sistema de autenticação e autorização utilizando JWT (JSON Web Token). A aplicação suporta dois tipos de usuários com permissões diferentes: ROLE_USER_A e ROLE_USER_B. A segurança foi configurada para proteger as rotas de acordo com as regras de acesso definidas.

Funcionalidades
1. Autenticação e Autorização
Login: Geração de token JWT ao autenticar com credenciais válidas.
Cadastro: Registro de novos usuários com associação de roles.
Segurança de Rotas: Controle de acesso baseado nos papéis dos usuários (ROLE_USER_A e ROLE_USER_B).
2. Gerenciamento de Listas e Itens
Criar listas com itens: Associar itens a uma lista no momento da criação.
Consultar listas: Buscar todas as listas ou uma lista específica com seus itens.
Atualizar listas: Modificar nome, descrição e itens de uma lista existente.
Excluir listas: Remover uma lista e seus itens associados.
Excluir itens: Remover itens específicos de uma lista.
3. Gerenciamento de Usuários
Listar todos os usuários (apenas ROLE_USER_A).
Consultar detalhes de um usuário pelo ID.
Atualizar informações de um usuário, incluindo roles e senha.
Excluir usuários.
Tecnologias Utilizadas
Java: Linguagem principal.
Spring Boot: Framework para construção da aplicação.
Spring Security: Configuração de segurança.
JWT: Autenticação baseada em tokens.
Hibernate/JPA: Gerenciamento de persistência de dados.
H2 Database: Banco de dados em memória para testes (pode ser substituído por MySQL ou outro).
Swagger: Documentação interativa da API.
Configuração do Projeto
Pré-requisitos
Java 17 ou superior.
Maven para gerenciamento de dependências.
Passos para Executar
Clone o repositório:
bash
git clone <https://github.com/Joaogabrielgabry/socialProj.git>
Navegue até o diretório do projeto:
bash
cd sistema-seguranca-jwt
Compile e execute o projeto:
bash
mvn spring-boot:run
Acesse a aplicação em:
arduino
http://localhost:8000
Configuração de Segurança
Para acessar rotas protegidas, obtenha um token JWT através do login (/auth/signin).
Adicione o token no cabeçalho das requisições como:
makefile
Authorization: Bearer <seu-token-jwt>
Estrutura do Projeto
Controllers:
AuthController: Gerenciamento de autenticação e usuários.
ListaController: Operações CRUD para listas e itens.
DTOs:
Representação de dados de entrada e saída.
Entities:
User, Role, Lista, Item: Modelos de dados principais.
Repositórios:
Interfaces para persistência de dados.
Serviços:
Camada intermediária entre controllers e repositórios.
JWT Utils:
Geração e validação de tokens.
Exemplo de Requisição
Cadastro de Usuário
Endpoint: /auth/signup
Método: POST
Body:

json
{
  "username": "usuario1",
  "email": "usuario1@exemplo.com",
  "password": "senha123",
  "role": ["ROLE_USER_A"]
}
Login
Endpoint: /auth/signin
Método: POST
Body:

json
{
  "username": "usuario1",
  "password": "senha123"
}
Criar Lista com Itens
Endpoint: /listas
Método: POST
Body:

json
{
  "nome": "Lista de Compras",
  "descricao": "Lista para o mercado",
  "itens": [
    { "nome": "Arroz", "descricao": "5kg" },
    { "nome": "Feijão", "descricao": "2kg" }
  ]
}
Listar Todas as Listas
Endpoint: /listas
Método: GET

Remover Item de uma Lista
Endpoint: /listas/{id}/itens/{itemId}
Método: DELETE


