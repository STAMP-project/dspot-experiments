/**
 * *****************************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * *****************************************************************************
 */
package org.apache.hadoop.yarn.server.resourcemanager.reservation;


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.ParameterizedSchedulerTestBase;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.AbstractYarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairSchedulerTestBase;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.hadoop.yarn.server.resourcemanager.ParameterizedSchedulerTestBase.SchedulerType.CAPACITY;


@SuppressWarnings({ "rawtypes" })
public class TestReservationSystem extends ParameterizedSchedulerTestBase {
    private static final String ALLOC_FILE = new File(FairSchedulerTestBase.TEST_DIR, ((TestReservationSystem.class.getName()) + ".xml")).getAbsolutePath();

    private AbstractYarnScheduler scheduler;

    private AbstractReservationSystem reservationSystem;

    private RMContext rmContext;

    private Configuration conf;

    private RMContext mockRMContext;

    public TestReservationSystem(ParameterizedSchedulerTestBase.SchedulerType type) throws IOException {
        super(type);
    }

    @Test
    public void testInitialize() throws IOException {
        try {
            reservationSystem.reinitialize(scheduler.getConfig(), rmContext);
        } catch (YarnException e) {
            Assert.fail(e.getMessage());
        }
        if (getSchedulerType().equals(CAPACITY)) {
            ReservationSystemTestUtil.validateReservationQueue(reservationSystem, ReservationSystemTestUtil.getReservationQueueName());
        } else {
            ReservationSystemTestUtil.validateReservationQueue(reservationSystem, ReservationSystemTestUtil.getFullReservationQueueName());
        }
    }

    @Test
    public void testReinitialize() throws IOException {
        conf = scheduler.getConfig();
        try {
            reservationSystem.reinitialize(conf, rmContext);
        } catch (YarnException e) {
            Assert.fail(e.getMessage());
        }
        if (getSchedulerType().equals(CAPACITY)) {
            ReservationSystemTestUtil.validateReservationQueue(reservationSystem, ReservationSystemTestUtil.getReservationQueueName());
        } else {
            ReservationSystemTestUtil.validateReservationQueue(reservationSystem, ReservationSystemTestUtil.getFullReservationQueueName());
        }
        // Dynamically add a plan
        String newQ = "reservation";
        Assert.assertNull(reservationSystem.getPlan(newQ));
        updateSchedulerConf(conf, newQ);
        try {
            scheduler.reinitialize(conf, rmContext);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        try {
            reservationSystem.reinitialize(conf, rmContext);
        } catch (YarnException e) {
            Assert.fail(e.getMessage());
        }
        if (getSchedulerType().equals(CAPACITY)) {
            ReservationSystemTestUtil.validateReservationQueue(reservationSystem, newQ);
        } else {
            ReservationSystemTestUtil.validateReservationQueue(reservationSystem, ("root." + newQ));
        }
    }
}

