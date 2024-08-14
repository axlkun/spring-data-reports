-- PROCEDURE: public.get_filtered_compras(text, text, text)

-- DROP PROCEDURE IF EXISTS public.get_filtered_compras(text, text, text);

CREATE OR REPLACE PROCEDURE public.get_filtered_compras(
	IN p_apellido text DEFAULT NULL::text,
	IN p_ciudad text DEFAULT NULL::text,
	IN p_monto text DEFAULT NULL::text)
LANGUAGE 'plpgsql'
AS $BODY$
DECLARE
    dynamic_query TEXT;
    compras_record RECORD;
BEGIN
    -- Construcción de la consulta dinámica
    dynamic_query := 'SELECT
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
    FOR compras_record IN EXECUTE dynamic_query USING p_apellido, p_ciudad, p_monto LOOP
        -- Imprimir detalles de la compra
        RAISE NOTICE 'Cliente ID: %, Apellido: %, Email: %, Ciudad: %, Monto: %, Fecha: %',
                     compras_record.cliente_id,
                     compras_record.apellido,
                     compras_record.email,
                     compras_record.ciudad,
                     compras_record.monto,
                     compras_record.fecha;
    END LOOP;
END;
$BODY$;
ALTER PROCEDURE public.get_filtered_compras(text, text, text)
    OWNER TO postgres;
