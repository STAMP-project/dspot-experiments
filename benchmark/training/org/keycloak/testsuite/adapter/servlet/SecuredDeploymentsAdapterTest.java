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
package org.keycloak.testsuite.adapter.servlet;


import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.adapter.AbstractServletsAdapterTest;
import org.keycloak.testsuite.adapter.page.CustomerPortalSubsystem;
import org.keycloak.testsuite.adapter.page.ProductPortalSubsystem;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;
import org.keycloak.testsuite.arquillian.containers.SelfManagedAppContainerLifecycle;
import org.keycloak.testsuite.util.URLAssert;


@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY_DEPRECATED)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP6)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP71)
public class SecuredDeploymentsAdapterTest extends AbstractServletsAdapterTest implements SelfManagedAppContainerLifecycle {
    @ArquillianResource
    private ContainerController controller;

    @Page
    private CustomerPortalSubsystem customerPortalSubsystem;

    @Page
    private ProductPortalSubsystem productPortalSubsystem;

    @Test
    public void testSecuredDeployments() {
        customerPortalSubsystem.navigateTo();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        testRealmLoginPage.form().login("bburke@redhat.com", "password");
        assertPageContains("Bill Burke");
        assertPageContains("Stian Thorgersen");
        productPortalSubsystem.navigateTo();
        URLAssert.assertCurrentUrlEquals(productPortalSubsystem);
        assertPageContains("iPhone");
        assertPageContains("iPad");
    }
}

