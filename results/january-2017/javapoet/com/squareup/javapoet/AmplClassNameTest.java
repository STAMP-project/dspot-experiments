/**
 * Copyright (C) 2014 Google, Inc.
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


@org.junit.runner.RunWith(value = org.junit.runners.JUnit4.class)
public final class AmplClassNameTest {
    @org.junit.Rule
    public com.google.testing.compile.CompilationRule compilationRule = new com.google.testing.compile.CompilationRule();

    @org.junit.Test
    public void bestGuessForString_simpleClass() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess(java.lang.String.class.getName())).isEqualTo(com.squareup.javapoet.ClassName.get("java.lang", "String"));
    }

    @org.junit.Test
    public void bestGuessNonAscii() {
        com.squareup.javapoet.ClassName className = com.squareup.javapoet.ClassName.bestGuess("com.\ud835\udc1andro\ud835\udc22d.\ud835\udc00ctiv\ud835\udc22ty");
        org.junit.Assert.assertEquals("com.\ud835\udc1andro\ud835\udc22d", className.packageName());
        org.junit.Assert.assertEquals("\ud835\udc00ctiv\ud835\udc22ty", className.simpleName());
    }

    static class OuterClass {
        static class InnerClass {        }
    }

    @org.junit.Test
    public void bestGuessForString_nestedClass() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess(java.util.Map.Entry.class.getCanonicalName())).isEqualTo(com.squareup.javapoet.ClassName.get("java.util", "Map", "Entry"));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess(com.squareup.javapoet.ClassNameTest.OuterClass.InnerClass.class.getCanonicalName())).isEqualTo(com.squareup.javapoet.ClassName.get("com.squareup.javapoet", "ClassNameTest", "OuterClass", "InnerClass"));
    }

    @org.junit.Test
    public void bestGuessForString_defaultPackage() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess("SomeClass")).isEqualTo(com.squareup.javapoet.ClassName.get("", "SomeClass"));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess("SomeClass.Nested")).isEqualTo(com.squareup.javapoet.ClassName.get("", "SomeClass", "Nested"));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess("SomeClass.Nested.EvenMore")).isEqualTo(com.squareup.javapoet.ClassName.get("", "SomeClass", "Nested", "EvenMore"));
    }

    @org.junit.Test
    public void bestGuessForString_confusingInput() {
        assertBestGuessThrows("");
        assertBestGuessThrows(".");
        assertBestGuessThrows(".Map");
        assertBestGuessThrows("java");
        assertBestGuessThrows("java.util");
        assertBestGuessThrows("java.util.");
        assertBestGuessThrows("java..util.Map.Entry");
        assertBestGuessThrows("java.util..Map.Entry");
        assertBestGuessThrows("java.util.Map..Entry");
        assertBestGuessThrows("com.test.$");
        assertBestGuessThrows("com.test.LooksLikeAClass.pkg");
        assertBestGuessThrows("!@#$gibberish%^&*");
    }

    private void assertBestGuessThrows(java.lang.String s) {
        try {
            com.squareup.javapoet.ClassName.bestGuess(s);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
        }
    }

    @org.junit.Test
    public void createNestedClass() {
        com.squareup.javapoet.ClassName foo = com.squareup.javapoet.ClassName.get("com.example", "Foo");
        com.squareup.javapoet.ClassName bar = foo.nestedClass("Bar");
        com.google.common.truth.Truth.assertThat(bar).isEqualTo(com.squareup.javapoet.ClassName.get("com.example", "Foo", "Bar"));
        com.squareup.javapoet.ClassName baz = bar.nestedClass("Baz");
        com.google.common.truth.Truth.assertThat(baz).isEqualTo(com.squareup.javapoet.ClassName.get("com.example", "Foo", "Bar", "Baz"));
    }

    @org.junit.Test
    public void classNameFromTypeElement() {
        javax.lang.model.util.Elements elements = compilationRule.getElements();
        javax.lang.model.element.TypeElement element = elements.getTypeElement(java.lang.Object.class.getCanonicalName());
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(element).toString()).isEqualTo("java.lang.Object");
    }

    @org.junit.Test
    public void classNameFromClass() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(java.lang.Object.class).toString()).isEqualTo("java.lang.Object");
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(com.squareup.javapoet.ClassNameTest.OuterClass.InnerClass.class).toString()).isEqualTo("com.squareup.javapoet.ClassNameTest.OuterClass.InnerClass");
    }

    @org.junit.Test
    public void peerClass() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(java.lang.Double.class).peerClass("Short")).isEqualTo(com.squareup.javapoet.ClassName.get(java.lang.Short.class));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("", "Double").peerClass("Short")).isEqualTo(com.squareup.javapoet.ClassName.get("", "Short"));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("a.b", "Combo", "Taco").peerClass("Burrito")).isEqualTo(com.squareup.javapoet.ClassName.get("a.b", "Combo", "Burrito"));
    }

    @org.junit.Test
    public void fromClassRejectionTypes() {
        try {
            com.squareup.javapoet.ClassName.get(int.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException ignored) {
        }
        try {
            com.squareup.javapoet.ClassName.get(void.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException ignored) {
        }
        try {
            com.squareup.javapoet.ClassName.get(java.lang.Object[].class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException ignored) {
        }
    }

    @org.junit.Test
    public void reflectionName() {
        org.junit.Assert.assertEquals("java.lang.Object", com.squareup.javapoet.TypeName.OBJECT.reflectionName());
        org.junit.Assert.assertEquals("java.lang.Thread$State", com.squareup.javapoet.ClassName.get(java.lang.Thread.State.class).reflectionName());
        org.junit.Assert.assertEquals("java.util.Map$Entry", com.squareup.javapoet.ClassName.get(java.util.Map.Entry.class).reflectionName());
        org.junit.Assert.assertEquals("Foo", com.squareup.javapoet.ClassName.get("", "Foo").reflectionName());
        org.junit.Assert.assertEquals("Foo$Bar$Baz", com.squareup.javapoet.ClassName.get("", "Foo", "Bar", "Baz").reflectionName());
        org.junit.Assert.assertEquals("a.b.c.Foo$Bar$Baz", com.squareup.javapoet.ClassName.get("a.b.c", "Foo", "Bar", "Baz").reflectionName());
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#bestGuessNonAscii */
    @org.junit.Test(timeout = 1000)
    public void bestGuessNonAscii_cf112_failAssert27() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.ClassName className = com.squareup.javapoet.ClassName.bestGuess("com.\ud835\udc1andro\ud835\udc22d.\ud835\udc00ctiv\ud835\udc22ty");
            // MethodAssertGenerator build local variable
            Object o_3_0 = className.packageName();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_27 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            className.nestedClass(vc_27);
            // MethodAssertGenerator build local variable
            Object o_9_0 = className.simpleName();
            org.junit.Assert.fail("bestGuessNonAscii_cf112 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#bestGuessNonAscii */
    @org.junit.Test(timeout = 1000)
    public void bestGuessNonAscii_cf166_cf782_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.ClassName className = com.squareup.javapoet.ClassName.bestGuess("com.\ud835\udc1andro\ud835\udc22d.\ud835\udc00ctiv\ud835\udc22ty");
            // MethodAssertGenerator build local variable
            Object o_3_0 = className.packageName();
            // AssertGenerator replace invocation
            java.lang.String o_bestGuessNonAscii_cf166__5 = // StatementAdderMethod cloned existing statement
className.simpleName();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_bestGuessNonAscii_cf166__5, "𝐀ctiv𝐢ty");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_231 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            className.peerClass(vc_231);
            // MethodAssertGenerator build local variable
            Object o_13_0 = className.simpleName();
            org.junit.Assert.fail("bestGuessNonAscii_cf166_cf782 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#bestGuessNonAscii */
    @org.junit.Test(timeout = 1000)
    public void bestGuessNonAscii_cf162_cf661_literalMutation832_failAssert35() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.ClassName className = com.squareup.javapoet.ClassName.bestGuess("URllB^!%Y\\i_E");
            // MethodAssertGenerator build local variable
            Object o_3_0 = className.packageName();
            // AssertGenerator replace invocation
            java.lang.String o_bestGuessNonAscii_cf162__5 = // StatementAdderMethod cloned existing statement
className.reflectionName();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_bestGuessNonAscii_cf162__5, "com.𝐚ndro𝐢d.𝐀ctiv𝐢ty");
            // AssertGenerator replace invocation
            java.lang.String o_bestGuessNonAscii_cf162_cf661__9 = // StatementAdderMethod cloned existing statement
className.reflectionName();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_bestGuessNonAscii_cf162_cf661__9, "com.𝐚ndro𝐢d.𝐀ctiv𝐢ty");
            // MethodAssertGenerator build local variable
            Object o_13_0 = className.simpleName();
            org.junit.Assert.fail("bestGuessNonAscii_cf162_cf661_literalMutation832 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#reflectionName */
    @org.junit.Test(timeout = 1000)
    public void reflectionName_cf39303_failAssert30() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.TypeName.OBJECT.reflectionName();
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.ClassName.get(java.lang.Thread.State.class).reflectionName();
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.squareup.javapoet.ClassName.get(java.util.Map.Entry.class).reflectionName();
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.ClassName.get("", "Foo").reflectionName();
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.ClassName.get("", "Foo", "Bar", "Baz").reflectionName();
            // StatementAdderOnAssert create null value
            java.util.List<com.squareup.javapoet.AnnotationSpec> vc_13452 = (java.util.List)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.ClassName vc_13450 = (com.squareup.javapoet.ClassName)null;
            // StatementAdderMethod cloned existing statement
            vc_13450.annotated(vc_13452);
            // MethodAssertGenerator build local variable
            Object o_21_0 = com.squareup.javapoet.ClassName.get("a.b.c", "Foo", "Bar", "Baz").reflectionName();
            org.junit.Assert.fail("reflectionName_cf39303 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#reflectionName */
    @org.junit.Test(timeout = 1000)
    public void reflectionName_cf39303_failAssert30_add39365() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.TypeName.OBJECT.reflectionName();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_1_0, "java.lang.Object");
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.ClassName.get(java.lang.Thread.State.class).reflectionName();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_3_0, "java.lang.Thread$State");
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.squareup.javapoet.ClassName.get(java.util.Map.Entry.class).reflectionName();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_6_0, "java.util.Map$Entry");
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.ClassName.get("", "Foo").reflectionName();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_9_0, "Foo");
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.ClassName.get("", "Foo", "Bar", "Baz").reflectionName();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_12_0, "Foo$Bar$Baz");
            // StatementAdderOnAssert create null value
            java.util.List<com.squareup.javapoet.AnnotationSpec> vc_13452 = (java.util.List)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_13452);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.ClassName vc_13450 = (com.squareup.javapoet.ClassName)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_13450);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_13450.annotated(vc_13452);
            // StatementAdderMethod cloned existing statement
            vc_13450.annotated(vc_13452);
            // MethodAssertGenerator build local variable
            Object o_21_0 = com.squareup.javapoet.ClassName.get("a.b.c", "Foo", "Bar", "Baz").reflectionName();
            org.junit.Assert.fail("reflectionName_cf39303 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#reflectionName */
    @org.junit.Test(timeout = 1000)
    public void reflectionName_cf39342_failAssert40_literalMutation39382_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_1_0 = com.squareup.javapoet.TypeName.OBJECT.reflectionName();
                // MethodAssertGenerator build local variable
                Object o_3_0 = com.squareup.javapoet.ClassName.get(java.lang.Thread.State.class).reflectionName();
                // MethodAssertGenerator build local variable
                Object o_6_0 = com.squareup.javapoet.ClassName.get(java.util.Map.Entry.class).reflectionName();
                // MethodAssertGenerator build local variable
                Object o_9_0 = com.squareup.javapoet.ClassName.get("", "Foo").reflectionName();
                // MethodAssertGenerator build local variable
                Object o_12_0 = com.squareup.javapoet.ClassName.get("", "Foo", "Bar", "UP?-eRo7mbR5V=$1LS%(\\D").reflectionName();
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_191 = "java.lang.Thread$State";
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.ClassName vc_13478 = (com.squareup.javapoet.ClassName)null;
                // StatementAdderMethod cloned existing statement
                vc_13478.peerClass(String_vc_191);
                // MethodAssertGenerator build local variable
                Object o_21_0 = com.squareup.javapoet.ClassName.get("a.b.c", "Foo", "Bar", "Baz").reflectionName();
                org.junit.Assert.fail("reflectionName_cf39342 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("reflectionName_cf39342_failAssert40_literalMutation39382 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#reflectionName */
    @org.junit.Test(timeout = 1000)
    public void reflectionName_cf39303_failAssert30_add39365_add39390() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.TypeName.OBJECT.reflectionName();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_1_0, "java.lang.Object");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_1_0, "java.lang.Object");
            // MethodAssertGenerator build local variable
            Object o_3_0 = com.squareup.javapoet.ClassName.get(java.lang.Thread.State.class).reflectionName();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_3_0, "java.lang.Thread$State");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_3_0, "java.lang.Thread$State");
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.squareup.javapoet.ClassName.get(java.util.Map.Entry.class).reflectionName();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_6_0, "java.util.Map$Entry");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_6_0, "java.util.Map$Entry");
            // MethodAssertGenerator build local variable
            Object o_9_0 = com.squareup.javapoet.ClassName.get("", "Foo").reflectionName();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_9_0, "Foo");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_9_0, "Foo");
            // MethodAssertGenerator build local variable
            Object o_12_0 = com.squareup.javapoet.ClassName.get("", "Foo", "Bar", "Baz").reflectionName();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_12_0, "Foo$Bar$Baz");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_12_0, "Foo$Bar$Baz");
            // StatementAdderOnAssert create null value
            java.util.List<com.squareup.javapoet.AnnotationSpec> vc_13452 = (java.util.List)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_13452);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_13452);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.ClassName vc_13450 = (com.squareup.javapoet.ClassName)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_13450);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_13450);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_13450.annotated(vc_13452);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_13450.annotated(vc_13452);
            // StatementAdderMethod cloned existing statement
            vc_13450.annotated(vc_13452);
            // MethodAssertGenerator build local variable
            Object o_21_0 = com.squareup.javapoet.ClassName.get("a.b.c", "Foo", "Bar", "Baz").reflectionName();
            org.junit.Assert.fail("reflectionName_cf39303 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ClassNameTest#reflectionName */
    @org.junit.Test(timeout = 1000)
    public void reflectionName_cf39342_failAssert40_literalMutation39379_failAssert3_add39431() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_1_0 = com.squareup.javapoet.TypeName.OBJECT.reflectionName();
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(o_1_0, "java.lang.Object");
                // MethodAssertGenerator build local variable
                Object o_3_0 = com.squareup.javapoet.ClassName.get(java.lang.Thread.State.class).reflectionName();
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(o_3_0, "java.lang.Thread$State");
                // MethodAssertGenerator build local variable
                Object o_6_0 = com.squareup.javapoet.ClassName.get(java.util.Map.Entry.class).reflectionName();
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(o_6_0, "java.util.Map$Entry");
                // MethodAssertGenerator build local variable
                Object o_9_0 = com.squareup.javapoet.ClassName.get("", "Foo").reflectionName();
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(o_9_0, "Foo");
                // MethodAssertGenerator build local variable
                Object o_12_0 = com.squareup.javapoet.ClassName.get("", "Foo", "Bar", "java.lang\\.Thread$State").reflectionName();
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_191 = "java.lang.Thread$State";
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.ClassName vc_13478 = (com.squareup.javapoet.ClassName)null;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_13478.peerClass(String_vc_191);
                // StatementAdderMethod cloned existing statement
                vc_13478.peerClass(String_vc_191);
                // MethodAssertGenerator build local variable
                Object o_21_0 = com.squareup.javapoet.ClassName.get("a.b.c", "Foo", "Bar", "Baz").reflectionName();
                org.junit.Assert.fail("reflectionName_cf39342 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("reflectionName_cf39342_failAssert40_literalMutation39379 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

