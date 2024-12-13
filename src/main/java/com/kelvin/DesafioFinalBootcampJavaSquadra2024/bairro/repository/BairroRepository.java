package com.kelvin.DesafioFinalBootcampJavaSquadra2024.bairro.repository;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.bairro.entity.BairroEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
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
public class BairroRepository {

    private final DataSource dataSource;

    public BairroRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Long sequence() throws SQLException {
        String sql = "SELECT SEQUENCE_BAIRRO.NEXTVAL FROM dual";
        Long proximoCodigoBairro = null;

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                proximoCodigoBairro = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao obter o próximo código do bairro.", e);
        }

        return proximoCodigoBairro;
    }

    public void cadastrarBairro(BairroEntity bairroEntity) throws SQLException, ListarException {

        String sqlCadastrarBairro = "INSERT INTO TB_BAIRRO ( CODIGO_BAIRRO, CODIGO_MUNICIPIO, NOME, STATUS) VALUES (?, ?, ?, ?)";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlCadastrarBairro)) {

            ps.setLong(1, sequence());
            ps.setLong(2, bairroEntity.getCodigoMunicipio());
            ps.setString(3, bairroEntity.getNome().toUpperCase());
            ps.setInt(4, bairroEntity.getStatus());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ListarException("Erro ao cadastrar bairro", HttpStatus.BAD_REQUEST);
        }
    }

    public List<BairroEntity> buscarTodos() throws ListarException {
        List<BairroEntity> bairroList = new ArrayList<>();

        String query = "SELECT * FROM TB_BAIRRO ORDER BY CODIGO_MUNICIPIO";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BairroEntity bairro = new BairroEntity();
                bairro.setCodigoBairro(rs.getLong("CODIGO_BAIRRO"));
                bairro.setCodigoMunicipio(rs.getLong("CODIGO_MUNICIPIO"));
                bairro.setNome(rs.getString("NOME"));
                bairro.setStatus(rs.getInt("STATUS"));
                bairroList.add(bairro);
            }
        } catch (SQLException e) {
            throw new ListarException("Erro ao buscar Bairro.", HttpStatus.BAD_REQUEST);
        }

        return bairroList;
    }

    public Object buscarCodigoBairro(Long codigoBairro, Long codigoMunicipio, String nome, Integer status) throws ListarException {
        BairroEntity bairro = null;

        StringBuilder sqlBuscaCodigoBairro = new StringBuilder("SELECT * FROM TB_BAIRRO WHERE 1 = 1");

        ArrayList<Object> params = new ArrayList<>();
        if (codigoBairro != null) {
            sqlBuscaCodigoBairro.append(" AND CODIGO_BAIRRO = ?");
            params.add(codigoBairro);
        }
        if (codigoMunicipio != null) {
            sqlBuscaCodigoBairro.append(" AND CODIGO_MUNICIPIO = ?");
            params.add(codigoMunicipio);
        }
        if (nome != null) {
            sqlBuscaCodigoBairro.append(" AND NOME = UPPER(?)");
            params.add(nome);
        }
        if (status != null) {
            sqlBuscaCodigoBairro.append(" AND STATUS = ?");
            params.add(status);
        }

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlBuscaCodigoBairro.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                bairro = new BairroEntity();
                bairro.setCodigoBairro(rs.getLong("CODIGO_BAIRRO"));
                bairro.setCodigoMunicipio(rs.getLong("CODIGO_MUNICIPIO"));
                bairro.setNome(rs.getString("NOME"));
                bairro.setStatus(rs.getInt("STATUS"));
            }
        } catch (SQLException e) {
            throw new ListarException("Erro ao buscar bairro pelo código.", HttpStatus.BAD_REQUEST);
        }
        return bairro;
    }

    public List<BairroEntity> buscarBairro(Long codigoBairro, Long codigoMunicipio, String nome, Integer status) throws ListarException{
        List<BairroEntity> bairroList = new ArrayList<>();

        StringBuilder sqlBuscarMunicipios = new StringBuilder("SELECT * FROM TB_BAIRRO WHERE 1 = 1");
        ArrayList<Object> params = new ArrayList<>();
        if (codigoBairro != null) {
            sqlBuscarMunicipios.append(" AND CODIGO_BAIRRO = ?");
            params.add(codigoBairro);
        }
        if (codigoMunicipio != null) {
            sqlBuscarMunicipios.append(" AND CODIGO_MUNICIPIO = ?");
            params.add(codigoMunicipio);
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
                    BairroEntity bairro = new BairroEntity();
                    bairro.setCodigoBairro(rs.getLong("CODIGO_BAIRRO"));
                    bairro.setCodigoMunicipio(rs.getLong("CODIGO_MUNICIPIO"));
                    bairro.setNome(rs.getString("NOME").toUpperCase());
                    bairro.setStatus(rs.getInt("STATUS"));
                    bairroList.add(bairro);
                }
            }
        } catch (SQLException e) {
            throw new ListarException("Erro ao executar a consulta no banco de dados.", HttpStatus.BAD_REQUEST);
        }
        return bairroList;
    }

    public void atualizarInfo(BairroEntity bairroEntity) throws ListarException{
        String sqlAtualizarInfo = "UPDATE TB_BAIRRO SET CODIGO_MUNICIPIO = ?, NOME = ?, STATUS = ? WHERE CODIGO_BAIRRO = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlAtualizarInfo)) {
            ps.setLong(1, bairroEntity.getCodigoMunicipio());
            ps.setString(2, bairroEntity.getNome().toUpperCase());
            ps.setInt(3, bairroEntity.getStatus());
            ps.setLong(4, bairroEntity.getCodigoBairro());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ListarException("Utilizacao do metodo indisponivel", HttpStatus.NOT_FOUND);
        }
    }
}
