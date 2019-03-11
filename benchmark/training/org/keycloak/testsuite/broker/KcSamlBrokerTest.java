package org.keycloak.testsuite.broker;


import Binding.POST;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.saml.processing.api.saml.v2.request.SAML2Request;
import org.keycloak.saml.processing.core.saml.v2.common.SAMLDocumentHolder;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.saml.AbstractSamlTest;
import org.keycloak.testsuite.util.SamlClient;
import org.keycloak.testsuite.util.SamlClientBuilder;
import org.w3c.dom.Document;

import static org.keycloak.testsuite.util.Matchers.isSamlResponse;


public class KcSamlBrokerTest extends AbstractBrokerTest {
    // KEYCLOAK-3987
    @Test
    @Override
    public void grantNewRoleFromToken() {
        createRolesForRealm(bc.providerRealmName());
        createRolesForRealm(bc.consumerRealmName());
        createRoleMappersForConsumerRealm();
        RoleRepresentation managerRole = adminClient.realm(bc.providerRealmName()).roles().get(AbstractBrokerTest.ROLE_MANAGER).toRepresentation();
        RoleRepresentation friendlyManagerRole = adminClient.realm(bc.providerRealmName()).roles().get(AbstractBrokerTest.ROLE_FRIENDLY_MANAGER).toRepresentation();
        RoleRepresentation userRole = adminClient.realm(bc.providerRealmName()).roles().get(AbstractBrokerTest.ROLE_USER).toRepresentation();
        UserResource userResource = adminClient.realm(bc.providerRealmName()).users().get(userId);
        userResource.roles().realmLevel().add(Collections.singletonList(managerRole));
        logInAsUserInIDPForFirstTime();
        Set<String> currentRoles = userResource.roles().realmLevel().listAll().stream().map(RoleRepresentation::getName).collect(Collectors.toSet());
        Assert.assertThat(currentRoles, Matchers.hasItems(AbstractBrokerTest.ROLE_MANAGER));
        Assert.assertThat(currentRoles, Matchers.not(Matchers.hasItems(AbstractBrokerTest.ROLE_USER, AbstractBrokerTest.ROLE_FRIENDLY_MANAGER)));
        logoutFromRealm(bc.consumerRealmName());
        userResource.roles().realmLevel().add(Collections.singletonList(userRole));
        userResource.roles().realmLevel().add(Collections.singletonList(friendlyManagerRole));
        logInAsUserInIDP();
        currentRoles = userResource.roles().realmLevel().listAll().stream().map(RoleRepresentation::getName).collect(Collectors.toSet());
        Assert.assertThat(currentRoles, Matchers.hasItems(AbstractBrokerTest.ROLE_MANAGER, AbstractBrokerTest.ROLE_USER, AbstractBrokerTest.ROLE_FRIENDLY_MANAGER));
        logoutFromRealm(bc.consumerRealmName());
        userResource.roles().realmLevel().remove(Collections.singletonList(friendlyManagerRole));
        logInAsUserInIDP();
        currentRoles = userResource.roles().realmLevel().listAll().stream().map(RoleRepresentation::getName).collect(Collectors.toSet());
        Assert.assertThat(currentRoles, Matchers.hasItems(AbstractBrokerTest.ROLE_MANAGER, AbstractBrokerTest.ROLE_USER));
        Assert.assertThat(currentRoles, Matchers.not(Matchers.hasItems(AbstractBrokerTest.ROLE_FRIENDLY_MANAGER)));
        logoutFromRealm(bc.providerRealmName());
        logoutFromRealm(bc.consumerRealmName());
    }

    // KEYCLOAK-6106
    @Test
    public void loginClientWithDotsInName() throws Exception {
        AuthnRequestType loginRep = SamlClient.createLoginRequestDocument(((AbstractSamlTest.SAML_CLIENT_ID_SALES_POST) + ".dot/ted"), ((((AbstractKeycloakTest.AUTH_SERVER_SCHEME) + "://localhost:") + (AbstractKeycloakTest.AUTH_SERVER_PORT)) + "/sales-post/saml"), null);
        Document doc = SAML2Request.convert(loginRep);
        SAMLDocumentHolder samlResponse = // first-broker flow
        // Response from producer IdP
        // AuthnRequest to producer IdP
        // Request to consumer IdP
        new SamlClientBuilder().authnRequest(getAuthServerSamlEndpoint(bc.consumerRealmName()), doc, POST).build().login().idp(bc.getIDPAlias()).build().processSamlResponse(POST).targetAttributeSamlRequest().build().login().user(bc.getUserLogin(), bc.getUserPassword()).build().processSamlResponse(POST).build().updateProfile().firstName("a").lastName("b").email(bc.getUserEmail()).username(bc.getUserLogin()).build().followOneRedirect().getSamlResponse(POST);// Response from consumer IdP

        Assert.assertThat(samlResponse, Matchers.notNullValue());
        Assert.assertThat(samlResponse.getSamlObject(), isSamlResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
    }
}

