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
package libcore.java.security;


import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.Security;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;


public class KeyPairGeneratorTest extends TestCase {
    public void test_getInstance() throws Exception {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                String type = service.getType();
                if (!(type.equals("KeyPairGenerator"))) {
                    continue;
                }
                String algorithm = service.getAlgorithm();
                // AndroidKeyStore is tested in CTS.
                if ("AndroidKeyStore".equals(provider.getName())) {
                    continue;
                }
                AlgorithmParameterSpec params = null;
                // TODO: detect if we're running in vogar and run the full test
                if ("DH".equals(algorithm)) {
                    params = KeyPairGeneratorTest.getDHParams();
                }
                try {
                    // KeyPairGenerator.getInstance(String)
                    KeyPairGenerator kpg1 = KeyPairGenerator.getInstance(algorithm);
                    TestCase.assertEquals(algorithm, kpg1.getAlgorithm());
                    if (params != null) {
                        kpg1.initialize(params);
                    }
                    test_KeyPairGenerator(kpg1);
                    // KeyPairGenerator.getInstance(String, Provider)
                    KeyPairGenerator kpg2 = KeyPairGenerator.getInstance(algorithm, provider);
                    TestCase.assertEquals(algorithm, kpg2.getAlgorithm());
                    TestCase.assertEquals(provider, kpg2.getProvider());
                    if (params != null) {
                        kpg2.initialize(params);
                    }
                    test_KeyPairGenerator(kpg2);
                    // KeyPairGenerator.getInstance(String, String)
                    KeyPairGenerator kpg3 = KeyPairGenerator.getInstance(algorithm, provider.getName());
                    TestCase.assertEquals(algorithm, kpg3.getAlgorithm());
                    TestCase.assertEquals(provider, kpg3.getProvider());
                    if (params != null) {
                        kpg3.initialize(params);
                    }
                    test_KeyPairGenerator(kpg3);
                } catch (Exception e) {
                    throw new Exception(("Problem testing KeyPairGenerator." + algorithm), e);
                }
            }
        }
    }

    private static final Map<String, List<Integer>> KEY_SIZES = new HashMap<String, List<Integer>>();

    static {
        KeyPairGeneratorTest.putKeySize("DSA", 512);
        KeyPairGeneratorTest.putKeySize("DSA", (512 + 64));
        KeyPairGeneratorTest.putKeySize("DSA", 1024);
        KeyPairGeneratorTest.putKeySize("RSA", 512);
        KeyPairGeneratorTest.putKeySize("DH", 512);
        KeyPairGeneratorTest.putKeySize("DH", (512 + 64));
        KeyPairGeneratorTest.putKeySize("DH", 1024);
        KeyPairGeneratorTest.putKeySize("DiffieHellman", 512);
        KeyPairGeneratorTest.putKeySize("DiffieHellman", (512 + 64));
        KeyPairGeneratorTest.putKeySize("DiffieHellman", 1024);
        KeyPairGeneratorTest.putKeySize("EC", 192);
        KeyPairGeneratorTest.putKeySize("EC", 224);
        KeyPairGeneratorTest.putKeySize("EC", 256);
        KeyPairGeneratorTest.putKeySize("EC", 384);
        KeyPairGeneratorTest.putKeySize("EC", 521);
    }

    /**
     * Elliptic Curve Crypto named curves that should be supported.
     */
    private static final String[] EC_NAMED_CURVES = new String[]{ // NIST P-192 aka SECG secp192r1 aka ANSI X9.62 prime192v1
    "secp192r1", "prime192v1", // NIST P-256 aka SECG secp256r1 aka ANSI X9.62 prime256v1
    "secp256r1", "prime256v1" };

    private static final BigInteger DSA_P = new BigInteger(new byte[]{ ((byte) (0)), ((byte) (158)), ((byte) (97)), ((byte) (194)), ((byte) (137)), ((byte) (239)), ((byte) (119)), ((byte) (169)), ((byte) (78)), ((byte) (19)), ((byte) (103)), ((byte) (100)), ((byte) (31)), ((byte) (9)), ((byte) (1)), ((byte) (254)), ((byte) (36)), ((byte) (19)), ((byte) (83)), ((byte) (224)), ((byte) (183)), ((byte) (144)), ((byte) (168)), ((byte) (78)), ((byte) (118)), ((byte) (254)), ((byte) (137)), ((byte) (130)), ((byte) (127)), ((byte) (122)), ((byte) (197)), ((byte) (60)), ((byte) (78)), ((byte) (12)), ((byte) (32)), ((byte) (85)), ((byte) (48)), ((byte) (149)), ((byte) (66)), ((byte) (133)), ((byte) (225)), ((byte) (64)), ((byte) (125)), ((byte) (39)), ((byte) (143)), ((byte) (7)), ((byte) (13)), ((byte) (232)), ((byte) (220)), ((byte) (153)), ((byte) (239)), ((byte) (179)), ((byte) (7)), ((byte) (148)), ((byte) (52)), ((byte) (214)), ((byte) (124)), ((byte) (255)), ((byte) (156)), ((byte) (190)), ((byte) (105)), ((byte) (211)), ((byte) (235)), ((byte) (68)), ((byte) (55)), ((byte) (80)), ((byte) (239)), ((byte) (73)), ((byte) (248)), ((byte) (226)), ((byte) (91)), ((byte) (216)), ((byte) (209)), ((byte) (16)), ((byte) (132)), ((byte) (151)), ((byte) (234)), ((byte) (227)), ((byte) (165)), ((byte) (28)), ((byte) (192)), ((byte) (78)), ((byte) (105)), ((byte) (202)), ((byte) (112)), ((byte) (61)), ((byte) (120)), ((byte) (185)), ((byte) (22)), ((byte) (229)), ((byte) (254)), ((byte) (97)), ((byte) (93)), ((byte) (138)), ((byte) (90)), ((byte) (179)), ((byte) (44)), ((byte) (97)), ((byte) (182)), ((byte) (1)), ((byte) (59)), ((byte) (208)), ((byte) (1)), ((byte) (124)), ((byte) (50)), ((byte) (141)), ((byte) (225)), ((byte) (243)), ((byte) (105)), ((byte) (14)), ((byte) (139)), ((byte) (88)), ((byte) (198)), ((byte) (207)), ((byte) (0)), ((byte) (148)), ((byte) (248)), ((byte) (73)), ((byte) (42)), ((byte) (75)), ((byte) (234)), ((byte) (218)), ((byte) (0)), ((byte) (255)), ((byte) (75)), ((byte) (208)), ((byte) (190)), ((byte) (64)), ((byte) (35)) });

    private static final BigInteger DSA_Q = new BigInteger(new byte[]{ ((byte) (0)), ((byte) (191)), ((byte) (238)), ((byte) (170)), ((byte) (15)), ((byte) (18)), ((byte) (52)), ((byte) (80)), ((byte) (114)), ((byte) (248)), ((byte) (96)), ((byte) (19)), ((byte) (216)), ((byte) (241)), ((byte) (65)), ((byte) (1)), ((byte) (16)), ((byte) (165)), ((byte) (47)), ((byte) (87)), ((byte) (95)) });

    private static final BigInteger DSA_G = new BigInteger(new byte[]{ ((byte) (119)), ((byte) (212)), ((byte) (122)), ((byte) (18)), ((byte) (204)), ((byte) (129)), ((byte) (126)), ((byte) (126)), ((byte) (235)), ((byte) (58)), ((byte) (251)), ((byte) (230)), ((byte) (134)), ((byte) (109)), ((byte) (90)), ((byte) (16)), ((byte) (29)), ((byte) (173)), ((byte) (169)), ((byte) (79)), ((byte) (185)), ((byte) (3)), ((byte) (93)), ((byte) (33)), ((byte) (26)), ((byte) (228)), ((byte) (48)), ((byte) (149)), ((byte) (117)), ((byte) (142)), ((byte) (205)), ((byte) (94)), ((byte) (209)), ((byte) (189)), ((byte) (10)), ((byte) (69)), ((byte) (238)), ((byte) (231)), ((byte) (247)), ((byte) (107)), ((byte) (101)), ((byte) (2)), ((byte) (96)), ((byte) (208)), ((byte) (46)), ((byte) (175)), ((byte) (61)), ((byte) (188)), ((byte) (7)), ((byte) (221)), ((byte) (43)), ((byte) (142)), ((byte) (51)), ((byte) (192)), ((byte) (147)), ((byte) (128)), ((byte) (217)), ((byte) (43)), ((byte) (167)), ((byte) (113)), ((byte) (87)), ((byte) (118)), ((byte) (188)), ((byte) (142)), ((byte) (185)), ((byte) (224)), ((byte) (215)), ((byte) (244)), ((byte) (35)), ((byte) (141)), ((byte) (65)), ((byte) (26)), ((byte) (151)), ((byte) (79)), ((byte) (44)), ((byte) (27)), ((byte) (213)), ((byte) (75)), ((byte) (102)), ((byte) (232)), ((byte) (250)), ((byte) (210)), ((byte) (80)), ((byte) (13)), ((byte) (23)), ((byte) (171)), ((byte) (52)), ((byte) (49)), ((byte) (61)), ((byte) (164)), ((byte) (136)), ((byte) (216)), ((byte) (142)), ((byte) (168)), ((byte) (167)), ((byte) (110)), ((byte) (23)), ((byte) (3)), ((byte) (183)), ((byte) (15)), ((byte) (104)), ((byte) (124)), ((byte) (100)), ((byte) (123)), ((byte) (146)), ((byte) (184)), ((byte) (99)), ((byte) (228)), ((byte) (154)), ((byte) (103)), ((byte) (24)), ((byte) (129)), ((byte) (39)), ((byte) (212)), ((byte) (11)), ((byte) (19)), ((byte) (72)), ((byte) (211)), ((byte) (125)), ((byte) (78)), ((byte) (246)), ((byte) (168)), ((byte) (143)), ((byte) (86)), ((byte) (23)), ((byte) (45)), ((byte) (8)), ((byte) (81)) });

    public void testDSAGeneratorWithParams() throws Exception {
        final DSAParameterSpec dsaSpec = new DSAParameterSpec(KeyPairGeneratorTest.DSA_P, KeyPairGeneratorTest.DSA_Q, KeyPairGeneratorTest.DSA_G);
        boolean failure = false;
        final Provider[] providers = Security.getProviders();
        for (final Provider p : providers) {
            Provider.Service s = p.getService("KeyPairGenerator", "DSA");
            if (s == null) {
                continue;
            }
            final KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA", p);
            kpg.initialize(dsaSpec);
            KeyPair pair = kpg.generateKeyPair();
            DSAPrivateKey privKey = ((DSAPrivateKey) (pair.getPrivate()));
            DSAPublicKey pubKey = ((DSAPublicKey) (pair.getPublic()));
            DSAParams actualParams = privKey.getParams();
            TestCase.assertNotNull("DSA params should not be null", actualParams);
            TestCase.assertEquals(("DSA P should be the same as supplied with provider " + (p.getName())), KeyPairGeneratorTest.DSA_P, actualParams.getP());
            TestCase.assertEquals(("DSA Q should be the same as supplied with provider " + (p.getName())), KeyPairGeneratorTest.DSA_Q, actualParams.getQ());
            TestCase.assertEquals(("DSA G should be the same as supplied with provider " + (p.getName())), KeyPairGeneratorTest.DSA_G, actualParams.getG());
            actualParams = pubKey.getParams();
            TestCase.assertNotNull("DSA params should not be null", actualParams);
            TestCase.assertEquals(("DSA P should be the same as supplied with provider " + (p.getName())), KeyPairGeneratorTest.DSA_P, actualParams.getP());
            TestCase.assertEquals(("DSA Q should be the same as supplied with provider " + (p.getName())), KeyPairGeneratorTest.DSA_Q, actualParams.getQ());
            TestCase.assertEquals(("DSA G should be the same as supplied with provider " + (p.getName())), KeyPairGeneratorTest.DSA_G, actualParams.getG());
        }
    }
}

