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
package uk.gov.gchq.gaffer.accumulostore.retriever.impl;


import org.apache.accumulo.core.client.AccumuloException;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class AccumuloSingleIDRetrieverTest {
    private static final int numEntries = 1000;

    private static AccumuloStore byteEntityStore;

    private static AccumuloStore gaffer1KeyStore;

    private static final Schema schema = Schema.fromJson(StreamUtil.schemas(AccumuloSingleIDRetrieverTest.class));

    private static final AccumuloProperties PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloSingleIDRetrieverTest.class));

    private static final AccumuloProperties CLASSIC_PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.openStream(AccumuloSingleIDRetrieverTest.class, "/accumuloStoreClassicKeys.properties"));

    @Test
    public void testEntityIdQueryEdgesAndEntitiesByteEntityStore() throws AccumuloException, StoreException {
        testEntityIdQueryEdgesAndEntities(AccumuloSingleIDRetrieverTest.byteEntityStore);
    }

    @Test
    public void testEntityIdQueryEdgesAndEntitiesGaffer1Store() throws AccumuloException, StoreException {
        testEntityIdQueryEdgesAndEntities(AccumuloSingleIDRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void testEntityIdQueryEdgesOnly() throws AccumuloException, StoreException {
        testEntityIdQueryEdgesOnly(AccumuloSingleIDRetrieverTest.byteEntityStore);
        testEntityIdQueryEdgesOnly(AccumuloSingleIDRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void testEntityIdQueryEntitiesOnly() throws AccumuloException, StoreException {
        testEntityIdQueryEntitiesOnly(AccumuloSingleIDRetrieverTest.byteEntityStore);
        testEntityIdQueryEntitiesOnly(AccumuloSingleIDRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void testUndirectedEdgeIdQueries() throws AccumuloException, StoreException {
        testUndirectedEdgeIdQueries(AccumuloSingleIDRetrieverTest.byteEntityStore);
        testUndirectedEdgeIdQueries(AccumuloSingleIDRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void testDirectedEdgeIdQueries() throws AccumuloException, StoreException {
        testDirectedEdgeIdQueries(AccumuloSingleIDRetrieverTest.byteEntityStore);
        testDirectedEdgeIdQueries(AccumuloSingleIDRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void testEntityIdQueryIncomingEdgesOnly() throws AccumuloException, StoreException {
        testEntityIdQueryIncomingEdgesOnly(AccumuloSingleIDRetrieverTest.byteEntityStore);
        testEntityIdQueryIncomingEdgesOnly(AccumuloSingleIDRetrieverTest.gaffer1KeyStore);
    }

    @Test
    public void testEntityIdQueryOutgoingEdgesOnly() throws AccumuloException, StoreException {
        testEntityIdQueryOutgoingEdgesOnly(AccumuloSingleIDRetrieverTest.byteEntityStore);
        testEntityIdQueryOutgoingEdgesOnly(AccumuloSingleIDRetrieverTest.gaffer1KeyStore);
    }
}

