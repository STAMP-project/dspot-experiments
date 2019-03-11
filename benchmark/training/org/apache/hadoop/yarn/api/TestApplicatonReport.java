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
package org.apache.hadoop.yarn.api;


import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.junit.Assert;
import org.junit.Test;


public class TestApplicatonReport {
    @Test
    public void testApplicationReport() {
        long timestamp = System.currentTimeMillis();
        ApplicationReport appReport1 = TestApplicatonReport.createApplicationReport(1, 1, timestamp);
        ApplicationReport appReport2 = TestApplicatonReport.createApplicationReport(1, 1, timestamp);
        ApplicationReport appReport3 = TestApplicatonReport.createApplicationReport(1, 1, timestamp);
        Assert.assertEquals(appReport1, appReport2);
        Assert.assertEquals(appReport2, appReport3);
        appReport1.setApplicationId(null);
        Assert.assertNull(appReport1.getApplicationId());
        Assert.assertNotSame(appReport1, appReport2);
        appReport2.setCurrentApplicationAttemptId(null);
        Assert.assertNull(appReport2.getCurrentApplicationAttemptId());
        Assert.assertNotSame(appReport2, appReport3);
        Assert.assertNull(appReport1.getAMRMToken());
    }
}

