/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.s3.endpoint;


import S3ErrorTable.NO_SUCH_UPLOAD;
import javax.ws.rs.core.Response;
import org.apache.hadoop.ozone.s3.exception.OS3Exception;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class test list parts request.
 */
public class TestListParts {
    private static final ObjectEndpoint REST = new ObjectEndpoint();

    private static final String BUCKET = "s3bucket";

    private static final String KEY = "key1";

    private static String uploadID;

    @Test
    public void testListParts() throws Exception {
        Response response = TestListParts.REST.get(TestListParts.BUCKET, TestListParts.KEY, TestListParts.uploadID, 3, "0", null);
        ListPartsResponse listPartsResponse = ((ListPartsResponse) (response.getEntity()));
        Assert.assertFalse(listPartsResponse.getTruncated());
        Assert.assertTrue(((listPartsResponse.getPartList().size()) == 3));
    }

    @Test
    public void testListPartsContinuation() throws Exception {
        Response response = TestListParts.REST.get(TestListParts.BUCKET, TestListParts.KEY, TestListParts.uploadID, 2, "0", null);
        ListPartsResponse listPartsResponse = ((ListPartsResponse) (response.getEntity()));
        Assert.assertTrue(listPartsResponse.getTruncated());
        Assert.assertTrue(((listPartsResponse.getPartList().size()) == 2));
        // Continue
        response = TestListParts.REST.get(TestListParts.BUCKET, TestListParts.KEY, TestListParts.uploadID, 2, Integer.toString(listPartsResponse.getNextPartNumberMarker()), null);
        listPartsResponse = ((ListPartsResponse) (response.getEntity()));
        Assert.assertFalse(listPartsResponse.getTruncated());
        Assert.assertTrue(((listPartsResponse.getPartList().size()) == 1));
    }

    @Test
    public void testListPartsWithUnknownUploadID() throws Exception {
        try {
            Response response = TestListParts.REST.get(TestListParts.BUCKET, TestListParts.KEY, TestListParts.uploadID, 2, "0", null);
        } catch (OS3Exception ex) {
            Assert.assertEquals(NO_SUCH_UPLOAD.getErrorMessage(), ex.getErrorMessage());
        }
    }
}

