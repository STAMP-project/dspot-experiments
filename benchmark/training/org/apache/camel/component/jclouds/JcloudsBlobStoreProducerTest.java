/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jclouds;


import JcloudsConstants.BLOB_NAME_LIST;
import JcloudsConstants.CLEAR_CONTAINER;
import JcloudsConstants.CONTAINER_EXISTS;
import JcloudsConstants.CONTAINER_NAME;
import JcloudsConstants.COUNT_BLOBS;
import JcloudsConstants.DELETE_CONTAINER;
import JcloudsConstants.GET;
import JcloudsConstants.OPERATION;
import JcloudsConstants.REMOVE_BLOB;
import JcloudsConstants.REMOVE_BLOBS;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import org.apache.camel.Exchange;
import org.apache.camel.StreamCache;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.converter.stream.StreamCacheConverter;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.junit.Test;
import org.xml.sax.InputSource;


public class JcloudsBlobStoreProducerTest extends CamelTestSupport {
    private static final String TEST_CONTAINER = "testContainer";

    private static final String TEST_BLOB_IN_DIR = "/dir/testBlob";

    private static final String MESSAGE = "<test>This is a test</test>";

    BlobStoreContext blobStoreContext = ContextBuilder.newBuilder("transient").credentials("identity", "credential").build(BlobStoreContext.class);

    BlobStore blobStore = blobStoreContext.getBlobStore();

    @Test
    public void testBlobStorePut() throws InterruptedException {
        MockEndpoint mockEndpoint = resolveMandatoryEndpoint("mock:results", MockEndpoint.class);
        mockEndpoint.expectedMessageCount(1);
        template.sendBody("direct:put", "Some message");
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void testBlobStorePutAndGet() throws InterruptedException {
        String message = "Some message";
        template.sendBody("direct:put-and-get", message);
        Object result = template.requestBodyAndHeader("direct:put-and-get", null, OPERATION, GET, String.class);
        assertEquals(message, result);
    }

    @Test
    public void testBlobStorePutWithStreamAndGet() throws InterruptedException, TransformerException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(JcloudsBlobStoreProducerTest.MESSAGE.getBytes());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        StreamCache streamCache = StreamCacheConverter.convertToStreamCache(new SAXSource(new InputSource(inputStream)), exchange);
        template.sendBody("direct:put-and-get", streamCache);
        Object result = template.requestBodyAndHeader("direct:put-and-get", null, OPERATION, GET, String.class);
        assertEquals(JcloudsBlobStoreProducerTest.MESSAGE, result);
    }

    @Test
    public void testBlobStorePutAndCount() throws InterruptedException {
        String message = "Some message";
        template.sendBody("direct:put-and-count", message);
        Object result = template.requestBodyAndHeader("direct:put-and-count", null, OPERATION, COUNT_BLOBS, Long.class);
        assertEquals(new Long(1), result);
    }

    @Test
    public void testBlobStorePutAndRemove() throws InterruptedException {
        String message = "Some message";
        template.sendBody("direct:put-and-remove", message);
        template.requestBodyAndHeader("direct:put-and-remove", null, OPERATION, REMOVE_BLOB);
        Object result = template.requestBodyAndHeader("direct:put-and-remove", null, OPERATION, COUNT_BLOBS, Long.class);
        assertEquals(new Long(0), result);
    }

    @Test
    public void testBlobStorePutAndClear() throws InterruptedException {
        String message = "Some message";
        template.sendBody("direct:put-and-clear", message);
        Object result = template.requestBodyAndHeader("direct:put-and-count", null, OPERATION, COUNT_BLOBS, Long.class);
        assertEquals(new Long(1), result);
        template.requestBodyAndHeader("direct:put-and-clear", null, OPERATION, CLEAR_CONTAINER);
        result = template.requestBodyAndHeader("direct:put-and-count", null, OPERATION, COUNT_BLOBS, Long.class);
        assertEquals(new Long(0), result);
    }

    @Test
    public void testBlobStorePutAndDeleteContainer() throws InterruptedException {
        String message = "Some message";
        template.sendBody("direct:put-and-delete-container", message);
        Object result = template.requestBodyAndHeader("direct:put-and-count", null, OPERATION, COUNT_BLOBS, Long.class);
        assertEquals(new Long(1), result);
        template.requestBodyAndHeader("direct:put-and-delete-container", null, OPERATION, DELETE_CONTAINER);
    }

    @Test
    public void testCheckContainerExists() throws InterruptedException {
        Object result = template.requestBodyAndHeader("direct:put-and-count", null, OPERATION, CONTAINER_EXISTS, Boolean.class);
        assertEquals(true, result);
        Map<String, Object> headers = new HashMap<>();
        headers.put(OPERATION, CONTAINER_EXISTS);
        headers.put(CONTAINER_NAME, "otherTest");
        result = template.requestBodyAndHeaders("direct:container-exists", null, headers, Boolean.class);
        assertEquals(false, result);
    }

    @Test
    public void testRemoveBlobs() throws InterruptedException {
        template.sendBody("direct:put", "test message");
        Object result = template.requestBodyAndHeader("direct:put-and-count", null, OPERATION, COUNT_BLOBS, Long.class);
        assertEquals(new Long(1), result);
        List blobsToRemove = new ArrayList<>();
        blobsToRemove.add(JcloudsBlobStoreProducerTest.TEST_BLOB_IN_DIR);
        Map<String, Object> headers = new HashMap<>();
        headers.put(OPERATION, REMOVE_BLOBS);
        headers.put(CONTAINER_NAME, JcloudsBlobStoreProducerTest.TEST_CONTAINER);
        headers.put(BLOB_NAME_LIST, blobsToRemove);
        template.sendBodyAndHeaders("direct:remove-blobs", null, headers);
        result = template.requestBodyAndHeader("direct:put-and-count", null, OPERATION, COUNT_BLOBS, Long.class);
        assertEquals(new Long(0), result);
    }
}

