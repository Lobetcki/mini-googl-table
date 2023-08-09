package com.example.minigoogltable.controller;

import com.example.minigoogltable.model.CellDTO;
import com.example.minigoogltable.service.ServiceCell;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class ControllerCell {

    private final ServiceCell serviceCell;

    public ControllerCell(ServiceCell cellRepository) {
        this.serviceCell = cellRepository;
    }

    @GetMapping("/")
    public ResponseEntity<CellDTO> getData(String columnNumber, Integer row) {
        CellDTO cellDTO = serviceCell.getData(columnNumber, row);
        if (cellDTO != null) {
            return ResponseEntity.ok(cellDTO);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/calculate")
    public ResponseEntity<List<CellDTO>> calculateSpreadsheet(
            @RequestBody List<CellDTO> cells) {
        List<CellDTO> cellDTOs = serviceCell.calculateSpreadsheet(cells);
        if (!cellDTOs.isEmpty()) {
            return ResponseEntity.ok(cellDTOs);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
