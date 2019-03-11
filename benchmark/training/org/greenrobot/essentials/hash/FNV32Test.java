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


import org.junit.Assert;
import org.junit.Test;


public class FNV32Test extends AbstractChecksumTest {
    private static final byte[] INPUT32_ZERO1 = new byte[]{ ((byte) (204)), 36, 49, ((byte) (196)) };

    private static final byte[] INPUT32_ZERO2 = new byte[]{ ((byte) (224)), 77, ((byte) (159)), ((byte) (203)) };

    public FNV32Test() {
        super(new FNV32());
    }

    @Test
    public void testFnv32UpdateZeroHash() {
        for (int b : FNV32Test.INPUT32_ZERO1) {
            checksum.update(b);
        }
        Assert.assertEquals(0, checksum.getValue());
        checksum.reset();
        for (int b : FNV32Test.INPUT32_ZERO2) {
            checksum.update(b);
        }
        Assert.assertEquals(0, checksum.getValue());
    }

    @Test
    public void testFnv32UpdateBytesZeroHash() {
        checksum.update(FNV32Test.INPUT32_ZERO1, 0, FNV32Test.INPUT32_ZERO1.length);
        Assert.assertEquals(0, checksum.getValue());
        checksum.reset();
        checksum.update(FNV32Test.INPUT32_ZERO2, 0, FNV32Test.INPUT32_ZERO1.length);
        Assert.assertEquals(0, checksum.getValue());
    }
}

