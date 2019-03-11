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
package org.nd4j.linalg.shape.ones;


import DataType.DOUBLE;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 *
 *
 * @author Adam Gibson
 */
@RunWith(Parameterized.class)
public class LeadingAndTrailingOnes extends BaseNd4jTest {
    public LeadingAndTrailingOnes(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testSliceConstructor() {
        List<INDArray> testList = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            testList.add(Nd4j.scalar(DOUBLE, (i + 1)));

        INDArray test = Nd4j.create(testList, new int[]{ 1, testList.size() });
        INDArray expected = Nd4j.create(new double[]{ 1, 2, 3, 4, 5 }, new int[]{ 1, 5 });
        Assert.assertEquals(expected, test);
    }

    @Test
    public void testLeadAndTrail() {
        INDArray fourD = Nd4j.create(1, 2, 1, 1);
        Assert.assertEquals(2, fourD.length());
        for (int i = 0; i < (fourD.length()); i++)
            Assert.assertEquals(0.0, fourD.getDouble(i), 0.1);

    }

    @Test
    public void testCreateLeadingAndTrailingOnes() {
        INDArray arr = Nd4j.create(1, 10, 1, 1);
        arr.assign(1);
        arr.toString();
        System.out.println(arr);
    }
}

