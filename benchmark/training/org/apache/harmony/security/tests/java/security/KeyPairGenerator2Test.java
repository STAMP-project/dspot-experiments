/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 *
 *
 * @author Vera Y. Petrashkova
 * @version $Revision$
 */
package org.apache.harmony.security.tests.java.security;


import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.MyKeyPairGenerator1;
import org.apache.harmony.security.tests.support.MyKeyPairGenerator2;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for <code>KeyPairGenerator</code> class constructors and methods.
 */
public class KeyPairGenerator2Test extends TestCase {
    private String KeyPairGeneratorProviderClass = "";

    private static final String KeyPairGeneratorProviderClass1 = "org.apache.harmony.security.tests.support.MyKeyPairGenerator1";

    private static final String KeyPairGeneratorProviderClass2 = "org.apache.harmony.security.tests.support.MyKeyPairGenerator2";

    private static final String KeyPairGeneratorProviderClass3 = "org.apache.harmony.security.tests.support.MyKeyPairGenerator3";

    private static final String KeyPairGeneratorProviderClass4 = "org.apache.harmony.security.tests.support.MyKeyPairGeneratorSpi";

    private static final String defaultAlg = "KPGen";

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static final String[] validValues;

    String post;

    static {
        validValues = new String[4];
        KeyPairGenerator2Test.validValues[0] = KeyPairGenerator2Test.defaultAlg;
        KeyPairGenerator2Test.validValues[1] = KeyPairGenerator2Test.defaultAlg.toLowerCase();
        KeyPairGenerator2Test.validValues[2] = "kpGEN";
        KeyPairGenerator2Test.validValues[3] = "kPGEn";
    }

    Provider mProv;

    String resAlg;

    public void testGetInstance01() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        KeyPairGeneratorProviderClass = KeyPairGenerator2Test.KeyPairGeneratorProviderClass1;
        resAlg = MyKeyPairGenerator1.getResAlgorithm();
        post = "_1";
        setProv();
        GetInstance01(1);
    }

    public void testGetInstance02() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGeneratorProviderClass = KeyPairGenerator2Test.KeyPairGeneratorProviderClass1;
        resAlg = MyKeyPairGenerator1.getResAlgorithm();
        post = "_1";
        setProv();
        GetInstance02(1);
    }

    public void testGetInstance03() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        KeyPairGeneratorProviderClass = KeyPairGenerator2Test.KeyPairGeneratorProviderClass1;
        resAlg = MyKeyPairGenerator1.getResAlgorithm();
        post = "_1";
        setProv();
        GetInstance03(1);
    }

    public void testGetInstance04() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        KeyPairGeneratorProviderClass = KeyPairGenerator2Test.KeyPairGeneratorProviderClass2;
        resAlg = MyKeyPairGenerator2.getResAlgorithm();
        post = "_2";
        setProv();
        GetInstance01(2);
    }

    public void testGetInstance05() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGeneratorProviderClass = KeyPairGenerator2Test.KeyPairGeneratorProviderClass2;
        resAlg = MyKeyPairGenerator2.getResAlgorithm();
        post = "_2";
        setProv();
        GetInstance02(2);
    }

    public void testGetInstance06() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        KeyPairGeneratorProviderClass = KeyPairGenerator2Test.KeyPairGeneratorProviderClass2;
        resAlg = MyKeyPairGenerator2.getResAlgorithm();
        post = "_2";
        setProv();
        GetInstance03(2);
    }

    public void testGetInstance07() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        KeyPairGeneratorProviderClass = KeyPairGenerator2Test.KeyPairGeneratorProviderClass3;
        resAlg = "";
        post = "_3";
        setProv();
        GetInstance01(3);
    }

    public void testGetInstance08() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGeneratorProviderClass = KeyPairGenerator2Test.KeyPairGeneratorProviderClass3;
        resAlg = "";
        post = "_3";
        setProv();
        GetInstance02(3);
    }

    public void testGetInstance09() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        KeyPairGeneratorProviderClass = KeyPairGenerator2Test.KeyPairGeneratorProviderClass3;
        resAlg = "";
        post = "_3";
        setProv();
        GetInstance03(3);
    }

    public void testGetInstance10() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        KeyPairGeneratorProviderClass = KeyPairGenerator2Test.KeyPairGeneratorProviderClass4;
        resAlg = "";
        post = "_4";
        setProv();
        GetInstance01(4);
    }

    public void testGetInstance11() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGeneratorProviderClass = KeyPairGenerator2Test.KeyPairGeneratorProviderClass4;
        resAlg = "";
        post = "_4";
        setProv();
        GetInstance02(4);
    }

    public void testGetInstance12() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        KeyPairGeneratorProviderClass = KeyPairGenerator2Test.KeyPairGeneratorProviderClass4;
        resAlg = "";
        post = "_4";
        setProv();
        GetInstance03(4);
    }
}

