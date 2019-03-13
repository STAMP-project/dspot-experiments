/**
 * Copyright 2017 Google Inc.
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
package com.google.crypto.tink.streamingaead;


import HashType.SHA1;
import HashType.SHA256;
import HashType.SHA512;
import HashType.UNKNOWN_HASH;
import com.google.crypto.tink.TestUtil;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for StreamingAeadUtil.
 */
@RunWith(JUnit4.class)
public class StreamingAeadUtilTest {
    @Test
    public void testToHmacAlgo() throws Exception {
        Assert.assertEquals("HmacSha1", StreamingAeadUtil.toHmacAlgo(SHA1));
        Assert.assertEquals("HmacSha256", StreamingAeadUtil.toHmacAlgo(SHA256));
        Assert.assertEquals("HmacSha512", StreamingAeadUtil.toHmacAlgo(SHA512));
        try {
            StreamingAeadUtil.toHmacAlgo(UNKNOWN_HASH);
            Assert.fail("should throw NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException ex) {
            TestUtil.assertExceptionContains(ex, "hash unsupported for HMAC");
        }
    }
}

