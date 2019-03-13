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


import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.OzoneClientStub;
import org.apache.hadoop.ozone.s3.endpoint.MultiDeleteRequest.DeleteObject;
import org.apache.hadoop.ozone.s3.exception.OS3Exception;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test object multi delete.
 */
public class TestObjectMultiDelete {
    @Test
    public void delete() throws IOException, JAXBException, OS3Exception {
        // GIVEN
        OzoneClient client = new OzoneClientStub();
        OzoneBucket bucket = initTestData(client);
        BucketEndpoint rest = new BucketEndpoint();
        rest.setClient(client);
        MultiDeleteRequest mdr = new MultiDeleteRequest();
        mdr.getObjects().add(new DeleteObject("key1"));
        mdr.getObjects().add(new DeleteObject("key2"));
        mdr.getObjects().add(new DeleteObject("key4"));
        // WHEN
        MultiDeleteResponse response = rest.multiDelete("b1", "", mdr);
        // THEN
        Set<String> keysAtTheEnd = Sets.newHashSet(bucket.listKeys("")).stream().map(OzoneKey::getName).collect(Collectors.toSet());
        Set<String> expectedResult = new HashSet<>();
        expectedResult.add("key3");
        // THEN
        Assert.assertEquals(expectedResult, keysAtTheEnd);
        Assert.assertEquals(3, response.getDeletedObjects().size());
        Assert.assertEquals(0, response.getErrors().size());
    }

    @Test
    public void deleteQuiet() throws IOException, JAXBException, OS3Exception {
        // GIVEN
        OzoneClient client = new OzoneClientStub();
        OzoneBucket bucket = initTestData(client);
        BucketEndpoint rest = new BucketEndpoint();
        rest.setClient(client);
        MultiDeleteRequest mdr = new MultiDeleteRequest();
        mdr.setQuiet(true);
        mdr.getObjects().add(new DeleteObject("key1"));
        mdr.getObjects().add(new DeleteObject("key2"));
        mdr.getObjects().add(new DeleteObject("key4"));
        // WHEN
        MultiDeleteResponse response = rest.multiDelete("b1", "", mdr);
        // THEN
        Set<String> keysAtTheEnd = Sets.newHashSet(bucket.listKeys("")).stream().map(OzoneKey::getName).collect(Collectors.toSet());
        // THEN
        Assert.assertEquals(0, response.getDeletedObjects().size());
        Assert.assertEquals(0, response.getErrors().size());
    }
}

