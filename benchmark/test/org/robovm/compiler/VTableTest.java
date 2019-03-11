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


import VTable.Cache;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.compiler.VTable.Entry;
import soot.SootClass;


/**
 * Tests {@link VTable}.
 */
public class VTableTest {
    @Test
    public void testObject() {
        SootClass sc = getSootClass("java.lang.Object");
        VTable.Cache cache = new VTable.Cache();
        VTable vtable = cache.get(sc);
        Assert.assertEquals(11, vtable.size());
        Assert.assertNotNull(vtable.findEntry("toString", "()Ljava/lang/String;"));
    }

    @Test
    public void testA() {
        SootClass sc = getSootClass("org.robovm.compiler.a.A");
        VTable.Cache cache = new VTable.Cache();
        VTable vtable = cache.get(sc);
        Assert.assertEquals(14, vtable.size());
        Entry toStringEntry = vtable.findEntry("toString", "()Ljava/lang/String;");
        Assert.assertEquals(sc.getName(), toStringEntry.getDeclaringClass());
        Entry superToStringEntry = cache.get(getSootClass("java.lang.Object")).findEntry("toString", "()Ljava/lang/String;");
        Assert.assertEquals(superToStringEntry.getIndex(), toStringEntry.getIndex());
        Assert.assertNotSame(toStringEntry, superToStringEntry);
        Entry equalsEntry = vtable.findEntry("equals", "(Ljava/lang/Object;)Z");
        Assert.assertEquals(sc.getName(), equalsEntry.getDeclaringClass());
        Entry superEqualsEntry = cache.get(getSootClass("java.lang.Object")).findEntry("equals", "(Ljava/lang/Object;)Z");
        Assert.assertEquals(superEqualsEntry.getIndex(), equalsEntry.getIndex());
        Assert.assertNotSame(superEqualsEntry, equalsEntry);
        Entry cloneEntry = vtable.findEntry("clone", "()Ljava/lang/Object;");
        Assert.assertEquals("java.lang.Object", cloneEntry.getDeclaringClass());
        Entry superCloneEntry = cache.get(getSootClass("java.lang.Object")).findEntry("clone", "()Ljava/lang/Object;");
        Assert.assertSame(superCloneEntry, cloneEntry);
        Assert.assertNull(vtable.findEntry("foo", "()V"));
        Entry fooEntry = vtable.findEntry("org.robovm.compiler.a", "foo", "()V");
        Assert.assertEquals(sc.getName(), fooEntry.getDeclaringClass());
        Assert.assertEquals(11, fooEntry.getIndex());
    }

    @Test
    public void testB() {
        SootClass scJLO = getSootClass("java.lang.Object");
        SootClass scA = getSootClass("org.robovm.compiler.a.A");
        SootClass scB = getSootClass("org.robovm.compiler.b.B");
        VTable.Cache cache = new VTable.Cache();
        VTable vtableJLO = cache.get(scJLO);
        VTable vtableA = cache.get(scA);
        VTable vtableB = cache.get(scB);
        Assert.assertEquals(16, vtableB.size());
        Entry toStringEntry = vtableB.findEntry("toString", "()Ljava/lang/String;");
        Assert.assertEquals(scA.getName(), toStringEntry.getDeclaringClass());
        Entry superToStringEntry = vtableA.findEntry("toString", "()Ljava/lang/String;");
        Assert.assertSame(toStringEntry, superToStringEntry);
        Entry equalsEntry = vtableB.findEntry("equals", "(Ljava/lang/Object;)Z");
        Assert.assertEquals(scA.getName(), equalsEntry.getDeclaringClass());
        Entry superEqualsEntry = vtableA.findEntry("equals", "(Ljava/lang/Object;)Z");
        Assert.assertSame(superEqualsEntry, equalsEntry);
        Entry cloneEntry = vtableB.findEntry("clone", "()Ljava/lang/Object;");
        Assert.assertEquals(scJLO.getName(), cloneEntry.getDeclaringClass());
        Entry superCloneEntry = vtableJLO.findEntry("clone", "()Ljava/lang/Object;");
        Assert.assertSame(superCloneEntry, cloneEntry);
        Entry fooInAEntry = vtableB.findEntry("org.robovm.compiler.a", "foo", "()V");
        Assert.assertEquals(scA.getName(), fooInAEntry.getDeclaringClass());
        Assert.assertEquals(11, fooInAEntry.getIndex());
        Entry fooInBEntry = vtableB.findEntry("org.robovm.compiler.b", "foo", "()V");
        Assert.assertEquals(scB.getName(), fooInBEntry.getDeclaringClass());
        Assert.assertEquals(14, fooInBEntry.getIndex());
        Assert.assertNotSame(fooInAEntry, fooInBEntry);
        Entry fooIVEntry = vtableB.findEntry("foo", "(I)V");
        Assert.assertEquals(scB.getName(), fooIVEntry.getDeclaringClass());
        Entry superFooIVEntry = vtableA.findEntry("foo", "(I)V");
        Assert.assertEquals(superFooIVEntry.getIndex(), fooIVEntry.getIndex());
        Assert.assertNotSame(superFooIVEntry, fooIVEntry);
        Entry barInAEntry = vtableB.findEntry("org.robovm.compiler.a", "bar", "()V");
        Assert.assertEquals(scA.getName(), barInAEntry.getDeclaringClass());
        Assert.assertEquals(12, barInAEntry.getIndex());
        Entry barInBEntry = vtableB.findEntry("org.robovm.compiler.b", "bar", "()V");
        Assert.assertEquals(scB.getName(), barInBEntry.getDeclaringClass());
        Assert.assertEquals(15, barInBEntry.getIndex());
        Assert.assertNotSame(barInAEntry, barInBEntry);
    }

    @Test
    public void testC() {
        SootClass scA = getSootClass("org.robovm.compiler.a.A");
        SootClass scC = getSootClass("org.robovm.compiler.a.C");
        VTable.Cache cache = new VTable.Cache();
        VTable vtableA = cache.get(scA);
        VTable vtableC = cache.get(scC);
        Assert.assertEquals(14, vtableC.size());
        Entry fooEntry = vtableC.findEntry("org.robovm.compiler.a", "foo", "()V");
        Assert.assertEquals(scC.getName(), fooEntry.getDeclaringClass());
        Entry superFooEntry = vtableA.findEntry("org.robovm.compiler.a", "foo", "()V");
        Assert.assertEquals(superFooEntry.getIndex(), fooEntry.getIndex());
        Assert.assertNotSame(superFooEntry, fooEntry);
        Entry barEntry = vtableC.findEntry("org.robovm.compiler.a", "bar", "()V");
        Assert.assertEquals(scA.getName(), barEntry.getDeclaringClass());
        Entry superBarEntry = vtableA.findEntry("org.robovm.compiler.a", "bar", "()V");
        Assert.assertSame(superBarEntry, barEntry);
    }

    @Test
    public void testD() {
        SootClass scA = getSootClass("org.robovm.compiler.a.A");
        SootClass scB = getSootClass("org.robovm.compiler.b.B");
        SootClass scD = getSootClass("org.robovm.compiler.b.D");
        VTable.Cache cache = new VTable.Cache();
        VTable vtableB = cache.get(scB);
        VTable vtableD = cache.get(scD);
        Assert.assertEquals(vtableB.size(), vtableD.size());
        Entry barInAEntry = vtableD.findEntry("org.robovm.compiler.a", "bar", "()V");
        Assert.assertEquals(scA.getName(), barInAEntry.getDeclaringClass());
        Assert.assertEquals(12, barInAEntry.getIndex());
        Entry barInDEntry = vtableD.findEntry("org.robovm.compiler.b", "bar", "()V");
        Assert.assertEquals(scD.getName(), barInDEntry.getDeclaringClass());
        Assert.assertEquals(15, barInDEntry.getIndex());
        Assert.assertNotSame(barInAEntry, barInDEntry);
        Entry barInBEntry = vtableD.findEntry("org.robovm.compiler.b", "bar", "()V");
        Assert.assertSame(barInBEntry, barInDEntry);
    }

    @Test
    public void testEmpty() {
        SootClass scJLO = getSootClass("java.lang.Object");
        SootClass scEmpty = getSootClass("org.robovm.compiler.a.Empty");
        VTable.Cache cache = new VTable.Cache();
        VTable vtableJLO = cache.get(scJLO);
        VTable vtableEmpty = cache.get(scEmpty);
        Assert.assertArrayEquals(vtableJLO.getEntries(), vtableEmpty.getEntries());
    }
}

