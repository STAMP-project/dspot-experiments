/**
 * Copyright (C) 2011 Clearspring Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clearspring.analytics.stream.cardinality;


import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@Ignore
@RunWith(Parameterized.class)
public class TestAndGraphResults {
    private static int CARDINALITY = 50000000;

    private static int NUM_RESULTS = 10000;

    private static int NUM_TRIALS = 1000;

    private final int k;

    public TestAndGraphResults(int k) {
        this.k = k;
    }

    @Test
    public void testLogLog() {
        testLogLog(new LogLog.Builder(), "k%2d %8d max:%f avg:%f min:%f");
    }
}

