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
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.Matrix;
import smile.validation.CrossValidation;
import smile.validation.LOOCV;


/**
 *
 *
 * @author Haifeng Li
 */
public class LASSOTest {
    public LASSOTest() {
    }

    /* @Test
    public void testToy() {
    double[][] A = {
    {1, 0, 0, 0.5},
    {0, 1, 0.2, 0.3},
    {0, 0.1, 1, 0.2}
    };

    double[] x0 = {1, 0, 1, 0};    // original signal
    double[] y = new double[A.length];
    Math.ax(A, x0, y);          // measurements with no noise

    LASSO lasso = new LASSO(A, y, 0.01, 0.01, 500);

    double rss = 0.0;
    int n = A.length;
    for (int i = 0; i < n; i++) {
    double r = y[i] - lasso.predict(A[i]);
    rss += r * r;
    }
    System.out.println("MSE = " + rss / n);

    assertEquals(0.0, lasso.intercept(), 1E-4);
    double[] w = {0.9930, 0.0004, 0.9941, 0.0040};
    for (int i = 0; i < w.length; i++) {
    assertEquals(w[i], lasso.coefficients()[i], 1E-4);
    }
    }
     */
    @Test
    public void testToy2() {
        double[][] A = new double[][]{ new double[]{ 1, 0, 0, 0.5 }, new double[]{ 0, 1, 0.2, 0.3 }, new double[]{ 1, 0.5, 0.2, 0.3 }, new double[]{ 0, 0.1, 0, 0.2 }, new double[]{ 0, 0.1, 1, 0.2 } };
        double[] x0 = new double[]{ 1, 0, 1, 0 };// original signal

        double[] y = new double[A.length];
        DenseMatrix a = Matrix.newInstance(A);
        a.ax(x0, y);
        // measurements with no noise
        for (int i = 0; i < (y.length); i++) {
            y[i] += 5;
        }
        LASSO lasso = new LASSO(A, y, 0.1, 0.001, 500);
        double rss = 0.0;
        int n = A.length;
        for (int i = 0; i < n; i++) {
            double r = (y[i]) - (lasso.predict(A[i]));
            rss += r * r;
        }
        System.out.println(("MSE = " + (rss / n)));
        Assert.assertEquals(5.0259443688265355, lasso.intercept(), 1.0E-7);
        double[] w = new double[]{ 0.9659945126777854, -3.7147706312985876E-4, 0.9553629503697613, 9.416740009376934E-4 };
        for (int i = 0; i < (w.length); i++) {
            Assert.assertEquals(w[i], lasso.coefficients()[i], 1.0E-5);
        }
    }

    /**
     * Test of learn method, of class RidgeRegression.
     */
    @Test
    public void testLongley() {
        System.out.println("longley");
        double[][] longley = new double[][]{ new double[]{ 234.289, 235.6, 159.0, 107.608, 1947, 60.323 }, new double[]{ 259.426, 232.5, 145.6, 108.632, 1948, 61.122 }, new double[]{ 258.054, 368.2, 161.6, 109.773, 1949, 60.171 }, new double[]{ 284.599, 335.1, 165.0, 110.929, 1950, 61.187 }, new double[]{ 328.975, 209.9, 309.9, 112.075, 1951, 63.221 }, new double[]{ 346.999, 193.2, 359.4, 113.27, 1952, 63.639 }, new double[]{ 365.385, 187.0, 354.7, 115.094, 1953, 64.989 }, new double[]{ 363.112, 357.8, 335.0, 116.219, 1954, 63.761 }, new double[]{ 397.469, 290.4, 304.8, 117.388, 1955, 66.019 }, new double[]{ 419.18, 282.2, 285.7, 118.734, 1956, 67.857 }, new double[]{ 442.769, 293.6, 279.8, 120.445, 1957, 68.169 }, new double[]{ 444.546, 468.1, 263.7, 121.95, 1958, 66.513 }, new double[]{ 482.704, 381.3, 255.2, 123.366, 1959, 68.655 }, new double[]{ 502.601, 393.1, 251.4, 125.368, 1960, 69.564 }, new double[]{ 518.173, 480.6, 257.2, 127.852, 1961, 69.331 }, new double[]{ 554.894, 400.7, 282.7, 130.081, 1962, 70.551 } };
        double[] y = new double[]{ 83.0, 88.5, 88.2, 89.5, 96.2, 98.1, 99.0, 100.0, 101.2, 104.6, 108.4, 110.8, 112.6, 114.2, 115.7, 116.9 };
        double rss = 0.0;
        int n = longley.length;
        LOOCV loocv = new LOOCV(n);
        for (int i = 0; i < n; i++) {
            double[][] trainx = Math.slice(longley, loocv.train[i]);
            double[] trainy = Math.slice(y, loocv.train[i]);
            LASSO lasso = new LASSO(trainx, trainy, 0.1);
            double r = (y[loocv.test[i]]) - (lasso.predict(longley[loocv.test[i]]));
            rss += r * r;
        }
        System.out.println(("LOOCV MSE = " + (rss / n)));
        Assert.assertEquals(2.0012529348358212, (rss / n), 1.0E-4);
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
                LASSO lasso = new LASSO(trainx, trainy, 50.0);
                for (int j = 0; j < (testx.length); j++) {
                    double r = (testy[j]) - (lasso.predict(testx[j]));
                    rss += r * r;
                }
            }
            System.out.println(("10-CV MSE = " + (rss / n)));
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

