/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.security;


import HttpServer2.BIND_ADDRESS;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.FilterContainer;
import org.apache.hadoop.security.authentication.server.AuthenticationFilter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestAuthenticationFilter {
    @SuppressWarnings("unchecked")
    @Test
    public void testConfiguration() throws Exception {
        Configuration conf = new Configuration();
        conf.set("hadoop.http.authentication.foo", "bar");
        conf.set(BIND_ADDRESS, "barhost");
        FilterContainer container = Mockito.mock(FilterContainer.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                Assert.assertEquals("authentication", args[0]);
                Assert.assertEquals(AuthenticationFilter.class.getName(), args[1]);
                Map<String, String> conf = ((Map<String, String>) (args[2]));
                Assert.assertEquals("/", conf.get("cookie.path"));
                Assert.assertEquals("simple", conf.get("type"));
                Assert.assertEquals("36000", conf.get("token.validity"));
                Assert.assertNull(conf.get("cookie.domain"));
                Assert.assertEquals("true", conf.get("simple.anonymous.allowed"));
                Assert.assertEquals("HTTP/barhost@LOCALHOST", conf.get("kerberos.principal"));
                Assert.assertEquals(((System.getProperty("user.home")) + "/hadoop.keytab"), conf.get("kerberos.keytab"));
                Assert.assertEquals("bar", conf.get("foo"));
                return null;
            }
        }).when(container).addFilter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        new AuthenticationFilterInitializer().initFilter(container, conf);
    }
}

