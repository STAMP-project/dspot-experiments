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
package org.keycloak.testsuite.console.themes;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.auth.page.login.TermsAndConditions;
import org.keycloak.testsuite.console.AbstractConsoleTest;


/**
 *
 */
public class TermsAndConditionsThemeTest extends AbstractConsoleTest {
    private static final String REALM = "CustomLook";

    private static final String HOMER = "Homer";

    private static final String HOMER_PASS = "Mmm donuts.";

    @Page
    private TermsAndConditions termsAndConditionsPage;

    @Test
    public void testTermsAndConditions() {
        String userId = createUser(TermsAndConditionsThemeTest.REALM, TermsAndConditionsThemeTest.HOMER, TermsAndConditionsThemeTest.HOMER_PASS);
        setRequiredActionEnabled(TermsAndConditionsThemeTest.REALM, RequiredActions.TERMS_AND_CONDITIONS, true, false);
        setRequiredActionEnabled(TermsAndConditionsThemeTest.REALM, userId, RequiredActions.TERMS_AND_CONDITIONS, true);
        RealmResource realmResource = adminClient.realm(TermsAndConditionsThemeTest.REALM);
        RealmRepresentation realmRepresentation = realmResource.toRepresentation();
        realmRepresentation.setLoginTheme("qe");
        realmResource.update(realmRepresentation);
        testRealmAdminConsolePage.navigateTo();
        testRealmLoginPage.form().login(TermsAndConditionsThemeTest.HOMER, TermsAndConditionsThemeTest.HOMER_PASS);
        Assert.assertTrue(termsAndConditionsPage.isCurrent());
        Assert.assertTrue(termsAndConditionsPage.getText().contains("See QA for more information."));
        Assert.assertEquals("Yes", termsAndConditionsPage.getAcceptButtonText());
        Assert.assertEquals("No", termsAndConditionsPage.getDeclineButtonText());
    }
}

