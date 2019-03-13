package org.apereo.cas.util;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link RegexUtils}
 *
 * @author David Rodriguez
 * @since 5.1.0
 */
public class RegexUtilsTests {
    @Test
    public void verifyNotValidRegex() {
        val notValidRegex = "***";
        Assertions.assertFalse(RegexUtils.isValidRegex(notValidRegex));
    }

    @Test
    public void verifyNullRegex() {
        Assertions.assertFalse(RegexUtils.isValidRegex(null));
    }
}

