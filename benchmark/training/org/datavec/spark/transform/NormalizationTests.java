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


import Schema.Builder;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.util.ndarray.RecordConverter;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.Writable;
import org.datavec.spark.BaseSparkTest;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;


/**
 * Created by agibsonccc on 10/22/16.
 */
public class NormalizationTests extends BaseSparkTest {
    @Test
    public void testMeanStdZeros() {
        List<List<Writable>> data = new ArrayList<>();
        Schema.Builder builder = new Schema.Builder();
        int numColumns = 6;
        for (int i = 0; i < numColumns; i++)
            builder.addColumnDouble(String.valueOf(i));

        for (int i = 0; i < 5; i++) {
            List<Writable> record = new ArrayList<>(numColumns);
            data.add(record);
            for (int j = 0; j < numColumns; j++) {
                record.add(new DoubleWritable(1.0));
            }
        }
        INDArray arr = RecordConverter.toMatrix(data);
        Schema schema = builder.build();
        JavaRDD<List<Writable>> rdd = BaseSparkTest.sc.parallelize(data);
        DataRowsFacade dataFrame = DataFrames.toDataFrame(schema, rdd);
        // assert equivalent to the ndarray pre processing
        NormalizerStandardize standardScaler = new NormalizerStandardize();
        standardScaler.fit(new org.nd4j.linalg.dataset.DataSet(arr.dup(), arr.dup()));
        INDArray standardScalered = arr.dup();
        standardScaler.transform(new org.nd4j.linalg.dataset.DataSet(standardScalered, standardScalered));
        DataNormalization zeroToOne = new NormalizerMinMaxScaler();
        zeroToOne.fit(new org.nd4j.linalg.dataset.DataSet(arr.dup(), arr.dup()));
        INDArray zeroToOnes = arr.dup();
        zeroToOne.transform(new org.nd4j.linalg.dataset.DataSet(zeroToOnes, zeroToOnes));
        List<Row> rows = Normalization.stdDevMeanColumns(dataFrame, dataFrame.get().columns());
        INDArray assertion = DataFrames.toMatrix(rows);
        // compare standard deviation
        TestCase.assertTrue(standardScaler.getStd().equalsWithEps(assertion.getRow(0), 0.1));
        // compare mean
        TestCase.assertTrue(standardScaler.getMean().equalsWithEps(assertion.getRow(1), 0.1));
    }

    @Test
    public void normalizationTests() {
        List<List<Writable>> data = new ArrayList<>();
        Schema.Builder builder = new Schema.Builder();
        int numColumns = 6;
        for (int i = 0; i < numColumns; i++)
            builder.addColumnDouble(String.valueOf(i));

        for (int i = 0; i < 5; i++) {
            List<Writable> record = new ArrayList<>(numColumns);
            data.add(record);
            for (int j = 0; j < numColumns; j++) {
                record.add(new DoubleWritable(1.0));
            }
        }
        INDArray arr = RecordConverter.toMatrix(data);
        Schema schema = builder.build();
        JavaRDD<List<Writable>> rdd = BaseSparkTest.sc.parallelize(data);
        Assert.assertEquals(schema, DataFrames.fromStructType(DataFrames.fromSchema(schema)));
        Assert.assertEquals(rdd.collect(), DataFrames.toRecords(DataFrames.toDataFrame(schema, rdd)).getSecond().collect());
        DataRowsFacade dataFrame = DataFrames.toDataFrame(schema, rdd);
        dataFrame.get().show();
        Normalization.zeromeanUnitVariance(dataFrame).get().show();
        Normalization.normalize(dataFrame).get().show();
        // assert equivalent to the ndarray pre processing
        NormalizerStandardize standardScaler = new NormalizerStandardize();
        standardScaler.fit(new org.nd4j.linalg.dataset.DataSet(arr.dup(), arr.dup()));
        INDArray standardScalered = arr.dup();
        standardScaler.transform(new org.nd4j.linalg.dataset.DataSet(standardScalered, standardScalered));
        DataNormalization zeroToOne = new NormalizerMinMaxScaler();
        zeroToOne.fit(new org.nd4j.linalg.dataset.DataSet(arr.dup(), arr.dup()));
        INDArray zeroToOnes = arr.dup();
        zeroToOne.transform(new org.nd4j.linalg.dataset.DataSet(zeroToOnes, zeroToOnes));
        INDArray zeroMeanUnitVarianceDataFrame = RecordConverter.toMatrix(Normalization.zeromeanUnitVariance(schema, rdd).collect());
        INDArray zeroMeanUnitVarianceDataFrameZeroToOne = RecordConverter.toMatrix(Normalization.normalize(schema, rdd).collect());
        Assert.assertEquals(standardScalered, zeroMeanUnitVarianceDataFrame);
        TestCase.assertTrue(zeroToOnes.equalsWithEps(zeroMeanUnitVarianceDataFrameZeroToOne, 0.1));
    }
}

