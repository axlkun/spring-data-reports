package com.example.springdata.repositories;

import com.example.springdata.models.Cliente;
import com.example.springdata.models.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DireccionRepository extends JpaRepository<Direccion, Integer> {

}
