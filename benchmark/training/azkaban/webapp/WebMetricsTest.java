/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.webapp;


import azkaban.metrics.MetricsTestUtility;
import org.junit.Assert;
import org.junit.Test;


public class WebMetricsTest {
    private MetricsTestUtility testUtil;

    private WebMetrics metrics;

    @Test
    public void testLogFetchLatencyMetrics() {
        this.metrics.setFetchLogLatency(14);
        Assert.assertEquals(14, this.testUtil.getGaugeValue("fetchLogLatency"));
    }
}

