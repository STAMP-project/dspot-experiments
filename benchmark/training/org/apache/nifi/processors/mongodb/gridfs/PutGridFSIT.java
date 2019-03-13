/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.nifi.processors.mongodb.gridfs;


import CoreAttributes.FILENAME;
import PutGridFS.CHUNK_SIZE;
import PutGridFS.PROPERTIES_PREFIX;
import PutGridFS.REL_DUPLICATE;
import PutGridFS.REL_FAILURE;
import PutGridFS.REL_SUCCESS;
import PutGridFS.UNIQUE_BOTH;
import PutGridFS.UNIQUE_HASH;
import PutGridFS.UNIQUE_NAME;
import com.mongodb.client.MongoCollection;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.util.TestRunner;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;


public class PutGridFSIT extends GridFSITTestBase {
    TestRunner runner;

    static final String BUCKET = "put_test_bucket";

    @Test
    public void testSimplePut() {
        final String fileName = "simple_test.txt";
        Map<String, String> attrs = new HashMap<>();
        attrs.put(FILENAME.key(), fileName);
        runner.enqueue("12345", attrs);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Assert.assertTrue("File does not exist", fileExists(fileName, PutGridFSIT.BUCKET));
    }

    @Test
    public void testWithProperties() {
        final String fileName = "simple_test_props.txt";
        Map<String, String> attrs = new HashMap<>();
        attrs.put(FILENAME.key(), fileName);
        attrs.put("prop.created_by", "john.smith");
        attrs.put("prop.created_for", "jane.doe");
        attrs.put("prop.restrictions", "PHI&PII");
        attrs.put("prop.department", "Accounting");
        runner.setProperty(PROPERTIES_PREFIX, "prop");
        runner.enqueue("12345", attrs);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        attrs = new HashMap<String, String>() {
            {
                put("created_by", "john.smith");
                put("created_for", "jane.doe");
                put("restrictions", "PHI&PII");
                put("department", "Accounting");
            }
        };
        Assert.assertTrue("File does not exist", fileExists(fileName, PutGridFSIT.BUCKET));
        Assert.assertTrue("File is missing PARENT_PROPERTIES", fileHasProperties(fileName, PutGridFSIT.BUCKET, attrs));
    }

    @Test
    public void testNoUniqueness() {
        String fileName = "test_duplicates.txt";
        Map<String, String> attrs = new HashMap<>();
        attrs.put(FILENAME.key(), fileName);
        for (int x = 0; x < 10; x++) {
            runner.enqueue("Duplicates are ok.", attrs);
            runner.run();
        }
        runner.assertTransferCount(REL_SUCCESS, 10);
        String bucketName = String.format("%s.files", PutGridFSIT.BUCKET);
        MongoCollection files = client.getDatabase(GridFSITTestBase.DB).getCollection(bucketName);
        Document query = Document.parse(String.format("{\"filename\": \"%s\"}", fileName));
        long count = files.count(query);
        Assert.assertTrue("Wrong count", (count == 10));
    }

    @Test
    public void testFileNameUniqueness() {
        String fileName = "test_duplicates.txt";
        Map<String, String> attrs = new HashMap<>();
        attrs.put(FILENAME.key(), fileName);
        testUniqueness(attrs, "Hello, world", UNIQUE_NAME);
    }

    @Test
    public void testFileNameAndHashUniqueness() {
        testHashUniqueness(UNIQUE_BOTH);
    }

    @Test
    public void testHashUniqueness() {
        testHashUniqueness(UNIQUE_HASH);
    }

    @Test
    public void testChunkSize() {
        String[] chunkSizes = new String[]{ "128 KB", "256 KB", "384 KB", "512KB", "768KB", "1024 KB" };
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < 10000; x++) {
            sb.append("This is a test string used to build up a largish text file.");
        }
        final String testData = sb.toString();
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(FILENAME.key(), "big-putgridfs-test-file.txt");
        for (String chunkSize : chunkSizes) {
            runner.setProperty(CHUNK_SIZE, chunkSize);
            runner.enqueue(testData, attrs);
            runner.run();
            runner.assertTransferCount(REL_FAILURE, 0);
            runner.assertTransferCount(REL_DUPLICATE, 0);
            runner.assertTransferCount(REL_SUCCESS, 1);
            runner.clearTransferState();
        }
        runner.setProperty(CHUNK_SIZE, "${gridfs.chunk.size}");
        attrs.put("gridfs.chunk.size", "768 KB");
        runner.enqueue(testData, attrs);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_DUPLICATE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
    }
}

