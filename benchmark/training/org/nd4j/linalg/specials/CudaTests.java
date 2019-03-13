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
package org.nd4j.linalg.specials;


import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ops.executioner.GridExecutioner;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


@Slf4j
@RunWith(Parameterized.class)
public class CudaTests extends BaseNd4jTest {
    DataType initialType;

    public CudaTests(Nd4jBackend backend) {
        super(backend);
        this.initialType = Nd4j.dataType();
    }

    @Test
    public void testMGrid_1() {
        if (!((Nd4j.getExecutioner()) instanceof GridExecutioner))
            return;

        val arrayA = Nd4j.create(128, 128);
        val arrayB = Nd4j.create(128, 128);
        val arrayC = Nd4j.create(128, 128);
        arrayA.muli(arrayB);
        val executioner = ((GridExecutioner) (Nd4j.getExecutioner()));
        Assert.assertEquals(1, executioner.getQueueLength());
        arrayA.addi(arrayC);
        Assert.assertEquals(1, executioner.getQueueLength());
    }

    @Test
    public void testMGrid_2() {
        if (!((Nd4j.getExecutioner()) instanceof GridExecutioner))
            return;

        val exp = Nd4j.create(128, 128).assign(2.0);
        Nd4j.getExecutioner().commit();
        val arrayA = Nd4j.create(128, 128);
        val arrayB = Nd4j.create(128, 128);
        arrayA.muli(arrayB);
        val executioner = ((GridExecutioner) (Nd4j.getExecutioner()));
        Assert.assertEquals(1, executioner.getQueueLength());
        arrayA.addi(2.0F);
        Assert.assertEquals(0, executioner.getQueueLength());
        Nd4j.getExecutioner().commit();
        Assert.assertEquals(exp, arrayA);
    }
}

