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
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;


/**
 * Unit test for idempotent repository.
 */
public class MailIdempotentRepositoryDuplicateTest extends CamelTestSupport {
    MemoryIdempotentRepository myRepo = new MemoryIdempotentRepository();

    @Test
    public void testIdempotent() throws Exception {
        assertEquals(1, myRepo.getCacheSize());
        MockEndpoint mock = getMockEndpoint("mock:result");
        // no 3 is already in the idempotent repo
        mock.expectedBodiesReceived("Message 0", "Message 1", "Message 2", "Message 4");
        context.getRouteController().startRoute("foo");
        assertMockEndpointsSatisfied();
        // windows need a little slack
        Thread.sleep(500);
        assertEquals(0, Mailbox.get("jones@localhost").getNewMessageCount());
        // they are removed on confirm
        assertEquals(1, myRepo.getCacheSize());
    }
}

