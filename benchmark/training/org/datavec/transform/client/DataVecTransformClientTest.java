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
package org.datavec.transform.client;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.spark.transform.CSVSparkTransformServer;
import org.datavec.spark.transform.client.DataVecTransformClient;
import org.datavec.spark.transform.model.Base64NDArrayBody;
import org.datavec.spark.transform.model.BatchCSVRecord;
import org.datavec.spark.transform.model.SequenceBatchCSVRecord;
import org.datavec.spark.transform.model.SingleCSVRecord;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.serde.base64.Nd4jBase64;


/**
 * Created by agibsonccc on 6/12/17.
 */
public class DataVecTransformClientTest {
    private static CSVSparkTransformServer server;

    private static int port = DataVecTransformClientTest.getAvailablePort();

    private static DataVecTransformClient client;

    private static Schema schema = new Schema.Builder().addColumnDouble("1.0").addColumnDouble("2.0").build();

    private static TransformProcess transformProcess = convertToDouble("2.0").build();

    private static File fileSave = new File(((UUID.randomUUID().toString()) + ".json"));

    @Test
    public void testSequenceClient() {
        SequenceBatchCSVRecord sequenceBatchCSVRecord = new SequenceBatchCSVRecord();
        SingleCSVRecord singleCsvRecord = new SingleCSVRecord(new String[]{ "0", "0" });
        BatchCSVRecord batchCSVRecord = new BatchCSVRecord(Arrays.asList(singleCsvRecord, singleCsvRecord));
        List<BatchCSVRecord> batchCSVRecordList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            batchCSVRecordList.add(batchCSVRecord);
        }
        sequenceBatchCSVRecord.add(batchCSVRecordList);
        SequenceBatchCSVRecord sequenceBatchCSVRecord1 = DataVecTransformClientTest.client.transformSequence(sequenceBatchCSVRecord);
        Assume.assumeNotNull(sequenceBatchCSVRecord1);
        Base64NDArrayBody array = DataVecTransformClientTest.client.transformSequenceArray(sequenceBatchCSVRecord);
        Assume.assumeNotNull(array);
        Base64NDArrayBody incrementalBody = DataVecTransformClientTest.client.transformSequenceArrayIncremental(batchCSVRecord);
        Assume.assumeNotNull(incrementalBody);
        Base64NDArrayBody incrementalSequenceBody = DataVecTransformClientTest.client.transformSequenceArrayIncremental(batchCSVRecord);
        Assume.assumeNotNull(incrementalSequenceBody);
    }

    @Test
    public void testRecord() throws Exception {
        SingleCSVRecord singleCsvRecord = new SingleCSVRecord(new String[]{ "0", "0" });
        SingleCSVRecord transformed = DataVecTransformClientTest.client.transformIncremental(singleCsvRecord);
        Assert.assertEquals(singleCsvRecord.getValues().size(), transformed.getValues().size());
        Base64NDArrayBody body = DataVecTransformClientTest.client.transformArrayIncremental(singleCsvRecord);
        INDArray arr = Nd4jBase64.fromBase64(body.getNdarray());
        Assume.assumeNotNull(arr);
    }

    @Test
    public void testBatchRecord() throws Exception {
        SingleCSVRecord singleCsvRecord = new SingleCSVRecord(new String[]{ "0", "0" });
        BatchCSVRecord batchCSVRecord = new BatchCSVRecord(Arrays.asList(singleCsvRecord, singleCsvRecord));
        BatchCSVRecord batchCSVRecord1 = DataVecTransformClientTest.client.transform(batchCSVRecord);
        Assert.assertEquals(batchCSVRecord.getRecords().size(), batchCSVRecord1.getRecords().size());
        Base64NDArrayBody body = DataVecTransformClientTest.client.transformArray(batchCSVRecord);
        INDArray arr = Nd4jBase64.fromBase64(body.getNdarray());
        Assume.assumeNotNull(arr);
    }
}

