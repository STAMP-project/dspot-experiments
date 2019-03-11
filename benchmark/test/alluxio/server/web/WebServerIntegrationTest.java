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
package alluxio.server.web;


import ServiceType.MASTER_WEB;
import ServiceType.WORKER_WEB;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.util.network.NetworkAddressUtils.ServiceType;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests the web server is up when Alluxio starts.
 */
public class WebServerIntegrationTest extends BaseIntegrationTest {
    // Web pages that will be verified.
    private static final Multimap<ServiceType, String> PAGES = new ImmutableListMultimap.Builder<ServiceType, String>().putAll(MASTER_WEB, "/").putAll(WORKER_WEB, "/").build();

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().build();

    /**
     * Tests whether the metrics json is being served.
     */
    @Test
    public void metricsJson() throws Exception {
        for (ServiceType serviceType : WebServerIntegrationTest.PAGES.keys()) {
            verifyMetricsJson(serviceType);
        }
    }

    /**
     * Tests whether the master and worker web homepage is up.
     */
    @Test
    public void serverUp() throws Exception {
        for (Map.Entry<ServiceType, String> entry : WebServerIntegrationTest.PAGES.entries()) {
            verifyWebService(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Tests whether the web resources directory is created.
     */
    @Test
    public void tempDirectoryCreated() {
        Files.isDirectory(Paths.get(mLocalAlluxioClusterResource.get().getAlluxioHome(), "web"));
    }
}

