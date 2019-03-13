/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.adapters.osgi;


import HttpFacade.Cookie;
import java.io.InputStream;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.adapters.OIDCHttpFacade;
import org.keycloak.adapters.spi.AuthenticationError;
import org.keycloak.adapters.spi.LogoutError;


public class HierarchicalPathBasedKeycloakConfigResolverTest {
    @Test
    public void genericAndSpecificConfigurations() throws Exception {
        HierarchicalPathBasedKeycloakConfigResolver resolver = new HierarchicalPathBasedKeycloakConfigResolver();
        populate(resolver, true);
        Assert.assertThat(resolver.resolve(new HierarchicalPathBasedKeycloakConfigResolverTest.MockRequest("http://localhost/a/b/c/d/e?a=b")).getRealm(), CoreMatchers.equalTo("a-b-c-d-e"));
        Assert.assertThat(resolver.resolve(new HierarchicalPathBasedKeycloakConfigResolverTest.MockRequest("http://localhost/a/b/c/d/x?a=b")).getRealm(), CoreMatchers.equalTo("a-b-c-d"));
        Assert.assertThat(resolver.resolve(new HierarchicalPathBasedKeycloakConfigResolverTest.MockRequest("http://localhost/a/b/c/x/x?a=b")).getRealm(), CoreMatchers.equalTo("a-b-c"));
        Assert.assertThat(resolver.resolve(new HierarchicalPathBasedKeycloakConfigResolverTest.MockRequest("http://localhost/a/b/x/x/x?a=b")).getRealm(), CoreMatchers.equalTo("a-b"));
        Assert.assertThat(resolver.resolve(new HierarchicalPathBasedKeycloakConfigResolverTest.MockRequest("http://localhost/a/x/x/x/x?a=b")).getRealm(), CoreMatchers.equalTo("a"));
        Assert.assertThat(resolver.resolve(new HierarchicalPathBasedKeycloakConfigResolverTest.MockRequest("http://localhost/x/x/x/x/x?a=b")).getRealm(), CoreMatchers.equalTo(""));
        populate(resolver, false);
        try {
            resolver.resolve(new HierarchicalPathBasedKeycloakConfigResolverTest.MockRequest("http://localhost/x/x/x/x/x?a=b"));
            Assert.fail("Expected java.lang.IllegalStateException: Can't find Keycloak configuration ...");
        } catch (IllegalStateException expected) {
        }
    }

    private class MockRequest implements OIDCHttpFacade.Request {
        private String uri;

        public MockRequest(String uri) {
            this.uri = uri;
        }

        @Override
        public String getMethod() {
            return null;
        }

        @Override
        public String getURI() {
            return this.uri;
        }

        @Override
        public String getRelativePath() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public String getFirstParam(String param) {
            return null;
        }

        @Override
        public String getQueryParamValue(String param) {
            return null;
        }

        @Override
        public Cookie getCookie(String cookieName) {
            return null;
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public List<String> getHeaders(String name) {
            return null;
        }

        @Override
        public InputStream getInputStream() {
            return null;
        }

        @Override
        public InputStream getInputStream(boolean buffered) {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return null;
        }

        @Override
        public void setError(AuthenticationError error) {
        }

        @Override
        public void setError(LogoutError error) {
        }
    }
}

