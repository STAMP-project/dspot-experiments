/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment.incremental;


import DimensionSchema.MultiValueHandling;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.druid.data.input.MapBasedInputRow;
import org.apache.druid.data.input.Row;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.segment.VirtualColumns;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class IncrementalIndexMultiValueSpecTest {
    @Test
    public void test() throws IndexSizeExceededException {
        DimensionsSpec dimensionsSpec = new DimensionsSpec(Arrays.asList(new org.apache.druid.data.input.impl.StringDimensionSchema("string1", MultiValueHandling.ARRAY, true), new org.apache.druid.data.input.impl.StringDimensionSchema("string2", MultiValueHandling.SORTED_ARRAY, true), new org.apache.druid.data.input.impl.StringDimensionSchema("string3", MultiValueHandling.SORTED_SET, true)), null, null);
        IncrementalIndexSchema schema = new IncrementalIndexSchema(0, new TimestampSpec("ds", "auto", null), Granularities.ALL, VirtualColumns.EMPTY, dimensionsSpec, new AggregatorFactory[0], false);
        Map<String, Object> map = new HashMap<String, Object>() {
            @Override
            public Object get(Object key) {
                if (((String) (key)).startsWith("string")) {
                    return Arrays.asList("xsd", "aba", "fds", "aba");
                }
                if (((String) (key)).startsWith("float")) {
                    return Arrays.asList(3.92F, (-2.76F), 42.153F, Float.NaN, (-2.76F), (-2.76F));
                }
                if (((String) (key)).startsWith("long")) {
                    return Arrays.asList((-231238789L), 328L, 923L, 328L, (-2L), 0L);
                }
                return null;
            }
        };
        IncrementalIndex<?> index = new IncrementalIndex.Builder().setIndexSchema(schema).setMaxRowCount(10000).buildOnheap();
        index.add(new MapBasedInputRow(0, Arrays.asList("string1", "string2", "string3", "float1", "float2", "float3", "long1", "long2", "long3"), map));
        Row row = index.iterator().next();
        Assert.assertEquals(Lists.newArrayList("xsd", "aba", "fds", "aba"), row.getRaw("string1"));
        Assert.assertEquals(Lists.newArrayList("aba", "aba", "fds", "xsd"), row.getRaw("string2"));
        Assert.assertEquals(Lists.newArrayList("aba", "fds", "xsd"), row.getRaw("string3"));
    }
}

