package com.grupocastores.operadores.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupocastores.commons.inhouse.EsquemasPago;
import com.grupocastores.commons.inhouse.Operadores;
import com.grupocastores.commons.inhouse.OperadoresSecundariosRequest;
import com.grupocastores.commons.inhouse.UnidadOperadorRequest;
import com.grupocastores.operadores.service.IAsignacionService;

@RestController
@RequestMapping(value = "/asignacion")
public class AsignacionController {
	
	@Autowired
	private IAsignacionService operadoresService;
	
	@GetMapping(value = "/getEsquemasPago")
	public ResponseEntity<List<EsquemasPago>> getEsquemasPago() {
		List<EsquemasPago> list = operadoresService.getEsquemasPago();
		if (list == null || list.isEmpty())
			return ResponseEntity.noContent().build();
		return ResponseEntity.ok(list);
	}
	
	@GetMapping(value = "/getUnidadesCliente/{idClienteInhouse}")
	public ResponseEntity<List<UnidadOperadorRequest>> getUnidadesCliente(@PathVariable("idClienteInhouse") int idClienteInhouse) {
		List<UnidadOperadorRequest> list = operadoresService.getUnidadesCliente(idClienteInhouse);
		if (list == null || list.isEmpty())
			return ResponseEntity.noContent().build();
		return ResponseEntity.ok(list);
	}
	
	@GetMapping(value = {"/filtraOperadoresDisponibles", "/filtraOperadoresDisponibles/{nombre}"})
	public ResponseEntity<List<Operadores>> filtraOperadoresDisponibles(@PathVariable("nombre") Optional<String> nombreOp) {
		String nombre = nombreOp.orElse("");
		List<Operadores> list = operadoresService.filtraOperadoresDisponibles(nombre);
		if (list == null || list.isEmpty())
			return ResponseEntity.noContent().build();
		return ResponseEntity.ok(list);
	}
	
	@GetMapping(value = {"/getOperadoresAsignados/{idUnidad}"})
	public ResponseEntity<List<OperadoresSecundariosRequest>> getOperadoresAsignados(@PathVariable("idUnidad") int idUnidad) {
		List<OperadoresSecundariosRequest> list = operadoresService.getOperadoresAsignados(idUnidad);
		if (list == null || list.isEmpty())
			return ResponseEntity.noContent().build();
		return ResponseEntity.ok(list);
	}
	
	@PostMapping(value = "/asignarOperadores")
	public ResponseEntity<List<OperadoresSecundariosRequest>> asignarOperadores(@RequestBody List<OperadoresSecundariosRequest> operadoresSecundarios) {
		List<OperadoresSecundariosRequest> list = operadoresService.asignarOperadores(operadoresSecundarios);
		return ResponseEntity.ok(list);
	}
	
	@PutMapping(value = "/updateOperadores")
	public ResponseEntity<List<OperadoresSecundariosRequest>> updateOperadores(@RequestBody List<OperadoresSecundariosRequest> operadoresSecundarios) {
		List<OperadoresSecundariosRequest> list = operadoresService.updateOperadores(operadoresSecundarios);
		return ResponseEntity.ok(list);
	}

}
