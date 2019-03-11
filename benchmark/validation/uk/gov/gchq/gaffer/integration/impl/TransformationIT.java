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
package uk.gov.gchq.gaffer.integration.impl;


import IdentifierType.SOURCE;
import IdentifierType.VERTEX;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import TestPropertyNames.COUNT;
import TestPropertyNames.INT;
import TestPropertyNames.SET;
import TestPropertyNames.TRANSIENT_1;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.function.ElementTransformer;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.integration.TraitRequirement;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EdgeSeed;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.koryphe.impl.function.Concat;


public class TransformationIT extends AbstractStoreIT {
    private static final String VERTEX = "vertexWithTransientProperty";

    /**
     * Tests that the entity stored does not contain any transient properties not stored in the Schemas.
     *
     * @throws OperationException
     * 		should never be thrown.
     */
    @Test
    public void shouldNotStoreEntityPropertiesThatAreNotInSchema() throws OperationException {
        // Given
        final GetElements getEntities = new GetElements.Builder().input(new EntitySeed(TransformationIT.VERTEX)).view(new View.Builder().entity(ENTITY).entity(ENTITY_2).build()).build();
        // When
        final List<Element> results = Lists.newArrayList(AbstractStoreIT.graph.execute(getEntities, getUser()));
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        for (final Element result : results) {
            Assert.assertNull(result.getProperty(TRANSIENT_1));
        }
    }

    /**
     * Tests that the edge stored does not contain any transient properties not stored in the Schemas.
     *
     * @throws OperationException
     * 		should never be thrown.
     */
    @Test
    public void shouldNotStoreEdgePropertiesThatAreNotInSchema() throws OperationException {
        // Given
        final GetElements getEdges = new GetElements.Builder().input(new EdgeSeed(((TransformationIT.VERTEX) + (AbstractStoreIT.SOURCE)), ((TransformationIT.VERTEX) + (AbstractStoreIT.DEST)), true)).view(new View.Builder().edge(EDGE).build()).build();
        // When
        final List<Element> results = Lists.newArrayList(AbstractStoreIT.graph.execute(getEdges, getUser()));
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        for (final Element result : results) {
            Assert.assertEquals(1L, result.getProperty(COUNT));
            Assert.assertNull(result.getProperty(TRANSIENT_1));
        }
    }

    @Test
    @TraitRequirement(StoreTrait.TRANSFORMATION)
    public void shouldCreateTransientEntityProperty() throws OperationException {
        // Given
        final GetElements getEntities = new GetElements.Builder().input(new EntitySeed("A1")).view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().transientProperty(TRANSIENT_1, String.class).transformer(new ElementTransformer.Builder().select(IdentifierType.VERTEX.name(), SET).execute(new Concat()).project(TRANSIENT_1).build()).build()).build()).build();
        // When
        final List<Element> results = Lists.newArrayList(AbstractStoreIT.graph.execute(getEntities, getUser()));
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        for (final Element result : results) {
            Assert.assertEquals("A1,[3]", result.getProperty(TRANSIENT_1));
        }
    }

    @Test
    @TraitRequirement(StoreTrait.TRANSFORMATION)
    public void shouldCreateTransientEdgeProperty() throws OperationException {
        // Given
        final GetElements getEdges = new GetElements.Builder().input(new EdgeSeed(AbstractStoreIT.SOURCE_1, AbstractStoreIT.DEST_1, false)).view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().transientProperty(TRANSIENT_1, String.class).transformer(new ElementTransformer.Builder().select(IdentifierType.SOURCE.name(), INT).execute(new Concat()).project(TRANSIENT_1).build()).build()).build()).build();
        // When
        final List<Element> results = Lists.newArrayList(AbstractStoreIT.graph.execute(getEdges, getUser()));
        Assert.assertNotNull(results);
        for (final Element result : results) {
            Assert.assertEquals((((AbstractStoreIT.SOURCE_1) + ",") + (result.getProperty(INT))), result.getProperty(TRANSIENT_1));
        }
    }

    @Test
    @TraitRequirement(StoreTrait.TRANSFORMATION)
    public void shouldTransformVertex() throws OperationException {
        // Given
        final GetElements getEntities = new GetElements.Builder().input(new EntitySeed("A1")).view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().transformer(new ElementTransformer.Builder().select(IdentifierType.VERTEX.name(), SET).execute(new Concat()).project(IdentifierType.VERTEX.name()).build()).build()).build()).build();
        // When
        final List<Element> results = Lists.newArrayList(AbstractStoreIT.graph.execute(getEntities, getUser()));
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        for (final Element result : results) {
            Assert.assertEquals("A1,[3]", getVertex());
        }
    }
}

