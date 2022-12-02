package com.grupocastores.operadores.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.grupocastores.commons.inhouse.Camiones;
import com.grupocastores.commons.inhouse.CatalogoConfiguraciones;
import com.grupocastores.commons.inhouse.ClaveprodservcpSat;
import com.grupocastores.commons.inhouse.ClaveunidadpesoSat;
import com.grupocastores.commons.inhouse.Esquemasdocumentacion;
import com.grupocastores.commons.inhouse.EstatusViaje;
import com.grupocastores.commons.inhouse.Sucursales2009;
import com.grupocastores.commons.inhouse.TipocomprobanteSat;
import com.grupocastores.commons.inhouse.Tipodepago;
import com.grupocastores.commons.inhouse.Tipotalon;
import com.grupocastores.commons.inhouse.Tipounidad;
import com.grupocastores.commons.inhouse.Unidades;
import com.grupocastores.commons.inhouse.UnidadesUrea;
import com.grupocastores.commons.inhouse.UsoCfdi;

/**
 * CatalogoRepository: Repositorio para consultar los catalogos.
 * 
 * @version 0.0.1
 * @author Moises Lopez Arrona [moisesarrona]
 * @date 2022-06-14
 */
@Repository
public class CatalogoRepository extends UtilitiesRepository {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	
	static final String queryFindTipoDocumentoSat =
			"SELECT *FROM OPENQUERY(" + DB_23 + ", 'SELECT *FROM cfdinomina.tipocomprobante_sat WHERE idtipocomprobante IN(1, 2);');";
	
	static final String queryFindTipoTalon =
			"SELECT *FROM OPENQUERY(" + DB_23 + ", 'SELECT * FROM talones.tipotalon WHERE idtipotalon IN(1, 2);');";
	
	static final String queryFindEsquemas =
			"SELECT *FROM OPENQUERY(" + DB_23 + ", 'SELECT * FROM bitacorasinhouse.esquemas WHERE id_esquema IN(1,2,3);');";
	 	
	static final String queryFindTipounidad =
			"SELECT *FROM OPENQUERY(" + DB_23 + ", 'SELECT * FROM camiones.tipounidad WHERE idtipounidad IN(1, 2);');";
	
	static final String queryFindTipoPago =
			"SELECT *FROM OPENQUERY(" + DB_23 + ", 'SELECT * FROM talones.tipodepago WHERE idtipopago IN(1, 2);');";
	
	static final String queryFindClaveProducto =
			"SELECT TOP 20 *FROM OPENQUERY(" + DB_23 + ", 'SELECT *FROM cfdinomina.claveprodservcp_sat WHERE descripcion LIKE \"%s\" OR claveprodservcp LIKE \"%s\";')";
	
	static final String queryFindClaveUnidadPeso =
			"SELECT TOP 20 *FROM OPENQUERY(" + DB_23 + ", 'SELECT *FROM cfdinomina.claveunidadpeso_sat WHERE claveunidadpeso LIKE \"%s\" OR nombre LIKE \"%s\";')";
	
	static final String queryFindEmpaque =
			"SELECT *FROM OPENQUERY(" + DB_23 + ",'SELECT ce.*,crc.id AS idClaveUnidad, crc.claveunidad AS claveUnidad, cs.descripcion FROM clientes.empaques ce INNER JOIN cfdinomina.relacion_empaques_claveunidad_sat crc ON ce.idempaque = crc.idempaque INNER JOIN cfdinomina.claveunidadpeso_sat cs ON crc.claveunidad = cs.claveunidadpeso WHERE ce.nombre LIKE \"%s\";')";
	
	static final String queryFilterSucursal =
			"SELECT TOP 10 * FROM OPENQUERY(" + DB_13 + ",'SELECT * FROM clientes.sucursales2009 WHERE idcliente = \"%s\" AND TRIM(CONCAT(nombre, \", \", colonia,  \", \") ) LIKE \"%s\";')";
	
	static final String queryFindSucursalByClient =
			"SELECT * FROM OPENQUERY(" + DB_13 + ",'SELECT *FROM clientes.sucursales2009 WHERE idcliente = \"%s\" AND idoficina = \"%s\";')";
	
	static final String queryGetSucursalByClient =
			"SELECT * FROM OPENQUERY(" + DB_13 + ",'SELECT *FROM clientes.sucursales2009 WHERE idcliente = \"%s\" AND idsucursal = \"%s\" AND idoficina = \"%s\" ;')";
	
	static final String queryFindSucursalesCliente =
			"SELECT * FROM OPENQUERY(" + DB_13 + ",'SELECT * FROM clientes.sucursales2009 WHERE idcliente IN (%s);')";
	
	static final String queryFindOneSucursal =
			"SELECT *FROM OPENQUERY(" + DB_13 + ",'SELECT *FROM clientes.sucursales2009 WHERE idcliente = \"%s\" AND idoficina = %s ORDER BY idsucursal DESC LIMIT 1;') ";
		
	static final String queryFindSucursal = 
			"SELECT * FROM OPENQUERY(" + DB_13 + ",'SELECT * FROM clientes.sucursales2009 WHERE idcliente = %s AND idsucursal = %s AND idoficina = %s;');";
	
	static final String queryFindCiudadAndEstado =
			"SELECT *FROM OPENQUERY (" + DB_23 + ", 'SELECT c.idciudad, c.nombre AS ciudad, e.idestado, e.nombre as estado FROM camiones.ciudades c INNER JOIN camiones.estados e ON e.idestado = c.idestado WHERE c.idciudad = %s AND estatus = 1;');";
	
	static final String queryFindCiudadAndEstadoByCiudad =
			"SELECT *FROM OPENQUERY (" + DB_23 + ", 'SELECT c.idciudad, c.nombre AS ciudad, e.idestado, e.nombre as estado FROM camiones.ciudades c INNER JOIN camiones.estados e ON e.idestado = c.idestado WHERE c.nombre LIKE \"%s\" AND estatus = 1;');";

	static final String queryFindOficinaByCiudad =
			"SELECT *FROM OPENQUERY (" + DB_23 + ", 'SELECT c.idoficina FROM camiones.ciudades c WHERE c.idciudad = %s;');";

	static final String queryFindNegociaciones = 
			"SELECT *FROM OPENQUERY(" + DB_23 + ", 'SELECT nc.id_negociacion_cliente as idNegociacionCliente,cc.id_cliente as idCliente,c.id_cliente_inhouse as idClienteInhouse,nc.id_negociacion as idNegociacion,e.id_esquema as idEsquema,c.alias_inhouse as aliasInhouse,n.desc_negociacion as descripcionNegociacion FROM bitacorasinhouse.negociaciones_clientes nc LEFT JOIN bitacorasinhouse.negociaciones n ON n.id_negociacion = nc.id_negociacion LEFT JOIN bitacorasinhouse.esquemas e ON e.id_esquema = n.id_esquema JOIN bitacorasinhouse.clientes_inhouse c ON c.id_cliente_inhouse = nc.id_cliente_inhouse INNER JOIN bitacorasinhouse.clientes_inhouse_clientes2009 cc ON c.id_cliente_inhouse = cc.id_cliente_inhouse WHERE nc.estatus_activo = 1 AND id_cliente = %s AND c.id_cliente_inhouse = %s AND e.id_esquema = %s  GROUP BY id_negociacion_cliente;');";
	
	static final String queryFindPersonal =
			"SELECT * FROM OPENQUERY(" + DB_13 + ", 'SELECT %s FROM personal.personal WHERE %s = %s;')";
	
	static final String queryGetCatalogoEstatusViaje =
			"SELECT * FROM OPENQUERY(" + DB_23 + ", 'SELECT * FROM bitacorasinhouse.estatus_viaje;');";
	
	static final String queryGetCatalogoConfiguraciones =
			"SELECT * FROM OPENQUERY(" + DB_23 + ", 'SELECT * FROM bitacorasinhouse.catalogo_config;');";
	
	static final String queryFilterCatalogoConfiguracionesByModulo =
			"SELECT * FROM OPENQUERY(" + DB_23 + ", 'SELECT * FROM bitacorasinhouse.catalogo_config WHERE modulo = %s;');";

	static final String queryFindUnidadByNoEconomico =
			"SELECT * FROM OPENQUERY(" + DB_13 + ", 'SELECT * FROM camiones.unidades WHERE (tipounidad = 1 OR tipounidad = 2) AND estatus = 1 AND noeconomico LIKE \"%s\";');";
	
	static final String queryFindUnidadUrea =
			"SELECT * FROM OPENQUERY(" + DB_13 + ", 'SELECT * FROM camiones.unidades_urea WHERE estatus = 1 AND unidad = %s AND noeconomico LIKE \"%s\";');";

	static final String queryFindUnidad =
			"SELECT * FROM OPENQUERY(" + DB_13 + ", 'SELECT	 * FROM camiones.`camiones` WHERE unidad = %s');";
	
	static final String querygetUsoCfdi =
			"SELECT * FROM OPENQUERY(" + DB_23 + ", 'SELECT * FROM cfdinomina.usocfdi_sat WHERE idusocfdi = %s');";
	
	/**
	 * findTipoDocumentoSat: Encuentra los documentos del sat (1, 2)
	 * 
	 * @return List<TipocomprobanteSat>
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<TipocomprobanteSat> findTipoDocumentoSat() {
		
		Query query = entityManager.createNativeQuery(
				queryFindTipoDocumentoSat, 
				TipocomprobanteSat.class
			);
		return query.getResultList();
		
	}
	
	/**
	 * findTipoTalon: Encuentra los tipos de talones (1, 2)
	 * 
	 * @return List<Tipotalon>
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<Tipotalon> findTipoTalon() {
		
		Query query = entityManager.createNativeQuery(
				queryFindTipoTalon, 
				Tipotalon.class
			);
		return query.getResultList();
		
	}

	/**
	 * findEsquemaDoc: Encuentra el tipo de esquema (1, 2, 3)
	 * 
	 * @return List<Esquemasdocumentacion>
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<Esquemasdocumentacion> findEsquemaDoc() {
		
		Query query = entityManager.createNativeQuery(
				queryFindEsquemas, 
				Esquemasdocumentacion.class
			);
		return query.getResultList();
		
	}
	
	/**
	 * findTipoUnidad: Encuentra el tipo de unidad (1, 2)
	 * 
	 * @return List<Tipounidad>
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<Tipounidad> findTipoUnidad() {
		
		Query query = entityManager.createNativeQuery(
				queryFindTipounidad,
				Tipounidad.class
			);
		return query.getResultList();
		
	}
	
	/**
	 * findTipoPago: Encuentra el tipo de pago (1, 2)
	 * 
	 * @return List<Tipodepago>
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<Tipodepago> findTipoPago() {
		
		Query query = entityManager.createNativeQuery(
				queryFindTipoPago,
				Tipodepago.class
			);
		return query.getResultList();
		
	}

	/**
	 * findClaveProducto: Encuentra las claves de producto
	 * 
	 * @param description (String)
	 * @return List<ClaveprodservcpSat>
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<ClaveprodservcpSat> findClaveProducto(String description) {
		
		Query query = entityManager.createNativeQuery(String.format(
				queryFindClaveProducto,
				"%" + description + "%",
				"%" + description + "%"
			), ClaveprodservcpSat.class);
		return query.getResultList();
		
	}
	
	/**
	 * findClaveUnidadPeso: Encuentra las claves de unidad
	 * 
	 * @param key (String)
	 * @return List<ClaveunidadpesoSat>
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<ClaveunidadpesoSat> findClaveUnidadPeso(String key) {
		
		Query query = entityManager.createNativeQuery(String.format(
				queryFindClaveUnidadPeso,
				"%" + key + "%",
				"%" + key + "%"
			), ClaveunidadpesoSat.class);
		return query.getResultList();
		
	}
	
	/**
	 * findEmpaque: Encuentra los empaques
	 * 
	 * @param name (String)
	 * @return List<Empaques>
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findEmpaque(String name) {
		
		Query query = entityManager.createNativeQuery(String.format(
				queryFindEmpaque,
				"%" + name + "%"
			));
		return query.getResultList();
		
	}
	
	/**
	 * findSucursal: Encuentra y filtra los sucurssales de un inhouse
	 * 
	 * @param idCliente (String)
	 * @param sucursal (String)
	 * @return List<Sucursales2009>
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<Sucursales2009> findSucursal (String idCliente, String sucursal) {
		
		Query query = entityManager.createNativeQuery(String.format(
				queryFilterSucursal,
				idCliente,
				"%" + sucursal + "%"
			), Sucursales2009.class);
		return query.getResultList();
		
	}
	
	/**
	 * findSucursal: Encuentra sucursales de inhouse por id de cliente
	 * 
	 * @param idCliente (String)
	 * @param sucursal (String)
	 * @return List<Sucursales2009>
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<Sucursales2009> findSucursalByCliente (String idCliente, String idOficina) {
		
		Query query = entityManager.createNativeQuery(String.format(
				queryFindSucursalByClient,
				idCliente,
				idOficina
			), Sucursales2009.class);
		return query.getResultList();
		
	}
	@SuppressWarnings("unchecked")
	public Sucursales2009 findSucursalByCliente (int idCliente,int idSucursal, String idOficina) {
		
		Query query = entityManager.createNativeQuery(String.format(
				queryGetSucursalByClient,
				idCliente,
				idSucursal,
				idOficina
			), Sucursales2009.class);
		return(Sucursales2009) query.getResultList().get(0);
		
	}
	
	/**
	 * findSucursalesCliente: Encuentra sucursales de un inhouse
	 * 
	 * @param idCliente (String)
	 * @return List<Sucursales2009>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-08-19
	 */
	@SuppressWarnings("unchecked")
	public List<Sucursales2009> findSucursalesCliente (String idCliente) {
		
		Query query = entityManager.createNativeQuery(String.format(
				queryFindSucursalesCliente,
				idCliente
			), Sucursales2009.class);
		return query.getResultList();
		
	}
	
	/**
	 * findOneSucursal: Encuentra solo una los sucurssal de los inhouse
	 * 
	 * @param idcliente (int)
	 * @param idoficina (String)
	 * @return Sucursales2009
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	public Sucursales2009 findOneSucursal(int idcliente, String idoficina) {
		
		Query query = entityManager.createNativeQuery(String.format(
				queryFindOneSucursal, 
				idcliente,
				idoficina
			), Sucursales2009.class);
		List<?> list = query.getResultList();
		if (list.isEmpty())
			return null;
		return (Sucursales2009)list.get(0);
		
	}
	
	/**
	 * findSucursal: Encuentra una Sucursal que corresponda con el idSucursal, el idCliente y el idOficina
	 * 
	 * @param idCliente		(int)
	 * @param idSucursal	(int)
	 * @param idOficina		(int)
	 * @return Sucursales2009
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-08-15
	 */
	@SuppressWarnings("unchecked")
	public Sucursales2009 findSucursal(int idCliente, int idSucursal, int idOficina) {

		Query query = entityManager.createNativeQuery(
						String.format(queryFindSucursal, idCliente, idSucursal, idOficina), Sucursales2009.class);
		
		List<Object> list = query.getResultList();
		if (list.isEmpty())
			return null;
		return (Sucursales2009)list.get(0);

	}
	
	/**
	 * findCiudadAndEstado: Encuentra ciudad y estado por ID
	 * 
	 * @param idCiudad (int)
	 * @return List<Object[]>
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findCiudadAndEstado (int idCiudad) {
		
		Query query = entityManager.createNativeQuery(String.format(
				queryFindCiudadAndEstado,
				idCiudad
			));
		return query.getResultList();
		
	}
	
	/**
	 * findCiudadAndEstado: Encuentra ciudad y estado por ciudad
	 * 
	 * @param ciudad (String)
	 * @return List<Object[]>
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findCiudadAndEstadoByCiudad (String ciudad) {
		
		Query query = entityManager.createNativeQuery(String.format(
				queryFindCiudadAndEstadoByCiudad,
				"%" + ciudad + "%"
			));
		return query.getResultList();
		
	}
	
	/**
	 * findOficinaByCiudad: Encuentra el idoficina por idciudad
	 * 
	 * @param idCiudad         (int)
	 * @return Object
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-09-20
	 */
	@SuppressWarnings("unchecked")
	public Object findOficinaByCiudad(int idCiudad) {

		Query query = entityManager
				.createNativeQuery(String.format(queryFindOficinaByCiudad, idCiudad));

		List<Object> list = query.getResultList();
		if (!list.isEmpty())
			return list.get(0);
		return null;

	}

	/**
	 * findNegociaciones: Encuentra las negociaciones de un inhouse
	 * 
	 * @param idCliente (String)
	 * @param idClienteInhouse (String)
	 * @param esquema (String)
	 * @return List<Object[]>
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findNegociaciones (String idCliente, String idClienteInhouse, String esquema) {
		
		Query query = entityManager.createNativeQuery(String.format(
				queryFindNegociaciones,
				idCliente,
				idClienteInhouse,
				esquema
			));
		
		return query.getResultList();
		
	}
	
	/**
	 * insertSucursal: Guarda una sucursal
	 * 
	 * @param Sucursales2009 (String)
	 * @return Sucursales2009
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	public Sucursales2009 insertSucursal (Sucursales2009 sucursales2009) throws Exception {
		String queryCreateSucursal =
				"INSERT INTO OPENQUERY(" + DB_13 + ", 'SELECT * FROM clientes.sucursales2009 LIMIT 1') VALUES(" + sucursales2009.getIdcliente() + "," + sucursales2009.getIdsucursal() + ",'" + sucursales2009.getNombre() + "','" + 
						sucursales2009.getCalle() + "','"+ sucursales2009.getNoexterior() + "','" + sucursales2009.getNointerior() + "','" + sucursales2009.getColonia() + "','" + sucursales2009.getDelegacion() + "','" +
						sucursales2009.getCp() + "','" + sucursales2009.getIdciudad( ) + "','" + sucursales2009.getTelefono() + "','" + sucursales2009.getFechaalta() + "'," + sucursales2009.getAltapor() + ",'" + 
						sucursales2009.getIdoficina() + "')";
		if (executeStoredProcedure(queryCreateSucursal) == false)
			throw new Exception("No se pudo agregar la nueva sucursal");
		return findOneSucursal(sucursales2009.getIdcliente(), sucursales2009.getIdoficina());
		
	}
	
	/**
	 * findPersonal: Encuentra el usuario por idPersonal o idUsuario
	 * 	 
	 * @param out (String)
	 * @param in (String)
	 * @param id (int)
	 * @return Object
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-07-29
	 */	
	@SuppressWarnings("unchecked")
	public Object findPersonal(String out, String in, String id) {
		Query query = entityManager
				.createNativeQuery(String.format(queryFindPersonal, out, in, id));
		
		List<Object> list = query.getResultList();
		if (!list.isEmpty())
			return list.get(0);
		return null;

	}
	
	/**
	 * getEstatusViaje: Obtiene el catalogo de estatus de viaje
	 * 
	 * @param Sucursales2009 (String)
	 * @return Sucursales2009
	 * @author Moises Lopez Arrona [moisesarrona]
	 * @date 2022-06-14
	 */
	@SuppressWarnings("unchecked")
	public List<EstatusViaje> getEstatusViaje(){
		Query query = entityManager.createNativeQuery(String.format(queryGetCatalogoEstatusViaje),
				EstatusViaje.class);
		List<EstatusViaje> list = query.getResultList();
		return list;
	}
	
	 
	/**
	 * getCatalogoConfiguraciones: Obtiene el catalogo configuraciones por cliente
	 * 
	 * @param Sucursales2009 (String)
	 * @return List<CatalogoConfiguraciones>
	 * @author Oscar Eduardo Guerra Salcedo[OscarGuerra]
	 * @date 2022-08-23
	 */
	@SuppressWarnings("unchecked")
	public List<CatalogoConfiguraciones> getCatalogoConfiguraciones() {
		Query query = entityManager.createNativeQuery(String.format(
				queryGetCatalogoConfiguraciones),
				CatalogoConfiguraciones.class);
		return (List<CatalogoConfiguraciones>) query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<CatalogoConfiguraciones> getCatalogoConfiguraciones(int idModulo) {
		Query query = entityManager.createNativeQuery(String.format(
				queryFilterCatalogoConfiguracionesByModulo,
				idModulo),
				CatalogoConfiguraciones.class);
		return (List<CatalogoConfiguraciones>) query.getResultList();	
	}
	
	/**
	 * findUnidadByNoEconomico: Obtiene la unidad por noeconomico
	 * 
	 * @param noeconomico (String)
	 * @return List<Unidades>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-09-30
	 */
	@SuppressWarnings("unchecked")
	public List<Unidades> findUnidadByNoEconomico(String noEconomico){
		Query query = entityManager.createNativeQuery(String.format(queryFindUnidadByNoEconomico, noEconomico + "%"),
				Unidades.class);
		List<Unidades> list = query.getResultList();
		return list;
	}
	
	/**
	 * findUnidadUrea: Obtiene la unidad de urea
	 * 
	 * @param unidad (int)
	 * @param noeconomico (String)
	 * @return List<UnidadesUrea>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-09-30
	 */
	@SuppressWarnings("unchecked")
	public List<UnidadesUrea> findUnidadUrea(int unidad, String noEconomico){
		Query query = entityManager.createNativeQuery(String.format(queryFindUnidadUrea, unidad, "%" + noEconomico + "%"),
				UnidadesUrea.class);
		List<UnidadesUrea> list = query.getResultList();
		return list;
	}
	
	/**
	 * getEstatusViaje: Obtiene unidad por id de unidad
	 * 
	 * @param idUnidad 
	 * @return Camiones
	 * @author OscarEduardo Guerra Salcedo [OscarGuerra]
	 * @date 2022-09-08
	 */
	@SuppressWarnings("unchecked")
	public Camiones getUnidad(int idUnidad){
		Query query = entityManager.createNativeQuery(String.format(
				queryFindUnidad,
				idUnidad),
				Camiones.class);
		return (Camiones) query.getResultList().get(0);
	
	}
	
	/**
	 * getEstatusViaje: Obtiene unidad por id de unidad
	 * 
	 * @param idUnidad 
	 * @return Camiones
	 * @author OscarEduardo Guerra Salcedo [OscarGuerra]
	 * @date 2022-09-08
	 */
	@SuppressWarnings("unchecked")
	public UsoCfdi getUsoCfdi(String idUsoCfdi){
		Query query = entityManager.createNativeQuery(String.format(
				querygetUsoCfdi,
				idUsoCfdi),UsoCfdi.class
				);
		return (UsoCfdi) query.getResultList().get(0);
	
	}
	
}
