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
package com.google.auto.common;


import com.google.common.collect.Iterables;
import com.google.common.testing.EquivalenceTester;
import com.google.testing.compile.CompilationRule;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link AnnotationMirrors}.
 */
@RunWith(JUnit4.class)
public class AnnotationMirrorsTest {
    @Rule
    public CompilationRule compilationRule = new CompilationRule();

    private Elements elements;

    @interface SimpleAnnotation {}

    @AnnotationMirrorsTest.SimpleAnnotation
    class SimplyAnnotated {}

    @AnnotationMirrorsTest.SimpleAnnotation
    class AlsoSimplyAnnotated {}

    enum SimpleEnum {

        BLAH,
        FOO;}

    @interface Outer {
        AnnotationMirrorsTest.SimpleEnum value();
    }

    @AnnotationMirrorsTest.Outer(AnnotationMirrorsTest.SimpleEnum.BLAH)
    static class TestClassBlah {}

    @AnnotationMirrorsTest.Outer(AnnotationMirrorsTest.SimpleEnum.BLAH)
    static class TestClassBlah2 {}

    @AnnotationMirrorsTest.Outer(AnnotationMirrorsTest.SimpleEnum.FOO)
    static class TestClassFoo {}

    @interface DefaultingOuter {
        AnnotationMirrorsTest.SimpleEnum value() default AnnotationMirrorsTest.SimpleEnum.BLAH;
    }

    @AnnotationMirrorsTest.DefaultingOuter
    class TestWithDefaultingOuterDefault {}

    @AnnotationMirrorsTest.DefaultingOuter(AnnotationMirrorsTest.SimpleEnum.BLAH)
    class TestWithDefaultingOuterBlah {}

    @AnnotationMirrorsTest.DefaultingOuter(AnnotationMirrorsTest.SimpleEnum.FOO)
    class TestWithDefaultingOuterFoo {}

    @interface AnnotatedOuter {
        AnnotationMirrorsTest.DefaultingOuter value();
    }

    @AnnotationMirrorsTest.AnnotatedOuter(@AnnotationMirrorsTest.DefaultingOuter)
    class TestDefaultNestedAnnotated {}

    @AnnotationMirrorsTest.AnnotatedOuter(@AnnotationMirrorsTest.DefaultingOuter(AnnotationMirrorsTest.SimpleEnum.BLAH))
    class TestBlahNestedAnnotated {}

    @AnnotationMirrorsTest.AnnotatedOuter(@AnnotationMirrorsTest.DefaultingOuter(AnnotationMirrorsTest.SimpleEnum.FOO))
    class TestFooNestedAnnotated {}

    @interface OuterWithValueArray {
        AnnotationMirrorsTest.DefaultingOuter[] value() default {  };
    }

    @AnnotationMirrorsTest.OuterWithValueArray
    class TestValueArrayWithDefault {}

    @AnnotationMirrorsTest.OuterWithValueArray({  })
    class TestValueArrayWithEmpty {}

    @AnnotationMirrorsTest.OuterWithValueArray({ @AnnotationMirrorsTest.DefaultingOuter })
    class TestValueArrayWithOneDefault {}

    @AnnotationMirrorsTest.OuterWithValueArray(@AnnotationMirrorsTest.DefaultingOuter(AnnotationMirrorsTest.SimpleEnum.BLAH))
    class TestValueArrayWithOneBlah {}

    @AnnotationMirrorsTest.OuterWithValueArray(@AnnotationMirrorsTest.DefaultingOuter(AnnotationMirrorsTest.SimpleEnum.FOO))
    class TestValueArrayWithOneFoo {}

    @AnnotationMirrorsTest.OuterWithValueArray({ @AnnotationMirrorsTest.DefaultingOuter(AnnotationMirrorsTest.SimpleEnum.FOO), @AnnotationMirrorsTest.DefaultingOuter })
    class TestValueArrayWithFooAndDefaultBlah {}

    @AnnotationMirrorsTest.OuterWithValueArray({ @AnnotationMirrorsTest.DefaultingOuter(AnnotationMirrorsTest.SimpleEnum.FOO), @AnnotationMirrorsTest.DefaultingOuter(AnnotationMirrorsTest.SimpleEnum.BLAH) })
    class TestValueArrayWithFooBlah {}

    @AnnotationMirrorsTest.OuterWithValueArray({ @AnnotationMirrorsTest.DefaultingOuter(AnnotationMirrorsTest.SimpleEnum.FOO), @AnnotationMirrorsTest.DefaultingOuter(AnnotationMirrorsTest.SimpleEnum.BLAH) })
    class TestValueArrayWithFooBlah2 {}

    @AnnotationMirrorsTest.OuterWithValueArray({ @AnnotationMirrorsTest.DefaultingOuter(AnnotationMirrorsTest.SimpleEnum.BLAH), @AnnotationMirrorsTest.DefaultingOuter(AnnotationMirrorsTest.SimpleEnum.FOO) })
    class TestValueArrayWithBlahFoo {}

    @Test
    public void testEquivalences() {
        EquivalenceTester<AnnotationMirror> tester = EquivalenceTester.of(AnnotationMirrors.equivalence());
        tester.addEquivalenceGroup(annotationOn(AnnotationMirrorsTest.SimplyAnnotated.class), annotationOn(AnnotationMirrorsTest.AlsoSimplyAnnotated.class));
        tester.addEquivalenceGroup(annotationOn(AnnotationMirrorsTest.TestClassBlah.class), annotationOn(AnnotationMirrorsTest.TestClassBlah2.class));
        tester.addEquivalenceGroup(annotationOn(AnnotationMirrorsTest.TestClassFoo.class));
        tester.addEquivalenceGroup(annotationOn(AnnotationMirrorsTest.TestWithDefaultingOuterDefault.class), annotationOn(AnnotationMirrorsTest.TestWithDefaultingOuterBlah.class));
        tester.addEquivalenceGroup(annotationOn(AnnotationMirrorsTest.TestWithDefaultingOuterFoo.class));
        tester.addEquivalenceGroup(annotationOn(AnnotationMirrorsTest.TestDefaultNestedAnnotated.class), annotationOn(AnnotationMirrorsTest.TestBlahNestedAnnotated.class));
        tester.addEquivalenceGroup(annotationOn(AnnotationMirrorsTest.TestFooNestedAnnotated.class));
        tester.addEquivalenceGroup(annotationOn(AnnotationMirrorsTest.TestValueArrayWithDefault.class), annotationOn(AnnotationMirrorsTest.TestValueArrayWithEmpty.class));
        tester.addEquivalenceGroup(annotationOn(AnnotationMirrorsTest.TestValueArrayWithOneDefault.class), annotationOn(AnnotationMirrorsTest.TestValueArrayWithOneBlah.class));
        tester.addEquivalenceGroup(annotationOn(AnnotationMirrorsTest.TestValueArrayWithOneFoo.class));
        tester.addEquivalenceGroup(annotationOn(AnnotationMirrorsTest.TestValueArrayWithFooAndDefaultBlah.class), annotationOn(AnnotationMirrorsTest.TestValueArrayWithFooBlah.class), annotationOn(AnnotationMirrorsTest.TestValueArrayWithFooBlah2.class));
        tester.addEquivalenceGroup(annotationOn(AnnotationMirrorsTest.TestValueArrayWithBlahFoo.class));
        tester.test();
    }

    @interface Stringy {
        String value() default "default";
    }

    @AnnotationMirrorsTest.Stringy
    class StringyUnset {}

    @AnnotationMirrorsTest.Stringy("foo")
    class StringySet {}

    @Test
    public void testGetDefaultValuesUnset() {
        assertThat(annotationOn(AnnotationMirrorsTest.StringyUnset.class).getElementValues()).isEmpty();
        Iterable<AnnotationValue> values = AnnotationMirrors.getAnnotationValuesWithDefaults(annotationOn(AnnotationMirrorsTest.StringyUnset.class)).values();
        String value = Iterables.getOnlyElement(values).accept(new SimpleAnnotationValueVisitor6<String, Void>() {
            @Override
            public String visitString(String value, Void ignored) {
                return value;
            }
        }, null);
        assertThat(value).isEqualTo("default");
    }

    @Test
    public void testGetDefaultValuesSet() {
        Iterable<AnnotationValue> values = AnnotationMirrors.getAnnotationValuesWithDefaults(annotationOn(AnnotationMirrorsTest.StringySet.class)).values();
        String value = Iterables.getOnlyElement(values).accept(new SimpleAnnotationValueVisitor6<String, Void>() {
            @Override
            public String visitString(String value, Void ignored) {
                return value;
            }
        }, null);
        assertThat(value).isEqualTo("foo");
    }

    @Test
    public void testGetValueEntry() {
        Map.Entry<ExecutableElement, AnnotationValue> elementValue = AnnotationMirrors.getAnnotationElementAndValue(annotationOn(AnnotationMirrorsTest.TestClassBlah.class), "value");
        assertThat(elementValue.getKey().getSimpleName().toString()).isEqualTo("value");
        assertThat(elementValue.getValue().getValue()).isInstanceOf(VariableElement.class);
        AnnotationValue value = AnnotationMirrors.getAnnotationValue(annotationOn(AnnotationMirrorsTest.TestClassBlah.class), "value");
        assertThat(value.getValue()).isInstanceOf(VariableElement.class);
    }

    @Test
    public void testGetValueEntryFailure() {
        try {
            AnnotationMirrors.getAnnotationValue(annotationOn(AnnotationMirrorsTest.TestClassBlah.class), "a");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("@com.google.auto.common.AnnotationMirrorsTest.Outer does not define an element a()");
            return;
        }
        Assert.fail("Should have thrown.");
    }
}

