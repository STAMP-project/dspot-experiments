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


import OpenPgpTrustStore.Trust.trusted;
import OpenPgpTrustStore.Trust.undecided;
import OpenPgpTrustStore.Trust.untrusted;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.jivesoftware.smack.test.util.FileTestUtil;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smackx.ox.callback.SecretKeyPassphraseCallback;
import org.jivesoftware.smackx.ox.exception.MissingUserIdOnKeyException;
import org.jivesoftware.smackx.ox.store.definition.OpenPgpStore;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.JidTestUtil;
import org.pgpainless.key.OpenPgpV4Fingerprint;
import org.pgpainless.key.collection.PGPKeyRing;
import org.pgpainless.key.protection.UnprotectedKeysProtector;
import org.pgpainless.util.Passphrase;


@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OpenPgpStoreTest extends SmackTestSuite {
    private static final File storagePath;

    private static final BareJid alice = JidTestUtil.BARE_JID_1;

    private static final BareJid bob = JidTestUtil.BARE_JID_2;

    private static final OpenPgpV4Fingerprint finger1 = new OpenPgpV4Fingerprint("DEADBEEFDEADBEEFDEADBEEFDEADBEEFDEADBEEF");

    private static final OpenPgpV4Fingerprint finger2 = new OpenPgpV4Fingerprint("C0FFEEC0FFEEC0FFEEC0FFEEC0FFEEC0FFEE1234");

    private static final OpenPgpV4Fingerprint finger3 = new OpenPgpV4Fingerprint("0123012301230123012301230123012301230123");

    private final OpenPgpStore openPgpStoreInstance1;

    private final OpenPgpStore openPgpStoreInstance2;

    static {
        storagePath = FileTestUtil.getTempDir("storeTest");
        Security.addProvider(new BouncyCastleProvider());
    }

    public OpenPgpStoreTest(OpenPgpStore firstInstance, OpenPgpStore secondInstance) {
        if ((firstInstance == secondInstance) || (!(firstInstance.getClass().equals(secondInstance.getClass())))) {
            throw new IllegalArgumentException("firstInstance must be another instance of the same class as secondInstance.");
        }
        this.openPgpStoreInstance1 = firstInstance;
        this.openPgpStoreInstance2 = secondInstance;
    }

    /* Generic */
    @Test
    public void t00_store_protectorGetSet() {
        openPgpStoreInstance1.setKeyRingProtector(new UnprotectedKeysProtector());
        TestCase.assertNotNull(openPgpStoreInstance1.getKeyRingProtector());
        // TODO: Test method below
        openPgpStoreInstance1.setSecretKeyPassphraseCallback(new SecretKeyPassphraseCallback() {
            @Override
            public Passphrase onPassphraseNeeded(OpenPgpV4Fingerprint fingerprint) {
                return null;
            }
        });
    }

    /* OpenPgpKeyStore */
    @Test
    public void t00_deleteTest() throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, PGPException, MissingUserIdOnKeyException {
        TestCase.assertNull(openPgpStoreInstance1.getSecretKeysOf(OpenPgpStoreTest.alice));
        TestCase.assertNull(openPgpStoreInstance1.getPublicKeysOf(OpenPgpStoreTest.alice));
        PGPKeyRing keys = openPgpStoreInstance1.generateKeyRing(OpenPgpStoreTest.alice);
        openPgpStoreInstance1.importSecretKey(OpenPgpStoreTest.alice, keys.getSecretKeys());
        openPgpStoreInstance1.importPublicKey(OpenPgpStoreTest.alice, keys.getPublicKeys());
        TestCase.assertNotNull(openPgpStoreInstance1.getSecretKeysOf(OpenPgpStoreTest.alice));
        TestCase.assertNotNull(openPgpStoreInstance1.getPublicKeysOf(OpenPgpStoreTest.alice));
        openPgpStoreInstance1.deleteSecretKeyRing(OpenPgpStoreTest.alice, new OpenPgpV4Fingerprint(keys.getSecretKeys()));
        openPgpStoreInstance1.deletePublicKeyRing(OpenPgpStoreTest.alice, new OpenPgpV4Fingerprint(keys.getSecretKeys()));
        TestCase.assertNull(openPgpStoreInstance1.getPublicKeysOf(OpenPgpStoreTest.alice));
        TestCase.assertNull(openPgpStoreInstance1.getSecretKeysOf(OpenPgpStoreTest.alice));
    }

    @Test
    public void t01_key_emptyStoreTest() throws IOException, PGPException {
        TestCase.assertNull(openPgpStoreInstance1.getPublicKeysOf(OpenPgpStoreTest.alice));
        TestCase.assertNull(openPgpStoreInstance1.getSecretKeysOf(OpenPgpStoreTest.alice));
        TestCase.assertNull(openPgpStoreInstance1.getPublicKeyRing(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger1));
        TestCase.assertNull(openPgpStoreInstance1.getSecretKeyRing(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger1));
    }

    @Test
    public void t02_key_importKeysTest() throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, PGPException, MissingUserIdOnKeyException {
        // Test for nullity of all possible values.
        PGPKeyRing keys = openPgpStoreInstance1.generateKeyRing(OpenPgpStoreTest.alice);
        PGPSecretKeyRing secretKeys = keys.getSecretKeys();
        PGPPublicKeyRing publicKeys = keys.getPublicKeys();
        TestCase.assertNotNull(secretKeys);
        TestCase.assertNotNull(publicKeys);
        OpenPgpContact cAlice = openPgpStoreInstance1.getOpenPgpContact(OpenPgpStoreTest.alice);
        TestCase.assertNull(cAlice.getAnyPublicKeys());
        OpenPgpV4Fingerprint fingerprint = new OpenPgpV4Fingerprint(publicKeys);
        TestCase.assertEquals(fingerprint, new OpenPgpV4Fingerprint(secretKeys));
        TestCase.assertNull(openPgpStoreInstance1.getPublicKeysOf(OpenPgpStoreTest.alice));
        TestCase.assertNull(openPgpStoreInstance1.getSecretKeysOf(OpenPgpStoreTest.alice));
        openPgpStoreInstance1.importPublicKey(OpenPgpStoreTest.alice, publicKeys);
        TestCase.assertTrue(Arrays.equals(publicKeys.getEncoded(), openPgpStoreInstance1.getPublicKeysOf(OpenPgpStoreTest.alice).getEncoded()));
        TestCase.assertNotNull(openPgpStoreInstance1.getPublicKeyRing(OpenPgpStoreTest.alice, fingerprint));
        TestCase.assertNull(openPgpStoreInstance1.getSecretKeysOf(OpenPgpStoreTest.alice));
        cAlice = openPgpStoreInstance1.getOpenPgpContact(OpenPgpStoreTest.alice);
        TestCase.assertNotNull(cAlice.getAnyPublicKeys());
        // Import keys a second time -> No change expected.
        openPgpStoreInstance1.importPublicKey(OpenPgpStoreTest.alice, publicKeys);
        TestCase.assertTrue(Arrays.equals(publicKeys.getEncoded(), openPgpStoreInstance1.getPublicKeysOf(OpenPgpStoreTest.alice).getEncoded()));
        openPgpStoreInstance1.importSecretKey(OpenPgpStoreTest.alice, secretKeys);
        TestCase.assertTrue(Arrays.equals(secretKeys.getEncoded(), openPgpStoreInstance1.getSecretKeysOf(OpenPgpStoreTest.alice).getEncoded()));
        openPgpStoreInstance1.importSecretKey(OpenPgpStoreTest.alice, secretKeys);
        TestCase.assertNotNull(openPgpStoreInstance1.getSecretKeysOf(OpenPgpStoreTest.alice));
        TestCase.assertTrue(Arrays.equals(secretKeys.getEncoded(), openPgpStoreInstance1.getSecretKeysOf(OpenPgpStoreTest.alice).getEncoded()));
        TestCase.assertNotNull(openPgpStoreInstance1.getSecretKeyRing(OpenPgpStoreTest.alice, fingerprint));
        TestCase.assertTrue(Arrays.equals(secretKeys.getEncoded(), openPgpStoreInstance1.getSecretKeyRing(OpenPgpStoreTest.alice, fingerprint).getEncoded()));
        TestCase.assertTrue(Arrays.equals(publicKeys.getEncoded(), openPgpStoreInstance1.getPublicKeyRing(OpenPgpStoreTest.alice, fingerprint).getEncoded()));
        // Clean up
        openPgpStoreInstance1.deletePublicKeyRing(OpenPgpStoreTest.alice, fingerprint);
        openPgpStoreInstance1.deleteSecretKeyRing(OpenPgpStoreTest.alice, fingerprint);
    }

    @Test(expected = MissingUserIdOnKeyException.class)
    public void t04_key_wrongBareJidOnSecretKeyImportTest() throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, PGPException, MissingUserIdOnKeyException {
        PGPSecretKeyRing secretKeys = openPgpStoreInstance1.generateKeyRing(OpenPgpStoreTest.alice).getSecretKeys();
        openPgpStoreInstance1.importSecretKey(OpenPgpStoreTest.bob, secretKeys);
    }

    @Test(expected = MissingUserIdOnKeyException.class)
    public void t05_key_wrongBareJidOnPublicKeyImportTest() throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, PGPException, MissingUserIdOnKeyException {
        PGPPublicKeyRing publicKeys = openPgpStoreInstance1.generateKeyRing(OpenPgpStoreTest.alice).getPublicKeys();
        openPgpStoreInstance1.importPublicKey(OpenPgpStoreTest.bob, publicKeys);
    }

    @Test
    public void t06_key_keyReloadTest() throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, PGPException, MissingUserIdOnKeyException {
        PGPKeyRing keys = openPgpStoreInstance1.generateKeyRing(OpenPgpStoreTest.alice);
        PGPSecretKeyRing secretKeys = keys.getSecretKeys();
        OpenPgpV4Fingerprint fingerprint = new OpenPgpV4Fingerprint(secretKeys);
        PGPPublicKeyRing publicKeys = keys.getPublicKeys();
        openPgpStoreInstance1.importSecretKey(OpenPgpStoreTest.alice, secretKeys);
        openPgpStoreInstance1.importPublicKey(OpenPgpStoreTest.alice, publicKeys);
        TestCase.assertNotNull(openPgpStoreInstance2.getSecretKeysOf(OpenPgpStoreTest.alice));
        TestCase.assertNotNull(openPgpStoreInstance2.getPublicKeysOf(OpenPgpStoreTest.alice));
        // Clean up
        openPgpStoreInstance1.deletePublicKeyRing(OpenPgpStoreTest.alice, fingerprint);
        openPgpStoreInstance1.deleteSecretKeyRing(OpenPgpStoreTest.alice, fingerprint);
        openPgpStoreInstance2.deletePublicKeyRing(OpenPgpStoreTest.alice, fingerprint);
        openPgpStoreInstance2.deleteSecretKeyRing(OpenPgpStoreTest.alice, fingerprint);
    }

    @Test
    public void t07_multipleKeysTest() throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, PGPException, MissingUserIdOnKeyException {
        PGPKeyRing one = openPgpStoreInstance1.generateKeyRing(OpenPgpStoreTest.alice);
        PGPKeyRing two = openPgpStoreInstance1.generateKeyRing(OpenPgpStoreTest.alice);
        OpenPgpV4Fingerprint fingerprint1 = new OpenPgpV4Fingerprint(one.getSecretKeys());
        OpenPgpV4Fingerprint fingerprint2 = new OpenPgpV4Fingerprint(two.getSecretKeys());
        openPgpStoreInstance1.importSecretKey(OpenPgpStoreTest.alice, one.getSecretKeys());
        openPgpStoreInstance1.importSecretKey(OpenPgpStoreTest.alice, two.getSecretKeys());
        openPgpStoreInstance1.importPublicKey(OpenPgpStoreTest.alice, one.getPublicKeys());
        openPgpStoreInstance1.importPublicKey(OpenPgpStoreTest.alice, two.getPublicKeys());
        TestCase.assertTrue(Arrays.equals(one.getSecretKeys().getEncoded(), openPgpStoreInstance1.getSecretKeyRing(OpenPgpStoreTest.alice, fingerprint1).getEncoded()));
        TestCase.assertTrue(Arrays.equals(two.getSecretKeys().getEncoded(), openPgpStoreInstance1.getSecretKeyRing(OpenPgpStoreTest.alice, fingerprint2).getEncoded()));
        TestCase.assertTrue(Arrays.equals(one.getSecretKeys().getEncoded(), openPgpStoreInstance1.getSecretKeysOf(OpenPgpStoreTest.alice).getSecretKeyRing(fingerprint1.getKeyId()).getEncoded()));
        TestCase.assertTrue(Arrays.equals(one.getPublicKeys().getEncoded(), openPgpStoreInstance1.getPublicKeyRing(OpenPgpStoreTest.alice, fingerprint1).getEncoded()));
        // Cleanup
        openPgpStoreInstance1.deletePublicKeyRing(OpenPgpStoreTest.alice, fingerprint1);
        openPgpStoreInstance1.deletePublicKeyRing(OpenPgpStoreTest.alice, fingerprint2);
        openPgpStoreInstance1.deleteSecretKeyRing(OpenPgpStoreTest.alice, fingerprint1);
        openPgpStoreInstance1.deleteSecretKeyRing(OpenPgpStoreTest.alice, fingerprint2);
    }

    /* OpenPgpTrustStore */
    @Test
    public void t08_trust_emptyStoreTest() throws IOException {
        TestCase.assertEquals(undecided, openPgpStoreInstance1.getTrust(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger2));
        openPgpStoreInstance1.setTrust(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger2, trusted);
        TestCase.assertEquals(trusted, openPgpStoreInstance1.getTrust(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger2));
        // Set trust a second time -> no change
        openPgpStoreInstance1.setTrust(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger2, trusted);
        TestCase.assertEquals(trusted, openPgpStoreInstance1.getTrust(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger2));
        TestCase.assertEquals(undecided, openPgpStoreInstance1.getTrust(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger3));
        openPgpStoreInstance1.setTrust(OpenPgpStoreTest.bob, OpenPgpStoreTest.finger2, untrusted);
        TestCase.assertEquals(untrusted, openPgpStoreInstance1.getTrust(OpenPgpStoreTest.bob, OpenPgpStoreTest.finger2));
        TestCase.assertEquals(trusted, openPgpStoreInstance1.getTrust(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger2));
        // clean up
        openPgpStoreInstance1.setTrust(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger2, undecided);
        openPgpStoreInstance1.setTrust(OpenPgpStoreTest.bob, OpenPgpStoreTest.finger2, undecided);
    }

    @Test
    public void t09_trust_reloadTest() throws IOException {
        openPgpStoreInstance1.setTrust(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger1, trusted);
        TestCase.assertEquals(trusted, openPgpStoreInstance2.getTrust(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger1));
        // cleanup
        openPgpStoreInstance1.setTrust(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger1, undecided);
        openPgpStoreInstance2.setTrust(OpenPgpStoreTest.alice, OpenPgpStoreTest.finger1, undecided);
    }

    /* OpenPgpMetadataStore */
    @Test
    public void t10_meta_emptyStoreTest() throws IOException {
        TestCase.assertNotNull(openPgpStoreInstance1.getAnnouncedFingerprintsOf(OpenPgpStoreTest.alice));
        TestCase.assertTrue(openPgpStoreInstance1.getAnnouncedFingerprintsOf(OpenPgpStoreTest.alice).isEmpty());
        Map<OpenPgpV4Fingerprint, Date> map = new HashMap<>();
        Date date1 = new Date(12354563423L);
        Date date2 = new Date(8274729879812L);
        map.put(OpenPgpStoreTest.finger1, date1);
        map.put(OpenPgpStoreTest.finger2, date2);
        openPgpStoreInstance1.setAnnouncedFingerprintsOf(OpenPgpStoreTest.alice, map);
        TestCase.assertFalse(openPgpStoreInstance1.getAnnouncedFingerprintsOf(OpenPgpStoreTest.alice).isEmpty());
        TestCase.assertEquals(map, openPgpStoreInstance1.getAnnouncedFingerprintsOf(OpenPgpStoreTest.alice));
        TestCase.assertTrue(openPgpStoreInstance1.getAnnouncedFingerprintsOf(OpenPgpStoreTest.bob).isEmpty());
        TestCase.assertFalse(openPgpStoreInstance2.getAnnouncedFingerprintsOf(OpenPgpStoreTest.alice).isEmpty());
        TestCase.assertEquals(map, openPgpStoreInstance2.getAnnouncedFingerprintsOf(OpenPgpStoreTest.alice));
        openPgpStoreInstance1.setAnnouncedFingerprintsOf(OpenPgpStoreTest.alice, Collections.<OpenPgpV4Fingerprint, Date>emptyMap());
        openPgpStoreInstance2.setAnnouncedFingerprintsOf(OpenPgpStoreTest.alice, Collections.<OpenPgpV4Fingerprint, Date>emptyMap());
    }

    @Test
    public void t11_key_fetchDateTest() throws IOException {
        Map<OpenPgpV4Fingerprint, Date> fetchDates1 = openPgpStoreInstance1.getPublicKeyFetchDates(OpenPgpStoreTest.alice);
        TestCase.assertNotNull(fetchDates1);
        TestCase.assertTrue(fetchDates1.isEmpty());
        Date date1 = new Date(85092830954L);
        fetchDates1.put(OpenPgpStoreTest.finger1, date1);
        openPgpStoreInstance1.setPublicKeyFetchDates(OpenPgpStoreTest.alice, fetchDates1);
        Map<OpenPgpV4Fingerprint, Date> fetchDates2 = openPgpStoreInstance1.getPublicKeyFetchDates(OpenPgpStoreTest.alice);
        TestCase.assertNotNull(fetchDates2);
        TestCase.assertFalse(fetchDates2.isEmpty());
        TestCase.assertEquals(fetchDates1, fetchDates2);
        Map<OpenPgpV4Fingerprint, Date> fetchDates3 = openPgpStoreInstance2.getPublicKeyFetchDates(OpenPgpStoreTest.alice);
        TestCase.assertNotNull(fetchDates3);
        TestCase.assertEquals(fetchDates1, fetchDates3);
        openPgpStoreInstance1.setPublicKeyFetchDates(OpenPgpStoreTest.alice, null);
        openPgpStoreInstance2.setPublicKeyFetchDates(OpenPgpStoreTest.alice, null);
        TestCase.assertNotNull(openPgpStoreInstance1.getPublicKeyFetchDates(OpenPgpStoreTest.alice));
        TestCase.assertTrue(openPgpStoreInstance1.getPublicKeyFetchDates(OpenPgpStoreTest.alice).isEmpty());
    }
}

