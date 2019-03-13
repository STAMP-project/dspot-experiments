/**
 * Copyright (C) 2007 The Android Open Source Project
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
package tests.api.javax.net.ssl;


import javax.net.ssl.SSLHandshakeException;
import junit.framework.TestCase;


public class SSLHandshakeExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    /**
     * Test for <code>SSLHandshakeException(String)</code> constructor Assertion:
     * constructs SSLHandshakeException with detail message msg. Parameter
     * <code>msg</code> is not null.
     */
    public void test_Constructor01() {
        SSLHandshakeException sslE;
        for (int i = 0; i < (SSLHandshakeExceptionTest.msgs.length); i++) {
            sslE = new SSLHandshakeException(SSLHandshakeExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(SSLHandshakeExceptionTest.msgs[i]), sslE.getMessage(), SSLHandshakeExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", sslE.getCause());
        }
    }

    /**
     * Test for <code>SSLHandshakeException(String)</code> constructor Assertion:
     * constructs SSLHandshakeException with detail message msg. Parameter
     * <code>msg</code> is null.
     */
    public void test_Constructor02() {
        String msg = null;
        SSLHandshakeException sslE = new SSLHandshakeException(msg);
        TestCase.assertNull("getMessage() must return null.", sslE.getMessage());
        TestCase.assertNull("getCause() must return null", sslE.getCause());
    }
}

