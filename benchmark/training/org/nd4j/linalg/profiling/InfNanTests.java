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
package org.nd4j.linalg.profiling;


import OpExecutioner.ProfilingMode.ANY_PANIC;
import OpExecutioner.ProfilingMode.DISABLED;
import OpExecutioner.ProfilingMode.INF_PANIC;
import OpExecutioner.ProfilingMode.NAN_PANIC;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.executioner.OpExecutionerUtil;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 *
 *
 * @author raver119@gmail.com
 */
@RunWith(Parameterized.class)
public class InfNanTests extends BaseNd4jTest {
    public InfNanTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testInf1() {
        Nd4j.getExecutioner().setProfilingMode(INF_PANIC);
        INDArray x = Nd4j.create(100);
        x.putScalar(2, Float.NEGATIVE_INFINITY);
        OpExecutionerUtil.checkForAny(x);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testInf2() {
        Nd4j.getExecutioner().setProfilingMode(ANY_PANIC);
        INDArray x = Nd4j.create(100);
        x.putScalar(2, Float.NEGATIVE_INFINITY);
        OpExecutionerUtil.checkForAny(x);
    }

    @Test
    public void testInf3() {
        Nd4j.getExecutioner().setProfilingMode(ANY_PANIC);
        INDArray x = Nd4j.create(100);
        OpExecutionerUtil.checkForAny(x);
    }

    @Test
    public void testInf4() {
        Nd4j.getExecutioner().setProfilingMode(DISABLED);
        INDArray x = Nd4j.create(100);
        OpExecutionerUtil.checkForAny(x);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testNaN1() {
        Nd4j.getExecutioner().setProfilingMode(NAN_PANIC);
        INDArray x = Nd4j.create(100);
        x.putScalar(2, Float.NaN);
        OpExecutionerUtil.checkForAny(x);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testNaN2() {
        Nd4j.getExecutioner().setProfilingMode(ANY_PANIC);
        INDArray x = Nd4j.create(100);
        x.putScalar(2, Float.NaN);
        OpExecutionerUtil.checkForAny(x);
    }

    @Test
    public void testNaN3() {
        Nd4j.getExecutioner().setProfilingMode(ANY_PANIC);
        INDArray x = Nd4j.create(100);
        OpExecutionerUtil.checkForAny(x);
    }

    @Test
    public void testNaN4() {
        Nd4j.getExecutioner().setProfilingMode(DISABLED);
        INDArray x = Nd4j.create(100);
        OpExecutionerUtil.checkForAny(x);
    }
}

