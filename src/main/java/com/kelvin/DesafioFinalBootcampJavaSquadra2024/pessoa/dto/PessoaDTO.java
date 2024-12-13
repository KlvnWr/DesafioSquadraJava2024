package com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.dto;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.dto.EnderecoDTO;
import java.util.ArrayList;
import java.util.List;

public class PessoaDTO {
    private Long codigoPessoa;

    private String nome;

    private String sobrenome;

    private Integer idade;

    private String login;

    private String senha;

    private Integer status;

    public List<EnderecoDTO> enderecos = new ArrayList<>();

    public Long getCodigoPessoa() {
        return codigoPessoa;
    }

    public void setCodigoPessoa(Long codigoPessoa) {
        this.codigoPessoa = codigoPessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<EnderecoDTO> getEnderecos() {
        return enderecos;
    }

    public void setEndereco(List<EnderecoDTO> enderecos) {
        this.enderecos = enderecos;
    }
}
