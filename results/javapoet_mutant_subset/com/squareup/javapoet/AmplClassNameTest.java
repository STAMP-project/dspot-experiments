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
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.bestGuess(com.squareup.javapoet.AmplClassNameTest.OuterClass.InnerClass.class.getCanonicalName())).isEqualTo(com.squareup.javapoet.ClassName.get("com.squareup.javapoet", "ClassNameTest", "OuterClass", "InnerClass"));
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
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(com.squareup.javapoet.AmplClassNameTest.OuterClass.InnerClass.class).toString()).isEqualTo("com.squareup.javapoet.ClassNameTest.OuterClass.InnerClass");
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
}

