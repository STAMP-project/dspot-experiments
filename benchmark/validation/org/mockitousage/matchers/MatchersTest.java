/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.matchers;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.RandomAccess;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class MatchersTest extends TestBase {
    private IMethods mock = Mockito.mock(IMethods.class);

    @Test
    public void and_overloaded() {
        Mockito.when(mock.oneArg(AdditionalMatchers.and(ArgumentMatchers.eq(false), ArgumentMatchers.eq(false)))).thenReturn("0");
        Mockito.when(mock.oneArg(AdditionalMatchers.and(ArgumentMatchers.eq(((byte) (1))), ArgumentMatchers.eq(((byte) (1)))))).thenReturn("1");
        Mockito.when(mock.oneArg(AdditionalMatchers.and(ArgumentMatchers.eq('a'), ArgumentMatchers.eq('a')))).thenReturn("2");
        Mockito.when(mock.oneArg(AdditionalMatchers.and(ArgumentMatchers.eq(1.0), ArgumentMatchers.eq(1.0)))).thenReturn("3");
        Mockito.when(mock.oneArg(AdditionalMatchers.and(ArgumentMatchers.eq(1.0F), ArgumentMatchers.eq(1.0F)))).thenReturn("4");
        Mockito.when(mock.oneArg(AdditionalMatchers.and(ArgumentMatchers.eq(1), ArgumentMatchers.eq(1)))).thenReturn("5");
        Mockito.when(mock.oneArg(AdditionalMatchers.and(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(1L)))).thenReturn("6");
        Mockito.when(mock.oneArg(AdditionalMatchers.and(ArgumentMatchers.eq(((short) (1))), ArgumentMatchers.eq(((short) (1)))))).thenReturn("7");
        Mockito.when(mock.oneArg(AdditionalMatchers.and(ArgumentMatchers.contains("a"), ArgumentMatchers.contains("d")))).thenReturn("8");
        Mockito.when(mock.oneArg(AdditionalMatchers.and(ArgumentMatchers.isA(Class.class), ArgumentMatchers.eq(Object.class)))).thenReturn("9");
        Assert.assertEquals("0", mock.oneArg(false));
        Assert.assertEquals(null, mock.oneArg(true));
        Assert.assertEquals("1", mock.oneArg(((byte) (1))));
        Assert.assertEquals("2", mock.oneArg('a'));
        Assert.assertEquals("3", mock.oneArg(1.0));
        Assert.assertEquals("4", mock.oneArg(1.0F));
        Assert.assertEquals("5", mock.oneArg(1));
        Assert.assertEquals("6", mock.oneArg(1L));
        Assert.assertEquals("7", mock.oneArg(((short) (1))));
        Assert.assertEquals("8", mock.oneArg("abcde"));
        Assert.assertEquals(null, mock.oneArg("aaaaa"));
        Assert.assertEquals("9", mock.oneArg(Object.class));
    }

    @Test
    public void or_overloaded() {
        Mockito.when(mock.oneArg(AdditionalMatchers.or(ArgumentMatchers.eq(false), ArgumentMatchers.eq(true)))).thenReturn("0");
        Mockito.when(mock.oneArg(AdditionalMatchers.or(ArgumentMatchers.eq(((byte) (1))), ArgumentMatchers.eq(((byte) (2)))))).thenReturn("1");
        Mockito.when(mock.oneArg(AdditionalMatchers.or(ArgumentMatchers.eq(((char) (1))), ArgumentMatchers.eq(((char) (2)))))).thenReturn("2");
        Mockito.when(mock.oneArg(AdditionalMatchers.or(ArgumentMatchers.eq(1.0), ArgumentMatchers.eq(2.0)))).thenReturn("3");
        Mockito.when(mock.oneArg(AdditionalMatchers.or(ArgumentMatchers.eq(1.0F), ArgumentMatchers.eq(2.0F)))).thenReturn("4");
        Mockito.when(mock.oneArg(AdditionalMatchers.or(ArgumentMatchers.eq(1), ArgumentMatchers.eq(2)))).thenReturn("5");
        Mockito.when(mock.oneArg(AdditionalMatchers.or(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(2L)))).thenReturn("6");
        Mockito.when(mock.oneArg(AdditionalMatchers.or(ArgumentMatchers.eq(((short) (1))), ArgumentMatchers.eq(((short) (2)))))).thenReturn("7");
        Mockito.when(mock.oneArg(AdditionalMatchers.or(ArgumentMatchers.eq("asd"), ArgumentMatchers.eq("jkl")))).thenReturn("8");
        Mockito.when(mock.oneArg(AdditionalMatchers.or(ArgumentMatchers.eq(this.getClass()), ArgumentMatchers.eq(Object.class)))).thenReturn("9");
        Assert.assertEquals("0", mock.oneArg(true));
        Assert.assertEquals("0", mock.oneArg(false));
        Assert.assertEquals("1", mock.oneArg(((byte) (2))));
        Assert.assertEquals("2", mock.oneArg(((char) (1))));
        Assert.assertEquals("3", mock.oneArg(2.0));
        Assert.assertEquals("4", mock.oneArg(1.0F));
        Assert.assertEquals("5", mock.oneArg(2));
        Assert.assertEquals("6", mock.oneArg(1L));
        Assert.assertEquals("7", mock.oneArg(((short) (1))));
        Assert.assertEquals("8", mock.oneArg("jkl"));
        Assert.assertEquals("8", mock.oneArg("asd"));
        Assert.assertEquals(null, mock.oneArg("asdjkl"));
        Assert.assertEquals("9", mock.oneArg(Object.class));
        Assert.assertEquals(null, mock.oneArg(String.class));
    }

    @Test
    public void not_overloaded() {
        Mockito.when(mock.oneArg(AdditionalMatchers.not(ArgumentMatchers.eq(false)))).thenReturn("0");
        Mockito.when(mock.oneArg(AdditionalMatchers.not(ArgumentMatchers.eq(((byte) (1)))))).thenReturn("1");
        Mockito.when(mock.oneArg(AdditionalMatchers.not(ArgumentMatchers.eq('a')))).thenReturn("2");
        Mockito.when(mock.oneArg(AdditionalMatchers.not(ArgumentMatchers.eq(1.0)))).thenReturn("3");
        Mockito.when(mock.oneArg(AdditionalMatchers.not(ArgumentMatchers.eq(1.0F)))).thenReturn("4");
        Mockito.when(mock.oneArg(AdditionalMatchers.not(ArgumentMatchers.eq(1)))).thenReturn("5");
        Mockito.when(mock.oneArg(AdditionalMatchers.not(ArgumentMatchers.eq(1L)))).thenReturn("6");
        Mockito.when(mock.oneArg(AdditionalMatchers.not(ArgumentMatchers.eq(((short) (1)))))).thenReturn("7");
        Mockito.when(mock.oneArg(AdditionalMatchers.not(ArgumentMatchers.contains("a")))).thenReturn("8");
        Mockito.when(mock.oneArg(AdditionalMatchers.not(ArgumentMatchers.isA(Class.class)))).thenReturn("9");
        Assert.assertEquals("0", mock.oneArg(true));
        Assert.assertEquals(null, mock.oneArg(false));
        Assert.assertEquals("1", mock.oneArg(((byte) (2))));
        Assert.assertEquals("2", mock.oneArg('b'));
        Assert.assertEquals("3", mock.oneArg(2.0));
        Assert.assertEquals("4", mock.oneArg(2.0F));
        Assert.assertEquals("5", mock.oneArg(2));
        Assert.assertEquals("6", mock.oneArg(2L));
        Assert.assertEquals("7", mock.oneArg(((short) (2))));
        Assert.assertEquals("8", mock.oneArg("bcde"));
        Assert.assertEquals("9", mock.oneArg(new Object()));
        Assert.assertEquals(null, mock.oneArg(Class.class));
    }

    @Test
    public void less_or_equal_overloaded() {
        Mockito.when(mock.oneArg(AdditionalMatchers.leq(((byte) (1))))).thenReturn("1");
        Mockito.when(mock.oneArg(AdditionalMatchers.leq(1.0))).thenReturn("3");
        Mockito.when(mock.oneArg(AdditionalMatchers.leq(1.0F))).thenReturn("4");
        Mockito.when(mock.oneArg(AdditionalMatchers.leq(1))).thenReturn("5");
        Mockito.when(mock.oneArg(AdditionalMatchers.leq(1L))).thenReturn("6");
        Mockito.when(mock.oneArg(AdditionalMatchers.leq(((short) (1))))).thenReturn("7");
        Mockito.when(mock.oneArg(AdditionalMatchers.leq(new BigDecimal("1")))).thenReturn("8");
        Assert.assertEquals("1", mock.oneArg(((byte) (1))));
        Assert.assertEquals(null, mock.oneArg(((byte) (2))));
        Assert.assertEquals("3", mock.oneArg(1.0));
        Assert.assertEquals("7", mock.oneArg(((short) (0))));
        Assert.assertEquals("4", mock.oneArg((-5.0F)));
        Assert.assertEquals("5", mock.oneArg((-2)));
        Assert.assertEquals("6", mock.oneArg((-3L)));
        Assert.assertEquals("8", mock.oneArg(new BigDecimal("0.5")));
        Assert.assertEquals(null, mock.oneArg(new BigDecimal("1.1")));
    }

    @Test
    public void less_than_overloaded() {
        Mockito.when(mock.oneArg(AdditionalMatchers.lt(((byte) (1))))).thenReturn("1");
        Mockito.when(mock.oneArg(AdditionalMatchers.lt(1.0))).thenReturn("3");
        Mockito.when(mock.oneArg(AdditionalMatchers.lt(1.0F))).thenReturn("4");
        Mockito.when(mock.oneArg(AdditionalMatchers.lt(1))).thenReturn("5");
        Mockito.when(mock.oneArg(AdditionalMatchers.lt(1L))).thenReturn("6");
        Mockito.when(mock.oneArg(AdditionalMatchers.lt(((short) (1))))).thenReturn("7");
        Mockito.when(mock.oneArg(AdditionalMatchers.lt(new BigDecimal("1")))).thenReturn("8");
        Assert.assertEquals("1", mock.oneArg(((byte) (0))));
        Assert.assertEquals(null, mock.oneArg(((byte) (1))));
        Assert.assertEquals("3", mock.oneArg(0.0));
        Assert.assertEquals("7", mock.oneArg(((short) (0))));
        Assert.assertEquals("4", mock.oneArg((-4.0F)));
        Assert.assertEquals("5", mock.oneArg((-34)));
        Assert.assertEquals("6", mock.oneArg((-6L)));
        Assert.assertEquals("8", mock.oneArg(new BigDecimal("0.5")));
        Assert.assertEquals(null, mock.oneArg(new BigDecimal("23")));
    }

    @Test
    public void greater_or_equal_matcher_overloaded() {
        Mockito.when(mock.oneArg(AdditionalMatchers.geq(((byte) (1))))).thenReturn("1");
        Mockito.when(mock.oneArg(AdditionalMatchers.geq(1.0))).thenReturn("3");
        Mockito.when(mock.oneArg(AdditionalMatchers.geq(1.0F))).thenReturn("4");
        Mockito.when(mock.oneArg(AdditionalMatchers.geq(1))).thenReturn("5");
        Mockito.when(mock.oneArg(AdditionalMatchers.geq(1L))).thenReturn("6");
        Mockito.when(mock.oneArg(AdditionalMatchers.geq(((short) (1))))).thenReturn("7");
        Mockito.when(mock.oneArg(AdditionalMatchers.geq(new BigDecimal("1")))).thenReturn("8");
        Assert.assertEquals("1", mock.oneArg(((byte) (2))));
        Assert.assertEquals(null, mock.oneArg(((byte) (0))));
        Assert.assertEquals("3", mock.oneArg(1.0));
        Assert.assertEquals("7", mock.oneArg(((short) (2))));
        Assert.assertEquals("4", mock.oneArg(3.0F));
        Assert.assertEquals("5", mock.oneArg(4));
        Assert.assertEquals("6", mock.oneArg(5L));
        Assert.assertEquals("8", mock.oneArg(new BigDecimal("1.00")));
        Assert.assertEquals(null, mock.oneArg(new BigDecimal("0.9")));
    }

    @Test
    public void greater_than_matcher_overloaded() {
        Mockito.when(mock.oneArg(AdditionalMatchers.gt(((byte) (1))))).thenReturn("1");
        Mockito.when(mock.oneArg(AdditionalMatchers.gt(1.0))).thenReturn("3");
        Mockito.when(mock.oneArg(AdditionalMatchers.gt(1.0F))).thenReturn("4");
        Mockito.when(mock.oneArg(AdditionalMatchers.gt(1))).thenReturn("5");
        Mockito.when(mock.oneArg(AdditionalMatchers.gt(1L))).thenReturn("6");
        Mockito.when(mock.oneArg(AdditionalMatchers.gt(((short) (1))))).thenReturn("7");
        Mockito.when(mock.oneArg(AdditionalMatchers.gt(new BigDecimal("1")))).thenReturn("8");
        Assert.assertEquals("1", mock.oneArg(((byte) (2))));
        Assert.assertEquals(null, mock.oneArg(((byte) (1))));
        Assert.assertEquals("3", mock.oneArg(2.0));
        Assert.assertEquals("7", mock.oneArg(((short) (2))));
        Assert.assertEquals("4", mock.oneArg(3.0F));
        Assert.assertEquals("5", mock.oneArg(2));
        Assert.assertEquals("6", mock.oneArg(5L));
        Assert.assertEquals("8", mock.oneArg(new BigDecimal("1.5")));
        Assert.assertEquals(null, mock.oneArg(new BigDecimal("0.9")));
    }

    @Test
    public void compare_to_matcher() {
        Mockito.when(mock.oneArg(AdditionalMatchers.cmpEq(new BigDecimal("1.5")))).thenReturn("0");
        Assert.assertEquals("0", mock.oneArg(new BigDecimal("1.50")));
        Assert.assertEquals(null, mock.oneArg(new BigDecimal("1.51")));
    }

    @Test
    public void any_String_matcher() {
        Mockito.when(mock.oneArg(ArgumentMatchers.anyString())).thenReturn("matched");
        Assert.assertEquals("matched", mock.oneArg(""));
        Assert.assertEquals("matched", mock.oneArg("any string"));
        Assert.assertEquals(null, mock.oneArg(((String) (null))));
    }

    @Test
    public void any_matcher() {
        Mockito.when(mock.forObject(ArgumentMatchers.any())).thenReturn("matched");
        Assert.assertEquals("matched", mock.forObject(123));
        Assert.assertEquals("matched", mock.forObject("any string"));
        Assert.assertEquals("matched", mock.forObject("any string"));
        Assert.assertEquals("matched", mock.forObject(null));
    }

    @Test
    public void any_T_matcher() {
        Mockito.when(mock.oneArg(ArgumentMatchers.anyBoolean())).thenReturn("0");
        Mockito.when(mock.oneArg(ArgumentMatchers.anyByte())).thenReturn("1");
        Mockito.when(mock.oneArg(ArgumentMatchers.anyChar())).thenReturn("2");
        Mockito.when(mock.oneArg(ArgumentMatchers.anyDouble())).thenReturn("3");
        Mockito.when(mock.oneArg(ArgumentMatchers.anyFloat())).thenReturn("4");
        Mockito.when(mock.oneArg(ArgumentMatchers.anyInt())).thenReturn("5");
        Mockito.when(mock.oneArg(ArgumentMatchers.anyLong())).thenReturn("6");
        Mockito.when(mock.oneArg(ArgumentMatchers.anyShort())).thenReturn("7");
        Mockito.when(mock.oneArg(((String) (ArgumentMatchers.anyObject())))).thenReturn("8");
        Mockito.when(mock.oneArg(Mockito.<Object>anyObject())).thenReturn("9");
        Mockito.when(mock.oneArg(ArgumentMatchers.any(RandomAccess.class))).thenReturn("10");
        Assert.assertEquals("0", mock.oneArg(true));
        Assert.assertEquals("0", mock.oneArg(false));
        Assert.assertEquals("1", mock.oneArg(((byte) (1))));
        Assert.assertEquals("2", mock.oneArg(((char) (1))));
        Assert.assertEquals("3", mock.oneArg(1.0));
        Assert.assertEquals("4", mock.oneArg(889.0F));
        Assert.assertEquals("5", mock.oneArg(1));
        Assert.assertEquals("6", mock.oneArg(1L));
        Assert.assertEquals("7", mock.oneArg(((short) (1))));
        Assert.assertEquals("8", mock.oneArg("Test"));
        Assert.assertEquals("9", mock.oneArg(new Object()));
        Assert.assertEquals("9", mock.oneArg(new HashMap()));
        Assert.assertEquals("10", mock.oneArg(new ArrayList()));
    }

    @Test
    public void should_array_equals_deal_with_null_array() throws Exception {
        Object[] nullArray = null;
        Mockito.when(mock.oneArray(AdditionalMatchers.aryEq(nullArray))).thenReturn("null");
        Assert.assertEquals("null", mock.oneArray(nullArray));
        mock = Mockito.mock(IMethods.class);
        try {
            Mockito.verify(mock).oneArray(AdditionalMatchers.aryEq(nullArray));
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            assertThat(e).hasMessageContaining("oneArray(null)");
        }
    }

    @Test
    public void should_use_smart_equals_for_arrays() throws Exception {
        // issue 143
        mock.arrayMethod(new String[]{ "one" });
        Mockito.verify(mock).arrayMethod(ArgumentMatchers.eq(new String[]{ "one" }));
        Mockito.verify(mock).arrayMethod(new String[]{ "one" });
    }

    @Test
    public void should_use_smart_equals_for_primitive_arrays() throws Exception {
        // issue 143
        mock.objectArgMethod(new int[]{ 1, 2 });
        Mockito.verify(mock).objectArgMethod(ArgumentMatchers.eq(new int[]{ 1, 2 }));
        Mockito.verify(mock).objectArgMethod(new int[]{ 1, 2 });
    }

    @Test(expected = ArgumentsAreDifferent.class)
    public void array_equals_should_throw_ArgumentsAreDifferentException_for_non_matching_arguments() {
        List<Object> list = Mockito.mock(List.class);
        list.add("test");// testing fix for issue 20

        list.contains(new Object[]{ "1" });
        Mockito.verify(list).contains(new Object[]{ "1", "2", "3" });
    }

    @Test
    public void array_equals_matcher() {
        Mockito.when(mock.oneArray(AdditionalMatchers.aryEq(new boolean[]{ true, false, false }))).thenReturn("0");
        Mockito.when(mock.oneArray(AdditionalMatchers.aryEq(new byte[]{ 1 }))).thenReturn("1");
        Mockito.when(mock.oneArray(AdditionalMatchers.aryEq(new char[]{ 1 }))).thenReturn("2");
        Mockito.when(mock.oneArray(AdditionalMatchers.aryEq(new double[]{ 1 }))).thenReturn("3");
        Mockito.when(mock.oneArray(AdditionalMatchers.aryEq(new float[]{ 1 }))).thenReturn("4");
        Mockito.when(mock.oneArray(AdditionalMatchers.aryEq(new int[]{ 1 }))).thenReturn("5");
        Mockito.when(mock.oneArray(AdditionalMatchers.aryEq(new long[]{ 1 }))).thenReturn("6");
        Mockito.when(mock.oneArray(AdditionalMatchers.aryEq(new short[]{ 1 }))).thenReturn("7");
        Mockito.when(mock.oneArray(AdditionalMatchers.aryEq(new String[]{ "Test" }))).thenReturn("8");
        Mockito.when(mock.oneArray(AdditionalMatchers.aryEq(new Object[]{ "Test", new Integer(4) }))).thenReturn("9");
        Assert.assertEquals("0", mock.oneArray(new boolean[]{ true, false, false }));
        Assert.assertEquals("1", mock.oneArray(new byte[]{ 1 }));
        Assert.assertEquals("2", mock.oneArray(new char[]{ 1 }));
        Assert.assertEquals("3", mock.oneArray(new double[]{ 1 }));
        Assert.assertEquals("4", mock.oneArray(new float[]{ 1 }));
        Assert.assertEquals("5", mock.oneArray(new int[]{ 1 }));
        Assert.assertEquals("6", mock.oneArray(new long[]{ 1 }));
        Assert.assertEquals("7", mock.oneArray(new short[]{ 1 }));
        Assert.assertEquals("8", mock.oneArray(new String[]{ "Test" }));
        Assert.assertEquals("9", mock.oneArray(new Object[]{ "Test", new Integer(4) }));
        Assert.assertEquals(null, mock.oneArray(new Object[]{ "Test", new Integer(999) }));
        Assert.assertEquals(null, mock.oneArray(new Object[]{ "Test", new Integer(4), "x" }));
        Assert.assertEquals(null, mock.oneArray(new boolean[]{ true, false }));
        Assert.assertEquals(null, mock.oneArray(new boolean[]{ true, true, false }));
    }

    @Test
    public void greater_or_equal_matcher() {
        Mockito.when(mock.oneArg(AdditionalMatchers.geq(7))).thenReturn(">= 7");
        Mockito.when(mock.oneArg(AdditionalMatchers.lt(7))).thenReturn("< 7");
        Assert.assertEquals(">= 7", mock.oneArg(7));
        Assert.assertEquals(">= 7", mock.oneArg(8));
        Assert.assertEquals(">= 7", mock.oneArg(9));
        Assert.assertEquals("< 7", mock.oneArg(6));
        Assert.assertEquals("< 7", mock.oneArg(6));
    }

    @Test
    public void greater_than_matcher() {
        Mockito.when(mock.oneArg(AdditionalMatchers.gt(7))).thenReturn("> 7");
        Mockito.when(mock.oneArg(AdditionalMatchers.leq(7))).thenReturn("<= 7");
        Assert.assertEquals("> 7", mock.oneArg(8));
        Assert.assertEquals("> 7", mock.oneArg(9));
        Assert.assertEquals("> 7", mock.oneArg(10));
        Assert.assertEquals("<= 7", mock.oneArg(7));
        Assert.assertEquals("<= 7", mock.oneArg(6));
    }

    @Test
    public void less_or_equal_matcher() {
        Mockito.when(mock.oneArg(AdditionalMatchers.leq(7))).thenReturn("<= 7");
        Mockito.when(mock.oneArg(AdditionalMatchers.gt(7))).thenReturn("> 7");
        Assert.assertEquals("<= 7", mock.oneArg(7));
        Assert.assertEquals("<= 7", mock.oneArg(6));
        Assert.assertEquals("<= 7", mock.oneArg(5));
        Assert.assertEquals("> 7", mock.oneArg(8));
        Assert.assertEquals("> 7", mock.oneArg(9));
    }

    @Test
    public void less_than_matcher() {
        Mockito.when(mock.oneArg(AdditionalMatchers.lt(7))).thenReturn("< 7");
        Mockito.when(mock.oneArg(AdditionalMatchers.geq(7))).thenReturn(">= 7");
        Assert.assertEquals("< 7", mock.oneArg(5));
        Assert.assertEquals("< 7", mock.oneArg(6));
        Assert.assertEquals("< 7", mock.oneArg(4));
        Assert.assertEquals(">= 7", mock.oneArg(7));
        Assert.assertEquals(">= 7", mock.oneArg(8));
    }

    @Test
    public void or_matcher() {
        Mockito.when(mock.oneArg(ArgumentMatchers.anyInt())).thenReturn("other");
        Mockito.when(mock.oneArg(AdditionalMatchers.or(ArgumentMatchers.eq(7), ArgumentMatchers.eq(9)))).thenReturn("7 or 9");
        Assert.assertEquals("other", mock.oneArg(10));
        Assert.assertEquals("7 or 9", mock.oneArg(7));
        Assert.assertEquals("7 or 9", mock.oneArg(9));
    }

    @Test
    public void null_matcher() {
        Mockito.when(mock.threeArgumentMethod(ArgumentMatchers.eq(1), ArgumentMatchers.isNull(), ArgumentMatchers.eq(""))).thenReturn("1");
        Mockito.when(mock.threeArgumentMethod(ArgumentMatchers.eq(1), AdditionalMatchers.not(ArgumentMatchers.isNull()), ArgumentMatchers.eq(""))).thenReturn("2");
        Assert.assertEquals("1", mock.threeArgumentMethod(1, null, ""));
        Assert.assertEquals("2", mock.threeArgumentMethod(1, new Object(), ""));
    }

    @Test
    public void null_matcher_for_primitive_wrappers() {
        Mockito.when(mock.forBoolean(ArgumentMatchers.isNull(Boolean.class))).thenReturn("ok");
        Mockito.when(mock.forInteger(ArgumentMatchers.isNull(Integer.class))).thenReturn("ok");
        Mockito.when(mock.forLong(ArgumentMatchers.isNull(Long.class))).thenReturn("ok");
        Mockito.when(mock.forByte(ArgumentMatchers.isNull(Byte.class))).thenReturn("ok");
        Mockito.when(mock.forShort(ArgumentMatchers.isNull(Short.class))).thenReturn("ok");
        Mockito.when(mock.forCharacter(ArgumentMatchers.isNull(Character.class))).thenReturn("ok");
        Mockito.when(mock.forDouble(ArgumentMatchers.isNull(Double.class))).thenReturn("ok");
        Mockito.when(mock.forFloat(ArgumentMatchers.isNull(Float.class))).thenReturn("ok");
        Assert.assertEquals("ok", mock.forBoolean(null));
        Assert.assertEquals("ok", mock.forInteger(null));
        Assert.assertEquals("ok", mock.forLong(null));
        Assert.assertEquals("ok", mock.forByte(null));
        Assert.assertEquals("ok", mock.forShort(null));
        Assert.assertEquals("ok", mock.forCharacter(null));
        Assert.assertEquals("ok", mock.forDouble(null));
        Assert.assertEquals("ok", mock.forFloat(null));
    }

    @Test
    public void not_null_matcher() {
        Mockito.when(mock.threeArgumentMethod(ArgumentMatchers.eq(1), ArgumentMatchers.notNull(), ArgumentMatchers.eq(""))).thenReturn("1");
        Mockito.when(mock.threeArgumentMethod(ArgumentMatchers.eq(1), AdditionalMatchers.not(ArgumentMatchers.isNotNull()), ArgumentMatchers.eq(""))).thenReturn("2");
        Assert.assertEquals("1", mock.threeArgumentMethod(1, new Object(), ""));
        Assert.assertEquals("2", mock.threeArgumentMethod(1, null, ""));
    }

    @Test
    public void find_matcher() {
        Mockito.when(mock.oneArg(AdditionalMatchers.find("([a-z]+)\\d"))).thenReturn("1");
        Assert.assertEquals("1", mock.oneArg("ab12"));
        Assert.assertEquals(null, mock.oneArg("12345"));
        Assert.assertEquals(null, mock.oneArg(((Object) (null))));
    }

    @Test
    public void matches_matcher() {
        Mockito.when(mock.oneArg(ArgumentMatchers.matches("[a-z]+\\d\\d"))).thenReturn("1");
        Mockito.when(mock.oneArg(ArgumentMatchers.matches("\\d\\d\\d"))).thenReturn("2");
        Assert.assertEquals("1", mock.oneArg("a12"));
        Assert.assertEquals("2", mock.oneArg("131"));
        Assert.assertEquals(null, mock.oneArg("blah"));
    }

    @Test
    public void matches_Pattern_matcher() {
        Mockito.when(mock.oneArg(ArgumentMatchers.matches(Pattern.compile("[a-z]+\\d\\d")))).thenReturn("1");
        Mockito.when(mock.oneArg(ArgumentMatchers.matches(Pattern.compile("\\d\\d\\d")))).thenReturn("2");
        Assert.assertEquals("1", mock.oneArg("a12"));
        Assert.assertEquals("2", mock.oneArg("131"));
        Assert.assertEquals(null, mock.oneArg("blah"));
    }

    @Test
    public void contains_matcher() {
        Mockito.when(mock.oneArg(ArgumentMatchers.contains("ell"))).thenReturn("1");
        Mockito.when(mock.oneArg(ArgumentMatchers.contains("ld"))).thenReturn("2");
        Assert.assertEquals("1", mock.oneArg("hello"));
        Assert.assertEquals("2", mock.oneArg("world"));
        Assert.assertEquals(null, mock.oneArg("xlx"));
    }

    @Test
    public void starts_with_matcher() {
        Mockito.when(mock.oneArg(ArgumentMatchers.startsWith("ab"))).thenReturn("1");
        Mockito.when(mock.oneArg(ArgumentMatchers.startsWith("bc"))).thenReturn("2");
        Assert.assertEquals("1", mock.oneArg("ab quake"));
        Assert.assertEquals("2", mock.oneArg("bc quake"));
        Assert.assertEquals(null, mock.oneArg("ba quake"));
    }

    @Test
    public void ends_with_matcher() {
        Mockito.when(mock.oneArg(ArgumentMatchers.endsWith("ab"))).thenReturn("1");
        Mockito.when(mock.oneArg(ArgumentMatchers.endsWith("bc"))).thenReturn("2");
        Assert.assertEquals("1", mock.oneArg("xab"));
        Assert.assertEquals("2", mock.oneArg("xbc"));
        Assert.assertEquals(null, mock.oneArg("ac"));
    }

    @Test
    public void delta_matcher() {
        Mockito.when(mock.oneArg(AdditionalMatchers.eq(1.0, 0.1))).thenReturn("1");
        Mockito.when(mock.oneArg(AdditionalMatchers.eq(2.0, 0.1))).thenReturn("2");
        Mockito.when(mock.oneArg(AdditionalMatchers.eq(1.0F, 0.1F))).thenReturn("3");
        Mockito.when(mock.oneArg(AdditionalMatchers.eq(2.0F, 0.1F))).thenReturn("4");
        Mockito.when(mock.oneArg(AdditionalMatchers.eq(2.0F, 0.1F))).thenReturn("4");
        Assert.assertEquals("1", mock.oneArg(1.0));
        Assert.assertEquals("1", mock.oneArg(0.91));
        Assert.assertEquals("1", mock.oneArg(1.09));
        Assert.assertEquals("2", mock.oneArg(2.0));
        Assert.assertEquals("3", mock.oneArg(1.0F));
        Assert.assertEquals("3", mock.oneArg(0.91F));
        Assert.assertEquals("3", mock.oneArg(1.09F));
        Assert.assertEquals("4", mock.oneArg(2.1F));
        Assert.assertEquals(null, mock.oneArg(2.2F));
    }

    @Test
    public void delta_matcher_prints_itself() {
        try {
            Mockito.verify(mock).oneArg(AdditionalMatchers.eq(1.0, 0.1));
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            assertThat(e).hasMessageContaining("eq(1.0, 0.1)");
        }
    }

    @Test
    public void same_matcher() {
        Object one = new String("1243");
        Object two = new String("1243");
        Object three = new String("1243");
        Assert.assertNotSame(one, two);
        Assert.assertEquals(one, two);
        Assert.assertEquals(two, three);
        Mockito.when(mock.oneArg(ArgumentMatchers.same(one))).thenReturn("1");
        Mockito.when(mock.oneArg(ArgumentMatchers.same(two))).thenReturn("2");
        Assert.assertEquals("1", mock.oneArg(one));
        Assert.assertEquals("2", mock.oneArg(two));
        Assert.assertEquals(null, mock.oneArg(three));
    }

    @Test
    public void eq_matcher_and_nulls() {
        mock.simpleMethod(((Object) (null)));
        Mockito.verify(mock).simpleMethod(Mockito.<Object>eq(null));
    }

    @Test
    public void same_matcher_and_nulls() {
        mock.simpleMethod(((Object) (null)));
        Mockito.verify(mock).simpleMethod(Mockito.<Object>same(null));
    }

    @Test
    public void nullable_matcher() throws Exception {
        // imagine a Stream.of(...).map(c -> mock.oneArg(c))...
        mock.oneArg(((Character) (null)));
        mock.oneArg(Character.valueOf('?'));
        Mockito.verify(mock, Mockito.times(2)).oneArg(ArgumentMatchers.nullable(Character.class));
    }
}

