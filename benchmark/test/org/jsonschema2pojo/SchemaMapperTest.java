/**
 * Copyright ? 2010-2017 Nokia
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
package org.jsonschema2pojo;


import SourceType.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.rules.SchemaRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SchemaMapperTest {
    @Test
    public void generateReadsSchemaAsObject() throws IOException {
        final SchemaRule mockSchemaRule = Mockito.mock(SchemaRule.class);
        final RuleFactory mockRuleFactory = Mockito.mock(RuleFactory.class);
        Mockito.when(mockRuleFactory.getSchemaRule()).thenReturn(mockSchemaRule);
        Mockito.when(mockRuleFactory.getGenerationConfig()).thenReturn(new DefaultGenerationConfig());
        URL schemaContent = this.getClass().getResource("/schema/address.json");
        new SchemaMapper(mockRuleFactory, new SchemaGenerator()).generate(new JCodeModel(), "Address", "com.example.package", schemaContent);
        ArgumentCaptor<JPackage> capturePackage = ArgumentCaptor.forClass(JPackage.class);
        ArgumentCaptor<JsonNode> captureNode = ArgumentCaptor.forClass(JsonNode.class);
        Mockito.verify(mockSchemaRule).apply(ArgumentMatchers.eq("Address"), captureNode.capture(), ArgumentMatchers.eq(null), capturePackage.capture(), Mockito.isA(Schema.class));
        MatcherAssert.assertThat(capturePackage.getValue().name(), is("com.example.package"));
        MatcherAssert.assertThat(captureNode.getValue(), is(notNullValue()));
    }

    @Test
    public void generateCreatesSchemaFromExampleJsonWhenInJsonMode() throws IOException {
        URL schemaContent = this.getClass().getResource("/schema/address.json");
        ObjectNode schemaNode = JsonNodeFactory.instance.objectNode();
        final SchemaRule mockSchemaRule = Mockito.mock(SchemaRule.class);
        final GenerationConfig mockGenerationConfig = Mockito.mock(GenerationConfig.class);
        Mockito.when(mockGenerationConfig.getSourceType()).thenReturn(JSON);
        final SchemaGenerator mockSchemaGenerator = Mockito.mock(SchemaGenerator.class);
        Mockito.when(mockSchemaGenerator.schemaFromExample(schemaContent)).thenReturn(schemaNode);
        final RuleFactory mockRuleFactory = Mockito.mock(RuleFactory.class);
        Mockito.when(mockRuleFactory.getSchemaRule()).thenReturn(mockSchemaRule);
        Mockito.when(mockRuleFactory.getGenerationConfig()).thenReturn(mockGenerationConfig);
        new SchemaMapper(mockRuleFactory, mockSchemaGenerator).generate(new JCodeModel(), "Address", "com.example.package", schemaContent);
        ArgumentCaptor<JPackage> capturePackage = ArgumentCaptor.forClass(JPackage.class);
        Mockito.verify(mockSchemaRule).apply(ArgumentMatchers.eq("Address"), ArgumentMatchers.eq(schemaNode), ArgumentMatchers.eq(null), capturePackage.capture(), Mockito.isA(Schema.class));
        MatcherAssert.assertThat(capturePackage.getValue().name(), is("com.example.package"));
    }

    @Test
    public void generateCreatesSchemaFromExampleJSONAsStringInput() throws IOException {
        String jsonContent = IOUtils.toString(this.getClass().getResourceAsStream("/example-json/user.json"));
        ObjectNode schemaNode = JsonNodeFactory.instance.objectNode();
        final SchemaRule mockSchemaRule = Mockito.mock(SchemaRule.class);
        final GenerationConfig mockGenerationConfig = Mockito.mock(GenerationConfig.class);
        Mockito.when(mockGenerationConfig.getSourceType()).thenReturn(JSON);
        final SchemaGenerator mockSchemaGenerator = Mockito.mock(SchemaGenerator.class);
        Mockito.when(mockSchemaGenerator.schemaFromExample(new ObjectMapper().readTree(jsonContent))).thenReturn(schemaNode);
        final RuleFactory mockRuleFactory = Mockito.mock(RuleFactory.class);
        Mockito.when(mockRuleFactory.getSchemaRule()).thenReturn(mockSchemaRule);
        Mockito.when(mockRuleFactory.getGenerationConfig()).thenReturn(mockGenerationConfig);
        new SchemaMapper(mockRuleFactory, mockSchemaGenerator).generate(new JCodeModel(), "User", "com.example.package", jsonContent);
        ArgumentCaptor<JPackage> capturePackage = ArgumentCaptor.forClass(JPackage.class);
        Mockito.verify(mockSchemaRule).apply(ArgumentMatchers.eq("User"), ArgumentMatchers.eq(schemaNode), ArgumentMatchers.eq(null), capturePackage.capture(), Mockito.isA(Schema.class));
        MatcherAssert.assertThat(capturePackage.getValue().name(), is("com.example.package"));
    }

    @Test
    public void generateCreatesSchemaFromSchemaAsStringInput() throws IOException {
        String schemaContent = IOUtils.toString(this.getClass().getResourceAsStream("/schema/address.json"));
        final SchemaRule mockSchemaRule = Mockito.mock(SchemaRule.class);
        final RuleFactory mockRuleFactory = Mockito.mock(RuleFactory.class);
        Mockito.when(mockRuleFactory.getSchemaRule()).thenReturn(mockSchemaRule);
        Mockito.when(mockRuleFactory.getGenerationConfig()).thenReturn(new DefaultGenerationConfig());
        new SchemaMapper(mockRuleFactory, new SchemaGenerator()).generate(new JCodeModel(), "Address", "com.example.package", schemaContent);
        ArgumentCaptor<JPackage> capturePackage = ArgumentCaptor.forClass(JPackage.class);
        ArgumentCaptor<JsonNode> captureNode = ArgumentCaptor.forClass(JsonNode.class);
        Mockito.verify(mockSchemaRule).apply(ArgumentMatchers.eq("Address"), captureNode.capture(), ArgumentMatchers.eq(null), capturePackage.capture(), Mockito.isA(Schema.class));
        MatcherAssert.assertThat(capturePackage.getValue().name(), is("com.example.package"));
        MatcherAssert.assertThat(captureNode.getValue(), is(notNullValue()));
    }
}

