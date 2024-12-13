package com.kelvin.DesafioFinalBootcampJavaSquadra2024.uf.service;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.uf.repository.UfRepository;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.uf.entity.UfEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class UfService {

    @Autowired
    private DataSource dataSource;

    private UfRepository ufRepository;

    public UfService(UfRepository ufRepository) {
        this.ufRepository = ufRepository;
    }

    public Object listarUfs(Long codigoUf, String sigla, String nome, Integer status) throws ListarException {
        if (codigoUf == null && nome == null && sigla == null && status == null) {
            return ufRepository.buscarTodos();
        } else if (codigoUf == null && nome == null && sigla == null && status != null) {
            return ufRepository.buscarUfStatus(status);
        } else {
            UfEntity uf = ufRepository.buscarUfObjeto(codigoUf, nome, sigla, status);
            return uf != null ? uf : new ArrayList<UfEntity>();
        }
    }

    public Object trasUfs() throws ListarException, SQLException {
        return ufRepository.buscarTodos();
    }

    public void cadastrarUf(UfEntity ufEntity) throws SQLException, ListarException {
        if (ufEntity == null) {
            throw new ListarException("A entidade UF não pode ser nula.", HttpStatus.BAD_REQUEST);
        }

        String sigla = ufEntity.getSigla();
        if (sigla == null || sigla.isEmpty()) {
            throw new ListarException("Não é possível cadastrar com o campo sigla vazio.", HttpStatus.BAD_REQUEST);
        }
        if (sigla.length() != 2) {
            throw new ListarException("A sigla deve ter exatamente 2 caracteres.", HttpStatus.BAD_REQUEST);
        }
        if (siglaJaExisteCadastrar(sigla)) {
            throw new ListarException("Sigla " + sigla.toUpperCase() + " já existe.", HttpStatus.BAD_REQUEST);
        }

        String nome = ufEntity.getNome();
        if (nome == null || nome.isEmpty()) {
            throw new ListarException("Não é possível cadastrar com o campo nome vazio.", HttpStatus.BAD_REQUEST);
        }

        if (ufEntity.getStatus() == null) {
            throw new ListarException("Status não pode ser nulo.", HttpStatus.BAD_REQUEST);
        }

        if (nomeJaExisteCadastrar(nome)){
            throw new ListarException("Nome do estado " + nome.toUpperCase() + " já existe.", HttpStatus.BAD_REQUEST);
        }

        ufRepository.cadastrarUf(ufEntity);
    }

    public void atualizarUf(UfEntity ufEntity) throws ListarException, SQLException{
        if (ufEntity.getCodigoUF() == null) {
            throw new ListarException("Não é possível atualizar o registro com o campo codigoUF vazio.", HttpStatus.BAD_REQUEST);
        }

        if (!registroExiste(ufEntity.getCodigoUF())) {
            throw new ListarException("Registro " + ufEntity.getCodigoUF() + " não foi encontrado.", HttpStatus.NOT_FOUND);
        }

        if (ufEntity.getSigla() == null || ufEntity.getSigla().isEmpty()) {
            throw new ListarException("Não é possível atualizar o registro com o campo SIGLA vazio.", HttpStatus.BAD_REQUEST);
        }

        if (siglaJaExiste(ufEntity.getSigla(), ufEntity.getCodigoUF())) {
            throw new ListarException("Sigla " + ufEntity.getSigla().toUpperCase() + " já existe.", HttpStatus.BAD_REQUEST);
        }

        if (ufEntity.getSigla().length() != 2) {
            throw new ListarException("A sigla deve ter exatamente 2 caracteres.", HttpStatus.NOT_FOUND);
        }

        if (ufEntity.getNome() == null || ufEntity.getNome().isEmpty()) {
            throw new ListarException("Não é possível atualizar o registro com o campo NOME vazio.", HttpStatus.BAD_REQUEST);
        }

        if (nomeJaExiste(ufEntity.getNome(), ufEntity.getCodigoUF())) {
            throw new ListarException("Nome do estado " + ufEntity.getNome().toUpperCase() + " já existe.", HttpStatus.BAD_REQUEST);
        }

        if (ufEntity.getStatus() == null) {
            throw new ListarException("Não é possível atualizar o registro com o campo STATUS vazio.", HttpStatus.BAD_REQUEST);
        }

            ufRepository.atualizarInfo(ufEntity);
    }

    private boolean registroExiste(Long codigoUf) throws SQLException {
        String sqlVerificarRegistro = "SELECT COUNT(*) FROM TB_UF WHERE CODIGO_UF = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlVerificarRegistro)) {

            ps.setLong(1, codigoUf);

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

    private boolean nomeJaExisteCadastrar(String nome) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TB_UF WHERE NOME = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, nome.toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private boolean nomeJaExiste(String nome, Long codigoUf) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TB_UF WHERE NOME = ? AND CODIGO_UF != ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, nome.toUpperCase());
            ps.setLong(2, codigoUf);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private boolean siglaJaExisteCadastrar(String sigla) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TB_UF WHERE SIGLA = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, sigla.toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private boolean siglaJaExiste(String sigla, Long codigoUf) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TB_UF WHERE SIGLA = ? AND CODIGO_UF != ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, sigla.toUpperCase());
            ps.setLong(2, codigoUf);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

}

