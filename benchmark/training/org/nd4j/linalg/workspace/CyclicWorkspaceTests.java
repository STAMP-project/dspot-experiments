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


import AllocationPolicy.OVERALLOCATE;
import LearningPolicy.FIRST_LOOP;
import ResetPolicy.ENDOFBUFFER_REACHED;
import SpillPolicy.REALLOCATE;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.memory.conf.WorkspaceConfiguration;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


@Slf4j
@RunWith(Parameterized.class)
public class CyclicWorkspaceTests extends BaseNd4jTest {
    public CyclicWorkspaceTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testBasicMechanics_1() {
        val fShape = new long[]{ 128, 784 };
        val lShape = new long[]{ 128, 10 };
        val prefetchSize = 24;
        val configuration = WorkspaceConfiguration.builder().minSize(((10 * 1024L) * 1024L)).overallocationLimit((prefetchSize + 1)).policyReset(ENDOFBUFFER_REACHED).policyLearning(FIRST_LOOP).policyAllocation(OVERALLOCATE).policySpill(REALLOCATE).build();
        for (int e = 0; e < 100; e++) {
            try (val ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(configuration, ("randomNameHere" + 119))) {
                val fArray = Nd4j.create(fShape).assign(e);
                val lArray = Nd4j.create(lShape).assign(e);
                log.info("Current offset: {}; Current size: {};", ws.getCurrentOffset(), ws.getCurrentSize());
            }
        }
    }
}

