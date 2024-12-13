package com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.service;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.entity.EnderecoEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.repository.EnderecoRepository;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class EnderecoService {

    @Autowired
    private DataSource dataSource;

    private EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public void cadastrarEndereco(EnderecoEntity enderecoEntity) throws ListarException, SQLException {

        if (enderecoEntity.getCodigoBairro() == null){
            throw new ListarException("O campo codigoBairro não pode estar vazio.", HttpStatus.BAD_REQUEST);
        }

        if (!registroBairroExiste(enderecoEntity.getCodigoBairro())) {
            throw new ListarException("O Bairro com codigo " + enderecoEntity.getCodigoBairro() + " nao foi encontrado", HttpStatus.NOT_FOUND);
        }

        if (enderecoEntity.getNomeRua() == null || enderecoEntity.getNomeRua().isEmpty()) {
            throw new ListarException("O nome da rua é obrigatório.", HttpStatus.BAD_REQUEST);
        }

        if (enderecoEntity.getNumero() == null) {
            throw new ListarException("O número do endereço não pode estar vazio.", HttpStatus.BAD_REQUEST);
        }

        if (enderecoEntity.getCep() == null || enderecoEntity.getCep().isEmpty()) {
            throw new ListarException("O CEP é obrigatório.", HttpStatus.BAD_REQUEST);
        }

        if (!enderecoEntity.getCep().matches("\\d{5}-\\d{3}")) {
            throw new ListarException("CEP deve estar no formato xxxxx-xxx.", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean registroBairroExiste(Long codigoBairro) throws SQLException {
        String sqlVerificarRegistro = "SELECT COUNT(*) FROM TB_BAIRRO WHERE CODIGO_BAIRRO = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlVerificarRegistro)) {

            ps.setLong(1, codigoBairro);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao verificar a existência do registro: " + e.getMessage(), e);
        }
        return false;
    }
}
