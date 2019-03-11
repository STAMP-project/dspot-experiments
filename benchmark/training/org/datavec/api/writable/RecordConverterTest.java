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
package org.datavec.api.writable;


import DataType.FLOAT;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.util.ndarray.RecordConverter;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;


public class RecordConverterTest {
    @Test
    public void toRecords_PassInClassificationDataSet_ExpectNDArrayAndIntWritables() {
        INDArray feature1 = Nd4j.create(new double[]{ 4, -5.7, 10, -0.1 }, new long[]{ 1, 3 }, FLOAT);
        INDArray feature2 = Nd4j.create(new double[]{ 11, 0.7, -1.3, 4 }, new long[]{ 1, 3 }, FLOAT);
        INDArray label1 = Nd4j.create(new double[]{ 0, 0, 1, 0 }, new long[]{ 1, 4 }, FLOAT);
        INDArray label2 = Nd4j.create(new double[]{ 0, 1, 0, 0 }, new long[]{ 1, 4 }, FLOAT);
        DataSet dataSet = new DataSet(Nd4j.vstack(Lists.newArrayList(feature1, feature2)), Nd4j.vstack(Lists.newArrayList(label1, label2)));
        List<List<Writable>> writableList = RecordConverter.toRecords(dataSet);
        Assert.assertEquals(2, writableList.size());
        testClassificationWritables(feature1, 2, writableList.get(0));
        testClassificationWritables(feature2, 1, writableList.get(1));
    }

    @Test
    public void toRecords_PassInRegressionDataSet_ExpectNDArrayAndDoubleWritables() {
        INDArray feature = Nd4j.create(new double[]{ 4, -5.7, 10, -0.1 }, new long[]{ 1, 4 }, FLOAT);
        INDArray label = Nd4j.create(new double[]{ 0.5, 2, 3, 0.5 }, new long[]{ 1, 4 }, FLOAT);
        DataSet dataSet = new DataSet(feature, label);
        List<List<Writable>> writableList = RecordConverter.toRecords(dataSet);
        List<Writable> results = writableList.get(0);
        NDArrayWritable ndArrayWritable = ((NDArrayWritable) (results.get(0)));
        Assert.assertEquals(1, writableList.size());
        Assert.assertEquals(5, results.size());
        Assert.assertEquals(feature, ndArrayWritable.get());
        for (int i = 0; i < (label.shape()[1]); i++) {
            DoubleWritable doubleWritable = ((DoubleWritable) (results.get((i + 1))));
            Assert.assertEquals(label.getDouble(i), doubleWritable.get(), 0);
        }
    }

    @Test
    public void testNDArrayWritableConcat() {
        List<Writable> l = Arrays.<Writable>asList(new DoubleWritable(1), new NDArrayWritable(Nd4j.create(new double[]{ 2, 3, 4 }, new long[]{ 1, 3 }, FLOAT)), new DoubleWritable(5), new NDArrayWritable(Nd4j.create(new double[]{ 6, 7, 8 }, new long[]{ 1, 3 }, FLOAT)), new IntWritable(9), new IntWritable(1));
        INDArray exp = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 1 }, new long[]{ 1, 10 }, FLOAT);
        INDArray act = RecordConverter.toArray(l);
        Assert.assertEquals(exp, act);
    }

    @Test
    public void testNDArrayWritableConcatToMatrix() {
        List<Writable> l1 = Arrays.<Writable>asList(new DoubleWritable(1), new NDArrayWritable(Nd4j.create(new double[]{ 2, 3, 4 }, new long[]{ 1, 3 }, FLOAT)), new DoubleWritable(5));
        List<Writable> l2 = Arrays.<Writable>asList(new DoubleWritable(6), new NDArrayWritable(Nd4j.create(new double[]{ 7, 8, 9 }, new long[]{ 1, 3 }, FLOAT)), new DoubleWritable(10));
        INDArray exp = Nd4j.create(new double[][]{ new double[]{ 1, 2, 3, 4, 5 }, new double[]{ 6, 7, 8, 9, 10 } }).castTo(FLOAT);
        INDArray act = RecordConverter.toMatrix(Arrays.asList(l1, l2));
        Assert.assertEquals(exp, act);
    }

    @Test
    public void testToRecordWithListOfObject() {
        final List<Object> list = Arrays.asList(((Object) (3)), 7.0F, "Foo", "Bar", 1.0, 3.0F, 3L, 7, 0L);
        final Schema schema = new Schema.Builder().addColumnInteger("a").addColumnFloat("b").addColumnString("c").addColumnCategorical("d", "Bar", "Baz").addColumnDouble("e").addColumnFloat("f").addColumnLong("g").addColumnInteger("h").addColumnTime("i", TimeZone.getDefault()).build();
        final List<Writable> record = RecordConverter.toRecord(schema, list);
        Assert.assertEquals(record.get(0).toInt(), 3);
        Assert.assertEquals(record.get(1).toFloat(), 7.0F, 1.0E-6);
        Assert.assertEquals(record.get(2).toString(), "Foo");
        Assert.assertEquals(record.get(3).toString(), "Bar");
        Assert.assertEquals(record.get(4).toDouble(), 1.0, 1.0E-6);
        Assert.assertEquals(record.get(5).toFloat(), 3.0F, 1.0E-6);
        Assert.assertEquals(record.get(6).toLong(), 3L);
        Assert.assertEquals(record.get(7).toInt(), 7);
        Assert.assertEquals(record.get(8).toLong(), 0);
    }
}

