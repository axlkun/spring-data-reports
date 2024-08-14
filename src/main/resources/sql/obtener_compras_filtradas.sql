-- FUNCTION: public.obtener_compras_filtradas(character varying, character varying, character varying)

-- DROP FUNCTION IF EXISTS public.obtener_compras_filtradas(character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION public.obtener_compras_filtradas(
	search_apellido character varying,
	search_ciudad character varying,
	search_monto character varying,
	OUT resultado jsonb,
	OUT estado text)
    RETURNS SETOF record
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS $BODY$
BEGIN
    -- Realizar la consulta con los filtros especificados y construir el JSON dentro de la consulta
    SELECT jsonb_agg(
        jsonb_build_object(
            'cliente_id', subquery.cliente_id,
            'apellido', subquery.apellido,
            'email', subquery.email,
            'ciudad', subquery.ciudad,
            'monto', subquery.monto,
            'fecha', subquery.fecha
        )
    )
    INTO resultado
    FROM (
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
            (search_apellido IS NULL OR c.apellido ILIKE '%' || search_apellido || '%')
            AND (search_ciudad IS NULL OR d.ciudad ILIKE '%' || search_ciudad || '%')
            AND (search_monto IS NULL OR co.monto::TEXT ILIKE '%' || search_monto || '%')
        ORDER BY co.fecha DESC
    ) AS subquery;

    -- Verificar si se encontraron resultados
    IF resultado IS NULL THEN
        resultado := '[]'::jsonb;  -- Retornar un array vacío si no hay resultados
        estado := 'Sin resultados';
    ELSE
        estado := 'Éxito';
    END IF;

    RETURN;
END;
$BODY$;

ALTER FUNCTION public.obtener_compras_filtradas(character varying, character varying, character varying)
    OWNER TO postgres;
