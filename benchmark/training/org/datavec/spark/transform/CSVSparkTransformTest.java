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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.integer.BaseIntegerTransform;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.datavec.spark.transform.model.Base64NDArrayBody;
import org.datavec.spark.transform.model.BatchCSVRecord;
import org.datavec.spark.transform.model.SequenceBatchCSVRecord;
import org.datavec.spark.transform.model.SingleCSVRecord;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.serde.base64.Nd4jBase64;


/**
 * Created by agibsonccc on 12/24/16.
 */
public class CSVSparkTransformTest {
    @Test
    public void testTransformer() throws Exception {
        List<Writable> input = new ArrayList<>();
        input.add(new DoubleWritable(1.0));
        input.add(new DoubleWritable(2.0));
        Schema schema = new Schema.Builder().addColumnDouble("1.0").addColumnDouble("2.0").build();
        List<Writable> output = new ArrayList<>();
        output.add(new Text("1.0"));
        output.add(new Text("2.0"));
        TransformProcess transformProcess = convertToString("2.0").build();
        CSVSparkTransform csvSparkTransform = new CSVSparkTransform(transformProcess);
        String[] values = new String[]{ "1.0", "2.0" };
        SingleCSVRecord record = csvSparkTransform.transform(new SingleCSVRecord(values));
        Base64NDArrayBody body = csvSparkTransform.toArray(new SingleCSVRecord(values));
        INDArray fromBase64 = Nd4jBase64.fromBase64(body.getNdarray());
        Assert.assertTrue(fromBase64.isVector());
        System.out.println(("Base 64ed array " + fromBase64));
    }

    @Test
    public void testTransformerBatch() throws Exception {
        List<Writable> input = new ArrayList<>();
        input.add(new DoubleWritable(1.0));
        input.add(new DoubleWritable(2.0));
        Schema schema = new Schema.Builder().addColumnDouble("1.0").addColumnDouble("2.0").build();
        List<Writable> output = new ArrayList<>();
        output.add(new Text("1.0"));
        output.add(new Text("2.0"));
        TransformProcess transformProcess = convertToString("2.0").build();
        CSVSparkTransform csvSparkTransform = new CSVSparkTransform(transformProcess);
        String[] values = new String[]{ "1.0", "2.0" };
        SingleCSVRecord record = csvSparkTransform.transform(new SingleCSVRecord(values));
        BatchCSVRecord batchCSVRecord = new BatchCSVRecord();
        for (int i = 0; i < 3; i++)
            batchCSVRecord.add(record);

        // data type is string, unable to convert
        BatchCSVRecord batchCSVRecord1 = csvSparkTransform.transform(batchCSVRecord);
        /* Base64NDArrayBody body = csvSparkTransform.toArray(batchCSVRecord1);
        INDArray fromBase64 = Nd4jBase64.fromBase64(body.getNdarray());
        assertTrue(fromBase64.isMatrix());
        System.out.println("Base 64ed array " + fromBase64);
         */
    }

    @Test
    public void testSingleBatchSequence() throws Exception {
        List<Writable> input = new ArrayList<>();
        input.add(new DoubleWritable(1.0));
        input.add(new DoubleWritable(2.0));
        Schema schema = new Schema.Builder().addColumnDouble("1.0").addColumnDouble("2.0").build();
        List<Writable> output = new ArrayList<>();
        output.add(new Text("1.0"));
        output.add(new Text("2.0"));
        TransformProcess transformProcess = convertToString("2.0").build();
        CSVSparkTransform csvSparkTransform = new CSVSparkTransform(transformProcess);
        String[] values = new String[]{ "1.0", "2.0" };
        SingleCSVRecord record = csvSparkTransform.transform(new SingleCSVRecord(values));
        BatchCSVRecord batchCSVRecord = new BatchCSVRecord();
        for (int i = 0; i < 3; i++)
            batchCSVRecord.add(record);

        BatchCSVRecord batchCSVRecord1 = csvSparkTransform.transform(batchCSVRecord);
        SequenceBatchCSVRecord sequenceBatchCSVRecord = new SequenceBatchCSVRecord();
        sequenceBatchCSVRecord.add(Arrays.asList(batchCSVRecord));
        Base64NDArrayBody sequenceArray = csvSparkTransform.transformSequenceArray(sequenceBatchCSVRecord);
        INDArray outputBody = Nd4jBase64.fromBase64(sequenceArray.getNdarray());
        // ensure accumulation
        sequenceBatchCSVRecord.add(Arrays.asList(batchCSVRecord));
        sequenceArray = csvSparkTransform.transformSequenceArray(sequenceBatchCSVRecord);
        Assert.assertArrayEquals(new long[]{ 2, 2, 3 }, Nd4jBase64.fromBase64(sequenceArray.getNdarray()).shape());
        SequenceBatchCSVRecord transformed = csvSparkTransform.transformSequence(sequenceBatchCSVRecord);
        Assert.assertNotNull(transformed.getRecords());
        System.out.println(transformed);
    }

    @Test
    public void testSpecificSequence() throws Exception {
        final Schema schema = new Schema.Builder().addColumnsString("action").build();
        final TransformProcess transformProcess = removeAllColumnsExceptFor("action").transform(new CSVSparkTransformTest.ConverToLowercase("action")).convertToSequence().transform(new org.datavec.api.transform.transform.nlp.TextToCharacterIndexTransform("action", "action_sequence", CSVSparkTransformTest.defaultCharIndex(), false)).integerToOneHot("action_sequence", 0, 29).build();
        final String[] data1 = new String[]{ "test1" };
        final String[] data2 = new String[]{ "test2" };
        final BatchCSVRecord batchCsvRecord = new BatchCSVRecord(Arrays.asList(new SingleCSVRecord(data1), new SingleCSVRecord(data2)));
        final CSVSparkTransform transform = new CSVSparkTransform(transformProcess);
        System.out.println(transform.transformSequenceIncremental(batchCsvRecord));
        Assert.assertEquals(3, Nd4jBase64.fromBase64(transform.transformSequenceArrayIncremental(batchCsvRecord).getNdarray()).rank());
    }

    public static class ConverToLowercase extends BaseIntegerTransform {
        public ConverToLowercase(String column) {
            super(column);
        }

        public Text map(Writable writable) {
            return new Text(writable.toString().toLowerCase());
        }

        public Object map(Object input) {
            return new Text(input.toString().toLowerCase());
        }
    }
}

