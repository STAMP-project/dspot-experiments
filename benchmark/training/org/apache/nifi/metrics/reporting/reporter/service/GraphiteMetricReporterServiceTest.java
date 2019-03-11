/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.metrics.reporting.reporter.service;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.graphite.GraphiteSender;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Test class for {@link GraphiteMetricReporterService}.
 *
 * @author Omer Hadari
 */
@RunWith(MockitoJUnitRunner.class)
public class GraphiteMetricReporterServiceTest {
    /**
     * Service identifier for registerting the tested service to the tests runner.
     */
    private static final String SERVICE_IDENTIFIER = "graphite-metric-reporter-service";

    /**
     * Sample host name for the {@link GraphiteMetricReporterService#HOST} property.
     */
    private static final String TEST_HOST = "some-host";

    /**
     * Sample port for the {@link GraphiteMetricReporterService#PORT} property.
     */
    private static final int TEST_PORT = 12345;

    /**
     * Sample charset for the {@link GraphiteMetricReporterService#CHARSET} property.
     */
    private static final Charset TEST_CHARSET = StandardCharsets.UTF_16LE;

    /**
     * Sample prefix for metric names.
     */
    private static final String METRIC_NAMES_PREFIX = "test-metric-name-prefix";

    /**
     * Sample metric for verifying that a graphite sender with the correct configuration is used.
     */
    private static final String TEST_METRIC_NAME = "test-metric";

    /**
     * The fixed value of {@link #TEST_METRIC_NAME}.
     */
    private static final int TEST_METRIC_VALUE = 2;

    /**
     * Dummy processor for creating {@link #runner}.
     */
    @Mock
    private Processor processorDummy;

    /**
     * Mock sender for verifying creation with the correct configuration.
     */
    @Mock
    private GraphiteSender graphiteSenderMock;

    /**
     * Stub metric registry, that contains the test metrics.
     */
    private MetricRegistry metricRegistryStub;

    /**
     * Test runner for activating and configuring the service.
     */
    private TestRunner runner;

    /**
     * The test subject.
     */
    private GraphiteMetricReporterService testedService;

    /**
     * Make sure that a correctly configured service can be activated.
     */
    @Test
    public void testGraphiteMetricReporterSanityConfiguration() throws Exception {
        runner.addControllerService(GraphiteMetricReporterServiceTest.SERVICE_IDENTIFIER, testedService);
        setServiceProperties(GraphiteMetricReporterServiceTest.TEST_HOST, GraphiteMetricReporterServiceTest.TEST_PORT, GraphiteMetricReporterServiceTest.TEST_CHARSET, GraphiteMetricReporterServiceTest.METRIC_NAMES_PREFIX);
        runner.enableControllerService(testedService);
        runner.assertValid(testedService);
    }

    /**
     * Make sure that a correctly configured service provides a reporter for the matching configuration, and
     * actually reports to the correct address.
     */
    @Test
    public void testCreateReporterUsesCorrectSender() throws Exception {
        testedService = new GraphiteMetricReporterServiceTest.TestableGraphiteMetricReporterService();
        runner.addControllerService(GraphiteMetricReporterServiceTest.SERVICE_IDENTIFIER, testedService);
        setServiceProperties(GraphiteMetricReporterServiceTest.TEST_HOST, GraphiteMetricReporterServiceTest.TEST_PORT, GraphiteMetricReporterServiceTest.TEST_CHARSET, GraphiteMetricReporterServiceTest.METRIC_NAMES_PREFIX);
        Mockito.when(graphiteSenderMock.isConnected()).thenReturn(false);
        runner.enableControllerService(testedService);
        ScheduledReporter createdReporter = testedService.createReporter(metricRegistryStub);
        createdReporter.report();
        String expectedMetricName = MetricRegistry.name(GraphiteMetricReporterServiceTest.METRIC_NAMES_PREFIX, GraphiteMetricReporterServiceTest.TEST_METRIC_NAME);
        Mockito.verify(graphiteSenderMock).send(ArgumentMatchers.eq(expectedMetricName), ArgumentMatchers.eq(String.valueOf(GraphiteMetricReporterServiceTest.TEST_METRIC_VALUE)), ArgumentMatchers.anyLong());
    }

    /**
     * Make sure that {@link GraphiteMetricReporterService#shutdown()} closes the connection to graphite.
     */
    @Test
    public void testShutdownClosesSender() throws Exception {
        testedService = new GraphiteMetricReporterServiceTest.TestableGraphiteMetricReporterService();
        runner.addControllerService(GraphiteMetricReporterServiceTest.SERVICE_IDENTIFIER, testedService);
        setServiceProperties(GraphiteMetricReporterServiceTest.TEST_HOST, GraphiteMetricReporterServiceTest.TEST_PORT, GraphiteMetricReporterServiceTest.TEST_CHARSET, GraphiteMetricReporterServiceTest.METRIC_NAMES_PREFIX);
        runner.enableControllerService(testedService);
        runner.disableControllerService(testedService);
        Mockito.verify(graphiteSenderMock).close();
    }

    /**
     * This class is a patch. It overrides {@link GraphiteMetricReporterService#createSender(String, int, Charset)}
     * so that it is possible to verify a correct creation of graphite senders according to property values.
     */
    private class TestableGraphiteMetricReporterService extends GraphiteMetricReporterService {
        /**
         * Overrides the actual methods in order to inject the mock {@link #graphiteSenderMock}.
         * <p>
         * If this method is called with the test property values, it returns the mock. Otherwise operate
         * regularly.
         *
         * @param host
         * 		the provided hostname.
         * @param port
         * 		the provided port.
         * @param charset
         * 		the provided graphite server charset.
         * @return {@link #graphiteSenderMock} if all params were the constant test params, regular result otherwise.
         */
        @Override
        protected GraphiteSender createSender(String host, int port, Charset charset) {
            if (((GraphiteMetricReporterServiceTest.TEST_HOST.equals(host)) && ((GraphiteMetricReporterServiceTest.TEST_PORT) == port)) && (GraphiteMetricReporterServiceTest.TEST_CHARSET.equals(charset))) {
                return graphiteSenderMock;
            }
            return super.createSender(host, port, charset);
        }
    }
}

