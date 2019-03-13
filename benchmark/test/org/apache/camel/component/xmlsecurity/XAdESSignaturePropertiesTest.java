/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.xmlsecurity;


import XAdESSignatureProperties.HTTP_URI_ETSI_ORG_01903_V1_1_1;
import XAdESSignatureProperties.HTTP_URI_ETSI_ORG_01903_V1_2_2;
import XAdESSignatureProperties.SIG_POLICY_IMPLIED;
import XAdESSignatureProperties.SIG_POLICY_NONE;
import XmlSignatureConstants.HEADER_XADES_DATA_OBJECT_FORMAT_ENCODING;
import XmlSignatureConstants.HEADER_XADES_NAMESPACE;
import XmlSignatureConstants.HEADER_XADES_PREFIX;
import XmlSignatureConstants.HEADER_XADES_QUALIFYING_PROPERTIES_ID;
import XmlSignatureConstants.HEADER_XADES_SIGNED_DATA_OBJECT_PROPERTIES_ID;
import XmlSignatureConstants.HEADER_XADES_SIGNED_SIGNATURE_PROPERTIES_ID;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.xmlsecurity.api.XAdESEncapsulatedPKIData;
import org.apache.camel.component.xmlsecurity.api.XAdESSignatureProperties;
import org.apache.camel.component.xmlsecurity.api.XmlSignatureException;
import org.apache.camel.component.xmlsecurity.util.TestKeystore;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class XAdESSignaturePropertiesTest extends CamelTestSupport {
    private static final String NOT_EMPTY = "NOT_EMPTY";

    private static String payload;

    static {
        boolean includeNewLine = true;
        if ((TestSupport.getJavaMajorVersion()) >= 9) {
            includeNewLine = false;
        }
        XAdESSignaturePropertiesTest.payload = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (includeNewLine ? "\n" : "")) + "<root xmlns=\"http://test/test\"><test>Test Message</test></root>";
    }

    @Test
    public void envelopingAllParameters() throws Exception {
        Document doc = testEnveloping();
        Map<String, String> prefix2Namespace = XAdESSignaturePropertiesTest.getPrefix2NamespaceMap();
        String pathToSignatureProperties = getPathToSignatureProperties();
        // signing time
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SigningTime/text()"), prefix2Namespace, XAdESSignaturePropertiesTest.NOT_EMPTY);
        // signing certificate
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SigningCertificate/etsi:Cert/etsi:CertDigest/ds:DigestMethod/@Algorithm"), prefix2Namespace, DigestMethod.SHA256);
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SigningCertificate/etsi:Cert/etsi:CertDigest/ds:DigestValue/text()"), prefix2Namespace, XAdESSignaturePropertiesTest.NOT_EMPTY);
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SigningCertificate/etsi:Cert/etsi:IssuerSerial/ds:X509IssuerName/text()"), prefix2Namespace, XAdESSignaturePropertiesTest.NOT_EMPTY);
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SigningCertificate/etsi:Cert/etsi:IssuerSerial/ds:X509SerialNumber/text()"), prefix2Namespace, XAdESSignaturePropertiesTest.NOT_EMPTY);
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SigningCertificate/etsi:Cert/@URI"), prefix2Namespace, "http://certuri");
        // signature policy
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignaturePolicyIdentifier/etsi:SignaturePolicyId/etsi:SigPolicyId/etsi:Identifier/text()"), prefix2Namespace, "1.2.840.113549.1.9.16.6.1");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignaturePolicyIdentifier/etsi:SignaturePolicyId/etsi:SigPolicyId/etsi:Identifier/@Qualifier"), prefix2Namespace, "OIDAsURN");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignaturePolicyIdentifier/etsi:SignaturePolicyId/etsi:SigPolicyId/etsi:Description/text()"), prefix2Namespace, "invoice version 3.1");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignaturePolicyIdentifier/etsi:SignaturePolicyId/etsi:SigPolicyHash/ds:DigestMethod/@Algorithm"), prefix2Namespace, DigestMethod.SHA256);
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignaturePolicyIdentifier/etsi:SignaturePolicyId/etsi:SigPolicyHash/ds:DigestValue/text()"), prefix2Namespace, "Ohixl6upD6av8N7pEvDABhEL6hM=");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignaturePolicyIdentifier/etsi:SignaturePolicyId/etsi:SigPolicyQualifiers/etsi:SigPolicyQualifier[1]/etsi:SPURI/text()"), prefix2Namespace, "http://test.com/sig.policy.pdf");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignaturePolicyIdentifier/etsi:SignaturePolicyId/etsi:SigPolicyQualifiers/etsi:SigPolicyQualifier[1]/etsi:SPUserNotice/etsi:ExplicitText/text()"), prefix2Namespace, "display text");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignaturePolicyIdentifier/etsi:SignaturePolicyId/etsi:SigPolicyQualifiers/etsi:SigPolicyQualifier[2]/text()"), prefix2Namespace, "category B");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignaturePolicyIdentifier/etsi:SignaturePolicyId/etsi:SigPolicyId/etsi:DocumentationReferences/etsi:DocumentationReference[1]/text()"), prefix2Namespace, "http://test.com/policy.doc.ref1.txt");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignaturePolicyIdentifier/etsi:SignaturePolicyId/etsi:SigPolicyId/etsi:DocumentationReferences/etsi:DocumentationReference[2]/text()"), prefix2Namespace, "http://test.com/policy.doc.ref2.txt");
        // production place
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignatureProductionPlace/etsi:City/text()"), prefix2Namespace, "Munich");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignatureProductionPlace/etsi:StateOrProvince/text()"), prefix2Namespace, "Bavaria");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignatureProductionPlace/etsi:PostalCode/text()"), prefix2Namespace, "80331");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignatureProductionPlace/etsi:CountryName/text()"), prefix2Namespace, "Germany");
        // signer role
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignerRole/etsi:ClaimedRoles/etsi:ClaimedRole[1]/text()"), prefix2Namespace, "test");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignerRole/etsi:ClaimedRoles/etsi:ClaimedRole[2]/TestRole/text()"), prefix2Namespace, "TestRole");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignerRole/etsi:CertifiedRoles/etsi:CertifiedRole/text()"), prefix2Namespace, "Ahixl6upD6av8N7pEvDABhEL6hM=");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignerRole/etsi:CertifiedRoles/etsi:CertifiedRole/@Encoding"), prefix2Namespace, "http://uri.etsi.org/01903/v1.2.2#DER");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SignerRole/etsi:CertifiedRoles/etsi:CertifiedRole/@Id"), prefix2Namespace, "IdCertifiedRole");
        String pathToDataObjectProperties = "/ds:Signature/ds:Object/etsi:QualifyingProperties/etsi:SignedProperties/etsi:SignedDataObjectProperties/";
        // DataObjectFormat
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:DataObjectFormat/etsi:Description/text()"), prefix2Namespace, "invoice");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:DataObjectFormat/etsi:MimeType/text()"), prefix2Namespace, "text/xml");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:DataObjectFormat/@ObjectReference"), prefix2Namespace, "#", true);
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:DataObjectFormat/etsi:ObjectIdentifier/etsi:Identifier/text()"), prefix2Namespace, "1.2.840.113549.1.9.16.6.2");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:DataObjectFormat/etsi:ObjectIdentifier/etsi:Identifier/@Qualifier"), prefix2Namespace, "OIDAsURN");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:DataObjectFormat/etsi:ObjectIdentifier/etsi:Description/text()"), prefix2Namespace, "identifier desc");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:DataObjectFormat/etsi:ObjectIdentifier/etsi:DocumentationReferences/etsi:DocumentationReference[1]/text()"), prefix2Namespace, "http://test.com/dataobject.format.doc.ref1.txt");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:DataObjectFormat/etsi:ObjectIdentifier/etsi:DocumentationReferences/etsi:DocumentationReference[2]/text()"), prefix2Namespace, "http://test.com/dataobject.format.doc.ref2.txt");
        // commitment
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:CommitmentTypeIndication/etsi:CommitmentTypeId/etsi:Identifier/text()"), prefix2Namespace, "1.2.840.113549.1.9.16.6.4");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:CommitmentTypeIndication/etsi:CommitmentTypeId/etsi:Identifier/@Qualifier"), prefix2Namespace, "OIDAsURI");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:CommitmentTypeIndication/etsi:CommitmentTypeId/etsi:Description/text()"), prefix2Namespace, "description for commitment type ID");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:CommitmentTypeIndication/etsi:CommitmentTypeId/etsi:DocumentationReferences/etsi:DocumentationReference[1]/text()"), prefix2Namespace, "http://test.com/commitment.ref1.txt");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:CommitmentTypeIndication/etsi:CommitmentTypeId/etsi:DocumentationReferences/etsi:DocumentationReference[2]/text()"), prefix2Namespace, "http://test.com/commitment.ref2.txt");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:CommitmentTypeIndication/etsi:CommitmentTypeQualifiers/etsi:CommitmentTypeQualifier[1]/text()"), prefix2Namespace, "commitment qualifier");
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToDataObjectProperties + "etsi:CommitmentTypeIndication/etsi:CommitmentTypeQualifiers/etsi:CommitmentTypeQualifier[2]/C/text()"), prefix2Namespace, "c");
    }

    @Test
    public void noSigningTime() throws Exception {
        XmlSignerEndpoint endpoint = getSignerEndpoint();
        XAdESSignatureProperties props = ((XAdESSignatureProperties) (endpoint.getProperties()));
        props.setAddSigningTime(false);
        Document doc = testEnveloping();
        Map<String, String> prefix2Namespace = XAdESSignaturePropertiesTest.getPrefix2NamespaceMap();
        String pathToSignatureProperties = getPathToSignatureProperties();
        checkNode(doc, (pathToSignatureProperties + "etsi:SigningTime"), prefix2Namespace, false);
    }

    @Test
    public void noSigningCertificate() throws Exception {
        XmlSignerEndpoint endpoint = getSignerEndpoint();
        XAdESSignatureProperties newProps = new XAdESSignatureProperties();
        newProps.setAddSigningTime(true);
        endpoint.setProperties(newProps);
        Document doc = testEnveloping();
        Map<String, String> prefix2Namespace = XAdESSignaturePropertiesTest.getPrefix2NamespaceMap();
        String pathToSignatureProperties = getPathToSignatureProperties();
        checkNode(doc, (pathToSignatureProperties + "etsi:SigningTime"), prefix2Namespace, true);
        checkNode(doc, (pathToSignatureProperties + "etsi:SigningCertificate"), prefix2Namespace, false);
    }

    @Test
    public void certificateChain() throws Exception {
        XmlSignerEndpoint endpoint = getSignerEndpoint();
        endpoint.setProperties(new XAdESSignaturePropertiesTest.CertChainXAdESSignatureProperties());
        Document doc = testEnveloping();
        Map<String, String> prefix2Namespace = XAdESSignaturePropertiesTest.getPrefix2NamespaceMap();
        String pathToSignatureProperties = getPathToSignatureProperties();
        // signing certificate
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SigningCertificate/etsi:Cert/etsi:CertDigest/ds:DigestMethod/@Algorithm"), prefix2Namespace, DigestMethod.SHA256);
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SigningCertificate/etsi:Cert/etsi:CertDigest/ds:DigestValue/text()"), prefix2Namespace, XAdESSignaturePropertiesTest.NOT_EMPTY);
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SigningCertificate/etsi:Cert/etsi:IssuerSerial/ds:X509IssuerName/text()"), prefix2Namespace, XAdESSignaturePropertiesTest.NOT_EMPTY);
        XAdESSignaturePropertiesTest.checkXpath(doc, (pathToSignatureProperties + "etsi:SigningCertificate/etsi:Cert/etsi:IssuerSerial/ds:X509SerialNumber/text()"), prefix2Namespace, XAdESSignaturePropertiesTest.NOT_EMPTY);
    }

    @Test
    public void noPropertiesSpecified() throws Exception {
        XmlSignerEndpoint endpoint = getSignerEndpoint();
        XAdESSignatureProperties props = new XAdESSignatureProperties();
        props.setAddSigningTime(false);
        endpoint.setProperties(props);
        Document doc = testEnveloping();
        // expecting no Qualifying Properties
        checkNode(doc, "/ds:Signature/ds:Object/etsi:QualifyingProperties", XAdESSignaturePropertiesTest.getPrefix2NamespaceMap(), false);
    }

    @Test
    public void policyImplied() throws Exception {
        XmlSignerEndpoint endpoint = getSignerEndpoint();
        XAdESSignatureProperties props = ((XAdESSignatureProperties) (endpoint.getProperties()));
        props.setSignaturePolicy(SIG_POLICY_IMPLIED);
        Document doc = testEnveloping();
        String pathToSignatureProperties = getPathToSignatureProperties();
        checkNode(doc, (pathToSignatureProperties + "etsi:SignaturePolicyIdentifier/etsi:SignaturePolicyId"), XAdESSignaturePropertiesTest.getPrefix2NamespaceMap(), false);
        checkNode(doc, (pathToSignatureProperties + "etsi:SignaturePolicyIdentifier/etsi:SignaturePolicyImplied"), XAdESSignaturePropertiesTest.getPrefix2NamespaceMap(), true);
    }

    @Test
    public void policyNone() throws Exception {
        XmlSignerEndpoint endpoint = getSignerEndpoint();
        XAdESSignatureProperties props = ((XAdESSignatureProperties) (endpoint.getProperties()));
        props.setSignaturePolicy(SIG_POLICY_NONE);
        Document doc = testEnveloping();
        String pathToSignatureProperties = getPathToSignatureProperties();
        checkNode(doc, (pathToSignatureProperties + "etsi:SignaturePolicyIdentifier"), XAdESSignaturePropertiesTest.getPrefix2NamespaceMap(), false);
    }

    @Test
    public void allPropertiesEmpty() throws Exception {
        XmlSignerEndpoint endpoint = getSignerEndpoint();
        XAdESSignatureProperties props = new XAdESSignatureProperties();
        props.setAddSigningTime(false);
        props.setCommitmentTypeId("");
        props.setCommitmentTypeIdDescription("");
        props.setCommitmentTypeIdQualifier("");
        props.setDataObjectFormatDescription("");
        props.setDataObjectFormatIdentifier("");
        props.setDataObjectFormatIdentifierDescription("");
        props.setDataObjectFormatIdentifierQualifier("");
        props.setDataObjectFormatMimeType("");
        props.setDigestAlgorithmForSigningCertificate("");
        props.setSignaturePolicy("None");
        props.setSigPolicyId("");
        props.setSigPolicyIdDescription("");
        props.setSigPolicyIdQualifier("");
        props.setSignaturePolicyDigestAlgorithm("");
        props.setSignaturePolicyDigestValue("");
        props.setSignatureProductionPlaceCity("");
        props.setSignatureProductionPlaceCountryName("");
        props.setSignatureProductionPlacePostalCode("");
        props.setSignatureProductionPlaceStateOrProvince("");
        endpoint.setProperties(props);
        Document doc = testEnveloping();
        // expecting no Qualifying Properties
        checkNode(doc, "/ds:Signature/ds:Object/etsi:QualifyingProperties", XAdESSignaturePropertiesTest.getPrefix2NamespaceMap(), false);
    }

    @Test
    public void emptySignatureId() throws Exception {
        Document doc = testEnveloping("direct:emptySignatureId");
        checkNode(doc, "/ds:Signature/ds:Object/etsi:QualifyingProperties", XAdESSignaturePropertiesTest.getPrefix2NamespaceMap(), true);
    }

    @Test
    public void prefixAndNamespace() throws Exception {
        XmlSignerEndpoint endpoint = getSignerEndpoint();
        XAdESSignatureProperties props = ((XAdESSignatureProperties) (endpoint.getProperties()));
        props.setPrefix("p");
        props.setNamespace(HTTP_URI_ETSI_ORG_01903_V1_1_1);
        props.setCommitmentTypeIdDescription(null);
        props.setCommitmentTypeIdDocumentationReferences(Collections.<String>emptyList());
        props.setCommitmentTypeIdQualifier(null);
        props.setDataObjectFormatIdentifierDescription(null);
        props.setDataObjectFormatIdentifierDocumentationReferences(Collections.<String>emptyList());
        props.setDataObjectFormatIdentifierQualifier(null);
        props.setSigPolicyIdDescription(null);
        props.setSigPolicyIdDocumentationReferences(Collections.<String>emptyList());
        props.setSigPolicyIdQualifier(null);
        // the following lists must be set to empty because otherwise they would contain XML fragments with a wrong namespace
        props.setSigPolicyQualifiers(Collections.<String>emptyList());
        props.setSignerClaimedRoles(Collections.<String>emptyList());
        props.setCommitmentTypeQualifiers(Collections.<String>emptyList());
        Document doc = testEnveloping();
        Map<String, String> prefix2Namespace = new TreeMap<>();
        prefix2Namespace.put("ds", XMLSignature.XMLNS);
        prefix2Namespace.put("etsi", HTTP_URI_ETSI_ORG_01903_V1_1_1);
        XPathExpression expr = XAdESSignaturePropertiesTest.getXpath("/ds:Signature/ds:Object/etsi:QualifyingProperties", prefix2Namespace);
        Object result = expr.evaluate(doc, XPathConstants.NODE);
        assertNotNull(result);
        Node node = ((Node) (result));
        assertEquals("p", node.getPrefix());
        assertEquals(HTTP_URI_ETSI_ORG_01903_V1_1_1, node.getNamespaceURI());
    }

    @Test
    public void headers() throws Exception {
        Map<String, Object> header = new TreeMap<>();
        header.put(HEADER_XADES_PREFIX, "ns1");
        header.put(HEADER_XADES_NAMESPACE, HTTP_URI_ETSI_ORG_01903_V1_2_2);
        header.put(HEADER_XADES_QUALIFYING_PROPERTIES_ID, "QualId");
        header.put(HEADER_XADES_SIGNED_DATA_OBJECT_PROPERTIES_ID, "ObjId");
        header.put(HEADER_XADES_SIGNED_SIGNATURE_PROPERTIES_ID, "SigId");
        header.put(HEADER_XADES_DATA_OBJECT_FORMAT_ENCODING, "base64");
        XmlSignerEndpoint endpoint = getSignerEndpoint();
        XAdESSignatureProperties props = ((XAdESSignatureProperties) (endpoint.getProperties()));
        // the following lists must be set to empty because otherwise they would contain XML fragments with a wrong namespace
        props.setSigPolicyQualifiers(Collections.<String>emptyList());
        props.setSignerClaimedRoles(Collections.<String>emptyList());
        props.setCommitmentTypeQualifiers(Collections.<String>emptyList());
        Document doc = testEnveloping("direct:enveloping", header);
        Map<String, String> prefix2Namespace = new TreeMap<>();
        prefix2Namespace.put("ds", XMLSignature.XMLNS);
        prefix2Namespace.put("etsi", HTTP_URI_ETSI_ORG_01903_V1_2_2);
        XPathExpression expr = XAdESSignaturePropertiesTest.getXpath("/ds:Signature/ds:Object/etsi:QualifyingProperties", prefix2Namespace);
        Object result = expr.evaluate(doc, XPathConstants.NODE);
        assertNotNull(result);
        Node node = ((Node) (result));
        assertEquals("ns1", node.getPrefix());
        assertEquals(HTTP_URI_ETSI_ORG_01903_V1_2_2, node.getNamespaceURI());
        XAdESSignaturePropertiesTest.checkXpath(doc, "/ds:Signature/ds:Object/etsi:QualifyingProperties/@Id", prefix2Namespace, "QualId");
        XAdESSignaturePropertiesTest.checkXpath(doc, "/ds:Signature/ds:Object/etsi:QualifyingProperties/etsi:SignedProperties/etsi:SignedDataObjectProperties/@Id", prefix2Namespace, "ObjId");
        XAdESSignaturePropertiesTest.checkXpath(doc, "/ds:Signature/ds:Object/etsi:QualifyingProperties/etsi:SignedProperties/etsi:SignedSignatureProperties/@Id", prefix2Namespace, "SigId");
        XAdESSignaturePropertiesTest.checkXpath(doc, "/ds:Signature/ds:Object/etsi:QualifyingProperties/etsi:SignedProperties/etsi:SignedDataObjectProperties/etsi:DataObjectFormat/etsi:Encoding/text()", prefix2Namespace, "base64");
    }

    @Test
    public void enveloped() throws Exception {
        setupMock();
        sendBody("direct:enveloped", XAdESSignaturePropertiesTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void detached() throws Exception {
        String detachedPayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"// 
         + "<ns:root xmlns:ns=\"http://test\"><a ID=\"myID\"><b>bValue</b></a></ns:root>";
        setupMock();
        sendBody("direct:detached", detachedPayload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void sigPolicyIdEmpty() throws Exception {
        testExceptionSigPolicyIdMissing("");
    }

    @Test
    public void sigPolicyIdNull() throws Exception {
        testExceptionSigPolicyIdMissing(null);
    }

    @Test
    public void sigPolicyDigestEmpty() throws Exception {
        testExceptionSigPolicyDigestMissing("");
    }

    @Test
    public void sigPolicyDigestNull() throws Exception {
        testExceptionSigPolicyDigestMissing(null);
    }

    @Test
    public void sigPolicyDigestAlgoEmpty() throws Exception {
        testExceptionSigPolicyDigestAlgoMissing("");
    }

    @Test
    public void sigPolicyDigestAlgoNull() throws Exception {
        testExceptionSigPolicyDigestAlgoMissing(null);
    }

    @Test
    public void invalidXmlFragmentForClaimedRole() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:exception");
        mock.expectedMessageCount(1);
        XmlSignerEndpoint endpoint = getSignerEndpoint();
        XAdESSignatureProperties props = ((XAdESSignatureProperties) (endpoint.getProperties()));
        props.setSignerClaimedRoles(Collections.singletonList("<ClaimedRole>wrong XML fragment<ClaimedRole>"));// Element 'ClaimedRole' is not closed correctly

        sendBody("direct:enveloping", XAdESSignaturePropertiesTest.payload, Collections.<String, Object>emptyMap());
        assertMockEndpointsSatisfied();
        XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, ("The XAdES configuration is invalid. The list of the claimed roles contains the invalid entry '<ClaimedRole>wrong XML fragment<ClaimedRole>'. An entry must either be a text or" + " an XML fragment with the root element 'ClaimedRole' with the namespace 'http://uri.etsi.org/01903/v1.3.2#'."), null);
    }

    @Test
    public void invalidXmlFragmentForCommitmentTypeQualifier() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:exception");
        mock.expectedMessageCount(1);
        XmlSignerEndpoint endpoint = getSignerEndpoint();
        XAdESSignatureProperties props = ((XAdESSignatureProperties) (endpoint.getProperties()));
        props.setCommitmentTypeQualifiers(Collections.singletonList("<CommitmentTypeQualifier>wrong XML fragment<CommitmentTypeQualifier>"));// end tag is not correct

        sendBody("direct:enveloping", XAdESSignaturePropertiesTest.payload, Collections.<String, Object>emptyMap());
        assertMockEndpointsSatisfied();
        XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, ("The XAdES configuration is invalid. The list of the commitment type qualifiers contains the invalid entry '<CommitmentTypeQualifier>wrong XML fragment<CommitmentTypeQualifier>'." + " An entry must either be a text or an XML fragment with the root element 'CommitmentTypeQualifier' with the namespace 'http://uri.etsi.org/01903/v1.3.2#'."), null);
    }

    @Test
    public void invalidXmlFragmentForSigPolicyQualifier() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:exception");
        mock.expectedMessageCount(1);
        XmlSignerEndpoint endpoint = getSignerEndpoint();
        XAdESSignatureProperties props = ((XAdESSignatureProperties) (endpoint.getProperties()));
        props.setSigPolicyQualifiers(Collections.singletonList("<SigPolicyQualifier>wrong XML fragment<SigPolicyQualifier>"));// end tag is not correct

        sendBody("direct:enveloping", XAdESSignaturePropertiesTest.payload, Collections.<String, Object>emptyMap());
        assertMockEndpointsSatisfied();
        XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, ("The XAdES configuration is invalid. The list of the signatue policy qualifiers contains the invalid entry '<SigPolicyQualifier>wrong XML fragment<SigPolicyQualifier>'." + " An entry must either be a text or an XML fragment with the root element 'SigPolicyQualifier' with the namespace 'http://uri.etsi.org/01903/v1.3.2#'."), null);
    }

    @Test
    public void invalidNamespaceForTheRootElementInXmlFragmentForSigPolicyQualifier() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:exception");
        mock.expectedMessageCount(1);
        XmlSignerEndpoint endpoint = getSignerEndpoint();
        XAdESSignatureProperties props = ((XAdESSignatureProperties) (endpoint.getProperties()));
        props.setSigPolicyQualifiers(Collections.singletonList("<SigPolicyQualifier xmlns=\"http://invalid.com\">XML fragment with wrong namespace for root element</SigPolicyQualifier>"));
        sendBody("direct:enveloping", XAdESSignaturePropertiesTest.payload, Collections.<String, Object>emptyMap());
        assertMockEndpointsSatisfied();
        XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, ("The XAdES configuration is invalid. The root element 'SigPolicyQualifier' of the provided XML fragment " + ("\'<SigPolicyQualifier xmlns=\"http://invalid.com\">XML fragment with wrong namespace for root element</SigPolicyQualifier>\' has the invalid namespace \'http://invalid.com\'." + " The correct namespace is 'http://uri.etsi.org/01903/v1.3.2#'.")), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void namespaceNull() throws Exception {
        new XAdESSignatureProperties().setNamespace(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void signingCertificateURIsNull() throws Exception {
        new XAdESSignatureProperties().setSigningCertificateURIs(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sigPolicyInvalid() throws Exception {
        new XAdESSignatureProperties().setSignaturePolicy("invalid");
    }

    @Test(expected = IllegalArgumentException.class)
    public void sigPolicyIdDocumentationReferencesNull() throws Exception {
        new XAdESSignatureProperties().setSigPolicyIdDocumentationReferences(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sigPolicyIdDocumentationReferencesNullEntry() throws Exception {
        new XAdESSignatureProperties().setSigPolicyIdDocumentationReferences(Collections.<String>singletonList(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void sigPolicyIdDocumentationReferencesEmptyEntry() throws Exception {
        new XAdESSignatureProperties().setSigPolicyIdDocumentationReferences(Collections.<String>singletonList(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void dataObjectFormatIdentifierDocumentationReferencesNull() throws Exception {
        new XAdESSignatureProperties().setDataObjectFormatIdentifierDocumentationReferences(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void dataObjectFormatIdentifierDocumentationReferencesNullEntry() throws Exception {
        new XAdESSignatureProperties().setDataObjectFormatIdentifierDocumentationReferences(Collections.<String>singletonList(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void dataObjectFormatIdentifierDocumentationReferencesEmptyEntry() throws Exception {
        new XAdESSignatureProperties().setDataObjectFormatIdentifierDocumentationReferences(Collections.<String>singletonList(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void signerClaimedRolesNull() throws Exception {
        new XAdESSignatureProperties().setSignerClaimedRoles(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void signerClaimedRolesNullEntry() throws Exception {
        new XAdESSignatureProperties().setSignerClaimedRoles(Collections.<String>singletonList(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void signerClaimedRolesEmptyEntry() throws Exception {
        new XAdESSignatureProperties().setSignerClaimedRoles(Collections.<String>singletonList(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void signerCertifiedRolesNull() throws Exception {
        new XAdESSignatureProperties().setSignerCertifiedRoles(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void signerCertifiedRolesNullEntry() throws Exception {
        new XAdESSignatureProperties().setSignerCertifiedRoles(Collections.<XAdESEncapsulatedPKIData>singletonList(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitmentTypeIdDocumentationReferencesNull() throws Exception {
        new XAdESSignatureProperties().setCommitmentTypeIdDocumentationReferences(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitmentTypeIdDocumentationReferencesNullEntry() throws Exception {
        new XAdESSignatureProperties().setCommitmentTypeIdDocumentationReferences(Collections.<String>singletonList(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitmentTypeIdDocumentationReferencesEmptyEntry() throws Exception {
        new XAdESSignatureProperties().setCommitmentTypeIdDocumentationReferences(Collections.<String>singletonList(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitmentTypeQualifiersNull() throws Exception {
        new XAdESSignatureProperties().setCommitmentTypeQualifiers(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitmentTypeQualifiersNullEntry() throws Exception {
        new XAdESSignatureProperties().setCommitmentTypeQualifiers(Collections.<String>singletonList(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitmentTypeQualifiersEmptyEntry() throws Exception {
        new XAdESSignatureProperties().setCommitmentTypeQualifiers(Collections.<String>singletonList(""));
    }

    private static class CertChainXAdESSignatureProperties extends XAdESSignatureProperties {
        private KeyStore keystore = XAdESSignaturePropertiesTest.CertChainXAdESSignatureProperties.getKeystore();

        private String alias = "bob";

        CertChainXAdESSignatureProperties() {
            setAddSigningTime(false);
        }

        @Override
        protected X509Certificate getSigningCertificate() throws Exception {
            // NOPMD
            return null;
        }

        @Override
        protected X509Certificate[] getSigningCertificateChain() throws Exception {
            // NOPMD
            Certificate[] certs = keystore.getCertificateChain(alias);
            X509Certificate[] result = new X509Certificate[certs.length];
            int counter = 0;
            for (Certificate cert : certs) {
                result[counter] = ((X509Certificate) (cert));
                counter++;
            }
            return result;
        }

        private static KeyStore getKeystore() {
            try {
                return TestKeystore.getKeyStore();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}

