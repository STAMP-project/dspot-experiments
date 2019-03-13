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
package org.apache.camel.component.mail;


import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;


/**
 * This is a test that checks integration of the sort term in Camel. The actual sorting logic is tested in the
 * SortUtilTest.
 */
public class MailSortTermThreeTest extends CamelTestSupport {
    @Test
    public void testSortTerm() throws Exception {
        Mailbox mailbox = Mailbox.get("bill@localhost");
        assertEquals(3, mailbox.size());
        // This one has search term set
        MockEndpoint mockDescImap = getMockEndpoint("mock:resultDescendingImap");
        mockDescImap.expectedBodiesReceived("Even later date", "Later date", "Earlier date");
        context.getRouteController().startAllRoutes();
        assertMockEndpointsSatisfied();
    }
}

