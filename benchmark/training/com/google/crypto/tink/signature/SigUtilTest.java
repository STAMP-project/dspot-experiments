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
package com.google.crypto.tink.signature;


import Enums.HashType.SHA256;
import Enums.HashType.SHA384;
import Enums.HashType.SHA512;
import HashType.UNKNOWN_HASH;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for SigUtil.
 */
@RunWith(JUnit4.class)
public final class SigUtilTest {
    @Test
    public void testToHashType() throws Exception {
        Assert.assertEquals(SHA256, SigUtil.toHashType(HashType.SHA256));
        Assert.assertEquals(SHA384, SigUtil.toHashType(HashType.SHA384));
        Assert.assertEquals(SHA512, SigUtil.toHashType(HashType.SHA512));
        try {
            SigUtil.toHashType(UNKNOWN_HASH);
            Assert.fail("Invalid hash, should have thrown exception");
        } catch (GeneralSecurityException expected) {
        }
    }
}

