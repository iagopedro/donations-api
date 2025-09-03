package com.donation.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API de Gerenciamento de Doações Locais",
        description = "Esta API permite o gerenciamento completo de doações locais, incluindo cadastro de usuários, " +
                     "criação de doações, solicitações e matches entre doadores e beneficiários.",
        version = "v1.0.0",
        contact = @Contact(
            name = "Equipe de Desenvolvimento",
            email = "contato@doacoes.com.br",
            url = "https://www.doacoes.com.br"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Servidor de Desenvolvimento"
        ),
        @Server(
            url = "https://api.doacoes.com.br",
            description = "Servidor de Produção"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "Token JWT obtido através do endpoint de login (/api/auth/login). " +
                 "Adicione 'Bearer ' antes do token no cabeçalho Authorization."
)
public class OpenApiConfig {
}
