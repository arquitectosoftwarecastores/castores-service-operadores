package com.grupocastores.operadores.service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.ws.Holder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.grupocastores.commons.inhouse.EsquemasPago;
import com.grupocastores.commons.inhouse.Operadores;
import com.grupocastores.commons.inhouse.Unidades;
import com.grupocastores.operadores.dto.UnidadOperadorDTO;
import com.grupocastores.operadores.dto.OperadoresSecundariosDTO;
import com.grupocastores.operadores.service.domain.OperadoresSecundariosUnidad;
import com.grupocastores.operadores.repository.AsignacionRepository;
import com.grupocastores.operadores.repository.UtilitiesRepository;

/**
 * AsignacionServiceImpl: Servicio para la asignación de operadores.
 * 
 * @version 0.0.1
 * @author Cynthia Fuentes
 * @date 2022-10-27
 */
@Service
public class AsignacionServiceImpl implements IAsignacionService {

	@Autowired
	private AsignacionRepository operadoresRepository;
	
	@Autowired
	private UtilitiesRepository utilitiesRepository;
	
	/**
	 * getEsquemasPago: Obtiene los esquemas de pago del catálogo
	 * 
	 * @return List<EsquemasPago>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-10-28
	 */
	@Override
	public List<EsquemasPago> getEsquemasPago() {
		
		List<EsquemasPago> lstEsquemasPago = operadoresRepository.getEsquemasPago();
		
		lstEsquemasPago.stream().forEach(i -> {
			String idUsuarioMod = (String) utilitiesRepository.findPersonal("idusuario", "idpersonal", String.valueOf(i.getIdPersonalMod()));
			i.setIdUsuarioMod(idUsuarioMod);
		});
		
		return lstEsquemasPago;
		
	}

	/**
	 * getUnidadesCliente: Obtiene las unidades de un cliente inhouse
	 * 
	 * @param idClienteInhouse (int)
	 * @return List<UnidadOperadorRequest>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-10-28
	 */
	@Override
	public List<UnidadOperadorDTO> getUnidadesCliente(int idClienteInhouse) {
		
		List<Object []> lstUnidades = operadoresRepository.getUnidadesCliente(idClienteInhouse);
		List<UnidadOperadorDTO> lstUnidadesResponse = new ArrayList<>();
		
		lstUnidades.stream().forEach(unidad -> {
			lstUnidadesResponse.add(new UnidadOperadorDTO((int)unidad[0], (int)unidad[1], (int)unidad[2], (String)unidad[3], (String)unidad[4], (String)unidad[5], (String)unidad[6], 
				(int)unidad[7], (String)unidad[8], (String)unidad[9], unidad[18] != null ? ((Time)unidad[18]).toLocalTime() : null, unidad[19] != null ? ((Time)unidad[19]).toLocalTime() : null, // Operador 1
				0, "", "", null, null, // Operador 2
				(int)unidad[10], (String)unidad[11], unidad[12] != null ? ((String)unidad[12]).replaceAll("\r\n", "") : null, 
				unidad[13] != null ? ((String)unidad[13]).replaceAll("\r\n", "") : null, (String)unidad[14], (int)unidad[15], (String)unidad[16], (String)unidad[17], 
				unidad[20] != null ? (int)unidad[20] : 0, unidad[21] != null ? (String)unidad[21] : null, unidad[22] != null ? (int)unidad[22] : 0));
		});
		
		lstUnidadesResponse.stream().filter(unidad -> unidad.getComplementoCliente() != null && unidad.getComplementoCliente().contains(unidad.getAliasInhouse())).collect(Collectors.toList());
		
		return lstUnidadesResponse;
		
	}
	
	/**
	 * filtraOperadoresDisponibles: Obtiene los operadores disponibles para asignar
	 * 
	 * @param nombre (String)
	 * @return List<Operadores>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-10-31
	 */
	@Override
	public List<Operadores> filtraOperadoresDisponibles(String nombre) {
		
		List<Operadores> op = operadoresRepository.filterOperadoresDisponibles(nombre); 
		
		op.stream().forEach(operador -> {
			List<Unidades> o = operadoresRepository.getUnidadesAsignadasByOperador(operador.getIdPersonal(), 2, "");
			
			operador.setUnidadesAsignadas(o != null ? o : new ArrayList<>());
		});
		
		return op;
		
	}
	
	/**
	 * getOperadoresAsignados: Obtiene los operadores asignados a una unidad
	 * 
	 * @param idUnidad (int)
	 * @return List<OperadoresSecundariosRequest>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-11-02
	 */
	@Override
	public List<OperadoresSecundariosDTO> getOperadoresAsignados(int idUnidad) {
		
		List<Object []> lstOperadores = operadoresRepository.getOperadoresAsignados(idUnidad);
		List<OperadoresSecundariosDTO> lstOperadoresResponse = new ArrayList<>();
		
		lstOperadores.stream().forEach(operador -> {
			String idUsuarioMod = (String) utilitiesRepository.findPersonal("idusuario", "idpersonal", String.valueOf((Integer)operador[11]));
			
			lstOperadoresResponse.add(new OperadoresSecundariosDTO((int)operador[12], (int)operador[0], (int)operador[1], (int)operador[2], (String)operador[14], (int)operador[3], (String)operador[4], (int)operador[5], (int)operador[6], 
					((Time)operador[7]).toLocalTime(), ((Time)operador[8]).toLocalTime(), (short)1, ((Date)operador[9]).toLocalDate(), ((Time)operador[10]).toLocalTime(), idUsuarioMod, operador[13] != null ? (int)operador[13] : 0));
		});
		
		return lstOperadoresResponse;
		
	}
	
	/**
	 * asignarOperadores: Asigna los operadores a una unidad
	 * 
	 * @param List<OperadoresSecundariosRequest>
	 * @return List<OperadoresSecundariosRequest>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-11-03
	 */
	@Override
	public List<OperadoresSecundariosDTO> asignarOperadores(List<OperadoresSecundariosDTO> lstOperadoresSecundarios) {
		
		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();
		List<EsquemasPago> lstEsquemasPago = operadoresRepository.getEsquemasPago();
		
		if(lstOperadoresSecundarios != null && !lstOperadoresSecundarios.isEmpty()) {
			int idPersonalMod  = (int) utilitiesRepository.findPersonal("idpersonal", "idusuario", lstOperadoresSecundarios.get(0).getIdUsuarioMod());
			int operadoresTitulares = lstOperadoresSecundarios.stream()
					.filter(operador -> operador.getTipoOperador() == 1)
					.collect(Collectors.toList()).size();
			int operadoresAuxiliares = lstOperadoresSecundarios.stream()
					.filter(operador -> operador.getTipoOperador() == 2)
					.collect(Collectors.toList()).size();
			
			if(!validarOperadoresPorEsquema(1, lstOperadoresSecundarios.get(0).getIdEsquemaPago(), operadoresTitulares)
					|| !validarOperadoresPorEsquema(2, lstOperadoresSecundarios.get(0).getIdEsquemaPago(), operadoresAuxiliares))
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Se excedió la cantidad permitida de operadores para el esquema");
			
			try {
				// Dar de baja todos los operadores asignados a esa unidad
				operadoresRepository.updateOperadoresSecundarios(
						new OperadoresSecundariosUnidad(0, 0, 0, lstOperadoresSecundarios.get(0).getIdOperador(), 0, 0, 0, null, null, (short)0, today, now, idPersonalMod, 0), 
						"idunidad = " + lstOperadoresSecundarios.get(0).getIdUnidad());
				
				Holder<Integer> contadorAuxiliares = new Holder<>(1);
//				Holder<Integer> contadorTitulares = new Holder<>(1);
				
				lstOperadoresSecundarios.stream().forEach(operador -> {
					try {
						// Validar esquema de pago
						EsquemasPago esquemaPago = lstEsquemasPago.stream().filter(x -> x.getIdEsquemaPago() == operador.getIdEsquemaPago()).findFirst().orElse(null);
						if(esquemaPago == null)
							throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se encontró el esquema de pago");
						
						// Dar de baja registros anteriores si el operador tiene unidades asignadas
//						if(operador.getTipoOperador() == 2) {
////							if(esquemaPago.getModificable() != 1)
////								throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "El esquema de pago seleccionado no es modificable");
////							
//						}
							
						List<Unidades> lstUnidades = operadoresRepository.getUnidadesAsignadasByOperador(operador.getIdOperador(), operador.getTipoOperador(), "");
						if(!lstUnidades.isEmpty())
							operadoresRepository.updateOperadoresSecundarios(
									new OperadoresSecundariosUnidad(0, 0, 0, operador.getIdOperador(), 0, 0, 0, null, null, (short)0, today, now, idPersonalMod, 0), "idoperador = " + operador.getIdOperador());
						
						// Insertar operador 
						OperadoresSecundariosUnidad operadorInserted = operadoresRepository.insertOperadoresSecundarios(
								new OperadoresSecundariosUnidad(0, operador.getIdUnidad(), operador.getTipoUnidad(), operador.getIdOperador(), operador.getIdEsquemaPago(), 
									operador.getTipoOperador(), operador.getTipoOperador() == 1 ? operador.getOrdenOperador() : contadorAuxiliares.value++, 
									operador.getHoraEntrada(), operador.getHoraSalida(), (short)1, today, now, idPersonalMod, operador.getIdEsquemaNegociacion()));
						
						if(operadorInserted != null && operadorInserted.getIdOperadoresUnidad() != 0) 
							operador.setIdOperadoresUnidad(operadorInserted.getIdOperadoresUnidad());
					} 
					catch(ResponseStatusException e) {
						throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getReason());
					}
					catch(Exception e) {
						throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo insertar el registro de operadores");
					}
							
				});
			} 
			catch(ResponseStatusException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getReason());
			}
			catch(Exception e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo insertar el registro de operadores");
			}
		}
		
		return lstOperadoresSecundarios;
		
	}
	
	/**
	 * updateOperadores: Actualiza los operadores para asignarlos a una unidad
	 * 
	 * @param List<OperadoresSecundariosRequest>
	 * @return List<OperadoresSecundariosRequest>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-11-03
	 */
	@Override
	public List<OperadoresSecundariosDTO> updateOperadores(List<OperadoresSecundariosDTO> lstOperadoresSecundarios) {
		
		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();
		List<EsquemasPago> lstEsquemasPago = operadoresRepository.getEsquemasPago();
		
		if(lstOperadoresSecundarios != null && !lstOperadoresSecundarios.isEmpty()) {
			lstOperadoresSecundarios.stream().forEach(operador -> {
				try {
					// Validar esquema de pago
					EsquemasPago esquemaPago = lstEsquemasPago.stream().filter(x -> x.getIdEsquemaPago() == operador.getIdEsquemaPago()).findFirst().orElse(null);
					if(esquemaPago == null)
						throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se encontró el esquema de pago");
					
					if(esquemaPago.getModificable() != 1)
						throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "El esquema de pago seleccionado no es modificable");
					
					int idPersonalMod = (int) utilitiesRepository.findPersonal("idpersonal", "idusuario", operador.getIdUsuarioMod());
					
					// Dar de baja registros anteriores si el operador tiene unidades asignadas
					if(operador.getTipoOperador() == 2 && operador.getEstatus() == 1) {
						List<Unidades> lstUnidades = operador.getIdOperadoresUnidad() != null && operador.getIdOperadoresUnidad() != 0 ?
								operadoresRepository.getUnidadesAsignadasByOperador(operador.getIdOperador(), operador.getTipoOperador(), "AND idoperadoresunidad <> " + operador.getIdOperadoresUnidad()) :
								operadoresRepository.getUnidadesAsignadasByOperador(operador.getIdOperador(), operador.getTipoOperador(), "");
						if(!lstUnidades.isEmpty())
							operadoresRepository.updateOperadoresSecundarios(
									new OperadoresSecundariosUnidad(0, 0, 0, operador.getIdOperador(), 0, 0, 0, null, null, (short)0, today, now, idPersonalMod, 0), "idoperador");
					}
					
					// Si sí se envía el id, modificar
					if(operador.getIdOperadoresUnidad() != null && operador.getIdOperadoresUnidad() != 0) 
						operadoresRepository.updateOperadoresSecundarios(
								new OperadoresSecundariosUnidad(operador.getIdOperadoresUnidad(), operador.getIdUnidad(), operador.getTipoUnidad(), operador.getIdOperador(), operador.getIdEsquemaPago(), 
									operador.getTipoOperador(), operador.getOrdenOperador(), operador.getHoraEntrada(), operador.getHoraSalida(), operador.getEstatus(), today, now, idPersonalMod, 0), "idoperadoresunidad");
					
					// Si no se envía el id, insertar
					else {
						OperadoresSecundariosUnidad operadorInserted = operadoresRepository.insertOperadoresSecundarios(
								new OperadoresSecundariosUnidad(0, operador.getIdUnidad(), operador.getTipoUnidad(), operador.getIdOperador(), operador.getIdEsquemaPago(), 
									operador.getTipoOperador(), operador.getOrdenOperador(), operador.getHoraEntrada(), operador.getHoraSalida(), (short)1, today, now, idPersonalMod, operador.getIdEsquemaNegociacion()));
						
						if(operadorInserted != null && operadorInserted.getIdOperadoresUnidad() != 0) 
							operador.setIdOperadoresUnidad(operadorInserted.getIdOperadoresUnidad());
					}
				} 
				catch(ResponseStatusException e) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getReason());
				}
				catch(Exception e) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo actualizar el registro de operadores");
				}
			});
		}
		
		return lstOperadoresSecundarios;
		
	}
	
	public Boolean validarOperadoresPorEsquema(int tipoOperador, int idEsquemaPago, int cantidadRegistrada) {
		int cantidadPermitida;
		switch(idEsquemaPago) {
		case 1: // NÓMINA
			cantidadPermitida = tipoOperador == 1 ? 1 : 2;
			break;
		case 2: // DOBLE OPERADOR
			cantidadPermitida = tipoOperador == 1 ? 2 : 0;
			break;
		case 3: // DOBLE OPERADOR DIFERENTE HORARIO
			cantidadPermitida = tipoOperador == 1 ? 4 : 0;
			break;
		case 4: // POSTURAS
			cantidadPermitida = tipoOperador == 1 ? 1 : 1;
			break;
		case 5: // FLETE FIJO
			cantidadPermitida = tipoOperador == 1 ? 1 : 2;
			break;
		case 6: // PAGO POR VIAJE
			cantidadPermitida = tipoOperador == 1 ? 1 : 2;
			break;
		case 7: // POR COMISIÓN
			cantidadPermitida = tipoOperador == 1 ? 4 : 0;
			break;
		default: 
			cantidadPermitida = 0;
		}
		
		return cantidadRegistrada <= cantidadPermitida;
	}
}
