/**
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2016 Philipp C. Heckel <philipp.heckel@gmail.com>
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
package org.syncany.crypto;


import CipherSpecs.AES_128_GCM;
import CipherSpecs.TWOFISH_128_GCM;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class CipherSpecsTest {
    @Test
    public void testCipherSpecs() {
        Map<Integer, CipherSpec> availableCipherSpecs = CipherSpecs.getAvailableCipherSpecs();
        Assert.assertEquals(4, availableCipherSpecs.size());
        Assert.assertEquals(availableCipherSpecs.get(AES_128_GCM).getAlgorithm(), "AES/GCM/NoPadding");
    }

    @Test
    public void testCipherSpec2() {
        CipherSpec twofish128CipherSpec = CipherSpecs.getCipherSpec(TWOFISH_128_GCM);
        Assert.assertEquals(twofish128CipherSpec.getId(), 2);
        Assert.assertEquals(twofish128CipherSpec.getAlgorithm(), "Twofish/GCM/NoPadding");
        Assert.assertEquals(twofish128CipherSpec.getKeySize(), 128);
        Assert.assertEquals(twofish128CipherSpec.getIvSize(), 128);
        Assert.assertEquals(twofish128CipherSpec.needsUnlimitedStrength(), false);
        Assert.assertNotNull(twofish128CipherSpec.toString());
    }

    @Test
    public void testCipherSpecHashCodeEquals() {
        CipherSpec cipherSpec1 = CipherSpecs.getCipherSpec(AES_128_GCM);
        CipherSpec cipherSpec2 = CipherSpecs.getCipherSpec(TWOFISH_128_GCM);
        Assert.assertNotSame(cipherSpec1.hashCode(), cipherSpec2.hashCode());
        Assert.assertNotSame(cipherSpec1, cipherSpec2);
        Assert.assertEquals(1, cipherSpec1.getId());
    }
}

