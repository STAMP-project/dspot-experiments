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
package tests.security.cert;


import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for CertificateFactory class constructors and methods
 */
public class CertificateFactory2Test extends TestCase {
    private static final String defaultAlg = "CertFac";

    private static final String CertificateFactoryProviderClass = "org.apache.harmony.security.tests.support.cert.MyCertificateFactorySpi";

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static final String[] validValues;

    static {
        validValues = new String[4];
        CertificateFactory2Test.validValues[0] = CertificateFactory2Test.defaultAlg;
        CertificateFactory2Test.validValues[1] = CertificateFactory2Test.defaultAlg.toLowerCase();
        CertificateFactory2Test.validValues[2] = "CeRtFaC";
        CertificateFactory2Test.validValues[3] = "cerTFac";
    }

    Provider mProv;

    public void testGetInstance01() throws CRLException, CertificateException {
        GetInstance01(true);
    }

    public void testGetInstance02() throws IllegalArgumentException, NoSuchProviderException, CRLException, CertificateException {
        GetInstance02(true);
    }

    public void testGetInstance03() throws IllegalArgumentException, CRLException, CertificateException {
        GetInstance03(true);
    }

    public void testGetInstance04() throws CRLException, CertificateException {
        GetInstance01(false);
    }

    public void testGetInstance05() throws IllegalArgumentException, NoSuchProviderException, CRLException, CertificateException {
        GetInstance02(false);
    }

    public void testGetInstance06() throws IllegalArgumentException, CRLException, CertificateException {
        GetInstance03(false);
    }
}

