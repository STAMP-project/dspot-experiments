package org.apereo.cas.util.gen;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class DefaultLongNumericGeneratorTests {
    @Test
    public void verifyWrap() {
        Assertions.assertEquals(Long.MAX_VALUE, new DefaultLongNumericGenerator(Long.MAX_VALUE).getNextLong());
    }

    @Test
    public void verifyInitialValue() {
        Assertions.assertEquals(10L, new DefaultLongNumericGenerator(10L).getNextLong());
    }

    @Test
    public void verifyIncrementWithNoWrap() {
        Assertions.assertEquals(0, new DefaultLongNumericGenerator().getNextLong());
    }

    @Test
    public void verifyIncrementWithNoWrap2() {
        val g = new DefaultLongNumericGenerator();
        g.getNextLong();
        Assertions.assertEquals(1, g.getNextLong());
    }

    @Test
    public void verifyMinimumSize() {
        Assertions.assertEquals(1, new DefaultLongNumericGenerator().minLength());
    }

    @Test
    public void verifyMaximumLength() {
        Assertions.assertEquals(Long.toString(Long.MAX_VALUE).length(), new DefaultLongNumericGenerator().maxLength());
    }
}

