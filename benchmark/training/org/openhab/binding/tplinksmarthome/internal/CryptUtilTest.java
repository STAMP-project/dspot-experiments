/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.tplinksmarthome.internal;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for {@link CryptUtil} class.
 *
 * @author Hilbrand Bouwkamp - Initial contribution
 */
public class CryptUtilTest {
    private static final String TEST_STRING = "This is just a message";

    /**
     * Test round trip of encrypt and decrypt that should return the same value.
     *
     * @throws IOException
     * 		exception in case device not reachable
     */
    @Test
    public void testCrypt() throws IOException {
        Assert.assertEquals("Crypting should result in same string", CryptUtilTest.TEST_STRING, CryptUtil.decrypt(CryptUtil.encrypt(CryptUtilTest.TEST_STRING), CryptUtilTest.TEST_STRING.length()));
    }

    /**
     * Test round trip of encrypt and decrypt with length that should return the same value.
     *
     * @throws IOException
     * 		exception in case device not reachable
     */
    @Test
    public void testCryptWithLength() throws IOException {
        try (final ByteArrayInputStream is = new ByteArrayInputStream(CryptUtil.encryptWithLength(CryptUtilTest.TEST_STRING))) {
            Assert.assertEquals("Crypting should result in same string", CryptUtilTest.TEST_STRING, CryptUtil.decryptWithLength(is));
        }
    }
}

