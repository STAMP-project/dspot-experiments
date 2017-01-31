/**
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.squareup.javapoet;


public class AmplUtilTest {
    @org.junit.Test
    public void characterLiteral() {
        org.junit.Assert.assertEquals("a", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a'));
        org.junit.Assert.assertEquals("b", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b'));
        org.junit.Assert.assertEquals("c", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c'));
        org.junit.Assert.assertEquals("%", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%'));
        // common escapes
        org.junit.Assert.assertEquals("\\b", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b'));
        org.junit.Assert.assertEquals("\\t", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t'));
        org.junit.Assert.assertEquals("\\n", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n'));
        org.junit.Assert.assertEquals("\\f", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f'));
        org.junit.Assert.assertEquals("\\r", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r'));
        org.junit.Assert.assertEquals("\"", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"'));
        org.junit.Assert.assertEquals("\\\'", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\''));
        org.junit.Assert.assertEquals("\\\\", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\'));
        // octal escapes
        org.junit.Assert.assertEquals("\\u0000", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes(' '));
        org.junit.Assert.assertEquals("\\u0007", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes(''));
        org.junit.Assert.assertEquals("?", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?'));
        org.junit.Assert.assertEquals("\\u007f", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes(''));
        org.junit.Assert.assertEquals("¿", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf'));
        org.junit.Assert.assertEquals("ÿ", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff'));
        // unicode escapes
        org.junit.Assert.assertEquals("\\u0000", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes(' '));
        org.junit.Assert.assertEquals("\\u0001", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes(''));
        org.junit.Assert.assertEquals("\\u0002", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes(''));
        org.junit.Assert.assertEquals("€", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac'));
        org.junit.Assert.assertEquals("☃", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603'));
        org.junit.Assert.assertEquals("♠", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660'));
        org.junit.Assert.assertEquals("♣", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663'));
        org.junit.Assert.assertEquals("♥", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665'));
        org.junit.Assert.assertEquals("♦", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666'));
        org.junit.Assert.assertEquals("✵", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735'));
        org.junit.Assert.assertEquals("✺", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a'));
        org.junit.Assert.assertEquals("／", com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f'));
    }

    @org.junit.Test
    public void stringLiteral() {
        stringLiteral("abc");
        stringLiteral("♦♥♠♣");
        stringLiteral("\u20ac\\t@\\t$", "\u20ac\t@\t$", " ");
        stringLiteral("abc();\\n\"\n  + \"def();", "abc();\ndef();", " ");
        stringLiteral("This is \\\"quoted\\\"!", "This is \"quoted\"!", " ");
        stringLiteral("e^{i\\\\pi}+1=0", "e^{i\\pi}+1=0", " ");
    }

    void stringLiteral(java.lang.String string) {
        stringLiteral(string, string, " ");
    }

    void stringLiteral(java.lang.String expected, java.lang.String value, java.lang.String indent) {
        org.junit.Assert.assertEquals((("\"" + expected) + "\""), com.squareup.javapoet.Util.stringLiteralWithDoubleQuotes(value, indent));
    }

    /* amplification of com.squareup.javapoet.UtilTest#characterLiteral */
    @org.junit.Test(timeout = 1000)
    public void characterLiteral_cf165_cf3339_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a');
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b');
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c');
            // MethodAssertGenerator build local variable
            Object o_7_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%');
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b');
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t');
            // MethodAssertGenerator build local variable
            Object o_14_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n');
            // MethodAssertGenerator build local variable
            Object o_16_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f');
            // MethodAssertGenerator build local variable
            Object o_18_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r');
            // MethodAssertGenerator build local variable
            Object o_20_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"');
            // MethodAssertGenerator build local variable
            Object o_22_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\'');
            // MethodAssertGenerator build local variable
            Object o_24_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\');
            // MethodAssertGenerator build local variable
            Object o_26_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes(' ');
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('');
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?');
            // MethodAssertGenerator build local variable
            Object o_33_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('');
            // MethodAssertGenerator build local variable
            Object o_35_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf');
            // MethodAssertGenerator build local variable
            Object o_37_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff');
            // MethodAssertGenerator build local variable
            Object o_39_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes(' ');
            // MethodAssertGenerator build local variable
            Object o_42_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('');
            // MethodAssertGenerator build local variable
            Object o_44_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('');
            // MethodAssertGenerator build local variable
            Object o_46_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac');
            // MethodAssertGenerator build local variable
            Object o_48_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603');
            // MethodAssertGenerator build local variable
            Object o_50_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660');
            // MethodAssertGenerator build local variable
            Object o_52_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663');
            // MethodAssertGenerator build local variable
            Object o_54_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665');
            // MethodAssertGenerator build local variable
            Object o_56_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666');
            // MethodAssertGenerator build local variable
            Object o_58_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735');
            // MethodAssertGenerator build local variable
            Object o_60_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a');
            // StatementAdderOnAssert create null value
            java.lang.Object[] vc_57 = (java.lang.Object[])null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_57);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_56 = new java.lang.String();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_56, "");
            // StatementAdderOnAssert create random local variable
            boolean vc_54 = true;
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(vc_54);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_52 = (com.squareup.javapoet.Util)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_52);
            // StatementAdderMethod cloned existing statement
            vc_52.checkState(vc_54, vc_56, vc_57);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_52);
            // StatementAdderOnAssert create random local variable
            java.lang.Object[] vc_513 = new java.lang.Object []{new java.lang.Object(),new java.lang.Object(),new java.lang.Object(),new java.lang.Object()};
            // StatementAdderOnAssert create null value
            java.lang.String vc_510 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            boolean vc_509 = false;
            // StatementAdderMethod cloned existing statement
            vc_52.checkState(vc_509, vc_510, vc_513);
            // MethodAssertGenerator build local variable
            Object o_90_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f');
            org.junit.Assert.fail("characterLiteral_cf165_cf3339 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.UtilTest#characterLiteral */
    @org.junit.Test(timeout = 1000)
    public void characterLiteral_cf165_cf3177_cf11623_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('a');
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('b');
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('c');
            // MethodAssertGenerator build local variable
            Object o_7_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('%');
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\b');
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\t');
            // MethodAssertGenerator build local variable
            Object o_14_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\n');
            // MethodAssertGenerator build local variable
            Object o_16_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\f');
            // MethodAssertGenerator build local variable
            Object o_18_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\r');
            // MethodAssertGenerator build local variable
            Object o_20_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('"');
            // MethodAssertGenerator build local variable
            Object o_22_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\'');
            // MethodAssertGenerator build local variable
            Object o_24_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\\');
            // MethodAssertGenerator build local variable
            Object o_26_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes(' ');
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('');
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('?');
            // MethodAssertGenerator build local variable
            Object o_33_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('');
            // MethodAssertGenerator build local variable
            Object o_35_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00bf');
            // MethodAssertGenerator build local variable
            Object o_37_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u00ff');
            // MethodAssertGenerator build local variable
            Object o_39_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes(' ');
            // MethodAssertGenerator build local variable
            Object o_42_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('');
            // MethodAssertGenerator build local variable
            Object o_44_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('');
            // MethodAssertGenerator build local variable
            Object o_46_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u20ac');
            // MethodAssertGenerator build local variable
            Object o_48_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2603');
            // MethodAssertGenerator build local variable
            Object o_50_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2660');
            // MethodAssertGenerator build local variable
            Object o_52_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2663');
            // MethodAssertGenerator build local variable
            Object o_54_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2665');
            // MethodAssertGenerator build local variable
            Object o_56_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2666');
            // MethodAssertGenerator build local variable
            Object o_58_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u2735');
            // MethodAssertGenerator build local variable
            Object o_60_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\u273a');
            // StatementAdderOnAssert create null value
            java.lang.Object[] vc_57 = (java.lang.Object[])null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_57);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_57);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_56 = new java.lang.String();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_56, "");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_56, "");
            // StatementAdderOnAssert create random local variable
            boolean vc_54 = true;
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(vc_54);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(vc_54);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.Util vc_52 = (com.squareup.javapoet.Util)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_52);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_52);
            // StatementAdderMethod cloned existing statement
            vc_52.checkState(vc_54, vc_56, vc_57);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_52);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_52);
            // StatementAdderOnAssert create random local variable
            java.lang.Object[] vc_506 = new java.lang.Object []{new java.lang.Object()};
            // StatementAdderMethod cloned existing statement
            vc_52.checkArgument(vc_54, vc_56, vc_506);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_52);
            // StatementAdderOnAssert create null value
            java.lang.String vc_1485 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            boolean vc_1484 = false;
            // StatementAdderMethod cloned existing statement
            vc_52.checkState(vc_1484, vc_1485, vc_506);
            // MethodAssertGenerator build local variable
            Object o_104_0 = com.squareup.javapoet.Util.characterLiteralWithoutSingleQuotes('\uff0f');
            org.junit.Assert.fail("characterLiteral_cf165_cf3177_cf11623 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

