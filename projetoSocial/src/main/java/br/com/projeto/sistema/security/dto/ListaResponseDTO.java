package br.com.projeto.sistema.security.dto;

import java.util.List;

public class ListaResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private List<ItemResponseDTO> itens;

    public ListaResponseDTO() {}

    public ListaResponseDTO(Long id, String nome, String descricao, List<ItemResponseDTO> itens) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.itens = itens;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ItemResponseDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemResponseDTO> itens) {
        this.itens = itens;
    }
}
