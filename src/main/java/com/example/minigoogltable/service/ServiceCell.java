package com.example.minigoogltable.service;

import com.example.minigoogltable.model.Cell;
import com.example.minigoogltable.model.CellDTO;
import com.example.minigoogltable.repozitory.CellRepository;
import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceCell {

    private final CellRepository cellRepository;

    public ServiceCell(CellRepository cellRepozitory) {
        this.cellRepository = cellRepozitory;
    }

    @Transactional
    public List<CellDTO> calculateSpreadsheet(List<CellDTO> cellDTOs) {
        List<Cell> cellsToSave = cellDTOs.stream()
                .filter(c -> c != null && !c.getContent().isBlank())
                .map(CellDTO::toCell)
                .collect(Collectors.toList());

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
        // Заменяем ссылки на значения в формуле
        for (CellDTO cell : allCells) {
            String cellReference = cell.getColumnNumber() + "" + cell.getRow();
            formula = formula.replace(cellReference, cell.getContent());
        }
        try {
            // Вычисляем формулу
            Double result = eval.evaluate(formula);
            return String.valueOf(result);
        } catch (RuntimeException e) {
            return "=" + formula;
        }
    }
}
