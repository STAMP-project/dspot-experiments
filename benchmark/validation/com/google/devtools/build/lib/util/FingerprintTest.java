/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.util;


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.clock.BlazeClock;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for Fingerprint.
 */
@RunWith(JUnit4.class)
public class FingerprintTest {
    // You can validate the md5 of the simple string against
    // echo -n 'Hello World!'| md5sum
    @Test
    public void bytesFingerprint() {
        assertThat(new Fingerprint().addBytes("Hello World!".getBytes(StandardCharsets.UTF_8)).hexDigestAndReset()).isEqualTo("ed076287532e86365e841e92bfc50d8c");
        assertThat(Fingerprint.getHexDigest("Hello World!")).isEqualTo("ed076287532e86365e841e92bfc50d8c");
    }

    @Test
    public void otherStringFingerprint() {
        FingerprintTest.assertFingerprintsDiffer(ImmutableList.of("Hello World!"), ImmutableList.of("Goodbye World."));
    }

    @Test
    public void multipleUpdatesDiffer() throws Exception {
        FingerprintTest.assertFingerprintsDiffer(ImmutableList.of("Hello ", "World!"), ImmutableList.of("Hello World!"));
    }

    @Test
    public void multipleUpdatesShiftedDiffer() throws Exception {
        FingerprintTest.assertFingerprintsDiffer(ImmutableList.of("Hello ", "World!"), ImmutableList.of("Hello", " World!"));
    }

    @Test
    public void listFingerprintNotSameAsIndividualElements() throws Exception {
        Fingerprint f1 = new Fingerprint();
        f1.addString("Hello ");
        f1.addString("World!");
        Fingerprint f2 = new Fingerprint();
        f2.addStrings(ImmutableList.of("Hello ", "World!"));
        assertThat(f1.hexDigestAndReset()).isNotEqualTo(f2.hexDigestAndReset());
    }

    @Test
    public void mapFingerprintNotSameAsIndividualElements() throws Exception {
        Fingerprint f1 = new Fingerprint();
        Map<String, String> map = new HashMap<>();
        map.put("Hello ", "World!");
        f1.addStringMap(map);
        Fingerprint f2 = new Fingerprint();
        f2.addStrings(ImmutableList.of("Hello ", "World!"));
        assertThat(f1.hexDigestAndReset()).isNotEqualTo(f2.hexDigestAndReset());
    }

    @Test
    public void addBoolean() throws Exception {
        String f1 = new Fingerprint().addBoolean(true).hexDigestAndReset();
        String f2 = new Fingerprint().addBoolean(false).hexDigestAndReset();
        String f3 = new Fingerprint().addBoolean(true).hexDigestAndReset();
        assertThat(f1).isEqualTo(f3);
        assertThat(f1).isNotEqualTo(f2);
    }

    @Test
    public void addPath() throws Exception {
        PathFragment pf = PathFragment.create("/etc/pwd");
        assertThat(new Fingerprint().addPath(pf).hexDigestAndReset()).isEqualTo("63ab5c47c117635407a1af6377e216bc");
        Path p = new com.google.devtools.build.lib.vfs.inmemoryfs.InMemoryFileSystem(BlazeClock.instance()).getPath(pf);
        assertThat(new Fingerprint().addPath(p).hexDigestAndReset()).isEqualTo("63ab5c47c117635407a1af6377e216bc");
    }

    @Test
    public void addNullableBoolean() throws Exception {
        String f1 = new Fingerprint().addNullableBoolean(null).hexDigestAndReset();
        assertThat(f1).isEqualTo(new Fingerprint().addNullableBoolean(null).hexDigestAndReset());
        assertThat(f1).isNotEqualTo(new Fingerprint().addNullableBoolean(false).hexDigestAndReset());
        assertThat(f1).isNotEqualTo(new Fingerprint().addNullableBoolean(true).hexDigestAndReset());
    }

    @Test
    public void addNullableInteger() throws Exception {
        String f1 = new Fingerprint().addNullableInt(null).hexDigestAndReset();
        assertThat(f1).isEqualTo(new Fingerprint().addNullableInt(null).hexDigestAndReset());
        assertThat(f1).isNotEqualTo(new Fingerprint().addNullableInt(0).hexDigestAndReset());
        assertThat(f1).isNotEqualTo(new Fingerprint().addNullableInt(1).hexDigestAndReset());
    }

    @Test
    public void addNullableString() throws Exception {
        String f1 = new Fingerprint().addNullableString(null).hexDigestAndReset();
        assertThat(f1).isEqualTo(new Fingerprint().addNullableString(null).hexDigestAndReset());
        assertThat(f1).isNotEqualTo(new Fingerprint().addNullableString("").hexDigestAndReset());
    }

    @Test
    public void testReusableAfterReset() throws Exception {
        Fingerprint fp = new Fingerprint();
        String f1 = FingerprintTest.convolutedFingerprintAndReset(fp);
        String f2 = FingerprintTest.convolutedFingerprintAndReset(fp);
        assertThat(f1).isEqualTo(f2);
    }
}

