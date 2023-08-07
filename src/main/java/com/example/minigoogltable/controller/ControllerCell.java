package com.example.minigoogltable.controller;

import com.example.minigoogltable.model.Cell;
import com.example.minigoogltable.model.CellDTO;
import com.example.minigoogltable.service.ServiceCell;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ControllerCell {

    private final ServiceCell serviceCell;

    public ControllerCell(ServiceCell cellRepository) {
        this.serviceCell = cellRepository;
    }

    @GetMapping("/calculate")
    public ResponseEntity<List<CellDTO>> calculateSpreadsheet(@RequestBody List<CellDTO> cells) {
        List<CellDTO> cellDTOs = serviceCell.calculateSpreadsheet(cells);
        if (!cellDTOs.isEmpty()) {
            return ResponseEntity.ok(cellDTOs);
        } else {
            return ResponseEntity.noContent().build();
        }


    }
}
