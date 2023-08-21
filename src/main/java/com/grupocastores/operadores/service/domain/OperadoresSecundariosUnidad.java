package com.grupocastores.operadores.service.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OperadoresSecundariosUnidad: Entidad para la tabla bitacorasinhouse.operadores_secundarios_unidad
 * 
 * @version 0.0.1
 * @author Cynthia Fuentes Amaro
 * @date 2022-10-27
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "bitacorasinhouse.operadores_secundarios_unidad_op")
@IdClass(OperadoresSecundariosUnidad.OperadoresSecundariosUnidadId.class)
public class OperadoresSecundariosUnidad implements Serializable {

	private static final long serialVersionUID = 378624882465672096L;

	public static class OperadoresSecundariosUnidadId implements Serializable {

		private static final long serialVersionUID = 7014006197907626199L;

		public OperadoresSecundariosUnidadId() {
    		super();
    	}

		int idOperadoresUnidad;
    }
	
    @Id
    @Column(name = "idoperadoresunidad", nullable = false, precision = 11)
    private Integer idOperadoresUnidad;
    
    @Column(name = "idunidad", nullable = false, precision = 11)
    private Integer idUnidad;
    
    @Column(name = "tipounidad", precision = 11)
    private Integer tipoUnidad;

    @Column(name = "idoperador", precision = 11)
    private Integer idOperador;
    
    @Column(name = "idesquemapago", precision = 11)
    private Integer idEsquemaPago;
    
    @Column(name = "tipooperador", precision = 11)
    private Integer tipoOperador;
    
    @Column(name = "ordenoperador", precision = 11)
    private Integer ordenOperador;
        
    @Column(name = "horaentrada")
    private LocalTime horaEntrada;

    @Column(name = "horasalida")
    private LocalTime horaSalida;
    
    @Column()
    private short estatus;
    
    @Column(name = "fechamod")
    private LocalDate fechaMod;
    
    @Column(name = "horamod")
    private LocalTime horaMod;
    
    @Column(name = "idpersonalmod", precision = 11)
    private int idPersonalMod;
    
    @Column(name = "idesquemanegociacion", precision = 11)
    private Integer idEsquemaNegociacion;
    
}
