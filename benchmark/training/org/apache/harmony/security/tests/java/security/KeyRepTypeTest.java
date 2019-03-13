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
package org.apache.harmony.security.tests.java.security;


import java.security.KeyRep;
import java.util.Arrays;
import junit.framework.TestCase;

import static java.security.KeyRep.Type.PRIVATE;
import static java.security.KeyRep.Type.PUBLIC;
import static java.security.KeyRep.Type.SECRET;
import static java.security.KeyRep.Type.valueOf;
import static java.security.KeyRep.Type.values;


public class KeyRepTypeTest extends TestCase {
    /**
     * java.security.KeyRep.Type#valueOf(String)
     */
    public void testValueOf() {
        try {
            valueOf("type");
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            valueOf(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        TestCase.assertEquals(PRIVATE, valueOf(PRIVATE.toString()));
        TestCase.assertEquals(PUBLIC, valueOf(PUBLIC.toString()));
        TestCase.assertEquals(SECRET, valueOf(SECRET.toString()));
    }

    /**
     * java.security.KeyRep.Type#values()
     */
    public void testValues() {
        KeyRep.Type[] types = new KeyRep.Type[]{ SECRET, PUBLIC, PRIVATE };
        try {
            TestCase.assertTrue(Arrays.equals(types, values()));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.getMessage())));
        }
    }
}

