package org.keycloak.testsuite.broker;


import Binding.POST;
import JBossSAMLURIConstants.STATUS_SUCCESS;
import java.util.List;
import org.hamcrest.Matchers;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.dom.saml.v2.protocol.ResponseType;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.saml.processing.core.saml.v2.common.SAMLDocumentHolder;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.UpdateAccountInformationPage;
import org.keycloak.testsuite.util.SamlClientBuilder;
import org.openqa.selenium.By;


/**
 *
 *
 * @author hmlnarik
 */
public class KcSamlIdPInitiatedSsoTest extends AbstractKeycloakTest {
    private static final String PROVIDER_REALM_USER_NAME = "test";

    private static final String PROVIDER_REALM_USER_PASSWORD = "test";

    private static final String CONSUMER_CHOSEN_USERNAME = "mytest";

    @Page
    protected LoginPage accountLoginPage;

    @Page
    protected UpdateAccountInformationPage updateAccountInformationPage;

    private String urlRealmConsumer2;

    private String urlRealmConsumer;

    private String urlRealmProvider;

    @Test
    public void testProviderIdpInitiatedLogin() throws Exception {
        driver.navigate().to(getSamlIdpInitiatedUrl(BrokerTestConstants.REALM_PROV_NAME, "samlbroker"));
        waitForPage("log in to", true);
        org.keycloak.testsuite.Assert.assertThat("Driver should be on the provider realm page right now", driver.getCurrentUrl(), Matchers.containsString((("/auth/realms/" + (BrokerTestConstants.REALM_PROV_NAME)) + "/")));
        log.debug("Logging in");
        accountLoginPage.login(KcSamlIdPInitiatedSsoTest.PROVIDER_REALM_USER_NAME, KcSamlIdPInitiatedSsoTest.PROVIDER_REALM_USER_PASSWORD);
        waitForPage("update account information", false);
        org.keycloak.testsuite.Assert.assertTrue(updateAccountInformationPage.isCurrent());
        org.keycloak.testsuite.Assert.assertThat("We must be on consumer realm right now", driver.getCurrentUrl(), Matchers.containsString((("/auth/realms/" + (BrokerTestConstants.REALM_CONS_NAME)) + "/")));
        log.debug("Updating info on updateAccount page");
        updateAccountInformationPage.updateAccountInformation(KcSamlIdPInitiatedSsoTest.CONSUMER_CHOSEN_USERNAME, "test@localhost", "Firstname", "Lastname");
        UsersResource consumerUsers = adminClient.realm(BrokerTestConstants.REALM_CONS_NAME).users();
        int userCount = consumerUsers.count();
        org.keycloak.testsuite.Assert.assertTrue("There must be at least one user", (userCount > 0));
        List<UserRepresentation> users = consumerUsers.search("", 0, userCount);
        boolean isUserFound = users.stream().anyMatch(( user) -> (user.getUsername().equals(CONSUMER_CHOSEN_USERNAME)) && (user.getEmail().equals("test@localhost")));
        org.keycloak.testsuite.Assert.assertTrue(((("There must be user " + (KcSamlIdPInitiatedSsoTest.CONSUMER_CHOSEN_USERNAME)) + " in realm ") + (BrokerTestConstants.REALM_CONS_NAME)), isUserFound);
        org.keycloak.testsuite.Assert.assertThat(driver.findElement(By.tagName("a")).getAttribute("id"), Matchers.containsString("account"));
    }

    @Test
    public void testProviderIdpInitiatedLoginToApp() throws Exception {
        SAMLDocumentHolder samlResponse = // Obtain the response sent to the app
        // Send the response to the consumer realm
        // Login in provider realm
        new SamlClientBuilder().navigateTo(getSamlIdpInitiatedUrl(BrokerTestConstants.REALM_PROV_NAME, "samlbroker")).login().user(KcSamlIdPInitiatedSsoTest.PROVIDER_REALM_USER_NAME, KcSamlIdPInitiatedSsoTest.PROVIDER_REALM_USER_PASSWORD).build().processSamlResponse(POST).transformObject(( ob) -> {
            assertThat(ob, org.keycloak.testsuite.util.Matchers.isSamlResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
            ResponseType resp = ((ResponseType) (ob));
            assertThat(resp.getDestination(), is(getSamlBrokerIdpInitiatedUrl(REALM_CONS_NAME, "sales")));
            assertAudience(resp, getSamlBrokerIdpInitiatedUrl(REALM_CONS_NAME, "sales"));
            return ob;
        }).build().updateProfile().username(KcSamlIdPInitiatedSsoTest.CONSUMER_CHOSEN_USERNAME).email("test@localhost").firstName("Firstname").lastName("Lastname").build().followOneRedirect().getSamlResponse(POST);
        Assert.assertThat(samlResponse.getSamlObject(), org.keycloak.testsuite.util.Matchers.isSamlResponse(STATUS_SUCCESS));
        ResponseType resp = ((ResponseType) (samlResponse.getSamlObject()));
        Assert.assertThat(resp.getDestination(), Matchers.is(((urlRealmConsumer) + "/app/auth")));
        assertAudience(resp, ((urlRealmConsumer) + "/app/auth"));
    }

    @Test
    public void testConsumerIdpInitiatedLoginToApp() throws Exception {
        SAMLDocumentHolder samlResponse = // Obtain the response sent to the app
        // Send the response to the consumer realm
        // Login in provider realm
        // AuthnRequest to producer IdP
        // Request login via saml-leaf
        new SamlClientBuilder().navigateTo(getSamlIdpInitiatedUrl(BrokerTestConstants.REALM_CONS_NAME, "sales")).login().idp("saml-leaf").build().processSamlResponse(POST).targetAttributeSamlRequest().build().login().user(KcSamlIdPInitiatedSsoTest.PROVIDER_REALM_USER_NAME, KcSamlIdPInitiatedSsoTest.PROVIDER_REALM_USER_PASSWORD).build().processSamlResponse(POST).transformObject(( ob) -> {
            assertThat(ob, org.keycloak.testsuite.util.Matchers.isSamlResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
            ResponseType resp = ((ResponseType) (ob));
            assertThat(resp.getDestination(), is(getSamlBrokerUrl(REALM_CONS_NAME)));
            assertAudience(resp, urlRealmConsumer);
            return ob;
        }).build().updateProfile().username(KcSamlIdPInitiatedSsoTest.CONSUMER_CHOSEN_USERNAME).email("test@localhost").firstName("Firstname").lastName("Lastname").build().followOneRedirect().getSamlResponse(POST);
        Assert.assertThat(samlResponse.getSamlObject(), org.keycloak.testsuite.util.Matchers.isSamlResponse(STATUS_SUCCESS));
        ResponseType resp = ((ResponseType) (samlResponse.getSamlObject()));
        Assert.assertThat(resp.getDestination(), Matchers.is(((urlRealmConsumer) + "/app/auth")));
        assertAudience(resp, ((urlRealmConsumer) + "/app/auth"));
    }

    @Test
    public void testTwoConsequentIdpInitiatedLogins() throws Exception {
        SAMLDocumentHolder samlResponse = // Send the response to the consumer realm
        // Login in provider realm
        // Now login to the second app
        // Obtain the response sent to the app and ignore result
        // Send the response to the consumer realm
        // Login in provider realm
        new SamlClientBuilder().navigateTo(getSamlIdpInitiatedUrl(BrokerTestConstants.REALM_PROV_NAME, "samlbroker")).login().user(KcSamlIdPInitiatedSsoTest.PROVIDER_REALM_USER_NAME, KcSamlIdPInitiatedSsoTest.PROVIDER_REALM_USER_PASSWORD).build().processSamlResponse(POST).transformObject(( ob) -> {
            assertThat(ob, org.keycloak.testsuite.util.Matchers.isSamlResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
            ResponseType resp = ((ResponseType) (ob));
            assertThat(resp.getDestination(), is(getSamlBrokerIdpInitiatedUrl(REALM_CONS_NAME, "sales")));
            assertAudience(resp, getSamlBrokerIdpInitiatedUrl(REALM_CONS_NAME, "sales"));
            return ob;
        }).build().updateProfile().username(KcSamlIdPInitiatedSsoTest.CONSUMER_CHOSEN_USERNAME).email("test@localhost").firstName("Firstname").lastName("Lastname").build().followOneRedirect().processSamlResponse(POST).transformObject(( ob) -> {
            assertThat(ob, org.keycloak.testsuite.util.Matchers.isSamlResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
            ResponseType resp = ((ResponseType) (ob));
            assertThat(resp.getDestination(), is(((urlRealmConsumer) + "/app/auth")));
            assertAudience(resp, ((urlRealmConsumer) + "/app/auth"));
            return null;
        }).build().navigateTo(getSamlIdpInitiatedUrl(BrokerTestConstants.REALM_PROV_NAME, "samlbroker-2")).login().sso(true).build().processSamlResponse(POST).transformObject(( ob) -> {
            assertThat(ob, org.keycloak.testsuite.util.Matchers.isSamlResponse(JBossSAMLURIConstants.STATUS_SUCCESS));
            ResponseType resp = ((ResponseType) (ob));
            assertThat(resp.getDestination(), is(getSamlBrokerIdpInitiatedUrl(REALM_CONS_NAME, "sales2")));
            assertAudience(resp, getSamlBrokerIdpInitiatedUrl(REALM_CONS_NAME, "sales2"));
            return ob;
        }).build().getSamlResponse(POST);
        Assert.assertThat(samlResponse.getSamlObject(), org.keycloak.testsuite.util.Matchers.isSamlResponse(STATUS_SUCCESS));
        ResponseType resp = ((ResponseType) (samlResponse.getSamlObject()));
        Assert.assertThat(resp.getDestination(), Matchers.is(((urlRealmConsumer) + "/app/auth2/saml")));
        assertAudience(resp, ((urlRealmConsumer) + "/app/auth2"));
        assertSingleUserSession(BrokerTestConstants.REALM_CONS_NAME, KcSamlIdPInitiatedSsoTest.CONSUMER_CHOSEN_USERNAME, ((urlRealmConsumer) + "/app/auth"), ((urlRealmConsumer) + "/app/auth2"));
        assertSingleUserSession(BrokerTestConstants.REALM_PROV_NAME, KcSamlIdPInitiatedSsoTest.PROVIDER_REALM_USER_NAME, ((urlRealmConsumer) + "/broker/saml-leaf/endpoint/clients/sales"), ((urlRealmConsumer) + "/broker/saml-leaf/endpoint/clients/sales2"));
    }
}

