package com.example.minigoogltable.repozitory;

import com.example.minigoogltable.model.Cell;
import com.example.minigoogltable.model.CellKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CellRepository extends JpaRepository<Cell, CellKey> {



}
