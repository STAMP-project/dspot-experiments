/**
 * Copyright (C) 2014 Google Inc.
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


import com.google.auto.value.annotations.Empty;
import com.google.auto.value.annotations.GwtArrays;
import com.google.auto.value.annotations.StringValues;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.primitives.Ints;
import com.google.common.testing.EqualsTester;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 *
 * @author emcmanus@google.com (?amonn McManus)
 */
@RunWith(JUnit4.class)
public class AutoAnnotationTest {
    @Empty
    @StringValues("oops")
    static class AnnotatedClass {}

    @Test
    public void testSimple() {
        StringValues expectedStringValues = AutoAnnotationTest.AnnotatedClass.class.getAnnotation(StringValues.class);
        StringValues actualStringValues = AutoAnnotationTest.newStringValues(new String[]{ "oops" });
        StringValues otherStringValues = AutoAnnotationTest.newStringValues(new String[]{  });
        new EqualsTester().addEqualityGroup(expectedStringValues, actualStringValues).addEqualityGroup(otherStringValues).testEquals();
    }

    @Test
    public void testArraysAreCloned() {
        String[] array = new String[]{ "Jekyll" };
        StringValues stringValues = AutoAnnotationTest.newStringValues(array);
        array[0] = "Hyde";
        Assert.assertEquals("Jekyll", stringValues.value()[0]);
        stringValues.value()[0] = "Hyde";
        Assert.assertEquals("Jekyll", stringValues.value()[0]);
    }

    @Test
    public void testGwtArraysAreCloned() {
        String[] strings = new String[]{ "Jekyll" };
        int[] ints = new int[]{ 2, 3, 5 };
        GwtArrays arrays = AutoAnnotationTest.newGwtArrays(strings, ints);
        Assert.assertEquals(ImmutableList.of("Jekyll"), ImmutableList.copyOf(arrays.strings()));
        Assert.assertEquals(ImmutableList.of(2, 3, 5), Ints.asList(arrays.ints()));
        strings[0] = "Hyde";
        ints[0] = -1;
        Assert.assertEquals(ImmutableList.of("Jekyll"), ImmutableList.copyOf(arrays.strings()));
        Assert.assertEquals(ImmutableList.of(2, 3, 5), Ints.asList(arrays.ints()));
    }

    @Test
    public void testSimpleVarArgs() {
        StringValues expectedStringValues = AutoAnnotationTest.AnnotatedClass.class.getAnnotation(StringValues.class);
        StringValues actualStringValues = AutoAnnotationTest.newStringValuesVarArgs("oops");
        StringValues otherStringValues = AutoAnnotationTest.newStringValuesVarArgs(new String[]{  });
        new EqualsTester().addEqualityGroup(expectedStringValues, actualStringValues).addEqualityGroup(otherStringValues).testEquals();
    }

    @Test
    public void testEmpty() {
        Empty expectedEmpty = AutoAnnotationTest.AnnotatedClass.class.getAnnotation(Empty.class);
        Empty actualEmpty = AutoAnnotationTest.newEmpty();
        new EqualsTester().addEqualityGroup(expectedEmpty, actualEmpty).testEquals();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Everything {
        byte aByte();

        short aShort();

        int anInt();

        long aLong();

        float aFloat();

        double aDouble();

        char aChar();

        boolean aBoolean();

        String aString();

        RetentionPolicy anEnum();

        StringValues anAnnotation();

        byte[] bytes();

        short[] shorts();

        int[] ints();

        long[] longs();

        float[] floats();

        double[] doubles();

        char[] chars();

        boolean[] booleans();

        String[] strings();

        RetentionPolicy[] enums();

        StringValues[] annotations();
    }

    @AutoAnnotationTest.Everything(aByte = 1, aShort = 2, anInt = 3, aLong = -4, aFloat = Float.NaN, aDouble = Double.NaN, aChar = '#', aBoolean = true, aString = "maybe\nmaybe not\n", anEnum = RetentionPolicy.RUNTIME, anAnnotation = @StringValues("whatever"), bytes = { 5, 6 }, shorts = {  }, ints = { 7 }, longs = { 8, 9 }, floats = { 10, 11 }, doubles = { Double.NEGATIVE_INFINITY, -12.0, Double.POSITIVE_INFINITY }, chars = { '?', '!', '\n' }, booleans = { false, true, false }, strings = { "ver", "vers", "vert", "verre", "vair" }, enums = { RetentionPolicy.CLASS, RetentionPolicy.RUNTIME }, annotations = { @StringValues({  }), @StringValues({ "foo", "bar" }) })
    private static class AnnotatedWithEverything {}

    // Get an instance of @Everything via reflection on the class AnnotatedWithEverything,
    // fabricate an instance using newEverything that is supposed to be equal to it, and
    // fabricate another instance using newEverything that is supposed to be different.
    private static final AutoAnnotationTest.Everything EVERYTHING_FROM_REFLECTION = AutoAnnotationTest.AnnotatedWithEverything.class.getAnnotation(AutoAnnotationTest.Everything.class);

    private static final AutoAnnotationTest.Everything EVERYTHING_FROM_AUTO = AutoAnnotationTest.newEverything(((byte) (1)), ((short) (2)), 3, (-4), Float.NaN, Double.NaN, '#', true, "maybe\nmaybe not\n", RetentionPolicy.RUNTIME, AutoAnnotationTest.newStringValues(new String[]{ "whatever" }), new byte[]{ 5, 6 }, new short[]{  }, new int[]{ 7 }, new long[]{ 8, 9 }, new float[]{ 10, 11 }, new double[]{ Double.NEGATIVE_INFINITY, -12.0, Double.POSITIVE_INFINITY }, new char[]{ '?', '!', '\n' }, new boolean[]{ false, true, false }, new String[]{ "ver", "vers", "vert", "verre", "vair" }, new RetentionPolicy[]{ RetentionPolicy.CLASS, RetentionPolicy.RUNTIME }, new StringValues[]{ AutoAnnotationTest.newStringValues(new String[]{  }), AutoAnnotationTest.newStringValues(new String[]{ "foo", "bar" }) });

    private static final AutoAnnotationTest.Everything EVERYTHING_FROM_AUTO_COLLECTIONS = AutoAnnotationTest.newEverythingCollections(((byte) (1)), ((short) (2)), 3, (-4), Float.NaN, Double.NaN, '#', true, "maybe\nmaybe not\n", RetentionPolicy.RUNTIME, AutoAnnotationTest.newStringValues(new String[]{ "whatever" }), Arrays.asList(((byte) (5)), ((byte) (6))), Collections.<Short>emptyList(), new ArrayList<Integer>(Collections.singleton(7)), ImmutableSet.of(8L, 9L), ImmutableSortedSet.of(10.0F, 11.0F), new TreeSet<Double>(ImmutableList.of(Double.NEGATIVE_INFINITY, (-12.0), Double.POSITIVE_INFINITY)), new LinkedHashSet<Character>(ImmutableList.of('?', '!', '\n')), ImmutableList.of(false, true, false), ImmutableList.of("ver", "vers", "vert", "verre", "vair"), ImmutableSet.of(RetentionPolicy.CLASS, RetentionPolicy.RUNTIME), ImmutableSet.of(AutoAnnotationTest.newStringValues(new String[]{  }), AutoAnnotationTest.newStringValues(new String[]{ "foo", "bar" })));

    private static final AutoAnnotationTest.Everything EVERYTHING_ELSE_FROM_AUTO = AutoAnnotationTest.newEverything(((byte) (0)), ((short) (0)), 0, 0, 0, 0, '0', false, "", RetentionPolicy.SOURCE, AutoAnnotationTest.newStringValues(new String[]{ "" }), new byte[0], new short[0], new int[0], new long[0], new float[0], new double[0], new char[0], new boolean[0], new String[0], new RetentionPolicy[0], new StringValues[0]);

    private static final AutoAnnotationTest.Everything EVERYTHING_ELSE_FROM_AUTO_COLLECTIONS = AutoAnnotationTest.newEverythingCollections(((byte) (0)), ((short) (0)), 0, 0, 0, 0, '0', false, "", RetentionPolicy.SOURCE, AutoAnnotationTest.newStringValues(new String[]{ "" }), ImmutableList.<Byte>of(), Collections.<Short>emptyList(), new ArrayList<Integer>(), Collections.<Long>emptySet(), ImmutableSortedSet.<Float>of(), new TreeSet<Double>(), new LinkedHashSet<Character>(), ImmutableSet.<Boolean>of(), ImmutableList.<String>of(), ImmutableSet.<RetentionPolicy>of(), Collections.<StringValues>emptySet());

    @Test
    public void testEqualsAndHashCode() {
        new EqualsTester().addEqualityGroup(AutoAnnotationTest.EVERYTHING_FROM_REFLECTION, AutoAnnotationTest.EVERYTHING_FROM_AUTO, AutoAnnotationTest.EVERYTHING_FROM_AUTO_COLLECTIONS).addEqualityGroup(AutoAnnotationTest.EVERYTHING_ELSE_FROM_AUTO, AutoAnnotationTest.EVERYTHING_ELSE_FROM_AUTO_COLLECTIONS).testEquals();
    }

    public static class IntList extends ArrayList<Integer> {
        IntList(Collection<Integer> c) {
            super(c);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface IntArray {
        int[] ints();
    }

    @AutoAnnotationTest.IntArray(ints = { 1, 2, 3 })
    private static class AnnotatedWithIntArray {}

    /**
     * Test that we can represent a primitive array member with a parameter whose type is a collection
     * of the corresponding wrapper type, even if the wrapper type is not explicitly a type parameter.
     * Specifically, if the member is an {@code int[]} then obviously we can represent it as a {@code List<Integer>}, but here we test that we can also represent it as an {@code IntList}, which is
     * only a {@code List<Integer>} by virtue of inheritance. This is a separate test rather than just
     * putting an {@code IntList} parameter into {@link #newEverythingCollections} because we want to
     * check that we are still able to detect the primitive wrapper type even though it's hidden in
     * this way. We need to generate a helper method for every primitive wrapper.
     */
    @Test
    public void testDerivedPrimitiveCollection() {
        AutoAnnotationTest.IntList intList = new AutoAnnotationTest.IntList(ImmutableList.of(1, 2, 3));
        AutoAnnotationTest.IntArray actual = AutoAnnotationTest.newIntArray(intList);
        AutoAnnotationTest.IntArray expected = AutoAnnotationTest.AnnotatedWithIntArray.class.getAnnotation(AutoAnnotationTest.IntArray.class);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testToString() {
        String expected = "@com.google.auto.value.AutoAnnotationTest.Everything(" + ((((((((((((("aByte=1, aShort=2, anInt=3, aLong=-4, aFloat=NaN, aDouble=NaN, aChar='#', " + "aBoolean=true, aString=\"maybe\\nmaybe not\\n\", anEnum=RUNTIME, ") + "anAnnotation=@com.google.auto.value.annotations.StringValues([\"whatever\"]), ") + "bytes=[5, 6], shorts=[], ints=[7], longs=[8, 9], floats=[10.0, 11.0], ") + "doubles=[-Infinity, -12.0, Infinity], ") + "chars=[\'?\', \'!\', \'\\n\'], ") + "booleans=[false, true, false], ") + "strings=[\"ver\", \"vers\", \"vert\", \"verre\", \"vair\"], ") + "enums=[CLASS, RUNTIME], ") + "annotations=[") + "@com.google.auto.value.annotations.StringValues([]), ") + "@com.google.auto.value.annotations.StringValues([\"foo\", \"bar\"])") + "]") + ")");
        Assert.assertEquals(expected, AutoAnnotationTest.EVERYTHING_FROM_AUTO.toString());
        Assert.assertEquals(expected, AutoAnnotationTest.EVERYTHING_FROM_AUTO_COLLECTIONS.toString());
    }

    @Test
    public void testStringQuoting() {
        StringValues instance = AutoAnnotationTest.newStringValues(new String[]{ "", "\r\n", "hello, world", "?amonn", "\u0007\uffef" });
        String expected = "@com.google.auto.value.annotations.StringValues(" + "[\"\", \"\\r\\n\", \"hello, world\", \"\u00c9amonn\", \"\\007\\uffef\"])";
        Assert.assertEquals(expected, instance.toString());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface AnnotationsAnnotation {
        Class<? extends Annotation>[] value();
    }

    @AutoAnnotationTest.AnnotationsAnnotation(AutoAnnotationTest.AnnotationsAnnotation.class)
    static class AnnotatedWithAnnotationsAnnotation {}

    @Test
    public void testGenericArray() {
        AutoAnnotationTest.AnnotationsAnnotation generated = AutoAnnotationTest.newAnnotationsAnnotation(ImmutableList.<Class<? extends Annotation>>of(AutoAnnotationTest.AnnotationsAnnotation.class));
        AutoAnnotationTest.AnnotationsAnnotation fromReflect = AutoAnnotationTest.AnnotatedWithAnnotationsAnnotation.class.getAnnotation(AutoAnnotationTest.AnnotationsAnnotation.class);
        Assert.assertEquals(fromReflect, generated);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface ClassesAnnotation {
        Class<?>[] value();
    }

    @AutoAnnotationTest.ClassesAnnotation(AutoAnnotationTest.AnnotationsAnnotation.class)
    static class AnnotatedWithClassesAnnotation {}

    @Test
    public void testWildcardArray() {
        AutoAnnotationTest.ClassesAnnotation generated = AutoAnnotationTest.newClassesAnnotation(Arrays.<Class<?>>asList(AutoAnnotationTest.AnnotationsAnnotation.class));
        AutoAnnotationTest.ClassesAnnotation fromReflect = AutoAnnotationTest.AnnotatedWithClassesAnnotation.class.getAnnotation(AutoAnnotationTest.ClassesAnnotation.class);
        Assert.assertEquals(fromReflect, generated);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface IntegersAnnotation {
        int one() default Integer.MAX_VALUE;

        int two() default Integer.MAX_VALUE;

        int three();
    }

    @AutoAnnotationTest.IntegersAnnotation(three = 23)
    static class AnnotatedWithIntegersAnnotation {}

    @Test
    public void testConstantOverflowInHashCode() {
        AutoAnnotationTest.IntegersAnnotation generated = AutoAnnotationTest.newIntegersAnnotation(23);
        AutoAnnotationTest.IntegersAnnotation fromReflect = AutoAnnotationTest.AnnotatedWithIntegersAnnotation.class.getAnnotation(AutoAnnotationTest.IntegersAnnotation.class);
        new EqualsTester().addEqualityGroup(generated, fromReflect).testEquals();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface EverythingWithDefaults {
        byte aByte() default 5;

        short aShort() default 17;

        int anInt() default 23;

        long aLong() default 1729;

        float aFloat() default 5;

        double aDouble() default 17;

        char aChar() default 'x';

        boolean aBoolean() default true;

        String aString() default "whatever";

        RetentionPolicy anEnum() default RetentionPolicy.CLASS;

        // We don't yet support defaulting annotation values.
        // StringValues anAnnotation() default @StringValues({"foo", "bar"});
        byte[] bytes() default { 1, 2 };

        short[] shorts() default { 3, 4 };

        int[] ints() default { 5, 6 };

        long[] longs() default { 7, 8 };

        float[] floats() default { 9, 10 };

        double[] doubles() default { 11, 12 };

        char[] chars() default { 'D', 'E' };

        boolean[] booleans() default { true, false };

        String[] strings() default { "vrai", "faux" };

        RetentionPolicy[] enums() default { RetentionPolicy.SOURCE, RetentionPolicy.CLASS };
    }

    @AutoAnnotationTest.EverythingWithDefaults
    static class AnnotatedWithEverythingWithDefaults {}

    @Test
    public void testDefaultedValues() {
        AutoAnnotationTest.EverythingWithDefaults generated = AutoAnnotationTest.newEverythingWithDefaults();
        AutoAnnotationTest.EverythingWithDefaults fromReflect = AutoAnnotationTest.AnnotatedWithEverythingWithDefaults.class.getAnnotation(AutoAnnotationTest.EverythingWithDefaults.class);
        new EqualsTester().addEqualityGroup(generated, fromReflect).testEquals();
    }
}

