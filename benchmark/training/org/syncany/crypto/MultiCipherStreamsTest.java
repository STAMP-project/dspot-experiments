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


import MultiCipherOutputStream.HMAC_SPEC;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.crypto.Mac;
import org.junit.Test;
import org.syncany.config.Logging;


public class MultiCipherStreamsTest {
    private static final Logger logger = Logger.getLogger(MultiCipherStreamsTest.class.getSimpleName());

    private static SaltedSecretKey masterKey;

    static {
        Logging.init();
    }

    @Test
    public void testCipherAes128AndTwofish128() throws Exception {
        doTestEncryption(Arrays.asList(new CipherSpec[]{ CipherSpecs.getCipherSpec(1), CipherSpecs.getCipherSpec(2) }));
    }

    @Test
    public void testCipherAes256AndTwofish256() throws Exception {
        doTestEncryption(Arrays.asList(new CipherSpec[]{ CipherSpecs.getCipherSpec(3), CipherSpecs.getCipherSpec(4) }));
    }

    @Test
    public void testHmacAvailability() throws Exception {
        Mac.getInstance(HMAC_SPEC.getAlgorithm());
        // Should not throw an exception
    }
}

