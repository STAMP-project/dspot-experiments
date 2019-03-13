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


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.federation.PassThroughFederatedUserStorageProvider;
import org.keycloak.testsuite.pages.AccountFederatedIdentityPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.UpdateAccountInformationPage;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AccountLinkTest extends AbstractKeycloakTest {
    public static final String CHILD_IDP = "child";

    public static final String PARENT_IDP = "parent-idp";

    public static final String PARENT_USERNAME = "parent";

    @Page
    protected AccountFederatedIdentityPage accountFederatedIdentityPage;

    @Page
    protected UpdateAccountInformationPage profilePage;

    @Page
    protected LoginPage loginPage;

    @Test
    public void testAccountLink() {
        String childUsername = "child";
        String childPassword = "password";
        String childIdp = AccountLinkTest.CHILD_IDP;
        testAccountLink(childUsername, childPassword, childIdp);
    }

    @Test
    public void testAccountLinkWithUserStorageProvider() {
        String childUsername = PassThroughFederatedUserStorageProvider.PASSTHROUGH_USERNAME;
        String childPassword = PassThroughFederatedUserStorageProvider.INITIAL_PASSWORD;
        String childIdp = AccountLinkTest.CHILD_IDP;
        testAccountLink(childUsername, childPassword, childIdp);
    }
}

