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
package uk.gov.gchq.gaffer.mapstore;


import StoreTrait.INGEST_AGGREGATION;
import StoreTrait.MATCHED_VERTEX;
import StoreTrait.POST_AGGREGATION_FILTERING;
import StoreTrait.POST_TRANSFORMATION_FILTERING;
import StoreTrait.PRE_AGGREGATION_FILTERING;
import StoreTrait.TRANSFORMATION;
import java.util.Arrays;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class MapStoreTest {
    @Test
    public void testTraits() throws StoreException {
        final MapStore mapStore = new MapStore();
        mapStore.initialise("graphId", new Schema(), new MapStoreProperties());
        final Set<StoreTrait> expectedTraits = new java.util.HashSet(Arrays.asList(INGEST_AGGREGATION, PRE_AGGREGATION_FILTERING, POST_AGGREGATION_FILTERING, TRANSFORMATION, POST_TRANSFORMATION_FILTERING, MATCHED_VERTEX));
        Assert.assertEquals(expectedTraits, mapStore.getTraits());
    }
}

