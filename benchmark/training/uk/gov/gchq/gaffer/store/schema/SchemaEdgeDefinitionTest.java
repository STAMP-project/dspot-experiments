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


import TestGroups.EDGE;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.store.TestTypes;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition.Builder;
import uk.gov.gchq.koryphe.ValidationResult;


public class SchemaEdgeDefinitionTest extends SchemaElementDefinitionTest<SchemaEdgeDefinition> {
    @Test
    public void shouldBuildEdgeDefinition() {
        // When
        final SchemaEdgeDefinition elementDef = createBuilder().build();
        setupSchema(elementDef);
        // Then
        Assert.assertEquals(3, elementDef.getIdentifiers().size());
        Assert.assertEquals("id.integer", elementDef.getSource());
        Assert.assertEquals("id.date", elementDef.getDestination());
        Assert.assertEquals("directed.true", elementDef.getDirected());
    }

    @Test
    public void shouldOverrideSourceWhenMerging() {
        // Given
        final SchemaEdgeDefinition elementDef1 = new SchemaEdgeDefinition.Builder().source("source.integer").build();
        final SchemaEdgeDefinition elementDef2 = new SchemaEdgeDefinition.Builder().source("source.string").build();
        // When
        final SchemaEdgeDefinition mergedDef = new Builder().merge(elementDef1).merge(elementDef2).build();
        // Then
        Assert.assertEquals("source.string", mergedDef.getSource());
    }

    @Test
    public void shouldOverrideDestinationWhenMerging() {
        // Given
        final SchemaEdgeDefinition elementDef1 = new SchemaEdgeDefinition.Builder().destination("destination.integer").build();
        final SchemaEdgeDefinition elementDef2 = new SchemaEdgeDefinition.Builder().destination("destination.string").build();
        // When
        final SchemaEdgeDefinition mergedDef = new Builder().merge(elementDef1).merge(elementDef2).build();
        // Then
        Assert.assertEquals("destination.string", mergedDef.getDestination());
    }

    @Test
    public void shouldPassValidationWhenEdgeSourceAndDestinationDefined() {
        // Given
        final SchemaEdgeDefinition elementDef = new SchemaEdgeDefinition.Builder().source("src").destination("dest").directed(TestTypes.DIRECTED_EITHER).build();
        final Schema schema = new Schema.Builder().edge(EDGE, elementDef).type("src", String.class).type("dest", String.class).type(TestTypes.DIRECTED_EITHER, Boolean.class).build();
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        // When
        final ValidationResult result = validator.validate(elementDef);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldFailValidationWhenEdgeSourceOrDestinationNull() {
        // Given
        final SchemaEdgeDefinition elementDef = new SchemaEdgeDefinition.Builder().source(null).destination("dest").build();
        final Schema schema = new Schema.Builder().edge(EDGE, elementDef).type("src", String.class).type("dest", String.class).build();
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        // When
        final ValidationResult result = validator.validate(elementDef);
        // Then
        Assert.assertFalse(result.isValid());
    }
}

