package com.example.minigoogltable.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class CellKey implements Serializable {

    private Integer row;
    private String columnNumber;

}
