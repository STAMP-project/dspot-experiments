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
package org.apache.camel.component.language;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.Expression;
import org.junit.Assert;
import org.junit.Test;


public class LanguageCacheScriptTest extends ContextTestSupport {
    private LanguageEndpoint endpoint;

    @Test
    public void testCache() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("World", "Camel");
        template.sendBody("direct:start", "World");
        Expression first = endpoint.getExpression();
        template.sendBody("direct:start", "Camel");
        Expression second = endpoint.getExpression();
        assertMockEndpointsSatisfied();
        Assert.assertSame(first, second);
        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
    }
}

