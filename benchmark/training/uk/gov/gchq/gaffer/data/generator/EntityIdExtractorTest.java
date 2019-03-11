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
import TestGroups.ENTITY;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.IdentifierType;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.operation.data.generator.EntityIdExtractor;


public class EntityIdExtractorTest {
    @Test
    public void shouldGetIdentifierFromEntity() {
        // Given
        final EntityIdExtractor extractor = new EntityIdExtractor();
        final Entity entity = new Entity.Builder().group(ENTITY).vertex("identifier").build();
        // When
        final EntityId seed = extractor._apply(entity);
        // Then
        Assert.assertSame("identifier", seed.getVertex());
    }

    @Test
    public void shouldGetSourceFromEdge() {
        // Given
        final EntityIdExtractor extractor = new EntityIdExtractor(IdentifierType.SOURCE);
        final Edge edge = new Edge.Builder().group(EDGE).source("1").dest("2").directed(false).build();
        // When
        final EntityId seed = extractor._apply(edge);
        // Then
        Assert.assertEquals("1", seed.getVertex());
    }

    @Test
    public void shouldGetDestinationFromEdge() {
        // Given
        final EntityIdExtractor extractor = new EntityIdExtractor(IdentifierType.DESTINATION);
        final Edge edge = new Edge.Builder().group(EDGE).source("1").dest("2").directed(false).build();
        // When
        final EntityId seed = extractor._apply(edge);
        // Then
        Assert.assertEquals("2", seed.getVertex());
    }

    @Test
    public void shouldGetMatchedVertexFromEdge() {
        // Given
        final EntityIdExtractor extractor = new EntityIdExtractor(IdentifierType.MATCHED_VERTEX);
        final Edge edge = new Edge.Builder().group(EDGE).source("1").dest("2").directed(false).build();
        // When
        final EntityId seed = extractor._apply(edge);
        // Then
        Assert.assertEquals("1", seed.getVertex());
    }

    @Test
    public void shouldGetOppositeMatchedVertexFromEdge() {
        // Given
        final EntityIdExtractor extractor = new EntityIdExtractor(IdentifierType.ADJACENT_MATCHED_VERTEX);
        final Edge edge = new Edge.Builder().group(EDGE).source("1").dest("2").directed(false).build();
        // When
        final EntityId seed = extractor._apply(edge);
        // Then
        Assert.assertEquals("2", seed.getVertex());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionFromEdgeWhenIdTypeIsDirected() {
        // Given
        final EntityIdExtractor extractor = new EntityIdExtractor(IdentifierType.DIRECTED);
        final Edge edge = new Edge.Builder().group(EDGE).source("source").dest("destination").directed(false).build();
        // When / Then
        try {
            extractor._apply(edge);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }
    }
}

