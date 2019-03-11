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


import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.federatedstore.operation.AddGraph;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.user.User;


public class FederatedStoreMultiCacheTest {
    public static final String FEDERATED_STORE_ID = "testFederatedStoreId";

    public static final String ACC_ID_1 = "mockAccGraphId1";

    public static final String PATH_ACC_STORE_PROPERTIES = "properties/singleUseMockAccStore.properties";

    public static final String PATH_BASIC_ENTITY_SCHEMA_JSON = "schema/basicEntitySchema.json";

    public static final String CACHE_SERVICE_CLASS_STRING = "uk.gov.gchq.gaffer.cache.impl.HashMapCacheService";

    public static User authUser = authUser();

    public static User testUser = testUser();

    public FederatedStore store;

    public FederatedStoreProperties federatedStoreProperties;

    public Collection<String> originalStoreIds;

    public FederatedStore store2;

    public User blankUser;

    @Test
    public void shouldInitialiseByCacheToContainSameGraphsForAddingUser() throws Exception {
        originalStoreIds = store.getAllGraphIds(FederatedStoreMultiCacheTest.testUser);
        final int firstStoreSize = originalStoreIds.size();
        Assert.assertEquals("adding user should have visibility of first store graphs", 1, firstStoreSize);
        Collection<String> storeGetIds2 = store2.getAllGraphIds(FederatedStoreMultiCacheTest.testUser);
        Assert.assertEquals("adding user should have same visibility of second store graphs", firstStoreSize, storeGetIds2.size());
        Assert.assertTrue(originalStoreIds.containsAll(storeGetIds2));
    }

    @Test
    public void shouldInitialiseByCacheToContainSameGraphsForAuthUser() throws Exception {
        originalStoreIds = store.getAllGraphIds(FederatedStoreMultiCacheTest.authUser);
        final int firstStoreSize = originalStoreIds.size();
        Assert.assertEquals("auth user should have visibility of first store graphs", 1, firstStoreSize);
        Collection<String> storeGetIds2 = store2.getAllGraphIds(FederatedStoreMultiCacheTest.authUser);
        Assert.assertEquals("auth user should have same visibility of second store graphs", firstStoreSize, storeGetIds2.size());
        Assert.assertTrue(originalStoreIds.containsAll(storeGetIds2));
    }

    @Test
    public void shouldInitialiseByCacheToContainSameGraphsForBlankUser() throws Exception {
        originalStoreIds = store.getAllGraphIds(blankUser);
        final int firstStoreSize = originalStoreIds.size();
        Assert.assertEquals("There should be 1 graphs", 1, store.getAllGraphIds(FederatedStoreMultiCacheTest.testUser).size());
        Assert.assertEquals("blank user should not have visibility of first store graphs", 0, firstStoreSize);
        Collection<String> storeGetIds2 = store2.getAllGraphIds(blankUser);
        Assert.assertEquals("blank user should have same visibility of second store graphs", firstStoreSize, storeGetIds2.size());
        Assert.assertEquals("blank user should have same visibility of second store graphs", firstStoreSize, storeGetIds2.size());
        Assert.assertTrue(originalStoreIds.containsAll(storeGetIds2));
    }

    @Test
    public void shouldInitialiseByCacheToContainSamePublicGraphsForBlankUser() throws Exception {
        store.execute(new AddGraph.Builder().graphId(((FederatedStoreMultiCacheTest.ACC_ID_1) + 1)).isPublic(true).storeProperties(AccumuloProperties.loadStoreProperties(FederatedStoreMultiCacheTest.PATH_ACC_STORE_PROPERTIES)).schema(Schema.fromJson(StreamUtil.openStream(Schema.class, FederatedStoreMultiCacheTest.PATH_BASIC_ENTITY_SCHEMA_JSON))).build(), new Context.Builder().user(FederatedStoreMultiCacheTest.testUser).build());
        store2 = new FederatedStore();
        store2.initialise(((FederatedStoreMultiCacheTest.FEDERATED_STORE_ID) + 1), null, federatedStoreProperties);
        Assert.assertEquals("There should be 2 graphs", 2, store.getAllGraphIds(FederatedStoreMultiCacheTest.testUser).size());
        originalStoreIds = store.getAllGraphIds(blankUser);
        final int firstStoreSize = originalStoreIds.size();
        Assert.assertEquals("blank user should have visibility of public graph", 1, firstStoreSize);
        Collection<String> storeGetIds2 = store2.getAllGraphIds(blankUser);
        Assert.assertEquals("blank user should have same visibility of second store graphs", firstStoreSize, storeGetIds2.size());
        Assert.assertTrue(originalStoreIds.containsAll(storeGetIds2));
    }
}

