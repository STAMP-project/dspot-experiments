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
package org.springframework.boot.actuate.autoconfigure.endpoint.web.documentation;


import java.io.FileWriter;
import java.util.Map;
import org.junit.Test;
import org.springframework.boot.actuate.management.HeapDumpWebEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.cli.CurlRequestSnippet;
import org.springframework.restdocs.operation.Operation;
import org.springframework.util.FileCopyUtils;


/**
 * Tests for generating documentation describing the {@link HeapDumpWebEndpoint}.
 *
 * @author Andy Wilkinson
 */
public class HeapDumpWebEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    @Test
    public void heapDump() throws Exception {
        this.mockMvc.perform(get("/actuator/heapdump")).andExpect(status().isOk()).andDo(document("heapdump", new CurlRequestSnippet(CliDocumentation.multiLineFormat()) {
            @Override
            protected Map<String, Object> createModel(Operation operation) {
                Map<String, Object> model = super.createModel(operation);
                model.put("options", "-O");
                return model;
            }
        }));
    }

    @Configuration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public HeapDumpWebEndpoint endpoint() {
            return new HeapDumpWebEndpoint() {
                @Override
                protected HeapDumper createHeapDumper() throws HeapDumperUnavailableException {
                    return ( file, live) -> FileCopyUtils.copy("<<binary content>>", new FileWriter(file));
                }
            };
        }
    }
}

