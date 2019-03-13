/**
 * Copyright (C) 2016 Neo Visionaries Inc.
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


public class MiscTest {
    @Test
    public void test01() {
        MiscTest.extractHostFromAuthorityPartTest("example.com", "example.com");
    }

    @Test
    public void test02() {
        MiscTest.extractHostFromAuthorityPartTest("example.com", "example.com:8080");
    }

    @Test
    public void test03() {
        MiscTest.extractHostFromAuthorityPartTest("example.com", "id:password@example.com");
    }

    @Test
    public void test04() {
        MiscTest.extractHostFromAuthorityPartTest("example.com", "id:password@example.com:8080");
    }

    @Test
    public void test05() {
        MiscTest.extractHostFromAuthorityPartTest("example.com", "id@example.com");
    }

    @Test
    public void test06() {
        MiscTest.extractHostFromAuthorityPartTest("example.com", "id:@example.com");
    }

    @Test
    public void test07() {
        MiscTest.extractHostFromAuthorityPartTest("example.com", ":@example.com");
    }

    @Test
    public void test08() {
        MiscTest.extractHostFromAuthorityPartTest("example.com", ":password@example.com");
    }

    @Test
    public void test09() {
        MiscTest.extractHostFromAuthorityPartTest("example.com", "@example.com");
    }

    @Test
    public void test10() {
        MiscTest.extractHostFromEntireUriTest("example.com", "ws://example.com");
    }

    @Test
    public void test11() {
        MiscTest.extractHostFromEntireUriTest("example.com", "ws://example.com:8080");
    }

    @Test
    public void test12() {
        MiscTest.extractHostFromEntireUriTest("example.com", "ws://id:password@example.com");
    }

    @Test
    public void test13() {
        MiscTest.extractHostFromEntireUriTest("example.com", "ws://id:password@example.com:8080");
    }

    @Test
    public void test14() {
        MiscTest.extractHostFromEntireUriTest("example.com", "ws://example.com/");
    }

    @Test
    public void test15() {
        MiscTest.extractHostFromEntireUriTest("example.com", "ws://example.com:8080/");
    }

    @Test
    public void test16() {
        MiscTest.extractHostFromEntireUriTest("example.com", "ws://id:password@example.com/");
    }

    @Test
    public void test17() {
        MiscTest.extractHostFromEntireUriTest("example.com", "ws://id:password@example.com:8080/");
    }

    @Test
    public void test18() {
        MiscTest.extractHostFromEntireUriTest("example.com", "ws://example.com/path?key=@value");
    }

    @Test
    public void test19() {
        MiscTest.extractHostFromEntireUriTest("example.com", "ws://example.com:8080/path?key=@value");
    }

    @Test
    public void test20() {
        MiscTest.extractHostFromEntireUriTest("example.com", "ws://id:password@example.com/path?key=@value");
    }

    @Test
    public void test21() {
        MiscTest.extractHostFromEntireUriTest("example.com", "ws://id:password@example.com:8080/path?key=@value");
    }

    @Test
    public void test22() {
        MiscTest.extractHostTest("example.com", "ws://example.com");
    }

    @Test
    public void test23() {
        MiscTest.extractHostTest("example.com", "ws://example.com:8080");
    }

    @Test
    public void test24() {
        MiscTest.extractHostTest("example.com", "ws://id:password@example.com");
    }

    @Test
    public void test25() {
        MiscTest.extractHostTest("example.com", "ws://id:password@example.com:8080");
    }

    @Test
    public void test26() {
        MiscTest.extractHostTest("example.com", "ws://id:password@example.com:8080/path?key=@value");
    }
}

