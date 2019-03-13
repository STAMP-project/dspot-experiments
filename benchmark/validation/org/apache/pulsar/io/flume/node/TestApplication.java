/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pulsar.io.flume.node;


import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.io.Files;
import java.io.File;
import java.util.List;
import java.util.Random;
import org.apache.flume.Channel;
import org.apache.flume.SinkRunner;
import org.apache.flume.SourceRunner;
import org.apache.flume.lifecycle.LifecycleAware;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestApplication {
    private File baseDir;

    @Test
    public void testBasicConfiguration() throws Exception {
        EventBus eventBus = new EventBus("test-event-bus");
        MaterializedConfiguration materializedConfiguration = new SimpleMaterializedConfiguration();
        SourceRunner sourceRunner = mockLifeCycle(SourceRunner.class);
        materializedConfiguration.addSourceRunner("test", sourceRunner);
        SinkRunner sinkRunner = mockLifeCycle(SinkRunner.class);
        materializedConfiguration.addSinkRunner("test", sinkRunner);
        Channel channel = mockLifeCycle(Channel.class);
        materializedConfiguration.addChannel("test", channel);
        ConfigurationProvider configurationProvider = Mockito.mock(ConfigurationProvider.class);
        Mockito.when(configurationProvider.getConfiguration()).thenReturn(materializedConfiguration);
        Application application = new Application();
        eventBus.register(application);
        eventBus.post(materializedConfiguration);
        application.start();
        Thread.sleep(1000L);
        Mockito.verify(sourceRunner).start();
        Mockito.verify(sinkRunner).start();
        Mockito.verify(channel).start();
        application.stop();
        Thread.sleep(1000L);
        Mockito.verify(sourceRunner).stop();
        Mockito.verify(sinkRunner).stop();
        Mockito.verify(channel).stop();
    }

    @Test
    public void testFLUME1854() throws Exception {
        File configFile = new File(baseDir, "flume-conf.properties");
        Files.copy(new File(getClass().getClassLoader().getResource("flume-conf.properties").getFile()), configFile);
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            EventBus eventBus = new EventBus("test-event-bus");
            PollingPropertiesFileConfigurationProvider configurationProvider = new PollingPropertiesFileConfigurationProvider("host1", configFile, eventBus, 1);
            List<LifecycleAware> components = Lists.newArrayList();
            components.add(configurationProvider);
            Application application = new Application(components);
            eventBus.register(application);
            application.start();
            Thread.sleep(random.nextInt(10000));
            application.stop();
        }
    }

    @Test(timeout = 10000L)
    public void testFLUME2786() throws Exception {
        final String agentName = "test";
        final int interval = 1;
        final long intervalMs = 1000L;
        File configFile = new File(baseDir, "flume-conf.properties");
        Files.copy(new File(getClass().getClassLoader().getResource("flume-conf.properties.2786").getFile()), configFile);
        File mockConfigFile = Mockito.spy(configFile);
        Mockito.when(mockConfigFile.lastModified()).then(new Answer<Long>() {
            @Override
            public Long answer(InvocationOnMock invocation) throws Throwable {
                Thread.sleep(intervalMs);
                return System.currentTimeMillis();
            }
        });
        EventBus eventBus = new EventBus((agentName + "-event-bus"));
        PollingPropertiesFileConfigurationProvider configurationProvider = new PollingPropertiesFileConfigurationProvider(agentName, mockConfigFile, eventBus, interval);
        PollingPropertiesFileConfigurationProvider mockConfigurationProvider = Mockito.spy(configurationProvider);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Thread.sleep(intervalMs);
                invocation.callRealMethod();
                return null;
            }
        }).when(mockConfigurationProvider).stop();
        List<LifecycleAware> components = Lists.newArrayList();
        components.add(mockConfigurationProvider);
        Application application = new Application(components);
        eventBus.register(application);
        application.start();
        Thread.sleep(1500L);
        application.stop();
    }
}

