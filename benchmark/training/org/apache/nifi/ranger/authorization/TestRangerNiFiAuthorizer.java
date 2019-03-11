/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.nifi.ranger.authorization;


import RangerNiFiAuthorizer.RANGER_KERBEROS_ENABLED_PROP;
import RangerNiFiAuthorizer.RANGER_NIFI_RESOURCE_NAME;
import RangerNiFiAuthorizer.RESOURCES_RESOURCE;
import UserContextKeys.CLIENT_ADDRESS;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.login.LoginException;
import org.apache.nifi.authorization.AuthorizationRequest;
import org.apache.nifi.authorization.AuthorizationResult;
import org.apache.nifi.authorization.AuthorizerConfigurationContext;
import org.apache.nifi.authorization.RequestAction;
import org.apache.nifi.authorization.Resource;
import org.apache.nifi.authorization.exception.AuthorizerCreationException;
import org.apache.nifi.util.MockPropertyValue;
import org.apache.nifi.util.NiFiProperties;
import org.apache.ranger.plugin.policyengine.RangerAccessRequest;
import org.apache.ranger.plugin.policyengine.RangerAccessRequestImpl;
import org.apache.ranger.plugin.policyengine.RangerAccessResourceImpl;
import org.apache.ranger.plugin.policyengine.RangerAccessResult;
import org.apache.ranger.plugin.policyengine.RangerAccessResultProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestRangerNiFiAuthorizer {
    private TestRangerNiFiAuthorizer.MockRangerNiFiAuthorizer authorizer;

    private RangerBasePluginWithPolicies rangerBasePlugin;

    private AuthorizerConfigurationContext configurationContext;

    private NiFiProperties nifiProperties;

    private final String serviceType = "nifiService";

    private final String appId = "nifiAppId";

    private RangerAccessResult allowedResult;

    private RangerAccessResult notAllowedResult;

    @Test
    public void testOnConfigured() {
        Mockito.verify(rangerBasePlugin, Mockito.times(1)).init();
        Assert.assertEquals(appId, authorizer.mockRangerBasePlugin.getAppId());
        Assert.assertEquals(serviceType, authorizer.mockRangerBasePlugin.getServiceType());
    }

    @Test
    public void testKerberosEnabledWithoutKeytab() {
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(RANGER_KERBEROS_ENABLED_PROP))).thenReturn(new MockPropertyValue("true"));
        nifiProperties = Mockito.mock(NiFiProperties.class);
        Mockito.when(nifiProperties.getKerberosServicePrincipal()).thenReturn("");
        authorizer = new TestRangerNiFiAuthorizer.MockRangerNiFiAuthorizer(rangerBasePlugin);
        authorizer.setNiFiProperties(nifiProperties);
        try {
            authorizer.onConfigured(configurationContext);
            Assert.fail("Should have thrown exception");
        } catch (AuthorizerCreationException e) {
            // want to make sure this exception is from our authorizer code
            verifyOnlyAuthorizeCreationExceptions(e);
        }
    }

    @Test
    public void testKerberosEnabledWithoutPrincipal() {
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(RANGER_KERBEROS_ENABLED_PROP))).thenReturn(new MockPropertyValue("true"));
        nifiProperties = Mockito.mock(NiFiProperties.class);
        Mockito.when(nifiProperties.getKerberosServiceKeytabLocation()).thenReturn("");
        authorizer = new TestRangerNiFiAuthorizer.MockRangerNiFiAuthorizer(rangerBasePlugin);
        authorizer.setNiFiProperties(nifiProperties);
        try {
            authorizer.onConfigured(configurationContext);
            Assert.fail("Should have thrown exception");
        } catch (AuthorizerCreationException e) {
            // want to make sure this exception is from our authorizer code
            verifyOnlyAuthorizeCreationExceptions(e);
        }
    }

    @Test
    public void testKerberosEnabledWithoutKeytabOrPrincipal() {
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(RANGER_KERBEROS_ENABLED_PROP))).thenReturn(new MockPropertyValue("true"));
        nifiProperties = Mockito.mock(NiFiProperties.class);
        Mockito.when(nifiProperties.getKerberosServiceKeytabLocation()).thenReturn("");
        Mockito.when(nifiProperties.getKerberosServicePrincipal()).thenReturn("");
        authorizer = new TestRangerNiFiAuthorizer.MockRangerNiFiAuthorizer(rangerBasePlugin);
        authorizer.setNiFiProperties(nifiProperties);
        try {
            authorizer.onConfigured(configurationContext);
            Assert.fail("Should have thrown exception");
        } catch (AuthorizerCreationException e) {
            // want to make sure this exception is from our authorizer code
            verifyOnlyAuthorizeCreationExceptions(e);
        }
    }

    @Test
    public void testKerberosEnabled() {
        Mockito.when(configurationContext.getProperty(ArgumentMatchers.eq(RANGER_KERBEROS_ENABLED_PROP))).thenReturn(new MockPropertyValue("true"));
        nifiProperties = Mockito.mock(NiFiProperties.class);
        Mockito.when(nifiProperties.getKerberosServiceKeytabLocation()).thenReturn("test");
        Mockito.when(nifiProperties.getKerberosServicePrincipal()).thenReturn("test");
        authorizer = new TestRangerNiFiAuthorizer.MockRangerNiFiAuthorizer(rangerBasePlugin);
        authorizer.setNiFiProperties(nifiProperties);
        try {
            authorizer.onConfigured(configurationContext);
            Assert.fail("Should have thrown exception");
        } catch (AuthorizerCreationException e) {
            // getting a LoginException here means we attempted to login which is what we want
            boolean foundLoginException = false;
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause instanceof LoginException) {
                    foundLoginException = true;
                    break;
                }
                cause = cause.getCause();
            } 
            Assert.assertTrue(foundLoginException);
        }
    }

    @Test
    public void testApprovedWithDirectAccess() {
        final String systemResource = "/system";
        final RequestAction action = RequestAction.WRITE;
        final String user = "admin";
        final String clientIp = "192.168.1.1";
        final Map<String, String> userContext = new HashMap<>();
        userContext.put(CLIENT_ADDRESS.name(), clientIp);
        // the incoming NiFi request to test
        final AuthorizationRequest request = new AuthorizationRequest.Builder().resource(new TestRangerNiFiAuthorizer.MockResource(systemResource, systemResource)).action(action).identity(user).resourceContext(new HashMap()).userContext(userContext).accessAttempt(true).anonymous(false).build();
        // the expected Ranger resource and request that are created
        final RangerAccessResourceImpl resource = new RangerAccessResourceImpl();
        resource.setValue(RANGER_NIFI_RESOURCE_NAME, systemResource);
        final RangerAccessRequestImpl expectedRangerRequest = new RangerAccessRequestImpl();
        expectedRangerRequest.setResource(resource);
        expectedRangerRequest.setAction(request.getAction().name());
        expectedRangerRequest.setAccessType(request.getAction().name());
        expectedRangerRequest.setUser(request.getIdentity());
        expectedRangerRequest.setClientIPAddress(clientIp);
        // a non-null result processor should be used for direct access
        Mockito.when(rangerBasePlugin.isAccessAllowed(ArgumentMatchers.argThat(new TestRangerNiFiAuthorizer.RangerAccessRequestMatcher(expectedRangerRequest)))).thenReturn(allowedResult);
        final AuthorizationResult result = authorizer.authorize(request);
        Assert.assertEquals(AuthorizationResult.approved().getResult(), result.getResult());
    }

    @Test
    public void testApprovedWithNonDirectAccess() {
        final String systemResource = "/system";
        final RequestAction action = RequestAction.WRITE;
        final String user = "admin";
        // the incoming NiFi request to test
        final AuthorizationRequest request = new AuthorizationRequest.Builder().resource(new TestRangerNiFiAuthorizer.MockResource(systemResource, systemResource)).action(action).identity(user).resourceContext(new HashMap()).accessAttempt(false).anonymous(false).build();
        // the expected Ranger resource and request that are created
        final RangerAccessResourceImpl resource = new RangerAccessResourceImpl();
        resource.setValue(RANGER_NIFI_RESOURCE_NAME, systemResource);
        final RangerAccessRequestImpl expectedRangerRequest = new RangerAccessRequestImpl();
        expectedRangerRequest.setResource(resource);
        expectedRangerRequest.setAction(request.getAction().name());
        expectedRangerRequest.setAccessType(request.getAction().name());
        expectedRangerRequest.setUser(request.getIdentity());
        // no result processor should be provided used non-direct access
        Mockito.when(rangerBasePlugin.isAccessAllowed(ArgumentMatchers.argThat(new TestRangerNiFiAuthorizer.RangerAccessRequestMatcher(expectedRangerRequest)))).thenReturn(allowedResult);
        final AuthorizationResult result = authorizer.authorize(request);
        Assert.assertEquals(AuthorizationResult.approved().getResult(), result.getResult());
    }

    @Test
    public void testResourceNotFound() {
        final String systemResource = "/system";
        final RequestAction action = RequestAction.WRITE;
        final String user = "admin";
        // the incoming NiFi request to test
        final AuthorizationRequest request = new AuthorizationRequest.Builder().resource(new TestRangerNiFiAuthorizer.MockResource(systemResource, systemResource)).action(action).identity(user).resourceContext(new HashMap()).accessAttempt(true).anonymous(false).build();
        // the expected Ranger resource and request that are created
        final RangerAccessResourceImpl resource = new RangerAccessResourceImpl();
        resource.setValue(RANGER_NIFI_RESOURCE_NAME, systemResource);
        final RangerAccessRequestImpl expectedRangerRequest = new RangerAccessRequestImpl();
        expectedRangerRequest.setResource(resource);
        expectedRangerRequest.setAction(request.getAction().name());
        expectedRangerRequest.setAccessType(request.getAction().name());
        expectedRangerRequest.setUser(request.getIdentity());
        // no result processor should be provided used non-direct access
        Mockito.when(rangerBasePlugin.isAccessAllowed(ArgumentMatchers.argThat(new TestRangerNiFiAuthorizer.RangerAccessRequestMatcher(expectedRangerRequest)), ArgumentMatchers.notNull(RangerAccessResultProcessor.class))).thenReturn(notAllowedResult);
        // return false when checking if a policy exists for the resource
        Mockito.when(rangerBasePlugin.doesPolicyExist(systemResource, action)).thenReturn(false);
        final AuthorizationResult result = authorizer.authorize(request);
        Assert.assertEquals(AuthorizationResult.resourceNotFound().getResult(), result.getResult());
    }

    @Test
    public void testDenied() {
        final String systemResource = "/system";
        final RequestAction action = RequestAction.WRITE;
        final String user = "admin";
        // the incoming NiFi request to test
        final AuthorizationRequest request = new AuthorizationRequest.Builder().resource(new TestRangerNiFiAuthorizer.MockResource(systemResource, systemResource)).action(action).identity(user).resourceContext(new HashMap()).accessAttempt(true).anonymous(false).build();
        // the expected Ranger resource and request that are created
        final RangerAccessResourceImpl resource = new RangerAccessResourceImpl();
        resource.setValue(RANGER_NIFI_RESOURCE_NAME, systemResource);
        final RangerAccessRequestImpl expectedRangerRequest = new RangerAccessRequestImpl();
        expectedRangerRequest.setResource(resource);
        expectedRangerRequest.setAction(request.getAction().name());
        expectedRangerRequest.setAccessType(request.getAction().name());
        expectedRangerRequest.setUser(request.getIdentity());
        // no result processor should be provided used non-direct access
        Mockito.when(rangerBasePlugin.isAccessAllowed(ArgumentMatchers.argThat(new TestRangerNiFiAuthorizer.RangerAccessRequestMatcher(expectedRangerRequest)))).thenReturn(notAllowedResult);
        // return true when checking if a policy exists for the resource
        Mockito.when(rangerBasePlugin.doesPolicyExist(systemResource, action)).thenReturn(true);
        final AuthorizationResult result = authorizer.authorize(request);
        Assert.assertEquals(AuthorizationResult.denied().getResult(), result.getResult());
    }

    @Test
    public void testRangerAdminApproved() {
        runRangerAdminTest(RESOURCES_RESOURCE, AuthorizationResult.approved().getResult());
    }

    @Test
    public void testRangerAdminDenied() {
        runRangerAdminTest("/flow", AuthorizationResult.denied().getResult());
    }

    /**
     * Extend RangerNiFiAuthorizer to inject a mock base plugin for testing.
     */
    private static class MockRangerNiFiAuthorizer extends RangerNiFiAuthorizer {
        RangerBasePluginWithPolicies mockRangerBasePlugin;

        public MockRangerNiFiAuthorizer(RangerBasePluginWithPolicies mockRangerBasePlugin) {
            this.mockRangerBasePlugin = mockRangerBasePlugin;
        }

        @Override
        protected RangerBasePluginWithPolicies createRangerBasePlugin(String serviceType, String appId) {
            Mockito.when(mockRangerBasePlugin.getAppId()).thenReturn(appId);
            Mockito.when(mockRangerBasePlugin.getServiceType()).thenReturn(serviceType);
            return mockRangerBasePlugin;
        }
    }

    /**
     * Resource implementation for testing.
     */
    private static class MockResource implements Resource {
        private final String identifier;

        private final String name;

        public MockResource(String identifier, String name) {
            this.identifier = identifier;
            this.name = name;
        }

        @Override
        public String getIdentifier() {
            return identifier;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSafeDescription() {
            return name;
        }
    }

    /**
     * Custom Mockito matcher for RangerAccessRequest objects.
     */
    private static class RangerAccessRequestMatcher extends ArgumentMatcher<RangerAccessRequest> {
        private final RangerAccessRequest request;

        public RangerAccessRequestMatcher(RangerAccessRequest request) {
            this.request = request;
        }

        @Override
        public boolean matches(Object o) {
            if (!(o instanceof RangerAccessRequest)) {
                return false;
            }
            final RangerAccessRequest other = ((RangerAccessRequest) (o));
            final boolean clientIpsMatch = (((other.getClientIPAddress()) == null) && ((request.getClientIPAddress()) == null)) || ((((other.getClientIPAddress()) != null) && ((request.getClientIPAddress()) != null)) && (other.getClientIPAddress().equals(request.getClientIPAddress())));
            return ((((other.getResource().equals(request.getResource())) && (other.getAccessType().equals(request.getAccessType()))) && (other.getAction().equals(request.getAction()))) && (other.getUser().equals(request.getUser()))) && clientIpsMatch;
        }
    }
}

