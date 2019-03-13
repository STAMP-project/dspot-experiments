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
package org.apache.camel.processor;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Test cases for dealing with stream types in an exception handler
 */
public class ExceptionHandlerStreamCacheTest extends ContextTestSupport {
    private MockEndpoint successEndpoint;

    private MockEndpoint exceptionEndpoint;

    @Test
    public void testSendFault() throws Exception {
        doTestInputStreamPayload("fault");
    }

    @Test
    public void testSendError() throws Exception {
        doTestInputStreamPayload("error");
    }

    @Test
    public void testSendFaultXml() throws Exception {
        doTestXmlPayload("<fault/>");
    }

    @Test
    public void testSendErrorXml() throws Exception {
        doTestXmlPayload("<error/>");
    }
}

