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
package org.keycloak.testsuite.console.authorization;


import Logic.NEGATIVE;
import Profile.Feature.AUTHZ_DROOLS_POLICY;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.authorization.RulePolicyRepresentation;
import org.keycloak.testsuite.ProfileAssume;
import org.keycloak.testsuite.console.page.clients.authorization.policy.RulePolicy;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class RulePolicyManagementTest extends AbstractAuthorizationSettingsTest {
    @Test
    public void testUpdate() {
        ProfileAssume.assumeFeatureEnabled(AUTHZ_DROOLS_POLICY);
        authorizationPage.navigateTo();
        RulePolicyRepresentation expected = createDefaultRepresentation("Test Rule Policy");
        expected = createPolicy(expected);
        String previousName = expected.getName();
        expected.setName(("Changed " + previousName));
        expected.setDescription("Changed description");
        expected.setLogic(NEGATIVE);
        expected.setModuleName("PhotozAuthzContextualPolicy");
        expected.setSessionName("MainContextualSession");
        expected.setScannerPeriod("12");
        expected.setScannerPeriodUnit("Days");
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().policies().update(previousName, expected);
        assertAlertSuccess();
        authorizationPage.navigateTo();
        RulePolicy actual = authorizationPage.authorizationTabs().policies().name(expected.getName());
        assertPolicy(expected, actual);
    }

    @Test
    public void testDelete() {
        ProfileAssume.assumeFeatureEnabled(AUTHZ_DROOLS_POLICY);
        authorizationPage.navigateTo();
        RulePolicyRepresentation expected = createDefaultRepresentation("Delete Rule Policy");
        expected = createPolicy(expected);
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().policies().delete(expected.getName());
        assertAlertSuccess();
        authorizationPage.navigateTo();
        Assert.assertNull(authorizationPage.authorizationTabs().policies().policies().findByName(expected.getName()));
    }

    @Test
    public void testDeleteFromList() {
        ProfileAssume.assumeFeatureEnabled(AUTHZ_DROOLS_POLICY);
        authorizationPage.navigateTo();
        RulePolicyRepresentation expected = createDefaultRepresentation("Delete Rule Policy");
        expected = createPolicy(expected);
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().policies().deleteFromList(expected.getName());
        authorizationPage.navigateTo();
        Assert.assertNull(authorizationPage.authorizationTabs().policies().policies().findByName(expected.getName()));
    }
}

