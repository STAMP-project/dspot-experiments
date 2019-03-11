/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.crossdc;


import DC.FIRST;
import DC.SECOND;
import UserModel.RequiredAction.UPDATE_PASSWORD;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.common.util.Retry;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.arquillian.CrossDCTestEnricher;
import org.keycloak.testsuite.arquillian.annotation.InitialDcState;
import org.keycloak.testsuite.page.LoginPasswordUpdatePage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.PageUtils;
import org.keycloak.testsuite.pages.ProceedPage;
import org.keycloak.testsuite.util.GreenMailRule;
import org.keycloak.testsuite.util.MailUtils;

import static ServerSetup.FIRST_NODE_IN_FIRST_DC;


/**
 *
 *
 * @author hmlnarik
 */
public class ActionTokenCrossDCTest extends AbstractAdminCrossDCTest {
    @Rule
    public GreenMailRule greenMail = new GreenMailRule();

    @Page
    protected LoginPasswordUpdatePage passwordUpdatePage;

    @Page
    protected ProceedPage proceedPage;

    @Page
    protected ErrorPage errorPage;

    @Test
    @InitialDcState(authServers = FIRST_NODE_IN_FIRST_DC)
    public void sendResetPasswordEmailAfterNewNodeAdded() throws IOException, MessagingException {
        log.debug("--DC: START sendResetPasswordEmailAfterNewNodeAdded");
        disableDcOnLoadBalancer(SECOND);
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEnabled(true);
        userRep.setUsername("user1");
        userRep.setEmail("user1@test.com");
        String id = createUser(userRep);
        UserResource user = realm.users().get(id);
        List<String> actions = new LinkedList<>();
        actions.add(UPDATE_PASSWORD.name());
        user.executeActionsEmail(actions);
        Assert.assertEquals(1, greenMail.getReceivedMessages().length);
        MimeMessage message = greenMail.getReceivedMessages()[0];
        String link = MailUtils.getPasswordResetEmailLink(message);
        driver.navigate().to(link);
        proceedPage.assertCurrent();
        proceedPage.clickProceedLink();
        passwordUpdatePage.assertCurrent();
        passwordUpdatePage.changePassword("new-pass", "new-pass");
        Assert.assertEquals("Your account has been updated.", PageUtils.getPageTitle(driver));
        disableDcOnLoadBalancer(FIRST);
        CrossDCTestEnricher.startAuthServerBackendNode(SECOND, 1);
        CrossDCTestEnricher.stopAuthServerBackendNode(FIRST, 0);
        enableLoadBalancerNode(SECOND, 1);
        Retry.execute(() -> {
            driver.navigate().to(link);
            errorPage.assertCurrent();
        }, 3, 400);
        log.debug("--DC: END sendResetPasswordEmailAfterNewNodeAdded");
    }
}

