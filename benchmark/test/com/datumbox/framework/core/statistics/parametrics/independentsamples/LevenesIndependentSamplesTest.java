/**
 * Copyright (C) 2013-2018 Vasilis Vryniotis <bbriniotis@datumbox.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datumbox.framework.core.statistics.parametrics.independentsamples;


import com.datumbox.framework.common.dataobjects.FlatDataCollection;
import com.datumbox.framework.common.dataobjects.TransposeDataCollection;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for LevenesIndependentSamples.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class LevenesIndependentSamplesTest extends AbstractTest {
    /**
     * Test of testVariances method, of class LevenesIndependentSamples.
     */
    @Test
    public void testTestVariances() {
        logger.info("testVariances");
        TransposeDataCollection transposeDataCollection = new TransposeDataCollection();
        transposeDataCollection.put(0, new FlatDataCollection(Arrays.asList(new Object[]{ 60.8, 57.0, 65.0, 58.6, 61.7 })));
        transposeDataCollection.put(1, new FlatDataCollection(Arrays.asList(new Object[]{ 68.7, 67.7, 74.0, 66.3, 69.8 })));
        transposeDataCollection.put(2, new FlatDataCollection(Arrays.asList(new Object[]{ 102.6, 103.1, 100.2, 96.5 })));
        transposeDataCollection.put(3, new FlatDataCollection(Arrays.asList(new Object[]{ 87.9, 84.2, 83.1, 85.7, 90.3 })));
        double aLevel = 0.05;
        boolean expResult = true;
        boolean result = LevenesIndependentSamples.testVariances(transposeDataCollection, aLevel);
        Assert.assertEquals(expResult, result);
    }
}

