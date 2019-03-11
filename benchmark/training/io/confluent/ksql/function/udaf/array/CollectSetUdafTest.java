/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.function.udaf.array;


import io.confluent.ksql.function.udaf.Udaf;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CollectSetUdafTest {
    @Test
    public void shouldCollectDistinctInts() {
        final Udaf<Integer, List<Integer>> udaf = CollectSetUdaf.createCollectSetInt();
        final Integer[] values = new Integer[]{ 3, 4, 5, 3 };
        List<Integer> runningList = udaf.initialize();
        for (final Integer i : values) {
            runningList = udaf.aggregate(i, runningList);
        }
        Assert.assertThat(runningList, Matchers.contains(3, 4, 5));
    }

    @Test
    public void shouldMergeDistinctIntsIncludingNulls() {
        final Udaf<Integer, List<Integer>> udaf = CollectSetUdaf.createCollectSetInt();
        List<Integer> lhs = udaf.initialize();
        final Integer[] lhsValues = new Integer[]{ 1, 2, null, 3 };
        for (final Integer i : lhsValues) {
            lhs = udaf.aggregate(i, lhs);
        }
        Assert.assertThat(lhs, Matchers.contains(1, 2, null, 3));
        List<Integer> rhs = udaf.initialize();
        final Integer[] rhsValues = new Integer[]{ 2, null, 3, 4, 5, 6 };
        for (final Integer i : rhsValues) {
            rhs = udaf.aggregate(i, rhs);
        }
        Assert.assertThat(rhs, Matchers.contains(2, null, 3, 4, 5, 6));
        final List<Integer> merged = udaf.merge(lhs, rhs);
        Assert.assertThat(merged, Matchers.contains(1, 2, null, 3, 4, 5, 6));
    }

    @Test
    public void shouldRespectSizeLimit() {
        final Udaf<Integer, List<Integer>> udaf = CollectSetUdaf.createCollectSetInt();
        List<Integer> runningList = udaf.initialize();
        for (int i = 1; i < 2500; i++) {
            runningList = udaf.aggregate(i, runningList);
        }
        Assert.assertThat(runningList, Matchers.hasSize(1000));
        Assert.assertThat(runningList, Matchers.hasItem(1));
        Assert.assertThat(runningList, Matchers.hasItem(1000));
        Assert.assertThat(runningList, Matchers.not(Matchers.hasItem(1001)));
    }
}

