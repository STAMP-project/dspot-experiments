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
package org.sufficientlysecure.keychain.pgp;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.pgp.UncachedKeyRing.IteratorWithIOThrow;
import org.sufficientlysecure.keychain.pgp.exception.PgpGeneralException;


@RunWith(KeychainTestRunner.class)
public class UncachedKeyringTest {
    static UncachedKeyRing staticRing;

    static UncachedKeyRing staticPubRing;

    UncachedKeyRing ring;

    UncachedKeyRing pubRing;

    @Test(expected = UnsupportedOperationException.class)
    public void testPublicKeyItRemove() throws Exception {
        Iterator<UncachedPublicKey> it = ring.getPublicKeys();
        it.remove();
    }

    @Test(expected = PgpGeneralException.class)
    public void testDecodeFromEmpty() throws Exception {
        UncachedKeyRing.decodeFromData(new byte[0]);
    }

    @Test
    public void testArmorIdentity() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ring.encodeArmored(out, "OpenKeychain");
        Assert.assertArrayEquals("armor encoded and decoded ring should be identical to original", ring.getEncoded(), UncachedKeyRing.decodeFromData(out.toByteArray()).getEncoded());
    }

    @Test(expected = PgpGeneralException.class)
    public void testDecodeEncodeMulti() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // encode secret and public ring in here
        ring.encodeArmored(out, "OpenKeychain");
        pubRing.encodeArmored(out, "OpenKeychain");
        IteratorWithIOThrow<UncachedKeyRing> it = UncachedKeyRing.fromStream(new ByteArrayInputStream(out.toByteArray()));
        Assert.assertTrue("there should be two rings in the stream", it.hasNext());
        Assert.assertArrayEquals("first ring should be the first we put in", ring.getEncoded(), it.next().getEncoded());
        Assert.assertTrue("there should be two rings in the stream", it.hasNext());
        Assert.assertArrayEquals("second ring should be the second we put in", pubRing.getEncoded(), it.next().getEncoded());
        Assert.assertFalse("there should be two rings in the stream", it.hasNext());
        // this should fail with PgpGeneralException, since it expects exactly one ring
        UncachedKeyRing.decodeFromData(out.toByteArray());
    }

    @Test(expected = RuntimeException.class)
    public void testPublicExtractPublic() throws Exception {
        // can't do this, either!
        pubRing.extractPublicKeyRing();
    }

    @Test(expected = IOException.class)
    public void testBrokenVersionCert() throws Throwable {
        // this is a test for one of the patches we use on top of stock bouncycastle, which
        // returns an IOException rather than a RuntimeException in case of a bad certificate
        // version byte
        readRingFromResource("/test-keys/broken_cert_version.asc");
    }
}

