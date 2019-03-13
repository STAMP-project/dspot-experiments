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
package com.datumbox.framework.core.statistics.nonparametrics.independentsamples;


import com.datumbox.framework.common.dataobjects.FlatDataCollection;
import com.datumbox.framework.common.dataobjects.TransposeDataCollection;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for MannWhitney.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class MannWhitneyTest extends AbstractTest {
    /**
     * Test of test method, of class MannWhitney.
     */
    @Test
    public void testTest() {
        logger.info("test");
        // Example from Dimaki's Non-parametrics notes. It should reject the null hypothesis and return true.
        TransposeDataCollection transposeDataCollection = new TransposeDataCollection();
        transposeDataCollection.put("group1", new FlatDataCollection(Arrays.asList(new Object[]{ 32, 26.5, 28.5, 30, 26 })));
        transposeDataCollection.put("group2", new FlatDataCollection(Arrays.asList(new Object[]{ 18.5, 16, 19.5, 20 })));
        boolean is_twoTailed = true;
        double aLevel = 0.05;
        boolean expResult = true;
        boolean result = MannWhitney.test(transposeDataCollection, is_twoTailed, aLevel);
        Assert.assertEquals(expResult, result);
    }
}

