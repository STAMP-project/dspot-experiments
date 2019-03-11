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
package uk.gov.gchq.gaffer.data.generator;


import TestGroups.EDGE;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.id.EdgeId;
import uk.gov.gchq.gaffer.operation.data.generator.EdgeIdExtractor;


public class EdgeIdExtractorTest {
    @Test
    public void shouldGetIdentifierFromEdge() {
        // Given
        final EdgeIdExtractor extractor = new EdgeIdExtractor();
        final Edge edge = new Edge.Builder().group(EDGE).source("source").dest("destination").directed(true).build();
        // When
        final EdgeId seed = extractor._apply(edge);
        // Then
        Assert.assertEquals("source", seed.getSource());
        Assert.assertEquals("destination", seed.getDestination());
        Assert.assertTrue(seed.isDirected());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionForEntity() {
        // Given
        final EdgeIdExtractor extractor = new EdgeIdExtractor();
        final Entity entity = new Entity(TestGroups.ENTITY, "identifier");
        // When / Then
        try {
            extractor._apply(entity);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }
    }
}

