package com.example.minigoogltable.service;

import com.example.minigoogltable.model.Cell;
import com.example.minigoogltable.model.CellDTO;
import com.example.minigoogltable.model.CellKey;
import com.example.minigoogltable.repozitory.CellRepository;
import com.fathzer.soft.javaluator.DoubleEvaluator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceCell {

    private final CellRepository cellRepository;

    public ServiceCell(CellRepository cellRepository) {
        this.cellRepository = cellRepository;
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

        List<Cell> cellsToSave = cellDTOs.stream()
                .map(CellDTO::toCell).collect(Collectors.toList());
        cellRepository.deleteAll();
        cellRepository.saveAll(cellsToSave);

        return cellDTOs.stream().peek(cellDTO -> {
            String content = cellDTO.getContent().trim();
            if (content.startsWith("=")) {
                String formula = content.substring(1);

                // Парсинг формулы и выполнение вычислений
                String result = evaluateFormula(formula, cellDTOs);

                cellDTO.setContent(result);
            }
        }).collect(Collectors.toList());
    }

    private static String evaluateFormula(String formula, List<CellDTO> allCells) {
        DoubleEvaluator eval = new DoubleEvaluator();
        String formulaReplace = formula;

        // Заменяем ссылки на значения в формуле
        for (CellDTO cell : allCells) {
            String cellReference = cell.getColumnNumber() + "" + cell.getRow();
            String content = cell.getContent();
            if (content.isBlank()) {content = "0";}
            formulaReplace = formulaReplace.replace(cellReference, content);
        }

        formulaReplace = formulaReplace.replaceAll("[a-zA-Z]+\\d+", "0");

        try {
            // Вычисляем формулу
            Double result = eval.evaluate(formulaReplace);
            return String.valueOf(result);
        } catch (RuntimeException e) {
            return "Error";
        }
    }
}
