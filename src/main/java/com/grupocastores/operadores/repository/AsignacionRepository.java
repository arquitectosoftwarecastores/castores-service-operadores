package com.grupocastores.operadores.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.grupocastores.commons.inhouse.EsquemasPago;
import com.grupocastores.commons.inhouse.Operadores;
import com.grupocastores.commons.inhouse.Unidades;
import com.grupocastores.operadores.service.domain.OperadoresSecundariosUnidad;

/**
 * AsignacionRepository: Repositorio para gestionar procesos de asignación de operadores.
 * 
 * @version 0.0.1
 * @author Cynthia Fuentes Amaro
 * @date 2022-10-28
 */
@Repository
public class AsignacionRepository extends UtilitiesRepository {

	@PersistenceContext
	private EntityManager entityManager;
	
	static final String queryGetEsquemasPago = 
			"SELECT * FROM OPENQUERY (" + DB_23 + ", 'SELECT * FROM bitacorasinhouse.esquemas_pago WHERE estatus = 1;');";
	
	static final String queryGetUnidadesCliente = 
			"SELECT a.*, c.id_cliente_inhouse, c.alias_inhouse, c.rfc, "
					+ "  o.horaentrada, o.horasalida, o.idesquemapago, o.nombre esquema, o.idesquemanegociacion "
					+ " FROM OPENQUERY (" + DB_13 + ", "
					+ "'SELECT u.idunidad, u.unidad, u.tipounidad, u.noeconomico, u.modelo, u.placas, c.marca, u.idoperador, "
					+ "  o.idusuario, o.nombre, ur.idcliente, ur.idoficina, ur.cliente, ur.complementocliente, ur.observacion "
					+ " FROM monitoreo.unidades_renta ur "
					+ "  LEFT JOIN camiones.unidades u ON ur.noeconomico = u.noeconomico "
					+ "  LEFT JOIN camiones.camiones c ON u.unidad = c.unidad AND u.noeconomico = c.noeconomico AND u.idoperador = c.operador "
					+ "  LEFT JOIN camiones.operadores o ON u.idoperador = o.idpersonal AND o.status = 1 "
					+ " WHERE u.estatus = 1 AND ur.estatus = 1') AS a " 
					+ "INNER JOIN OPENQUERY(" + DB_23 + ", "
					+ "'SELECT ci.id_cliente_inhouse, ci.alias_inhouse, b.rfc, c.idcliente "
					+ " FROM bitacorasinhouse.clientes_inhouse ci "
					+ "  INNER JOIN bitacorasinhouse.clientes_inhouse_clientes2009 b ON ci.id_cliente_inhouse = b.id_cliente_inhouse "
					+ "  INNER JOIN clientes.clientes2009 c ON b.id_cliente = c.idcliente AND b.rfc = c.rfc "
					+ " WHERE b.estatus = 1 AND c.status = 1 AND ci.id_cliente_inhouse = %s GROUP BY ci.alias_inhouse;') AS c ON a.complementocliente = c.alias_inhouse " 
					+ "LEFT JOIN OPENQUERY("+ DB_23 + ", "
					+ "'SELECT op.idoperador, op.idunidad, op.horaentrada, op.horasalida, "
					+ "  op.idesquemapago, ep.nombre, op.idesquemanegociacion "
					+ " FROM bitacorasinhouse.operadores_secundarios_unidad op "
					+ "  INNER JOIN bitacorasinhouse.esquemas_pago ep ON op.idesquemapago = ep.idesquemapago "
					+ " WHERE op.estatus = 1 AND op.tipooperador = 1 AND op.ordenoperador = 1 AND ep.estatus = 1;') AS o ON a.idoperador = o.idoperador AND a.idunidad = o.idunidad;";
	
	static final String queryFilterOperadoresDisponibles = 
			"SELECT * FROM OPENQUERY (" + DB_13 + ", "
					+ "'SELECT o.* FROM camiones.operadores o "
					+ "  LEFT JOIN camiones.unidades u ON o.idpersonal = u.idoperador "
					+ "  LEFT JOIN camiones.camiones c ON o.idpersonal = c.operador "
					+ " WHERE u.idoperador IS NULL AND c.operador IS NULL AND o.unidad IS NULL AND o.status = 1 AND o.nombre LIKE \"%s\";');";
	
	static final String queryGetOperadoresAsignados = 
			"SELECT a.*, b.nombre FROM OPENQUERY (" + DB_23 + ", "
					+ "'SELECT os.idunidad, os.tipounidad, os.idoperador, os.idesquemapago, ep.nombre esquemaPago, "
					+ "  os.tipooperador, os.ordenoperador, os.horaentrada, os.horasalida, os.fechamod, os.horamod, "
					+ "  os.idpersonalmod, os.idoperadoresunidad, os.idesquemanegociacion "
					+ " FROM bitacorasinhouse.operadores_secundarios_unidad os "
					+ "  INNER JOIN bitacorasinhouse.esquemas_pago ep ON os.idesquemapago = ep.idesquemapago "
					+ " WHERE os.idunidad = \"%s\" AND os.tipooperador = 2 AND os.estatus = 1;') AS a "
					+ "LEFT JOIN OPENQUERY(" + DB_13 + ", "
					+ "'SELECT idpersonal, nombre "
					+ " FROM camiones.operadores;') AS b ON a.idoperador = b.idpersonal ORDER BY 7;";
	
	static final String queryGetMaxIdOperadorAsignado = 
			"SELECT * FROM OPENQUERY (" + DB_23 + ", "
					+ "'SELECT MAX(os.idoperadoresunidad) FROM bitacorasinhouse.operadores_secundarios_unidad os;');";
	
	static final String queryGetUnidadesAsignadasByOperador = 
			"SELECT c.* FROM OPENQUERY (" + DB_23 + ", "
					+ "'SELECT * FROM bitacorasinhouse.operadores_secundarios_unidad os "
					+ " WHERE os.idoperador = \"%s\" AND os.tipooperador = %s AND os.estatus = 1 %s') AS a "
					+ "INNER JOIN OPENQUERY(" + DB_13 + ", "
					+ "'SELECT * FROM camiones.unidades;') AS c ON a.idunidad = c.idunidad;";
	
	static final String queryCreateOperadoresSecundarios =
			"INSERT INTO OPENQUERY(" + DB_23 + ", 'SELECT * FROM bitacorasinhouse.operadores_secundarios_unidad') VALUES(NULL, %s, %s, %s, %s, %s, %s, %s, '%s', '%s', 1, '%s', '%s', %s)";
	
	static final String queryUpdateOperadoresSecundarios =
			"UPDATE OPENQUERY(" + DB_23 + ", 'SELECT * FROM bitacorasinhouse.operadores_secundarios_unidad WHERE %s') SET fechamod = '%s', horamod = '%s', idpersonalmod = %s, idunidad = %s, tipounidad = %s, idoperador = %s, idesquemapago = %s, idesquemanegociacion = %s, tipooperador = %s, ordenoperador = %s, horaentrada = '%s', horasalida = '%s';";
	
	static final String queryUpdateEstatusOperadoresSecundarios =
			"UPDATE OPENQUERY(" + DB_23 + ", 'SELECT * FROM bitacorasinhouse.operadores_secundarios_unidad WHERE %s') SET fechamod = '%s', horamod = '%s', estatus = %s;";
	
	/**
	 * getEsquemasPago: Obtiene los esquemas de pago del catálogo
	 * 
	 * @return List<EsquemasPago>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-10-28
	 */
	@SuppressWarnings("unchecked")
	public List<EsquemasPago> getEsquemasPago() {
		Query query = entityManager
				.createNativeQuery(queryGetEsquemasPago, EsquemasPago.class);
		
		return query.getResultList();

	}
	
	/**
	 * getUnidadesCliente: Obtiene las unidades de renta de un cliente
	 * 
	 * @param idClienteInhouse (int)
	 * @return List<Object[]>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-10-28
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getUnidadesCliente(int idClienteInhouse) {
		Query query = entityManager
				.createNativeQuery(String.format(queryGetUnidadesCliente, idClienteInhouse));

		return query.getResultList();

	}
	
	/**
	 * filterOperadoresDisponibles: Filtra los operadores disponibles
	 * 
	 * @param nombre (String)
	 * @return List<Operadores>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-10-31
	 */
	@SuppressWarnings("unchecked")
	public List<Operadores> filterOperadoresDisponibles(String nombre) {
		Query query = entityManager
				.createNativeQuery(String.format(queryFilterOperadoresDisponibles, "%" + nombre + "%"), Operadores.class);

		return query.getResultList();

	}
	
	/**
	 * getOperadoresAsignados: Obtiene los datos de los operadores asignados
	 * 
	 * @param idUnidad (int)
	 * @return List<Object[]>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-11-01
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getOperadoresAsignados(int idUnidad) {
		Query query = entityManager
				.createNativeQuery(String.format(queryGetOperadoresAsignados, idUnidad));

		return query.getResultList();

	}
	
	/**
	 * getUnidadesAsignadasByOperador: Obtiene los datos de las unidades asignadas por operador secundario
	 * 
	 * @param idOperador (int)
	 * @param tipoOperador (int)
	 * @param validacionExtra (String)
	 * @return List<OperadoresSecundariosUnidad>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-11-03
	 */
	@SuppressWarnings("unchecked")
	public List<Unidades> getUnidadesAsignadasByOperador(int idOperador, int tipoOperador, String validacionExtra) {
		Query query = entityManager
				.createNativeQuery(String.format(queryGetUnidadesAsignadasByOperador, idOperador, tipoOperador, validacionExtra), Unidades.class);

		return query.getResultList();

	}
	
	/**
	 * insertOperadoresSecundarios: Asigna operadores secundarios a una unidad
	 * 
	 * @param OperadoresSecundariosUnidad
	 * @return OperadoresSecundariosUnidad
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-11-02
	 */
	@SuppressWarnings("unchecked")
	public OperadoresSecundariosUnidad insertOperadoresSecundarios(OperadoresSecundariosUnidad operadoresSecundarios) throws Exception {

		String query = String.format(queryCreateOperadoresSecundarios, 
				operadoresSecundarios.getIdUnidad(), operadoresSecundarios.getTipoUnidad(), 
				operadoresSecundarios.getIdOperador(), operadoresSecundarios.getIdEsquemaPago(), 
				operadoresSecundarios.getIdEsquemaNegociacion(), operadoresSecundarios.getTipoOperador(), 
				operadoresSecundarios.getOrdenOperador(), operadoresSecundarios.getHoraEntrada(), 
				operadoresSecundarios.getHoraSalida(), operadoresSecundarios.getFechaMod(), 
				operadoresSecundarios.getHoraMod(), operadoresSecundarios.getIdPersonalMod());
		
		if (executeStoredProcedure(query) == false)
			return null;
		
		List<Object> list = entityManager.createNativeQuery(String.format(queryGetMaxIdOperadorAsignado)).getResultList();
		if (!list.isEmpty()) {
			operadoresSecundarios.setIdOperadoresUnidad(list.get(0) instanceof Integer ? (int)list.get(0) : 0);
			operadoresSecundarios.setEstatus((short)1);
		}
				
		return operadoresSecundarios;
		
	}
	
	/**
	 * updateOperadoresSecundarios: Modifica operadores secundarios asignados a una unidad
	 * 
	 * @param OperadoresSecundariosUnidad
	 * @return OperadoresSecundariosUnidad
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-11-02
	 */
	public OperadoresSecundariosUnidad updateOperadoresSecundarios(OperadoresSecundariosUnidad operadoresSecundarios, String param) throws Exception {
		
		String query = operadoresSecundarios.getEstatus() == 1 ? 
				String.format(queryUpdateOperadoresSecundarios, 
						param, operadoresSecundarios.getFechaMod(), operadoresSecundarios.getHoraMod(), 
						operadoresSecundarios.getIdPersonalMod(), operadoresSecundarios.getIdUnidad(),
						operadoresSecundarios.getTipoUnidad(), operadoresSecundarios.getIdOperador(), 
						operadoresSecundarios.getIdEsquemaPago(), operadoresSecundarios.getIdEsquemaNegociacion(), 
						operadoresSecundarios.getTipoOperador(), operadoresSecundarios.getOrdenOperador(), 
						operadoresSecundarios.getHoraEntrada(), operadoresSecundarios.getHoraSalida()) :
				String.format(queryUpdateEstatusOperadoresSecundarios, 
						param, operadoresSecundarios.getFechaMod(), operadoresSecundarios.getHoraMod(), 
						operadoresSecundarios.getIdPersonalMod(), operadoresSecundarios.getEstatus());
		
		if (executeStoredProcedure(query) == false)
			return null;
		
		return operadoresSecundarios;
		
	}
	
}
