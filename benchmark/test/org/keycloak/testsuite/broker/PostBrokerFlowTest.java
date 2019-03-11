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
package org.keycloak.testsuite.broker;


import AuthenticationExecutionModel.Requirement.DISABLED;
import AuthenticationExecutionModel.Requirement.REQUIRED;
import DefaultAuthenticationFlows.FIRST_BROKER_LOGIN_HANDLE_EXISTING_SUBFLOW;
import IdentityProviderRepresentation.UPFLM_OFF;
import IdpEmailVerificationAuthenticatorFactory.PROVIDER_ID;
import org.junit.ClassRule;
import org.junit.Test;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.TimeBasedOTP;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.testsuite.KeycloakServer;
import org.keycloak.testsuite.pages.IdpConfirmLinkPage;
import org.keycloak.testsuite.pages.LoginConfigTotpPage;
import org.keycloak.testsuite.pages.LoginTotpPage;
import org.keycloak.testsuite.rule.AbstractKeycloakRule;
import org.keycloak.testsuite.rule.WebResource;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class PostBrokerFlowTest extends AbstractIdentityProviderTest {
    private static final int PORT = 8082;

    private static String POST_BROKER_FLOW_ID;

    private static final String APP_REALM_ID = "realm-with-broker";

    @ClassRule
    public static AbstractKeycloakRule samlServerRule = new AbstractKeycloakRule() {
        @Override
        protected void configureServer(KeycloakServer server) {
            server.getConfig().setPort(PostBrokerFlowTest.PORT);
        }

        @Override
        protected void configure(KeycloakSession session, RealmManager manager, RealmModel adminRealm) {
            server.importRealm(getClass().getResourceAsStream("/broker-test/test-broker-realm-with-kc-oidc.json"));
            server.importRealm(getClass().getResourceAsStream("/broker-test/test-broker-realm-with-saml.json"));
            RealmModel realmWithBroker = AbstractIdentityProviderTest.getRealm(session);
            // Disable "idp-email-verification" authenticator in firstBrokerLogin flow. Disable updateProfileOnFirstLogin page
            AbstractFirstBrokerLoginTest.setExecutionRequirement(realmWithBroker, FIRST_BROKER_LOGIN_HANDLE_EXISTING_SUBFLOW, PROVIDER_ID, DISABLED);
            AbstractIdentityProviderTest.setUpdateProfileFirstLogin(realmWithBroker, UPFLM_OFF);
            // Add post-broker flow with OTP authenticator to the realm
            AuthenticationFlowModel postBrokerFlow = new AuthenticationFlowModel();
            postBrokerFlow.setAlias("post-broker");
            postBrokerFlow.setDescription("post-broker flow with OTP");
            postBrokerFlow.setProviderId("basic-flow");
            postBrokerFlow.setTopLevel(true);
            postBrokerFlow.setBuiltIn(false);
            postBrokerFlow = realmWithBroker.addAuthenticationFlow(postBrokerFlow);
            PostBrokerFlowTest.POST_BROKER_FLOW_ID = postBrokerFlow.getId();
            AuthenticationExecutionModel execution = new AuthenticationExecutionModel();
            execution.setParentFlow(postBrokerFlow.getId());
            execution.setRequirement(REQUIRED);
            execution.setAuthenticator("auth-otp-form");
            execution.setPriority(20);
            execution.setAuthenticatorFlow(false);
            realmWithBroker.addAuthenticatorExecution(execution);
        }

        @Override
        protected String[] getTestRealms() {
            return new String[]{ "realm-with-oidc-identity-provider", "realm-with-saml-idp-basic" };
        }
    };

    @WebResource
    protected IdpConfirmLinkPage idpConfirmLinkPage;

    @WebResource
    protected LoginTotpPage loginTotpPage;

    @WebResource
    protected LoginConfigTotpPage totpPage;

    private TimeBasedOTP totp = new TimeBasedOTP();

    @Test
    public void testPostBrokerLoginWithOTP() {
        // enable post-broker flow
        IdentityProviderModel identityProvider = getIdentityProviderModel();
        setPostBrokerFlowForProvider(identityProvider, getRealm(), true);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
        this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
        // login with broker and assert that OTP needs to be set.
        loginIDP("test-user");
        totpPage.assertCurrent();
        String totpSecret = totpPage.getTotpSecret();
        totpPage.configure(totp.generateTOTP(totpSecret));
        assertFederatedUser("test-user", "test-user@localhost", "test-user", getProviderId());
        driver.navigate().to("http://localhost:8081/test-app/logout");
        // Login again and assert that OTP needs to be provided.
        loginIDP("test-user");
        loginTotpPage.assertCurrent();
        loginTotpPage.login(totp.generateTOTP(totpSecret));
        assertFederatedUser("test-user", "test-user@localhost", "test-user", getProviderId());
        driver.navigate().to("http://localhost:8081/test-app/logout");
        // Disable post-broker and ensure that OTP is not required anymore
        setPostBrokerFlowForProvider(identityProvider, getRealm(), false);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
        this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
        loginIDP("test-user");
        assertFederatedUser("test-user", "test-user@localhost", "test-user", getProviderId());
        driver.navigate().to("http://localhost:8081/test-app/logout");
    }

    @Test
    public void testBrokerReauthentication_samlBrokerWithOTPRequired() throws Exception {
        RealmModel realmWithBroker = getRealm();
        // Enable OTP just for SAML provider
        IdentityProviderModel samlIdentityProvider = realmWithBroker.getIdentityProviderByAlias("kc-saml-idp-basic");
        setPostBrokerFlowForProvider(samlIdentityProvider, realmWithBroker, true);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
        this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
        // ensure TOTP setup is required during SAML broker firstLogin and during reauthentication for link OIDC broker too
        reauthenticateOIDCWithSAMLBroker(true, false);
        // Disable TOTP for SAML provider
        realmWithBroker = getRealm();
        samlIdentityProvider = realmWithBroker.getIdentityProviderByAlias("kc-saml-idp-basic");
        setPostBrokerFlowForProvider(samlIdentityProvider, realmWithBroker, false);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
        this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
    }

    @Test
    public void testBrokerReauthentication_oidcBrokerWithOTPRequired() throws Exception {
        // Enable OTP just for OIDC provider
        IdentityProviderModel oidcIdentityProvider = getIdentityProviderModel();
        setPostBrokerFlowForProvider(oidcIdentityProvider, getRealm(), true);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
        this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
        // ensure TOTP setup is not required during SAML broker firstLogin, but during reauthentication for link OIDC broker
        reauthenticateOIDCWithSAMLBroker(false, true);
        // Disable TOTP for SAML provider
        oidcIdentityProvider = getIdentityProviderModel();
        setPostBrokerFlowForProvider(oidcIdentityProvider, getRealm(), false);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
        this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
    }

    @Test
    public void testBrokerReauthentication_bothBrokerWithOTPRequired() throws Exception {
        RealmModel realmWithBroker = getRealm();
        // Enable OTP for both OIDC and SAML provider
        IdentityProviderModel samlIdentityProvider = realmWithBroker.getIdentityProviderByAlias("kc-saml-idp-basic");
        setPostBrokerFlowForProvider(samlIdentityProvider, realmWithBroker, true);
        IdentityProviderModel oidcIdentityProvider = getIdentityProviderModel();
        setPostBrokerFlowForProvider(oidcIdentityProvider, getRealm(), true);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
        this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
        // ensure TOTP setup is required during SAML broker firstLogin and during reauthentication for link OIDC broker too
        reauthenticateOIDCWithSAMLBroker(true, true);
        // Disable TOTP for both SAML and OIDC provider
        realmWithBroker = getRealm();
        samlIdentityProvider = realmWithBroker.getIdentityProviderByAlias("kc-saml-idp-basic");
        setPostBrokerFlowForProvider(samlIdentityProvider, realmWithBroker, false);
        oidcIdentityProvider = getIdentityProviderModel();
        setPostBrokerFlowForProvider(oidcIdentityProvider, getRealm(), false);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
        this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
    }
}

