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
package org.apache.camel.component.restlet;


import org.junit.Test;


public class RestletPostXmlRouteTest extends RestletTestSupport {
    private static final String REQUEST_MESSAGE = "<mail><body>HelloWorld!</body><subject>test</subject><to>x@y.net</to></mail>";

    private static final String REQUEST_MESSAGE_WITH_XML_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + (RestletPostXmlRouteTest.REQUEST_MESSAGE);

    private String url = ("restlet:http://localhost:" + (RestletTestSupport.portNum)) + "/users?restletMethod=POST";

    @Test
    public void testPostXml() throws Exception {
        postRequestMessage(RestletPostXmlRouteTest.REQUEST_MESSAGE);
    }

    @Test
    public void testPostXmlWithXmlTag() throws Exception {
        postRequestMessage(RestletPostXmlRouteTest.REQUEST_MESSAGE_WITH_XML_TAG);
    }
}

