-- PROCEDURE: public.actualizar_email_cliente(integer, character varying)

-- DROP PROCEDURE IF EXISTS public.actualizar_email_cliente(integer, character varying);

CREATE OR REPLACE PROCEDURE public.actualizar_email_cliente(
	IN p_id integer,
	IN p_nuevo_email character varying,
	OUT p_mensaje character varying,
	OUT p_status integer)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
    -- Actualizar el correo electrónico del cliente
    UPDATE clientes
    SET email = p_nuevo_email
    WHERE id = p_id;

    -- Verificar si la actualización fue exitosa
    IF FOUND THEN
        p_mensaje := 'Correo electrónico actualizado correctamente.';
		p_status := 200;
    ELSE
        p_mensaje := 'No se encontró el cliente con el ID proporcionado.';
		p_status := 404;
    END IF;
END;
$BODY$;
ALTER PROCEDURE public.actualizar_email_cliente(integer, character varying)
    OWNER TO postgres;
