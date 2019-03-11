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
package org.apache.flink.metrics.influxdb;


import java.util.HashMap;
import java.util.Map;
import org.apache.flink.runtime.metrics.groups.FrontMetricGroup;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static MeasurementInfoProvider.SCOPE_SEPARATOR;


/**
 * Test for {@link MeasurementInfoProvider}.
 */
public class MeasurementInfoProviderTest extends TestLogger {
    private final MeasurementInfoProvider provider = new MeasurementInfoProvider();

    @Test
    public void simpleTestGetMetricInfo() {
        String logicalScope = "myService.Status.JVM.ClassLoader";
        Map<String, String> variables = new HashMap<>();
        variables.put("<A>", "a");
        variables.put("<B>", "b");
        variables.put("<C>", "c");
        String metricName = "ClassesLoaded";
        FrontMetricGroup metricGroup = Mockito.mock(FrontMetricGroup.class, ( invocation) -> {
            throw new UnsupportedOperationException("unexpected method call");
        });
        Mockito.doReturn(variables).when(metricGroup).getAllVariables();
        Mockito.doReturn(logicalScope).when(metricGroup).getLogicalScope(ArgumentMatchers.any(), ArgumentMatchers.anyChar());
        MeasurementInfo info = provider.getMetricInfo(metricName, metricGroup);
        Assert.assertNotNull(info);
        Assert.assertEquals(String.join(("" + (SCOPE_SEPARATOR)), logicalScope, metricName), info.getName());
        Assert.assertThat(info.getTags(), Matchers.hasEntry("A", "a"));
        Assert.assertThat(info.getTags(), Matchers.hasEntry("B", "b"));
        Assert.assertThat(info.getTags(), Matchers.hasEntry("C", "c"));
        Assert.assertEquals(3, info.getTags().size());
    }
}

