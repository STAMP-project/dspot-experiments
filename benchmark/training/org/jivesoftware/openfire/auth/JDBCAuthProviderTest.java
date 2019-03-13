package org.jivesoftware.openfire.auth;


import JDBCAuthProvider.PasswordType.bcrypt;
import JDBCAuthProvider.PasswordType.md5;
import JDBCAuthProvider.PasswordType.sha1;
import JDBCAuthProvider.PasswordType.sha256;
import JDBCAuthProvider.PasswordType.sha512;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;
import org.junit.Assert;
import org.junit.Test;


public class JDBCAuthProviderTest {
    private static final String PASSWORD = "password";

    private static final String MD5_SHA1_PASSWORD = "55c3b5386c486feb662a0785f340938f518d547f";

    private static final String MD5_SHA512_PASSWORD = "85ec0898f0998c95a023f18f1123cbc77ba51f2632137b61999655d59817d942ecef3012762604e442d395a194c53e94e9fb5bb8fe74d61900eb05cb0c078bb6";

    private static final String MD5_PASSWORD = "5f4dcc3b5aa765d61d8327deb882cf99";

    private static final String SHA1_PASSWORD = "5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8";

    private static final String SHA256_PASSWORD = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";

    private static final String SHA512_PASSWORD = "b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f1326af5a2ea6d103fd07c95385ffab0cacbc86";

    private static final String BCRYPTED_PASSWORD = "$2a$10$TS9mWNnHbTU.ukLUlrOopuGooirFR3IltqgRFcyM.iSPQuoPDAafG";

    private final JDBCAuthProvider jdbcAuthProvider = new JDBCAuthProvider();

    @Test
    public void hashPassword() throws Exception {
        Assert.assertTrue(JDBCAuthProviderTest.MD5_PASSWORD.equals(jdbcAuthProvider.hashPassword(JDBCAuthProviderTest.PASSWORD, md5)));
        Assert.assertTrue(JDBCAuthProviderTest.SHA1_PASSWORD.equals(jdbcAuthProvider.hashPassword(JDBCAuthProviderTest.PASSWORD, sha1)));
        Assert.assertTrue(JDBCAuthProviderTest.SHA256_PASSWORD.equals(jdbcAuthProvider.hashPassword(JDBCAuthProviderTest.PASSWORD, sha256)));
        Assert.assertTrue(JDBCAuthProviderTest.SHA512_PASSWORD.equals(jdbcAuthProvider.hashPassword(JDBCAuthProviderTest.PASSWORD, sha512)));
        Assert.assertFalse(JDBCAuthProviderTest.BCRYPTED_PASSWORD.equals(jdbcAuthProvider.hashPassword(JDBCAuthProviderTest.PASSWORD, bcrypt)));
        Assert.assertTrue(OpenBSDBCrypt.checkPassword(JDBCAuthProviderTest.BCRYPTED_PASSWORD, JDBCAuthProviderTest.PASSWORD.toCharArray()));
    }

    @Test
    public void comparePasswords_sha256() throws Exception {
        setPasswordTypes("sha256");
        Assert.assertTrue("password should be sha256", jdbcAuthProvider.comparePasswords(JDBCAuthProviderTest.PASSWORD, JDBCAuthProviderTest.SHA256_PASSWORD));
    }

    @Test
    public void comparePasswords_bcrypt() throws Exception {
        setPasswordTypes("bcrypt");
        Assert.assertTrue("password should be bcrypted", jdbcAuthProvider.comparePasswords(JDBCAuthProviderTest.PASSWORD, JDBCAuthProviderTest.BCRYPTED_PASSWORD));
    }

    @Test
    public void comparePasswords_bcryptLast() throws Exception {
        setPasswordTypes("bcrypt,md5,plain");
        Assert.assertTrue("should ignore everything beyond bcrypt", jdbcAuthProvider.comparePasswords(JDBCAuthProviderTest.PASSWORD, JDBCAuthProviderTest.BCRYPTED_PASSWORD));
    }

    @Test
    public void comparePasswords_ignoreUnknownDefaultPlain() throws Exception {
        setPasswordTypes("blowfish,puckerfish,rainbowtrout");
        Assert.assertTrue("should passively ignore unknown, add plain if empty", jdbcAuthProvider.comparePasswords(JDBCAuthProviderTest.PASSWORD, JDBCAuthProviderTest.PASSWORD));
    }

    @Test
    public void comparePasswords_md5_sha1() throws Exception {
        setPasswordTypes("md5,sha1");
        Assert.assertTrue("password should be md5 -> sha1", jdbcAuthProvider.comparePasswords(JDBCAuthProviderTest.PASSWORD, JDBCAuthProviderTest.MD5_SHA1_PASSWORD));
    }

    @Test
    public void comparePasswords_md5_sha512() throws Exception {
        setPasswordTypes("md5,sha512");
        Assert.assertTrue("password should be md5 -> sha512", jdbcAuthProvider.comparePasswords(JDBCAuthProviderTest.PASSWORD, JDBCAuthProviderTest.MD5_SHA512_PASSWORD));
    }

    @Test
    public void comparePasswords_plain_md5_plain_plain() throws Exception {
        setPasswordTypes("plain,md5,plain,plain");
        Assert.assertTrue("weird password chains are fine", jdbcAuthProvider.comparePasswords(JDBCAuthProviderTest.PASSWORD, JDBCAuthProviderTest.MD5_PASSWORD));
    }
}

