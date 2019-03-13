/**
 * Copyright (C) 2014 Dominik Sch?rmann <dominik@dominikschuermann.de>
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
package org.sufficientlysecure.keychain.provider;


import Algorithm.EDDSA;
import KeyFlags.CERTIFY_OTHER;
import OpenPgpSignatureResult.RESULT_VALID_KEY_UNCONFIRMED;
import SaveKeyringParcel.Builder;
import android.app.Application;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.daos.KeyWritableRepository;
import org.sufficientlysecure.keychain.operations.results.DecryptVerifyResult;
import org.sufficientlysecure.keychain.operations.results.OperationResult.OperationLog;
import org.sufficientlysecure.keychain.operations.results.PgpEditKeyResult;
import org.sufficientlysecure.keychain.operations.results.PgpSignEncryptResult;
import org.sufficientlysecure.keychain.pgp.CanonicalizedKeyRing;
import org.sufficientlysecure.keychain.pgp.PgpDecryptVerifyInputParcel;
import org.sufficientlysecure.keychain.pgp.PgpDecryptVerifyOperation;
import org.sufficientlysecure.keychain.pgp.PgpKeyOperation;
import org.sufficientlysecure.keychain.pgp.PgpSignEncryptData;
import org.sufficientlysecure.keychain.pgp.PgpSignEncryptInputParcel;
import org.sufficientlysecure.keychain.pgp.PgpSignEncryptOperation;
import org.sufficientlysecure.keychain.pgp.UncachedKeyRing;
import org.sufficientlysecure.keychain.service.SaveKeyringParcel;
import org.sufficientlysecure.keychain.service.SaveKeyringParcel.SubkeyAdd;
import org.sufficientlysecure.keychain.service.input.CryptoInputParcel;


@SuppressWarnings("WeakerAccess")
@RunWith(KeychainTestRunner.class)
public class EddsaTest {
    public static final byte[] SIGNED_BYTES = "hi".getBytes();

    private KeyWritableRepository keyRepository;

    private Application context;

    @Test
    public void testGpgSampleSignature() throws Exception {
        // key from GnuPG's test suite, sample msg generated using GnuPG v2.1.18
        UncachedKeyRing ring = loadPubkeyFromResource("/test-keys/eddsa-sample-1-pub.asc");
        byte[] signedText = readBytesFromResource("/test-keys/eddsa-sample-msg.asc");
        PgpDecryptVerifyInputParcel pgpDecryptVerifyInputParcel = PgpDecryptVerifyInputParcel.builder().setInputBytes(signedText).build();
        PgpDecryptVerifyOperation decryptVerifyOperation = new PgpDecryptVerifyOperation(context, keyRepository, null);
        DecryptVerifyResult result = decryptVerifyOperation.execute(pgpDecryptVerifyInputParcel, null);
        Assert.assertTrue(result.success());
        Assert.assertEquals(RESULT_VALID_KEY_UNCONFIRMED, result.getSignatureResult().getResult());
        Assert.assertEquals(ring.getMasterKeyId(), result.getSignatureResult().getKeyId());
    }

    @Test
    public void testEddsaSign() throws Exception {
        // key from GnuPG's test suite, sample msg generated using GnuPG v2.1.18
        UncachedKeyRing ring = loadSeckeyFromResource("/test-keys/eddsa-key.sec");
        PgpSignEncryptData data = PgpSignEncryptData.builder().setDetachedSignature(true).setSignatureMasterKeyId(ring.getMasterKeyId()).build();
        PgpSignEncryptInputParcel inputParcel = PgpSignEncryptInputParcel.createForBytes(data, null, EddsaTest.SIGNED_BYTES);
        PgpSignEncryptOperation op = new PgpSignEncryptOperation(context, keyRepository, null);
        PgpSignEncryptResult result = op.execute(inputParcel, CryptoInputParcel.createCryptoInputParcel());
        Assert.assertTrue(result.success());
        PgpDecryptVerifyInputParcel pgpDecryptVerifyInputParcel = PgpDecryptVerifyInputParcel.builder().setInputBytes(EddsaTest.SIGNED_BYTES).setDetachedSignature(result.getDetachedSignature()).build();
        PgpDecryptVerifyOperation decryptVerifyOperation = new PgpDecryptVerifyOperation(context, keyRepository, null);
        DecryptVerifyResult result2 = decryptVerifyOperation.execute(pgpDecryptVerifyInputParcel, null);
        Assert.assertTrue(result2.success());
    }

    @Test
    public void testCreateEddsa() throws Exception {
        SaveKeyringParcel.Builder builder = SaveKeyringParcel.buildNewKeyringParcel();
        builder.addSubkeyAdd(SubkeyAdd.createSubkeyAdd(EDDSA, 0, null, CERTIFY_OTHER, 0L));
        builder.addUserId("ed");
        PgpKeyOperation op = new PgpKeyOperation(null);
        PgpEditKeyResult result = op.createSecretKeyRing(builder.build());
        Assert.assertTrue("initial test key creation must succeed", result.success());
        Assert.assertNotNull("initial test key creation must succeed", result.getRing());
        CanonicalizedKeyRing canonicalizedKeyRing = result.getRing().canonicalize(new OperationLog(), 0);
        Assert.assertNotNull(canonicalizedKeyRing);
    }
}

