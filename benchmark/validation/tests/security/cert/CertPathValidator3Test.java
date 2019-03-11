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


import java.security.InvalidAlgorithmParameterException;
import java.security.Provider;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.PKIXParameters;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;
import org.apache.harmony.security.tests.support.cert.MyCertPath;
import org.apache.harmony.security.tests.support.cert.TestUtils;


/**
 * Tests for <code>CertPathValidator</code> class  methods.
 */
public class CertPathValidator3Test extends TestCase {
    private static final String defaultType = CertPathBuilder1Test.defaultType;

    private static boolean PKIXSupport = false;

    private static Provider defaultProvider;

    private static String defaultProviderName;

    private static String NotSupportMsg = "";

    static {
        CertPathValidator3Test.defaultProvider = SpiEngUtils.isSupport(CertPathValidator3Test.defaultType, CertPathValidator1Test.srvCertPathValidator);
        CertPathValidator3Test.PKIXSupport = (CertPathValidator3Test.defaultProvider) != null;
        CertPathValidator3Test.defaultProviderName = (CertPathValidator3Test.PKIXSupport) ? CertPathValidator3Test.defaultProvider.getName() : null;
        CertPathValidator3Test.NotSupportMsg = CertPathValidator3Test.defaultType.concat(" is not supported");
    }

    /**
     * Test for <code>validate(CertPath certpath, CertPathParameters params)</code> method
     * Assertion: throws InvalidAlgorithmParameterException
     * when params is instance of PKIXParameters and
     * certpath is not X.509 type
     */
    public void testValidate01() throws InvalidAlgorithmParameterException, CertPathValidatorException {
        if (!(CertPathValidator3Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator3Test.NotSupportMsg);
            return;
        }
        MyCertPath mCP = new MyCertPath(new byte[0]);
        CertPathParameters params = new PKIXParameters(TestUtils.getTrustAnchorSet());
        CertPathValidator[] certPV = CertPathValidator3Test.createCPVs();
        TestCase.assertNotNull("CertPathValidator objects were not created", certPV);
        for (int i = 0; i < (certPV.length); i++) {
            try {
                certPV[i].validate(mCP, null);
                TestCase.fail("InvalidAlgorithmParameterException must be thrown");
            } catch (InvalidAlgorithmParameterException e) {
            }
            try {
                certPV[i].validate(null, params);
                TestCase.fail("NullPointerException must be thrown");
            } catch (NullPointerException e) {
            }
        }
    }
}

