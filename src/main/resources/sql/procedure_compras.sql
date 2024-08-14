-- PROCEDURE: public.procedure_compras(text, text, text)

-- DROP PROCEDURE IF EXISTS public.procedure_compras(text, text, text);

CREATE OR REPLACE PROCEDURE public.procedure_compras(
	IN p_apellido text DEFAULT NULL::text,
	IN p_ciudad text DEFAULT NULL::text,
	IN p_monto text DEFAULT NULL::text)
LANGUAGE 'plpgsql'
AS $BODY$
DECLARE
    dynamic_query TEXT;
BEGIN
    -- Crear tabla persistente para almacenar resultados si no existe
    CREATE TABLE IF NOT EXISTS persistent_compras_result (
        cliente_id INT,
        apellido TEXT,
        email TEXT,
        ciudad TEXT,
        monto NUMERIC,
        fecha TIMESTAMP
    );

    -- Limpiar la tabla persistente
    TRUNCATE TABLE persistent_compras_result;

    -- Construcción de la consulta dinámica
    dynamic_query := 'INSERT INTO persistent_compras_result
                      SELECT
                          c.id AS cliente_id,
                          c.apellido,
                          c.email,
                          d.ciudad,
                          co.monto,
                          co.fecha
                      FROM compras co
                      JOIN clientes c ON co.cliente_id = c.id
                      LEFT JOIN direcciones d ON c.id = d.cliente_id
                      WHERE
                          ($1 IS NULL OR c.apellido ILIKE ''%'' || $1 || ''%'') AND
                          ($2 IS NULL OR d.ciudad ILIKE ''%'' || $2 || ''%'') AND
                          ($3 IS NULL OR co.monto::TEXT ILIKE ''%'' || $3 || ''%'')
                      ORDER BY co.fecha DESC';

    -- Ejecutar la consulta dinámica con parámetros
    EXECUTE dynamic_query USING p_apellido, p_ciudad, p_monto;
END;
$BODY$;
ALTER PROCEDURE public.procedure_compras(text, text, text)
    OWNER TO postgres;
