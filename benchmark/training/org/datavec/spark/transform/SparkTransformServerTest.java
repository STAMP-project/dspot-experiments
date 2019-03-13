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


import TransformDataType.CSV;
import TransformDataType.IMAGE;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import java.io.File;
import java.util.UUID;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * Created by kepricon on 17. 6. 20.
 */
public class SparkTransformServerTest {
    private static SparkTransformServerChooser serverChooser;

    private static Schema schema = new Schema.Builder().addColumnDouble("1.0").addColumnDouble("2.0").build();

    private static TransformProcess transformProcess = convertToDouble("2.0").build();

    private static File imageTransformFile = new File(((UUID.randomUUID().toString()) + ".json"));

    private static File csvTransformFile = new File(((UUID.randomUUID().toString()) + ".json"));

    @Test
    public void testImageServer() throws Exception {
        SparkTransformServerTest.serverChooser.runMain(new String[]{ "--jsonPath", SparkTransformServerTest.imageTransformFile.getAbsolutePath(), "-dp", "9060", "-dt", IMAGE.toString() });
        SingleImageRecord record = new SingleImageRecord(new ClassPathResource("datavec-spark-inference/testimages/class0/0.jpg").getFile().toURI());
        JsonNode jsonNode = Unirest.post("http://localhost:9060/transformincrementalarray").header("accept", "application/json").header("Content-Type", "application/json").body(record).asJson().getBody();
        Base64NDArrayBody array = Unirest.post("http://localhost:9060/transformincrementalarray").header("accept", "application/json").header("Content-Type", "application/json").body(record).asObject(Base64NDArrayBody.class).getBody();
        BatchImageRecord batch = new BatchImageRecord();
        batch.add(new ClassPathResource("datavec-spark-inference/testimages/class0/0.jpg").getFile().toURI());
        batch.add(new ClassPathResource("datavec-spark-inference/testimages/class0/1.png").getFile().toURI());
        batch.add(new ClassPathResource("datavec-spark-inference/testimages/class0/2.jpg").getFile().toURI());
        JsonNode jsonNodeBatch = Unirest.post("http://localhost:9060/transformarray").header("accept", "application/json").header("Content-Type", "application/json").body(batch).asJson().getBody();
        Base64NDArrayBody batchArray = Unirest.post("http://localhost:9060/transformarray").header("accept", "application/json").header("Content-Type", "application/json").body(batch).asObject(Base64NDArrayBody.class).getBody();
        INDArray result = getNDArray(jsonNode);
        Assert.assertEquals(1, result.size(0));
        INDArray batchResult = getNDArray(jsonNodeBatch);
        Assert.assertEquals(3, batchResult.size(0));
        SparkTransformServerTest.serverChooser.getSparkTransformServer().stop();
    }

    @Test
    public void testCSVServer() throws Exception {
        SparkTransformServerTest.serverChooser.runMain(new String[]{ "--jsonPath", SparkTransformServerTest.csvTransformFile.getAbsolutePath(), "-dp", "9050", "-dt", CSV.toString() });
        String[] values = new String[]{ "1.0", "2.0" };
        SingleCSVRecord record = new SingleCSVRecord(values);
        JsonNode jsonNode = Unirest.post("http://localhost:9050/transformincremental").header("accept", "application/json").header("Content-Type", "application/json").body(record).asJson().getBody();
        SingleCSVRecord singleCsvRecord = Unirest.post("http://localhost:9050/transformincremental").header("accept", "application/json").header("Content-Type", "application/json").body(record).asObject(SingleCSVRecord.class).getBody();
        BatchCSVRecord batchCSVRecord = new BatchCSVRecord();
        for (int i = 0; i < 3; i++)
            batchCSVRecord.add(singleCsvRecord);

        BatchCSVRecord batchCSVRecord1 = Unirest.post("http://localhost:9050/transform").header("accept", "application/json").header("Content-Type", "application/json").body(batchCSVRecord).asObject(BatchCSVRecord.class).getBody();
        Base64NDArrayBody array = Unirest.post("http://localhost:9050/transformincrementalarray").header("accept", "application/json").header("Content-Type", "application/json").body(record).asObject(Base64NDArrayBody.class).getBody();
        Base64NDArrayBody batchArray1 = Unirest.post("http://localhost:9050/transformarray").header("accept", "application/json").header("Content-Type", "application/json").body(batchCSVRecord).asObject(Base64NDArrayBody.class).getBody();
        SparkTransformServerTest.serverChooser.getSparkTransformServer().stop();
    }
}

