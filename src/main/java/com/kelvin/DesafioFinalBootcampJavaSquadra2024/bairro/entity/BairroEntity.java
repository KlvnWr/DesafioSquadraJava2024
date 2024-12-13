package com.kelvin.DesafioFinalBootcampJavaSquadra2024.bairro.entity;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.municipio.entity.MunicipioEntity;
import jakarta.persistence.*;
import org.springframework.http.HttpStatus;

@Entity
@Table(name = "TB_BAIRRO")
public class BairroEntity {

    @Id
    @Column(name = "CODIGO_BAIRRO")
    private Long codigoBairro;

//    @OneToMany
    @Column(name = "CODIGO_MUNICIPIO")
    private Long codigoMunicipio;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "STATUS")
    private Integer status;

    public BairroEntity() {}

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

    public Integer getStatus() throws ListarException {
        return status;
    }

    public void setStatus(Integer status) throws ListarException {
        if (status == 1 || status == 2) {
            this.status = status;
        } else {
            throw new ListarException("Status deve ser 1 ou 2.", HttpStatus.BAD_REQUEST);
        }
    }
}