package com.qbe.cloudstarter.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

  private final OpenApiConfig openApiConfig = new OpenApiConfig();

  @Test
  @DisplayName("Should create OpenAPI bean with expected information")
  void shouldCreateCustomOpenApi() {
    OpenAPI openAPI = openApiConfig.customOpenAPI();
    assertNotNull(openAPI);
    assertNotNull(openAPI.getInfo());

    Info info = openAPI.getInfo();
    assertEquals("Cloud Starter API", info.getTitle());
    assertEquals("1.0.0", info.getVersion());
    assertEquals("Spring Boot CRUD API - DevOps Demo", info.getDescription());
  }
}
