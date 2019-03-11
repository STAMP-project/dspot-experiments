/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.bazel.rules.android.ndkcrosstools;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for NdkRelease.
 */
@RunWith(JUnit4.class)
public class NdkReleaseTest {
    @Test
    public void testReleaseParsing() {
        NdkReleaseTest.testNdkRelease("r8", 8, null, null, false);
        NdkReleaseTest.testNdkRelease("r8 (64-bit)", 8, null, null, true);
        NdkReleaseTest.testNdkRelease("r10", 10, null, null, false);
        NdkReleaseTest.testNdkRelease("r10 (64-bit)", 10, null, null, true);
        NdkReleaseTest.testNdkRelease("r10-rc4", 10, null, "rc4", false);
        NdkReleaseTest.testNdkRelease("r10-rc4 (64-bit)", 10, null, "rc4", true);
        NdkReleaseTest.testNdkRelease("r10e", 10, "e", null, false);
        NdkReleaseTest.testNdkRelease("r10e (64-bit)", 10, "e", null, true);
        NdkReleaseTest.testNdkRelease("r10e-rc4", 10, "e", "rc4", false);
        NdkReleaseTest.testNdkRelease("r10e-rc4 (64-bit)", 10, "e", "rc4", true);
        try {
            // this is actually invalid
            NdkReleaseTest.testNdkRelease("r10e-rc4 (abc)", 10, "e", "rc4", false);
            throw new Error();
        } catch (AssertionError e) {
            // expected
        }
    }

    @Test
    public void test11cReleaseParsing() {
        String releaseString = "Pkg.Desc = Android NDK\n" + "Pkg.Revision = 11.2.2725575\n";
        NdkRelease ndkRelease = NdkRelease.create(releaseString);
        assertThat(ndkRelease.isValid).isTrue();
        assertThat(ndkRelease.rawRelease).isEqualTo("11.2.2725575");
        assertThat(ndkRelease.majorRevision).isEqualTo(11);
        assertThat(ndkRelease.minorRevision).isEqualTo("2");
        assertThat(ndkRelease.releaseCandidate).isNull();
        assertThat(ndkRelease.is64Bit).isTrue();
    }

    @Test
    public void test12bReleaseParsing() {
        String releaseString = "Pkg.Desc = Android NDK\n" + "Pkg.Revision = 12.1.297705\n";
        NdkRelease ndkRelease = NdkRelease.create(releaseString);
        assertThat(ndkRelease.isValid).isTrue();
        assertThat(ndkRelease.rawRelease).isEqualTo("12.1.297705");
        assertThat(ndkRelease.majorRevision).isEqualTo(12);
        assertThat(ndkRelease.minorRevision).isEqualTo("1");
        assertThat(ndkRelease.releaseCandidate).isNull();
        assertThat(ndkRelease.is64Bit).isTrue();
    }

    @Test
    public void testBadRelease() {
        NdkReleaseTest.testBadNdkRelease("");
        NdkReleaseTest.testBadNdkRelease("r");
        NdkReleaseTest.testBadNdkRelease("rZ");
        NdkReleaseTest.testBadNdkRelease("r10erc4");
        NdkReleaseTest.testBadNdkRelease("r10e-rcZ");
        NdkReleaseTest.testBadNdkRelease("r10e-rc4 64-bit");
        NdkReleaseTest.testBadNdkRelease("r10e-rc4 abc");
        NdkReleaseTest.testBadNdkRelease("r10e-rc4 (64-bit) abc");
        NdkReleaseTest.testBadNdkRelease("r10e-rc4 (abc)");
        try {
            // this is actually valid
            NdkReleaseTest.testBadNdkRelease("r10e-rc4 (64-bit)");
            throw new Error();
        } catch (AssertionError e) {
            // expected
        }
    }
}

