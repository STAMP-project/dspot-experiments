/**
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.lang.reflect;


import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import junit.framework.TestCase;


public final class AnnotationsTest extends TestCase {
    public void testClassDirectAnnotations() {
        assertAnnotatedElement(AnnotationsTest.Type.class, AnnotationsTest.AnnotationA.class, AnnotationsTest.AnnotationB.class);
    }

    public void testClassInheritedAnnotations() {
        assertAnnotatedElement(AnnotationsTest.ExtendsType.class, AnnotationsTest.AnnotationB.class);
    }

    public void testConstructorAnnotations() throws Exception {
        Constructor<AnnotationsTest.Type> constructor = AnnotationsTest.Type.class.getConstructor();
        assertAnnotatedElement(constructor, AnnotationsTest.AnnotationA.class, AnnotationsTest.AnnotationC.class);
    }

    public void testFieldAnnotations() throws Exception {
        Field field = AnnotationsTest.Type.class.getField("field");
        assertAnnotatedElement(field, AnnotationsTest.AnnotationA.class, AnnotationsTest.AnnotationD.class);
    }

    public void testMethodAnnotations() throws Exception {
        Method method = AnnotationsTest.Type.class.getMethod("method", String.class, String.class);
        assertAnnotatedElement(method, AnnotationsTest.AnnotationB.class, AnnotationsTest.AnnotationC.class);
    }

    public void testParameterAnnotations() throws Exception {
        Method method = AnnotationsTest.Type.class.getMethod("method", String.class, String.class);
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        TestCase.assertEquals(2, parameterAnnotations.length);
        TestCase.assertEquals(set(AnnotationsTest.AnnotationB.class, AnnotationsTest.AnnotationD.class), annotationsToTypes(parameterAnnotations[0]));
        TestCase.assertEquals(set(AnnotationsTest.AnnotationC.class, AnnotationsTest.AnnotationD.class), annotationsToTypes(parameterAnnotations[1]));
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationA {}

    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationB {}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationC {}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationD {}

    @AnnotationsTest.AnnotationA
    @AnnotationsTest.AnnotationB
    public static class Type {
        @AnnotationsTest.AnnotationA
        @AnnotationsTest.AnnotationC
        public Type() {
        }

        @AnnotationsTest.AnnotationA
        @AnnotationsTest.AnnotationD
        public String field;

        @AnnotationsTest.AnnotationB
        @AnnotationsTest.AnnotationC
        public void method(@AnnotationsTest.AnnotationB
        @AnnotationsTest.AnnotationD
        String parameter1, @AnnotationsTest.AnnotationC
        @AnnotationsTest.AnnotationD
        String parameter2) {
        }
    }

    public static class ExtendsType extends AnnotationsTest.Type {}
}

