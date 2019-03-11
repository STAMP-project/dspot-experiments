/**
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.console.authentication.actions;


import CredentialRepresentation.PASSWORD;
import java.util.ArrayList;
import java.util.List;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.auth.page.login.LoginError;
import org.keycloak.testsuite.auth.page.login.Registration;
import org.keycloak.testsuite.auth.page.login.TermsAndConditions;
import org.keycloak.testsuite.console.AbstractConsoleTest;


/**
 *
 */
public class TermsAndConditionsTest extends AbstractConsoleTest {
    private static final String TERMS_TEXT = "Terms and conditions to be defined";

    private static final String REALM = "TermsAndConditions";

    private static final String BART = "Bart";

    private static final String BART_PASS = "Ay caramba!";

    private static final String HOMER = "Homer";

    private static final String HOMER_PASS = "Mmm donuts.";

    private static final String FLANDERS = "Flanders";

    private static final String FLANDERS_PASS = "Okily Dokily";

    @Page
    private TermsAndConditions termsAndConditionsPage;

    @Page
    private Registration registrationPage;

    @Page
    protected LoginError errorPage;

    @Test
    public void testExistingUser() {
        // create user
        String userId = createUser(TermsAndConditionsTest.REALM, TermsAndConditionsTest.HOMER, TermsAndConditionsTest.HOMER_PASS);
        // test t&c - log in and make sure t&c is not displayed
        testRealmAdminConsolePage.navigateTo();
        testRealmLoginPage.form().login(TermsAndConditionsTest.HOMER, TermsAndConditionsTest.HOMER_PASS);
        testRealmAdminConsolePage.logOut();
        // enable terms
        setRequiredActionEnabled(TermsAndConditionsTest.REALM, RequiredActions.TERMS_AND_CONDITIONS, true, false);
        setRequiredActionEnabled(TermsAndConditionsTest.REALM, userId, RequiredActions.TERMS_AND_CONDITIONS, true);
        // test t&c - log in and accept
        testRealmLoginPage.form().login(TermsAndConditionsTest.HOMER, TermsAndConditionsTest.HOMER_PASS);
        Assert.assertEquals(TermsAndConditionsTest.TERMS_TEXT, termsAndConditionsPage.getText());
        termsAndConditionsPage.declineTerms();
        testRealmLoginPage.form().login(TermsAndConditionsTest.HOMER, TermsAndConditionsTest.HOMER_PASS);
        Assert.assertEquals(TermsAndConditionsTest.TERMS_TEXT, termsAndConditionsPage.getText());
        termsAndConditionsPage.acceptTerms();
        testRealmAdminConsolePage.logOut();
        testRealmLoginPage.form().login(TermsAndConditionsTest.HOMER, TermsAndConditionsTest.HOMER_PASS);
        testRealmAdminConsolePage.logOut();
        // disable terms
        setRequiredActionEnabled(TermsAndConditionsTest.REALM, RequiredActions.TERMS_AND_CONDITIONS, false, false);
    }

    @Test
    public void testAdminCreatedUser() {
        // enable terms
        setRequiredActionEnabled(TermsAndConditionsTest.REALM, RequiredActions.TERMS_AND_CONDITIONS, true, false);
        // create user
        String userId = createUser(TermsAndConditionsTest.REALM, TermsAndConditionsTest.BART, TermsAndConditionsTest.BART_PASS);
        setRequiredActionEnabled(TermsAndConditionsTest.REALM, userId, RequiredActions.TERMS_AND_CONDITIONS, true);
        // test t&c
        testRealmAdminConsolePage.navigateTo();
        testRealmLoginPage.form().login(TermsAndConditionsTest.BART, TermsAndConditionsTest.BART_PASS);
        Assert.assertTrue(termsAndConditionsPage.isCurrent());
        // disable terms
        setRequiredActionEnabled(TermsAndConditionsTest.REALM, RequiredActions.TERMS_AND_CONDITIONS, false, false);
    }

    @Test
    public void testSelfRegisteredUser() {
        // enable self-registration
        RealmResource realmResource = adminClient.realm(TermsAndConditionsTest.REALM);
        RealmRepresentation realmRepresentation = realmResource.toRepresentation();
        realmRepresentation.setRegistrationAllowed(true);
        realmResource.update(realmRepresentation);
        // enable terms
        setRequiredActionEnabled(TermsAndConditionsTest.REALM, RequiredActions.TERMS_AND_CONDITIONS, true, true);
        // self-register
        CredentialRepresentation mrBurnsPassword = new CredentialRepresentation();
        mrBurnsPassword.setType(PASSWORD);
        mrBurnsPassword.setValue("Excellent.");
        List<CredentialRepresentation> credentials = new ArrayList<CredentialRepresentation>();
        credentials.add(mrBurnsPassword);
        UserRepresentation mrBurns = new UserRepresentation();
        mrBurns.setUsername("mrburns");
        mrBurns.setFirstName("Montgomery");
        mrBurns.setLastName("Burns");
        mrBurns.setEmail("mburns@keycloak.org");
        mrBurns.setCredentials(credentials);
        testRealmAdminConsolePage.navigateTo();
        testRealmLoginPage.form().register();
        registrationPage.register(mrBurns);
        // test t&c
        Assert.assertTrue(termsAndConditionsPage.isCurrent());
        // disable terms
        setRequiredActionEnabled(TermsAndConditionsTest.REALM, RequiredActions.TERMS_AND_CONDITIONS, false, false);
    }

    @Test
    public void testTermsAndConditionsOnAccountPage() {
        String userId = createUser(TermsAndConditionsTest.REALM, TermsAndConditionsTest.FLANDERS, TermsAndConditionsTest.FLANDERS_PASS);
        setRequiredActionEnabled(TermsAndConditionsTest.REALM, RequiredActions.TERMS_AND_CONDITIONS, true, false);
        setRequiredActionEnabled(TermsAndConditionsTest.REALM, userId, RequiredActions.TERMS_AND_CONDITIONS, true);
        // login and decline the terms -- an error page should be shown
        testRealmAccountPage.navigateTo();
        loginPage.form().login(TermsAndConditionsTest.FLANDERS, TermsAndConditionsTest.FLANDERS_PASS);
        termsAndConditionsPage.assertCurrent();
        termsAndConditionsPage.declineTerms();
        // check an error page after declining the terms
        errorPage.assertCurrent();
        Assert.assertEquals("No access", errorPage.getErrorMessage());
        // follow the link "back to application"
        errorPage.backToApplication();
        // login again and accept the terms for now
        loginPage.form().login(TermsAndConditionsTest.FLANDERS, TermsAndConditionsTest.FLANDERS_PASS);
        termsAndConditionsPage.assertCurrent();
        termsAndConditionsPage.acceptTerms();
        testRealmAccountPage.assertCurrent();
        testRealmAccountPage.logOut();
        // disable terms
        setRequiredActionEnabled(TermsAndConditionsTest.REALM, RequiredActions.TERMS_AND_CONDITIONS, false, false);
    }
}

