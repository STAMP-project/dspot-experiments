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
package uk.gov.gchq.gaffer.store.integration;


import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.data.elementdefinition.exception.SchemaException;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.serialisation.Serialiser;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialiser;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.store.operation.handler.OperationHandler;
import uk.gov.gchq.gaffer.store.operation.handler.OutputOperationHandler;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class StoreIT {
    @Test
    public void shouldCreateStoreAndValidateSchemas() throws IOException, SchemaException, StoreException {
        // Given
        final StoreIT.TestStore testStore = new StoreIT.TestStore();
        final Schema schema = Schema.fromJson(StreamUtil.schemas(getClass()));
        // When
        testStore.initialise("graphId", schema, new StoreProperties());
        // Then
        Assert.assertTrue(getSchema().getEdges().containsKey(EDGE));
        Assert.assertTrue(getSchema().getEdges().containsKey(EDGE));
        Assert.assertTrue(getSchema().getEntities().containsKey(ENTITY));
        Assert.assertTrue(getSchema().getEntities().containsKey(ENTITY));
        Assert.assertFalse(getSchema().getEdges().containsKey(EDGE_2));
        Assert.assertFalse(getSchema().getEntities().containsKey(ENTITY_2));
        Assert.assertFalse(getSchema().getEdges().containsKey(EDGE_2));
        Assert.assertFalse(getSchema().getEntities().containsKey(ENTITY_2));
        Assert.assertTrue(getSchema().validate().isValid());
    }

    private class TestStore extends Store {
        private final Set<StoreTrait> TRAITS = new java.util.HashSet(Arrays.asList(StoreTrait.INGEST_AGGREGATION, StoreTrait.PRE_AGGREGATION_FILTERING, StoreTrait.TRANSFORMATION));

        @Override
        public Set<StoreTrait> getTraits() {
            return TRAITS;
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
            return ToBytesSerialiser.class;
        }
    }
}

