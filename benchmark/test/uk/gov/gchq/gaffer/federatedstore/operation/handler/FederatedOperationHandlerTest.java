/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.federatedstore.operation.handler;


import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.LinkedHashSet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.federatedstore.FederatedStore;
import uk.gov.gchq.gaffer.federatedstore.FederatedStoreConstants;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.user.User;


public class FederatedOperationHandlerTest {
    private static final String TEST_GRAPH_ID = "testGraphId";

    private User user;

    private Context context;

    @Test
    public final void shouldMergeResultsFromFieldObjects() throws Exception {
        // Given
        final Operation op = Mockito.mock(Operation.class);
        final Operation opClone = Mockito.mock(Operation.class);
        BDDMockito.given(op.shallowClone()).willReturn(opClone);
        final OperationChain<?> opChainClone = OperationChain.wrap(opClone);
        Schema unusedSchema = new Schema.Builder().build();
        StoreProperties storeProperties = new StoreProperties();
        Store mockStore1 = getMockStore(unusedSchema, storeProperties);
        Store mockStore2 = getMockStore(unusedSchema, storeProperties);
        Store mockStore3 = getMockStore(unusedSchema, storeProperties);
        Store mockStore4 = getMockStore(unusedSchema, storeProperties);
        Graph graph1 = getGraphWithMockStore(mockStore1);
        Graph graph2 = getGraphWithMockStore(mockStore2);
        Graph graph3 = getGraphWithMockStore(mockStore3);
        Graph graph4 = getGraphWithMockStore(mockStore4);
        FederatedStore mockStore = Mockito.mock(FederatedStore.class);
        LinkedHashSet<Graph> linkedGraphs = Sets.newLinkedHashSet();
        linkedGraphs.add(graph1);
        linkedGraphs.add(graph2);
        linkedGraphs.add(graph3);
        linkedGraphs.add(graph4);
        Mockito.when(mockStore.getGraphs(user, null)).thenReturn(linkedGraphs);
        // When
        new FederatedOperationHandler().doOperation(op, context, mockStore);
        Mockito.verify(mockStore1).execute(ArgumentMatchers.eq(opChainClone), ArgumentMatchers.any(Context.class));
        Mockito.verify(mockStore2).execute(ArgumentMatchers.eq(opChainClone), ArgumentMatchers.any(Context.class));
        Mockito.verify(mockStore3).execute(ArgumentMatchers.eq(opChainClone), ArgumentMatchers.any(Context.class));
        Mockito.verify(mockStore4).execute(ArgumentMatchers.eq(opChainClone), ArgumentMatchers.any(Context.class));
    }

    @Test
    public final void shouldMergeResultsFromFieldObjectsWithGivenGraphIds() throws Exception {
        // Given
        final Operation op = Mockito.mock(Operation.class);
        final Operation opClone = Mockito.mock(Operation.class);
        BDDMockito.given(op.getOption(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS)).willReturn("1,3");
        BDDMockito.given(op.shallowClone()).willReturn(opClone);
        final OperationChain<?> opChainClone = OperationChain.wrap(opClone);
        Schema unusedSchema = new Schema.Builder().build();
        StoreProperties storeProperties = new StoreProperties();
        Store mockStore1 = getMockStore(unusedSchema, storeProperties);
        Store mockStore2 = getMockStore(unusedSchema, storeProperties);
        Store mockStore3 = getMockStore(unusedSchema, storeProperties);
        Store mockStore4 = getMockStore(unusedSchema, storeProperties);
        Graph graph1 = getGraphWithMockStore(mockStore1);
        Graph graph3 = getGraphWithMockStore(mockStore3);
        FederatedStore mockStore = Mockito.mock(FederatedStore.class);
        LinkedHashSet<Graph> filteredGraphs = Sets.newLinkedHashSet();
        filteredGraphs.add(graph1);
        filteredGraphs.add(graph3);
        Mockito.when(mockStore.getGraphs(user, "1,3")).thenReturn(filteredGraphs);
        // When
        new FederatedOperationHandler().doOperation(op, context, mockStore);
        Mockito.verify(mockStore1).execute(ArgumentMatchers.eq(opChainClone), ArgumentMatchers.any(Context.class));
        Mockito.verify(mockStore2, Mockito.never()).execute(ArgumentMatchers.eq(opChainClone), ArgumentMatchers.any(Context.class));
        Mockito.verify(mockStore3).execute(ArgumentMatchers.eq(opChainClone), ArgumentMatchers.any(Context.class));
        Mockito.verify(mockStore4, Mockito.never()).execute(ArgumentMatchers.eq(opChainClone), ArgumentMatchers.any(Context.class));
    }

    @Test
    public void shouldThrowException() throws Exception {
        String message = "test exception";
        final Operation op = Mockito.mock(Operation.class);
        final String graphID = "1,3";
        BDDMockito.given(op.getOption(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS)).willReturn(graphID);
        Schema unusedSchema = new Schema.Builder().build();
        StoreProperties storeProperties = new StoreProperties();
        Store mockStoreInner = getMockStore(unusedSchema, storeProperties);
        BDDMockito.given(mockStoreInner.execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.any(Context.class))).willThrow(new RuntimeException(message));
        FederatedStore mockStore = Mockito.mock(FederatedStore.class);
        HashSet<Graph> filteredGraphs = Sets.newHashSet(getGraphWithMockStore(mockStoreInner));
        Mockito.when(mockStore.getGraphs(user, graphID)).thenReturn(filteredGraphs);
        try {
            new FederatedOperationHandler().doOperation(op, context, mockStore);
            Assert.fail("Exception Not thrown");
        } catch (OperationException e) {
            Assert.assertEquals(message, e.getCause().getMessage());
        }
    }

    @Test
    public final void shouldNotThrowExceptionBecauseSkipFlagSetTrue() throws Exception {
        // Given
        final String graphID = "1,3";
        final Operation op = Mockito.mock(Operation.class);
        Mockito.when(op.getOption(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS)).thenReturn(graphID);
        Mockito.when(op.getOption(FederatedStoreConstants.KEY_SKIP_FAILED_FEDERATED_STORE_EXECUTE)).thenReturn(String.valueOf(true));
        Mockito.when(op.getOption(ArgumentMatchers.eq(FederatedStoreConstants.KEY_SKIP_FAILED_FEDERATED_STORE_EXECUTE), ArgumentMatchers.any(String.class))).thenReturn(String.valueOf(true));
        Schema unusedSchema = new Schema.Builder().build();
        StoreProperties storeProperties = new StoreProperties();
        Store mockStore1 = getMockStore(unusedSchema, storeProperties);
        BDDMockito.given(mockStore1.execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.eq(context))).willReturn(1);
        Store mockStore2 = getMockStore(unusedSchema, storeProperties);
        BDDMockito.given(mockStore2.execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.eq(context))).willThrow(new RuntimeException("Test Exception"));
        FederatedStore mockStore = Mockito.mock(FederatedStore.class);
        LinkedHashSet<Graph> filteredGraphs = Sets.newLinkedHashSet();
        filteredGraphs.add(getGraphWithMockStore(mockStore1));
        filteredGraphs.add(getGraphWithMockStore(mockStore2));
        Mockito.when(mockStore.getGraphs(user, graphID)).thenReturn(filteredGraphs);
        // When
        try {
            new FederatedOperationHandler().doOperation(op, context, mockStore);
        } catch (Exception e) {
            Assert.fail(("Exception should not have been thrown: " + (e.getMessage())));
        }
        // Then
        final ArgumentCaptor<Context> contextCaptor1 = ArgumentCaptor.forClass(Context.class);
        Mockito.verify(mockStore1, Mockito.atLeastOnce()).execute(ArgumentMatchers.any(OperationChain.class), contextCaptor1.capture());
        Assert.assertEquals(context.getUser(), contextCaptor1.getValue().getUser());
        Assert.assertNotEquals(context.getJobId(), contextCaptor1.getValue().getJobId());
        final ArgumentCaptor<Context> contextCaptor2 = ArgumentCaptor.forClass(Context.class);
        Mockito.verify(mockStore2, Mockito.atLeastOnce()).execute(ArgumentMatchers.any(OperationChain.class), contextCaptor2.capture());
        Assert.assertEquals(context.getUser(), contextCaptor2.getValue().getUser());
        Assert.assertNotEquals(context.getJobId(), contextCaptor2.getValue().getJobId());
    }
}

