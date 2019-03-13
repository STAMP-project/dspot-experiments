/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.memgroupby;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class MemoryGroupByNewAggregateTest {
    static StepMockHelper<MemoryGroupByMeta, MemoryGroupByData> mockHelper;

    static List<Integer> strings;

    static List<Integer> statistics;

    MemoryGroupBy step;

    MemoryGroupByData data;

    @Test
    public void testNewAggregate() throws KettleException {
        Object[] r = new Object[16];
        Arrays.fill(r, null);
        Aggregate agg = new Aggregate();
        step.newAggregate(r, agg);
        Assert.assertEquals("All possible aggregation cases considered", 16, agg.agg.length);
        // all aggregations types is int values, filled in ascending order in perconditions
        for (int i = 0; i < (agg.agg.length); i++) {
            int type = i + 1;
            if (MemoryGroupByNewAggregateTest.strings.contains(type)) {
                Assert.assertTrue(("This is appendable type, type=" + type), ((agg.agg[i]) instanceof Appendable));
            } else
                if (MemoryGroupByNewAggregateTest.statistics.contains(type)) {
                    Assert.assertTrue(("This is collection, type=" + type), ((agg.agg[i]) instanceof Collection));
                } else {
                    Assert.assertNull(("Aggregation initialized with null, type=" + type), agg.agg[i]);
                }

        }
    }
}

