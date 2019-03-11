package org.jivesoftware.util;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class LocaleUtilsTest {
    @Test
    public void getLocalizedStringWillReturnASensibleDefaultValue() {
        final String key = "if.this.key.exists.the.test.will.fail";
        Assert.assertThat(LocaleUtils.getLocalizedString(key), Matchers.is((("???" + key) + "???")));
    }
}

