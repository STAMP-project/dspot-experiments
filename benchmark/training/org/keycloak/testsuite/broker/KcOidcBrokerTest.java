package org.keycloak.testsuite.broker;


import Algorithm.RS256;
import OIDCConfigAttributes.USER_INFO_RESPONSE_SIGNATURE_ALG;
import OIDCIdentityProviderConfig.JWKS_URL;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.IdentityProviderResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;


public class KcOidcBrokerTest extends AbstractBrokerTest {
    @Test
    public void loginFetchingUserFromUserEndpoint() {
        RealmResource realm = realmsResouce().realm(bc.providerRealmName());
        ClientsResource clients = realm.clients();
        ClientRepresentation brokerApp = clients.findByClientId("brokerapp").get(0);
        try {
            IdentityProviderResource identityProviderResource = realmsResouce().realm(bc.consumerRealmName()).identityProviders().get(bc.getIDPAlias());
            IdentityProviderRepresentation idp = identityProviderResource.toRepresentation();
            idp.getConfig().put(JWKS_URL, ((((BrokerTestTools.getAuthRoot(suiteContext)) + "/auth/realms/") + (BrokerTestConstants.REALM_PROV_NAME)) + "/protocol/openid-connect/certs"));
            identityProviderResource.update(idp);
            brokerApp.getAttributes().put(USER_INFO_RESPONSE_SIGNATURE_ALG, RS256);
            brokerApp.getAttributes().put("validateSignature", Boolean.TRUE.toString());
            clients.get(brokerApp.getId()).update(brokerApp);
            driver.navigate().to(getAccountUrl(bc.consumerRealmName()));
            log.debug(("Clicking social " + (bc.getIDPAlias())));
            accountLoginPage.clickSocial(bc.getIDPAlias());
            BrokerTestTools.waitForPage(driver, "log in to", true);
            org.keycloak.testsuite.Assert.assertTrue("Driver should be on the provider realm page right now", driver.getCurrentUrl().contains((("/auth/realms/" + (bc.providerRealmName())) + "/")));
            log.debug("Logging in");
            accountLoginPage.login(bc.getUserLogin(), bc.getUserPassword());
            BrokerTestTools.waitForPage(driver, "update account information", false);
            updateAccountInformationPage.assertCurrent();
            org.keycloak.testsuite.Assert.assertTrue("We must be on correct realm right now", driver.getCurrentUrl().contains((("/auth/realms/" + (bc.consumerRealmName())) + "/")));
            log.debug("Updating info on updateAccount page");
            updateAccountInformationPage.updateAccountInformation(bc.getUserLogin(), bc.getUserEmail(), "Firstname", "Lastname");
            UsersResource consumerUsers = adminClient.realm(bc.consumerRealmName()).users();
            int userCount = consumerUsers.count();
            org.keycloak.testsuite.Assert.assertTrue("There must be at least one user", (userCount > 0));
            List<UserRepresentation> users = consumerUsers.search("", 0, userCount);
            boolean isUserFound = false;
            for (UserRepresentation user : users) {
                if ((user.getUsername().equals(bc.getUserLogin())) && (user.getEmail().equals(bc.getUserEmail()))) {
                    isUserFound = true;
                    break;
                }
            }
            org.keycloak.testsuite.Assert.assertTrue(((("There must be user " + (bc.getUserLogin())) + " in realm ") + (bc.consumerRealmName())), isUserFound);
        } finally {
            brokerApp.getAttributes().put(USER_INFO_RESPONSE_SIGNATURE_ALG, null);
            brokerApp.getAttributes().put("validateSignature", Boolean.FALSE.toString());
            clients.get(brokerApp.getId()).update(brokerApp);
        }
    }
}

