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
package org.datavec.api.transform.filter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.datavec.api.transform.condition.Condition;
import org.datavec.api.transform.condition.ConditionOp;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Alex on 21/03/2016.
 */
public class TestFilters {
    @Test
    public void testFilterNumColumns() {
        List<List<Writable>> list = new ArrayList<>();
        list.add(Collections.singletonList(((Writable) (new IntWritable((-1))))));
        list.add(Collections.singletonList(((Writable) (new IntWritable(0)))));
        list.add(Collections.singletonList(((Writable) (new IntWritable(2)))));
        Schema schema = // -100 to 100 only; no NaN or infinite
        // Only values in the range 0 to 10 are ok
        new Schema.Builder().addColumnInteger("intCol", 0, 10).addColumnDouble("doubleCol", (-100.0), 100.0).build();
        Filter numColumns = new InvalidNumColumns(schema);
        for (int i = 0; i < (list.size()); i++)
            Assert.assertTrue(numColumns.removeExample(list.get(i)));

        List<Writable> correct = Arrays.<Writable>asList(new IntWritable(0), new DoubleWritable(2));
        Assert.assertFalse(numColumns.removeExample(correct));
    }

    @Test
    public void testFilterInvalidValues() {
        List<List<Writable>> list = new ArrayList<>();
        list.add(Collections.singletonList(((Writable) (new IntWritable((-1))))));
        list.add(Collections.singletonList(((Writable) (new IntWritable(0)))));
        list.add(Collections.singletonList(((Writable) (new IntWritable(2)))));
        Schema schema = // -100 to 100 only; no NaN or infinite
        // Only values in the range 0 to 10 are ok
        new Schema.Builder().addColumnInteger("intCol", 0, 10).addColumnDouble("doubleCol", (-100.0), 100.0).build();
        Filter filter = new FilterInvalidValues("intCol", "doubleCol");
        filter.setInputSchema(schema);
        // Test valid examples:
        Assert.assertFalse(filter.removeExample(Arrays.asList(((Writable) (new IntWritable(0))), new DoubleWritable(0))));
        Assert.assertFalse(filter.removeExample(Arrays.asList(((Writable) (new IntWritable(10))), new DoubleWritable(0))));
        Assert.assertFalse(filter.removeExample(Arrays.asList(((Writable) (new IntWritable(0))), new DoubleWritable((-100)))));
        Assert.assertFalse(filter.removeExample(Arrays.asList(((Writable) (new IntWritable(0))), new DoubleWritable(100))));
        // Test invalid:
        Assert.assertTrue(filter.removeExample(Arrays.asList(((Writable) (new IntWritable((-1)))), new DoubleWritable(0))));
        Assert.assertTrue(filter.removeExample(Arrays.asList(((Writable) (new IntWritable(11))), new DoubleWritable(0))));
        Assert.assertTrue(filter.removeExample(Arrays.asList(((Writable) (new IntWritable(0))), new DoubleWritable((-101)))));
        Assert.assertTrue(filter.removeExample(Arrays.asList(((Writable) (new IntWritable(0))), new DoubleWritable(101))));
    }

    @Test
    public void testConditionFilter() {
        Schema schema = new Schema.Builder().addColumnInteger("column").build();
        Condition condition = new org.datavec.api.transform.condition.column.IntegerColumnCondition("column", ConditionOp.LessThan, 0);
        condition.setInputSchema(schema);
        Filter filter = new ConditionFilter(condition);
        Assert.assertFalse(filter.removeExample(Collections.singletonList(((Writable) (new IntWritable(10))))));
        Assert.assertFalse(filter.removeExample(Collections.singletonList(((Writable) (new IntWritable(1))))));
        Assert.assertFalse(filter.removeExample(Collections.singletonList(((Writable) (new IntWritable(0))))));
        Assert.assertTrue(filter.removeExample(Collections.singletonList(((Writable) (new IntWritable((-1)))))));
        Assert.assertTrue(filter.removeExample(Collections.singletonList(((Writable) (new IntWritable((-10)))))));
    }
}

