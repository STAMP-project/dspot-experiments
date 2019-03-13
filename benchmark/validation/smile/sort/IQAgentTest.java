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
package smile.sort;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class IQAgentTest {
    public IQAgentTest() {
    }

    /**
     * Test of add method, of class IQAgent.
     */
    @Test
    public void testAdd() {
        System.out.println("IQAgent");
        double[] data = new double[100000];
        for (int i = 0; i < (data.length); i++)
            data[i] = i + 1;

        permutate(data);
        IQAgent instance = new IQAgent();
        for (int i = 0; i < (data.length); i++)
            instance.add(data[i]);

        for (int i = 1; i <= 100; i++) {
            System.out.println(((((i + "%\t") + (instance.quantile((i / 100.0)))) + "\t") + (Math.abs((1 - ((instance.quantile((i / 100.0))) / (i * 1000)))))));
            Assert.assertTrue(((Math.abs((1 - ((instance.quantile((i / 100.0))) / (i * 1000))))) < 0.01));
        }
    }
}

