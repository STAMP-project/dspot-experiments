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
package org.datavec.api.util;


import java.util.ArrayList;
import java.util.List;
import org.datavec.api.timeseries.util.TimeSeriesWritableUtils;
import org.datavec.api.writable.Writable;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;


public class TimeSeriesUtilsTest {
    @Test
    public void testTimeSeriesCreation() {
        List<List<List<Writable>>> test = new ArrayList<>();
        List<List<Writable>> timeStep = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            timeStep.add(getRecord(5));
        }
        test.add(timeStep);
        INDArray arr = TimeSeriesWritableUtils.convertWritablesSequence(test).getFirst();
        Assert.assertArrayEquals(new long[]{ 1, 5, 5 }, arr.shape());
    }
}

