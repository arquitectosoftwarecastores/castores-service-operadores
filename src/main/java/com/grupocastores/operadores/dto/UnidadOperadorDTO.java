package com.grupocastores.operadores.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UnidadOperadorRequest: Clase que construye las unidades y operador de un cliente inHouse.
 * 
 * @version 0.0.1
 * @author Cynthia Fuentes Amaro
 * @date 2022-10-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnidadOperadorDTO {

	private int idUnidad;
	private int unidad;
	private int tipoUnidad;
	private String noEconomico;
	private String modelo;
	private String placas;
	private String marca;
	private int idOperador;
	private String idUsuarioOperador;
	private String nombreOperador;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
	private int idOperador2;
	private String idUsuarioOperador2;
	private String nombreOperador2;
    private LocalTime horaEntrada2;
    private LocalTime horaSalida2;
	private int idCliente;
	private String idOficina;
	private String cliente;
	private String complementoCliente;
	private String observacion;
	private int idClienteInhouse;
	private String aliasInhouse;
	private String rfc;
	private int idEsquemaPago;
	private String esquemaPago;
	private int idEsquemaNegociacion;
	
}
