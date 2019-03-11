/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 * Modifications copyright (C) 2017 Sam Erickson
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


import org.junit.Test;


/**
 *
 *
 * @author Sam Erickson
 */
public class RLSTest {
    public RLSTest() {
    }

    @Test
    public void testOnlineLearn() {
        testOnlineLearn("CPU", "weka/cpu.arff", 6);
        testOnlineLearn("2dplanes", "weka/regression/2dplanes.arff", 10);
        testOnlineLearn("abalone", "weka/regression/abalone.arff", 8);
        // testOnlineLearn(true, "bank32nh", "weka/regression/bank32nh.arff", 32);
        // testOnlineLearn(true, "cal_housing", "weka/regression/cal_housing.arff", 8);
        // testOnlineLearn(true, "puma8nh", "weka/regression/puma8nh.arff", 8);
        // testOnlineLearn(true, "kin8nm", "weka/regression/kin8nm.arff", 8);
    }
}

