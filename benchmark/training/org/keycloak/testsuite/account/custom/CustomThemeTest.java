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
package org.keycloak.testsuite.account.custom;


import Details.REDIRECT_URI;
import EventType.UPDATE_PROFILE;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AccountUpdateProfilePage;
import org.keycloak.testsuite.pages.LoginPage;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CustomThemeTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected LoginPage loginPage;

    @Page
    protected AccountUpdateProfilePage profilePage;

    // KEYCLOAK-3494
    @Test
    public void changeProfile() throws Exception {
        profilePage.open();
        loginPage.login("test-user@localhost", "password");
        events.expectLogin().client("account").detail(REDIRECT_URI, getAccountRedirectUrl()).assertEvent();
        Assert.assertEquals("test-user@localhost", profilePage.getEmail());
        Assert.assertEquals("", profilePage.getAttribute("street"));
        profilePage.updateAttribute("street", "Elm 1");
        Assert.assertEquals("Elm 1", profilePage.getAttribute("street"));
        profilePage.updateAttribute("street", "Elm 2");
        Assert.assertEquals("Elm 2", profilePage.getAttribute("street"));
        events.expectAccount(UPDATE_PROFILE).assertEvent();
    }
}

