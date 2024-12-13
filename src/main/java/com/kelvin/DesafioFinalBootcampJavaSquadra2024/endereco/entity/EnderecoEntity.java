package com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.entity;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.bairro.entity.BairroEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.entity.PessoaEntity;
import jakarta.persistence.*;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_ENDERECO")
public class EnderecoEntity {

    public EnderecoEntity(Long codigoEndereco, PessoaEntity pessoa, Long codigoBairro, String nomeRua, String numero, String complemento, String cep, List<BairroEntity> bairros) {
        this.codigoEndereco = codigoEndereco;
        this.pessoa = pessoa;
        this.codigoBairro = codigoBairro;
        this.nomeRua = nomeRua;
        this.numero = numero;
        this.complemento = complemento;
        this.cep = cep;
        this.bairros = bairros;
    }

    @Id
    @Column(name = "CODIGO_ENDERECO")
    private Long codigoEndereco;

    @ManyToOne
    @JoinColumn(name = "CODIGO_PESSOA", nullable = false)
    private PessoaEntity pessoa;

    @Column(name = "CODIGO_BAIRRO")
    private Long codigoBairro;

    @Column(name = "NOME_RUA")
    private String nomeRua;

    @Column(name = "NUMERO", length = 10)
    private String numero;

    @Column(name = "COMPLEMENTO")
    private String complemento;

    @Column(name = "CEP")
    private String cep;

    @OneToMany(mappedBy = "codigoBairro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BairroEntity> bairros = new ArrayList<>();

    public EnderecoEntity() {}

    public Long getCodigoEndereco() {
        return codigoEndereco;
    }

    public void setCodigoEndereco(Long codigoEndereco) {
        this.codigoEndereco = codigoEndereco;
    }

    public Long getCodigoPessoa() {
        return pessoa.getCodigoPessoa();
    }

    @JsonSetter("codigoPessoa")
    public void setCodigoPessoa(Long codigoPessoa) {
        if (codigoPessoa != null) {
            PessoaEntity pessoa = new PessoaEntity();
            pessoa.setCodigoPessoa(codigoPessoa);
            this.pessoa = pessoa;
        }
    }


    public PessoaEntity getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaEntity codigopessoa) {
        this.pessoa = codigopessoa;
    }

    public Long getCodigoBairro() {
        return codigoBairro;
    }

    public void setCodigoBairro(Long codigoBairro) {
        this.codigoBairro = codigoBairro;
    }

    public String getNomeRua() {
        return nomeRua;
    }

    public void setNomeRua(String nomeRua) {
        this.nomeRua = nomeRua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) throws ListarException {
        if (!cep.matches("\\d{5}-\\d{3}")) {
            throw new ListarException("CEP deve estar no formato xxxxx-xxx.", HttpStatus.BAD_REQUEST);
        } else {
            this.cep = cep;
        }
    }
}