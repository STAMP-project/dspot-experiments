/**
 * Copyright (C) 2012 Google, Inc.
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
package com.google.auto.value;


import AutoValue.CopyAnnotations;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.testing.EqualsTester;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static javax.tools.Diagnostic.Kind.ERROR;


/**
 * Tests for constructs new in Java 8, such as type annotations.
 *
 * @author Till Brychcy
 * @author emcmanus@google.com (?amonn McManus)
 */
@RunWith(JUnit4.class)
public class AutoValueJava8Test {
    private static boolean javacHandlesTypeAnnotationsCorrectly;

    private static final String JAVAC_HAS_BUG_ERROR = "javac has the type-annotation bug";

    @SupportedAnnotationTypes("*")
    @SupportedSourceVersion(SourceVersion.RELEASE_8)
    private static class BugTestProcessor extends AbstractProcessor {
        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            if (roundEnv.processingOver()) {
                test();
            }
            return false;
        }

        private void test() {
            TypeElement test = processingEnv.getElementUtils().getTypeElement("Test");
            List<ExecutableElement> methods = ElementFilter.methodsIn(test.getEnclosedElements());
            ExecutableElement t = Iterables.getOnlyElement(methods);
            assertThat(t.getSimpleName().toString()).isEqualTo("t");
            List<? extends AnnotationMirror> typeAnnotations = t.getReturnType().getAnnotationMirrors();
            if (typeAnnotations.isEmpty()) {
                processingEnv.getMessager().printMessage(ERROR, AutoValueJava8Test.JAVAC_HAS_BUG_ERROR);
                return;
            }
            AnnotationMirror typeAnnotation = Iterables.getOnlyElement(typeAnnotations);
            assertThat(typeAnnotation.getAnnotationType().toString()).contains("Nullable");
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE_USE)
    public @interface Nullable {}

    @AutoValue
    abstract static class NullableProperties {
        @AutoValueJava8Test.Nullable
        abstract String nullableString();

        abstract int randomInt();

        static AutoValueJava8Test.NullableProperties create(@AutoValueJava8Test.Nullable
        String nullableString, int randomInt) {
            return new AutoValue_AutoValueJava8Test_NullableProperties(nullableString, randomInt);
        }
    }

    @Test
    public void testNullablePropertiesCanBeNull() {
        AutoValueJava8Test.NullableProperties instance = AutoValueJava8Test.NullableProperties.create(null, 23);
        assertThat(instance.nullableString()).isNull();
        assertThat(instance.randomInt()).isEqualTo(23);
        assertThat(instance.toString()).isEqualTo("NullableProperties{nullableString=null, randomInt=23}");
    }

    @Test
    public void testNullablePropertyImplementationIsNullable() throws NoSuchMethodException {
        Method method = AutoValue_AutoValueJava8Test_NullableProperties.class.getDeclaredMethod("nullableString");
        assertThat(method.getAnnotatedReturnType().getAnnotations()).asList().contains(AutoValueJava8Test.nullable());
    }

    @Test
    public void testNullablePropertyConstructorParameterIsNullable() throws NoSuchMethodException {
        Constructor<?> constructor = AutoValue_AutoValueJava8Test_NullableProperties.class.getDeclaredConstructor(String.class, int.class);
        try {
            assertThat(constructor.getAnnotatedParameterTypes()[0].getAnnotations()).asList().contains(AutoValueJava8Test.nullable());
        } catch (AssertionError e) {
            if (AutoValueJava8Test.javacHandlesTypeAnnotationsCorrectly) {
                throw e;
            }
        }
    }

    @AutoValue
    abstract static class NullablePropertiesNotCopied {
        @CopyAnnotations(exclude = AutoValueJava8Test.Nullable.class)
        @AutoValueJava8Test.Nullable
        abstract String nullableString();

        abstract int randomInt();

        AutoValueJava8Test.NullablePropertiesNotCopied create(String notNullableAfterAll, int randomInt) {
            return new AutoValue_AutoValueJava8Test_NullablePropertiesNotCopied(notNullableAfterAll, randomInt);
        }
    }

    @Test
    public void testExcludedNullablePropertyImplementation() throws NoSuchMethodException {
        Method method = AutoValue_AutoValueJava8Test_NullablePropertiesNotCopied.class.getDeclaredMethod("nullableString");
        assertThat(method.getAnnotatedReturnType().getAnnotations()).asList().doesNotContain(AutoValueJava8Test.nullable());
    }

    @Test
    public void testExcludedNullablePropertyConstructorParameter() throws NoSuchMethodException {
        Constructor<?> constructor = AutoValue_AutoValueJava8Test_NullablePropertiesNotCopied.class.getDeclaredConstructor(String.class, int.class);
        try {
            assertThat(constructor.getAnnotatedParameterTypes()[0].getAnnotations()).asList().doesNotContain(AutoValueJava8Test.nullable());
        } catch (AssertionError e) {
            if (AutoValueJava8Test.javacHandlesTypeAnnotationsCorrectly) {
                throw e;
            }
        }
    }

    @AutoValue
    abstract static class NullableNonNullable {
        @AutoValueJava8Test.Nullable
        abstract String nullableString();

        @AutoValueJava8Test.Nullable
        abstract String otherNullableString();

        abstract String nonNullableString();

        static AutoValueJava8Test.NullableNonNullable create(String nullableString, String otherNullableString, String nonNullableString) {
            return new AutoValue_AutoValueJava8Test_NullableNonNullable(nullableString, otherNullableString, nonNullableString);
        }
    }

    @Test
    public void testEqualsWithNullable() throws Exception {
        AutoValueJava8Test.NullableNonNullable everythingNull = AutoValueJava8Test.NullableNonNullable.create(null, null, "nonNullableString");
        AutoValueJava8Test.NullableNonNullable somethingNull = AutoValueJava8Test.NullableNonNullable.create(null, "otherNullableString", "nonNullableString");
        AutoValueJava8Test.NullableNonNullable nothingNull = AutoValueJava8Test.NullableNonNullable.create("nullableString", "otherNullableString", "nonNullableString");
        AutoValueJava8Test.NullableNonNullable nothingNullAgain = AutoValueJava8Test.NullableNonNullable.create("nullableString", "otherNullableString", "nonNullableString");
        new EqualsTester().addEqualityGroup(everythingNull).addEqualityGroup(somethingNull).addEqualityGroup(nothingNull, nothingNullAgain).testEquals();
    }

    public static class Nested {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE_USE)
    public @interface OtherTypeAnnotation {}

    @AutoValue
    abstract static class NestedNullableProperties {
        @AutoValueJava8Test.Nullable
        @AutoValueJava8Test.OtherTypeAnnotation
        abstract AutoValueJava8Test.Nested nullableThing();

        abstract int randomInt();

        static AutoValueJava8Test.NestedNullableProperties.Builder builder() {
            return new AutoValue_AutoValueJava8Test_NestedNullableProperties.Builder();
        }

        @AutoValue.Builder
        abstract static class Builder {
            abstract AutoValueJava8Test.NestedNullableProperties.Builder setNullableThing(@AutoValueJava8Test.Nullable
            @AutoValueJava8Test.OtherTypeAnnotation
            AutoValueJava8Test.Nested thing);

            abstract AutoValueJava8Test.NestedNullableProperties.Builder setRandomInt(int x);

            abstract AutoValueJava8Test.NestedNullableProperties build();
        }
    }

    @Test
    public void testNestedNullablePropertiesCanBeNull() {
        AutoValueJava8Test.NestedNullableProperties instance = AutoValueJava8Test.NestedNullableProperties.builder().setRandomInt(23).build();
        assertThat(instance.nullableThing()).isNull();
        assertThat(instance.randomInt()).isEqualTo(23);
        assertThat(instance.toString()).isEqualTo("NestedNullableProperties{nullableThing=null, randomInt=23}");
    }

    @Test
    public void testNestedNullablePropertiesAreCopied() throws Exception {
        try {
            Method generatedGetter = AutoValue_AutoValueJava8Test_NestedNullableProperties.class.getDeclaredMethod("nullableThing");
            Annotation[] getterAnnotations = generatedGetter.getAnnotatedReturnType().getAnnotations();
            assertThat(getterAnnotations).asList().containsAllOf(AutoValueJava8Test.nullable(), AutoValueJava8Test.otherTypeAnnotation());
            Method generatedSetter = AutoValue_AutoValueJava8Test_NestedNullableProperties.Builder.class.getDeclaredMethod("setNullableThing", AutoValueJava8Test.Nested.class);
            Annotation[] setterAnnotations = generatedSetter.getAnnotatedParameterTypes()[0].getAnnotations();
            assertThat(setterAnnotations).asList().containsAllOf(AutoValueJava8Test.nullable(), AutoValueJava8Test.otherTypeAnnotation());
        } catch (AssertionError e) {
            if (AutoValueJava8Test.javacHandlesTypeAnnotationsCorrectly) {
                throw e;
            }
        }
    }

    @AutoValue
    @SuppressWarnings("AutoValueImmutableFields")
    abstract static class PrimitiveArrays {
        @SuppressWarnings("mutable")
        abstract boolean[] booleans();

        @SuppressWarnings("mutable")
        abstract int[] ints();

        static AutoValueJava8Test.PrimitiveArrays create(boolean[] booleans, int[] ints) {
            // Real code would likely clone these parameters, but here we want to check that the
            // generated constructor rejects a null value for booleans.
            return new AutoValue_AutoValueJava8Test_PrimitiveArrays(booleans, ints);
        }
    }

    @Test
    public void testPrimitiveArrays() {
        AutoValueJava8Test.PrimitiveArrays object0 = AutoValueJava8Test.PrimitiveArrays.create(new boolean[0], new int[0]);
        boolean[] booleans = new boolean[]{ false, true, true, false };
        int[] ints = new int[]{ 6, 28, 496, 8128, 33550336 };
        AutoValueJava8Test.PrimitiveArrays object1 = AutoValueJava8Test.PrimitiveArrays.create(booleans.clone(), ints.clone());
        AutoValueJava8Test.PrimitiveArrays object2 = AutoValueJava8Test.PrimitiveArrays.create(booleans.clone(), ints.clone());
        new EqualsTester().addEqualityGroup(object1, object2).addEqualityGroup(object0).testEquals();
        // EqualsTester also exercises hashCode(). We clone the arrays above to ensure that using the
        // default Object.hashCode() will fail.
        String expectedString = (((("PrimitiveArrays{booleans=" + (Arrays.toString(booleans))) + ", ") + "ints=") + (Arrays.toString(ints))) + "}";
        assertThat(object1.toString()).isEqualTo(expectedString);
        assertThat(object1.ints()).isSameAs(object1.ints());
    }

    @Test
    public void testNullablePrimitiveArrays() {
        Assume.assumeTrue(AutoValueJava8Test.javacHandlesTypeAnnotationsCorrectly);
        AutoValueJava8Test.PrimitiveArrays object0 = AutoValueJava8Test.PrimitiveArrays.create(new boolean[0], null);
        boolean[] booleans = new boolean[]{ false, true, true, false };
        AutoValueJava8Test.PrimitiveArrays object1 = AutoValueJava8Test.PrimitiveArrays.create(booleans.clone(), null);
        AutoValueJava8Test.PrimitiveArrays object2 = AutoValueJava8Test.PrimitiveArrays.create(booleans.clone(), null);
        new EqualsTester().addEqualityGroup(object1, object2).addEqualityGroup(object0).testEquals();
        String expectedString = (("PrimitiveArrays{booleans=" + (Arrays.toString(booleans))) + ", ") + "ints=null}";
        assertThat(object1.toString()).isEqualTo(expectedString);
        assertThat(object1.booleans()).isSameAs(object1.booleans());
        assertThat(object1.booleans()).isEqualTo(booleans);
        object1.booleans()[0] ^= true;
        assertThat(object1.booleans()).isNotEqualTo(booleans);
    }

    @Test
    public void testNotNullablePrimitiveArrays() {
        try {
            AutoValueJava8Test.PrimitiveArrays.create(null, new int[0]);
            Assert.fail("Construction with null value for non-@Nullable array should have failed");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).contains("booleans");
        }
    }

    @AutoValue
    public abstract static class NullablePropertyWithBuilder {
        public abstract String notNullable();

        @AutoValueJava8Test.Nullable
        public abstract String nullable();

        public static AutoValueJava8Test.NullablePropertyWithBuilder.Builder builder() {
            return new AutoValue_AutoValueJava8Test_NullablePropertyWithBuilder.Builder();
        }

        @AutoValue.Builder
        public interface Builder {
            AutoValueJava8Test.NullablePropertyWithBuilder.Builder notNullable(String s);

            AutoValueJava8Test.NullablePropertyWithBuilder.Builder nullable(@AutoValueJava8Test.Nullable
            String s);

            AutoValueJava8Test.NullablePropertyWithBuilder build();
        }
    }

    @Test
    public void testOmitNullableWithBuilder() {
        AutoValueJava8Test.NullablePropertyWithBuilder instance1 = AutoValueJava8Test.NullablePropertyWithBuilder.builder().notNullable("hello").build();
        assertThat(instance1.notNullable()).isEqualTo("hello");
        assertThat(instance1.nullable()).isNull();
        AutoValueJava8Test.NullablePropertyWithBuilder instance2 = AutoValueJava8Test.NullablePropertyWithBuilder.builder().notNullable("hello").nullable(null).build();
        assertThat(instance2.notNullable()).isEqualTo("hello");
        assertThat(instance2.nullable()).isNull();
        assertThat(instance1).isEqualTo(instance2);
        AutoValueJava8Test.NullablePropertyWithBuilder instance3 = AutoValueJava8Test.NullablePropertyWithBuilder.builder().notNullable("hello").nullable("world").build();
        assertThat(instance3.notNullable()).isEqualTo("hello");
        assertThat(instance3.nullable()).isEqualTo("world");
        try {
            AutoValueJava8Test.NullablePropertyWithBuilder.builder().build();
            Assert.fail("Expected IllegalStateException for unset non-@Nullable property");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).contains("notNullable");
        }
    }

    @AutoValue
    public abstract static class OptionalPropertyWithNullableBuilder {
        public abstract String notOptional();

        public abstract Optional<String> optional();

        public static AutoValueJava8Test.OptionalPropertyWithNullableBuilder.Builder builder() {
            return new AutoValue_AutoValueJava8Test_OptionalPropertyWithNullableBuilder.Builder();
        }

        @AutoValue.Builder
        public interface Builder {
            AutoValueJava8Test.OptionalPropertyWithNullableBuilder.Builder notOptional(String s);

            AutoValueJava8Test.OptionalPropertyWithNullableBuilder.Builder optional(@AutoValueJava8Test.Nullable
            String s);

            AutoValueJava8Test.OptionalPropertyWithNullableBuilder build();
        }
    }

    @Test
    public void testOmitOptionalWithNullableBuilder() {
        AutoValueJava8Test.OptionalPropertyWithNullableBuilder instance1 = AutoValueJava8Test.OptionalPropertyWithNullableBuilder.builder().notOptional("hello").build();
        assertThat(instance1.notOptional()).isEqualTo("hello");
        assertThat(instance1.optional()).isEmpty();
        AutoValueJava8Test.OptionalPropertyWithNullableBuilder instance2 = AutoValueJava8Test.OptionalPropertyWithNullableBuilder.builder().notOptional("hello").optional(null).build();
        assertThat(instance2.notOptional()).isEqualTo("hello");
        assertThat(instance2.optional()).isEmpty();
        assertThat(instance1).isEqualTo(instance2);
        AutoValueJava8Test.OptionalPropertyWithNullableBuilder instance3 = AutoValueJava8Test.OptionalPropertyWithNullableBuilder.builder().notOptional("hello").optional("world").build();
        assertThat(instance3.notOptional()).isEqualTo("hello");
        assertThat(instance3.optional()).hasValue("world");
        try {
            AutoValueJava8Test.OptionalPropertyWithNullableBuilder.builder().build();
            Assert.fail("Expected IllegalStateException for unset non-Optional property");
        } catch (IllegalStateException expected) {
        }
    }

    @AutoValue
    @SuppressWarnings("AutoValueImmutableFields")
    public abstract static class BuilderWithUnprefixedGetters<T extends Comparable<T>> {
        public abstract ImmutableList<T> list();

        @AutoValueJava8Test.Nullable
        public abstract T t();

        @SuppressWarnings("mutable")
        public abstract int[] ints();

        public abstract int noGetter();

        public static <T extends Comparable<T>> AutoValueJava8Test.BuilderWithUnprefixedGetters.Builder<T> builder() {
            return new AutoValue_AutoValueJava8Test_BuilderWithUnprefixedGetters.Builder<T>();
        }

        @AutoValue.Builder
        public interface Builder<T extends Comparable<T>> {
            AutoValueJava8Test.BuilderWithUnprefixedGetters.Builder<T> setList(ImmutableList<T> list);

            AutoValueJava8Test.BuilderWithUnprefixedGetters.Builder<T> setT(T t);

            AutoValueJava8Test.BuilderWithUnprefixedGetters.Builder<T> setInts(int[] ints);

            AutoValueJava8Test.BuilderWithUnprefixedGetters.Builder<T> setNoGetter(int x);

            ImmutableList<T> list();

            T t();

            int[] ints();

            AutoValueJava8Test.BuilderWithUnprefixedGetters<T> build();
        }
    }

    @Test
    public void testBuilderWithUnprefixedGetter() {
        Assume.assumeTrue(AutoValueJava8Test.javacHandlesTypeAnnotationsCorrectly);
        ImmutableList<String> names = ImmutableList.of("fred", "jim");
        int[] ints = new int[]{ 6, 28, 496, 8128, 33550336 };
        int noGetter = -1;
        AutoValueJava8Test.BuilderWithUnprefixedGetters.Builder<String> builder = AutoValueJava8Test.BuilderWithUnprefixedGetters.builder();
        assertThat(builder.t()).isNull();
        try {
            builder.list();
            Assert.fail("Attempt to retrieve unset list property should have failed");
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Property \"list\" has not been set");
        }
        try {
            builder.ints();
            Assert.fail("Attempt to retrieve unset ints property should have failed");
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Property \"ints\" has not been set");
        }
        builder.setList(names);
        assertThat(builder.list()).isSameAs(names);
        builder.setInts(ints);
        assertThat(builder.ints()).isEqualTo(ints);
        // The array is not cloned by the getter, so the client can modify it (but shouldn't).
        ints[0] = 0;
        assertThat(builder.ints()[0]).isEqualTo(0);
        ints[0] = 6;
        AutoValueJava8Test.BuilderWithUnprefixedGetters<String> instance = builder.setNoGetter(noGetter).build();
        assertThat(instance.list()).isSameAs(names);
        assertThat(instance.t()).isNull();
        assertThat(instance.ints()).isEqualTo(ints);
        assertThat(instance.noGetter()).isEqualTo(noGetter);
    }

    @AutoValue
    @SuppressWarnings("AutoValueImmutableFields")
    public abstract static class BuilderWithPrefixedGetters<T extends Comparable<T>> {
        public abstract ImmutableList<T> getList();

        public abstract T getT();

        @SuppressWarnings("mutable")
        public abstract int[] getInts();

        public abstract int getNoGetter();

        public static <T extends Comparable<T>> AutoValueJava8Test.BuilderWithPrefixedGetters.Builder<T> builder() {
            return new AutoValue_AutoValueJava8Test_BuilderWithPrefixedGetters.Builder<T>();
        }

        @AutoValue.Builder
        public abstract static class Builder<T extends Comparable<T>> {
            public abstract AutoValueJava8Test.BuilderWithPrefixedGetters.Builder<T> setList(ImmutableList<T> list);

            public abstract AutoValueJava8Test.BuilderWithPrefixedGetters.Builder<T> setT(T t);

            public abstract AutoValueJava8Test.BuilderWithPrefixedGetters.Builder<T> setInts(int[] ints);

            public abstract AutoValueJava8Test.BuilderWithPrefixedGetters.Builder<T> setNoGetter(int x);

            abstract ImmutableList<T> getList();

            abstract T getT();

            abstract int[] getInts();

            public abstract AutoValueJava8Test.BuilderWithPrefixedGetters<T> build();
        }
    }

    @Test
    public void testBuilderWithPrefixedGetter() {
        Assume.assumeTrue(AutoValueJava8Test.javacHandlesTypeAnnotationsCorrectly);
        ImmutableList<String> names = ImmutableList.of("fred", "jim");
        String name = "sheila";
        int noGetter = -1;
        AutoValueJava8Test.BuilderWithPrefixedGetters.Builder<String> builder = AutoValueJava8Test.BuilderWithPrefixedGetters.builder();
        assertThat(builder.getInts()).isNull();
        try {
            builder.getList();
            Assert.fail("Attempt to retrieve unset list property should have failed");
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Property \"list\" has not been set");
        }
        builder.setList(names);
        assertThat(builder.getList()).isSameAs(names);
        builder.setT(name);
        assertThat(builder.getInts()).isNull();
        AutoValueJava8Test.BuilderWithPrefixedGetters<String> instance = builder.setNoGetter(noGetter).build();
        assertThat(instance.getList()).isSameAs(names);
        assertThat(instance.getT()).isEqualTo(name);
        assertThat(instance.getInts()).isNull();
        assertThat(instance.getNoGetter()).isEqualTo(noGetter);
    }

    // This class tests the case where an annotation is both a method annotation and a type
    // annotation. If we weren't careful, we might emit it twice in the generated code.
    @AutoValue
    abstract static class FunkyNullable {
        @Target({ ElementType.METHOD, ElementType.TYPE_USE })
        @interface Nullable {}

        @AutoValueJava8Test.FunkyNullable.Nullable
        abstract String foo();

        abstract Optional<String> bar();

        static AutoValueJava8Test.FunkyNullable.Builder builder() {
            return new AutoValue_AutoValueJava8Test_FunkyNullable.Builder();
        }

        @AutoValue.Builder
        interface Builder {
            AutoValueJava8Test.FunkyNullable.Builder setFoo(@AutoValueJava8Test.FunkyNullable.Nullable
            String foo);

            AutoValueJava8Test.FunkyNullable.Builder setBar(@AutoValueJava8Test.FunkyNullable.Nullable
            String bar);

            AutoValueJava8Test.FunkyNullable build();
        }
    }

    @Test
    public void testFunkyNullable() {
        AutoValueJava8Test.FunkyNullable explicitNull = AutoValueJava8Test.FunkyNullable.builder().setFoo(null).setBar(null).build();
        AutoValueJava8Test.FunkyNullable implicitNull = AutoValueJava8Test.FunkyNullable.builder().build();
        assertThat(explicitNull).isEqualTo(implicitNull);
    }

    @AutoValue
    abstract static class EqualsNullable {
        @Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
        @Retention(RetentionPolicy.RUNTIME)
        @interface Nullable {}

        abstract String foo();

        static AutoValueJava8Test.EqualsNullable create(String foo) {
            return new AutoValue_AutoValueJava8Test_EqualsNullable(foo);
        }

        @Override
        public abstract boolean equals(@AutoValueJava8Test.EqualsNullable.Nullable
        Object x);

        @Override
        public abstract int hashCode();
    }

    /**
     * Tests that a type annotation on the parameter of {@code equals(Object)} is copied into the
     * implementation class.
     */
    @Test
    public void testEqualsNullable() throws ReflectiveOperationException {
        AutoValueJava8Test.EqualsNullable x = AutoValueJava8Test.EqualsNullable.create("foo");
        Class<? extends AutoValueJava8Test.EqualsNullable> implClass = x.getClass();
        Method equals = implClass.getDeclaredMethod("equals", Object.class);
        AnnotatedType[] parameterTypes = equals.getAnnotatedParameterTypes();
        assertThat(parameterTypes[0].isAnnotationPresent(AutoValueJava8Test.EqualsNullable.Nullable.class)).isTrue();
    }

    @AutoValue
    abstract static class AnnotatedTypeParameter<@AutoValueJava8Test.Nullable
    T> {
        @AutoValueJava8Test.Nullable
        abstract T thing();

        static <@AutoValueJava8Test.Nullable
        T> AutoValueJava8Test.AnnotatedTypeParameter<T> create(T thing) {
            return new AutoValue_AutoValueJava8Test_AnnotatedTypeParameter<T>(thing);
        }
    }

    /**
     * Tests that an annotation on a type parameter of an {@code @AutoValue} class is copied to the
     * implementation class.
     */
    @Test
    public void testTypeAnnotationCopiedToImplementation() {
        @AutoValueJava8Test.Nullable
        String nullableString = "blibby";
        AutoValueJava8Test.AnnotatedTypeParameter<@AutoValueJava8Test.Nullable
        String> x = AutoValueJava8Test.AnnotatedTypeParameter.create(nullableString);
        Class<?> c = x.getClass();
        assertThat(c.getTypeParameters()).hasLength(1);
        TypeVariable<?> typeParameter = c.getTypeParameters()[0];
        assertThat(typeParameter.getAnnotations()).named(typeParameter.toString()).asList().contains(AutoValueJava8Test.nullable());
    }

    @AutoValue
    abstract static class AnnotatedTypeParameterWithBuilder<@AutoValueJava8Test.Nullable
    T> {
        @AutoValueJava8Test.Nullable
        abstract T thing();

        static <@AutoValueJava8Test.Nullable
        T> AutoValueJava8Test.AnnotatedTypeParameterWithBuilder.Builder<T> builder() {
            return new AutoValue_AutoValueJava8Test_AnnotatedTypeParameterWithBuilder.Builder<T>();
        }

        @AutoValue.Builder
        abstract static class Builder<@AutoValueJava8Test.Nullable
        T> {
            abstract AutoValueJava8Test.AnnotatedTypeParameterWithBuilder.Builder<T> setThing(T thing);

            abstract AutoValueJava8Test.AnnotatedTypeParameterWithBuilder<T> build();
        }
    }

    /**
     * Tests that an annotation on a type parameter of an {@code @AutoValue} builder is copied to the
     * implementation class.
     */
    @Test
    public void testTypeAnnotationOnBuilderCopiedToImplementation() {
        AutoValueJava8Test.AnnotatedTypeParameterWithBuilder.Builder<@AutoValueJava8Test.Nullable
        String> builder = AutoValueJava8Test.AnnotatedTypeParameterWithBuilder.builder();
        Class<?> c = builder.getClass();
        assertThat(c.getTypeParameters()).hasLength(1);
        TypeVariable<?> typeParameter = c.getTypeParameters()[0];
        assertThat(typeParameter.getAnnotations()).named(typeParameter.toString()).asList().contains(AutoValueJava8Test.nullable());
    }
}

