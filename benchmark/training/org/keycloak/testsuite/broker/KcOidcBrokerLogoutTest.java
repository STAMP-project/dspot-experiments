package org.keycloak.testsuite.broker;


import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;


public class KcOidcBrokerLogoutTest extends AbstractBaseBrokerTest {
    @Test
    public void logoutWithoutInitiatingIdpLogsOutOfIdp() {
        logInAsUserInIDPForFirstTime();
        assertLoggedInAccountManagement();
        logoutFromRealm(bc.consumerRealmName());
        driver.navigate().to(getAccountUrl(BrokerTestConstants.REALM_PROV_NAME));
        BrokerTestTools.waitForPage(driver, "log in to provider", true);
    }

    @Test
    public void logoutWithActualIdpAsInitiatingIdpDoesNotLogOutOfIdp() {
        logInAsUserInIDPForFirstTime();
        assertLoggedInAccountManagement();
        logoutFromRealm(bc.consumerRealmName(), "kc-oidc-idp");
        driver.navigate().to(getAccountUrl(BrokerTestConstants.REALM_PROV_NAME));
        // could be 'keycloak account management' or 'rh-sso account management'
        BrokerTestTools.waitForPage(driver, " account management", true);
    }

    @Test
    public void logoutWithOtherIdpAsInitiatinIdpLogsOutOfIdp() {
        logInAsUserInIDPForFirstTime();
        assertLoggedInAccountManagement();
        logoutFromRealm(bc.consumerRealmName(), "something-else");
        driver.navigate().to(getAccountUrl(BrokerTestConstants.REALM_PROV_NAME));
        BrokerTestTools.waitForPage(driver, "log in to provider", true);
    }
}

