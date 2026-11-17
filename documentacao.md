# Documentação Inicial — Controle de Estoque e Registro de Vendas

Versão 0.1 · 17/11/2026 · Rascunho para validação antes da implementação

## 1. Visão geral do projeto

Aplicação web para pequenos vendedores e revendedores cadastrarem seus produtos, acompanharem o estoque e registrarem vendas que já aconteceram fora do sistema (pessoalmente, por mensagem, etc.). Funciona como um caderno digital organizado: **não vende, não cobra e não integra pagamentos**.

Arquitetura em resumo: SPA em Angular, API REST em Java + Spring Boot (monólito modular) e PostgreSQL. Três peças, um único backend, deploy simples.

## 2. Objetivo da aplicação

- Substituir caderno ou planilha por algo simples e confiável.
- Mostrar em tempo real quanto resta de cada produto e o que está acabando.
- Manter um histórico de vendas consultável, com valores que não mudam depois de registrados.
- Critério de sucesso: cadastrar um produto em cerca de 1 minuto e registrar uma venda em poucos toques, inclusive pelo celular.

## 3. Escopo

**Dentro do escopo inicial**

- Cadastro, edição, visualização e exclusão (lógica) de produtos, com imagem.
- Controle de estoque atualizado por movimentações (cadastro, venda, ajuste, cancelamento).
- Registro e consulta do histórico de vendas.
- Indicadores básicos de estoque.
- Login simples (ver ponto a definir nº 1).

**Fora do escopo inicial**

Pagamentos, carrinho, loja virtual, marketplace, nota fiscal, APIs de terceiros, controle financeiro completo, contabilidade, WhatsApp, transportadoras, permissões complexas e microserviços.

**Pressupostos**

- Idioma único (pt-BR) e moeda única (BRL).
- Uso principal em celular e computador, por poucos usuários simultâneos.
- Volume pequeno: centenas de produtos e alguns milhares de vendas por vendedor.

## 4. Funcionalidades

**F1. Produtos.** Listar (com busca e filtro), ver detalhes, cadastrar, editar e excluir. Campos: nome, preço, quantidade em estoque, imagem e descrição opcional.

**F2. Estoque.** Saldo atual por produto, histórico de movimentações, ajuste manual (reposição ou correção) e indicadores: total de produtos, total de itens em estoque, produtos com estoque baixo e produtos sem estoque.

**F3. Vendas.** Registrar venda já realizada (produto, cliente, data, quantidade, preço unitário), calcular o total automaticamente, baixar o estoque, consultar histórico com filtros e cancelar venda registrada por engano.

**F4. Painel inicial.** Tela com os indicadores de estoque e as últimas vendas.

**F5. Acesso.** Cadastro e login do vendedor; cada vendedor vê apenas os próprios dados.

## 5. Regras de negócio

- **RN01.** O estoque nunca pode ficar negativo.
- **RN02.** Não é possível registrar venda com quantidade maior que o estoque disponível.
- **RN03.** Ao registrar uma venda, a quantidade vendida é subtraída do estoque na mesma transação.
- **RN04.** O total da venda é calculado pelo sistema: `quantidade × preço unitário`. O cliente da API não envia o total.
- **RN05.** O preço unitário e o nome do produto são gravados na venda (snapshot) e nunca mudam, mesmo que o produto seja alterado depois.
- **RN06.** O preço unitário da venda vem preenchido com o preço atual do produto, mas pode ser alterado no momento do registro (desconto, preço combinado).
- **RN07.** Quantidade vendida deve ser inteira e maior que zero; preço não pode ser negativo.
- **RN08.** A data da venda não pode ser futura; datas passadas são permitidas.
- **RN09.** Produto com vendas nunca é apagado fisicamente: a exclusão é lógica (arquivamento). Produto arquivado some das listas e não aceita novas vendas, mas continua aparecendo no histórico.
- **RN10.** Vendas não são editadas. Para corrigir um erro, a venda é cancelada (o estoque retorna) e uma nova é registrada.
- **RN11.** Estoque baixo: quantidade maior que zero e menor ou igual ao limite do produto (padrão 5). Sem estoque: quantidade igual a zero.
- **RN12.** A quantidade em estoque só muda por movimentação: venda, cancelamento de venda ou ajuste manual (o cadastro inicial gera a movimentação de entrada inicial).
- **RN13.** Cada vendedor acessa somente os próprios produtos e vendas.

## 6. Requisitos funcionais

- **RF01.** Cadastrar produto com nome, preço, quantidade inicial, imagem e descrição opcional.
- **RF02.** Editar nome, preço, descrição, imagem e limite de estoque baixo de um produto.
- **RF03.** Listar produtos com paginação, busca por nome e filtro (todos, estoque baixo, sem estoque).
- **RF04.** Exibir detalhes do produto com saldo atual e histórico de movimentações.
- **RF05.** Excluir produto (arquivamento, conforme RN09).
- **RF06.** Ajustar estoque manualmente, informando entrada ou saída e um motivo curto.
- **RF07.** Exibir indicadores: total de produtos, total de itens em estoque, quantidade de produtos com estoque baixo e sem estoque.
- **RF08.** Registrar venda e baixar o estoque automaticamente.
- **RF09.** Listar histórico de vendas com paginação e filtros por período, produto e cliente.
- **RF10.** Exibir detalhes de uma venda.
- **RF11.** Cancelar venda, devolvendo a quantidade ao estoque (a confirmar, ver ponto nº 4).
- **RF12.** Cadastrar vendedor e autenticar com e-mail e senha.

## 7. Requisitos não funcionais

- **Simplicidade de uso:** telas objetivas, fluxo de venda em no máximo uma tela, layout responsivo pensando primeiro no celular.
- **Consistência:** operações de estoque e venda atômicas (transação única) e protegidas contra concorrência.
- **Segurança:** senhas com BCrypt, autenticação por token (JWT) ou sessão, HTTPS obrigatório em produção, CORS restrito ao domínio do front, validação de entrada no backend e filtro por vendedor em toda consulta.
- **Desempenho:** listagens paginadas; respostas abaixo de 500 ms para o volume previsto.
- **Confiabilidade:** backup diário do banco e migrações versionadas.
- **Manutenibilidade:** código organizado por módulo, testes automatizados nas regras de negócio e documentação da API (OpenAPI).
- **Custo:** executar em um servidor pequeno ou plataforma de baixo custo; tudo empacotado em Docker.
- **Privacidade:** nomes de clientes são dados pessoais; coletar o mínimo e não expor em logs (cuidados básicos da LGPD).
- **Acessibilidade:** contraste adequado, rótulos nos campos e navegação por teclado.

## 8. Entidades principais e relacionamentos

- **Usuário (vendedor):** dono dos dados.
- **Produto:** item vendido, com preço atual e saldo em estoque.
- **Venda:** registro de uma venda já ocorrida, de um produto, com valores congelados.
- **Movimentação de estoque:** cada alteração do saldo de um produto.

Relacionamentos:

- Usuário 1 — N Produto
- Usuário 1 — N Venda
- Produto 1 — N Venda (a venda aponta para o produto e guarda o snapshot)
- Produto 1 — N Movimentação de estoque
- Venda 1 — N Movimentação de estoque (a venda gera uma movimentação de saída e, se cancelada, outra de retorno)

## 9. Modelo inicial do banco de dados (PostgreSQL)

Convenções: tabelas em inglês e snake\_case, chaves `BIGINT` geradas por identity, valores monetários em `NUMERIC(12,2)` (nunca `float`), datas de auditoria em `TIMESTAMPTZ`.

**users**

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| name | VARCHAR(120) | NOT NULL |
| email | VARCHAR(255) | NOT NULL, UNIQUE |
| password\_hash | VARCHAR(255) | NOT NULL |
| created\_at | TIMESTAMPTZ | NOT NULL, default now() |

**products**

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| owner\_id | BIGINT | FK users, NOT NULL |
| name | VARCHAR(150) | NOT NULL |
| description | VARCHAR(1000) | opcional |
| price | NUMERIC(12,2) | NOT NULL, CHECK (price >= 0) |
| quantity\_in\_stock | INTEGER | NOT NULL, default 0, CHECK (quantity\_in\_stock >= 0) |
| low\_stock\_threshold | INTEGER | NOT NULL, default 5, CHECK (>= 0) |
| image\_path | VARCHAR(255) | opcional, caminho ou chave do arquivo |
| archived\_at | TIMESTAMPTZ | NULL = ativo; preenchido = excluído logicamente |
| created\_at / updated\_at | TIMESTAMPTZ | NOT NULL |

**sales**

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| owner\_id | BIGINT | FK users, NOT NULL |
| product\_id | BIGINT | FK products, ON DELETE RESTRICT |
| product\_name | VARCHAR(150) | NOT NULL, snapshot do nome |
| customer\_name | VARCHAR(150) | NOT NULL |
| sale\_date | DATE | NOT NULL |
| quantity | INTEGER | NOT NULL, CHECK (quantity > 0) |
| unit\_price | NUMERIC(12,2) | NOT NULL, CHECK (unit\_price >= 0) |
| total\_amount | NUMERIC(12,2) | NOT NULL, CHECK (total\_amount = quantity \* unit\_price) |
| status | VARCHAR(20) | NOT NULL, default COMPLETED (COMPLETED ou CANCELED) |
| canceled\_at | TIMESTAMPTZ | opcional |
| created\_at | TIMESTAMPTZ | NOT NULL |

**stock\_movements**

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | BIGINT | PK |
| product\_id | BIGINT | FK products, NOT NULL |
| sale\_id | BIGINT | FK sales, opcional |
| type | VARCHAR(25) | INITIAL, SALE, SALE\_CANCELLATION ou ADJUSTMENT |
| quantity\_change | INTEGER | NOT NULL, com sinal (ex.: -2), diferente de zero |
| quantity\_after | INTEGER | NOT NULL, saldo após a movimentação |
| reason | VARCHAR(255) | opcional (obrigatório em ajustes) |
| created\_at | TIMESTAMPTZ | NOT NULL |

**Índices sugeridos:** `products (owner_id, archived_at)`, `sales (owner_id, sale_date DESC)`, `sales (product_id)`, `stock_movements (product_id, created_at DESC)`.

**Migrações:** Flyway, com um script versionado por alteração (`V1__create_tables.sql`, etc.). O Hibernate não deve criar ou alterar tabelas (`ddl-auto=validate`).

## 10. Endpoints REST sugeridos

Prefixo `/api/v1`. Todos, exceto autenticação, exigem token. Listagens aceitam `page`, `size` e `sort`.

**Autenticação**

| Método | Rota | Descrição |
| --- | --- | --- |
| POST | /auth/register | Cria o vendedor |
| POST | /auth/login | Retorna o token |

**Produtos**

| Método | Rota | Descrição |
| --- | --- | --- |
| GET | /products?search=&stock=all\|low\|out | Lista produtos ativos |
| GET | /products/{id} | Detalhes |
| POST | /products | Cadastra (gera a movimentação INITIAL) |
| PUT | /products/{id} | Edita dados (não altera o saldo) |
| DELETE | /products/{id} | Arquiva o produto |
| PUT | /products/{id}/image | Envia ou troca a imagem (multipart) |
| GET | /products/{id}/image | Retorna a imagem |

**Estoque**

| Método | Rota | Descrição |
| --- | --- | --- |
| GET | /stock/summary | Total de produtos, total de itens, estoque baixo, sem estoque |
| POST | /products/{id}/stock-adjustments | Ajuste manual com motivo |
| GET | /products/{id}/stock-movements | Histórico de movimentações |

**Vendas**

| Método | Rota | Descrição |
| --- | --- | --- |
| GET | /sales?from=&to=&productId=&customer= | Histórico com filtros |
| GET | /sales/{id} | Detalhes |
| POST | /sales | Registra venda (productId, customerName, saleDate, quantity, unitPrice) |
| POST | /sales/{id}/cancel | Cancela venda e devolve o estoque |

**Padrão de erros:** `ProblemDetail` (RFC 7807, nativo no Spring 6) com um campo `code`. Exemplos: 400 para validação, 404 para recurso inexistente, 409 com `INSUFFICIENT_STOCK` quando a quantidade excede o saldo e 409 com `PRODUCT_ARCHIVED` ao vender produto arquivado.

## 11. Estrutura sugerida do backend (Spring Boot)

Monólito modular organizado **por funcionalidade** (não por camada técnica), de modo que cada módulo possa virar um serviço separado no futuro, se um dia for preciso.

```
src/main/java/br/.../estoque/
├── EstoqueApplication.java
├── common/            # exceções, handler global de erros, config, utilitários
├── security/          # configuração do Spring Security, JWT, filtro
├── auth/              # AuthController, AuthService, DTOs
├── user/              # entidade User, repositório
├── product/           # Product, ProductController, ProductService, ProductRepository, DTOs
│   └── image/         # ImageStorage (interface) e LocalImageStorage
├── stock/             # StockMovement, StockService, StockController
├── sale/              # Sale, SaleController, SaleService, SaleRepository, DTOs
└── dashboard/         # (opcional) StockSummaryService
src/main/resources/
├── application.yml (+ perfis dev e prod)
└── db/migration/      # scripts Flyway
```

**Tecnologias:** Java 21, Spring Boot 3.x, Spring Web, Spring Data JPA, Spring Security, Bean Validation, Flyway, springdoc-openapi, JUnit 5 e Testcontainers (PostgreSQL real nos testes), Maven.

**Práticas:**

- Controllers finos; regras de negócio nos services.
- DTOs como `record`; entidades JPA nunca expostas na API.
- `@Transactional` nos casos de uso que alteram estoque.
- A venda depende apenas do `StockService` (sem acessar o repositório de estoque diretamente), mantendo as fronteiras entre módulos.
- Mapeamento manual entre entidade e DTO (suficiente para este tamanho, sem biblioteca extra).

## 12. Estrutura sugerida do frontend (Angular)

Uso de componentes standalone, rotas com carregamento tardio (lazy loading), formulários reativos e signals para estado local, na versão LTS vigente do Angular. Biblioteca de componentes: Angular Material, para ganhar tempo em formulários, tabelas, diálogos e responsividade.

```
src/app/
├── core/              # AuthService, interceptors (token, erros), guards, config
├── shared/            # componentes reutilizáveis (diálogo de confirmação, estado vazio), pipes (moeda, data)
├── features/
│   ├── auth/          # login, cadastro
│   ├── dashboard/     # indicadores e últimas vendas
│   ├── products/      # lista, formulário (criar/editar), detalhes, ajuste de estoque
│   └── sales/         # lista com filtros, formulário de venda, detalhes
├── app.routes.ts
└── app.config.ts
```

**Telas principais:** login, painel, lista de produtos (cards no celular, tabela no desktop), formulário de produto, detalhes do produto com histórico de estoque, lista de vendas e formulário de nova venda.

**Cuidados:** interceptor de erros que exibe a mensagem da API; botão de salvar desabilitado durante o envio (evita venda duplicada); validações no formulário espelhando as do backend (que continua sendo a fonte da verdade); `environment` separado para a URL da API.

## 13. Fluxo das principais operações

**Cadastro de produto**

1. O usuário preenche nome, preço, quantidade inicial e, opcionalmente, descrição e imagem.
2. O front valida os campos e envia `POST /products`.
3. O backend valida (nome obrigatório, preço maior ou igual a zero, quantidade maior ou igual a zero).
4. Em uma transação: grava o produto e cria a movimentação `INITIAL` com a quantidade informada.
5. Se houver imagem, o front envia `PUT /products/{id}/image`; o backend valida tipo (JPEG, PNG ou WebP) e tamanho, grava o arquivo e salva o caminho.
6. O front retorna à lista com a confirmação.

**Registro de venda**

1. O usuário escolhe o produto; o preço unitário vem preenchido com o preço atual e o estoque disponível aparece na tela.
2. Informa cliente, data e quantidade (e ajusta o preço, se necessário). O total é exibido na tela como prévia.
3. O front envia `POST /sales`.
4. O backend, em **uma única transação**:
   - busca o produto do vendedor com bloqueio de escrita e confirma que não está arquivado;
   - verifica `quantidade <= saldo`; se não, responde 409 `INSUFFICIENT_STOCK`;
   - calcula `total = quantidade × preço unitário`;
   - grava a venda com o snapshot (nome e preço unitário);
   - subtrai o saldo e cria a movimentação `SALE` com `quantity_change` negativo.
5. A API devolve a venda criada; o front exibe a confirmação e o novo saldo.

**Cancelamento de venda**

1. O usuário cancela uma venda do histórico, com confirmação.
2. Em uma transação: marca a venda como `CANCELED`, devolve a quantidade ao saldo e cria a movimentação `SALE_CANCELLATION`.
3. A venda permanece no histórico, com o status visível.

**Exclusão de produto**

1. O usuário confirma a exclusão.
2. O backend preenche `archived_at`. O produto some das listas e novas vendas são bloqueadas.
3. O histórico de vendas continua intacto, exibindo o nome gravado na venda.

## 14. Decisões técnicas importantes

| Decisão | Recomendação | Motivo |
| --- | --- | --- |
| Arquitetura | Monólito modular por funcionalidade | Simples de desenvolver, testar e implantar; permite evoluir |
| Exclusão de produto | Exclusão lógica (`archived_at`) + snapshot do nome na venda + FK com RESTRICT | Preserva o histórico sem perder integridade; é mais simples que duplicar tabelas |
| Preço na venda | Gravar `unit_price` e `total_amount` na venda | Valores históricos não dependem do produto |
| Saldo de estoque | Coluna `quantity_in_stock` em products **e** tabela `stock_movements` | Leitura rápida do saldo e auditoria de como ele chegou ali |
| Concorrência | Bloqueio pessimista (`PESSIMISTIC_WRITE`) ao vender e `CHECK (quantity_in_stock >= 0)` | Evita venda dupla do mesmo saldo; o banco é a última barreira. Para este volume, o bloqueio é simples e suficiente |
| Vendas por registro | Um produto por venda | Simples e igual ao exemplo do enunciado; evolução prevista para itens múltiplos |
| Cliente | Texto livre na venda, sem cadastro de clientes | Reduz telas e regras; o cadastro pode vir depois |
| Dinheiro | `NUMERIC(12,2)` no banco e `BigDecimal` no Java | Evita erros de arredondamento |
| Imagens | Arquivo em disco/volume, só o caminho no banco, atrás da interface `ImageStorage` | Mantém o banco leve e o custo baixo; trocar por armazenamento de objetos (S3 ou similar) depois exige só uma nova implementação |
| Autenticação | E-mail e senha, JWT, sem papéis | Atende o acesso por vendedor sem permissões complexas |
| Migrações | Flyway | Banco versionado e reproduzível em qualquer ambiente |
| Erros da API | `ProblemDetail` com `code` | Padrão do Spring e fácil de tratar no Angular |
| Interface | Angular Material, mobile first | Menos esforço de UI e componentes acessíveis |
| Deploy | Docker Compose (API, PostgreSQL e Nginx servindo o Angular) em uma VPS pequena; PaaS com PostgreSQL gerenciado é a alternativa | Baixo custo e poucos componentes para operar |
| Testes | Unitários nas regras de venda e estoque; integração com Testcontainers | As regras críticas ficam protegidas contra regressão |

## 15. Possíveis problemas e pontos a definir antes da implementação

1. **Login e multiusuário.** O enunciado não menciona autenticação, mas uma aplicação web com dados de clientes e vendas precisa de login. A documentação assume um login simples com `owner_id` em produtos e vendas (isso evita uma migração difícil depois). Se o uso for de um único vendedor, o módulo pode ser reduzido.
2. **Como o estoque aumenta.** A edição do produto não altera o saldo. Entrada de mercadoria e correções entram pelo ajuste de estoque (RF06). Confirmar se esse fluxo atende ou se a edição deve permitir mudar a quantidade (que viraria um ajuste automático).
3. **Venda com vários produtos.** Hoje, cada registro tem um produto. Se na prática o vendedor registra uma compra com vários itens, será preciso `sale_items` já na primeira versão.
4. **Cancelamento de venda.** Não está no enunciado, mas sem ele não há como corrigir um registro errado sem quebrar o estoque. Recomendação: manter o cancelamento no escopo inicial, por ser pequeno e necessário à consistência.
5. **Cliente obrigatório?** O enunciado lista o cliente como campo mínimo, mas muitos vendedores não anotam o nome. Decidir se vira opcional.
6. **Limite de estoque baixo.** Definido como campo por produto (padrão 5). Alternativa: um valor global único.
7. **Nomes duplicados de produtos.** Permitir ou impedir (índice único por vendedor entre produtos ativos)?
8. **Armazenamento das imagens em produção.** Disco local exige volume persistente e backup; confirmar a hospedagem antes de fixar a solução.
9. **Limites de imagem.** Definir tamanho máximo (sugestão: 2 MB), formatos aceitos e se o backend redimensiona.
10. **Fuso horário e datas.** A data da venda é um `DATE` sem fuso; o horário de criação fica em UTC. Confirmar que o fuso do vendedor (Brasil) basta.
11. **Produto arquivado.** Prever se haverá tela para restaurar. Pela regra atual, não há (fica como evolução).
12. **LGPD.** Definir política mínima de privacidade e como o vendedor poderá apagar os próprios dados.
13. **Recuperação de senha.** Exigiria envio de e-mail, o que é um serviço externo. Sugestão: deixar fora da v1.
14. **Hospedagem e backup.** Escolher onde roda e como o backup é guardado.

## 16. Possíveis evoluções futuras

- Vendas com vários itens (`sale_items`).
- Cadastro de clientes e histórico de compras por cliente.
- Categorias de produtos e variações (tamanho, cor).
- Relatórios simples (vendas por período e produtos mais vendidos) e exportação para CSV.
- Importação de produtos por planilha.
- Preço de custo e cálculo de lucro por venda.
- Restauração de produtos arquivados.
- Alertas de estoque baixo por e-mail.
- Recuperação de senha e verificação de e-mail.
- Armazenamento de imagens em serviço de objetos.
- Leitura de código de barras pela câmera.
- Modo PWA com uso offline.
- Perfis de acesso (por exemplo, vendedor e ajudante), se aparecer a necessidade.
