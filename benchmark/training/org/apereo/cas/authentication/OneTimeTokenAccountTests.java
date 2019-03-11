package org.apereo.cas.authentication;


import lombok.val;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link OneTimeTokenAccountTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class OneTimeTokenAccountTests {
    @Test
    public void verifyComparisonWorks() {
        val otp1 = new OneTimeTokenAccount("casuser", "secret", 123456, CollectionUtils.wrapList(1, 2, 3, 4, 5, 6));
        val otp2 = new OneTimeTokenAccount("casuser", "secret", 987063, CollectionUtils.wrapList(1, 2, 1, 4, 7, 6));
        Assertions.assertEquals(1, otp1.compareTo(otp2));
        Assertions.assertEquals(0, otp1.compareTo(otp1.clone()));
    }
}

