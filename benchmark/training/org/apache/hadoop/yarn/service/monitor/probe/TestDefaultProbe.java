/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.service.monitor.probe;


import org.apache.hadoop.yarn.service.component.instance.ComponentInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for default probe.
 */
@RunWith(Parameterized.class)
public class TestDefaultProbe {
    private final DefaultProbe probe;

    public TestDefaultProbe(Probe probe) {
        this.probe = ((DefaultProbe) (probe));
    }

    @Test
    public void testDefaultProbe() {
        // component instance has a good hostname, so probe will eventually succeed
        // whether or not DNS checking is enabled
        ComponentInstance componentInstance = TestDefaultProbe.createMockComponentInstance("example.com");
        TestDefaultProbe.checkPingResults(probe, componentInstance, false);
        // component instance has a bad hostname, so probe will fail when DNS
        // checking is enabled
        componentInstance = TestDefaultProbe.createMockComponentInstance("bad.dns.test");
        TestDefaultProbe.checkPingResults(probe, componentInstance, probe.isDnsCheckEnabled());
    }
}

