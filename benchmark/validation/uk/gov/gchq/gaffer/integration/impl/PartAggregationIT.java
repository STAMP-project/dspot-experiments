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
import TestGroups.EDGE_3;
import TestGroups.EDGE_4;
import TestGroups.ENTITY;
import TestGroups.ENTITY_3;
import TestGroups.ENTITY_4;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.CollectionUtil;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.data.util.ElementUtil;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.integration.TraitRequirement;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.store.StoreTrait;


public class PartAggregationIT extends AbstractStoreIT {
    @Test
    public void shouldAggregateOnlyRequiredGroups() throws OperationException {
        // When
        final CloseableIterable<? extends Element> elements = AbstractStoreIT.graph.execute(new GetAllElements(), getUser());
        // Then
        final List<Element> resultElements = Lists.newArrayList(elements);
        final List<Element> expectedElements = new ArrayList<>();
        getEntities().values().forEach(( e) -> {
            final Entity clone = e.emptyClone();
            clone.copyProperties(e.getProperties());
            expectedElements.add(clone);
        });
        getEntities().values().forEach(( e) -> {
            final Entity clone = new Entity.Builder().group(TestGroups.ENTITY_4).vertex(e.getVertex()).build();
            clone.copyProperties(e.getProperties());
            clone.putProperty(TestPropertyNames.COUNT, 2L);
            expectedElements.add(clone);
        });
        getEntities().values().forEach(( e) -> {
            final Entity clone = new Entity.Builder().group(TestGroups.ENTITY_4).vertex(e.getVertex()).build();
            clone.copyProperties(e.getProperties());
            clone.putProperty(TestPropertyNames.SET, CollectionUtil.treeSet("a different string"));
            clone.putProperty(TestPropertyNames.COUNT, 2L);
            expectedElements.add(clone);
        });
        getEdges().values().forEach(( e) -> {
            final Edge clone = e.emptyClone();
            clone.copyProperties(e.getProperties());
            expectedElements.add(clone);
        });
        getEdges().values().forEach(( e) -> {
            final Edge clone = new Edge.Builder().group(TestGroups.EDGE_4).source(e.getSource()).dest(e.getDestination()).directed(e.isDirected()).build();
            clone.copyProperties(e.getProperties());
            clone.putProperty(TestPropertyNames.COUNT, 2L);
            expectedElements.add(clone);
        });
        expectedElements.forEach(( e) -> {
            if (TestGroups.ENTITY.equals(e.getGroup())) {
                e.putProperty(TestPropertyNames.SET, CollectionUtil.treeSet("3"));
                e.putProperty(TestPropertyNames.COUNT, 2L);
            } else
                if (TestGroups.EDGE.equals(e.getGroup())) {
                    e.putProperty(TestPropertyNames.COUNT, 2L);
                } else
                    if (TestGroups.EDGE_2.equals(e.getGroup())) {
                        e.putProperty(TestPropertyNames.INT, 2);
                    }


        });
        // Non aggregated elements should appear twice
        expectedElements.addAll(getNonAggregatedEntities());
        expectedElements.addAll(getNonAggregatedEntities());
        expectedElements.addAll(getNonAggregatedEdges());
        expectedElements.addAll(getNonAggregatedEdges());
        ElementUtil.assertElementEquals(expectedElements, resultElements);
    }

    @TraitRequirement(StoreTrait.QUERY_AGGREGATION)
    @Test
    public void shouldAggregateOnlyRequiredGroupsWithQueryTimeAggregation() throws OperationException {
        // When
        final CloseableIterable<? extends Element> elements = AbstractStoreIT.graph.execute(new GetAllElements.Builder().view(new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().groupBy().build()).edge(EDGE_3, new ViewElementDefinition.Builder().groupBy().build()).edge(EDGE_4, new ViewElementDefinition.Builder().groupBy().build()).entity(ENTITY, new ViewElementDefinition.Builder().groupBy().build()).entity(ENTITY_3, new ViewElementDefinition.Builder().groupBy().build()).entity(ENTITY_4, new ViewElementDefinition.Builder().groupBy().build()).build()).build(), getUser());
        // Then
        final List<Element> resultElements = Lists.newArrayList(elements);
        final List<Element> expectedElements = new ArrayList<>();
        getEntities().values().forEach(( e) -> {
            final Entity clone = e.emptyClone();
            clone.copyProperties(e.getProperties());
            expectedElements.add(clone);
        });
        getEntities().values().forEach(( e) -> {
            final Entity clone = new Entity.Builder().group(TestGroups.ENTITY_4).vertex(e.getVertex()).build();
            clone.copyProperties(e.getProperties());
            clone.putProperty(TestPropertyNames.COUNT, 4L);
            final TreeSet<String> treeSet = new TreeSet<>(((java.util.TreeSet) (e.getProperty(TestPropertyNames.SET))));
            treeSet.add("a different string");
            clone.putProperty(TestPropertyNames.SET, treeSet);
            expectedElements.add(clone);
        });
        getEdges().values().forEach(( e) -> {
            final Edge clone = e.emptyClone();
            clone.copyProperties(e.getProperties());
            expectedElements.add(clone);
        });
        getEdges().values().forEach(( e) -> {
            final Edge clone = new Edge.Builder().group(TestGroups.EDGE_4).source(e.getSource()).dest(e.getDestination()).directed(e.isDirected()).build();
            clone.copyProperties(e.getProperties());
            clone.putProperty(TestPropertyNames.COUNT, 2L);
            expectedElements.add(clone);
        });
        expectedElements.forEach(( e) -> {
            if (TestGroups.ENTITY.equals(e.getGroup())) {
                e.putProperty(TestPropertyNames.SET, CollectionUtil.treeSet("3"));
                e.putProperty(TestPropertyNames.COUNT, 2L);
            } else
                if (TestGroups.EDGE.equals(e.getGroup())) {
                    e.putProperty(TestPropertyNames.COUNT, 2L);
                } else
                    if (TestGroups.EDGE_2.equals(e.getGroup())) {
                        e.putProperty(TestPropertyNames.INT, 2);
                    }


        });
        // Non aggregated elements should appear twice
        expectedElements.addAll(getNonAggregatedEntities());
        expectedElements.addAll(getNonAggregatedEntities());
        expectedElements.addAll(getNonAggregatedEdges());
        expectedElements.addAll(getNonAggregatedEdges());
        ElementUtil.assertElementEquals(expectedElements, resultElements);
    }
}

