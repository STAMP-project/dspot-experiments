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
package org.keycloak.saml.processing.core.parsers.saml;


import java.io.InputStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.dom.saml.v2.assertion.NameIDType;
import org.keycloak.dom.saml.v2.protocol.LogoutRequestType;
import org.w3c.dom.Element;


/**
 * Test class for SAML SLO parser.
 *
 * @author hmlnarik
 */
public class SAMLSloRequestParserTest {
    private SAMLParser parser;

    @Test(timeout = 2000)
    public void testSaml20SloResponseWithExtension() throws Exception {
        try (InputStream is = SAMLSloRequestParserTest.class.getResourceAsStream("KEYCLOAK-4552-saml20-aslo-response-via-extension.xml")) {
            Object parsedObject = parser.parse(is);
            Assert.assertThat(parsedObject, CoreMatchers.instanceOf(LogoutRequestType.class));
            LogoutRequestType resp = ((LogoutRequestType) (parsedObject));
            Assert.assertThat(resp.getSignature(), CoreMatchers.nullValue());
            Assert.assertThat(resp.getConsent(), CoreMatchers.nullValue());
            Assert.assertThat(resp.getIssuer(), CoreMatchers.not(CoreMatchers.nullValue()));
            Assert.assertThat(resp.getIssuer().getValue(), CoreMatchers.is("https://sp/"));
            NameIDType nameId = resp.getNameID();
            Assert.assertThat(nameId.getValue(), CoreMatchers.is("G-XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"));
            Assert.assertThat(resp.getExtensions(), CoreMatchers.not(CoreMatchers.nullValue()));
            Assert.assertThat(resp.getExtensions().getAny().size(), CoreMatchers.is(1));
            Assert.assertThat(resp.getExtensions().getAny().get(0), CoreMatchers.instanceOf(Element.class));
            Element el = ((Element) (resp.getExtensions().getAny().get(0)));
            Assert.assertThat(el.getLocalName(), CoreMatchers.is("Asynchronous"));
            Assert.assertThat(el.getNamespaceURI(), CoreMatchers.is("urn:oasis:names:tc:SAML:2.0:protocol:ext:async-slo"));
        }
    }
}

