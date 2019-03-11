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


import EncryptionMethodType.EncryptionMethod;
import EntityDescriptorType.EDTChoiceType;
import JBossSAMLURIConstants.AC_UNSPECIFIED;
import JBossSAMLURIConstants.NAMEID_FORMAT_ENTITY;
import JBossSAMLURIConstants.SUBJECT_CONFIRMATION_BEARER;
import KeyTypes.ENCRYPTION;
import KeyTypes.SIGNING;
import ResponseType.RTChoiceType;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.keycloak.common.util.Base64;
import org.keycloak.common.util.DerUtils;
import org.keycloak.dom.saml.v2.assertion.AssertionType;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType.ASTChoiceType;
import org.keycloak.dom.saml.v2.assertion.AttributeType;
import org.keycloak.dom.saml.v2.assertion.AudienceRestrictionType;
import org.keycloak.dom.saml.v2.assertion.AuthnContextType;
import org.keycloak.dom.saml.v2.assertion.AuthnStatementType;
import org.keycloak.dom.saml.v2.assertion.EncryptedAssertionType;
import org.keycloak.dom.saml.v2.assertion.NameIDType;
import org.keycloak.dom.saml.v2.assertion.StatementAbstractType;
import org.keycloak.dom.saml.v2.assertion.SubjectConfirmationType;
import org.keycloak.dom.saml.v2.assertion.SubjectType;
import org.keycloak.dom.saml.v2.metadata.AttributeAuthorityDescriptorType;
import org.keycloak.dom.saml.v2.metadata.AttributeConsumingServiceType;
import org.keycloak.dom.saml.v2.metadata.AuthnAuthorityDescriptorType;
import org.keycloak.dom.saml.v2.metadata.EndpointType;
import org.keycloak.dom.saml.v2.metadata.EntitiesDescriptorType;
import org.keycloak.dom.saml.v2.metadata.EntityDescriptorType;
import org.keycloak.dom.saml.v2.metadata.IDPSSODescriptorType;
import org.keycloak.dom.saml.v2.metadata.IndexedEndpointType;
import org.keycloak.dom.saml.v2.metadata.KeyDescriptorType;
import org.keycloak.dom.saml.v2.metadata.LocalizedNameType;
import org.keycloak.dom.saml.v2.metadata.LocalizedURIType;
import org.keycloak.dom.saml.v2.metadata.PDPDescriptorType;
import org.keycloak.dom.saml.v2.metadata.RequestedAttributeType;
import org.keycloak.dom.saml.v2.metadata.SPSSODescriptorType;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.dom.saml.v2.protocol.LogoutRequestType;
import org.keycloak.dom.saml.v2.protocol.ResponseType;
import org.keycloak.dom.saml.v2.protocol.StatusResponseType;
import org.keycloak.dom.xmlsec.w3.xmldsig.DSAKeyValueType;
import org.keycloak.dom.xmlsec.w3.xmldsig.KeyInfoType;
import org.keycloak.dom.xmlsec.w3.xmldsig.RSAKeyValueType;
import org.keycloak.dom.xmlsec.w3.xmldsig.X509CertificateType;
import org.keycloak.dom.xmlsec.w3.xmldsig.X509DataType;
import org.keycloak.dom.xmlsec.w3.xmlenc.EncryptionMethodType;
import org.keycloak.saml.common.exceptions.ParsingException;
import org.keycloak.saml.common.exceptions.ProcessingException;
import org.keycloak.saml.processing.core.saml.v2.common.SAMLDocumentHolder;
import org.keycloak.saml.processing.core.saml.v2.util.AssertionUtil;
import org.keycloak.saml.processing.core.saml.v2.util.XMLTimeUtil;
import org.w3c.dom.Element;


/**
 * Test class for SAML parser.
 *
 * To create SAML XML for use in the test, use for instance https://www.samltool.com, {@link #PRIVATE_KEY} and
 * {@link #PUBLIC_CERT}.
 *
 * TODO: Add further tests.
 *
 * @author hmlnarik
 */
public class SAMLParserTest {
    private static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOJDgeiUFOERg6N2qUeLjkE7HDzg4h2m4hMG8WDYLhuAm6k2pq+1SkRDvisD67y2AvXXFz069Vw5TMDnpbmGpigEbdr+YT7As+YpVhxI1nSwTzzEAuL2Ywun1FNfOFAeBt6hzQ/veJ+rc3D3BSgjeY/yiZNen36T3BD8pi3HojErAgMBAAECgYBWmtpVqKCZSXfmkJvYy70GkNaNItLJ4L+14rlvhS+YzVBHo6iHps+nc3qNwnFwCQb3DH5TrIaP50rOp5wSeEyOWk7fOJeAwM4Vsc3d+av/Iu/WwNDyAFW3gGO19YvccfGvEbToMPtppOt47UDK26xfibCwUFwEGg0hGc0gcVQWMQJBAPl8YnETSjI0wZuaSdkUp2/FtHEAZa1yFPtPx7CVCpUG53frKOAx2t0N7AQ2vQUNPqOas8gDGr5O5J+l9tjOD0MCQQDoK+asMuMnSClm/RZRFIckcToFwjfgNQY/AKN31k705jJr+3+er3VlODL7dpnF0X2mVDjiXIp4hH9K0qfW+TP5AkAQOIMaAPwQ+Zcg684jXBFq1freYf06YrF0iYJdO8N9Xv6LsHFu6i7lsnMG7xwpCOxqrLNFrNX/S5fXvW2oOPWLAkEAiTu547tIjaWX42ph0JdDsoTC+Tht8rck9ASam3Evxo5y62UDcHbh+2yWphDaoBVOIgzSeuqcZtRasY2G7AjtcQJAEiXYeHB5+lancDDkBhH8KKdl1+FORRB9kJ4gxTwrjLwprWFjdatMb3O+xJGss+KnVUa/THRa5CRX4pHh93711Q==";

    /**
     * The public certificate that corresponds to {@link #PRIVATE_KEY}.
     */
    private static final String PUBLIC_CERT = "MIICXjCCAcegAwIBAgIBADANBgkqhkiG9w0BAQ0FADBLMQswCQYDVQQGEwJubzERMA8GA1UECAwIVmVzdGZvbGQxEzARBgNVBAoMCkV4YW1wbGVPcmcxFDASBgNVBAMMC2V4YW1wbGUub3JnMCAXDTE3MDIyNzEwNTY0MFoYDzIxMTcwMjAzMTA1NjQwWjBLMQswCQYDVQQGEwJubzERMA8GA1UECAwIVmVzdGZvbGQxEzARBgNVBAoMCkV4YW1wbGVPcmcxFDASBgNVBAMMC2V4YW1wbGUub3JnMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDiQ4HolBThEYOjdqlHi45BOxw84OIdpuITBvFg2C4bgJupNqavtUpEQ74rA+u8tgL11xc9OvVcOUzA56W5hqYoBG3a/mE+wLPmKVYcSNZ0sE88xALi9mMLp9RTXzhQHgbeoc0P73ifq3Nw9wUoI3mP8omTXp9+k9wQ/KYtx6IxKwIDAQABo1AwTjAdBgNVHQ4EFgQUzWjvSL0O2V2B2N9G1qARQiVgv3QwHwYDVR0jBBgwFoAUzWjvSL0O2V2B2N9G1qARQiVgv3QwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQ0FAAOBgQBgvKTTcLGlF0KvnIGxkzdaFeYewQtsQZHgnUt+JGKge0CyUU+QPVFhrH19b7fjKeykq/avm/2hku4mKaPyRYpvU9Gm+ARz67rs/vr0ZgJFk00TGI6ssGhdFd7iCptuIh5lEvWk1hD5LzThOI3isq0gK2tTbhafQOkKa45IwbOQ8Q==";

    private SAMLParser parser;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSaml20EncryptedAssertionsSignedReceivedWithRedirectBinding() throws Exception {
        ResponseType resp = assertParsed("saml20-encrypted-signed-redirect-response.xml", ResponseType.class);
        Assert.assertThat(resp.getSignature(), Matchers.nullValue());
        Assert.assertThat(resp.getConsent(), Matchers.nullValue());
        Assert.assertThat(resp.getIssuer(), Matchers.not(Matchers.nullValue()));
        Assert.assertThat(resp.getIssuer().getValue(), Matchers.is("http://localhost:8081/auth/realms/saml-demo"));
        Assert.assertThat(resp.getIssuer().getFormat(), Matchers.is(NAMEID_FORMAT_ENTITY.getUri()));
        Assert.assertThat(resp.getExtensions(), Matchers.not(Matchers.nullValue()));
        Assert.assertThat(resp.getExtensions().getAny().size(), Matchers.is(1));
        Assert.assertThat(resp.getExtensions().getAny().get(0), Matchers.instanceOf(Element.class));
        Element el = ((Element) (resp.getExtensions().getAny().get(0)));
        Assert.assertThat(el.getLocalName(), Matchers.is("KeyInfo"));
        Assert.assertThat(el.getNamespaceURI(), Matchers.is("urn:keycloak:ext:key:1.0"));
        Assert.assertThat(el.hasAttribute("MessageSigningKeyId"), Matchers.is(true));
        Assert.assertThat(el.getAttribute("MessageSigningKeyId"), Matchers.is("FJ86GcF3jTbNLOco4NvZkUCIUmfYCqoqtOQeMfbhNlE"));
        Assert.assertThat(resp.getAssertions(), Matchers.not(Matchers.nullValue()));
        Assert.assertThat(resp.getAssertions().size(), Matchers.is(1));
        final EncryptedAssertionType ea = resp.getAssertions().get(0).getEncryptedAssertion();
        Assert.assertThat(ea, Matchers.notNullValue());
        Assert.assertThat(ea.getEncryptedElement(), Matchers.notNullValue());
        Assert.assertThat(ea.getEncryptedElement().getLocalName(), Matchers.is("EncryptedAssertion"));
    }

    @Test
    public void testSaml20EncryptedAssertion() throws Exception {
        EncryptedAssertionType ea = assertParsed("saml20-assertion-encrypted.xml", EncryptedAssertionType.class);
        Assert.assertThat(ea, Matchers.notNullValue());
        Assert.assertThat(ea.getEncryptedElement(), Matchers.notNullValue());
        Assert.assertThat(ea.getEncryptedElement().getLocalName(), Matchers.is("EncryptedAssertion"));
    }

    @Test
    public void testSaml20EncryptedAssertionWithNewlines() throws Exception {
        SAMLDocumentHolder holder = assertParsed("KEYCLOAK-4489-encrypted-assertion-with-newlines.xml", SAMLDocumentHolder.class);
        Assert.assertThat(holder.getSamlObject(), Matchers.instanceOf(ResponseType.class));
        ResponseType resp = ((ResponseType) (holder.getSamlObject()));
        Assert.assertThat(resp.getAssertions().size(), Matchers.is(1));
        ResponseType.RTChoiceType rtChoiceType = resp.getAssertions().get(0);
        Assert.assertNull(rtChoiceType.getAssertion());
        Assert.assertNotNull(rtChoiceType.getEncryptedAssertion());
        PrivateKey privateKey = DerUtils.decodePrivateKey(Base64.decode(SAMLParserTest.PRIVATE_KEY));
        AssertionUtil.decryptAssertion(holder, resp, privateKey);
        rtChoiceType = resp.getAssertions().get(0);
        Assert.assertNotNull(rtChoiceType.getAssertion());
        Assert.assertNull(rtChoiceType.getEncryptedAssertion());
    }

    @Test
    public void testSaml20EncryptedAssertionsSignedTwoExtensionsReceivedWithRedirectBinding() throws Exception {
        Element el;
        ResponseType resp = assertParsed("saml20-encrypted-signed-redirect-response-two-extensions.xml", ResponseType.class);
        Assert.assertThat(resp.getSignature(), Matchers.nullValue());
        Assert.assertThat(resp.getConsent(), Matchers.nullValue());
        Assert.assertThat(resp.getIssuer(), Matchers.not(Matchers.nullValue()));
        Assert.assertThat(resp.getIssuer().getValue(), Matchers.is("http://localhost:8081/auth/realms/saml-demo"));
        Assert.assertThat(resp.getExtensions(), Matchers.not(Matchers.nullValue()));
        Assert.assertThat(resp.getExtensions().getAny().size(), Matchers.is(2));
        Assert.assertThat(resp.getExtensions().getAny().get(0), Matchers.instanceOf(Element.class));
        el = ((Element) (resp.getExtensions().getAny().get(0)));
        Assert.assertThat(el.getLocalName(), Matchers.is("KeyInfo"));
        Assert.assertThat(el.getNamespaceURI(), Matchers.is("urn:keycloak:ext:key:1.0"));
        Assert.assertThat(el.hasAttribute("MessageSigningKeyId"), Matchers.is(true));
        Assert.assertThat(el.getAttribute("MessageSigningKeyId"), Matchers.is("FJ86GcF3jTbNLOco4NvZkUCIUmfYCqoqtOQeMfbhNlE"));
        Assert.assertThat(resp.getExtensions().getAny().get(1), Matchers.instanceOf(Element.class));
        el = ((Element) (resp.getExtensions().getAny().get(1)));
        Assert.assertThat(el.getLocalName(), Matchers.is("ever"));
        Assert.assertThat(el.getNamespaceURI(), Matchers.is("urn:keycloak:ext:what:1.0"));
        Assert.assertThat(el.hasAttribute("what"), Matchers.is(true));
        Assert.assertThat(el.getAttribute("what"), Matchers.is("ever"));
        Assert.assertThat(resp.getAssertions(), Matchers.not(Matchers.nullValue()));
        Assert.assertThat(resp.getAssertions().size(), Matchers.is(1));
    }

    @Test
    public void testSaml20AuthnResponseNonAsciiNameDefaultUtf8() throws Exception {
        ResponseType rt = assertParsed("KEYCLOAK-3971-utf-8-no-header-authnresponse.xml", ResponseType.class);
        Assert.assertThat(rt.getAssertions().size(), Matchers.is(1));
        final AssertionType assertion = rt.getAssertions().get(0).getAssertion();
        Assert.assertThat(assertion.getSubject().getSubType().getBaseID(), Matchers.instanceOf(NameIDType.class));
        NameIDType nameId = ((NameIDType) (assertion.getSubject().getSubType().getBaseID()));
        Assert.assertThat(nameId.getValue(), Matchers.is("ro?????????????????????"));
        Assert.assertThat(assertion.getSubject().getConfirmation(), Matchers.hasSize(1));
        Assert.assertThat(assertion.getSubject().getConfirmation().get(0).getSubjectConfirmationData(), Matchers.notNullValue());
        Assert.assertThat(assertion.getSubject().getConfirmation().get(0).getSubjectConfirmationData().getAnyType(), Matchers.instanceOf(KeyInfoType.class));
        KeyInfoType kit = ((KeyInfoType) (assertion.getSubject().getConfirmation().get(0).getSubjectConfirmationData().getAnyType()));
        Assert.assertThat(kit.getContent(), Matchers.hasItem(Matchers.instanceOf(X509DataType.class)));
        X509DataType rsaKit = ((X509DataType) (kit.getContent().get(0)));
        Assert.assertThat(rsaKit.getDataObjects(), Matchers.hasSize(1));
        Assert.assertThat(rsaKit.getDataObjects().get(0), Matchers.instanceOf(X509CertificateType.class));
    }

    @Test
    public void testSaml20AuthnResponseNonAsciiNameDefaultLatin2() throws Exception {
        ResponseType rt = assertParsed("KEYCLOAK-3971-8859-2-in-header-authnresponse.xml", ResponseType.class);
        Assert.assertThat(rt.getAssertions().size(), Matchers.is(1));
        final AssertionType assertion = rt.getAssertions().get(0).getAssertion();
        final SubjectType subject = assertion.getSubject();
        Assert.assertThat(subject.getConfirmation(), Matchers.hasSize(1));
        SubjectConfirmationType confirmation = subject.getConfirmation().get(0);
        Assert.assertThat(confirmation.getMethod(), Matchers.is(SUBJECT_CONFIRMATION_BEARER.get()));
        Assert.assertThat(confirmation.getSubjectConfirmationData(), Matchers.notNullValue());
        Assert.assertThat(confirmation.getSubjectConfirmationData().getInResponseTo(), Matchers.is("ID_cc0ff6f7-b481-4c98-9a79-481d50958290"));
        Assert.assertThat(confirmation.getSubjectConfirmationData().getRecipient(), Matchers.is("http://localhost:8080/sales-post-sig/saml"));
        Assert.assertThat(subject.getSubType().getBaseID(), Matchers.instanceOf(NameIDType.class));
        NameIDType nameId = ((NameIDType) (subject.getSubType().getBaseID()));
        Assert.assertThat(nameId.getValue(), Matchers.is("ro?????????"));
    }

    @Test
    public void testSaml20PostLogoutRequest() throws Exception {
        assertParsed("saml20-signed-logout-request.xml", LogoutRequestType.class);
    }

    @Test
    public void testOrganizationDetailsMetadata() throws Exception {
        assertParsed("KEYCLOAK-4040-sharefile-metadata.xml", EntityDescriptorType.class);
    }

    @Test
    public void testProxyRestrictionTagHandling() throws Exception {
        assertParsed("KEYCLOAK-6412-response-with-proxy-restriction.xml", ResponseType.class);
    }

    @Test
    public void testSaml20MetadataEntityDescriptorIdP() throws Exception {
        EntityDescriptorType entityDescriptor = assertParsed("saml20-entity-descriptor-idp.xml", EntityDescriptorType.class);
        List<EntityDescriptorType.EDTChoiceType> descriptors = entityDescriptor.getChoiceType();
        Assert.assertThat(descriptors, Matchers.hasSize(2));
        // IDPSSO descriptor
        IDPSSODescriptorType idpDescriptor = descriptors.get(0).getDescriptors().get(0).getIdpDescriptor();
        Assert.assertThat(idpDescriptor, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(idpDescriptor.isWantAuthnRequestsSigned(), Matchers.is(true));
        Assert.assertThat(idpDescriptor.getProtocolSupportEnumeration(), Matchers.contains("urn:oasis:names:tc:SAML:2.0:protocol"));
        // Key descriptor
        List<KeyDescriptorType> keyDescriptors = idpDescriptor.getKeyDescriptor();
        Assert.assertThat(keyDescriptors, Matchers.hasSize(1));
        KeyDescriptorType signingKey = keyDescriptors.get(0);
        Assert.assertThat(signingKey.getUse(), Matchers.is(SIGNING));
        Assert.assertThat(signingKey.getEncryptionMethod(), Matchers.is(Matchers.emptyCollectionOf(EncryptionMethodType.class)));
        Assert.assertThat(signingKey.getKeyInfo().getElementsByTagName("ds:KeyName").item(0).getTextContent(), Matchers.is("IdentityProvider.com SSO Key"));
        // Single logout services
        Assert.assertThat(idpDescriptor.getSingleLogoutService(), Matchers.hasSize(2));
        EndpointType singleLS1 = idpDescriptor.getSingleLogoutService().get(0);
        Assert.assertThat(singleLS1.getBinding(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:bindings:SOAP")));
        Assert.assertThat(singleLS1.getLocation(), Matchers.is(URI.create("https://IdentityProvider.com/SAML/SLO/SOAP")));
        Assert.assertThat(singleLS1.getResponseLocation(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(singleLS1.getAny(), Matchers.is(Matchers.emptyCollectionOf(Object.class)));
        Assert.assertThat(singleLS1.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        EndpointType singleLS2 = idpDescriptor.getSingleLogoutService().get(1);
        Assert.assertThat(singleLS2.getBinding(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect")));
        Assert.assertThat(singleLS2.getLocation(), Matchers.is(URI.create("https://IdentityProvider.com/SAML/SLO/Browser")));
        Assert.assertThat(singleLS2.getResponseLocation(), Matchers.is(URI.create("https://IdentityProvider.com/SAML/SLO/Response")));
        Assert.assertThat(singleLS2.getAny(), Matchers.is(Matchers.emptyCollectionOf(Object.class)));
        Assert.assertThat(singleLS2.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        // NameID
        Assert.assertThat(idpDescriptor.getNameIDFormat(), Matchers.containsInAnyOrder("urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName", "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent", "urn:oasis:names:tc:SAML:2.0:nameid-format:transient"));
        // Single sign on services
        Assert.assertThat(idpDescriptor.getSingleSignOnService(), Matchers.hasSize(2));
        EndpointType singleSO1 = idpDescriptor.getSingleSignOnService().get(0);
        Assert.assertThat(singleSO1.getBinding(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect")));
        Assert.assertThat(singleSO1.getLocation(), Matchers.is(URI.create("https://IdentityProvider.com/SAML/SSO/Browser")));
        Assert.assertThat(singleSO1.getResponseLocation(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(singleSO1.getAny(), Matchers.is(Matchers.emptyCollectionOf(Object.class)));
        Assert.assertThat(singleSO1.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        EndpointType singleSO2 = idpDescriptor.getSingleSignOnService().get(1);
        Assert.assertThat(singleSO2.getBinding(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST")));
        Assert.assertThat(singleSO2.getLocation(), Matchers.is(URI.create("https://IdentityProvider.com/SAML/SSO/Browser")));
        Assert.assertThat(singleSO2.getResponseLocation(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(singleSO2.getAny(), Matchers.is(Matchers.emptyCollectionOf(Object.class)));
        Assert.assertThat(singleSO2.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        // Attributes
        Assert.assertThat(idpDescriptor.getAttribute(), Matchers.hasSize(2));
        AttributeType attr1 = idpDescriptor.getAttribute().get(0);
        Assert.assertThat(attr1.getNameFormat(), Matchers.is("urn:oasis:names:tc:SAML:2.0:attrname-format:uri"));
        Assert.assertThat(attr1.getName(), Matchers.is("urn:oid:1.3.6.1.4.1.5923.1.1.1.6"));
        Assert.assertThat(attr1.getFriendlyName(), Matchers.is("eduPersonPrincipalName"));
        Assert.assertThat(attr1.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        Assert.assertThat(attr1.getAttributeValue(), Matchers.is(Matchers.emptyCollectionOf(Object.class)));
        AttributeType attr2 = idpDescriptor.getAttribute().get(1);
        Assert.assertThat(attr2.getNameFormat(), Matchers.is("urn:oasis:names:tc:SAML:2.0:attrname-format:uri"));
        Assert.assertThat(attr2.getName(), Matchers.is("urn:oid:1.3.6.1.4.1.5923.1.1.1.1"));
        Assert.assertThat(attr2.getFriendlyName(), Matchers.is("eduPersonAffiliation"));
        Assert.assertThat(attr2.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        Assert.assertThat(attr2.getAttributeValue(), Matchers.containsInAnyOrder(((Object) ("member")), "student", "faculty", "employee", "staff"));
        // Organization
        Assert.assertThat(entityDescriptor.getOrganization().getOrganizationName(), Matchers.hasSize(1));
        LocalizedNameType orgName = entityDescriptor.getOrganization().getOrganizationName().get(0);
        Assert.assertThat(orgName.getLang(), Matchers.is("en"));
        Assert.assertThat(orgName.getValue(), Matchers.is("Identity Providers R\n            US"));
        Assert.assertThat(entityDescriptor.getOrganization().getOrganizationDisplayName(), Matchers.hasSize(1));
        LocalizedNameType orgDispName = entityDescriptor.getOrganization().getOrganizationDisplayName().get(0);
        Assert.assertThat(orgDispName.getLang(), Matchers.is("en"));
        Assert.assertThat(orgDispName.getValue(), Matchers.is("Identity Providers R US, a Division of Lerxst Corp."));
        Assert.assertThat(entityDescriptor.getOrganization().getOrganizationURL(), Matchers.hasSize(1));
        LocalizedURIType orgURL = entityDescriptor.getOrganization().getOrganizationURL().get(0);
        Assert.assertThat(orgURL.getLang(), Matchers.is("en"));
        Assert.assertThat(orgURL.getValue(), Matchers.is(URI.create("https://IdentityProvider.com")));
    }

    @Test
    public void testSAML20MetadataEntityDescriptorAttrA() throws Exception {
        EntityDescriptorType entityDescriptor = assertParsed("saml20-entity-descriptor-idp.xml", EntityDescriptorType.class);
        List<EntityDescriptorType.EDTChoiceType> descriptors = entityDescriptor.getChoiceType();
        Assert.assertThat(descriptors, Matchers.hasSize(2));
        AttributeAuthorityDescriptorType aaDescriptor = descriptors.get(1).getDescriptors().get(0).getAttribDescriptor();
        Assert.assertThat(aaDescriptor, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(aaDescriptor.getProtocolSupportEnumeration(), Matchers.contains("urn:oasis:names:tc:SAML:2.0:protocol"));
        // Key descriptor
        List<KeyDescriptorType> keyDescriptors = aaDescriptor.getKeyDescriptor();
        Assert.assertThat(keyDescriptors, Matchers.hasSize(1));
        KeyDescriptorType signingKey = keyDescriptors.get(0);
        Assert.assertThat(signingKey.getUse(), Matchers.is(SIGNING));
        Assert.assertThat(signingKey.getEncryptionMethod(), Matchers.is(Matchers.emptyCollectionOf(EncryptionMethodType.class)));
        Assert.assertThat(signingKey.getKeyInfo().getElementsByTagName("ds:KeyName").item(0).getTextContent(), Matchers.is("IdentityProvider.com AA Key"));
        // Attribute service
        Assert.assertThat(aaDescriptor.getAttributeService(), Matchers.hasSize(1));
        EndpointType attrServ = aaDescriptor.getAttributeService().get(0);
        Assert.assertThat(attrServ.getBinding(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:bindings:SOAP")));
        Assert.assertThat(attrServ.getLocation(), Matchers.is(URI.create("https://IdentityProvider.com/SAML/AA/SOAP")));
        Assert.assertThat(attrServ.getResponseLocation(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(attrServ.getAny(), Matchers.is(Matchers.emptyCollectionOf(Object.class)));
        Assert.assertThat(attrServ.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        // AssertionIDRequestService
        Assert.assertThat(aaDescriptor.getAssertionIDRequestService(), Matchers.hasSize(1));
        EndpointType assertIDRServ = aaDescriptor.getAssertionIDRequestService().get(0);
        Assert.assertThat(assertIDRServ.getBinding(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:bindings:URI")));
        Assert.assertThat(assertIDRServ.getLocation(), Matchers.is(URI.create("https://IdentityProvider.com/SAML/AA/URI")));
        Assert.assertThat(assertIDRServ.getResponseLocation(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(assertIDRServ.getAny(), Matchers.is(Matchers.emptyCollectionOf(Object.class)));
        Assert.assertThat(assertIDRServ.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        // NameID
        Assert.assertThat(aaDescriptor.getNameIDFormat(), Matchers.containsInAnyOrder("urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName", "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent", "urn:oasis:names:tc:SAML:2.0:nameid-format:transient"));
        Assert.assertThat(aaDescriptor.getAttribute(), Matchers.hasSize(2));
        AttributeType attr1 = aaDescriptor.getAttribute().get(0);
        Assert.assertThat(attr1.getNameFormat(), Matchers.is("urn:oasis:names:tc:SAML:2.0:attrname-format:uri"));
        Assert.assertThat(attr1.getName(), Matchers.is("urn:oid:1.3.6.1.4.1.5923.1.1.1.6"));
        Assert.assertThat(attr1.getFriendlyName(), Matchers.is("eduPersonPrincipalName"));
        Assert.assertThat(attr1.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        Assert.assertThat(attr1.getAttributeValue(), Matchers.is(Matchers.emptyCollectionOf(Object.class)));
        AttributeType attr2 = aaDescriptor.getAttribute().get(1);
        Assert.assertThat(attr2.getNameFormat(), Matchers.is("urn:oasis:names:tc:SAML:2.0:attrname-format:uri"));
        Assert.assertThat(attr2.getName(), Matchers.is("urn:oid:1.3.6.1.4.1.5923.1.1.1.1"));
        Assert.assertThat(attr2.getFriendlyName(), Matchers.is("eduPersonAffiliation"));
        Assert.assertThat(attr2.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        Assert.assertThat(attr2.getAttributeValue(), Matchers.containsInAnyOrder(((Object) ("member")), "student", "faculty", "employee", "staff"));
    }

    @Test
    public void testSaml20MetadataEntityDescriptorSP() throws Exception {
        EntityDescriptorType entityDescriptor = assertParsed("saml20-entity-descriptor-sp.xml", EntityDescriptorType.class);
        Assert.assertThat(entityDescriptor.getEntityID(), Matchers.is("https://ServiceProvider.com/SAML"));
        Assert.assertThat(entityDescriptor.getValidUntil(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(entityDescriptor.getCacheDuration(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(entityDescriptor.getID(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(entityDescriptor.getExtensions(), Matchers.is(Matchers.nullValue()));
        List<EntityDescriptorType.EDTChoiceType> descriptors = entityDescriptor.getChoiceType();
        Assert.assertThat(descriptors, Matchers.hasSize(1));
        // SP Descriptor
        SPSSODescriptorType spDescriptor = descriptors.get(0).getDescriptors().get(0).getSpDescriptor();
        Assert.assertThat(spDescriptor, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(spDescriptor.isAuthnRequestsSigned(), Matchers.is(true));
        Assert.assertThat(spDescriptor.isWantAssertionsSigned(), Matchers.is(false));
        Assert.assertThat(spDescriptor.getProtocolSupportEnumeration(), Matchers.contains("urn:oasis:names:tc:SAML:2.0:protocol"));
        // Key descriptor
        List<KeyDescriptorType> keyDescriptors = spDescriptor.getKeyDescriptor();
        Assert.assertThat(keyDescriptors, Matchers.hasSize(2));
        KeyDescriptorType signingKey = keyDescriptors.get(0);
        Assert.assertThat(signingKey.getUse(), Matchers.is(SIGNING));
        Assert.assertThat(signingKey.getEncryptionMethod(), Matchers.is(Matchers.emptyCollectionOf(EncryptionMethodType.class)));
        Assert.assertThat(signingKey.getKeyInfo().getElementsByTagName("ds:KeyName").item(0).getTextContent(), Matchers.is("ServiceProvider.com SSO Key"));
        KeyDescriptorType encryptionKey = keyDescriptors.get(1);
        Assert.assertThat(encryptionKey.getUse(), Matchers.is(ENCRYPTION));
        Assert.assertThat(encryptionKey.getKeyInfo().getElementsByTagName("ds:KeyName").item(0).getTextContent(), Matchers.is("ServiceProvider.com Encrypt Key"));
        List<EncryptionMethodType> encryptionMethods = encryptionKey.getEncryptionMethod();
        Assert.assertThat(encryptionMethods, Matchers.<EncryptionMethodType>hasSize(1));
        Assert.assertThat(encryptionMethods.get(0).getAlgorithm(), Matchers.is("http://www.w3.org/2001/04/xmlenc#rsa-1_5"));
        Assert.assertThat(encryptionMethods.get(0).getEncryptionMethod(), Matchers.is(Matchers.nullValue()));
        // Single logout services
        Assert.assertThat(spDescriptor.getSingleLogoutService(), Matchers.hasSize(2));
        EndpointType singleLS1 = spDescriptor.getSingleLogoutService().get(0);
        Assert.assertThat(singleLS1.getBinding(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:bindings:SOAP")));
        Assert.assertThat(singleLS1.getLocation(), Matchers.is(URI.create("https://ServiceProvider.com/SAML/SLO/SOAP")));
        Assert.assertThat(singleLS1.getResponseLocation(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(singleLS1.getAny(), Matchers.is(Matchers.emptyCollectionOf(Object.class)));
        Assert.assertThat(singleLS1.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        EndpointType singleLS2 = spDescriptor.getSingleLogoutService().get(1);
        Assert.assertThat(singleLS2.getBinding(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect")));
        Assert.assertThat(singleLS2.getLocation(), Matchers.is(URI.create("https://ServiceProvider.com/SAML/SLO/Browser")));
        Assert.assertThat(singleLS2.getResponseLocation(), Matchers.is(URI.create("https://ServiceProvider.com/SAML/SLO/Response")));
        Assert.assertThat(singleLS2.getAny(), Matchers.is(Matchers.emptyCollectionOf(Object.class)));
        Assert.assertThat(singleLS2.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        // NameID
        Assert.assertThat(spDescriptor.getNameIDFormat(), Matchers.contains("urn:oasis:names:tc:SAML:2.0:nameid-format:transient"));
        // Assertion consumer services
        List<IndexedEndpointType> assertionConsumerServices = spDescriptor.getAssertionConsumerService();
        Assert.assertThat(assertionConsumerServices, Matchers.hasSize(2));
        IndexedEndpointType assertionCS1 = assertionConsumerServices.get(0);
        Assert.assertThat(assertionCS1.getIndex(), Matchers.is(0));
        Assert.assertThat(assertionCS1.isIsDefault(), Matchers.is(true));
        Assert.assertThat(assertionCS1.getBinding(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact")));
        Assert.assertThat(assertionCS1.getLocation(), Matchers.is(URI.create("https://ServiceProvider.com/SAML/SSO/Artifact")));
        Assert.assertThat(assertionCS1.getResponseLocation(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(assertionCS1.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        IndexedEndpointType assertionCS2 = assertionConsumerServices.get(1);
        Assert.assertThat(assertionCS2.getIndex(), Matchers.is(1));
        Assert.assertThat(assertionCS2.isIsDefault(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(assertionCS2.getBinding(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST")));
        Assert.assertThat(assertionCS2.getLocation(), Matchers.is(URI.create("https://ServiceProvider.com/SAML/SSO/POST")));
        Assert.assertThat(assertionCS2.getResponseLocation(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(assertionCS2.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        // Attribute consuming services
        List<AttributeConsumingServiceType> attributeConsumingServices = spDescriptor.getAttributeConsumingService();
        Assert.assertThat(attributeConsumingServices, Matchers.hasSize(1));
        AttributeConsumingServiceType attributeConsumingService = attributeConsumingServices.get(0);
        Assert.assertThat(attributeConsumingService.getIndex(), Matchers.is(0));
        Assert.assertThat(attributeConsumingService.getServiceName(), Matchers.hasSize(1));
        LocalizedNameType servName = attributeConsumingService.getServiceName().get(0);
        Assert.assertThat(servName.getLang(), Matchers.is("en"));
        Assert.assertThat(servName.getValue(), Matchers.is("Academic Journals R US"));
        Assert.assertThat(attributeConsumingService.getServiceDescription(), Matchers.is(Matchers.emptyCollectionOf(LocalizedNameType.class)));
        List<RequestedAttributeType> requestedAttributes = attributeConsumingService.getRequestedAttribute();
        Assert.assertThat(requestedAttributes, Matchers.hasSize(1));
        // Requested attribute
        RequestedAttributeType requestedAttribute = requestedAttributes.get(0);
        Assert.assertThat(requestedAttribute.getNameFormat(), Matchers.is("urn:oasis:names:tc:SAML:2.0:attrname-format:uri"));
        Assert.assertThat(requestedAttribute.getName(), Matchers.is("urn:oid:1.3.6.1.4.1.5923.1.1.1.7"));
        Assert.assertThat(requestedAttribute.getFriendlyName(), Matchers.is("eduPersonEntitlement"));
        Assert.assertThat(requestedAttribute.getAttributeValue(), Matchers.hasSize(1));
        Assert.assertThat(((String) (requestedAttribute.getAttributeValue().get(0))), Matchers.is("https://ServiceProvider.com/entitlements/123456789"));
        Assert.assertThat(requestedAttribute.getOtherAttributes(), Matchers.is(Collections.<QName, String>emptyMap()));
        // Organization
        Assert.assertThat(entityDescriptor.getOrganization().getOrganizationName(), Matchers.hasSize(1));
        LocalizedNameType orgName = entityDescriptor.getOrganization().getOrganizationName().get(0);
        Assert.assertThat(orgName.getLang(), Matchers.is("en"));
        Assert.assertThat(orgName.getValue(), Matchers.is("Academic Journals R\n            US"));
        Assert.assertThat(entityDescriptor.getOrganization().getOrganizationDisplayName(), Matchers.hasSize(1));
        LocalizedNameType orgDispName = entityDescriptor.getOrganization().getOrganizationDisplayName().get(0);
        Assert.assertThat(orgDispName.getLang(), Matchers.is("en"));
        Assert.assertThat(orgDispName.getValue(), Matchers.is("Academic Journals R US, a Division of Dirk Corp."));
        Assert.assertThat(entityDescriptor.getOrganization().getOrganizationURL(), Matchers.hasSize(1));
        LocalizedURIType orgURL = entityDescriptor.getOrganization().getOrganizationURL().get(0);
        Assert.assertThat(orgURL.getLang(), Matchers.is("en"));
        Assert.assertThat(orgURL.getValue(), Matchers.is(URI.create("https://ServiceProvider.com")));
    }

    @Test
    public void testSaml20MetadataEntityDescriptorPDP() throws Exception {
        EntityDescriptorType descriptor = assertParsed("saml20-entity-descriptor-pdp.xml", EntityDescriptorType.class);
        Assert.assertThat(descriptor.getChoiceType(), Matchers.<EntityDescriptorType.EDTChoiceType>hasSize(1));
        Assert.assertThat(descriptor.getChoiceType().get(0).getDescriptors().get(0).getPdpDescriptor(), Matchers.is(Matchers.notNullValue()));
        PDPDescriptorType pdpDescriptor = descriptor.getChoiceType().get(0).getDescriptors().get(0).getPdpDescriptor();
        Assert.assertThat(pdpDescriptor.getKeyDescriptor(), Matchers.<KeyDescriptorType>hasSize(1));
        KeyDescriptorType keyDescriptorType = pdpDescriptor.getKeyDescriptor().get(0);
        Assert.assertThat(keyDescriptorType.getEncryptionMethod(), Matchers.<EncryptionMethodType>hasSize(1));
        EncryptionMethodType encryptionMethodType = keyDescriptorType.getEncryptionMethod().get(0);
        Assert.assertThat(encryptionMethodType.getAlgorithm(), Matchers.is("http://www.example.com/"));
        EncryptionMethodType.EncryptionMethod encryptionMethod = encryptionMethodType.getEncryptionMethod();
        Assert.assertThat(encryptionMethod.getKeySize(), Matchers.is(BigInteger.ONE));
        Assert.assertThat(encryptionMethod.getOAEPparams(), Matchers.is("GpM7".getBytes()));
        // EndpointType parser already tested so we are not checking further
        Assert.assertThat(pdpDescriptor.getAuthzService(), Matchers.<EndpointType>hasSize(1));
        Assert.assertThat(pdpDescriptor.getAssertionIDRequestService(), Matchers.<EndpointType>hasSize(1));
    }

    @Test
    public void testSaml20MetadataEntityDescriptorAuthnAuthority() throws Exception {
        EntityDescriptorType descriptor = assertParsed("saml20-entity-descriptor-authn-authority.xml", EntityDescriptorType.class);
        Assert.assertThat(descriptor.getChoiceType(), Matchers.<EntityDescriptorType.EDTChoiceType>hasSize(1));
        Assert.assertThat(descriptor.getChoiceType().get(0).getDescriptors().get(0).getAuthnDescriptor(), Matchers.is(Matchers.notNullValue()));
        AuthnAuthorityDescriptorType authnDescriptor = descriptor.getChoiceType().get(0).getDescriptors().get(0).getAuthnDescriptor();
        Assert.assertThat(authnDescriptor.getAssertionIDRequestService(), Matchers.hasSize(1));
        Assert.assertThat(authnDescriptor.getAuthnQueryService(), Matchers.hasSize(1));
        Assert.assertThat(authnDescriptor.getProtocolSupportEnumeration(), Matchers.containsInAnyOrder("http://www.example.com/", "http://www.example2.com/"));
    }

    @Test
    public void testSaml20MetadataEntitiesDescriptor() throws Exception {
        EntitiesDescriptorType entities = assertParsed("saml20-entities-descriptor.xml", EntitiesDescriptorType.class);
        Assert.assertThat(entities.getName(), Matchers.is("https://your-federation.org/metadata/federation-name.xml"));
        Assert.assertThat(entities.getID(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(entities.getCacheDuration(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(entities.getExtensions(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(entities.getSignature(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(entities.getValidUntil(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(entities.getEntityDescriptor(), Matchers.hasSize(3));
        Assert.assertThat(entities.getEntityDescriptor().get(0), Matchers.instanceOf(EntityDescriptorType.class));
        Assert.assertThat(entities.getEntityDescriptor().get(1), Matchers.instanceOf(EntityDescriptorType.class));
        Assert.assertThat(entities.getEntityDescriptor().get(2), Matchers.instanceOf(EntitiesDescriptorType.class));
        EntitiesDescriptorType nestedEntities = ((EntitiesDescriptorType) (entities.getEntityDescriptor().get(2)));
        Assert.assertThat(nestedEntities.getEntityDescriptor(), Matchers.hasSize(2));
    }

    @Test
    public void testSaml20MetadataEntityDescriptorAdfsIdP() throws Exception {
        assertParsed("KEYCLOAK-4809-IdPMetadata_test.xml", EntityDescriptorType.class);
    }

    @Test
    public void testAttributeProfileMetadata() throws Exception {
        assertParsed("KEYCLOAK-4236-AttributeProfile-element.xml", EntityDescriptorType.class);
    }

    @Test
    public void testEmptyAttributeValue() throws Exception {
        ResponseType resp = assertParsed("KEYCLOAK-4790-Empty-attribute-value.xml", ResponseType.class);
        Assert.assertThat(resp.getAssertions(), Matchers.hasSize(1));
        final AssertionType a = resp.getAssertions().get(0).getAssertion();
        Assert.assertThat(a, Matchers.notNullValue());
        Assert.assertThat(a.getAttributeStatements(), Matchers.hasSize(1));
        final List<ASTChoiceType> attributes = a.getAttributeStatements().iterator().next().getAttributes();
        Assert.assertThat(attributes, Matchers.hasSize(3));
        Assert.assertThat(attributes, Matchers.everyItem(Matchers.notNullValue(ASTChoiceType.class)));
        final AttributeType attr0 = attributes.get(0).getAttribute();
        final AttributeType attr1 = attributes.get(1).getAttribute();
        final AttributeType attr2 = attributes.get(2).getAttribute();
        Assert.assertThat(attr0.getName(), Matchers.is("urn:oid:0.9.2342.19200300.100.1.2"));
        Assert.assertThat(attr0.getAttributeValue(), Matchers.hasSize(1));
        Assert.assertThat(attr0.getAttributeValue().get(0), Matchers.instanceOf(String.class));
        Assert.assertThat(((String) (attr0.getAttributeValue().get(0))), Matchers.is(""));
        Assert.assertThat(attr1.getName(), Matchers.is("urn:oid:0.9.2342.19200300.100.1.3"));
        Assert.assertThat(attr1.getAttributeValue(), Matchers.hasSize(1));
        Assert.assertThat(attr1.getAttributeValue().get(0), Matchers.instanceOf(String.class));
        Assert.assertThat(((String) (attr1.getAttributeValue().get(0))), Matchers.is("aa"));
        Assert.assertThat(attr2.getName(), Matchers.is("urn:oid:0.9.2342.19200300.100.1.4"));
        Assert.assertThat(attr2.getAttributeValue(), Matchers.hasSize(1));
        Assert.assertThat(attr2.getAttributeValue().get(0), Matchers.instanceOf(String.class));
        Assert.assertThat(((String) (attr2.getAttributeValue().get(0))), Matchers.is(""));
    }

    @Test
    public void testEmptyAttributeValueLast() throws Exception {
        assertParsed("KEYCLOAK-4790-Empty-attribute-value-last.xml", ResponseType.class);
    }

    @Test
    public void testAuthnRequest() throws Exception {
        AuthnRequestType req = assertParsed("saml20-authnrequest.xml", AuthnRequestType.class);
        Assert.assertThat(req.getRequestedAuthnContext(), Matchers.notNullValue());
        Assert.assertThat(req.getRequestedAuthnContext().getAuthnContextClassRef(), Matchers.hasItem(Matchers.is("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport")));
        Assert.assertThat(req.getRequestedAuthnContext().getAuthnContextDeclRef(), Matchers.hasItem(Matchers.is("urn:kc:SAML:2.0:ac:ref:demo:decl")));
    }

    // https://issues.jboss.org/browse/KEYCLOAK-7316
    @Test
    public void testAuthnRequestOptionalIsPassive() throws Exception {
        AuthnRequestType req = assertParsed("KEYCLOAK-7316-noAtrributes.xml", AuthnRequestType.class);
        Assert.assertThat("Not null!", req.isIsPassive(), Matchers.nullValue());
        Assert.assertThat("Not null!", req.isForceAuthn(), Matchers.nullValue());
        req = assertParsed("KEYCLOAK-7316-withTrueAttributes.xml", AuthnRequestType.class);
        Assert.assertThat(req.isIsPassive(), Matchers.notNullValue());
        Assert.assertTrue("Wrong value!", req.isIsPassive().booleanValue());
        Assert.assertThat(req.isForceAuthn(), Matchers.notNullValue());
        Assert.assertTrue("Wrong value!", req.isForceAuthn().booleanValue());
        req = assertParsed("KEYCLOAK-7316-withFalseAttributes.xml", AuthnRequestType.class);
        Assert.assertThat(req.isIsPassive(), Matchers.notNullValue());
        Assert.assertFalse("Wrong value!", req.isIsPassive().booleanValue());
        Assert.assertThat(req.isForceAuthn(), Matchers.notNullValue());
        Assert.assertFalse("Wrong value!", req.isForceAuthn().booleanValue());
    }

    @Test
    public void testAuthnRequestInvalidPerXsdWithValidationDisabled() throws Exception {
        AuthnRequestType req = assertParsed("saml20-authnrequest-invalid-per-xsd.xml", AuthnRequestType.class);
    }

    @Test
    public void testAuthnRequestInvalidPerXsdWithValidationEnabled() throws Exception {
        try {
            thrown.expect(ProcessingException.class);
            System.setProperty("picketlink.schema.validate", "true");
            AuthnRequestType req = assertParsed("saml20-authnrequest-invalid-per-xsd.xml", AuthnRequestType.class);
        } finally {
            System.clearProperty("picketlink.schema.validate");
        }
    }

    @Test
    public void testAuthnRequestInvalidNamespace() throws Exception {
        thrown.expect(ParsingException.class);
        thrown.expectMessage(Matchers.containsString("Unknown Start Element"));
        assertParsed("saml20-authnrequest-invalid-namespace.xml", AuthnRequestType.class);
    }

    @Test
    public void testInvalidEndElement() throws Exception {
        thrown.expect(ParsingException.class);
        // see KEYCLOAK-7444
        thrown.expectMessage(Matchers.containsString("NameIDFormat"));
        assertParsed("saml20-entity-descriptor-idp-invalid-end-element.xml", EntityDescriptorType.class);
    }

    @Test
    public void testMissingRequiredAttributeIDPSSODescriptorType() throws Exception {
        testMissingAttribute("IDPSSODescriptorType", "protocolSupportEnumeration");
    }

    @Test
    public void testMissingRequiredAttributeSPSSODescriptorType() throws Exception {
        testMissingAttribute("SPSSODescriptorType", "protocolSupportEnumeration");
    }

    @Test
    public void testMissingRequiredAttributeAttributeAuthorityDescriptorType() throws Exception {
        testMissingAttribute("AttributeAuthorityDescriptorType", "protocolSupportEnumeration");
    }

    @Test
    public void testMissingRequiredAttributeAuthnAuthorityDescriptorType() throws Exception {
        testMissingAttribute("AuthnAuthorityDescriptorType", "protocolSupportEnumeration");
    }

    @Test
    public void testMissingRequiredAttributePDPDescriptorType() throws Exception {
        testMissingAttribute("PDPDescriptorType", "protocolSupportEnumeration");
    }

    @Test
    public void testMissingRequiredAttributeAttributeConsumingServiceType() throws Exception {
        testMissingAttribute("AttributeConsumingServiceType", "index");
    }

    @Test
    public void testMissingRequiredAttributeAttributeType() throws Exception {
        testMissingAttribute("AttributeType", "Name");
    }

    @Test
    public void testMissingRequiredAttributeContactType() throws Exception {
        testMissingAttribute("ContactType", "contactType");
    }

    @Test
    public void testMissingRequiredAttributeEncryptionMethodType() throws Exception {
        testMissingAttribute("EncryptionMethodType", "Algorithm");
    }

    @Test
    public void testMissingRequiredAttributeEndpointTypeBinding() throws Exception {
        testMissingAttribute("EndpointType", "Binding");
    }

    @Test
    public void testMissingRequiredAttributeEndpointTypeLocation() throws Exception {
        testMissingAttribute("EndpointType", "Location");
    }

    @Test
    public void testMissingRequiredAttributeEntityDescriptorType() throws Exception {
        testMissingAttribute("EntityDescriptorType", "entityID");
    }

    @Test
    public void testMissingRequiredAttributeRequestedAttributeType() throws Exception {
        testMissingAttribute("RequestedAttributeType", "Name");
    }

    @Test
    public void testAuthnRequestScoping() throws Exception {
        assertParsed("KEYCLOAK-6109-authnrequest-scoping.xml", AuthnRequestType.class);
    }

    @Test
    public void testLogoutResponseStatusDetail() throws Exception {
        StatusResponseType resp = assertParsed("saml20-logout-response-status-detail.xml", StatusResponseType.class);
        Assert.assertThat(resp.getIssuer(), Matchers.notNullValue());
        Assert.assertThat(resp.getIssuer().getValue(), Matchers.is("http://idp.example.com/metadata.php"));
        Assert.assertThat(resp.getIssuer().getFormat(), Matchers.is(NAMEID_FORMAT_ENTITY.getUri()));
        Assert.assertThat(resp.getStatus(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusDetail(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusDetail().getAny(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusDetail().getAny().size(), Matchers.is(2));
        Assert.assertThat(resp.getStatus().getStatusCode(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusCode().getValue(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:status:Responder")));
        Assert.assertThat(resp.getStatus().getStatusCode().getStatusCode(), Matchers.nullValue());
    }

    @Test
    public void testLogoutResponseSimpleStatus() throws Exception {
        StatusResponseType resp = assertParsed("saml20-logout-response-status.xml", StatusResponseType.class);
        Assert.assertThat(resp.getStatus(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusMessage(), Matchers.is("Status Message"));
        Assert.assertThat(resp.getStatus().getStatusCode(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusCode().getValue(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:status:Responder")));
        Assert.assertThat(resp.getStatus().getStatusCode().getStatusCode(), Matchers.nullValue());
    }

    @Test
    public void testLogoutResponseNestedStatus() throws Exception {
        StatusResponseType resp = assertParsed("saml20-logout-response-nested-status.xml", StatusResponseType.class);
        Assert.assertThat(resp.getStatus(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusCode(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusCode().getValue(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:status:Responder")));
        Assert.assertThat(resp.getStatus().getStatusCode().getStatusCode(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusCode().getStatusCode().getValue(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:status:AuthnFailed")));
        Assert.assertThat(resp.getStatus().getStatusCode().getStatusCode().getStatusCode(), Matchers.nullValue());
    }

    @Test
    public void testLogoutResponseDeepNestedStatus() throws Exception {
        StatusResponseType resp = assertParsed("saml20-logout-response-nested-status-deep.xml", StatusResponseType.class);
        Assert.assertThat(resp.getStatus(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusDetail(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusDetail().getAny(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusDetail().getAny().size(), Matchers.is(2));
        Assert.assertThat(resp.getStatus().getStatusCode(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusCode().getValue(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:status:Responder")));
        Assert.assertThat(resp.getStatus().getStatusCode().getStatusCode(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusCode().getStatusCode().getValue(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:status:AuthnFailed")));
        Assert.assertThat(resp.getStatus().getStatusCode().getStatusCode().getStatusCode(), Matchers.notNullValue());
        Assert.assertThat(resp.getStatus().getStatusCode().getStatusCode().getStatusCode().getValue(), Matchers.is(URI.create("urn:oasis:names:tc:SAML:2.0:status:VersionMismatch")));
    }

    @Test
    public void testSaml20AssertionContents() throws Exception {
        AssertionType a = assertParsed("saml20-assertion-example.xml", AssertionType.class);
        Assert.assertThat(a.getSubject().getConfirmation(), Matchers.hasSize(1));
        Assert.assertThat(a.getSubject().getConfirmation().get(0).getSubjectConfirmationData(), Matchers.notNullValue());
        Assert.assertThat(a.getSubject().getConfirmation().get(0).getSubjectConfirmationData().getAnyType(), Matchers.instanceOf(KeyInfoType.class));
        KeyInfoType kit = ((KeyInfoType) (a.getSubject().getConfirmation().get(0).getSubjectConfirmationData().getAnyType()));
        Assert.assertThat(kit.getContent(), Matchers.hasItem(Matchers.instanceOf(RSAKeyValueType.class)));
        RSAKeyValueType rsaKit = ((RSAKeyValueType) (kit.getContent().get(0)));
        Assert.assertThat(rsaKit.getModulus(), Matchers.notNullValue());
        Assert.assertThat(rsaKit.getExponent(), Matchers.notNullValue());
        Assert.assertThat(a.getStatements(), Matchers.containsInAnyOrder(Matchers.instanceOf(AuthnStatementType.class), Matchers.instanceOf(AttributeStatementType.class)));
        for (StatementAbstractType statement : a.getStatements()) {
            if (statement instanceof AuthnStatementType) {
                AuthnStatementType as = ((AuthnStatementType) (statement));
                Assert.assertThat(as.getSessionNotOnOrAfter(), Matchers.notNullValue());
                Assert.assertThat(as.getSessionNotOnOrAfter(), Matchers.is(XMLTimeUtil.parse("2009-06-17T18:55:10.738Z")));
                final AuthnContextType ac = as.getAuthnContext();
                Assert.assertThat(ac, Matchers.notNullValue());
                Assert.assertThat(ac.getSequence(), Matchers.notNullValue());
                Assert.assertThat(ac.getSequence().getClassRef().getValue(), Matchers.is(AC_UNSPECIFIED.getUri()));
                Assert.assertThat(ac.getSequence(), Matchers.notNullValue());
                Assert.assertThat(ac.getSequence().getAuthnContextDecl(), Matchers.nullValue());
            }
        }
    }

    @Test
    public void testSaml20AssertionDsaKey() throws Exception {
        AssertionType a = assertParsed("saml20-assertion-dsakey.xml", AssertionType.class);
        Assert.assertThat(a.getSubject().getConfirmation(), Matchers.hasSize(1));
        Assert.assertThat(a.getSubject().getConfirmation().get(0).getSubjectConfirmationData(), Matchers.notNullValue());
        Assert.assertThat(a.getSubject().getConfirmation().get(0).getSubjectConfirmationData().getAnyType(), Matchers.instanceOf(KeyInfoType.class));
        KeyInfoType kit = ((KeyInfoType) (a.getSubject().getConfirmation().get(0).getSubjectConfirmationData().getAnyType()));
        Assert.assertThat(kit.getContent(), Matchers.hasItem(Matchers.instanceOf(DSAKeyValueType.class)));
        DSAKeyValueType rsaKit = ((DSAKeyValueType) (kit.getContent().get(0)));
        Assert.assertThat(rsaKit.getG(), Matchers.notNullValue());
        Assert.assertThat(rsaKit.getJ(), Matchers.nullValue());
        Assert.assertThat(rsaKit.getP(), Matchers.notNullValue());
        Assert.assertThat(rsaKit.getQ(), Matchers.notNullValue());
        Assert.assertThat(rsaKit.getY(), Matchers.notNullValue());
    }

    @Test
    public void testSaml20AssertionsAnyTypeAttributeValue() throws Exception {
        AssertionType assertion = assertParsed("saml20-assertion-anytype-attribute-value.xml", AssertionType.class);
        AttributeStatementType attributeStatementType = assertion.getAttributeStatements().iterator().next();
        Assert.assertThat(attributeStatementType.getAttributes(), Matchers.hasSize(5));
        for (AttributeStatementType.ASTChoiceType choiceType : attributeStatementType.getAttributes()) {
            AttributeType attr = choiceType.getAttribute();
            String attrName = attr.getName();
            Object value = attr.getAttributeValue().get(0);
            // test selected attributes
            switch (attrName) {
                case "attr:type:string" :
                    Assert.assertThat(value, Matchers.is(((Object) ("CITIZEN"))));
                    break;
                case "attr:notype:string" :
                    Assert.assertThat(value, Matchers.instanceOf(String.class));
                    Assert.assertThat(value, Matchers.is(((Object) ("CITIZEN"))));
                    break;
                case "attr:notype:element" :
                    Assert.assertThat(value, Matchers.instanceOf(String.class));
                    Assert.assertThat(((String) (value)), Matchers.containsString("hospitaal x"));
                    value = attr.getAttributeValue().get(1);
                    Assert.assertThat(value, Matchers.instanceOf(String.class));
                    Assert.assertThat(((String) (value)), Matchers.containsString("hopital x"));
                    break;
                case "founded" :
                    Assert.assertThat(value, Matchers.is(((Object) (XMLTimeUtil.parse("2002-05-30T09:30:10-06:00")))));
                    break;
                case "expanded" :
                    Assert.assertThat(value, Matchers.is(((Object) (XMLTimeUtil.parse("2002-06-30")))));
                    break;
                default :
                    break;
            }
        }
    }

    @Test
    public void testSaml20AssertionExample() throws Exception {
        AssertionType assertion = assertParsed("saml20-assertion-example.xml", AssertionType.class);
        AttributeStatementType attributeStatementType = assertion.getAttributeStatements().iterator().next();
        Assert.assertThat(attributeStatementType.getAttributes(), Matchers.hasSize(9));
        for (AttributeStatementType.ASTChoiceType choiceType : attributeStatementType.getAttributes()) {
            AttributeType attr = choiceType.getAttribute();
            String attrName = attr.getName();
            Object value = attr.getAttributeValue().get(0);
            // test selected attributes
            switch (attrName) {
                case "portal_id" :
                    Assert.assertEquals(value, "060D00000000SHZ");
                    break;
                case "organization_id" :
                    Assert.assertThat(value, Matchers.instanceOf(String.class));
                    Assert.assertThat(((String) (value)), Matchers.containsString("<n3:stuff xmlns:n3=\"ftp://example.org\">00DD0000000F7L5</n3:stuff>"));
                    break;
                case "has_sub_organization" :
                    Assert.assertThat(value, Matchers.is(((Object) ("true"))));
                    break;
                case "anytype_test" :
                    Assert.assertThat(value, Matchers.instanceOf(String.class));
                    Assert.assertThat(((String) (value)), Matchers.containsString("<elem2>val2</elem2>"));
                    break;
                case "anytype_no_xml_test" :
                    Assert.assertThat(value, Matchers.is(((Object) ("value_no_xml"))));
                    break;
                case "logouturl" :
                    Assert.assertThat(value, Matchers.is(((Object) ("http://www.salesforce.com/security/del_auth/SsoLogoutPage.html"))));
                    break;
                case "nil_value_attribute" :
                    Assert.assertNull(value);
                    break;
                case "status" :
                    Assert.assertThat(value, Matchers.is(((Object) ("<status><code><status>XYZ</status></code></status>"))));
                    break;
                default :
                    break;
            }
        }
    }

    @Test(expected = ParsingException.class)
    public void testSaml20AssertionsNil1() throws Exception {
        try (InputStream st = SAMLParserTest.class.getResourceAsStream("saml20-assertion-nil-wrong-1.xml")) {
            parser.parse(st);
        }
    }

    @Test(expected = ParsingException.class)
    public void testSaml20AssertionsNil2() throws Exception {
        try (InputStream st = SAMLParserTest.class.getResourceAsStream("saml20-assertion-nil-wrong-2.xml")) {
            parser.parse(st);
        }
    }

    @Test
    public void testSaml20AssertionsMissingId() throws Exception {
        try (InputStream st = removeAttribute("saml20-assertion-example.xml", "ID")) {
            thrown.expect(ParsingException.class);
            thrown.expectMessage(Matchers.endsWith("Required attribute missing: ID"));
            parser.parse(st);
        }
    }

    @Test
    public void testSaml20AssertionsMissingVersion() throws Exception {
        try (InputStream st = removeAttribute("saml20-assertion-example.xml", "Version")) {
            thrown.expect(ParsingException.class);
            thrown.expectMessage(Matchers.endsWith("Required attribute missing: Version"));
            parser.parse(st);
        }
    }

    @Test
    public void testSaml20AssertionsWrongVersion() throws Exception {
        try (InputStream st = updateAttribute("saml20-assertion-example.xml", "Version", "1.1")) {
            thrown.expect(ParsingException.class);
            thrown.expectMessage(Matchers.endsWith("Assertion Version required to be \"2.0\""));
            parser.parse(st);
        }
    }

    @Test
    public void testSaml20AssertionsMissingIssueInstance() throws Exception {
        try (InputStream st = removeAttribute("saml20-assertion-example.xml", "IssueInstant")) {
            thrown.expect(ParsingException.class);
            thrown.expectMessage(Matchers.endsWith("Required attribute missing: IssueInstant"));
            parser.parse(st);
        }
    }

    @Test
    public void testSaml20AssertionsAdviceTag() throws Exception {
        Matcher<String>[] ATTR_NAME = new Matcher[]{ Matchers.is("portal_id"), Matchers.is("organization_id"), Matchers.is("status"), Matchers.is("has_sub_organization"), Matchers.is("anytype_test"), Matchers.is("anytype_no_xml_test"), Matchers.is("ssostartpage"), Matchers.is("logouturl"), Matchers.is("nil_value_attribute") };
        Matcher<List<Object>>[] ATTR_VALUE = new Matcher[]{ Matchers.contains(Matchers.is("060D00000000SHZ")), Matchers.contains(Matchers.is("<n1:elem2 xmlns:n1=\"http://example.net\" xml:lang=\"en\"><n3:stuff xmlns:n3=\"ftp://example.org\">00DD0000000F7L5</n3:stuff></n1:elem2>")), Matchers.contains(Matchers.is("<status><code><status>XYZ</status></code></status>")), Matchers.contains(Matchers.is("true")), Matchers.contains(Matchers.is("<elem1 atttr1=\"en\"><elem2>val2</elem2></elem1>")), Matchers.contains(Matchers.is("value_no_xml")), Matchers.contains(Matchers.is("http://www.salesforce.com/security/saml/saml20-gen.jsp")), Matchers.contains(Matchers.is("http://www.salesforce.com/security/del_auth/SsoLogoutPage.html")), Matchers.contains(Matchers.nullValue()) };
        AssertionType a = assertParsed("saml20-assertion-advice.xml", AssertionType.class);
        Assert.assertThat(a.getStatements(), Matchers.containsInAnyOrder(Matchers.instanceOf(AuthnStatementType.class), Matchers.instanceOf(AttributeStatementType.class)));
        for (StatementAbstractType statement : a.getStatements()) {
            if (statement instanceof AuthnStatementType) {
                AuthnStatementType as = ((AuthnStatementType) (statement));
                final AuthnContextType ac = as.getAuthnContext();
                Assert.assertThat(ac, Matchers.notNullValue());
                Assert.assertThat(ac.getSequence(), Matchers.notNullValue());
                Assert.assertThat(ac.getSequence().getClassRef().getValue(), Matchers.is(AC_UNSPECIFIED.getUri()));
                Assert.assertThat(ac.getSequence(), Matchers.notNullValue());
                Assert.assertThat(ac.getSequence().getAuthnContextDecl(), Matchers.notNullValue());
                Assert.assertThat(ac.getSequence().getAuthnContextDecl().getValue(), Matchers.instanceOf(Element.class));
                final Element el = ((Element) (ac.getSequence().getAuthnContextDecl().getValue()));
                Assert.assertThat(el.getTextContent(), Matchers.is("auth.weak"));
            } else {
                AttributeStatementType as = ((AttributeStatementType) (statement));
                Assert.assertThat(as.getAttributes(), Matchers.hasSize(9));
                for (int i = 0; i < (as.getAttributes().size()); i++) {
                    AttributeType attr = as.getAttributes().get(i).getAttribute();
                    Assert.assertThat(attr.getName(), ATTR_NAME[i]);
                    Assert.assertThat(attr.getAttributeValue(), ATTR_VALUE[i]);
                }
            }
        }
        Assert.assertThat(a.getConditions().getConditions(), Matchers.contains(Matchers.instanceOf(AudienceRestrictionType.class)));
    }
}

