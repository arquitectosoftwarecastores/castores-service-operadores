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
import com.grupocastores.commons.inhouse.OperadoresSecundariosRequest;
import com.grupocastores.commons.inhouse.OperadoresSecundariosUnidad;
import com.grupocastores.commons.inhouse.Unidades;
import com.grupocastores.commons.inhouse.UnidadOperadorRequest;
import com.grupocastores.operadores.repository.CatalogoRepository;
import com.grupocastores.operadores.repository.AsignacionRepository;

/**
 * OperadoresServiceImpl: Servicio para la asignación de operadores.
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
	private CatalogoRepository catalogoRepository;
	
	/**
	 * getEsquemasPago: Obtiene los esquemas de pago del catálogo
	 * 
	 * @return List<EsquemasPago>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-10-28
	 */
	@Override
	public List<EsquemasPago> getEsquemasPago() {
		
		List<EsquemasPago> lista = operadoresRepository.getEsquemasPago();
		
		if(lista != null && !lista.isEmpty()) {
			lista.stream().forEach(i -> {
				String idUsuarioMod = (String) catalogoRepository.findPersonal("idusuario", "idpersonal", String.valueOf(i.getIdPersonalMod()));
				i.setIdUsuarioMod(idUsuarioMod);
			});
		}
		return lista;
		
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
	public List<UnidadOperadorRequest> getUnidadesCliente(int idClienteInhouse) {
		
		List<Object []> lista = operadoresRepository.getUnidadesCliente(idClienteInhouse);
		List<UnidadOperadorRequest> unidades = new ArrayList<>();
		
		if(lista != null ) {
			lista.stream().forEach(i -> {
				unidades.add(new UnidadOperadorRequest((int)i[0], (int)i[1], (int)i[2], (String)i[3], (String)i[4], (String)i[5], (String)i[6], 
					(int)i[7], (String)i[8], (String)i[9], i[18] != null ? ((Time)i[18]).toLocalTime() : null, i[19] != null ? ((Time)i[19]).toLocalTime() : null, // Operador 1
					0, "", "", null, null, // Operador 2
					(int)i[10], (String)i[11], i[12] != null ? ((String)i[12]).replaceAll("\r\n", "") : null, 
					i[13] != null ? ((String)i[13]).replaceAll("\r\n", "") : null, (String)i[14], (int)i[15], (String)i[16], (String)i[17], 
					i[20] != null ? (int)i[20] : 0, i[21] != null ? (String)i[21] : null));
			});
			
			 unidades.stream().filter(x -> x.getComplementoCliente() != null && x.getComplementoCliente().contains(x.getAliasInhouse())).collect(Collectors.toList());
		}
		
		return unidades;
		
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
		
		op.stream().forEach(i -> {
			List<Unidades> o = operadoresRepository.getUnidadesAsignadasByOperador(i.getIdPersonal(), 2, "");
			
			i.setUnidadesAsignadas(o != null ? o : new ArrayList<>());
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
	public List<OperadoresSecundariosRequest> getOperadoresAsignados(int idUnidad) {
		
		List<Object []> lista = operadoresRepository.getOperadoresAsignados(idUnidad);
		List<OperadoresSecundariosRequest> operadores = new ArrayList<>();
		
		if(lista != null ) {
			lista.stream().forEach(i -> {
				String idUsuarioMod = (String) catalogoRepository.findPersonal("idusuario", "idpersonal", String.valueOf((Integer)i[11]));
				
				operadores.add(new OperadoresSecundariosRequest((int)i[12], (int)i[0], (int)i[1], (int)i[2], (String)i[13], (int)i[3], (String)i[4], (int)i[5], (int)i[6], 
					((Time)i[7]).toLocalTime(), ((Time)i[8]).toLocalTime(), (short)1, ((Date)i[9]).toLocalDate(), ((Time)i[10]).toLocalTime(), idUsuarioMod));
			});
		}
		
		return operadores;
		
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
	public List<OperadoresSecundariosRequest> asignarOperadores(List<OperadoresSecundariosRequest> operadoresSecundarios) {
		
		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();
		List<EsquemasPago> esquemasPago = operadoresRepository.getEsquemasPago();
		
		if(operadoresSecundarios != null && !operadoresSecundarios.isEmpty()) {
			int idPersonalMod  = (int) catalogoRepository.findPersonal("idpersonal", "idusuario", operadoresSecundarios.get(0).getIdUsuarioMod());

			try {
				// Dar de baja todos los operadores asignados a esa unidad
				operadoresRepository.updateOperadoresSecundarios(
						new OperadoresSecundariosUnidad(0, 0, 0, operadoresSecundarios.get(0).getIdOperador(), 0, 0, 0, null, null, (short)0, today, now, idPersonalMod), 
						"idunidad = " + operadoresSecundarios.get(0).getIdUnidad());
				
				Holder<Integer> contadorSecundarios = new Holder<>(1);
				Holder<Integer> contadorTitulares = new Holder<>(1);
				
				operadoresSecundarios.stream().forEach(i -> {
					try {
						// Validar esquema de pago
						EsquemasPago ep = esquemasPago.stream().filter(x -> x.getIdEsquemaPago() == i.getIdEsquemaPago()).findFirst().orElse(null);
						if(ep == null)
							throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se encontró el esquema de pago");
						
						// Dar de baja registros anteriores si el operador tiene unidades asignadas
						if(i.getTipoOperador() == 2) {
							if(ep.getModificable() != 1)
								throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "El esquema de pago seleccionado no es modificable");
							
							List<Unidades> o = operadoresRepository.getUnidadesAsignadasByOperador(i.getIdOperador(), i.getTipoOperador(), "");
							if(o != null && !o.isEmpty())
								operadoresRepository.updateOperadoresSecundarios(
										new OperadoresSecundariosUnidad(0, 0, 0, i.getIdOperador(), 0, 0, 0, null, null, (short)0, today, now, idPersonalMod), "idoperador = " + i.getIdOperador());
						}
							
						// Insertar operador 
						OperadoresSecundariosUnidad operador = operadoresRepository.insertOperadoresSecundarios(
								new OperadoresSecundariosUnidad(0, i.getIdUnidad(), i.getTipoUnidad(), i.getIdOperador(), i.getIdEsquemaPago(), 
									i.getTipoOperador(), i.getTipoOperador() == 1 ? contadorTitulares.value++ : contadorSecundarios.value++, i.getHoraEntrada(), i.getHoraSalida(), (short)1, today, now, idPersonalMod));
						
						if(operador != null && operador.getIdOperadoresUnidad() != 0) 
							i.setIdOperadoresUnidad(operador.getIdOperadoresUnidad());
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
		
		return operadoresSecundarios;
		
	}
	/*@Override
	public List<OperadoresSecundariosRequest> insertOperadores(List<OperadoresSecundariosRequest> operadoresSecundarios) {
		
		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();
		List<EsquemasPago> esquemasPago = operadoresRepository.getEsquemasPago();
		
		if(operadoresSecundarios != null && !operadoresSecundarios.isEmpty()) {
			operadoresSecundarios.stream().forEach(i -> {
				try {
					// Validar esquema de pago
					EsquemasPago ep = esquemasPago.stream().filter(x -> x.getIdEsquemaPago() == i.getIdEsquemaPago()).findFirst().orElse(null);
					if(ep == null)
						throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se encontró el esquema de pago");
					
					if(ep.getModificable() != 1)
						throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "El esquema de pago seleccionado no es modificable");
					
					int idPersonalMod = (int) catalogoRepository.findPersonal("idpersonal", "idusuario", i.getIdUsuarioMod());
					
					// Dar de baja registros anteriores si el operador tiene unidades asignadas
					if(i.getTipoOperador() == 2) {
						List<Unidades> o = operadoresRepository.getUnidadesAsignadasByOperador(i.getIdOperador(), i.getTipoOperador(), "");
						if(o != null && !o.isEmpty())
							operadoresRepository.updateOperadoresSecundarios(
									new OperadoresSecundariosUnidad(0, 0, 0, i.getIdOperador(), 0, 0, 0, null, null, (short)0, today, now, idPersonalMod), "idoperador");
					}
						
					// Insertar operador 
					OperadoresSecundariosUnidad operador = operadoresRepository.insertOperadoresSecundarios(
							new OperadoresSecundariosUnidad(0, i.getIdUnidad(), i.getTipoUnidad(), i.getIdOperador(), i.getIdEsquemaPago(), 
								i.getTipoOperador(), i.getOrdenOperador(), i.getHoraEntrada(), i.getHoraSalida(), (short)1, today, now, idPersonalMod));
					
					if(operador != null && operador.getIdOperadoresUnidad() != 0) 
						i.setIdOperadoresUnidad(operador.getIdOperadoresUnidad());
					
				} 
				catch(ResponseStatusException e) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getReason());
				}
				catch(Exception e) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo insertar el registro de operadores");
				}
			});
		}
		
		return operadoresSecundarios;
		
	}*/
	
	/**
	 * updateOperadores: Actualiza los operadores para asignarlos a una unidad
	 * 
	 * @param List<OperadoresSecundariosRequest>
	 * @return List<OperadoresSecundariosRequest>
	 * @author Cynthia Fuentes Amaro
	 * @date 2022-11-03
	 */
	@Override
	public List<OperadoresSecundariosRequest> updateOperadores(List<OperadoresSecundariosRequest> operadoresSecundarios) {
		
		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();
		List<EsquemasPago> esquemasPago = operadoresRepository.getEsquemasPago();
		
		if(operadoresSecundarios != null && !operadoresSecundarios.isEmpty()) {
			operadoresSecundarios.stream().forEach(i -> {
				try {
					// Validar esquema de pago
					EsquemasPago ep = esquemasPago.stream().filter(x -> x.getIdEsquemaPago() == i.getIdEsquemaPago()).findFirst().orElse(null);
					if(ep == null)
						throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se encontró el esquema de pago");
					
					if(ep.getModificable() != 1)
						throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "El esquema de pago seleccionado no es modificable");
					
					int idPersonalMod = (int) catalogoRepository.findPersonal("idpersonal", "idusuario", i.getIdUsuarioMod());
					
					// Dar de baja registros anteriores si el operador tiene unidades asignadas
					if(i.getTipoOperador() == 2 && i.getEstatus() == 1) {
						List<Unidades> o = i.getIdOperadoresUnidad() != null && i.getIdOperadoresUnidad() != 0 ?
								operadoresRepository.getUnidadesAsignadasByOperador(i.getIdOperador(), i.getTipoOperador(), "AND idoperadoresunidad <> " + i.getIdOperadoresUnidad()) :
								operadoresRepository.getUnidadesAsignadasByOperador(i.getIdOperador(), i.getTipoOperador(), "");
						if(o != null && !o.isEmpty())
							operadoresRepository.updateOperadoresSecundarios(
									new OperadoresSecundariosUnidad(0, 0, 0, i.getIdOperador(), 0, 0, 0, null, null, (short)0, today, now, idPersonalMod), "idoperador");
					}
					
					// Si sí se envía el id, modificar
					if(i.getIdOperadoresUnidad() != null && i.getIdOperadoresUnidad() != 0) 
						operadoresRepository.updateOperadoresSecundarios(
								new OperadoresSecundariosUnidad(i.getIdOperadoresUnidad(), i.getIdUnidad(), i.getTipoUnidad(), i.getIdOperador(), i.getIdEsquemaPago(), 
									i.getTipoOperador(), i.getOrdenOperador(), i.getHoraEntrada(), i.getHoraSalida(), i.getEstatus(), today, now, idPersonalMod), "idoperadoresunidad");
					
					// Si no se envía el id, insertar
					else {
						OperadoresSecundariosUnidad operador = operadoresRepository.insertOperadoresSecundarios(
								new OperadoresSecundariosUnidad(0, i.getIdUnidad(), i.getTipoUnidad(), i.getIdOperador(), i.getIdEsquemaPago(), 
									i.getTipoOperador(), i.getOrdenOperador(), i.getHoraEntrada(), i.getHoraSalida(), (short)1, today, now, idPersonalMod));
						
						if(operador != null && operador.getIdOperadoresUnidad() != 0) 
							i.setIdOperadoresUnidad(operador.getIdOperadoresUnidad());
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
		
		return operadoresSecundarios;
		
	}
}
