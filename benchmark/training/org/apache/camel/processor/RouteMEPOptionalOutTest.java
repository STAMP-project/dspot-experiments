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
package org.apache.camel.processor;


import ExchangePattern.InOnly;
import ExchangePattern.InOptionalOut;
import ExchangePattern.InOut;
import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for in optional out
 */
public class RouteMEPOptionalOutTest extends ContextTestSupport {
    @Test
    public void testHasOut() {
        Object out = template.requestBody("direct:start", "Hi");
        Assert.assertEquals("Bye World", out);
    }

    @Test
    public void testHasNotOutForInOptionalOut() {
        // OUT is optional in the route but we should not get a response
        Object out = template.sendBody("direct:noout", InOptionalOut, "Hi");
        Assert.assertEquals(null, out);
    }

    @Test
    public void testHasNotOutForInOut() {
        // OUT is optional in the route but we should still not get a response
        Object out = template.sendBody("direct:noout", InOut, "Hi");
        Assert.assertEquals(null, out);
    }

    @Test
    public void testHasNotOutForInOnly() {
        Object out = template.sendBody("direct:noout", InOnly, "Hi");
        Assert.assertEquals(null, out);
    }

    @Test
    public void testInOnly() {
        Object out = template.sendBody("direct:inonly", InOnly, "Hi");
        Assert.assertEquals(null, out);
    }
}

