/**
 * Copyright (C) 2012 RoboVM AB
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


import Arch.thumbv7;
import OS.ios;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.compiler.llvm.FunctionType;
import org.robovm.compiler.llvm.StructureType;


/**
 * Tests {@link Types}.
 */
public class TypesTest {
    @Test
    public void testGetFunctionTypeFromDescriptor() {
        FunctionType type = null;
        type = getFunctionType("()V", true);
        Assert.assertEquals(VOID, type.getReturnType());
        Assert.assertEquals(1, type.getParameterTypes().length);
        Assert.assertEquals(ENV_PTR, type.getParameterTypes()[0]);
        type = getFunctionType("()V", false);
        Assert.assertEquals(VOID, type.getReturnType());
        Assert.assertEquals(2, type.getParameterTypes().length);
        Assert.assertEquals(ENV_PTR, type.getParameterTypes()[0]);
        Assert.assertEquals(OBJECT_PTR, type.getParameterTypes()[1]);
        type = getFunctionType("(Ljava/lang/Object;)Ljava/lang/String;", true);
        Assert.assertEquals(OBJECT_PTR, type.getReturnType());
        Assert.assertEquals(2, type.getParameterTypes().length);
        Assert.assertEquals(ENV_PTR, type.getParameterTypes()[0]);
        Assert.assertEquals(OBJECT_PTR, type.getParameterTypes()[1]);
        type = getFunctionType("(Ljava/lang/Object;)Ljava/lang/String;", false);
        Assert.assertEquals(OBJECT_PTR, type.getReturnType());
        Assert.assertEquals(3, type.getParameterTypes().length);
        Assert.assertEquals(ENV_PTR, type.getParameterTypes()[0]);
        Assert.assertEquals(OBJECT_PTR, type.getParameterTypes()[1]);
        Assert.assertEquals(OBJECT_PTR, type.getParameterTypes()[2]);
    }

    public static class A {
        byte A_c;

        byte A_b;

        Object A_a;
    }

    public static class B extends TypesTest.A {
        byte B_c;

        Object B_a;
    }

    public static class C extends TypesTest.B {
        byte C_a;
    }

    public static class D extends TypesTest.C {
        final long D_d = 0;

        Object D_a;

        Object D_b;

        Object D_c;
    }

    @Test
    public void testGetInstanceType() throws Exception {
        StructureType type = Types.Types.getInstanceType(ios, thumbv7, getSootClass(TypesTest.D.class.getName()));
        int size = getAllocSize(type, "thumbv7-unknown-ios");
        Assert.assertEquals(48, size);
    }
}

