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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;


/**
 * Integration tests for {@link RestDocsAutoConfiguration} with REST Assured.
 *
 * @author Edd? Mel?ndez
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestDocs
public class RestAssuredRestDocsAutoConfigurationIntegrationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private RequestSpecification documentationSpec;

    private File generatedSnippets;

    @Test
    public void defaultSnippetsAreWritten() {
        given(this.documentationSpec).filter(document("default-snippets", preprocessRequest(modifyUris().scheme("https").host("api.example.com").removePort()))).when().port(this.port).get("/").then().assertThat().statusCode(CoreMatchers.is(200));
        File defaultSnippetsDir = new File(this.generatedSnippets, "default-snippets");
        assertThat(defaultSnippetsDir).exists();
        assertThat(contentOf(new File(defaultSnippetsDir, "curl-request.adoc"))).contains("'https://api.example.com/'");
        assertThat(contentOf(new File(defaultSnippetsDir, "http-request.adoc"))).contains("api.example.com");
        assertThat(new File(defaultSnippetsDir, "http-response.adoc")).isFile();
    }
}

