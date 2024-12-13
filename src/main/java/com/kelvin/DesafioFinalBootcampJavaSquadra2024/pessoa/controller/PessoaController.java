package com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.controller;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.entity.EnderecoEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.entity.PessoaEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.repository.PessoaRepository;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.pessoa.service.PessoaService;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.endereco.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/pessoa")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private EnderecoService enderecoService;

    @GetMapping
    public ResponseEntity<?> listarPessoas(
            @RequestParam(required = false) Long codigoPessoa,
            @RequestParam(required = false) String login,
            @RequestParam(required = false) Integer status) {
        try {
            Object pessoas = pessoaService.buscarPessoa(codigoPessoa, login, status);
            return ResponseEntity.ok(pessoas);
        } catch (ListarException e) {
            return ResponseEntity.ok(new ArrayList<>());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody PessoaEntity pessoaEntity) throws ListarException{
        try {
            for (EnderecoEntity endereco : pessoaEntity.getEnderecos()) {
                enderecoService.cadastrarEndereco(endereco);
            }

            pessoaService.cadastrarPessoa(pessoaEntity);
            Object retorno = pessoaRepository.buscarTodos();
            return ResponseEntity.ok(retorno);
        }  catch (ListarException | SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassCastException e){
            throw new ClassCastException("Erro");
        }
    }

    @PutMapping
    public ResponseEntity<?> atualizar(@RequestBody PessoaEntity pessoaEntity) {
        try {
            pessoaService.atualizarPessoaComEnderecos(pessoaEntity);
            Object retorno = pessoaRepository.buscarTodos();
            return ResponseEntity.ok(retorno);
        } catch (ListarException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
