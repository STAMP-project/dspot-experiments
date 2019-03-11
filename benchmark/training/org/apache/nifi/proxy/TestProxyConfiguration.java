/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.proxy;


import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.junit.Test;

import static java.net.Proxy.Type.HTTP;
import static java.net.Proxy.Type.SOCKS;


public class TestProxyConfiguration {
    private static class ComponentUsingProxy extends AbstractProcessor {
        private ProxySpec[] proxySpecs;

        private void setProxySpecs(ProxySpec... proxySpecs) {
            this.proxySpecs = proxySpecs;
        }

        @Override
        protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
            return Collections.singletonList(ProxyConfiguration.createProxyConfigPropertyDescriptor(true, proxySpecs));
        }

        @Override
        public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        }

        @Override
        protected Collection<ValidationResult> customValidate(ValidationContext validationContext) {
            final List<ValidationResult> results = new ArrayList<>();
            ProxyConfiguration.validateProxySpec(validationContext, results, proxySpecs);
            return results;
        }
    }

    private static final ProxyConfiguration HTTP_CONFIG = new ProxyConfiguration();

    private static final ProxyConfiguration SOCKS_CONFIG = new ProxyConfiguration();

    private static final ProxyConfiguration HTTP_AUTH_CONFIG = new ProxyConfiguration();

    private static final ProxyConfiguration SOCKS_AUTH_CONFIG = new ProxyConfiguration();

    static {
        TestProxyConfiguration.HTTP_CONFIG.setProxyType(HTTP);
        TestProxyConfiguration.HTTP_AUTH_CONFIG.setProxyType(HTTP);
        TestProxyConfiguration.HTTP_AUTH_CONFIG.setProxyUserName("proxy-user");
        TestProxyConfiguration.HTTP_AUTH_CONFIG.setProxyUserPassword("proxy-password");
        TestProxyConfiguration.SOCKS_CONFIG.setProxyType(SOCKS);
        TestProxyConfiguration.SOCKS_AUTH_CONFIG.setProxyType(SOCKS);
        TestProxyConfiguration.SOCKS_AUTH_CONFIG.setProxyUserName("proxy-user");
        TestProxyConfiguration.SOCKS_AUTH_CONFIG.setProxyUserPassword("proxy-password");
    }

    @Test
    public void testHTTP() throws Exception {
        // DEFAULT, HTTP
        testValidateProxySpec(new boolean[]{ true, true, false, false, false }, ProxySpec.HTTP);
    }

    @Test
    public void testHTTPAuth() throws Exception {
        // DEFAULT, HTTP, HTTP_AUTH
        testValidateProxySpec(new boolean[]{ true, true, true, false, false }, ProxySpec.HTTP_AUTH);
    }

    @Test
    public void testHTTP_HTTPAuth() throws Exception {
        // DEFAULT, HTTP, HTTP_AUTH
        testValidateProxySpec(new boolean[]{ true, true, true, false, false }, ProxySpec.HTTP, ProxySpec.HTTP_AUTH);
    }

    @Test
    public void testSOCKS() throws Exception {
        // DEFAULT, SOCKS
        testValidateProxySpec(new boolean[]{ true, false, false, true, false }, ProxySpec.SOCKS);
    }

    @Test
    public void testSOCKSAuth() throws Exception {
        // DEFAULT, SOCKS, SOCKS_AUTH
        testValidateProxySpec(new boolean[]{ true, false, false, true, true }, ProxySpec.SOCKS_AUTH);
    }

    @Test
    public void testSOCKS_SOCKSAuth() throws Exception {
        // DEFAULT, SOCKS, SOCKS_AUTH
        testValidateProxySpec(new boolean[]{ true, false, false, true, true }, ProxySpec.SOCKS, ProxySpec.SOCKS_AUTH);
    }

    @Test
    public void testHTTPAuth_SOCKS() throws Exception {
        // DEFAULT, HTTP, HTTP_AUTH, SOCKS
        testValidateProxySpec(new boolean[]{ true, true, true, true, false }, ProxySpec.HTTP_AUTH, ProxySpec.SOCKS);
    }

    @Test
    public void testHTTPAuth_SOCKSAuth() throws Exception {
        // DEFAULT, HTTP, HTTP_AUTH, SOCKS, SOCKS_AUTH
        testValidateProxySpec(new boolean[]{ true, true, true, true, true }, ProxySpec.HTTP_AUTH, ProxySpec.SOCKS_AUTH);
    }
}

