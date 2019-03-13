/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
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
 * *****************************************************************************
 */
package smile.regression;


import org.junit.Assert;
import org.junit.Test;
import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import smile.data.parser.IOUtils;
import smile.validation.CrossValidation;
import smile.validation.LOOCV;


/**
 *
 *
 * @author Haifeng Li
 */
public class OLSTest {
    double[][] longley = new double[][]{ new double[]{ 234.289, 235.6, 159.0, 107.608, 1947, 60.323 }, new double[]{ 259.426, 232.5, 145.6, 108.632, 1948, 61.122 }, new double[]{ 258.054, 368.2, 161.6, 109.773, 1949, 60.171 }, new double[]{ 284.599, 335.1, 165.0, 110.929, 1950, 61.187 }, new double[]{ 328.975, 209.9, 309.9, 112.075, 1951, 63.221 }, new double[]{ 346.999, 193.2, 359.4, 113.27, 1952, 63.639 }, new double[]{ 365.385, 187.0, 354.7, 115.094, 1953, 64.989 }, new double[]{ 363.112, 357.8, 335.0, 116.219, 1954, 63.761 }, new double[]{ 397.469, 290.4, 304.8, 117.388, 1955, 66.019 }, new double[]{ 419.18, 282.2, 285.7, 118.734, 1956, 67.857 }, new double[]{ 442.769, 293.6, 279.8, 120.445, 1957, 68.169 }, new double[]{ 444.546, 468.1, 263.7, 121.95, 1958, 66.513 }, new double[]{ 482.704, 381.3, 255.2, 123.366, 1959, 68.655 }, new double[]{ 502.601, 393.1, 251.4, 125.368, 1960, 69.564 }, new double[]{ 518.173, 480.6, 257.2, 127.852, 1961, 69.331 }, new double[]{ 554.894, 400.7, 282.7, 130.081, 1962, 70.551 } };

    double[] y = new double[]{ 83.0, 88.5, 88.2, 89.5, 96.2, 98.1, 99.0, 100.0, 101.2, 104.6, 108.4, 110.8, 112.6, 114.2, 115.7, 116.9 };

    double[][] w = new double[][]{ new double[]{ 0.26353, 0.10815, 2.437, 0.0376 }, new double[]{ 0.03648, 0.03024, 1.206, 0.2585 }, new double[]{ 0.01116, 0.01545, 0.722, 0.4885 }, new double[]{ -1.73703, 0.67382, -2.578, 0.0298 }, new double[]{ -1.4188, 2.9446, -0.482, 0.6414 }, new double[]{ 0.23129, 1.30394, 0.177, 0.8631 }, new double[]{ 2946.85636, 5647.97658, 0.522, 0.6144 } };

    double[] residuals = new double[]{ -0.6008156, 1.5502732, 0.1032287, -1.2306486, -0.3355139, 0.2693345, 0.8776759, 0.1222429, -2.0086121, -0.4859826, 1.0663129, 1.2274906, -0.3835821, 0.2710215, 0.1978569, -0.6402823 };

    public OLSTest() {
    }

    /**
     * Test of learn method, of class LinearRegression.
     */
    @Test
    public void testLearn() {
        System.out.println("learn");
        OLS model = new OLS(longley, y);
        System.out.println(model);
        Assert.assertEquals(12.844, model.RSS(), 1.0E-4);
        Assert.assertEquals(1.1946, model.error(), 1.0E-4);
        Assert.assertEquals(9, model.df());
        Assert.assertEquals(0.9926, model.RSquared(), 1.0E-4);
        Assert.assertEquals(0.9877, model.adjustedRSquared(), 1.0E-4);
        Assert.assertEquals(202.5094, model.ftest(), 1.0E-4);
        Assert.assertEquals(4.42579E-9, model.pvalue(), 1.0E-14);
        for (int i = 0; i < (w.length); i++) {
            for (int j = 0; j < 4; j++) {
                Assert.assertEquals(w[i][j], model.ttest()[i][j], 0.001);
            }
        }
        int n = longley.length;
        LOOCV loocv = new LOOCV(n);
        double rss = 0.0;
        for (int i = 0; i < n; i++) {
            double[][] trainx = Math.slice(longley, loocv.train[i]);
            double[] trainy = Math.slice(y, loocv.train[i]);
            OLS linear = new OLS(trainx, trainy);
            double r = (y[loocv.test[i]]) - (linear.predict(longley[loocv.test[i]]));
            rss += r * r;
        }
        System.out.println(("MSE = " + (rss / n)));
        Assert.assertEquals(2.2148948268123756, (rss / n), 1.0E-4);
    }

    /**
     * Test of learn method, of class LinearRegression.
     */
    @Test
    public void testCPU() {
        System.out.println("CPU");
        ArffParser parser = new ArffParser();
        parser.setResponseIndex(6);
        try {
            AttributeDataset data = parser.parse(IOUtils.getTestDataFile("weka/cpu.arff"));
            double[][] datax = data.toArray(new double[data.size()][]);
            double[] datay = data.toArray(new double[data.size()]);
            int n = datax.length;
            int k = 10;
            CrossValidation cv = new CrossValidation(n, k);
            double rss = 0.0;
            for (int i = 0; i < k; i++) {
                double[][] trainx = Math.slice(datax, cv.train[i]);
                double[] trainy = Math.slice(datay, cv.train[i]);
                double[][] testx = Math.slice(datax, cv.test[i]);
                double[] testy = Math.slice(datay, cv.test[i]);
                OLS linear = new OLS(trainx, trainy);
                for (int j = 0; j < (testx.length); j++) {
                    double r = (testy[j]) - (linear.predict(testx[j]));
                    rss += r * r;
                }
            }
            System.out.println(("MSE = " + (rss / n)));
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

