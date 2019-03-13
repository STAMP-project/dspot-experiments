/**
 * Copyright (c) 2010-2019. Axon Framework
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


import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.Message;
import org.axonframework.monitoring.MessageMonitor;
import org.axonframework.monitoring.NoOpMessageMonitor;
import org.junit.Assert;
import org.junit.Test;


public class GlobalMetricRegistryTest {
    private GlobalMetricRegistry subject;

    private MetricRegistry dropWizardRegistry;

    @Test
    public void createEventProcessorMonitor() {
        MessageMonitor<? super EventMessage<?>> monitor1 = subject.registerEventProcessor("test1");
        MessageMonitor<? super EventMessage<?>> monitor2 = subject.registerEventProcessor("test2");
        monitor1.onMessageIngested(asEventMessage("test")).reportSuccess();
        monitor2.onMessageIngested(asEventMessage("test")).reportSuccess();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ConsoleReporter.forRegistry(dropWizardRegistry).outputTo(new PrintStream(out)).build().report();
        String output = new String(out.toByteArray());
        Assert.assertTrue(output.contains("test1"));
        Assert.assertTrue(output.contains("test2"));
    }

    @Test
    public void createEventBusMonitor() {
        MessageMonitor<? super EventMessage<?>> monitor = subject.registerEventBus("eventBus");
        monitor.onMessageIngested(asEventMessage("test")).reportSuccess();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ConsoleReporter.forRegistry(dropWizardRegistry).outputTo(new PrintStream(out)).build().report();
        String output = new String(out.toByteArray());
        Assert.assertTrue(output.contains("eventBus"));
    }

    @Test
    public void createCommandBusMonitor() {
        MessageMonitor<? super CommandMessage<?>> monitor = subject.registerCommandBus("commandBus");
        monitor.onMessageIngested(new org.axonframework.commandhandling.GenericCommandMessage("test")).reportSuccess();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ConsoleReporter.forRegistry(dropWizardRegistry).outputTo(new PrintStream(out)).build().report();
        String output = new String(out.toByteArray());
        Assert.assertTrue(output.contains("commandBus"));
    }

    @Test
    public void createMonitorForUnknownComponent() {
        MessageMonitor<? extends Message<?>> actual = subject.registerComponent(String.class, "test");
        Assert.assertSame(NoOpMessageMonitor.instance(), actual);
    }
}

