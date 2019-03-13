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


import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.bean.ProxyHelper;
import org.junit.Assert;
import org.junit.Test;


public class ProxyReturnNullIssueTest extends ContextTestSupport {
    @Test
    public void testEcho() throws Exception {
        ProxyReturnNullIssueTest.Echo service = ProxyHelper.createProxy(context.getEndpoint("direct:echo"), ProxyReturnNullIssueTest.Echo.class);
        Assert.assertEquals("Hello World", service.echo("Hello World"));
    }

    @Test
    public void testEchoNull() throws Exception {
        ProxyReturnNullIssueTest.Echo service = ProxyHelper.createProxy(context.getEndpoint("direct:echo"), ProxyReturnNullIssueTest.Echo.class);
        Assert.assertEquals(null, service.echo(null));
    }

    public interface Echo {
        String echo(String text);
    }

    public static class MyEchoBean implements ProxyReturnNullIssueTest.Echo {
        public String echo(String text) {
            return text;
        }
    }
}

