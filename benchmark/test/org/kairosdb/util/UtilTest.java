package org.kairosdb.util;


import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class UtilTest {
    @Test(expected = NullPointerException.class)
    public void test_isNumber_nullString_invalid() {
        Util.isNumber(null);
    }

    @Test
    public void test_isNumber_emptyString() {
        Assert.assertThat(Util.isNumber(""), IsEqual.equalTo(false));
    }

    @Test
    public void test_isNumber_onlyPlus() {
        Assert.assertThat(Util.isNumber("+"), IsEqual.equalTo(false));
    }

    @Test
    public void test_isNumber_onlyMinus() {
        Assert.assertThat(Util.isNumber("-"), IsEqual.equalTo(false));
    }

    @Test
    public void test_isNumber_onlyPeriod() {
        Assert.assertThat(Util.isNumber("."), IsEqual.equalTo(false));
    }

    @Test
    public void test_isNumber_trailingPeriod() {
        Assert.assertThat(Util.isNumber("3."), IsEqual.equalTo(false));
    }

    @Test
    public void test_isNumber_hasLetter() {
        Assert.assertThat(Util.isNumber("3.5A5"), IsEqual.equalTo(false));
    }

    @Test
    public void test_isNumber_withMinusSignInMiddle() {
        Assert.assertThat(Util.isNumber("10-2"), IsEqual.equalTo(false));
    }

    @Test
    public void test_isNumber_withPlusSignInMiddle() {
        Assert.assertThat(Util.isNumber("10+2"), IsEqual.equalTo(false));
    }

    @Test
    public void test_isNumber_startsWithLetter() {
        Assert.assertThat(Util.isNumber("s123"), IsEqual.equalTo(false));
    }

    @Test
    public void test_isNumber_doubleValue() {
        Assert.assertThat(Util.isNumber("3.55"), IsEqual.equalTo(true));
    }

    @Test
    public void test_isNumber_integerValue() {
        Assert.assertThat(Util.isNumber("102"), IsEqual.equalTo(true));
    }

    @Test
    public void test_isNumber_withPlusSign() {
        Assert.assertThat(Util.isNumber("+102"), IsEqual.equalTo(true));
    }

    @Test
    public void test_isNumber_withMinusSign() {
        Assert.assertThat(Util.isNumber("-102"), IsEqual.equalTo(true));
    }

    @Test
    public void test_isNumber_withPoint() {
        Assert.assertThat(Util.isNumber("6.0.5"), IsEqual.equalTo(false));
    }
}

