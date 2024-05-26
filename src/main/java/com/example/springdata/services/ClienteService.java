package com.example.springdata.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ClienteService {

    @Autowired
    private EntityManager entityManager;

    public Map<String, Object> actualizarEmailCliente(int id, String nuevoEmail) {
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("actualizar_email_cliente")
                .registerStoredProcedureParameter("p_id", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_nuevo_email", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("p_status", Integer.class, ParameterMode.OUT)
                .setParameter("p_id", id)
                .setParameter("p_nuevo_email", nuevoEmail);

        storedProcedureQuery.execute();

        String mensaje = (String) storedProcedureQuery.getOutputParameterValue("p_mensaje");
        int status = (int) storedProcedureQuery.getOutputParameterValue("p_status");

        return Map.of("mensaje", mensaje, "status", status);
    }
}
