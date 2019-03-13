/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.datanode;


import DataNode.METRICS_LOG;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.metrics2.util.MBeans;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test periodic logging of DataNode metrics.
 */
public class TestDataNodeMetricsLogger {
    static final Logger LOG = LoggerFactory.getLogger(TestDataNodeMetricsLogger.class);

    private static final String DATA_DIR = (MiniDFSCluster.getBaseDirectory()) + "data";

    private static final InetSocketAddress NN_ADDR = new InetSocketAddress("localhost", 5020);

    private DataNode dn;

    static final Random random = new Random(System.currentTimeMillis());

    @Rule
    public Timeout timeout = new Timeout(300000);

    @Test
    public void testMetricsLoggerOnByDefault() throws IOException {
        startDNForTest(true);
        Assert.assertNotNull(dn);
        Assert.assertNotNull(dn.getMetricsLoggerTimer());
    }

    @Test
    public void testDisableMetricsLogger() throws IOException {
        startDNForTest(false);
        Assert.assertNotNull(dn);
        Assert.assertNull(dn.getMetricsLoggerTimer());
    }

    @Test
    public void testMetricsLoggerIsAsync() throws IOException {
        startDNForTest(true);
        Assert.assertNotNull(dn);
        org.apache.log4j.Logger logger = ((Log4JLogger) (METRICS_LOG)).getLogger();
        @SuppressWarnings("unchecked")
        List<Appender> appenders = Collections.list(logger.getAllAppenders());
        Assert.assertTrue(((appenders.get(0)) instanceof AsyncAppender));
    }

    /**
     * Publish a fake metric under the "Hadoop:" domain and ensure it is logged by
     * the metrics logger.
     */
    @Test
    public void testMetricsLogOutput() throws IOException, InterruptedException, TimeoutException {
        TestDataNodeMetricsLogger.TestFakeMetric metricsProvider = new TestDataNodeMetricsLogger.TestFakeMetric();
        MBeans.register(this.getClass().getSimpleName(), "DummyMetrics", metricsProvider);
        startDNForTest(true);
        Assert.assertNotNull(dn);
        final TestDataNodeMetricsLogger.PatternMatchingAppender appender = new TestDataNodeMetricsLogger.PatternMatchingAppender("^.*FakeMetric.*$");
        addAppender(METRICS_LOG, appender);
        // Ensure that the supplied pattern was matched.
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return appender.isMatched();
            }
        }, 1000, 60000);
        dn.shutdown();
    }

    public interface TestFakeMetricMXBean {
        int getFakeMetric();
    }

    /**
     * MBean for testing
     */
    public static class TestFakeMetric implements TestDataNodeMetricsLogger.TestFakeMetricMXBean {
        @Override
        public int getFakeMetric() {
            return 0;
        }
    }

    /**
     * An appender that matches logged messages against the given regular
     * expression.
     */
    public static class PatternMatchingAppender extends AppenderSkeleton {
        private final Pattern pattern;

        private volatile boolean matched;

        public PatternMatchingAppender(String pattern) {
            this.pattern = Pattern.compile(pattern);
            this.matched = false;
        }

        public boolean isMatched() {
            return matched;
        }

        @Override
        protected void append(LoggingEvent event) {
            if (pattern.matcher(event.getMessage().toString()).matches()) {
                matched = true;
            }
        }

        @Override
        public void close() {
        }

        @Override
        public boolean requiresLayout() {
            return false;
        }
    }
}

