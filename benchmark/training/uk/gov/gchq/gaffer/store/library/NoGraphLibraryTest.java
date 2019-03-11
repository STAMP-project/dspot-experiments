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


public class NoGraphLibraryTest {
    NoGraphLibrary noGraphLibrary = new NoGraphLibrary();

    private static final String GRAPH_ID = "noGraphLibraryTestId";

    private static final String SCHEMA_ID = "noGraphLibrarySchemaId";

    private static final String PROPERTIES_ID = "noGraphLibraryPropertiesId";

    private final StoreProperties storeProperties = new StoreProperties();

    final Schema schema = new Schema.Builder().build();

    @Test
    public void shouldReturnNullWhenGettingIds() {
        // When / Then
        noGraphLibrary.add(NoGraphLibraryTest.GRAPH_ID, NoGraphLibraryTest.SCHEMA_ID, schema, NoGraphLibraryTest.PROPERTIES_ID, storeProperties);
        Assert.assertNull(noGraphLibrary.getIds(NoGraphLibraryTest.GRAPH_ID));
    }

    @Test
    public void shouldReturnNullWhenGettingSchema() {
        // When / Then
        noGraphLibrary.add(NoGraphLibraryTest.GRAPH_ID, NoGraphLibraryTest.SCHEMA_ID, schema, NoGraphLibraryTest.PROPERTIES_ID, storeProperties);
        Assert.assertNull(noGraphLibrary.getSchema(NoGraphLibraryTest.SCHEMA_ID));
    }

    @Test
    public void shouldReturnNullWhenGettingProperties() {
        // When / Then
        noGraphLibrary.add(NoGraphLibraryTest.GRAPH_ID, NoGraphLibraryTest.SCHEMA_ID, schema, NoGraphLibraryTest.PROPERTIES_ID, storeProperties);
        Assert.assertNull(noGraphLibrary.getProperties(NoGraphLibraryTest.PROPERTIES_ID));
    }
}

