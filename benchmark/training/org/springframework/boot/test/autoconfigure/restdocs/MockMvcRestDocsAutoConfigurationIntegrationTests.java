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


import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Integration tests for {@link RestDocsAutoConfiguration} with Mock MVC.
 *
 * @author Andy Wilkinson
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.example.com", uriPort = 443)
public class MockMvcRestDocsAutoConfigurationIntegrationTests {
    @Autowired
    private MockMvc mvc;

    private File generatedSnippets;

    @Test
    public void defaultSnippetsAreWritten() throws Exception {
        this.mvc.perform(get("/")).andDo(document("default-snippets"));
        File defaultSnippetsDir = new File(this.generatedSnippets, "default-snippets");
        assertThat(defaultSnippetsDir).exists();
        assertThat(contentOf(new File(defaultSnippetsDir, "curl-request.adoc"))).contains("'https://api.example.com/'");
        assertThat(contentOf(new File(defaultSnippetsDir, "http-request.adoc"))).contains("api.example.com");
        assertThat(new File(defaultSnippetsDir, "http-response.adoc")).isFile();
    }
}

