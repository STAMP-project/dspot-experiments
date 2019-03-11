/**
 * Copyright 2016, Google Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 * Neither the name of Google Inc. nor the names of its
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
package org.jf.smali;


import java.util.HashMap;
import org.junit.Test;
import smaliParser.tokenNames;

import static smaliParser.tokenNames;


public class LexerTest {
    private static final HashMap<String, Integer> tokenTypesByName;

    static {
        tokenTypesByName = new HashMap<String, Integer>();
        for (int i = 0; i < (tokenNames.length); i++) {
            LexerTest.tokenTypesByName.put(tokenNames[i], i);
        }
    }

    @Test
    public void DirectiveTest() {
        runTest("DirectiveTest");
    }

    @Test
    public void ByteLiteralTest() {
        runTest("ByteLiteralTest");
    }

    @Test
    public void ShortLiteralTest() {
        runTest("ShortLiteralTest");
    }

    @Test
    public void IntegerLiteralTest() {
        runTest("IntegerLiteralTest");
    }

    @Test
    public void LongLiteralTest() {
        runTest("LongLiteralTest");
    }

    @Test
    public void FloatLiteralTest() {
        runTest("FloatLiteralTest");
    }

    @Test
    public void CharLiteralTest() {
        runTest("CharLiteralTest");
    }

    @Test
    public void StringLiteralTest() {
        runTest("StringLiteralTest");
    }

    @Test
    public void MiscTest() {
        runTest("MiscTest");
    }

    @Test
    public void CommentTest() {
        runTest("CommentTest", false);
    }

    @Test
    public void InstructionTest() {
        runTest("InstructionTest", true);
    }

    @Test
    public void TypeAndIdentifierTest() {
        runTest("TypeAndIdentifierTest");
    }

    @Test
    public void SymbolTest() {
        runTest("SymbolTest", false);
    }

    @Test
    public void RealSmaliFileTest() {
        runTest("RealSmaliFileTest", true);
    }
}

