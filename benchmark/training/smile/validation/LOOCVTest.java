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
package smile.validation;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class LOOCVTest {
    public LOOCVTest() {
    }

    /**
     * Test if the train and test dataset are complete, of class LeaveOneOut.
     */
    @Test
    public void testComplete() {
        System.out.println("Complete");
        int n = 57;
        LOOCV instance = new LOOCV(n);
        boolean[] hit = new boolean[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                hit[j] = false;
            }
            int[] train = instance.train[i];
            for (int j = 0; j < (train.length); j++) {
                Assert.assertFalse(hit[train[j]]);
                hit[train[j]] = true;
            }
            int test = instance.test[i];
            Assert.assertFalse(hit[test]);
            hit[test] = true;
            for (int j = 0; j < n; j++) {
                Assert.assertTrue(hit[j]);
            }
        }
    }

    /**
     * Test if different dataset are different, of class LeaveOneOut.
     */
    @Test
    public void testOrthogonal() {
        System.out.println("Orthogonal");
        int n = 57;
        LOOCV instance = new LOOCV(n);
        boolean[] hit = new boolean[n];
        for (int i = 0; i < n; i++) {
            int test = instance.test[i];
            Assert.assertFalse(hit[test]);
            hit[test] = true;
        }
        for (int j = 0; j < n; j++) {
            Assert.assertTrue(hit[j]);
        }
    }
}

