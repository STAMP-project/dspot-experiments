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


import java.util.Properties;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.cache.exception.CacheOperationException;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.commonutil.exception.OverwritingException;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.GraphConfig;


public class FederatedStoreCacheTest {
    private static final String PATH_MAP_STORE_PROPERTIES = "properties/singleUseMockAccStore.properties";

    private static final String PATH_BASIC_EDGE_SCHEMA_JSON = "schema/basicEdgeSchema.json";

    private static final String CACHE_SERVICE_CLASS_STRING = "uk.gov.gchq.gaffer.cache.impl.HashMapCacheService";

    private static final String MAP_ID_1 = "mockMapGraphId1";

    private Graph testGraph = new Graph.Builder().config(new GraphConfig(FederatedStoreCacheTest.MAP_ID_1)).storeProperties(StreamUtil.openStream(FederatedStoreTest.class, FederatedStoreCacheTest.PATH_MAP_STORE_PROPERTIES)).addSchema(StreamUtil.openStream(FederatedStoreTest.class, FederatedStoreCacheTest.PATH_BASIC_EDGE_SCHEMA_JSON)).build();

    private static FederatedStoreCache federatedStoreCache;

    private static Properties properties = new Properties();

    @Test
    public void shouldAddAndGetGraphToCache() throws CacheOperationException {
        FederatedStoreCacheTest.federatedStoreCache.addGraphToCache(testGraph, null, false);
        Graph cached = FederatedStoreCacheTest.federatedStoreCache.getGraphFromCache(FederatedStoreCacheTest.MAP_ID_1);
        Assert.assertEquals(testGraph.getGraphId(), cached.getGraphId());
        Assert.assertEquals(testGraph.getSchema().toString(), cached.getSchema().toString());
        Assert.assertEquals(testGraph.getStoreProperties(), cached.getStoreProperties());
    }

    @Test
    public void shouldGetAllGraphIdsFromCache() throws CacheOperationException {
        FederatedStoreCacheTest.federatedStoreCache.addGraphToCache(testGraph, null, false);
        Set<String> cachedGraphIds = FederatedStoreCacheTest.federatedStoreCache.getAllGraphIds();
        Assert.assertEquals(1, cachedGraphIds.size());
        Assert.assertTrue(cachedGraphIds.contains(testGraph.getGraphId()));
    }

    @Test
    public void shouldDeleteFromCache() throws CacheOperationException {
        FederatedStoreCacheTest.federatedStoreCache.addGraphToCache(testGraph, null, false);
        Set<String> cachedGraphIds = FederatedStoreCacheTest.federatedStoreCache.getAllGraphIds();
        Assert.assertEquals(1, cachedGraphIds.size());
        Assert.assertTrue(cachedGraphIds.contains(testGraph.getGraphId()));
        FederatedStoreCacheTest.federatedStoreCache.deleteFromCache(testGraph.getGraphId());
        Set<String> cachedGraphIdsAfterDelete = FederatedStoreCacheTest.federatedStoreCache.getAllGraphIds();
        Assert.assertEquals(0, cachedGraphIdsAfterDelete.size());
    }

    @Test
    public void shouldThrowExceptionIfGraphAlreadyExistsInCache() throws CacheOperationException {
        FederatedStoreCacheTest.federatedStoreCache.addGraphToCache(testGraph, null, false);
        try {
            FederatedStoreCacheTest.federatedStoreCache.addGraphToCache(testGraph, null, false);
            Assert.fail("Exception expected");
        } catch (OverwritingException e) {
            Assert.assertTrue(e.getMessage().contains("Cache entry already exists"));
        }
    }

    @Test
    public void shouldThrowExceptionIfGraphIdToBeRemovedIsNull() throws CacheOperationException {
        FederatedStoreCacheTest.federatedStoreCache.addGraphToCache(testGraph, null, false);
        FederatedStoreCacheTest.federatedStoreCache.deleteFromCache(null);
        Assert.assertEquals(1, FederatedStoreCacheTest.federatedStoreCache.getAllGraphIds().size());
    }

    @Test
    public void shouldThrowExceptionIfGraphIdToGetIsNull() throws CacheOperationException {
        FederatedStoreCacheTest.federatedStoreCache.addGraphToCache(testGraph, null, false);
        Assert.assertNull(FederatedStoreCacheTest.federatedStoreCache.getGraphFromCache(null));
    }
}

