package com.kelvin.DesafioFinalBootcampJavaSquadra2024.bairro.dto;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.municipio.dto.MunicipioDTO;

import java.util.ArrayList;
import java.util.List;

public class BairroDTO {
    public Long codigoBairro;
    public Long codigoMunicipio;
    public String nome;
    public Integer status;
    public List<MunicipioDTO> municipio = new ArrayList<>();

    public BairroDTO() {
    }

    public Long getCodigoBairro() {
        return codigoBairro;
    }

    public void setCodigoBairro(Long codigoBairro) {
        this.codigoBairro = codigoBairro;
    }

    public Long getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(Long codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<MunicipioDTO> getMunicipio() {
        return municipio;
    }

    public void setMunicipio(List<MunicipioDTO> municipio) {
        this.municipio = municipio;
    }
}
