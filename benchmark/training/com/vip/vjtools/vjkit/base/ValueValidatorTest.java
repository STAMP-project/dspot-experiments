package com.vip.vjtools.vjkit.base;


import Validator.INTEGER_GT_ZERO_VALIDATOR;
import Validator.STRICT_BOOL_VALUE_VALIDATOR;
import Validator.STRING_EMPTY_VALUE_VALIDATOR;
import org.junit.Test;


public class ValueValidatorTest {
    @Test
    public void testValidator() {
        assertThat(ValueValidator.checkAndGet((-1), 1, INTEGER_GT_ZERO_VALIDATOR)).isEqualTo(1);
        assertThat(ValueValidator.checkAndGet("testUnEmpty", "isEmpty", STRING_EMPTY_VALUE_VALIDATOR)).isEqualTo("testUnEmpty");
        assertThat(ValueValidator.checkAndGet("flase", "true", STRICT_BOOL_VALUE_VALIDATOR)).isEqualTo("true");
    }
}

