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
package org.jsonschema2pojo.rules;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import java.net.URI;
import java.net.URISyntaxException;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.SchemaStore;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SchemaRuleTest {
    private static final String NODE_NAME = "nodeName";

    private static final String TARGET_CLASS_NAME = (SchemaRuleTest.class.getName()) + ".DummyClass";

    private RuleFactory mockRuleFactory = Mockito.mock(RuleFactory.class);

    private SchemaRule rule = new SchemaRule(mockRuleFactory);

    @Test
    public void refsToOtherSchemasAreLoaded() throws JClassAlreadyExistsException, URISyntaxException {
        URI schemaUri = getClass().getResource("/schema/address.json").toURI();
        ObjectNode schemaWithRef = new ObjectMapper().createObjectNode();
        schemaWithRef.put("$ref", schemaUri.toString());
        JDefinedClass jclass = new JCodeModel()._class(SchemaRuleTest.TARGET_CLASS_NAME);
        final GenerationConfig mockGenerationConfig = Mockito.mock(GenerationConfig.class);
        Mockito.when(mockGenerationConfig.getRefFragmentPathDelimiters()).thenReturn("#/.");
        TypeRule mockTypeRule = Mockito.mock(TypeRule.class);
        Mockito.when(mockRuleFactory.getTypeRule()).thenReturn(mockTypeRule);
        Mockito.when(mockRuleFactory.getSchemaStore()).thenReturn(new SchemaStore());
        Mockito.when(mockRuleFactory.getGenerationConfig()).thenReturn(mockGenerationConfig);
        ArgumentCaptor<JsonNode> captureJsonNode = ArgumentCaptor.forClass(JsonNode.class);
        ArgumentCaptor<Schema> captureSchema = ArgumentCaptor.forClass(Schema.class);
        rule.apply(SchemaRuleTest.NODE_NAME, schemaWithRef, null, jclass, null);
        Mockito.verify(mockTypeRule).apply(ArgumentMatchers.eq("address"), captureJsonNode.capture(), ArgumentMatchers.any(), ArgumentMatchers.eq(jclass.getPackage()), captureSchema.capture());
        Assert.assertThat(captureSchema.getValue().getId(), is(equalTo(schemaUri)));
        Assert.assertThat(captureSchema.getValue().getContent(), is(equalTo(captureJsonNode.getValue())));
        Assert.assertThat(captureJsonNode.getValue().get("description").asText(), is(equalTo("An Address following the convention of http://microformats.org/wiki/hcard")));
    }

    @Test
    public void enumAsRootIsGeneratedCorrectly() throws JClassAlreadyExistsException {
        ObjectNode schemaContent = new ObjectMapper().createObjectNode();
        ObjectNode enumNode = schemaContent.objectNode();
        enumNode.put("type", "string");
        schemaContent.set("enum", enumNode);
        JDefinedClass jclass = new JCodeModel()._class(SchemaRuleTest.TARGET_CLASS_NAME);
        Schema schema = Mockito.mock(Schema.class);
        Mockito.when(schema.getContent()).thenReturn(schemaContent);
        Mockito.when(schema.deriveChildSchema(ArgumentMatchers.any())).thenReturn(schema);
        schema.setJavaTypeIfEmpty(jclass);
        EnumRule enumRule = Mockito.mock(EnumRule.class);
        Mockito.when(mockRuleFactory.getEnumRule()).thenReturn(enumRule);
        Mockito.when(enumRule.apply(SchemaRuleTest.NODE_NAME, enumNode, null, jclass, schema)).thenReturn(jclass);
        rule.apply(SchemaRuleTest.NODE_NAME, schemaContent, null, jclass, schema);
        Mockito.verify(enumRule).apply(SchemaRuleTest.NODE_NAME, schemaContent, null, jclass, schema);
        Mockito.verify(schema, Mockito.atLeastOnce()).setJavaTypeIfEmpty(jclass);
    }

    @Test
    public void existingTypeIsUsedWhenTypeIsAlreadyGenerated() throws URISyntaxException {
        JType previouslyGeneratedType = Mockito.mock(JType.class);
        URI schemaUri = getClass().getResource("/schema/address.json").toURI();
        SchemaStore schemaStore = new SchemaStore();
        Schema schema = schemaStore.create(schemaUri, "#/.");
        schema.setJavaType(previouslyGeneratedType);
        final GenerationConfig mockGenerationConfig = Mockito.mock(GenerationConfig.class);
        Mockito.when(mockGenerationConfig.getRefFragmentPathDelimiters()).thenReturn("#/.");
        Mockito.when(mockRuleFactory.getSchemaStore()).thenReturn(schemaStore);
        Mockito.when(mockRuleFactory.getGenerationConfig()).thenReturn(mockGenerationConfig);
        ObjectNode schemaNode = new ObjectMapper().createObjectNode();
        schemaNode.put("$ref", schemaUri.toString());
        JType result = rule.apply(SchemaRuleTest.NODE_NAME, schemaNode, null, null, schema);
        Assert.assertThat(result, is(sameInstance(previouslyGeneratedType)));
    }
}

