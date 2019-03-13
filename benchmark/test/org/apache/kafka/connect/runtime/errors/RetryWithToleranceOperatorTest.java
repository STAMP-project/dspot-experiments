/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.runtime.errors;


import Stage.HEADER_CONVERTER;
import Stage.KAFKA_CONSUME;
import Stage.KAFKA_PRODUCE;
import Stage.KEY_CONVERTER;
import Stage.TASK_POLL;
import Stage.TASK_PUT;
import Stage.TRANSFORMATION;
import Stage.VALUE_CONVERTER;
import ToleranceType.NONE;
import java.util.Collections;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.runtime.ConnectorConfig;
import org.apache.kafka.connect.runtime.isolation.Plugins;
import org.easymock.Mock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ ProcessingContext.class })
@PowerMockIgnore("javax.management.*")
public class RetryWithToleranceOperatorTest {
    public static final RetryWithToleranceOperator NOOP_OPERATOR = new RetryWithToleranceOperator(ConnectorConfig.ERRORS_RETRY_TIMEOUT_DEFAULT, ConnectorConfig.ERRORS_RETRY_MAX_DELAY_DEFAULT, ToleranceType.NONE, SYSTEM);

    static {
        RetryWithToleranceOperatorTest.NOOP_OPERATOR.metrics(new ErrorHandlingMetrics());
    }

    @SuppressWarnings("unused")
    @Mock
    private Operation<String> mockOperation;

    @Mock
    ErrorHandlingMetrics errorHandlingMetrics;

    @Mock
    Plugins plugins;

    @Test
    public void testHandleExceptionInTransformations() {
        testHandleExceptionInStage(TRANSFORMATION, new Exception());
    }

    @Test
    public void testHandleExceptionInHeaderConverter() {
        testHandleExceptionInStage(HEADER_CONVERTER, new Exception());
    }

    @Test
    public void testHandleExceptionInValueConverter() {
        testHandleExceptionInStage(VALUE_CONVERTER, new Exception());
    }

    @Test
    public void testHandleExceptionInKeyConverter() {
        testHandleExceptionInStage(KEY_CONVERTER, new Exception());
    }

    @Test
    public void testHandleExceptionInTaskPut() {
        testHandleExceptionInStage(TASK_PUT, new RetriableException("Test"));
    }

    @Test
    public void testHandleExceptionInTaskPoll() {
        testHandleExceptionInStage(TASK_POLL, new RetriableException("Test"));
    }

    @Test(expected = ConnectException.class)
    public void testThrowExceptionInTaskPut() {
        testHandleExceptionInStage(TASK_PUT, new Exception());
    }

    @Test(expected = ConnectException.class)
    public void testThrowExceptionInTaskPoll() {
        testHandleExceptionInStage(TASK_POLL, new Exception());
    }

    @Test(expected = ConnectException.class)
    public void testThrowExceptionInKafkaConsume() {
        testHandleExceptionInStage(KAFKA_CONSUME, new Exception());
    }

    @Test(expected = ConnectException.class)
    public void testThrowExceptionInKafkaProduce() {
        testHandleExceptionInStage(KAFKA_PRODUCE, new Exception());
    }

    @Test
    public void testExecAndHandleRetriableErrorOnce() throws Exception {
        execAndHandleRetriableError(1, 300, new RetriableException("Test"));
    }

    @Test
    public void testExecAndHandleRetriableErrorThrice() throws Exception {
        execAndHandleRetriableError(3, 2100, new RetriableException("Test"));
    }

    @Test
    public void testExecAndHandleNonRetriableErrorOnce() throws Exception {
        execAndHandleNonRetriableError(1, 0, new Exception("Non Retriable Test"));
    }

    @Test
    public void testExecAndHandleNonRetriableErrorThrice() throws Exception {
        execAndHandleNonRetriableError(3, 0, new Exception("Non Retriable Test"));
    }

    @Test
    public void testCheckRetryLimit() {
        MockTime time = new MockTime(0, 0, 0);
        RetryWithToleranceOperator retryWithToleranceOperator = new RetryWithToleranceOperator(500, 100, ToleranceType.NONE, time);
        time.setCurrentTimeMs(100);
        Assert.assertTrue(retryWithToleranceOperator.checkRetry(0));
        time.setCurrentTimeMs(200);
        Assert.assertTrue(retryWithToleranceOperator.checkRetry(0));
        time.setCurrentTimeMs(400);
        Assert.assertTrue(retryWithToleranceOperator.checkRetry(0));
        time.setCurrentTimeMs(499);
        Assert.assertTrue(retryWithToleranceOperator.checkRetry(0));
        time.setCurrentTimeMs(501);
        Assert.assertFalse(retryWithToleranceOperator.checkRetry(0));
        time.setCurrentTimeMs(600);
        Assert.assertFalse(retryWithToleranceOperator.checkRetry(0));
    }

    @Test
    public void testBackoffLimit() {
        MockTime time = new MockTime(0, 0, 0);
        RetryWithToleranceOperator retryWithToleranceOperator = new RetryWithToleranceOperator(5, 5000, ToleranceType.NONE, time);
        long prevTs = time.hiResClockMs();
        retryWithToleranceOperator.backoff(1, 5000);
        Assert.assertEquals(300, ((time.hiResClockMs()) - prevTs));
        prevTs = time.hiResClockMs();
        retryWithToleranceOperator.backoff(2, 5000);
        Assert.assertEquals(600, ((time.hiResClockMs()) - prevTs));
        prevTs = time.hiResClockMs();
        retryWithToleranceOperator.backoff(3, 5000);
        Assert.assertEquals(1200, ((time.hiResClockMs()) - prevTs));
        prevTs = time.hiResClockMs();
        retryWithToleranceOperator.backoff(4, 5000);
        Assert.assertEquals(2400, ((time.hiResClockMs()) - prevTs));
        prevTs = time.hiResClockMs();
        retryWithToleranceOperator.backoff(5, 5000);
        Assert.assertEquals(500, ((time.hiResClockMs()) - prevTs));
        prevTs = time.hiResClockMs();
        retryWithToleranceOperator.backoff(6, 5000);
        Assert.assertEquals(0, ((time.hiResClockMs()) - prevTs));
        PowerMock.verifyAll();
    }

    @Test
    public void testToleranceLimit() {
        RetryWithToleranceOperator retryWithToleranceOperator = new RetryWithToleranceOperator(ConnectorConfig.ERRORS_RETRY_TIMEOUT_DEFAULT, ConnectorConfig.ERRORS_RETRY_MAX_DELAY_DEFAULT, ToleranceType.NONE, SYSTEM);
        retryWithToleranceOperator.metrics(errorHandlingMetrics);
        retryWithToleranceOperator.markAsFailed();
        Assert.assertFalse("should not tolerate any errors", retryWithToleranceOperator.withinToleranceLimits());
        retryWithToleranceOperator = new RetryWithToleranceOperator(ConnectorConfig.ERRORS_RETRY_TIMEOUT_DEFAULT, ConnectorConfig.ERRORS_RETRY_MAX_DELAY_DEFAULT, ToleranceType.ALL, SYSTEM);
        retryWithToleranceOperator.metrics(errorHandlingMetrics);
        retryWithToleranceOperator.markAsFailed();
        retryWithToleranceOperator.markAsFailed();
        Assert.assertTrue("should tolerate all errors", retryWithToleranceOperator.withinToleranceLimits());
        retryWithToleranceOperator = new RetryWithToleranceOperator(ConnectorConfig.ERRORS_RETRY_TIMEOUT_DEFAULT, ConnectorConfig.ERRORS_RETRY_MAX_DELAY_DEFAULT, ToleranceType.NONE, SYSTEM);
        Assert.assertTrue("no tolerance is within limits if no failures", retryWithToleranceOperator.withinToleranceLimits());
    }

    @Test
    public void testDefaultConfigs() {
        ConnectorConfig configuration = config(Collections.emptyMap());
        Assert.assertEquals(configuration.errorRetryTimeout(), ConnectorConfig.ERRORS_RETRY_TIMEOUT_DEFAULT);
        Assert.assertEquals(configuration.errorMaxDelayInMillis(), ConnectorConfig.ERRORS_RETRY_MAX_DELAY_DEFAULT);
        Assert.assertEquals(configuration.errorToleranceType(), ConnectorConfig.ERRORS_TOLERANCE_DEFAULT);
        PowerMock.verifyAll();
    }

    @Test
    public void testSetConfigs() {
        ConnectorConfig configuration;
        configuration = config(Collections.singletonMap(ConnectorConfig.ERRORS_RETRY_TIMEOUT_CONFIG, "100"));
        Assert.assertEquals(configuration.errorRetryTimeout(), 100);
        configuration = config(Collections.singletonMap(ConnectorConfig.ERRORS_RETRY_MAX_DELAY_CONFIG, "100"));
        Assert.assertEquals(configuration.errorMaxDelayInMillis(), 100);
        configuration = config(Collections.singletonMap(ConnectorConfig.ERRORS_TOLERANCE_CONFIG, "none"));
        Assert.assertEquals(configuration.errorToleranceType(), NONE);
        PowerMock.verifyAll();
    }

    private static class ExceptionThrower implements Operation<Object> {
        private Exception e;

        public ExceptionThrower(Exception e) {
            this.e = e;
        }

        @Override
        public Object call() throws Exception {
            throw e;
        }
    }
}

