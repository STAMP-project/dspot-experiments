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
package org.keycloak.testsuite.adapter.example.fuse;


import org.hamcrest.Matchers;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.adapter.AbstractExampleAdapterTest;
import org.keycloak.testsuite.adapter.page.fuse.AdminInterface;
import org.keycloak.testsuite.adapter.page.fuse.CustomerListing;
import org.keycloak.testsuite.adapter.page.fuse.CustomerPortalFuseExample;
import org.keycloak.testsuite.adapter.page.fuse.ProductPortalFuseExample;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;
import org.keycloak.testsuite.auth.page.account.Account;
import org.keycloak.testsuite.util.URLAssert;
import org.keycloak.testsuite.util.WaitUtils;


/**
 *
 *
 * @author tkyjovsk
 */
@AppServerContainer(ContainerConstants.APP_SERVER_FUSE63)
@AppServerContainer(ContainerConstants.APP_SERVER_FUSE7X)
public class FuseExampleAdapterTest extends AbstractExampleAdapterTest {
    @Page
    protected CustomerPortalFuseExample customerPortal;

    @Page
    protected CustomerListing customerListing;

    @Page
    protected AdminInterface adminInterface;

    @Page
    protected ProductPortalFuseExample productPortal;

    @Page
    protected Account testRealmAccount;

    // no Arquillian deployments - examples already installed by maven
    @Test
    public void testCustomerListingAndAccountManagement() {
        customerPortal.navigateTo();
        URLAssert.assertCurrentUrlStartsWith(customerPortal);
        customerPortal.clickCustomerListingLink();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        testRealmLoginPage.form().login("bburke@redhat.com", "password");
        URLAssert.assertCurrentUrlStartsWith(customerListing);
        String src = driver.getPageSource();
        Assert.assertThat(src, Matchers.allOf(Matchers.containsString("Username: bburke@redhat.com"), Matchers.containsString("Bill Burke"), Matchers.containsString("Stian Thorgersen")));
        // account mgmt
        customerListing.clickAccountManagement();
        URLAssert.assertCurrentUrlStartsWith(testRealmAccount);
        Assert.assertEquals(testRealmAccount.getUsername(), "bburke@redhat.com");
        driver.navigate().back();
        customerListing.clickLogOut();
        // assert user not logged in
        customerPortal.clickCustomerListingLink();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
    }

    @Test
    public void testAdminInterface() {
        customerPortal.navigateTo();
        URLAssert.assertCurrentUrlStartsWith(customerPortal);
        customerPortal.clickAdminInterfaceLink();
        WaitUtils.waitForPageToLoad();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        testRealmLoginPage.form().login("admin", "password");
        URLAssert.assertCurrentUrlStartsWith(adminInterface);
        Assert.assertThat(driver.getPageSource(), Matchers.containsString("Hello admin!"));
        Assert.assertThat(driver.getPageSource(), Matchers.containsString("This second sentence is returned from a Camel RestDSL endpoint"));
        customerListing.navigateTo();
        WaitUtils.waitForPageToLoad();
        customerListing.clickLogOut();
        WaitUtils.waitForPageToLoad();
        WaitUtils.pause(2500);
        customerPortal.navigateTo();// needed for phantomjs

        WaitUtils.waitForPageToLoad();
        customerPortal.clickAdminInterfaceLink();
        WaitUtils.waitForPageToLoad();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        testRealmLoginPage.form().login("bburke@redhat.com", "password");
        URLAssert.assertCurrentUrlStartsWith(adminInterface);
        Assert.assertThat(driver.getPageSource(), Matchers.containsString("Status code is 403"));
    }

    @Test
    public void testProductPortal() {
        productPortal.navigateTo();
        WaitUtils.waitForPageToLoad();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        testRealmLoginPage.form().login("bburke@redhat.com", "password");
        URLAssert.assertCurrentUrlStartsWith(productPortal);
        Assert.assertThat(productPortal.getProduct1UnsecuredText(), Matchers.containsString("401: Unauthorized"));
        Assert.assertThat(productPortal.getProduct1SecuredText(), Matchers.containsString("Product received: id=1"));
        Assert.assertThat(productPortal.getProduct2SecuredText(), Matchers.containsString("Product received: id=2"));
        productPortal.clickLogOutLink();
        WaitUtils.waitForPageToLoad();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
    }
}

