/**
 * Copyright 2016 KairosDB Authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.datastore.h2;


import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.datastore.DatastoreTestHelper;


public class H2DatastoreTest extends DatastoreTestHelper {
    private static final String DB_PATH = "build/h2db_test";

    private static H2Datastore h2Datastore;

    /**
     * This is here because hbase throws an exception in this case
     */
    @Test
    public void test_queryDatabase_noMetric() throws DatastoreException {
        Map<String, String> tags = new TreeMap<>();
        QueryMetric query = new QueryMetric(500, 0, "metric_not_there");
        query.setEndTime(3000);
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        List<DataPointGroup> results = dq.execute();
        MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
        DataPointGroup dpg = results.get(0);
        MatcherAssert.assertThat(dpg.getName(), Is.is("metric_not_there"));
        Assert.assertFalse(dpg.hasNext());
        dq.close();
    }

    @Test
    public void test_serviceKeyStore_singleService() throws DatastoreException {
        H2DatastoreTest.h2Datastore.setValue("Service", "ServiceKey", "key1", "value1");
        H2DatastoreTest.h2Datastore.setValue("Service", "ServiceKey", "key2", "value2");
        H2DatastoreTest.h2Datastore.setValue("Service", "ServiceKey", "foo", "value3");
        // Test setValue and getValue
        assertServiceKeyValue("Service", "ServiceKey", "key1", "value1");
        assertServiceKeyValue("Service", "ServiceKey", "key2", "value2");
        assertServiceKeyValue("Service", "ServiceKey", "foo", "value3");
        // Test lastModified value changes
        long lastModified = H2DatastoreTest.h2Datastore.getValue("Service", "ServiceKey", "key2").getLastModified().getTime();
        H2DatastoreTest.h2Datastore.setValue("Service", "ServiceKey", "key2", "changed");
        assertServiceKeyValue("Service", "ServiceKey", "key2", "changed");
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.getValue("Service", "ServiceKey", "key2").getLastModified().getTime(), Matchers.greaterThan(lastModified));
        // Test listKeys
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.listKeys("Service", "ServiceKey"), CoreMatchers.hasItems("foo", "key1", "key2"));
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.listKeys("Service", "ServiceKey", "key"), CoreMatchers.hasItems("key1", "key2"));
        // Test delete
        lastModified = H2DatastoreTest.h2Datastore.getServiceKeyLastModifiedTime("Service", "ServiceKey").getTime();
        H2DatastoreTest.h2Datastore.deleteKey("Service", "ServiceKey", "key2");
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.listKeys("Service", "ServiceKey"), CoreMatchers.hasItems("foo", "key1"));
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.getValue("Service", "ServiceKey", "key2"), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.getServiceKeyLastModifiedTime("Service", "ServiceKey").getTime(), Matchers.greaterThan(lastModified));
        lastModified = H2DatastoreTest.h2Datastore.getServiceKeyLastModifiedTime("Service", "ServiceKey").getTime();
        H2DatastoreTest.h2Datastore.deleteKey("Service", "ServiceKey", "foo");
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.listKeys("Service", "ServiceKey"), CoreMatchers.hasItems("key1"));
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.getValue("Service", "ServiceKey", "foo"), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.getServiceKeyLastModifiedTime("Service", "ServiceKey").getTime(), Matchers.greaterThan(lastModified));
    }

    @Test
    public void tet_serviceKeyStore_multipleServices() throws DatastoreException {
        H2DatastoreTest.h2Datastore.setValue("Service1", "ServiceKey1", "key1", "value1");
        H2DatastoreTest.h2Datastore.setValue("Service1", "ServiceKey2", "key1", "value2");
        H2DatastoreTest.h2Datastore.setValue("Service1", "ServiceKey3", "key1", "value3");
        H2DatastoreTest.h2Datastore.setValue("Service2", "ServiceKey1", "key1", "value4");
        H2DatastoreTest.h2Datastore.setValue("Service2", "ServiceKey1", "key2", "value5");
        H2DatastoreTest.h2Datastore.setValue("Service2", "ServiceKey1", "key3", "value6");
        H2DatastoreTest.h2Datastore.setValue("Service2", "ServiceKey1", "key4", "value7");
        H2DatastoreTest.h2Datastore.setValue("Service3", "ServiceKey1", "foo", "value8");
        H2DatastoreTest.h2Datastore.setValue("Service3", "ServiceKey1", "bar", "value9");
        // Test listKeys
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.listKeys("Service1", "ServiceKey1"), CoreMatchers.hasItems("key1"));
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.listKeys("Service1", "ServiceKey2"), CoreMatchers.hasItems("key1"));
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.listKeys("Service1", "ServiceKey3"), CoreMatchers.hasItems("key1"));
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.listKeys("Service2", "ServiceKey1"), CoreMatchers.hasItems("key1", "key2", "key3", "key4"));
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.listKeys("Service3", "ServiceKey1"), CoreMatchers.hasItems("bar", "foo"));
        // Test listServiceKeys
        MatcherAssert.assertThat(H2DatastoreTest.h2Datastore.listServiceKeys("Service1"), CoreMatchers.hasItems("ServiceKey1", "ServiceKey2", "ServiceKey3"));
        // Test get
        assertServiceKeyValue("Service1", "ServiceKey1", "key1", "value1");
        assertServiceKeyValue("Service1", "ServiceKey2", "key1", "value2");
        assertServiceKeyValue("Service1", "ServiceKey3", "key1", "value3");
        assertServiceKeyValue("Service2", "ServiceKey1", "key1", "value4");
        assertServiceKeyValue("Service2", "ServiceKey1", "key2", "value5");
        assertServiceKeyValue("Service2", "ServiceKey1", "key3", "value6");
        assertServiceKeyValue("Service2", "ServiceKey1", "key4", "value7");
        assertServiceKeyValue("Service3", "ServiceKey1", "foo", "value8");
        assertServiceKeyValue("Service3", "ServiceKey1", "bar", "value9");
    }
}

