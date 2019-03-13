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


import java.io.IOException;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Protocol;


public class RestletRouteBuilderWithSpacesTest extends RestletTestSupport {
    @Test
    public void testConsumerWithSpaces() throws IOException {
        Client client = new Client(Protocol.HTTP);
        Response response = client.handle(new org.restlet.Request(Method.GET, (("http://localhost:" + (RestletTestSupport.portNum)) + "/orders with spaces in path/99991/6")));
        assertEquals("received GET request with id=99991 and x=6", response.getEntity().getText());
    }
}

