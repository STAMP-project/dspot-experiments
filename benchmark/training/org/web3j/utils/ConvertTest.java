package org.web3j.utils;


import Convert.Unit;
import Convert.Unit.ETHER;
import Convert.Unit.FINNEY;
import Convert.Unit.GETHER;
import Convert.Unit.GWEI;
import Convert.Unit.KETHER;
import Convert.Unit.KWEI;
import Convert.Unit.METHER;
import Convert.Unit.MWEI;
import Convert.Unit.SZABO;
import Convert.Unit.WEI;
import java.math.BigDecimal;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ConvertTest {
    @Test
    public void testFromWei() {
        MatcherAssert.assertThat(Convert.fromWei("21000000000000", WEI), CoreMatchers.is(new BigDecimal("21000000000000")));
        MatcherAssert.assertThat(Convert.fromWei("21000000000000", KWEI), CoreMatchers.is(new BigDecimal("21000000000")));
        MatcherAssert.assertThat(Convert.fromWei("21000000000000", MWEI), CoreMatchers.is(new BigDecimal("21000000")));
        MatcherAssert.assertThat(Convert.fromWei("21000000000000", GWEI), CoreMatchers.is(new BigDecimal("21000")));
        MatcherAssert.assertThat(Convert.fromWei("21000000000000", SZABO), CoreMatchers.is(new BigDecimal("21")));
        MatcherAssert.assertThat(Convert.fromWei("21000000000000", FINNEY), CoreMatchers.is(new BigDecimal("0.021")));
        MatcherAssert.assertThat(Convert.fromWei("21000000000000", ETHER), CoreMatchers.is(new BigDecimal("0.000021")));
        MatcherAssert.assertThat(Convert.fromWei("21000000000000", KETHER), CoreMatchers.is(new BigDecimal("0.000000021")));
        MatcherAssert.assertThat(Convert.fromWei("21000000000000", METHER), CoreMatchers.is(new BigDecimal("0.000000000021")));
        MatcherAssert.assertThat(Convert.fromWei("21000000000000", GETHER), CoreMatchers.is(new BigDecimal("0.000000000000021")));
    }

    @Test
    public void testToWei() {
        MatcherAssert.assertThat(Convert.toWei("21", WEI), CoreMatchers.is(new BigDecimal("21")));
        MatcherAssert.assertThat(Convert.toWei("21", KWEI), CoreMatchers.is(new BigDecimal("21000")));
        MatcherAssert.assertThat(Convert.toWei("21", MWEI), CoreMatchers.is(new BigDecimal("21000000")));
        MatcherAssert.assertThat(Convert.toWei("21", GWEI), CoreMatchers.is(new BigDecimal("21000000000")));
        MatcherAssert.assertThat(Convert.toWei("21", SZABO), CoreMatchers.is(new BigDecimal("21000000000000")));
        MatcherAssert.assertThat(Convert.toWei("21", FINNEY), CoreMatchers.is(new BigDecimal("21000000000000000")));
        MatcherAssert.assertThat(Convert.toWei("21", ETHER), CoreMatchers.is(new BigDecimal("21000000000000000000")));
        MatcherAssert.assertThat(Convert.toWei("21", KETHER), CoreMatchers.is(new BigDecimal("21000000000000000000000")));
        MatcherAssert.assertThat(Convert.toWei("21", METHER), CoreMatchers.is(new BigDecimal("21000000000000000000000000")));
        MatcherAssert.assertThat(Convert.toWei("21", GETHER), CoreMatchers.is(new BigDecimal("21000000000000000000000000000")));
    }

    @Test
    public void testUnit() {
        MatcherAssert.assertThat(Unit.fromString("ether"), CoreMatchers.is(ETHER));
        MatcherAssert.assertThat(Unit.fromString("ETHER"), CoreMatchers.is(ETHER));
        MatcherAssert.assertThat(Unit.fromString("wei"), CoreMatchers.is(WEI));
    }
}

