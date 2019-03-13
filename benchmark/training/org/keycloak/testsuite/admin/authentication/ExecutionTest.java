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


import AuthenticationFlow.CLIENT_FLOW;
import ClientIdAndSecretAuthenticator.PROVIDER_ID;
import OperationType.CREATE;
import OperationType.DELETE;
import OperationType.UPDATE;
import ResourceType.AUTH_EXECUTION;
import ResourceType.AUTH_FLOW;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticationExecutionRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.AdminEventPaths;
import org.keycloak.testsuite.util.AssertAdminEvents;


/**
 *
 *
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class ExecutionTest extends AbstractAuthenticationTest {
    // KEYCLOAK-7975
    @Test
    public void testUpdateAuthenticatorConfig() {
        // copy built-in flow so we get a new editable flow
        HashMap<String, String> params = new HashMap<>();
        params.put("newName", "new-browser-flow");
        Response response = authMgmtResource.copy("browser", params);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, CREATE, AdminEventPaths.authCopyFlowPath("browser"), params, AUTH_FLOW);
        try {
            Assert.assertEquals("Copy flow", 201, response.getStatus());
        } finally {
            response.close();
        }
        // create Conditional OTP Form execution
        params.put("provider", "auth-conditional-otp-form");
        authMgmtResource.addExecution("new-browser-flow", params);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, CREATE, AdminEventPaths.authAddExecutionPath("new-browser-flow"), params, AUTH_EXECUTION);
        List<AuthenticationExecutionInfoRepresentation> executionReps = authMgmtResource.getExecutions("new-browser-flow");
        AuthenticationExecutionInfoRepresentation exec = AbstractAuthenticationTest.findExecutionByProvider("auth-conditional-otp-form", executionReps);
        // create authenticator config for the execution
        Map<String, String> config = new HashMap<>();
        config.put("defaultOtpOutcome", "skip");
        config.put("otpControlAttribute", "test");
        config.put("forceOtpForHeaderPattern", "");
        config.put("forceOtpRole", "");
        config.put("noOtpRequiredForHeaderPattern", "");
        config.put("skipOtpRole", "");
        AuthenticatorConfigRepresentation authConfigRep = new AuthenticatorConfigRepresentation();
        authConfigRep.setAlias("conditional-otp-form-config-alias");
        authConfigRep.setConfig(config);
        response = authMgmtResource.newExecutionConfig(exec.getId(), authConfigRep);
        try {
            authConfigRep.setId(ApiUtil.getCreatedId(response));
        } finally {
            response.close();
        }
        // try to update the config adn check
        config.put("otpControlAttribute", "test-updated");
        authConfigRep.setConfig(config);
        authMgmtResource.updateAuthenticatorConfig(authConfigRep.getId(), authConfigRep);
        AuthenticatorConfigRepresentation updated = authMgmtResource.getAuthenticatorConfig(authConfigRep.getId());
        Assert.assertThat(updated.getConfig().values(), Matchers.hasItems("test-updated", "skip"));
    }

    @Test
    public void testAddRemoveExecution() {
        // try add execution to built-in flow
        HashMap<String, String> params = new HashMap<>();
        params.put("provider", "idp-review-profile");
        try {
            authMgmtResource.addExecution("browser", params);
            Assert.fail("add execution to built-in flow should fail");
        } catch (BadRequestException expected) {
            // Expected
        }
        // try add execution to not-existent flow
        try {
            authMgmtResource.addExecution("not-existent", params);
            Assert.fail("add execution to not-existent flow should fail");
        } catch (BadRequestException expected) {
            // Expected
        }
        // copy built-in flow so we get a new editable flow
        params.put("newName", "Copy-of-browser");
        Response response = authMgmtResource.copy("browser", params);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, CREATE, AdminEventPaths.authCopyFlowPath("browser"), params, AUTH_FLOW);
        try {
            Assert.assertEquals("Copy flow", 201, response.getStatus());
        } finally {
            response.close();
        }
        // add execution using inexistent provider
        params.put("provider", "test-execution");
        try {
            authMgmtResource.addExecution("CopyOfBrowser", params);
            Assert.fail("add execution with inexistent provider should fail");
        } catch (BadRequestException expected) {
            // Expected
        }
        // add execution - should succeed
        params.put("provider", "idp-review-profile");
        authMgmtResource.addExecution("Copy-of-browser", params);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, CREATE, AdminEventPaths.authAddExecutionPath("Copy-of-browser"), params, AUTH_EXECUTION);
        // check execution was added
        List<AuthenticationExecutionInfoRepresentation> executionReps = authMgmtResource.getExecutions("Copy-of-browser");
        AuthenticationExecutionInfoRepresentation exec = AbstractAuthenticationTest.findExecutionByProvider("idp-review-profile", executionReps);
        Assert.assertNotNull("idp-review-profile added", exec);
        // we'll need auth-cookie later
        AuthenticationExecutionInfoRepresentation authCookieExec = AbstractAuthenticationTest.findExecutionByProvider("auth-cookie", executionReps);
        compareExecution(newExecInfo("Review Profile", "idp-review-profile", true, 0, 4, AbstractAuthenticationTest.DISABLED, null, new String[]{ AbstractAuthenticationTest.REQUIRED, AbstractAuthenticationTest.DISABLED }), exec);
        // remove execution
        authMgmtResource.removeExecution(exec.getId());
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, DELETE, AdminEventPaths.authExecutionPath(exec.getId()), AUTH_EXECUTION);
        // check execution was removed
        executionReps = authMgmtResource.getExecutions("Copy-of-browser");
        exec = AbstractAuthenticationTest.findExecutionByProvider("idp-review-profile", executionReps);
        Assert.assertNull("idp-review-profile removed", exec);
        // now add the execution again using a different method and representation
        // delete auth-cookie
        authMgmtResource.removeExecution(authCookieExec.getId());
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, DELETE, AdminEventPaths.authExecutionPath(authCookieExec.getId()), AUTH_EXECUTION);
        AuthenticationExecutionRepresentation rep = new AuthenticationExecutionRepresentation();
        rep.setPriority(10);
        rep.setAuthenticator("auth-cookie");
        rep.setRequirement(AbstractAuthenticationTest.OPTIONAL);
        // Should fail - missing parent flow
        response = authMgmtResource.addExecution(rep);
        try {
            Assert.assertEquals("added execution missing parent flow", 400, response.getStatus());
        } finally {
            response.close();
        }
        // Should fail - not existent parent flow
        rep.setParentFlow("not-existent-id");
        response = authMgmtResource.addExecution(rep);
        try {
            Assert.assertEquals("added execution missing parent flow", 400, response.getStatus());
        } finally {
            response.close();
        }
        // Should fail - add execution to builtin flow
        AuthenticationFlowRepresentation browserFlow = findFlowByAlias("browser", authMgmtResource.getFlows());
        rep.setParentFlow(browserFlow.getId());
        response = authMgmtResource.addExecution(rep);
        try {
            Assert.assertEquals("added execution to builtin flow", 400, response.getStatus());
        } finally {
            response.close();
        }
        // get Copy-of-browser flow id, and set it on execution
        List<AuthenticationFlowRepresentation> flows = authMgmtResource.getFlows();
        AuthenticationFlowRepresentation flow = findFlowByAlias("Copy-of-browser", flows);
        rep.setParentFlow(flow.getId());
        // add execution - should succeed
        response = authMgmtResource.addExecution(rep);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, CREATE, AssertAdminEvents.isExpectedPrefixFollowedByUuid(((AdminEventPaths.authMgmtBasePath()) + "/executions")), rep, AUTH_EXECUTION);
        try {
            Assert.assertEquals("added execution", 201, response.getStatus());
        } finally {
            response.close();
        }
        // check execution was added
        List<AuthenticationExecutionInfoRepresentation> executions = authMgmtResource.getExecutions("Copy-of-browser");
        exec = AbstractAuthenticationTest.findExecutionByProvider("auth-cookie", executions);
        Assert.assertNotNull("auth-cookie added", exec);
        // Note: there is no checking in addExecution if requirement is one of requirementChoices
        // Thus we can have OPTIONAL which is neither ALTERNATIVE, nor DISABLED
        compareExecution(newExecInfo("Cookie", "auth-cookie", false, 0, 3, AbstractAuthenticationTest.OPTIONAL, null, new String[]{ AbstractAuthenticationTest.ALTERNATIVE, AbstractAuthenticationTest.DISABLED }), exec);
    }

    @Test
    public void testUpdateExecution() {
        // get current auth-cookie execution
        List<AuthenticationExecutionInfoRepresentation> executionReps = authMgmtResource.getExecutions("browser");
        AuthenticationExecutionInfoRepresentation exec = AbstractAuthenticationTest.findExecutionByProvider("auth-cookie", executionReps);
        Assert.assertEquals("auth-cookie set to ALTERNATIVE", AbstractAuthenticationTest.ALTERNATIVE, exec.getRequirement());
        // switch from DISABLED to ALTERNATIVE
        exec.setRequirement(AbstractAuthenticationTest.DISABLED);
        authMgmtResource.updateExecutions("browser", exec);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, UPDATE, AdminEventPaths.authUpdateExecutionPath("browser"), exec, AUTH_EXECUTION);
        // make sure the change is visible
        executionReps = authMgmtResource.getExecutions("browser");
        // get current auth-cookie execution
        AuthenticationExecutionInfoRepresentation exec2 = AbstractAuthenticationTest.findExecutionByProvider("auth-cookie", executionReps);
        compareExecution(exec, exec2);
    }

    @Test
    public void testClientFlowExecutions() {
        // Create client flow
        AuthenticationFlowRepresentation clientFlow = newFlow("new-client-flow", "desc", CLIENT_FLOW, true, false);
        createFlow(clientFlow);
        // Add execution to it
        Map<String, String> executionData = new HashMap<>();
        executionData.put("provider", PROVIDER_ID);
        authMgmtResource.addExecution("new-client-flow", executionData);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, CREATE, AdminEventPaths.authAddExecutionPath("new-client-flow"), executionData, AUTH_EXECUTION);
        // Check executions of not-existent flow - SHOULD FAIL
        try {
            authMgmtResource.getExecutions("not-existent");
            Assert.fail("Not expected to find executions");
        } catch (NotFoundException nfe) {
            // Expected
        }
        // Check existent executions
        List<AuthenticationExecutionInfoRepresentation> executions = authMgmtResource.getExecutions("new-client-flow");
        AuthenticationExecutionInfoRepresentation executionRep = AbstractAuthenticationTest.findExecutionByProvider(PROVIDER_ID, executions);
        Assert.assertNotNull(executionRep);
        // Update execution with not-existent flow - SHOULD FAIL
        try {
            authMgmtResource.updateExecutions("not-existent", executionRep);
            Assert.fail("Not expected to update execution with not-existent flow");
        } catch (NotFoundException nfe) {
            // Expected
        }
        // Update execution with not-existent ID - SHOULD FAIL
        try {
            AuthenticationExecutionInfoRepresentation executionRep2 = new AuthenticationExecutionInfoRepresentation();
            executionRep2.setId("not-existent");
            authMgmtResource.updateExecutions("new-client-flow", executionRep2);
            Assert.fail("Not expected to update not-existent execution");
        } catch (NotFoundException nfe) {
            // Expected
        }
        // Update success
        executionRep.setRequirement(AbstractAuthenticationTest.ALTERNATIVE);
        authMgmtResource.updateExecutions("new-client-flow", executionRep);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, UPDATE, AdminEventPaths.authUpdateExecutionPath("new-client-flow"), executionRep, AUTH_EXECUTION);
        // Check updated
        executionRep = AbstractAuthenticationTest.findExecutionByProvider(PROVIDER_ID, authMgmtResource.getExecutions("new-client-flow"));
        Assert.assertEquals(AbstractAuthenticationTest.ALTERNATIVE, executionRep.getRequirement());
        // Remove execution with not-existent ID
        try {
            authMgmtResource.removeExecution("not-existent");
            Assert.fail("Didn't expect to find execution");
        } catch (NotFoundException nfe) {
            // Expected
        }
        // Successfuly remove execution and flow
        authMgmtResource.removeExecution(executionRep.getId());
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, DELETE, AdminEventPaths.authExecutionPath(executionRep.getId()), AUTH_EXECUTION);
        AuthenticationFlowRepresentation rep = findFlowByAlias("new-client-flow", authMgmtResource.getFlows());
        authMgmtResource.deleteFlow(rep.getId());
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, DELETE, AdminEventPaths.authFlowPath(rep.getId()), AUTH_FLOW);
    }
}

