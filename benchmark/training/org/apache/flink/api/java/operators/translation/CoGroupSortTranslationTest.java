/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.java.operators.translation;


import Order.ASCENDING;
import Order.DESCENDING;
import java.io.Serializable;
import org.apache.flink.api.common.Plan;
import org.apache.flink.api.common.operators.GenericDataSinkBase;
import org.apache.flink.api.common.operators.base.CoGroupOperatorBase;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for translation of co-group sort.
 */
@SuppressWarnings({ "serial", "unchecked" })
public class CoGroupSortTranslationTest implements Serializable {
    @Test
    public void testGroupSortTuples() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            DataSet<Tuple2<Long, Long>> input1 = env.fromElements(new Tuple2<Long, Long>(0L, 0L));
            DataSet<Tuple3<Long, Long, Long>> input2 = env.fromElements(new Tuple3<Long, Long, Long>(0L, 0L, 0L));
            input1.coGroup(input2).where(1).equalTo(2).sortFirstGroup(0, DESCENDING).sortSecondGroup(1, ASCENDING).sortSecondGroup(0, DESCENDING).with(new org.apache.flink.api.common.functions.CoGroupFunction<Tuple2<Long, Long>, Tuple3<Long, Long, Long>, Long>() {
                @Override
                public void coGroup(Iterable<Tuple2<Long, Long>> first, Iterable<Tuple3<Long, Long, Long>> second, Collector<Long> out) {
                }
            }).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Long>());
            Plan p = env.createProgramPlan();
            GenericDataSinkBase<?> sink = p.getDataSinks().iterator().next();
            CoGroupOperatorBase<?, ?, ?, ?> coGroup = ((CoGroupOperatorBase<?, ?, ?, ?>) (sink.getInput()));
            Assert.assertNotNull(coGroup.getGroupOrderForInputOne());
            Assert.assertNotNull(coGroup.getGroupOrderForInputTwo());
            Assert.assertEquals(1, coGroup.getGroupOrderForInputOne().getNumberOfFields());
            Assert.assertEquals(0, coGroup.getGroupOrderForInputOne().getFieldNumber(0).intValue());
            Assert.assertEquals(DESCENDING, coGroup.getGroupOrderForInputOne().getOrder(0));
            Assert.assertEquals(2, coGroup.getGroupOrderForInputTwo().getNumberOfFields());
            Assert.assertEquals(1, coGroup.getGroupOrderForInputTwo().getFieldNumber(0).intValue());
            Assert.assertEquals(0, coGroup.getGroupOrderForInputTwo().getFieldNumber(1).intValue());
            Assert.assertEquals(ASCENDING, coGroup.getGroupOrderForInputTwo().getOrder(0));
            Assert.assertEquals(DESCENDING, coGroup.getGroupOrderForInputTwo().getOrder(1));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSortTuplesAndPojos() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            DataSet<Tuple2<Long, Long>> input1 = env.fromElements(new Tuple2<Long, Long>(0L, 0L));
            DataSet<CoGroupSortTranslationTest.TestPoJo> input2 = env.fromElements(new CoGroupSortTranslationTest.TestPoJo());
            input1.coGroup(input2).where(1).equalTo("b").sortFirstGroup(0, DESCENDING).sortSecondGroup("c", ASCENDING).sortSecondGroup("a", DESCENDING).with(new org.apache.flink.api.common.functions.CoGroupFunction<Tuple2<Long, Long>, CoGroupSortTranslationTest.TestPoJo, Long>() {
                @Override
                public void coGroup(Iterable<Tuple2<Long, Long>> first, Iterable<CoGroupSortTranslationTest.TestPoJo> second, Collector<Long> out) {
                }
            }).output(new org.apache.flink.api.java.io.DiscardingOutputFormat<Long>());
            Plan p = env.createProgramPlan();
            GenericDataSinkBase<?> sink = p.getDataSinks().iterator().next();
            CoGroupOperatorBase<?, ?, ?, ?> coGroup = ((CoGroupOperatorBase<?, ?, ?, ?>) (sink.getInput()));
            Assert.assertNotNull(coGroup.getGroupOrderForInputOne());
            Assert.assertNotNull(coGroup.getGroupOrderForInputTwo());
            Assert.assertEquals(1, coGroup.getGroupOrderForInputOne().getNumberOfFields());
            Assert.assertEquals(0, coGroup.getGroupOrderForInputOne().getFieldNumber(0).intValue());
            Assert.assertEquals(DESCENDING, coGroup.getGroupOrderForInputOne().getOrder(0));
            Assert.assertEquals(2, coGroup.getGroupOrderForInputTwo().getNumberOfFields());
            Assert.assertEquals(2, coGroup.getGroupOrderForInputTwo().getFieldNumber(0).intValue());
            Assert.assertEquals(0, coGroup.getGroupOrderForInputTwo().getFieldNumber(1).intValue());
            Assert.assertEquals(ASCENDING, coGroup.getGroupOrderForInputTwo().getOrder(0));
            Assert.assertEquals(DESCENDING, coGroup.getGroupOrderForInputTwo().getOrder(1));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Sample test pojo.
     */
    public static class TestPoJo {
        public long a;

        public long b;

        public long c;
    }
}

