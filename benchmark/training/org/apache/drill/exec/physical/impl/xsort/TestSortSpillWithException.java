/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.physical.impl.xsort;


import ErrorType.RESOURCE;
import ExecConstants.EXTERNAL_SORT_DISABLE_MANAGED_OPTION;
import ExternalSortBatch.INTERRUPTION_WHILE_SPILLING;
import java.io.IOException;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.exec.testing.Controls;
import org.apache.drill.exec.testing.ControlsInjectionUtil;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.ClusterTest;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Testing External Sort's spilling to disk.
 * <br>
 * This class changes the following Drill property to force external sort to spill after the 2nd batch:
 * {@link ExecConstants#EXTERNAL_SORT_SPILL_THRESHOLD} = 1
 * <br>
 * {@link ExecConstants#EXTERNAL_SORT_SPILL_GROUP_SIZE} = 1
 */
@Category(OperatorTest.class)
public class TestSortSpillWithException extends ClusterTest {
    @ClassRule
    public static final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    @Test
    public void testSpillLeakLegacy() throws Exception {
        ClusterTest.client.alterSession(EXTERNAL_SORT_DISABLE_MANAGED_OPTION.getOptionName(), true);
        // inject exception in sort while spilling
        final String controls = Controls.newBuilder().addExceptionOnBit(ExternalSortBatch.class, INTERRUPTION_WHILE_SPILLING, IOException.class, ClusterTest.cluster.drillbit().getContext().getEndpoint()).build();
        ControlsInjectionUtil.setControls(ClusterTest.cluster.client(), controls);
        // run a simple order by query
        try {
            runAndLog("select employee_id from dfs.`xsort/2batches` order by employee_id");
            Assert.fail("Query should have failed!");
        } catch (UserRemoteException e) {
            Assert.assertEquals(RESOURCE, e.getErrorType());
            Assert.assertTrue("Incorrect error message", e.getMessage().contains("External Sort encountered an error while spilling to disk"));
        }
    }

    @Test
    public void testSpillLeakManaged() throws Exception {
        ClusterTest.client.alterSession(EXTERNAL_SORT_DISABLE_MANAGED_OPTION.getOptionName(), false);
        // inject exception in sort while spilling
        final String controls = Controls.newBuilder().addExceptionOnBit(ExternalSortBatch.class, INTERRUPTION_WHILE_SPILLING, IOException.class, ClusterTest.cluster.drillbit().getContext().getEndpoint()).build();
        ControlsInjectionUtil.setControls(ClusterTest.cluster.client(), controls);
        // run a simple order by query
        try {
            runAndLog("SELECT id_i, name_s250 FROM `mock`.`employee_500K` ORDER BY id_i");
            Assert.fail("Query should have failed!");
        } catch (UserRemoteException e) {
            Assert.assertEquals(RESOURCE, e.getErrorType());
            Assert.assertTrue("Incorrect error message", e.getMessage().contains("External Sort encountered an error while spilling to disk"));
        }
    }
}

