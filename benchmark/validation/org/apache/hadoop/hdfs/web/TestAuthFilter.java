/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.web;


import AuthFilter.AUTH_TYPE;
import AuthFilter.CONF_PREFIX;
import DFSConfigKeys.DFS_WEB_AUTHENTICATION_KERBEROS_KEYTAB_KEY;
import DFSConfigKeys.DFS_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL_KEY;
import DFSConfigKeys.DFS_WEB_AUTHENTICATION_SIMPLE_ANONYMOUS_ALLOWED;
import PseudoAuthenticationHandler.ANONYMOUS_ALLOWED;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.junit.Assert;
import org.junit.Test;

import static AuthFilter.AUTH_TYPE;
import static AuthFilter.CONF_PREFIX;


public class TestAuthFilter {
    private static class DummyFilterConfig implements FilterConfig {
        final Map<String, String> map;

        DummyFilterConfig(Map<String, String> map) {
            this.map = map;
        }

        @Override
        public String getFilterName() {
            return "dummy";
        }

        @Override
        public String getInitParameter(String arg0) {
            return map.get(arg0);
        }

        @Override
        public Enumeration<String> getInitParameterNames() {
            return Collections.enumeration(map.keySet());
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }
    }

    @Test
    public void testGetConfiguration() throws ServletException {
        AuthFilter filter = new AuthFilter();
        Map<String, String> m = new HashMap<String, String>();
        m.put(DFS_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL_KEY, "xyz/thehost@REALM");
        m.put(DFS_WEB_AUTHENTICATION_KERBEROS_KEYTAB_KEY, "thekeytab");
        FilterConfig config = new TestAuthFilter.DummyFilterConfig(m);
        Properties p = filter.getConfiguration("random", config);
        Assert.assertEquals("xyz/thehost@REALM", p.getProperty("kerberos.principal"));
        Assert.assertEquals("thekeytab", p.getProperty("kerberos.keytab"));
        Assert.assertEquals("true", p.getProperty(ANONYMOUS_ALLOWED));
    }

    @Test
    public void testGetSimpleAuthDisabledConfiguration() throws ServletException {
        AuthFilter filter = new AuthFilter();
        Map<String, String> m = new HashMap<String, String>();
        m.put(DFS_WEB_AUTHENTICATION_SIMPLE_ANONYMOUS_ALLOWED, "false");
        FilterConfig config = new TestAuthFilter.DummyFilterConfig(m);
        Properties p = filter.getConfiguration("random", config);
        Assert.assertEquals("false", p.getProperty(ANONYMOUS_ALLOWED));
    }

    @Test
    public void testGetSimpleAuthDefaultConfiguration() throws ServletException {
        AuthFilter filter = new AuthFilter();
        Map<String, String> m = new HashMap<String, String>();
        FilterConfig config = new TestAuthFilter.DummyFilterConfig(m);
        Properties p = filter.getConfiguration("random", config);
        Assert.assertEquals("true", p.getProperty(ANONYMOUS_ALLOWED));
    }

    @Test
    public void testGetCustomAuthConfiguration() throws ServletException {
        AuthFilter filter = new AuthFilter();
        Map<String, String> m = new HashMap<String, String>();
        m.put(((CONF_PREFIX) + (AUTH_TYPE)), "com.yourclass");
        m.put(((CONF_PREFIX) + "alt-kerberos.param"), "value");
        FilterConfig config = new TestAuthFilter.DummyFilterConfig(m);
        Properties p = filter.getConfiguration(CONF_PREFIX, config);
        Assert.assertEquals("com.yourclass", p.getProperty(AUTH_TYPE));
        Assert.assertEquals("value", p.getProperty("alt-kerberos.param"));
    }
}

