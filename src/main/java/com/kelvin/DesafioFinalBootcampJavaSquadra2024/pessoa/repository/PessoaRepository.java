package com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.repository;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.bairro.dto.BairroDTO;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.dto.EnderecoDTO;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.entity.EnderecoEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.municipio.dto.MunicipioDTO;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.dto.PessoaDTO;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.uf.dto.UfDTO;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.entity.PessoaEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PessoaRepository{

    private final DataSource dataSource;

    public PessoaRepository(DataSource dataSource) {
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

    public PessoaEntity cadastrarPessoaComEnderecos(PessoaEntity pessoaEntity) throws SQLException {
        Long codigoPessoa = sequence();

        String sqlCadastrarPessoa = "INSERT INTO TB_PESSOA (CODIGO_PESSOA, NOME, SOBRENOME, IDADE, LOGIN, SENHA, STATUS) VALUES (?,?,?,?,?,?,?)";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlCadastrarPessoa)) {
            ps.setLong(1, codigoPessoa);
            ps.setString(2, pessoaEntity.getNome().toUpperCase());
            ps.setString(3, pessoaEntity.getSobrenome().toUpperCase());
            ps.setInt(4, pessoaEntity.getIdade());
            ps.setString(5, pessoaEntity.getLogin());
            ps.setString(6, pessoaEntity.getSenha());
            ps.setInt(7, pessoaEntity.getStatus());
            ps.executeUpdate();
        }

        for (EnderecoEntity endereco : pessoaEntity.getEnderecos()) {
            String sqlCadastrarEndereco = "INSERT INTO TB_ENDERECO (CODIGO_ENDERECO, CODIGO_PESSOA, CODIGO_BAIRRO, NOME_RUA, NUMERO, COMPLEMENTO, CEP) VALUES (?,?,?,?,?,?,?)";

            try (Connection conexao = dataSource.getConnection();
                 PreparedStatement ps = conexao.prepareStatement(sqlCadastrarEndereco)) {
                ps.setLong(1, sequence());
                ps.setLong(2, codigoPessoa);
                ps.setLong(3, endereco.getCodigoBairro());
                ps.setString(4, endereco.getNomeRua().toUpperCase());
                ps.setString(5, endereco.getNumero());
                ps.setString(6, endereco.getComplemento() != null ? endereco.getComplemento().toUpperCase() : "");
                ps.setString(7, endereco.getCep());
                ps.executeUpdate();
            }
        }
        return pessoaEntity;
    }

    public List<PessoaEntity> buscarTodos() throws ListarException {
        List<PessoaEntity> pessoaList = new ArrayList<>();
        String sqlPesquisarTodos = "SELECT * FROM TB_PESSOA ORDER BY CODIGO_PESSOA";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlPesquisarTodos);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PessoaEntity pessoa = new PessoaEntity();
                pessoa.setCodigoPessoa(rs.getLong("CODIGO_PESSOA"));
                pessoa.setNome(rs.getString("NOME"));
                pessoa.setSobrenome(rs.getString("SOBRENOME"));
                pessoa.setIdade(rs.getInt("IDADE"));
                pessoa.setLogin(rs.getString("LOGIN"));
                pessoa.setSenha(rs.getString("SENHA"));
                pessoa.setStatus(rs.getInt("STATUS"));

                List<EnderecoEntity> enderecos = new ArrayList<>();
                pessoa.setEnderecos(enderecos);
                pessoaList.add(pessoa);
            }
        } catch (SQLException e) {
            throw new ListarException("Erro ao buscar Pessoa.", HttpStatus.BAD_REQUEST);
        }

        return pessoaList;
    }

    public List<PessoaEntity> buscarLogin(String login) throws ListarException {
        List<PessoaEntity> pessoaList = new ArrayList<>();
        String sqlPesquisarLogin = "SELECT * FROM TB_PESSOA WHERE LOGIN = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlPesquisarLogin)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PessoaEntity pessoa = new PessoaEntity();
                    pessoa.setCodigoPessoa(rs.getLong("CODIGO_PESSOA"));
                    pessoa.setNome(rs.getString("NOME"));
                    pessoa.setSobrenome(rs.getString("SOBRENOME"));
                    pessoa.setIdade(rs.getInt("IDADE"));
                    pessoa.setLogin(rs.getString("LOGIN"));
                    pessoa.setSenha(rs.getString("SENHA"));
                    pessoa.setStatus(rs.getInt("STATUS"));

                    List<EnderecoEntity> enderecos = new ArrayList<>();
                    pessoa.setEnderecos(enderecos);
                    pessoaList.add(pessoa);
                }
            }
        } catch (SQLException e) {
            throw new ListarException("Erro ao buscar Pessoa.", HttpStatus.BAD_REQUEST);
        }

        return pessoaList;
    }

    public Object buscarPessoaComEnderecos(Long codigoPessoa) throws ListarException {
        PessoaDTO pessoa = null;
        String sqlBuscaPessoa = "SELECT tb_pessoa.*, tb_endereco.*, tb_bairro.*, tb_municipio.*, tb_uf.*, " +
                "tb_pessoa.NOME AS NOME_PESSOA, tb_bairro.NOME AS NOME_BAIRRO, tb_municipio.NOME AS NOME_MUNICIPIO, tb_uf.NOME AS NOME_UF, " +
                "tb_pessoa.STATUS AS STATUS_PESSOA, tb_bairro.STATUS AS STATUS_BAIRRO, " +
                "tb_municipio.STATUS AS STATUS_MUNICIPIO, tb_uf.STATUS AS STATUS_UF " +
                "FROM TB_PESSOA " +
                "LEFT JOIN TB_ENDERECO ON tb_pessoa.CODIGO_PESSOA = tb_endereco.CODIGO_PESSOA " +
                "LEFT JOIN TB_BAIRRO ON tb_endereco.CODIGO_BAIRRO = tb_bairro.CODIGO_BAIRRO " +
                "LEFT JOIN TB_MUNICIPIO ON tb_bairro.codigo_municipio = tb_municipio.codigo_municipio " +
                "LEFT JOIN TB_UF ON tb_municipio.codigo_uf = tb_uf.codigo_uf " +
                "WHERE tb_pessoa.CODIGO_PESSOA = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlBuscaPessoa)) {

            ps.setLong(1, codigoPessoa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pessoa = new PessoaDTO();
                    pessoa.setCodigoPessoa(rs.getLong("CODIGO_PESSOA"));
                    pessoa.setNome(rs.getString("NOME"));
                    pessoa.setSobrenome(rs.getString("SOBRENOME"));
                    pessoa.setIdade(rs.getInt("IDADE"));
                    pessoa.setLogin(rs.getString("LOGIN"));
                    pessoa.setSenha(rs.getString("SENHA"));
                    pessoa.setStatus(rs.getInt("STATUS_PESSOA"));

                    List<EnderecoDTO> enderecoLista = new ArrayList<>();
                    Map<Long, EnderecoDTO> enderecoMap = new HashMap<>();

                    do {
                        Long codigoEndereco = rs.getLong("CODIGO_ENDERECO");

                        if (!enderecoMap.containsKey(codigoEndereco)) {
                            EnderecoDTO endereco = new EnderecoDTO();
                            List<BairroDTO> bairrosLista = new ArrayList<>();
                            List<MunicipioDTO> municipioLista = new ArrayList<>();
                            List<UfDTO> ufLista = new ArrayList<>();

                            endereco.setCodigoEndereco(rs.getLong("CODIGO_ENDERECO"));
                            endereco.setCodigoPessoa(pessoa.getCodigoPessoa());
                            endereco.setCodigoBairro(rs.getLong("CODIGO_BAIRRO"));
                            endereco.setNomeRua(rs.getString("NOME_RUA"));
                            endereco.setNumero(rs.getString("NUMERO"));
                            endereco.setComplemento(rs.getString("COMPLEMENTO"));
                            endereco.setCep(rs.getString("CEP"));

                            BairroDTO bairro = new BairroDTO();
                            bairro.setCodigoBairro(rs.getLong("CODIGO_BAIRRO"));
                            bairro.setCodigoMunicipio(rs.getLong("CODIGO_MUNICIPIO"));
                            bairro.setNome(rs.getString("NOME_BAIRRO"));
                            bairro.setStatus(rs.getInt("STATUS_BAIRRO"));

                            MunicipioDTO municipio = new MunicipioDTO();
                            municipio.setCodigoMunicipio(rs.getLong("CODIGO_MUNICIPIO"));
                            municipio.setCodigoUF(rs.getLong("CODIGO_UF"));
                            municipio.setNome(rs.getString("NOME_MUNICIPIO"));
                            municipio.setStatus(rs.getInt("STATUS_MUNICIPIO"));

                            UfDTO uf = new UfDTO();
                            uf.setCodigoUF(rs.getLong("CODIGO_UF"));
                            uf.setSigla(rs.getString("SIGLA"));
                            uf.setNome(rs.getString("NOME_UF"));
                            uf.setStatus(rs.getInt("STATUS_UF"));

                            bairrosLista.add(bairro);
                            endereco.setBairro(bairrosLista);

                            municipioLista.add(municipio);
                            bairro.setMunicipio(municipioLista);

                            ufLista.add(uf);
                            municipio.setUf(ufLista);

                            enderecoMap.put(codigoEndereco, endereco);
                            enderecoLista.add(endereco);

                        }
                    } while (rs.next());

                    pessoa.setEndereco(enderecoLista);
                } else {
                    return new ArrayList<>();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ListarException("Erro ao buscar pessoa pelo código.", HttpStatus.BAD_REQUEST);
        }
        return pessoa;
    }

    public List<PessoaEntity> buscarPorStatus(Integer status) throws ListarException {
        List<PessoaEntity> pessoaList = new ArrayList<>();
        String sqlBuscarPorStatus = "SELECT * FROM TB_PESSOA WHERE STATUS = ? ORDER BY CODIGO_PESSOA";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlBuscarPorStatus)) {

            ps.setInt(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PessoaEntity pessoa = new PessoaEntity();
                    pessoa.setCodigoPessoa(rs.getLong("CODIGO_PESSOA"));
                    pessoa.setNome(rs.getString("NOME"));
                    pessoa.setSobrenome(rs.getString("SOBRENOME"));
                    pessoa.setIdade(rs.getInt("IDADE"));
                    pessoa.setLogin(rs.getString("LOGIN"));
                    pessoa.setSenha(rs.getString("SENHA"));
                    pessoa.setStatus(rs.getInt("STATUS"));

                    List<EnderecoEntity> enderecos = new ArrayList<>();
                    pessoa.setEnderecos(enderecos);
                    pessoaList.add(pessoa);
                }
            }
        } catch (SQLException e) {
            throw new ListarException("Erro ao buscar pessoas pelo status.", HttpStatus.BAD_REQUEST);
        }

        return pessoaList;
    }

    public void atualizarPessoa(PessoaEntity pessoaEntity) throws SQLException {
        String sqlAtualizarPessoa = "UPDATE TB_PESSOA SET NOME = ?, SOBRENOME = ?, IDADE = ?, LOGIN = ?, SENHA = ?, STATUS = ? WHERE CODIGO_PESSOA = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlAtualizarPessoa)) {
            ps.setString(1, pessoaEntity.getNome().toUpperCase());
            ps.setString(2, pessoaEntity.getSobrenome().toUpperCase());
            ps.setInt(3, pessoaEntity.getIdade());
            ps.setString(4, pessoaEntity.getLogin());
            ps.setString(5, pessoaEntity.getSenha());
            ps.setInt(6, pessoaEntity.getStatus());
            ps.setLong(7, pessoaEntity.getCodigoPessoa());
            ps.executeUpdate();
        }
    }
}