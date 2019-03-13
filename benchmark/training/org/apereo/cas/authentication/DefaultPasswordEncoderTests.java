package org.apereo.cas.authentication;


import PasswordEncoderProperties.PasswordEncoderTypes.DEFAULT;
import lombok.val;
import org.apereo.cas.authentication.support.password.PasswordEncoderUtils;
import org.apereo.cas.configuration.model.core.authentication.PasswordEncoderProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.StandardPasswordEncoder;


/**
 * This is {@link DefaultPasswordEncoderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
public class DefaultPasswordEncoderTests {
    @Test
    public void verifyPasswordEncoderByCustomClassName() {
        val p = new PasswordEncoderProperties();
        p.setType(StandardPasswordEncoder.class.getName());
        p.setSecret("SECRET");
        val e = PasswordEncoderUtils.newPasswordEncoder(p);
        Assertions.assertNotNull(e);
    }

    @Test
    public void verifyPasswordEncoderByMD5() {
        val p = new PasswordEncoderProperties();
        p.setType(DEFAULT.name());
        p.setEncodingAlgorithm("MD5");
        p.setCharacterEncoding("UTF-8");
        val e = PasswordEncoderUtils.newPasswordEncoder(p);
        Assertions.assertTrue(e.matches("asd123", "bfd59291e825b5f2bbf1eb76569f8fe7"));
    }

    @Test
    public void verifyPasswordEncoderBySHA1() {
        val p = new PasswordEncoderProperties();
        p.setType(DEFAULT.name());
        p.setEncodingAlgorithm("SHA-1");
        p.setCharacterEncoding("UTF-8");
        val e = PasswordEncoderUtils.newPasswordEncoder(p);
        Assertions.assertTrue(e.matches("asd123", "2891baceeef1652ee698294da0e71ba78a2a4064"));
    }

    @Test
    public void verifyPasswordEncoderBySHA256() {
        val p = new PasswordEncoderProperties();
        p.setType(DEFAULT.name());
        p.setEncodingAlgorithm("SHA-256");
        p.setCharacterEncoding("UTF-8");
        val e = PasswordEncoderUtils.newPasswordEncoder(p);
        Assertions.assertTrue(e.matches("asd123", "54d5cb2d332dbdb4850293caae4559ce88b65163f1ea5d4e4b3ac49d772ded14"));
    }
}

