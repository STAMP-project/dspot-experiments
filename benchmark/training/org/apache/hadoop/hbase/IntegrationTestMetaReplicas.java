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
package org.apache.hadoop.hbase;


import org.apache.hadoop.hbase.client.TestMetaWithReplicas;
import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An integration test that starts the cluster with three replicas for the meta
 * It then creates a table, flushes the meta, kills the server holding the primary.
 * After that a client issues put/get requests on the created table - the other
 * replicas of the meta would be used to get the location of the region of the created
 * table.
 */
@Category(IntegrationTests.class)
public class IntegrationTestMetaReplicas {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestMetaReplicas.class);

    /**
     * Util to get at the cluster.
     */
    private static IntegrationTestingUtility util;

    @Test
    public void testShutdownHandling() throws Exception {
        // This test creates a table, flushes the meta (with 3 replicas), kills the
        // server holding the primary meta replica. Then it does a put/get into/from
        // the test table. The put/get operations would use the replicas to locate the
        // location of the test table's region
        TestMetaWithReplicas.shutdownMetaAndDoValidations(IntegrationTestMetaReplicas.util);
    }
}

