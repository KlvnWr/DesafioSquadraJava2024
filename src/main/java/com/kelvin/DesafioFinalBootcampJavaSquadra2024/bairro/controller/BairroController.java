package com.kelvin.DesafioFinalBootcampJavaSquadra2024.bairro.controller;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.bairro.entity.BairroEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.bairro.service.BairroService;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;


@RestController
@RequestMapping("/bairro")
public class BairroController {

    private BairroService bairroService;

    @Autowired
    public BairroController(BairroService bairroService) {
        this.bairroService = bairroService;
    }

    @GetMapping
    public ResponseEntity<?> listarBairros(
            @RequestParam(required = false) Long codigoBairro,
            @RequestParam(required = false) Long codigoMunicipio,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer status) {

        try {
            Object bairros = bairroService.listarBairros(codigoBairro, codigoMunicipio, nome, status);
            return ResponseEntity.ok(bairros);
        } catch (ListarException e) {
            return ResponseEntity.ok(new ArrayList<>());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody BairroEntity bairroEntity) throws SQLException {
        try {
            bairroService.cadastrarBairro(bairroEntity);
            Object retorno = bairroService.retornarBairros();
            return ResponseEntity.ok(retorno);
        } catch (ListarException e) {
            throw new RuntimeException(e);
        } catch (ClassCastException e){
            throw new ClassCastException();
        }
    }

    @PutMapping
    public ResponseEntity<?> atualizarInformacoesBairro(@RequestBody BairroEntity bairroEntity) throws SQLException, ListarException {
        bairroService.atualizarBairro(bairroEntity);
        Object resposta = bairroService.retornarBairros();
        return ResponseEntity.ok(resposta);
    }

}
