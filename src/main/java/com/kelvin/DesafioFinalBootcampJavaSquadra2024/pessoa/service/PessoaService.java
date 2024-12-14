package com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.service;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.entity.EnderecoEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.repository.EnderecoRepository;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.entity.PessoaEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class PessoaService {

    @Autowired
    private DataSource dataSource;

    private PessoaRepository pessoaRepository;
    private EnderecoRepository enderecoRepository;

    public PessoaService(PessoaRepository pessoaRepository, EnderecoRepository enderecoRepository) {
        this.pessoaRepository = pessoaRepository;
        this.enderecoRepository = enderecoRepository;
    }

    public Object buscarPessoa(Long codigoPessoa, String login, Integer status) throws ListarException, SQLException {
        if(codigoPessoa == null && login == null && status == null){
            return pessoaRepository.buscarTodos();
        }
        else if (codigoPessoa != null){
            return pessoaRepository.buscarPessoaComEnderecos(codigoPessoa);
        }
        else if (login != null) {
            return pessoaRepository.buscarLogin(login);
        }
        else {
            return pessoaRepository.buscarPorStatus(status);
        }
    }

    public PessoaEntity cadastrarPessoa(PessoaEntity pessoaEntity) throws ListarException, SQLException {
        validarPessoa(pessoaEntity);

        return pessoaRepository.cadastrarPessoaComEnderecos(pessoaEntity);
    }

    @Transactional
    public PessoaEntity atualizarPessoaComEnderecos(PessoaEntity pessoaEntity) throws ListarException, SQLException {
        if(pessoaEntity.getCodigoPessoa() == null){
            throw new ListarException("codigoPessoa não pode ser nulo.", HttpStatus.BAD_REQUEST);
        }

        if (!registroCodigoPessoaExisteAtualizar(pessoaEntity.getCodigoPessoa())) {
            throw new ListarException("Registro " + pessoaEntity.getCodigoPessoa() + " não foi encontrado.", HttpStatus.NOT_FOUND);
        }

        validarPessoa(pessoaEntity);

        pessoaRepository.atualizarPessoa(pessoaEntity);

        List<Long> enderecosExistentes = enderecoRepository.buscarEnderecosPorPessoa(pessoaEntity.getCodigoPessoa());

        for (EnderecoEntity endereco : pessoaEntity.getEnderecos()) {
            if (endereco.getCodigoEndereco() != null) {
                enderecoRepository.atualizarEndereco(endereco);
                enderecosExistentes.remove(endereco.getCodigoEndereco());
            } else {
                enderecoRepository.cadastrarEndereco(pessoaEntity, endereco);
            }
        }

        for (Long codigoEndereco : enderecosExistentes) {
            enderecoRepository.excluirEndereco(codigoEndereco);
        }

        return pessoaEntity;
    }

    private void validarPessoa(PessoaEntity pessoaEntity) throws ListarException, SQLException {
        if (pessoaEntity == null) {
            throw new ListarException("Pessoa não pode ser nula.", HttpStatus.BAD_REQUEST);
        }

        validarCamposPessoa(pessoaEntity);
        validarLogin(pessoaEntity);
        validarEnderecos(pessoaEntity.getEnderecos());
    }

    private void validarCamposPessoa(PessoaEntity pessoaEntity) throws ListarException {
        if (pessoaEntity.getNome() == null || pessoaEntity.getNome().isEmpty()) {
            throw new ListarException("Nome não pode ser vazio.", HttpStatus.BAD_REQUEST);
        }
        if (pessoaEntity.getNome().length() < 2 || pessoaEntity.getNome().length() > 50) {
            throw new ListarException("Nome deve ter entre 2 e 50 caracteres.", HttpStatus.BAD_REQUEST);
        }

        if (pessoaEntity.getSobrenome() == null || pessoaEntity.getSobrenome().isEmpty()) {
            throw new ListarException("Sobrenome não pode ser vazio.", HttpStatus.BAD_REQUEST);
        }
        if (pessoaEntity.getSobrenome().length() < 2 || pessoaEntity.getSobrenome().length() > 50) {
            throw new ListarException("Sobrenome deve ter entre 2 e 50 caracteres.", HttpStatus.BAD_REQUEST);
        }
        if (pessoaEntity.getIdade() == null) {
            throw new ListarException("Idade não pode ser vazia.", HttpStatus.BAD_REQUEST);
        }
        if (pessoaEntity.getStatus() == null) {
            throw new ListarException("Status não pode ser vazio.", HttpStatus.BAD_REQUEST);
        }

        if (pessoaEntity.getSenha() == null || pessoaEntity.getSenha().isEmpty()) {
            throw new ListarException("Senha não pode ser vazia.", HttpStatus.BAD_REQUEST);
        }

        if (pessoaEntity.getSenha().length() < 6) {
            throw new ListarException("Senha deve ter pelo menos 6 caracteres.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validarLogin(PessoaEntity pessoaEntity) throws ListarException, SQLException {
        if (pessoaEntity.getLogin() == null || pessoaEntity.getLogin().isEmpty()) {
            throw new ListarException("Login não pode ser vazio.", HttpStatus.BAD_REQUEST);
        }

        if (loginJaExisteCadastrar(pessoaEntity.getLogin(), pessoaEntity.getCodigoPessoa())) {
            throw new ListarException("Login " + pessoaEntity.getLogin() + " já existe.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validarEnderecos(List<EnderecoEntity> enderecos) throws ListarException {
        if (enderecos == null || enderecos.isEmpty()) {
            throw new ListarException("A lista de endereços é obrigatória com pelo menos um endereço.", HttpStatus.BAD_REQUEST);
        }

        for (EnderecoEntity endereco : enderecos) {
            if (endereco == null) {
                throw new ListarException("Endereço não pode ser nulo.", HttpStatus.BAD_REQUEST);
            }

            if (endereco.getNomeRua() == null || endereco.getNomeRua().isEmpty()) {
                throw new ListarException("Nome da rua não pode ser vazio.", HttpStatus.BAD_REQUEST);
            }

            if (endereco.getNumero() == null || endereco.getNumero().isEmpty()) {
                throw new ListarException("Número não pode ser vazio.", HttpStatus.BAD_REQUEST);
            }

            if (endereco.getCodigoBairro() == null) {
                throw new ListarException("Código do bairro não pode ser nulo.", HttpStatus.BAD_REQUEST);
            }

            if (endereco.getComplemento().length() > 50){
                throw new ListarException("O complemento tem que ter no maximo 50 caracteres", HttpStatus.BAD_REQUEST);
            }

            if (endereco.getCep() == null || endereco.getCep().isEmpty()) {
                throw new ListarException("CEP não pode ser vazio.", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private boolean loginJaExisteCadastrar(String login, Long codigoPessoa) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM TB_PESSOA WHERE LOGIN = ?");

        if (codigoPessoa != null) {
            sql.append(" AND CODIGO_PESSOA != ?");
        }

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql.toString())) {
            ps.setString(1, login);

            if (codigoPessoa != null) {
                ps.setLong(2, codigoPessoa);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private boolean registroCodigoPessoaExisteAtualizar(Long codigoPessoa) throws SQLException {
        String sqlVerificarRegistro = "SELECT COUNT(*) FROM TB_PESSOA WHERE CODIGO_PESSOA = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sqlVerificarRegistro)) {

            ps.setLong(1, codigoPessoa);

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
