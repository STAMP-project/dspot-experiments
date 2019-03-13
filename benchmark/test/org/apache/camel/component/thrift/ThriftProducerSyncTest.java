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
package org.apache.camel.component.thrift;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.component.thrift.generated.InvalidOperation;
import org.apache.camel.component.thrift.generated.Operation;
import org.apache.camel.component.thrift.generated.Work;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ThriftProducerSyncTest extends ThriftProducerBaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(ThriftProducerSyncTest.class);

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testCalculateMethodInvocation() throws Exception {
        ThriftProducerSyncTest.LOG.info("Thrift calculate method sync test start");
        List requestBody = new ArrayList();
        requestBody.add(1);
        requestBody.add(new Work(ThriftProducerBaseTest.THRIFT_TEST_NUM1, ThriftProducerBaseTest.THRIFT_TEST_NUM2, Operation.MULTIPLY));
        Object responseBody = template.requestBody("direct:thrift-calculate", requestBody);
        assertNotNull(responseBody);
        assertTrue((responseBody instanceof Integer));
        assertEquals(((ThriftProducerBaseTest.THRIFT_TEST_NUM1) * (ThriftProducerBaseTest.THRIFT_TEST_NUM2)), responseBody);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testAddMethodInvocation() throws Exception {
        ThriftProducerSyncTest.LOG.info("Thrift add method (primitive parameters only) sync test start");
        List requestBody = new ArrayList();
        requestBody.add(ThriftProducerBaseTest.THRIFT_TEST_NUM1);
        requestBody.add(ThriftProducerBaseTest.THRIFT_TEST_NUM2);
        Object responseBody = template.requestBody("direct:thrift-add", requestBody);
        assertNotNull(responseBody);
        assertTrue((responseBody instanceof Integer));
        assertEquals(((ThriftProducerBaseTest.THRIFT_TEST_NUM1) + (ThriftProducerBaseTest.THRIFT_TEST_NUM2)), responseBody);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testCalculateWithException() throws Exception {
        ThriftProducerSyncTest.LOG.info("Thrift calculate method with business exception sync test start");
        List requestBody = new ArrayList();
        requestBody.add(1);
        requestBody.add(new Work(ThriftProducerBaseTest.THRIFT_TEST_NUM1, 0, Operation.DIVIDE));
        try {
            template.requestBody("direct:thrift-calculate", requestBody);
            fail("Expect the exception here");
        } catch (Exception ex) {
            assertTrue("Expect CamelExecutionException", (ex instanceof CamelExecutionException));
            assertTrue("Get an InvalidOperation exception", ((ex.getCause()) instanceof InvalidOperation));
        }
    }

    @Test
    public void testVoidMethodInvocation() throws Exception {
        ThriftProducerSyncTest.LOG.info("Thrift method with empty parameters and void output sync test start");
        Object requestBody = null;
        Object responseBody = template.requestBody("direct:thrift-ping", requestBody);
        assertNull(responseBody);
    }

    @Test
    public void testOneWayMethodInvocation() throws Exception {
        ThriftProducerSyncTest.LOG.info("Thrift one-way method sync test start");
        Object requestBody = null;
        Object responseBody = template.requestBody("direct:thrift-zip", requestBody);
        assertNull(responseBody);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testAllTypesMethodInvocation() throws Exception {
        ThriftProducerSyncTest.LOG.info("Thrift method with all possile types sync test start");
        List requestBody = new ArrayList();
        requestBody.add(true);
        requestBody.add(((byte) (ThriftProducerBaseTest.THRIFT_TEST_NUM1)));
        requestBody.add(((short) (ThriftProducerBaseTest.THRIFT_TEST_NUM1)));
        requestBody.add(ThriftProducerBaseTest.THRIFT_TEST_NUM1);
        requestBody.add(((long) (ThriftProducerBaseTest.THRIFT_TEST_NUM1)));
        requestBody.add(((double) (ThriftProducerBaseTest.THRIFT_TEST_NUM1)));
        requestBody.add("empty");
        requestBody.add(ByteBuffer.allocate(10));
        requestBody.add(new Work(ThriftProducerBaseTest.THRIFT_TEST_NUM1, ThriftProducerBaseTest.THRIFT_TEST_NUM2, Operation.MULTIPLY));
        requestBody.add(new ArrayList<Integer>());
        requestBody.add(new HashSet<String>());
        requestBody.add(new HashMap<String, Long>());
        Object responseBody = template.requestBody("direct:thrift-alltypes", requestBody);
        assertNotNull(responseBody);
        assertTrue((responseBody instanceof Integer));
        assertEquals(1, responseBody);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testEchoMethodInvocation() throws Exception {
        ThriftProducerSyncTest.LOG.info("Thrift echo method (return output as pass input parameter) sync test start");
        List requestBody = new ArrayList();
        requestBody.add(new Work(ThriftProducerBaseTest.THRIFT_TEST_NUM1, ThriftProducerBaseTest.THRIFT_TEST_NUM2, Operation.MULTIPLY));
        Object responseBody = template.requestBody("direct:thrift-echo", requestBody);
        assertNotNull(responseBody);
        assertTrue((responseBody instanceof Work));
        assertEquals(ThriftProducerBaseTest.THRIFT_TEST_NUM1, ((Work) (responseBody)).num1);
        assertEquals(Operation.MULTIPLY, ((Work) (responseBody)).op);
    }
}

