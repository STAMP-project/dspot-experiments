/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.linalg.workspace;


import AllocationPolicy.STRICT;
import DataType.DOUBLE;
import DebugMode.BYPASS_EVERYTHING;
import DebugMode.DISABLED;
import DebugMode.SPILL_EVERYTHING;
import LearningPolicy.FIRST_LOOP;
import MirroringPolicy.FULL;
import SpillPolicy.EXTERNAL;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.memory.conf.WorkspaceConfiguration;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.memory.abstracts.Nd4jWorkspace;


@Slf4j
@RunWith(Parameterized.class)
public class DebugModeTests extends BaseNd4jTest {
    DataType initialType;

    public DebugModeTests(Nd4jBackend backend) {
        super(backend);
        this.initialType = Nd4j.dataType();
    }

    @Test
    public void testDebugMode_1() {
        Assert.assertEquals(DISABLED, Nd4j.getWorkspaceManager().getDebugMode());
        Nd4j.getWorkspaceManager().setDebugMode(SPILL_EVERYTHING);
        Assert.assertEquals(SPILL_EVERYTHING, Nd4j.getWorkspaceManager().getDebugMode());
    }

    @Test
    public void testSpillMode_1() {
        Nd4j.getWorkspaceManager().setDebugMode(SPILL_EVERYTHING);
        val basicConfig = WorkspaceConfiguration.builder().initialSize(((10 * 1024) * 1024)).maxSize(((10 * 1024) * 1024)).overallocationLimit(0.1).policyAllocation(STRICT).policyLearning(FIRST_LOOP).policyMirroring(FULL).policySpill(EXTERNAL).build();
        try (val ws = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(basicConfig, "R_119_1993")))) {
            Assert.assertEquals(((10 * 1024) * 1024L), ws.getCurrentSize());
            Assert.assertEquals(0, ws.getDeviceOffset());
            Assert.assertEquals(0, ws.getHostOffset());
            val array = Nd4j.create(DOUBLE, 10, 10).assign(1.0F);
            Assert.assertTrue(array.isAttached());
            // nothing should get into workspace
            Assert.assertEquals(0, ws.getHostOffset());
            Assert.assertEquals(0, ws.getDeviceOffset());
            // array buffer should be spilled now
            Assert.assertEquals((((10 * 10) * (Nd4j.sizeOfDataType(DOUBLE))) + (Nd4j.sizeOfDataType(DOUBLE))), ws.getSpilledSize());
        }
    }

    @Test
    public void testSpillMode_2() {
        Nd4j.getWorkspaceManager().setDebugMode(SPILL_EVERYTHING);
        val basicConfig = WorkspaceConfiguration.builder().initialSize(0).maxSize(((10 * 1024) * 1024)).overallocationLimit(0.1).policyAllocation(STRICT).policyLearning(FIRST_LOOP).policyMirroring(FULL).policySpill(EXTERNAL).build();
        try (val ws = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(basicConfig, "R_119_1992")))) {
            Assert.assertEquals(0L, ws.getCurrentSize());
            Assert.assertEquals(0, ws.getDeviceOffset());
            Assert.assertEquals(0, ws.getHostOffset());
            val array = Nd4j.create(DOUBLE, 10, 10).assign(1.0F);
            Assert.assertTrue(array.isAttached());
            // nothing should get into workspace
            Assert.assertEquals(0, ws.getHostOffset());
            Assert.assertEquals(0, ws.getDeviceOffset());
            // array buffer should be spilled now
            Assert.assertEquals((((10 * 10) * (Nd4j.sizeOfDataType(DOUBLE))) + (Nd4j.sizeOfDataType(DOUBLE))), ws.getSpilledSize());
        }
        try (val ws = ((Nd4jWorkspace) (Nd4j.getWorkspaceManager().getAndActivateWorkspace(basicConfig, "R_119_1992")))) {
            Assert.assertEquals(0L, ws.getCurrentSize());
            Assert.assertEquals(0, ws.getDeviceOffset());
            Assert.assertEquals(0, ws.getHostOffset());
            Assert.assertEquals(0, ws.getSpilledSize());
        }
    }

    @Test
    public void testBypassMode_1() {
        Nd4j.getWorkspaceManager().setDebugMode(BYPASS_EVERYTHING);
        val basicConfig = WorkspaceConfiguration.builder().initialSize(0).maxSize(((10 * 1024) * 1024)).overallocationLimit(0.1).policyAllocation(STRICT).policyLearning(FIRST_LOOP).policyMirroring(FULL).policySpill(EXTERNAL).build();
        try (val ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(basicConfig, "R_119_1994")) {
            val array = Nd4j.create(10, 10).assign(1.0F);
            Assert.assertFalse(array.isAttached());
        }
    }
}

