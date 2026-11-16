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