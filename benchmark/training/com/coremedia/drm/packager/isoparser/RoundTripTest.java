/**
 * Copyright 2008 CoreMedia AG, Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.coremedia.drm.packager.isoparser;


import junit.framework.TestCase;


/**
 * Tests ISO Roundtrip.
 */
public class RoundTripTest extends TestCase {
    String defaultTestFileDir;

    /* public void testRoundDeleteMe() throws Exception {
    testRoundTrip_1("/suckerpunch-distantplanet_h1080p.mov");
    }
     */
    public void testRoundTrip_TinyExamples_Old() throws Exception {
        testRoundTrip_1(((defaultTestFileDir) + "/Tiny Sample - OLD.mp4"));
    }

    public void testRoundTrip_TinyExamples_Metaxed() throws Exception {
        testRoundTrip_1(((defaultTestFileDir) + "/Tiny Sample - NEW - Metaxed.mp4"));
    }

    public void testRoundTrip_TinyExamples_Untouched() throws Exception {
        testRoundTrip_1(((defaultTestFileDir) + "/Tiny Sample - NEW - Untouched.mp4"));
    }

    public void testRoundTrip_1a() throws Exception {
        testRoundTrip_1(((defaultTestFileDir) + "/multiTrack.3gp"));
    }

    public void testRoundTrip_1b() throws Exception {
        testRoundTrip_1(((defaultTestFileDir) + "/MOV00006.3gp"));
    }

    public void testRoundTrip_1c() throws Exception {
        testRoundTrip_1(((defaultTestFileDir) + "/Beethoven - Bagatelle op.119 no.11 i.m4a"));
    }

    public void testRoundTrip_1d() throws Exception {
        testRoundTrip_1(((defaultTestFileDir) + "/test.m4p"));
    }

    public void testRoundTrip_1e() throws Exception {
        testRoundTrip_1(((defaultTestFileDir) + "/test-pod.m4a"));
    }

    public void testRoundTrip_QuickTimeFormat() throws Exception {
        testRoundTrip_1(((defaultTestFileDir) + "/QuickTimeFormat.mp4"));
    }
}

