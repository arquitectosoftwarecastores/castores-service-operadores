package com.grupocastores.operadores.service;

import java.util.List;

import com.grupocastores.commons.inhouse.EsquemasPago;
import com.grupocastores.commons.inhouse.Operadores;
import com.grupocastores.operadores.dto.UnidadOperadorDTO;
import com.grupocastores.operadores.dto.OperadoresSecundariosDTO;
/**
 * IAsignacionService: Interfaz para obtener datos de los operadores.
 * 
 * @version 0.0.1
 * @author Cynthia Fuentes Amaro
 * @date 2022-10-28
 */
public interface IAsignacionService {

	/**
	 * getEsquemasPago: Obtiene los esquemas de pago del catálogo
	 * 
	 * @return List<EsquemasPago>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-10-28
	 */
	public List<EsquemasPago> getEsquemasPago();
	
	/**
	 * getUnidadesCliente: Obtiene las unidades de un cliente inhouse
	 * 
	 * @param idClienteInhouse (int)
	 * @return List<UnidadOperadorRequest>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-10-28
	 */
	public List<UnidadOperadorDTO> getUnidadesCliente(int idClienteInhouse);
	
	/**
	 * filtraOperadoresDisponibles: Obtiene los operadores disponibles para asignar
	 * 
	 * @param nombre (String)
	 * @return List<Operadores>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-10-31
	 */
	public List<Operadores> filtraOperadoresDisponibles(String nombre);
	
	/**
	 * getOperadoresAsignados: Obtiene los operadores asignados a una unidad
	 * 
	 * @param idUnidad (int)
	 * @return List<OperadoresSecundariosRequest>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-11-02
	 */
	public List<OperadoresSecundariosDTO> getOperadoresAsignados(int idUnidad);
	
	/**
	 * asignarOperadores: Asigna los operadores a una unidad
	 * 
	 * @param List<OperadoresSecundariosRequest>
	 * @return List<OperadoresSecundariosRequest>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-11-03
	 */
	public List<OperadoresSecundariosDTO> asignarOperadores(List<OperadoresSecundariosDTO> operadoresSecundarios);
	
	/**
	 * updateOperadores: Actualiza los operadores para asignarlos a una unidad
	 * 
	 * @param List<OperadoresSecundariosRequest>
	 * @return List<OperadoresSecundariosRequest>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-11-03
	 */
	public List<OperadoresSecundariosDTO> updateOperadores(List<OperadoresSecundariosDTO> operadoresSecundarios);
	
}
