package com.kelvin.DesafioFinalBootcampJavaSquadra2024.bairro.service;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.bairro.entity.BairroEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.bairro.repository.BairroRepository;
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
import java.util.List;

@Service
public class BairroService {

    @Autowired
    private DataSource dataSource;

    private BairroRepository bairroRepository;

    public BairroService(BairroRepository bairroRepository) {
        this.bairroRepository = bairroRepository;
    }

    public void cadastrarBairro(BairroEntity bairroEntity) throws SQLException, ListarException {
        if (bairroEntity == null) {
            throw new ListarException("Bairro não pode ser nulo.", HttpStatus.NOT_FOUND);
        }

        if (bairroEntity.getCodigoMunicipio() == null) {
            throw new ListarException("O código do Municipio não pode ser nulo.", HttpStatus.NOT_FOUND);
        }

        if (!registroMunicipioExiste(bairroEntity.getCodigoMunicipio())) {
            throw new ListarException("Registro do municipio " + bairroEntity.getCodigoMunicipio() + " não foi encontrado.", HttpStatus.NOT_FOUND);
        }

        if (bairroEntity.getNome() == null || bairroEntity.getNome().isEmpty()) {
            throw new ListarException("O nome do bairro não pode ser vazio.", HttpStatus.NOT_FOUND);
        }

        if (nomeJaExisteCadastrar(bairroEntity.getNome(), bairroEntity.getCodigoMunicipio())) {
            throw new ListarException("Nome do Bairro " + bairroEntity.getNome().toUpperCase() + " no Municipio de codigo " + bairroEntity.getCodigoMunicipio() + " já existe.", HttpStatus.BAD_REQUEST);
        }

        if (bairroEntity.getStatus() == null) {
            throw new ListarException("Status não pode ser nulo.", HttpStatus.BAD_REQUEST);
        }

        if (nomeJaExisteCadastrar(bairroEntity.getNome(), bairroEntity.getCodigoMunicipio())) {
            throw new ListarException("Nome do Bairro " + bairroEntity.getNome().toUpperCase() + " no Municipio de codigo " + bairroEntity.getCodigoMunicipio() + " já existe.", HttpStatus.BAD_REQUEST);
        }

        bairroRepository.cadastrarBairro(bairroEntity);
    }

    public Object listarBairros(Long codigoBairro, Long codigoMunicipio, String nome, Integer status) throws ListarException, SQLException {


        if (codigoBairro == null && codigoMunicipio == null && nome == null && status == null) {
            return bairroRepository.buscarTodos();
        } else if (codigoBairro != null) {
            Object bairro = bairroRepository.buscarCodigoBairro(codigoBairro, codigoMunicipio, nome, status);
            return bairro != null ? bairro : new ArrayList<>();
        } else {
            List<BairroEntity> bairros = bairroRepository.buscarBairro(codigoBairro, codigoMunicipio, nome, status);
            return bairros != null ? bairros : new ArrayList<>();
        }
    }

    public Object retornarBairros() throws ListarException{
        return bairroRepository.buscarTodos();
    }

    public void atualizarBairro(BairroEntity bairroEntity) throws ListarException, SQLException{
        if (bairroEntity.getCodigoBairro() == null) {
            throw new ListarException("Não é possível atualizar o registro com o campo codigoBairro vazio.", HttpStatus.BAD_REQUEST);
        }

        if (bairroEntity.getCodigoMunicipio() == null) {
            throw new ListarException("Não é possível atualizar o registro com o campo codigoMunicipio vazio.", HttpStatus.BAD_REQUEST);
        }

        if (!registroBairroExiste(bairroEntity.getCodigoBairro())) {
            throw new ListarException("Registro " + bairroEntity.getCodigoBairro() + " não foi encontrado.", HttpStatus.NOT_FOUND);
        }

        if (!registroMunicipioExiste(bairroEntity.getCodigoMunicipio())) {
            throw new ListarException("Registro " + bairroEntity.getCodigoMunicipio() + " não foi encontrado.", HttpStatus.NOT_FOUND);
        }



        if (bairroEntity.getNome() == null || bairroEntity.getNome().isEmpty()) {
            throw new ListarException("Não é possível atualizar o registro com o campo NOME vazio.", HttpStatus.BAD_REQUEST);
        }

        if (bairroEntity.getStatus() == null) {
            throw new ListarException("Não é possível atualizar o registro com o campo STATUS vazio.", HttpStatus.BAD_REQUEST);
        }

        bairroRepository.atualizarInfo(bairroEntity);
    }

    private boolean registroBairroExiste(Long codigoBairro) throws SQLException, ListarException {
        if (codigoBairro == null) {
            throw new ListarException("Não é possível atualizar o registro com o campo CodigoBairro vazio.", HttpStatus.BAD_REQUEST);
        }

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

    private boolean registroMunicipioExiste(Long codigoBairro) throws SQLException {
        String sqlVerificarRegistro = "SELECT COUNT(*) FROM TB_MUNICIPIO WHERE CODIGO_MUNICIPIO = ?";

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

    private boolean nomeJaExisteCadastrar(String nome, Long codigoMunicipio) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TB_BAIRRO WHERE NOME = ? AND CODIGO_MUNICIPIO = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, nome.toUpperCase());
            ps.setLong(2, codigoMunicipio);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }


}
