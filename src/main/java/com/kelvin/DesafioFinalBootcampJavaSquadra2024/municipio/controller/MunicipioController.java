package com.kelvin.DesafioFinalBootcampJavaSquadra2024.municipio.controller;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.municipio.entity.MunicipioEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.municipio.service.MunicipioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/municipio")
public class MunicipioController {

    private final MunicipioService municipioService;

    @Autowired
    public MunicipioController(MunicipioService municipioService) {
        this.municipioService = municipioService;
    }

    @GetMapping
    public ResponseEntity<?> listarMunicipios(
            @RequestParam(required = false) Long codigoMunicipio,
            @RequestParam(required = false) Long codigoUF,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer status) {

        try {
            Object municipios = municipioService.listarMunicipios(codigoMunicipio, codigoUF, nome, status);
            return ResponseEntity.ok(municipios);
        } catch (ListarException e) {
            return ResponseEntity.ok(new ArrayList<>());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody MunicipioEntity municipioEntity) {
        try {
            municipioService.cadastrarMunicipio(municipioEntity);
            Object retorno = municipioService.retornarMunicipios();
            return ResponseEntity.ok(retorno);
        } catch (ListarException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro de banco de dados");
        }
    }

    @PutMapping
    public ResponseEntity<?> atualizarInformacoesMunicipio(@RequestBody MunicipioEntity municipioEntity) throws SQLException, ListarException {
        municipioService.atualizarMunicipio(municipioEntity);
        Object resposta = municipioService.retornarMunicipios();
        return ResponseEntity.ok(resposta);
    }
}