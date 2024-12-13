package com.kelvin.DesafioFinalBootcampJavaSquadra2024.municipio.service;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.municipio.entity.MunicipioEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.municipio.repository.MunicipioRepository;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.uf.entity.UfEntity;
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
public class MunicipioService {

    @Autowired
    private DataSource dataSource;

    private MunicipioRepository municipioRepository;
    private UfEntity ufEntity;

    public MunicipioService(MunicipioRepository municipioRepository) {
        this.municipioRepository = municipioRepository;
    }

    public void cadastrarMunicipio(MunicipioEntity municipioEntity) throws SQLException, ListarException {
        if (municipioEntity == null) {
            throw new ListarException("MunicipioEntity não pode ser nulo.", HttpStatus.NOT_FOUND);
        }

        if (municipioEntity.getCodigoUF() == null) {
            throw new ListarException("O código da UF não pode ser nulo.", HttpStatus.NOT_FOUND);
        }

        if (!registroUfExiste(municipioEntity.getCodigoUF())) {
            throw new ListarException("Registro do Estado " + municipioEntity.getCodigoUF() + " não foi encontrado.", HttpStatus.NOT_FOUND);
        }

        if (municipioEntity.getNome() == null || municipioEntity.getNome().isEmpty()) {
            throw new ListarException("O nome do município não pode ser vazio.", HttpStatus.NOT_FOUND);
        }

        if (municipioEntity.getStatus() == null) {
            throw new ListarException("Status não pode ser nulo.", HttpStatus.BAD_REQUEST);
        }

        if (nomeJaExisteCadastrar(municipioEntity.getNome(), municipioEntity.getCodigoUF())) {
            throw new ListarException("Nome do municipio " + municipioEntity.getNome().toUpperCase() + " no Estado de código " + municipioEntity.getCodigoUF() + " já existe.", HttpStatus.BAD_REQUEST);
        }

        municipioRepository.cadastrarMunicipio(municipioEntity);
    }


    public Object listarMunicipios(Long codigoMunicipio, Long codigoUF, String nome, Integer status) throws ListarException, SQLException {

        if (codigoMunicipio == null && codigoUF == null && nome == null && status == null) {
            return municipioRepository.buscarTodos();
        } else if (codigoMunicipio != null && codigoUF == null && nome == null && status == null) {
            Object municipio = municipioRepository.buscarCodigoMunicipio(codigoMunicipio);
            return municipio != null ? municipio : new ArrayList<>();
        } else {
            List<MunicipioEntity> municipios = municipioRepository.buscarMunicipio(codigoMunicipio, codigoUF, nome, status);
            return municipios != null ? municipios : new ArrayList<>();
        }
    }

    public Object retornarMunicipios() throws ListarException {
        return municipioRepository.buscarTodos();
    }

    public void atualizarMunicipio(MunicipioEntity municipioEntity) throws ListarException, SQLException{
        if (municipioEntity.getCodigoMunicipio() == null) {
            throw new ListarException("Não é possível atualizar o registro com o campo codigoMunicipio vazio.", HttpStatus.BAD_REQUEST);
        }

        if (municipioEntity.getCodigoUF() == null) {
            throw new ListarException("Não é possível atualizar o registro com o campo codigoUF vazio.", HttpStatus.BAD_REQUEST);
        }

        if (!registroMunicipioExiste(municipioEntity.getCodigoMunicipio())) {
            throw new ListarException("Registro " + municipioEntity.getCodigoMunicipio() + " não foi encontrado.", HttpStatus.NOT_FOUND);
        }

        if (!registroUfExiste(municipioEntity.getCodigoUF())) {
            throw new ListarException("Registro " + municipioEntity.getCodigoUF() + " não foi encontrado.", HttpStatus.NOT_FOUND);
        }

        if (municipioEntity.getNome() == null || municipioEntity.getNome().isEmpty()) {
            throw new ListarException("Não é possível atualizar o registro com o campo NOME vazio.", HttpStatus.BAD_REQUEST);
        }

        if (municipioEntity.getStatus() == null) {
            throw new ListarException("Não é possível atualizar o registro com o campo STATUS vazio.", HttpStatus.BAD_REQUEST);
        }

        municipioRepository.atualizarInfo(municipioEntity);
    }

    private boolean registroUfExiste(Long codigoUf) throws SQLException {
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

    private boolean registroMunicipioExiste(Long codigoMunicipio) throws SQLException, ListarException {
        if (codigoMunicipio == null) {
            throw new ListarException("Não é possível atualizar o registro com o campo CodigoMunicipio vazio.", HttpStatus.BAD_REQUEST);
        }

        String sqlVerificarRegistro = "SELECT COUNT(*) FROM TB_MUNICIPIO WHERE CODIGO_MUNICIPIO = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlVerificarRegistro)) {

            ps.setLong(1, codigoMunicipio);

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

    private boolean nomeJaExisteCadastrar(String nome, Long codigoUf) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TB_MUNICIPIO WHERE NOME = ? AND CODIGO_UF = ?";
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
}


