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
package com.datumbox.framework.core.statistics.descriptivestatistics;


import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.common.dataobjects.AssociativeArray;
import com.datumbox.framework.common.dataobjects.DataTable2D;
import com.datumbox.framework.core.common.dataobjects.Dataframe;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import com.datumbox.framework.tests.utilities.TestUtils;
import org.junit.Test;


/**
 * Test cases for Bivariate.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class BivariateTest extends AbstractTest {
    /**
     * Test of covarianceMatrix method, of class Bivariate.
     */
    @Test
    public void testCovarianceMatrix() {
        logger.info("covarianceMatrix");
        Configuration configuration = getConfiguration();
        Dataframe dataset = generateDataset(configuration);
        DataTable2D expResult = new DataTable2D();
        expResult.put2d(0, 0, 4.5625);
        expResult.put2d(0, 1, 0.5875);
        expResult.put2d(0, 2, (-2.7));
        expResult.put2d(0, 3, (-2.2041666666667));
        expResult.put2d(1, 0, 0.5875);
        expResult.put2d(1, 1, 2.1958333333333);
        expResult.put2d(1, 2, (-0.43333333333333));
        expResult.put2d(1, 3, (-0.3125));
        expResult.put2d(2, 0, (-2.7));
        expResult.put2d(2, 1, (-0.43333333333333));
        expResult.put2d(2, 2, 4.0);
        expResult.put2d(2, 3, 3.0333333333333);
        expResult.put2d(3, 0, (-2.2041666666667));
        expResult.put2d(3, 1, (-0.3125));
        expResult.put2d(3, 2, 3.0333333333333);
        expResult.put2d(3, 3, 3.1625);
        DataTable2D result = Bivariate.covarianceMatrix(dataset);
        TestUtils.assertDoubleDataTable2D(expResult, result);
        dataset.close();
    }

    /**
     * Test of pearsonMatrix method, of class Bivariate.
     */
    @Test
    public void testPearsonMatrix() {
        logger.info("pearsonMatrix");
        Configuration configuration = getConfiguration();
        Dataframe dataset = generateDataset(configuration);
        DataTable2D expResult = new DataTable2D();
        expResult.put2d(0, 0, 1.0);
        expResult.put2d(0, 1, 0.18561229707779);
        expResult.put2d(0, 2, (-0.63202219485911));
        expResult.put2d(0, 3, (-0.58026680188263));
        expResult.put2d(1, 0, 0.18561229707779);
        expResult.put2d(1, 1, 1.0);
        expResult.put2d(1, 2, (-0.14621516381791));
        expResult.put2d(1, 3, (-0.11858644989229));
        expResult.put2d(2, 0, (-0.63202219485911));
        expResult.put2d(2, 1, (-0.14621516381791));
        expResult.put2d(2, 2, 1.0);
        expResult.put2d(2, 3, 0.85285436162523);
        expResult.put2d(3, 0, (-0.58026680188263));
        expResult.put2d(3, 1, (-0.11858644989229));
        expResult.put2d(3, 2, 0.85285436162523);
        expResult.put2d(3, 3, 1.0);
        DataTable2D result = Bivariate.pearsonMatrix(dataset);
        TestUtils.assertDoubleDataTable2D(expResult, result);
        dataset.close();
    }

    /**
     * Test of spearmanMatrix method, of class Bivariate.
     */
    @Test
    public void testSpearmanMatrix() {
        logger.info("spearmanMatrix");
        Configuration configuration = getConfiguration();
        Dataframe dataset = generateDataset(configuration);
        DataTable2D expResult = new DataTable2D();
        expResult.put(0, new AssociativeArray());
        expResult.put2d(0, 0, 1.0);
        expResult.put2d(0, 1, 0.10229198378533);
        expResult.put2d(0, 2, (-0.60665935791938));
        expResult.put2d(0, 3, (-0.56631689758552));
        expResult.put2d(1, 0, 0.10229198378533);
        expResult.put2d(1, 1, 1.0);
        expResult.put2d(1, 2, (-0.14688588181833));
        expResult.put2d(1, 3, (-0.087423415709411));
        expResult.put2d(2, 0, (-0.60665935791938));
        expResult.put2d(2, 1, (-0.14688588181833));
        expResult.put2d(2, 2, 1.0);
        expResult.put2d(2, 3, 0.8472888999181);
        expResult.put2d(3, 0, (-0.56631689758552));
        expResult.put2d(3, 1, (-0.087423415709411));
        expResult.put2d(3, 2, 0.8472888999181);
        expResult.put2d(3, 3, 1.0);
        DataTable2D result = Bivariate.spearmanMatrix(dataset);
        TestUtils.assertDoubleDataTable2D(expResult, result);
        dataset.close();
    }

    /**
     * Test of kendalltauMatrix method, of class Bivariate.
     */
    @Test
    public void testKendalltauMatrix() {
        logger.info("kendalltauMatrix");
        Configuration configuration = getConfiguration();
        Dataframe dataset = generateDataset(configuration);
        DataTable2D expResult = new DataTable2D();
        expResult.put2d(0, 0, 1.0);
        expResult.put2d(0, 1, 0.066666666666667);
        expResult.put2d(0, 2, (-0.36666666666667));
        expResult.put2d(0, 3, (-0.35));
        expResult.put2d(1, 0, 0.066666666666667);
        expResult.put2d(1, 1, 1.0);
        expResult.put2d(1, 2, (-0.083333333333333));
        expResult.put2d(1, 3, (-0.05));
        expResult.put2d(2, 0, (-0.36666666666667));
        expResult.put2d(2, 1, (-0.083333333333333));
        expResult.put2d(2, 2, 1.0);
        expResult.put2d(2, 3, 0.64166666666667);
        expResult.put2d(3, 0, (-0.35));
        expResult.put2d(3, 1, (-0.05));
        expResult.put2d(3, 2, 0.64166666666667);
        expResult.put2d(3, 3, 1.0);
        DataTable2D result = Bivariate.kendalltauMatrix(dataset);
        TestUtils.assertDoubleDataTable2D(expResult, result);
        dataset.close();
    }
}

