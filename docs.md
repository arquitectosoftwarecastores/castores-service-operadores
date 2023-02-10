
# DOCUMENTACION DE ENDPOINTS 
~~~
### Este microservicio está ligado al módulo de asignación de operadores del sistema InHouse
~~~
    
## ENDPOINTS PARA ASIGNACIÓN DE OPERADORES
## Path /asignacion

### Servicio para obtener el catálogo de esquemas de pago
	Get(value = "/getEsquemasPago")
	
Response: `[
    {
        "idEsquemaPago": 1,
        "nombre": "Nómina",
        "modificable": 1,
        "estatus": 1,
        "fechaMod": "2022-10-28",
        "horaMod": "11:00:00",
        "idPersonalMod": 45048
    },
    {
        "idEsquemaPago": 2,
        "nombre": "Doble operador",
        "modificable": 0,
        "estatus": 1,
        "fechaMod": "2022-10-28",
        "horaMod": "11:00:00",
        "idPersonalMod": 45048
    },
    {
        "idEsquemaPago": 3,
        "nombre": "Doble operador diferente horario",
        "modificable": 0,
        "estatus": 1,
        "fechaMod": "2022-10-28",
        "horaMod": "11:00:00",
        "idPersonalMod": 45048
    },
    {
        "idEsquemaPago": 4,
        "nombre": "Posturas",
        "modificable": 1,
        "estatus": 1,
        "fechaMod": "2022-10-28",
        "horaMod": "11:00:00",
        "idPersonalMod": 45048
    }
]`


### Servicio para obtener las unidades de renta de un cliente y los operadores titulares asignados
	@Get(value = "/getUnidadesCliente/{idClienteInhouse}")

Response: `[
    {
        "idUnidad": 5616,
        "unidad": 3557,
        "tipoUnidad": 1,
        "noEconomico": "47004",
        "modelo": "2017",
        "placas": "10AC1G",
        "marca": "KENWORTH",
        "idOperador": 9446,
        "idUsuarioOperador": "11073097",
        "nombreOperador": "VELOZ MARTINEZ MIGUEL",
        "horaEntrada": null,
        "horaSalida": null,
        "idOperador2": 0,
        "idUsuarioOperador2": "",
        "nombreOperador2": "",
        "horaEntrada2": null,
        "horaSalida2": null,
        "idCliente": 1042,
        "idOficina": "1501",
        "cliente": "NUEVA WAL MART DE MEXICO",
        "complementoCliente": "WALMART CULIACÁN",
        "observacion": "EN RENTA",
        "idClienteInhouse": 36,
        "aliasInhouse": "WALMART CULIACÁN",
        "rfc": "NWM9709244W0",
        "idEsquemaPago": 0,
        "esquemaPago": null
    }
]`


### Servicio para filtrar los operadores disponibles buscando por nombre o ningún parámetro
	@Get(value = "/filtraOperadoresDisponibles", "/filtraOperadoresDisponibles/{nombre}")

Response: `[
    {
        "idPersonal": 13017,
        "idUsuario": "26130057",
        "nombre": "PUCHETA DE LA CRUZ CESAR AUGUSTO",
        "status": 1,
        "fechaMod": "2022-03-30",
        "horaMod": "16:35:15",
        "unidadesAsignadas": [
            {
                "idUnidad": 7852,
                "unidad": 4935,
                "tipoUnidad": 2,
                "noEconomico": "90360",
                "socio": 12006,
                "modelo": "2020",
                "idMarca": 38,
                "serie": "3BKHKM9X4LF319871",
                "placas": "61AL1J",
                "serieMotor": "74516054",
                "fechaCompra": "2019-06-27",
                "kmts": 0,
                "idOperador": 46473,
                "estatus": 1,
                "idClase": 5,
                "idPersonal": 38154,
                "idOficina": "1100",
                "fechaMod": "2022-04-28",
                "horaMod": "11:28:20"
            }
        ]
    }
]`


### Servicio para obtener los operadores secundarios asignados a una unidad
	@Get(value = {"/getOperadoresAsignados/{idUnidad}"})

Response: `[
    {
        "idOperadoresUnidad": 84,
        "idUnidad": 7852,
        "tipoUnidad": 1,
        "idOperador": 13017,
        "nombreOperador": "PUCHETA DE LA CRUZ CESAR AUGUSTO",
        "idEsquemaPago": 1,
        "esquemaPago": "Nómina",
        "tipoOperador": 2,
        "ordenOperador": 1,
        "horaEntrada": "08:00:00",
        "horaSalida": "16:00:00",
        "estatus": 1,
        "fechaMod": "2023-02-10",
        "horaMod": "11:45:40",
        "idUsuarioMod": "11040531"
    }
]`


### Servicio para hacer la asignación de operadores a una unidad y sus horarios
    @Post(value = "/asignarOperadores")

Request: `[
    {
        "idUnidad": 7852,
        "tipoUnidad": 1,
        "idOperador": 46473,
        "idEsquemaPago": 1,
        "tipoOperador": 1,
        "horaEntrada": "08:00:00",
        "horaSalida": "16:00:00",
        "idUsuarioMod": "11040531"
    },
    {
        "idUnidad": 7852,
        "tipoUnidad": 1,
        "idOperador": 13017,
        "idEsquemaPago": 1,
        "tipoOperador": 2,
        "horaEntrada": "08:00:00",
        "horaSalida": "16:00:00",
        "idUsuarioMod": "11040531"
    }
]`
