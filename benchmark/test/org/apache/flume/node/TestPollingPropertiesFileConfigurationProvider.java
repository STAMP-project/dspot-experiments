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
package org.apache.flume.node;


import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.File;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class TestPollingPropertiesFileConfigurationProvider {
    private static final File TESTFILE = new File(TestPollingPropertiesFileConfigurationProvider.class.getClassLoader().getResource("flume-conf.properties").getFile());

    private PollingPropertiesFileConfigurationProvider provider;

    private File baseDir;

    private File configFile;

    private EventBus eventBus;

    @Test
    public void testPolling() throws Exception {
        // let first event fire
        Thread.sleep(2000L);
        final List<MaterializedConfiguration> events = Lists.newArrayList();
        Object eventHandler = new Object() {
            @Subscribe
            public synchronized void handleConfigurationEvent(MaterializedConfiguration event) {
                events.add(event);
            }
        };
        eventBus.register(eventHandler);
        configFile.setLastModified(System.currentTimeMillis());
        // now wait for second event to fire
        Thread.sleep(2000L);
        Assert.assertEquals(String.valueOf(events), 1, events.size());
        MaterializedConfiguration materializedConfiguration = events.remove(0);
        Assert.assertEquals(1, materializedConfiguration.getSourceRunners().size());
        Assert.assertEquals(1, materializedConfiguration.getSinkRunners().size());
        Assert.assertEquals(1, materializedConfiguration.getChannels().size());
    }
}

