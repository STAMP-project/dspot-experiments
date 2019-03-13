package org.apereo.cas.util.gen;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link HexRandomStringGenerator}.
 *
 * @author Timur Duehr
 * @since 5.2.0
 */
public class HexRandomStringGeneratorTests {
    private static final int LENGTH = 36;

    private final RandomStringGenerator randomStringGenerator = new HexRandomStringGenerator(HexRandomStringGeneratorTests.LENGTH);

    @Test
    public void verifyDefaultLength() {
        Assertions.assertEquals(HexRandomStringGeneratorTests.LENGTH, this.randomStringGenerator.getDefaultLength());
        Assertions.assertEquals(HexRandomStringGeneratorTests.LENGTH, new HexRandomStringGenerator().getDefaultLength());
    }

    @Test
    public void verifyRandomString() {
        Assertions.assertNotSame(this.randomStringGenerator.getNewString(), this.randomStringGenerator.getNewString());
    }
}

