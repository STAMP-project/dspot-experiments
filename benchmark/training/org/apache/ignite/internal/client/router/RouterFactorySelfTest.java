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
package org.apache.ignite.internal.client.router;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.apache.ignite.IgniteSystemProperties;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test routers factory.
 */
public class RouterFactorySelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int GRID_HTTP_PORT = 11087;

    /**
     * Test router's start/stop.
     *
     * @throws Exception
     * 		In case of any exception.
     */
    @Test
    public void testRouterFactory() throws Exception {
        try {
            System.setProperty(IgniteSystemProperties.IGNITE_JETTY_PORT, String.valueOf(RouterFactorySelfTest.GRID_HTTP_PORT));
            try {
                startGrid();
            } finally {
                System.clearProperty(IgniteSystemProperties.IGNITE_JETTY_PORT);
            }
            final int size = 20;
            final Collection<GridTcpRouter> tcpRouters = new ArrayList<>(size);
            final GridTcpRouterConfiguration tcpCfg = new GridTcpRouterConfiguration();
            tcpCfg.setPortRange(size);
            for (int i = 0; i < size; i++)
                tcpRouters.add(GridRouterFactory.startTcpRouter(tcpCfg));

            for (GridTcpRouter tcpRouter : tcpRouters) {
                assertEquals(tcpCfg, tcpRouter.configuration());
                assertEquals(tcpRouter, GridRouterFactory.tcpRouter(tcpRouter.id()));
            }
            assertEquals("Validate all started tcp routers.", new java.util.HashSet(tcpRouters), new java.util.HashSet(GridRouterFactory.allTcpRouters()));
            for (Iterator<GridTcpRouter> it = tcpRouters.iterator(); it.hasNext();) {
                GridTcpRouter tcpRouter = it.next();
                assertEquals("Validate all started tcp routers.", new java.util.HashSet(tcpRouters), new java.util.HashSet(GridRouterFactory.allTcpRouters()));
                it.remove();
                GridRouterFactory.stopTcpRouter(tcpRouter.id());
                assertEquals("Validate all started tcp routers.", new java.util.HashSet(tcpRouters), new java.util.HashSet(GridRouterFactory.allTcpRouters()));
            }
            assertEquals(Collections.<GridTcpRouter>emptyList(), GridRouterFactory.allTcpRouters());
        } finally {
            GridRouterFactory.stopAllRouters();
            stopAllGrids();
        }
    }
}

