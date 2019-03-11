/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapred.gridmix;


import org.junit.Test;


public class TestRandomAlgorithm {
    private static final int[][] parameters = new int[][]{ new int[]{ 5, 1, 1 }, new int[]{ 10, 1, 2 }, new int[]{ 10, 2, 2 }, new int[]{ 20, 1, 3 }, new int[]{ 20, 2, 3 }, new int[]{ 20, 3, 3 }, new int[]{ 100, 3, 10 }, new int[]{ 100, 3, 100 }, new int[]{ 100, 3, 1000 }, new int[]{ 100, 3, 10000 }, new int[]{ 100, 3, 100000 }, new int[]{ 100, 3, 1000000 } };

    @Test
    public void testRandomSelect() {
        for (int[] param : TestRandomAlgorithm.parameters) {
            testRandomSelect(param[0], param[1], param[2]);
        }
    }

    @Test
    public void testRandomSelectSelector() {
        for (int[] param : TestRandomAlgorithm.parameters) {
            testRandomSelectSelector(param[0], param[1], param[2]);
        }
    }
}

