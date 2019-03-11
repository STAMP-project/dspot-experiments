/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.rest;


import AlluxioJobMasterRestServiceHandler.GET_INFO;
import alluxio.master.LocalAlluxioJobCluster;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.ws.rs.HttpMethod;
import org.junit.Test;


/**
 * Tests for {@link AlluxioJobMasterRestServiceHandler}.
 */
public final class JobMasterRestApiTest extends RestApiTest {
    private static final Map<String, String> NO_PARAMS = Maps.newHashMap();

    private LocalAlluxioJobCluster mJobCluster;

    @Test
    public void getInfo() throws Exception {
        new TestCase(mHostname, mPort, getEndpoint(GET_INFO), JobMasterRestApiTest.NO_PARAMS, HttpMethod.GET, null).call();
    }
}

