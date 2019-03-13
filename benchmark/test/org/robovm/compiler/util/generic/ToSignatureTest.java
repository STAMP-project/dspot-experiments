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
package org.robovm.compiler.util.generic;


import java.util.Comparator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link Type#toSignature()} for the various implementations of the
 * {@link Type} interface.
 */
public class ToSignatureTest {
    public abstract static class A<U, V> {}

    public abstract static class B<T> extends ToSignatureTest.A<T, String> implements Comparable<T> , Comparator<Integer> {
        class Inner<U> {}
    }

    public abstract static class Methods {
        abstract void m1(Object o, String s, ToSignatureTest.A<Number, ?> a1, ToSignatureTest.A<Number, ? extends Comparable<String>> a2, ToSignatureTest.B<Integer>.Inner<String> b);

        abstract void m2(ToSignatureTest.B<String> i);
    }

    @Test
    public void testMethodParamSignature() {
        Type type = null;
        type = methodParamType("m1", 0);
        Assert.assertEquals("Ljava/lang/Object;", type.toGenericSignature());
        type = methodParamType("m1", 1);
        Assert.assertEquals("Ljava/lang/String;", type.toGenericSignature());
        type = methodParamType("m1", 2);
        Assert.assertEquals("Lorg/robovm/compiler/util/generic/ToSignatureTest$A<Ljava/lang/Number;*>;", type.toGenericSignature());
        type = methodParamType("m1", 3);
        Assert.assertEquals("Lorg/robovm/compiler/util/generic/ToSignatureTest$A<Ljava/lang/Number;+Ljava/lang/Comparable<Ljava/lang/String;>;>;", type.toGenericSignature());
        type = methodParamType("m1", 4);
        Assert.assertEquals("Lorg/robovm/compiler/util/generic/ToSignatureTest$B<Ljava/lang/Integer;>.Inner<Ljava/lang/String;>;", type.toGenericSignature());
    }
}

