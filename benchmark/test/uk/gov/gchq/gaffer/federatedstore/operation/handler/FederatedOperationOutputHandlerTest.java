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


import com.google.common.collect.Iterables;
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
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.io.Output;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.user.User;


public abstract class FederatedOperationOutputHandlerTest<OP extends Output<O>, O> {
    public static final String TEST_ENTITY = "TestEntity";

    public static final String TEST_GRAPH_ID = "testGraphId";

    public static final String PROPERTY_TYPE = "property";

    protected O o1;

    protected O o2;

    protected O o3;

    protected O o4;

    protected User user;

    protected Context context;

    @Test
    public void shouldBeSetUp() throws Exception {
        Assert.assertNotNull("Required field object o1 is null", o1);
        Assert.assertNotNull("Required field object o2 is null", o2);
        Assert.assertNotNull("Required field object o3 is null", o3);
        Assert.assertNotNull("Required field object o4 is null", o4);
    }

    @Test
    public final void shouldMergeResultsFromFieldObjects() throws Exception {
        // Given
        final OP op = getExampleOperation();
        Schema unusedSchema = new Schema.Builder().build();
        StoreProperties storeProperties = new StoreProperties();
        Store mockStore1 = getMockStore(unusedSchema, storeProperties, o1);
        Store mockStore2 = getMockStore(unusedSchema, storeProperties, o2);
        Store mockStore3 = getMockStore(unusedSchema, storeProperties, o3);
        Store mockStore4 = getMockStore(unusedSchema, storeProperties, o4);
        FederatedStore mockStore = Mockito.mock(FederatedStore.class);
        LinkedHashSet<Graph> linkedGraphs = Sets.newLinkedHashSet();
        linkedGraphs.add(getGraphWithMockStore(mockStore1));
        linkedGraphs.add(getGraphWithMockStore(mockStore2));
        linkedGraphs.add(getGraphWithMockStore(mockStore3));
        linkedGraphs.add(getGraphWithMockStore(mockStore4));
        Mockito.when(mockStore.getGraphs(user, null)).thenReturn(linkedGraphs);
        // When
        O theMergedResultsOfOperation = getFederatedHandler().doOperation(op, context, mockStore);
        // Then
        validateMergeResultsFromFieldObjects(theMergedResultsOfOperation, o1, o2, o3, o4);
        Mockito.verify(mockStore1).execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.any(Context.class));
        Mockito.verify(mockStore2).execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.any(Context.class));
        Mockito.verify(mockStore3).execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.any(Context.class));
        Mockito.verify(mockStore4).execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.any(Context.class));
    }

    @Test
    public final void shouldMergeResultsFromFieldObjectsWithGivenGraphIds() throws Exception {
        // Given
        final OP op = getExampleOperation();
        op.addOption(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS, "1,3");
        Schema unusedSchema = new Schema.Builder().build();
        StoreProperties storeProperties = new StoreProperties();
        Store mockStore1 = getMockStore(unusedSchema, storeProperties, o1);
        Store mockStore2 = getMockStore(unusedSchema, storeProperties, o2);
        Store mockStore3 = getMockStore(unusedSchema, storeProperties, o3);
        Store mockStore4 = getMockStore(unusedSchema, storeProperties, o4);
        FederatedStore mockStore = Mockito.mock(FederatedStore.class);
        LinkedHashSet<Graph> filteredGraphs = Sets.newLinkedHashSet();
        filteredGraphs.add(getGraphWithMockStore(mockStore1));
        filteredGraphs.add(getGraphWithMockStore(mockStore3));
        Mockito.when(mockStore.getGraphs(user, "1,3")).thenReturn(filteredGraphs);
        // When
        O theMergedResultsOfOperation = getFederatedHandler().doOperation(op, context, mockStore);
        // Then
        validateMergeResultsFromFieldObjects(theMergedResultsOfOperation, o1, o3);
        Mockito.verify(mockStore1).execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.any(Context.class));
        Mockito.verify(mockStore2, Mockito.never()).execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.any(Context.class));
        Mockito.verify(mockStore3).execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.any(Context.class));
        Mockito.verify(mockStore4, Mockito.never()).execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.any(Context.class));
    }

    @Test
    public final void shouldThrowException() throws Exception {
        // Given
        final String message = "Test Exception";
        final OP op = getExampleOperation();
        op.addOption(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedOperationOutputHandlerTest.TEST_GRAPH_ID);
        Schema unusedSchema = new Schema.Builder().build();
        StoreProperties storeProperties = new StoreProperties();
        Store mockStoreInner = Mockito.mock(Store.class);
        BDDMockito.given(mockStoreInner.getSchema()).willReturn(unusedSchema);
        BDDMockito.given(mockStoreInner.getProperties()).willReturn(storeProperties);
        BDDMockito.given(mockStoreInner.execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.any(Context.class))).willThrow(new RuntimeException(message));
        FederatedStore mockStore = Mockito.mock(FederatedStore.class);
        HashSet<Graph> filteredGraphs = Sets.newHashSet(getGraphWithMockStore(mockStoreInner));
        Mockito.when(mockStore.getGraphs(user, FederatedOperationOutputHandlerTest.TEST_GRAPH_ID)).thenReturn(filteredGraphs);
        // When
        try {
            getFederatedHandler().doOperation(op, context, mockStore);
            Assert.fail("Exception not thrown");
        } catch (OperationException e) {
            Assert.assertEquals(message, e.getCause().getMessage());
        }
    }

    @Test
    public final void shouldReturnEmptyIterableWhenNoResults() throws Exception {
        // Given
        final OP op = getExampleOperation();
        op.addOption(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedOperationOutputHandlerTest.TEST_GRAPH_ID);
        Schema unusedSchema = new Schema.Builder().build();
        StoreProperties storeProperties = new StoreProperties();
        Store mockStoreInner = Mockito.mock(Store.class);
        BDDMockito.given(mockStoreInner.getSchema()).willReturn(unusedSchema);
        BDDMockito.given(mockStoreInner.getProperties()).willReturn(storeProperties);
        BDDMockito.given(mockStoreInner.execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.eq(context))).willReturn(null);
        FederatedStore mockStore = Mockito.mock(FederatedStore.class);
        HashSet<Graph> filteredGraphs = Sets.newHashSet(getGraphWithMockStore(mockStoreInner));
        Mockito.when(mockStore.getGraphs(user, FederatedOperationOutputHandlerTest.TEST_GRAPH_ID)).thenReturn(filteredGraphs);
        // When
        final O results = getFederatedHandler().doOperation(op, context, mockStore);
        Assert.assertEquals(0, Iterables.size(((Iterable) (results))));
    }

    @Test
    public final void shouldNotThrowException() throws Exception {
        // Given
        // Given
        final OP op = getExampleOperation();
        op.addOption(FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS, "1,3");
        op.addOption(FederatedStoreConstants.KEY_SKIP_FAILED_FEDERATED_STORE_EXECUTE, String.valueOf(true));
        Schema unusedSchema = new Schema.Builder().build();
        StoreProperties storeProperties = new StoreProperties();
        Store mockStore1 = getMockStore(unusedSchema, storeProperties, o1);
        Store mockStore2 = getMockStore(unusedSchema, storeProperties, o2);
        Store mockStore3 = Mockito.mock(Store.class);
        BDDMockito.given(mockStore3.getSchema()).willReturn(unusedSchema);
        BDDMockito.given(mockStore3.getProperties()).willReturn(storeProperties);
        BDDMockito.given(mockStore3.execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.eq(context))).willThrow(new RuntimeException("Test Exception"));
        Store mockStore4 = getMockStore(unusedSchema, storeProperties, o4);
        FederatedStore mockStore = Mockito.mock(FederatedStore.class);
        LinkedHashSet<Graph> filteredGraphs = Sets.newLinkedHashSet();
        filteredGraphs.add(getGraphWithMockStore(mockStore1));
        filteredGraphs.add(getGraphWithMockStore(mockStore3));
        Mockito.when(mockStore.getGraphs(user, "1,3")).thenReturn(filteredGraphs);
        // When
        O theMergedResultsOfOperation = null;
        try {
            theMergedResultsOfOperation = getFederatedHandler().doOperation(op, context, mockStore);
        } catch (Exception e) {
            Assert.fail(("Exception should not have been thrown: " + (e.getMessage())));
        }
        // Then
        validateMergeResultsFromFieldObjects(theMergedResultsOfOperation, o1);
        ArgumentCaptor<Context> context1Captor = ArgumentCaptor.forClass(Context.class);
        Mockito.verify(mockStore1).execute(ArgumentMatchers.any(OperationChain.class), context1Captor.capture());
        Assert.assertNotEquals(context.getJobId(), context1Captor.getValue().getJobId());
        Assert.assertEquals(context.getUser(), context1Captor.getValue().getUser());
        Mockito.verify(mockStore2, Mockito.never()).execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.any(Context.class));
        ArgumentCaptor<Context> context3Captor = ArgumentCaptor.forClass(Context.class);
        Mockito.verify(mockStore3).execute(ArgumentMatchers.any(OperationChain.class), context3Captor.capture());
        Assert.assertNotEquals(context.getJobId(), context3Captor.getValue().getJobId());
        Assert.assertEquals(context.getUser(), context3Captor.getValue().getUser());
        Mockito.verify(mockStore4, Mockito.never()).execute(ArgumentMatchers.any(OperationChain.class), ArgumentMatchers.any(Context.class));
    }
}

