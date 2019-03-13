/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.util.datastructures;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RandomRemovalListTest {
    private static final Logger log = LoggerFactory.getLogger(RandomRemovalListTest.class);

    @Test
    public void test1() {
        int max = 1000000;
        final RandomRemovalList<Integer> list = new RandomRemovalList();
        for (int i = 1; i <= max; i++) {
            list.add(i);
        }
        long sum = 0;
        int subset = max / 10;
        for (int j = 1; j <= subset; j++) {
            sum += list.getRandom();
        }
        double avg = sum / ((double) (subset));
        RandomRemovalListTest.log.debug("Average: {}", avg);
        Assertions.assertEquals(avg, (((double) (max)) / 2), (max / 100));
    }

    @Test
    public void test2() {
        runIndividual();
    }

    @Test
    public void test3() {
        long max = 20000;
        final RandomRemovalList<Integer> list = new RandomRemovalList();
        for (int i = 1; i <= max; i++) {
            list.add(i);
        }
        long sum = 0;
        int numReturned = 0;
        for (final Integer aList : list) {
            sum += aList;
            numReturned++;
        }
        Assertions.assertEquals(sum, (((max + 1) * max) / 2));
        Assertions.assertEquals(numReturned, max);
    }
}

