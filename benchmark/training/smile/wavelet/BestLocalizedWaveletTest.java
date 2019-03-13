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
package smile.wavelet;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class BestLocalizedWaveletTest {
    public BestLocalizedWaveletTest() {
    }

    @Test
    public void testFilter() {
        System.out.println("filter");
        int[] order = new int[]{ 14, 18, 20 };
        for (int p : order) {
            System.out.format("p = %d%n", p);
            double[] a = new double[]{ 0.2, -0.4, -0.6, -0.5, -0.8, -0.4, -0.9, 0, -0.2, 0.1, -0.1, 0.1, 0.7, 0.9, 0, 0.3, 0.2, -0.4, -0.6, -0.5, -0.8, -0.4, -0.9, 0, -0.2, 0.1, -0.1, 0.1, 0.7, 0.9, 0, 0.3 };
            double[] b = a.clone();
            Wavelet instance = new BestLocalizedWavelet(p);
            instance.transform(a);
            instance.inverse(a);
            for (int i = 0; i < (a.length); i++) {
                Assert.assertEquals(b[i], a[i], 1.0E-7);
            }
        }
    }
}

