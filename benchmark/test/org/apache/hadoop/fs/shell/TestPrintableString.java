/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.shell;


import org.junit.Test;


/**
 * Test {@code PrintableString} class.
 */
public class TestPrintableString {
    /**
     * Test printable characters.
     */
    @Test
    public void testPrintableCharacters() throws Exception {
        // ASCII
        expect("Should keep ASCII letter", "abcdef237", "abcdef237");
        expect("Should keep ASCII symbol", " !\"|}~", " !\"|}~");
        // Unicode BMP
        expect("Should keep Georgian U+1050 and Box Drawing U+2533", "\u1050\u2533--", "\u1050\u2533--");
        // Unicode SMP
        expect("Should keep Linear B U+10000 and Phoenician U+10900", "\ud800\udc00\'\'\'\ud802\udd00", "\ud800\udc00\'\'\'\ud802\udd00");
    }

    /**
     * Test non-printable characters.
     */
    @Test
    public void testNonPrintableCharacters() throws Exception {
        // Control characters
        expect("Should replace single control character", "abc\rdef", "abc?def");
        expect("Should replace multiple control characters", "\babc\tdef", "?abc?def");
        expect("Should replace all control characters", "\f\f\b\n", "????");
        expect("Should replace mixed characters starting with a control", "\u0017ab\u0000", "?ab?");
        // Formatting Unicode
        expect("Should replace Byte Order Mark", "-\ufeff--", "-?--");
        expect("Should replace Invisible Separator", "\u2063\t", "??");
        // Private use Unicode
        expect("Should replace private use U+E000", "\ue000", "?");
        expect("Should replace private use U+E123 and U+F432", "\ue123abc\uf432", "?abc?");
        expect(("Should replace private use in Plane 15 and 16: U+F0000 and " + "U+10FFFD, but keep U+1050"), "x\udb80\udc00y\udbff\udffdz\u1050", "x?y?z\u1050");
        // Unassigned Unicode
        expect("Should replace unassigned U+30000 and U+DFFFF", "-\ud880\udc00-\udb3f\udfff-", "-?-?-");
        // Standalone surrogate character (not in a pair)
        expect("Should replace standalone surrogate U+DB80", "x\udb80yz", "x?yz");
        expect("Should replace standalone surrogate mixed with valid pair", "x\udb80\ud802\udd00yz", "x?\ud802\udd00yz");
    }
}

