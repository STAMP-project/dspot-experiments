/**
 * Copyright (C) 2014 RoboVM AB
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
package org.robovm.rt.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.rt.bro.annotation.Bridge;


/**
 * Tests the annotation implementation classes the {@code AnnotationImplPlugin}
 * compiler plugin generates.
 */
public class AnnotationTest {
    public @interface Anno1 {}

    @Retention(RetentionPolicy.SOURCE)
    public @interface Anno2 {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Anno3 {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Anno4 {
        boolean boolean1();

        boolean boolean2() default true;

        byte byte1();

        byte byte2() default 100;

        short short1();

        short short2() default 23000;

        char char1();

        char char2() default 46000;

        int int1();

        int int2() default 2000000000;

        long long1();

        long long2() default 20000000000L;

        float float1();

        float float2() default 1234567.6F;

        double double1();

        double double2() default 7654321.234567;

        String string1();

        String string2() default "string2default";

        Class<?> class1();

        Class<?> class2() default byte.class;

        Bridge anno1();

        Bridge anno2() default @Bridge(symbol = "foo", dynamic = true);

        boolean[] booleans1();

        boolean[] booleans2() default { true, false, true };

        byte[] bytes1();

        byte[] bytes2() default { 100, 101, 102 };

        short[] shorts1();

        short[] shorts2() default { 23000, 23001, 23002 };

        char[] chars1();

        char[] chars2() default { 46000, 46001, 46002 };

        int[] ints1();

        int[] ints2() default { 2000000000, 2000000001, 2000000002 };

        long[] longs1();

        long[] longs2() default { 20000000000L, 20000000001L, 20000000002L };

        float[] floats1();

        float[] floats2() default { 1234567.6F, 2234567.8F, 3234567.8F };

        double[] doubles1();

        double[] doubles2() default { 7654321.234567, 8654321.234567, 9654321.234567 };

        String[] strings1();

        String[] strings2() default { "a", "b", "c" };
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Anno5 {
        String v1() default "v1";

        String v2() default "v2";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Anno6 {
        AnnotationTest.Anno1 anno1() default @AnnotationTest.Anno1;

        AnnotationTest.Anno2 anno2() default @AnnotationTest.Anno2;
    }

    @AnnotationTest.Anno3
    public static class AnnoHost1 {}

    @AnnotationTest.Anno3
    public static class AnnoHost2 {}

    @AnnotationTest.Anno4(boolean1 = false, byte1 = 101, short1 = 23001, char1 = 46001, int1 = 2000000001, long1 = 20000000001L, float1 = 2234567.8F, double1 = 8654321.234567, string1 = "string1value", class1 = Integer.class, anno1 = @Bridge(symbol = "bar", dynamic = true), booleans1 = { false, true }, bytes1 = { 102, 101, 100 }, shorts1 = { 23002, 23001, 23000 }, chars1 = { 46002, 46001, 46000 }, ints1 = { 2000000002, 2000000001, 2000000000 }, longs1 = { 20000000002L, 20000000001L, 20000000000L }, floats1 = { 3234567.8F, 2234567.8F, 1234567.6F }, doubles1 = { 9654321.234567, 8654321.234567, 7654321.234567 }, strings1 = { "c", "b", "a" })
    public static class AnnoHost3 {}

    @AnnotationTest.Anno4(boolean1 = false, byte1 = 101, short1 = 23001, char1 = 46001, int1 = 2000000001, long1 = 20000000001L, float1 = 2234567.8F, double1 = 8654321.234567, string1 = "string1value", class1 = Integer.class, anno1 = @Bridge(symbol = "bar", dynamic = true), booleans1 = { false, true }, bytes1 = { 102, 101, 100 }, shorts1 = { 23002, 23001, 23000 }, chars1 = { 46002, 46001, 46000 }, ints1 = { 2000000002, 2000000001, 2000000000 }, longs1 = { 20000000002L, 20000000001L, 20000000000L }, floats1 = { 3234567.8F, 2234567.8F, 1234567.6F }, doubles1 = { 9654321.234567, 8654321.234567, 7654321.234567 }, strings1 = { "c", "b", "a" })
    public static class AnnoHost4 {}

    @AnnotationTest.Anno4(boolean1 = true// Only diff!
    , byte1 = 101, short1 = 23001, char1 = 46001, int1 = 2000000001, long1 = 20000000001L, float1 = 2234567.8F, double1 = 8654321.234567, string1 = "string1value", class1 = Integer.class, anno1 = @Bridge(symbol = "bar", dynamic = true), booleans1 = { false, true }, bytes1 = { 102, 101, 100 }, shorts1 = { 23002, 23001, 23000 }, chars1 = { 46002, 46001, 46000 }, ints1 = { 2000000002, 2000000001, 2000000000 }, longs1 = { 20000000002L, 20000000001L, 20000000000L }, floats1 = { 3234567.8F, 2234567.8F, 1234567.6F }, doubles1 = { 9654321.234567, 8654321.234567, 7654321.234567 }, strings1 = { "c", "b", "a" })
    public static class AnnoHost5 {}

    @AnnotationTest.Anno5
    public static class AnnoHost6 {}

    @AnnotationTest.Anno5
    public static class AnnoHost7 {}

    @AnnotationTest.Anno5(v1 = "a1", v2 = "a2")
    public static class AnnoHost8 {}

    @AnnotationTest.Anno5(v1 = "a1", v2 = "a2")
    public static class AnnoHost9 {}

    @AnnotationTest.Anno6
    public static class AnnoHost10 {}

    @Test
    public void testIsFastImpl() {
        AnnotationTest.Anno3 anno = AnnotationTest.AnnoHost1.class.getAnnotation(AnnotationTest.Anno3.class);
        Assert.assertTrue((anno instanceof Annotation));
    }

    @Test
    public void testAnnotationMethodsNoMembers() {
        AnnotationTest.Anno3 anno = AnnotationTest.AnnoHost1.class.getAnnotation(AnnotationTest.Anno3.class);
        Assert.assertSame(AnnotationTest.Anno3.class, anno.annotationType());
        Assert.assertEquals(0, anno.hashCode());
        Assert.assertTrue(anno.equals(anno));
        Assert.assertEquals("@org.robovm.rt.annotation.AnnotationTest$Anno3()", anno.toString());
    }

    @Test
    public void testSingletonAnnotation() {
        AnnotationTest.Anno3 anno1 = AnnotationTest.AnnoHost1.class.getAnnotation(AnnotationTest.Anno3.class);
        AnnotationTest.Anno3 anno2 = AnnotationTest.AnnoHost2.class.getAnnotation(AnnotationTest.Anno3.class);
        Assert.assertSame(anno1, anno2);
    }

    @Test
    public void testAnnotationMembersWithoutDefaults() {
        AnnotationTest.Anno4 anno = AnnotationTest.AnnoHost3.class.getAnnotation(AnnotationTest.Anno4.class);
        Assert.assertEquals(false, anno.boolean1());
        Assert.assertEquals(101, anno.byte1());
        Assert.assertEquals(23001, anno.short1());
        Assert.assertEquals(46001, anno.char1());
        Assert.assertEquals(2000000001, anno.int1());
        Assert.assertEquals(20000000001L, anno.long1());
        Assert.assertEquals(2234567.8F, anno.float1(), 0.0F);
        Assert.assertEquals(8654321.234567, anno.double1(), 0.0);
        Assert.assertEquals("string1value", anno.string1());
        Assert.assertEquals(Integer.class, anno.class1());
        Assert.assertEquals("bar", anno.anno1().symbol());
        Assert.assertEquals(true, anno.anno1().dynamic());
        Assert.assertTrue(Arrays.equals(new boolean[]{ false, true }, anno.booleans1()));
        Assert.assertArrayEquals(new byte[]{ 102, 101, 100 }, anno.bytes1());
        Assert.assertArrayEquals(new short[]{ 23002, 23001, 23000 }, anno.shorts1());
        Assert.assertArrayEquals(new char[]{ 46002, 46001, 46000 }, anno.chars1());
        Assert.assertArrayEquals(new int[]{ 2000000002, 2000000001, 2000000000 }, anno.ints1());
        Assert.assertArrayEquals(new long[]{ 20000000002L, 20000000001L, 20000000000L }, anno.longs1());
        Assert.assertArrayEquals(new float[]{ 3234567.8F, 2234567.8F, 1234567.6F }, anno.floats1(), 0.0F);
        Assert.assertArrayEquals(new double[]{ 9654321.234567, 8654321.234567, 7654321.234567 }, anno.doubles1(), 0.0);
        Assert.assertArrayEquals(new String[]{ "c", "b", "a" }, anno.strings1());
    }

    @Test
    public void testAnnotationMemberDefaults() {
        AnnotationTest.Anno4 anno = AnnotationTest.AnnoHost3.class.getAnnotation(AnnotationTest.Anno4.class);
        Assert.assertEquals(true, anno.boolean2());
        Assert.assertEquals(100, anno.byte2());
        Assert.assertEquals(23000, anno.short2());
        Assert.assertEquals(46000, anno.char2());
        Assert.assertEquals(2000000000, anno.int2());
        Assert.assertEquals(20000000000L, anno.long2());
        Assert.assertEquals(1234567.6F, anno.float2(), 0.0F);
        Assert.assertEquals(7654321.234567, anno.double2(), 0.0);
        Assert.assertEquals("string2default", anno.string2());
        Assert.assertEquals(byte.class, anno.class2());
        Assert.assertEquals("foo", anno.anno2().symbol());
        Assert.assertEquals(true, anno.anno2().dynamic());
        Assert.assertTrue(Arrays.equals(new boolean[]{ true, false, true }, anno.booleans2()));
        Assert.assertArrayEquals(new byte[]{ 100, 101, 102 }, anno.bytes2());
        Assert.assertArrayEquals(new short[]{ 23000, 23001, 23002 }, anno.shorts2());
        Assert.assertArrayEquals(new char[]{ 46000, 46001, 46002 }, anno.chars2());
        Assert.assertArrayEquals(new int[]{ 2000000000, 2000000001, 2000000002 }, anno.ints2());
        Assert.assertArrayEquals(new long[]{ 20000000000L, 20000000001L, 20000000002L }, anno.longs2());
        Assert.assertArrayEquals(new float[]{ 1234567.6F, 2234567.8F, 3234567.8F }, anno.floats2(), 0.0F);
        Assert.assertArrayEquals(new double[]{ 7654321.234567, 8654321.234567, 9654321.234567 }, anno.doubles2(), 0.0);
        Assert.assertArrayEquals(new String[]{ "a", "b", "c" }, anno.strings2());
    }

    @Test
    public void testEquals() {
        AnnotationTest.Anno4 anno1 = AnnotationTest.AnnoHost3.class.getAnnotation(AnnotationTest.Anno4.class);
        AnnotationTest.Anno4 anno2 = AnnotationTest.AnnoHost4.class.getAnnotation(AnnotationTest.Anno4.class);
        AnnotationTest.Anno4 anno3 = AnnotationTest.AnnoHost5.class.getAnnotation(AnnotationTest.Anno4.class);
        Assert.assertNotSame(anno1, anno2);
        Assert.assertTrue(anno1.equals(anno2));
        Assert.assertFalse(anno1.equals(anno3));
    }

    @Test
    public void testHashCodeWithMembers() {
        AnnotationTest.Anno4 anno1 = AnnotationTest.AnnoHost3.class.getAnnotation(AnnotationTest.Anno4.class);
        AnnotationTest.Anno4 anno2 = AnnotationTest.AnnoHost4.class.getAnnotation(AnnotationTest.Anno4.class);
        AnnotationTest.Anno4 anno3 = AnnotationTest.AnnoHost5.class.getAnnotation(AnnotationTest.Anno4.class);
        Assert.assertFalse((0 == (anno1.hashCode())));
        Assert.assertFalse((0 == (anno3.hashCode())));
        Assert.assertEquals(anno1.hashCode(), anno2.hashCode());
        Assert.assertFalse(((anno1.hashCode()) == (anno3.hashCode())));
    }

    @Test
    public void testAccessorsReturnCopies() throws Exception {
        AnnotationTest.Anno4 anno = AnnotationTest.AnnoHost3.class.getAnnotation(AnnotationTest.Anno4.class);
        Assert.assertSame(anno.class1(), anno.class1());
        Assert.assertSame(anno.class2(), anno.class2());
        Assert.assertSame(anno.string1(), anno.string1());
        Assert.assertSame(anno.string2(), anno.string2());
        Assert.assertSame(anno.anno1(), anno.anno1());
        Assert.assertSame(anno.anno2(), anno.anno2());
        Assert.assertNotSame(anno.booleans1(), anno.booleans1());
        Assert.assertNotSame(anno.booleans2(), anno.booleans2());
        Assert.assertNotSame(anno.bytes1(), anno.bytes1());
        Assert.assertNotSame(anno.bytes2(), anno.bytes2());
        Assert.assertNotSame(anno.shorts1(), anno.shorts1());
        Assert.assertNotSame(anno.shorts2(), anno.shorts2());
        Assert.assertNotSame(anno.chars1(), anno.chars1());
        Assert.assertNotSame(anno.chars2(), anno.chars2());
        Assert.assertNotSame(anno.ints1(), anno.ints1());
        Assert.assertNotSame(anno.ints2(), anno.ints2());
        Assert.assertNotSame(anno.longs1(), anno.longs1());
        Assert.assertNotSame(anno.longs2(), anno.longs2());
        Assert.assertNotSame(anno.floats1(), anno.floats1());
        Assert.assertNotSame(anno.floats2(), anno.floats2());
        Assert.assertNotSame(anno.double1(), anno.double1());
        Assert.assertNotSame(anno.double2(), anno.double2());
        Assert.assertNotSame(anno.strings1(), anno.strings1());
        Assert.assertNotSame(anno.strings2(), anno.strings2());
    }

    @Test
    public void testSingletonWhenOnlyDefaults() throws Exception {
        AnnotationTest.Anno5 anno1 = AnnotationTest.AnnoHost6.class.getAnnotation(AnnotationTest.Anno5.class);
        AnnotationTest.Anno5 anno2 = AnnotationTest.AnnoHost7.class.getAnnotation(AnnotationTest.Anno5.class);
        AnnotationTest.Anno5 anno3 = AnnotationTest.AnnoHost8.class.getAnnotation(AnnotationTest.Anno5.class);
        AnnotationTest.Anno5 anno4 = AnnotationTest.AnnoHost9.class.getAnnotation(AnnotationTest.Anno5.class);
        Assert.assertSame(anno1, anno2);
        Assert.assertNotSame(anno1, anno3);
        Assert.assertNotSame(anno3, anno4);
    }

    @Test
    public void testNonRuntimeVisibleAnnotationsAsDefaultValues() throws Exception {
        AnnotationTest.Anno6 anno = AnnotationTest.AnnoHost10.class.getAnnotation(AnnotationTest.Anno6.class);
        Assert.assertTrue(((anno.anno1()) instanceof AnnotationTest.Anno1));
        Assert.assertTrue(((anno.anno2()) instanceof AnnotationTest.Anno2));
    }
}

