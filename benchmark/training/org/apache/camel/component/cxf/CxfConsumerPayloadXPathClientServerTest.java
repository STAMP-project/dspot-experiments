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
package org.apache.camel.component.cxf;


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class CxfConsumerPayloadXPathClientServerTest extends CamelTestSupport {
    private static final String ECHO_RESPONSE = "<ns1:echoResponse xmlns:ns1=\"http://cxf.component.camel.apache.org/\">" + ("<return xmlns=\"http://cxf.component.camel.apache.org/\">echo Hello World!</return>" + "</ns1:echoResponse>");

    protected final String simpleEndpointAddress = ((("http://localhost:" + (CXFTestSupport.getPort1())) + "/") + (getClass().getSimpleName())) + "/test";

    protected final String simpleEndpointURI = ("cxf://" + (simpleEndpointAddress)) + "?serviceClass=org.apache.camel.component.cxf.HelloService";

    private String testMessage;

    private String receivedMessageCxfPayloadApplyingXPath;

    private String receivedMessageByDom;

    private String receivedMessageStringApplyingXPath;

    @Test
    public void testMessageWithIncreasingSize() throws Exception {
        execTest(1);
        execTest(10);
        execTest(100);
        execTest(1000);
        execTest(10000);
        execTest(100000);
    }
}

