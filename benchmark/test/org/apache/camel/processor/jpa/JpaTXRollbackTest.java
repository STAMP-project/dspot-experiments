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
package org.apache.camel.processor.jpa;


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.examples.SendEmail;
import org.junit.Test;


public class JpaTXRollbackTest extends AbstractJpaTest {
    protected static final String SELECT_ALL_STRING = ("select x from " + (SendEmail.class.getName())) + " x";

    private static AtomicInteger foo = new AtomicInteger();

    private static AtomicInteger bar = new AtomicInteger();

    @Test
    public void testTXRollback() throws Exception {
        // first create three records
        template.sendBody(("jpa://" + (SendEmail.class.getName())), new SendEmail("foo@beer.org"));
        template.sendBody(("jpa://" + (SendEmail.class.getName())), new SendEmail("bar@beer.org"));
        template.sendBody(("jpa://" + (SendEmail.class.getName())), new SendEmail("kaboom@beer.org"));
        // should rollback the entire
        MockEndpoint mock = getMockEndpoint("mock:result");
        // we should retry and try again
        mock.expectedMinimumMessageCount(4);
        // start route
        context.getRouteController().startRoute("foo");
        assertMockEndpointsSatisfied();
        assertTrue(("Should be >= 2, was: " + (JpaTXRollbackTest.foo.intValue())), ((JpaTXRollbackTest.foo.intValue()) >= 2));
        assertTrue(("Should be >= 2, was: " + (JpaTXRollbackTest.bar.intValue())), ((JpaTXRollbackTest.bar.intValue()) >= 2));
    }
}

