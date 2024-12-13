package com.kelvin.DesafioFinalBootcampJavaSquadra2024.municipio.entity;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import jakarta.persistence.*;
import org.springframework.http.HttpStatus;

@Entity
@Cacheable(false)
@Table(name = "TB_MUNICIPIO")
public class MunicipioEntity {

    @Id
    @Column(name = "CODIGO_MUNICIPIO")
    private Long codigoMunicipio;

    @Column(name = "CODIGO_UF")
    private Long codigoUF;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "STATUS")
    private Integer status;

    public Long getCodigoMunicipio() {
        return this.codigoMunicipio;
    }

    public void setCodigoMunicipio(Long codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public Long getCodigoUF() {
        return codigoUF;
    }

    public void setCodigoUF(Long codigoUf) {
        this.codigoUF = codigoUf;
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

    public void setStatus(Integer status) throws ListarException {
        if (status == 1 || status == 2) {
            this.status = status;
        } else {
            throw new ListarException("Status deve ser 1 ou 2.", HttpStatus.CONFLICT);
        }
    }

}