package com.grupocastores.operadores.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OperadoresSecundariosRequest: Clase que construye la petición para la asignación de operadores
 * 
 * @version 0.0.1
 * @author Cynthia Fuentes Amaro
 * @date 2022-11-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperadoresSecundariosDTO {
	
	private Integer idOperadoresUnidad;
	private int idUnidad;
	private int tipoUnidad;
	private int idOperador;
	private String nombreOperador;
    private int idEsquemaPago;
    private String esquemaPago;
    private int tipoOperador;
    private int ordenOperador;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private short estatus;
    private LocalDate fechaMod;
    private LocalTime horaMod;
    private String idUsuarioMod;
    private Integer idEsquemaNegociacion;
    
}
