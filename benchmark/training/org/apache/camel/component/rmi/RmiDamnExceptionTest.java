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
package org.apache.camel.component.rmi;


import org.apache.camel.CamelExecutionException;
import org.junit.Test;


public class RmiDamnExceptionTest extends RmiRouteTestSupport {
    @Test
    public void tesDamn() throws Exception {
        if (classPathHasSpaces()) {
            return;
        }
        getMockEndpoint("mock:result").expectedMessageCount(0);
        try {
            template.sendBody("direct:echo", "Hello World");
            fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            assertIsInstanceOf(DamnException.class, e.getCause());
            assertEquals("Damn this did not work", e.getCause().getMessage());
        }
        assertMockEndpointsSatisfied();
    }
}

