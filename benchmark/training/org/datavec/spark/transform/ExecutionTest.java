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
package org.datavec.spark.transform;


import MathOp.Add;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.spark.api.java.JavaRDD;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.schema.SequenceSchema;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.datavec.spark.BaseSparkTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Alex on 25/11/2016.
 */
public class ExecutionTest extends BaseSparkTest {
    @Test
    public void testExecutionSimple() {
        Schema schema = new Schema.Builder().addColumnInteger("col0").addColumnCategorical("col1", "state0", "state1", "state2").addColumnDouble("col2").build();
        TransformProcess tp = categoricalToInteger("col1").doubleMathOp("col2", Add, 10.0).build();
        List<List<Writable>> inputData = new ArrayList<>();
        inputData.add(Arrays.<Writable>asList(new IntWritable(0), new Text("state2"), new DoubleWritable(0.1)));
        inputData.add(Arrays.<Writable>asList(new IntWritable(1), new Text("state1"), new DoubleWritable(1.1)));
        inputData.add(Arrays.<Writable>asList(new IntWritable(2), new Text("state0"), new DoubleWritable(2.1)));
        JavaRDD<List<Writable>> rdd = BaseSparkTest.sc.parallelize(inputData);
        List<List<Writable>> out = new ArrayList(SparkTransformExecutor.execute(rdd, tp).collect());
        Collections.sort(out, new Comparator<List<Writable>>() {
            @Override
            public int compare(List<Writable> o1, List<Writable> o2) {
                return Integer.compare(o1.get(0).toInt(), o2.get(0).toInt());
            }
        });
        List<List<Writable>> expected = new ArrayList<>();
        expected.add(Arrays.<Writable>asList(new IntWritable(0), new IntWritable(2), new DoubleWritable(10.1)));
        expected.add(Arrays.<Writable>asList(new IntWritable(1), new IntWritable(1), new DoubleWritable(11.1)));
        expected.add(Arrays.<Writable>asList(new IntWritable(2), new IntWritable(0), new DoubleWritable(12.1)));
        Assert.assertEquals(expected, out);
    }

    @Test
    public void testExecutionSequence() {
        Schema schema = new SequenceSchema.Builder().addColumnInteger("col0").addColumnCategorical("col1", "state0", "state1", "state2").addColumnDouble("col2").build();
        TransformProcess tp = categoricalToInteger("col1").doubleMathOp("col2", Add, 10.0).build();
        List<List<List<Writable>>> inputSequences = new ArrayList<>();
        List<List<Writable>> seq1 = new ArrayList<>();
        seq1.add(Arrays.<Writable>asList(new IntWritable(0), new Text("state2"), new DoubleWritable(0.1)));
        seq1.add(Arrays.<Writable>asList(new IntWritable(1), new Text("state1"), new DoubleWritable(1.1)));
        seq1.add(Arrays.<Writable>asList(new IntWritable(2), new Text("state0"), new DoubleWritable(2.1)));
        List<List<Writable>> seq2 = new ArrayList<>();
        seq2.add(Arrays.<Writable>asList(new IntWritable(3), new Text("state0"), new DoubleWritable(3.1)));
        seq2.add(Arrays.<Writable>asList(new IntWritable(4), new Text("state1"), new DoubleWritable(4.1)));
        inputSequences.add(seq1);
        inputSequences.add(seq2);
        JavaRDD<List<List<Writable>>> rdd = BaseSparkTest.sc.parallelize(inputSequences);
        List<List<List<Writable>>> out = new ArrayList(SparkTransformExecutor.executeSequenceToSequence(rdd, tp).collect());
        Collections.sort(out, new Comparator<List<List<Writable>>>() {
            @Override
            public int compare(List<List<Writable>> o1, List<List<Writable>> o2) {
                return -(Integer.compare(o1.size(), o2.size()));
            }
        });
        List<List<List<Writable>>> expectedSequence = new ArrayList<>();
        List<List<Writable>> seq1e = new ArrayList<>();
        seq1e.add(Arrays.<Writable>asList(new IntWritable(0), new IntWritable(2), new DoubleWritable(10.1)));
        seq1e.add(Arrays.<Writable>asList(new IntWritable(1), new IntWritable(1), new DoubleWritable(11.1)));
        seq1e.add(Arrays.<Writable>asList(new IntWritable(2), new IntWritable(0), new DoubleWritable(12.1)));
        List<List<Writable>> seq2e = new ArrayList<>();
        seq2e.add(Arrays.<Writable>asList(new IntWritable(3), new IntWritable(0), new DoubleWritable(13.1)));
        seq2e.add(Arrays.<Writable>asList(new IntWritable(4), new IntWritable(1), new DoubleWritable(14.1)));
        expectedSequence.add(seq1e);
        expectedSequence.add(seq2e);
        Assert.assertEquals(expectedSequence, out);
    }

    @Test
    public void testReductionGlobal() {
        List<List<Writable>> in = Arrays.asList(Arrays.<Writable>asList(new Text("first"), new DoubleWritable(3.0)), Arrays.<Writable>asList(new Text("second"), new DoubleWritable(5.0)));
        JavaRDD<List<Writable>> inData = BaseSparkTest.sc.parallelize(in);
        Schema s = new Schema.Builder().addColumnString("textCol").addColumnDouble("doubleCol").build();
        TransformProcess tp = new TransformProcess.Builder(s).reduce(takeFirstColumns("textCol").meanColumns("doubleCol").build()).build();
        JavaRDD<List<Writable>> outRdd = SparkTransformExecutor.execute(inData, tp);
        List<List<Writable>> out = outRdd.collect();
        List<List<Writable>> expOut = Collections.singletonList(Arrays.<Writable>asList(new Text("first"), new DoubleWritable(4.0)));
        Assert.assertEquals(expOut, out);
    }

    @Test
    public void testReductionByKey() {
        List<List<Writable>> in = Arrays.asList(Arrays.<Writable>asList(new IntWritable(0), new Text("first"), new DoubleWritable(3.0)), Arrays.<Writable>asList(new IntWritable(0), new Text("second"), new DoubleWritable(5.0)), Arrays.<Writable>asList(new IntWritable(1), new Text("f"), new DoubleWritable(30.0)), Arrays.<Writable>asList(new IntWritable(1), new Text("s"), new DoubleWritable(50.0)));
        JavaRDD<List<Writable>> inData = BaseSparkTest.sc.parallelize(in);
        Schema s = new Schema.Builder().addColumnInteger("intCol").addColumnString("textCol").addColumnDouble("doubleCol").build();
        TransformProcess tp = new TransformProcess.Builder(s).reduce(takeFirstColumns("textCol").meanColumns("doubleCol").build()).build();
        JavaRDD<List<Writable>> outRdd = SparkTransformExecutor.execute(inData, tp);
        List<List<Writable>> out = outRdd.collect();
        List<List<Writable>> expOut = Arrays.asList(Arrays.<Writable>asList(new IntWritable(0), new Text("first"), new DoubleWritable(4.0)), Arrays.<Writable>asList(new IntWritable(1), new Text("f"), new DoubleWritable(40.0)));
        out = new ArrayList(out);
        Collections.sort(out, new Comparator<List<Writable>>() {
            @Override
            public int compare(List<Writable> o1, List<Writable> o2) {
                return Integer.compare(o1.get(0).toInt(), o2.get(0).toInt());
            }
        });
        Assert.assertEquals(expOut, out);
    }

    @Test
    public void testUniqueMultiCol() {
        Schema schema = new Schema.Builder().addColumnInteger("col0").addColumnCategorical("col1", "state0", "state1", "state2").addColumnDouble("col2").build();
        List<List<Writable>> inputData = new ArrayList<>();
        inputData.add(Arrays.<Writable>asList(new IntWritable(0), new Text("state2"), new DoubleWritable(0.1)));
        inputData.add(Arrays.<Writable>asList(new IntWritable(1), new Text("state1"), new DoubleWritable(1.1)));
        inputData.add(Arrays.<Writable>asList(new IntWritable(2), new Text("state0"), new DoubleWritable(2.1)));
        inputData.add(Arrays.<Writable>asList(new IntWritable(0), new Text("state2"), new DoubleWritable(0.1)));
        inputData.add(Arrays.<Writable>asList(new IntWritable(1), new Text("state1"), new DoubleWritable(1.1)));
        inputData.add(Arrays.<Writable>asList(new IntWritable(2), new Text("state0"), new DoubleWritable(2.1)));
        inputData.add(Arrays.<Writable>asList(new IntWritable(0), new Text("state2"), new DoubleWritable(0.1)));
        inputData.add(Arrays.<Writable>asList(new IntWritable(1), new Text("state1"), new DoubleWritable(1.1)));
        inputData.add(Arrays.<Writable>asList(new IntWritable(2), new Text("state0"), new DoubleWritable(2.1)));
        JavaRDD<List<Writable>> rdd = BaseSparkTest.sc.parallelize(inputData);
        Map<String, List<Writable>> l = AnalyzeSpark.getUnique(Arrays.asList("col0", "col1"), schema, rdd);
        Assert.assertEquals(2, l.size());
        List<Writable> c0 = l.get("col0");
        Assert.assertEquals(3, c0.size());
        Assert.assertTrue((((c0.contains(new IntWritable(0))) && (c0.contains(new IntWritable(1)))) && (c0.contains(new IntWritable(2)))));
        List<Writable> c1 = l.get("col1");
        Assert.assertEquals(3, c1.size());
        Assert.assertTrue((((c1.contains(new Text("state0"))) && (c1.contains(new Text("state1")))) && (c1.contains(new Text("state2")))));
    }
}

