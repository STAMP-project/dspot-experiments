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
package uk.gov.gchq.gaffer.federatedstore.operation.handler.impl;


import FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS;
import StoreTrait.ALL_TRAITS;
import StoreTrait.INGEST_AGGREGATION;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.federatedstore.FederatedStore;
import uk.gov.gchq.gaffer.federatedstore.FederatedStoreProperties;
import uk.gov.gchq.gaffer.federatedstore.operation.AddGraph;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.serialisation.Serialiser;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.store.operation.GetTraits;
import uk.gov.gchq.gaffer.store.operation.handler.OperationHandler;
import uk.gov.gchq.gaffer.store.operation.handler.OutputOperationHandler;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class FederatedGetTraitsHandlerTest {
    public static final String ALT_STORE = "altStore";

    public static final String FED_STORE_ID = "fedStoreId";

    public static final String ACC_STORE = "accStore";

    private FederatedStore federatedStore;

    private FederatedStoreProperties properties;

    @Test
    public void shouldGetAllTraitsForEmptyStore() throws Exception {
        // Given
        federatedStore.initialise(FederatedGetTraitsHandlerTest.FED_STORE_ID, null, properties);
        // When
        final Set<StoreTrait> traits = federatedStore.execute(new GetTraits.Builder().currentTraits(false).build(), new uk.gov.gchq.gaffer.store.Context(testUser()));
        // Then
        Assert.assertEquals(ALL_TRAITS, traits);
    }

    @Test
    public void shouldGetAllTraitsForEmptyStoreWithCurrentTraits() throws Exception {
        // Given
        federatedStore.initialise(FederatedGetTraitsHandlerTest.FED_STORE_ID, null, properties);
        Assert.assertEquals("graph is not starting empty", 0, federatedStore.getAllGraphIds(testUser()).size());
        // When
        final Set<StoreTrait> traits = federatedStore.execute(new GetTraits.Builder().currentTraits(true).build(), new uk.gov.gchq.gaffer.store.Context(testUser()));
        // Then
        Assert.assertEquals(ALL_TRAITS, traits);
    }

    @Test
    public void shouldGetAllTraitsWhenContainsStoreWithOtherTraits() throws Exception {
        // Given
        federatedStore.initialise(FederatedGetTraitsHandlerTest.FED_STORE_ID, null, properties);
        federatedStore.execute(new AddGraph.Builder().isPublic(true).graphId(FederatedGetTraitsHandlerTest.ALT_STORE).storeProperties(new FederatedGetTraitsHandlerTest.TestStorePropertiesImpl()).schema(new Schema()).build(), new uk.gov.gchq.gaffer.store.Context(testUser()));
        // When
        final Set<StoreTrait> traits = federatedStore.execute(new GetTraits.Builder().currentTraits(false).build(), new uk.gov.gchq.gaffer.store.Context(testUser()));
        // Then
        Assert.assertEquals(ALL_TRAITS, traits);
    }

    @Test
    public void shouldGetCurrentTraitsWhenContainsStoreWithOtherTraits() throws Exception {
        // Given
        federatedStore.initialise(FederatedGetTraitsHandlerTest.FED_STORE_ID, null, properties);
        federatedStore.execute(new AddGraph.Builder().isPublic(true).graphId(FederatedGetTraitsHandlerTest.ALT_STORE).storeProperties(new FederatedGetTraitsHandlerTest.TestStorePropertiesImpl()).schema(new Schema()).build(), new uk.gov.gchq.gaffer.store.Context(testUser()));
        // When
        final Set<StoreTrait> traits = federatedStore.execute(new GetTraits.Builder().currentTraits(true).build(), new uk.gov.gchq.gaffer.store.Context(testUser()));
        // Then
        Assert.assertEquals(Sets.newHashSet(TRANSFORMATION, MATCHED_VERTEX, PRE_AGGREGATION_FILTERING, POST_AGGREGATION_FILTERING, POST_TRANSFORMATION_FILTERING), traits);
    }

    @Test
    public void shouldGetCurrentTraitsWhenContainsStoreWithOtherTraitsWithOptions() throws Exception {
        // Given
        federatedStore.initialise(FederatedGetTraitsHandlerTest.FED_STORE_ID, null, properties);
        federatedStore.execute(new AddGraph.Builder().isPublic(true).graphId(FederatedGetTraitsHandlerTest.ALT_STORE).storeProperties(new FederatedGetTraitsHandlerTest.TestStorePropertiesImpl()).schema(new Schema()).build(), new uk.gov.gchq.gaffer.store.Context(testUser()));
        federatedStore.execute(new AddGraph.Builder().isPublic(true).graphId(FederatedGetTraitsHandlerTest.ACC_STORE).storeProperties(StoreProperties.loadStoreProperties("/properties/singleUseMockAccStore.properties")).schema(new Schema()).build(), new uk.gov.gchq.gaffer.store.Context(testUser()));
        // When
        final Set<StoreTrait> traits = federatedStore.execute(new GetTraits.Builder().option(KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedGetTraitsHandlerTest.ALT_STORE).currentTraits(true).build(), new uk.gov.gchq.gaffer.store.Context(testUser()));
        // Then
        Assert.assertEquals(Sets.newHashSet(TRANSFORMATION, MATCHED_VERTEX, PRE_AGGREGATION_FILTERING, POST_AGGREGATION_FILTERING, POST_TRANSFORMATION_FILTERING), traits);
    }

    @Test
    public void shouldGetAllTraitsWhenContainsStoreWithOtherTraitsWithOptions() throws Exception {
        // Given
        federatedStore.initialise(FederatedGetTraitsHandlerTest.FED_STORE_ID, null, properties);
        federatedStore.execute(new AddGraph.Builder().isPublic(true).graphId(FederatedGetTraitsHandlerTest.ALT_STORE).storeProperties(new FederatedGetTraitsHandlerTest.TestStorePropertiesImpl()).schema(new Schema()).build(), new uk.gov.gchq.gaffer.store.Context(testUser()));
        federatedStore.execute(new AddGraph.Builder().isPublic(true).graphId(FederatedGetTraitsHandlerTest.ACC_STORE).storeProperties(StoreProperties.loadStoreProperties("/properties/singleUseMockAccStore.properties")).schema(new Schema()).build(), new uk.gov.gchq.gaffer.store.Context(testUser()));
        // When
        final Set<StoreTrait> traits = federatedStore.execute(new GetTraits.Builder().option(KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedGetTraitsHandlerTest.ALT_STORE).currentTraits(false).build(), new uk.gov.gchq.gaffer.store.Context(testUser()));
        // Then
        Assert.assertEquals(ALL_TRAITS, traits);
    }

    public static class TestStorePropertiesImpl extends StoreProperties {
        public TestStorePropertiesImpl() {
            super(FederatedGetTraitsHandlerTest.TestStoreImpl.class);
        }
    }

    public static class TestStoreImpl extends Store {
        @Override
        public Set<StoreTrait> getTraits() {
            return new java.util.HashSet(Arrays.asList(INGEST_AGGREGATION, StoreTrait.PRE_AGGREGATION_FILTERING, StoreTrait.POST_AGGREGATION_FILTERING, StoreTrait.TRANSFORMATION, StoreTrait.POST_TRANSFORMATION_FILTERING, StoreTrait.MATCHED_VERTEX));
        }

        @Override
        protected void addAdditionalOperationHandlers() {
        }

        @Override
        protected OutputOperationHandler<GetElements, CloseableIterable<? extends Element>> getGetElementsHandler() {
            return null;
        }

        @Override
        protected OutputOperationHandler<GetAllElements, CloseableIterable<? extends Element>> getGetAllElementsHandler() {
            return null;
        }

        @Override
        protected OutputOperationHandler<? extends GetAdjacentIds, CloseableIterable<? extends EntityId>> getAdjacentIdsHandler() {
            return null;
        }

        @Override
        protected OperationHandler<? extends AddElements> getAddElementsHandler() {
            return null;
        }

        @Override
        protected Class<? extends Serialiser> getRequiredParentSerialiserClass() {
            return null;
        }
    }
}

