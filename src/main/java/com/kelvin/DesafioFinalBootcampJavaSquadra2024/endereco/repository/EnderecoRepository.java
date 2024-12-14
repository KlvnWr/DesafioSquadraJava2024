package com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.repository;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.entity.EnderecoEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.entity.PessoaEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EnderecoRepository {

    private final DataSource dataSource;

    public EnderecoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Long sequence() throws SQLException {
        String sql = "SELECT SEQUENCE_PESSOA.NEXTVAL FROM dual";
        Long proximoCodigoPessoa = null;

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                proximoCodigoPessoa = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao obter o próximo código da pessoa.", e);
        }

        return proximoCodigoPessoa;
    }

    public void atualizarEndereco(EnderecoEntity endereco) throws SQLException {
        String sqlAtualizarEndereco = "UPDATE TB_ENDERECO SET CODIGO_BAIRRO = ?, NOME_RUA = ?, NUMERO = ?, COMPLEMENTO = ?, CEP = ? WHERE CODIGO_ENDERECO = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlAtualizarEndereco)) {
            ps.setLong(1, endereco.getCodigoBairro());
            ps.setString(2, endereco.getNomeRua().toUpperCase());
            ps.setString(3, endereco.getNumero());
            ps.setString(4, endereco.getComplemento() != null ? endereco.getComplemento().toUpperCase() : "");
            ps.setString(5, endereco.getCep());
            ps.setLong(6, endereco.getCodigoEndereco());
            ps.executeUpdate();
        }
    }

    public void cadastrarEndereco(PessoaEntity pessoaEntity, EnderecoEntity endereco) throws SQLException {
        String sqlCadastrarEndereco = "INSERT INTO TB_ENDERECO (CODIGO_ENDERECO, CODIGO_PESSOA, CODIGO_BAIRRO, NOME_RUA, NUMERO, COMPLEMENTO, CEP) VALUES (?,?,?,?,?,?,?)";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlCadastrarEndereco)) {
            ps.setLong(1, sequence());
            ps.setLong(2, pessoaEntity.getCodigoPessoa());
            ps.setLong(3, endereco.getCodigoBairro());
            ps.setString(4, endereco.getNomeRua().toUpperCase());
            ps.setString(5, endereco.getNumero());
            ps.setString(6, endereco.getComplemento() != null ? endereco.getComplemento().toUpperCase() : "");
            ps.setString(7, endereco.getCep());
            ps.executeUpdate();
        }
    }

    public void excluirEndereco(Long codigoEndereco) throws SQLException {
        String sqlExcluirEndereco = "DELETE FROM TB_ENDERECO WHERE CODIGO_ENDERECO = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlExcluirEndereco)) {
            ps.setLong(1, codigoEndereco);
            ps.executeUpdate();
        }
    }

    public List<Long> buscarEnderecosPorPessoa(Long codigoPessoa) throws ListarException {
        List<Long> enderecosIds = new ArrayList<>();
        String sqlBuscarEnderecos = "SELECT CODIGO_ENDERECO FROM TB_ENDERECO WHERE CODIGO_PESSOA = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlBuscarEnderecos)) {

            ps.setLong(1, codigoPessoa);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long codigoEndereco = rs.getLong("CODIGO_ENDERECO");
                    enderecosIds.add(codigoEndereco);
                }
            }
        } catch (SQLException e) {
            throw new ListarException("Erro ao buscar endereços da pessoa.", HttpStatus.BAD_REQUEST);
        }

        return enderecosIds;
    }
}