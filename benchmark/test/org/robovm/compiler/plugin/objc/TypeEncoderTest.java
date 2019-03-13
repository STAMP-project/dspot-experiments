/**
 * Copyright (C) 2015 RoboVM AB
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
package org.robovm.compiler.plugin.objc;


import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.rt.bro.Struct;
import org.robovm.rt.bro.annotation.Array;
import org.robovm.rt.bro.annotation.ByVal;
import org.robovm.rt.bro.annotation.MachineSizedFloat;
import org.robovm.rt.bro.annotation.MachineSizedSInt;
import org.robovm.rt.bro.annotation.MachineSizedUInt;
import org.robovm.rt.bro.annotation.Pointer;
import org.robovm.rt.bro.annotation.StructMember;


/**
 * Tests {@link TypeEncoder}.
 */
public class TypeEncoderTest {
    public static class IPoint extends Struct<TypeEncoderTest.IPoint> {
        @StructMember(0)
        public native int getX();

        @StructMember(0)
        public native void setX(int x);

        @StructMember(1)
        public native int getY();

        @StructMember(1)
        public native void setY(int y);
    }

    public static class FPoint extends Struct<TypeEncoderTest.FPoint> {
        @StructMember(0)
        @MachineSizedFloat
        public native double getX();

        @StructMember(0)
        public native void setX(@MachineSizedFloat
        double x);

        @StructMember(1)
        @MachineSizedFloat
        public native double getY();

        @StructMember(1)
        public native void setY(@MachineSizedFloat
        double y);
    }

    public static class FRect extends Struct<TypeEncoderTest.FRect> {
        @StructMember(0)
        @ByVal
        public native TypeEncoderTest.FPoint getP1();

        @StructMember(0)
        public native void setP1(@ByVal
        TypeEncoderTest.FPoint x);

        @StructMember(1)
        @ByVal
        public native TypeEncoderTest.FPoint getP2();

        @StructMember(1)
        public native void setP2(@ByVal
        TypeEncoderTest.FPoint x);
    }

    public static class Union extends Struct<TypeEncoderTest.Union> {
        @StructMember(0)
        @ByVal
        public native TypeEncoderTest.FPoint getV1();

        @StructMember(0)
        public native void setV1(@ByVal
        TypeEncoderTest.FPoint x);

        @StructMember(0)
        public native int getV2();

        @StructMember(0)
        public native void setV2(int x);
    }

    public static class Unsupported1 extends Struct<TypeEncoderTest.Unsupported1> {
        @StructMember(0)
        @Array(10)
        public native ByteBuffer getV();

        @StructMember(0)
        public native void setV(@Array(10)
        ByteBuffer x);
    }

    public static class Unsupported2 extends Struct<TypeEncoderTest.Unsupported2> {
        @StructMember(0)
        @Array(10)
        public native byte[] getV();

        @StructMember(0)
        public native void setV(@Array(10)
        byte[] x);
    }

    public abstract static class Methods {
        abstract void m1();

        abstract boolean m2(byte b, short s, char c, int i, long l, float f, double d);

        @Pointer
        abstract long m3(@Pointer
        long a, @MachineSizedSInt
        long b, @MachineSizedUInt
        long c, @MachineSizedFloat
        float d, @MachineSizedFloat
        double e);

        abstract TypeEncoderTest.IPoint m4(@ByVal
        TypeEncoderTest.IPoint p1, @ByVal
        TypeEncoderTest.FPoint p2);

        abstract void m5(@ByVal
        TypeEncoderTest.FRect r);

        abstract void m6(@ByVal
        TypeEncoderTest.Union u);

        abstract String m7(String s);

        abstract void m8(@ByVal
        TypeEncoderTest.Unsupported1 u);

        abstract void m9(@ByVal
        TypeEncoderTest.Unsupported2 u);
    }

    @Test
    public void testVoidReturnTypeNoParameters() {
        Assert.assertEquals("v", new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m1"), false));
    }

    @Test
    public void testSimplePrimitiveTypes() {
        Assert.assertEquals("ccsSiqfd", new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m2"), false));
        Assert.assertEquals("ccsSiqfd", new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m2"), true));
    }

    @Test
    public void testArchDependentPrimitiveTypes() {
        Assert.assertEquals("^v^viIff", new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m3"), false));
        Assert.assertEquals("^v^vqQdd", new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m3"), true));
    }

    @Test
    public void testSimpleStructTypes() {
        Assert.assertEquals("^v{?=ii}{?=ff}", new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m4"), false));
        Assert.assertEquals("^v{?=ii}{?=dd}", new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m4"), true));
    }

    @Test
    public void testNestedStructTypes() {
        Assert.assertEquals("v{?={?=ff}{?=ff}}", new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m5"), false));
        Assert.assertEquals("v{?={?=dd}{?=dd}}", new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m5"), true));
    }

    @Test
    public void testUnionTypes() {
        Assert.assertEquals("v(?=i{?=ff})", new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m6"), false));
        Assert.assertEquals("v(?=i{?=dd})", new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m6"), true));
    }

    @Test
    public void testObjectType() {
        Assert.assertEquals("@@", new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m7"), false));
    }

    @Test
    public void testArrayInStructTypeFails1() {
        // We don't support arrays in structs yet
        try {
            new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m8"), false);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void testArrayInStructTypeFails2() {
        // We don't support arrays in structs yet
        try {
            new TypeEncoder().encode(toSootClass(TypeEncoderTest.Methods.class).getMethodByName("m9"), false);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }
}

