package com.programacion2.config;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Crea UNA sola vez el PersistenceManagerFactory (es costoso de construir)
 * y lo ofrece a Spring para que lo inyecte en los repositorios.
 * Los datos de conexion vienen de application.properties.
 */
@Configuration
public class PersistenciaConfig {

    @Bean(destroyMethod = "close")
    public PersistenceManagerFactory persistenceManagerFactory(
            @Value("${taller.db.url}") String url,
            @Value("${taller.db.user}") String usuario,
            @Value("${taller.db.password}") String password) {

        Map<String, Object> propiedades = new HashMap<>();
        propiedades.put("javax.jdo.option.ConnectionDriverName",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        propiedades.put("javax.jdo.option.ConnectionURL", url);
        propiedades.put("javax.jdo.option.ConnectionUserName", usuario);
        propiedades.put("javax.jdo.option.ConnectionPassword", password);

        // Permite que DataNucleus cree las tablas y relaciones al iniciar
        // cuando todavía no existen en la base de datos.
        propiedades.put("datanucleus.schema.autoCreateAll", "true");

        return JDOHelper.getPersistenceManagerFactory(propiedades, "Programacion2");
    }
}
