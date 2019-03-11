/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.fn.impl;


import org.apache.drill.categories.OperatorTest;
import org.apache.drill.exec.pop.PopUnitTestBase;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(OperatorTest.class)
public class TestAggregateFunction extends PopUnitTestBase {
    @Test
    public void testSortDate() throws Throwable {
        String planPath = "/functions/test_stddev_variance.json";
        String dataPath = "/simple_stddev_variance_input.json";
        Double[] expectedValues = new Double[]{ 2.0, 2.138089935299395, 2.138089935299395, 4.0, 4.571428571428571, 4.571428571428571 };
        runTest(expectedValues, planPath, dataPath);
    }

    @Test
    public void testCovarianceCorrelation() throws Throwable {
        String planPath = "/functions/test_covariance.json";
        String dataPath = "/covariance_input.json";
        Double[] expectedValues = new Double[]{ 4.571428571428571, 4.857142857142857, -6.000000000000002, 4.0, 4.25, -5.250000000000002, 1.0, 0.9274260335029677, -1.0000000000000004 };
        runTest(expectedValues, planPath, dataPath);
    }
}

