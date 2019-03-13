/**
 * Copyright 2018 Google Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.google.crypto.tink.subtle;


import HashType.SHA1;
import HashType.SHA256;
import HashType.SHA512;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for SubtleUtil.
 */
@RunWith(JUnit4.class)
public final class SubtleUtilTest {
    @Test
    public void testToEcdsaAlgo() throws Exception {
        Assert.assertEquals("SHA256withECDSA", SubtleUtil.toEcdsaAlgo(SHA256));
        Assert.assertEquals("SHA512withECDSA", SubtleUtil.toEcdsaAlgo(SHA512));
        try {
            SubtleUtil.toEcdsaAlgo(SHA1);
            Assert.fail("Invalid hash, should have thrown exception");
        } catch (GeneralSecurityException expected) {
        }
    }

    @Test
    public void testToRsaSsaPkcs1Algo() throws Exception {
        Assert.assertEquals("SHA256withRSA", SubtleUtil.toRsaSsaPkcs1Algo(SHA256));
        Assert.assertEquals("SHA512withRSA", SubtleUtil.toRsaSsaPkcs1Algo(SHA512));
        try {
            SubtleUtil.toRsaSsaPkcs1Algo(SHA1);
            Assert.fail("Invalid hash, should have thrown exception");
        } catch (GeneralSecurityException expected) {
        }
    }
}

