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


import AeadKeyTemplates.AES128_GCM;
import KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC;
import KeyStatusType.DISABLED;
import KeyStatusType.ENABLED;
import Keyset.Key;
import OutputPrefixType.TINK;
import com.google.crypto.tink.config.TinkConfig;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.Keyset;
import com.google.crypto.tink.proto.KeysetInfo;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


// TODO(b/74251398): add tests for other functions.
/**
 * Tests for Util.
 */
@RunWith(JUnit4.class)
public class UtilTest {
    @Test
    public void testValidateKeyset() throws Exception {
        String keyValue = "01234567890123456";
        Keyset keyset = TestUtil.createKeyset(TestUtil.createKey(TestUtil.createHmacKeyData(keyValue.getBytes("UTF-8"), 16), (-42), ENABLED, TINK));
        try {
            Util.validateKeyset(keyset);
        } catch (GeneralSecurityException e) {
            Assert.fail(("Valid keyset; should not throw Exception: " + e));
        }
        // Empty keyset.
        try {
            Util.validateKeyset(Keyset.newBuilder().build());
            Assert.fail("Invalid keyset. Expect GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "empty keyset");
        }
        // Multiple primary keys.
        Keyset invalidKeyset = TestUtil.createKeyset(TestUtil.createKey(TestUtil.createHmacKeyData(keyValue.getBytes("UTF-8"), 16), 42, ENABLED, TINK), TestUtil.createKey(TestUtil.createHmacKeyData(keyValue.getBytes("UTF-8"), 16), 42, ENABLED, TINK));
        try {
            Util.validateKeyset(invalidKeyset);
            Assert.fail("Invalid keyset. Expect GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "keyset contains multiple primary keys");
        }
        // Primary key is disabled.
        invalidKeyset = TestUtil.createKeyset(TestUtil.createKey(TestUtil.createHmacKeyData(keyValue.getBytes("UTF-8"), 16), 42, DISABLED, TINK));
        try {
            Util.validateKeyset(invalidKeyset);
            Assert.fail("Invalid keyset. Expect GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "keyset doesn't contain a valid primary key");
        }
        // No primary key.
        invalidKeyset = Keyset.newBuilder().addKey(Key.newBuilder().setKeyData(TestUtil.createHmacKeyData(keyValue.getBytes("UTF-8"), 16)).setKeyId(1).setStatus(ENABLED).setOutputPrefixType(TINK).build()).build();
        try {
            Util.validateKeyset(invalidKeyset);
            Assert.fail("Invalid keyset. Expect GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "keyset doesn't contain a valid primary key");
        }
        // No primary key, but contains only public key material.
        Keyset validKeyset = Keyset.newBuilder().addKey(Key.newBuilder().setKeyData(TestUtil.createKeyData(KeyData.newBuilder().build(), "typeUrl", ASYMMETRIC_PUBLIC)).setKeyId(1).setStatus(ENABLED).setOutputPrefixType(TINK).build()).build();
        try {
            Util.validateKeyset(validKeyset);
        } catch (GeneralSecurityException e) {
            Assert.fail(("Valid keyset, should not fail: " + e));
        }
    }

    @Test
    public void testValidateKeyset_withDestroyedKey() throws Exception {
        TinkConfig.register();
        KeysetManager keysetManager = KeysetManager.withEmptyKeyset();
        keysetManager.addNewKey(AES128_GCM, true);
        int secondaryKey = keysetManager.addNewKey(AES128_GCM, false);
        keysetManager.destroy(secondaryKey);
        Util.validateKeyset(CleartextKeysetHandle.getKeyset(keysetManager.getKeysetHandle()));
    }

    /**
     * Tests that getKeysetInfo doesn't contain key material.
     */
    @Test
    public void testGetKeysetInfo() throws Exception {
        String keyValue = "01234567890123456";
        Keyset keyset = TestUtil.createKeyset(TestUtil.createKey(TestUtil.createHmacKeyData(keyValue.getBytes("UTF-8"), 16), 42, ENABLED, TINK));
        Assert.assertTrue(keyset.toString().contains(keyValue));
        KeysetInfo keysetInfo = Util.getKeysetInfo(keyset);
        Assert.assertFalse(keysetInfo.toString().contains(keyValue));
    }

    @Test
    public void testAssertExceptionContains() throws Exception {
        TestUtil.assertExceptionContains(new GeneralSecurityException("abc"), "abc");
        try {
            TestUtil.assertExceptionContains(new GeneralSecurityException("abc"), "def");
        } catch (AssertionError e) {
            TestUtil.assertExceptionContains(e, "Got exception with message \"abc\", expected it to contain \"def\".");
        }
    }
}

