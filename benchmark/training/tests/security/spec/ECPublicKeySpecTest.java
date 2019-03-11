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
package tests.security.spec;


import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import junit.framework.TestCase;


public class ECPublicKeySpecTest extends TestCase {
    ECPoint w;

    ECParameterSpec params;

    ECPublicKeySpec ecpks;

    /**
     * test for constructor ECPublicKeySpec(ECPoint, ECParameterSpec)
     * test covers following usecases:
     * case 1: creating object with valid parameters
     * case 2: catch NullPointerException - if w is null.
     * case 3: catch NullPointerException - if params is null.
     */
    public final void test_constructorLjava_security_spec_ECPointLjava_security_spec_ECParameterSpec() {
        // case 1: creating object with valid parameters
        TestCase.assertEquals("wrong params value", params, ecpks.getParams());
        TestCase.assertEquals("wrong w value", w, ecpks.getW());
        // case 2: catch NullPointerException - if w is null.
        try {
            new ECPublicKeySpec(null, params);
            TestCase.fail("NullPointerException has not been thrown");
        } catch (NullPointerException e) {
            // expected
        }
        // case 3: catch NullPointerException - if params is null.
        try {
            new ECPublicKeySpec(w, null);
            TestCase.fail("NullPointerException has not been thrown");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * test for getW() method
     */
    public final void testGetW() {
        TestCase.assertEquals("wrong w value", w, ecpks.getW());
    }

    /**
     * test for getParams() meyhod
     */
    public final void testGetParams() {
        TestCase.assertEquals("wrong params value", params, ecpks.getParams());
    }
}

