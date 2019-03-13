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
package org.keycloak.testsuite.oidc.flows;


import org.junit.Test;


/**
 * Tests with response_type=code id_token
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class OIDCHybridResponseTypeCodeIDTokenTest extends AbstractOIDCResponseTypeTest {
    @Test
    public void nonceNotUsedErrorExpected() {
        super.validateNonceNotUsedErrorExpected();
    }

    @Test
    public void errorStandardFlowNotAllowed() throws Exception {
        super.validateErrorStandardFlowNotAllowed();
    }

    @Test
    public void errorImplicitFlowNotAllowed() throws Exception {
        super.validateErrorImplicitFlowNotAllowed();
    }
}

