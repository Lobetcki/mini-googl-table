package com.example.minigoogltable.model;

import lombok.Data;

@Data
public class CellDTO {

    private Integer row;
    private String columnNumber;
    private String content;

    public static CellDTO fromCellDTO(Cell cell) {
        CellDTO cellDTO = new CellDTO();
        cellDTO.setRow(cell.getCellKey().getRow());
        cellDTO.setColumnNumber(cell.getCellKey().getColumnNumber());
        cellDTO.setContent(cell.getContent());
        return cellDTO;
    }

    public Cell toCell() {
        CellKey cellKey = new CellKey();
        cellKey.setRow(this.getRow());
        cellKey.setColumnNumber(this.getColumnNumber());

        Cell cell = new Cell();
        cell.setCellKey(cellKey);
        cell.setContent(this.getContent());
        return cell;
    }

}
