/**
 * Copyright (C) 2014 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.compiler.plugin.annotation;


import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.compiler.ModuleBuilder;
import org.robovm.compiler.clazz.Clazz;
import org.robovm.compiler.config.Config;
import org.robovm.rt.bro.annotation.Bridge;


/**
 * Tests {@link AnnotationImplPlugin}.
 */
public class AnnotationImplPluginTest {
    static Config config;

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
        AnnotationImplPluginTest.Anno1 anno1() default @AnnotationImplPluginTest.Anno1;

        AnnotationImplPluginTest.Anno2 anno2() default @AnnotationImplPluginTest.Anno2;
    }

    @AnnotationImplPluginTest.Anno3
    @AnnotationImplPluginTest.Anno4(boolean1 = false, byte1 = 101, short1 = 23001, char1 = 46001, int1 = 2000000001, long1 = 20000000001L, float1 = 2234567.8F, double1 = 8654321.234567, string1 = "string1value", class1 = Integer.class, anno1 = @Bridge(symbol = "bar", dynamic = true), booleans1 = { false, true }, bytes1 = { 102, 101, 100 }, shorts1 = { 23002, 23001, 23000 }, chars1 = { 46002, 46001, 46000 }, ints1 = { 2000000002, 2000000001, 2000000000 }, longs1 = { 20000000002L, 20000000001L, 20000000000L }, floats1 = { 3234567.8F, 2234567.8F, 1234567.6F }, doubles1 = { 9654321.234567, 8654321.234567, 7654321.234567 }, strings1 = { "c", "b", "a" })
    public static class AnnoHost {}

    @Test
    public void testNotVisibleAnno() throws Exception {
        AnnotationImplPlugin plugin = new AnnotationImplPlugin();
        Clazz clazz = toClazz(AnnotationImplPluginTest.Anno1.class);
        clazz.resetClazzInfo();
        plugin.beforeClass(AnnotationImplPluginTest.config, clazz, new ModuleBuilder());
        Assert.assertFalse(clazz.getClazzInfo().getAllDependencies().isEmpty());
    }

    @Test
    public void testCreateSingleton1() throws Exception {
        final File implFile = createAnnoImpl(AnnotationImplPluginTest.Anno3.class);
        Class<?> implClass = loadClassFromFile(implFile, ((AnnotationImplPluginTest.Anno3.class.getName()) + "$Impl"));
        AnnotationImplPluginTest.Anno3 impl1 = ((AnnotationImplPluginTest.Anno3) (implClass.getMethod("$createSingleton").invoke(null)));
        AnnotationImplPluginTest.Anno3 impl2 = ((AnnotationImplPluginTest.Anno3) (implClass.getMethod("$createSingleton").invoke(null)));
        // $createSingleton() should always return the same instance
        Assert.assertSame(impl1, impl2);
    }

    @Test
    public void testCreateSingleton2() throws Exception {
        final File implFile = createAnnoImpl(AnnotationImplPluginTest.Anno4.class);
        Class<?> implClass = loadClassFromFile(implFile, ((AnnotationImplPluginTest.Anno4.class.getName()) + "$Impl"));
        AnnotationImplPluginTest.Anno4 impl1 = ((AnnotationImplPluginTest.Anno4) (implClass.getMethod("$createSingleton").invoke(null)));
        AnnotationImplPluginTest.Anno4 impl2 = ((AnnotationImplPluginTest.Anno4) (implClass.getMethod("$createSingleton").invoke(null)));
        // $createSingleton() should always return the same instance
        Assert.assertSame(impl1, impl2);
    }

    @Test
    public void testCreateImplNoMembers() throws Exception {
        final File implFile = createAnnoImpl(AnnotationImplPluginTest.Anno3.class);
        Class<?> implClass = loadClassFromFile(implFile, ((AnnotationImplPluginTest.Anno3.class.getName()) + "$Impl"));
        AnnotationImplPluginTest.Anno3 impl1 = ((AnnotationImplPluginTest.Anno3) (implClass.getMethod("$create").invoke(null)));
        Assert.assertSame(AnnotationImplPluginTest.Anno3.class, impl1.annotationType());
        // $create() should always return the same instance when there are no members
        AnnotationImplPluginTest.Anno3 impl2 = ((AnnotationImplPluginTest.Anno3) (implClass.getMethod("$create").invoke(null)));
        Assert.assertSame(impl1, impl2);
        // Make sure our generated Anno3 impl is compatible with Anno3 impl instances generated by the current JVM
        AnnotationImplPluginTest.Anno3 annotation = AnnotationImplPluginTest.AnnoHost.class.getAnnotation(AnnotationImplPluginTest.Anno3.class);
        Assert.assertTrue(annotation.equals(impl1));
        Assert.assertTrue(impl1.equals(annotation));
        Assert.assertEquals(annotation.hashCode(), impl1.hashCode());
        Assert.assertEquals(annotation.toString(), impl1.toString());
        Assert.assertSame(annotation.annotationType(), impl1.annotationType());
    }

    @Test
    public void testCompatibleWithCurrentJvm() throws Exception {
        final File implFile = createAnnoImpl(AnnotationImplPluginTest.Anno4.class);
        Class<?> implClass = loadClassFromFile(implFile, ((AnnotationImplPluginTest.Anno4.class.getName()) + "$Impl"));
        AnnotationImplPluginTest.Anno4 impl1 = ((AnnotationImplPluginTest.Anno4) (implClass.getMethod("$create").invoke(null)));
        Assert.assertSame(AnnotationImplPluginTest.Anno4.class, impl1.annotationType());
        Assert.assertEquals(impl1, impl1);
        // Make sure arrays are cloned before returned
        Assert.assertNotSame(impl1.booleans2(), impl1.booleans2());
        Assert.assertNotSame(impl1.bytes2(), impl1.bytes2());
        Assert.assertNotSame(impl1.shorts2(), impl1.shorts2());
        Assert.assertNotSame(impl1.chars2(), impl1.chars2());
        Assert.assertNotSame(impl1.ints2(), impl1.ints2());
        Assert.assertNotSame(impl1.longs2(), impl1.longs2());
        Assert.assertNotSame(impl1.floats2(), impl1.floats2());
        Assert.assertNotSame(impl1.doubles2(), impl1.doubles2());
        Assert.assertNotSame(impl1.strings2(), impl1.strings2());
        // $create() should return a new instance
        AnnotationImplPluginTest.Anno4 impl2 = ((AnnotationImplPluginTest.Anno4) (implClass.getMethod("$create").invoke(null)));
        Assert.assertNotSame(impl1, impl2);
        // impl1 and impl2 should be equal. This tests the fastEquals() method.
        Assert.assertEquals(impl2, impl1);
        // Make sure our generated Anno4 impl is compatible with Anno4 impl instances generated by the current JVM
        AnnotationImplPluginTest.Anno4 annotation = AnnotationImplPluginTest.AnnoHost.class.getAnnotation(AnnotationImplPluginTest.Anno4.class);
        // At first the instance should not be equal since our anno instances haven't got any values set yet except defaults.
        Assert.assertFalse(impl1.equals(annotation));
        // And hash codes should not match
        Assert.assertFalse(((annotation.hashCode()) == (impl1.hashCode())));
        // Make sure default values are equal
        Assert.assertEquals(annotation.boolean2(), impl1.boolean2());
        Assert.assertEquals(annotation.byte2(), impl1.byte2());
        Assert.assertEquals(annotation.short2(), impl1.short2());
        Assert.assertEquals(annotation.char2(), impl1.char2());
        Assert.assertEquals(annotation.int2(), impl1.int2());
        Assert.assertEquals(annotation.long2(), impl1.long2());
        Assert.assertEquals(annotation.float2(), impl1.float2(), 0.0F);
        Assert.assertEquals(annotation.double2(), impl1.double2(), 0.0);
        Assert.assertEquals(annotation.string2(), impl1.string2());
        Assert.assertEquals(annotation.class2(), impl1.class2());
        Assert.assertEquals(annotation.anno2(), impl1.anno2());
        Assert.assertTrue(Arrays.equals(annotation.booleans2(), impl1.booleans2()));
        Assert.assertArrayEquals(annotation.bytes2(), impl1.bytes2());
        Assert.assertArrayEquals(annotation.shorts2(), impl1.shorts2());
        Assert.assertArrayEquals(annotation.chars2(), impl1.chars2());
        Assert.assertArrayEquals(annotation.ints2(), impl1.ints2());
        Assert.assertArrayEquals(annotation.longs2(), impl1.longs2());
        Assert.assertTrue(Arrays.equals(annotation.floats2(), impl1.floats2()));
        Assert.assertTrue(Arrays.equals(annotation.doubles2(), impl1.doubles2()));
        Assert.assertArrayEquals(annotation.strings2(), impl1.strings2());
        // Set the values which have no defaults and compare
        setAnnotationMemberValue(impl1, "boolean1", annotation.boolean1());
        setAnnotationMemberValue(impl1, "byte1", annotation.byte1());
        setAnnotationMemberValue(impl1, "short1", annotation.short1());
        setAnnotationMemberValue(impl1, "char1", annotation.char1());
        setAnnotationMemberValue(impl1, "int1", annotation.int1());
        setAnnotationMemberValue(impl1, "long1", annotation.long1());
        setAnnotationMemberValue(impl1, "float1", annotation.float1());
        setAnnotationMemberValue(impl1, "double1", annotation.double1());
        setAnnotationMemberValue(impl1, "string1", annotation.string1());
        setAnnotationMemberValue(impl1, "class1", annotation.class1());
        setAnnotationMemberValue(impl1, "anno1", annotation.anno1());
        setAnnotationMemberValue(impl1, "booleans1", annotation.booleans1());
        setAnnotationMemberValue(impl1, "bytes1", annotation.bytes1());
        setAnnotationMemberValue(impl1, "shorts1", annotation.shorts1());
        setAnnotationMemberValue(impl1, "chars1", annotation.chars1());
        setAnnotationMemberValue(impl1, "ints1", annotation.ints1());
        setAnnotationMemberValue(impl1, "longs1", annotation.longs1());
        setAnnotationMemberValue(impl1, "floats1", annotation.floats1());
        setAnnotationMemberValue(impl1, "doubles1", annotation.doubles1());
        setAnnotationMemberValue(impl1, "strings1", annotation.strings1());
        Assert.assertEquals(annotation.boolean1(), impl1.boolean1());
        Assert.assertEquals(annotation.byte1(), impl1.byte1());
        Assert.assertEquals(annotation.short1(), impl1.short1());
        Assert.assertEquals(annotation.char1(), impl1.char1());
        Assert.assertEquals(annotation.int1(), impl1.int1());
        Assert.assertEquals(annotation.long1(), impl1.long1());
        Assert.assertEquals(annotation.float1(), impl1.float1(), 0.0F);
        Assert.assertEquals(annotation.double1(), impl1.double1(), 0.0);
        Assert.assertEquals(annotation.string1(), impl1.string1());
        Assert.assertEquals(annotation.class1(), impl1.class1());
        Assert.assertEquals(annotation.anno1(), impl1.anno1());
        Assert.assertTrue(Arrays.equals(annotation.booleans1(), impl1.booleans1()));
        Assert.assertArrayEquals(annotation.bytes1(), impl1.bytes1());
        Assert.assertArrayEquals(annotation.shorts1(), impl1.shorts1());
        Assert.assertArrayEquals(annotation.chars1(), impl1.chars1());
        Assert.assertArrayEquals(annotation.ints1(), impl1.ints1());
        Assert.assertArrayEquals(annotation.longs1(), impl1.longs1());
        Assert.assertTrue(Arrays.equals(annotation.floats1(), impl1.floats1()));
        Assert.assertTrue(Arrays.equals(annotation.doubles1(), impl1.doubles1()));
        Assert.assertArrayEquals(annotation.strings1(), impl1.strings1());
        Assert.assertTrue(annotation.equals(impl1));
        Assert.assertTrue(impl1.equals(annotation));
        Assert.assertEquals(annotation.hashCode(), impl1.hashCode());
        Assert.assertSame(annotation.annotationType(), impl1.annotationType());
        // We cannot require that toString() returns the same string as the ref
        // impl does. We lay out members as they occur in the annotation definition
        // while the ref impl orders them differently. We can however check that the
        // number of chars and the sum of all chars are the same.
        Assert.assertEquals(annotation.toString().length(), impl1.toString().length());
        Assert.assertEquals(sum(annotation.toString()), sum(impl1.toString()));
    }

    @Test
    public void testNonRuntimeVisibleAnnotationsAsDefaultValues() throws Exception {
        final File implFile = createAnnoImpl(AnnotationImplPluginTest.Anno5.class);
        Class<?> implClass = loadClassFromFile(implFile, ((AnnotationImplPluginTest.Anno5.class.getName()) + "$Impl"));
        AnnotationImplPluginTest.Anno5 impl = ((AnnotationImplPluginTest.Anno5) (implClass.getMethod("$create").invoke(null)));
        Assert.assertTrue(((impl.anno1()) instanceof AnnotationImplPluginTest.Anno1));
        Assert.assertTrue(((impl.anno2()) instanceof AnnotationImplPluginTest.Anno2));
    }
}

