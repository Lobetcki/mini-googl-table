package com.example.minigoogltable.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class Cell {
    @EmbeddedId
    private CellKey cellKey;
    private String content;

}
