/**
 * Copyright 2013, Google Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jf.util;


import org.junit.Test;


public class StringWrapperTest {
    @Test
    public void testWrapStringByWords() {
        StringWrapperTest.validateResult2(new String[]{ "abc", "abcdef", "abcdef" }, "abc\nabcdefabcdef", 6);
        StringWrapperTest.validateResult2(new String[]{ "abc", "abcdef", " ", "abcdef" }, "abc\nabcdef abcdef", 6);
        StringWrapperTest.validateResult2(new String[]{ "abc", "abcde ", "fabcde", "f" }, "abc\nabcde fabcdef", 6);
        StringWrapperTest.validateResult2(new String[]{ "abc def ghi ", "kjl mon pqr ", "stu vwx yz" }, "abc def ghi kjl mon pqr stu vwx yz", 14);
        StringWrapperTest.validateResult2(new String[]{ "abcdefg", "hikjlmo", "npqrstu", "vwxyz" }, "abcdefghikjlmonpqrstuvwxyz", 7);
        StringWrapperTest.validateResult2(new String[]{ "abc", "defhig" }, "abc\ndefhig", 20);
    }

    @Test
    public void testWrapString() {
        StringWrapperTest.validateResult(new String[]{ "abc", "abcdef", "abcdef" }, StringWrapper.wrapString("abc\nabcdefabcdef", 6, null));
        StringWrapperTest.validateResult(new String[]{ "abc" }, StringWrapper.wrapString("abc", 6, new String[3]));
        StringWrapperTest.validateResult(new String[]{ "abc" }, StringWrapper.wrapString("abc", 6, new String[0]));
        StringWrapperTest.validateResult(new String[]{ "abc" }, StringWrapper.wrapString("abc", 6, new String[1]));
        StringWrapperTest.validateResult(new String[]{ "" }, StringWrapper.wrapString("", 6, new String[3]));
        StringWrapperTest.validateResult(new String[]{ "abcdef" }, StringWrapper.wrapString("abcdef", 6, new String[3]));
        StringWrapperTest.validateResult(new String[]{ "abcdef", "abcdef" }, StringWrapper.wrapString("abcdef\nabcdef", 6, new String[3]));
        StringWrapperTest.validateResult(new String[]{ "abc", "", "def" }, StringWrapper.wrapString("abc\n\ndef", 6, new String[3]));
        StringWrapperTest.validateResult(new String[]{ "", "abcdef" }, StringWrapper.wrapString("\nabcdef", 6, new String[3]));
        StringWrapperTest.validateResult(new String[]{ "", "", "abcdef" }, StringWrapper.wrapString("\n\nabcdef", 6, new String[3]));
        StringWrapperTest.validateResult(new String[]{ "", "", "abcdef" }, StringWrapper.wrapString("\n\nabcdef", 6, new String[4]));
        StringWrapperTest.validateResult(new String[]{ "", "", "abcdef", "" }, StringWrapper.wrapString("\n\nabcdef\n\n", 6, new String[4]));
        StringWrapperTest.validateResult(new String[]{ "", "", "abcdef", "a", "" }, StringWrapper.wrapString("\n\nabcdefa\n\n", 6, new String[4]));
        StringWrapperTest.validateResult(new String[]{ "", "", "abcdef", "a", "" }, StringWrapper.wrapString("\n\nabcdefa\n\n", 6, new String[0]));
        StringWrapperTest.validateResult(new String[]{ "", "", "abcdef", "a", "" }, StringWrapper.wrapString("\n\nabcdefa\n\n", 6, new String[5]));
        StringWrapperTest.validateResult(new String[]{ "", "", "a", "b", "c", "d", "e", "f", "a", "" }, StringWrapper.wrapString("\n\nabcdefa\n\n", 1, new String[5]));
    }
}

