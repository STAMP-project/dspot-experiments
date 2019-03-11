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
package org.nd4j.linalg.crash;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 * This set of test launches different ops in different order, to check for possible data corruption cases
 *
 * @author raver119@gmail.com
 */
@Slf4j
@RunWith(Parameterized.class)
public class CrashTest extends BaseNd4jTest {
    public CrashTest(Nd4jBackend backend) {
        super(backend);
    }

    private static final int ITERATIONS = 10;

    private static final boolean[] paramsA = new boolean[]{ true, false };

    private static final boolean[] paramsB = new boolean[]{ true, false };

    /**
     * tensorAlongDimension() produces shapeInfo without EWS defined
     */
    @Test
    public void testNonEWSViews1() {
        log.debug("non-EWS 1");
        INDArray x = Nd4j.create(64, 1024, 64);
        INDArray y = Nd4j.create(64, 64, 1024);
        for (int i = 0; i < (CrashTest.ITERATIONS); i++) {
            int slice = RandomUtils.nextInt(0, ((int) (x.size(0))));
            op(x.tensorAlongDimension(slice, 1, 2), y.tensorAlongDimension(slice, 1, 2), i);
        }
    }

    @Test
    public void testNonEWSViews2() {
        log.debug("non-EWS 2");
        INDArray x = Nd4j.create(new int[]{ 64, 1024, 64 }, 'f');
        INDArray y = Nd4j.create(new int[]{ 64, 64, 1024 }, 'f');
        for (int i = 0; i < (CrashTest.ITERATIONS); i++) {
            int slice = RandomUtils.nextInt(0, ((int) (x.size(0))));
            op(x.tensorAlongDimension(slice, 1, 2), y.tensorAlongDimension(slice, 1, 2), i);
        }
    }

    /**
     * slice() produces shapeInfo with EWS being 1 in our case
     */
    @Test
    public void testEWSViews1() {
        log.debug("EWS 1");
        INDArray x = Nd4j.create(64, 1024, 64);
        INDArray y = Nd4j.create(64, 64, 1024);
        for (int i = 0; i < (CrashTest.ITERATIONS); i++) {
            // FIXME: int cast
            int slice = RandomUtils.nextInt(0, ((int) (x.shape()[0])));
            op(x.slice(slice), y.slice(slice), i);
        }
    }

    @Test
    public void testEWSViews2() {
        log.debug("EWS 2");
        INDArray x = Nd4j.create(new int[]{ 96, 1024, 64 }, 'f');
        INDArray y = Nd4j.create(new int[]{ 96, 64, 1024 }, 'f');
        for (int i = 0; i < 1; i++) {
            int slice = 0;// RandomUtils.nextInt(0, x.shape()[0]);

            op(x.slice(slice), y.slice(slice), i);
        }
    }
}

