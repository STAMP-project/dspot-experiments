package org.pac4j.core.credentials.password;


import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests {@link JBCryptPasswordEncoder}.
 *
 * @author Jerome Leleu
 * @since 3.1.0
 */
public final class JBCryptPasswordEncoderTests implements TestsConstants {
    private final JBCryptPasswordEncoder encoder = new JBCryptPasswordEncoder();

    @Test
    public void test() {
        final String hashedPwd = encoder.encode(TestsConstants.PASSWORD);
        Assert.assertTrue(encoder.matches(TestsConstants.PASSWORD, hashedPwd));
        Assert.assertFalse(encoder.matches(TestsConstants.VALUE, hashedPwd));
    }
}

