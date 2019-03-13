/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.crypto;


import java.io.IOException;
import java.math.BigInteger;
import org.ethereum.ConcatKDFBytesGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.encoders.Hex;


public class ECIESTest {
    public static final int KEY_SIZE = 128;

    static Logger log = LoggerFactory.getLogger("test");

    private static ECDomainParameters curve;

    private static final String CIPHERTEXT1 = "042a851331790adacf6e64fcb19d0872fcdf1285a899a12cdc897da941816b0ea6485402aaf6c2e0a5d98ae3af1b05c68b307d1e0eb7a426a46f1617ba5b94f90b606eee3b5e9d2b527a9ee52cfa377bcd118b9390ed27ffe7d48e8155004375cae209012c3e057bb13a478a64a201d79ad4ae83";

    private static final X9ECParameters IES_CURVE_PARAM = SECNamedCurves.getByName("secp256r1");

    private static final BigInteger PRIVATE_KEY1 = new BigInteger("51134539186617376248226283012294527978458758538121566045626095875284492680246");

    @Test
    public void testKDF() {
        ConcatKDFBytesGenerator kdf = new ConcatKDFBytesGenerator(new SHA256Digest());
        kdf.init(new KDFParameters("Hello".getBytes(), new byte[0]));
        byte[] bytes = new byte[2];
        kdf.generateBytes(bytes, 0, bytes.length);
        Assert.assertArrayEquals(new byte[]{ -66, -89 }, bytes);
    }

    @Test
    public void testDecryptTestVector() throws IOException, InvalidCipherTextException {
        ECPoint pub1 = ECIESTest.pub(ECIESTest.PRIVATE_KEY1);
        byte[] ciphertext = Hex.decode(ECIESTest.CIPHERTEXT1);
        byte[] plaintext = ECIESTest.decrypt(ECIESTest.PRIVATE_KEY1, ciphertext);
        Assert.assertArrayEquals(new byte[]{ 1, 1, 1 }, plaintext);
    }

    @Test
    public void testRoundTrip() throws IOException, InvalidCipherTextException {
        ECPoint pub1 = ECIESTest.pub(ECIESTest.PRIVATE_KEY1);
        byte[] plaintext = "Hello world".getBytes();
        byte[] ciphertext = ECIESTest.encrypt(pub1, plaintext);
        byte[] plaintext1 = ECIESTest.decrypt(ECIESTest.PRIVATE_KEY1, ciphertext);
        Assert.assertArrayEquals(plaintext, plaintext1);
    }
}

