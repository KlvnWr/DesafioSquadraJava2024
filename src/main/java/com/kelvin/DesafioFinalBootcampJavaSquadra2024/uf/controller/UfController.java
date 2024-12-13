package com.kelvin.DesafioFinalBootcampJavaSquadra2024.uf.controller;

import com.kelvin.DesafioFinalBootcampJavaSquadra2024.uf.entity.UfEntity;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception.ListarException;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.uf.repository.UfRepository;
import com.kelvin.DesafioFinalBootcampJavaSquadra2024.uf.service.UfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/uf")
public class UfController {

    @Autowired
    private UfService ufService;
    private UfRepository ufRepository;

    public UfController(UfService ufService) {
        this.ufService = ufService;
    }

    @GetMapping
    public ResponseEntity<?> listarUfs(
            @RequestParam(required = false) String codigoUF,
            @RequestParam(required = false) String sigla,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer status) throws ListarException {

        try {
            Long codigoUfConvertido = null;
            if (codigoUF != null) {
                try {
                    codigoUfConvertido = Long.valueOf(codigoUF);
                } catch (NumberFormatException e) {
                    throw new ListarException("O campo 'codigoUF' deve ser um número válido.", HttpStatus.BAD_REQUEST);
                }
            }

            Object ufs = ufService.listarUfs(codigoUfConvertido, sigla, nome, status);
            return ResponseEntity.ok(ufs);
        } catch (ListarException e) {
            return ResponseEntity.status(e.getStatus()).body(e.toJson());
        }
    }

    @PostMapping
    public ResponseEntity<?> cadastrar (@RequestBody UfEntity ufEntity) throws SQLException {
        try {
            ufService.cadastrarUf(ufEntity);
            Object resposta = ufService.trasUfs();
            return ResponseEntity.ok(resposta);
        } catch (ListarException e) {
            throw new RuntimeException(e);
        } catch (ClassCastException e){
            throw new ClassCastException();
        }
    }

    @PutMapping
    public ResponseEntity<?> atualizarInformacoesUf(@RequestBody UfEntity ufEntity) throws SQLException, ListarException {
        ufService.atualizarUf(ufEntity);
        Object resposta = ufService.trasUfs();
        return ResponseEntity.ok(resposta);
    }
}

