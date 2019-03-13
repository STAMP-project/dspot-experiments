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
package org.pache.flink.metrics.tests;


import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.Nullable;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.rest.RestClient;
import org.apache.flink.runtime.rest.RestClientConfiguration;
import org.apache.flink.tests.util.FlinkDistribution;
import org.apache.flink.util.TestLogger;
import org.junit.Rule;
import org.junit.Test;


/**
 * End-to-end test for the availability of metrics.
 */
public class MetricsAvailabilityITCase extends TestLogger {
    private static final String HOST = "localhost";

    private static final int PORT = 8081;

    @Rule
    public final FlinkDistribution dist = new FlinkDistribution();

    @Nullable
    private static ScheduledExecutorService scheduledExecutorService = null;

    @Test
    public void testReporter() throws Exception {
        dist.startFlinkCluster();
        final RestClient restClient = new RestClient(RestClientConfiguration.fromConfiguration(new Configuration()), MetricsAvailabilityITCase.scheduledExecutorService);
        MetricsAvailabilityITCase.checkJobManagerMetricAvailability(restClient);
        final Collection<ResourceID> taskManagerIds = MetricsAvailabilityITCase.getTaskManagerIds(restClient);
        for (final ResourceID taskManagerId : taskManagerIds) {
            MetricsAvailabilityITCase.checkTaskManagerMetricAvailability(restClient, taskManagerId);
        }
    }
}

