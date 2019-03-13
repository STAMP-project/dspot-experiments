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


import JsonFieldType.STRING;
import JsonFieldType.VARIES;
import java.util.List;
import java.util.Properties;
import org.junit.Test;
import org.springframework.boot.actuate.info.BuildInfoContributor;
import org.springframework.boot.actuate.info.GitInfoContributor;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;


/**
 * Tests for generating documentation describing the {@link InfoEndpoint}.
 *
 * @author Andy Wilkinson
 */
public class InfoEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    @Test
    public void info() throws Exception {
        this.mockMvc.perform(get("/actuator/info")).andExpect(status().isOk()).andDo(MockMvcRestDocumentation.document("info", responseFields(beneathPath("git"), fieldWithPath("branch").description("Name of the Git branch, if any."), fieldWithPath("commit").description("Details of the Git commit, if any."), fieldWithPath("commit.time").description("Timestamp of the commit, if any.").type(VARIES), fieldWithPath("commit.id").description("ID of the commit, if any.")), responseFields(beneathPath("build"), fieldWithPath("artifact").description("Artifact ID of the application, if any.").optional(), fieldWithPath("group").description("Group ID of the application, if any.").optional(), fieldWithPath("name").description("Name of the application, if any.").type(STRING).optional(), fieldWithPath("version").description("Version of the application, if any.").optional(), fieldWithPath("time").description("Timestamp of when the application was built, if any.").type(VARIES).optional())));
    }

    @Configuration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public InfoEndpoint endpoint(List<InfoContributor> infoContributors) {
            return new InfoEndpoint(infoContributors);
        }

        @Bean
        public GitInfoContributor gitInfoContributor() {
            Properties properties = new Properties();
            properties.put("branch", "master");
            properties.put("commit.id", "df027cf1ec5aeba2d4fedd7b8c42b88dc5ce38e5");
            properties.put("commit.id.abbrev", "df027cf");
            properties.put("commit.time", Long.toString(System.currentTimeMillis()));
            GitProperties gitProperties = new GitProperties(properties);
            return new GitInfoContributor(gitProperties);
        }

        @Bean
        public BuildInfoContributor buildInfoContributor() {
            Properties properties = new Properties();
            properties.put("group", "com.example");
            properties.put("artifact", "application");
            properties.put("version", "1.0.3");
            BuildProperties buildProperties = new BuildProperties(properties);
            return new BuildInfoContributor(buildProperties);
        }
    }
}

