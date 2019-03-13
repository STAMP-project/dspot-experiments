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
package com.google.crypto.tink;


import OutputPrefixType.CRUNCHY;
import OutputPrefixType.LEGACY;
import OutputPrefixType.RAW;
import OutputPrefixType.TINK;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for CryptoFormat.
 */
@RunWith(JUnit4.class)
public class CryptoFormatTest {
    /**
     * Tests that prefixes for keys with "extreme" key id are generated correctly.
     */
    @Test
    public void testPrefixWithWeirdKeyIds() throws Exception {
        /* INT_MAX */
        /* INT_MIN */
        testPrefix(RAW, 0, (-1), 2147483647, -2147483648);
        testPrefix(TINK, 0, (-1), 2147483647, -2147483648);
        testPrefix(LEGACY, 0, (-1), 2147483647, -2147483648);
        testPrefix(CRUNCHY, 0, (-1), 2147483647, -2147483648);
    }
}

