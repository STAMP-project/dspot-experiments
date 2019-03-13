package cn.hutool.crypto.test;


import CharsetUtil.CHARSET_UTF_8;
import KeyType.PrivateKey;
import KeyType.PublicKey;
import KeyUtil.SM2_DEFAULT_CURVE;
import SM2Mode.C1C3C2;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import java.security.KeyPair;
import org.junit.Assert;
import org.junit.Test;


/**
 * SM2??????
 *
 * @author Looly, Gsealy
 */
public class SM2Test {
    @Test
    public void generateKeyPairTest() {
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        Assert.assertNotNull(pair.getPrivate());
        Assert.assertNotNull(pair.getPublic());
    }

    @Test
    public void KeyPairOIDTest() {
        // OBJECT IDENTIFIER 1.2.156.10197.1.301
        String OID = "06082A811CCF5501822D";
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        Assert.assertTrue(HexUtil.encodeHexStr(pair.getPrivate().getEncoded()).toUpperCase().contains(OID));
        Assert.assertTrue(HexUtil.encodeHexStr(pair.getPublic().getEncoded()).toUpperCase().contains(OID));
    }

    @Test
    public void sm2CustomKeyTest() {
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        byte[] privateKey = pair.getPrivate().getEncoded();
        byte[] publicKey = pair.getPublic().getEncoded();
        SM2 sm2 = SmUtil.sm2(privateKey, publicKey);
        sm2.setMode(C1C3C2);
        // ?????????
        byte[] encrypt = sm2.encrypt(StrUtil.bytes("??????aaaa", CHARSET_UTF_8), PublicKey);
        byte[] decrypt = sm2.decrypt(encrypt, PrivateKey);
        Assert.assertEquals("??????aaaa", StrUtil.str(decrypt, CHARSET_UTF_8));
    }

    @Test
    public void sm2Test() {
        final SM2 sm2 = SmUtil.sm2();
        // ???????
        Assert.assertNotNull(sm2.getPrivateKey());
        Assert.assertNotNull(sm2.getPrivateKeyBase64());
        Assert.assertNotNull(sm2.getPublicKey());
        Assert.assertNotNull(sm2.getPrivateKeyBase64());
        // ?????????
        byte[] encrypt = sm2.encrypt(StrUtil.bytes("??????aaaa", CHARSET_UTF_8), PublicKey);
        byte[] decrypt = sm2.decrypt(encrypt, PrivateKey);
        Assert.assertEquals("??????aaaa", StrUtil.str(decrypt, CHARSET_UTF_8));
    }

    @Test
    public void sm2BcdTest() {
        String text = "??????aaaa";
        final SM2 sm2 = SmUtil.sm2();
        // ?????????
        String encryptStr = sm2.encryptBcd(text, PublicKey);
        String decryptStr = StrUtil.utf8Str(sm2.decryptFromBcd(encryptStr, PrivateKey));
        Assert.assertEquals(text, decryptStr);
    }

    @Test
    public void sm2Base64Test() {
        String textBase = "??????????";
        String text = "";
        for (int i = 0; i < 100; i++) {
            text += textBase;
        }
        final SM2 sm2 = new SM2();
        // ?????????
        String encryptStr = sm2.encryptBase64(text, PublicKey);
        String decryptStr = StrUtil.utf8Str(sm2.decryptFromBase64(encryptStr, PrivateKey));
        Assert.assertEquals(text, decryptStr);
    }

    @Test
    public void sm2SignAndVerifyTest() {
        String content = "??Hanley.";
        final SM2 sm2 = SmUtil.sm2();
        byte[] sign = sm2.sign(content.getBytes());
        boolean verify = sm2.verify(content.getBytes(), sign);
        Assert.assertTrue(verify);
    }

    @Test
    public void sm2SignAndVerifyUseKeyTest() {
        String content = "??Hanley.";
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        final SM2 sm2 = // 
        // 
        // 
        new SM2(HexUtil.encodeHexStr(pair.getPrivate().getEncoded()), HexUtil.encodeHexStr(pair.getPublic().getEncoded()));
        byte[] sign = sm2.sign(content.getBytes());
        boolean verify = sm2.verify(content.getBytes(), sign);
        Assert.assertTrue(verify);
    }

    @Test
    public void sm2PublicKeyEncodeDecodeTest() {
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        java.security.PublicKey publicKey = pair.getPublic();
        byte[] data = KeyUtil.encodeECPublicKey(publicKey);
        String encodeHex = HexUtil.encodeHexStr(data);
        String encodeB64 = Base64.encode(data);
        java.security.PublicKey Hexdecode = KeyUtil.decodeECPoint(encodeHex, SM2_DEFAULT_CURVE);
        java.security.PublicKey B64decode = KeyUtil.decodeECPoint(encodeB64, SM2_DEFAULT_CURVE);
        Assert.assertEquals(HexUtil.encodeHexStr(publicKey.getEncoded()), HexUtil.encodeHexStr(Hexdecode.getEncoded()));
        Assert.assertEquals(HexUtil.encodeHexStr(publicKey.getEncoded()), HexUtil.encodeHexStr(B64decode.getEncoded()));
    }
}

