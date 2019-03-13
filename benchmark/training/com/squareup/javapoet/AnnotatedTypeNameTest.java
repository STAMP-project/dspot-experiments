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


import TypeName.BOOLEAN;
import TypeName.DOUBLE;
import TypeName.VOID;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class AnnotatedTypeNameTest {
    private static final String NN = AnnotatedTypeNameTest.NeverNull.class.getCanonicalName();

    private final AnnotationSpec NEVER_NULL = AnnotationSpec.builder(AnnotatedTypeNameTest.NeverNull.class).build();

    private static final String TUA = AnnotatedTypeNameTest.TypeUseAnnotation.class.getCanonicalName();

    private final AnnotationSpec TYPE_USE_ANNOTATION = AnnotationSpec.builder(AnnotatedTypeNameTest.TypeUseAnnotation.class).build();

    @Target(ElementType.TYPE_USE)
    public @interface NeverNull {}

    @Target(ElementType.TYPE_USE)
    public @interface TypeUseAnnotation {}

    @Test(expected = NullPointerException.class)
    public void nullAnnotationArray() {
        BOOLEAN.annotated(((AnnotationSpec[]) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void nullAnnotationList() {
        DOUBLE.annotated(((List<AnnotationSpec>) (null)));
    }

    @Test
    public void annotated() {
        TypeName simpleString = TypeName.get(String.class);
        Assert.assertFalse(simpleString.isAnnotated());
        Assert.assertEquals(simpleString, TypeName.get(String.class));
        TypeName annotated = simpleString.annotated(NEVER_NULL);
        Assert.assertTrue(annotated.isAnnotated());
        Assert.assertEquals(annotated, annotated.annotated());
    }

    @Test
    public void annotatedType() {
        TypeName type = TypeName.get(String.class);
        TypeName actual = type.annotated(TYPE_USE_ANNOTATION);
        assertThat(actual.toString()).isEqualTo((("java.lang. @" + (AnnotatedTypeNameTest.TUA)) + " String"));
    }

    @Test
    public void annotatedTwice() {
        TypeName type = TypeName.get(String.class);
        TypeName actual = type.annotated(NEVER_NULL).annotated(TYPE_USE_ANNOTATION);
        assertThat(actual.toString()).isEqualTo((((("java.lang. @" + (AnnotatedTypeNameTest.NN)) + " @") + (AnnotatedTypeNameTest.TUA)) + " String"));
    }

    @Test
    public void annotatedParameterizedType() {
        TypeName type = ParameterizedTypeName.get(List.class, String.class);
        TypeName actual = type.annotated(TYPE_USE_ANNOTATION);
        assertThat(actual.toString()).isEqualTo((("java.util. @" + (AnnotatedTypeNameTest.TUA)) + " List<java.lang.String>"));
    }

    @Test
    public void annotatedArgumentOfParameterizedType() {
        TypeName type = TypeName.get(String.class).annotated(TYPE_USE_ANNOTATION);
        TypeName actual = ParameterizedTypeName.get(ClassName.get(List.class), type);
        assertThat(actual.toString()).isEqualTo((("java.util.List<java.lang. @" + (AnnotatedTypeNameTest.TUA)) + " String>"));
    }

    @Test
    public void annotatedWildcardTypeNameWithSuper() {
        TypeName type = TypeName.get(String.class).annotated(TYPE_USE_ANNOTATION);
        TypeName actual = WildcardTypeName.supertypeOf(type);
        assertThat(actual.toString()).isEqualTo((("? super java.lang. @" + (AnnotatedTypeNameTest.TUA)) + " String"));
    }

    @Test
    public void annotatedWildcardTypeNameWithExtends() {
        TypeName type = TypeName.get(String.class).annotated(TYPE_USE_ANNOTATION);
        TypeName actual = WildcardTypeName.subtypeOf(type);
        assertThat(actual.toString()).isEqualTo((("? extends java.lang. @" + (AnnotatedTypeNameTest.TUA)) + " String"));
    }

    @Test
    public void annotatedEquivalence() {
        annotatedEquivalence(VOID);
        annotatedEquivalence(ArrayTypeName.get(Object[].class));
        annotatedEquivalence(ClassName.get(Object.class));
        annotatedEquivalence(ParameterizedTypeName.get(List.class, Object.class));
        annotatedEquivalence(TypeVariableName.get(Object.class));
        annotatedEquivalence(WildcardTypeName.get(Object.class));
    }

    // https://github.com/square/javapoet/issues/431
    @Test
    public void annotatedNestedType() {
        TypeName type = TypeName.get(Map.Entry.class).annotated(TYPE_USE_ANNOTATION);
        assertThat(type.toString()).isEqualTo((("java.util.Map. @" + (AnnotatedTypeNameTest.TUA)) + " Entry"));
    }

    @Test
    public void annotatedEnclosingAndNestedType() {
        TypeName type = nestedClass("Entry").annotated(TYPE_USE_ANNOTATION);
        assertThat(type.toString()).isEqualTo((((("java.util. @" + (AnnotatedTypeNameTest.TUA)) + " Map. @") + (AnnotatedTypeNameTest.TUA)) + " Entry"));
    }

    // https://github.com/square/javapoet/issues/431
    @Test
    public void annotatedNestedParameterizedType() {
        TypeName type = ParameterizedTypeName.get(Map.Entry.class, Byte.class, Byte.class).annotated(TYPE_USE_ANNOTATION);
        assertThat(type.toString()).isEqualTo((("java.util.Map. @" + (AnnotatedTypeNameTest.TUA)) + " Entry<java.lang.Byte, java.lang.Byte>"));
    }

    @Test
    public void withoutAnnotationsOnAnnotatedEnclosingAndNestedType() {
        TypeName type = nestedClass("Entry").annotated(TYPE_USE_ANNOTATION);
        assertThat(type.isAnnotated()).isTrue();
        assertThat(type.withoutAnnotations()).isEqualTo(TypeName.get(Map.Entry.class));
    }

    @Test
    public void withoutAnnotationsOnAnnotatedEnclosingType() {
        TypeName type = ((ClassName) (TypeName.get(Map.class).annotated(TYPE_USE_ANNOTATION))).nestedClass("Entry");
        assertThat(type.isAnnotated()).isTrue();
        assertThat(type.withoutAnnotations()).isEqualTo(TypeName.get(Map.Entry.class));
    }

    @Test
    public void withoutAnnotationsOnAnnotatedNestedType() {
        TypeName type = nestedClass("Entry").annotated(TYPE_USE_ANNOTATION);
        assertThat(type.isAnnotated()).isTrue();
        assertThat(type.withoutAnnotations()).isEqualTo(TypeName.get(Map.Entry.class));
    }

    // https://github.com/square/javapoet/issues/614
    @Test
    public void annotatedArrayType() {
        TypeName type = ArrayTypeName.of(ClassName.get(Object.class)).annotated(TYPE_USE_ANNOTATION);
        assertThat(type.toString()).isEqualTo((("java.lang.Object @" + (AnnotatedTypeNameTest.TUA)) + " []"));
    }

    @Test
    public void annotatedArrayElementType() {
        TypeName type = ArrayTypeName.of(ClassName.get(Object.class).annotated(TYPE_USE_ANNOTATION));
        assertThat(type.toString()).isEqualTo((("java.lang. @" + (AnnotatedTypeNameTest.TUA)) + " Object[]"));
    }

    // https://github.com/square/javapoet/issues/614
    @Test
    public void annotatedOuterMultidimensionalArrayType() {
        TypeName type = ArrayTypeName.of(ArrayTypeName.of(ClassName.get(Object.class))).annotated(TYPE_USE_ANNOTATION);
        assertThat(type.toString()).isEqualTo((("java.lang.Object @" + (AnnotatedTypeNameTest.TUA)) + " [][]"));
    }

    // https://github.com/square/javapoet/issues/614
    @Test
    public void annotatedInnerMultidimensionalArrayType() {
        TypeName type = ArrayTypeName.of(ArrayTypeName.of(ClassName.get(Object.class)).annotated(TYPE_USE_ANNOTATION));
        assertThat(type.toString()).isEqualTo((("java.lang.Object[] @" + (AnnotatedTypeNameTest.TUA)) + " []"));
    }

    // https://github.com/square/javapoet/issues/614
    @Test
    public void annotatedArrayTypeVarargsParameter() {
        TypeName type = ArrayTypeName.of(ArrayTypeName.of(ClassName.get(Object.class))).annotated(TYPE_USE_ANNOTATION);
        MethodSpec varargsMethod = MethodSpec.methodBuilder("m").addParameter(ParameterSpec.builder(type, "p").build()).varargs().build();
        assertThat(varargsMethod.toString()).isEqualTo((((("" + "void m(java.lang.Object @") + (AnnotatedTypeNameTest.TUA)) + " []... p) {\n") + "}\n"));
    }

    // https://github.com/square/javapoet/issues/614
    @Test
    public void annotatedArrayTypeInVarargsParameter() {
        TypeName type = ArrayTypeName.of(ArrayTypeName.of(ClassName.get(Object.class)).annotated(TYPE_USE_ANNOTATION));
        MethodSpec varargsMethod = MethodSpec.methodBuilder("m").addParameter(ParameterSpec.builder(type, "p").build()).varargs().build();
        assertThat(varargsMethod.toString()).isEqualTo((((("" + "void m(java.lang.Object[] @") + (AnnotatedTypeNameTest.TUA)) + " ... p) {\n") + "}\n"));
    }
}

