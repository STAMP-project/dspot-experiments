/**
 * Copyright (C) 2012 The Android Open Source Project
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


import OpenSSLProvider.PROVIDER_NAME;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Set;
import junit.framework.TestCase;


public class SignatureTest extends TestCase {
    // 20 bytes for DSA
    private final byte[] DATA = new byte[20];

    public void test_getInstance_OpenSSL_ENGINE() throws Exception {
        final String pem_private = "-----BEGIN RSA PRIVATE KEY-----\n" + ((((((((((((("MIICXAIBAAKBgQDpm4KamxulJnycEzNONGM7p0CvAaoZxJEd5Dvio5b6BROdCtRN\n" + "lEsB+9vtB5thkyDVC7N+IW0AjtyDE6h2QP+AWa+c4dh0RM2uNVXkUWPrA8C++GHv\n") + "EDlxZzRGiQEMuippYfIyBVkO+4+GRvnkG4dKjzxrQYPqKUK3C4PgFW2FewIDAQAB\n") + "AoGAGUTSBsk6X03fcr588TundD9uNr/2V1002Ufj1msdnKPJ8FXIiy+8QVWt/2Cw\n") + "RQi2J3VhkAYrlUDex2rr8Qas3E9uuwKgg/MZ4EsJbnKKgkd7uBZfmZ2ogcNJ82u7\n") + "teVijFpdsVLDa9aczEppt5sZzyTaBrovrRb+AIRDpMw3I0ECQQD3JkWeQUA9Is1V\n") + "z0X/ly/kaQKQLlrwYNdiKF0qOpyTLAguI7asAS72Zj7fThk5bHLM+mmgYwkicIIb\n") + "67J32GQbAkEA8fkXqEnwMFYSkRmT9M/qUkwWUsMW12/AoZFI5gwKNDHZYxytGGLw\n") + "mC//0qKnyeUG00vz06vLApe4/Sq4ODe6IQJBALEGastF9ZtUuDsEciD2y8kRJlLb\n") + "wSt4Ug3u13yN6uTHnzxdPFTLrDW1WsdcC1lEQp5rpwjIpxxR9f/FvVl2V40CQHOY\n") + "F6EhkUjGFaCTo4b0PHCMQK3Q3PyWOmP0z+p2HfnJRpx+eoKH4YASjhfF9HoSmywd\n") + "wKGCFD1s1ca7vb29gYECQH86GmYZsDoLNWurEVJbkmCr7X1+xwim6umdrNKR27P7\n") + "F1y0Sa3YY+LiiRb+IRSWE/onlP+28LIzWGF4lcTfDMc=\n") + "-----END RSA PRIVATE KEY-----");
        final byte[] der_public = new byte[]{ ((byte) (48)), ((byte) (129)), ((byte) (159)), ((byte) (48)), ((byte) (13)), ((byte) (6)), ((byte) (9)), ((byte) (42)), ((byte) (134)), ((byte) (72)), ((byte) (134)), ((byte) (247)), ((byte) (13)), ((byte) (1)), ((byte) (1)), ((byte) (1)), ((byte) (5)), ((byte) (0)), ((byte) (3)), ((byte) (129)), ((byte) (141)), ((byte) (0)), ((byte) (48)), ((byte) (129)), ((byte) (137)), ((byte) (2)), ((byte) (129)), ((byte) (129)), ((byte) (0)), ((byte) (233)), ((byte) (155)), ((byte) (130)), ((byte) (154)), ((byte) (155)), ((byte) (27)), ((byte) (165)), ((byte) (38)), ((byte) (124)), ((byte) (156)), ((byte) (19)), ((byte) (51)), ((byte) (78)), ((byte) (52)), ((byte) (99)), ((byte) (59)), ((byte) (167)), ((byte) (64)), ((byte) (175)), ((byte) (1)), ((byte) (170)), ((byte) (25)), ((byte) (196)), ((byte) (145)), ((byte) (29)), ((byte) (228)), ((byte) (59)), ((byte) (226)), ((byte) (163)), ((byte) (150)), ((byte) (250)), ((byte) (5)), ((byte) (19)), ((byte) (157)), ((byte) (10)), ((byte) (212)), ((byte) (77)), ((byte) (148)), ((byte) (75)), ((byte) (1)), ((byte) (251)), ((byte) (219)), ((byte) (237)), ((byte) (7)), ((byte) (155)), ((byte) (97)), ((byte) (147)), ((byte) (32)), ((byte) (213)), ((byte) (11)), ((byte) (179)), ((byte) (126)), ((byte) (33)), ((byte) (109)), ((byte) (0)), ((byte) (142)), ((byte) (220)), ((byte) (131)), ((byte) (19)), ((byte) (168)), ((byte) (118)), ((byte) (64)), ((byte) (255)), ((byte) (128)), ((byte) (89)), ((byte) (175)), ((byte) (156)), ((byte) (225)), ((byte) (216)), ((byte) (116)), ((byte) (68)), ((byte) (205)), ((byte) (174)), ((byte) (53)), ((byte) (85)), ((byte) (228)), ((byte) (81)), ((byte) (99)), ((byte) (235)), ((byte) (3)), ((byte) (192)), ((byte) (190)), ((byte) (248)), ((byte) (97)), ((byte) (239)), ((byte) (16)), ((byte) (57)), ((byte) (113)), ((byte) (103)), ((byte) (52)), ((byte) (70)), ((byte) (137)), ((byte) (1)), ((byte) (12)), ((byte) (186)), ((byte) (42)), ((byte) (105)), ((byte) (97)), ((byte) (242)), ((byte) (50)), ((byte) (5)), ((byte) (89)), ((byte) (14)), ((byte) (251)), ((byte) (143)), ((byte) (134)), ((byte) (70)), ((byte) (249)), ((byte) (228)), ((byte) (27)), ((byte) (135)), ((byte) (74)), ((byte) (143)), ((byte) (60)), ((byte) (107)), ((byte) (65)), ((byte) (131)), ((byte) (234)), ((byte) (41)), ((byte) (66)), ((byte) (183)), ((byte) (11)), ((byte) (131)), ((byte) (224)), ((byte) (21)), ((byte) (109)), ((byte) (133)), ((byte) (123)), ((byte) (2)), ((byte) (3)), ((byte) (1)), ((byte) (0)), ((byte) (1)) };
        // We only need to test this on the OpenSSL provider.
        Provider p = Security.getProvider(PROVIDER_NAME);
        /* ENGINE-based private key */
        NativeCryptoTest.loadTestEngine();
        OpenSSLEngine engine = OpenSSLEngine.getInstance(NativeCryptoTest.TEST_ENGINE_ID);
        PrivateKey privKey = engine.getPrivateKeyById(pem_private);
        TestCase.assertTrue((privKey instanceof RSAPrivateKey));
        /* Non-ENGINE-based public key */
        KeyFactory kf = KeyFactory.getInstance("RSA", p);
        PublicKey pubKey = kf.generatePublic(new X509EncodedKeySpec(der_public));
        KeyPair kp = new KeyPair(pubKey, privKey);
        Set<Provider.Service> services = p.getServices();
        for (Provider.Service service : services) {
            if (("Signature".equals(service.getType())) && (service.getAlgorithm().contains("RSA"))) {
                Signature sig1 = Signature.getInstance(service.getAlgorithm(), p);
                test_Signature(sig1, kp);
            }
        }
    }
}

