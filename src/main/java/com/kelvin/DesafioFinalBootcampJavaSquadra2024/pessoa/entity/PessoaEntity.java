package com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.entity.EnderecoEntity;
import jakarta.persistence.*;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_PESSOA")
public class PessoaEntity {

    @Id
    @Column(name = "CODIGO_PESSOA")
    private Long codigoPessoa;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "SOBRENOME")
    private String sobrenome;

    @Column(name = "IDADE")
    private Integer idade;

    @Column(name = "LOGIN")
    private String login;

    @Column(name = "SENHA")
    private String senha;

    @Column(name = "STATUS")
    private Integer status;

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnderecoEntity> endereco = new ArrayList<>();

    public Long getCodigoPessoa() {
        return this.codigoPessoa;
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

    public void setIdade(Integer idade) throws ListarException{
        if (idade < 0){
            throw new ListarException("Não é possível cadastrar idade menor que zero", HttpStatus.BAD_REQUEST);
        } else {
            this.idade = idade;
        }
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

    public void setStatus(Integer status) throws ListarException {
        if (status == 1 || status == 2) {
            this.status = status;
        } else {
            throw new ListarException("Status deve ser 1 ou 2.", HttpStatus.CONFLICT);
        }
    }

    public List<EnderecoEntity> getEnderecos() {
        return endereco;
    }

    public void setEnderecos(List<EnderecoEntity> enderecos) {
        this.endereco = enderecos;
    }

}
