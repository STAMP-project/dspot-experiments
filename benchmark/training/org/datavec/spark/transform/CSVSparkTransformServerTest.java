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
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.spark.transform.model.Base64NDArrayBody;
import org.datavec.spark.transform.model.BatchCSVRecord;
import org.datavec.spark.transform.model.SingleCSVRecord;
import org.junit.Test;


/**
 * Created by agibsonccc on 1/22/17.
 */
public class CSVSparkTransformServerTest {
    private static CSVSparkTransformServer server;

    private static Schema schema = new Schema.Builder().addColumnDouble("1.0").addColumnDouble("2.0").build();

    private static TransformProcess transformProcess = convertToDouble("2.0").build();

    private static File fileSave = new File(((UUID.randomUUID().toString()) + ".json"));

    @Test
    public void testServer() throws Exception {
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
    }
}

