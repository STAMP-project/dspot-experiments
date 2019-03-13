/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.test.autoconfigure.restdocs;


import io.restassured.specification.RequestSpecification;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;


/**
 * Integration tests for advanced configuration of {@link AutoConfigureRestDocs} with REST
 * Assured.
 *
 * @author Edd? Mel?ndez
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestDocs
public class RestAssuredRestDocsAutoConfigurationAdvancedConfigurationIntegrationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private RequestSpecification documentationSpec;

    private File generatedSnippets;

    @Test
    public void snippetGeneration() {
        given(this.documentationSpec).filter(document("default-snippets", preprocessRequest(modifyUris().scheme("https").host("api.example.com").removePort()))).when().port(this.port).get("/").then().assertThat().statusCode(CoreMatchers.is(200));
        File defaultSnippetsDir = new File(this.generatedSnippets, "default-snippets");
        assertThat(defaultSnippetsDir).exists();
        assertThat(contentOf(new File(defaultSnippetsDir, "curl-request.md"))).contains("'https://api.example.com/'");
        assertThat(contentOf(new File(defaultSnippetsDir, "http-request.md"))).contains("api.example.com");
        assertThat(new File(defaultSnippetsDir, "http-response.md")).isFile();
        assertThat(new File(defaultSnippetsDir, "response-fields.md")).isFile();
    }

    @TestConfiguration
    public static class CustomizationConfiguration {
        @Bean
        public RestDocumentationResultHandler restDocumentation() {
            return MockMvcRestDocumentation.document("{method-name}");
        }

        @Bean
        public RestDocsRestAssuredConfigurationCustomizer templateFormatCustomizer() {
            return ( configurer) -> configurer.snippets().withTemplateFormat(TemplateFormats.markdown());
        }

        @Bean
        public RestDocsRestAssuredConfigurationCustomizer defaultSnippetsCustomizer() {
            return ( configurer) -> configurer.snippets().withAdditionalDefaults(responseFields(fieldWithPath("_links.self").description("Main URL")));
        }
    }
}

