# Checklist --- Backend (Spring Boot)

## 1. Criar o projeto

-   [x] Abrir o Spring Initializr
-   [x] Project: Maven
-   [x] Language: Java
-   [x] Spring Boot: versão 3.x atual
-   [x] Java: 21
-   [x] Group: definir, por exemplo `br.com.seuprojeto`
-   [x] Artifact: `estoque-api`
-   [x] Packaging: Jar

## 2. Adicionar dependências / Starters

No Spring Initializr:

-  [x] Spring Web  
-  [x] Spring Data JPA  
-  [x] Spring Security  
-  [x] Validation  
-  [x] PostgreSQL Driver  
-  [x] Flyway Migration  
-  [x] Spring Boot Actuator (opcional)

Adicionar manualmente ao `pom.xml`: - \[ \] springdoc-openapi - \[ \]
Testcontainers - \[ \] Módulo PostgreSQL do Testcontainers

## 3. Configurar o projeto

- [x] Configurar `application.yml`
- [x] Criar perfil `dev`
- [x] Criar perfil `prod`
- [x] Configurar conexão PostgreSQL
- [x] Configurar `ddl-auto=validate`
- [x] Configurar timezone
- [x] Configurar porta da API
- [x] Configurar tamanho máximo de upload de imagem
- [x] Configurar CORS para o Angular

Estrutura inicial:

``` text
src/main/resources/
├── application.yml
└── db/
    └── migration/
```

## 4. Configurar o banco de dados

-   [x] Criar banco PostgreSQL
-   [x] Configurar usuário e senha
-   [x] Configurar conexão no Spring
-   [x] Criar `V1__create_tables.sql`
-   [x] Criar tabela `users`
-   [x] Criar tabela `products`
-   [x] Criar tabela `sales`
-   [x] Criar tabela `stock_movements`
-   [x] Criar chaves primárias
-   [x] Criar chaves estrangeiras
-   [x] Criar constraints
-   [x] Criar índices
-   [x] Executar a aplicação e verificar o Flyway

## 5. Criar estrutura de pacotes

``` text
br.com.seuprojeto.estoque/
├── common/
├── security/
├── auth/
├── user/
├── product/
│   └── image/
├── stock/
├── sale/
└── dashboard/
```

-   [ ] Criar `common`
-   [ ] Criar `security`
-   [ ] Criar `auth`
-   [ ] Criar `user`
-   [ ] Criar `product`
-   [ ] Criar `product.image`
-   [ ] Criar `stock`
-   [ ] Criar `sale`
-   [ ] Criar `dashboard`

## 6. Criar entidades

### User

-   [ ] Criar `User`
-   [ ] `id`
-   [ ] `name`
-   [ ] `email`
-   [ ] `passwordHash`
-   [ ] `createdAt`

### Product

-   [ ] Criar `Product`
-   [ ] `id`
-   [ ] `owner`
-   [ ] `name`
-   [ ] `description`
-   [ ] `price`
-   [ ] `quantityInStock`
-   [ ] `lowStockThreshold`
-   [ ] `imagePath`
-   [ ] `archivedAt`
-   [ ] `createdAt`
-   [ ] `updatedAt`

### Sale

-   [ ] Criar `Sale`
-   [ ] `id`
-   [ ] `owner`
-   [ ] `product`
-   [ ] `productName`
-   [ ] `customerName`
-   [ ] `saleDate`
-   [ ] `quantity`
-   [ ] `unitPrice`
-   [ ] `totalAmount`
-   [ ] `status`
-   [ ] `canceledAt`
-   [ ] `createdAt`

### StockMovement

-   [ ] Criar `StockMovement`
-   [ ] `id`
-   [ ] `product`
-   [ ] `sale`
-   [ ] `type`
-   [ ] `quantityChange`
-   [ ] `quantityAfter`
-   [ ] `reason`
-   [ ] `createdAt`

## 7. Criar repositories

-   [ ] `UserRepository`
-   [ ] `ProductRepository`
-   [ ] `SaleRepository`
-   [ ] `StockMovementRepository`
-   [ ] Criar consultas necessárias para paginação
-   [ ] Criar busca de produtos
-   [ ] Criar filtros de estoque
-   [ ] Criar filtros de vendas
-   [ ] Criar consultas de movimentações

## 8. Criar DTOs

-   [ ] DTO de cadastro de usuário
-   [ ] DTO de login
-   [ ] DTO de resposta de usuário
-   [ ] DTO de criação de produto
-   [ ] DTO de atualização de produto
-   [ ] DTO de resposta de produto
-   [ ] DTO de ajuste de estoque
-   [ ] DTO de criação de venda
-   [ ] DTO de resposta de venda
-   [ ] DTO de movimentação
-   [ ] Usar `record`
-   [ ] Não retornar entidades JPA diretamente

## 9. Implementar autenticação

-   [ ] Criar cadastro
-   [ ] Criptografar senha com BCrypt
-   [ ] Criar login
-   [ ] Implementar JWT
-   [ ] Criar filtro JWT
-   [ ] Configurar Spring Security
-   [ ] Criar `SecurityConfig`
-   [ ] Identificar usuário autenticado
-   [ ] Garantir que o usuário só veja os próprios dados

Endpoints:

``` http
POST /api/v1/auth/register
POST /api/v1/auth/login
```

## 10. Implementar produtos

-   [ ] Criar `ProductService`
-   [ ] Criar `ProductController`
-   [ ] Criar produto
-   [ ] Editar produto
-   [ ] Listar produtos
-   [ ] Buscar por nome
-   [ ] Filtrar estoque
-   [ ] Ver detalhes
-   [ ] Arquivar produto
-   [ ] Impedir venda de produto arquivado

Endpoints:

``` http
GET    /api/v1/products
GET    /api/v1/products/{id}
POST   /api/v1/products
PUT    /api/v1/products/{id}
DELETE /api/v1/products/{id}
```

## 11. Implementar estoque

-   [ ] Criar `StockService`
-   [ ] Implementar movimentação `INITIAL`
-   [ ] Implementar `ADJUSTMENT`
-   [ ] Implementar `SALE`
-   [ ] Implementar `SALE_CANCELLATION`
-   [ ] Implementar ajuste de entrada
-   [ ] Implementar ajuste de saída
-   [ ] Impedir estoque negativo
-   [ ] Criar histórico de movimentações
-   [ ] Criar resumo do estoque

Endpoints:

``` http
GET  /api/v1/stock/summary
POST /api/v1/products/{id}/stock-adjustments
GET  /api/v1/products/{id}/stock-movements
```

## 12. Implementar vendas

-   [ ] Criar `SaleService`
-   [ ] Criar `SaleController`
-   [ ] Selecionar produto
-   [ ] Verificar estoque
-   [ ] Verificar produto arquivado
-   [ ] Calcular `totalAmount`
-   [ ] Salvar snapshot do nome
-   [ ] Salvar snapshot do preço
-   [ ] Baixar estoque
-   [ ] Criar movimentação `SALE`
-   [ ] Usar `@Transactional`
-   [ ] Usar bloqueio pessimista na venda
-   [ ] Implementar cancelamento
-   [ ] Devolver estoque no cancelamento
-   [ ] Criar movimentação de cancelamento

Endpoints:

``` http
GET  /api/v1/sales
GET  /api/v1/sales/{id}
POST /api/v1/sales
POST /api/v1/sales/{id}/cancel
```

## 13. Tratamento de erros

-   [ ] Criar handler global
-   [ ] Usar `ProblemDetail`
-   [ ] Criar códigos de erro
-   [ ] Erros de validação: HTTP 400
-   [ ] Recurso não encontrado: HTTP 404
-   [ ] Estoque insuficiente: HTTP 409
-   [ ] Produto arquivado: HTTP 409
-   [ ] Criar mensagens padronizadas

## 14. Upload de imagens

-   [ ] Criar interface `ImageStorage`
-   [ ] Criar `LocalImageStorage`
-   [ ] Validar JPEG, PNG e WebP
-   [ ] Definir tamanho máximo
-   [ ] Salvar arquivo
-   [ ] Salvar apenas o caminho no banco
-   [ ] Criar endpoint de upload
-   [ ] Criar endpoint para recuperar imagem

## 15. Dashboard

-   [ ] Criar `StockSummaryService`
-   [ ] Consultar total de produtos
-   [ ] Consultar total de itens em estoque
-   [ ] Consultar produtos com estoque baixo
-   [ ] Consultar produtos sem estoque
-   [ ] Consultar últimas vendas

## 16. Documentação e testes

-   [ ] Configurar Swagger/OpenAPI
-   [ ] Testar endpoints
-   [ ] Testar autenticação
-   [ ] Testar regras de estoque
-   [ ] Testar venda
-   [ ] Testar cancelamento
-   [ ] Testar produto arquivado
-   [ ] Criar testes unitários
-   [ ] Criar testes de integração
-   [ ] Configurar Testcontainers com PostgreSQL
