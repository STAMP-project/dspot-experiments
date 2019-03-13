/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.tests.java.util;


import com.google.j2objc.util.ReflectionUtil;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.harmony.testframework.serialization.SerializationTest;


public class EnumSetTest extends TestCase {
    static final boolean disableRIBugs = true;

    static enum EnumWithInnerClass {

        a,
        b,
        c,
        d,
        e,
        f() {};}

    enum EnumWithAllInnerClass {

        a() {},
        b() {};}

    static enum EnumFoo {

        a,
        b,
        c,
        d,
        e,
        f,
        g,
        h,
        i,
        j,
        k,
        l,
        m,
        n,
        o,
        p,
        q,
        r,
        s,
        t,
        u,
        v,
        w,
        x,
        y,
        z,
        A,
        B,
        C,
        D,
        E,
        F,
        G,
        H,
        I,
        J,
        K,
        L,
        M,
        N,
        O,
        P,
        Q,
        R,
        S,
        T,
        U,
        V,
        W,
        X,
        Y,
        Z,
        aa,
        bb,
        cc,
        dd,
        ee,
        ff,
        gg,
        hh,
        ii,
        jj,
        kk,
        ll;}

    // expected
    static enum EmptyEnum {
        ;
    }

    static enum HugeEnumWithInnerClass {

        a() {},
        b() {},
        c() {},
        d() {},
        e() {},
        f() {},
        g() {},
        h() {},
        i() {},
        j() {},
        k() {},
        l() {},
        m() {},
        n() {},
        o() {},
        p() {},
        q() {},
        r() {},
        s() {},
        t() {},
        u() {},
        v() {},
        w() {},
        x() {},
        y() {},
        z() {},
        A() {},
        B() {},
        C() {},
        D() {},
        E() {},
        F() {},
        G() {},
        H() {},
        I() {},
        J() {},
        K() {},
        L() {},
        M() {},
        N() {},
        O() {},
        P() {},
        Q() {},
        R() {},
        S() {},
        T() {},
        U() {},
        V() {},
        W() {},
        X() {},
        Y() {},
        Z() {},
        aa() {},
        bb() {},
        cc() {},
        dd() {},
        ee() {},
        ff() {},
        gg() {},
        hh() {},
        ii() {},
        jj() {},
        kk() {},
        ll() {},
        mm() {};}

    static enum HugeEnum {

        a,
        b,
        c,
        d,
        e,
        f,
        g,
        h,
        i,
        j,
        k,
        l,
        m,
        n,
        o,
        p,
        q,
        r,
        s,
        t,
        u,
        v,
        w,
        x,
        y,
        z,
        A,
        B,
        C,
        D,
        E,
        F,
        G,
        H,
        I,
        J,
        K,
        L,
        M,
        N,
        O,
        P,
        Q,
        R,
        S,
        T,
        U,
        V,
        W,
        X,
        Y,
        Z,
        aa,
        bb,
        cc,
        dd,
        ee,
        ff,
        gg,
        hh,
        ii,
        jj,
        kk,
        ll,
        mm;}

    static enum HugeEnumCount {

        NO1,
        NO2,
        NO3,
        NO4,
        NO5,
        NO6,
        NO7,
        NO8,
        NO9,
        NO10,
        NO11,
        NO12,
        NO13,
        NO14,
        NO15,
        NO16,
        NO17,
        NO18,
        NO19,
        NO20,
        NO21,
        NO22,
        NO23,
        NO24,
        NO25,
        NO26,
        NO27,
        NO28,
        NO29,
        NO30,
        NO31,
        NO32,
        NO33,
        NO34,
        NO35,
        NO36,
        NO37,
        NO38,
        NO39,
        NO40,
        NO41,
        NO42,
        NO43,
        NO44,
        NO45,
        NO46,
        NO47,
        NO48,
        NO49,
        NO50,
        NO51,
        NO52,
        NO53,
        NO54,
        NO55,
        NO56,
        NO57,
        NO58,
        NO59,
        NO60,
        NO61,
        NO62,
        NO63,
        NO64,
        NO65,
        NO66,
        NO67,
        NO68,
        NO69,
        NO70,
        NO71,
        NO72,
        NO73,
        NO74,
        NO75,
        NO76,
        NO77,
        NO78,
        NO79,
        NO80,
        NO81,
        NO82,
        NO83,
        NO84,
        NO85,
        NO86,
        NO87,
        NO88,
        NO89,
        NO90,
        NO91,
        NO92,
        NO93,
        NO94,
        NO95,
        NO96,
        NO97,
        NO98,
        NO99,
        NO100,
        NO101,
        NO102,
        NO103,
        NO104,
        NO105,
        NO106,
        NO107,
        NO108,
        NO109,
        NO110,
        NO111,
        NO112,
        NO113,
        NO114,
        NO115,
        NO116,
        NO117,
        NO118,
        NO119,
        NO120,
        NO121,
        NO122,
        NO123,
        NO124,
        NO125,
        NO126,
        NO127,
        NO128,
        NO129,
        NO130;}

    /**
     * java.util.HugeEnumSet#iterator()
     */
    public void test_iterator_HugeEnumSet() {
        EnumSet<EnumSetTest.HugeEnumCount> set;
        Object[] array;
        // Test HugeEnumSet with 65 elements
        // which is more than the bits of Long
        set = EnumSet.range(EnumSetTest.HugeEnumCount.NO1, EnumSetTest.HugeEnumCount.NO65);
        array = set.toArray();
        for (EnumSetTest.HugeEnumCount count : set) {
            TestCase.assertEquals(count, ((EnumSetTest.HugeEnumCount) (array[count.ordinal()])));
        }
        // Test HugeEnumSet with 130 elements
        // which is more than twice of the bits of Long
        set = EnumSet.range(EnumSetTest.HugeEnumCount.NO1, EnumSetTest.HugeEnumCount.NO130);
        array = set.toArray();
        for (EnumSetTest.HugeEnumCount count : set) {
            TestCase.assertEquals(count, ((EnumSetTest.HugeEnumCount) (array[count.ordinal()])));
        }
    }

    public void testRemoveIteratorRemoveFromHugeEnumSet() {
        EnumSet<EnumSetTest.HugeEnumCount> set = EnumSet.noneOf(EnumSetTest.HugeEnumCount.class);
        set.add(EnumSetTest.HugeEnumCount.NO64);
        set.add(EnumSetTest.HugeEnumCount.NO65);
        set.add(EnumSetTest.HugeEnumCount.NO128);
        Iterator<EnumSetTest.HugeEnumCount> iterator = set.iterator();
        TestCase.assertTrue(iterator.hasNext());
        TestCase.assertEquals(EnumSetTest.HugeEnumCount.NO64, iterator.next());
        TestCase.assertTrue(iterator.hasNext());
        iterator.remove();
        TestCase.assertEquals(EnumSetTest.HugeEnumCount.NO65, iterator.next());
        TestCase.assertTrue(iterator.hasNext());
        TestCase.assertEquals(EnumSetTest.HugeEnumCount.NO128, iterator.next());
        TestCase.assertFalse(iterator.hasNext());
        TestCase.assertEquals(EnumSet.of(EnumSetTest.HugeEnumCount.NO65, EnumSetTest.HugeEnumCount.NO128), set);
        iterator.remove();
        TestCase.assertEquals(EnumSet.of(EnumSetTest.HugeEnumCount.NO65), set);
    }

    /**
     * java.util.EnumSet#remove(Object)
     */
    public void test_remove_LOject() {
        Set<EnumSetTest.EnumFoo> set = EnumSet.noneOf(EnumSetTest.EnumFoo.class);
        Enum[] elements = EnumSetTest.EnumFoo.class.getEnumConstants();
        for (int i = 0; i < (elements.length); i++) {
            set.add(((EnumSetTest.EnumFoo) (elements[i])));
        }
        boolean result = set.remove(null);
        TestCase.assertFalse("'set' does not contain null", result);
        result = set.remove(EnumSetTest.EnumFoo.a);
        TestCase.assertTrue("Should return true", result);
        result = set.remove(EnumSetTest.EnumFoo.a);
        TestCase.assertFalse("Should return false", result);
        TestCase.assertEquals("Size of set should be 63:", 63, set.size());
        result = set.remove(EnumSetTest.EnumWithInnerClass.a);
        TestCase.assertFalse("Should return false", result);
        result = set.remove(EnumSetTest.EnumWithInnerClass.f);
        TestCase.assertFalse("Should return false", result);
        // test enum with more than 64 elements
        Set<EnumSetTest.HugeEnum> hugeSet = EnumSet.allOf(EnumSetTest.HugeEnum.class);
        result = hugeSet.remove(null);
        TestCase.assertFalse("'set' does not contain null", result);
        result = hugeSet.remove(EnumSetTest.HugeEnum.a);
        TestCase.assertTrue("Should return true", result);
        result = hugeSet.remove(EnumSetTest.HugeEnum.a);
        TestCase.assertFalse("Should return false", result);
        TestCase.assertEquals("Size of set should be 64:", 64, hugeSet.size());
        result = hugeSet.remove(EnumSetTest.HugeEnumWithInnerClass.a);
        TestCase.assertFalse("Should return false", result);
        result = hugeSet.remove(EnumSetTest.HugeEnumWithInnerClass.f);
        TestCase.assertFalse("Should return false", result);
    }

    /**
     * java.util.EnumSet#equals(Object)
     */
    public void test_equals_LObject() {
        Set<EnumSetTest.EnumFoo> set = EnumSet.noneOf(EnumSetTest.EnumFoo.class);
        Enum[] elements = EnumSetTest.EnumFoo.class.getEnumConstants();
        for (int i = 0; i < (elements.length); i++) {
            set.add(((EnumSetTest.EnumFoo) (elements[i])));
        }
        TestCase.assertFalse("Should return false", set.equals(null));
        TestCase.assertFalse("Should return false", set.equals(new Object()));
        Set<EnumSetTest.EnumFoo> anotherSet = EnumSet.noneOf(EnumSetTest.EnumFoo.class);
        elements = EnumSetTest.EnumFoo.class.getEnumConstants();
        for (int i = 0; i < (elements.length); i++) {
            anotherSet.add(((EnumSetTest.EnumFoo) (elements[i])));
        }
        TestCase.assertTrue("Should return true", set.equals(anotherSet));
        anotherSet.remove(EnumSetTest.EnumFoo.a);
        TestCase.assertFalse("Should return false", set.equals(anotherSet));
        Set<EnumSetTest.EnumWithInnerClass> setWithInnerClass = EnumSet.noneOf(EnumSetTest.EnumWithInnerClass.class);
        elements = EnumSetTest.EnumWithInnerClass.class.getEnumConstants();
        for (int i = 0; i < (elements.length); i++) {
            setWithInnerClass.add(((EnumSetTest.EnumWithInnerClass) (elements[i])));
        }
        TestCase.assertFalse("Should return false", set.equals(setWithInnerClass));
        setWithInnerClass.clear();
        set.clear();
        TestCase.assertTrue("Should be equal", set.equals(setWithInnerClass));
        // test enum type with more than 64 elements
        Set<EnumSetTest.HugeEnum> hugeSet = EnumSet.noneOf(EnumSetTest.HugeEnum.class);
        TestCase.assertTrue(hugeSet.equals(set));
        hugeSet = EnumSet.allOf(EnumSetTest.HugeEnum.class);
        TestCase.assertFalse(hugeSet.equals(null));
        TestCase.assertFalse(hugeSet.equals(new Object()));
        Set<EnumSetTest.HugeEnum> anotherHugeSet = EnumSet.allOf(EnumSetTest.HugeEnum.class);
        anotherHugeSet.remove(EnumSetTest.HugeEnum.a);
        TestCase.assertFalse(hugeSet.equals(anotherHugeSet));
        Set<EnumSetTest.HugeEnumWithInnerClass> hugeSetWithInnerClass = EnumSet.allOf(EnumSetTest.HugeEnumWithInnerClass.class);
        TestCase.assertFalse(hugeSet.equals(hugeSetWithInnerClass));
        hugeSetWithInnerClass.clear();
        hugeSet.clear();
        TestCase.assertTrue(hugeSet.equals(hugeSetWithInnerClass));
    }

    /**
     * java.util.EnumSet#clear()
     */
    public void test_clear() {
        Set<EnumSetTest.EnumFoo> set = EnumSet.noneOf(EnumSetTest.EnumFoo.class);
        set.add(EnumSetTest.EnumFoo.a);
        set.add(EnumSetTest.EnumFoo.b);
        TestCase.assertEquals("Size should be 2", 2, set.size());
        set.clear();
        TestCase.assertEquals("Size should be 0", 0, set.size());
        // test enum type with more than 64 elements
        Set<EnumSetTest.HugeEnum> hugeSet = EnumSet.allOf(EnumSetTest.HugeEnum.class);
        TestCase.assertEquals(65, hugeSet.size());
        boolean result = hugeSet.contains(EnumSetTest.HugeEnum.aa);
        TestCase.assertTrue(result);
        hugeSet.clear();
        TestCase.assertEquals(0, hugeSet.size());
        result = hugeSet.contains(EnumSetTest.HugeEnum.aa);
        TestCase.assertFalse(result);
    }

    /**
     * java.util.EnumSet#size()
     */
    public void test_size() {
        Set<EnumSetTest.EnumFoo> set = EnumSet.noneOf(EnumSetTest.EnumFoo.class);
        set.add(EnumSetTest.EnumFoo.a);
        set.add(EnumSetTest.EnumFoo.b);
        TestCase.assertEquals("Size should be 2", 2, set.size());
        // test enum type with more than 64 elements
        Set<EnumSetTest.HugeEnum> hugeSet = EnumSet.noneOf(EnumSetTest.HugeEnum.class);
        hugeSet.add(EnumSetTest.HugeEnum.a);
        hugeSet.add(EnumSetTest.HugeEnum.bb);
        TestCase.assertEquals("Size should be 2", 2, hugeSet.size());
    }

    /**
     * java.util.EnumSet#complementOf(java.util.EnumSet)
     */
    public void test_ComplementOf_LEnumSet() {
        try {
            EnumSet.complementOf(((EnumSet<EnumSetTest.EnumFoo>) (null)));
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        EnumSet<EnumSetTest.EnumWithInnerClass> set = EnumSet.noneOf(EnumSetTest.EnumWithInnerClass.class);
        set.add(EnumSetTest.EnumWithInnerClass.d);
        set.add(EnumSetTest.EnumWithInnerClass.e);
        set.add(EnumSetTest.EnumWithInnerClass.f);
        TestCase.assertEquals("Size should be 3:", 3, set.size());
        EnumSet<EnumSetTest.EnumWithInnerClass> complementOfE = EnumSet.complementOf(set);
        TestCase.assertTrue(set.contains(EnumSetTest.EnumWithInnerClass.d));
        TestCase.assertEquals("complementOfE should have size 3", 3, complementOfE.size());
        TestCase.assertTrue("complementOfE should contain EnumWithSubclass.a:", complementOfE.contains(EnumSetTest.EnumWithInnerClass.a));
        TestCase.assertTrue("complementOfE should contain EnumWithSubclass.b:", complementOfE.contains(EnumSetTest.EnumWithInnerClass.b));
        TestCase.assertTrue("complementOfE should contain EnumWithSubclass.c:", complementOfE.contains(EnumSetTest.EnumWithInnerClass.c));
        // test enum type with more than 64 elements
        EnumSet<EnumSetTest.HugeEnum> hugeSet = EnumSet.noneOf(EnumSetTest.HugeEnum.class);
        TestCase.assertEquals(0, hugeSet.size());
        Set<EnumSetTest.HugeEnum> complementHugeSet = EnumSet.complementOf(hugeSet);
        TestCase.assertEquals(65, complementHugeSet.size());
        hugeSet.add(EnumSetTest.HugeEnum.A);
        hugeSet.add(EnumSetTest.HugeEnum.mm);
        complementHugeSet = EnumSet.complementOf(hugeSet);
        TestCase.assertEquals(63, complementHugeSet.size());
        try {
            EnumSet.complementOf(((EnumSet<EnumSetTest.HugeEnum>) (null)));
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    /**
     * java.util.EnumSet#contains(Object)
     */
    public void test_contains_LObject() {
        Set<EnumSetTest.EnumFoo> set = EnumSet.noneOf(EnumSetTest.EnumFoo.class);
        Enum[] elements = EnumSetTest.EnumFoo.class.getEnumConstants();
        for (int i = 0; i < (elements.length); i++) {
            set.add(((EnumSetTest.EnumFoo) (elements[i])));
        }
        boolean result = set.contains(null);
        TestCase.assertFalse("Should not contain null:", result);
        result = set.contains(EnumSetTest.EnumFoo.a);
        TestCase.assertTrue("Should contain EnumFoo.a", result);
        result = set.contains(EnumSetTest.EnumFoo.ll);
        TestCase.assertTrue("Should contain EnumFoo.ll", result);
        result = set.contains(EnumSetTest.EnumFoo.b);
        TestCase.assertTrue("Should contain EnumFoo.b", result);
        result = set.contains(new Object());
        TestCase.assertFalse("Should not contain Object instance", result);
        result = set.contains(EnumSetTest.EnumWithInnerClass.a);
        TestCase.assertFalse("Should not contain EnumWithSubclass.a", result);
        set = EnumSet.noneOf(EnumSetTest.EnumFoo.class);
        set.add(EnumSetTest.EnumFoo.aa);
        set.add(EnumSetTest.EnumFoo.bb);
        set.add(EnumSetTest.EnumFoo.cc);
        TestCase.assertEquals("Size of set should be 3", 3, set.size());
        TestCase.assertTrue("set should contain EnumFoo.aa", set.contains(EnumSetTest.EnumFoo.aa));
        Set<EnumSetTest.EnumWithInnerClass> setWithSubclass = EnumSet.noneOf(EnumSetTest.EnumWithInnerClass.class);
        setWithSubclass.add(EnumSetTest.EnumWithInnerClass.a);
        setWithSubclass.add(EnumSetTest.EnumWithInnerClass.b);
        setWithSubclass.add(EnumSetTest.EnumWithInnerClass.c);
        setWithSubclass.add(EnumSetTest.EnumWithInnerClass.d);
        setWithSubclass.add(EnumSetTest.EnumWithInnerClass.e);
        setWithSubclass.add(EnumSetTest.EnumWithInnerClass.f);
        result = setWithSubclass.contains(EnumSetTest.EnumWithInnerClass.f);
        TestCase.assertTrue("Should contain EnumWithSubclass.f", result);
        // test enum type with more than 64 elements
        Set<EnumSetTest.HugeEnum> hugeSet = EnumSet.allOf(EnumSetTest.HugeEnum.class);
        hugeSet.add(EnumSetTest.HugeEnum.a);
        result = hugeSet.contains(EnumSetTest.HugeEnum.a);
        TestCase.assertTrue(result);
        result = hugeSet.contains(EnumSetTest.HugeEnum.b);
        TestCase.assertTrue(result);
        result = hugeSet.contains(null);
        TestCase.assertFalse(result);
        result = hugeSet.contains(EnumSetTest.HugeEnum.a);
        TestCase.assertTrue(result);
        result = hugeSet.contains(EnumSetTest.HugeEnum.ll);
        TestCase.assertTrue(result);
        result = hugeSet.contains(new Object());
        TestCase.assertFalse(result);
        result = hugeSet.contains(Enum.class);
        TestCase.assertFalse(result);
    }

    /**
     * java.util.EnumSet#iterator()
     */
    public void test_iterator() {
        Set<EnumSetTest.EnumFoo> set = EnumSet.noneOf(EnumSetTest.EnumFoo.class);
        set.add(EnumSetTest.EnumFoo.a);
        set.add(EnumSetTest.EnumFoo.b);
        Iterator<EnumSetTest.EnumFoo> iterator = set.iterator();
        Iterator<EnumSetTest.EnumFoo> anotherIterator = set.iterator();
        TestCase.assertNotSame("Should not be same", iterator, anotherIterator);
        try {
            iterator.remove();
            TestCase.fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expectedd
        }
        TestCase.assertTrue("Should has next element:", iterator.hasNext());
        TestCase.assertSame("Should be identical", EnumSetTest.EnumFoo.a, iterator.next());
        iterator.remove();
        TestCase.assertTrue("Should has next element:", iterator.hasNext());
        TestCase.assertSame("Should be identical", EnumSetTest.EnumFoo.b, iterator.next());
        TestCase.assertFalse("Should not has next element:", iterator.hasNext());
        TestCase.assertFalse("Should not has next element:", iterator.hasNext());
        TestCase.assertEquals("Size should be 1:", 1, set.size());
        try {
            iterator.next();
            TestCase.fail("Should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
        set = EnumSet.noneOf(EnumSetTest.EnumFoo.class);
        set.add(EnumSetTest.EnumFoo.a);
        iterator = set.iterator();
        TestCase.assertEquals("Should be equal", EnumSetTest.EnumFoo.a, iterator.next());
        iterator.remove();
        try {
            iterator.remove();
            TestCase.fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
        Set<EnumSetTest.EmptyEnum> emptySet = EnumSet.allOf(EnumSetTest.EmptyEnum.class);
        Iterator<EnumSetTest.EmptyEnum> emptyIterator = emptySet.iterator();
        try {
            emptyIterator.next();
            TestCase.fail("Should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
        Set<EnumSetTest.EnumWithInnerClass> setWithSubclass = EnumSet.allOf(EnumSetTest.EnumWithInnerClass.class);
        setWithSubclass.remove(EnumSetTest.EnumWithInnerClass.e);
        Iterator<EnumSetTest.EnumWithInnerClass> iteratorWithSubclass = setWithSubclass.iterator();
        TestCase.assertSame("Should be same", EnumSetTest.EnumWithInnerClass.a, iteratorWithSubclass.next());
        TestCase.assertTrue("Should return true", iteratorWithSubclass.hasNext());
        TestCase.assertSame("Should be same", EnumSetTest.EnumWithInnerClass.b, iteratorWithSubclass.next());
        setWithSubclass.remove(EnumSetTest.EnumWithInnerClass.c);
        TestCase.assertTrue("Should return true", iteratorWithSubclass.hasNext());
        TestCase.assertSame("Should be same", EnumSetTest.EnumWithInnerClass.c, iteratorWithSubclass.next());
        TestCase.assertTrue("Should return true", iteratorWithSubclass.hasNext());
        TestCase.assertSame("Should be same", EnumSetTest.EnumWithInnerClass.d, iteratorWithSubclass.next());
        setWithSubclass.add(EnumSetTest.EnumWithInnerClass.e);
        TestCase.assertTrue("Should return true", iteratorWithSubclass.hasNext());
        TestCase.assertSame("Should be same", EnumSetTest.EnumWithInnerClass.f, iteratorWithSubclass.next());
        set = EnumSet.noneOf(EnumSetTest.EnumFoo.class);
        iterator = set.iterator();
        try {
            iterator.next();
            TestCase.fail("Should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
        set.add(EnumSetTest.EnumFoo.a);
        iterator = set.iterator();
        TestCase.assertEquals("Should return EnumFoo.a", EnumSetTest.EnumFoo.a, iterator.next());
        TestCase.assertEquals("Size of set should be 1", 1, set.size());
        iterator.remove();
        TestCase.assertEquals("Size of set should be 0", 0, set.size());
        TestCase.assertFalse("Should return false", set.contains(EnumSetTest.EnumFoo.a));
        set.add(EnumSetTest.EnumFoo.a);
        set.add(EnumSetTest.EnumFoo.b);
        iterator = set.iterator();
        TestCase.assertEquals("Should be equals", EnumSetTest.EnumFoo.a, iterator.next());
        iterator.remove();
        try {
            iterator.remove();
            TestCase.fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
        TestCase.assertTrue("Should have next element", iterator.hasNext());
        try {
            iterator.remove();
            TestCase.fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
        TestCase.assertEquals("Size of set should be 1", 1, set.size());
        TestCase.assertTrue("Should have next element", iterator.hasNext());
        TestCase.assertEquals("Should return EnumFoo.b", EnumSetTest.EnumFoo.b, iterator.next());
        set.remove(EnumSetTest.EnumFoo.b);
        TestCase.assertEquals("Size of set should be 0", 0, set.size());
        iterator.remove();
        TestCase.assertFalse("Should return false", set.contains(EnumSetTest.EnumFoo.a));
        // RI's bug, EnumFoo.b should not exist at the moment.
        if (!(EnumSetTest.disableRIBugs)) {
            TestCase.assertFalse("Should return false", set.contains(EnumSetTest.EnumFoo.b));
        }
        // test enum type with more than 64 elements
        Set<EnumSetTest.HugeEnum> hugeSet = EnumSet.noneOf(EnumSetTest.HugeEnum.class);
        hugeSet.add(EnumSetTest.HugeEnum.a);
        hugeSet.add(EnumSetTest.HugeEnum.b);
        Iterator<EnumSetTest.HugeEnum> hIterator = hugeSet.iterator();
        Iterator<EnumSetTest.HugeEnum> anotherHugeIterator = hugeSet.iterator();
        TestCase.assertNotSame(hIterator, anotherHugeIterator);
        try {
            hIterator.remove();
            TestCase.fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expectedd
        }
        TestCase.assertTrue(hIterator.hasNext());
        TestCase.assertSame(EnumSetTest.HugeEnum.a, hIterator.next());
        hIterator.remove();
        TestCase.assertTrue(hIterator.hasNext());
        TestCase.assertSame(EnumSetTest.HugeEnum.b, hIterator.next());
        TestCase.assertFalse(hIterator.hasNext());
        TestCase.assertFalse(hIterator.hasNext());
        TestCase.assertEquals(1, hugeSet.size());
        try {
            hIterator.next();
            TestCase.fail("Should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
        Set<EnumSetTest.HugeEnumWithInnerClass> hugeSetWithSubclass = EnumSet.allOf(EnumSetTest.HugeEnumWithInnerClass.class);
        hugeSetWithSubclass.remove(EnumSetTest.HugeEnumWithInnerClass.e);
        Iterator<EnumSetTest.HugeEnumWithInnerClass> hugeIteratorWithSubclass = hugeSetWithSubclass.iterator();
        TestCase.assertSame(EnumSetTest.HugeEnumWithInnerClass.a, hugeIteratorWithSubclass.next());
        TestCase.assertTrue(hugeIteratorWithSubclass.hasNext());
        TestCase.assertSame(EnumSetTest.HugeEnumWithInnerClass.b, hugeIteratorWithSubclass.next());
        setWithSubclass.remove(EnumSetTest.HugeEnumWithInnerClass.c);
        TestCase.assertTrue(hugeIteratorWithSubclass.hasNext());
        TestCase.assertSame(EnumSetTest.HugeEnumWithInnerClass.c, hugeIteratorWithSubclass.next());
        TestCase.assertTrue(hugeIteratorWithSubclass.hasNext());
        TestCase.assertSame(EnumSetTest.HugeEnumWithInnerClass.d, hugeIteratorWithSubclass.next());
        hugeSetWithSubclass.add(EnumSetTest.HugeEnumWithInnerClass.e);
        TestCase.assertTrue(hugeIteratorWithSubclass.hasNext());
        TestCase.assertSame(EnumSetTest.HugeEnumWithInnerClass.f, hugeIteratorWithSubclass.next());
        hugeSet = EnumSet.noneOf(EnumSetTest.HugeEnum.class);
        hIterator = hugeSet.iterator();
        try {
            hIterator.next();
            TestCase.fail("Should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
        hugeSet.add(EnumSetTest.HugeEnum.a);
        hIterator = hugeSet.iterator();
        TestCase.assertEquals(EnumSetTest.HugeEnum.a, hIterator.next());
        TestCase.assertEquals(1, hugeSet.size());
        hIterator.remove();
        TestCase.assertEquals(0, hugeSet.size());
        TestCase.assertFalse(hugeSet.contains(EnumSetTest.HugeEnum.a));
        hugeSet.add(EnumSetTest.HugeEnum.a);
        hugeSet.add(EnumSetTest.HugeEnum.b);
        hIterator = hugeSet.iterator();
        hIterator.next();
        hIterator.remove();
        TestCase.assertTrue(hIterator.hasNext());
        try {
            hIterator.remove();
            TestCase.fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
        TestCase.assertEquals(1, hugeSet.size());
        TestCase.assertTrue(hIterator.hasNext());
        TestCase.assertEquals(EnumSetTest.HugeEnum.b, hIterator.next());
        hugeSet.remove(EnumSetTest.HugeEnum.b);
        TestCase.assertEquals(0, hugeSet.size());
        hIterator.remove();
        TestCase.assertFalse(hugeSet.contains(EnumSetTest.HugeEnum.a));
        // RI's bug, EnumFoo.b should not exist at the moment.
        if (!(EnumSetTest.disableRIBugs)) {
            TestCase.assertFalse("Should return false", set.contains(EnumSetTest.EnumFoo.b));
        }
    }

    /**
     * java.util.EnumSet#of(E)
     */
    public void test_Of_E() {
        EnumSet<EnumSetTest.EnumWithInnerClass> enumSet = EnumSet.of(EnumSetTest.EnumWithInnerClass.a);
        TestCase.assertEquals("enumSet should have length 1:", 1, enumSet.size());
        TestCase.assertTrue("enumSet should contain EnumWithSubclass.a:", enumSet.contains(EnumSetTest.EnumWithInnerClass.a));
        try {
            EnumSet.of(((EnumSetTest.EnumWithInnerClass) (null)));
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        // test enum type with more than 64 elements
        EnumSet<EnumSetTest.HugeEnumWithInnerClass> hugeEnumSet = EnumSet.of(EnumSetTest.HugeEnumWithInnerClass.a);
        TestCase.assertEquals(1, hugeEnumSet.size());
        TestCase.assertTrue(hugeEnumSet.contains(EnumSetTest.HugeEnumWithInnerClass.a));
    }

    /**
     * java.util.EnumSet#of(E, E)
     */
    public void test_Of_EE() {
        EnumSet<EnumSetTest.EnumWithInnerClass> enumSet = EnumSet.of(EnumSetTest.EnumWithInnerClass.a, EnumSetTest.EnumWithInnerClass.b);
        TestCase.assertEquals("enumSet should have length 2:", 2, enumSet.size());
        TestCase.assertTrue("enumSet should contain EnumWithSubclass.a:", enumSet.contains(EnumSetTest.EnumWithInnerClass.a));
        TestCase.assertTrue("enumSet should contain EnumWithSubclass.b:", enumSet.contains(EnumSetTest.EnumWithInnerClass.b));
        try {
            EnumSet.of(((EnumSetTest.EnumWithInnerClass) (null)), EnumSetTest.EnumWithInnerClass.a);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            EnumSet.of(EnumSetTest.EnumWithInnerClass.a, ((EnumSetTest.EnumWithInnerClass) (null)));
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            EnumSet.of(((EnumSetTest.EnumWithInnerClass) (null)), ((EnumSetTest.EnumWithInnerClass) (null)));
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        enumSet = EnumSet.of(EnumSetTest.EnumWithInnerClass.a, EnumSetTest.EnumWithInnerClass.a);
        TestCase.assertEquals("Size of enumSet should be 1", 1, enumSet.size());
        // test enum type with more than 64 elements
        EnumSet<EnumSetTest.HugeEnumWithInnerClass> hugeEnumSet = EnumSet.of(EnumSetTest.HugeEnumWithInnerClass.a, EnumSetTest.HugeEnumWithInnerClass.b);
        TestCase.assertEquals(2, hugeEnumSet.size());
        TestCase.assertTrue(hugeEnumSet.contains(EnumSetTest.HugeEnumWithInnerClass.a));
        TestCase.assertTrue(hugeEnumSet.contains(EnumSetTest.HugeEnumWithInnerClass.b));
        try {
            EnumSet.of(((EnumSetTest.HugeEnumWithInnerClass) (null)), EnumSetTest.HugeEnumWithInnerClass.a);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            EnumSet.of(EnumSetTest.HugeEnumWithInnerClass.a, ((EnumSetTest.HugeEnumWithInnerClass) (null)));
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            EnumSet.of(((EnumSetTest.HugeEnumWithInnerClass) (null)), ((EnumSetTest.HugeEnumWithInnerClass) (null)));
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        hugeEnumSet = EnumSet.of(EnumSetTest.HugeEnumWithInnerClass.a, EnumSetTest.HugeEnumWithInnerClass.a);
        TestCase.assertEquals(1, hugeEnumSet.size());
    }

    /**
     * java.util.EnumSet#of(E, E, E)
     */
    public void test_Of_EEE() {
        EnumSet<EnumSetTest.EnumWithInnerClass> enumSet = EnumSet.of(EnumSetTest.EnumWithInnerClass.a, EnumSetTest.EnumWithInnerClass.b, EnumSetTest.EnumWithInnerClass.c);
        TestCase.assertEquals("Size of enumSet should be 3:", 3, enumSet.size());
        TestCase.assertTrue("enumSet should contain EnumWithSubclass.a:", enumSet.contains(EnumSetTest.EnumWithInnerClass.a));
        TestCase.assertTrue("Should return true", enumSet.contains(EnumSetTest.EnumWithInnerClass.c));
        try {
            EnumSet.of(((EnumSetTest.EnumWithInnerClass) (null)), null, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        enumSet = EnumSet.of(EnumSetTest.EnumWithInnerClass.a, EnumSetTest.EnumWithInnerClass.b, EnumSetTest.EnumWithInnerClass.b);
        TestCase.assertEquals("enumSet should contain 2 elements:", 2, enumSet.size());
        // test enum type with more than 64 elements
        EnumSet<EnumSetTest.HugeEnumWithInnerClass> hugeEnumSet = EnumSet.of(EnumSetTest.HugeEnumWithInnerClass.a, EnumSetTest.HugeEnumWithInnerClass.b, EnumSetTest.HugeEnumWithInnerClass.c);
        TestCase.assertEquals(3, hugeEnumSet.size());
        TestCase.assertTrue(hugeEnumSet.contains(EnumSetTest.HugeEnumWithInnerClass.a));
        TestCase.assertTrue(hugeEnumSet.contains(EnumSetTest.HugeEnumWithInnerClass.c));
        try {
            EnumSet.of(((EnumSetTest.HugeEnumWithInnerClass) (null)), null, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        hugeEnumSet = EnumSet.of(EnumSetTest.HugeEnumWithInnerClass.a, EnumSetTest.HugeEnumWithInnerClass.b, EnumSetTest.HugeEnumWithInnerClass.b);
        TestCase.assertEquals(2, hugeEnumSet.size());
    }

    /**
     * java.util.EnumSet#of(E, E, E, E)
     */
    public void test_Of_EEEE() {
        EnumSet<EnumSetTest.EnumWithInnerClass> enumSet = EnumSet.of(EnumSetTest.EnumWithInnerClass.a, EnumSetTest.EnumWithInnerClass.b, EnumSetTest.EnumWithInnerClass.c, EnumSetTest.EnumWithInnerClass.d);
        TestCase.assertEquals("Size of enumSet should be 4", 4, enumSet.size());
        TestCase.assertTrue("enumSet should contain EnumWithSubclass.a:", enumSet.contains(EnumSetTest.EnumWithInnerClass.a));
        TestCase.assertTrue("enumSet should contain EnumWithSubclass.d:", enumSet.contains(EnumSetTest.EnumWithInnerClass.d));
        try {
            EnumSet.of(((EnumSetTest.EnumWithInnerClass) (null)), null, null, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        // test enum type with more than 64 elements
        EnumSet<EnumSetTest.HugeEnumWithInnerClass> hugeEnumSet = EnumSet.of(EnumSetTest.HugeEnumWithInnerClass.a, EnumSetTest.HugeEnumWithInnerClass.b, EnumSetTest.HugeEnumWithInnerClass.c, EnumSetTest.HugeEnumWithInnerClass.d);
        TestCase.assertEquals(4, hugeEnumSet.size());
        TestCase.assertTrue(hugeEnumSet.contains(EnumSetTest.HugeEnumWithInnerClass.a));
        TestCase.assertTrue(hugeEnumSet.contains(EnumSetTest.HugeEnumWithInnerClass.d));
        try {
            EnumSet.of(((EnumSetTest.HugeEnumWithInnerClass) (null)), null, null, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    /**
     * java.util.EnumSet#of(E, E, E, E, E)
     */
    public void test_Of_EEEEE() {
        EnumSet<EnumSetTest.EnumWithInnerClass> enumSet = EnumSet.of(EnumSetTest.EnumWithInnerClass.a, EnumSetTest.EnumWithInnerClass.b, EnumSetTest.EnumWithInnerClass.c, EnumSetTest.EnumWithInnerClass.d, EnumSetTest.EnumWithInnerClass.e);
        TestCase.assertEquals("Size of enumSet should be 5:", 5, enumSet.size());
        TestCase.assertTrue("Should return true", enumSet.contains(EnumSetTest.EnumWithInnerClass.a));
        TestCase.assertTrue("Should return true", enumSet.contains(EnumSetTest.EnumWithInnerClass.e));
        try {
            EnumSet.of(((EnumSetTest.EnumWithInnerClass) (null)), null, null, null, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        // test enum with more than 64 elements
        EnumSet<EnumSetTest.HugeEnumWithInnerClass> hugeEnumSet = EnumSet.of(EnumSetTest.HugeEnumWithInnerClass.a, EnumSetTest.HugeEnumWithInnerClass.b, EnumSetTest.HugeEnumWithInnerClass.c, EnumSetTest.HugeEnumWithInnerClass.d, EnumSetTest.HugeEnumWithInnerClass.e);
        TestCase.assertEquals(5, hugeEnumSet.size());
        TestCase.assertTrue(hugeEnumSet.contains(EnumSetTest.HugeEnumWithInnerClass.a));
        TestCase.assertTrue(hugeEnumSet.contains(EnumSetTest.HugeEnumWithInnerClass.e));
        try {
            EnumSet.of(((EnumSetTest.HugeEnumWithInnerClass) (null)), null, null, null, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    /**
     * java.util.EnumSet#of(E, E...)
     */
    public void test_Of_EEArray() {
        EnumSetTest.EnumWithInnerClass[] enumArray = new EnumSetTest.EnumWithInnerClass[]{ EnumSetTest.EnumWithInnerClass.b, EnumSetTest.EnumWithInnerClass.c };
        EnumSet<EnumSetTest.EnumWithInnerClass> enumSet = EnumSet.of(EnumSetTest.EnumWithInnerClass.a, enumArray);
        TestCase.assertEquals("Should be equal", 3, enumSet.size());
        TestCase.assertTrue("Should return true", enumSet.contains(EnumSetTest.EnumWithInnerClass.a));
        TestCase.assertTrue("Should return true", enumSet.contains(EnumSetTest.EnumWithInnerClass.c));
        try {
            EnumSet.of(EnumSetTest.EnumWithInnerClass.a, ((EnumSetTest.EnumWithInnerClass[]) (null)));
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        EnumSetTest.EnumFoo[] foos = new EnumSetTest.EnumFoo[]{ EnumSetTest.EnumFoo.a, EnumSetTest.EnumFoo.c, EnumSetTest.EnumFoo.d };
        EnumSet<EnumSetTest.EnumFoo> set = EnumSet.of(EnumSetTest.EnumFoo.c, foos);
        TestCase.assertEquals("size of set should be 1", 3, set.size());
        TestCase.assertTrue("Should contain EnumFoo.a", set.contains(EnumSetTest.EnumFoo.a));
        TestCase.assertTrue("Should contain EnumFoo.c", set.contains(EnumSetTest.EnumFoo.c));
        TestCase.assertTrue("Should contain EnumFoo.d", set.contains(EnumSetTest.EnumFoo.d));
        // test enum type with more than 64 elements
        EnumSetTest.HugeEnumWithInnerClass[] hugeEnumArray = new EnumSetTest.HugeEnumWithInnerClass[]{ EnumSetTest.HugeEnumWithInnerClass.b, EnumSetTest.HugeEnumWithInnerClass.c };
        EnumSet<EnumSetTest.HugeEnumWithInnerClass> hugeEnumSet = EnumSet.of(EnumSetTest.HugeEnumWithInnerClass.a, hugeEnumArray);
        TestCase.assertEquals(3, hugeEnumSet.size());
        TestCase.assertTrue(hugeEnumSet.contains(EnumSetTest.HugeEnumWithInnerClass.a));
        TestCase.assertTrue(hugeEnumSet.contains(EnumSetTest.HugeEnumWithInnerClass.c));
        try {
            EnumSet.of(EnumSetTest.HugeEnumWithInnerClass.a, ((EnumSetTest.HugeEnumWithInnerClass[]) (null)));
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        EnumSetTest.HugeEnumWithInnerClass[] huges = new EnumSetTest.HugeEnumWithInnerClass[]{ EnumSetTest.HugeEnumWithInnerClass.a, EnumSetTest.HugeEnumWithInnerClass.c, EnumSetTest.HugeEnumWithInnerClass.d };
        EnumSet<EnumSetTest.HugeEnumWithInnerClass> hugeSet = EnumSet.of(EnumSetTest.HugeEnumWithInnerClass.c, huges);
        TestCase.assertEquals(3, hugeSet.size());
        TestCase.assertTrue(hugeSet.contains(EnumSetTest.HugeEnumWithInnerClass.a));
        TestCase.assertTrue(hugeSet.contains(EnumSetTest.HugeEnumWithInnerClass.c));
        TestCase.assertTrue(hugeSet.contains(EnumSetTest.HugeEnumWithInnerClass.d));
    }

    /**
     * java.util.EnumSet#range(E, E)
     */
    public void test_Range_EE() {
        try {
            EnumSet.range(EnumSetTest.EnumWithInnerClass.c, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            EnumSet.range(null, EnumSetTest.EnumWithInnerClass.c);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            EnumSet.range(null, ((EnumSetTest.EnumWithInnerClass) (null)));
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            EnumSet.range(EnumSetTest.EnumWithInnerClass.b, EnumSetTest.EnumWithInnerClass.a);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        EnumSet<EnumSetTest.EnumWithInnerClass> enumSet = EnumSet.range(EnumSetTest.EnumWithInnerClass.a, EnumSetTest.EnumWithInnerClass.a);
        TestCase.assertEquals("Size of enumSet should be 1", 1, enumSet.size());
        enumSet = EnumSet.range(EnumSetTest.EnumWithInnerClass.a, EnumSetTest.EnumWithInnerClass.c);
        TestCase.assertEquals("Size of enumSet should be 3", 3, enumSet.size());
        // test enum with more than 64 elements
        try {
            EnumSet.range(EnumSetTest.HugeEnumWithInnerClass.c, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            EnumSet.range(null, EnumSetTest.HugeEnumWithInnerClass.c);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            EnumSet.range(null, ((EnumSetTest.HugeEnumWithInnerClass) (null)));
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            EnumSet.range(EnumSetTest.HugeEnumWithInnerClass.b, EnumSetTest.HugeEnumWithInnerClass.a);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        EnumSet<EnumSetTest.HugeEnumWithInnerClass> hugeEnumSet = EnumSet.range(EnumSetTest.HugeEnumWithInnerClass.a, EnumSetTest.HugeEnumWithInnerClass.a);
        TestCase.assertEquals(1, hugeEnumSet.size());
        hugeEnumSet = EnumSet.range(EnumSetTest.HugeEnumWithInnerClass.c, EnumSetTest.HugeEnumWithInnerClass.aa);
        TestCase.assertEquals(51, hugeEnumSet.size());
        hugeEnumSet = EnumSet.range(EnumSetTest.HugeEnumWithInnerClass.a, EnumSetTest.HugeEnumWithInnerClass.mm);
        TestCase.assertEquals(65, hugeEnumSet.size());
        hugeEnumSet = EnumSet.range(EnumSetTest.HugeEnumWithInnerClass.b, EnumSetTest.HugeEnumWithInnerClass.mm);
        TestCase.assertEquals(64, hugeEnumSet.size());
    }

    /**
     * java.util.EnumSet#clone()
     */
    public void test_Clone() {
        EnumSet<EnumSetTest.EnumFoo> enumSet = EnumSet.allOf(EnumSetTest.EnumFoo.class);
        EnumSet<EnumSetTest.EnumFoo> clonedEnumSet = enumSet.clone();
        TestCase.assertEquals(enumSet, clonedEnumSet);
        TestCase.assertNotSame(enumSet, clonedEnumSet);
        TestCase.assertTrue(clonedEnumSet.contains(EnumSetTest.EnumFoo.a));
        TestCase.assertTrue(clonedEnumSet.contains(EnumSetTest.EnumFoo.b));
        TestCase.assertEquals(64, clonedEnumSet.size());
        // test enum type with more than 64 elements
        EnumSet<EnumSetTest.HugeEnum> hugeEnumSet = EnumSet.allOf(EnumSetTest.HugeEnum.class);
        EnumSet<EnumSetTest.HugeEnum> hugeClonedEnumSet = hugeEnumSet.clone();
        TestCase.assertEquals(hugeEnumSet, hugeClonedEnumSet);
        TestCase.assertNotSame(hugeEnumSet, hugeClonedEnumSet);
        TestCase.assertTrue(hugeClonedEnumSet.contains(EnumSetTest.HugeEnum.a));
        TestCase.assertTrue(hugeClonedEnumSet.contains(EnumSetTest.HugeEnum.b));
        TestCase.assertEquals(65, hugeClonedEnumSet.size());
        hugeClonedEnumSet.remove(EnumSetTest.HugeEnum.a);
        TestCase.assertEquals(64, hugeClonedEnumSet.size());
        TestCase.assertFalse(hugeClonedEnumSet.contains(EnumSetTest.HugeEnum.a));
        TestCase.assertEquals(65, hugeEnumSet.size());
        TestCase.assertTrue(hugeEnumSet.contains(EnumSetTest.HugeEnum.a));
    }

    /**
     * java.util.EnumSet#Serialization()
     */
    public void test_serialization() throws Exception {
        if (ReflectionUtil.isJreReflectionStripped()) {
            return;
        }
        EnumSet<EnumSetTest.EnumFoo> set = EnumSet.allOf(EnumSetTest.EnumFoo.class);
        SerializationTest.verifySelf(set);
    }
}

