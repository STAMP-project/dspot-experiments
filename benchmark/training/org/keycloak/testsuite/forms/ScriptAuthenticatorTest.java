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
package org.keycloak.testsuite.forms;


import Details.USERNAME;
import Errors.USER_NOT_FOUND;
import EventType.LOGIN_ERROR;
import com.google.common.collect.ImmutableMap;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.LoginPage;


/**
 * Tests for {@link org.keycloak.authentication.authenticators.browser.ScriptBasedAuthenticator}
 *
 * @author <a href="mailto:thomas.darimont@gmail.com">Thomas Darimont</a>
 */
public class ScriptAuthenticatorTest extends AbstractFlowTest {
    @Page
    protected LoginPage loginPage;

    @Rule
    public AssertEvents events = new AssertEvents(this);

    private AuthenticationFlowRepresentation flow;

    public static final String EXECUTION_ID = "scriptAuth";

    /**
     * KEYCLOAK-3491
     */
    @Test
    public void loginShouldWorkWithScriptAuthenticator() {
        addConfigFromFile("/scripts/authenticator-example.js");
        loginPage.open();
        loginPage.login("user", "password");
        events.expectLogin().user("user").detail(USERNAME, "user").assertEvent();
    }

    /**
     * KEYCLOAK-3491
     */
    @Test
    public void loginShouldFailWithScriptAuthenticator() {
        addConfigFromFile("/scripts/authenticator-example.js");
        loginPage.open();
        loginPage.login("fail", "password");
        events.expect(LOGIN_ERROR).user(((String) (null))).error(USER_NOT_FOUND).assertEvent();
    }

    /**
     * KEYCLOAK-4505
     */
    @Test
    public void scriptWithClientSession() {
        addConfigFromFile("/scripts/client-session-test.js", ImmutableMap.of("realm", "test", "clientId", "test-app", "authMethod", "openid-connect"));
        loginPage.open();
        loginPage.login("user", "password");
        events.expectLogin().user("user").detail(USERNAME, "user").assertEvent();
    }
}

