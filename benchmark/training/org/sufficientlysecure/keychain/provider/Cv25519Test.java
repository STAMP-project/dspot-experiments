/**
 * Copyright (C) 2017 Sch?rmann & Breitmoser GbR
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
package org.sufficientlysecure.keychain.provider;


import android.app.Application;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.daos.KeyWritableRepository;
import org.sufficientlysecure.keychain.operations.results.DecryptVerifyResult;
import org.sufficientlysecure.keychain.operations.results.PgpSignEncryptResult;
import org.sufficientlysecure.keychain.pgp.PgpSignEncryptData;
import org.sufficientlysecure.keychain.pgp.PgpSignEncryptInputParcel;
import org.sufficientlysecure.keychain.pgp.PgpSignEncryptOperation;
import org.sufficientlysecure.keychain.pgp.UncachedKeyRing;
import org.sufficientlysecure.keychain.service.input.CryptoInputParcel;

import static junit.framework.Assert.assertTrue;


@SuppressWarnings("WeakerAccess")
@RunWith(KeychainTestRunner.class)
public class Cv25519Test {
    public static final byte[] ENCRYPTED_PLAINTEXT = "hi\n".getBytes();

    private KeyWritableRepository keyRepository;

    private Application context;

    @Test
    public void testDecryptX25519() throws Exception {
        loadSecretKeyringFromResource("/test-keys/cv25519-key.sec.asc");
        byte[] encryptedText = readBytesFromResource("/test-keys/cv25519-encrypted.asc");
        DecryptVerifyResult result = simpleDecryptText(encryptedText);
        Assert.assertArrayEquals(Cv25519Test.ENCRYPTED_PLAINTEXT, result.getOutputBytes());
    }

    @Test
    public void testEncryptX25519() throws Exception {
        UncachedKeyRing uncachedKeyRing = loadSecretKeyringFromResource("/test-keys/cv25519-key.sec.asc");
        PgpSignEncryptData data = PgpSignEncryptData.builder().setEncryptionMasterKeyIds(new long[]{ uncachedKeyRing.getMasterKeyId() }).build();
        PgpSignEncryptInputParcel inputParcel = PgpSignEncryptInputParcel.createForBytes(data, null, Cv25519Test.ENCRYPTED_PLAINTEXT);
        PgpSignEncryptOperation op = new PgpSignEncryptOperation(context, keyRepository, null);
        PgpSignEncryptResult result = op.execute(inputParcel, CryptoInputParcel.createCryptoInputParcel());
        assertTrue(result.success());
        DecryptVerifyResult decryptResult = simpleDecryptText(result.getOutputBytes());
        assertTrue(decryptResult.success());
        Assert.assertArrayEquals(Cv25519Test.ENCRYPTED_PLAINTEXT, decryptResult.getOutputBytes());
    }
}

