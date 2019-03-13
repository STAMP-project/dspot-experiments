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
package org.apache.ambari.server.state.alert;


import MetricSource.JmxInfo;
import java.util.Arrays;
import java.util.Optional;
import org.apache.ambari.server.controller.jmx.JMXMetricHolder;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class JmxInfoTest {
    private static final String JMX_PROP_NAME1 = "Hadoop:service=NameNode,name=FSNamesystem/CapacityUsed";

    private static final String JMX_PROP_NAME2 = "Hadoop:service=NameNode,name=FSNamesystem/CapacityRemaining";

    @Test
    public void testFindJmxMetricsAndCalculateSimpleValue() throws Exception {
        MetricSource.JmxInfo jmxInfo = jmxInfoWith("{1}");
        JMXMetricHolder metrics = metrics(12.5, 3.5);
        Assert.assertThat(jmxInfo.eval(metrics), Is.is(Optional.of(3.5)));
    }

    @Test
    public void testFindJmxMetricsAndCalculateComplexValue() throws Exception {
        MetricSource.JmxInfo jmxInfo = jmxInfoWith("2 * ({0} + {1})");
        JMXMetricHolder metrics = metrics(12.5, 2.5);
        Assert.assertThat(jmxInfo.eval(metrics), Is.is(Optional.of(30.0)));
    }

    @Test
    public void testReturnsEmptyWhenJmxPropertyWasNotFound() throws Exception {
        MetricSource.JmxInfo jmxInfo = new MetricSource.JmxInfo();
        jmxInfo.setPropertyList(Arrays.asList("notfound/notfound"));
        JMXMetricHolder metrics = metrics(1, 2);
        Assert.assertThat(jmxInfo.eval(metrics), Is.is(Optional.empty()));
    }
}

