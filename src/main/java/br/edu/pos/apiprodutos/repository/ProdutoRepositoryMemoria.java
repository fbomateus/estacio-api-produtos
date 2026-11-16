package br.edu.pos.apiprodutos.repository;

import br.edu.pos.apiprodutos.model.Produto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoRepositoryMemoria implements ProdutoRepository {

    private final List<Produto> produtos = new ArrayList<>();

    @Override
    public void salvar(Produto produto) {

        produtos.removeIf(p -> p.getId().equals(produto.getId()));

        produtos.add(produto);
    }

    @Override
    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos);
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {

        return produtos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public void excluir(Long id) {

        produtos.removeIf(p -> p.getId().equals(id));
    }

    @Override
    public synchronized boolean adicionarEstoque(Long id, int quantidade) {
        Optional<Produto> resultado = buscarPorId(id);

        if (resultado.isEmpty()) {
            return false;
        }

        Produto produto = resultado.get();

        if (produto.getQuantidadeEstoque() > Integer.MAX_VALUE - quantidade) {
            return false;
        }

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
        return true;
    }

    @Override
    public synchronized boolean retirarEstoque(Long id, int quantidade) {
        Optional<Produto> resultado = buscarPorId(id);

        if (resultado.isEmpty()) {
            return false;
        }

        Produto produto = resultado.get();

        if (produto.getQuantidadeEstoque() < quantidade) {
            return false;
        }

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        return true;
    }

    @Override
    public List<Produto> listarEstoqueBaixo(int limite) {
        List<Produto> resultado = new ArrayList<>();

        for (Produto produto : produtos) {
            if (produto.getQuantidadeEstoque() <= limite) {
                resultado.add(produto);
            }
        }

        return resultado;
    }
}