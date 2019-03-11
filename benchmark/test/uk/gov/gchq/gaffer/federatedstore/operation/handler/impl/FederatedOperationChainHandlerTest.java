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
package uk.gov.gchq.gaffer.federatedstore.operation.handler.impl;


import FederatedOperationChain.Builder;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.util.ElementUtil;
import uk.gov.gchq.gaffer.federatedstore.FederatedStore;
import uk.gov.gchq.gaffer.federatedstore.FederatedStoreConstants;
import uk.gov.gchq.gaffer.federatedstore.PredefinedFederatedStore;
import uk.gov.gchq.gaffer.federatedstore.operation.FederatedOperationChain;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.store.Context;


public class FederatedOperationChainHandlerTest {
    public static final String GRAPH_IDS = ((PredefinedFederatedStore.ACCUMULO_GRAPH_WITH_ENTITIES) + ",") + (PredefinedFederatedStore.ACCUMULO_GRAPH_WITH_EDGES);

    private Element[] elements = new Element[]{ new Entity.Builder().group(ENTITY).vertex("1").build(), new Edge.Builder().group(EDGE).source("1").dest("2").directed(true).build() };

    private Element[] elements2 = new Element[]{ new Entity.Builder().group(ENTITY).vertex("2").build(), new Edge.Builder().group(EDGE).source("2").dest("3").directed(true).build() };

    @Test
    public void shouldHandleChainWithoutSpecialFederation() throws OperationException {
        // Given
        final FederatedStore store = createStore();
        final Context context = new Context();
        final OperationChain<Iterable<? extends Element>> opChain = new OperationChain.Builder().first(// Ensure the elements are returned form the graphs in the right order
        new GetAllElements.Builder().option(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedOperationChainHandlerTest.GRAPH_IDS).build()).then(new uk.gov.gchq.gaffer.operation.impl.Limit(1)).build();
        // When
        final Iterable result = store.execute(opChain, context);
        // Then - the result will contain just 1 element from the first graph
        ElementUtil.assertElementEquals(Collections.singletonList(elements[0]), result);
    }

    @Test
    public void shouldHandleChainWithIterableOutput() throws OperationException {
        // Given
        final FederatedStore store = createStore();
        final Context context = new Context();
        final FederatedOperationChain<Void, Element> opChain = new Builder<Void, Element>().operationChain(new OperationChain.Builder().first(new GetAllElements()).then(new uk.gov.gchq.gaffer.operation.impl.Limit(1)).build()).option(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedOperationChainHandlerTest.GRAPH_IDS).build();
        // When
        final Iterable result = store.execute(opChain, context);
        // Then - the result will contain 2 elements - 1 from each graph
        ElementUtil.assertElementEquals(Arrays.asList(elements[0], elements[1]), result);
    }

    @Test
    public void shouldHandleChainWithNoOutput() throws OperationException {
        // Given
        final FederatedStore store = createStore();
        final Context context = new Context();
        final FederatedOperationChain<Object, Void> opChain = new Builder<Object, Void>().operationChain(new OperationChain.Builder().first(new AddElements.Builder().input(elements2).build()).build()).option(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedOperationChainHandlerTest.GRAPH_IDS).build();
        // When
        final Iterable result = store.execute(opChain, context);
        // Then
        Assert.assertNull(result);
        final CloseableIterable<? extends Element> allElements = store.execute(new GetAllElements(), context);
        ElementUtil.assertElementEquals(Arrays.asList(elements[0], elements[1], elements2[0], elements2[1]), allElements);
    }

    @Test
    public void shouldHandleChainWithLongOutput() throws OperationException {
        // Given
        final FederatedStore store = createStore();
        final Context context = new Context();
        final FederatedOperationChain<Void, Long> opChain = new Builder<Void, Long>().operationChain(new OperationChain.Builder().first(new GetAllElements()).then(new uk.gov.gchq.gaffer.operation.impl.Count()).build()).option(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedOperationChainHandlerTest.GRAPH_IDS).build();
        // When
        final Iterable result = store.execute(opChain, context);
        // Then
        Assert.assertEquals(Lists.newArrayList(1L, 1L), Lists.newArrayList(result));
    }

    @Test
    public void shouldHandleChainNestedInsideAnOperationChain() throws OperationException {
        // Given
        final FederatedStore store = createStore();
        final Context context = new Context();
        final OperationChain<CloseableIterable<Element>> opChain = new OperationChain.Builder().first(new Builder<Void, Element>().operationChain(new OperationChain.Builder().first(new GetAllElements()).then(new uk.gov.gchq.gaffer.operation.impl.Limit(1)).build()).option(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedOperationChainHandlerTest.GRAPH_IDS).build()).build();
        // When
        final Iterable result = store.execute(opChain, context);
        // Then - the result will contain 2 elements - 1 from each graph
        ElementUtil.assertElementEquals(Arrays.asList(elements[0], elements[1]), result);
    }

    @Test
    public void shouldHandleChainWithExtraLimit() throws OperationException {
        // Given
        final FederatedStore store = createStore();
        final Context context = new Context();
        final OperationChain<Iterable<? extends Element>> opChain = new OperationChain.Builder().first(new Builder<Void, Element>().operationChain(new OperationChain.Builder().first(new GetAllElements()).then(new uk.gov.gchq.gaffer.operation.impl.Limit(1)).build()).option(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedOperationChainHandlerTest.GRAPH_IDS).build()).then(new uk.gov.gchq.gaffer.operation.impl.Limit(1)).build();
        // When
        final Iterable result = store.execute(opChain, context);
        // Then - the result will contain 1 element from the first graph
        ElementUtil.assertElementEquals(Collections.singletonList(elements[0]), result);
    }
}

