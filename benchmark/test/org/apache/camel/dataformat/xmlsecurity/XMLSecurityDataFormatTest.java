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
package org.apache.camel.dataformat.xmlsecurity;


import XMLCipher.AES_128;
import XMLCipher.RSA_OAEP;
import XMLCipher.RSA_v1dot5;
import XMLCipher.TRIPLEDES;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.codec.Charsets;
import org.apache.xml.security.encryption.XMLCipher;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


/**
 * Unit test of the encryptXML data format.
 */
public class XMLSecurityDataFormatTest extends CamelTestSupport {
    // one could use testCypherAlgorithm = XMLCipher.AES_128 if she had the AES Optional Pack installed
    protected static String testCypherAlgorithm = XMLCipher.AES_128;

    TestHelper xmlsecTestHelper = new TestHelper();

    /* Encryption Tests */
    @Test
    public void testFullPayloadXMLEncryption() throws Exception {
        if (!(TestHelper.HAS_3DES)) {
            return;
        }
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML().to("mock:encrypted");
            }
        });
        xmlsecTestHelper.testEncryption(context);
    }

    @Test
    public void testPartialPayloadXMLContentEncryption() throws Exception {
        if (!(TestHelper.HAS_3DES)) {
            return;
        }
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//cheesesites/italy/cheese", true).to("mock:encrypted");
            }
        });
        xmlsecTestHelper.testEncryption(context);
    }

    @Test
    public void testPartialPayloadMultiNodeXMLContentEncryption() throws Exception {
        if (!(TestHelper.HAS_3DES)) {
            return;
        }
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//cheesesites/*/cheese", true).to("mock:encrypted");
            }
        });
        xmlsecTestHelper.testEncryption(context);
    }

    @Test
    public void testPartialPayloadXMLElementEncryptionWithKey() throws Exception {
        if (!(TestHelper.HAS_3DES)) {
            return;
        }
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//cheesesites/france/cheese", false, "Just another 24 Byte key").to("mock:encrypted");
            }
        });
        xmlsecTestHelper.testEncryption(context);
    }

    @Test
    public void testPartialPayloadXMLElementEncryptionWithKeyAndAlgorithm() throws Exception {
        final byte[] bits128 = new byte[]{ ((byte) (8)), ((byte) (9)), ((byte) (10)), ((byte) (11)), ((byte) (12)), ((byte) (13)), ((byte) (14)), ((byte) (15)), ((byte) (16)), ((byte) (17)), ((byte) (18)), ((byte) (19)), ((byte) (20)), ((byte) (21)), ((byte) (22)), ((byte) (23)) };
        final String passCode = new String(bits128);
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//cheesesites/netherlands", false, passCode, AES_128).to("mock:encrypted");
            }
        });
        xmlsecTestHelper.testEncryption(context);
    }

    @Test
    public void testPartialPayloadXMLElementEncryptionWithByteKeyAndAlgorithm() throws Exception {
        final byte[] bits192 = new byte[]{ ((byte) (36)), ((byte) (242)), ((byte) (211)), ((byte) (69)), ((byte) (192)), ((byte) (117)), ((byte) (177)), ((byte) (0)), ((byte) (48)), ((byte) (212)), ((byte) (61)), ((byte) (245)), ((byte) (109)), ((byte) (170)), ((byte) (125)), ((byte) (194)), ((byte) (133)), ((byte) (50)), ((byte) (42)), ((byte) (182)), ((byte) (254)), ((byte) (237)), ((byte) (190)), ((byte) (239)) };
        final Charset passCodeCharset = Charsets.UTF_8;
        final String passCode = new String(bits192, passCodeCharset);
        byte[] bytes = passCode.getBytes(passCodeCharset);
        assertTrue(((bits192.length) != (bytes.length)));
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//cheesesites/netherlands", false, bits192, TRIPLEDES).to("mock:encrypted");
            }
        });
        xmlsecTestHelper.testEncryption(context);
    }

    @Test
    public void testFullPayloadAsymmetricKeyEncryption() throws Exception {
        KeyStoreParameters tsParameters = new KeyStoreParameters();
        tsParameters.setPassword("password");
        tsParameters.setResource("sender.ts");
        final XMLSecurityDataFormat xmlEncDataFormat = new XMLSecurityDataFormat();
        xmlEncDataFormat.setKeyCipherAlgorithm(RSA_v1dot5);
        xmlEncDataFormat.setKeyOrTrustStoreParameters(tsParameters);
        xmlEncDataFormat.setXmlCipherAlgorithm(XMLSecurityDataFormatTest.testCypherAlgorithm);
        xmlEncDataFormat.setRecipientKeyAlias("recipient");
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal(xmlEncDataFormat).to("mock:encrypted");
            }
        });
        xmlsecTestHelper.testEncryption(context);
    }

    @Test
    public void testPartialPayloadAsymmetricKeyEncryptionWithContextTruststoreProperties() throws Exception {
        final KeyStoreParameters tsParameters = new KeyStoreParameters();
        tsParameters.setPassword("password");
        tsParameters.setResource("sender.ts");
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//cheesesites/italy/cheese", true, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_v1dot5, tsParameters).to("mock:encrypted");
            }
        });
        xmlsecTestHelper.testEncryption(context);
    }

    @Test
    public void testAsymmetricEncryptionAddKeyValue() throws Exception {
        KeyStoreParameters tsParameters = new KeyStoreParameters();
        tsParameters.setPassword("password");
        tsParameters.setResource("sender.ts");
        final XMLSecurityDataFormat xmlEncDataFormat = new XMLSecurityDataFormat();
        xmlEncDataFormat.setKeyOrTrustStoreParameters(tsParameters);
        xmlEncDataFormat.setXmlCipherAlgorithm(XMLSecurityDataFormatTest.testCypherAlgorithm);
        xmlEncDataFormat.setRecipientKeyAlias("recipient");
        xmlEncDataFormat.setAddKeyValueForEncryptedKey(true);
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal(xmlEncDataFormat).to("mock:encrypted");
            }
        });
        Document doc = xmlsecTestHelper.testEncryption(TestHelper.XML_FRAGMENT, context);
        NodeList nodeList = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "RSAKeyValue");
        Assert.assertTrue(((nodeList.getLength()) > 0));
    }

    @Test
    public void testAsymmetricEncryptionNoKeyValue() throws Exception {
        KeyStoreParameters tsParameters = new KeyStoreParameters();
        tsParameters.setPassword("password");
        tsParameters.setResource("sender.ts");
        final XMLSecurityDataFormat xmlEncDataFormat = new XMLSecurityDataFormat();
        xmlEncDataFormat.setKeyOrTrustStoreParameters(tsParameters);
        xmlEncDataFormat.setXmlCipherAlgorithm(XMLSecurityDataFormatTest.testCypherAlgorithm);
        xmlEncDataFormat.setRecipientKeyAlias("recipient");
        xmlEncDataFormat.setAddKeyValueForEncryptedKey(false);
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal(xmlEncDataFormat).to("mock:encrypted");
            }
        });
        Document doc = xmlsecTestHelper.testEncryption(TestHelper.XML_FRAGMENT, context);
        NodeList nodeList = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "RSAKeyValue");
        Assert.assertTrue(((nodeList.getLength()) == 0));
    }

    /* Decryption Tests */
    @Test
    public void testFullPayloadXMLDecryption() throws Exception {
        if (!(TestHelper.HAS_3DES)) {
            return;
        }
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML().to("mock:encrypted").unmarshal().secureXML().to("mock:decrypted");
            }
        });
        xmlsecTestHelper.testDecryption(context);
    }

    @Test
    public void testPartialPayloadXMLContentDecryption() throws Exception {
        if (!(TestHelper.HAS_3DES)) {
            return;
        }
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//cheesesites/italy/cheese", true).to("mock:encrypted").unmarshal().secureXML("//cheesesites/italy/cheese", true).to("mock:decrypted");
            }
        });
        xmlsecTestHelper.testDecryption(context);
    }

    @Test
    public void testPartialPayloadMultiNodeXMLContentDecryption() throws Exception {
        if (!(TestHelper.HAS_3DES)) {
            return;
        }
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//cheesesites/*/cheese", true).to("mock:encrypted").unmarshal().secureXML("//cheesesites/*/cheese", true).to("mock:decrypted");
            }
        });
        xmlsecTestHelper.testDecryption(context);
    }

    @Test
    public void testPartialPayloadXMLElementDecryptionWithKey() throws Exception {
        if (!(TestHelper.HAS_3DES)) {
            return;
        }
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//cheesesites/france/cheese", false, "Just another 24 Byte key").to("mock:encrypted").unmarshal().secureXML("//cheesesites/france", false, "Just another 24 Byte key").to("mock:decrypted");
            }
        });
        xmlsecTestHelper.testDecryption(context);
    }

    @Test
    public void testXMLElementDecryptionWithoutEncryptedKey() throws Exception {
        if (!(TestHelper.HAS_3DES)) {
            return;
        }
        String passPhrase = "this is a test passphrase";
        byte[] bytes = passPhrase.getBytes();
        final byte[] keyBytes = Arrays.copyOf(bytes, 24);
        for (int j = 0, k = 16; j < 8;) {
            keyBytes[(k++)] = keyBytes[(j++)];
        }
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("timer://foo?period=5000&repeatCount=1").to("language:constant:resource:classpath:org/apache/camel/component/xmlsecurity/EncryptedMessage.xml").unmarshal().secureXML("/*[local-name()='Envelope']/*[local-name()='Body']", true, keyBytes, TRIPLEDES).to("mock:decrypted");
            }
        });
        xmlsecTestHelper.testDecryptionNoEncryptedKey(context);
    }

    @Test
    public void testPartialPayloadXMLContentDecryptionWithKeyAndAlgorithm() throws Exception {
        final byte[] bits128 = new byte[]{ ((byte) (8)), ((byte) (9)), ((byte) (10)), ((byte) (11)), ((byte) (12)), ((byte) (13)), ((byte) (14)), ((byte) (15)), ((byte) (16)), ((byte) (17)), ((byte) (18)), ((byte) (19)), ((byte) (20)), ((byte) (21)), ((byte) (22)), ((byte) (23)) };
        final String passCode = new String(bits128);
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//cheesesites/italy", true, passCode, AES_128).to("mock:encrypted").unmarshal().secureXML("//cheesesites/italy", true, passCode, AES_128).to("mock:decrypted");
            }
        });
        xmlsecTestHelper.testDecryption(context);
    }

    @Test
    public void testFullPayloadAsymmetricKeyDecryption() throws Exception {
        final KeyStoreParameters tsParameters = new KeyStoreParameters();
        tsParameters.setPassword("password");
        tsParameters.setResource("sender.ts");
        final KeyStoreParameters ksParameters = new KeyStoreParameters();
        ksParameters.setPassword("password");
        ksParameters.setResource("recipient.ks");
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("", true, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_v1dot5, tsParameters).to("mock:encrypted").unmarshal().secureXML("", true, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_v1dot5, ksParameters).to("mock:decrypted");
            }
        });
        xmlsecTestHelper.testDecryption(context);
    }

    @Test
    public void testFullPayloadAsymmetricKeyDecryptionWithKeyPassword() throws Exception {
        final KeyStoreParameters tsParameters = new KeyStoreParameters();
        tsParameters.setPassword("password");
        tsParameters.setResource("sender.ts");
        final KeyStoreParameters ksParameters = new KeyStoreParameters();
        ksParameters.setPassword("password");
        ksParameters.setResource("recipient-with-key-pass.ks");
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("", true, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_v1dot5, tsParameters).to("mock:encrypted").unmarshal().secureXML("", true, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_v1dot5, ksParameters, "keyPassword").to("mock:decrypted");
            }
        });
        xmlsecTestHelper.testDecryption(context);
    }

    @Test
    public void testPartialPayloadAsymmetricKeyDecryption() throws Exception {
        final Map<String, String> namespaces = new HashMap<>();
        namespaces.put("ns1", "http://cheese.xmlsecurity.camel.apache.org/");
        final KeyStoreParameters tsParameters = new KeyStoreParameters();
        tsParameters.setPassword("password");
        tsParameters.setResource("sender.ts");
        final KeyStoreParameters ksParameters = new KeyStoreParameters();
        ksParameters.setPassword("password");
        ksParameters.setResource("recipient.ks");
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//ns1:cheesesites/italy", namespaces, true, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_v1dot5, tsParameters).to("mock:encrypted").unmarshal().secureXML("//ns1:cheesesites/italy", namespaces, true, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_v1dot5, ksParameters).to("mock:decrypted");
            }
        });
        xmlsecTestHelper.testDecryption(TestHelper.NS_XML_FRAGMENT, context);
    }

    @Test
    public void testPartialPayloadAsymmetricKeyDecryptionCustomNS() throws Exception {
        final KeyStoreParameters tsParameters = new KeyStoreParameters();
        tsParameters.setPassword("password");
        tsParameters.setResource("sender.ts");
        final KeyStoreParameters ksParameters = new KeyStoreParameters();
        ksParameters.setPassword("password");
        ksParameters.setResource("recipient.ks");
        final Map<String, String> namespaces = new HashMap<>();
        namespaces.put("cust", "http://cheese.xmlsecurity.camel.apache.org/");
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//cust:cheesesites/italy", namespaces, true, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_v1dot5, tsParameters).to("mock:encrypted").unmarshal().secureXML("//cust:cheesesites/italy", namespaces, true, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_v1dot5, ksParameters).to("mock:decrypted");
            }
        });
        xmlsecTestHelper.testDecryption(TestHelper.NS_XML_FRAGMENT, context);
    }

    @Test
    public void testAsymmetricEncryptionAlgorithmFullPayload() throws Exception {
        final KeyStoreParameters tsParameters = new KeyStoreParameters();
        tsParameters.setPassword("password");
        tsParameters.setResource("sender.ts");
        final KeyStoreParameters ksParameters = new KeyStoreParameters();
        ksParameters.setPassword("password");
        ksParameters.setResource("recipient.ks");
        // RSA v1.5 is not allowed unless explicitly configured
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("", true, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_v1dot5, tsParameters).to("mock:encrypted").unmarshal().secureXML("", true, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_OAEP, ksParameters).to("mock:decrypted");
            }
        });
        MockEndpoint resultEndpoint = context.getEndpoint("mock:decrypted", MockEndpoint.class);
        resultEndpoint.setExpectedMessageCount(0);
        // verify that the message was encrypted before checking that it is decrypted
        xmlsecTestHelper.testEncryption(TestHelper.XML_FRAGMENT, context);
        resultEndpoint.assertIsSatisfied(100);
    }

    @Test
    public void testAsymmetricEncryptionAlgorithmPartialPayload() throws Exception {
        final KeyStoreParameters tsParameters = new KeyStoreParameters();
        tsParameters.setPassword("password");
        tsParameters.setResource("sender.ts");
        final KeyStoreParameters ksParameters = new KeyStoreParameters();
        ksParameters.setPassword("password");
        ksParameters.setResource("recipient.ks");
        // RSA v1.5 is not allowed unless explicitly configured
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//cheesesites/italy", true, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_v1dot5, tsParameters).to("mock:encrypted").unmarshal().secureXML("//cheesesites/italy", true, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_OAEP, ksParameters).to("mock:decrypted");
            }
        });
        MockEndpoint resultEndpoint = context.getEndpoint("mock:decrypted", MockEndpoint.class);
        resultEndpoint.setExpectedMessageCount(0);
        // verify that the message was encrypted before checking that it is decrypted
        xmlsecTestHelper.testEncryption(TestHelper.XML_FRAGMENT, context);
        resultEndpoint.assertIsSatisfied(100);
    }

    @Test
    public void testAsymmetricEncryptionAlgorithmPartialPayloadElement() throws Exception {
        final KeyStoreParameters tsParameters = new KeyStoreParameters();
        tsParameters.setPassword("password");
        tsParameters.setResource("sender.ts");
        final KeyStoreParameters ksParameters = new KeyStoreParameters();
        ksParameters.setPassword("password");
        ksParameters.setResource("recipient.ks");
        // RSA v1.5 is not allowed unless explicitly configured
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").marshal().secureXML("//cheesesites/france/cheese", false, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_v1dot5, tsParameters).to("mock:encrypted").unmarshal().secureXML("//cheesesites/france", false, "recipient", XMLSecurityDataFormatTest.testCypherAlgorithm, RSA_OAEP, ksParameters).to("mock:decrypted");
            }
        });
        MockEndpoint resultEndpoint = context.getEndpoint("mock:decrypted", MockEndpoint.class);
        resultEndpoint.setExpectedMessageCount(0);
        // verify that the message was encrypted before checking that it is decrypted
        xmlsecTestHelper.testEncryption(TestHelper.XML_FRAGMENT, context);
        resultEndpoint.assertIsSatisfied(100);
    }
}

