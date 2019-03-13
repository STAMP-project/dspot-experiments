/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.store.schema;


import TestGroups.ENTITY;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.koryphe.ValidationResult;


public class SchemaEntityDefinitionTest extends SchemaElementDefinitionTest<SchemaEntityDefinition> {
    @Test
    public void shouldBuildEntityDefinition() {
        // When
        final SchemaEntityDefinition elementDef = createBuilder().build();
        setupSchema(elementDef);
        // Then
        Assert.assertEquals(1, elementDef.getIdentifiers().size());
        Assert.assertEquals("id.integer", elementDef.getVertex());
    }

    @Test
    public void shouldOverrideVertexWhenMerging() {
        // Given
        final SchemaEntityDefinition elementDef1 = new SchemaEntityDefinition.Builder().vertex("vertex.integer").build();
        final SchemaEntityDefinition elementDef2 = new SchemaEntityDefinition.Builder().vertex("vertex.string").build();
        // When
        final SchemaEntityDefinition mergedDef = new SchemaEntityDefinition.Builder().merge(elementDef1).merge(elementDef2).build();
        // Then
        Assert.assertEquals("vertex.string", mergedDef.getVertex());
    }

    @Test
    public void shouldPassValidationWhenVertexDefined() {
        // Given
        final SchemaEntityDefinition elementDef = new SchemaEntityDefinition.Builder().vertex("vertex.string").build();
        final Schema schema = new Schema.Builder().entity(ENTITY, elementDef).type("vertex.string", String.class).build();
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        // When
        final ValidationResult result = validator.validate(elementDef);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldFailValidationWhenVertexNotDefined() {
        // Given
        final SchemaEntityDefinition elementDef = new SchemaEntityDefinition.Builder().vertex(null).build();
        final Schema schema = new Schema.Builder().entity(ENTITY, elementDef).type("vertex.string", String.class).build();
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        // When
        final ValidationResult result = validator.validate(elementDef);
        // Then
        Assert.assertFalse(result.isValid());
    }
}

