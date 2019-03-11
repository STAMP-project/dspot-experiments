/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.it.metric;


import com.codahale.metrics.MetricRegistry;
import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.ClientFactoryBuilder;
import com.linecorp.armeria.common.metric.DropwizardMeterRegistries;
import com.linecorp.armeria.common.metric.MeterIdPrefixFunction;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.metric.MetricCollectingService;
import com.linecorp.armeria.server.thrift.THttpService;
import com.linecorp.armeria.service.test.thrift.main.HelloService.Iface;
import com.linecorp.armeria.testing.server.ServerRule;
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry;
import java.util.concurrent.TimeUnit;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;


public class DropwizardMetricsIntegrationTest {
    private static final DropwizardMeterRegistry registry = DropwizardMeterRegistries.newRegistry();

    private static final MetricRegistry dropwizardRegistry = DropwizardMetricsIntegrationTest.registry.getDropwizardRegistry();

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.meterRegistry(DropwizardMetricsIntegrationTest.registry);
            sb.service("/helloservice", THttpService.of(((Iface) (( name) -> {
                if ("world".equals(name)) {
                    return "success";
                }
                throw new IllegalArgumentException("bad argument");
            }))).decorate(MetricCollectingService.newDecorator(MeterIdPrefixFunction.ofDefault("armeria.server.HelloService"))));
        }
    };

    private static final ClientFactory clientFactory = new ClientFactoryBuilder().meterRegistry(DropwizardMetricsIntegrationTest.registry).build();

    @Rule
    public final TestRule globalTimeout = new DisableOnDebug(new Timeout(30, TimeUnit.SECONDS));

    @Test
    public void normal() throws Exception {
        DropwizardMetricsIntegrationTest.makeRequest("world");
        DropwizardMetricsIntegrationTest.makeRequest("world");
        DropwizardMetricsIntegrationTest.makeRequest("space");
        DropwizardMetricsIntegrationTest.makeRequest("world");
        DropwizardMetricsIntegrationTest.makeRequest("space");
        DropwizardMetricsIntegrationTest.makeRequest("space");
        DropwizardMetricsIntegrationTest.makeRequest("world");
        await().untilAsserted(() -> {
            assertThat(dropwizardRegistry.getMeters().get(clientMetricNameWithStatusAndResult("requests", 200, "failure")).getCount()).isEqualTo(3L);
            assertThat(dropwizardRegistry.getMeters().get(serverMetricNameWithStatusAndResult("requests", 200, "failure")).getCount()).isEqualTo(3L);
            assertThat(dropwizardRegistry.getMeters().get(clientMetricNameWithStatusAndResult("requests", 200, "success")).getCount()).isEqualTo(4L);
            assertThat(dropwizardRegistry.getMeters().get(serverMetricNameWithStatusAndResult("requests", 200, "success")).getCount()).isEqualTo(4L);
            assertTimer("requestDuration", 7);
            assertHistogram("requestLength", 7);
            assertTimer("responseDuration", 7);
            assertHistogram("responseLength", 7);
            assertTimer("totalDuration", 7);
        });
    }
}

