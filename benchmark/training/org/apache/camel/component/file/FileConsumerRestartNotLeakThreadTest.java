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
package org.apache.camel.component.file;


import Exchange.FILE_NAME;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class FileConsumerRestartNotLeakThreadTest extends ContextTestSupport {
    @Test
    public void testLeak() throws Exception {
        int before = Thread.activeCount();
        getMockEndpoint("mock:foo").expectedMessageCount(1);
        template.sendBodyAndHeader("file:target/data/leak", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
        for (int i = 0; i < 50; i++) {
            context.getRouteController().stopRoute("foo");
            context.getRouteController().startRoute("foo");
        }
        resetMocks();
        getMockEndpoint("mock:foo").expectedMessageCount(1);
        template.sendBodyAndHeader("file:target/data/leak", "Bye World", FILE_NAME, "bye.txt");
        assertMockEndpointsSatisfied();
        int active = (Thread.activeCount()) - before;
        log.info("Active threads after restarts: {}", active);
        Assert.assertTrue(("There should not be so many active threads, was " + active), (active < 10));
    }
}

