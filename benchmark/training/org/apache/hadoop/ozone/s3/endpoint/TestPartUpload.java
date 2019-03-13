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


import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import javax.ws.rs.core.Response;
import org.apache.hadoop.ozone.s3.exception.OS3Exception;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests Upload part request.
 */
public class TestPartUpload {
    private static final ObjectEndpoint REST = new ObjectEndpoint();

    private static final String BUCKET = "s3bucket";

    private static final String KEY = "key1";

    @Test
    public void testPartUpload() throws Exception {
        Response response = TestPartUpload.REST.multipartUpload(TestPartUpload.BUCKET, TestPartUpload.KEY, "", "", null);
        MultipartUploadInitiateResponse multipartUploadInitiateResponse = ((MultipartUploadInitiateResponse) (response.getEntity()));
        Assert.assertNotNull(multipartUploadInitiateResponse.getUploadID());
        String uploadID = multipartUploadInitiateResponse.getUploadID();
        Assert.assertEquals(response.getStatus(), 200);
        String content = "Multipart Upload";
        ByteArrayInputStream body = new ByteArrayInputStream(content.getBytes());
        response = TestPartUpload.REST.put(TestPartUpload.BUCKET, TestPartUpload.KEY, content.length(), 1, uploadID, body);
        Assert.assertNotNull(response.getHeaderString("ETag"));
    }

    @Test
    public void testPartUploadWithOverride() throws Exception {
        Response response = TestPartUpload.REST.multipartUpload(TestPartUpload.BUCKET, TestPartUpload.KEY, "", "", null);
        MultipartUploadInitiateResponse multipartUploadInitiateResponse = ((MultipartUploadInitiateResponse) (response.getEntity()));
        Assert.assertNotNull(multipartUploadInitiateResponse.getUploadID());
        String uploadID = multipartUploadInitiateResponse.getUploadID();
        Assert.assertEquals(response.getStatus(), 200);
        String content = "Multipart Upload";
        ByteArrayInputStream body = new ByteArrayInputStream(content.getBytes());
        response = TestPartUpload.REST.put(TestPartUpload.BUCKET, TestPartUpload.KEY, content.length(), 1, uploadID, body);
        Assert.assertNotNull(response.getHeaderString("ETag"));
        String eTag = response.getHeaderString("ETag");
        // Upload part again with same part Number, the ETag should be changed.
        content = "Multipart Upload Changed";
        response = TestPartUpload.REST.put(TestPartUpload.BUCKET, TestPartUpload.KEY, content.length(), 1, uploadID, body);
        Assert.assertNotNull(response.getHeaderString("ETag"));
        Assert.assertNotEquals(eTag, response.getHeaderString("ETag"));
    }

    @Test
    public void testPartUploadWithIncorrectUploadID() throws Exception {
        try {
            String content = "Multipart Upload With Incorrect uploadID";
            ByteArrayInputStream body = new ByteArrayInputStream(content.getBytes());
            TestPartUpload.REST.put(TestPartUpload.BUCKET, TestPartUpload.KEY, content.length(), 1, "random", body);
            Assert.fail("testPartUploadWithIncorrectUploadID failed");
        } catch (OS3Exception ex) {
            Assert.assertEquals("NoSuchUpload", ex.getCode());
            Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, ex.getHttpCode());
        }
    }
}

