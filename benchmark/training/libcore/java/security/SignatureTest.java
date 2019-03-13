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
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;

import static StandardNames.IS_RI;


public class SignatureTest extends TestCase {
    // 20 bytes for DSA
    private final byte[] DATA = new byte[20];

    public void test_getInstance() throws Exception {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                String type = service.getType();
                if (!(type.equals("Signature"))) {
                    continue;
                }
                String algorithm = service.getAlgorithm();
                try {
                    KeyPair kp = keyPair(algorithm, provider.getName());
                    // Signature.getInstance(String)
                    Signature sig1 = Signature.getInstance(algorithm);
                    TestCase.assertEquals(algorithm, sig1.getAlgorithm());
                    test_Signature(sig1, kp);
                    // Signature.getInstance(String, Provider)
                    Signature sig2 = Signature.getInstance(algorithm, provider);
                    TestCase.assertEquals(algorithm, sig2.getAlgorithm());
                    TestCase.assertEquals(provider, sig2.getProvider());
                    test_Signature(sig2, kp);
                    // Signature.getInstance(String, String)
                    Signature sig3 = Signature.getInstance(algorithm, provider.getName());
                    TestCase.assertEquals(algorithm, sig3.getAlgorithm());
                    TestCase.assertEquals(provider, sig3.getProvider());
                    test_Signature(sig3, kp);
                } catch (Exception e) {
                    throw new Exception(("Problem testing Signature." + algorithm), e);
                }
            }
        }
    }

    private final Map<String, KeyPair> keypairAlgorithmToInstance = new HashMap<String, KeyPair>();

    private static final byte[] PK_BYTES = SignatureTest.hexToBytes(("30819f300d06092a864886f70d010101050003818d0030818902818100cd769d178f61475fce3001" + ((("2604218320c77a427121d3b41dd76756c8fc0c428cd15cb754adc85466f47547b1c85623d9c17fc6" + "4f202fca21099caf99460c824ad657caa8c2db34996838d32623c4f23c8b6a4e6698603901262619") + "4840e0896b1a6ec4f6652484aad04569bb6a885b822a10d700224359c632dc7324520cbb3d020301") + "0001")));

    private static final byte[] CONTENT = SignatureTest.hexToBytes(("f2fa9d73656e00fa01edc12e73656e2e7670632e6432004867268c46dd95030b93ce7260423e5c00" + (((((((((((("fabd4d656d6265727300fa018dc12e73656e2e7670632e643100d7c258dc00fabd44657669636573" + "00faa54b65797300fa02b5c12e4d2e4b009471968cc68835f8a68dde10f53d19693d480de767e5fb") + "976f3562324006372300fabdfd04e1f51ef3aa00fa8d00000001a203e202859471968cc68835f8a6") + "8dde10f53d19693d480de767e5fb976f356232400637230002bab504e1f51ef5810002c29d28463f") + "0003da8d000001e201eaf2fa9d73656e00fa01edc12e73656e2e7670632e6432004867268c46dd95") + "030b93ce7260423e5c00fabd4d656d6265727300fa018dc12e73656e2e7670632e643100d7c258dc") + "00fabd4465766963657300faa54b65797300fa02b5c12e4d2e4b009471968cc68835f8a68dde10f5") + "3d19693d480de767e5fb976f3562324006372300fabdfd04e1f51ef3aa000003e202859471968cc6") + "8835f8a68dde10f53d19693d480de767e5fb976f3562324006372300000000019a0a9530819f300d") + "06092a864886f70d010101050003818d0030818902818100cd769d178f61475fce30012604218320") + "c77a427121d3b41dd76756c8fc0c428cd15cb754adc85466f47547b1c85623d9c17fc64f202fca21") + "099caf99460c824ad657caa8c2db34996838d32623c4f23c8b6a4e66986039012626194840e0896b") + "1a6ec4f6652484aad04569bb6a885b822a10d700224359c632dc7324520cbb3d020301000100")));

    private static final byte[] SIGNATURE = SignatureTest.hexToBytes(("b4016456148cd2e9f580470aad63d19c1fee52b38c9dcb5b4d61a7ca369a7277497775d106d86394" + (("a69229184333b5a3e6261d5bcebdb02530ca9909f4d790199eae7c140f7db39dee2232191bdf0bfb" + "34fdadc44326b9b3f3fa828652bab07f0362ac141c8c3784ebdec44e0b156a5e7bccdc81a56fe954") + "56ac8c0e4ae12d97")));

    // http://code.google.com/p/android/issues/detail?id=18566
    // http://b/5038554
    public void test18566() throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(SignatureTest.PK_BYTES);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pk = keyFactory.generatePublic(keySpec);
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(pk);
        sig.update(SignatureTest.CONTENT);
        TestCase.assertTrue(sig.verify(SignatureTest.SIGNATURE));
    }

    /* Test vectors generated with this private key:

    -----BEGIN RSA PRIVATE KEY-----
    MIIEpAIBAAKCAQEA4Ec+irjyKE/rnnQv+XSPoRjtmGM8kvUq63ouvg075gMpvnZq
    0Q62pRXQ0s/ZvqeTDwwwZTeJn3lYzT6FsB+IGFJNMSWEqUslHjYltUFB7b/uGYgI
    4buX/Hy0m56qr2jpyY19DtxTu8D6ADQ1bWMF+7zDxwAUBThqu8hzyw8+90JfPTPf
    ezFa4DbSoLZq/UdQOxab8247UWJRW3Ff2oPeryxYrrmr+zCXw8yd2dvl7ylsF2E5
    Ao6KZx5jBW1F9AGI0sQTNJCEXeUsJTTpxrJHjAe9rpKII7YtBmx3cPn2Pz26JH9T
    CER0e+eqqF2FO4vSRKzsPePImrRkU6tNJMOsaQIDAQABAoIBADd4R3al8XaY9ayW
    DfuDobZ1ZOZIvQWXz4q4CHGG8macJ6nsvdSA8Bl6gNBzCebGqW+SUzHlf4tKxvTU
    XtpFojJpwJ/EKMB6Tm7fc4oV3sl/q9Lyu0ehTyDqcvz+TDbgGtp3vRN82NTaELsW
    LpSkZilx8XX5hfoYjwVsuX7igW9Dq503R2Ekhs2owWGWwwgYqZXshdOEZ3kSZ7O/
    IfJzcQppJYYldoQcW2cSwS1L0govMpmtt8E12l6VFavadufK8qO+gFUdBzt4vxFi
    xIrSt/R0OgI47k0lL31efmUzzK5kzLOTYAdaL9HgNOw65c6cQIzL8OJeQRQCFoez
    3UdUroECgYEA9UGIS8Nzeyki1BGe9F4t7izUy7dfRVBaFXqlAJ+Zxzot8HJKxGAk
    MGMy6omBd2NFRl3G3x4KbxQK/ztzluaomUrF2qloc0cv43dJ0U6z4HXmKdvrNYMz
    im82SdCiZUp6Qv2atr+krE1IHTkLsimwZL3DEcwb4bYxidp8QM3s8rECgYEA6hp0
    LduIHO23KIyH442GjdekCdFaQ/RF1Td6C1cx3b/KLa8oqOE81cCvzsM0fXSjniNa
    PNljPydN4rlPkt9DgzkR2enxz1jyfeLgj/RZZMcg0+whOdx8r8kSlTzeyy81Wi4s
    NaUPrXVMs7IxZkJLo7bjESoriYw4xcFe2yOGkzkCgYBRgo8exv2ZYCmQG68dfjN7
    pfCvJ+mE6tiVrOYr199O5FoiQInyzBUa880XP84EdLywTzhqLNzA4ANrokGfVFeS
    YtRxAL6TGYSj76Bb7PFBV03AebOpXEqD5sQ/MhTW3zLVEt4ZgIXlMeYWuD/X3Z0f
    TiYHwzM9B8VdEH0dOJNYcQKBgQDbT7UPUN6O21P/NMgJMYigUShn2izKBIl3WeWH
    wkQBDa+GZNWegIPRbBZHiTAfZ6nweAYNg0oq29NnV1toqKhCwrAqibPzH8zsiiL+
    OVeVxcbHQitOXXSh6ajzDndZufwtY5wfFWc+hOk6XvFQb0MVODw41Fy9GxQEj0ch
    3IIyYQKBgQDYEUWTr0FfthLb8ZI3ENVNB0hiBadqO0MZSWjA3/HxHvD2GkozfV/T
    dBu8lkDkR7i2tsR8OsEgQ1fTsMVbqShr2nP2KSlvX6kUbYl2NX08dR51FIaWpAt0
    aFyCzjCQLWOdck/yTV4ulAfuNO3tLjtN9lqpvP623yjQe6aQPxZXaA==
    -----END RSA PRIVATE KEY-----
     */
    private static final BigInteger RSA_2048_modulus = new BigInteger(new byte[]{ ((byte) (0)), ((byte) (224)), ((byte) (71)), ((byte) (62)), ((byte) (138)), ((byte) (184)), ((byte) (242)), ((byte) (40)), ((byte) (79)), ((byte) (235)), ((byte) (158)), ((byte) (116)), ((byte) (47)), ((byte) (249)), ((byte) (116)), ((byte) (143)), ((byte) (161)), ((byte) (24)), ((byte) (237)), ((byte) (152)), ((byte) (99)), ((byte) (60)), ((byte) (146)), ((byte) (245)), ((byte) (42)), ((byte) (235)), ((byte) (122)), ((byte) (46)), ((byte) (190)), ((byte) (13)), ((byte) (59)), ((byte) (230)), ((byte) (3)), ((byte) (41)), ((byte) (190)), ((byte) (118)), ((byte) (106)), ((byte) (209)), ((byte) (14)), ((byte) (182)), ((byte) (165)), ((byte) (21)), ((byte) (208)), ((byte) (210)), ((byte) (207)), ((byte) (217)), ((byte) (190)), ((byte) (167)), ((byte) (147)), ((byte) (15)), ((byte) (12)), ((byte) (48)), ((byte) (101)), ((byte) (55)), ((byte) (137)), ((byte) (159)), ((byte) (121)), ((byte) (88)), ((byte) (205)), ((byte) (62)), ((byte) (133)), ((byte) (176)), ((byte) (31)), ((byte) (136)), ((byte) (24)), ((byte) (82)), ((byte) (77)), ((byte) (49)), ((byte) (37)), ((byte) (132)), ((byte) (169)), ((byte) (75)), ((byte) (37)), ((byte) (30)), ((byte) (54)), ((byte) (37)), ((byte) (181)), ((byte) (65)), ((byte) (65)), ((byte) (237)), ((byte) (191)), ((byte) (238)), ((byte) (25)), ((byte) (136)), ((byte) (8)), ((byte) (225)), ((byte) (187)), ((byte) (151)), ((byte) (252)), ((byte) (124)), ((byte) (180)), ((byte) (155)), ((byte) (158)), ((byte) (170)), ((byte) (175)), ((byte) (104)), ((byte) (233)), ((byte) (201)), ((byte) (141)), ((byte) (125)), ((byte) (14)), ((byte) (220)), ((byte) (83)), ((byte) (187)), ((byte) (192)), ((byte) (250)), ((byte) (0)), ((byte) (52)), ((byte) (53)), ((byte) (109)), ((byte) (99)), ((byte) (5)), ((byte) (251)), ((byte) (188)), ((byte) (195)), ((byte) (199)), ((byte) (0)), ((byte) (20)), ((byte) (5)), ((byte) (56)), ((byte) (106)), ((byte) (187)), ((byte) (200)), ((byte) (115)), ((byte) (203)), ((byte) (15)), ((byte) (62)), ((byte) (247)), ((byte) (66)), ((byte) (95)), ((byte) (61)), ((byte) (51)), ((byte) (223)), ((byte) (123)), ((byte) (49)), ((byte) (90)), ((byte) (224)), ((byte) (54)), ((byte) (210)), ((byte) (160)), ((byte) (182)), ((byte) (106)), ((byte) (253)), ((byte) (71)), ((byte) (80)), ((byte) (59)), ((byte) (22)), ((byte) (155)), ((byte) (243)), ((byte) (110)), ((byte) (59)), ((byte) (81)), ((byte) (98)), ((byte) (81)), ((byte) (91)), ((byte) (113)), ((byte) (95)), ((byte) (218)), ((byte) (131)), ((byte) (222)), ((byte) (175)), ((byte) (44)), ((byte) (88)), ((byte) (174)), ((byte) (185)), ((byte) (171)), ((byte) (251)), ((byte) (48)), ((byte) (151)), ((byte) (195)), ((byte) (204)), ((byte) (157)), ((byte) (217)), ((byte) (219)), ((byte) (229)), ((byte) (239)), ((byte) (41)), ((byte) (108)), ((byte) (23)), ((byte) (97)), ((byte) (57)), ((byte) (2)), ((byte) (142)), ((byte) (138)), ((byte) (103)), ((byte) (30)), ((byte) (99)), ((byte) (5)), ((byte) (109)), ((byte) (69)), ((byte) (244)), ((byte) (1)), ((byte) (136)), ((byte) (210)), ((byte) (196)), ((byte) (19)), ((byte) (52)), ((byte) (144)), ((byte) (132)), ((byte) (93)), ((byte) (229)), ((byte) (44)), ((byte) (37)), ((byte) (52)), ((byte) (233)), ((byte) (198)), ((byte) (178)), ((byte) (71)), ((byte) (140)), ((byte) (7)), ((byte) (189)), ((byte) (174)), ((byte) (146)), ((byte) (136)), ((byte) (35)), ((byte) (182)), ((byte) (45)), ((byte) (6)), ((byte) (108)), ((byte) (119)), ((byte) (112)), ((byte) (249)), ((byte) (246)), ((byte) (63)), ((byte) (61)), ((byte) (186)), ((byte) (36)), ((byte) (127)), ((byte) (83)), ((byte) (8)), ((byte) (68)), ((byte) (116)), ((byte) (123)), ((byte) (231)), ((byte) (170)), ((byte) (168)), ((byte) (93)), ((byte) (133)), ((byte) (59)), ((byte) (139)), ((byte) (210)), ((byte) (68)), ((byte) (172)), ((byte) (236)), ((byte) (61)), ((byte) (227)), ((byte) (200)), ((byte) (154)), ((byte) (180)), ((byte) (100)), ((byte) (83)), ((byte) (171)), ((byte) (77)), ((byte) (36)), ((byte) (195)), ((byte) (172)), ((byte) (105)) });

    private static final BigInteger RSA_2048_privateExponent = new BigInteger(new byte[]{ ((byte) (55)), ((byte) (120)), ((byte) (71)), ((byte) (118)), ((byte) (165)), ((byte) (241)), ((byte) (118)), ((byte) (152)), ((byte) (245)), ((byte) (172)), ((byte) (150)), ((byte) (13)), ((byte) (251)), ((byte) (131)), ((byte) (161)), ((byte) (182)), ((byte) (117)), ((byte) (100)), ((byte) (230)), ((byte) (72)), ((byte) (189)), ((byte) (5)), ((byte) (151)), ((byte) (207)), ((byte) (138)), ((byte) (184)), ((byte) (8)), ((byte) (113)), ((byte) (134)), ((byte) (242)), ((byte) (102)), ((byte) (156)), ((byte) (39)), ((byte) (169)), ((byte) (236)), ((byte) (189)), ((byte) (212)), ((byte) (128)), ((byte) (240)), ((byte) (25)), ((byte) (122)), ((byte) (128)), ((byte) (208)), ((byte) (115)), ((byte) (9)), ((byte) (230)), ((byte) (198)), ((byte) (169)), ((byte) (111)), ((byte) (146)), ((byte) (83)), ((byte) (49)), ((byte) (229)), ((byte) (127)), ((byte) (139)), ((byte) (74)), ((byte) (198)), ((byte) (244)), ((byte) (212)), ((byte) (94)), ((byte) (218)), ((byte) (69)), ((byte) (162)), ((byte) (50)), ((byte) (105)), ((byte) (192)), ((byte) (159)), ((byte) (196)), ((byte) (40)), ((byte) (192)), ((byte) (122)), ((byte) (78)), ((byte) (110)), ((byte) (223)), ((byte) (115)), ((byte) (138)), ((byte) (21)), ((byte) (222)), ((byte) (201)), ((byte) (127)), ((byte) (171)), ((byte) (210)), ((byte) (242)), ((byte) (187)), ((byte) (71)), ((byte) (161)), ((byte) (79)), ((byte) (32)), ((byte) (234)), ((byte) (114)), ((byte) (252)), ((byte) (254)), ((byte) (76)), ((byte) (54)), ((byte) (224)), ((byte) (26)), ((byte) (218)), ((byte) (119)), ((byte) (189)), ((byte) (19)), ((byte) (124)), ((byte) (216)), ((byte) (212)), ((byte) (218)), ((byte) (16)), ((byte) (187)), ((byte) (22)), ((byte) (46)), ((byte) (148)), ((byte) (164)), ((byte) (102)), ((byte) (41)), ((byte) (113)), ((byte) (241)), ((byte) (117)), ((byte) (249)), ((byte) (133)), ((byte) (250)), ((byte) (24)), ((byte) (143)), ((byte) (5)), ((byte) (108)), ((byte) (185)), ((byte) (126)), ((byte) (226)), ((byte) (129)), ((byte) (111)), ((byte) (67)), ((byte) (171)), ((byte) (157)), ((byte) (55)), ((byte) (71)), ((byte) (97)), ((byte) (36)), ((byte) (134)), ((byte) (205)), ((byte) (168)), ((byte) (193)), ((byte) (97)), ((byte) (150)), ((byte) (195)), ((byte) (8)), ((byte) (24)), ((byte) (169)), ((byte) (149)), ((byte) (236)), ((byte) (133)), ((byte) (211)), ((byte) (132)), ((byte) (103)), ((byte) (121)), ((byte) (18)), ((byte) (103)), ((byte) (179)), ((byte) (191)), ((byte) (33)), ((byte) (242)), ((byte) (115)), ((byte) (113)), ((byte) (10)), ((byte) (105)), ((byte) (37)), ((byte) (134)), ((byte) (37)), ((byte) (118)), ((byte) (132)), ((byte) (28)), ((byte) (91)), ((byte) (103)), ((byte) (18)), ((byte) (193)), ((byte) (45)), ((byte) (75)), ((byte) (210)), ((byte) (10)), ((byte) (47)), ((byte) (50)), ((byte) (153)), ((byte) (173)), ((byte) (183)), ((byte) (193)), ((byte) (53)), ((byte) (218)), ((byte) (94)), ((byte) (149)), ((byte) (21)), ((byte) (171)), ((byte) (218)), ((byte) (118)), ((byte) (231)), ((byte) (202)), ((byte) (242)), ((byte) (163)), ((byte) (190)), ((byte) (128)), ((byte) (85)), ((byte) (29)), ((byte) (7)), ((byte) (59)), ((byte) (120)), ((byte) (191)), ((byte) (17)), ((byte) (98)), ((byte) (196)), ((byte) (138)), ((byte) (210)), ((byte) (183)), ((byte) (244)), ((byte) (116)), ((byte) (58)), ((byte) (2)), ((byte) (56)), ((byte) (238)), ((byte) (77)), ((byte) (37)), ((byte) (47)), ((byte) (125)), ((byte) (94)), ((byte) (126)), ((byte) (101)), ((byte) (51)), ((byte) (204)), ((byte) (174)), ((byte) (100)), ((byte) (204)), ((byte) (179)), ((byte) (147)), ((byte) (96)), ((byte) (7)), ((byte) (90)), ((byte) (47)), ((byte) (209)), ((byte) (224)), ((byte) (52)), ((byte) (236)), ((byte) (58)), ((byte) (229)), ((byte) (206)), ((byte) (156)), ((byte) (64)), ((byte) (140)), ((byte) (203)), ((byte) (240)), ((byte) (226)), ((byte) (94)), ((byte) (65)), ((byte) (20)), ((byte) (2)), ((byte) (22)), ((byte) (135)), ((byte) (179)), ((byte) (221)), ((byte) (71)), ((byte) (84)), ((byte) (174)), ((byte) (129)) });

    private static final BigInteger RSA_2048_publicExponent = new BigInteger(new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (1)) });

    private static final BigInteger RSA_2048_primeP = new BigInteger(new byte[]{ ((byte) (0)), ((byte) (245)), ((byte) (65)), ((byte) (136)), ((byte) (75)), ((byte) (195)), ((byte) (115)), ((byte) (123)), ((byte) (41)), ((byte) (34)), ((byte) (212)), ((byte) (17)), ((byte) (158)), ((byte) (244)), ((byte) (94)), ((byte) (45)), ((byte) (238)), ((byte) (44)), ((byte) (212)), ((byte) (203)), ((byte) (183)), ((byte) (95)), ((byte) (69)), ((byte) (80)), ((byte) (90)), ((byte) (21)), ((byte) (122)), ((byte) (165)), ((byte) (0)), ((byte) (159)), ((byte) (153)), ((byte) (199)), ((byte) (58)), ((byte) (45)), ((byte) (240)), ((byte) (114)), ((byte) (74)), ((byte) (196)), ((byte) (96)), ((byte) (36)), ((byte) (48)), ((byte) (99)), ((byte) (50)), ((byte) (234)), ((byte) (137)), ((byte) (129)), ((byte) (119)), ((byte) (99)), ((byte) (69)), ((byte) (70)), ((byte) (93)), ((byte) (198)), ((byte) (223)), ((byte) (30)), ((byte) (10)), ((byte) (111)), ((byte) (20)), ((byte) (10)), ((byte) (255)), ((byte) (59)), ((byte) (115)), ((byte) (150)), ((byte) (230)), ((byte) (168)), ((byte) (153)), ((byte) (74)), ((byte) (197)), ((byte) (218)), ((byte) (169)), ((byte) (104)), ((byte) (115)), ((byte) (71)), ((byte) (47)), ((byte) (227)), ((byte) (119)), ((byte) (73)), ((byte) (209)), ((byte) (78)), ((byte) (179)), ((byte) (224)), ((byte) (117)), ((byte) (230)), ((byte) (41)), ((byte) (219)), ((byte) (235)), ((byte) (53)), ((byte) (131)), ((byte) (51)), ((byte) (138)), ((byte) (111)), ((byte) (54)), ((byte) (73)), ((byte) (208)), ((byte) (162)), ((byte) (101)), ((byte) (74)), ((byte) (122)), ((byte) (66)), ((byte) (253)), ((byte) (154)), ((byte) (182)), ((byte) (191)), ((byte) (164)), ((byte) (172)), ((byte) (77)), ((byte) (72)), ((byte) (29)), ((byte) (57)), ((byte) (11)), ((byte) (178)), ((byte) (41)), ((byte) (176)), ((byte) (100)), ((byte) (189)), ((byte) (195)), ((byte) (17)), ((byte) (204)), ((byte) (27)), ((byte) (225)), ((byte) (182)), ((byte) (49)), ((byte) (137)), ((byte) (218)), ((byte) (124)), ((byte) (64)), ((byte) (205)), ((byte) (236)), ((byte) (242)), ((byte) (177)) });

    private static final BigInteger RSA_2048_primeQ = new BigInteger(new byte[]{ ((byte) (0)), ((byte) (234)), ((byte) (26)), ((byte) (116)), ((byte) (45)), ((byte) (219)), ((byte) (136)), ((byte) (28)), ((byte) (237)), ((byte) (183)), ((byte) (40)), ((byte) (140)), ((byte) (135)), ((byte) (227)), ((byte) (141)), ((byte) (134)), ((byte) (141)), ((byte) (215)), ((byte) (164)), ((byte) (9)), ((byte) (209)), ((byte) (90)), ((byte) (67)), ((byte) (244)), ((byte) (69)), ((byte) (213)), ((byte) (55)), ((byte) (122)), ((byte) (11)), ((byte) (87)), ((byte) (49)), ((byte) (221)), ((byte) (191)), ((byte) (202)), ((byte) (45)), ((byte) (175)), ((byte) (40)), ((byte) (168)), ((byte) (225)), ((byte) (60)), ((byte) (213)), ((byte) (192)), ((byte) (175)), ((byte) (206)), ((byte) (195)), ((byte) (52)), ((byte) (125)), ((byte) (116)), ((byte) (163)), ((byte) (158)), ((byte) (35)), ((byte) (90)), ((byte) (60)), ((byte) (217)), ((byte) (99)), ((byte) (63)), ((byte) (39)), ((byte) (77)), ((byte) (226)), ((byte) (185)), ((byte) (79)), ((byte) (146)), ((byte) (223)), ((byte) (67)), ((byte) (131)), ((byte) (57)), ((byte) (17)), ((byte) (217)), ((byte) (233)), ((byte) (241)), ((byte) (207)), ((byte) (88)), ((byte) (242)), ((byte) (125)), ((byte) (226)), ((byte) (224)), ((byte) (143)), ((byte) (244)), ((byte) (89)), ((byte) (100)), ((byte) (199)), ((byte) (32)), ((byte) (211)), ((byte) (236)), ((byte) (33)), ((byte) (57)), ((byte) (220)), ((byte) (124)), ((byte) (175)), ((byte) (201)), ((byte) (18)), ((byte) (149)), ((byte) (60)), ((byte) (222)), ((byte) (203)), ((byte) (47)), ((byte) (53)), ((byte) (90)), ((byte) (46)), ((byte) (44)), ((byte) (53)), ((byte) (165)), ((byte) (15)), ((byte) (173)), ((byte) (117)), ((byte) (76)), ((byte) (179)), ((byte) (178)), ((byte) (49)), ((byte) (102)), ((byte) (66)), ((byte) (75)), ((byte) (163)), ((byte) (182)), ((byte) (227)), ((byte) (17)), ((byte) (42)), ((byte) (43)), ((byte) (137)), ((byte) (140)), ((byte) (56)), ((byte) (197)), ((byte) (193)), ((byte) (94)), ((byte) (219)), ((byte) (35)), ((byte) (134)), ((byte) (147)), ((byte) (57)) });

    /* Test data is: "Android.\n" */
    private static final byte[] Vector1Data = new byte[]{ ((byte) (65)), ((byte) (110)), ((byte) (100)), ((byte) (114)), ((byte) (111)), ((byte) (105)), ((byte) (100)), ((byte) (46)), ((byte) (10)) };

    private static final byte[] SHA1withRSA_Vector1Signature = new byte[]{ ((byte) (109)), ((byte) (91)), ((byte) (255)), ((byte) (104)), ((byte) (218)), ((byte) (24)), ((byte) (152)), ((byte) (114)), ((byte) (92)), ((byte) (31)), ((byte) (70)), ((byte) (81)), ((byte) (119)), ((byte) (21)), ((byte) (17)), ((byte) (203)), ((byte) (224)), ((byte) (185)), ((byte) (59)), ((byte) (125)), ((byte) (245)), ((byte) (150)), ((byte) (152)), ((byte) (36)), ((byte) (133)), ((byte) (157)), ((byte) (62)), ((byte) (237)), ((byte) (155)), ((byte) (178)), ((byte) (138)), ((byte) (145)), ((byte) (251)), ((byte) (246)), ((byte) (133)), ((byte) (100)), ((byte) (116)), ((byte) (24)), ((byte) (181)), ((byte) (28)), ((byte) (179)), ((byte) (141)), ((byte) (153)), ((byte) (13)), ((byte) (223)), ((byte) (170)), ((byte) (166)), ((byte) (161)), ((byte) (195)), ((byte) (182)), ((byte) (37)), ((byte) (179)), ((byte) (6)), ((byte) (224)), ((byte) (239)), ((byte) (40)), ((byte) (176)), ((byte) (77)), ((byte) (80)), ((byte) (199)), ((byte) (117)), ((byte) (57)), ((byte) (185)), ((byte) (44)), ((byte) (71)), ((byte) (181)), ((byte) (226)), ((byte) (150)), ((byte) (248)), ((byte) (246)), ((byte) (203)), ((byte) (160)), ((byte) (88)), ((byte) (201)), ((byte) (62)), ((byte) (213)), ((byte) (252)), ((byte) (38)), ((byte) (217)), ((byte) (85)), ((byte) (115)), ((byte) (57)), ((byte) (117)), ((byte) (179)), ((byte) (176)), ((byte) (10)), ((byte) (95)), ((byte) (94)), ((byte) (59)), ((byte) (74)), ((byte) (46)), ((byte) (177)), ((byte) (14)), ((byte) (125)), ((byte) (229)), ((byte) (204)), ((byte) (4)), ((byte) (44)), ((byte) (209)), ((byte) (10)), ((byte) (50)), ((byte) (170)), ((byte) (217)), ((byte) (141)), ((byte) (31)), ((byte) (203)), ((byte) (227)), ((byte) (127)), ((byte) (99)), ((byte) (18)), ((byte) (177)), ((byte) (152)), ((byte) (70)), ((byte) (70)), ((byte) (7)), ((byte) (217)), ((byte) (73)), ((byte) (210)), ((byte) (191)), ((byte) (181)), ((byte) (188)), ((byte) (187)), ((byte) (253)), ((byte) (28)), ((byte) (215)), ((byte) (17)), ((byte) (148)), ((byte) (170)), ((byte) (95)), ((byte) (123)), ((byte) (178)), ((byte) (12)), ((byte) (93)), ((byte) (148)), ((byte) (83)), ((byte) (94)), ((byte) (129)), ((byte) (92)), ((byte) (187)), ((byte) (29)), ((byte) (79)), ((byte) (48)), ((byte) (205)), ((byte) (248)), ((byte) (215)), ((byte) (165)), ((byte) (250)), ((byte) (94)), ((byte) (224)), ((byte) (25)), ((byte) (63)), ((byte) (164)), ((byte) (170)), ((byte) (86)), ((byte) (78)), ((byte) (236)), ((byte) (235)), ((byte) (238)), ((byte) (162)), ((byte) (108)), ((byte) (201)), ((byte) (79)), ((byte) (194)), ((byte) (204)), ((byte) (42)), ((byte) (188)), ((byte) (91)), ((byte) (9)), ((byte) (16)), ((byte) (115)), ((byte) (97)), ((byte) (12)), ((byte) (4)), ((byte) (182)), ((byte) (183)), ((byte) (44)), ((byte) (55)), ((byte) (210)), ((byte) (202)), ((byte) (45)), ((byte) (84)), ((byte) (242)), ((byte) (247)), ((byte) (119)), ((byte) (225)), ((byte) (186)), ((byte) (159)), ((byte) (41)), ((byte) (7)), ((byte) (162)), ((byte) (116)), ((byte) (198)), ((byte) (233)), ((byte) (30)), ((byte) (222)), ((byte) (215)), ((byte) (156)), ((byte) (75)), ((byte) (183)), ((byte) (102)), ((byte) (82)), ((byte) (232)), ((byte) (172)), ((byte) (246)), ((byte) (118)), ((byte) (171)), ((byte) (22)), ((byte) (130)), ((byte) (150)), ((byte) (135)), ((byte) (64)), ((byte) (15)), ((byte) (173)), ((byte) (45)), ((byte) (70)), ((byte) (166)), ((byte) (40)), ((byte) (4)), ((byte) (19)), ((byte) (194)), ((byte) (206)), ((byte) (80)), ((byte) (86)), ((byte) (109)), ((byte) (190)), ((byte) (12)), ((byte) (145)), ((byte) (208)), ((byte) (142)), ((byte) (128)), ((byte) (158)), ((byte) (145)), ((byte) (143)), ((byte) (98)), ((byte) (179)), ((byte) (87)), ((byte) (214)), ((byte) (174)), ((byte) (83)), ((byte) (145)), ((byte) (131)), ((byte) (233)), ((byte) (56)), ((byte) (119)), ((byte) (143)), ((byte) (32)), ((byte) (221)), ((byte) (19)), ((byte) (125)), ((byte) (21)), ((byte) (68)), ((byte) (126)), ((byte) (181)), ((byte) (0)), ((byte) (214)), ((byte) (69)) };

    private static final byte[] Vector2Data = new byte[]{ ((byte) (84)), ((byte) (104)), ((byte) (105)), ((byte) (115)), ((byte) (32)), ((byte) (105)), ((byte) (115)), ((byte) (32)), ((byte) (97)), ((byte) (32)), ((byte) (115)), ((byte) (105)), ((byte) (103)), ((byte) (110)), ((byte) (101)), ((byte) (100)), ((byte) (32)), ((byte) (109)), ((byte) (101)), ((byte) (115)), ((byte) (115)), ((byte) (97)), ((byte) (103)), ((byte) (101)), ((byte) (32)), ((byte) (102)), ((byte) (114)), ((byte) (111)), ((byte) (109)), ((byte) (32)), ((byte) (75)), ((byte) (101)), ((byte) (110)), ((byte) (110)), ((byte) (121)), ((byte) (32)), ((byte) (82)), ((byte) (111)), ((byte) (111)), ((byte) (116)), ((byte) (46)), ((byte) (10)) };

    private static final byte[] SHA1withRSA_Vector2Signature = new byte[]{ ((byte) (46)), ((byte) (166)), ((byte) (51)), ((byte) (209)), ((byte) (157)), ((byte) (252)), ((byte) (78)), ((byte) (39)), ((byte) (179)), ((byte) (168)), ((byte) (154)), ((byte) (242)), ((byte) (72)), ((byte) (98)), ((byte) (21)), ((byte) (162)), ((byte) (206)), ((byte) (95)), ((byte) (43)), ((byte) (14)), ((byte) (197)), ((byte) (38)), ((byte) (186)), ((byte) (217)), ((byte) (15)), ((byte) (96)), ((byte) (235)), ((byte) (240)), ((byte) (213)), ((byte) (92)), ((byte) (107)), ((byte) (35)), ((byte) (17)), ((byte) (149)), ((byte) (164)), ((byte) (189)), ((byte) (17)), ((byte) (104)), ((byte) (231)), ((byte) (58)), ((byte) (55)), ((byte) (61)), ((byte) (121)), ((byte) (184)), ((byte) (79)), ((byte) (233)), ((byte) (161)), ((byte) (136)), ((byte) (251)), ((byte) (169)), ((byte) (139)), ((byte) (52)), ((byte) (161)), ((byte) (224)), ((byte) (202)), ((byte) (17)), ((byte) (221)), ((byte) (208)), ((byte) (131)), ((byte) (127)), ((byte) (193)), ((byte) (11)), ((byte) (22)), ((byte) (97)), ((byte) (172)), ((byte) (9)), ((byte) (162)), ((byte) (221)), ((byte) (64)), ((byte) (91)), ((byte) (140)), ((byte) (122)), ((byte) (178)), ((byte) (180)), ((byte) (2)), ((byte) (124)), ((byte) (212)), ((byte) (154)), ((byte) (230)), ((byte) (165)), ((byte) (26)), ((byte) (39)), ((byte) (119)), ((byte) (112)), ((byte) (227)), ((byte) (227)), ((byte) (113)), ((byte) (199)), ((byte) (89)), ((byte) (199)), ((byte) (159)), ((byte) (184)), ((byte) (239)), ((byte) (231)), ((byte) (21)), ((byte) (2)), ((byte) (13)), ((byte) (112)), ((byte) (220)), ((byte) (44)), ((byte) (233)), ((byte) (247)), ((byte) (99)), ((byte) (42)), ((byte) (181)), ((byte) (238)), ((byte) (159)), ((byte) (41)), ((byte) (86)), ((byte) (134)), ((byte) (153)), ((byte) (179)), ((byte) (15)), ((byte) (229)), ((byte) (31)), ((byte) (118)), ((byte) (34)), ((byte) (59)), ((byte) (127)), ((byte) (169)), ((byte) (158)), ((byte) (212)), ((byte) (196)), ((byte) (131)), ((byte) (93)), ((byte) (87)), ((byte) (204)), ((byte) (55)), ((byte) (203)), ((byte) (154)), ((byte) (158)), ((byte) (115)), ((byte) (68)), ((byte) (147)), ((byte) (180)), ((byte) (241)), ((byte) (107)), ((byte) (152)), ((byte) (160)), ((byte) (87)), ((byte) (187)), ((byte) (94)), ((byte) (143)), ((byte) (137)), ((byte) (91)), ((byte) (151)), ((byte) (38)), ((byte) (228)), ((byte) (208)), ((byte) (81)), ((byte) (10)), ((byte) (90)), ((byte) (183)), ((byte) (18)), ((byte) (26)), ((byte) (109)), ((byte) (176)), ((byte) (121)), ((byte) (48)), ((byte) (81)), ((byte) (131)), ((byte) (46)), ((byte) (226)), ((byte) (122)), ((byte) (103)), ((byte) (102)), ((byte) (211)), ((byte) (149)), ((byte) (202)), ((byte) (252)), ((byte) (203)), ((byte) (146)), ((byte) (121)), ((byte) (50)), ((byte) (38)), ((byte) (134)), ((byte) (225)), ((byte) (13)), ((byte) (216)), ((byte) (25)), ((byte) (250)), ((byte) (101)), ((byte) (55)), ((byte) (201)), ((byte) (76)), ((byte) (42)), ((byte) (225)), ((byte) (66)), ((byte) (199)), ((byte) (212)), ((byte) (183)), ((byte) (235)), ((byte) (31)), ((byte) (195)), ((byte) (83)), ((byte) (100)), ((byte) (111)), ((byte) (43)), ((byte) (120)), ((byte) (24)), ((byte) (3)), ((byte) (218)), ((byte) (141)), ((byte) (98)), ((byte) (36)), ((byte) (112)), ((byte) (171)), ((byte) (230)), ((byte) (22)), ((byte) (19)), ((byte) (36)), ((byte) (107)), ((byte) (95)), ((byte) (211)), ((byte) (236)), ((byte) (193)), ((byte) (88)), ((byte) (100)), ((byte) (189)), ((byte) (48)), ((byte) (152)), ((byte) (94)), ((byte) (51)), ((byte) (206)), ((byte) (135)), ((byte) (100)), ((byte) (20)), ((byte) (7)), ((byte) (133)), ((byte) (67)), ((byte) (62)), ((byte) (159)), ((byte) (39)), ((byte) (159)), ((byte) (99)), ((byte) (102)), ((byte) (157)), ((byte) (38)), ((byte) (25)), ((byte) (192)), ((byte) (2)), ((byte) (8)), ((byte) (21)), ((byte) (203)), ((byte) (180)), ((byte) (170)), ((byte) (74)), ((byte) (200)), ((byte) (192)), ((byte) (9)), ((byte) (21)), ((byte) (125)), ((byte) (138)), ((byte) (33)), ((byte) (188)), ((byte) (163)) };

    private static final byte[] SHA256withRSA_Vector2Signature = new byte[]{ ((byte) (24)), ((byte) (110)), ((byte) (49)), ((byte) (31)), ((byte) (29)), ((byte) (68)), ((byte) (9)), ((byte) (62)), ((byte) (160)), ((byte) (196)), ((byte) (61)), ((byte) (180)), ((byte) (27)), ((byte) (242)), ((byte) (216)), ((byte) (164)), ((byte) (89)), ((byte) (171)), ((byte) (181)), ((byte) (55)), ((byte) (40)), ((byte) (184)), ((byte) (148)), ((byte) (107)), ((byte) (111)), ((byte) (19)), ((byte) (84)), ((byte) (255)), ((byte) (172)), ((byte) (21)), ((byte) (132)), ((byte) (208)), ((byte) (201)), ((byte) (21)), ((byte) (91)), ((byte) (105)), ((byte) (5)), ((byte) (241)), ((byte) (68)), ((byte) (253)), ((byte) (222)), ((byte) (232)), ((byte) (180)), ((byte) (18)), ((byte) (89)), ((byte) (158)), ((byte) (76)), ((byte) (11)), ((byte) (213)), ((byte) (73)), ((byte) (51)), ((byte) (40)), ((byte) (224)), ((byte) (203)), ((byte) (135)), ((byte) (133)), ((byte) (216)), ((byte) (24)), ((byte) (111)), ((byte) (254)), ((byte) (162)), ((byte) (35)), ((byte) (130)), ((byte) (240)), ((byte) (229)), ((byte) (57)), ((byte) (27)), ((byte) (140)), ((byte) (147)), ((byte) (17)), ((byte) (73)), ((byte) (114)), ((byte) (42)), ((byte) (91)), ((byte) (37)), ((byte) (255)), ((byte) (78)), ((byte) (136)), ((byte) (112)), ((byte) (157)), ((byte) (157)), ((byte) (255)), ((byte) (226)), ((byte) (192)), ((byte) (126)), ((byte) (200)), ((byte) (3)), ((byte) (64)), ((byte) (190)), ((byte) (68)), ((byte) (9)), ((byte) (235)), ((byte) (158)), ((byte) (142)), ((byte) (136)), ((byte) (228)), ((byte) (152)), ((byte) (130)), ((byte) (6)), ((byte) (164)), ((byte) (157)), ((byte) (99)), ((byte) (136)), ((byte) (101)), ((byte) (163)), ((byte) (142)), ((byte) (13)), ((byte) (34)), ((byte) (243)), ((byte) (51)), ((byte) (242)), ((byte) (64)), ((byte) (232)), ((byte) (145)), ((byte) (103)), ((byte) (114)), ((byte) (41)), ((byte) (28)), ((byte) (8)), ((byte) (255)), ((byte) (84)), ((byte) (160)), ((byte) (204)), ((byte) (173)), ((byte) (132)), ((byte) (136)), ((byte) (75)), ((byte) (59)), ((byte) (239)), ((byte) (249)), ((byte) (94)), ((byte) (179)), ((byte) (65)), ((byte) (106)), ((byte) (189)), ((byte) (148)), ((byte) (22)), ((byte) (125)), ((byte) (157)), ((byte) (83)), ((byte) (119)), ((byte) (241)), ((byte) (106)), ((byte) (149)), ((byte) (87)), ((byte) (173)), ((byte) (101)), ((byte) (157)), ((byte) (117)), ((byte) (149)), ((byte) (246)), ((byte) (106)), ((byte) (210)), ((byte) (136)), ((byte) (234)), ((byte) (91)), ((byte) (162)), ((byte) (148)), ((byte) (143)), ((byte) (94)), ((byte) (132)), ((byte) (24)), ((byte) (25)), ((byte) (70)), ((byte) (131)), ((byte) (11)), ((byte) (109)), ((byte) (91)), ((byte) (185)), ((byte) (219)), ((byte) (164)), ((byte) (229)), ((byte) (23)), ((byte) (2)), ((byte) (158)), ((byte) (17)), ((byte) (237)), ((byte) (217)), ((byte) (123)), ((byte) (131)), ((byte) (135)), ((byte) (137)), ((byte) (243)), ((byte) (228)), ((byte) (191)), ((byte) (14)), ((byte) (232)), ((byte) (220)), ((byte) (85)), ((byte) (156)), ((byte) (247)), ((byte) (201)), ((byte) (195)), ((byte) (226)), ((byte) (44)), ((byte) (247)), ((byte) (140)), ((byte) (170)), ((byte) (23)), ((byte) (31)), ((byte) (209)), ((byte) (199)), ((byte) (116)), ((byte) (199)), ((byte) (142)), ((byte) (28)), ((byte) (91)), ((byte) (210)), ((byte) (49)), ((byte) (116)), ((byte) (67)), ((byte) (154)), ((byte) (82)), ((byte) (191)), ((byte) (137)), ((byte) (197)), ((byte) (180)), ((byte) (128)), ((byte) (106)), ((byte) (158)), ((byte) (5)), ((byte) (219)), ((byte) (187)), ((byte) (7)), ((byte) (140)), ((byte) (8)), ((byte) (97)), ((byte) (186)), ((byte) (164)), ((byte) (188)), ((byte) (128)), ((byte) (58)), ((byte) (221)), ((byte) (59)), ((byte) (26)), ((byte) (140)), ((byte) (33)), ((byte) (216)), ((byte) (163)), ((byte) (192)), ((byte) (199)), ((byte) (209)), ((byte) (8)), ((byte) (225)), ((byte) (52)), ((byte) (153)), ((byte) (192)), ((byte) (207)), ((byte) (128)), ((byte) (255)), ((byte) (250)), ((byte) (7)), ((byte) (239)), ((byte) (92)), ((byte) (69)), ((byte) (229)) };

    private static final byte[] SHA384withRSA_Vector2Signature = new byte[]{ ((byte) (175)), ((byte) (247)), ((byte) (122)), ((byte) (194)), ((byte) (187)), ((byte) (184)), ((byte) (189)), ((byte) (227)), ((byte) (66)), ((byte) (170)), ((byte) (22)), ((byte) (138)), ((byte) (82)), ((byte) (108)), ((byte) (153)), ((byte) (102)), ((byte) (8)), ((byte) (190)), ((byte) (21)), ((byte) (217)), ((byte) (124)), ((byte) (96)), ((byte) (44)), ((byte) (172)), ((byte) (77)), ((byte) (76)), ((byte) (244)), ((byte) (223)), ((byte) (188)), ((byte) (22)), ((byte) (88)), ((byte) (10)), ((byte) (78)), ((byte) (222)), ((byte) (141)), ((byte) (179)), ((byte) (189)), ((byte) (3)), ((byte) (78)), ((byte) (35)), ((byte) (64)), ((byte) (165)), ((byte) (128)), ((byte) (174)), ((byte) (131)), ((byte) (180)), ((byte) (15)), ((byte) (153)), ((byte) (68)), ((byte) (195)), ((byte) (94)), ((byte) (219)), ((byte) (89)), ((byte) (29)), ((byte) (234)), ((byte) (123)), ((byte) (77)), ((byte) (243)), ((byte) (210)), ((byte) (173)), ((byte) (189)), ((byte) (33)), ((byte) (159)), ((byte) (142)), ((byte) (135)), ((byte) (143)), ((byte) (18)), ((byte) (19)), ((byte) (51)), ((byte) (241)), ((byte) (192)), ((byte) (157)), ((byte) (231)), ((byte) (236)), ((byte) (110)), ((byte) (173)), ((byte) (234)), ((byte) (93)), ((byte) (105)), ((byte) (187)), ((byte) (171)), ((byte) (91)), ((byte) (216)), ((byte) (85)), ((byte) (86)), ((byte) (200)), ((byte) (218)), ((byte) (129)), ((byte) (65)), ((byte) (251)), ((byte) (211)), ((byte) (17)), ((byte) (108)), ((byte) (151)), ((byte) (167)), ((byte) (195)), ((byte) (241)), ((byte) (49)), ((byte) (191)), ((byte) (190)), ((byte) (63)), ((byte) (219)), ((byte) (53)), ((byte) (133)), ((byte) (183)), ((byte) (176)), ((byte) (117)), ((byte) (127)), ((byte) (175)), ((byte) (251)), ((byte) (101)), ((byte) (97)), ((byte) (199)), ((byte) (14)), ((byte) (99)), ((byte) (181)), ((byte) (125)), ((byte) (149)), ((byte) (233)), ((byte) (22)), ((byte) (157)), ((byte) (106)), ((byte) (0)), ((byte) (159)), ((byte) (94)), ((byte) (205)), ((byte) (255)), ((byte) (166)), ((byte) (188)), ((byte) (113)), ((byte) (242)), ((byte) (44)), ((byte) (211)), ((byte) (104)), ((byte) (185)), ((byte) (63)), ((byte) (170)), ((byte) (6)), ((byte) (241)), ((byte) (156)), ((byte) (126)), ((byte) (202)), ((byte) (74)), ((byte) (254)), ((byte) (177)), ((byte) (115)), ((byte) (25)), ((byte) (128)), ((byte) (5)), ((byte) (166)), ((byte) (133)), ((byte) (20)), ((byte) (218)), ((byte) (122)), ((byte) (22)), ((byte) (122)), ((byte) (194)), ((byte) (70)), ((byte) (87)), ((byte) (167)), ((byte) (192)), ((byte) (191)), ((byte) (205)), ((byte) (220)), ((byte) (47)), ((byte) (100)), ((byte) (246)), ((byte) (109)), ((byte) (220)), ((byte) (203)), ((byte) (90)), ((byte) (41)), ((byte) (149)), ((byte) (28)), ((byte) (254)), ((byte) (242)), ((byte) (218)), ((byte) (126)), ((byte) (203)), ((byte) (38)), ((byte) (18)), ((byte) (198)), ((byte) (176)), ((byte) (186)), ((byte) (132)), ((byte) (155)), ((byte) (79)), ((byte) (186)), ((byte) (27)), ((byte) (120)), ((byte) (37)), ((byte) (184)), ((byte) (143)), ((byte) (46)), ((byte) (81)), ((byte) (95)), ((byte) (158)), ((byte) (252)), ((byte) (64)), ((byte) (188)), ((byte) (133)), ((byte) (205)), ((byte) (134)), ((byte) (127)), ((byte) (136)), ((byte) (197)), ((byte) (170)), ((byte) (43)), ((byte) (120)), ((byte) (177)), ((byte) (156)), ((byte) (81)), ((byte) (154)), ((byte) (225)), ((byte) (225)), ((byte) (192)), ((byte) (64)), ((byte) (71)), ((byte) (203)), ((byte) (164)), ((byte) (183)), ((byte) (108)), ((byte) (49)), ((byte) (242)), ((byte) (200)), ((byte) (154)), ((byte) (173)), ((byte) (11)), ((byte) (211)), ((byte) (246)), ((byte) (133)), ((byte) (154)), ((byte) (143)), ((byte) (79)), ((byte) (201)), ((byte) (216)), ((byte) (51)), ((byte) (124)), ((byte) (69)), ((byte) (48)), ((byte) (234)), ((byte) (23)), ((byte) (211)), ((byte) (227)), ((byte) (144)), ((byte) (44)), ((byte) (218)), ((byte) (222)), ((byte) (65)), ((byte) (23)), ((byte) (63)), ((byte) (8)), ((byte) (185)), ((byte) (52)), ((byte) (192)), ((byte) (209)) };

    private static final byte[] SHA512withRSA_Vector2Signature = new byte[]{ ((byte) (25)), ((byte) (226)), ((byte) (229)), ((byte) (243)), ((byte) (24)), ((byte) (131)), ((byte) (236)), ((byte) (240)), ((byte) (171)), ((byte) (80)), ((byte) (5)), ((byte) (75)), ((byte) (95)), ((byte) (34)), ((byte) (252)), ((byte) (130)), ((byte) (109)), ((byte) (202)), ((byte) (231)), ((byte) (190)), ((byte) (35)), ((byte) (148)), ((byte) (250)), ((byte) (249)), ((byte) (164)), ((byte) (138)), ((byte) (149)), ((byte) (77)), ((byte) (20)), ((byte) (8)), ((byte) (139)), ((byte) (94)), ((byte) (3)), ((byte) (27)), ((byte) (116)), ((byte) (222)), ((byte) (193)), ((byte) (69)), ((byte) (156)), ((byte) (206)), ((byte) (29)), ((byte) (172)), ((byte) (171)), ((byte) (211)), ((byte) (168)), ((byte) (195)), ((byte) (202)), ((byte) (103)), ((byte) (128)), ((byte) (246)), ((byte) (3)), ((byte) (70)), ((byte) (101)), ((byte) (119)), ((byte) (89)), ((byte) (187)), ((byte) (184)), ((byte) (131)), ((byte) (238)), ((byte) (194)), ((byte) (62)), ((byte) (120)), ((byte) (221)), ((byte) (137)), ((byte) (205)), ((byte) (155)), ((byte) (120)), ((byte) (53)), ((byte) (169)), ((byte) (9)), ((byte) (200)), ((byte) (119)), ((byte) (221)), ((byte) (211)), ((byte) (160)), ((byte) (100)), ((byte) (176)), ((byte) (116)), ((byte) (72)), ((byte) (81)), ((byte) (79)), ((byte) (160)), ((byte) (174)), ((byte) (51)), ((byte) (179)), ((byte) (40)), ((byte) (176)), ((byte) (168)), ((byte) (120)), ((byte) (143)), ((byte) (162)), ((byte) (50)), ((byte) (166)), ((byte) (10)), ((byte) (170)), ((byte) (9)), ((byte) (181)), ((byte) (141)), ((byte) (76)), ((byte) (68)), ((byte) (70)), ((byte) (180)), ((byte) (210)), ((byte) (6)), ((byte) (107)), ((byte) (140)), ((byte) (81)), ((byte) (110)), ((byte) (156)), ((byte) (250)), ((byte) (31)), ((byte) (148)), ((byte) (62)), ((byte) (25)), ((byte) (156)), ((byte) (99)), ((byte) (254)), ((byte) (169)), ((byte) (154)), ((byte) (227)), ((byte) (108)), ((byte) (130)), ((byte) (100)), ((byte) (95)), ((byte) (202)), ((byte) (194)), ((byte) (141)), ((byte) (102)), ((byte) (190)), ((byte) (18)), ((byte) (110)), ((byte) (182)), ((byte) (53)), ((byte) (109)), ((byte) (170)), ((byte) (237)), ((byte) (75)), ((byte) (80)), ((byte) (8)), ((byte) (28)), ((byte) (191)), ((byte) (7)), ((byte) (112)), ((byte) (120)), ((byte) (192)), ((byte) (187)), ((byte) (197)), ((byte) (141)), ((byte) (108)), ((byte) (141)), ((byte) (53)), ((byte) (255)), ((byte) (4)), ((byte) (129)), ((byte) (216)), ((byte) (244)), ((byte) (210)), ((byte) (74)), ((byte) (195)), ((byte) (5)), ((byte) (35)), ((byte) (203)), ((byte) (235)), ((byte) (32)), ((byte) (177)), ((byte) (212)), ((byte) (45)), ((byte) (216)), ((byte) (122)), ((byte) (212)), ((byte) (126)), ((byte) (246)), ((byte) (169)), ((byte) (232)), ((byte) (114)), ((byte) (105)), ((byte) (254)), ((byte) (171)), ((byte) (84)), ((byte) (77)), ((byte) (209)), ((byte) (244)), ((byte) (107)), ((byte) (131)), ((byte) (49)), ((byte) (23)), ((byte) (237)), ((byte) (38)), ((byte) (233)), ((byte) (210)), ((byte) (91)), ((byte) (173)), ((byte) (66)), ((byte) (66)), ((byte) (165)), ((byte) (143)), ((byte) (152)), ((byte) (124)), ((byte) (27)), ((byte) (92)), ((byte) (142)), ((byte) (136)), ((byte) (86)), ((byte) (32)), ((byte) (142)), ((byte) (72)), ((byte) (249)), ((byte) (77)), ((byte) (130)), ((byte) (145)), ((byte) (203)), ((byte) (200)), ((byte) (28)), ((byte) (124)), ((byte) (165)), ((byte) (105)), ((byte) (27)), ((byte) (64)), ((byte) (194)), ((byte) (76)), ((byte) (37)), ((byte) (22)), ((byte) (79)), ((byte) (250)), ((byte) (9)), ((byte) (235)), ((byte) (245)), ((byte) (108)), ((byte) (85)), ((byte) (60)), ((byte) (110)), ((byte) (247)), ((byte) (192)), ((byte) (193)), ((byte) (52)), ((byte) (209)), ((byte) (83)), ((byte) (163)), ((byte) (105)), ((byte) (100)), ((byte) (238)), ((byte) (244)), ((byte) (249)), ((byte) (199)), ((byte) (150)), ((byte) (96)), ((byte) (132)), ((byte) (135)), ((byte) (180)), ((byte) (199)), ((byte) (60)), ((byte) (38)), ((byte) (167)), ((byte) (58)), ((byte) (191)), ((byte) (149)) };

    private static final byte[] MD5withRSA_Vector2Signature = new byte[]{ ((byte) (4)), ((byte) (23)), ((byte) (131)), ((byte) (16)), ((byte) (226)), ((byte) (110)), ((byte) (223)), ((byte) (169)), ((byte) (174)), ((byte) (210)), ((byte) (220)), ((byte) (95)), ((byte) (112)), ((byte) (29)), ((byte) (175)), ((byte) (84)), ((byte) (192)), ((byte) (95)), ((byte) (11)), ((byte) (44)), ((byte) (230)), ((byte) (208)), ((byte) (0)), ((byte) (24)), ((byte) (76)), ((byte) (246)), ((byte) (143)), ((byte) (24)), ((byte) (16)), ((byte) (116)), ((byte) (144)), ((byte) (153)), ((byte) (169)), ((byte) (144)), ((byte) (60)), ((byte) (90)), ((byte) (56)), ((byte) (211)), ((byte) (61)), ((byte) (72)), ((byte) (207)), ((byte) (49)), ((byte) (175)), ((byte) (18)), ((byte) (152)), ((byte) (251)), ((byte) (102)), ((byte) (232)), ((byte) (88)), ((byte) (236)), ((byte) (202)), ((byte) (225)), ((byte) (66)), ((byte) (249)), ((byte) (132)), ((byte) (23)), ((byte) (111)), ((byte) (76)), ((byte) (62)), ((byte) (196)), ((byte) (64)), ((byte) (198)), ((byte) (112)), ((byte) (176)), ((byte) (56)), ((byte) (243)), ((byte) (71)), ((byte) (235)), ((byte) (111)), ((byte) (203)), ((byte) (234)), ((byte) (33)), ((byte) (65)), ((byte) (243)), ((byte) (160)), ((byte) (62)), ((byte) (66)), ((byte) (173)), ((byte) (165)), ((byte) (173)), ((byte) (93)), ((byte) (44)), ((byte) (26)), ((byte) (142)), ((byte) (62)), ((byte) (179)), ((byte) (165)), ((byte) (120)), ((byte) (61)), ((byte) (86)), ((byte) (9)), ((byte) (147)), ((byte) (201)), ((byte) (147)), ((byte) (211)), ((byte) (210)), ((byte) (154)), ((byte) (197)), ((byte) (165)), ((byte) (46)), ((byte) (178)), ((byte) (216)), ((byte) (55)), ((byte) (199)), ((byte) (19)), ((byte) (26)), ((byte) (11)), ((byte) (218)), ((byte) (80)), ((byte) (40)), ((byte) (109)), ((byte) (71)), ((byte) (101)), ((byte) (82)), ((byte) (205)), ((byte) (231)), ((byte) (236)), ((byte) (87)), ((byte) (0)), ((byte) (65)), ((byte) (52)), ((byte) (40)), ((byte) (185)), ((byte) (139)), ((byte) (3)), ((byte) (65)), ((byte) (182)), ((byte) (213)), ((byte) (168)), ((byte) (239)), ((byte) (211)), ((byte) (221)), ((byte) (128)), ((byte) (213)), ((byte) (105)), ((byte) (228)), ((byte) (240)), ((byte) (77)), ((byte) (164)), ((byte) (125)), ((byte) (96)), ((byte) (47)), ((byte) (239)), ((byte) (121)), ((byte) (7)), ((byte) (117)), ((byte) (235)), ((byte) (247)), ((byte) (75)), ((byte) (67)), ((byte) (65)), ((byte) (219)), ((byte) (51)), ((byte) (173)), ((byte) (156)), ((byte) (123)), ((byte) (120)), ((byte) (131)), ((byte) (52)), ((byte) (119)), ((byte) (228)), ((byte) (128)), ((byte) (190)), ((byte) (230)), ((byte) (111)), ((byte) (221)), ((byte) (172)), ((byte) (165)), ((byte) (55)), ((byte) (207)), ((byte) (181)), ((byte) (68)), ((byte) (17)), ((byte) (119)), ((byte) (150)), ((byte) (69)), ((byte) (249)), ((byte) (174)), ((byte) (72)), ((byte) (166)), ((byte) (190)), ((byte) (48)), ((byte) (50)), ((byte) (235)), ((byte) (67)), ((byte) (111)), ((byte) (102)), ((byte) (57)), ((byte) (87)), ((byte) (248)), ((byte) (230)), ((byte) (96)), ((byte) (49)), ((byte) (208)), ((byte) (252)), ((byte) (207)), ((byte) (159)), ((byte) (229)), ((byte) (61)), ((byte) (207)), ((byte) (189)), ((byte) (123)), ((byte) (19)), ((byte) (32)), ((byte) (206)), ((byte) (17)), ((byte) (253)), ((byte) (229)), ((byte) (255)), ((byte) (144)), ((byte) (133)), ((byte) (223)), ((byte) (202)), ((byte) (61)), ((byte) (217)), ((byte) (68)), ((byte) (22)), ((byte) (194)), ((byte) (50)), ((byte) (40)), ((byte) (199)), ((byte) (1)), ((byte) (109)), ((byte) (234)), ((byte) (203)), ((byte) (13)), ((byte) (133)), ((byte) (8)), ((byte) (111)), ((byte) (203)), ((byte) (65)), ((byte) (106)), ((byte) (60)), ((byte) (15)), ((byte) (61)), ((byte) (56)), ((byte) (181)), ((byte) (97)), ((byte) (197)), ((byte) (100)), ((byte) (100)), ((byte) (129)), ((byte) (76)), ((byte) (205)), ((byte) (209)), ((byte) (106)), ((byte) (135)), ((byte) (40)), ((byte) (2)), ((byte) (175)), ((byte) (143)), ((byte) (89)), ((byte) (229)), ((byte) (103)), ((byte) (37)), ((byte) (0)) };

    /* openssl rsautl -raw -sign -inkey rsa.key | recode ../x1 | sed 's/0x/(byte) 0x/g' */
    private static final byte[] NONEwithRSA_Vector1Signature = new byte[]{ ((byte) (53)), ((byte) (67)), ((byte) (56)), ((byte) (68)), ((byte) (173)), ((byte) (63)), ((byte) (151)), ((byte) (2)), ((byte) (251)), ((byte) (89)), ((byte) (31)), ((byte) (74)), ((byte) (43)), ((byte) (185)), ((byte) (6)), ((byte) (236)), ((byte) (102)), ((byte) (230)), ((byte) (210)), ((byte) (197)), ((byte) (139)), ((byte) (123)), ((byte) (227)), ((byte) (24)), ((byte) (191)), ((byte) (7)), ((byte) (214)), ((byte) (1)), ((byte) (249)), ((byte) (217)), ((byte) (137)), ((byte) (196)), ((byte) (219)), ((byte) (0)), ((byte) (104)), ((byte) (255)), ((byte) (155)), ((byte) (67)), ((byte) (144)), ((byte) (242)), ((byte) (219)), ((byte) (131)), ((byte) (244)), ((byte) (126)), ((byte) (198)), ((byte) (129)), ((byte) (1)), ((byte) (58)), ((byte) (11)), ((byte) (229)), ((byte) (237)), ((byte) (8)), ((byte) (115)), ((byte) (62)), ((byte) (225)), ((byte) (63)), ((byte) (223)), ((byte) (31)), ((byte) (7)), ((byte) (109)), ((byte) (34)), ((byte) (141)), ((byte) (204)), ((byte) (78)), ((byte) (227)), ((byte) (154)), ((byte) (188)), ((byte) (204)), ((byte) (143)), ((byte) (158)), ((byte) (155)), ((byte) (2)), ((byte) (72)), ((byte) (0)), ((byte) (172)), ((byte) (159)), ((byte) (164)), ((byte) (143)), ((byte) (135)), ((byte) (161)), ((byte) (168)), ((byte) (230)), ((byte) (157)), ((byte) (205)), ((byte) (139)), ((byte) (5)), ((byte) (233)), ((byte) (210)), ((byte) (5)), ((byte) (141)), ((byte) (201)), ((byte) (149)), ((byte) (22)), ((byte) (208)), ((byte) (205)), ((byte) (67)), ((byte) (37)), ((byte) (138)), ((byte) (17)), ((byte) (70)), ((byte) (215)), ((byte) (116)), ((byte) (76)), ((byte) (207)), ((byte) (88)), ((byte) (249)), ((byte) (161)), ((byte) (48)), ((byte) (132)), ((byte) (82)), ((byte) (201)), ((byte) (1)), ((byte) (95)), ((byte) (36)), ((byte) (76)), ((byte) (177)), ((byte) (159)), ((byte) (125)), ((byte) (18)), ((byte) (56)), ((byte) (39)), ((byte) (15)), ((byte) (94)), ((byte) (255)), ((byte) (224)), ((byte) (85)), ((byte) (139)), ((byte) (163)), ((byte) (173)), ((byte) (96)), ((byte) (53)), ((byte) (131)), ((byte) (88)), ((byte) (175)), ((byte) (153)), ((byte) (222)), ((byte) (63)), ((byte) (93)), ((byte) (128)), ((byte) (128)), ((byte) (255)), ((byte) (155)), ((byte) (222)), ((byte) (92)), ((byte) (171)), ((byte) (151)), ((byte) (67)), ((byte) (100)), ((byte) (217)), ((byte) (159)), ((byte) (251)), ((byte) (103)), ((byte) (101)), ((byte) (165)), ((byte) (153)), ((byte) (231)), ((byte) (230)), ((byte) (235)), ((byte) (5)), ((byte) (149)), ((byte) (252)), ((byte) (70)), ((byte) (40)), ((byte) (75)), ((byte) (216)), ((byte) (140)), ((byte) (245)), ((byte) (10)), ((byte) (235)), ((byte) (31)), ((byte) (48)), ((byte) (234)), ((byte) (231)), ((byte) (103)), ((byte) (17)), ((byte) (37)), ((byte) (240)), ((byte) (68)), ((byte) (117)), ((byte) (116)), ((byte) (148)), ((byte) (6)), ((byte) (120)), ((byte) (208)), ((byte) (33)), ((byte) (244)), ((byte) (63)), ((byte) (200)), ((byte) (196)), ((byte) (74)), ((byte) (87)), ((byte) (190)), ((byte) (2)), ((byte) (60)), ((byte) (147)), ((byte) (246)), ((byte) (149)), ((byte) (251)), ((byte) (209)), ((byte) (119)), ((byte) (139)), ((byte) (67)), ((byte) (240)), ((byte) (185)), ((byte) (125)), ((byte) (224)), ((byte) (50)), ((byte) (225)), ((byte) (114)), ((byte) (181)), ((byte) (98)), ((byte) (63)), ((byte) (134)), ((byte) (195)), ((byte) (212)), ((byte) (95)), ((byte) (94)), ((byte) (84)), ((byte) (27)), ((byte) (91)), ((byte) (230)), ((byte) (116)), ((byte) (161)), ((byte) (11)), ((byte) (229)), ((byte) (24)), ((byte) (210)), ((byte) (79)), ((byte) (147)), ((byte) (243)), ((byte) (9)), ((byte) (88)), ((byte) (206)), ((byte) (240)), ((byte) (163)), ((byte) (97)), ((byte) (228)), ((byte) (110)), ((byte) (70)), ((byte) (69)), ((byte) (137)), ((byte) (80)), ((byte) (189)), ((byte) (3)), ((byte) (63)), ((byte) (56)), ((byte) (218)), ((byte) (93)), ((byte) (208)), ((byte) (27)), ((byte) (31)), ((byte) (177)), ((byte) (238)), ((byte) (137)), ((byte) (89)), ((byte) (197)) };

    public void testGetCommonInstances_Success() throws Exception {
        TestCase.assertNotNull(Signature.getInstance("SHA1withRSA"));
        TestCase.assertNotNull(Signature.getInstance("SHA256withRSA"));
        TestCase.assertNotNull(Signature.getInstance("SHA384withRSA"));
        TestCase.assertNotNull(Signature.getInstance("SHA512withRSA"));
        TestCase.assertNotNull(Signature.getInstance("NONEwithRSA"));
        TestCase.assertNotNull(Signature.getInstance("MD5withRSA"));
        TestCase.assertNotNull(Signature.getInstance("SHA1withDSA"));
    }

    public void testVerify_SHA1withRSA_Key_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(keySpec);
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector1Data);
        TestCase.assertTrue("Signature must match expected signature", sig.verify(SignatureTest.SHA1withRSA_Vector1Signature));
    }

    public void testVerify_SHA256withRSA_Key_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(keySpec);
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector2Data);
        TestCase.assertTrue("Signature must match expected signature", sig.verify(SignatureTest.SHA256withRSA_Vector2Signature));
    }

    public void testVerify_SHA384withRSA_Key_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(keySpec);
        Signature sig = Signature.getInstance("SHA384withRSA");
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector2Data);
        TestCase.assertTrue("Signature must match expected signature", sig.verify(SignatureTest.SHA384withRSA_Vector2Signature));
    }

    public void testVerify_SHA512withRSA_Key_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(keySpec);
        Signature sig = Signature.getInstance("SHA512withRSA");
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector2Data);
        TestCase.assertTrue("Signature must match expected signature", sig.verify(SignatureTest.SHA512withRSA_Vector2Signature));
    }

    public void testVerify_MD5withRSA_Key_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(keySpec);
        Signature sig = Signature.getInstance("MD5withRSA");
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector2Data);
        TestCase.assertTrue("Signature must match expected signature", sig.verify(SignatureTest.MD5withRSA_Vector2Signature));
    }

    public void testVerify_SHA1withRSA_Key_InitSignThenInitVerify_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        RSAPrivateKeySpec privKeySpec = new RSAPrivateKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_privateExponent);
        PrivateKey privKey = kf.generatePrivate(privKeySpec);
        Signature sig = Signature.getInstance("SHA1withRSA");
        // Start a signing operation
        sig.initSign(privKey);
        sig.update(SignatureTest.Vector2Data);
        // Switch to verify
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector1Data);
        TestCase.assertTrue("Signature must match expected signature", sig.verify(SignatureTest.SHA1withRSA_Vector1Signature));
    }

    public void testVerify_SHA1withRSA_Key_TwoMessages_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(keySpec);
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector1Data);
        TestCase.assertTrue("First signature must match expected signature", sig.verify(SignatureTest.SHA1withRSA_Vector1Signature));
        sig.update(SignatureTest.Vector2Data);
        TestCase.assertTrue("Second signature must match expected signature", sig.verify(SignatureTest.SHA1withRSA_Vector2Signature));
    }

    public void testVerify_SHA1withRSA_Key_WrongExpectedSignature_Failure() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(keySpec);
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector1Data);
        TestCase.assertFalse("Signature should fail to verify", sig.verify(SignatureTest.SHA1withRSA_Vector2Signature));
    }

    public void testSign_SHA1withRSA_CrtKeyWithPublicExponent_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent, SignatureTest.RSA_2048_privateExponent, null, null, null, null, null);
        // The RI fails on this key which is totally unreasonable.
        final PrivateKey privKey;
        try {
            privKey = kf.generatePrivate(keySpec);
        } catch (NullPointerException e) {
            if (IS_RI) {
                return;
            } else {
                TestCase.fail("Private key should be created");
                return;
            }
        }
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initSign(privKey);
        sig.update(SignatureTest.Vector1Data);
        byte[] signature = sig.sign();
        TestCase.assertNotNull("Signature must not be null", signature);
        TestCase.assertTrue("Signature should match expected", Arrays.equals(signature, SignatureTest.SHA1withRSA_Vector1Signature));
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector1Data);
        TestCase.assertTrue("Signature must verify correctly", sig.verify(signature));
    }

    public void testSign_SHA1withRSA_CrtKey_NoPrivateExponent_Failure() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent, null, SignatureTest.RSA_2048_primeP, SignatureTest.RSA_2048_primeQ, null, null, null);
        // Failing on this key early is okay.
        final PrivateKey privKey;
        try {
            privKey = kf.generatePrivate(keySpec);
        } catch (NullPointerException e) {
            return;
        } catch (InvalidKeySpecException e) {
            return;
        }
        Signature sig = Signature.getInstance("SHA1withRSA");
        try {
            sig.initSign(privKey);
            TestCase.fail("Should throw error when private exponent is not available");
        } catch (InvalidKeyException expected) {
        }
    }

    public void testSign_SHA1withRSA_CrtKey_NoModulus_Failure() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(null, SignatureTest.RSA_2048_publicExponent, SignatureTest.RSA_2048_privateExponent, SignatureTest.RSA_2048_primeP, SignatureTest.RSA_2048_primeQ, null, null, null);
        // Failing on this key early is okay.
        final PrivateKey privKey;
        try {
            privKey = kf.generatePrivate(keySpec);
        } catch (NullPointerException e) {
            return;
        } catch (InvalidKeySpecException e) {
            return;
        }
        Signature sig = Signature.getInstance("SHA1withRSA");
        try {
            sig.initSign(privKey);
            TestCase.fail("Should throw error when modulus is not available");
        } catch (InvalidKeyException expected) {
        }
    }

    public void testSign_SHA1withRSA_Key_EmptyKey_Failure() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(null, null);
        // Failing on this key early is okay.
        final PrivateKey privKey;
        try {
            privKey = kf.generatePrivate(keySpec);
        } catch (NullPointerException e) {
            return;
        } catch (InvalidKeySpecException e) {
            return;
        }
        Signature sig = Signature.getInstance("SHA1withRSA");
        try {
            sig.initSign(privKey);
            TestCase.fail("Should throw error when key is empty");
        } catch (InvalidKeyException expected) {
        }
    }

    public void testSign_SHA1withRSA_Key_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_privateExponent);
        PrivateKey privKey = kf.generatePrivate(keySpec);
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initSign(privKey);
        sig.update(SignatureTest.Vector1Data);
        byte[] signature = sig.sign();
        TestCase.assertNotNull("Signature must not be null", signature);
        TestCase.assertTrue("Signature should match expected", Arrays.equals(signature, SignatureTest.SHA1withRSA_Vector1Signature));
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector1Data);
        TestCase.assertTrue("Signature must verify correctly", sig.verify(signature));
    }

    public void testSign_SHA256withRSA_Key_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_privateExponent);
        final PrivateKey privKey = kf.generatePrivate(keySpec);
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privKey);
        sig.update(SignatureTest.Vector2Data);
        byte[] signature = sig.sign();
        TestCase.assertNotNull("Signature must not be null", signature);
        TestCase.assertTrue("Signature should match expected", Arrays.equals(signature, SignatureTest.SHA256withRSA_Vector2Signature));
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector2Data);
        TestCase.assertTrue("Signature must verify correctly", sig.verify(signature));
    }

    public void testSign_SHA384withRSA_Key_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_privateExponent);
        PrivateKey privKey = kf.generatePrivate(keySpec);
        Signature sig = Signature.getInstance("SHA384withRSA");
        sig.initSign(privKey);
        sig.update(SignatureTest.Vector2Data);
        byte[] signature = sig.sign();
        TestCase.assertNotNull("Signature must not be null", signature);
        TestCase.assertTrue("Signature should match expected", Arrays.equals(signature, SignatureTest.SHA384withRSA_Vector2Signature));
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector2Data);
        TestCase.assertTrue("Signature must verify correctly", sig.verify(signature));
    }

    public void testSign_SHA512withRSA_Key_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_privateExponent);
        PrivateKey privKey = kf.generatePrivate(keySpec);
        Signature sig = Signature.getInstance("SHA512withRSA");
        sig.initSign(privKey);
        sig.update(SignatureTest.Vector2Data);
        byte[] signature = sig.sign();
        TestCase.assertNotNull("Signature must not be null", signature);
        TestCase.assertTrue("Signature should match expected", Arrays.equals(signature, SignatureTest.SHA512withRSA_Vector2Signature));
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector2Data);
        TestCase.assertTrue("Signature must verify correctly", sig.verify(signature));
    }

    public void testSign_MD5withRSA_Key_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_privateExponent);
        PrivateKey privKey = kf.generatePrivate(keySpec);
        Signature sig = Signature.getInstance("MD5withRSA");
        sig.initSign(privKey);
        sig.update(SignatureTest.Vector2Data);
        byte[] signature = sig.sign();
        TestCase.assertNotNull("Signature must not be null", signature);
        TestCase.assertTrue("Signature should match expected", Arrays.equals(signature, SignatureTest.MD5withRSA_Vector2Signature));
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector2Data);
        TestCase.assertTrue("Signature must verify correctly", sig.verify(signature));
    }

    public void testSign_NONEwithRSA_Key_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_privateExponent);
        PrivateKey privKey = kf.generatePrivate(keySpec);
        Signature sig = Signature.getInstance("NONEwithRSA");
        sig.initSign(privKey);
        sig.update(SignatureTest.Vector1Data);
        byte[] signature = sig.sign();
        TestCase.assertNotNull("Signature must not be null", signature);
        TestCase.assertTrue("Signature should match expected", Arrays.equals(signature, SignatureTest.NONEwithRSA_Vector1Signature));
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector1Data);
        TestCase.assertTrue("Signature must verify correctly", sig.verify(signature));
    }

    public void testVerify_NONEwithRSA_Key_WrongSignature_Failure() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        Signature sig = Signature.getInstance("NONEwithRSA");
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector1Data);
        TestCase.assertFalse("Invalid signature must not verify", sig.verify("Invalid".getBytes()));
    }

    public void testSign_NONEwithRSA_Key_DataTooLarge_Failure() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_privateExponent);
        PrivateKey privKey = kf.generatePrivate(keySpec);
        Signature sig = Signature.getInstance("NONEwithRSA");
        sig.initSign(privKey);
        final int oneTooBig = (SignatureTest.RSA_2048_modulus.bitLength()) - 10;
        for (int i = 0; i < oneTooBig; i++) {
            sig.update(((byte) (i)));
        }
        try {
            sig.sign();
            TestCase.fail("Should throw exception when data is too large");
        } catch (SignatureException expected) {
        }
    }

    public void testSign_NONEwithRSA_Key_DataTooLarge_SingleByte_Failure() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_privateExponent);
        PrivateKey privKey = kf.generatePrivate(keySpec);
        Signature sig = Signature.getInstance("NONEwithRSA");
        sig.initSign(privKey);
        // This should make it two bytes too big.
        final int oneTooBig = (SignatureTest.RSA_2048_modulus.bitLength()) - 10;
        for (int i = 0; i < oneTooBig; i++) {
            sig.update(((byte) (i)));
        }
        try {
            sig.sign();
            TestCase.fail("Should throw exception when data is too large");
        } catch (SignatureException expected) {
        }
    }

    public void testVerify_NONEwithRSA_Key_DataTooLarge_Failure() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        Signature sig = Signature.getInstance("NONEwithRSA");
        sig.initVerify(pubKey);
        // This should make it one bytes too big.
        final int oneTooBig = (SignatureTest.RSA_2048_modulus.bitLength()) + 1;
        final byte[] vector = new byte[oneTooBig];
        for (int i = 0; i < oneTooBig; i++) {
            vector[i] = ((byte) (SignatureTest.Vector1Data[(i % (SignatureTest.Vector1Data.length))]));
        }
        sig.update(vector);
        TestCase.assertFalse("Should not verify when signature is too large", sig.verify(SignatureTest.NONEwithRSA_Vector1Signature));
    }

    public void testVerify_NONEwithRSA_Key_DataTooLarge_SingleByte_Failure() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        Signature sig = Signature.getInstance("NONEwithRSA");
        sig.initVerify(pubKey);
        // This should make it twice as big as it should be.
        final int tooBig = (SignatureTest.RSA_2048_modulus.bitLength()) * 2;
        for (int i = 0; i < tooBig; i++) {
            sig.update(((byte) (SignatureTest.Vector1Data[(i % (SignatureTest.Vector1Data.length))])));
        }
        TestCase.assertFalse("Should not verify when signature is too large", sig.verify(SignatureTest.NONEwithRSA_Vector1Signature));
    }

    public void testVerify_NONEwithRSA_Key_SignatureTooSmall_Failure() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        Signature sig = Signature.getInstance("NONEwithRSA");
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector1Data);
        TestCase.assertFalse("Invalid signature should not verify", sig.verify("Invalid sig".getBytes()));
    }

    public void testVerify_NONEwithRSA_Key_SignatureTooLarge_Failure() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(SignatureTest.RSA_2048_modulus, SignatureTest.RSA_2048_publicExponent);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        Signature sig = Signature.getInstance("NONEwithRSA");
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector1Data);
        byte[] invalidSignature = new byte[(SignatureTest.NONEwithRSA_Vector1Signature.length) * 2];
        System.arraycopy(SignatureTest.NONEwithRSA_Vector1Signature, 0, invalidSignature, 0, SignatureTest.NONEwithRSA_Vector1Signature.length);
        System.arraycopy(SignatureTest.NONEwithRSA_Vector1Signature, 0, invalidSignature, SignatureTest.NONEwithRSA_Vector1Signature.length, SignatureTest.NONEwithRSA_Vector1Signature.length);
        try {
            sig.verify(invalidSignature);
            TestCase.fail("Should throw exception when signature is too large");
        } catch (SignatureException expected) {
        }
    }

    /* These tests were generated with this DSA private key:

    -----BEGIN DSA PRIVATE KEY-----
    MIIBugIBAAKBgQCeYcKJ73epThNnZB8JAf4kE1Pgt5CoTnb+iYJ/esU8TgwgVTCV
    QoXhQH0njwcN6NyZ77MHlDTWfP+cvmnT60Q3UO9J+OJb2NEQhJfq46UcwE5pynA9
    eLkW5f5hXYpasyxhtgE70AF8Mo3h82kOi1jGzwCU+EkqS+raAP9L0L5AIwIVAL/u
    qg8SNFBy+GAT2PFBARClL1dfAoGAd9R6EsyBfn7rOvvmhm1aEB2tqU+5A10hGuQw
    lXWOzV7RvQpF7uf3a2UCYNAurz28B90rjjPAk4DZK6dxV3a8jrng1/QjjUEal08s
    G9VLZuj60lANF6s0MT2kiNiOqKduFwO3D2h8ZHuSuGPkmmcYgSfUCxNI031O9qiP
    VhctCFECgYAz7i1DhjRGUkCdYQd5tVaI42lhXOV71MTYPbuFOIxTL/hny7Z0PZWR
    A1blmYE6vrArDEhzpmRvDJZSIMzMfJjUIGu1KO73zpo9siK0xY0/sw5r3QC9txP2
    2Mv3BUIl5TLrs9outQJ0VMwldY2fElgCLWcSVkH44qZwWir1cq+cIwIUEGPDardb
    pNvWlWgTDD6a6ZTby+M=
    -----END DSA PRIVATE KEY-----
     */
    private static final BigInteger DSA_priv = new BigInteger(new byte[]{ ((byte) (16)), ((byte) (99)), ((byte) (195)), ((byte) (106)), ((byte) (183)), ((byte) (91)), ((byte) (164)), ((byte) (219)), ((byte) (214)), ((byte) (149)), ((byte) (104)), ((byte) (19)), ((byte) (12)), ((byte) (62)), ((byte) (154)), ((byte) (233)), ((byte) (148)), ((byte) (219)), ((byte) (203)), ((byte) (227)) });

    private static final BigInteger DSA_pub = new BigInteger(new byte[]{ ((byte) (51)), ((byte) (238)), ((byte) (45)), ((byte) (67)), ((byte) (134)), ((byte) (52)), ((byte) (70)), ((byte) (82)), ((byte) (64)), ((byte) (157)), ((byte) (97)), ((byte) (7)), ((byte) (121)), ((byte) (181)), ((byte) (86)), ((byte) (136)), ((byte) (227)), ((byte) (105)), ((byte) (97)), ((byte) (92)), ((byte) (229)), ((byte) (123)), ((byte) (212)), ((byte) (196)), ((byte) (216)), ((byte) (61)), ((byte) (187)), ((byte) (133)), ((byte) (56)), ((byte) (140)), ((byte) (83)), ((byte) (47)), ((byte) (248)), ((byte) (103)), ((byte) (203)), ((byte) (182)), ((byte) (116)), ((byte) (61)), ((byte) (149)), ((byte) (145)), ((byte) (3)), ((byte) (86)), ((byte) (229)), ((byte) (153)), ((byte) (129)), ((byte) (58)), ((byte) (190)), ((byte) (176)), ((byte) (43)), ((byte) (12)), ((byte) (72)), ((byte) (115)), ((byte) (166)), ((byte) (100)), ((byte) (111)), ((byte) (12)), ((byte) (150)), ((byte) (82)), ((byte) (32)), ((byte) (204)), ((byte) (204)), ((byte) (124)), ((byte) (152)), ((byte) (212)), ((byte) (32)), ((byte) (107)), ((byte) (181)), ((byte) (40)), ((byte) (238)), ((byte) (247)), ((byte) (206)), ((byte) (154)), ((byte) (61)), ((byte) (178)), ((byte) (34)), ((byte) (180)), ((byte) (197)), ((byte) (141)), ((byte) (63)), ((byte) (179)), ((byte) (14)), ((byte) (107)), ((byte) (221)), ((byte) (0)), ((byte) (189)), ((byte) (183)), ((byte) (19)), ((byte) (246)), ((byte) (216)), ((byte) (203)), ((byte) (247)), ((byte) (5)), ((byte) (66)), ((byte) (37)), ((byte) (229)), ((byte) (50)), ((byte) (235)), ((byte) (179)), ((byte) (218)), ((byte) (46)), ((byte) (181)), ((byte) (2)), ((byte) (116)), ((byte) (84)), ((byte) (204)), ((byte) (37)), ((byte) (117)), ((byte) (141)), ((byte) (159)), ((byte) (18)), ((byte) (88)), ((byte) (2)), ((byte) (45)), ((byte) (103)), ((byte) (18)), ((byte) (86)), ((byte) (65)), ((byte) (248)), ((byte) (226)), ((byte) (166)), ((byte) (112)), ((byte) (90)), ((byte) (42)), ((byte) (245)), ((byte) (114)), ((byte) (175)), ((byte) (156)), ((byte) (35)) });

    private static final BigInteger DSA_P = new BigInteger(new byte[]{ ((byte) (0)), ((byte) (158)), ((byte) (97)), ((byte) (194)), ((byte) (137)), ((byte) (239)), ((byte) (119)), ((byte) (169)), ((byte) (78)), ((byte) (19)), ((byte) (103)), ((byte) (100)), ((byte) (31)), ((byte) (9)), ((byte) (1)), ((byte) (254)), ((byte) (36)), ((byte) (19)), ((byte) (83)), ((byte) (224)), ((byte) (183)), ((byte) (144)), ((byte) (168)), ((byte) (78)), ((byte) (118)), ((byte) (254)), ((byte) (137)), ((byte) (130)), ((byte) (127)), ((byte) (122)), ((byte) (197)), ((byte) (60)), ((byte) (78)), ((byte) (12)), ((byte) (32)), ((byte) (85)), ((byte) (48)), ((byte) (149)), ((byte) (66)), ((byte) (133)), ((byte) (225)), ((byte) (64)), ((byte) (125)), ((byte) (39)), ((byte) (143)), ((byte) (7)), ((byte) (13)), ((byte) (232)), ((byte) (220)), ((byte) (153)), ((byte) (239)), ((byte) (179)), ((byte) (7)), ((byte) (148)), ((byte) (52)), ((byte) (214)), ((byte) (124)), ((byte) (255)), ((byte) (156)), ((byte) (190)), ((byte) (105)), ((byte) (211)), ((byte) (235)), ((byte) (68)), ((byte) (55)), ((byte) (80)), ((byte) (239)), ((byte) (73)), ((byte) (248)), ((byte) (226)), ((byte) (91)), ((byte) (216)), ((byte) (209)), ((byte) (16)), ((byte) (132)), ((byte) (151)), ((byte) (234)), ((byte) (227)), ((byte) (165)), ((byte) (28)), ((byte) (192)), ((byte) (78)), ((byte) (105)), ((byte) (202)), ((byte) (112)), ((byte) (61)), ((byte) (120)), ((byte) (185)), ((byte) (22)), ((byte) (229)), ((byte) (254)), ((byte) (97)), ((byte) (93)), ((byte) (138)), ((byte) (90)), ((byte) (179)), ((byte) (44)), ((byte) (97)), ((byte) (182)), ((byte) (1)), ((byte) (59)), ((byte) (208)), ((byte) (1)), ((byte) (124)), ((byte) (50)), ((byte) (141)), ((byte) (225)), ((byte) (243)), ((byte) (105)), ((byte) (14)), ((byte) (139)), ((byte) (88)), ((byte) (198)), ((byte) (207)), ((byte) (0)), ((byte) (148)), ((byte) (248)), ((byte) (73)), ((byte) (42)), ((byte) (75)), ((byte) (234)), ((byte) (218)), ((byte) (0)), ((byte) (255)), ((byte) (75)), ((byte) (208)), ((byte) (190)), ((byte) (64)), ((byte) (35)) });

    private static final BigInteger DSA_Q = new BigInteger(new byte[]{ ((byte) (0)), ((byte) (191)), ((byte) (238)), ((byte) (170)), ((byte) (15)), ((byte) (18)), ((byte) (52)), ((byte) (80)), ((byte) (114)), ((byte) (248)), ((byte) (96)), ((byte) (19)), ((byte) (216)), ((byte) (241)), ((byte) (65)), ((byte) (1)), ((byte) (16)), ((byte) (165)), ((byte) (47)), ((byte) (87)), ((byte) (95)) });

    private static final BigInteger DSA_G = new BigInteger(new byte[]{ ((byte) (119)), ((byte) (212)), ((byte) (122)), ((byte) (18)), ((byte) (204)), ((byte) (129)), ((byte) (126)), ((byte) (126)), ((byte) (235)), ((byte) (58)), ((byte) (251)), ((byte) (230)), ((byte) (134)), ((byte) (109)), ((byte) (90)), ((byte) (16)), ((byte) (29)), ((byte) (173)), ((byte) (169)), ((byte) (79)), ((byte) (185)), ((byte) (3)), ((byte) (93)), ((byte) (33)), ((byte) (26)), ((byte) (228)), ((byte) (48)), ((byte) (149)), ((byte) (117)), ((byte) (142)), ((byte) (205)), ((byte) (94)), ((byte) (209)), ((byte) (189)), ((byte) (10)), ((byte) (69)), ((byte) (238)), ((byte) (231)), ((byte) (247)), ((byte) (107)), ((byte) (101)), ((byte) (2)), ((byte) (96)), ((byte) (208)), ((byte) (46)), ((byte) (175)), ((byte) (61)), ((byte) (188)), ((byte) (7)), ((byte) (221)), ((byte) (43)), ((byte) (142)), ((byte) (51)), ((byte) (192)), ((byte) (147)), ((byte) (128)), ((byte) (217)), ((byte) (43)), ((byte) (167)), ((byte) (113)), ((byte) (87)), ((byte) (118)), ((byte) (188)), ((byte) (142)), ((byte) (185)), ((byte) (224)), ((byte) (215)), ((byte) (244)), ((byte) (35)), ((byte) (141)), ((byte) (65)), ((byte) (26)), ((byte) (151)), ((byte) (79)), ((byte) (44)), ((byte) (27)), ((byte) (213)), ((byte) (75)), ((byte) (102)), ((byte) (232)), ((byte) (250)), ((byte) (210)), ((byte) (80)), ((byte) (13)), ((byte) (23)), ((byte) (171)), ((byte) (52)), ((byte) (49)), ((byte) (61)), ((byte) (164)), ((byte) (136)), ((byte) (216)), ((byte) (142)), ((byte) (168)), ((byte) (167)), ((byte) (110)), ((byte) (23)), ((byte) (3)), ((byte) (183)), ((byte) (15)), ((byte) (104)), ((byte) (124)), ((byte) (100)), ((byte) (123)), ((byte) (146)), ((byte) (184)), ((byte) (99)), ((byte) (228)), ((byte) (154)), ((byte) (103)), ((byte) (24)), ((byte) (129)), ((byte) (39)), ((byte) (212)), ((byte) (11)), ((byte) (19)), ((byte) (72)), ((byte) (211)), ((byte) (125)), ((byte) (78)), ((byte) (246)), ((byte) (168)), ((byte) (143)), ((byte) (86)), ((byte) (23)), ((byte) (45)), ((byte) (8)), ((byte) (81)) });

    /**
     * A possible signature using SHA1withDSA of Vector2Data. Note that DSS is
     * randomized, so this won't be the exact signature you'll get out of
     * another signing operation unless you use a fixed RNG.
     */
    public static final byte[] SHA1withDSA_Vector2Signature = new byte[]{ ((byte) (48)), ((byte) (45)), ((byte) (2)), ((byte) (21)), ((byte) (0)), ((byte) (136)), ((byte) (239)), ((byte) (172)), ((byte) (43)), ((byte) (139)), ((byte) (226)), ((byte) (97)), ((byte) (198)), ((byte) (43)), ((byte) (234)), ((byte) (213)), ((byte) (150)), ((byte) (188)), ((byte) (176)), ((byte) (161)), ((byte) (48)), ((byte) (12)), ((byte) (31)), ((byte) (237)), ((byte) (17)), ((byte) (2)), ((byte) (20)), ((byte) (21)), ((byte) (196)), ((byte) (252)), ((byte) (130)), ((byte) (111)), ((byte) (23)), ((byte) (220)), ((byte) (135)), ((byte) (130)), ((byte) (117)), ((byte) (35)), ((byte) (212)), ((byte) (88)), ((byte) (220)), ((byte) (115)), ((byte) (61)), ((byte) (243)), ((byte) (81)), ((byte) (192)), ((byte) (87)) };

    public void testSign_SHA1withDSA_Key_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("DSA");
        DSAPrivateKeySpec keySpec = new DSAPrivateKeySpec(SignatureTest.DSA_priv, SignatureTest.DSA_P, SignatureTest.DSA_Q, SignatureTest.DSA_G);
        PrivateKey privKey = kf.generatePrivate(keySpec);
        Signature sig = Signature.getInstance("SHA1withDSA");
        sig.initSign(privKey);
        sig.update(SignatureTest.Vector2Data);
        byte[] signature = sig.sign();
        TestCase.assertNotNull("Signature must not be null", signature);
        DSAPublicKeySpec pubKeySpec = new DSAPublicKeySpec(SignatureTest.DSA_pub, SignatureTest.DSA_P, SignatureTest.DSA_Q, SignatureTest.DSA_G);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector2Data);
        TestCase.assertTrue("Signature must verify correctly", sig.verify(signature));
    }

    public void testVerify_SHA1withDSA_Key_Success() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("DSA");
        DSAPublicKeySpec pubKeySpec = new DSAPublicKeySpec(SignatureTest.DSA_pub, SignatureTest.DSA_P, SignatureTest.DSA_Q, SignatureTest.DSA_G);
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        Signature sig = Signature.getInstance("SHA1withDSA");
        sig.initVerify(pubKey);
        sig.update(SignatureTest.Vector2Data);
        TestCase.assertTrue("Signature must verify correctly", sig.verify(SignatureTest.SHA1withDSA_Vector2Signature));
    }

    // NetscapeCertRequest looks up Signature algorithms by OID from
    // BC but BC version 1.47 had registration bugs and MD5withRSA was
    // overlooked.  http://b/7453821
    public void testGetInstanceFromOID() throws Exception {
        assertBouncyCastleSignatureFromOID("1.2.840.113549.1.1.4");// MD5withRSA

        assertBouncyCastleSignatureFromOID("1.2.840.113549.1.1.5");// SHA1withRSA

        assertBouncyCastleSignatureFromOID("1.3.14.3.2.29");// SHA1withRSA

        assertBouncyCastleSignatureFromOID("1.2.840.113549.1.1.11");// SHA256withRSA

        assertBouncyCastleSignatureFromOID("1.2.840.113549.1.1.12");// SHA384withRSA

        assertBouncyCastleSignatureFromOID("1.2.840.113549.1.1.13");// SHA512withRSA

        assertBouncyCastleSignatureFromOID("1.2.840.10040.4.3");// SHA1withDSA

    }
}

