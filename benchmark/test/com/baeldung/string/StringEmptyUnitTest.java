package com.baeldung.string;


import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class StringEmptyUnitTest {
    private String text = "baeldung";

    @Test
    public void givenAString_whenCheckedForEmptyUsingJunit_shouldAssertSuccessfully() {
        Assert.assertTrue((!(text.isEmpty())));
        Assert.assertFalse(text.isEmpty());
        Assert.assertNotEquals("", text);
        Assert.assertNotSame("", text);
    }

    @Test
    public void givenAString_whenCheckedForEmptyUsingHamcrest_shouldAssertSuccessfully() {
        Assert.assertThat(text, CoreMatchers.not(isEmptyString()));
        Assert.assertThat(text, CoreMatchers.not(isEmptyOrNullString()));
    }

    @Test
    public void givenAString_whenCheckedForEmptyUsingCommonsLang_shouldAssertSuccessfully() {
        Assert.assertTrue(StringUtils.isNotBlank(text));
    }

    @Test
    public void givenAString_whenCheckedForEmptyUsingAssertJ_shouldAssertSuccessfully() {
        Assertions.assertThat(text).isNotEmpty();
    }

    @Test
    public void givenAString_whenCheckedForEmptyUsingGuava_shouldAssertSuccessfully() {
        Assert.assertFalse(Strings.isNullOrEmpty(text));
    }
}

