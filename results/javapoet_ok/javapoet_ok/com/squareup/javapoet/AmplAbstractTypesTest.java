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


public abstract class AmplAbstractTypesTest {
    protected abstract javax.lang.model.util.Elements getElements();

    protected abstract javax.lang.model.util.Types getTypes();

    private javax.lang.model.element.TypeElement getElement(java.lang.Class<?> clazz) {
        return getElements().getTypeElement(clazz.getCanonicalName());
    }

    private javax.lang.model.type.TypeMirror getMirror(java.lang.Class<?> clazz) {
        return getElement(clazz).asType();
    }

    @org.junit.Test
    public void getBasicTypeMirror() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(getMirror(java.lang.Object.class))).isEqualTo(com.squareup.javapoet.ClassName.get(java.lang.Object.class));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(getMirror(java.nio.charset.Charset.class))).isEqualTo(com.squareup.javapoet.ClassName.get(java.nio.charset.Charset.class));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(getMirror(com.squareup.javapoet.AmplAbstractTypesTest.class))).isEqualTo(com.squareup.javapoet.ClassName.get(com.squareup.javapoet.AmplAbstractTypesTest.class));
    }

    @org.junit.Test
    public void getParameterizedTypeMirror() {
        javax.lang.model.type.DeclaredType setType = getTypes().getDeclaredType(getElement(java.util.Set.class), getMirror(java.lang.Object.class));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(setType)).isEqualTo(com.squareup.javapoet.ParameterizedTypeName.get(com.squareup.javapoet.ClassName.get(java.util.Set.class), com.squareup.javapoet.ClassName.OBJECT));
    }

    @org.junit.Test
    public void getErrorType() {
        javax.lang.model.type.ErrorType errorType = new com.squareup.javapoet.AmplAbstractTypesTest.DeclaredTypeAsErrorType(getTypes().getDeclaredType(getElement(java.util.Set.class)));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(errorType)).isEqualTo(com.squareup.javapoet.ClassName.get(java.util.Set.class));
    }

    static class Parameterized<Simple, ExtendsClass extends java.lang.Number, ExtendsInterface extends java.lang.Runnable, ExtendsTypeVariable extends Simple, Intersection extends java.lang.Number & java.lang.Runnable, IntersectionOfInterfaces extends java.lang.Runnable & java.io.Serializable> {    }

    @org.junit.Test
    public void getTypeVariableTypeMirror() {
        java.util.List<? extends javax.lang.model.element.TypeParameterElement> typeVariables = getElement(com.squareup.javapoet.AmplAbstractTypesTest.Parameterized.class).getTypeParameters();
        // Members of converted types use ClassName and not Class<?>.
        com.squareup.javapoet.ClassName number = com.squareup.javapoet.ClassName.get(java.lang.Number.class);
        com.squareup.javapoet.ClassName runnable = com.squareup.javapoet.ClassName.get(java.lang.Runnable.class);
        com.squareup.javapoet.ClassName serializable = com.squareup.javapoet.ClassName.get(java.io.Serializable.class);
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(typeVariables.get(0).asType())).isEqualTo(com.squareup.javapoet.TypeVariableName.get("Simple"));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(typeVariables.get(1).asType())).isEqualTo(com.squareup.javapoet.TypeVariableName.get("ExtendsClass", number));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(typeVariables.get(2).asType())).isEqualTo(com.squareup.javapoet.TypeVariableName.get("ExtendsInterface", runnable));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(typeVariables.get(3).asType())).isEqualTo(com.squareup.javapoet.TypeVariableName.get("ExtendsTypeVariable", com.squareup.javapoet.TypeVariableName.get("Simple")));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(typeVariables.get(4).asType())).isEqualTo(com.squareup.javapoet.TypeVariableName.get("Intersection", number, runnable));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(typeVariables.get(5).asType())).isEqualTo(com.squareup.javapoet.TypeVariableName.get("IntersectionOfInterfaces", runnable, serializable));
        com.google.common.truth.Truth.assertThat(((com.squareup.javapoet.TypeVariableName) (com.squareup.javapoet.TypeName.get(typeVariables.get(4).asType()))).bounds).containsExactly(number, runnable);
    }

    static class Recursive<T extends java.util.Map<java.util.List<T>, java.util.Set<T[]>>> {    }

    @org.junit.Test
    public void getTypeVariableTypeMirrorRecursive() {
        javax.lang.model.type.TypeMirror typeMirror = getElement(com.squareup.javapoet.AmplAbstractTypesTest.Recursive.class).asType();
        com.squareup.javapoet.ParameterizedTypeName typeName = ((com.squareup.javapoet.ParameterizedTypeName) (com.squareup.javapoet.TypeName.get(typeMirror)));
        java.lang.String className = com.squareup.javapoet.AmplAbstractTypesTest.Recursive.class.getCanonicalName();
        com.google.common.truth.Truth.assertThat(typeName.toString()).isEqualTo((className + "<T>"));
        com.squareup.javapoet.TypeVariableName typeVariableName = ((com.squareup.javapoet.TypeVariableName) (typeName.typeArguments.get(0)));
        try {
            typeVariableName.bounds.set(0, null);
            org.junit.Assert.fail("Expected UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException expected) {
        }
        com.google.common.truth.Truth.assertThat(typeVariableName.toString()).isEqualTo("T");
        com.google.common.truth.Truth.assertThat(typeVariableName.bounds.toString()).isEqualTo("[java.util.Map<java.util.List<T>, java.util.Set<T[]>>]");
    }

    @org.junit.Test
    public void getPrimitiveTypeMirror() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(getTypes().getPrimitiveType(javax.lang.model.type.TypeKind.BOOLEAN))).isEqualTo(com.squareup.javapoet.TypeName.BOOLEAN);
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(getTypes().getPrimitiveType(javax.lang.model.type.TypeKind.BYTE))).isEqualTo(com.squareup.javapoet.TypeName.BYTE);
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(getTypes().getPrimitiveType(javax.lang.model.type.TypeKind.SHORT))).isEqualTo(com.squareup.javapoet.TypeName.SHORT);
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(getTypes().getPrimitiveType(javax.lang.model.type.TypeKind.INT))).isEqualTo(com.squareup.javapoet.TypeName.INT);
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(getTypes().getPrimitiveType(javax.lang.model.type.TypeKind.LONG))).isEqualTo(com.squareup.javapoet.TypeName.LONG);
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(getTypes().getPrimitiveType(javax.lang.model.type.TypeKind.CHAR))).isEqualTo(com.squareup.javapoet.TypeName.CHAR);
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(getTypes().getPrimitiveType(javax.lang.model.type.TypeKind.FLOAT))).isEqualTo(com.squareup.javapoet.TypeName.FLOAT);
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(getTypes().getPrimitiveType(javax.lang.model.type.TypeKind.DOUBLE))).isEqualTo(com.squareup.javapoet.TypeName.DOUBLE);
    }

    @org.junit.Test
    public void getArrayTypeMirror() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(getTypes().getArrayType(getMirror(java.lang.Object.class)))).isEqualTo(com.squareup.javapoet.ArrayTypeName.of(com.squareup.javapoet.ClassName.OBJECT));
    }

    @org.junit.Test
    public void getVoidTypeMirror() {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.get(getTypes().getNoType(javax.lang.model.type.TypeKind.VOID))).isEqualTo(com.squareup.javapoet.TypeName.VOID);
    }

    @org.junit.Test
    public void getNullTypeMirror() {
        try {
            com.squareup.javapoet.TypeName.get(getTypes().getNullType());
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
        }
    }

    @org.junit.Test
    public void parameterizedType() throws java.lang.Exception {
        com.squareup.javapoet.ParameterizedTypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.Map.class, java.lang.String.class, java.lang.Long.class);
        com.google.common.truth.Truth.assertThat(type.toString()).isEqualTo("java.util.Map<java.lang.String, java.lang.Long>");
    }

    @org.junit.Test
    public void arrayType() throws java.lang.Exception {
        com.squareup.javapoet.ArrayTypeName type = com.squareup.javapoet.ArrayTypeName.of(java.lang.String.class);
        com.google.common.truth.Truth.assertThat(type.toString()).isEqualTo("java.lang.String[]");
    }

    @org.junit.Test
    public void wildcardExtendsType() throws java.lang.Exception {
        com.squareup.javapoet.WildcardTypeName type = com.squareup.javapoet.WildcardTypeName.subtypeOf(java.lang.CharSequence.class);
        com.google.common.truth.Truth.assertThat(type.toString()).isEqualTo("? extends java.lang.CharSequence");
    }

    @org.junit.Test
    public void wildcardExtendsObject() throws java.lang.Exception {
        com.squareup.javapoet.WildcardTypeName type = com.squareup.javapoet.WildcardTypeName.subtypeOf(java.lang.Object.class);
        com.google.common.truth.Truth.assertThat(type.toString()).isEqualTo("?");
    }

    @org.junit.Test
    public void wildcardSuperType() throws java.lang.Exception {
        com.squareup.javapoet.WildcardTypeName type = com.squareup.javapoet.WildcardTypeName.supertypeOf(java.lang.String.class);
        com.google.common.truth.Truth.assertThat(type.toString()).isEqualTo("? super java.lang.String");
    }

    @org.junit.Test
    public void wildcardMirrorNoBounds() throws java.lang.Exception {
        javax.lang.model.type.WildcardType wildcard = getTypes().getWildcardType(null, null);
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(wildcard);
        com.google.common.truth.Truth.assertThat(type.toString()).isEqualTo("?");
    }

    @org.junit.Test
    public void wildcardMirrorExtendsType() throws java.lang.Exception {
        javax.lang.model.util.Types types = getTypes();
        javax.lang.model.util.Elements elements = getElements();
        javax.lang.model.type.TypeMirror charSequence = elements.getTypeElement(java.lang.CharSequence.class.getName()).asType();
        javax.lang.model.type.WildcardType wildcard = types.getWildcardType(charSequence, null);
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(wildcard);
        com.google.common.truth.Truth.assertThat(type.toString()).isEqualTo("? extends java.lang.CharSequence");
    }

    @org.junit.Test
    public void wildcardMirrorSuperType() throws java.lang.Exception {
        javax.lang.model.util.Types types = getTypes();
        javax.lang.model.util.Elements elements = getElements();
        javax.lang.model.type.TypeMirror string = elements.getTypeElement(java.lang.String.class.getName()).asType();
        javax.lang.model.type.WildcardType wildcard = types.getWildcardType(null, string);
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(wildcard);
        com.google.common.truth.Truth.assertThat(type.toString()).isEqualTo("? super java.lang.String");
    }

    @org.junit.Test
    public void typeVariable() throws java.lang.Exception {
        com.squareup.javapoet.TypeVariableName type = com.squareup.javapoet.TypeVariableName.get("T", java.lang.CharSequence.class);
        com.google.common.truth.Truth.assertThat(type.toString()).isEqualTo("T");// (Bounds are only emitted in declaration.)
        
    }

    @org.junit.Test
    public void box() throws java.lang.Exception {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.INT.box()).isEqualTo(com.squareup.javapoet.ClassName.get(java.lang.Integer.class));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.VOID.box()).isEqualTo(com.squareup.javapoet.ClassName.get(java.lang.Void.class));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(java.lang.Integer.class).box()).isEqualTo(com.squareup.javapoet.ClassName.get(java.lang.Integer.class));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(java.lang.Void.class).box()).isEqualTo(com.squareup.javapoet.ClassName.get(java.lang.Void.class));
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.OBJECT.box()).isEqualTo(com.squareup.javapoet.TypeName.OBJECT);
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(java.lang.String.class).box()).isEqualTo(com.squareup.javapoet.ClassName.get(java.lang.String.class));
    }

    @org.junit.Test
    public void unbox() throws java.lang.Exception {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.INT).isEqualTo(com.squareup.javapoet.TypeName.INT.unbox());
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.VOID).isEqualTo(com.squareup.javapoet.TypeName.VOID.unbox());
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(java.lang.Integer.class).unbox()).isEqualTo(com.squareup.javapoet.TypeName.INT.unbox());
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get(java.lang.Void.class).unbox()).isEqualTo(com.squareup.javapoet.TypeName.VOID.unbox());
        try {
            com.squareup.javapoet.TypeName.OBJECT.unbox();
            org.junit.Assert.fail();
        } catch (java.lang.UnsupportedOperationException expected) {
        }
        try {
            com.squareup.javapoet.ClassName.get(java.lang.String.class).unbox();
            org.junit.Assert.fail();
        } catch (java.lang.UnsupportedOperationException expected) {
        }
    }

    private static class DeclaredTypeAsErrorType implements javax.lang.model.type.ErrorType {
        private final javax.lang.model.type.DeclaredType declaredType;

        public DeclaredTypeAsErrorType(javax.lang.model.type.DeclaredType declaredType) {
            this.declaredType = declaredType;
        }

        @java.lang.Override
        public javax.lang.model.element.Element asElement() {
            return declaredType.asElement();
        }

        @java.lang.Override
        public javax.lang.model.type.TypeMirror getEnclosingType() {
            return declaredType.getEnclosingType();
        }

        @java.lang.Override
        public java.util.List<? extends javax.lang.model.type.TypeMirror> getTypeArguments() {
            return declaredType.getTypeArguments();
        }

        @java.lang.Override
        public javax.lang.model.type.TypeKind getKind() {
            return declaredType.getKind();
        }

        @java.lang.Override
        public <R, P> R accept(javax.lang.model.type.TypeVisitor<R, P> typeVisitor, P p) {
            return typeVisitor.visitError(this, p);
        }

        // JDK8 Compatibility:
        public <A extends java.lang.annotation.Annotation> A[] getAnnotationsByType(java.lang.Class<A> annotationType) {
            throw new java.lang.UnsupportedOperationException();
        }

        public <A extends java.lang.annotation.Annotation> A getAnnotation(java.lang.Class<A> annotationType) {
            throw new java.lang.UnsupportedOperationException();
        }

        public java.util.List<? extends javax.lang.model.element.AnnotationMirror> getAnnotationMirrors() {
            throw new java.lang.UnsupportedOperationException();
        }
    }
}

