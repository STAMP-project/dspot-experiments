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
package org.apache.druid.java.util.metrics;


import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.util.List;
import org.apache.druid.java.util.emitter.core.Event;
import org.apache.druid.java.util.metrics.cgroups.CgroupDiscoverer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class CgroupMemoryMonitorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File procDir;

    private File cgroupDir;

    private CgroupDiscoverer discoverer;

    @Test
    public void testMonitor() {
        final CgroupMemoryMonitor monitor = new CgroupMemoryMonitor(discoverer, ImmutableMap.of(), "some_feed");
        final StubServiceEmitter emitter = new StubServiceEmitter("service", "host");
        Assert.assertTrue(monitor.doMonitor(emitter));
        final List<Event> actualEvents = emitter.getEvents();
        Assert.assertEquals(44, actualEvents.size());
    }
}

