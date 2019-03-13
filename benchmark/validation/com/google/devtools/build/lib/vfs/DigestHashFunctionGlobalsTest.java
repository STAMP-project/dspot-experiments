/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.vfs;


import DigestHashFunction.DefaultAlreadySetException;
import DigestHashFunction.MD5;
import DigestHashFunction.SHA1;
import DigestHashFunction.SHA256;
import com.google.common.hash.Hashing;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.vfs.DigestHashFunction.DefaultHashFunctionNotSetException;
import com.google.devtools.build.lib.vfs.DigestHashFunction.DigestFunctionConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for DigestHashFunction, notably that the static instances can be compared with reference
 * equality.
 */
@RunWith(JUnit4.class)
public class DigestHashFunctionGlobalsTest {
    private final DigestFunctionConverter converter = new DigestFunctionConverter();

    @Test
    public void convertReturnsTheSameValueAsTheConstant() throws Exception {
        assertThat(converter.convert("sha-256")).isSameAs(SHA256);
        assertThat(converter.convert("SHA-256")).isSameAs(SHA256);
        assertThat(converter.convert("SHA256")).isSameAs(SHA256);
        assertThat(converter.convert("sha256")).isSameAs(SHA256);
        assertThat(converter.convert("SHA-1")).isSameAs(SHA1);
        assertThat(converter.convert("sha-1")).isSameAs(SHA1);
        assertThat(converter.convert("SHA1")).isSameAs(SHA1);
        assertThat(converter.convert("sha1")).isSameAs(SHA1);
        assertThat(converter.convert("MD5")).isSameAs(MD5);
        assertThat(converter.convert("md5")).isSameAs(MD5);
    }

    @Test
    public void lateRegistrationGetsPickedUpByConverter() throws Exception {
        DigestHashFunction.register(Hashing.goodFastHash(32), "SHA-512");
        assertThat(converter.convert("SHA-512")).isSameAs(converter.convert("sha-512"));
    }

    @Test
    public void lateRegistrationWithAlternativeNamesGetsPickedUpByConverter() throws Exception {
        DigestHashFunction.register(Hashing.goodFastHash(64), "SHA-384", "SHA384", "SHA_384");
        assertThat(converter.convert("SHA-384")).isSameAs(converter.convert("SHA-384"));
        assertThat(converter.convert("Sha-384")).isSameAs(converter.convert("SHA-384"));
        assertThat(converter.convert("sha-384")).isSameAs(converter.convert("SHA-384"));
        assertThat(converter.convert("SHA384")).isSameAs(converter.convert("SHA-384"));
        assertThat(converter.convert("Sha384")).isSameAs(converter.convert("SHA-384"));
        assertThat(converter.convert("sha384")).isSameAs(converter.convert("SHA-384"));
        assertThat(converter.convert("SHA_384")).isSameAs(converter.convert("SHA-384"));
        assertThat(converter.convert("Sha_384")).isSameAs(converter.convert("SHA-384"));
        assertThat(converter.convert("sha_384")).isSameAs(converter.convert("SHA-384"));
    }

    @Test
    public void unsetDefaultThrows() {
        MoreAsserts.assertThrows(DefaultHashFunctionNotSetException.class, () -> DigestHashFunction.getDefault());
    }

    @Test
    public void setDefaultDoesNotThrow() throws Exception {
        DigestHashFunction.setDefault(SHA1);
        DigestHashFunction.getDefault();
    }

    @Test
    public void cannotSetDefaultMultipleTimes() throws Exception {
        DigestHashFunction.setDefault(MD5);
        MoreAsserts.assertThrows(DefaultAlreadySetException.class, () -> DigestHashFunction.setDefault(SHA1));
    }
}

