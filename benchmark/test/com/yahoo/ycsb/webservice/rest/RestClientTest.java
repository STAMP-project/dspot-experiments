/**
 * Copyright (c) 2016 YCSB contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.yahoo.ycsb.webservice.rest;


import Status.ERROR;
import Status.FORBIDDEN;
import Status.NOT_FOUND;
import Status.NOT_IMPLEMENTED;
import Status.OK;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.Status;
import com.yahoo.ycsb.StringByteIterator;
import java.util.HashMap;
import org.apache.catalina.startup.Tomcat;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases to verify the {@link RestClient} of the rest-binding
 * module. It performs these steps in order. 1. Runs an embedded Tomcat
 * server with a mock RESTFul web service. 2. Invokes the {@link RestClient}
 * class for all the various methods which make HTTP calls to the mock REST
 * service. 3. Compares the response from such calls to the mock REST
 * service with the response expected. 4. Stops the embedded Tomcat server.
 * Cases for verifying the handling of different HTTP status like 2xx, 4xx &
 * 5xx have been included in success and failure test cases.
 */
public class RestClientTest {
    private static Integer port = 8080;

    private static Tomcat tomcat;

    private static RestClient rc = new RestClient();

    private static final String RESPONSE_TAG = "response";

    private static final String DATA_TAG = "data";

    private static final String VALID_RESOURCE = "resource_valid";

    private static final String INVALID_RESOURCE = "resource_invalid";

    private static final String ABSENT_RESOURCE = "resource_absent";

    private static final String UNAUTHORIZED_RESOURCE = "resource_unauthorized";

    private static final String INPUT_DATA = "<field1>one</field1><field2>two</field2>";

    // Read success.
    @Test
    public void read_200() {
        HashMap<String, ByteIterator> result = new HashMap<String, ByteIterator>();
        Status status = RestClientTest.rc.read(null, RestClientTest.VALID_RESOURCE, null, result);
        Assert.assertEquals(OK, status);
        Assert.assertEquals(result.get(RestClientTest.RESPONSE_TAG).toString(), ("HTTP GET response to: " + (RestClientTest.VALID_RESOURCE)));
    }

    // Unauthorized request error.
    @Test
    public void read_403() {
        HashMap<String, ByteIterator> result = new HashMap<String, ByteIterator>();
        Status status = RestClientTest.rc.read(null, RestClientTest.UNAUTHORIZED_RESOURCE, null, result);
        Assert.assertEquals(FORBIDDEN, status);
    }

    // Not found error.
    @Test
    public void read_404() {
        HashMap<String, ByteIterator> result = new HashMap<String, ByteIterator>();
        Status status = RestClientTest.rc.read(null, RestClientTest.ABSENT_RESOURCE, null, result);
        Assert.assertEquals(NOT_FOUND, status);
    }

    // Server error.
    @Test
    public void read_500() {
        HashMap<String, ByteIterator> result = new HashMap<String, ByteIterator>();
        Status status = RestClientTest.rc.read(null, RestClientTest.INVALID_RESOURCE, null, result);
        Assert.assertEquals(ERROR, status);
    }

    // Insert success.
    @Test
    public void insert_200() {
        HashMap<String, ByteIterator> data = new HashMap<String, ByteIterator>();
        data.put(RestClientTest.DATA_TAG, new StringByteIterator(RestClientTest.INPUT_DATA));
        Status status = RestClientTest.rc.insert(null, RestClientTest.VALID_RESOURCE, data);
        Assert.assertEquals(OK, status);
    }

    @Test
    public void insert_403() {
        HashMap<String, ByteIterator> data = new HashMap<String, ByteIterator>();
        data.put(RestClientTest.DATA_TAG, new StringByteIterator(RestClientTest.INPUT_DATA));
        Status status = RestClientTest.rc.insert(null, RestClientTest.UNAUTHORIZED_RESOURCE, data);
        Assert.assertEquals(FORBIDDEN, status);
    }

    @Test
    public void insert_404() {
        HashMap<String, ByteIterator> data = new HashMap<String, ByteIterator>();
        data.put(RestClientTest.DATA_TAG, new StringByteIterator(RestClientTest.INPUT_DATA));
        Status status = RestClientTest.rc.insert(null, RestClientTest.ABSENT_RESOURCE, data);
        Assert.assertEquals(NOT_FOUND, status);
    }

    @Test
    public void insert_500() {
        HashMap<String, ByteIterator> data = new HashMap<String, ByteIterator>();
        data.put(RestClientTest.DATA_TAG, new StringByteIterator(RestClientTest.INPUT_DATA));
        Status status = RestClientTest.rc.insert(null, RestClientTest.INVALID_RESOURCE, data);
        Assert.assertEquals(ERROR, status);
    }

    // Delete success.
    @Test
    public void delete_200() {
        Status status = RestClientTest.rc.delete(null, RestClientTest.VALID_RESOURCE);
        Assert.assertEquals(OK, status);
    }

    @Test
    public void delete_403() {
        Status status = RestClientTest.rc.delete(null, RestClientTest.UNAUTHORIZED_RESOURCE);
        Assert.assertEquals(FORBIDDEN, status);
    }

    @Test
    public void delete_404() {
        Status status = RestClientTest.rc.delete(null, RestClientTest.ABSENT_RESOURCE);
        Assert.assertEquals(NOT_FOUND, status);
    }

    @Test
    public void delete_500() {
        Status status = RestClientTest.rc.delete(null, RestClientTest.INVALID_RESOURCE);
        Assert.assertEquals(ERROR, status);
    }

    @Test
    public void update_200() {
        HashMap<String, ByteIterator> data = new HashMap<String, ByteIterator>();
        data.put(RestClientTest.DATA_TAG, new StringByteIterator(RestClientTest.INPUT_DATA));
        Status status = RestClientTest.rc.update(null, RestClientTest.VALID_RESOURCE, data);
        Assert.assertEquals(OK, status);
    }

    @Test
    public void update_403() {
        HashMap<String, ByteIterator> data = new HashMap<String, ByteIterator>();
        data.put(RestClientTest.DATA_TAG, new StringByteIterator(RestClientTest.INPUT_DATA));
        Status status = RestClientTest.rc.update(null, RestClientTest.UNAUTHORIZED_RESOURCE, data);
        Assert.assertEquals(FORBIDDEN, status);
    }

    @Test
    public void update_404() {
        HashMap<String, ByteIterator> data = new HashMap<String, ByteIterator>();
        data.put(RestClientTest.DATA_TAG, new StringByteIterator(RestClientTest.INPUT_DATA));
        Status status = RestClientTest.rc.update(null, RestClientTest.ABSENT_RESOURCE, data);
        Assert.assertEquals(NOT_FOUND, status);
    }

    @Test
    public void update_500() {
        HashMap<String, ByteIterator> data = new HashMap<String, ByteIterator>();
        data.put(RestClientTest.DATA_TAG, new StringByteIterator(RestClientTest.INPUT_DATA));
        Status status = RestClientTest.rc.update(null, RestClientTest.INVALID_RESOURCE, data);
        Assert.assertEquals(ERROR, status);
    }

    @Test
    public void scan() {
        Assert.assertEquals(NOT_IMPLEMENTED, RestClientTest.rc.scan(null, null, 0, null, null));
    }
}

