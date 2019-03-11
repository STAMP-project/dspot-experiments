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


import IdentifierType.VERTEX;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import TestPropertyNames.COUNT;
import TestPropertyNames.INT;
import TestPropertyNames.SET;
import TestPropertyNames.TRANSIENT_1;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.element.function.ElementTransformer;
import uk.gov.gchq.gaffer.data.element.id.DirectedType;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.integration.TraitRequirement;
import uk.gov.gchq.gaffer.operation.data.EdgeSeed;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.user.User;
import uk.gov.gchq.koryphe.impl.function.Concat;
import uk.gov.gchq.koryphe.impl.predicate.IsEqual;
import uk.gov.gchq.koryphe.impl.predicate.IsIn;


public class GetAllElementsIT extends AbstractStoreIT {
    @Test
    @TraitRequirement({ StoreTrait.INGEST_AGGREGATION })
    public void shouldGetAllElements() throws Exception {
        for (final boolean includeEntities : Arrays.asList(true, false)) {
            for (final boolean includeEdges : Arrays.asList(true, false)) {
                if ((!includeEntities) && (!includeEdges)) {
                    // Cannot query for nothing!
                    continue;
                }
                for (final DirectedType directedType : DirectedType.values()) {
                    try {
                        shouldGetAllElements(includeEntities, includeEdges, directedType);
                    } catch (final AssertionError e) {
                        throw new AssertionError(((((("GetAllElements failed with parameters: includeEntities=" + includeEntities) + ", includeEdges=") + includeEdges) + ", directedType=") + (directedType.name())), e);
                    }
                }
            }
        }
    }

    @Test
    @TraitRequirement(StoreTrait.PRE_AGGREGATION_FILTERING)
    public void shouldGetAllElementsWithFilterWithoutSummarisation() throws Exception {
        final Edge edge1 = getEdges().get(new EdgeSeed(AbstractStoreIT.SOURCE_1, AbstractStoreIT.DEST_1, false)).emptyClone();
        edge1.putProperty(INT, 100);
        edge1.putProperty(COUNT, 1L);
        final Edge edge2 = edge1.emptyClone();
        edge2.putProperty(INT, 101);
        edge2.putProperty(COUNT, 1L);
        AbstractStoreIT.graph.execute(new AddElements.Builder().input(edge1, edge2).build(), getUser());
        final GetAllElements op = new GetAllElements.Builder().view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(INT).execute(new IsIn(Arrays.asList(((Object) (100)), 101))).build()).build()).build()).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        final List<Element> resultList = Lists.newArrayList(results);
        Assert.assertEquals(2, resultList.size());
        Assert.assertThat(resultList, IsCollectionContaining.hasItems(((Element) (edge1)), edge2));
    }

    @Test
    @TraitRequirement({ StoreTrait.PRE_AGGREGATION_FILTERING, StoreTrait.INGEST_AGGREGATION })
    public void shouldGetAllElementsFilteredOnGroup() throws Exception {
        final GetAllElements op = new GetAllElements.Builder().view(new View.Builder().entity(ENTITY).build()).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        final List<Element> resultList = Lists.newArrayList(results);
        Assert.assertEquals(getEntities().size(), resultList.size());
        for (final Element element : resultList) {
            Assert.assertEquals(ENTITY, element.getGroup());
        }
    }

    @Test
    @TraitRequirement(StoreTrait.PRE_AGGREGATION_FILTERING)
    public void shouldGetAllFilteredElements() throws Exception {
        final GetAllElements op = new GetAllElements.Builder().view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(VERTEX.name()).execute(new IsEqual("A1")).build()).build()).build()).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        final List<Element> resultList = Lists.newArrayList(results);
        Assert.assertEquals(1, resultList.size());
        Assert.assertEquals("A1", getVertex());
    }

    @Test
    @TraitRequirement({ StoreTrait.TRANSFORMATION, StoreTrait.PRE_AGGREGATION_FILTERING })
    public void shouldGetAllTransformedFilteredElements() throws Exception {
        final GetAllElements op = new GetAllElements.Builder().view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(VERTEX.name()).execute(new IsEqual("A1")).build()).transientProperty(TRANSIENT_1, String.class).transformer(new ElementTransformer.Builder().select(VERTEX.name(), SET).execute(new Concat()).project(TRANSIENT_1).build()).build()).build()).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        final List<Element> resultList = Lists.newArrayList(results);
        Assert.assertEquals(1, resultList.size());
        Assert.assertEquals("A1,[3]", resultList.get(0).getProperties().get(TRANSIENT_1));
    }

    @Test
    public void shouldGetAllElementsWithProvidedProperties() throws Exception {
        // Given
        final User user = new User();
        final GetAllElements op = new GetAllElements.Builder().view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, user);
        // Then
        for (final Element result : results) {
            Assert.assertEquals(1, result.getProperties().size());
            Assert.assertEquals(1L, result.getProperties().get(COUNT));
        }
    }

    @Test
    public void shouldGetAllElementsWithExcludedProperties() throws Exception {
        // Given
        final User user = new User();
        final GetAllElements op = new GetAllElements.Builder().view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).build()).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, user);
        // Then
        for (final Element result : results) {
            Assert.assertEquals(1, result.getProperties().size());
            Assert.assertEquals(1L, result.getProperties().get(COUNT));
        }
    }
}

