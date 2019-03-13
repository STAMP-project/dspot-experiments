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
import java.util.Arrays;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.condition.ConditionsReportEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


/**
 * Tests for generating documentation describing {@link ConditionsReportEndpoint}.
 *
 * @author Andy Wilkinson
 */
public class ConditionsReportEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext applicationContext;

    @Test
    public void conditions() throws Exception {
        List<FieldDescriptor> positiveMatchFields = Arrays.asList(fieldWithPath("").description(("Classes and methods with conditions that were " + "matched.")), fieldWithPath(".*.[].condition").description("Name of the condition."), fieldWithPath(".*.[].message").description("Details of why the condition was matched."));
        List<FieldDescriptor> negativeMatchFields = Arrays.asList(fieldWithPath("").description(("Classes and methods with conditions that " + "were not matched.")), fieldWithPath(".*.notMatched").description("Conditions that were matched."), fieldWithPath(".*.notMatched.[].condition").description("Name of the condition."), fieldWithPath(".*.notMatched.[].message").description(("Details of why the condition was" + " not matched.")), fieldWithPath(".*.matched").description("Conditions that were matched."), fieldWithPath(".*.matched.[].condition").description("Name of the condition.").type(STRING).optional(), fieldWithPath(".*.matched.[].message").description("Details of why the condition was matched.").type(STRING).optional());
        FieldDescriptor unconditionalClassesField = fieldWithPath("contexts.*.unconditionalClasses").description("Names of unconditional auto-configuration classes if any.");
        this.mockMvc.perform(get("/actuator/conditions")).andExpect(status().isOk()).andDo(MockMvcRestDocumentation.document("conditions", preprocessResponse(limit("contexts", getApplicationContext().getId(), "positiveMatches"), limit("contexts", getApplicationContext().getId(), "negativeMatches")), responseFields(fieldWithPath("contexts").description("Application contexts keyed by id.")).andWithPrefix("contexts.*.positiveMatches", positiveMatchFields).andWithPrefix("contexts.*.negativeMatches", negativeMatchFields).and(unconditionalClassesField, parentIdField())));
    }

    @Configuration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public ConditionsReportEndpoint autoConfigurationReportEndpoint(ConfigurableApplicationContext context) {
            ConditionEvaluationReport conditionEvaluationReport = ConditionEvaluationReport.get(context.getBeanFactory());
            conditionEvaluationReport.recordEvaluationCandidates(Arrays.asList(PropertyPlaceholderAutoConfiguration.class.getName()));
            return new ConditionsReportEndpoint(context);
        }
    }
}

