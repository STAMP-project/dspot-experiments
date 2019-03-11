/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.federation.store.driver;


import java.io.IOException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.junit.Test;


/**
 * Test the ZooKeeper implementation of the State Store driver.
 */
public class TestStateStoreZK extends TestStateStoreDriverBase {
    private static TestingServer curatorTestingServer;

    private static CuratorFramework curatorFramework;

    private static String baseZNode;

    @Test
    public void testGetNullRecord() throws Exception {
        testGetNullRecord(getStateStoreDriver());
    }

    @Test
    public void testInsert() throws IOException, IllegalAccessException, IllegalArgumentException {
        testInsert(getStateStoreDriver());
    }

    @Test
    public void testUpdate() throws IOException, IllegalArgumentException, ReflectiveOperationException, SecurityException {
        testPut(getStateStoreDriver());
    }

    @Test
    public void testDelete() throws IOException, IllegalAccessException, IllegalArgumentException {
        testRemove(getStateStoreDriver());
    }

    @Test
    public void testFetchErrors() throws IOException, IllegalAccessException, IllegalArgumentException {
        testFetchErrors(getStateStoreDriver());
    }
}

