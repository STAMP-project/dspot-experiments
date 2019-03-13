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
package org.deeplearning4j.datasets.iterator;


import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.parallel.JointParallelDataSetIterator;
import org.deeplearning4j.datasets.iterator.tools.SimpleVariableGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.enums.InequalityHandling;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class JointParallelDataSetIteratorTest extends BaseDL4JTest {
    /**
     * Simple test, checking datasets alignment. They all should have the same data for the same cycle
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testJointIterator1() throws Exception {
        DataSetIterator iteratorA = new SimpleVariableGenerator(119, 100, 32, 100, 10);
        DataSetIterator iteratorB = new SimpleVariableGenerator(119, 100, 32, 100, 10);
        JointParallelDataSetIterator jpdsi = new JointParallelDataSetIterator.Builder(InequalityHandling.STOP_EVERYONE).addSourceIterator(iteratorA).addSourceIterator(iteratorB).build();
        int cnt = 0;
        int example = 0;
        while (jpdsi.hasNext()) {
            DataSet ds = jpdsi.next();
            Assert.assertNotNull(("Failed on iteration " + cnt), ds);
            // ds.detach();
            // ds.migrate();
            Assert.assertEquals(("Failed on iteration " + cnt), ((double) (example)), ds.getFeatures().meanNumber().doubleValue(), 0.001);
            Assert.assertEquals(("Failed on iteration " + cnt), (((double) (example)) + 0.5), ds.getLabels().meanNumber().doubleValue(), 0.001);
            cnt++;
            if ((cnt % 2) == 0)
                example++;

        } 
        Assert.assertEquals(100, example);
        Assert.assertEquals(200, cnt);
    }

    /**
     * This test checks for pass_null scenario, so in total we should have 300 real datasets + 100 nulls
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testJointIterator2() throws Exception {
        DataSetIterator iteratorA = new SimpleVariableGenerator(119, 200, 32, 100, 10);
        DataSetIterator iteratorB = new SimpleVariableGenerator(119, 100, 32, 100, 10);
        JointParallelDataSetIterator jpdsi = new JointParallelDataSetIterator.Builder(InequalityHandling.PASS_NULL).addSourceIterator(iteratorA).addSourceIterator(iteratorB).build();
        int cnt = 0;
        int example = 0;
        int nulls = 0;
        while (jpdsi.hasNext()) {
            DataSet ds = jpdsi.next();
            if (cnt < 200)
                Assert.assertNotNull(("Failed on iteration " + cnt), ds);

            if (ds == null)
                nulls++;

            if ((cnt % 2) == 2) {
                Assert.assertEquals(("Failed on iteration " + cnt), ((double) (example)), ds.getFeatures().meanNumber().doubleValue(), 0.001);
                Assert.assertEquals(("Failed on iteration " + cnt), (((double) (example)) + 0.5), ds.getLabels().meanNumber().doubleValue(), 0.001);
            }
            cnt++;
            if ((cnt % 2) == 0)
                example++;

        } 
        Assert.assertEquals(100, nulls);
        Assert.assertEquals(200, example);
        Assert.assertEquals(400, cnt);
    }

    /**
     * Testing relocate
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testJointIterator3() throws Exception {
        DataSetIterator iteratorA = new SimpleVariableGenerator(119, 200, 32, 100, 10);
        DataSetIterator iteratorB = new SimpleVariableGenerator(119, 100, 32, 100, 10);
        JointParallelDataSetIterator jpdsi = new JointParallelDataSetIterator.Builder(InequalityHandling.RELOCATE).addSourceIterator(iteratorA).addSourceIterator(iteratorB).build();
        int cnt = 0;
        int example = 0;
        while (jpdsi.hasNext()) {
            DataSet ds = jpdsi.next();
            Assert.assertNotNull(("Failed on iteration " + cnt), ds);
            Assert.assertEquals(("Failed on iteration " + cnt), ((double) (example)), ds.getFeatures().meanNumber().doubleValue(), 0.001);
            Assert.assertEquals(("Failed on iteration " + cnt), (((double) (example)) + 0.5), ds.getLabels().meanNumber().doubleValue(), 0.001);
            cnt++;
            if (cnt < 200) {
                if ((cnt % 2) == 0)
                    example++;

            } else
                example++;

        } 
        Assert.assertEquals(300, cnt);
        Assert.assertEquals(200, example);
    }

    /**
     * Testing relocate
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testJointIterator4() throws Exception {
        DataSetIterator iteratorA = new SimpleVariableGenerator(119, 200, 32, 100, 10);
        DataSetIterator iteratorB = new SimpleVariableGenerator(119, 100, 32, 100, 10);
        JointParallelDataSetIterator jpdsi = new JointParallelDataSetIterator.Builder(InequalityHandling.RESET).addSourceIterator(iteratorA).addSourceIterator(iteratorB).build();
        int cnt = 0;
        int cnt_sec = 0;
        int example_sec = 0;
        int example = 0;
        while (jpdsi.hasNext()) {
            DataSet ds = jpdsi.next();
            Assert.assertNotNull(("Failed on iteration " + cnt), ds);
            if ((cnt % 2) == 0) {
                Assert.assertEquals(("Failed on iteration " + cnt), ((double) (example)), ds.getFeatures().meanNumber().doubleValue(), 0.001);
                Assert.assertEquals(("Failed on iteration " + cnt), (((double) (example)) + 0.5), ds.getLabels().meanNumber().doubleValue(), 0.001);
            } else {
                if (cnt <= 200) {
                    Assert.assertEquals(("Failed on iteration " + cnt), ((double) (example)), ds.getFeatures().meanNumber().doubleValue(), 0.001);
                    Assert.assertEquals(("Failed on iteration " + cnt), (((double) (example)) + 0.5), ds.getLabels().meanNumber().doubleValue(), 0.001);
                } else {
                    Assert.assertEquals(((("Failed on iteration " + cnt) + ", second iteration ") + cnt_sec), ((double) (example_sec)), ds.getFeatures().meanNumber().doubleValue(), 0.001);
                    Assert.assertEquals(((("Failed on iteration " + cnt) + ", second iteration ") + cnt_sec), (((double) (example_sec)) + 0.5), ds.getLabels().meanNumber().doubleValue(), 0.001);
                }
            }
            cnt++;
            if ((cnt % 2) == 0)
                example++;

            if ((cnt > 201) && ((cnt % 2) == 1)) {
                cnt_sec++;
                example_sec++;
            }
        } 
        Assert.assertEquals(400, cnt);
        Assert.assertEquals(200, example);
    }
}

