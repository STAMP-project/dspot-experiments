/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.classifier.df.split;


import org.apache.mahout.classifier.df.data.Data;
import org.apache.mahout.classifier.df.data.DescriptorException;
import org.apache.mahout.classifier.df.data.conditions.Condition;
import org.apache.mahout.common.MahoutTestCase;
import org.junit.Test;


@Deprecated
public final class RegressionSplitTest extends MahoutTestCase {
    @Test
    public void testComputeSplit() throws DescriptorException {
        Data[] datas = RegressionSplitTest.generateTrainingData();
        RegressionSplit igSplit = new RegressionSplit();
        Split split = igSplit.computeSplit(datas[0], 1);
        assertEquals(180.0, split.getIg(), MahoutTestCase.EPSILON);
        assertEquals(38.0, split.getSplit(), MahoutTestCase.EPSILON);
        split = igSplit.computeSplit(datas[0].subset(Condition.lesser(1, 38.0)), 1);
        assertEquals(76.5, split.getIg(), MahoutTestCase.EPSILON);
        assertEquals(21.5, split.getSplit(), MahoutTestCase.EPSILON);
        split = igSplit.computeSplit(datas[1], 0);
        assertEquals(2205.0, split.getIg(), MahoutTestCase.EPSILON);
        assertEquals(Double.NaN, split.getSplit(), MahoutTestCase.EPSILON);
        split = igSplit.computeSplit(datas[1].subset(Condition.equals(0, 0.0)), 1);
        assertEquals(250.0, split.getIg(), MahoutTestCase.EPSILON);
        assertEquals(41.0, split.getSplit(), MahoutTestCase.EPSILON);
    }
}

