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
package org.apache.hadoop.ozone.s3.endpoint;


import S3ErrorTable.INVALID_PART;
import S3ErrorTable.INVALID_PART_ORDER;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.ozone.client.OzoneClientStub;
import org.apache.hadoop.ozone.s3.endpoint.CompleteMultipartUploadRequest.Part;
import org.apache.hadoop.ozone.s3.exception.OS3Exception;
import org.junit.Assert;
import org.junit.Test;


/**
 * Class to test Multipart upload end to end.
 */
public class TestMultipartUploadComplete {
    private static final ObjectEndpoint REST = new ObjectEndpoint();

    private static final String BUCKET = "s3bucket";

    private static final String KEY = "key1";

    private static final OzoneClientStub CLIENT = new OzoneClientStub();

    @Test
    public void testMultipart() throws Exception {
        // Initiate multipart upload
        String uploadID = initiateMultipartUpload(TestMultipartUploadComplete.KEY);
        List<Part> partsList = new ArrayList<>();
        // Upload parts
        String content = "Multipart Upload 1";
        int partNumber = 1;
        Part part1 = uploadPart(TestMultipartUploadComplete.KEY, uploadID, partNumber, content);
        partsList.add(part1);
        content = "Multipart Upload 2";
        partNumber = 2;
        Part part2 = uploadPart(TestMultipartUploadComplete.KEY, uploadID, partNumber, content);
        partsList.add(part2);
        // complete multipart upload
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest();
        completeMultipartUploadRequest.setPartList(partsList);
        completeMultipartUpload(TestMultipartUploadComplete.KEY, completeMultipartUploadRequest, uploadID);
    }

    @Test
    public void testMultipartInvalidPartOrderError() throws Exception {
        // Initiate multipart upload
        String key = UUID.randomUUID().toString();
        String uploadID = initiateMultipartUpload(key);
        List<Part> partsList = new ArrayList<>();
        // Upload parts
        String content = "Multipart Upload 1";
        int partNumber = 1;
        Part part1 = uploadPart(key, uploadID, partNumber, content);
        // Change part number
        part1.setPartNumber(3);
        partsList.add(part1);
        content = "Multipart Upload 2";
        partNumber = 2;
        Part part2 = uploadPart(key, uploadID, partNumber, content);
        partsList.add(part2);
        // complete multipart upload
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest();
        completeMultipartUploadRequest.setPartList(partsList);
        try {
            completeMultipartUpload(key, completeMultipartUploadRequest, uploadID);
            Assert.fail("testMultipartInvalidPartOrderError");
        } catch (OS3Exception ex) {
            Assert.assertEquals(ex.getCode(), INVALID_PART_ORDER.getCode());
        }
    }

    @Test
    public void testMultipartInvalidPartError() throws Exception {
        // Initiate multipart upload
        String key = UUID.randomUUID().toString();
        String uploadID = initiateMultipartUpload(key);
        List<Part> partsList = new ArrayList<>();
        // Upload parts
        String content = "Multipart Upload 1";
        int partNumber = 1;
        Part part1 = uploadPart(key, uploadID, partNumber, content);
        // Change part number
        part1.seteTag("random");
        partsList.add(part1);
        content = "Multipart Upload 2";
        partNumber = 2;
        Part part2 = uploadPart(key, uploadID, partNumber, content);
        partsList.add(part2);
        // complete multipart upload
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest();
        completeMultipartUploadRequest.setPartList(partsList);
        try {
            completeMultipartUpload(key, completeMultipartUploadRequest, uploadID);
            Assert.fail("testMultipartInvalidPartOrderError");
        } catch (OS3Exception ex) {
            Assert.assertEquals(ex.getCode(), INVALID_PART.getCode());
        }
    }
}

