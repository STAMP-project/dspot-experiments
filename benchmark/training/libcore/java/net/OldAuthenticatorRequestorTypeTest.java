/**
 * Copyright (C) 2008 The Android Open Source Project
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
package libcore.java.net;


import java.net.Authenticator;
import junit.framework.TestCase;

import static java.net.Authenticator.RequestorType.PROXY;
import static java.net.Authenticator.RequestorType.SERVER;
import static java.net.Authenticator.RequestorType.valueOf;
import static java.net.Authenticator.RequestorType.values;


public class OldAuthenticatorRequestorTypeTest extends TestCase {
    public void test_valueOfLjava_lang_String() {
        TestCase.assertEquals(PROXY, valueOf("PROXY"));
        TestCase.assertEquals(SERVER, valueOf("SERVER"));
        try {
            valueOf("TEST");
            TestCase.fail("IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void test_values() {
        Authenticator.RequestorType[] expectedTypes = new Authenticator.RequestorType[]{ PROXY, SERVER };
        Authenticator.RequestorType[] types = values();
        TestCase.assertEquals(expectedTypes.length, types.length);
        for (int i = 0; i < (expectedTypes.length); i++) {
            TestCase.assertEquals(expectedTypes[i], types[i]);
        }
    }
}

