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
package org.deeplearning4j.util;


import org.deeplearning4j.BaseDL4JTest;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by agibsonccc on 12/29/14.
 */
public class TimeSeriesUtilsTest extends BaseDL4JTest {
    @Test
    public void testMovingAverage() {
        INDArray a = Nd4j.arange(0, 20);
        INDArray result = Nd4j.create(new double[]{ 1.5F, 2.5F, 3.5F, 4.5F, 5.5F, 6.5F, 7.5F, 8.5F, 9.5F, 10.5F, 11.5F, 12.5F, 13.5F, 14.5F, 15.5F, 16.5F, 17.5F });
        INDArray movingAvg = TimeSeriesUtils.movingAverage(a, 4);
        Assert.assertEquals(result, movingAvg);
    }
}

