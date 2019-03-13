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
package org.keycloak.testsuite.keys;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginPage;


public class GeneratedEcdsaKeyProviderTest extends AbstractKeycloakTest {
    private static final String DEFAULT_EC = "P-256";

    private static final String ECDSA_ELLIPTIC_CURVE_KEY = "ecdsaEllipticCurveKey";

    private static final String TEST_REALM_NAME = "test";

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Test
    public void defaultEc() {
        supportedEc(null);
    }

    @Test
    public void supportedEcP521() {
        supportedEc("P-521");
    }

    @Test
    public void supportedEcP384() {
        supportedEc("P-384");
    }

    @Test
    public void supportedEcP256() {
        supportedEc("P-256");
    }

    @Test
    public void unsupportedEcK163() {
        // NIST.FIPS.186-4 Koblitz Curve over Binary Field
        unsupportedEc("K-163");
    }
}

