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


import TestGroups.EDGE;
import TestPropertyNames.COUNT;
import TestPropertyNames.INT;
import TestPropertyNames.SET;
import TestPropertyNames.TIMESTAMP;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.CollectionUtil;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.function.ElementAggregator;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.data.util.ElementUtil;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.integration.TraitRequirement;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EdgeSeed;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.koryphe.impl.binaryoperator.Product;
import uk.gov.gchq.koryphe.impl.predicate.IsIn;


public class AggregationIT extends AbstractStoreIT {
    private final String AGGREGATED_SOURCE = (AbstractStoreIT.SOURCE) + 6;

    private final String AGGREGATED_DEST = (AbstractStoreIT.DEST) + 6;

    private final int NON_AGGREGATED_ID = 8;

    private final String NON_AGGREGATED_SOURCE = (AbstractStoreIT.SOURCE) + (NON_AGGREGATED_ID);

    private final String NON_AGGREGATED_DEST = (AbstractStoreIT.DEST) + (NON_AGGREGATED_ID);

    @Test
    @TraitRequirement(StoreTrait.INGEST_AGGREGATION)
    public void shouldAggregateIdenticalElements() throws OperationException {
        // Given
        final GetElements getElements = new GetElements.Builder().input(new EntitySeed(AGGREGATED_SOURCE)).build();
        // When
        final List<Element> results = Lists.newArrayList(AbstractStoreIT.graph.execute(getElements, getUser()));
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        final Entity expectedEntity = new Entity(TestGroups.ENTITY, AGGREGATED_SOURCE);
        expectedEntity.putProperty(SET, CollectionUtil.treeSet("3"));
        expectedEntity.putProperty(COUNT, 3L);
        final Edge expectedEdge = new Edge.Builder().group(EDGE).source(AGGREGATED_SOURCE).dest(AGGREGATED_DEST).directed(false).build();
        expectedEdge.putProperty(INT, 1);
        expectedEdge.putProperty(COUNT, 2L);
        ElementUtil.assertElementEquals(Arrays.asList(expectedEdge, expectedEntity), results);
    }

    @Test
    @TraitRequirement(StoreTrait.INGEST_AGGREGATION)
    public void shouldAggregateElementsWithNoGroupBy() throws OperationException {
        // Given
        final String vertex = "testVertex1";
        final long timestamp = System.currentTimeMillis();
        AbstractStoreIT.graph.execute(new AddElements.Builder().input(new Entity.Builder().group(ENTITY_2).vertex(vertex).property(INT, 1).property(TIMESTAMP, timestamp).build(), new Entity.Builder().group(ENTITY_2).vertex(vertex).property(INT, 2).property(TIMESTAMP, timestamp).build()).build(), getUser());
        AbstractStoreIT.graph.execute(new AddElements.Builder().input(new Entity.Builder().group(ENTITY_2).vertex(vertex).property(INT, 2).property(TIMESTAMP, timestamp).build(), new Entity.Builder().group(ENTITY_2).vertex(vertex).property(INT, 3).property(TIMESTAMP, timestamp).build(), new Entity.Builder().group(ENTITY_2).vertex(vertex).property(INT, 9).property(TIMESTAMP, timestamp).build()).build(), getUser());
        final GetElements getElements = new GetElements.Builder().input(new EntitySeed(vertex)).build();
        // When
        final List<Element> results = Lists.newArrayList(AbstractStoreIT.graph.execute(getElements, getUser()));
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        final Entity expectedEntity2 = new Entity.Builder().group(ENTITY_2).vertex(vertex).property(INT, 9).property(TIMESTAMP, timestamp).build();
        ElementUtil.assertElementEquals(Collections.singletonList(expectedEntity2), results);
    }

    @Test
    @TraitRequirement(StoreTrait.INGEST_AGGREGATION)
    public void shouldNotAggregateEdgesWithDifferentDirectionFlag() throws OperationException {
        // Given
        final GetElements getEdges = new GetElements.Builder().input(new EntitySeed(NON_AGGREGATED_SOURCE)).view(new View.Builder().edge(EDGE).build()).build();
        // When
        final List<Element> results = Lists.newArrayList(AbstractStoreIT.graph.execute(getEdges, getUser()));
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        ElementUtil.assertElementEquals(Arrays.asList(getEdge(NON_AGGREGATED_SOURCE, NON_AGGREGATED_DEST, false), new Edge.Builder().group(EDGE).source(NON_AGGREGATED_SOURCE).dest(NON_AGGREGATED_DEST).directed(true).build()), results);
    }

    @Test
    @TraitRequirement({ StoreTrait.PRE_AGGREGATION_FILTERING, StoreTrait.QUERY_AGGREGATION })
    public void shouldGetAllElementsWithFilterSummarisation() throws Exception {
        final Edge edge1 = getEdges().get(new EdgeSeed(AbstractStoreIT.SOURCE_1, AbstractStoreIT.DEST_1, false)).emptyClone();
        edge1.putProperty(INT, 100);
        edge1.putProperty(COUNT, 1L);
        final Edge edge2 = edge1.emptyClone();
        edge2.putProperty(INT, 101);
        edge2.putProperty(COUNT, 1L);
        AbstractStoreIT.graph.execute(new AddElements.Builder().input(edge1, edge2).build(), getUser());
        final GetAllElements op = new GetAllElements.Builder().view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(INT).execute(new IsIn(Arrays.asList(100, 101))).build()).groupBy().aggregator(new ElementAggregator.Builder().select(INT).execute(new Product()).build()).build()).build()).build();
        // When
        final CloseableIterable<? extends Element> results = AbstractStoreIT.graph.execute(op, getUser());
        // Then
        final List<Element> resultList = Lists.newArrayList(results);
        Assert.assertEquals(1, resultList.size());
        // aggregation is has been replaced with Product
        Assert.assertEquals(10100, resultList.get(0).getProperty(INT));
    }
}

