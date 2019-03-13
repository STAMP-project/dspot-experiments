/**
 * Copyright (C) 2008 The Android Open Source Project
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
package com.android.org.bouncycastle.crypto.digests;


import com.android.org.bouncycastle.crypto.Digest;
import junit.framework.TestCase;


/**
 * Implements unit tests for our JNI wrapper around OpenSSL. We use the
 * existing Bouncy Castle implementation as our test oracle.
 */
public class DigestTest extends TestCase {
    /**
     * Tests the MD5 implementation.
     */
    public void testMD5() {
        Digest oldDigest = new MD5Digest();
        Digest newDigest = new OpenSSLDigest.MD5();
        doTestMessageDigest(oldDigest, newDigest);
    }

    /**
     * Tests the SHA-1 implementation.
     */
    public void testSHA1() {
        Digest oldDigest = new SHA1Digest();
        Digest newDigest = new OpenSSLDigest.SHA1();
        doTestMessageDigest(oldDigest, newDigest);
    }

    /**
     * Tests the SHA-256 implementation.
     */
    public void testSHA256() {
        Digest oldDigest = new SHA256Digest();
        Digest newDigest = new OpenSSLDigest.SHA256();
        doTestMessageDigest(oldDigest, newDigest);
    }

    /**
     * Tests the SHA-384 implementation.
     */
    public void testSHA384() {
        Digest oldDigest = new SHA384Digest();
        Digest newDigest = new OpenSSLDigest.SHA384();
        doTestMessageDigest(oldDigest, newDigest);
    }

    /**
     * Tests the SHA-512 implementation.
     */
    public void testSHA512() {
        Digest oldDigest = new SHA512Digest();
        Digest newDigest = new OpenSSLDigest.SHA512();
        doTestMessageDigest(oldDigest, newDigest);
    }
}

