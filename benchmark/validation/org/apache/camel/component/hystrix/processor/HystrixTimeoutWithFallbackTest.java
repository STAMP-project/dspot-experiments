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
package org.apache.camel.component.hystrix.processor;


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Hystrix using timeout and fallback with Java DSL
 */
public class HystrixTimeoutWithFallbackTest extends CamelTestSupport {
    @Test
    public void testFast() throws Exception {
        // this calls the fast route and therefore we get a response
        Object out = template.requestBody("direct:start", "fast");
        assertEquals("Fast response", out);
    }

    @Test
    public void testSlow() throws Exception {
        // this calls the slow route and therefore causes a timeout which triggers the fallback
        Object out = template.requestBody("direct:start", "slow");
        assertEquals("Fallback response", out);
    }
}

