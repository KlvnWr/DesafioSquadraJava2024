package com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.dto;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.bairro.dto.BairroDTO;

import java.util.ArrayList;
import java.util.List;

public class EnderecoDTO {
    public Long codigoEndereco;
    public Long codigoPessoa;
    public Long codigoBairro;
    public String nomeRua;
    public String numero;
    public String complemento;
    public String cep;
    public BairroDTO bairro = new BairroDTO();

    public EnderecoDTO() {
    }

    public Long getCodigoEndereco() {
        return codigoEndereco;
    }

    public void setCodigoEndereco(Long codigoEndereco) {
        this.codigoEndereco = codigoEndereco;
    }

    public Long getCodigoPessoa() {
        return codigoPessoa;
    }

    public void setCodigoPessoa(Long codigoPessoa) {
        this.codigoPessoa = codigoPessoa;
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

    public void setCep(String cep) {
        this.cep = cep;
    }

    public BairroDTO getBairro() {
        return bairro;
    }

    public void setBairro(BairroDTO bairro) {
        this.bairro = bairro;
    }
}
