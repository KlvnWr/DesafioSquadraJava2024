package com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.repository;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class EnderecoRepository {

    private final DataSource dataSource;

    public EnderecoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}