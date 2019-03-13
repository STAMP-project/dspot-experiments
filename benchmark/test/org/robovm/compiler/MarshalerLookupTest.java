/**
 * Copyright (C) 2013 RoboVM AB
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
package org.robovm.compiler;


import ArrayMarshalers.ByteArrayMarshaler;
import ArrayMarshalers.ShortArrayMarshaler;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.compiler.MarshalerLookup.Marshaler;
import org.robovm.compiler.config.Config;
import org.robovm.compiler.config.Config.Home;
import org.robovm.rt.bro.MarshalerFlags;
import org.robovm.rt.bro.Struct;
import org.robovm.rt.bro.annotation.Array;
import org.robovm.rt.bro.annotation.Bridge;
import org.robovm.rt.bro.annotation.Callback;
import org.robovm.rt.bro.annotation.Marshalers;
import org.robovm.rt.bro.annotation.MarshalsArray;
import org.robovm.rt.bro.annotation.MarshalsPointer;
import org.robovm.rt.bro.annotation.MarshalsValue;
import org.robovm.rt.bro.annotation.StructMember;
import soot.SootMethod;


/**
 * Tests {@link MarshalerLookup}.
 */
public class MarshalerLookupTest {
    private static Config config;

    @Test
    public void testFindMarshalersBuiltins() {
        MarshalerLookup lookup = new MarshalerLookup(MarshalerLookupTest.config);
        List<Marshaler> l = lookup.findMarshalers(toSootClass(String.class));
        Assert.assertFalse(l.isEmpty());
        Assert.assertSame(toClazz(ByteArrayMarshaler.class), l.get(0).getClazz());
        Assert.assertSame(toClazz(ShortArrayMarshaler.class), l.get(1).getClazz());
    }

    @Test
    public void testFindMarshalersSkipBuiltins() {
        MarshalerLookup lookup = searchBuiltins(false);
        List<Marshaler> l = lookup.findMarshalers(toSootClass(MarshalerLookupTest.C1.class));
        Assert.assertEquals(1, l.size());
        Assert.assertSame(toClazz(MarshalerLookupTest.M1.class), l.get(0).getClazz());
    }

    @Test
    public void testFindMarshalersSearchesSuperclasses() {
        MarshalerLookup lookup = searchBuiltins(false);
        List<Marshaler> l = lookup.findMarshalers(toSootClass(MarshalerLookupTest.C2.class));
        Assert.assertEquals(1, l.size());
        Assert.assertSame(toClazz(MarshalerLookupTest.M1.class), l.get(0).getClazz());
    }

    @Test
    public void testFindMarshalersSearchesInterfaces() {
        MarshalerLookup lookup = searchBuiltins(false);
        List<Marshaler> l = lookup.findMarshalers(toSootClass(MarshalerLookupTest.C4.class));
        Assert.assertEquals(7, l.size());
        Assert.assertSame(toClazz(MarshalerLookupTest.M7.class), l.get(0).getClazz());
        Assert.assertSame(toClazz(MarshalerLookupTest.M1.class), l.get(1).getClazz());
        Assert.assertSame(toClazz(MarshalerLookupTest.M3.class), l.get(2).getClazz());
        Assert.assertSame(toClazz(MarshalerLookupTest.M6.class), l.get(3).getClazz());
        Assert.assertSame(toClazz(MarshalerLookupTest.M2.class), l.get(4).getClazz());
        Assert.assertSame(toClazz(MarshalerLookupTest.M4.class), l.get(5).getClazz());
        Assert.assertSame(toClazz(MarshalerLookupTest.M5.class), l.get(6).getClazz());
    }

    @Test
    public void testFindMarshalersSearchesOuterClasses() {
        MarshalerLookup lookup = searchBuiltins(false);
        List<Marshaler> l = lookup.findMarshalers(toSootClass(MarshalerLookupTest.C2.Inner.InnerInner.class));
        Assert.assertEquals(1, l.size());
        Assert.assertSame(toClazz(MarshalerLookupTest.M1.class), l.get(0).getClazz());
    }

    @Test
    public void testFindMarshalerMethodUnsuccessfulSearchNoMarshalers() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod method = toSootClass(MarshalerLookupTest.Foo1.class).getMethodByName("foo");
        try {
            lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
        try {
            lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method, 0));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testFindMarshalerMethodUnsuccessfulSearchNoMatchingMarshaler() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod method = toSootClass(MarshalerLookupTest.Foo2.class).getMethodByName("foo");
        try {
            lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
        try {
            lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method, 0));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testFindMarshalerMethodUnsuccessfulWithMarshalerAnnotation() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod method = toSootClass(MarshalerLookupTest.Foo3.class).getMethodByName("foo");
        try {
            lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
        try {
            lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method, 0));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testFindMarshalerMethodClassMatchPointer() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod method = toSootClass(MarshalerLookupTest.Foo4.class).getMethodByName("foo1");
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("stringToObject"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method)).getMethod());
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("stringToNative"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method, 0)).getMethod());
    }

    @Test
    public void testFindMarshalerMethodClassMatchValue() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod method = toSootClass(MarshalerLookupTest.Foo4.class).getMethodByName("foo2");
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("integerToObject"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method)).getMethod());
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("integerToNative"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method, 0)).getMethod());
    }

    @Test
    public void testFindMarshalerMethodInterfaceMatchPointer() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod method = toSootClass(MarshalerLookupTest.Foo4.class).getMethodByName("foo3");
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("charSequenceToObject"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method)).getMethod());
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("charSequenceToNative"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method, 0)).getMethod());
    }

    @Test
    public void testFindMarshalerMethodSuperclassMatchValue() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod method = toSootClass(MarshalerLookupTest.Foo4.class).getMethodByName("foo4");
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("numberToObject"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method)).getMethod());
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("numberToNative"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method, 0)).getMethod());
    }

    @Test
    public void testFindMarshalerMethodUnsuccessfulSearchUnsupportedCallTypeCallback() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod method = toSootClass(MarshalerLookupTest.Foo4.class).getMethodByName("foo5");
        try {
            lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method)).getMethod();
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
        try {
            lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method, 0)).getMethod();
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testFindMarshalerMethodClassMatchArray() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod method = toSootClass(MarshalerLookupTest.Foo4.class).getMethodByName("foo6");
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("stringArrayToObject"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method)).getMethod());
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("stringArrayToNative"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method, 0)).getMethod());
    }

    @Test
    public void testFindMarshalerMethodSuperclassMatchArray() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod method = toSootClass(MarshalerLookupTest.Foo4.class).getMethodByName("foo7");
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("numberArrayToObject"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method)).getMethod());
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("numberArrayToNative"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method, 0)).getMethod());
    }

    @Test
    public void testFindMarshalerMethodInterfaceMatchArray() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod method = toSootClass(MarshalerLookupTest.Foo4.class).getMethodByName("foo8");
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("charSequenceArrayToObject"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method)).getMethod());
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("charSequenceArrayToNative"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method, 0)).getMethod());
    }

    @Test
    public void testFindMarshalerMethodInterfaceDirectMatchPointer() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod method = toSootClass(MarshalerLookupTest.Foo4.class).getMethodByName("foo9");
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("charSequenceToObject"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method)).getMethod());
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("charSequenceToNative"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(method, 0)).getMethod());
    }

    @Test
    public void testFindMarshalerMethodUnsuccessfulSearchUnsupportedCallTypeStructMember() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod getter = toSootClass(MarshalerLookupTest.TestStruct.class).getMethodByName("getV1");
        SootMethod setter = toSootClass(MarshalerLookupTest.TestStruct.class).getMethodByName("setV1");
        try {
            lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(getter)).getMethod();
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
        try {
            lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(setter, 0)).getMethod();
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testFindMarshalerStructMemberPrimitiveArray1D() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod getter = toSootClass(MarshalerLookupTest.TestStruct.class).getMethodByName("getV2");
        SootMethod setter = toSootClass(MarshalerLookupTest.TestStruct.class).getMethodByName("setV2");
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("byteArray1DToObject"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(getter)).getMethod());
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("byteArray1DToNative"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(setter, 0)).getMethod());
    }

    @Test
    public void testFindMarshalerStructMemberPrimitiveArray2D() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod getter = toSootClass(MarshalerLookupTest.TestStruct.class).getMethodByName("getV3");
        SootMethod setter = toSootClass(MarshalerLookupTest.TestStruct.class).getMethodByName("setV3");
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("byteArray2DToObject"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(getter)).getMethod());
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("byteArray2DToNative"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(setter, 0)).getMethod());
    }

    @Test
    public void testFindMarshalerStructMemberPrimitiveArray3D() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod getter = toSootClass(MarshalerLookupTest.TestStruct.class).getMethodByName("getV4");
        SootMethod setter = toSootClass(MarshalerLookupTest.TestStruct.class).getMethodByName("setV4");
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("byteArray3DToObject"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(getter)).getMethod());
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("byteArray3DToNative"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(setter, 0)).getMethod());
    }

    @Test
    public void testFindMarshalerStructMemberByteBuffer1D() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod getter = toSootClass(MarshalerLookupTest.TestStruct.class).getMethodByName("getV5");
        SootMethod setter = toSootClass(MarshalerLookupTest.TestStruct.class).getMethodByName("setV5");
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("byteBuffer1DToObject"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(getter)).getMethod());
        Assert.assertEquals(toSootClass(MarshalerLookupTest.M2.class).getMethodByName("byteBuffer1DToNative"), lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(setter, 0)).getMethod());
    }

    @Test
    public void testFindMarshalerStructMemberUnsupportedArrayDimension() {
        MarshalerLookup lookup = searchBuiltins(false);
        SootMethod getter = toSootClass(MarshalerLookupTest.TestStruct.class).getMethodByName("getV6");
        SootMethod setter = toSootClass(MarshalerLookupTest.TestStruct.class).getMethodByName("setV6");
        try {
            lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(getter)).getMethod();
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
        try {
            lookup.findMarshalerMethod(new org.robovm.compiler.MarshalerLookup.MarshalSite(setter, 0)).getMethod();
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
        }
    }

    public static class M1 {}

    public static class M2 {
        @MarshalsPointer(supportedCallTypes = MarshalerFlags.CALL_TYPE_BRIDGE)
        public static String stringToObject(Class<?> cls, long handle, long flags) {
            return null;
        }

        @MarshalsPointer(supportedCallTypes = MarshalerFlags.CALL_TYPE_BRIDGE)
        public static long stringToNative(String s, long flags) {
            return 0L;
        }

        @MarshalsPointer
        public static CharSequence charSequenceToObject(Class<?> cls, long handle, long flags) {
            return null;
        }

        @MarshalsPointer
        public static long charSequenceToNative(CharSequence s, long flags) {
            return 0L;
        }

        @MarshalsValue
        public static Integer integerToObject(Class<?> cls, int v, long flags) {
            return null;
        }

        @MarshalsValue
        public static int integerToNative(Integer s, long flags) {
            return 0;
        }

        @MarshalsValue
        public static Number numberToObject(Class<?> cls, double v, long flags) {
            return null;
        }

        @MarshalsValue
        public static double numberToNative(Number s, long flags) {
            return 0;
        }

        @MarshalsPointer
        public static String[] stringArrayToObject(Class<?> cls, long handle, long flags) {
            return null;
        }

        @MarshalsPointer
        public static long stringArrayToNative(String[] s, long flags) {
            return 0L;
        }

        @MarshalsPointer
        public static Number[] numberArrayToObject(Class<?> cls, long handle, long flags) {
            return null;
        }

        @MarshalsPointer
        public static long numberArrayToNative(Number[] s, long flags) {
            return 0;
        }

        @MarshalsPointer
        public static CharSequence[] charSequenceArrayToObject(Class<?> cls, long handle, long flags) {
            return null;
        }

        @MarshalsPointer
        public static long charSequenceArrayToNative(CharSequence[] s, long flags) {
            return 0L;
        }

        @MarshalsArray
        public static byte[] byteArray1DToObject(Class<?> cls, long handle, long flags, int d1) {
            return null;
        }

        @MarshalsArray
        public static void byteArray1DToNative(byte[] b, long handle, long flags, int d1) {
        }

        @MarshalsArray
        public static byte[][] byteArray2DToObject(Class<?> cls, long handle, long flags, int d1, int d2) {
            return null;
        }

        @MarshalsArray
        public static void byteArray2DToNative(byte[][] b, long handle, long flags, int d1, int d2) {
        }

        @MarshalsArray
        public static byte[][][] byteArray3DToObject(Class<?> cls, long handle, long flags, int d1, int d2, int d3) {
            return null;
        }

        @MarshalsArray
        public static void byteArray3DToNative(byte[][][] b, long handle, long flags, int d1, int d2, int d3) {
        }

        @MarshalsArray
        public static ByteBuffer byteBuffer1DToObject(Class<?> cls, long handle, long flags, int d1) {
            return null;
        }

        @MarshalsArray
        public static void byteBuffer1DToNative(ByteBuffer b, long handle, long flags, int d1) {
        }
    }

    public static class M3 {}

    public static class M4 {}

    public static class M5 {}

    public static class M6 {}

    public static class M7 {}

    public static class M8 {}

    @org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M1.class)
    public static class C1 {}

    public static class C2 extends MarshalerLookupTest.C1 {
        public static class Inner {
            public static class InnerInner {}
        }
    }

    @org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M2.class)
    public interface I1 {}

    @org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M3.class)
    public interface I2 {}

    @org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M4.class)
    public interface I3 {}

    @Marshalers({ @org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M5.class), @org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M1.class) })
    public interface I4 {}

    public interface I5 extends MarshalerLookupTest.I3 , MarshalerLookupTest.I4 {}

    @org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M6.class)
    public interface I6 {}

    public static class C3 extends MarshalerLookupTest.C2 implements MarshalerLookupTest.I1 , MarshalerLookupTest.I5 {}

    @org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M7.class)
    public static class C4 extends MarshalerLookupTest.C3 implements MarshalerLookupTest.I2 , MarshalerLookupTest.I6 {}

    public static class Foo1 {
        @Bridge
        private static native String foo(String s);
    }

    @org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M1.class)
    public static class Foo2 {
        @Bridge
        private static native String foo(String s);
    }

    @org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M2.class)
    public static class Foo3 {
        @Bridge
        @org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M1.class)
        private static native String foo(@org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M1.class)
        String s);
    }

    @org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M2.class)
    public static class Foo4 {
        @Bridge
        private static native String foo1(String s);

        @Bridge
        private static native Integer foo2(Integer i);

        @Bridge
        private static native StringBuilder foo3(StringBuilder s);

        @Bridge
        private static native Double foo4(Double d);

        @Callback
        private static String foo5(String s) {
            return null;
        }

        @Bridge
        private static native String[] foo6(String[] s);

        @Bridge
        private static native Double[] foo7(Double[] s);

        @Bridge
        private static native StringBuilder[] foo8(StringBuilder[] s);

        @Bridge
        private static native CharSequence foo9(CharSequence s);
    }

    @org.robovm.rt.bro.annotation.Marshaler(MarshalerLookupTest.M2.class)
    public static class TestStruct extends Struct<MarshalerLookupTest.TestStruct> {
        @StructMember(0)
        public native String getV1();

        @StructMember(0)
        public native void setV1(String s);

        @StructMember(0)
        @Array(10)
        public native byte[] getV2();

        @StructMember(0)
        public native void setV2(@Array(10)
        byte[] b);

        @StructMember(0)
        @Array({ 10, 20 })
        public native byte[][] getV3();

        @StructMember(0)
        public native void setV3(@Array({ 10, 20 })
        byte[][] b);

        @StructMember(0)
        @Array({ 10, 20, 30 })
        public native byte[][][] getV4();

        @StructMember(0)
        public native void setV4(@Array({ 10, 20, 30 })
        byte[][][] b);

        @StructMember(0)
        @Array(10)
        public native ByteBuffer getV5();

        @StructMember(0)
        public native void setV5(@Array(10)
        ByteBuffer b);

        @StructMember(0)
        @Array({ 10, 20 })
        public native ByteBuffer getV6();

        @StructMember(0)
        public native void setV6(@Array({ 10, 20 })
        ByteBuffer b);
    }

    public static class MockHome extends Home {
        public MockHome(File homeDir) {
            super(homeDir, false);
        }
    }
}

