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
package smile.nlp.relevance;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class TFIDFTest {
    public TFIDFTest() {
    }

    /**
     * Test of rank method, of class TFIDF.
     */
    @Test
    public void testRank() {
        System.out.println("rank");
        int freq = 3;
        int maxFreq = 10;
        int N = 10000000;
        int n = 1000;
        TFIDF instance = new TFIDF();
        double expResult = 5.341997;
        double result = instance.rank(freq, maxFreq, N, n);
        Assert.assertEquals(expResult, result, 1.0E-6);
    }
}

