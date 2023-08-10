package com.example.minigoogltable.service;

import com.example.minigoogltable.model.Cell;
import com.example.minigoogltable.model.CellDTO;
import com.example.minigoogltable.model.CellKey;
import com.example.minigoogltable.repozitory.CellRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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

        // Заменяем ссылки на значения в формуле
        for (CellDTO cell : allCells) {
            String cellReference = cell.getColumnNumber() + "" + cell.getRow();
            String content = cell.getContent();
            if (content.isBlank()) {content = "0";}
            formula = formula.replace(cellReference, content);
        }

        formula = formula.replaceAll("[a-zA-Z]+\\d+", "0");

        return calculate(formula);
    }

    private static String calculate(String formula) {
        // парсим формулу

        try {
            List<String> formulaList = processing(formula);
            return calculationBrackets(formulaList).replace('.', ',');
        } catch (RuntimeException e) {
            return "Error";
        }

    }

    private static String calculationBrackets(List<String> formulaBrackets) throws RuntimeException {
        int numberBrackets = 0;
        Integer index1 = null;
        int index2 = 0;

        // Проверяем есть ли еще скобки
        if (formulaBrackets.contains("(")) {

            // Ищем начало и конец выражения в скобках
            for (int i = 0; i < formulaBrackets.size(); i++) {

                if (formulaBrackets.get(i).equals("(")) {
                    numberBrackets++;
                    if (index1 == null) index1 = i + 1;
                }
                if (formulaBrackets.get(i).equals(")")) {
                    numberBrackets--;
                    index2 = i - 1;
                }
            }
            if (numberBrackets != 0) throw new RuntimeException();

            // В лист собираем выражение в скобках
            List<String> brackets = new ArrayList<>();
            for (int i = index1; i <= index2; i++) {
                brackets.add(formulaBrackets.remove(i));
                i--;
                index2--;
            }
            formulaBrackets.remove(index2 + 1);
            formulaBrackets.set(index1 - 1, calculationBrackets(brackets));
        }

        return multiplyAndAdd(formulaBrackets);
    }

    public static String multiplyAndAdd(List<String> brackets) {
        double value1 = 0;
        double value2 = 0;
        int i = 0;
        // проверка на наличее нужного
        while (brackets.contains("*") || brackets.contains("/")) {
            if (brackets.get(i).equals("*")) {
                value1 = Double.parseDouble(brackets.remove(i - 1));
                value2 = Double.parseDouble(brackets.remove(i));
                brackets.set(i - 1, String.valueOf(value1 * value2));
                i--;
            }
            if (brackets.get(i).equals("/")) {
                value1 = Double.parseDouble(brackets.remove(i - 1));
                value2 = Double.parseDouble(brackets.remove(i));
                brackets.set(i - 1, String.valueOf(value1 / value2));
                i--;
            }
            i++;
        }

        i = 0;
        while (brackets.contains("+") || brackets.contains("-")) {
            if (brackets.get(i).equals("+")) {
                value1 = Double.parseDouble(brackets.remove(i - 1));
                value2 = Double.parseDouble(brackets.remove(i));
                brackets.set(i - 1, String.valueOf(value1 + value2));
                i--;
            }
            if (brackets.get(i).equals("-")) {
                value1 = Double.parseDouble(brackets.remove(i - 1));
                value2 = Double.parseDouble(brackets.remove(i));
                brackets.set(i - 1, String.valueOf(value1 - value2));
                i--;
            }
            i++;
        }
        return brackets.get(0);
    }

    // парсим формулу
    public static List<String> processing(String formula) {
        if (formula.contains(".")) throw new IllegalArgumentException("Error");

        String formulaReplace = formula.replaceAll("\\s", "");
        String[] formulaArr = formulaReplace
                .split("(?<=\\d)(?=[^\\d,]|,\\D)|(?<=[^\\d,]|,\\D)(?=\\d)");

        List<String> formulaList = new ArrayList<>();
        for (String arg : formulaArr) {
            if (arg.matches("\\d+") || arg.contains(",")) {
                arg = arg.replace(',', '.');
                formulaList.add(arg);
            } else {
                String[] array = arg.split("");
                formulaList.addAll(Arrays.asList(array));
            }
        }
        return formulaList;
    }
}
