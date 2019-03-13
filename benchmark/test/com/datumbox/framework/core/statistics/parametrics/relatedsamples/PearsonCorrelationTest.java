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
package com.datumbox.framework.core.statistics.parametrics.relatedsamples;


import com.datumbox.framework.common.dataobjects.FlatDataList;
import com.datumbox.framework.common.dataobjects.TransposeDataList;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for PearsonCorrelation.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class PearsonCorrelationTest extends AbstractTest {
    /**
     * Test of test method, of class PearsonCorrelation.
     */
    @Test
    public void testTest() {
        logger.info("test");
        TransposeDataList transposeDataList = new TransposeDataList();
        transposeDataList.put(0, new FlatDataList(Arrays.asList(new Object[]{ 64, 61, 84, 70, 88, 92, 72, 77 })));
        transposeDataList.put(1, new FlatDataList(Arrays.asList(new Object[]{ 20, 16, 34, 23, 27, 32, 18, 22 })));
        boolean is_twoTailed = true;
        double aLevel = 0.05;
        boolean expResult = true;
        boolean result = PearsonCorrelation.test(transposeDataList, is_twoTailed, aLevel);
        Assert.assertEquals(expResult, result);
    }
}

