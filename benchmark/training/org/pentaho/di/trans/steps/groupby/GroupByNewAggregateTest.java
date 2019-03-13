/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.groupby;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class GroupByNewAggregateTest {
    static StepMockHelper<GroupByMeta, GroupByData> mockHelper;

    GroupBy step;

    GroupByData data;

    static List<Integer> strings;

    static List<Integer> statistics;

    static int def = -113;

    /**
     * PDI-6960 - Group by and Memory group by ignores NULL values ("sum" aggregation)
     *
     * We do not assign 0 instead of null.
     */
    @Test
    public void newAggregateInitializationTest() {
        Object[] r = new Object[18];
        Arrays.fill(r, null);
        step.newAggregate(r);
        Object[] agg = data.agg;
        Assert.assertEquals("All possible aggregation cases considered", 18, agg.length);
        // all aggregations types is int values, filled in ascending order in perconditions
        for (int i = 0; i < (agg.length); i++) {
            int type = i + 1;
            if (GroupByNewAggregateTest.strings.contains(type)) {
                Assert.assertTrue(("This is appendable type, type=" + type), ((agg[i]) instanceof Appendable));
            } else
                if (GroupByNewAggregateTest.statistics.contains(type)) {
                    Assert.assertTrue(("This is collection, type=" + type), ((agg[i]) instanceof Collection));
                } else {
                    Assert.assertNull(("Aggregation initialized with null, type=" + type), agg[i]);
                }

        }
    }
}

