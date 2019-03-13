package com.vip.vjtools.vjkit.base;


import com.vip.vjtools.vjkit.collection.ListUtil;
import org.junit.Test;


public class EnumUtilTest {
    public enum Options {

        A,
        B,
        C,
        D;}

    @Test
    public void testBits() {
        assertThat(EnumUtil.generateBits(EnumUtilTest.Options.class, EnumUtilTest.Options.A)).isEqualTo(1);
        assertThat(EnumUtil.generateBits(EnumUtilTest.Options.class, EnumUtilTest.Options.A, EnumUtilTest.Options.B)).isEqualTo(3);
        assertThat(EnumUtil.generateBits(EnumUtilTest.Options.class, ListUtil.newArrayList(EnumUtilTest.Options.A))).isEqualTo(1);
        assertThat(EnumUtil.generateBits(EnumUtilTest.Options.class, ListUtil.newArrayList(EnumUtilTest.Options.A, EnumUtilTest.Options.B))).isEqualTo(3);
        assertThat(EnumUtil.processBits(EnumUtilTest.Options.class, 3)).hasSize(2).containsExactly(EnumUtilTest.Options.A, EnumUtilTest.Options.B);
        assertThat(EnumUtil.processBits(EnumUtilTest.Options.class, EnumUtil.generateBits(EnumUtilTest.Options.class, EnumUtilTest.Options.A, EnumUtilTest.Options.C, EnumUtilTest.Options.D))).hasSize(3).containsExactly(EnumUtilTest.Options.A, EnumUtilTest.Options.C, EnumUtilTest.Options.D);
    }

    @Test
    public void testString() {
        assertThat(EnumUtil.toString(EnumUtilTest.Options.A)).isEqualTo("A");
        assertThat(EnumUtil.fromString(EnumUtilTest.Options.class, "B")).isEqualTo(EnumUtilTest.Options.B);
    }
}

