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
package org.keycloak.testsuite.admin;


import CredentialRepresentation.PASSWORD;
import DummyUserFederationProviderFactory.PROVIDER_NAME;
import MailUtils.EmailBody;
import OperationType.ACTION;
import OperationType.CREATE;
import OperationType.DELETE;
import OperationType.UPDATE;
import PasswordPolicy.HASH_ALGORITHM_DEFAULT;
import PasswordPolicy.HASH_ITERATIONS_DEFAULT;
import ResourceType.CLIENT;
import ResourceType.CLIENT_ROLE_MAPPING;
import ResourceType.COMPONENT;
import ResourceType.REALM_ROLE_MAPPING;
import ResourceType.REQUIRED_ACTION;
import ResourceType.USER;
import UserModel.RequiredAction.UPDATE_PASSWORD;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.hamcrest.Matchers;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.common.util.Base64;
import org.keycloak.credential.CredentialModel;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.ErrorRepresentation;
import org.keycloak.representations.idm.FederatedIdentityRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RequiredActionProviderRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.services.resources.RealmsResource;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.page.LoginPasswordUpdatePage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.InfoPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.PageUtils;
import org.keycloak.testsuite.pages.ProceedPage;
import org.keycloak.testsuite.util.AdminEventPaths;
import org.keycloak.testsuite.util.ClientBuilder;
import org.keycloak.testsuite.util.GreenMailRule;
import org.keycloak.testsuite.util.MailUtils;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.RealmBuilder;
import org.keycloak.testsuite.util.RoleBuilder;
import org.keycloak.testsuite.util.UserBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.keycloak.testsuite.Assert.assertNames;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class UserTest extends AbstractAdminTest {
    @Rule
    public GreenMailRule greenMail = new GreenMailRule();

    @Drone
    protected WebDriver driver;

    @Page
    protected LoginPasswordUpdatePage passwordUpdatePage;

    @ArquillianResource
    protected OAuthClient oAuthClient;

    @Page
    protected InfoPage infoPage;

    @Page
    protected ProceedPage proceedPage;

    @Page
    protected ErrorPage errorPage;

    @Page
    protected LoginPage loginPage;

    @Test
    public void verifyCreateUser() {
        createUser();
    }

    @Test
    public void createDuplicatedUser1() {
        createUser();
        UserRepresentation user = new UserRepresentation();
        user.setUsername("user1");
        Response response = realm.users().create(user);
        Assert.assertEquals(409, response.getStatus());
        assertAdminEvents.assertEmpty();
        // Just to show how to retrieve underlying error message
        ErrorRepresentation error = response.readEntity(ErrorRepresentation.class);
        Assert.assertEquals("User exists with same username", error.getErrorMessage());
        response.close();
    }

    @Test
    public void createDuplicatedUser2() {
        createUser();
        UserRepresentation user = new UserRepresentation();
        user.setUsername("user2");
        user.setEmail("user1@localhost");
        Response response = realm.users().create(user);
        Assert.assertEquals(409, response.getStatus());
        response.close();
    }

    @Test
    public void createUserWithHashedCredentials() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("user_creds");
        user.setEmail("email@localhost");
        CredentialRepresentation hashedPassword = new CredentialRepresentation();
        hashedPassword.setAlgorithm("my-algorithm");
        hashedPassword.setCounter(11);
        hashedPassword.setCreatedDate(1001L);
        hashedPassword.setDevice("deviceX");
        hashedPassword.setDigits(6);
        hashedPassword.setHashIterations(22);
        hashedPassword.setHashedSaltedValue("ABC");
        hashedPassword.setPeriod(99);
        hashedPassword.setSalt(Base64.encodeBytes("theSalt".getBytes()));
        hashedPassword.setType(PASSWORD);
        user.setCredentials(Arrays.asList(hashedPassword));
        createUser(user);
        CredentialModel credentialHashed = fetchCredentials("user_creds");
        Assert.assertNotNull("Expecting credential", credentialHashed);
        Assert.assertEquals("my-algorithm", credentialHashed.getAlgorithm());
        Assert.assertEquals(11, credentialHashed.getCounter());
        Assert.assertEquals(Long.valueOf(1001), credentialHashed.getCreatedDate());
        Assert.assertEquals("deviceX", credentialHashed.getDevice());
        Assert.assertEquals(6, credentialHashed.getDigits());
        Assert.assertEquals(22, credentialHashed.getHashIterations());
        Assert.assertEquals("ABC", credentialHashed.getValue());
        Assert.assertEquals(99, credentialHashed.getPeriod());
        Assert.assertEquals("theSalt", new String(credentialHashed.getSalt()));
        Assert.assertEquals(PASSWORD, credentialHashed.getType());
    }

    @Test
    public void createUserWithRawCredentials() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("user_rawpw");
        user.setEmail("email.raw@localhost");
        CredentialRepresentation rawPassword = new CredentialRepresentation();
        rawPassword.setValue("ABCD");
        rawPassword.setType(PASSWORD);
        user.setCredentials(Arrays.asList(rawPassword));
        createUser(user);
        CredentialModel credential = fetchCredentials("user_rawpw");
        Assert.assertNotNull("Expecting credential", credential);
        Assert.assertEquals(HASH_ALGORITHM_DEFAULT, credential.getAlgorithm());
        Assert.assertEquals(HASH_ITERATIONS_DEFAULT, credential.getHashIterations());
        Assert.assertNotEquals("ABCD", credential.getValue());
        Assert.assertEquals(PASSWORD, credential.getType());
    }

    @Test
    public void createDuplicatedUser3() {
        createUser();
        UserRepresentation user = new UserRepresentation();
        user.setUsername("User1");
        Response response = realm.users().create(user);
        Assert.assertEquals(409, response.getStatus());
        response.close();
    }

    @Test
    public void createDuplicatedUser4() {
        createUser();
        UserRepresentation user = new UserRepresentation();
        user.setUsername("USER1");
        Response response = realm.users().create(user);
        Assert.assertEquals(409, response.getStatus());
        response.close();
    }

    @Test
    public void createDuplicatedUser5() {
        createUser();
        UserRepresentation user = new UserRepresentation();
        user.setUsername("user2");
        user.setEmail("User1@localhost");
        Response response = realm.users().create(user);
        Assert.assertEquals(409, response.getStatus());
        response.close();
    }

    @Test
    public void createDuplicatedUser6() {
        createUser();
        UserRepresentation user = new UserRepresentation();
        user.setUsername("user2");
        user.setEmail("user1@LOCALHOST");
        Response response = realm.users().create(user);
        Assert.assertEquals(409, response.getStatus());
        response.close();
    }

    @Test
    public void createDuplicatedUser7() {
        createUser("user1", "USer1@Localhost");
        UserRepresentation user = new UserRepresentation();
        user.setUsername("user2");
        user.setEmail("user1@localhost");
        Response response = realm.users().create(user);
        Assert.assertEquals(409, response.getStatus());
        response.close();
        assertAdminEvents.assertEmpty();
    }

    // KEYCLOAK-7015
    @Test
    public void createTwoUsersWithEmptyStringEmails() {
        createUser("user1", "");
        createUser("user2", "");
    }

    @Test
    public void createUserWithFederationLink() {
        // add a dummy federation provider
        ComponentRepresentation dummyFederationProvider = new ComponentRepresentation();
        dummyFederationProvider.setId(PROVIDER_NAME);
        dummyFederationProvider.setName(PROVIDER_NAME);
        dummyFederationProvider.setProviderId(PROVIDER_NAME);
        dummyFederationProvider.setProviderType(UserStorageProvider.class.getName());
        adminClient.realms().realm(AbstractAdminTest.REALM_NAME).components().add(dummyFederationProvider);
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.componentPath(PROVIDER_NAME), dummyFederationProvider, COMPONENT);
        UserRepresentation user = new UserRepresentation();
        user.setUsername("user1");
        user.setEmail("user1@localhost");
        user.setFederationLink(PROVIDER_NAME);
        String userId = createUser(user);
        // fetch user again and see federation link filled in
        UserRepresentation createdUser = realm.users().get(userId).toRepresentation();
        Assert.assertNotNull(createdUser);
        Assert.assertEquals(user.getFederationLink(), createdUser.getFederationLink());
    }

    @Test
    public void searchByEmail() {
        createUsers();
        List<UserRepresentation> users = realm.users().search(null, null, null, "user1@localhost", null, null);
        Assert.assertEquals(1, users.size());
        users = realm.users().search(null, null, null, "@localhost", null, null);
        Assert.assertEquals(9, users.size());
    }

    @Test
    public void searchByUsername() {
        createUsers();
        List<UserRepresentation> users = realm.users().search("username1", null, null, null, null, null);
        Assert.assertEquals(1, users.size());
        users = realm.users().search("user", null, null, null, null, null);
        Assert.assertEquals(9, users.size());
    }

    @Test
    public void searchById() {
        String expectedUserId = createUsers().get(0);
        List<UserRepresentation> users = realm.users().search(("id:" + expectedUserId), null, null);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals(expectedUserId, users.get(0).getId());
        users = realm.users().search((("id:   " + expectedUserId) + "     "), null, null);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals(expectedUserId, users.get(0).getId());
    }

    @Test
    public void search() {
        createUsers();
        List<UserRepresentation> users = realm.users().search("username1", null, null);
        Assert.assertEquals(1, users.size());
        users = realm.users().search("first1", null, null);
        Assert.assertEquals(1, users.size());
        users = realm.users().search("last", null, null);
        Assert.assertEquals(9, users.size());
    }

    @Test
    public void count() {
        createUsers();
        Integer count = realm.users().count();
        Assert.assertEquals(9, count.intValue());
    }

    @Test
    public void countUsersNotServiceAccount() {
        createUsers();
        Integer count = realm.users().count();
        Assert.assertEquals(9, count.intValue());
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId("test-client");
        client.setPublicClient(false);
        client.setSecret("secret");
        client.setServiceAccountsEnabled(true);
        client.setEnabled(true);
        client.setRedirectUris(Arrays.asList("http://url"));
        getAdminClient().realm(AbstractAdminTest.REALM_NAME).clients().create(client);
        // KEYCLOAK-5660, should not consider service accounts
        Assert.assertEquals(9, realm.users().count().intValue());
    }

    @Test
    public void delete() {
        String userId = createUser();
        Response response = realm.users().delete(userId);
        Assert.assertEquals(204, response.getStatus());
        response.close();
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.userResourcePath(userId), USER);
    }

    @Test
    public void deleteNonExistent() {
        Response response = realm.users().delete("does-not-exist");
        Assert.assertEquals(404, response.getStatus());
        response.close();
        assertAdminEvents.assertEmpty();
    }

    @Test
    public void searchPaginated() {
        createUsers();
        List<UserRepresentation> users = realm.users().search("username", 0, 1);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username1", users.get(0).getUsername());
        users = realm.users().search("username", 5, 2);
        Assert.assertEquals(2, users.size());
        Assert.assertEquals("username6", users.get(0).getUsername());
        Assert.assertEquals("username7", users.get(1).getUsername());
        users = realm.users().search("username", 7, 20);
        Assert.assertEquals(2, users.size());
        Assert.assertEquals("username8", users.get(0).getUsername());
        Assert.assertEquals("username9", users.get(1).getUsername());
        users = realm.users().search("username", 0, 20);
        Assert.assertEquals(9, users.size());
    }

    @Test
    public void getFederatedIdentities() {
        // Add sample identity provider
        addSampleIdentityProvider();
        // Add sample user
        String id = createUser();
        UserResource user = realm.users().get(id);
        Assert.assertEquals(0, user.getFederatedIdentity().size());
        // Add social link to the user
        FederatedIdentityRepresentation link = new FederatedIdentityRepresentation();
        link.setUserId("social-user-id");
        link.setUserName("social-username");
        Response response = user.addFederatedIdentity("social-provider-id", link);
        Assert.assertEquals(204, response.getStatus());
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.userFederatedIdentityLink(id, "social-provider-id"), link, USER);
        // Verify social link is here
        user = realm.users().get(id);
        List<FederatedIdentityRepresentation> federatedIdentities = user.getFederatedIdentity();
        Assert.assertEquals(1, federatedIdentities.size());
        link = federatedIdentities.get(0);
        Assert.assertEquals("social-provider-id", link.getIdentityProvider());
        Assert.assertEquals("social-user-id", link.getUserId());
        Assert.assertEquals("social-username", link.getUserName());
        // Remove social link now
        user.removeFederatedIdentity("social-provider-id");
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.userFederatedIdentityLink(id, "social-provider-id"), USER);
        Assert.assertEquals(0, user.getFederatedIdentity().size());
        removeSampleIdentityProvider();
    }

    @Test
    public void addRequiredAction() {
        String id = createUser();
        UserResource user = realm.users().get(id);
        Assert.assertTrue(user.toRepresentation().getRequiredActions().isEmpty());
        UserRepresentation userRep = user.toRepresentation();
        userRep.getRequiredActions().add("UPDATE_PASSWORD");
        updateUser(user, userRep);
        Assert.assertEquals(1, user.toRepresentation().getRequiredActions().size());
        Assert.assertEquals("UPDATE_PASSWORD", user.toRepresentation().getRequiredActions().get(0));
    }

    @Test
    public void removeRequiredAction() {
        String id = createUser();
        UserResource user = realm.users().get(id);
        Assert.assertTrue(user.toRepresentation().getRequiredActions().isEmpty());
        UserRepresentation userRep = user.toRepresentation();
        userRep.getRequiredActions().add("UPDATE_PASSWORD");
        updateUser(user, userRep);
        user = realm.users().get(id);
        userRep = user.toRepresentation();
        userRep.getRequiredActions().clear();
        updateUser(user, userRep);
        Assert.assertTrue(user.toRepresentation().getRequiredActions().isEmpty());
    }

    @Test
    public void attributes() {
        UserRepresentation user1 = new UserRepresentation();
        user1.setUsername("user1");
        user1.singleAttribute("attr1", "value1user1");
        user1.singleAttribute("attr2", "value2user1");
        String user1Id = createUser(user1);
        UserRepresentation user2 = new UserRepresentation();
        user2.setUsername("user2");
        user2.singleAttribute("attr1", "value1user2");
        List<String> vals = new ArrayList<>();
        vals.add("value2user2");
        vals.add("value2user2_2");
        user2.getAttributes().put("attr2", vals);
        String user2Id = createUser(user2);
        user1 = realm.users().get(user1Id).toRepresentation();
        Assert.assertEquals(2, user1.getAttributes().size());
        assertAttributeValue("value1user1", user1.getAttributes().get("attr1"));
        assertAttributeValue("value2user1", user1.getAttributes().get("attr2"));
        user2 = realm.users().get(user2Id).toRepresentation();
        Assert.assertEquals(2, user2.getAttributes().size());
        assertAttributeValue("value1user2", user2.getAttributes().get("attr1"));
        vals = user2.getAttributes().get("attr2");
        Assert.assertEquals(2, vals.size());
        Assert.assertTrue(((vals.contains("value2user2")) && (vals.contains("value2user2_2"))));
        user1.singleAttribute("attr1", "value3user1");
        user1.singleAttribute("attr3", "value4user1");
        updateUser(realm.users().get(user1Id), user1);
        user1 = realm.users().get(user1Id).toRepresentation();
        Assert.assertEquals(3, user1.getAttributes().size());
        assertAttributeValue("value3user1", user1.getAttributes().get("attr1"));
        assertAttributeValue("value2user1", user1.getAttributes().get("attr2"));
        assertAttributeValue("value4user1", user1.getAttributes().get("attr3"));
        user1.getAttributes().remove("attr1");
        updateUser(realm.users().get(user1Id), user1);
        user1 = realm.users().get(user1Id).toRepresentation();
        Assert.assertEquals(2, user1.getAttributes().size());
        assertAttributeValue("value2user1", user1.getAttributes().get("attr2"));
        assertAttributeValue("value4user1", user1.getAttributes().get("attr3"));
        user1.getAttributes().clear();
        updateUser(realm.users().get(user1Id), user1);
        user1 = realm.users().get(user1Id).toRepresentation();
        Assert.assertNull(user1.getAttributes());
    }

    @Test
    public void sendResetPasswordEmail() {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername("user1");
        String id = createUser(userRep);
        UserResource user = realm.users().get(id);
        List<String> actions = new LinkedList<>();
        try {
            user.executeActionsEmail(actions);
            Assert.fail("Expected failure");
        } catch (ClientErrorException e) {
            Assert.assertEquals(400, e.getResponse().getStatus());
            ErrorRepresentation error = e.getResponse().readEntity(ErrorRepresentation.class);
            Assert.assertEquals("User email missing", error.getErrorMessage());
        }
        try {
            userRep = user.toRepresentation();
            userRep.setEmail("user1@localhost");
            userRep.setEnabled(false);
            updateUser(user, userRep);
            user.executeActionsEmail(actions);
            Assert.fail("Expected failure");
        } catch (ClientErrorException e) {
            Assert.assertEquals(400, e.getResponse().getStatus());
            ErrorRepresentation error = e.getResponse().readEntity(ErrorRepresentation.class);
            Assert.assertEquals("User is disabled", error.getErrorMessage());
        }
        try {
            userRep.setEnabled(true);
            updateUser(user, userRep);
            user.executeActionsEmail("invalidClientId", "invalidUri", actions);
            Assert.fail("Expected failure");
        } catch (ClientErrorException e) {
            Assert.assertEquals(400, e.getResponse().getStatus());
            ErrorRepresentation error = e.getResponse().readEntity(ErrorRepresentation.class);
            Assert.assertEquals("invalidClientId not enabled", error.getErrorMessage());
        }
    }

    @Test
    public void sendResetPasswordEmailSuccess() throws IOException, MessagingException {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEnabled(true);
        userRep.setUsername("user1");
        userRep.setEmail("user1@test.com");
        String id = createUser(userRep);
        UserResource user = realm.users().get(id);
        List<String> actions = new LinkedList<>();
        actions.add(UPDATE_PASSWORD.name());
        user.executeActionsEmail(actions);
        assertAdminEvents.assertEvent(realmId, ACTION, ((AdminEventPaths.userResourcePath(id)) + "/execute-actions-email"), USER);
        Assert.assertEquals(1, greenMail.getReceivedMessages().length);
        MimeMessage message = greenMail.getReceivedMessages()[0];
        MailUtils.EmailBody body = MailUtils.getBody(message);
        Assert.assertTrue(body.getText().contains("Update Password"));
        Assert.assertTrue(body.getText().contains("your Admin-client-test account"));
        Assert.assertTrue(body.getText().contains("This link will expire within 12 hours"));
        Assert.assertTrue(body.getHtml().contains("Update Password"));
        Assert.assertTrue(body.getHtml().contains("your Admin-client-test account"));
        Assert.assertTrue(body.getHtml().contains("This link will expire within 12 hours"));
        String link = MailUtils.getPasswordResetEmailLink(body);
        driver.navigate().to(link);
        proceedPage.assertCurrent();
        Assert.assertThat(proceedPage.getInfo(), Matchers.containsString("Update Password"));
        proceedPage.clickProceedLink();
        passwordUpdatePage.assertCurrent();
        passwordUpdatePage.changePassword("new-pass", "new-pass");
        Assert.assertEquals("Your account has been updated.", PageUtils.getPageTitle(driver));
        driver.navigate().to(link);
        Assert.assertEquals("We're sorry...", PageUtils.getPageTitle(driver));
    }

    @Test
    public void sendResetPasswordEmailSuccessTwoLinks() throws IOException, MessagingException {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEnabled(true);
        userRep.setUsername("user1");
        userRep.setEmail("user1@test.com");
        String id = createUser(userRep);
        UserResource user = realm.users().get(id);
        List<String> actions = new LinkedList<>();
        actions.add(UPDATE_PASSWORD.name());
        user.executeActionsEmail(actions);
        user.executeActionsEmail(actions);
        assertAdminEvents.assertEvent(realmId, ACTION, ((AdminEventPaths.userResourcePath(id)) + "/execute-actions-email"), USER);
        assertAdminEvents.assertEvent(realmId, ACTION, ((AdminEventPaths.userResourcePath(id)) + "/execute-actions-email"), USER);
        Assert.assertEquals(2, greenMail.getReceivedMessages().length);
        int i = 1;
        for (MimeMessage message : greenMail.getReceivedMessages()) {
            String link = MailUtils.getPasswordResetEmailLink(message);
            driver.navigate().to(link);
            proceedPage.assertCurrent();
            Assert.assertThat(proceedPage.getInfo(), Matchers.containsString("Update Password"));
            proceedPage.clickProceedLink();
            passwordUpdatePage.assertCurrent();
            passwordUpdatePage.changePassword(("new-pass" + i), ("new-pass" + i));
            i++;
            Assert.assertEquals("Your account has been updated.", PageUtils.getPageTitle(driver));
        }
        for (MimeMessage message : greenMail.getReceivedMessages()) {
            String link = MailUtils.getPasswordResetEmailLink(message);
            driver.navigate().to(link);
            errorPage.assertCurrent();
        }
    }

    @Test
    public void sendResetPasswordEmailSuccessTwoLinksReverse() throws IOException, MessagingException {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEnabled(true);
        userRep.setUsername("user1");
        userRep.setEmail("user1@test.com");
        String id = createUser(userRep);
        UserResource user = realm.users().get(id);
        List<String> actions = new LinkedList<>();
        actions.add(UPDATE_PASSWORD.name());
        user.executeActionsEmail(actions);
        user.executeActionsEmail(actions);
        assertAdminEvents.assertEvent(realmId, ACTION, ((AdminEventPaths.userResourcePath(id)) + "/execute-actions-email"), USER);
        assertAdminEvents.assertEvent(realmId, ACTION, ((AdminEventPaths.userResourcePath(id)) + "/execute-actions-email"), USER);
        Assert.assertEquals(2, greenMail.getReceivedMessages().length);
        int i = 1;
        for (int j = (greenMail.getReceivedMessages().length) - 1; j >= 0; j--) {
            MimeMessage message = greenMail.getReceivedMessages()[j];
            String link = MailUtils.getPasswordResetEmailLink(message);
            driver.navigate().to(link);
            proceedPage.assertCurrent();
            Assert.assertThat(proceedPage.getInfo(), Matchers.containsString("Update Password"));
            proceedPage.clickProceedLink();
            passwordUpdatePage.assertCurrent();
            passwordUpdatePage.changePassword(("new-pass" + i), ("new-pass" + i));
            i++;
            Assert.assertEquals("Your account has been updated.", PageUtils.getPageTitle(driver));
        }
        for (MimeMessage message : greenMail.getReceivedMessages()) {
            String link = MailUtils.getPasswordResetEmailLink(message);
            driver.navigate().to(link);
            errorPage.assertCurrent();
        }
    }

    @Test
    public void sendResetPasswordEmailSuccessLinkOpenDoesNotExpireWhenOpenedOnly() throws IOException, MessagingException {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEnabled(true);
        userRep.setUsername("user1");
        userRep.setEmail("user1@test.com");
        String id = createUser(userRep);
        UserResource user = realm.users().get(id);
        List<String> actions = new LinkedList<>();
        actions.add(UPDATE_PASSWORD.name());
        user.executeActionsEmail(actions);
        assertAdminEvents.assertEvent(realmId, ACTION, ((AdminEventPaths.userResourcePath(id)) + "/execute-actions-email"), USER);
        Assert.assertEquals(1, greenMail.getReceivedMessages().length);
        MimeMessage message = greenMail.getReceivedMessages()[0];
        String link = MailUtils.getPasswordResetEmailLink(message);
        driver.navigate().to(link);
        proceedPage.assertCurrent();
        Assert.assertThat(proceedPage.getInfo(), Matchers.containsString("Update Password"));
        proceedPage.clickProceedLink();
        passwordUpdatePage.assertCurrent();
        driver.manage().deleteAllCookies();
        driver.navigate().to("about:blank");
        driver.navigate().to(link);
        proceedPage.assertCurrent();
        Assert.assertThat(proceedPage.getInfo(), Matchers.containsString("Update Password"));
        proceedPage.clickProceedLink();
        passwordUpdatePage.assertCurrent();
        passwordUpdatePage.changePassword("new-pass", "new-pass");
        Assert.assertEquals("Your account has been updated.", PageUtils.getPageTitle(driver));
    }

    @Test
    public void sendResetPasswordEmailSuccessTokenShortLifespan() throws IOException, MessagingException {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEnabled(true);
        userRep.setUsername("user1");
        userRep.setEmail("user1@test.com");
        String id = createUser(userRep);
        final AtomicInteger originalValue = new AtomicInteger();
        RealmRepresentation realmRep = realm.toRepresentation();
        originalValue.set(realmRep.getActionTokenGeneratedByAdminLifespan());
        realmRep.setActionTokenGeneratedByAdminLifespan(60);
        realm.update(realmRep);
        try {
            UserResource user = realm.users().get(id);
            List<String> actions = new LinkedList<>();
            actions.add(UPDATE_PASSWORD.name());
            user.executeActionsEmail(actions);
            Assert.assertEquals(1, greenMail.getReceivedMessages().length);
            MimeMessage message = greenMail.getReceivedMessages()[0];
            String link = MailUtils.getPasswordResetEmailLink(message);
            setTimeOffset(70);
            driver.navigate().to(link);
            errorPage.assertCurrent();
            Assert.assertEquals("Action expired.", errorPage.getError());
        } finally {
            setTimeOffset(0);
            realmRep.setActionTokenGeneratedByAdminLifespan(originalValue.get());
            realm.update(realmRep);
        }
    }

    @Test
    public void sendResetPasswordEmailSuccessWithRecycledAuthSession() throws IOException, MessagingException {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEnabled(true);
        userRep.setUsername("user1");
        userRep.setEmail("user1@test.com");
        String id = createUser(userRep);
        UserResource user = realm.users().get(id);
        List<String> actions = new LinkedList<>();
        actions.add(UPDATE_PASSWORD.name());
        // The following block creates a client and requests updating password with redirect to this client.
        // After clicking the link (starting a fresh auth session with client), the user goes away and sends the email
        // with password reset again - now without the client - and attempts to complete the password reset.
        {
            ClientRepresentation client = new ClientRepresentation();
            client.setClientId("myclient2");
            client.setRedirectUris(new LinkedList());
            client.getRedirectUris().add("http://myclient.com/*");
            client.setName("myclient2");
            client.setEnabled(true);
            Response response = realm.clients().create(client);
            String createdId = ApiUtil.getCreatedId(response);
            assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.clientResourcePath(createdId), client, CLIENT);
            user.executeActionsEmail("myclient2", "http://myclient.com/home.html", actions);
            assertAdminEvents.assertEvent(realmId, ACTION, ((AdminEventPaths.userResourcePath(id)) + "/execute-actions-email"), USER);
            Assert.assertEquals(1, greenMail.getReceivedMessages().length);
            MimeMessage message = greenMail.getReceivedMessages()[0];
            String link = MailUtils.getPasswordResetEmailLink(message);
            driver.navigate().to(link);
        }
        user.executeActionsEmail(actions);
        assertAdminEvents.assertEvent(realmId, ACTION, ((AdminEventPaths.userResourcePath(id)) + "/execute-actions-email"), USER);
        Assert.assertEquals(2, greenMail.getReceivedMessages().length);
        MimeMessage message = greenMail.getReceivedMessages()[((greenMail.getReceivedMessages().length) - 1)];
        String link = MailUtils.getPasswordResetEmailLink(message);
        driver.navigate().to(link);
        proceedPage.assertCurrent();
        Assert.assertThat(proceedPage.getInfo(), Matchers.containsString("Update Password"));
        proceedPage.clickProceedLink();
        passwordUpdatePage.assertCurrent();
        passwordUpdatePage.changePassword("new-pass", "new-pass");
        Assert.assertEquals("Your account has been updated.", PageUtils.getPageTitle(driver));
        driver.navigate().to(link);
        Assert.assertEquals("We're sorry...", PageUtils.getPageTitle(driver));
    }

    @Test
    public void sendResetPasswordEmailWithRedirect() throws IOException, MessagingException {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEnabled(true);
        userRep.setUsername("user1");
        userRep.setEmail("user1@test.com");
        String id = createUser(userRep);
        UserResource user = realm.users().get(id);
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId("myclient");
        client.setRedirectUris(new LinkedList());
        client.getRedirectUris().add("http://myclient.com/*");
        client.setName("myclient");
        client.setEnabled(true);
        Response response = realm.clients().create(client);
        String createdId = ApiUtil.getCreatedId(response);
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.clientResourcePath(createdId), client, CLIENT);
        List<String> actions = new LinkedList<>();
        actions.add(UPDATE_PASSWORD.name());
        try {
            // test that an invalid redirect uri is rejected.
            user.executeActionsEmail("myclient", "http://unregistered-uri.com/", actions);
            Assert.fail("Expected failure");
        } catch (ClientErrorException e) {
            Assert.assertEquals(400, e.getResponse().getStatus());
            ErrorRepresentation error = e.getResponse().readEntity(ErrorRepresentation.class);
            Assert.assertEquals("Invalid redirect uri.", error.getErrorMessage());
        }
        user.executeActionsEmail("myclient", "http://myclient.com/home.html", actions);
        assertAdminEvents.assertEvent(realmId, ACTION, ((AdminEventPaths.userResourcePath(id)) + "/execute-actions-email"), USER);
        Assert.assertEquals(1, greenMail.getReceivedMessages().length);
        MimeMessage message = greenMail.getReceivedMessages()[0];
        String link = MailUtils.getPasswordResetEmailLink(message);
        driver.navigate().to(link);
        proceedPage.assertCurrent();
        Assert.assertThat(proceedPage.getInfo(), Matchers.containsString("Update Password"));
        proceedPage.clickProceedLink();
        passwordUpdatePage.assertCurrent();
        passwordUpdatePage.changePassword("new-pass", "new-pass");
        Assert.assertEquals("Your account has been updated.", driver.findElement(By.id("kc-page-title")).getText());
        String pageSource = driver.getPageSource();
        // check to make sure the back link is set.
        Assert.assertTrue(pageSource.contains("http://myclient.com/home.html"));
        driver.navigate().to(link);
        Assert.assertEquals("We're sorry...", PageUtils.getPageTitle(driver));
    }

    @Test
    public void sendVerifyEmail() throws IOException, MessagingException {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername("user1");
        String id = createUser(userRep);
        UserResource user = realm.users().get(id);
        try {
            user.sendVerifyEmail();
            Assert.fail("Expected failure");
        } catch (ClientErrorException e) {
            Assert.assertEquals(400, e.getResponse().getStatus());
            ErrorRepresentation error = e.getResponse().readEntity(ErrorRepresentation.class);
            Assert.assertEquals("User email missing", error.getErrorMessage());
        }
        try {
            userRep = user.toRepresentation();
            userRep.setEmail("user1@localhost");
            userRep.setEnabled(false);
            updateUser(user, userRep);
            user.sendVerifyEmail();
            Assert.fail("Expected failure");
        } catch (ClientErrorException e) {
            Assert.assertEquals(400, e.getResponse().getStatus());
            ErrorRepresentation error = e.getResponse().readEntity(ErrorRepresentation.class);
            Assert.assertEquals("User is disabled", error.getErrorMessage());
        }
        try {
            userRep.setEnabled(true);
            updateUser(user, userRep);
            user.sendVerifyEmail("invalidClientId");
            Assert.fail("Expected failure");
        } catch (ClientErrorException e) {
            Assert.assertEquals(400, e.getResponse().getStatus());
            ErrorRepresentation error = e.getResponse().readEntity(ErrorRepresentation.class);
            Assert.assertEquals("invalidClientId not enabled", error.getErrorMessage());
        }
        user.sendVerifyEmail();
        assertAdminEvents.assertEvent(realmId, ACTION, ((AdminEventPaths.userResourcePath(id)) + "/send-verify-email"), USER);
        Assert.assertEquals(1, greenMail.getReceivedMessages().length);
        String link = MailUtils.getPasswordResetEmailLink(greenMail.getReceivedMessages()[0]);
        driver.navigate().to(link);
        proceedPage.assertCurrent();
        Assert.assertThat(proceedPage.getInfo(), Matchers.containsString("Verify Email"));
        proceedPage.clickProceedLink();
        Assert.assertEquals("Your account has been updated.", infoPage.getInfo());
        driver.navigate().to("about:blank");
        driver.navigate().to(link);// It should be possible to use the same action token multiple times

        proceedPage.assertCurrent();
        Assert.assertThat(proceedPage.getInfo(), Matchers.containsString("Verify Email"));
        proceedPage.clickProceedLink();
        Assert.assertEquals("Your account has been updated.", infoPage.getInfo());
    }

    @Test
    public void updateUserWithNewUsername() {
        switchEditUsernameAllowedOn(true);
        String id = createUser();
        UserResource user = realm.users().get(id);
        UserRepresentation userRep = user.toRepresentation();
        userRep.setUsername("user11");
        updateUser(user, userRep);
        userRep = realm.users().get(id).toRepresentation();
        Assert.assertEquals("user11", userRep.getUsername());
        // Revert
        switchEditUsernameAllowedOn(false);
    }

    @Test
    public void updateUserWithoutUsername() {
        switchEditUsernameAllowedOn(true);
        String id = createUser();
        UserResource user = realm.users().get(id);
        UserRepresentation rep = new UserRepresentation();
        rep.setFirstName("Firstname");
        user.update(rep);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.userResourcePath(id), rep, USER);
        rep = new UserRepresentation();
        rep.setLastName("Lastname");
        user.update(rep);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.userResourcePath(id), rep, USER);
        rep = realm.users().get(id).toRepresentation();
        Assert.assertEquals("user1", rep.getUsername());
        Assert.assertEquals("user1@localhost", rep.getEmail());
        Assert.assertEquals("Firstname", rep.getFirstName());
        Assert.assertEquals("Lastname", rep.getLastName());
        // Revert
        switchEditUsernameAllowedOn(false);
    }

    @Test
    public void updateUserWithNewUsernameNotPossible() {
        String id = createUser();
        UserResource user = realm.users().get(id);
        UserRepresentation userRep = user.toRepresentation();
        userRep.setUsername("user11");
        updateUser(user, userRep);
        userRep = realm.users().get(id).toRepresentation();
        Assert.assertEquals("user1", userRep.getUsername());
    }

    @Test
    public void updateUserWithNewUsernameAccessingViaOldUsername() {
        switchEditUsernameAllowedOn(true);
        createUser();
        try {
            UserResource user = realm.users().get("user1");
            UserRepresentation userRep = user.toRepresentation();
            userRep.setUsername("user1");
            updateUser(user, userRep);
            realm.users().get("user11").toRepresentation();
            Assert.fail("Expected failure");
        } catch (ClientErrorException e) {
            Assert.assertEquals(404, e.getResponse().getStatus());
        } finally {
            switchEditUsernameAllowedOn(false);
        }
    }

    @Test
    public void updateUserWithExistingUsername() {
        switchEditUsernameAllowedOn(true);
        enableBruteForce(true);
        createUser();
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername("user2");
        String createdId = createUser(userRep);
        try {
            UserResource user = realm.users().get(createdId);
            userRep = user.toRepresentation();
            userRep.setUsername("user1");
            user.update(userRep);
            Assert.fail("Expected failure");
        } catch (ClientErrorException e) {
            Assert.assertEquals(409, e.getResponse().getStatus());
            // TODO adminEvents: Event queue should be empty, but it's not because of bug in UsersResource.updateUser, which sends event earlier than transaction commit.
            // assertAdminEvents.assertEmpty();
            assertAdminEvents.poll();
        } finally {
            enableBruteForce(false);
            switchEditUsernameAllowedOn(false);
        }
    }

    @Test
    public void updateUserWithRawCredentials() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("user_rawpw");
        user.setEmail("email.raw@localhost");
        CredentialRepresentation rawPassword = new CredentialRepresentation();
        rawPassword.setValue("ABCD");
        rawPassword.setType(PASSWORD);
        user.setCredentials(Arrays.asList(rawPassword));
        String id = createUser(user);
        CredentialModel credential = fetchCredentials("user_rawpw");
        Assert.assertNotNull("Expecting credential", credential);
        Assert.assertEquals(HASH_ALGORITHM_DEFAULT, credential.getAlgorithm());
        Assert.assertEquals(HASH_ITERATIONS_DEFAULT, credential.getHashIterations());
        Assert.assertNotEquals("ABCD", credential.getValue());
        Assert.assertEquals(PASSWORD, credential.getType());
        UserResource userResource = realm.users().get(id);
        UserRepresentation userRep = userResource.toRepresentation();
        CredentialRepresentation rawPasswordForUpdate = new CredentialRepresentation();
        rawPasswordForUpdate.setValue("EFGH");
        rawPasswordForUpdate.setType(PASSWORD);
        userRep.setCredentials(Arrays.asList(rawPasswordForUpdate));
        updateUser(userResource, userRep);
        CredentialModel updatedCredential = fetchCredentials("user_rawpw");
        Assert.assertNotNull("Expecting credential", updatedCredential);
        Assert.assertEquals(HASH_ALGORITHM_DEFAULT, updatedCredential.getAlgorithm());
        Assert.assertEquals(HASH_ITERATIONS_DEFAULT, updatedCredential.getHashIterations());
        Assert.assertNotEquals("EFGH", updatedCredential.getValue());
        Assert.assertEquals(PASSWORD, updatedCredential.getType());
    }

    @Test
    public void resetUserPassword() {
        String userId = createUser("user1", "user1@localhost");
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(PASSWORD);
        cred.setValue("password");
        cred.setTemporary(false);
        realm.users().get(userId).resetPassword(cred);
        assertAdminEvents.assertEvent(realmId, ACTION, AdminEventPaths.userResetPasswordPath(userId), USER);
        String accountUrl = RealmsResource.accountUrl(UriBuilder.fromUri(getAuthServerRoot())).build(AbstractAdminTest.REALM_NAME).toString();
        driver.navigate().to(accountUrl);
        Assert.assertEquals("Log In", PageUtils.getPageTitle(driver));
        loginPage.login("user1", "password");
        Assert.assertTrue(driver.getTitle().contains("Account Management"));
    }

    @Test
    public void resetUserInvalidPassword() {
        String userId = createUser("user1", "user1@localhost");
        try {
            CredentialRepresentation cred = new CredentialRepresentation();
            cred.setType(PASSWORD);
            cred.setValue(" ");
            cred.setTemporary(false);
            realm.users().get(userId).resetPassword(cred);
            Assert.fail("Expected failure");
        } catch (ClientErrorException e) {
            Assert.assertEquals(400, e.getResponse().getStatus());
            e.getResponse().close();
            assertAdminEvents.assertEmpty();
        }
    }

    @Test
    public void testDefaultRequiredActionAdded() {
        // Add UPDATE_PASSWORD as default required action
        RequiredActionProviderRepresentation updatePasswordReqAction = realm.flows().getRequiredAction(UPDATE_PASSWORD.toString());
        updatePasswordReqAction.setDefaultAction(true);
        realm.flows().updateRequiredAction(UPDATE_PASSWORD.toString(), updatePasswordReqAction);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.authRequiredActionPath(UPDATE_PASSWORD.toString()), updatePasswordReqAction, REQUIRED_ACTION);
        // Create user
        String userId = createUser("user1", "user1@localhost");
        UserRepresentation userRep = realm.users().get(userId).toRepresentation();
        Assert.assertEquals(1, userRep.getRequiredActions().size());
        Assert.assertEquals(UPDATE_PASSWORD.toString(), userRep.getRequiredActions().get(0));
        // Remove UPDATE_PASSWORD default action
        updatePasswordReqAction = realm.flows().getRequiredAction(UPDATE_PASSWORD.toString());
        updatePasswordReqAction.setDefaultAction(true);
        realm.flows().updateRequiredAction(UPDATE_PASSWORD.toString(), updatePasswordReqAction);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.authRequiredActionPath(UPDATE_PASSWORD.toString()), updatePasswordReqAction, REQUIRED_ACTION);
    }

    @Test
    public void roleMappings() {
        RealmResource realm = adminClient.realms().realm("test");
        // Enable events
        RealmRepresentation realmRep = RealmBuilder.edit(realm.toRepresentation()).testEventListener().build();
        realm.update(realmRep);
        realm.roles().create(RoleBuilder.create().name("realm-role").build());
        realm.roles().create(RoleBuilder.create().name("realm-composite").build());
        realm.roles().create(RoleBuilder.create().name("realm-child").build());
        realm.roles().get("realm-composite").addComposites(Collections.singletonList(realm.roles().get("realm-child").toRepresentation()));
        Response response = realm.clients().create(ClientBuilder.create().clientId("myclient").build());
        String clientUuid = ApiUtil.getCreatedId(response);
        response.close();
        realm.clients().get(clientUuid).roles().create(RoleBuilder.create().name("client-role").build());
        realm.clients().get(clientUuid).roles().create(RoleBuilder.create().name("client-role2").build());
        realm.clients().get(clientUuid).roles().create(RoleBuilder.create().name("client-composite").build());
        realm.clients().get(clientUuid).roles().create(RoleBuilder.create().name("client-child").build());
        realm.clients().get(clientUuid).roles().get("client-composite").addComposites(Collections.singletonList(realm.clients().get(clientUuid).roles().get("client-child").toRepresentation()));
        response = realm.users().create(UserBuilder.create().username("myuser").build());
        String userId = ApiUtil.getCreatedId(response);
        response.close();
        // Admin events for creating role, client or user tested already in other places
        assertAdminEvents.clear();
        RoleMappingResource roles = realm.users().get(userId).roles();
        assertNames(roles.realmLevel().listAll(), "user", "offline_access", Constants.AUTHZ_UMA_AUTHORIZATION);
        // Add realm roles
        List<RoleRepresentation> l = new LinkedList<>();
        l.add(realm.roles().get("realm-role").toRepresentation());
        l.add(realm.roles().get("realm-composite").toRepresentation());
        roles.realmLevel().add(l);
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.userRealmRoleMappingsPath(userId), l, REALM_ROLE_MAPPING);
        // Add client roles
        List<RoleRepresentation> list = Collections.singletonList(realm.clients().get(clientUuid).roles().get("client-role").toRepresentation());
        roles.clientLevel(clientUuid).add(list);
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.userClientRoleMappingsPath(userId, clientUuid), list, CLIENT_ROLE_MAPPING);
        list = Collections.singletonList(realm.clients().get(clientUuid).roles().get("client-composite").toRepresentation());
        roles.clientLevel(clientUuid).add(list);
        assertAdminEvents.assertEvent("test", CREATE, AdminEventPaths.userClientRoleMappingsPath(userId, clientUuid), CLIENT_ROLE_MAPPING);
        // List realm roles
        assertNames(roles.realmLevel().listAll(), "realm-role", "realm-composite", "user", "offline_access", Constants.AUTHZ_UMA_AUTHORIZATION);
        assertNames(roles.realmLevel().listAvailable(), "admin", "customer-user-premium", "realm-composite-role", "sample-realm-role", "attribute-role");
        assertNames(roles.realmLevel().listEffective(), "realm-role", "realm-composite", "realm-child", "user", "offline_access", Constants.AUTHZ_UMA_AUTHORIZATION);
        // List client roles
        assertNames(roles.clientLevel(clientUuid).listAll(), "client-role", "client-composite");
        assertNames(roles.clientLevel(clientUuid).listAvailable(), "client-role2");
        assertNames(roles.clientLevel(clientUuid).listEffective(), "client-role", "client-composite", "client-child");
        // Get mapping representation
        MappingsRepresentation all = roles.getAll();
        assertNames(all.getRealmMappings(), "realm-role", "realm-composite", "user", "offline_access", Constants.AUTHZ_UMA_AUTHORIZATION);
        Assert.assertEquals(2, all.getClientMappings().size());
        assertNames(all.getClientMappings().get("myclient").getMappings(), "client-role", "client-composite");
        assertNames(all.getClientMappings().get("account").getMappings(), "manage-account", "view-profile");
        // Remove realm role
        RoleRepresentation realmRoleRep = realm.roles().get("realm-role").toRepresentation();
        roles.realmLevel().remove(Collections.singletonList(realmRoleRep));
        assertAdminEvents.assertEvent("test", DELETE, AdminEventPaths.userRealmRoleMappingsPath(userId), Collections.singletonList(realmRoleRep), REALM_ROLE_MAPPING);
        assertNames(roles.realmLevel().listAll(), "realm-composite", "user", "offline_access", Constants.AUTHZ_UMA_AUTHORIZATION);
        // Remove client role
        RoleRepresentation clientRoleRep = realm.clients().get(clientUuid).roles().get("client-role").toRepresentation();
        roles.clientLevel(clientUuid).remove(Collections.singletonList(clientRoleRep));
        assertAdminEvents.assertEvent("test", DELETE, AdminEventPaths.userClientRoleMappingsPath(userId, clientUuid), Collections.singletonList(clientRoleRep), CLIENT_ROLE_MAPPING);
        assertNames(roles.clientLevel(clientUuid).listAll(), "client-composite");
    }

    @Test
    public void defaultMaxResults() {
        UsersResource users = adminClient.realms().realm("test").users();
        for (int i = 0; i < 110; i++) {
            users.create(UserBuilder.create().username(("test-" + i)).addAttribute("aName", "aValue").build()).close();
        }
        List<UserRepresentation> result = users.search("test", null, null);
        Assert.assertEquals(100, result.size());
        for (UserRepresentation user : result) {
            Assert.assertThat(user.getAttributes(), Matchers.notNullValue());
            Assert.assertThat(user.getAttributes().keySet(), Matchers.hasSize(1));
            Assert.assertThat(user.getAttributes(), Matchers.hasEntry(Matchers.is("aName"), Matchers.contains("aValue")));
        }
        Assert.assertEquals(105, users.search("test", 0, 105).size());
        Assert.assertEquals(111, users.search("test", 0, 1000).size());
    }

    @Test
    public void defaultMaxResultsBrief() {
        UsersResource users = adminClient.realms().realm("test").users();
        for (int i = 0; i < 110; i++) {
            users.create(UserBuilder.create().username(("test-" + i)).addAttribute("aName", "aValue").build()).close();
        }
        List<UserRepresentation> result = users.search("test", null, null, true);
        Assert.assertEquals(100, result.size());
        for (UserRepresentation user : result) {
            Assert.assertThat(user.getAttributes(), Matchers.nullValue());
        }
    }
}

