/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.clearspring.analytics.stream.membership;


import BloomCalculations.BloomSpecification;
import org.junit.Test;


public class FilterTest {
    @Test
    public void testManyRandom() {
        testManyHashes(FilterTest.randomKeys());
    }

    // used by filter subclass tests
    static final double MAX_FAILURE_RATE = 0.1;

    public static final BloomSpecification spec = BloomCalculations.computeBucketsAndK(FilterTest.MAX_FAILURE_RATE);

    static final int ELEMENTS = 10000;
}

