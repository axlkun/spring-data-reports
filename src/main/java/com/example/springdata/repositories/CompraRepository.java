package com.example.springdata.repositories;

import com.example.springdata.models.Compra;
import com.example.springdata.models.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraRepository extends JpaRepository<Compra, Integer> {

}
