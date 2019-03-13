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
package uk.gov.gchq.gaffer.store.library;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class HashMapGraphLibraryTest extends AbstractGraphLibraryTest {
    private static final String TEST_GRAPH_ID = "testGraphId";

    private static final String TEST_SCHEMA_ID = "testSchemaId";

    private static final String TEST_PROPERTIES_ID = "testPropertiesId";

    private Schema schema = new Schema.Builder().build();

    private StoreProperties storeProperties = new StoreProperties();

    @Test
    public void shouldClearGraphLibrary() {
        // When
        final HashMapGraphLibrary graphLibrary = new HashMapGraphLibrary();
        graphLibrary.add(HashMapGraphLibraryTest.TEST_GRAPH_ID, HashMapGraphLibraryTest.TEST_SCHEMA_ID, schema, HashMapGraphLibraryTest.TEST_PROPERTIES_ID, storeProperties);
        graphLibrary.clear();
        // Then
        Assert.assertEquals(null, graphLibrary.getIds(HashMapGraphLibraryTest.TEST_GRAPH_ID));
        Assert.assertEquals(null, graphLibrary.getSchema(HashMapGraphLibraryTest.TEST_SCHEMA_ID));
        Assert.assertEquals(null, graphLibrary.getProperties(HashMapGraphLibraryTest.TEST_PROPERTIES_ID));
    }
}

