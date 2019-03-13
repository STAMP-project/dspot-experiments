/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.math.stats;


import org.apache.mahout.math.MahoutTestCase;
import org.junit.Test;


public final class OnlineSummarizerTest extends MahoutTestCase {
    @Test
    public void testStats() {
        /**
         * the reference limits here were derived using a numerical simulation where I took
         * 10,000 samples from the distribution in question and computed the stats from that
         * sample to get min, 25%-ile, median and so on. I did this 1000 times to get 5% and
         * 95% confidence limits for those values.
         */
        // symmetrical, well behaved
        System.out.printf("normal\n");
        OnlineSummarizerTest.check(OnlineSummarizerTest.normal(10000));
        // asymmetrical, well behaved. The range for the maximum was fudged slightly to all this to pass.
        System.out.printf("exp\n");
        OnlineSummarizerTest.check(OnlineSummarizerTest.exp(10000));
        // asymmetrical, wacko distribution where mean/median is about 200
        System.out.printf("gamma\n");
        OnlineSummarizerTest.check(OnlineSummarizerTest.gamma(10000, 0.1));
    }
}

