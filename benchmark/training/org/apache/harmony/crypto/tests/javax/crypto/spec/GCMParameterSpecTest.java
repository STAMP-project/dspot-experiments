/**
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.crypto.tests.javax.crypto.spec;


import java.util.Arrays;
import javax.crypto.spec.GCMParameterSpec;
import junit.framework.TestCase;


public class GCMParameterSpecTest extends TestCase {
    private static final byte[] TEST_IV = new byte[8];

    public void testConstructor_IntByteArray_Success() throws Exception {
        new GCMParameterSpec(8, GCMParameterSpecTest.TEST_IV);
    }

    public void testConstructor_IntByteArray_NegativeTLen_Failure() throws Exception {
        try {
            new GCMParameterSpec((-1), GCMParameterSpecTest.TEST_IV);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testConstructor_IntByteArray_NullIv_Failure() throws Exception {
        try {
            new GCMParameterSpec(8, null);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testConstructor_IntByteArrayWithOffsets_Success() throws Exception {
        new GCMParameterSpec(8, GCMParameterSpecTest.TEST_IV, 0, GCMParameterSpecTest.TEST_IV.length);
    }

    public void testConstructor_IntByteArrayWithOffsets_NullIv_Failure() throws Exception {
        try {
            new GCMParameterSpec(8, null, 0, GCMParameterSpecTest.TEST_IV.length);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testConstructor_IntByteArrayWithOffsets_NegativeOffset_Failure() throws Exception {
        try {
            new GCMParameterSpec(8, GCMParameterSpecTest.TEST_IV, (-1), GCMParameterSpecTest.TEST_IV.length);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testConstructor_IntByteArrayWithOffsets_TooLongLength_Failure() throws Exception {
        try {
            new GCMParameterSpec(8, GCMParameterSpecTest.TEST_IV, 0, ((GCMParameterSpecTest.TEST_IV.length) + 1));
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testGetIV_Success() throws Exception {
        GCMParameterSpec spec = new GCMParameterSpec(8, GCMParameterSpecTest.TEST_IV);
        byte[] actual = spec.getIV();
        TestCase.assertEquals(Arrays.toString(GCMParameterSpecTest.TEST_IV), Arrays.toString(actual));
        // XOR with 0xFF so we're sure we changed the array
        for (int i = 0; i < (actual.length); i++) {
            actual[i] ^= 255;
        }
        TestCase.assertFalse("Changing the IV returned shouldn't change the parameter spec", Arrays.equals(spec.getIV(), actual));
        TestCase.assertEquals(Arrays.toString(GCMParameterSpecTest.TEST_IV), Arrays.toString(spec.getIV()));
    }

    public void testGetIV_Subarray_Success() throws Exception {
        GCMParameterSpec spec = new GCMParameterSpec(8, GCMParameterSpecTest.TEST_IV, 2, 4);
        TestCase.assertEquals(Arrays.toString(Arrays.copyOfRange(GCMParameterSpecTest.TEST_IV, 2, 6)), Arrays.toString(spec.getIV()));
    }

    public void testGetTLen_Success() throws Exception {
        GCMParameterSpec spec = new GCMParameterSpec(8, GCMParameterSpecTest.TEST_IV);
        TestCase.assertEquals(8, spec.getTLen());
    }
}

