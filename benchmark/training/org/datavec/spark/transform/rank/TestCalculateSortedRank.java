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
package org.datavec.spark.transform.rank;


import ColumnType.Double;
import ColumnType.Long;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.datavec.api.writable.comparator.DoubleWritableComparator;
import org.datavec.spark.BaseSparkTest;
import org.datavec.spark.transform.SparkTransformExecutor;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Alex on 1/06/2016.
 */
public class TestCalculateSortedRank extends BaseSparkTest {
    @Test
    public void testCalculateSortedRank() {
        List<List<Writable>> data = new ArrayList<>();
        data.add(Arrays.asList(((Writable) (new Text("0"))), new DoubleWritable(0.0)));
        data.add(Arrays.asList(((Writable) (new Text("3"))), new DoubleWritable(0.3)));
        data.add(Arrays.asList(((Writable) (new Text("2"))), new DoubleWritable(0.2)));
        data.add(Arrays.asList(((Writable) (new Text("1"))), new DoubleWritable(0.1)));
        JavaRDD<List<Writable>> rdd = BaseSparkTest.sc.parallelize(data);
        Schema schema = new Schema.Builder().addColumnsString("TextCol").addColumnDouble("DoubleCol").build();
        TransformProcess tp = new TransformProcess.Builder(schema).calculateSortedRank("rank", "DoubleCol", new DoubleWritableComparator()).build();
        Schema outSchema = tp.getFinalSchema();
        Assert.assertEquals(3, outSchema.numColumns());
        Assert.assertEquals(Arrays.asList("TextCol", "DoubleCol", "rank"), outSchema.getColumnNames());
        Assert.assertEquals(Arrays.asList(ColumnType.String, Double, Long), outSchema.getColumnTypes());
        JavaRDD<List<Writable>> out = SparkTransformExecutor.execute(rdd, tp);
        List<List<Writable>> collected = out.collect();
        Assert.assertEquals(4, collected.size());
        for (int i = 0; i < 4; i++)
            Assert.assertEquals(3, collected.get(i).size());

        for (List<Writable> example : collected) {
            int exampleNum = example.get(0).toInt();
            int rank = example.get(2).toInt();
            Assert.assertEquals(exampleNum, rank);
        }
    }
}

