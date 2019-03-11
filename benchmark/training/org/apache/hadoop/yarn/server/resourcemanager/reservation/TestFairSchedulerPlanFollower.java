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
package org.apache.hadoop.yarn.server.resourcemanager.reservation;


import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairSchedulerTestBase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class TestFairSchedulerPlanFollower extends TestSchedulerPlanFollowerBase {
    private static final String ALLOC_FILE = new File(FairSchedulerTestBase.TEST_DIR, ((TestSchedulerPlanFollowerBase.class.getName()) + ".xml")).getAbsolutePath();

    private RMContext rmContext;

    private RMContext spyRMContext;

    private FairScheduler fs;

    private Configuration conf;

    private FairSchedulerTestBase testHelper = new FairSchedulerTestBase();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testWithMoveOnExpiry() throws InterruptedException, AccessControlException, PlanningException {
        // invoke plan follower test with move
        testPlanFollower(true);
    }

    @Test
    public void testWithKillOnExpiry() throws InterruptedException, AccessControlException, PlanningException {
        // invoke plan follower test with kill
        testPlanFollower(false);
    }
}

