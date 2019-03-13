/**
 * Copyright (C) 2017 Google, Inc.
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
package com.google.auto.common;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.testing.compile.CompilationRule;
import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link SimpleAnnotationMirror}.
 */
@RunWith(JUnit4.class)
public class SimpleAnnotationMirrorTest {
    @Rule
    public final CompilationRule compilation = new CompilationRule();

    @interface EmptyAnnotation {}

    @interface AnnotationWithDefault {
        int value() default 3;
    }

    @interface MultipleValues {
        int value1();

        int value2();
    }

    @Test
    public void emptyAnnotation() {
        TypeElement emptyAnnotation = getTypeElement(SimpleAnnotationMirrorTest.EmptyAnnotation.class);
        AnnotationMirror simpleAnnotation = SimpleAnnotationMirror.of(emptyAnnotation);
        assertThat(simpleAnnotation.getAnnotationType()).isEqualTo(emptyAnnotation.asType());
        assertThat(simpleAnnotation.getElementValues()).isEmpty();
    }

    @Test
    public void multipleValues() {
        TypeElement multipleValues = getTypeElement(SimpleAnnotationMirrorTest.MultipleValues.class);
        Map<String, AnnotationValue> values = new HashMap<>();
        values.put("value1", SimpleAnnotationMirrorTest.intValue(1));
        values.put("value2", SimpleAnnotationMirrorTest.intValue(2));
        assertThat(SimpleAnnotationMirror.of(multipleValues, values).getElementValues()).hasSize(2);
    }

    @Test
    public void extraValues() {
        TypeElement multipleValues = getTypeElement(SimpleAnnotationMirrorTest.MultipleValues.class);
        Map<String, AnnotationValue> values = new HashMap<>();
        values.put("value1", SimpleAnnotationMirrorTest.intValue(1));
        values.put("value2", SimpleAnnotationMirrorTest.intValue(2));
        values.put("value3", SimpleAnnotationMirrorTest.intValue(3));
        SimpleAnnotationMirrorTest.expectThrows(() -> SimpleAnnotationMirror.of(multipleValues, values));
    }

    @Test
    public void defaultValue() {
        TypeElement withDefaults = getTypeElement(SimpleAnnotationMirrorTest.AnnotationWithDefault.class);
        AnnotationMirror annotation = SimpleAnnotationMirror.of(withDefaults);
        assertThat(annotation.getElementValues()).hasSize(1);
        assertThat(Iterables.getOnlyElement(annotation.getElementValues().values()).getValue()).isEqualTo(3);
    }

    @Test
    public void overriddenDefaultValue() {
        TypeElement withDefaults = getTypeElement(SimpleAnnotationMirrorTest.AnnotationWithDefault.class);
        AnnotationMirror annotation = SimpleAnnotationMirror.of(withDefaults, ImmutableMap.of("value", SimpleAnnotationMirrorTest.intValue(4)));
        assertThat(annotation.getElementValues()).hasSize(1);
        assertThat(Iterables.getOnlyElement(annotation.getElementValues().values()).getValue()).isEqualTo(4);
    }

    @Test
    public void missingValues() {
        TypeElement multipleValues = getTypeElement(SimpleAnnotationMirrorTest.MultipleValues.class);
        SimpleAnnotationMirrorTest.expectThrows(() -> SimpleAnnotationMirror.of(multipleValues));
    }

    @Test
    public void notAnAnnotation() {
        TypeElement stringElement = getTypeElement(String.class);
        SimpleAnnotationMirrorTest.expectThrows(() -> SimpleAnnotationMirror.of(stringElement));
    }
}

