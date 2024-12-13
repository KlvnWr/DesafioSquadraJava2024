package com.kelvin.DesafioFinalBootcampJavaSquadra2024.uf.entity;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import jakarta.persistence.*;
import org.springframework.http.HttpStatus;

@Entity
@Cacheable(false)
@Table(name = "TB_UF")
public class UfEntity {

    @Id
    @Column(name = "CODIGO_UF")
    private Long codigoUF;

    @Column(name = "SIGLA", nullable = false, length = 2)
    private String sigla;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "STATUS")
    private Integer status;


    public Long getCodigoUF(){
        return this.codigoUF;
    }

    public void setCodigoUF(Long codigoUf) {
        this.codigoUF = codigoUf;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
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

    @Override
    public int hashCode() {
        return (codigoUF != null) ? codigoUF.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UfEntity outro = (UfEntity) obj;
        return codigoUF != null && codigoUF.equals(outro.codigoUF);
    }
}