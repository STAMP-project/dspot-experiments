/**
 * Copyright 2018 Paul Schaub.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smackx.ox;


import Message.Body;
import Message.Body.ELEMENT;
import Message.Body.NAMESPACE;
import OpenPgpMessage.State.crypt;
import OpenPgpMessage.State.sign;
import OpenPgpMessage.State.signcrypt;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;
import org.bouncycastle.openpgp.PGPException;
import org.jivesoftware.smack.DummyConnection;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.test.util.FileTestUtil;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smackx.ox.crypto.OpenPgpElementAndMetadata;
import org.jivesoftware.smackx.ox.crypto.PainlessOpenPgpProvider;
import org.jivesoftware.smackx.ox.element.CryptElement;
import org.jivesoftware.smackx.ox.element.SignElement;
import org.jivesoftware.smackx.ox.element.SigncryptElement;
import org.jivesoftware.smackx.ox.exception.MissingUserIdOnKeyException;
import org.jivesoftware.smackx.ox.store.definition.OpenPgpStore;
import org.jivesoftware.smackx.ox.store.filebased.FileBasedOpenPgpStore;
import org.junit.Test;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.JidTestUtil;
import org.pgpainless.key.OpenPgpV4Fingerprint;
import org.pgpainless.key.collection.PGPKeyRing;
import org.pgpainless.key.protection.UnprotectedKeysProtector;
import org.xmlpull.v1.XmlPullParserException;


public class PainlessOpenPgpProviderTest extends SmackTestSuite {
    private static final File storagePath;

    private static final BareJid alice = JidTestUtil.BARE_JID_1;

    private static final BareJid bob = JidTestUtil.BARE_JID_2;

    static {
        storagePath = FileTestUtil.getTempDir("smack-painlessprovidertest");
    }

    @Test
    public void encryptDecryptTest() throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, PGPException, MissingUserIdOnKeyException, XmlPullParserException {
        // Initialize
        OpenPgpStore aliceStore = new FileBasedOpenPgpStore(PainlessOpenPgpProviderTest.storagePath);
        OpenPgpStore bobStore = new FileBasedOpenPgpStore(PainlessOpenPgpProviderTest.storagePath);
        aliceStore.setKeyRingProtector(new UnprotectedKeysProtector());
        bobStore.setKeyRingProtector(new UnprotectedKeysProtector());
        PainlessOpenPgpProvider aliceProvider = new PainlessOpenPgpProvider(new DummyConnection(), aliceStore);
        PainlessOpenPgpProvider bobProvider = new PainlessOpenPgpProvider(new DummyConnection(), bobStore);
        PGPKeyRing aliceKeys = aliceStore.generateKeyRing(PainlessOpenPgpProviderTest.alice);
        PGPKeyRing bobKeys = bobStore.generateKeyRing(PainlessOpenPgpProviderTest.bob);
        OpenPgpV4Fingerprint aliceFingerprint = new OpenPgpV4Fingerprint(aliceKeys.getPublicKeys());
        OpenPgpV4Fingerprint bobFingerprint = new OpenPgpV4Fingerprint(bobKeys.getPublicKeys());
        aliceStore.importSecretKey(PainlessOpenPgpProviderTest.alice, aliceKeys.getSecretKeys());
        bobStore.importSecretKey(PainlessOpenPgpProviderTest.bob, bobKeys.getSecretKeys());
        aliceStore.setAnnouncedFingerprintsOf(PainlessOpenPgpProviderTest.alice, Collections.singletonMap(new OpenPgpV4Fingerprint(aliceKeys.getPublicKeys()), new Date()));
        bobStore.setAnnouncedFingerprintsOf(PainlessOpenPgpProviderTest.bob, Collections.singletonMap(new OpenPgpV4Fingerprint(bobKeys.getPublicKeys()), new Date()));
        OpenPgpSelf aliceSelf = new OpenPgpSelf(PainlessOpenPgpProviderTest.alice, aliceStore);
        aliceSelf.trust(aliceFingerprint);
        OpenPgpSelf bobSelf = new OpenPgpSelf(PainlessOpenPgpProviderTest.bob, bobStore);
        bobSelf.trust(bobFingerprint);
        // Exchange keys
        aliceStore.importPublicKey(PainlessOpenPgpProviderTest.bob, bobKeys.getPublicKeys());
        bobStore.importPublicKey(PainlessOpenPgpProviderTest.alice, aliceKeys.getPublicKeys());
        aliceStore.setAnnouncedFingerprintsOf(PainlessOpenPgpProviderTest.bob, Collections.singletonMap(new OpenPgpV4Fingerprint(bobKeys.getPublicKeys()), new Date()));
        bobStore.setAnnouncedFingerprintsOf(PainlessOpenPgpProviderTest.alice, Collections.singletonMap(new OpenPgpV4Fingerprint(aliceKeys.getPublicKeys()), new Date()));
        OpenPgpContact aliceForBob = new OpenPgpContact(PainlessOpenPgpProviderTest.alice, bobStore);
        aliceForBob.trust(aliceFingerprint);
        OpenPgpContact bobForAlice = new OpenPgpContact(PainlessOpenPgpProviderTest.bob, aliceStore);
        bobForAlice.trust(bobFingerprint);
        // Prepare message
        Message.Body body = new Message.Body(null, "Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        List<ExtensionElement> payload = Collections.<ExtensionElement>singletonList(body);
        OpenPgpElementAndMetadata encrypted;
        OpenPgpMessage decrypted;
        /* test signcrypt */
        SigncryptElement signcryptElement = new SigncryptElement(Collections.<Jid>singleton(PainlessOpenPgpProviderTest.bob), payload);
        // Encrypt and Sign
        encrypted = aliceProvider.signAndEncrypt(signcryptElement, aliceSelf, Collections.singleton(bobForAlice));
        // Decrypt and Verify
        decrypted = bobProvider.decryptAndOrVerify(encrypted.getElement(), bobSelf, aliceForBob);
        OpenPgpV4Fingerprint decryptionFingerprint = decrypted.getMetadata().getDecryptionFingerprint();
        TestCase.assertTrue(bobSelf.getSecretKeys().contains(decryptionFingerprint.getKeyId()));
        TestCase.assertTrue(decrypted.getMetadata().getVerifiedSignaturesFingerprints().contains(aliceFingerprint));
        TestCase.assertEquals(signcrypt, decrypted.getState());
        SigncryptElement decryptedSignCrypt = ((SigncryptElement) (decrypted.getOpenPgpContentElement()));
        TestCase.assertEquals(body.getMessage(), decryptedSignCrypt.<Message.Body>getExtension(ELEMENT, NAMESPACE).getMessage());
        /* test crypt */
        CryptElement cryptElement = new CryptElement(Collections.<Jid>singleton(PainlessOpenPgpProviderTest.bob), payload);
        // Encrypt
        encrypted = aliceProvider.encrypt(cryptElement, aliceSelf, Collections.singleton(bobForAlice));
        decrypted = bobProvider.decryptAndOrVerify(encrypted.getElement(), bobSelf, aliceForBob);
        decryptionFingerprint = decrypted.getMetadata().getDecryptionFingerprint();
        TestCase.assertTrue(bobSelf.getSecretKeys().contains(decryptionFingerprint.getKeyId()));
        TestCase.assertTrue(decrypted.getMetadata().getVerifiedSignaturesFingerprints().isEmpty());
        TestCase.assertEquals(crypt, decrypted.getState());
        CryptElement decryptedCrypt = ((CryptElement) (decrypted.getOpenPgpContentElement()));
        TestCase.assertEquals(body.getMessage(), decryptedCrypt.<Message.Body>getExtension(ELEMENT, NAMESPACE).getMessage());
        /* test sign */
        SignElement signElement = new SignElement(Collections.<Jid>singleton(PainlessOpenPgpProviderTest.bob), new Date(), payload);
        // Sign
        encrypted = aliceProvider.sign(signElement, aliceSelf);
        decrypted = bobProvider.decryptAndOrVerify(encrypted.getElement(), bobSelf, aliceForBob);
        TestCase.assertNull(decrypted.getMetadata().getDecryptionFingerprint());
        TestCase.assertTrue(decrypted.getMetadata().getVerifiedSignaturesFingerprints().contains(aliceFingerprint));
        TestCase.assertEquals(sign, decrypted.getState());
        SignElement decryptedSign = ((SignElement) (decrypted.getOpenPgpContentElement()));
        TestCase.assertEquals(body.getMessage(), decryptedSign.<Message.Body>getExtension(ELEMENT, NAMESPACE).getMessage());
    }
}

