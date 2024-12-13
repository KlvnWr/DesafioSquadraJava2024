package com.kelvin.DesafioFinalBootcampJavaSquadra2024.uf.repository;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.uf.entity.UfEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UfRepository {

    @Autowired
    private DataSource dataSource;

    public Long sequence() throws SQLException {
        String sql = "SELECT SEQUENCE_UF.NEXTVAL FROM dual";
        Long proximoCodigoUf = null;

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                proximoCodigoUf = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao obter o próximo código da UF.", e);
        }

        return proximoCodigoUf;
    }

    public void cadastrarUf(UfEntity ufEntity) throws SQLException, ListarException {
        String sqlCadastrarUF = "INSERT INTO TB_UF (CODIGO_UF, SIGLA, NOME, STATUS) VALUES (?, ?, ?, ?)";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlCadastrarUF)) {

            ps.setLong(1, sequence());
            ps.setString(2, ufEntity.getSigla().toUpperCase());
            ps.setString(3, ufEntity.getNome().toUpperCase());
            ps.setInt(4, ufEntity.getStatus());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ListarException("erro", HttpStatus.BAD_REQUEST);
        }
    }

    public List<UfEntity> buscarTodos() throws ListarException {
        List<UfEntity> ufList = new ArrayList<>();

        String query = "SELECT * FROM TB_UF ORDER BY NOME";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UfEntity uf = new UfEntity();
                uf.setCodigoUF(rs.getLong("CODIGO_UF"));
                uf.setSigla(rs.getString("SIGLA"));
                uf.setNome(rs.getString("NOME"));
                uf.setStatus(rs.getInt("STATUS"));
                ufList.add(uf);
            }
        } catch (SQLException e) {
            throw new ListarException("Erro ao buscar UF.", HttpStatus.BAD_REQUEST);
        }

        return ufList;
    }

    public UfEntity buscarUfObjeto(Long codigoUf, String nome, String sigla, Integer status) throws ListarException {
        UfEntity uf = null;

        StringBuilder query = new StringBuilder("SELECT CODIGO_UF, SIGLA, NOME, STATUS FROM TB_UF WHERE 1 = 1");
        ArrayList<Object> params = new ArrayList<>();
        if (codigoUf != null) {
            query.append(" AND CODIGO_UF = ?");
            params.add(codigoUf);
        }
        if (sigla != null) {
            query.append(" AND SIGLA = UPPER(?)");
            params.add(sigla);
        }
        if (nome != null) {
            query.append(" AND NOME = UPPER(?)");
            params.add(nome);
        }
        if (status != null) {
            query.append(" AND STATUS = ?");
            params.add(status);
        }

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                uf = new UfEntity();
                uf.setCodigoUF(rs.getLong("CODIGO_UF"));
                uf.setSigla(rs.getString("SIGLA"));
                uf.setNome(rs.getString("NOME"));
                uf.setStatus(rs.getInt("STATUS"));
            }
        } catch (SQLException e) {
            throw new ListarException("Erro ao executar a consulta no banco de dados.", HttpStatus.BAD_REQUEST);
        }
        return uf;
    }

    public List<UfEntity> buscarUfStatus(Integer status) throws ListarException {
        List<UfEntity> ufs = new ArrayList<>();
        String sql = "SELECT * FROM TB_UF WHERE STATUS = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, status);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UfEntity uf = new UfEntity();
                uf.setCodigoUF(rs.getLong("CODIGO_UF"));
                uf.setSigla(rs.getString("SIGLA"));
                uf.setNome(rs.getString("NOME"));
                uf.setStatus(rs.getInt("STATUS"));
                ufs.add(uf);
            }
        } catch (SQLException e) {
            throw new ListarException("Erro ao buscar UFs por status.", HttpStatus.BAD_REQUEST);
        }
        return ufs;
    }

    public void atualizarInfo(UfEntity ufEntity) throws ListarException{
        String sqlAtualizarInfo = "UPDATE TB_UF SET SIGLA = ?, NOME = ?, STATUS = ? WHERE CODIGO_UF = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlAtualizarInfo)) {
            ps.setString(1, ufEntity.getSigla().toUpperCase());
            ps.setString(2, ufEntity.getNome().toUpperCase());
            ps.setInt(3, ufEntity.getStatus());
            ps.setLong(4, ufEntity.getCodigoUF());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ListarException("Utilizacao do metodo indisponivel", HttpStatus.NOT_FOUND);
        }
    }
}
