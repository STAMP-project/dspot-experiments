/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.models.sessions.infinispan.initializer;


import java.io.Serializable;
import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.logging.Logger;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Reproducer for RHSSO-377 / ISPN-6806
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Ignore
public class L1SerializationIssueTest {
    protected static final Logger logger = Logger.getLogger(L1SerializationIssueTest.class);

    // Conf
    private static final boolean L1_ENABLED = true;// Workaround 1 : If it's disabled, the exception won't happen


    private static final int L1_LIFESPAN_MS = 6000;

    private static final int L1_CLEANER_MS = 60000;// Seems that this value doesn't matter to anything...


    private static final int NUM_OWNERS = 1;// Workaround 2 : If it's 2, the exception won't happen


    private static final int TIME_BEFORE_RESTORE_NODE = 5000;// Workaroud 3 : If it's bigger than L1_LIFESPAN_MS, the exception won't happen


    private static final int ITEMS_COUNT = 1000;

    private static final String CACHE_NAME = "my-dist";

    @Test
    public void testL1Bug() throws Exception {
        EmbeddedCacheManager node1 = null;
        EmbeddedCacheManager node2 = null;
        EmbeddedCacheManager node3 = null;
        try {
            node1 = createManager();
            Cache<String, Object> node1Cache = node1.getCache(L1SerializationIssueTest.CACHE_NAME);
            L1SerializationIssueTest.logger.info("Node1Cache started");
            node2 = createManager();
            Cache<String, Object> node2Cache = node2.getCache(L1SerializationIssueTest.CACHE_NAME);
            L1SerializationIssueTest.logger.info("Node2Cache started");
            node3 = createManager();
            Cache<String, Object> node3Cache = node3.getCache(L1SerializationIssueTest.CACHE_NAME);
            L1SerializationIssueTest.logger.info("Node3Cache started");
            // Put some items on node1
            writeItems(node1Cache);
            // Get all items on node2. This will save them to L1 cache on node2
            readItems(node2Cache);
            // See the great performance with L1 enabled as everything read from L1 cache and there are no remote calls at all
            // readItems(node2Cache);
            // Write and see worse performance with L1 enabled as every write needs to invalidate L1 cache on node2 too
            // writeItems(node1Cache);
            // Kill node3
            node3.stop();
            // Wait
            L1SerializationIssueTest.logger.infof("Stopped node3. Will wait %d milliseconds", L1SerializationIssueTest.TIME_BEFORE_RESTORE_NODE);
            Thread.sleep(L1SerializationIssueTest.TIME_BEFORE_RESTORE_NODE);
            // Start node3 again
            L1SerializationIssueTest.logger.info("Going to start node3 again");
            node3 = createManager();
            node3Cache = node3.getCache(L1SerializationIssueTest.CACHE_NAME);
            L1SerializationIssueTest.logger.info("Node3Cache started again");
            L1SerializationIssueTest.logger.info("Test finished successfuly");
        } finally {
            if (node1 != null)
                node1.stop();

            if (node2 != null)
                node2.stop();

            if (node3 != null)
                node3.stop();

        }
    }

    public static class MySerializable implements Serializable {}
}

