# API de Produtos

Projeto da disciplina de Projeto e Implementação Orientada a Objetos, com controle de estoque usando Java, Javalin, JDBC e H2.

## Executar

É necessário ter Java 21 ou superior e Maven instalado. Na pasta do projeto:

```bash
mvn compile dependency:build-classpath -Dmdep.outputFile=target/classpath.txt
java -cp "target/classes:$(cat target/classpath.txt)" br.edu.pos.apiprodutos.Main
```

A API usa a porta 7000, o Swagger fica em `http://localhost:7000/swagger` e o console H2 em `http://localhost:8082`. Para acessar o console, use a URL `jdbc:h2:file:./dados/produtos;AUTO_SERVER=TRUE`, usuário `sa` e senha vazia.

## Banco de dados

O banco é mantido na pasta `dados`. Na inicialização, a aplicação cria a tabela quando necessário e adiciona a coluna de estoque ao banco existente:

```sql
ALTER TABLE produtos ADD COLUMN IF NOT EXISTS
    quantidade_estoque INTEGER DEFAULT 0 NOT NULL;
```

Os produtos já cadastrados começam com estoque zero, sem perder nome, preço ou ID. A inicialização pode ser repetida sem zerar o estoque salvo. Também é adicionada uma restrição para impedir valores negativos na coluna.

Não é necessário apagar o banco para testar. É possível usar o banco que acompanha o projeto ou iniciar sem os arquivos da pasta `dados`, pois a aplicação cria um novo banco automaticamente.

## Controle de estoque

O cadastro recebe `quantidadeEstoque`, um inteiro maior ou igual a zero. Se o campo não for informado, o saldo inicial será zero. O PUT continua atualizando nome e preço e preserva o estoque.

As movimentações recebem `quantidade`, um inteiro maior que zero, e retornam o produto atualizado. Uma saída maior que o saldo disponível é rejeitada. O limite da consulta de estoque baixo é obrigatório, inteiro e não negativo.

A saída verifica o saldo e subtrai as unidades na mesma instrução SQL. Assim, saídas simultâneas não conseguem gerar estoque negativo. A entrada também soma as unidades diretamente no banco e verifica se o resultado cabe em um inteiro.

## Testar pelo terminal

Cadastre um produto com 10 unidades:

```bash
curl -i -X POST http://localhost:7000/produtos \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Café","preco":19.9,"quantidadeEstoque":10}'
```

Use o ID retornado no cadastro no lugar de `1` nos exemplos abaixo:

```bash
curl -i -X POST http://localhost:7000/produtos/1/estoque/entrada \
  -H 'Content-Type: application/json' -d '{"quantidade":5}'

curl -i -X POST http://localhost:7000/produtos/1/estoque/saida \
  -H 'Content-Type: application/json' -d '{"quantidade":3}'

curl -i 'http://localhost:7000/produtos/estoque-baixo?limite=12'
```

O saldo esperado é 12. O cadastro retorna 201; as movimentações e consultas retornam 200. Quantidades inválidas ou saldo insuficiente retornam 400. Produtos inexistentes retornam 404.

Para conferir a persistência, encerre a API, inicie novamente na mesma pasta e consulte `GET /produtos/{id}`. O produto deve continuar com as 12 unidades. Também é possível realizar todas essas operações pelo Swagger.
