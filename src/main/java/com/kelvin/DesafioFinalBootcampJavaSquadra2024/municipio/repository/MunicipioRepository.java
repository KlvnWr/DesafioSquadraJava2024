package com.kelvin.DesafioFinalBootcampJavaSquadra2024.municipio.repository;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.municipio.entity.MunicipioEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MunicipioRepository {

    private final DataSource dataSource;

    public MunicipioRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Long sequence() throws SQLException {
        String sql = "SELECT SEQUENCE_MUNICIPIO.NEXTVAL FROM dual";
        Long proximoCodigoMunicipio = null;

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                proximoCodigoMunicipio = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao obter o próximo código do município.", e);
        }

        return proximoCodigoMunicipio;
    }

    public void cadastrarMunicipio(MunicipioEntity municipioEntity) throws ListarException {
        String sqlCadastrarMunicipio = "INSERT INTO TB_MUNICIPIO (CODIGO_MUNICIPIO, CODIGO_UF, NOME, STATUS) VALUES (?, ?, ?, ?)";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlCadastrarMunicipio)) {

            ps.setLong(1, sequence());
            ps.setLong(2, municipioEntity.getCodigoUF());
            ps.setString(3, municipioEntity.getNome().toUpperCase());
            ps.setInt(4, municipioEntity.getStatus());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ListarException("Erro ao cadastrar município.", HttpStatus.BAD_REQUEST);
        }
    }

    public List<MunicipioEntity> buscarTodos() throws ListarException {
        List<MunicipioEntity> municipioList = new ArrayList<>();
        String query = "SELECT * FROM TB_MUNICIPIO ORDER BY CODIGO_UF";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MunicipioEntity municipio = new MunicipioEntity();
                municipio.setCodigoMunicipio(rs.getLong("CODIGO_MUNICIPIO"));
                municipio.setCodigoUF(rs.getLong("CODIGO_UF"));
                municipio.setNome(rs.getString("NOME"));
                municipio.setStatus(rs.getInt("STATUS"));
                municipioList.add(municipio);
            }
        } catch (SQLException e) {
            throw new ListarException("Erro ao buscar Municípios.", HttpStatus.BAD_REQUEST);
        }

        return municipioList;
    }

    public Object buscarCodigoMunicipio(Long codigoMunicipio) throws ListarException {
        MunicipioEntity municipio = null;
        String sqlBuscaCodigoMun = "SELECT * FROM TB_MUNICIPIO WHERE CODIGO_MUNICIPIO = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlBuscaCodigoMun)) {
            ps.setLong(1, codigoMunicipio);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                municipio = new MunicipioEntity();
                municipio.setCodigoMunicipio(rs.getLong("CODIGO_MUNICIPIO"));
                municipio.setCodigoUF(rs.getLong("CODIGO_UF"));
                municipio.setNome(rs.getString("NOME"));
                municipio.setStatus(rs.getInt("STATUS"));
            }
        } catch (SQLException e) {
            throw new ListarException("Erro ao buscar município pelo código.", HttpStatus.BAD_REQUEST);
        }
        return municipio;
    }

    public List<MunicipioEntity> buscarMunicipio(Long codigoMunicipio, Long codigoUF, String nome, Integer status) throws ListarException {
        List<MunicipioEntity> municipioList = new ArrayList<>();
        StringBuilder sqlBuscarMunicipios = new StringBuilder("SELECT * FROM TB_MUNICIPIO WHERE 1 = 1");
        ArrayList<Object> params = new ArrayList<>();

        if (codigoMunicipio != null) {
            sqlBuscarMunicipios.append(" AND CODIGO_MUNICIPIO = ?");
            params.add(codigoMunicipio);
        }
        if (codigoUF != null) {
            sqlBuscarMunicipios.append(" AND CODIGO_UF = ?");
            params.add(codigoUF);
        }
        if (nome != null) {
            sqlBuscarMunicipios.append(" AND NOME = UPPER(?)");
            params.add(nome);
        }
        if (status != null) {
            sqlBuscarMunicipios.append(" AND STATUS = ?");
            params.add(status);
        }

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlBuscarMunicipios.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MunicipioEntity municipio = new MunicipioEntity();
                    municipio.setCodigoMunicipio(rs.getLong("CODIGO_MUNICIPIO"));
                    municipio.setCodigoUF(rs.getLong("CODIGO_UF"));
                    municipio.setNome(rs.getString("NOME").toUpperCase());
                    municipio.setStatus(rs.getInt("STATUS"));
                    municipioList.add(municipio);
                }
            }
        } catch (SQLException e) {
            throw new ListarException("Erro ao executar a consulta no banco de dados.", HttpStatus.BAD_REQUEST);
        }
        return municipioList;
    }

    public void atualizarInfo(MunicipioEntity municipioEntity) throws ListarException {
        String sqlAtualizarInfo = "UPDATE TB_MUNICIPIO SET CODIGO_UF = ?, NOME = ?, STATUS = ? WHERE CODIGO_MUNICIPIO = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlAtualizarInfo)) {
            ps.setLong(1, municipioEntity.getCodigoUF());
            ps.setString(2, municipioEntity.getNome().toUpperCase());
            ps.setInt(3, municipioEntity.getStatus());
            ps.setLong(4, municipioEntity.getCodigoMunicipio());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ListarException("Utilização do método indisponível", HttpStatus.NOT_FOUND);
        }
    }
}