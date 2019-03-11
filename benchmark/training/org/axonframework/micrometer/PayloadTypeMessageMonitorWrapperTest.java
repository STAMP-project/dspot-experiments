/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.micrometer;


import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.lang.reflect.Field;
import java.util.Map;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.common.ReflectionUtils;
import org.axonframework.messaging.Message;
import org.axonframework.monitoring.MessageMonitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PayloadTypeMessageMonitorWrapperTest<T extends MessageMonitor<Message<?>>> {
    private static final CommandMessage<Object> STRING_MESSAGE = PayloadTypeMessageMonitorWrapperTest.asCommandMessage("stringCommand");

    private static final CommandMessage<Object> INTEGER_MESSAGE = PayloadTypeMessageMonitorWrapperTest.asCommandMessage(1);

    private SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

    private PayloadTypeMessageMonitorWrapper<CapacityMonitor> testSubject;

    private Class<CapacityMonitor> expectedMonitorClass;

    private final Appender appender = Mockito.mock(Appender.class);

    private final Logger logger = Logger.getRootLogger();

    @Test
    public void testInstantiateMessageMonitorOfTypeMonitorOnMessageIngested() throws Exception {
        Field payloadTypeMonitorsField = testSubject.getClass().getDeclaredField("payloadTypeMonitors");
        payloadTypeMonitorsField.setAccessible(true);
        String expectedMonitorName = PayloadTypeMessageMonitorWrapperTest.STRING_MESSAGE.getPayloadType().getName();
        testSubject.onMessageIngested(PayloadTypeMessageMonitorWrapperTest.STRING_MESSAGE);
        Map<String, T> payloadTypeMonitors = ReflectionUtils.getFieldValue(payloadTypeMonitorsField, testSubject);
        Assert.assertEquals(1, payloadTypeMonitors.size());
        MessageMonitor<Message<?>> messageMessageMonitor = payloadTypeMonitors.get(expectedMonitorName);
        Assert.assertNotNull(messageMessageMonitor);
        Assert.assertTrue(expectedMonitorClass.isInstance(messageMessageMonitor));
        Assert.assertEquals(1, meterRegistry.find((expectedMonitorName + ".capacity")).meters().size());
    }

    @Test
    public void testInstantiatesOneMessageMonitorPerIngestedPayloadType() throws Exception {
        Field payloadTypeMonitorsField = testSubject.getClass().getDeclaredField("payloadTypeMonitors");
        payloadTypeMonitorsField.setAccessible(true);
        String expectedStringMonitorName = PayloadTypeMessageMonitorWrapperTest.STRING_MESSAGE.getPayloadType().getName();
        String expectedIntegerMonitorName = PayloadTypeMessageMonitorWrapperTest.INTEGER_MESSAGE.getPayloadType().getName();
        testSubject.onMessageIngested(PayloadTypeMessageMonitorWrapperTest.STRING_MESSAGE);// First unique payload type

        testSubject.onMessageIngested(PayloadTypeMessageMonitorWrapperTest.STRING_MESSAGE);
        testSubject.onMessageIngested(PayloadTypeMessageMonitorWrapperTest.INTEGER_MESSAGE);// Second unique payload type

        Map<String, T> payloadTypeMonitors = ReflectionUtils.getFieldValue(payloadTypeMonitorsField, testSubject);
        Assert.assertEquals(2, payloadTypeMonitors.size());
        MessageMonitor<Message<?>> messageMessageMonitor = payloadTypeMonitors.get(expectedStringMonitorName);
        Assert.assertNotNull(messageMessageMonitor);
        Assert.assertTrue(expectedMonitorClass.isInstance(messageMessageMonitor));
        messageMessageMonitor = payloadTypeMonitors.get(expectedIntegerMonitorName);
        Assert.assertNotNull(messageMessageMonitor);
        Assert.assertTrue(expectedMonitorClass.isInstance(messageMessageMonitor));
        Assert.assertEquals(1, meterRegistry.find((expectedStringMonitorName + ".capacity")).meters().size());
        Assert.assertEquals(1, meterRegistry.find((expectedIntegerMonitorName + ".capacity")).meters().size());
    }

    @Test
    public void testMonitorNameFollowsGivenMonitorNameBuilderSpecifics() {
        String testPrefix = "additional-monitor-name.";
        PayloadTypeMessageMonitorWrapper<CapacityMonitor> testSubject = new PayloadTypeMessageMonitorWrapper(( name) -> CapacityMonitor.buildMonitor(name, meterRegistry), ( payloadType) -> testPrefix + (payloadType.getName()));
        String expectedMonitorName = testPrefix + (PayloadTypeMessageMonitorWrapperTest.STRING_MESSAGE.getPayloadType().getName());
        testSubject.onMessageIngested(PayloadTypeMessageMonitorWrapperTest.STRING_MESSAGE);
        Assert.assertEquals(1, meterRegistry.find((expectedMonitorName + ".capacity")).meters().size());
    }
}

