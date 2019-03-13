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
package org.keycloak.testsuite.openshift;


import Details.CONSENT_VALUE_PERSISTED_CONSENT;
import OAuthErrorException.INVALID_REDIRECT_URI;
import OpenshiftClientStorageProviderFactory.CONFIG_PROPERTY_REQUIRE_USER_CONSENT;
import io.undertow.Undertow;
import java.util.Arrays;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.ComponentResource;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ConsentPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;


/**
 * Test that clients can override auth flows
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public final class OpenshiftClientStorageTest extends AbstractTestRealmKeycloakTest {
    private static Undertow OPENSHIFT_API_SERVER;

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    private LoginPage loginPage;

    @Page
    private AppPage appPage;

    @Page
    private ConsentPage consentPage;

    @Page
    private ErrorPage errorPage;

    private String userId;

    private String clientStorageId;

    @Test
    public void testCodeGrantFlowWithServiceAccountUsingOAuthRedirectReference() {
        String clientId = "system:serviceaccount:default:sa-oauth-redirect-reference";
        testCodeGrantFlow(clientId, "https://myapp.org/callback", () -> assertSuccessfulResponseWithoutConsent(clientId));
    }

    @Test
    public void failCodeGrantFlowWithServiceAccountUsingOAuthRedirectReference() throws Exception {
        testCodeGrantFlow("system:serviceaccount:default:sa-oauth-redirect-reference", "http://myapp.org/callback", () -> Assert.assertEquals(INVALID_REDIRECT_URI, events.poll().getError()));
    }

    @Test
    public void testCodeGrantFlowWithServiceAccountUsingOAuthRedirectUri() {
        String clientId = "system:serviceaccount:default:sa-oauth-redirect-uri";
        testCodeGrantFlow(clientId, "http://localhost:8180/auth/realms/master/app/auth", () -> assertSuccessfulResponseWithoutConsent(clientId));
        testCodeGrantFlow(clientId, "http://localhost:8180/auth/realms/master/app/auth/second", () -> assertSuccessfulResponseWithoutConsent(clientId));
        testCodeGrantFlow(clientId, "http://localhost:8180/auth/realms/master/app/auth/third", () -> assertSuccessfulResponseWithoutConsent(clientId));
    }

    @Test
    public void testCodeGrantFlowWithUserConsent() {
        String clientId = "system:serviceaccount:default:sa-oauth-redirect-uri";
        testCodeGrantFlow(clientId, "http://localhost:8180/auth/realms/master/app/auth", () -> assertSuccessfulResponseWithConsent(clientId), "user:info user:check-access");
        ComponentResource component = testRealm().components().component(clientStorageId);
        ComponentRepresentation representation = component.toRepresentation();
        representation.getConfig().put(CONFIG_PROPERTY_REQUIRE_USER_CONSENT, Arrays.asList("false"));
        component.update(representation);
        testCodeGrantFlow(clientId, "http://localhost:8180/auth/realms/master/app/auth", () -> assertSuccessfulResponseWithoutConsent(clientId), "user:info user:check-access");
        representation.getConfig().put(CONFIG_PROPERTY_REQUIRE_USER_CONSENT, Arrays.asList("true"));
        component.update(representation);
        testCodeGrantFlow(clientId, "http://localhost:8180/auth/realms/master/app/auth", () -> assertSuccessfulResponseWithoutConsent(clientId, CONSENT_VALUE_PERSISTED_CONSENT), "user:info user:check-access");
        testRealm().users().get(userId).revokeConsent(clientId);
        testCodeGrantFlow(clientId, "http://localhost:8180/auth/realms/master/app/auth", () -> assertSuccessfulResponseWithConsent(clientId), "user:info user:check-access");
    }

    @Test
    public void failCodeGrantFlowWithServiceAccountUsingOAuthRedirectUri() throws Exception {
        testCodeGrantFlow("system:serviceaccount:default:sa-oauth-redirect-uri", "http://myapp.org/callback", () -> Assert.assertEquals(INVALID_REDIRECT_URI, events.poll().getError()));
    }
}

