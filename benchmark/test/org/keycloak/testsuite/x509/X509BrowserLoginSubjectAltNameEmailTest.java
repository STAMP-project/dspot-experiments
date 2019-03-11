/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.x509;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Test;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.x509.X509IdentityConfirmationPage;


/**
 *
 *
 * @author <a href="mailto:brat000012001@gmail.com">Peter Nalyvayko</a>
 * @version $Revision: 1 $
 * @unknown 8/12/2016
 */
public class X509BrowserLoginSubjectAltNameEmailTest extends AbstractX509AuthenticationTest {
    @Page
    protected AppPage appPage;

    @Page
    protected X509IdentityConfirmationPage loginConfirmationPage;

    @Page
    protected LoginPage loginPage;

    @Test
    public void loginAsUserFromCertSubjectEmail() {
        login(AbstractX509AuthenticationTest.createLoginSubjectAltNameEmail2UsernameOrEmailConfig(), userId, "test-user@localhost", "test-user@localhost");
    }
}

