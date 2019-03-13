/**
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2007 Kenny Root, Jeffrey Sharkey
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
package org.connectbot.util;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Kenny Root
 */
@RunWith(AndroidJUnit4.class)
public class PubkeyUtilsTest {
    @Test
    public void encodeHex_Null_Failure() throws Exception {
        try {
            PubkeyUtils.encodeHex(null);
            Assert.fail("Should throw null pointer exception when argument is null");
        } catch (NullPointerException e) {
            // success
        }
    }

    @Test
    public void encodeHex_Success() throws Exception {
        byte[] input = new byte[]{ ((byte) (255)), 0, ((byte) (165)), 90, 18, 35 };
        String expected = "ff00a55a1223";
        Assert.assertEquals("Encoded hex should match expected", PubkeyUtils.encodeHex(input), expected);
    }

    /* openssl ecparam -genkey -name prime256v1 -noout | openssl pkcs8 -topk8 -outform d -nocrypt | recode ../x1 | sed 's/0x/(byte) 0x/g' */
    private static final byte[] EC_KEY_PKCS8 = new byte[]{ ((byte) (48)), ((byte) (129)), ((byte) (135)), ((byte) (2)), ((byte) (1)), ((byte) (0)), ((byte) (48)), ((byte) (19)), ((byte) (6)), ((byte) (7)), ((byte) (42)), ((byte) (134)), ((byte) (72)), ((byte) (206)), ((byte) (61)), ((byte) (2)), ((byte) (1)), ((byte) (6)), ((byte) (8)), ((byte) (42)), ((byte) (134)), ((byte) (72)), ((byte) (206)), ((byte) (61)), ((byte) (3)), ((byte) (1)), ((byte) (7)), ((byte) (4)), ((byte) (109)), ((byte) (48)), ((byte) (107)), ((byte) (2)), ((byte) (1)), ((byte) (1)), ((byte) (4)), ((byte) (32)), ((byte) (199)), ((byte) (107)), ((byte) (165)), ((byte) (182)), ((byte) (183)), ((byte) (78)), ((byte) (11)), ((byte) (112)), ((byte) (46)), ((byte) (160)), ((byte) (93)), ((byte) (141)), ((byte) (10)), ((byte) (245)), ((byte) (67)), ((byte) (239)), ((byte) (84)), ((byte) (47)), ((byte) (5)), ((byte) (91)), ((byte) (102)), ((byte) (80)), ((byte) (197)), ((byte) (180)), ((byte) (168)), ((byte) (96)), ((byte) (22)), ((byte) (142)), ((byte) (141)), ((byte) (205)), ((byte) (17)), ((byte) (250)), ((byte) (161)), ((byte) (68)), ((byte) (3)), ((byte) (66)), ((byte) (0)), ((byte) (4)), ((byte) (18)), ((byte) (226)), ((byte) (112)), ((byte) (48)), ((byte) (135)), ((byte) (47)), ((byte) (222)), ((byte) (16)), ((byte) (217)), ((byte) (201)), ((byte) (131)), ((byte) (199)), ((byte) (141)), ((byte) (201)), ((byte) (155)), ((byte) (148)), ((byte) (36)), ((byte) (80)), ((byte) (93)), ((byte) (236)), ((byte) (241)), ((byte) (79)), ((byte) (82)), ((byte) (198)), ((byte) (231)), ((byte) (163)), ((byte) (215)), ((byte) (244)), ((byte) (124)), ((byte) (9)), ((byte) (161)), ((byte) (16)), ((byte) (17)), ((byte) (228)), ((byte) (158)), ((byte) (144)), ((byte) (175)), ((byte) (249)), ((byte) (74)), ((byte) (116)), ((byte) (9)), ((byte) (147)), ((byte) (199)), ((byte) (154)), ((byte) (179)), ((byte) (226)), ((byte) (216)), ((byte) (97)), ((byte) (95)), ((byte) (134)), ((byte) (20)), ((byte) (145)), ((byte) (122)), ((byte) (35)), ((byte) (129)), ((byte) (66)), ((byte) (169)), ((byte) (2)), ((byte) (29)), ((byte) (51)), ((byte) (25)), ((byte) (192)), ((byte) (75)), ((byte) (206)) };

    private static final BigInteger EC_KEY_priv = new BigInteger("c76ba5b6b74e0b702ea05d8d0af543ef542f055b6650c5b4a860168e8dcd11fa", 16);

    private static final BigInteger EC_KEY_pub_x = new BigInteger("12e27030872fde10d9c983c78dc99b9424505decf14f52c6e7a3d7f47c09a110", 16);

    private static final BigInteger EC_KEY_pub_y = new BigInteger("11e49e90aff94a740993c79ab3e2d8615f8614917a238142a9021d3319c04bce", 16);

    /* openssl genrsa 512 | openssl pkcs8 -topk8 -outform d -nocrypt | recode ../x1 | sed 's/0x/(byte) 0x/g' */
    private static final byte[] RSA_KEY_PKCS8 = new byte[]{ ((byte) (48)), ((byte) (130)), ((byte) (1)), ((byte) (85)), ((byte) (2)), ((byte) (1)), ((byte) (0)), ((byte) (48)), ((byte) (13)), ((byte) (6)), ((byte) (9)), ((byte) (42)), ((byte) (134)), ((byte) (72)), ((byte) (134)), ((byte) (247)), ((byte) (13)), ((byte) (1)), ((byte) (1)), ((byte) (1)), ((byte) (5)), ((byte) (0)), ((byte) (4)), ((byte) (130)), ((byte) (1)), ((byte) (63)), ((byte) (48)), ((byte) (130)), ((byte) (1)), ((byte) (59)), ((byte) (2)), ((byte) (1)), ((byte) (0)), ((byte) (2)), ((byte) (65)), ((byte) (0)), ((byte) (198)), ((byte) (0)), ((byte) (121)), ((byte) (12)), ((byte) (70)), ((byte) (249)), ((byte) (3)), ((byte) (21)), ((byte) (186)), ((byte) (53)), ((byte) (99)), ((byte) (108)), ((byte) (151)), ((byte) (58)), ((byte) (108)), ((byte) (200)), ((byte) (21)), ((byte) (50)), ((byte) (42)), ((byte) (98)), ((byte) (114)), ((byte) (189)), ((byte) (5)), ((byte) (1)), ((byte) (207)), ((byte) (230)), ((byte) (73)), ((byte) (236)), ((byte) (201)), ((byte) (138)), ((byte) (58)), ((byte) (78)), ((byte) (177)), ((byte) (242)), ((byte) (62)), ((byte) (134)), ((byte) (60)), ((byte) (100)), ((byte) (74)), ((byte) (10)), ((byte) (41)), ((byte) (214)), ((byte) (250)), ((byte) (249)), ((byte) (172)), ((byte) (216)), ((byte) (123)), ((byte) (159)), ((byte) (42)), ((byte) (107)), ((byte) (19)), ((byte) (6)), ((byte) (6)), ((byte) (235)), ((byte) (131)), ((byte) (27)), ((byte) (184)), ((byte) (151)), ((byte) (163)), ((byte) (145)), ((byte) (149)), ((byte) (96)), ((byte) (21)), ((byte) (229)), ((byte) (2)), ((byte) (3)), ((byte) (1)), ((byte) (0)), ((byte) (1)), ((byte) (2)), ((byte) (64)), ((byte) (15)), ((byte) (218)), ((byte) (51)), ((byte) (214)), ((byte) (206)), ((byte) (203)), ((byte) (218)), ((byte) (250)), ((byte) (95)), ((byte) (89)), ((byte) (44)), ((byte) (231)), ((byte) (161)), ((byte) (199)), ((byte) (244)), ((byte) (179)), ((byte) (164)), ((byte) (54)), ((byte) (202)), ((byte) (251)), ((byte) (236)), ((byte) (209)), ((byte) (195)), ((byte) (87)), ((byte) (220)), ((byte) (204)), ((byte) (68)), ((byte) (56)), ((byte) (231)), ((byte) (253)), ((byte) (224)), ((byte) (35)), ((byte) (14)), ((byte) (151)), ((byte) (135)), ((byte) (85)), ((byte) (128)), ((byte) (43)), ((byte) (242)), ((byte) (244)), ((byte) (28)), ((byte) (3)), ((byte) (210)), ((byte) (62)), ((byte) (9)), ((byte) (114)), ((byte) (73)), ((byte) (216)), ((byte) (156)), ((byte) (172)), ((byte) (218)), ((byte) (101)), ((byte) (104)), ((byte) (77)), ((byte) (56)), ((byte) (25)), ((byte) (216)), ((byte) (177)), ((byte) (91)), ((byte) (183)), ((byte) (56)), ((byte) (200)), ((byte) (148)), ((byte) (181)), ((byte) (2)), ((byte) (33)), ((byte) (0)), ((byte) (247)), ((byte) (142)), ((byte) (32)), ((byte) (220)), ((byte) (38)), ((byte) (18)), ((byte) (58)), ((byte) (133)), ((byte) (145)), ((byte) (95)), ((byte) (69)), ((byte) (166)), ((byte) (149)), ((byte) (229)), ((byte) (34)), ((byte) (208)), ((byte) (196)), ((byte) (215)), ((byte) (106)), ((byte) (241)), ((byte) (67)), ((byte) (56)), ((byte) (136)), ((byte) (32)), ((byte) (125)), ((byte) (128)), ((byte) (115)), ((byte) (123)), ((byte) (220)), ((byte) (115)), ((byte) (81)), ((byte) (59)), ((byte) (2)), ((byte) (33)), ((byte) (0)), ((byte) (204)), ((byte) (193)), ((byte) (153)), ((byte) (200)), ((byte) (192)), ((byte) (84)), ((byte) (188)), ((byte) (233)), ((byte) (251)), ((byte) (119)), ((byte) (40)), ((byte) (184)), ((byte) (38)), ((byte) (2)), ((byte) (192)), ((byte) (12)), ((byte) (222)), ((byte) (253)), ((byte) (234)), ((byte) (208)), ((byte) (21)), ((byte) (75)), ((byte) (59)), ((byte) (209)), ((byte) (221)), ((byte) (253)), ((byte) (91)), ((byte) (172)), ((byte) (179)), ((byte) (207)), ((byte) (195)), ((byte) (95)), ((byte) (2)), ((byte) (33)), ((byte) (0)), ((byte) (205)), ((byte) (140)), ((byte) (37)), ((byte) (156)), ((byte) (165)), ((byte) (191)), ((byte) (220)), ((byte) (247)), ((byte) (170)), ((byte) (141)), ((byte) (0)), ((byte) (184)), ((byte) (33)), ((byte) (29)), ((byte) (240)), ((byte) (154)), ((byte) (135)), ((byte) (214)), ((byte) (149)), ((byte) (229)), ((byte) (93)), ((byte) (123)), ((byte) (67)), ((byte) (12)), ((byte) (55)), ((byte) (40)), ((byte) (192)), ((byte) (186)), ((byte) (199)), ((byte) (128)), ((byte) (184)), ((byte) (161)), ((byte) (2)), ((byte) (33)), ((byte) (0)), ((byte) (204)), ((byte) (38)), ((byte) (111)), ((byte) (173)), ((byte) (96)), ((byte) (78)), ((byte) (92)), ((byte) (185)), ((byte) (50)), ((byte) (87)), ((byte) (97)), ((byte) (139)), ((byte) (17)), ((byte) (163)), ((byte) (6)), ((byte) (87)), ((byte) (14)), ((byte) (242)), ((byte) (190)), ((byte) (111)), ((byte) (79)), ((byte) (251)), ((byte) (222)), ((byte) (29)), ((byte) (230)), ((byte) (167)), ((byte) (25)), ((byte) (3)), ((byte) (125)), ((byte) (152)), ((byte) (182)), ((byte) (35)), ((byte) (2)), ((byte) (32)), ((byte) (36)), ((byte) (128)), ((byte) (148)), ((byte) (255)), ((byte) (221)), ((byte) (122)), ((byte) (34)), ((byte) (125)), ((byte) (196)), ((byte) (90)), ((byte) (253)), ((byte) (132)), ((byte) (193)), ((byte) (173)), ((byte) (138)), ((byte) (19)), ((byte) (42)), ((byte) (249)), ((byte) (93)), ((byte) (255)), ((byte) (11)), ((byte) (46)), ((byte) (15)), ((byte) (97)), ((byte) (66)), ((byte) (136)), ((byte) (87)), ((byte) (207)), ((byte) (193)), ((byte) (113)), ((byte) (201)), ((byte) (185)) };

    private static final BigInteger RSA_KEY_N = new BigInteger("C600790C46F90315BA35636C973A6CC815322A6272BD0501CFE649ECC98A3A4EB1F23E863C644A0A29D6FAF9ACD87B9F2A6B130606EB831BB897A391956015E5", 16);

    private static final BigInteger RSA_KEY_E = new BigInteger("010001", 16);

    /* openssl dsaparam -genkey -text -out dsakey.pem 1024
    openssl dsa -in dsakey.pem -text -noout
    openssl pkcs8 -topk8 -in dsakey.pem -outform d -nocrypt | recode ../x1 | sed 's/0x/(byte) 0x/g'
     */
    private static final byte[] DSA_KEY_PKCS8 = new byte[]{ ((byte) (48)), ((byte) (130)), ((byte) (1)), ((byte) (74)), ((byte) (2)), ((byte) (1)), ((byte) (0)), ((byte) (48)), ((byte) (130)), ((byte) (1)), ((byte) (43)), ((byte) (6)), ((byte) (7)), ((byte) (42)), ((byte) (134)), ((byte) (72)), ((byte) (206)), ((byte) (56)), ((byte) (4)), ((byte) (1)), ((byte) (48)), ((byte) (130)), ((byte) (1)), ((byte) (30)), ((byte) (2)), ((byte) (129)), ((byte) (129)), ((byte) (0)), ((byte) (210)), ((byte) (24)), ((byte) (219)), ((byte) (148)), ((byte) (124)), ((byte) (214)), ((byte) (46)), ((byte) (226)), ((byte) (7)), ((byte) (56)), ((byte) (66)), ((byte) (196)), ((byte) (22)), ((byte) (36)), ((byte) (148)), ((byte) (47)), ((byte) (193)), ((byte) (15)), ((byte) (146)), ((byte) (10)), ((byte) (68)), ((byte) (68)), ((byte) (153)), ((byte) (252)), ((byte) (1)), ((byte) (27)), ((byte) (248)), ((byte) (243)), ((byte) (130)), ((byte) (87)), ((byte) (1)), ((byte) (141)), ((byte) (230)), ((byte) (34)), ((byte) (112)), ((byte) (160)), ((byte) (214)), ((byte) (5)), ((byte) (15)), ((byte) (241)), ((byte) (208)), ((byte) (244)), ((byte) (11)), ((byte) (162)), ((byte) (228)), ((byte) (30)), ((byte) (211)), ((byte) (68)), ((byte) (121)), ((byte) (116)), ((byte) (76)), ((byte) (193)), ((byte) (167)), ((byte) (165)), ((byte) (132)), ((byte) (216)), ((byte) (185)), ((byte) (223)), ((byte) (163)), ((byte) (133)), ((byte) (250)), ((byte) (242)), ((byte) (253)), ((byte) (68)), ((byte) (11)), ((byte) (177)), ((byte) (165)), ((byte) (130)), ((byte) (141)), ((byte) (6)), ((byte) (146)), ((byte) (202)), ((byte) (180)), ((byte) (251)), ((byte) (223)), ((byte) (194)), ((byte) (253)), ((byte) (167)), ((byte) (203)), ((byte) (111)), ((byte) (3)), ((byte) (185)), ((byte) (239)), ((byte) (253)), ((byte) (127)), ((byte) (188)), ((byte) (179)), ((byte) (29)), ((byte) (164)), ((byte) (232)), ((byte) (125)), ((byte) (162)), ((byte) (207)), ((byte) (98)), ((byte) (53)), ((byte) (6)), ((byte) (200)), ((byte) (254)), ((byte) (230)), ((byte) (231)), ((byte) (110)), ((byte) (174)), ((byte) (34)), ((byte) (231)), ((byte) (130)), ((byte) (56)), ((byte) (84)), ((byte) (130)), ((byte) (205)), ((byte) (234)), ((byte) (216)), ((byte) (105)), ((byte) (187)), ((byte) (28)), ((byte) (211)), ((byte) (112)), ((byte) (50)), ((byte) (177)), ((byte) (251)), ((byte) (7)), ((byte) (1)), ((byte) (102)), ((byte) (204)), ((byte) (36)), ((byte) (214)), ((byte) (80)), ((byte) (70)), ((byte) (155)), ((byte) (2)), ((byte) (21)), ((byte) (0)), ((byte) (214)), ((byte) (230)), ((byte) (126)), ((byte) (26)), ((byte) (229)), ((byte) (202)), ((byte) (29)), ((byte) (182)), ((byte) (175)), ((byte) (78)), ((byte) (217)), ((byte) (24)), ((byte) (232)), ((byte) (135)), ((byte) (177)), ((byte) (188)), ((byte) (147)), ((byte) (225)), ((byte) (128)), ((byte) (245)), ((byte) (2)), ((byte) (129)), ((byte) (128)), ((byte) (25)), ((byte) (32)), ((byte) (204)), ((byte) (24)), ((byte) (246)), ((byte) (143)), ((byte) (115)), ((byte) (250)), ((byte) (159)), ((byte) (80)), ((byte) (200)), ((byte) (146)), ((byte) (190)), ((byte) (7)), ((byte) (124)), ((byte) (52)), ((byte) (216)), ((byte) (111)), ((byte) (99)), ((byte) (201)), ((byte) (53)), ((byte) (72)), ((byte) (121)), ((byte) (121)), ((byte) (38)), ((byte) (239)), ((byte) (30)), ((byte) (153)), ((byte) (84)), ((byte) (215)), ((byte) (48)), ((byte) (44)), ((byte) (104)), ((byte) (188)), ((byte) (255)), ((byte) (242)), ((byte) (76)), ((byte) (106)), ((byte) (211)), ((byte) (45)), ((byte) (28)), ((byte) (122)), ((byte) (6)), ((byte) (17)), ((byte) (114)), ((byte) (146)), ((byte) (156)), ((byte) (170)), ((byte) (149)), ((byte) (14)), ((byte) (68)), ((byte) (44)), ((byte) (95)), ((byte) (25)), ((byte) (37)), ((byte) (180)), ((byte) (191)), ((byte) (33)), ((byte) (143)), ((byte) (183)), ((byte) (126)), ((byte) (75)), ((byte) (100)), ((byte) (131)), ((byte) (89)), ((byte) (32)), ((byte) (32)), ((byte) (54)), ((byte) (132)), ((byte) (164)), ((byte) (29)), ((byte) (181)), ((byte) (202)), ((byte) (127)), ((byte) (16)), ((byte) (78)), ((byte) (39)), ((byte) (33)), ((byte) (142)), ((byte) (44)), ((byte) (165)), ((byte) (248)), ((byte) (172)), ((byte) (189)), ((byte) (245)), ((byte) (181)), ((byte) (186)), ((byte) (235)), ((byte) (134)), ((byte) (111)), ((byte) (127)), ((byte) (177)), ((byte) (224)), ((byte) (144)), ((byte) (53)), ((byte) (202)), ((byte) (168)), ((byte) (100)), ((byte) (110)), ((byte) (6)), ((byte) (61)), ((byte) (2)), ((byte) (61)), ((byte) (149)), ((byte) (87)), ((byte) (179)), ((byte) (138)), ((byte) (226)), ((byte) (11)), ((byte) (211)), ((byte) (158)), ((byte) (28)), ((byte) (19)), ((byte) (222)), ((byte) (72)), ((byte) (163)), ((byte) (194)), ((byte) (17)), ((byte) (218)), ((byte) (117)), ((byte) (9)), ((byte) (246)), ((byte) (146)), ((byte) (15)), ((byte) (15)), ((byte) (166)), ((byte) (243)), ((byte) (62)), ((byte) (4)), ((byte) (22)), ((byte) (2)), ((byte) (20)), ((byte) (41)), ((byte) (80)), ((byte) (228)), ((byte) (119)), ((byte) (79)), ((byte) (178)), ((byte) (255)), ((byte) (251)), ((byte) (93)), ((byte) (51)), ((byte) (201)), ((byte) (55)), ((byte) (240)), ((byte) (181)), ((byte) (143)), ((byte) (251)), ((byte) (13)), ((byte) (69)), ((byte) (194)), ((byte) (0)) };

    private static final BigInteger DSA_KEY_P = new BigInteger("00d218db947cd62ee2073842c41624942fc10f920a444499fc011bf8f38257018de62270a0d6050ff1d0f40ba2e41ed34479744cc1a7a584d8b9dfa385faf2fd440bb1a5828d0692cab4fbdfc2fda7cb6f03b9effd7fbcb31da4e87da2cf623506c8fee6e76eae22e782385482cdead869bb1cd37032b1fb070166cc24d650469b", 16);

    private static final BigInteger DSA_KEY_Q = new BigInteger("00d6e67e1ae5ca1db6af4ed918e887b1bc93e180f5", 16);

    private static final BigInteger DSA_KEY_G = new BigInteger("1920cc18f68f73fa9f50c892be077c34d86f63c93548797926ef1e9954d7302c68bcfff24c6ad32d1c7a061172929caa950e442c5f1925b4bf218fb77e4b64835920203684a41db5ca7f104e27218e2ca5f8acbdf5b5baeb866f7fb1e09035caa8646e063d023d9557b38ae20bd39e1c13de48a3c211da7509f6920f0fa6f33e", 16);

    private static final BigInteger DSA_KEY_priv = new BigInteger("2950e4774fb2fffb5d33c937f0b58ffb0d45c200", 16);

    private static final BigInteger DSA_KEY_pub = new BigInteger("0087b82cdf3232db3bec0d00e96c8393bc7f5629551ea1a00888961cf56e80a36f2a7b316bc10b1d367a5ea374235c9361a472a9176f6cf61f708b86a52b4fae814abd1f1bdd16eea94aea9281851032b1bad7567624c615d6899ca1c94ad614f14e767e49d2ba5223cd113a0d02b66183653cd346ae76d85843afe66520904274", 16);

    @Test
    public void getOidFromPkcs8Encoded_Ec_NistP256() throws Exception {
        Assert.assertEquals("1.2.840.10045.2.1", PubkeyUtils.getOidFromPkcs8Encoded(PubkeyUtilsTest.EC_KEY_PKCS8));
    }

    @Test
    public void getOidFromPkcs8Encoded_Rsa() throws Exception {
        Assert.assertEquals("1.2.840.113549.1.1.1", PubkeyUtils.getOidFromPkcs8Encoded(PubkeyUtilsTest.RSA_KEY_PKCS8));
    }

    @Test
    public void getOidFromPkcs8Encoded_Dsa() throws Exception {
        Assert.assertEquals("1.2.840.10040.4.1", PubkeyUtils.getOidFromPkcs8Encoded(PubkeyUtilsTest.DSA_KEY_PKCS8));
    }

    @Test
    public void getOidFromPkcs8Encoded_Null_Failure() throws Exception {
        try {
            PubkeyUtils.getOidFromPkcs8Encoded(null);
            Assert.fail("Should throw NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException expected) {
        }
    }

    @Test
    public void getOidFromPkcs8Encoded_NotCorrectDer_Failure() throws Exception {
        try {
            PubkeyUtils.getOidFromPkcs8Encoded(new byte[]{ 48, 1, 0 });
            Assert.fail("Should throw NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException expected) {
        }
    }

    @Test
    public void getAlgorithmForOid_Ecdsa() throws Exception {
        Assert.assertEquals("EC", PubkeyUtils.getAlgorithmForOid("1.2.840.10045.2.1"));
    }

    @Test
    public void getAlgorithmForOid_Rsa() throws Exception {
        Assert.assertEquals("RSA", PubkeyUtils.getAlgorithmForOid("1.2.840.113549.1.1.1"));
    }

    @Test
    public void getAlgorithmForOid_NullInput_Failure() throws Exception {
        try {
            PubkeyUtils.getAlgorithmForOid(null);
            Assert.fail("Should throw NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException expected) {
        }
    }

    @Test
    public void getAlgorithmForOid_UnknownOid_Failure() throws Exception {
        try {
            PubkeyUtils.getAlgorithmForOid("1.3.66666.2000.4000.1");
            Assert.fail("Should throw NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException expected) {
        }
    }

    @Test
    public void recoverKeyPair_Dsa() throws Exception {
        KeyPair kp = PubkeyUtils.recoverKeyPair(PubkeyUtilsTest.DSA_KEY_PKCS8);
        DSAPublicKey pubKey = ((DSAPublicKey) (kp.getPublic()));
        Assert.assertEquals(PubkeyUtilsTest.DSA_KEY_pub, pubKey.getY());
        DSAParams params = pubKey.getParams();
        Assert.assertEquals(params.getG(), PubkeyUtilsTest.DSA_KEY_G);
        Assert.assertEquals(params.getP(), PubkeyUtilsTest.DSA_KEY_P);
        Assert.assertEquals(params.getQ(), PubkeyUtilsTest.DSA_KEY_Q);
    }

    @Test
    public void recoverKeyPair_Rsa() throws Exception {
        KeyPair kp = PubkeyUtils.recoverKeyPair(PubkeyUtilsTest.RSA_KEY_PKCS8);
        RSAPublicKey pubKey = ((RSAPublicKey) (kp.getPublic()));
        Assert.assertEquals(PubkeyUtilsTest.RSA_KEY_N, pubKey.getModulus());
        Assert.assertEquals(PubkeyUtilsTest.RSA_KEY_E, pubKey.getPublicExponent());
    }

    @Test
    public void recoverKeyPair_Ec() throws Exception {
        KeyPair kp = PubkeyUtils.recoverKeyPair(PubkeyUtilsTest.EC_KEY_PKCS8);
        ECPublicKey pubKey = ((ECPublicKey) (kp.getPublic()));
        Assert.assertEquals(PubkeyUtilsTest.EC_KEY_pub_x, pubKey.getW().getAffineX());
        Assert.assertEquals(PubkeyUtilsTest.EC_KEY_pub_y, pubKey.getW().getAffineY());
    }

    private static class MyPrivateKey implements PrivateKey {
        @Override
        public String getAlgorithm() {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] getEncoded() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getFormat() {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void recoverPublicKey_FakeKey_Failure() throws Exception {
        try {
            PubkeyUtils.recoverPublicKey(null, new PubkeyUtilsTest.MyPrivateKey());
            Assert.fail("Should not accept unknown key types");
        } catch (NoSuchAlgorithmException expected) {
        }
    }

    @Test
    public void getRSAPublicExponentFromPkcs8Encoded_Success() throws Exception {
        Assert.assertEquals(PubkeyUtilsTest.RSA_KEY_E, PubkeyUtils.getRSAPublicExponentFromPkcs8Encoded(PubkeyUtilsTest.RSA_KEY_PKCS8));
    }
}

