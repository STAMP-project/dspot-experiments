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
package org.keycloak.testsuite.account;


import AccountFederatedIdentityPage.FederatedIdentity;
import java.util.List;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.IdentityProviderResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.broker.AbstractBaseBrokerTest;
import org.keycloak.testsuite.broker.KcOidcBrokerConfiguration;
import org.keycloak.testsuite.pages.AccountFederatedIdentityPage;
import org.keycloak.testsuite.pages.LoginPage;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class AccountBrokerTest extends AbstractBaseBrokerTest {
    @Page
    protected LoginPage loginPage;

    @Page
    protected AccountFederatedIdentityPage identityPage;

    @Test
    public void add() {
        identityPage.realm(KcOidcBrokerConfiguration.INSTANCE.consumerRealmName());
        identityPage.open();
        loginPage.login("accountbrokertest", "password");
        Assert.assertTrue(identityPage.isCurrent());
        List<AccountFederatedIdentityPage.FederatedIdentity> identities = identityPage.getIdentities();
        Assert.assertEquals(1, identities.size());
        Assert.assertEquals("kc-oidc-idp", identities.get(0).getProvider());
        Assert.assertEquals("", identities.get(0).getSubject());
        Assert.assertEquals("add-link-kc-oidc-idp", identities.get(0).getAction().getAttribute("id"));
        identities.get(0).getAction().click();
        loginPage.login(bc.getUserLogin(), bc.getUserPassword());
        Assert.assertTrue(identityPage.isCurrent());
        identities = identityPage.getIdentities();
        Assert.assertEquals(1, identities.size());
        Assert.assertEquals("kc-oidc-idp", identities.get(0).getProvider());
        Assert.assertEquals("user@localhost.com", identities.get(0).getSubject());
        Assert.assertEquals("remove-link-kc-oidc-idp", identities.get(0).getAction().getAttribute("id"));
        identities.get(0).getAction().click();
        Assert.assertTrue(identityPage.isCurrent());
        identities = identityPage.getIdentities();
        Assert.assertEquals("kc-oidc-idp", identities.get(0).getProvider());
        Assert.assertEquals("", identities.get(0).getSubject());
        Assert.assertEquals("add-link-kc-oidc-idp", identities.get(0).getAction().getAttribute("id"));
    }

    @Test
    public void displayEnabledIdentityProviders() {
        identityPage.realm(KcOidcBrokerConfiguration.INSTANCE.consumerRealmName());
        identityPage.open();
        loginPage.login("accountbrokertest", "password");
        Assert.assertTrue(identityPage.isCurrent());
        List<AccountFederatedIdentityPage.FederatedIdentity> identities = identityPage.getIdentities();
        Assert.assertEquals(1, identities.size());
        // Disable the identity provider
        RealmResource realm = adminClient.realm(bc.consumerRealmName());
        IdentityProviderResource providerResource = realm.identityProviders().get(bc.getIDPAlias());
        IdentityProviderRepresentation provider = providerResource.toRepresentation();
        provider.setEnabled(false);
        providerResource.update(provider);
        // Reload federated identities page
        identityPage.open();
        Assert.assertTrue(identityPage.isCurrent());
        identities = identityPage.getIdentities();
        Assert.assertEquals(0, identities.size());
    }
}

