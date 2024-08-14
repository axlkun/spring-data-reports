-- FUNCTION: public.search_compras(character varying, character varying, character varying)

-- DROP FUNCTION IF EXISTS public.search_compras(character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION public.search_compras(
	search_apellido character varying,
	search_ciudad character varying,
	search_monto character varying)
    RETURNS TABLE(cliente_id integer, apellido character varying, email character varying, ciudad character varying, monto numeric, fecha date)
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
    RETURN QUERY
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
        (search_apellido IS NULL OR c.apellido ILIKE '%' || search_apellido || '%') AND
        (search_ciudad IS NULL OR d.ciudad ILIKE '%' || search_ciudad || '%') AND
        (search_monto IS NULL OR co.monto::TEXT ILIKE '%' || search_monto || '%')
    ORDER BY co.fecha DESC;
END;
$BODY$;

ALTER FUNCTION public.search_compras(character varying, character varying, character varying)
    OWNER TO postgres;
