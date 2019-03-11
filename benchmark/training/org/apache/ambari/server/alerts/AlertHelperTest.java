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
package org.apache.ambari.server.alerts;


import java.util.ArrayList;
import java.util.List;
import org.apache.ambari.server.agent.StaleAlert;
import org.apache.ambari.server.state.alert.AlertHelper;
import org.junit.Assert;
import org.junit.Test;


public class AlertHelperTest {
    @Test
    public void testThresholdCalculations() {
        AlertHelper alertHelper = new AlertHelper();
        Assert.assertEquals(1, alertHelper.getThresholdValue(1, 2));
        Assert.assertEquals(1, alertHelper.getThresholdValue("1", 2));
        Assert.assertEquals(1, alertHelper.getThresholdValue("1.00", 2));
        Assert.assertEquals(1, alertHelper.getThresholdValue("foo", 1));
        Assert.assertEquals(1, alertHelper.getThresholdValue(new Object(), 1));
    }

    @Test
    public void testStaleAlertsOperations() {
        AlertHelper alertHelper = new AlertHelper();
        alertHelper.addStaleAlerts(1L, new ArrayList<StaleAlert>() {
            {
                add(new StaleAlert(1L, 111L));
                add(new StaleAlert(2L, 111L));
                add(new StaleAlert(3L, 111L));
                add(new StaleAlert(4L, 111L));
            }
        });
        Assert.assertEquals(4, alertHelper.getStaleAlerts(1L).size());
        alertHelper.addStaleAlerts(2L, new ArrayList<StaleAlert>() {
            {
                add(new StaleAlert(1L, 111L));
                add(new StaleAlert(2L, 111L));
            }
        });
        List<Long> hostIds = alertHelper.getHostIdsByDefinitionId(1L);
        Assert.assertEquals(2, hostIds.size());
        Assert.assertTrue(hostIds.contains(1L));
        Assert.assertTrue(hostIds.contains(2L));
    }
}

