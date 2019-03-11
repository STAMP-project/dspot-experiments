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


import MirroringPolicy.HOST_ONLY;
import OpExecutioner.ExecutionerType;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.memory.conf.WorkspaceConfiguration;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


@Slf4j
@RunWith(Parameterized.class)
public class CudaWorkspaceTests extends BaseNd4jTest {
    private DataType initialType;

    public CudaWorkspaceTests(Nd4jBackend backend) {
        super(backend);
        this.initialType = Nd4j.dataType();
    }

    @Test
    public void testWorkspaceReuse() {
        if ((Nd4j.getExecutioner().type()) != (ExecutionerType.CUDA))
            return;

        val workspaceConfig = // Commenting this out makes it so that assert is not triggered (for at least 40 secs or so...)
        WorkspaceConfiguration.builder().policyMirroring(HOST_ONLY).build();
        int cnt = 0;
        for (int e = 0; e < 10; e++) {
            try (val ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(workspaceConfig, "test")) {
                final INDArray zeros = Nd4j.zeros(4, 'f');
                // final INDArray zeros = Nd4j.create(4, 'f'); // Also fails, but maybe less of an issue as javadoc does not say that one can expect returned array to be all zeros.
                Assert.assertEquals((((("Got non-zero array " + zeros) + " after ") + cnt) + " iterations !"), 0.0, zeros.sumNumber().doubleValue(), 1.0E-10);
                zeros.putScalar(0, 1);
            }
        }
    }
}

