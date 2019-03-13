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
package libcore.java.util.zip;


import junit.framework.TestCase;


/**
 * tests for CRC32 and Adler32 checksum algorithms.
 */
public class OldAndroidChecksumTest extends TestCase {
    public void testChecksum() throws Exception {
        /* Values computed experimentally, using C interfaces. */
        adler32Test(OldAndroidChecksumTest.mTestString, 2648838363L);
        cRC32Test(OldAndroidChecksumTest.mTestString, 2476672175L);
        // Test for issue 1016037
        wrongChecksumWithAdler32Test();
    }

    // "The quick brown fox jumped over the lazy dogs\n"
    private static byte[] mTestString = new byte[]{ 84, 104, 101, 32, 113, 117, 105, 99, 107, 32, 98, 114, 111, 119, 110, 32, 102, 111, 120, 32, 106, 117, 109, 112, 101, 100, 32, 111, 118, 101, 114, 32, 116, 104, 101, 32, 108, 97, 122, 121, 32, 100, 111, 103, 115, 46, 10 };
}

