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


import java.io.IOException;
import net.jcip.annotations.NotThreadSafe;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * This clas tests {@code NoOverCommitPolicy} sharing policy.
 */
@RunWith(Parameterized.class)
@NotThreadSafe
@SuppressWarnings("VisibilityModifier")
public class TestNoOverCommitPolicy extends BaseSharingPolicyTest {
    static final long ONEHOUR = 3600 * 1000;

    static final String TWOHOURPERIOD = "7200000";

    @Test
    public void testAllocation() throws IOException, PlanningException {
        runTest();
    }
}

