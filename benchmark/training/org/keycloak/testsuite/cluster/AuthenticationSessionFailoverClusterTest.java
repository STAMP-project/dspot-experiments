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
package org.keycloak.testsuite.cluster;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.LoginPasswordUpdatePage;
import org.keycloak.testsuite.pages.LoginUpdateProfilePage;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class AuthenticationSessionFailoverClusterTest extends AbstractFailoverClusterTest {
    private String userId;

    @Page
    protected LoginPage loginPage;

    @Page
    protected LoginPasswordUpdatePage updatePasswordPage;

    @Page
    protected LoginUpdateProfilePage updateProfilePage;

    @Page
    protected AppPage appPage;

    @Test
    public void failoverDuringAuthentication() throws Exception {
        boolean expectSuccessfulFailover = (AbstractFailoverClusterTest.SESSION_CACHE_OWNERS) >= 2;
        log.info((((((("AUTHENTICATION FAILOVER TEST: cluster size = " + (getClusterSize())) + ", session-cache owners = ") + (AbstractFailoverClusterTest.SESSION_CACHE_OWNERS)) + " --> Testsing for ") + (expectSuccessfulFailover ? "" : "UN")) + "SUCCESSFUL session failover."));
        Assert.assertEquals(2, getClusterSize());
        failoverTest(expectSuccessfulFailover);
    }
}

