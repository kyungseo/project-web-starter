/* ============================================================================
 * [ Development Templates based on Spring Boot ]
 * ----------------------------------------------------------------------------
 * Copyright 2023 Kyungseo Park <Kyungseo.Park@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================================
 * Author     Date            Description
 * --------   ----------      -------------------------------------------------
 * Kyungseo   2023-03-02      initial version
 * ========================================================================= */

package kyungseo.poc.simple.web.appcore.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
    Info info = new Info()
          .title(EnvironmentAwareConfig.getProperty("springdoc.my.title"))
          .description(EnvironmentAwareConfig.getProperty("springdoc.my.description"))
          .version(EnvironmentAwareConfig.getProperty("springdoc.my.version"))
          //.termsOfService(EnvironmentAwareConfig.getProperty("springdoc.my.terms"))
          .contact(new Contact()
                  .name(EnvironmentAwareConfig.getProperty("springdoc.my.contack.name"))
                  .url(EnvironmentAwareConfig.getProperty("springdoc.my.contack.url"))
                  .email(EnvironmentAwareConfig.getProperty("springdoc.my.contack.email")))
          .license(new License()
                  .name(EnvironmentAwareConfig.getProperty("springdoc.my.license.name"))
                  .url(EnvironmentAwareConfig.getProperty("springdoc.my.license.url")));

      return new OpenAPI()
          .components(new Components())
          .info(info);
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("v1-definition")
                .pathsToMatch("/api/**")
                .build();
    }

}