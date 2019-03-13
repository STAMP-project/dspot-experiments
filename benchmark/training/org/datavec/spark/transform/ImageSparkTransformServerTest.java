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


import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import java.io.File;
import java.util.UUID;
import org.datavec.spark.transform.model.Base64NDArrayBody;
import org.datavec.spark.transform.model.BatchImageRecord;
import org.datavec.spark.transform.model.SingleImageRecord;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * Created by kepricon on 17. 6. 19.
 */
public class ImageSparkTransformServerTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    private static ImageSparkTransformServer server;

    private static File fileSave = new File(((UUID.randomUUID().toString()) + ".json"));

    @Test
    public void testImageServer() throws Exception {
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
        System.out.println(array);
    }

    @Test
    public void testImageServerMultipart() throws Exception {
        JsonNode jsonNode = Unirest.post("http://localhost:9060/transformimage").header("accept", "application/json").field("file1", new ClassPathResource("datavec-spark-inference/testimages/class0/0.jpg").getFile()).field("file2", new ClassPathResource("datavec-spark-inference/testimages/class0/1.png").getFile()).field("file3", new ClassPathResource("datavec-spark-inference/testimages/class0/2.jpg").getFile()).asJson().getBody();
        INDArray batchResult = getNDArray(jsonNode);
        Assert.assertEquals(3, batchResult.size(0));
        System.out.println(batchResult);
    }

    @Test
    public void testImageServerSingleMultipart() throws Exception {
        File f = testDir.newFolder();
        File imgFile = new ClassPathResource("datavec-spark-inference/testimages/class0/0.jpg").getTempFileFromArchive(f);
        JsonNode jsonNode = Unirest.post("http://localhost:9060/transformimage").header("accept", "application/json").field("file1", imgFile).asJson().getBody();
        INDArray result = getNDArray(jsonNode);
        Assert.assertEquals(1, result.size(0));
        System.out.println(result);
    }
}

