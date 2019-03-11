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
package org.apache.drill.exec;


import CoordinationProtos.DrillbitEndpoint;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.expression.ExpressionPosition;
import org.apache.drill.common.logical.PlanProperties;
import org.apache.drill.exec.physical.PhysicalPlan;
import org.apache.drill.exec.physical.base.PhysicalOperator;
import org.apache.drill.exec.physical.config.Filter;
import org.apache.drill.exec.physical.config.Screen;
import org.apache.drill.exec.physical.config.UnionExchange;
import org.apache.drill.exec.planner.PhysicalPlanReader;
import org.apache.drill.exec.store.direct.DirectSubScan;
import org.apache.drill.exec.store.mock.MockSubScanPOP;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestOpSerialization {
    static final Logger logger = LoggerFactory.getLogger(TestOpSerialization.class);

    private DrillConfig config;

    private PhysicalPlanReader reader;

    private ObjectWriter writer;

    @Test
    public void testDirectSubScan() throws Exception {
        LinkedHashMap<String, Class<?>> schema = new LinkedHashMap<>();
        schema.put("count1", Long.class);
        schema.put("count2", Long.class);
        DirectSubScan scan = new DirectSubScan(new org.apache.drill.exec.store.pojo.DynamicPojoRecordReader(schema, Collections.singletonList(Arrays.asList(0L, 1L))));
        scan = ((DirectSubScan) (reader.readFragmentLeaf(writer.writeValueAsString(TestOpSerialization.setupPhysicalOperator(scan)))));
        TestOpSerialization.assertOperator(scan);
    }

    @Test
    public void testMockSubScan() throws Exception {
        MockSubScanPOP scan = new MockSubScanPOP("abc", false, null);
        scan.setOperatorId(1);
        scan = ((MockSubScanPOP) (reader.readFragmentLeaf(writer.writeValueAsString(TestOpSerialization.setupPhysicalOperator(scan)))));
        TestOpSerialization.assertOperator(scan);
        Assert.assertEquals("abc", scan.getUrl());
        Assert.assertNull(scan.getReadEntries());
        Assert.assertFalse(scan.isExtended());
    }

    @Test
    public void testSerializedDeserialize() throws Throwable {
        MockSubScanPOP s = new MockSubScanPOP("abc", false, null);
        s.setOperatorId(3);
        Filter f = new Filter(s, new org.apache.drill.common.expression.ValueExpressions.BooleanExpression("true", ExpressionPosition.UNKNOWN), 0.1F);
        f.setOperatorId(2);
        UnionExchange e = new UnionExchange(f);
        e.setOperatorId(1);
        Screen screen = new Screen(e, DrillbitEndpoint.getDefaultInstance());
        screen.setOperatorId(0);
        boolean reversed = false;
        while (true) {
            List<PhysicalOperator> pops = Lists.newArrayList();
            pops.add(s);
            pops.add(e);
            pops.add(f);
            pops.add(screen);
            if (reversed) {
                pops = Lists.reverse(pops);
            }
            PhysicalPlan plan1 = new PhysicalPlan(PlanProperties.builder().build(), pops);
            String json = plan1.unparse(writer);
            PhysicalPlan plan2 = reader.readPhysicalPlan(json);
            PhysicalOperator root = plan2.getSortedOperators(false).iterator().next();
            Assert.assertEquals(0, root.getOperatorId());
            PhysicalOperator o1 = root.iterator().next();
            Assert.assertEquals(1, o1.getOperatorId());
            PhysicalOperator o2 = o1.iterator().next();
            Assert.assertEquals(2, o2.getOperatorId());
            if (reversed) {
                break;
            }
            reversed = !reversed;
        } 
    }
}

