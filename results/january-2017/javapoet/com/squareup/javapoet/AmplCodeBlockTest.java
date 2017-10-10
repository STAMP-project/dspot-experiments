/**
 * Copyright (C) 2015 Square, Inc.
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


public final class AmplCodeBlockTest {
    @org.junit.Test
    public void equalsAndHashCode() {
        com.squareup.javapoet.CodeBlock a = com.squareup.javapoet.CodeBlock.builder().build();
        com.squareup.javapoet.CodeBlock b = com.squareup.javapoet.CodeBlock.builder().build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.CodeBlock.builder().add("$L", "taco").build();
        b = com.squareup.javapoet.CodeBlock.builder().add("$L", "taco").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @org.junit.Test
    public void of() {
        com.squareup.javapoet.CodeBlock a = com.squareup.javapoet.CodeBlock.of("$L taco", "delicious");
        com.google.common.truth.Truth.assertThat(a.toString()).isEqualTo("delicious taco");
    }

    @org.junit.Test
    public void indentCannotBeIndexed() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$1>", "taco").build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException exp) {
            com.google.common.truth.Truth.assertThat(exp).hasMessage("$$, $>, $<, $[, $], and $W may not have an index");
        }
    }

    @org.junit.Test
    public void deindentCannotBeIndexed() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$1<", "taco").build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException exp) {
            com.google.common.truth.Truth.assertThat(exp).hasMessage("$$, $>, $<, $[, $], and $W may not have an index");
        }
    }

    @org.junit.Test
    public void dollarSignEscapeCannotBeIndexed() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$1$", "taco").build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException exp) {
            com.google.common.truth.Truth.assertThat(exp).hasMessage("$$, $>, $<, $[, $], and $W may not have an index");
        }
    }

    @org.junit.Test
    public void statementBeginningCannotBeIndexed() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$1[", "taco").build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException exp) {
            com.google.common.truth.Truth.assertThat(exp).hasMessage("$$, $>, $<, $[, $], and $W may not have an index");
        }
    }

    @org.junit.Test
    public void statementEndingCannotBeIndexed() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$1]", "taco").build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException exp) {
            com.google.common.truth.Truth.assertThat(exp).hasMessage("$$, $>, $<, $[, $], and $W may not have an index");
        }
    }

    @org.junit.Test
    public void nameFormatCanBeIndexed() {
        com.squareup.javapoet.CodeBlock block = com.squareup.javapoet.CodeBlock.builder().add("$1N", "taco").build();
        com.google.common.truth.Truth.assertThat(block.toString()).isEqualTo("taco");
    }

    @org.junit.Test
    public void literalFormatCanBeIndexed() {
        com.squareup.javapoet.CodeBlock block = com.squareup.javapoet.CodeBlock.builder().add("$1L", "taco").build();
        com.google.common.truth.Truth.assertThat(block.toString()).isEqualTo("taco");
    }

    @org.junit.Test
    public void stringFormatCanBeIndexed() {
        com.squareup.javapoet.CodeBlock block = com.squareup.javapoet.CodeBlock.builder().add("$1S", "taco").build();
        com.google.common.truth.Truth.assertThat(block.toString()).isEqualTo("\"taco\"");
    }

    @org.junit.Test
    public void typeFormatCanBeIndexed() {
        com.squareup.javapoet.CodeBlock block = com.squareup.javapoet.CodeBlock.builder().add("$1T", java.lang.String.class).build();
        com.google.common.truth.Truth.assertThat(block.toString()).isEqualTo("java.lang.String");
    }

    @org.junit.Test
    public void simpleNamedArgument() {
        java.util.Map<java.lang.String, java.lang.Object> map = new java.util.LinkedHashMap<>();
        map.put("text", "taco");
        com.squareup.javapoet.CodeBlock block = com.squareup.javapoet.CodeBlock.builder().addNamed("$text:S", map).build();
        com.google.common.truth.Truth.assertThat(block.toString()).isEqualTo("\"taco\"");
    }

    @org.junit.Test
    public void repeatedNamedArgument() {
        java.util.Map<java.lang.String, java.lang.Object> map = new java.util.LinkedHashMap<>();
        map.put("text", "tacos");
        com.squareup.javapoet.CodeBlock block = com.squareup.javapoet.CodeBlock.builder().addNamed("\"I like \" + $text:S + \". Do you like \" + $text:S + \"?\"", map).build();
        com.google.common.truth.Truth.assertThat(block.toString()).isEqualTo("\"I like \" + \"tacos\" + \". Do you like \" + \"tacos\" + \"?\"");
    }

    @org.junit.Test
    public void namedAndNoArgFormat() {
        java.util.Map<java.lang.String, java.lang.Object> map = new java.util.LinkedHashMap<>();
        map.put("text", "tacos");
        com.squareup.javapoet.CodeBlock block = com.squareup.javapoet.CodeBlock.builder().addNamed("$>\n$text:L for $$3.50", map).build();
        com.google.common.truth.Truth.assertThat(block.toString()).isEqualTo("\n  tacos for $3.50");
    }

    @org.junit.Test
    public void missingNamedArgument() {
        try {
            java.util.Map<java.lang.String, java.lang.Object> map = new java.util.LinkedHashMap<>();
            com.squareup.javapoet.CodeBlock.builder().addNamed("$text:S", map).build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("Missing named argument for $text");
        }
    }

    @org.junit.Test
    public void lowerCaseNamed() {
        try {
            java.util.Map<java.lang.String, java.lang.Object> map = new java.util.LinkedHashMap<>();
            map.put("Text", "tacos");
            com.squareup.javapoet.CodeBlock block = com.squareup.javapoet.CodeBlock.builder().addNamed("$Text:S", map).build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("argument 'Text' must start with a lowercase character");
        }
    }

    @org.junit.Test
    public void multipleNamedArguments() {
        java.util.Map<java.lang.String, java.lang.Object> map = new java.util.LinkedHashMap<>();
        map.put("pipe", java.lang.System.class);
        map.put("text", "tacos");
        com.squareup.javapoet.CodeBlock block = com.squareup.javapoet.CodeBlock.builder().addNamed("$pipe:T.out.println(\"Let\'s eat some $text:L\");", map).build();
        com.google.common.truth.Truth.assertThat(block.toString()).isEqualTo("java.lang.System.out.println(\"Let\'s eat some tacos\");");
    }

    @org.junit.Test
    public void namedNewline() {
        java.util.Map<java.lang.String, java.lang.Object> map = new java.util.LinkedHashMap<>();
        map.put("clazz", java.lang.Integer.class);
        com.squareup.javapoet.CodeBlock block = com.squareup.javapoet.CodeBlock.builder().addNamed("$clazz:T\n", map).build();
        com.google.common.truth.Truth.assertThat(block.toString()).isEqualTo("java.lang.Integer\n");
    }

    @org.junit.Test
    public void danglingNamed() {
        java.util.Map<java.lang.String, java.lang.Object> map = new java.util.LinkedHashMap<>();
        map.put("clazz", java.lang.Integer.class);
        try {
            com.squareup.javapoet.CodeBlock.builder().addNamed("$clazz:T$", map).build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("dangling $ at end");
        }
    }

    @org.junit.Test
    public void indexTooHigh() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$2T", java.lang.String.class).build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("index 2 for '$2T' not in range (received 1 arguments)");
        }
    }

    @org.junit.Test
    public void indexIsZero() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$0T", java.lang.String.class).build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("index 0 for '$0T' not in range (received 1 arguments)");
        }
    }

    @org.junit.Test
    public void indexIsNegative() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$-1T", java.lang.String.class).build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("invalid format string: '$-1T'");
        }
    }

    @org.junit.Test
    public void indexWithoutFormatType() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$1", java.lang.String.class).build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("dangling format characters in '$1'");
        }
    }

    @org.junit.Test
    public void indexWithoutFormatTypeNotAtStringEnd() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$1 taco", java.lang.String.class).build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("invalid format string: '$1 taco'");
        }
    }

    @org.junit.Test
    public void indexButNoArguments() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$1T").build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("index 1 for '$1T' not in range (received 0 arguments)");
        }
    }

    @org.junit.Test
    public void formatIndicatorAlone() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$", java.lang.String.class).build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("dangling format characters in '$'");
        }
    }

    @org.junit.Test
    public void formatIndicatorWithoutIndexOrFormatType() {
        try {
            com.squareup.javapoet.CodeBlock.builder().add("$ tacoString", java.lang.String.class).build();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("invalid format string: '$ tacoString'");
        }
    }

    @org.junit.Test
    public void sameIndexCanBeUsedWithDifferentFormats() {
        com.squareup.javapoet.CodeBlock block = com.squareup.javapoet.CodeBlock.builder().add("$1T.out.println($1S)", com.squareup.javapoet.ClassName.get(java.lang.System.class)).build();
        com.google.common.truth.Truth.assertThat(block.toString()).isEqualTo("java.lang.System.out.println(\"java.lang.System\")");
    }

    @org.junit.Test
    public void tooManyStatementEnters() {
        com.squareup.javapoet.CodeBlock codeBlock = com.squareup.javapoet.CodeBlock.builder().add("$[$[").build();
        try {
            // We can't report this error until rendering type because code blocks might be composed.
            codeBlock.toString();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("statement enter $[ followed by statement enter $[");
        }
    }

    @org.junit.Test
    public void statementExitWithoutStatementEnter() {
        com.squareup.javapoet.CodeBlock codeBlock = com.squareup.javapoet.CodeBlock.builder().add("$]").build();
        try {
            // We can't report this error until rendering type because code blocks might be composed.
            codeBlock.toString();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("statement exit $] has no matching statement enter $[");
        }
    }

    /* amplification of com.squareup.javapoet.CodeBlockTest#indexButNoArguments */
    @org.junit.Test(timeout = 1000)
    public void indexButNoArguments_cf86012_literalMutation86680_failAssert2_literalMutation87747() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            try {
                // AssertGenerator replace invocation
                com.squareup.javapoet.CodeBlock o_indexButNoArguments_cf86012_literalMutation86680_failAssert2_literalMutation87747__5 = com.squareup.javapoet.CodeBlock.builder().add("o|R").build();
                // AssertGenerator add assertion
                junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)o_indexButNoArguments_cf86012_literalMutation86680_failAssert2_literalMutation87747__5).isEmpty());
                // AssertGenerator add assertion
                junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)((com.squareup.javapoet.CodeBlock.Builder)((com.squareup.javapoet.CodeBlock)o_indexButNoArguments_cf86012_literalMutation86680_failAssert2_literalMutation87747__5).toBuilder()).build()).isEmpty());
                // StatementAdderOnAssert create null value
                java.lang.Object[] vc_23790 = (java.lang.Object[])null;
                // AssertGenerator add assertion
                junit.framework.Assert.assertNull(vc_23790);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_1189 = "$1T";
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(String_vc_1189, "$1T");
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.CodeBlock vc_23786 = (com.squareup.javapoet.CodeBlock)null;
                // AssertGenerator add assertion
                junit.framework.Assert.assertNull(vc_23786);
                // StatementAdderMethod cloned existing statement
                vc_23786.of(String_vc_1189, vc_23790);
                org.junit.Assert.fail();
            } catch (java.lang.IllegalArgumentException expected) {
                com.google.common.truth.Truth.assertThat(expected).hasMessage("index 1 for '$1T' not in range (received 0 arguments)");
            }
            org.junit.Assert.fail("indexButNoArguments_cf86012_literalMutation86680 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.CodeBlockTest#missingNamedArgument */
    @org.junit.Test(timeout = 1000)
    public void missingNamedArgument_cf171237_literalMutation172242_failAssert0_cf174157() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            try {
                java.util.Map<java.lang.String, java.lang.Object> map = new java.util.LinkedHashMap<>();
                // AssertGenerator replace invocation
                com.squareup.javapoet.CodeBlock o_missingNamedArgument_cf171237_literalMutation172242_failAssert0_cf174157__7 = com.squareup.javapoet.CodeBlock.builder().addNamed("0n%}7TtDn`z+z/suL*:=`&X]?/<]Farx", map).build();
                // AssertGenerator add assertion
                junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)o_missingNamedArgument_cf171237_literalMutation172242_failAssert0_cf174157__7).isEmpty());
                // AssertGenerator add assertion
                junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)((com.squareup.javapoet.CodeBlock.Builder)((com.squareup.javapoet.CodeBlock)o_missingNamedArgument_cf171237_literalMutation172242_failAssert0_cf174157__7).toBuilder()).build()).isEmpty());
                // StatementAdderOnAssert create null value
                java.lang.Object[] vc_47390 = (java.lang.Object[])null;
                // AssertGenerator add assertion
                junit.framework.Assert.assertNull(vc_47390);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_2369 = "Missing named argument for $text";
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(String_vc_2369, "Missing named argument for $text");
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.CodeBlock vc_47386 = (com.squareup.javapoet.CodeBlock)null;
                // AssertGenerator add assertion
                junit.framework.Assert.assertNull(vc_47386);
                // StatementAdderMethod cloned existing statement
                vc_47386.of(String_vc_2369, vc_47390);
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.CodeBlock vc_48224 = (com.squareup.javapoet.CodeBlock)null;
                // StatementAdderMethod cloned existing statement
                vc_48224.isEmpty();
                org.junit.Assert.fail();
            } catch (java.lang.IllegalArgumentException expected) {
                com.google.common.truth.Truth.assertThat(expected).hasMessage("Missing named argument for $text");
            }
            org.junit.Assert.fail("missingNamedArgument_cf171237_literalMutation172242 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.CodeBlockTest#statementExitWithoutStatementEnter */
    @org.junit.Test(timeout = 1000)
    public void statementExitWithoutStatementEnter_cf207673_literalMutation209008() {
        com.squareup.javapoet.CodeBlock codeBlock = com.squareup.javapoet.CodeBlock.builder().add("statement exit $] has no maftching statement enter $[").build();
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)((com.squareup.javapoet.CodeBlock.Builder)((com.squareup.javapoet.CodeBlock)codeBlock).toBuilder()).build()).isEmpty());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)codeBlock).isEmpty());
        try {
            // We can't report this error until rendering type because code blocks might be composed.
            codeBlock.toString();
            // StatementAdderOnAssert create null value
            java.lang.Object[] vc_57410 = (java.lang.Object[])null;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_2870 = "statement exit $] has no matching statement enter $[";
            // StatementAdderMethod cloned existing statement
            codeBlock.of(String_vc_2870, vc_57410);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
            com.google.common.truth.Truth.assertThat(expected).hasMessage("statement exit $] has no matching statement enter $[");
        }
    }

    /* amplification of com.squareup.javapoet.CodeBlockTest#statementExitWithoutStatementEnter */
    @org.junit.Test(timeout = 1000)
    public void statementExitWithoutStatementEnter_cf207673_literalMutation209008_add210302() {
        com.squareup.javapoet.CodeBlock codeBlock = com.squareup.javapoet.CodeBlock.builder().add("statement exit $] has no maftching statement enter $[").build();
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)((com.squareup.javapoet.CodeBlock.Builder)((com.squareup.javapoet.CodeBlock)codeBlock).toBuilder()).build()).isEmpty());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)codeBlock).isEmpty());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)((com.squareup.javapoet.CodeBlock.Builder)((com.squareup.javapoet.CodeBlock)codeBlock).toBuilder()).build()).isEmpty());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)codeBlock).isEmpty());
        try {
            // We can't report this error until rendering type because code blocks might be composed.
            codeBlock.toString();
            // StatementAdderOnAssert create null value
            java.lang.Object[] vc_57410 = (java.lang.Object[])null;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_2870 = "statement exit $] has no matching statement enter $[";
            // StatementAdderMethod cloned existing statement
            codeBlock.of(String_vc_2870, vc_57410);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
            // MethodCallAdder
            com.google.common.truth.Truth.assertThat(expected).hasMessage("statement exit $] has no matching statement enter $[");
            com.google.common.truth.Truth.assertThat(expected).hasMessage("statement exit $] has no matching statement enter $[");
        }
    }

    /* amplification of com.squareup.javapoet.CodeBlockTest#statementExitWithoutStatementEnter */
    @org.junit.Test(timeout = 1000)
    public void statementExitWithoutStatementEnter_cf207667_literalMutation208602_literalMutation210231_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.CodeBlock codeBlock = com.squareup.javapoet.CodeBlock.builder().add("8:QyPX#fC{!g1IB;Q T1uud+DJ-)JFr?]:-j9ETd]:Fv8RI]BCbK").build();
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)((com.squareup.javapoet.CodeBlock.Builder)((com.squareup.javapoet.CodeBlock)codeBlock).toBuilder()).build()).isEmpty());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)codeBlock).isEmpty());
            try {
                // We can't report this error until rendering type because code blocks might be composed.
                codeBlock.toString();
                // StatementAdderOnAssert create null value
                java.lang.Object[] vc_57410 = (java.lang.Object[])null;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_2870 = "statement exit $] has no matching statement enter $[";
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.CodeBlock vc_57406 = (com.squareup.javapoet.CodeBlock)null;
                // StatementAdderMethod cloned existing statement
                vc_57406.of(String_vc_2870, vc_57410);
                org.junit.Assert.fail();
            } catch (java.lang.IllegalStateException expected) {
                com.google.common.truth.Truth.assertThat(expected).hasMessage("statement exit $] has no matching statement enter $[");
            }
            org.junit.Assert.fail("statementExitWithoutStatementEnter_cf207667_literalMutation208602_literalMutation210231 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.CodeBlockTest#tooManyStatementEnters */
    @org.junit.Test(timeout = 1000)
    public void tooManyStatementEnters_cf210468_literalMutation211801_failAssert0_cf212983() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.CodeBlock codeBlock = com.squareup.javapoet.CodeBlock.builder().add("s4-y").build();
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)((com.squareup.javapoet.CodeBlock.Builder)((com.squareup.javapoet.CodeBlock)codeBlock).toBuilder()).build()).isEmpty());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.CodeBlock)codeBlock).isEmpty());
            try {
                // We can't report this error until rendering type because code blocks might be composed.
                codeBlock.toString();
                // StatementAdderOnAssert create null value
                java.lang.Object[] vc_58330 = (java.lang.Object[])null;
                // AssertGenerator add assertion
                junit.framework.Assert.assertNull(vc_58330);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_2916 = "$[$[";
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(String_vc_2916, "$[$[");
                // StatementAdderMethod cloned existing statement
                codeBlock.of(String_vc_2916, vc_58330);
                // StatementAdderMethod cloned existing statement
                codeBlock.of(String_vc_2916, vc_58330);
                org.junit.Assert.fail();
            } catch (java.lang.IllegalStateException expected) {
                com.google.common.truth.Truth.assertThat(expected).hasMessage("statement enter $[ followed by statement enter $[");
            }
            org.junit.Assert.fail("tooManyStatementEnters_cf210468_literalMutation211801 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

