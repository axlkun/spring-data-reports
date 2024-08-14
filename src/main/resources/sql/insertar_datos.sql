-- PROCEDURE: public.insertar_datos(character varying, character varying, character varying, numeric, date)

-- DROP PROCEDURE IF EXISTS public.insertar_datos(character varying, character varying, character varying, numeric, date);

CREATE OR REPLACE PROCEDURE public.insertar_datos(
	IN p_apellido character varying,
	IN p_email character varying,
	IN p_ciudad character varying,
	IN p_monto numeric,
	IN p_fecha date,
	OUT cliente_id integer,
	OUT direccion_id integer,
	OUT compra_id integer)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    -- Insertar en la tabla clientes
    INSERT INTO clientes (apellido, email)
    VALUES (p_apellido, p_email)
    RETURNING id INTO cliente_id;

    -- Insertar en la tabla direcciones
    INSERT INTO direcciones (ciudad, cliente_id)
    VALUES (p_ciudad, cliente_id)
    RETURNING id INTO direccion_id;

    -- Insertar en la tabla compras
    INSERT INTO compras (monto, fecha, cliente_id)
    VALUES (p_monto, p_fecha, cliente_id)
    RETURNING id INTO compra_id;
END;
$BODY$;
ALTER PROCEDURE public.insertar_datos(character varying, character varying, character varying, numeric, date)
    OWNER TO postgres;
