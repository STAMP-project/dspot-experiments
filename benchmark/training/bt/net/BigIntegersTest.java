/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.net;


import java.math.BigInteger;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;


public class BigIntegersTest {
    // 768-bit prime
    private static final BigInteger P = new BigInteger(("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1" + (("29024E088A67CC74020BBEA63B139B22514A08798E3404DD" + "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245") + "E485B576625E7EC6F44C42E9A63A36210000000000090563")), 16);

    private static final int[] Pbytes = new int[]{ 255, 255, 255, 255, 255, 255, 255, 255, 201, 15, 218, 162, 33, 104, 194, 52, 196, 198, 98, 139, 128, 220, 28, 209, 41, 2, 78, 8, 138, 103, 204, 116, 2, 11, 190, 166, 59, 19, 155, 34, 81, 74, 8, 121, 142, 52, 4, 221, 239, 149, 25, 179, 205, 58, 67, 27, 48, 43, 10, 109, 242, 95, 20, 55, 79, 225, 53, 109, 109, 81, 194, 69, 228, 133, 181, 118, 98, 94, 126, 198, 244, 76, 66, 233, 166, 58, 54, 33, 0, 0, 0, 0, 0, 9, 5, 99 };

    @Test
    public void testBigIntegers() {
        BigIntegersTest.assertSameAfterConversion(BigInteger.ZERO);
        BigIntegersTest.assertSameAfterConversion(BigInteger.ONE);
        BigIntegersTest.assertSameAfterConversion(BigInteger.valueOf(Long.MAX_VALUE));
        BigIntegersTest.assertSameAfterConversion(BigIntegersTest.P);
        Assert.assertArrayEquals(BigIntegers.encodeUnsigned(BigInteger.valueOf(255), 4), BigIntegers.encodeUnsigned(BigInteger.valueOf((-1)), 4));
        Assert.assertEquals(BigInteger.ZERO, BigIntegers.decodeUnsigned(ByteBuffer.wrap(new byte[20]), 20));
        Assert.assertEquals(BigInteger.valueOf(255), BigIntegers.decodeUnsigned(ByteBuffer.wrap(new byte[]{ 0, 0, 0, -1 }), 4));
        Assert.assertEquals(96, BigIntegersTest.Pbytes.length);
        Assert.assertEquals(BigIntegersTest.P, BigIntegers.decodeUnsigned(ByteBuffer.wrap(BigIntegersTest.toByteArray(BigIntegersTest.Pbytes)), 96));
        Assert.assertArrayEquals(BigIntegersTest.toByteArray(BigIntegersTest.Pbytes), BigIntegers.encodeUnsigned(BigIntegersTest.P, BigIntegersTest.Pbytes.length));
    }
}

