package org.apereo.cas.util.gen;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class DefaultRandomStringGeneratorTests {
    private static final int LENGTH = 36;

    private final RandomStringGenerator randomStringGenerator = new DefaultRandomStringGenerator(DefaultRandomStringGeneratorTests.LENGTH);

    @Test
    public void verifyDefaultLength() {
        Assertions.assertEquals(DefaultRandomStringGeneratorTests.LENGTH, this.randomStringGenerator.getDefaultLength());
    }

    @Test
    public void verifyRandomString() {
        Assertions.assertNotSame(this.randomStringGenerator.getNewString(), this.randomStringGenerator.getNewString());
    }
}

