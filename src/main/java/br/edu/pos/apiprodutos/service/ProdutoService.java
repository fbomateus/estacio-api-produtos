package br.edu.pos.apiprodutos.service;

import br.edu.pos.apiprodutos.model.Produto;
import br.edu.pos.apiprodutos.repository.ProdutoRepository;

import java.util.List;
import java.util.NoSuchElementException;

public class ProdutoService {

    private final ProdutoRepository repository;

    // Injeção de dependência pelo construtor
    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Produto cadastrar(String nome, double preco) {
        return cadastrar(nome, preco, 0);
    }

    public Produto cadastrar(String nome, double preco, Integer quantidadeEstoque) {

        validarProduto(nome, preco);

        if (quantidadeEstoque == null || quantidadeEstoque < 0) {
            throw new IllegalArgumentException(
                    "O estoque inicial deve ser um inteiro maior ou igual a zero."
            );
        }

        Produto produto = new Produto(null, nome, preco, quantidadeEstoque);

        repository.salvar(produto);

        return produto;
    }

    public List<Produto> listar() {
        return repository.listarTodos();
    }

    public Produto buscarPorId(Long id) {

        return repository.buscarPorId(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Produto não encontrado: " + id
                        )
                );
    }

    public Produto atualizar(Long id, String nome, double preco) {

        validarProduto(nome, preco);

        Produto produto = buscarPorId(id);

        produto.setNome(nome);
        produto.setPreco(preco);

        repository.salvar(produto);

        return produto;
    }

    public void excluir(Long id) {

        buscarPorId(id);

        repository.excluir(id);
    }

    public Produto registrarEntrada(Long id, Integer quantidade) {

        validarQuantidade(quantidade);

        if (!repository.adicionarEstoque(id, quantidade)) {
            buscarPorId(id);
            throw new IllegalArgumentException("A entrada ultrapassa o limite de estoque permitido.");
        }

        return buscarPorId(id);
    }

    public Produto registrarSaida(Long id, Integer quantidade) {

        validarQuantidade(quantidade);

        if (!repository.retirarEstoque(id, quantidade)) {
            buscarPorId(id);
            throw new IllegalArgumentException("Estoque insuficiente para realizar a saída.");
        }

        return buscarPorId(id);
    }

    public List<Produto> listarEstoqueBaixo(int limite) {

        if (limite < 0) {
            throw new IllegalArgumentException("O limite de estoque não pode ser negativo.");
        }

        return repository.listarEstoqueBaixo(limite);
    }

    private void validarQuantidade(Integer quantidade) {

        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser um inteiro maior que zero.");
        }
    }

    private void validarProduto(String nome, double preco) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do produto é obrigatório."
            );
        }

        if (nome.length() > 150) {
            throw new IllegalArgumentException(
                    "O nome do produto deve ter no máximo 150 caracteres."
            );
        }

        if (!Double.isFinite(preco) || preco <= 0) {
            throw new IllegalArgumentException(
                    "O preço deve ser maior que zero."
            );
        }
    }
}