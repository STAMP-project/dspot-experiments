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
package org.apache.camel.component.drill;


import org.apache.camel.Endpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import static DrillConnectionMode.ZK;


public class EndpointTest extends CamelTestSupport {
    private static final String HOST = "my.host.me";

    private static final Integer PORT = 4000;

    private static final String DIRECTORY = "directory";

    private static final String CLUSTERID = "clusterId";

    private static final DrillConnectionMode MODE = ZK;

    @Test
    public void testZKJdbcURL() throws Exception {
        Endpoint endpoint = context.getEndpoint(((((((((("drill://" + (EndpointTest.HOST)) + "?port=") + (EndpointTest.PORT)) + "&directory=") + (EndpointTest.DIRECTORY)) + "&clusterId=") + (EndpointTest.CLUSTERID)) + "&mode=") + (EndpointTest.MODE)));
        final String uri = (((((("jdbc:drill:zk=" + (EndpointTest.HOST)) + ":") + (EndpointTest.PORT)) + "/") + (EndpointTest.DIRECTORY)) + "/") + (EndpointTest.CLUSTERID);
        assertTrue((endpoint instanceof DrillEndpoint));
        assertEquals(EndpointTest.HOST, getHost());
        assertEquals(EndpointTest.PORT, getPort());
        assertEquals(EndpointTest.DIRECTORY, getDirectory());
        assertEquals(EndpointTest.CLUSTERID, getClusterId());
        assertEquals(EndpointTest.MODE, getMode());
        assertEquals(uri, toJDBCUri());
    }
}

