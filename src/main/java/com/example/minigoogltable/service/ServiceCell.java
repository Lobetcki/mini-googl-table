package com.example.minigoogltable.service;

import com.example.minigoogltable.model.Cell;
import com.example.minigoogltable.model.CellDTO;
import com.example.minigoogltable.model.CellKey;
import com.example.minigoogltable.repozitory.CellRepository;
import com.fathzer.soft.javaluator.DoubleEvaluator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceCell {

    private final CellRepository cellRepository;

    public ServiceCell(CellRepository cellRepozitory) {
        this.cellRepository = cellRepozitory;
    }

    public CellDTO getData(String columnNumber, Integer row) {
        CellKey cellKey = new CellKey();
        cellKey.setColumnNumber(columnNumber);
        cellKey.setRow(row);
        return  CellDTO.fromCellDTO(cellRepository.findById(cellKey)
                .orElseThrow(NullPointerException::new));
    }

    @Transactional
    public List<CellDTO> calculateSpreadsheet(List<CellDTO> cellDTOs) {
        List<Cell> cellsToSave = new ArrayList<>();
        for (CellDTO c : cellDTOs) {
            if (c.getContent().isBlank()) {
                Cell cel = c.toCell();
                cel.setContent("");
                cellsToSave.add(cel);
            } else {
                cellsToSave.add(c.toCell());
            }
        }


        cellRepository.saveAll(cellsToSave);

        List<Cell> allCells = cellRepository.findAll();

        cellDTOs = allCells.stream().map(CellDTO::fromCellDTO).collect(Collectors.toList());

        List<CellDTO> finalCellDTOs = cellDTOs;

        return cellDTOs.stream().map(cellDTO -> {
            String content = cellDTO.getContent().trim();
            if (content.startsWith("=")) {
                String formula = content.substring(1);

                // Парсинг формулы и выполнение вычислений
                String result = evaluateFormula(formula, finalCellDTOs);

                cellDTO.setContent(result);
                return cellDTO;
            } else {
                return cellDTO;
            }
        }).collect(Collectors.toList());
    }

    private static String evaluateFormula(String formula, List<CellDTO> allCells) {
        DoubleEvaluator eval = new DoubleEvaluator();
        String formulaReplace = formula;
        // Заменяем ссылки на значения в формуле
        for (CellDTO cell : allCells) {
            String cellReference = cell.getColumnNumber() + "" + cell.getRow();
            formulaReplace = formulaReplace.replace(cellReference, cell.getContent());
        }
        try {
            // Вычисляем формулу
            Double result = eval.evaluate(formulaReplace);
            return String.valueOf(result);
        } catch (RuntimeException e) {
            return "Error";
        }
    }
}
