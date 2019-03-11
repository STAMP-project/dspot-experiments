/**
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.greenrobot.essentials.hash;


import java.util.Random;
import java.util.zip.Checksum;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractChecksumTest {
    protected static final byte[] INPUT4 = new byte[]{ ((byte) (204)), 36, 49, ((byte) (196)) };

    protected static final byte[] INPUT16 = new byte[]{ ((byte) (224)), 77, ((byte) (159)), ((byte) (203)), ((byte) (213)), 107, ((byte) (185)), 83, 66, ((byte) (135)), 8, 54, 119, 35, 1, 0 };

    protected Checksum checksum;

    protected AbstractChecksumTest(Checksum checksum) {
        this.checksum = checksum;
    }

    @Test
    public void testBasics() {
        long initialHash = checksum.getValue();
        for (int b : AbstractChecksumTest.INPUT4) {
            checksum.update(b);
            Assert.assertNotEquals(initialHash, checksum.getValue());
        }
        long hash = checksum.getValue();
        checksum.reset();
        Assert.assertEquals(initialHash, checksum.getValue());
        checksum.update(AbstractChecksumTest.INPUT4, 0, AbstractChecksumTest.INPUT4.length);
        Assert.assertEquals(hash, checksum.getValue());
    }

    @Test
    public void testGetValueStable() {
        checksum.update(AbstractChecksumTest.INPUT16, 0, AbstractChecksumTest.INPUT16.length);
        long hash = checksum.getValue();
        // Calling checksum.getValue() twice should not change hash
        Assert.assertEquals(hash, checksum.getValue());
    }

    @Test
    public void testRestUnaligned() {
        checksum.update(42);
        long hash = checksum.getValue();
        checksum.reset();
        checksum.update(42);
        Assert.assertEquals(hash, checksum.getValue());
    }

    @Test
    public void testMixedUnaligned() {
        checksum.update(AbstractChecksumTest.INPUT16, 0, AbstractChecksumTest.INPUT16.length);
        long hash = checksum.getValue();
        checksum.reset();
        checksum.update(AbstractChecksumTest.INPUT16, 0, 2);
        checksum.update(AbstractChecksumTest.INPUT16[2]);
        checksum.update(AbstractChecksumTest.INPUT16, 3, 11);
        checksum.update(AbstractChecksumTest.INPUT16[14]);
        checksum.update(AbstractChecksumTest.INPUT16[15]);
        Assert.assertEquals(hash, checksum.getValue());
    }

    @Test
    public void testTrailingZero() {
        long lastHash = checksum.getValue();
        Assert.assertEquals(0, AbstractChecksumTest.INPUT16[((AbstractChecksumTest.INPUT16.length) - 1)]);
        for (int b : AbstractChecksumTest.INPUT16) {
            checksum.update(b);
            long hash = checksum.getValue();
            Assert.assertNotEquals(lastHash, hash);
            lastHash = hash;
        }
    }

    @Test
    public void testComparePerByteVsByteArray() {
        byte[] bytes = new byte[1024];
        new Random(42).nextBytes(bytes);
        for (int i = 0; i <= (bytes.length); i++) {
            checksum.reset();
            for (int j = 0; j < i; j++) {
                checksum.update(bytes[j]);
            }
            long expected = checksum.getValue();
            checksum.reset();
            checksum.update(bytes, 0, i);
            Assert.assertEquals(("Iteration " + i), expected, checksum.getValue());
        }
        for (int i = 0; i <= (bytes.length); i++) {
            checksum.reset();
            for (int j = i; j < (bytes.length); j++) {
                checksum.update(bytes[j]);
            }
            long expected = checksum.getValue();
            checksum.reset();
            checksum.update(bytes, i, ((bytes.length) - i));
            Assert.assertEquals((((("Iteration " + i) + " (") + ((bytes.length) - i)) + ")"), expected, checksum.getValue());
        }
    }
}

