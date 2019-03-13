/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.gcp.bigquery;


import AbstractBigQueryProcessor.REL_FAILURE;
import AbstractBigQueryProcessor.REL_SUCCESS;
import AbstractGCPProcessor.GCP_CREDENTIALS_PROVIDER_SERVICE;
import BigQueryAttributes.DATASET_ATTR;
import BigQueryAttributes.MAX_BADRECORDS_ATTR;
import BigQueryAttributes.SOURCE_TYPE_ATTR;
import BigQueryAttributes.TABLE_NAME_ATTR;
import BigQueryAttributes.TABLE_SCHEMA_ATTR;
import com.google.cloud.bigquery.FormatOptions;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.MockFlowFile;
import org.junit.Test;


public class PutBigQueryBatchIT extends AbstractBigQueryIT {
    private static final String TABLE_SCHEMA_STRING = "[\n" + (((((((((((((((((("  {\n" + "    \"description\": \"field 1\",\n") + "    \"mode\": \"REQUIRED\",\n") + "    \"name\": \"field_1\",\n") + "    \"type\": \"STRING\"\n") + "  },\n") + "  {\n") + "    \"description\": \"field 2\",\n") + "    \"mode\": \"REQUIRED\",\n") + "    \"name\": \"field_2\",\n") + "    \"type\": \"STRING\"\n") + "  },\n") + "  {\n") + "    \"description\": \"field 3\",\n") + "    \"mode\": \"NULLABLE\",\n") + "    \"name\": \"field_3\",\n") + "    \"type\": \"STRING\"\n") + "  }\n") + "]");

    @Test
    public void PutBigQueryBatchSmallPayloadTest() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        AbstractBigQueryIT.runner = setCredentialsControllerService(AbstractBigQueryIT.runner);
        AbstractBigQueryIT.runner.setProperty(GCP_CREDENTIALS_PROVIDER_SERVICE, AbstractBigQueryIT.CONTROLLER_SERVICE);
        AbstractBigQueryIT.runner.setProperty(DATASET_ATTR, AbstractBigQueryIT.dataset.getDatasetId().getDataset());
        AbstractBigQueryIT.runner.setProperty(TABLE_NAME_ATTR, methodName);
        AbstractBigQueryIT.runner.setProperty(SOURCE_TYPE_ATTR, FormatOptions.json().getType());
        AbstractBigQueryIT.runner.setProperty(TABLE_SCHEMA_ATTR, PutBigQueryBatchIT.TABLE_SCHEMA_STRING);
        String str = "{\"field_1\":\"Daniel is great\",\"field_2\":\"Daniel is great\"}\r\n";
        AbstractBigQueryIT.runner.enqueue(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
        AbstractBigQueryIT.runner.run(1);
        for (MockFlowFile flowFile : AbstractBigQueryIT.runner.getFlowFilesForRelationship(REL_SUCCESS)) {
            AbstractBigQueryIT.validateNoServiceExceptionAttribute(flowFile);
        }
        AbstractBigQueryIT.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void PutBigQueryBatchBadRecordTest() throws Exception {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        AbstractBigQueryIT.runner = setCredentialsControllerService(AbstractBigQueryIT.runner);
        AbstractBigQueryIT.runner.setProperty(GCP_CREDENTIALS_PROVIDER_SERVICE, AbstractBigQueryIT.CONTROLLER_SERVICE);
        AbstractBigQueryIT.runner.setProperty(DATASET_ATTR, AbstractBigQueryIT.dataset.getDatasetId().getDataset());
        AbstractBigQueryIT.runner.setProperty(TABLE_NAME_ATTR, methodName);
        AbstractBigQueryIT.runner.setProperty(SOURCE_TYPE_ATTR, FormatOptions.json().getType());
        AbstractBigQueryIT.runner.setProperty(TABLE_SCHEMA_ATTR, PutBigQueryBatchIT.TABLE_SCHEMA_STRING);
        String str = "{\"field_1\":\"Daniel is great\"}\r\n";
        AbstractBigQueryIT.runner.enqueue(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
        AbstractBigQueryIT.runner.run(1);
        AbstractBigQueryIT.runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void PutBigQueryBatchLargePayloadTest() throws IOException, InitializationException {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        AbstractBigQueryIT.runner = setCredentialsControllerService(AbstractBigQueryIT.runner);
        AbstractBigQueryIT.runner.setProperty(GCP_CREDENTIALS_PROVIDER_SERVICE, AbstractBigQueryIT.CONTROLLER_SERVICE);
        AbstractBigQueryIT.runner.setProperty(DATASET_ATTR, AbstractBigQueryIT.dataset.getDatasetId().getDataset());
        AbstractBigQueryIT.runner.setProperty(TABLE_NAME_ATTR, methodName);
        AbstractBigQueryIT.runner.setProperty(SOURCE_TYPE_ATTR, FormatOptions.json().getType());
        AbstractBigQueryIT.runner.setProperty(TABLE_SCHEMA_ATTR, PutBigQueryBatchIT.TABLE_SCHEMA_STRING);
        // Allow one bad record to deal with the extra line break.
        AbstractBigQueryIT.runner.setProperty(MAX_BADRECORDS_ATTR, String.valueOf(1));
        String str = "{\"field_1\":\"Daniel is great\",\"field_2\":\"Here\'s to the crazy ones. The misfits. The rebels. The troublemakers." + (((" The round pegs in the square holes. The ones who see things differently. They're not fond of rules. And they have no respect" + " for the status quo. You can quote them, disagree with them, glorify or vilify them. About the only thing you can't do is ignore") + " them. Because they change things. They push the human race forward. And while some may see them as the crazy ones, we see genius.") + " Because the people who are crazy enough to think they can change the world, are the ones who do.\"}\n");
        Path tempFile = Files.createTempFile(methodName, "");
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
            for (int i = 0; i < 2; i++) {
                for (int ii = 0; ii < 1000000; ii++) {
                    writer.write(str);
                }
                writer.flush();
            }
            writer.flush();
        }
        AbstractBigQueryIT.runner.enqueue(tempFile);
        AbstractBigQueryIT.runner.run(1);
        for (MockFlowFile flowFile : AbstractBigQueryIT.runner.getFlowFilesForRelationship(REL_SUCCESS)) {
            AbstractBigQueryIT.validateNoServiceExceptionAttribute(flowFile);
        }
        AbstractBigQueryIT.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }
}

