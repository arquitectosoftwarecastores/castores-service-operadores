package com.grupocastores.operadores.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Repository;

import com.grupocastores.commons.oficinas.Servidores;

/**
 * UtilitiesRepository: Repositorio base para para usar con servidores vinculados.
 * 
 * @version 0.0.1
 * @author Moises Lopez Arrona [moisesarrona]
 * @date 2022-06-14
 */
@Repository
public class UtilitiesRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    public static String DB_23 = "PRODUCCION23";
   
    public static String DB_13 = "PRODUCCION13";
    
    public static final String queryGetLinkedServerByIdOficina = 
            "SELECT * FROM syn.dbo.v_Oficinas where Oficina = \'%s\'";
    
    static final String queryFindPersonal =
			"SELECT * FROM OPENQUERY(" + DB_13 + ", 'SELECT %s FROM personal.personal WHERE %s = %s;')";
    
    /**
     * executeStoredProcedure: Ejecuta un procedimiento almacenado para Guardar, Editar
     * 
     * @param query (String) consulta a ejecutar
     * @return Boolean
     * @author Moises Lopez Arrona [moisesarrona]
     * @date 2022-06-14
     */
    public Boolean executeStoredProcedure(String query) {
        int resp = 0;
        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("PcrExecSQL");
        storedProcedure.registerStoredProcedureParameter("sql", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("respuesta", String.class, ParameterMode.OUT);
        storedProcedure.setParameter("sql", query);
        storedProcedure.execute();
        resp = Integer.valueOf((String) storedProcedure.getOutputParameterValue("respuesta"));
        return resp > 0 ? true : false;
    }
    
    /**
     * getLinkedServerByOfice: Obtiene el servidor vinculado por id de oficina
     * 
     * @param idOficina (String) consulta a ejecutar
     * @return Boolean
     * @author Oscar Eduardo Guerra Salcedo [OscarGuerra]
     * @date 2022-09-22
     */
    public Servidores getLinkedServerByOfice(String idOficina) {
        Query query = entityManager.createNativeQuery(String.format(queryGetLinkedServerByIdOficina,
                idOficina),Servidores.class
            );
        return (Servidores) query.getResultList().get(0);
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
    
}
