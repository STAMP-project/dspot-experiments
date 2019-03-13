/**
 * Copyright (C) 2014-2017 Vincent Breitmoser <v.breitmoser@mugenguild.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sufficientlysecure.keychain.pgp;


import DecryptVerifyResult.RESULT_KEY_DISALLOWED;
import KeyFlags.CERTIFY_OTHER;
import LogType.MSG_DC_ASKIP_BAD_FLAGS;
import LogType.MSG_DC_ASKIP_NOT_ALLOWED;
import LogType.MSG_DC_ASKIP_UNAVAILABLE;
import OpenPgpDecryptionResult.RESULT_ENCRYPTED;
import OpenPgpDecryptionResult.RESULT_INSECURE;
import OpenPgpDecryptionResult.RESULT_NOT_ENCRYPTED;
import OpenPgpSignatureResult.RESULT_KEY_MISSING;
import OpenPgpSignatureResult.RESULT_NO_SIGNATURE;
import OpenPgpSignatureResult.RESULT_VALID_KEY_CONFIRMED;
import PacketTags.SYM_ENC_INTEGRITY_PRO;
import PgpSecurityConstants.OpenKeychainSymmetricKeyAlgorithmTags.AES_128;
import PgpSignEncryptData.Builder;
import RequiredInputType.PASSPHRASE;
import RuntimeEnvironment.application;
import StringUtils.LINE_SEP;
import SymmetricKeyAlgorithmTags.DES;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.bouncycastle.bcpg.BCPGInputStream;
import org.bouncycastle.bcpg.Packet;
import org.bouncycastle.bcpg.PublicKeyEncSessionPacket;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openintents.openpgp.OpenPgpMetadata;
import org.robolectric.RuntimeEnvironment;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.daos.KeyWritableRepository;
import org.sufficientlysecure.keychain.operations.results.DecryptVerifyResult;
import org.sufficientlysecure.keychain.operations.results.PgpSignEncryptResult;
import org.sufficientlysecure.keychain.pgp.SecurityProblem.InsecureEncryptionAlgorithm;
import org.sufficientlysecure.keychain.service.SaveKeyringParcel;
import org.sufficientlysecure.keychain.service.SaveKeyringParcel.SubkeyChange;
import org.sufficientlysecure.keychain.service.input.CryptoInputParcel;
import org.sufficientlysecure.keychain.service.input.RequiredInputParcel;
import org.sufficientlysecure.keychain.support.KeyringTestingHelper;
import org.sufficientlysecure.keychain.util.InputData;
import org.sufficientlysecure.keychain.util.Passphrase;
import org.sufficientlysecure.keychain.util.TestingUtils;


/* skeleton for generating test data
@Test
public void generateData() throws IOException {
ByteArrayOutputStream out = new ByteArrayOutputStream();
ByteArrayInputStream in = new ByteArrayInputStream("dies ist ein plaintext ?".getBytes());

PgpSignEncryptOperation op = new PgpSignEncryptOperation(RuntimeEnvironment.application,
KeyWritableRepository.createDatabaseReadWriteInteractor(RuntimeEnvironment.application), null);

InputData data = new InputData(in, in.available());

PgpSignEncryptData.Builder pgpData = PgpSignEncryptData.builder();
pgpData.setEncryptionMasterKeyIds(new long[]{ mStaticRingInsecure.getMasterKeyId()});

PgpSignEncryptInputParcel input = new PgpSignEncryptInputParcel(pgpData);

PgpSignEncryptResult result = op.execute(input, new CryptoInputParcel(new Date()),
data, out);
Assert.assertTrue("encryption must succeed", result.success());

ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(new FileOutputStream("/tmp/rsa_1024.pgp.asc"));
armoredOutputStream.write(out.toByteArray());
armoredOutputStream.close();
}
 */
@SuppressWarnings("WeakerAccess")
@RunWith(KeychainTestRunner.class)
public class PgpEncryptDecryptTest {
    static Passphrase mSymmetricPassphrase = TestingUtils.testPassphrase0;

    static UncachedKeyRing mStaticRing1;

    static UncachedKeyRing mStaticRing2;

    static UncachedKeyRing mStaticRingInsecure;

    static Passphrase mKeyPhrase1;

    static Passphrase mKeyPhrase2;

    static PrintStream oldShadowStream;

    @Test
    public void testSymmetricEncryptDecrypt() {
        String plaintext = "dies ist ein plaintext ?";
        byte[] ciphertext;
        {
            // encrypt data with a given passphrase
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
            PgpSignEncryptOperation op = new PgpSignEncryptOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            InputData data = new InputData(in, in.available());
            PgpSignEncryptData.Builder pgpData = PgpSignEncryptData.builder();
            pgpData.setSymmetricPassphrase(PgpEncryptDecryptTest.mSymmetricPassphrase);
            pgpData.setSymmetricEncryptionAlgorithm(AES_128);
            PgpSignEncryptResult result = op.execute(pgpData.build(), CryptoInputParcel.createCryptoInputParcel(new Date()), data, out);
            Assert.assertTrue("encryption must succeed", result.success());
            ciphertext = out.toByteArray();
        }
        {
            // decryption with same passphrase should yield the same plaintext
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = new PgpDecryptVerifyOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().setAllowSymmetricDecryption(true).build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(PgpEncryptDecryptTest.mSymmetricPassphrase), data, out);
            Assert.assertTrue("decryption must succeed", result.success());
            Assert.assertArrayEquals("decrypted ciphertext should equal plaintext", out.toByteArray(), plaintext.getBytes());
            Assert.assertEquals("decryptionResult should be RESULT_ENCRYPTED", RESULT_ENCRYPTED, result.getDecryptionResult().getResult());
            Assert.assertEquals("signatureResult should be RESULT_NO_SIGNATURE", RESULT_NO_SIGNATURE, result.getSignatureResult().getResult());
            CryptoInputParcel cryptoInput = result.getCachedCryptoInputParcel();
            Assert.assertEquals("cached session keys must be empty", 0, cryptoInput.getCryptoData().size());
            OpenPgpMetadata metadata = result.getDecryptionMetadata();
            Assert.assertEquals("filesize must be correct", out.toByteArray().length, metadata.getOriginalSize());
        }
        {
            // decryption with a bad passphrase should fail
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = new PgpDecryptVerifyOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().setAllowSymmetricDecryption(true).build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(new Passphrase(((new String(PgpEncryptDecryptTest.mSymmetricPassphrase.getCharArray())) + "x"))), data, out);
            Assert.assertFalse("decryption must fail", result.success());
            Assert.assertEquals("decrypted plaintext should be empty", 0, out.size());
            Assert.assertNull("decryptionResult should be null", result.getDecryptionResult());
            Assert.assertNull("signatureResult should be null", result.getSignatureResult());
        }
        {
            // decryption with an unset passphrase should fail
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = new PgpDecryptVerifyOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().setAllowSymmetricDecryption(true).build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
            Assert.assertFalse("decryption must fail", result.success());
            Assert.assertEquals("decrypted plaintext should be empty", 0, out.size());
            Assert.assertNull("decryptionResult should be null", result.getDecryptionResult());
            Assert.assertNull("signatureResult should be null", result.getSignatureResult());
        }
        {
            // decryption if symmetric decryption isn't allowed should fail
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = new PgpDecryptVerifyOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
            Assert.assertFalse("decryption must fail", result.success());
            Assert.assertEquals("decrypted plaintext should be empty", 0, out.size());
            Assert.assertNull("decryptionResult should be null", result.getDecryptionResult());
            Assert.assertNull("signatureResult should be null", result.getSignatureResult());
        }
    }

    @Test
    public void testAsymmetricSignLiteral() {
        String plaintext = "dies ist ein plaintext ?";
        byte[] ciphertext;
        {
            // encrypt data with key
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
            PgpSignEncryptOperation op = new PgpSignEncryptOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            InputData data = new InputData(in, in.available());
            PgpSignEncryptData.Builder pgpData = PgpSignEncryptData.builder();
            // only sign, and not as cleartext
            pgpData.setSignatureMasterKeyId(PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId());
            pgpData.setSignatureSubKeyId(KeyringTestingHelper.getSubkeyId(PgpEncryptDecryptTest.mStaticRing1, 1));
            pgpData.setCleartextSignature(false);
            pgpData.setDetachedSignature(false);
            PgpSignEncryptResult result = op.execute(pgpData.build(), CryptoInputParcel.createCryptoInputParcel(PgpEncryptDecryptTest.mKeyPhrase1), data, out);
            Assert.assertTrue("signing must succeed", result.success());
            ciphertext = out.toByteArray();
        }
        {
            // verification should succeed
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(null, null, null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
            Assert.assertTrue("verification must succeed", result.success());
            Assert.assertArrayEquals("verification text should equal plaintext", out.toByteArray(), plaintext.getBytes());
            Assert.assertEquals("decryptionResult should be RESULT_NOT_ENCRYPTED", RESULT_NOT_ENCRYPTED, result.getDecryptionResult().getResult());
            Assert.assertEquals("signatureResult should be RESULT_VALID_CONFIRMED", RESULT_VALID_KEY_CONFIRMED, result.getSignatureResult().getResult());
            OpenPgpMetadata metadata = result.getDecryptionMetadata();
            Assert.assertEquals("filesize must be correct", out.toByteArray().length, metadata.getOriginalSize());
        }
    }

    @Test
    public void testAsymmetricSignCleartext() {
        String plaintext = "dies ist ein\r\nplaintext\n \u262d";
        byte[] ciphertext;
        {
            // encrypt data with key
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
            PgpSignEncryptOperation op = new PgpSignEncryptOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            InputData data = new InputData(in, in.available());
            PgpSignEncryptData.Builder pgpData = PgpSignEncryptData.builder();
            // only sign, as cleartext
            pgpData.setSignatureMasterKeyId(PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId());
            pgpData.setSignatureSubKeyId(KeyringTestingHelper.getSubkeyId(PgpEncryptDecryptTest.mStaticRing1, 1));
            pgpData.setCleartextSignature(true);
            pgpData.setEnableAsciiArmorOutput(true);
            pgpData.setDetachedSignature(false);
            PgpSignEncryptResult result = op.execute(pgpData.build(), CryptoInputParcel.createCryptoInputParcel(PgpEncryptDecryptTest.mKeyPhrase1), data, out);
            Assert.assertTrue("signing must succeed", result.success());
            ciphertext = out.toByteArray();
        }
        Assert.assertTrue("clearsigned text must contain plaintext (ignoring newlines)", new String(ciphertext).replace("\r\n", "").contains(plaintext.replace("\r", "").replace("\n", "")));
        {
            // verification should succeed
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(null, null, null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
            Assert.assertTrue("verification must succeed", result.success());
            Assert.assertTrue("verification text should equal plaintext (ignoring newlines)", new String(out.toByteArray()).replace(LINE_SEP, "").equals(plaintext.replace("\r", "").replace("\n", "")));
            Assert.assertEquals("decryptionResult should be RESULT_NOT_ENCRYPTED", RESULT_NOT_ENCRYPTED, result.getDecryptionResult().getResult());
            Assert.assertEquals("signatureResult should be RESULT_VALID_CONFIRMED", RESULT_VALID_KEY_CONFIRMED, result.getSignatureResult().getResult());
            OpenPgpMetadata metadata = result.getDecryptionMetadata();
            Assert.assertEquals("filesize must be correct", out.toByteArray().length, metadata.getOriginalSize());
        }
    }

    @Test
    public void testAsymmetricSignDetached() {
        String plaintext = "dies ist ein plaintext ?";
        byte[] detachedSignature;
        {
            // encrypt data with key
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
            PgpSignEncryptOperation op = new PgpSignEncryptOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            InputData data = new InputData(in, in.available());
            PgpSignEncryptData.Builder pgpData = PgpSignEncryptData.builder();
            // only sign, as cleartext
            pgpData.setSignatureMasterKeyId(PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId());
            pgpData.setSignatureSubKeyId(KeyringTestingHelper.getSubkeyId(PgpEncryptDecryptTest.mStaticRing1, 1));
            pgpData.setDetachedSignature(true);
            PgpSignEncryptResult result = op.execute(pgpData.build(), CryptoInputParcel.createCryptoInputParcel(PgpEncryptDecryptTest.mKeyPhrase1), data, out);
            Assert.assertTrue("signing must succeed", result.success());
            detachedSignature = result.getDetachedSignature();
        }
        {
            // verification should succeed
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(null, null, null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().setDetachedSignature(detachedSignature).build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
            Assert.assertTrue("verification must succeed", result.success());
            Assert.assertArrayEquals("verification text should equal plaintext (save for a newline)", plaintext.getBytes(), out.toByteArray());
            Assert.assertEquals("decryptionResult should be RESULT_NOT_ENCRYPTED", RESULT_NOT_ENCRYPTED, result.getDecryptionResult().getResult());
            Assert.assertEquals("signatureResult should be RESULT_VALID_CONFIRMED", RESULT_VALID_KEY_CONFIRMED, result.getSignatureResult().getResult());
            // TODO should detached verify return any metadata?
            // OpenPgpMetadata metadata = result.getDecryptionMetadata();
            // Assert.assertEquals("filesize must be correct",
            // out.toByteArray().length, metadata.getOriginalSize());
        }
    }

    @Test
    public void testAsymmetricEncryptDecrypt() {
        String plaintext = "dies ist ein plaintext ?";
        byte[] ciphertext;
        {
            // encrypt data with key
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
            PgpSignEncryptOperation op = new PgpSignEncryptOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            InputData data = new InputData(in, in.available());
            PgpSignEncryptData.Builder pgpData = PgpSignEncryptData.builder();
            pgpData.setEncryptionMasterKeyIds(new long[]{ PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId() });
            pgpData.setSymmetricEncryptionAlgorithm(AES_128);
            PgpSignEncryptResult result = op.execute(pgpData.build(), CryptoInputParcel.createCryptoInputParcel(new Date()), data, out);
            Assert.assertTrue("encryption must succeed", result.success());
            ciphertext = out.toByteArray();
        }
        {
            // decryption with provided passphrase should yield the same result
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(null, null, null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(PgpEncryptDecryptTest.mKeyPhrase1), data, out);
            Assert.assertTrue("decryption with provided passphrase must succeed", result.success());
            Assert.assertArrayEquals("decrypted ciphertext with provided passphrase should equal plaintext", out.toByteArray(), plaintext.getBytes());
            Assert.assertEquals("decryptionResult should be RESULT_ENCRYPTED", RESULT_ENCRYPTED, result.getDecryptionResult().getResult());
            Assert.assertEquals("signatureResult should be RESULT_NO_SIGNATURE", RESULT_NO_SIGNATURE, result.getSignatureResult().getResult());
            CryptoInputParcel cryptoInput = result.getCachedCryptoInputParcel();
            Assert.assertEquals("must have one cached session key", 1, cryptoInput.getCryptoData().size());
            OpenPgpMetadata metadata = result.getDecryptionMetadata();
            Assert.assertEquals("filesize must be correct", out.toByteArray().length, metadata.getOriginalSize());
        }
        {
            // decryption with passphrase cached should succeed
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(PgpEncryptDecryptTest.mKeyPhrase1, PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId(), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
            CryptoInputParcel cryptoInput = result.getCachedCryptoInputParcel();
            Assert.assertEquals("must have one cached session key", 1, cryptoInput.getCryptoData().size());
            Assert.assertTrue("decryption with cached passphrase must succeed", result.success());
            Assert.assertArrayEquals("decrypted ciphertext with cached passphrase  should equal plaintext", out.toByteArray(), plaintext.getBytes());
            Assert.assertEquals("decryptionResult should be RESULT_ENCRYPTED", RESULT_ENCRYPTED, result.getDecryptionResult().getResult());
            Assert.assertEquals("signatureResult should be RESULT_NO_SIGNATURE", RESULT_NO_SIGNATURE, result.getSignatureResult().getResult());
        }
        {
            // decryption with no passphrase provided should return status pending
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(null, PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId(), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
            Assert.assertFalse("decryption with no passphrase must return pending", result.success());
            Assert.assertTrue("decryption with no passphrase should return pending", result.isPending());
            Assert.assertEquals("decryption with no passphrase should return pending passphrase", PASSPHRASE, result.getRequiredInputParcel().mType);
        }
    }

    @Test
    public void testMultiSubkeyEncryptSkipStripOrBadFlag() throws Exception {
        String plaintext = "dies ist ein plaintext ?";
        byte[] ciphertext;
        long encKeyId1;
        {
            // encrypt data with key
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
            PgpSignEncryptOperation op = new PgpSignEncryptOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            InputData data = new InputData(in, in.available());
            PgpSignEncryptData.Builder pgpData = PgpSignEncryptData.builder();
            pgpData.setEncryptionMasterKeyIds(new long[]{ PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId() });
            pgpData.setSymmetricEncryptionAlgorithm(AES_128);
            PgpSignEncryptResult result = op.execute(pgpData.build(), CryptoInputParcel.createCryptoInputParcel(new Date()), data, out);
            Assert.assertTrue("encryption must succeed", result.success());
            ciphertext = out.toByteArray();
            Iterator<KeyringTestingHelper.RawPacket> packets = KeyringTestingHelper.parseKeyring(ciphertext);
            KeyringTestingHelper.RawPacket enc1 = packets.next();
            KeyringTestingHelper.RawPacket enc2 = packets.next();
            Assert.assertEquals("last packet must be encrypted data packet", SYM_ENC_INTEGRITY_PRO, packets.next().tag);
            Assert.assertFalse("no further packets", packets.hasNext());
            Packet p;
            p = new BCPGInputStream(new ByteArrayInputStream(enc1.buf)).readPacket();
            Assert.assertTrue("first packet must be session packet", (p instanceof PublicKeyEncSessionPacket));
            encKeyId1 = getKeyID();
            p = new BCPGInputStream(new ByteArrayInputStream(enc2.buf)).readPacket();
            Assert.assertTrue("second packet must be session packet", (p instanceof PublicKeyEncSessionPacket));
            long encKeyId2 = getKeyID();
            Assert.assertNotEquals("encrypted-to subkey ids must not be equal", encKeyId1, encKeyId2);
            Assert.assertThat("first packet must be encrypted to one of the subkeys", KeyringTestingHelper.getSubkeyId(PgpEncryptDecryptTest.mStaticRing1, 2), AnyOf.anyOf(Is.is(encKeyId1), Is.is(encKeyId2)));
            Assert.assertThat("second packet must be encrypted to one of the subkeys", KeyringTestingHelper.getSubkeyId(PgpEncryptDecryptTest.mStaticRing1, 3), AnyOf.anyOf(Is.is(encKeyId1), Is.is(encKeyId2)));
        }
        {
            // strip first encrypted subkey, decryption should skip it
            SaveKeyringParcel.Builder builder = SaveKeyringParcel.buildChangeKeyringParcel(PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId(), PgpEncryptDecryptTest.mStaticRing1.getFingerprint());
            builder.addOrReplaceSubkeyChange(SubkeyChange.createStripChange(encKeyId1));
            UncachedKeyRing modified = PgpKeyOperationTest.applyModificationWithChecks(builder.build(), PgpEncryptDecryptTest.mStaticRing1, new ArrayList<KeyringTestingHelper.RawPacket>(), new ArrayList<KeyringTestingHelper.RawPacket>(), CryptoInputParcel.createCryptoInputParcel(new Date(), PgpEncryptDecryptTest.mKeyPhrase1));
            KeyWritableRepository databaseInteractor = KeyWritableRepository.create(application);
            databaseInteractor.saveSecretKeyRing(modified);
            PgpDecryptVerifyOperation op = new PgpDecryptVerifyOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().setInputBytes(ciphertext).build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(PgpEncryptDecryptTest.mKeyPhrase1));
            Assert.assertTrue("decryption must succeed", result.success());
            Assert.assertTrue("decryption must have skipped first key", result.getLog().containsType(MSG_DC_ASKIP_UNAVAILABLE));
        }
        {
            // change flags of second encrypted subkey, decryption should skip it
            SaveKeyringParcel.Builder builder = SaveKeyringParcel.buildChangeKeyringParcel(PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId(), PgpEncryptDecryptTest.mStaticRing1.getFingerprint());
            builder.addOrReplaceSubkeyChange(SubkeyChange.createFlagsOrExpiryChange(encKeyId1, CERTIFY_OTHER, null));
            UncachedKeyRing modified = PgpKeyOperationTest.applyModificationWithChecks(builder.build(), PgpEncryptDecryptTest.mStaticRing1, new ArrayList<KeyringTestingHelper.RawPacket>(), new ArrayList<KeyringTestingHelper.RawPacket>(), CryptoInputParcel.createCryptoInputParcel(new Date(), PgpEncryptDecryptTest.mKeyPhrase1));
            KeyWritableRepository databaseInteractor = KeyWritableRepository.create(application);
            databaseInteractor.saveSecretKeyRing(modified);
            PgpDecryptVerifyOperation op = new PgpDecryptVerifyOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().setInputBytes(ciphertext).build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(PgpEncryptDecryptTest.mKeyPhrase1));
            Assert.assertTrue("decryption must succeed", result.success());
            Assert.assertTrue("decryption must have skipped first key", result.getLog().containsType(MSG_DC_ASKIP_BAD_FLAGS));
        }
    }

    @Test
    public void testMultiSubkeyEncryptSkipRevoked() throws Exception {
        String plaintext = "dies ist ein plaintext ?";
        {
            // revoke first encryption subkey of keyring in database
            SaveKeyringParcel.Builder builder = SaveKeyringParcel.buildChangeKeyringParcel(PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId(), PgpEncryptDecryptTest.mStaticRing1.getFingerprint());
            builder.addRevokeSubkey(KeyringTestingHelper.getSubkeyId(PgpEncryptDecryptTest.mStaticRing1, 2));
            UncachedKeyRing modified = PgpKeyOperationTest.applyModificationWithChecks(builder.build(), PgpEncryptDecryptTest.mStaticRing1, new ArrayList<KeyringTestingHelper.RawPacket>(), new ArrayList<KeyringTestingHelper.RawPacket>(), CryptoInputParcel.createCryptoInputParcel(new Date(), PgpEncryptDecryptTest.mKeyPhrase1));
            KeyWritableRepository databaseInteractor = KeyWritableRepository.create(application);
            databaseInteractor.saveSecretKeyRing(modified);
        }
        {
            // encrypt to this keyring, make sure it's not encrypted to the revoked subkey
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
            PgpSignEncryptOperation op = new PgpSignEncryptOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            InputData data = new InputData(in, in.available());
            PgpSignEncryptData.Builder pgpData = PgpSignEncryptData.builder();
            pgpData.setEncryptionMasterKeyIds(new long[]{ PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId() });
            pgpData.setSymmetricEncryptionAlgorithm(AES_128);
            PgpSignEncryptResult result = op.execute(pgpData.build(), CryptoInputParcel.createCryptoInputParcel(new Date()), data, out);
            Assert.assertTrue("encryption must succeed", result.success());
            byte[] ciphertext = out.toByteArray();
            Iterator<KeyringTestingHelper.RawPacket> packets = KeyringTestingHelper.parseKeyring(ciphertext);
            KeyringTestingHelper.RawPacket enc1 = packets.next();
            Assert.assertEquals("last packet must be encrypted data packet", SYM_ENC_INTEGRITY_PRO, packets.next().tag);
            Assert.assertFalse("no further packets", packets.hasNext());
            Packet p;
            p = new BCPGInputStream(new ByteArrayInputStream(enc1.buf)).readPacket();
            Assert.assertTrue("first packet must be session packet", (p instanceof PublicKeyEncSessionPacket));
            Assert.assertEquals("first packet must be encrypted to second enc subkey", KeyringTestingHelper.getSubkeyId(PgpEncryptDecryptTest.mStaticRing1, 3), ((PublicKeyEncSessionPacket) (p)).getKeyID());
        }
    }

    @Test
    public void testMultiAsymmetricEncryptDecrypt() {
        String plaintext = "dies ist ein plaintext ?";
        byte[] ciphertext;
        {
            // encrypt data with a given passphrase
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
            PgpSignEncryptOperation op = new PgpSignEncryptOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            InputData data = new InputData(in, in.available());
            PgpSignEncryptData.Builder pgpData = PgpSignEncryptData.builder();
            pgpData.setEncryptionMasterKeyIds(new long[]{ PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId(), PgpEncryptDecryptTest.mStaticRing2.getMasterKeyId() });
            pgpData.setSymmetricEncryptionAlgorithm(AES_128);
            PgpSignEncryptResult result = op.execute(pgpData.build(), CryptoInputParcel.createCryptoInputParcel(new Date()), data, out);
            Assert.assertTrue("encryption must succeed", result.success());
            ciphertext = out.toByteArray();
        }
        {
            // decryption with passphrase cached should succeed for the first key
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(PgpEncryptDecryptTest.mKeyPhrase1, PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId(), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
            Assert.assertTrue("decryption with cached passphrase must succeed for the first key", result.success());
            Assert.assertArrayEquals("decrypted ciphertext with cached passphrase  should equal plaintext", out.toByteArray(), plaintext.getBytes());
            Assert.assertEquals("decryptionResult should be RESULT_ENCRYPTED", RESULT_ENCRYPTED, result.getDecryptionResult().getResult());
            Assert.assertEquals("signatureResult should be RESULT_NO_SIGNATURE", RESULT_NO_SIGNATURE, result.getSignatureResult().getResult());
            OpenPgpMetadata metadata = result.getDecryptionMetadata();
            Assert.assertEquals("filesize must be correct", out.toByteArray().length, metadata.getOriginalSize());
        }
        {
            // decryption should succeed if key is allowed
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            // allow only the second to decrypt
            ArrayList<Long> allowed = new ArrayList<>();
            allowed.add(PgpEncryptDecryptTest.mStaticRing2.getMasterKeyId());
            // provide passphrase for the second, and check that the first is never asked for!
            PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(PgpEncryptDecryptTest.mKeyPhrase2, PgpEncryptDecryptTest.mStaticRing2.getMasterKeyId(), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().setAllowedKeyIds(allowed).build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
            Assert.assertTrue("decryption with cached passphrase must succeed for allowed key", result.success());
            Assert.assertArrayEquals("decrypted ciphertext with cached passphrase  should equal plaintext", out.toByteArray(), plaintext.getBytes());
            Assert.assertTrue("other key was skipped", result.getLog().containsType(MSG_DC_ASKIP_NOT_ALLOWED));
            Assert.assertEquals("decryptionResult should be RESULT_ENCRYPTED", RESULT_ENCRYPTED, result.getDecryptionResult().getResult());
            Assert.assertEquals("signatureResult should be RESULT_NO_SIGNATURE", RESULT_NO_SIGNATURE, result.getSignatureResult().getResult());
        }
        {
            // decryption should fail if no key is allowed
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            // provide passphrase for the second, and check that the first is never asked for!
            PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(PgpEncryptDecryptTest.mKeyPhrase2, PgpEncryptDecryptTest.mStaticRing2.getMasterKeyId(), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().setAllowedKeyIds(new ArrayList<Long>()).build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
            Assert.assertFalse("decryption must fail if no key allowed", result.success());
            Assert.assertEquals("decryption must fail with key disllowed status", RESULT_KEY_DISALLOWED, result.getResult());
        }
        {
            // decryption with passphrase cached should succeed for the other key if first is gone
            // delete first key from database
            KeyWritableRepository.create(application).deleteKeyRing(PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(PgpEncryptDecryptTest.mKeyPhrase2, PgpEncryptDecryptTest.mStaticRing2.getMasterKeyId(), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
            Assert.assertTrue("decryption with cached passphrase must succeed", result.success());
            Assert.assertArrayEquals("decrypted ciphertext with cached passphrase  should equal plaintext", out.toByteArray(), plaintext.getBytes());
            Assert.assertEquals("decryptionResult should be RESULT_ENCRYPTED", RESULT_ENCRYPTED, result.getDecryptionResult().getResult());
            Assert.assertEquals("signatureResult should be RESULT_NO_SIGNATURE", RESULT_NO_SIGNATURE, result.getSignatureResult().getResult());
        }
    }

    @Test
    public void testMultiAsymmetricSignEncryptDecryptVerify() {
        String plaintext = "dies ist ein plaintext ?";
        byte[] ciphertext;
        {
            // encrypt data with a given passphrase
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
            PgpSignEncryptOperation op = new PgpSignEncryptOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            InputData data = new InputData(in, in.available());
            PgpSignEncryptData.Builder pgpData = PgpSignEncryptData.builder();
            pgpData.setEncryptionMasterKeyIds(new long[]{ PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId(), PgpEncryptDecryptTest.mStaticRing2.getMasterKeyId() });
            pgpData.setSignatureMasterKeyId(PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId());
            pgpData.setSignatureSubKeyId(KeyringTestingHelper.getSubkeyId(PgpEncryptDecryptTest.mStaticRing1, 1));
            pgpData.setSymmetricEncryptionAlgorithm(AES_128);
            PgpSignEncryptResult result = op.execute(pgpData.build(), CryptoInputParcel.createCryptoInputParcel(new Date(), PgpEncryptDecryptTest.mKeyPhrase1), data, out);
            Assert.assertTrue("encryption must succeed", result.success());
            ciphertext = out.toByteArray();
        }
        {
            // decryption with passphrase cached should succeed for the first key
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(PgpEncryptDecryptTest.mKeyPhrase1, PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId(), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
            Assert.assertTrue("decryption with cached passphrase must succeed for the first key", result.success());
            Assert.assertArrayEquals("decrypted ciphertext with cached passphrase  should equal plaintext", out.toByteArray(), plaintext.getBytes());
            Assert.assertEquals("signature should be verified and certified", RESULT_VALID_KEY_CONFIRMED, result.getSignatureResult().getResult());
            OpenPgpMetadata metadata = result.getDecryptionMetadata();
            Assert.assertEquals("filesize must be correct", out.toByteArray().length, metadata.getOriginalSize());
        }
        {
            // decryption with passphrase cached should succeed for the other key if first is gone
            // delete first key from database
            KeyWritableRepository.create(application).deleteKeyRing(PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(PgpEncryptDecryptTest.mKeyPhrase2, PgpEncryptDecryptTest.mStaticRing2.getMasterKeyId(), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
            Assert.assertTrue("decryption with cached passphrase must succeed", result.success());
            Assert.assertArrayEquals("decrypted ciphertext with cached passphrase  should equal plaintext", out.toByteArray(), plaintext.getBytes());
            Assert.assertEquals("signature key should be missing", RESULT_KEY_MISSING, result.getSignatureResult().getResult());
        }
    }

    @Test
    public void testForeignEncoding() throws Exception {
        String plaintext = "???????";
        byte[] plaindata = plaintext.getBytes("iso-2022-jp");
        {
            // some quick sanity checks
            Assert.assertEquals(plaintext, new String(plaindata, "iso-2022-jp"));
            Assert.assertNotEquals(plaintext, new String(plaindata, "utf-8"));
        }
        byte[] ciphertext;
        {
            // encrypt data with a given passphrase
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(plaindata);
            PgpSignEncryptOperation op = new PgpSignEncryptOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            InputData data = new InputData(in, in.available());
            PgpSignEncryptData.Builder pgpData = PgpSignEncryptData.builder();
            pgpData.setEncryptionMasterKeyIds(new long[]{ PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId() });
            pgpData.setSymmetricEncryptionAlgorithm(AES_128);
            // this only works with ascii armored output!
            pgpData.setEnableAsciiArmorOutput(true);
            pgpData.setCharset("iso-2022-jp");
            PgpSignEncryptResult result = op.execute(pgpData.build(), CryptoInputParcel.createCryptoInputParcel(new Date()), data, out);
            Assert.assertTrue("encryption must succeed", result.success());
            ciphertext = out.toByteArray();
        }
        {
            // decryption with provided passphrase should yield the same result
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
            InputData data = new InputData(in, in.available());
            PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(null, null, null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().build();
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(PgpEncryptDecryptTest.mKeyPhrase1), data, out);
            Assert.assertTrue("decryption with provided passphrase must succeed", result.success());
            Assert.assertArrayEquals("decrypted ciphertext should equal plaintext bytes", out.toByteArray(), plaindata);
            Assert.assertEquals("charset should be read correctly", "iso-2022-jp", result.getDecryptionMetadata().getCharset());
            Assert.assertEquals("decrypted ciphertext should equal plaintext", new String(out.toByteArray(), result.getDecryptionMetadata().getCharset()), plaintext);
            Assert.assertEquals("decryptionResult should be RESULT_ENCRYPTED", RESULT_ENCRYPTED, result.getDecryptionResult().getResult());
            Assert.assertEquals("signatureResult should be RESULT_NO_SIGNATURE", RESULT_NO_SIGNATURE, result.getSignatureResult().getResult());
        }
    }

    @Test
    public void testAsymmetricSymmetricDesDecrypt() throws Exception {
        InputStream in = PgpEncryptDecryptTest.getResourceAsStream("/test-ciphertexts/algo_des.pgp.asc");
        String plaintext = "dies ist ein plaintext ?";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputData data = new InputData(in, in.available());
        PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(null, null, null);
        PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().build();
        DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(PgpEncryptDecryptTest.mKeyPhrase1), data, out);
        Assert.assertTrue(result.success());
        Assert.assertArrayEquals(out.toByteArray(), plaintext.getBytes());
        Assert.assertEquals(RESULT_INSECURE, result.getDecryptionResult().getResult());
        Assert.assertEquals(RESULT_NO_SIGNATURE, result.getSignatureResult().getResult());
        InsecureEncryptionAlgorithm symmetricSecurityProblem = ((InsecureEncryptionAlgorithm) (result.getSecurityProblem().symmetricSecurityProblem));
        Assert.assertEquals(symmetricSecurityProblem.symmetricAlgorithm, DES);
    }

    @Test
    public void testDecryptForTwoKeys() throws Exception {
        InputStream in = PgpEncryptDecryptTest.getResourceAsStream("/test-ciphertexts/two_keys.asc");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputData data = new InputData(in, in.available());
        PgpDecryptVerifyOperation op = operationWithFakePassphraseCache(null, null, null);
        PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().build();
        DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(), data, out);
        RequiredInputParcel requiredInputParcel = result.getRequiredInputParcel();
        Assert.assertNotNull(requiredInputParcel);
        Assert.assertEquals(3, requiredInputParcel.getMasterKeyIds().length);
        Assert.assertEquals(PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId(), requiredInputParcel.getMasterKeyIds()[0]);
        Assert.assertEquals(PgpEncryptDecryptTest.mStaticRing1.getMasterKeyId(), requiredInputParcel.getMasterKeyIds()[1]);
        Assert.assertEquals(PgpEncryptDecryptTest.mStaticRing2.getMasterKeyId(), requiredInputParcel.getMasterKeyIds()[2]);
    }
}

