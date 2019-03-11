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


import java.util.List;
import org.apache.ignite.internal.client.GridClient;
import org.apache.ignite.internal.client.GridClientNode;
import org.apache.ignite.internal.client.integration.ClientAbstractSelfTest;
import org.apache.ignite.internal.client.router.impl.GridTcpRouterImpl;
import org.apache.ignite.internal.util.typedef.F;
import org.junit.Test;


/**
 * Abstract base class for http routing tests.
 */
public abstract class TcpRouterAbstractSelfTest extends ClientAbstractSelfTest {
    /**
     * Port number to use by router.
     */
    private static final int ROUTER_PORT = (ClientAbstractSelfTest.BINARY_PORT) + 1;

    /**
     * TCP router instance.
     */
    private static GridTcpRouterImpl router;

    /**
     * Send count.
     */
    private static long sndCnt;

    /**
     * Receive count.
     */
    private static long rcvCnt;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    @Override
    public void testConnectable() throws Exception {
        GridClient client = client();
        List<GridClientNode> nodes = client.compute().refreshTopology(false, false);
        assertFalse(F.first(nodes).connectable());
    }
}

