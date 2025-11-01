package net.ab79.juntos.juntosapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI juntosApi() {
    final String securitySchemeName = "bearerAuth";

    return new OpenAPI()
        .info(
            new Info()
                .title("JuntosApp API")
                .version("1.0.0")
                .description(
                    "API RESTful do sistema JuntosApp, com autenticação JWT e controle de acesso por papéis.")
                .contact(
                    new Contact()
                        .name("Equipe JuntosApp")
                        .email("devs@juntosapp.com.br")
                        .url("https://adambravo79.ddns.net")))
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
        .components(
            new Components()
                .addSecuritySchemes(
                    securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .servers(
            List.of(
                new Server().url("http://localhost:8080").description("Ambiente Local"),
                new Server().url("https://adambravo79.ddns.net").description("Ambiente Produção")));
  }
}
