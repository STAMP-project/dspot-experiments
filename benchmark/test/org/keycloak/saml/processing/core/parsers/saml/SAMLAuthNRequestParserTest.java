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
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.w3c.dom.Element;


/**
 * Test class for SAML AuthNRequest parser.
 *
 * @author hmlnarik
 */
public class SAMLAuthNRequestParserTest {
    private SAMLParser parser;

    @Test(timeout = 2000)
    public void testSaml20AttributeQuery() throws Exception {
        try (InputStream is = SAMLAuthNRequestParserTest.class.getResourceAsStream("saml20-authnrequest.xml")) {
            Object parsedObject = parser.parse(is);
            Assert.assertThat(parsedObject, CoreMatchers.instanceOf(AuthnRequestType.class));
            AuthnRequestType req = ((AuthnRequestType) (parsedObject));
            Assert.assertThat(req.getSignature(), CoreMatchers.nullValue());
            Assert.assertThat(req.getConsent(), CoreMatchers.nullValue());
            Assert.assertThat(req.getIssuer(), CoreMatchers.not(CoreMatchers.nullValue()));
            Assert.assertThat(req.getIssuer().getValue(), CoreMatchers.is("https://sp/"));
            Assert.assertThat(req.getNameIDPolicy().getFormat().toString(), CoreMatchers.is("urn:oasis:names:tc:SAML:2.0:nameid-format:transient"));
        }
    }

    @Test(timeout = 2000)
    public void testSaml20AttributeQueryWithExtension() throws Exception {
        try (InputStream is = SAMLAuthNRequestParserTest.class.getResourceAsStream("saml20-authnrequest-with-extension.xml")) {
            Object parsedObject = parser.parse(is);
            Assert.assertThat(parsedObject, CoreMatchers.instanceOf(AuthnRequestType.class));
            AuthnRequestType req = ((AuthnRequestType) (parsedObject));
            Assert.assertThat(req.getSignature(), CoreMatchers.nullValue());
            Assert.assertThat(req.getConsent(), CoreMatchers.nullValue());
            Assert.assertThat(req.getIssuer(), CoreMatchers.not(CoreMatchers.nullValue()));
            Assert.assertThat(req.getIssuer().getValue(), CoreMatchers.is("https://sp/"));
            Assert.assertThat(req.getNameIDPolicy().getFormat().toString(), CoreMatchers.is("urn:oasis:names:tc:SAML:2.0:nameid-format:transient"));
            Assert.assertThat(req.getExtensions(), CoreMatchers.not(CoreMatchers.nullValue()));
            Assert.assertThat(req.getExtensions().getAny().size(), CoreMatchers.is(2));
            Assert.assertThat(req.getExtensions().getAny().get(0), CoreMatchers.instanceOf(Element.class));
            Assert.assertThat(req.getExtensions().getAny().get(1), CoreMatchers.instanceOf(Element.class));
            Element el = ((Element) (req.getExtensions().getAny().get(0)));
            Assert.assertThat(el.getLocalName(), CoreMatchers.is("KeyInfo"));
            Assert.assertThat(el.getNamespaceURI(), CoreMatchers.is("urn:keycloak:ext:key:1.0"));
            Assert.assertThat(el.getAttribute("MessageSigningKeyId"), CoreMatchers.is("FJ86GcF3jTbNLOco4NvZkUCIUmfYCqoqtOQeMfbhNlE"));
        }
    }
}

