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
package smile.classification;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class MaxentTest {
    class Dataset {
        int[][] x;

        int[] y;

        int p;
    }

    public MaxentTest() {
    }

    /**
     * Test of learn method, of class Maxent.
     */
    @Test
    public void testLearnProtein() {
        System.out.println("learn protein");
        MaxentTest.Dataset train = load("sequence/sparse.protein.11.train");
        MaxentTest.Dataset test = load("sequence/sparse.protein.11.test");
        Maxent maxent = new Maxent(train.p, train.x, train.y, 0.1, 1.0E-5, 500);
        int error = 0;
        for (int i = 0; i < (test.x.length); i++) {
            if ((test.y[i]) != (maxent.predict(test.x[i]))) {
                error++;
            }
        }
        System.out.format("Protein error is %d of %d%n", error, test.x.length);
        System.out.format("Protein error rate = %.2f%%%n", ((100.0 * error) / (test.x.length)));
        Assert.assertEquals(1338, error);
    }

    /**
     * Test of learn method, of class Maxent.
     */
    @Test
    public void testLearnHyphen() {
        System.out.println("learn hyphen");
        MaxentTest.Dataset train = load("sequence/sparse.hyphen.6.train");
        MaxentTest.Dataset test = load("sequence/sparse.hyphen.6.test");
        Maxent maxent = new Maxent(train.p, train.x, train.y, 0.1, 1.0E-5, 500);
        int error = 0;
        for (int i = 0; i < (test.x.length); i++) {
            if ((test.y[i]) != (maxent.predict(test.x[i]))) {
                error++;
            }
        }
        System.out.format("Protein error is %d of %d%n", error, test.x.length);
        System.out.format("Hyphen error rate = %.2f%%%n", ((100.0 * error) / (test.x.length)));
        Assert.assertEquals(765, error);
    }
}

