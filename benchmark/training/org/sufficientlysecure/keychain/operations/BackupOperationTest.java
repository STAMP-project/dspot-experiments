/**
 * Copyright (C) 2014 Vincent Breitmoser <v.breitmoser@mugenguild.com>
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
package org.sufficientlysecure.keychain.operations;


import LogType.MSG_DC_ERROR_SYM_PASSPHRASE;
import LogType.MSG_DC_PENDING_PASSPHRASE;
import RuntimeEnvironment.application;
import TemporaryFileProvider.CONTENT_URI;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.daos.KeyWritableRepository;
import org.sufficientlysecure.keychain.operations.results.DecryptVerifyResult;
import org.sufficientlysecure.keychain.operations.results.ExportResult;
import org.sufficientlysecure.keychain.operations.results.OperationResult.OperationLog;
import org.sufficientlysecure.keychain.pgp.PgpDecryptVerifyInputParcel;
import org.sufficientlysecure.keychain.pgp.PgpDecryptVerifyOperation;
import org.sufficientlysecure.keychain.pgp.UncachedKeyRing;
import org.sufficientlysecure.keychain.pgp.UncachedKeyRing.IteratorWithIOThrow;
import org.sufficientlysecure.keychain.service.BackupKeyringParcel;
import org.sufficientlysecure.keychain.service.input.CryptoInputParcel;
import org.sufficientlysecure.keychain.ui.util.KeyFormattingUtils;
import org.sufficientlysecure.keychain.util.Passphrase;
import org.sufficientlysecure.keychain.util.TestingUtils;


@RunWith(KeychainTestRunner.class)
public class BackupOperationTest {
    static Passphrase mPassphrase = TestingUtils.testPassphrase0;

    static UncachedKeyRing mStaticRing1;

    static UncachedKeyRing mStaticRing2;

    static PrintStream oldShadowStream;

    @Test
    public void testExportAllLocalStripped() throws Exception {
        BackupOperation op = new BackupOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
        // make sure there is a local cert (so the later checks that there are none are meaningful)
        Assert.assertTrue("second keyring has local certification", checkForLocal(BackupOperationTest.mStaticRing2));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean result = op.exportKeysToStream(new OperationLog(), null, false, true, out, null);
        Assert.assertTrue("export must be a success", result);
        IteratorWithIOThrow<UncachedKeyRing> unc = UncachedKeyRing.fromStream(new ByteArrayInputStream(out.toByteArray()));
        {
            Assert.assertTrue("export must have two keys (1/2)", unc.hasNext());
            UncachedKeyRing ring = unc.next();
            Assert.assertEquals("first exported key has correct masterkeyid", BackupOperationTest.mStaticRing2.getMasterKeyId(), ring.getMasterKeyId());
            Assert.assertFalse("first exported key must not be secret", ring.isSecret());
            Assert.assertFalse("there must be no local signatures in an exported keyring", checkForLocal(ring));
        }
        {
            Assert.assertTrue("export must have two keys (2/2)", unc.hasNext());
            UncachedKeyRing ring = unc.next();
            Assert.assertEquals("second exported key has correct masterkeyid", BackupOperationTest.mStaticRing1.getMasterKeyId(), ring.getMasterKeyId());
            Assert.assertFalse("second exported key must not be secret", ring.isSecret());
            Assert.assertFalse("there must be no local signatures in an exported keyring", checkForLocal(ring));
        }
        out = new ByteArrayOutputStream();
        result = op.exportKeysToStream(new OperationLog(), null, true, true, out, null);
        Assert.assertTrue("export must be a success", result);
        unc = UncachedKeyRing.fromStream(new ByteArrayInputStream(out.toByteArray()));
        {
            Assert.assertTrue("export must have four keys (1/4)", unc.hasNext());
            UncachedKeyRing ring = unc.next();
            Assert.assertEquals("1/4 exported key has correct masterkeyid", BackupOperationTest.mStaticRing2.getMasterKeyId(), ring.getMasterKeyId());
            Assert.assertFalse("1/4 exported key must not be public", ring.isSecret());
            Assert.assertFalse("there must be no local signatures in an exported keyring", checkForLocal(ring));
            Assert.assertTrue("export must have four keys (2/4)", unc.hasNext());
            ring = unc.next();
            Assert.assertEquals("2/4 exported key has correct masterkeyid", BackupOperationTest.mStaticRing2.getMasterKeyId(), ring.getMasterKeyId());
            Assert.assertTrue("2/4 exported key must be public", ring.isSecret());
            Assert.assertFalse("there must be no local signatures in an exported keyring", checkForLocal(ring));
        }
        {
            Assert.assertTrue("export must have four keys (3/4)", unc.hasNext());
            UncachedKeyRing ring = unc.next();
            Assert.assertEquals("3/4 exported key has correct masterkeyid", BackupOperationTest.mStaticRing1.getMasterKeyId(), ring.getMasterKeyId());
            Assert.assertFalse("3/4 exported key must not be public", ring.isSecret());
            Assert.assertFalse("there must be no local signatures in an exported keyring", checkForLocal(ring));
            Assert.assertTrue("export must have four keys (4/4)", unc.hasNext());
            ring = unc.next();
            Assert.assertEquals("4/4 exported key has correct masterkeyid", BackupOperationTest.mStaticRing1.getMasterKeyId(), ring.getMasterKeyId());
            Assert.assertTrue("4/4 exported key must be public", ring.isSecret());
            Assert.assertFalse("there must be no local signatures in an exported keyring", checkForLocal(ring));
        }
    }

    @Test
    public void testExportWithExtraHeaders() throws Exception {
        BackupOperation op = new BackupOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean result = op.exportKeysToStream(new OperationLog(), new long[]{ BackupOperationTest.mStaticRing1.getMasterKeyId() }, true, false, out, Arrays.asList("header: value"));
        Assert.assertTrue(result);
        String resultData = new String(out.toByteArray());
        Assert.assertTrue(resultData.startsWith("-----BEGIN PGP PRIVATE KEY BLOCK-----\nheader: value\n\n"));
    }

    @Test
    public void testExportUnencrypted() throws Exception {
        ContentResolver mockResolver = Mockito.mock(ContentResolver.class);
        Uri fakeOutputUri = Uri.parse("content://fake/out/1");
        ByteArrayOutputStream outStream1 = new ByteArrayOutputStream();
        Mockito.when(mockResolver.openOutputStream(fakeOutputUri)).thenReturn(outStream1);
        Application spyApplication = Mockito.spy(application);
        Mockito.when(spyApplication.getContentResolver()).thenReturn(mockResolver);
        BackupOperation op = new BackupOperation(spyApplication, KeyWritableRepository.create(application), null);
        BackupKeyringParcel parcel = BackupKeyringParcel.create(new long[]{ BackupOperationTest.mStaticRing1.getMasterKeyId() }, false, false, true, fakeOutputUri);
        ExportResult result = op.execute(parcel, null);
        Mockito.verify(mockResolver).openOutputStream(fakeOutputUri);
        Assert.assertTrue("export must succeed", result.success());
        TestingUtils.assertArrayEqualsPrefix("exported data must start with ascii armor header", "-----BEGIN PGP PUBLIC KEY BLOCK-----\n".getBytes(), outStream1.toByteArray());
        TestingUtils.assertArrayEqualsSuffix("exported data must end with ascii armor header", "-----END PGP PUBLIC KEY BLOCK-----\n".getBytes(), outStream1.toByteArray());
        {
            IteratorWithIOThrow<UncachedKeyRing> unc = UncachedKeyRing.fromStream(new ByteArrayInputStream(outStream1.toByteArray()));
            Assert.assertTrue("export must have one key", unc.hasNext());
            UncachedKeyRing ring = unc.next();
            Assert.assertEquals("exported key has correct masterkeyid", BackupOperationTest.mStaticRing1.getMasterKeyId(), ring.getMasterKeyId());
            Assert.assertFalse("export must have exactly one key", unc.hasNext());
        }
    }

    @Test
    public void testExportEncrypted() throws Exception {
        Application spyApplication;
        ContentResolver mockResolver = Mockito.mock(ContentResolver.class);
        Uri fakePipedUri;
        Uri fakeOutputUri;
        ByteArrayOutputStream outStream;
        {
            fakePipedUri = Uri.parse("content://fake/pipe/1");
            PipedInputStream pipedInStream = new PipedInputStream(8192);
            PipedOutputStream pipedOutStream = new PipedOutputStream(pipedInStream);
            Mockito.when(mockResolver.openOutputStream(fakePipedUri)).thenReturn(pipedOutStream);
            Mockito.when(mockResolver.openInputStream(fakePipedUri)).thenReturn(pipedInStream);
            Mockito.when(mockResolver.insert(ArgumentMatchers.eq(CONTENT_URI), ArgumentMatchers.any(ContentValues.class))).thenReturn(fakePipedUri);
            fakeOutputUri = Uri.parse("content://fake/out/1");
            outStream = new ByteArrayOutputStream();
            Mockito.when(mockResolver.openOutputStream(fakeOutputUri)).thenReturn(outStream);
            spyApplication = Mockito.spy(application);
            Mockito.when(spyApplication.getContentResolver()).thenReturn(mockResolver);
        }
        Passphrase passphrase = new Passphrase("abcde");
        {
            // export encrypted
            BackupOperation op = new BackupOperation(spyApplication, KeyWritableRepository.create(application), null);
            BackupKeyringParcel parcel = BackupKeyringParcel.create(new long[]{ BackupOperationTest.mStaticRing1.getMasterKeyId() }, false, true, true, fakeOutputUri);
            CryptoInputParcel inputParcel = CryptoInputParcel.createCryptoInputParcel(passphrase);
            ExportResult result = op.execute(parcel, inputParcel);
            Mockito.verify(mockResolver).openOutputStream(fakePipedUri);
            Mockito.verify(mockResolver).openInputStream(fakePipedUri);
            Mockito.verify(mockResolver).openOutputStream(fakeOutputUri);
            Assert.assertTrue("export must succeed", result.success());
            TestingUtils.assertArrayEqualsPrefix("exported data must start with ascii armor header", "-----BEGIN PGP MESSAGE-----\n".getBytes(), outStream.toByteArray());
        }
        {
            PgpDecryptVerifyOperation op = new PgpDecryptVerifyOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
            PgpDecryptVerifyInputParcel input = PgpDecryptVerifyInputParcel.builder().setAllowSymmetricDecryption(true).setInputBytes(outStream.toByteArray()).build();
            {
                DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel());
                Assert.assertTrue("decryption must return pending without passphrase", result.isPending());
                Assert.assertTrue("should contain pending passphrase log entry", result.getLog().containsType(MSG_DC_PENDING_PASSPHRASE));
            }
            {
                DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(new Passphrase("bad")));
                Assert.assertFalse("decryption must fail with bad passphrase", result.success());
                Assert.assertTrue("should contain bad passphrase log entry", result.getLog().containsType(MSG_DC_ERROR_SYM_PASSPHRASE));
            }
            DecryptVerifyResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel(passphrase));
            Assert.assertTrue("decryption must succeed with passphrase", result.success());
            Assert.assertEquals("backup filename should be backup_keyid.pub.asc", (("backup_" + (KeyFormattingUtils.convertKeyIdToHex(BackupOperationTest.mStaticRing1.getMasterKeyId()))) + ".pub.asc"), result.getDecryptionMetadata().getFilename());
            Assert.assertEquals("mime type for pgp keys must be correctly detected", "application/pgp-keys", result.getDecryptionMetadata().getMimeType());
            TestingUtils.assertArrayEqualsPrefix("exported data must start with ascii armor header", "-----BEGIN PGP PUBLIC KEY BLOCK-----\n".getBytes(), result.getOutputBytes());
            TestingUtils.assertArrayEqualsSuffix("exported data must end with ascii armor header", "-----END PGP PUBLIC KEY BLOCK-----\n".getBytes(), result.getOutputBytes());
            {
                IteratorWithIOThrow<UncachedKeyRing> unc = UncachedKeyRing.fromStream(new ByteArrayInputStream(result.getOutputBytes()));
                Assert.assertTrue("export must have one key", unc.hasNext());
                UncachedKeyRing ring = unc.next();
                Assert.assertEquals("exported key has correct masterkeyid", BackupOperationTest.mStaticRing1.getMasterKeyId(), ring.getMasterKeyId());
                Assert.assertFalse("export must have exactly one key", unc.hasNext());
            }
        }
    }
}

