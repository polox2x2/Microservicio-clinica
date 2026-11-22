package com.clinica.notificaciones.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

// Crea la base PostgreSQL si no existe
@Slf4j
@Component
public class PostgresDatabaseInitializer implements BeanFactoryPostProcessor, EnvironmentAware, Ordered {

    private static final Pattern SAFE_DB_NAME = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String datasourceUrl = environment.getProperty("spring.datasource.url");
        if (datasourceUrl == null || !datasourceUrl.startsWith("jdbc:postgresql://")) {
            return;
        }

        String username = environment.getProperty("spring.datasource.username", "postgres");
        String password = environment.getProperty("spring.datasource.password", "postgres");

        URI uri = URI.create(datasourceUrl.substring("jdbc:".length()));
        String path = uri.getPath();
        if (path == null || path.length() <= 1) {
            return;
        }

        String targetDatabase = path.substring(1);
        if (!SAFE_DB_NAME.matcher(targetDatabase).matches()) {
            throw new IllegalArgumentException("Nombre de base de datos PostgreSQL inválido: " + targetDatabase);
        }

        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String adminUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + "/postgres";

        try (Connection connection = DriverManager.getConnection(adminUrl, username, password);
             PreparedStatement checkStmt = connection.prepareStatement(
                     "SELECT 1 FROM pg_database WHERE datname = ?")) {
            checkStmt.setString(1, targetDatabase);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    try (Statement createStmt = connection.createStatement()) {
                        createStmt.executeUpdate("CREATE DATABASE \"" + targetDatabase + "\"");
                        log.info("Base de datos PostgreSQL '{}' creada automáticamente.", targetDatabase);
                    }
                } else {
                    log.debug("La base de datos PostgreSQL '{}' ya existe.", targetDatabase);
                }
            }
        } catch (SQLException ex) {
            log.warn("No se pudo verificar o crear la base de datos '{}' en PostgreSQL ({}): {}",
                    targetDatabase, adminUrl, ex.getMessage());
        }
    }
}
