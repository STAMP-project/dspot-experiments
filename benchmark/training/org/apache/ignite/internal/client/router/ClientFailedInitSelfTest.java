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


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.apache.ignite.internal.client.GridClientConfiguration;
import org.apache.ignite.internal.client.GridClientException;
import org.apache.ignite.internal.client.GridClientFactory;
import org.apache.ignite.internal.client.GridClientProtocol;
import org.apache.ignite.internal.client.integration.ClientAbstractSelfTest;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class ClientFailedInitSelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int RECONN_CNT = 3;

    /**
     *
     */
    private static final long TOP_REFRESH_PERIOD = 5000;

    /**
     *
     */
    private static final int ROUTER_BINARY_PORT = (ClientAbstractSelfTest.BINARY_PORT) + 1;

    /**
     *
     */
    private static final int ROUTER_JETTY_PORT = 8081;

    /**
     *
     */
    @Test
    public void testEmptyAddresses() {
        try {
            GridClientFactory.start(new GridClientConfiguration());
            assert false;
        } catch (GridClientException e) {
            info(("Caught expected exception: " + e));
        }
    }

    /**
     *
     */
    @Test
    public void testRoutersAndServersAddressesProvided() {
        try {
            GridClientConfiguration c = new GridClientConfiguration();
            c.setRouters(Collections.singleton("127.0.0.1:10000"));
            c.setServers(Collections.singleton("127.0.0.1:10000"));
            GridClientFactory.start(c);
            assert false;
        } catch (GridClientException e) {
            info(("Caught expected exception: " + e));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTcpClient() throws Exception {
        doTestClient(GridClientProtocol.TCP);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTcpRouter() throws Exception {
        doTestRouter(GridClientProtocol.TCP);
    }

    /**
     * Test task.
     */
    private static class TestTask extends ComputeTaskSplitAdapter<String, String> {
        /**
         * {@inheritDoc }
         */
        @Override
        protected Collection<? extends ComputeJob> split(int gridSize, final String arg) {
            return Collections.singleton(new ComputeJobAdapter() {
                @Override
                public String execute() {
                    return arg;
                }
            });
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String reduce(List<ComputeJobResult> results) {
            assertEquals(1, results.size());
            return results.get(0).getData();
        }
    }
}

