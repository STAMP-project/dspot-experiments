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
package org.apache.camel.issues;


import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.RollbackExchangeException;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class OnCompletionIssueTest extends ContextTestSupport {
    @Test
    public void testOnCompletionIssue() throws Exception {
        MockEndpoint end = getMockEndpoint("mock:end");
        end.expectedMessageCount(1);
        MockEndpoint complete = getMockEndpoint("mock:complete");
        complete.expectedBodiesReceivedInAnyOrder("finish", "stop", "ile", "markRollback");
        MockEndpoint failed = getMockEndpoint("mock:failed");
        failed.expectedBodiesReceivedInAnyOrder("faulted", "npe", "rollback");
        template.sendBody("direct:input", "finish");
        template.sendBody("direct:input", "stop");
        template.sendBody("direct:input", "fault");
        template.sendBody("direct:input", "ile");
        template.sendBody("direct:input", "markRollback");
        try {
            template.sendBody("direct:input", "npe");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            Assert.assertEquals("Darn NPE", e.getCause().getMessage());
        }
        try {
            template.sendBody("direct:input", "rollback");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            TestSupport.assertIsInstanceOf(RollbackExchangeException.class, e.getCause());
        }
        assertMockEndpointsSatisfied();
    }
}

