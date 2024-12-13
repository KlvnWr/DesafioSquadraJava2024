package com.kelvin.DesafioFinalBootcampJavaSquadra2024.municipio.dto;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.uf.dto.UfDTO;

import java.util.ArrayList;
import java.util.List;

public class MunicipioDTO {
    private Long codigoMunicipio;
    private Long codigoUF;
    private String nome;
    private Integer status;
    private List<UfDTO> uf = new ArrayList<>();

    public Long getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(Long codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public Long getCodigoUF() {
        return codigoUF;
    }

    public void setCodigoUF(Long codigoUF) {
        this.codigoUF = codigoUF;
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

    public List<UfDTO> getUf() {
        return uf;
    }

    public void setUf(List<UfDTO> uf) {
        this.uf = uf;
    }
}
