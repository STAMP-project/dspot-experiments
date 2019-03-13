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


import DummyRequiredActionFactory.PROVIDER_ID;
import OperationType.CREATE;
import OperationType.DELETE;
import OperationType.UPDATE;
import ResourceType.REQUIRED_ACTION;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ws.rs.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.RequiredActionProviderRepresentation;
import org.keycloak.representations.idm.RequiredActionProviderSimpleRepresentation;
import org.keycloak.testsuite.util.AdminEventPaths;


/**
 *
 *
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class RequiredActionsTest extends AbstractAuthenticationTest {
    @Test
    public void testRequiredActions() {
        List<RequiredActionProviderRepresentation> result = authMgmtResource.getRequiredActions();
        List<RequiredActionProviderRepresentation> expected = new ArrayList<>();
        addRequiredAction(expected, "CONFIGURE_TOTP", "Configure OTP", true, false, null);
        addRequiredAction(expected, "UPDATE_PASSWORD", "Update Password", true, false, null);
        addRequiredAction(expected, "UPDATE_PROFILE", "Update Profile", true, false, null);
        addRequiredAction(expected, "VERIFY_EMAIL", "Verify Email", true, false, null);
        addRequiredAction(expected, "terms_and_conditions", "Terms and Conditions", false, false, null);
        compareRequiredActions(expected, sort(result));
        RequiredActionProviderRepresentation forUpdate = newRequiredAction("VERIFY_EMAIL", "Verify Email", false, false, null);
        authMgmtResource.updateRequiredAction(forUpdate.getAlias(), forUpdate);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, UPDATE, AdminEventPaths.authRequiredActionPath(forUpdate.getAlias()), REQUIRED_ACTION);
        result = authMgmtResource.getRequiredActions();
        RequiredActionProviderRepresentation updated = findRequiredActionByAlias(forUpdate.getAlias(), result);
        Assert.assertNotNull("Required Action still there", updated);
        compareRequiredAction(forUpdate, updated);
        forUpdate.setConfig(Collections.<String, String>emptyMap());
        authMgmtResource.updateRequiredAction(forUpdate.getAlias(), forUpdate);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, UPDATE, AdminEventPaths.authRequiredActionPath(forUpdate.getAlias()), REQUIRED_ACTION);
        result = authMgmtResource.getRequiredActions();
        updated = findRequiredActionByAlias(forUpdate.getAlias(), result);
        Assert.assertNotNull("Required Action still there", updated);
        compareRequiredAction(forUpdate, updated);
    }

    @Test
    public void testCRUDRequiredAction() {
        int lastPriority = authMgmtResource.getRequiredActions().get(((authMgmtResource.getRequiredActions().size()) - 1)).getPriority();
        // Just Dummy RequiredAction is not registered in the realm
        List<RequiredActionProviderSimpleRepresentation> result = authMgmtResource.getUnregisteredRequiredActions();
        Assert.assertEquals(1, result.size());
        RequiredActionProviderSimpleRepresentation action = result.get(0);
        Assert.assertEquals(PROVIDER_ID, action.getProviderId());
        Assert.assertEquals("Dummy Action", action.getName());
        // Register it
        authMgmtResource.registerRequiredAction(action);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, CREATE, ((AdminEventPaths.authMgmtBasePath()) + "/register-required-action"), action, REQUIRED_ACTION);
        // Try to find not-existent action - should fail
        try {
            authMgmtResource.getRequiredAction("not-existent");
            Assert.fail("Didn't expect to find requiredAction of alias 'not-existent'");
        } catch (NotFoundException nfe) {
            // Expected
        }
        // Find existent
        RequiredActionProviderRepresentation rep = authMgmtResource.getRequiredAction(PROVIDER_ID);
        compareRequiredAction(rep, newRequiredAction(PROVIDER_ID, "Dummy Action", true, false, Collections.<String, String>emptyMap()));
        // Confirm the registered priority - should be N + 1
        Assert.assertEquals((lastPriority + 1), rep.getPriority());
        // Update not-existent - should fail
        try {
            authMgmtResource.updateRequiredAction("not-existent", rep);
            Assert.fail("Not expected to update not-existent requiredAction");
        } catch (NotFoundException nfe) {
            // Expected
        }
        // Update (set it as defaultAction)
        rep.setDefaultAction(true);
        authMgmtResource.updateRequiredAction(PROVIDER_ID, rep);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, UPDATE, AdminEventPaths.authRequiredActionPath(rep.getAlias()), rep, REQUIRED_ACTION);
        compareRequiredAction(rep, newRequiredAction(PROVIDER_ID, "Dummy Action", true, true, Collections.<String, String>emptyMap()));
        // Remove unexistent - should fail
        try {
            authMgmtResource.removeRequiredAction("not-existent");
            Assert.fail("Not expected to remove not-existent requiredAction");
        } catch (NotFoundException nfe) {
            // Expected
        }
        // Remove success
        authMgmtResource.removeRequiredAction(PROVIDER_ID);
        assertAdminEvents.assertEvent(AbstractAuthenticationTest.REALM_NAME, DELETE, AdminEventPaths.authRequiredActionPath(rep.getAlias()), REQUIRED_ACTION);
    }

    private static class RequiredActionProviderComparator implements Comparator<RequiredActionProviderRepresentation> {
        @Override
        public int compare(RequiredActionProviderRepresentation o1, RequiredActionProviderRepresentation o2) {
            return o1.getAlias().compareTo(o2.getAlias());
        }
    }
}

