/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.it.metric;


import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.ClientFactoryBuilder;
import com.linecorp.armeria.common.logging.RequestLog;
import com.linecorp.armeria.common.metric.MeterIdPrefix;
import com.linecorp.armeria.common.metric.MeterIdPrefixFunction;
import com.linecorp.armeria.common.metric.PrometheusMeterRegistries;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.metric.MetricCollectingService;
import com.linecorp.armeria.server.thrift.THttpService;
import com.linecorp.armeria.service.test.thrift.main.HelloService.Iface;
import com.linecorp.armeria.testing.server.ServerRule;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import java.util.concurrent.TimeUnit;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PrometheusMetricsIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(PrometheusMetricsIntegrationTest.class);

    private static final PrometheusMeterRegistry registry = PrometheusMeterRegistries.newRegistry();

    private static final CollectorRegistry prometheusRegistry = PrometheusMetricsIntegrationTest.registry.getPrometheusRegistry();

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.meterRegistry(PrometheusMetricsIntegrationTest.registry);
            final THttpService helloService = THttpService.of(((Iface) (( name) -> {
                if ("world".equals(name)) {
                    return "success";
                }
                throw new IllegalArgumentException("bad argument");
            })));
            sb.service("/foo", helloService.decorate(MetricCollectingService.newDecorator(new PrometheusMetricsIntegrationTest.MeterIdPrefixFunctionImpl("server", "Foo"))));
            sb.service("/bar", helloService.decorate(MetricCollectingService.newDecorator(new PrometheusMetricsIntegrationTest.MeterIdPrefixFunctionImpl("server", "Bar"))));
            sb.service("/internal/prometheus/metrics", new com.linecorp.armeria.server.metric.PrometheusExpositionService(PrometheusMetricsIntegrationTest.prometheusRegistry));
        }
    };

    private static final ClientFactory clientFactory = new ClientFactoryBuilder().meterRegistry(PrometheusMetricsIntegrationTest.registry).build();

    @Rule
    public final TestRule globalTimeout = new DisableOnDebug(new Timeout(30, TimeUnit.SECONDS));

    @Test
    public void hello_first_second_endpoint() throws Exception {
        PrometheusMetricsIntegrationTest.hello_first_endpoint();
        PrometheusMetricsIntegrationTest.hello_second_endpoint();
    }

    private static final class MeterIdPrefixFunctionImpl implements MeterIdPrefixFunction {
        private final String name;

        private final String serviceName;

        MeterIdPrefixFunctionImpl(String name, String serviceName) {
            this.name = name;
            this.serviceName = serviceName;
        }

        @Override
        public MeterIdPrefix apply(MeterRegistry registry, RequestLog log) {
            return MeterIdPrefixFunction.ofDefault(name).withTags("handler", serviceName).apply(registry, log);
        }

        @Override
        public MeterIdPrefix activeRequestPrefix(MeterRegistry registry, RequestLog log) {
            return MeterIdPrefixFunction.ofDefault(name).withTags("handler", serviceName).activeRequestPrefix(registry, log);
        }
    }
}

