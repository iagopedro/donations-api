package com.donation.api.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Profile("render")
public class RenderDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        
        // Obter a URL do ambiente
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            try {
                // Parse da URL original
                URI uri = new URI(databaseUrl);
                
                // Extrair componentes
                String username = uri.getUserInfo().split(":")[0];
                String password = uri.getUserInfo().split(":")[1];
                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 5432; // Usar 5432 como padrão
                String database = uri.getPath().substring(1); // Remover a barra inicial
                
                // Construir URL JDBC correta
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
                
                properties.setUrl(jdbcUrl);
                properties.setUsername(username);
                properties.setPassword(password);
                
                System.out.println("✅ Render Database URL converted successfully:");
                System.out.println("   Original: " + databaseUrl);
                System.out.println("   JDBC URL: " + jdbcUrl);
                System.out.println("   Host: " + host + ":" + port);
                System.out.println("   Database: " + database);
                System.out.println("   Username: " + username);
                
            } catch (URISyntaxException e) {
                System.err.println("❌ Error parsing DATABASE_URL: " + e.getMessage());
                // Fallback para método anterior
                String jdbcUrl = databaseUrl.replace("postgresql://", "jdbc:postgresql://");
                properties.setUrl(jdbcUrl);
            }
        }
        
        properties.setDriverClassName("org.postgresql.Driver");
        
        return properties;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        return dataSourceProperties().initializeDataSourceBuilder().build();
    }
}
