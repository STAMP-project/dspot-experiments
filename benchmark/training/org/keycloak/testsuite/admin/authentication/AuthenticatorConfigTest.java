/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.admin.authentication;


import IdpCreateUserIfUniqueAuthenticatorFactory.PROVIDER_ID;
import IdpCreateUserIfUniqueAuthenticatorFactory.REQUIRE_PASSWORD_UPDATE_AFTER_REGISTRATION;
import OperationType.DELETE;
import OperationType.UPDATE;
import ResourceType.AUTHENTICATOR_CONFIG;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;
import org.keycloak.testsuite.util.AdminEventPaths;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class AuthenticatorConfigTest extends AbstractAuthenticationTest {
    private String executionId;

    @Test
    public void testCreateConfig() {
        AuthenticatorConfigRepresentation cfg = newConfig("foo", REQUIRE_PASSWORD_UPDATE_AFTER_REGISTRATION, "true");
        // Attempt to create config for non-existent execution
        Response response = authMgmtResource.newExecutionConfig("exec-id-doesnt-exists", cfg);
        org.keycloak.testsuite.Assert.assertEquals(404, response.getStatus());
        response.close();
        // Create config success
        String cfgId = createConfig(executionId, cfg);
        // Assert found
        AuthenticatorConfigRepresentation cfgRep = authMgmtResource.getAuthenticatorConfig(cfgId);
        assertConfig(cfgRep, cfgId, "foo", REQUIRE_PASSWORD_UPDATE_AFTER_REGISTRATION, "true");
        // Cleanup
        authMgmtResource.removeAuthenticatorConfig(cfgId);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, DELETE, AdminEventPaths.authExecutionConfigPath(cfgId), AUTHENTICATOR_CONFIG);
    }

    @Test
    public void testUpdateConfig() {
        AuthenticatorConfigRepresentation cfg = newConfig("foo", REQUIRE_PASSWORD_UPDATE_AFTER_REGISTRATION, "true");
        String cfgId = createConfig(executionId, cfg);
        AuthenticatorConfigRepresentation cfgRep = authMgmtResource.getAuthenticatorConfig(cfgId);
        // Try to update not existent config
        try {
            authMgmtResource.updateAuthenticatorConfig("not-existent", cfgRep);
            org.keycloak.testsuite.Assert.fail("Config didn't found");
        } catch (NotFoundException nfe) {
            // Expected
        }
        // Assert nothing changed
        cfgRep = authMgmtResource.getAuthenticatorConfig(cfgId);
        assertConfig(cfgRep, cfgId, "foo", REQUIRE_PASSWORD_UPDATE_AFTER_REGISTRATION, "true");
        // Update success
        cfgRep.setAlias("foo2");
        cfgRep.getConfig().put("configKey2", "configValue2");
        authMgmtResource.updateAuthenticatorConfig(cfgRep.getId(), cfgRep);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, UPDATE, AdminEventPaths.authExecutionConfigPath(cfgId), cfgRep, AUTHENTICATOR_CONFIG);
        // Assert updated
        cfgRep = authMgmtResource.getAuthenticatorConfig(cfgRep.getId());
        assertConfig(cfgRep, cfgId, "foo2", REQUIRE_PASSWORD_UPDATE_AFTER_REGISTRATION, "true", "configKey2", "configValue2");
    }

    @Test
    public void testRemoveConfig() {
        AuthenticatorConfigRepresentation cfg = newConfig("foo", REQUIRE_PASSWORD_UPDATE_AFTER_REGISTRATION, "true");
        String cfgId = createConfig(executionId, cfg);
        AuthenticatorConfigRepresentation cfgRep = authMgmtResource.getAuthenticatorConfig(cfgId);
        // Assert execution has our config
        AuthenticationExecutionInfoRepresentation execution = AbstractAuthenticationTest.findExecutionByProvider(PROVIDER_ID, authMgmtResource.getExecutions("firstBrokerLogin2"));
        org.keycloak.testsuite.Assert.assertEquals(cfgRep.getId(), execution.getAuthenticationConfig());
        // Test remove not-existent
        try {
            authMgmtResource.removeAuthenticatorConfig("not-existent");
            org.keycloak.testsuite.Assert.fail("Config didn't found");
        } catch (NotFoundException nfe) {
            // Expected
        }
        // Test remove our config
        authMgmtResource.removeAuthenticatorConfig(cfgId);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, DELETE, AdminEventPaths.authExecutionConfigPath(cfgId), AUTHENTICATOR_CONFIG);
        // Assert config not found
        try {
            authMgmtResource.getAuthenticatorConfig(cfgRep.getId());
            org.keycloak.testsuite.Assert.fail("Not expected to find config");
        } catch (NotFoundException nfe) {
            // Expected
        }
        // Assert execution doesn't have our config
        execution = AbstractAuthenticationTest.findExecutionByProvider(PROVIDER_ID, authMgmtResource.getExecutions("firstBrokerLogin2"));
        org.keycloak.testsuite.Assert.assertNull(execution.getAuthenticationConfig());
    }
}

