package org.apereo.cas.util.gen;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link Base64RandomStringGenerator}.
 *
 * @author Timur Duehr
 * @since 5.2.0
 */
public class Base64RandomStringGeneratorTests {
    private static final int LENGTH = 36;

    private final RandomStringGenerator randomStringGenerator = new Base64RandomStringGenerator(Base64RandomStringGeneratorTests.LENGTH);

    @Test
    public void verifyDefaultLength() {
        Assertions.assertEquals(Base64RandomStringGeneratorTests.LENGTH, this.randomStringGenerator.getDefaultLength());
        Assertions.assertEquals(Base64RandomStringGeneratorTests.LENGTH, new Base64RandomStringGenerator().getDefaultLength());
    }

    @Test
    public void verifyRandomString() {
        val s1 = this.randomStringGenerator.getNewString();
        val s2 = this.randomStringGenerator.getNewString();
        Assertions.assertNotSame(s1, s2);
    }
}

