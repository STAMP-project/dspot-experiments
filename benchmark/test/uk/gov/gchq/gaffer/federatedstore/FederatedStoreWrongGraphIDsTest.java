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
package uk.gov.gchq.gaffer.federatedstore;


import FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.federatedstore.operation.AddGraph;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.library.HashMapGraphLibrary;


public class FederatedStoreWrongGraphIDsTest {
    public static final String GRAPH_1 = "graph1";

    public static final String PROP_1 = "prop1";

    public static final String SCHEMA_1 = "schema1";

    public static final String FED_ID = "testFedStore";

    public static final String E1_GROUP = "e1";

    public static final String THE_RETURN_OF_THE_OPERATIONS_SHOULD_NOT_BE_NULL = "the return of the operations should not be null";

    public static final String THERE_SHOULD_BE_ONE_ELEMENT = "There should be one element";

    public static final String EXCEPTION_NOT_AS_EXPECTED = "Exception not as expected";

    public static final String USING_THE_WRONG_GRAPH_ID_SHOULD_HAVE_THROWN_EXCEPTION = "Using the wrong graphId should have thrown exception.";

    private static final String CACHE_SERVICE_CLASS_STRING = "uk.gov.gchq.gaffer.cache.impl.HashMapCacheService";

    private FederatedStore store;

    private FederatedStoreProperties fedProps;

    private HashMapGraphLibrary library;

    private Context blankContext;

    public static final String WRONG_GRAPH_ID = "x";

    @Test
    public void shouldThrowWhenWrongGraphIDOptionIsUsed() throws Exception {
        store.initialise(FederatedStoreWrongGraphIDsTest.FED_ID, null, fedProps);
        store.execute(new AddGraph.Builder().graphId(FederatedStoreWrongGraphIDsTest.GRAPH_1).parentPropertiesId(FederatedStoreWrongGraphIDsTest.PROP_1).parentSchemaIds(Lists.newArrayList(FederatedStoreWrongGraphIDsTest.SCHEMA_1)).isPublic(true).build(), blankContext);
        final Entity expectedEntity = new Entity.Builder().group(FederatedStoreWrongGraphIDsTest.E1_GROUP).vertex("v1").build();
        store.execute(new AddElements.Builder().input(expectedEntity).option(KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedStoreWrongGraphIDsTest.GRAPH_1).build(), blankContext);
        CloseableIterable<? extends Element> execute = store.execute(new GetAllElements.Builder().build(), blankContext);
        Assert.assertNotNull(FederatedStoreWrongGraphIDsTest.THE_RETURN_OF_THE_OPERATIONS_SHOULD_NOT_BE_NULL, execute);
        Assert.assertEquals(FederatedStoreWrongGraphIDsTest.THERE_SHOULD_BE_ONE_ELEMENT, expectedEntity, execute.iterator().next());
        execute = store.execute(new GetAllElements.Builder().option(KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedStoreWrongGraphIDsTest.GRAPH_1).build(), blankContext);
        Assert.assertNotNull(FederatedStoreWrongGraphIDsTest.THE_RETURN_OF_THE_OPERATIONS_SHOULD_NOT_BE_NULL, execute);
        Assert.assertEquals(FederatedStoreWrongGraphIDsTest.THERE_SHOULD_BE_ONE_ELEMENT, expectedEntity, execute.iterator().next());
        try {
            store.execute(new GetAllElements.Builder().option(KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedStoreWrongGraphIDsTest.WRONG_GRAPH_ID).build(), blankContext);
            Assert.fail(FederatedStoreWrongGraphIDsTest.USING_THE_WRONG_GRAPH_ID_SHOULD_HAVE_THROWN_EXCEPTION);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(FederatedStoreWrongGraphIDsTest.EXCEPTION_NOT_AS_EXPECTED, String.format(FederatedGraphStorage.GRAPH_IDS_NOT_VISIBLE, Sets.newHashSet(FederatedStoreWrongGraphIDsTest.WRONG_GRAPH_ID)), e.getMessage());
        }
        try {
            store.execute(new AddElements.Builder().input(expectedEntity).option(KEY_OPERATION_OPTIONS_GRAPH_IDS, FederatedStoreWrongGraphIDsTest.WRONG_GRAPH_ID).build(), blankContext);
            Assert.fail(FederatedStoreWrongGraphIDsTest.USING_THE_WRONG_GRAPH_ID_SHOULD_HAVE_THROWN_EXCEPTION);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(FederatedStoreWrongGraphIDsTest.EXCEPTION_NOT_AS_EXPECTED, String.format(FederatedGraphStorage.GRAPH_IDS_NOT_VISIBLE, Sets.newHashSet(FederatedStoreWrongGraphIDsTest.WRONG_GRAPH_ID)), e.getMessage());
        }
    }
}

