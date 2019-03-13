/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.spi.discovery.zk;


import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Sanity test verifying that configuration callback specified via
 * {@link GridTestProperties#IGNITE_CFG_PREPROCESSOR_CLS} really works.
 * <p>
 * This test should be run as part of {@link ZookeeperDiscoverySpiTestSuite2}.
 */
public class ZookeeperDiscoverySuitePreprocessorTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final TcpDiscoveryIpFinder IP_FINDER = new TcpDiscoveryVmIpFinder(true);

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSpiConfigurationIsChanged() throws Exception {
        startGrid(0);
        checkDiscoverySpi(1);
        startGrid(1);
        checkDiscoverySpi(2);
        startGridsMultiThreaded(2, 2);
        checkDiscoverySpi(4);
        startGrid();
        checkDiscoverySpi(5);
    }
}

