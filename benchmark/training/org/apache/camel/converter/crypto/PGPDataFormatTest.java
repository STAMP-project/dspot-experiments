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
package org.apache.camel.converter.crypto;


import PGPKeyAccessDataFormat.KEY_USERID;
import PGPKeyAccessDataFormat.NUMBER_OF_ENCRYPTION_KEYS;
import PGPKeyAccessDataFormat.NUMBER_OF_SIGNING_KEYS;
import PGPKeyAccessDataFormat.SIGNATURE_KEY_USERID;
import PGPKeyAccessDataFormat.SIGNATURE_VERIFICATION_OPTION_IGNORE;
import PGPKeyAccessDataFormat.SIGNATURE_VERIFICATION_OPTION_NO_SIGNATURE_ALLOWED;
import PGPKeyAccessDataFormat.SIGNATURE_VERIFICATION_OPTION_REQUIRED;
import PGPLiteralData.BINARY;
import PGPLiteralData.CONSOLE;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.mock.MockEndpoint;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEKeyEncryptionMethodGenerator;
import org.junit.Test;


public class PGPDataFormatTest extends AbstractPGPDataFormatTest {
    private static final String PUB_KEY_RING_SUBKEYS_FILE_NAME = "org/apache/camel/component/crypto/pubringSubKeys.gpg";

    private static final String SEC_KEY_RING_FILE_NAME = "org/apache/camel/component/crypto/secring.gpg";

    private static final String PUB_KEY_RING_FILE_NAME = "org/apache/camel/component/crypto/pubring.gpg";

    PGPDataFormat encryptor = new PGPDataFormat();

    PGPDataFormat decryptor = new PGPDataFormat();

    @Test
    public void testEncryption() throws Exception {
        doRoundTripEncryptionTests("direct:inline");
    }

    @Test
    public void testEncryption2() throws Exception {
        doRoundTripEncryptionTests("direct:inline2");
    }

    @Test
    public void testEncryptionArmor() throws Exception {
        doRoundTripEncryptionTests("direct:inline-armor");
    }

    @Test
    public void testEncryptionSigned() throws Exception {
        doRoundTripEncryptionTests("direct:inline-sign");
    }

    @Test
    public void testEncryptionKeyRingByteArray() throws Exception {
        doRoundTripEncryptionTests("direct:key-ring-byte-array");
    }

    @Test
    public void testEncryptionSignedKeyRingByteArray() throws Exception {
        doRoundTripEncryptionTests("direct:sign-key-ring-byte-array");
    }

    @Test
    public void testSeveralSignerKeys() throws Exception {
        doRoundTripEncryptionTests("direct:several-signer-keys");
    }

    @Test
    public void testOneUserIdWithServeralKeys() throws Exception {
        doRoundTripEncryptionTests("direct:one-userid-several-keys");
    }

    @Test
    public void testKeyAccess() throws Exception {
        doRoundTripEncryptionTests("direct:key_access");
    }

    @Test
    public void testVerifyExceptionNoPublicKeyFoundCorrespondingToSignatureUserIds() throws Exception {
        setupExpectations(context, 1, "mock:encrypted");
        MockEndpoint exception = setupExpectations(context, 1, "mock:exception");
        String payload = "Hi Alice, Be careful Eve is listening, signed Bob";
        Map<String, Object> headers = getHeaders();
        template.sendBodyAndHeaders("direct:verify_exception_sig_userids", payload, headers);
        assertMockEndpointsSatisfied();
        PGPDataFormatTest.checkThrownException(exception, IllegalArgumentException.class, null, "No public key found for the key ID(s)");
    }

    @Test
    public void testVerifyExceptionNoPassphraseSpecifiedForSignatureKeyUserId() throws Exception {
        MockEndpoint exception = setupExpectations(context, 1, "mock:exception");
        String payload = "Hi Alice, Be careful Eve is listening, signed Bob";
        Map<String, Object> headers = new HashMap<>();
        // add signature user id which does not have a passphrase
        headers.put(SIGNATURE_KEY_USERID, "userIDWithNoPassphrase");
        // the following entry is necessary for the dynamic test
        headers.put(KEY_USERID, "second");
        template.sendBodyAndHeaders("direct:several-signer-keys", payload, headers);
        assertMockEndpointsSatisfied();
        PGPDataFormatTest.checkThrownException(exception, IllegalArgumentException.class, null, "No passphrase specified for signature key user ID");
    }

    /**
     * You get three keys with the UserId "keyflag", a primary key and its two
     * sub-keys. The sub-key with KeyFlag {@link KeyFlags#SIGN_DATA} should be
     * used for signing and the sub-key with KeyFlag
     * {@link KeyFlags#ENCRYPT_COMMS} or {@link KeyFlags#ENCRYPT_COMMS} or
     * {@link KeyFlags#ENCRYPT_STORAGE} should be used for decryption.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testKeyFlagSelectsCorrectKey() throws Exception {
        MockEndpoint mockKeyFlag = getMockEndpoint("mock:encrypted_keyflag");
        mockKeyFlag.setExpectedMessageCount(1);
        template.sendBody("direct:keyflag", "Test Message");
        assertMockEndpointsSatisfied();
        List<Exchange> exchanges = mockKeyFlag.getExchanges();
        assertEquals(1, exchanges.size());
        Exchange exchange = exchanges.get(0);
        Message inMess = exchange.getIn();
        assertNotNull(inMess);
        // must contain exactly one encryption key and one signature
        assertEquals(1, inMess.getHeader(NUMBER_OF_ENCRYPTION_KEYS));
        assertEquals(1, inMess.getHeader(NUMBER_OF_SIGNING_KEYS));
    }

    /**
     * You get three keys with the UserId "keyflag", a primary key and its two
     * sub-keys. The sub-key with KeyFlag {@link KeyFlags#SIGN_DATA} should be
     * used for signing and the sub-key with KeyFlag
     * {@link KeyFlags#ENCRYPT_COMMS} or {@link KeyFlags#ENCRYPT_COMMS} or
     * {@link KeyFlags#ENCRYPT_STORAGE} should be used for decryption.
     * <p>
     * Tests also the decryption and verifying part with the subkeys.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDecryptVerifyWithSubkey() throws Exception {
        // do not use doRoundTripEncryptionTests("direct:subkey"); because otherwise you get an error in the dynamic test
        String payload = "Test Message";
        MockEndpoint mockSubkey = getMockEndpoint("mock:unencrypted");
        mockSubkey.expectedBodiesReceived(payload);
        template.sendBody("direct:subkey", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testEmptyBody() throws Exception {
        String payload = "";
        MockEndpoint mockSubkey = getMockEndpoint("mock:unencrypted");
        mockSubkey.expectedBodiesReceived(payload);
        template.sendBody("direct:subkey", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExceptionDecryptorIncorrectInputFormatNoPGPMessage() throws Exception {
        String payload = "Not Correct Format";
        MockEndpoint mock = getMockEndpoint("mock:exception");
        mock.expectedMessageCount(1);
        template.sendBody("direct:subkeyUnmarshal", payload);
        assertMockEndpointsSatisfied();
        PGPDataFormatTest.checkThrownException(mock, IllegalArgumentException.class, null, "The input message body has an invalid format.");
    }

    @Test
    public void testExceptionDecryptorIncorrectInputFormatPGPSignedData() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        createSignature(bos);
        MockEndpoint mock = getMockEndpoint("mock:exception");
        mock.expectedMessageCount(1);
        template.sendBody("direct:subkeyUnmarshal", bos.toByteArray());
        assertMockEndpointsSatisfied();
        PGPDataFormatTest.checkThrownException(mock, IllegalArgumentException.class, null, "The input message body has an invalid format.");
    }

    @Test
    public void testEncryptSignWithoutCompressedDataPacket() throws Exception {
        doRoundTripEncryptionTests("direct:encrypt-sign-without-compressed-data-packet");
        // ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 
        // //        createEncryptedNonCompressedData(bos, PUB_KEY_RING_SUBKEYS_FILE_NAME);
        // 
        // MockEndpoint mock = getMockEndpoint("mock:exception");
        // mock.expectedMessageCount(1);
        // template.sendBody("direct:encrypt-sign-without-compressed-data-packet", bos.toByteArray());
        // assertMockEndpointsSatisfied();
        // 
        // //checkThrownException(mock, IllegalArgumentException.class, null, "The input message body has an invalid format.");
    }

    @Test
    public void testExceptionDecryptorNoKeyFound() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        createEncryptedNonCompressedData(bos, PGPDataFormatTest.PUB_KEY_RING_FILE_NAME);
        MockEndpoint mock = getMockEndpoint("mock:exception");
        mock.expectedMessageCount(1);
        template.sendBody("direct:subkeyUnmarshal", bos.toByteArray());
        assertMockEndpointsSatisfied();
        PGPDataFormatTest.checkThrownException(mock, PGPException.class, null, "PGP message is encrypted with a key which could not be found in the Secret Keyring");
    }

    @Test
    public void testExceptionDecryptorIncorrectInputFormatSymmetricEncryptedData() throws Exception {
        byte[] payload = "Not Correct Format".getBytes("UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(setSecureRandom(new SecureRandom()).setProvider(getProvider()));
        encGen.addMethod(new JcePBEKeyEncryptionMethodGenerator("pw".toCharArray()));
        OutputStream encOut = encGen.open(bos, new byte[1024]);
        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);
        OutputStream comOut = new BufferedOutputStream(comData.open(encOut));
        PGPLiteralDataGenerator litData = new PGPLiteralDataGenerator();
        OutputStream litOut = litData.open(comOut, BINARY, CONSOLE, new Date(), new byte[1024]);
        litOut.write(payload);
        litOut.flush();
        litOut.close();
        comOut.close();
        encOut.close();
        MockEndpoint mock = getMockEndpoint("mock:exception");
        mock.expectedMessageCount(1);
        template.sendBody("direct:subkeyUnmarshal", bos.toByteArray());
        assertMockEndpointsSatisfied();
        PGPDataFormatTest.checkThrownException(mock, IllegalArgumentException.class, null, "The input message body has an invalid format.");
    }

    @Test
    public void testExceptionForSignatureVerificationOptionNoSignatureAllowed() throws Exception {
        decryptor.setSignatureVerificationOption(SIGNATURE_VERIFICATION_OPTION_NO_SIGNATURE_ALLOWED);
        MockEndpoint mock = getMockEndpoint("mock:exception");
        mock.expectedMessageCount(1);
        template.sendBody("direct:subkey", "Test Message");
        assertMockEndpointsSatisfied();
        PGPDataFormatTest.checkThrownException(mock, PGPException.class, null, "PGP message contains a signature although a signature is not expected");
    }

    @Test
    public void testExceptionForSignatureVerificationOptionRequired() throws Exception {
        encryptor.setSignatureKeyUserid(null);// no signature

        decryptor.setSignatureVerificationOption(SIGNATURE_VERIFICATION_OPTION_REQUIRED);
        MockEndpoint mock = getMockEndpoint("mock:exception");
        mock.expectedMessageCount(1);
        template.sendBody("direct:subkey", "Test Message");
        assertMockEndpointsSatisfied();
        PGPDataFormatTest.checkThrownException(mock, PGPException.class, null, "PGP message does not contain any signatures although a signature is expected");
    }

    @Test
    public void testSignatureVerificationOptionIgnore() throws Exception {
        // encryptor is sending a PGP message with signature! Decryptor is ignoreing the signature
        decryptor.setSignatureVerificationOption(SIGNATURE_VERIFICATION_OPTION_IGNORE);
        decryptor.setSignatureKeyUserids(null);
        decryptor.setSignatureKeyFileName(null);// no public keyring! --> no signature validation possible

        String payload = "Test Message";
        MockEndpoint mock = getMockEndpoint("mock:unencrypted");
        mock.expectedBodiesReceived(payload);
        template.sendBody("direct:subkey", payload);
        assertMockEndpointsSatisfied();
    }
}

