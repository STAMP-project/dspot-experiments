/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.datavec.api.transform.condition;


import ColumnType.Double;
import ColumnType.Float;
import ColumnType.Integer;
import ColumnType.Long;
import ColumnType.Time;
import NullWritable.INSTANCE;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.datavec.api.transform.condition.string.StringRegexColumnCondition;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.TestTransforms;
import org.junit.Assert;
import org.junit.Test;

import static ConditionOp.Equal;
import static ConditionOp.GreaterOrEqual;
import static ConditionOp.InSet;
import static ConditionOp.LessOrEqual;
import static ConditionOp.LessThan;
import static ConditionOp.NotEqual;
import static ConditionOp.NotInSet;
import static SequenceConditionMode.Or;


/**
 * Created by Alex on 24/03/2016.
 */
public class TestConditions {
    @Test
    public void testIntegerCondition() {
        Schema schema = TestTransforms.getSchema(Integer);
        Condition condition = new IntegerColumnCondition("column", Or, LessThan, 0);
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new IntWritable((-1)))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new IntWritable((-2)))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new IntWritable(0))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new IntWritable(1))))));
        Set<Integer> set = new HashSet<>();
        set.add(0);
        set.add(3);
        condition = new IntegerColumnCondition("column", Or, InSet, set);
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new IntWritable(0))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new IntWritable(3))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new IntWritable(1))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new IntWritable(2))))));
    }

    @Test
    public void testLongCondition() {
        Schema schema = TestTransforms.getSchema(Long);
        Condition condition = new LongColumnCondition("column", Or, NotEqual, 5L);
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new LongWritable(0))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new LongWritable(1))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new LongWritable(5))))));
        Set<Long> set = new HashSet<>();
        set.add(0L);
        set.add(3L);
        condition = new LongColumnCondition("column", Or, NotInSet, set);
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new LongWritable(5))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new LongWritable(10))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new LongWritable(0))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new LongWritable(3))))));
    }

    @Test
    public void testDoubleCondition() {
        Schema schema = TestTransforms.getSchema(Double);
        Condition condition = new DoubleColumnCondition("column", Or, GreaterOrEqual, 0);
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new DoubleWritable(0.0))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new DoubleWritable(0.5))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new DoubleWritable((-0.5)))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new DoubleWritable((-1)))))));
        Set<Double> set = new HashSet<>();
        set.add(0.0);
        set.add(3.0);
        condition = new DoubleColumnCondition("column", Or, InSet, set);
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new DoubleWritable(0.0))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new DoubleWritable(3.0))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new DoubleWritable(1.0))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new DoubleWritable(2.0))))));
    }

    @Test
    public void testFloatCondition() {
        Schema schema = TestTransforms.getSchema(Float);
        Condition condition = new FloatColumnCondition("column", Or, GreaterOrEqual, 0);
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new FloatWritable(0.0F))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new FloatWritable(0.5F))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new FloatWritable((-0.5F)))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new FloatWritable((-1.0F)))))));
        Set<Float> set = new HashSet<Float>();
        set.add(0.0F);
        set.add(3.0F);
        condition = new FloatColumnCondition("column", Or, InSet, set);
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new FloatWritable(0.0F))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new FloatWritable(3.0F))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new FloatWritable(1.0F))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new FloatWritable(2.0F))))));
    }

    @Test
    public void testStringCondition() {
        Schema schema = TestTransforms.getSchema(Integer);
        Condition condition = new StringColumnCondition("column", Or, Equal, "value");
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new Text("value"))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new Text("not_value"))))));
        Set<String> set = new HashSet<>();
        set.add("in set");
        set.add("also in set");
        condition = new StringColumnCondition("column", Or, InSet, set);
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new Text("in set"))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new Text("also in set"))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new Text("not in the set"))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new Text(":)"))))));
    }

    @Test
    public void testCategoricalCondition() {
        Schema schema = new Schema.Builder().addColumnCategorical("column", "alpha", "beta", "gamma").build();
        Condition condition = new CategoricalColumnCondition("column", Or, Equal, "alpha");
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new Text("alpha"))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new Text("beta"))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new Text("gamma"))))));
        Set<String> set = new HashSet<>();
        set.add("alpha");
        set.add("beta");
        condition = new StringColumnCondition("column", Or, InSet, set);
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new Text("alpha"))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new Text("beta"))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new Text("gamma"))))));
    }

    @Test
    public void testTimeCondition() {
        Schema schema = TestTransforms.getSchema(Time);
        // 1451606400000 = 01/01/2016 00:00:00 GMT
        Condition condition = new TimeColumnCondition("column", Or, LessOrEqual, 1451606400000L);
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new LongWritable(1451606400000L))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new LongWritable((1451606400000L - 1L)))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new LongWritable((1451606400000L + 1L)))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new LongWritable((1451606400000L + 1000L)))))));
        Set<Long> set = new HashSet<>();
        set.add(1451606400000L);
        condition = new TimeColumnCondition("column", Or, InSet, set);
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new LongWritable(1451606400000L))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new LongWritable((1451606400000L + 1L)))))));
    }

    @Test
    public void testStringRegexCondition() {
        Schema schema = TestTransforms.getSchema(ColumnType.String);
        // Condition: String value starts with "abc"
        Condition condition = new StringRegexColumnCondition("column", "abc.*");
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new Text("abc"))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new Text("abcdefghijk"))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new Text("abc more text \tetc"))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new Text("ab"))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new Text("also doesn't match"))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new Text(" abc"))))));
        // Check application on non-String columns
        schema = TestTransforms.getSchema(Integer);
        condition = new StringRegexColumnCondition("column", "123\\d*");
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new IntWritable(123))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new IntWritable(123456))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new IntWritable((-123)))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new IntWritable(456789))))));
    }

    @Test
    public void testNullWritableColumnCondition() {
        Schema schema = TestTransforms.getSchema(Time);
        Condition condition = new NullWritableColumnCondition("column");
        condition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (INSTANCE)))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new NullWritable())))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new IntWritable(0))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new Text("1"))))));
    }

    @Test
    public void testBooleanConditionNot() {
        Schema schema = TestTransforms.getSchema(Integer);
        Condition condition = new IntegerColumnCondition("column", Or, LessThan, 0);
        condition.setInputSchema(schema);
        Condition notCondition = BooleanCondition.NOT(condition);
        notCondition.setInputSchema(schema);
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new IntWritable((-1)))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new IntWritable((-2)))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new IntWritable(0))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new IntWritable(1))))));
        // Expect opposite for not condition:
        Assert.assertFalse(notCondition.condition(Collections.singletonList(((Writable) (new IntWritable((-1)))))));
        Assert.assertFalse(notCondition.condition(Collections.singletonList(((Writable) (new IntWritable((-2)))))));
        Assert.assertTrue(notCondition.condition(Collections.singletonList(((Writable) (new IntWritable(0))))));
        Assert.assertTrue(notCondition.condition(Collections.singletonList(((Writable) (new IntWritable(1))))));
    }

    @Test
    public void testBooleanConditionAnd() {
        Schema schema = TestTransforms.getSchema(Integer);
        Condition condition1 = new IntegerColumnCondition("column", Or, LessThan, 0);
        condition1.setInputSchema(schema);
        Condition condition2 = new IntegerColumnCondition("column", Or, LessThan, (-1));
        condition2.setInputSchema(schema);
        Condition andCondition = BooleanCondition.AND(condition1, condition2);
        andCondition.setInputSchema(schema);
        Assert.assertFalse(andCondition.condition(Collections.singletonList(((Writable) (new IntWritable((-1)))))));
        Assert.assertTrue(andCondition.condition(Collections.singletonList(((Writable) (new IntWritable((-2)))))));
        Assert.assertFalse(andCondition.condition(Collections.singletonList(((Writable) (new IntWritable(0))))));
        Assert.assertFalse(andCondition.condition(Collections.singletonList(((Writable) (new IntWritable(1))))));
    }

    @Test
    public void testInvalidValueColumnConditionCondition() {
        Schema schema = TestTransforms.getSchema(Integer);
        Condition condition = new InvalidValueColumnCondition("column");
        condition.setInputSchema(schema);
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new IntWritable((-1)))))));// Not invalid -> condition does not apply

        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new IntWritable((-2)))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new LongWritable(1000))))));
        Assert.assertFalse(condition.condition(Collections.singletonList(((Writable) (new Text("1000"))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new Text("text"))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new Text("NaN"))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new LongWritable((1L + ((long) (Integer.MAX_VALUE)))))))));
        Assert.assertTrue(condition.condition(Collections.singletonList(((Writable) (new DoubleWritable(3.14159))))));
    }

    @Test
    public void testSequenceLengthCondition() {
        Condition c = new org.datavec.api.transform.condition.sequence.SequenceLengthCondition(LessThan, 2);
        List<List<Writable>> l1 = Arrays.asList(Collections.<Writable>singletonList(INSTANCE));
        List<List<Writable>> l2 = Arrays.asList(Collections.<Writable>singletonList(INSTANCE), Collections.<Writable>singletonList(INSTANCE));
        List<List<Writable>> l3 = Arrays.asList(Collections.<Writable>singletonList(INSTANCE), Collections.<Writable>singletonList(INSTANCE), Collections.<Writable>singletonList(INSTANCE));
        Assert.assertTrue(c.conditionSequence(l1));
        Assert.assertFalse(c.conditionSequence(l2));
        Assert.assertFalse(c.conditionSequence(l3));
        Set<Integer> set = new HashSet<>();
        set.add(2);
        c = new org.datavec.api.transform.condition.sequence.SequenceLengthCondition(InSet, set);
        Assert.assertFalse(c.conditionSequence(l1));
        Assert.assertTrue(c.conditionSequence(l2));
        Assert.assertFalse(c.conditionSequence(l3));
    }
}

