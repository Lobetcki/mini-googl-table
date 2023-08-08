package com.example.minigoogltable.controller;

import com.example.minigoogltable.model.CellDTO;
import com.example.minigoogltable.service.ServiceCell;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class ControllerCell {

    private final ServiceCell serviceCell;

    public ControllerCell(ServiceCell cellRepository) {
        this.serviceCell = cellRepository;
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
