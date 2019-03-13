/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.conscrypt;


import java.security.NoSuchAlgorithmException;
import junit.framework.TestCase;


public class OpenSSLSignatureTest extends TestCase {
    public void test_getInstance() throws Exception {
        try {
            OpenSSLSignature.getInstance("SHA1WITHDSA");
            OpenSSLSignature.getInstance("MD5WITHRSAENCRYPTION");
            OpenSSLSignature.getInstance("SHA1WITHRSAENCRYPTION");
            OpenSSLSignature.getInstance("SHA256WITHRSAENCRYPTION");
            OpenSSLSignature.getInstance("SHA384WITHRSAENCRYPTION");
            OpenSSLSignature.getInstance("SHA512WITHRSAENCRYPTION");
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail("getInstance is not case insensitive");
        }
    }
}

