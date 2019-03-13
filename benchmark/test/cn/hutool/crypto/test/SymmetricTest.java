package cn.hutool.crypto.test;


import CharsetUtil.CHARSET_UTF_8;
import SymmetricAlgorithm.AES;
import SymmetricAlgorithm.DES;
import SymmetricAlgorithm.DESede;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.hutool.crypto.symmetric.Vigenere;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????????
 *
 * @author Looly
 */
public class SymmetricTest {
    @Test
    public void aesTest() {
        String content = "test??";
        // ??????
        byte[] key = SecureUtil.generateKey(AES.getValue()).getEncoded();
        // ??
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        // ??
        byte[] encrypt = aes.encrypt(content);
        // ??
        byte[] decrypt = aes.decrypt(encrypt);
        Assert.assertEquals(content, StrUtil.str(decrypt, CHARSET_UTF_8));
        // ???16????
        String encryptHex = aes.encryptHex(content);
        // ??????
        String decryptStr = aes.decryptStr(encryptHex, CHARSET_UTF_8);
        Assert.assertEquals(content, decryptStr);
    }

    @Test
    public void aesTest2() {
        String content = "test??";
        // ??????
        byte[] key = SecureUtil.generateKey(AES.getValue()).getEncoded();
        // ??
        cn.hutool.crypto.symmetric.AES aes = SecureUtil.aes(key);
        // ??
        byte[] encrypt = aes.encrypt(content);
        // ??
        byte[] decrypt = aes.decrypt(encrypt);
        Assert.assertEquals(content, StrUtil.utf8Str(decrypt));
        // ???16????
        String encryptHex = aes.encryptHex(content);
        // ??????
        String decryptStr = aes.decryptStr(encryptHex, CHARSET_UTF_8);
        Assert.assertEquals(content, decryptStr);
    }

    @Test
    public void aesTest3() {
        String content = "test??aaaaaaaaaaaaaaaaaaaaa";
        cn.hutool.crypto.symmetric.AES aes = new cn.hutool.crypto.symmetric.AES(Mode.CTS, Padding.PKCS5Padding, "0CoJUm6Qyw8W8jud".getBytes(), "0102030405060708".getBytes());
        // ??
        byte[] encrypt = aes.encrypt(content);
        // ??
        byte[] decrypt = aes.decrypt(encrypt);
        Assert.assertEquals(content, StrUtil.utf8Str(decrypt));
        // ???16????
        String encryptHex = aes.encryptHex(content);
        // ??????
        String decryptStr = aes.decryptStr(encryptHex, CHARSET_UTF_8);
        Assert.assertEquals(content, decryptStr);
    }

    @Test
    public void desTest() {
        String content = "test??";
        byte[] key = SecureUtil.generateKey(DES.getValue()).getEncoded();
        SymmetricCrypto des = new SymmetricCrypto(SymmetricAlgorithm.DES, key);
        byte[] encrypt = des.encrypt(content);
        byte[] decrypt = des.decrypt(encrypt);
        Assert.assertEquals(content, StrUtil.utf8Str(decrypt));
        String encryptHex = des.encryptHex(content);
        String decryptStr = des.decryptStr(encryptHex);
        Assert.assertEquals(content, decryptStr);
    }

    @Test
    public void desTest2() {
        String content = "test??";
        byte[] key = SecureUtil.generateKey(DES.getValue()).getEncoded();
        cn.hutool.crypto.symmetric.DES des = SecureUtil.des(key);
        byte[] encrypt = des.encrypt(content);
        byte[] decrypt = des.decrypt(encrypt);
        Assert.assertEquals(content, StrUtil.utf8Str(decrypt));
        String encryptHex = des.encryptHex(content);
        String decryptStr = des.decryptStr(encryptHex);
        Assert.assertEquals(content, decryptStr);
    }

    @Test
    public void desTest3() {
        String content = "test??";
        cn.hutool.crypto.symmetric.DES des = new cn.hutool.crypto.symmetric.DES(Mode.CTS, Padding.PKCS5Padding, "0CoJUm6Qyw8W8jud".getBytes(), "01020304".getBytes());
        byte[] encrypt = des.encrypt(content);
        byte[] decrypt = des.decrypt(encrypt);
        Assert.assertEquals(content, StrUtil.utf8Str(decrypt));
        String encryptHex = des.encryptHex(content);
        String decryptStr = des.decryptStr(encryptHex);
        Assert.assertEquals(content, decryptStr);
    }

    @Test
    public void desdeTest() {
        String content = "test??";
        byte[] key = SecureUtil.generateKey(DESede.getValue()).getEncoded();
        cn.hutool.crypto.symmetric.DESede des = SecureUtil.desede(key);
        byte[] encrypt = des.encrypt(content);
        byte[] decrypt = des.decrypt(encrypt);
        Assert.assertEquals(content, StrUtil.utf8Str(decrypt));
        String encryptHex = des.encryptHex(content);
        String decryptStr = des.decryptStr(encryptHex);
        Assert.assertEquals(content, decryptStr);
    }

    @Test
    public void desdeTest2() {
        String content = "test??";
        byte[] key = SecureUtil.generateKey(DESede.getValue()).getEncoded();
        cn.hutool.crypto.symmetric.DESede des = new cn.hutool.crypto.symmetric.DESede(Mode.CBC, Padding.PKCS5Padding, key, "12345678".getBytes());
        byte[] encrypt = des.encrypt(content);
        byte[] decrypt = des.decrypt(encrypt);
        Assert.assertEquals(content, StrUtil.utf8Str(decrypt));
        String encryptHex = des.encryptHex(content);
        String decryptStr = des.decryptStr(encryptHex);
        Assert.assertEquals(content, decryptStr);
    }

    @Test
    public void vigenereTest() {
        String content = "Wherethereisawillthereisaway";
        String key = "CompleteVictory";
        String encrypt = Vigenere.encrypt(content, key);
        Assert.assertEquals("zXScRZ]KIOMhQjc0\\bYRXZOJK[Vi", encrypt);
        String decrypt = Vigenere.decrypt(encrypt, key);
        Assert.assertEquals(content, decrypt);
    }
}

