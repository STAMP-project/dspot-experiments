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
package org.apache.hadoop.hdfs.server.federation.router;


import java.util.LinkedList;
import java.util.List;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.server.federation.MiniRouterDFSCluster;
import org.apache.hadoop.hdfs.server.federation.StateStoreDFSCluster;
import org.junit.Test;


/**
 * Tests the use of the resolvers that write in all subclusters from the
 * Router. It supports:
 * <li>HashResolver
 * <li>RandomResolver.
 */
public class TestRouterAllResolver {
    /**
     * Directory that will be in a HASH_ALL mount point.
     */
    private static final String TEST_DIR_HASH_ALL = "/hashall";

    /**
     * Directory that will be in a RANDOM mount point.
     */
    private static final String TEST_DIR_RANDOM = "/random";

    /**
     * Directory that will be in a SPACE mount point.
     */
    private static final String TEST_DIR_SPACE = "/space";

    /**
     * Number of namespaces.
     */
    private static final int NUM_NAMESPACES = 2;

    /**
     * Mini HDFS clusters with Routers and State Store.
     */
    private static StateStoreDFSCluster cluster;

    /**
     * Router for testing.
     */
    private static MiniRouterDFSCluster.RouterContext routerContext;

    /**
     * Router/federated filesystem.
     */
    private static FileSystem routerFs;

    /**
     * Filesystem for each namespace.
     */
    private static List<FileSystem> nsFss = new LinkedList<>();

    @Test
    public void testHashAll() throws Exception {
        testAll(TestRouterAllResolver.TEST_DIR_HASH_ALL);
    }

    @Test
    public void testRandomAll() throws Exception {
        testAll(TestRouterAllResolver.TEST_DIR_RANDOM);
    }

    @Test
    public void testSpaceAll() throws Exception {
        testAll(TestRouterAllResolver.TEST_DIR_SPACE);
    }
}

