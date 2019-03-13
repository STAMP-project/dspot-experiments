/**
 * Copyright (C) 2015 Neo Visionaries Inc.
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
package com.neovisionaries.ws.client;


import org.junit.Test;


public class TokenTest {
    @Test
    public void test001() {
        TokenTest.isInvalid(null);
    }

    @Test
    public void test002() {
        TokenTest.isInvalid("");
    }

    @Test
    public void test003() {
        TokenTest.isInvalid(" ");
    }

    @Test
    public void test004() {
        TokenTest.isValid("abc");
    }

    @Test
    public void test005() {
        TokenTest.unescape(null, null);
    }

    @Test
    public void test006() {
        TokenTest.unescape("", "");
    }

    @Test
    public void test007() {
        TokenTest.unescape("abc", "abc");
    }

    @Test
    public void test008() {
        TokenTest.unescape("abc", "ab\\c");
    }

    @Test
    public void test009() {
        TokenTest.unescape("ab\\", "ab\\\\");
    }

    @Test
    public void test010() {
        TokenTest.unescape("ab\\c", "ab\\\\c");
    }

    @Test
    public void test011() {
        TokenTest.unquote(null, null);
    }

    @Test
    public void test012() {
        TokenTest.unquote("", "");
    }

    @Test
    public void test013() {
        TokenTest.unquote("abc", "abc");
    }

    @Test
    public void test014() {
        TokenTest.unquote("abc", "\"abc\"");
    }

    @Test
    public void test015() {
        TokenTest.unquote("\"abc", "\"abc");
    }

    @Test
    public void test016() {
        TokenTest.unquote("abc\"", "abc\"");
    }

    @Test
    public void test017() {
        TokenTest.unquote("abc", "\"ab\\c\"");
    }

    @Test
    public void test018() {
        TokenTest.unquote("ab\\c", "\"ab\\\\c\"");
    }
}

