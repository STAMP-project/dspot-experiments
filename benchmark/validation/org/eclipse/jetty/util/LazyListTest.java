/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util;


import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests for LazyList utility class.
 */
public class LazyListTest {
    public static final boolean STRICT = false;

    /**
     * Tests for {@link LazyList#add(Object, Object)}
     */
    @Test
    public void testAddObjectObject_NullInput_NullItem() {
        Object list = LazyList.add(null, null);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(1, LazyList.size(list));
    }

    /**
     * Tests for {@link LazyList#add(Object, Object)}
     */
    @Test
    public void testAddObjectObject_NullInput_NonListItem() {
        String item = "a";
        Object list = LazyList.add(null, item);
        Assertions.assertNotNull(list);
        if (LazyListTest.STRICT) {
            Assertions.assertTrue((list instanceof List));
        }
        Assertions.assertEquals(1, LazyList.size(list));
    }

    /**
     * Tests for {@link LazyList#add(Object, Object)}
     */
    @Test
    public void testAddObjectObject_NullInput_LazyListItem() {
        Object item = LazyList.add(null, "x");
        item = LazyList.add(item, "y");
        item = LazyList.add(item, "z");
        Object list = LazyList.add(null, item);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(1, LazyList.size(list));
        Object val = LazyList.get(list, 0);
        Assertions.assertTrue((val instanceof List));
    }

    /**
     * Tests for {@link LazyList#add(Object, Object)}
     */
    @Test
    public void testAddObjectObject_NonListInput() {
        String input = "a";
        Object list = LazyList.add(input, "b");
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(2, LazyList.size(list));
    }

    /**
     * Tests for {@link LazyList#add(Object, Object)}
     */
    @Test
    public void testAddObjectObject_LazyListInput() {
        Object input = LazyList.add(null, "a");
        Object list = LazyList.add(input, "b");
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        list = LazyList.add(list, "c");
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "c");
    }

    /**
     * Tests for {@link LazyList#add(Object, Object)}
     */
    @Test
    public void testAddObjectObject_GenericListInput() {
        List<String> input = new ArrayList<String>();
        input.add("a");
        Object list = LazyList.add(input, "b");
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        list = LazyList.add(list, "c");
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "c");
    }

    /**
     * Tests for {@link LazyList#add(Object, Object)}
     */
    @Test
    public void testAddObjectObject_AddNull() {
        Object list = null;
        list = LazyList.add(list, null);
        Assertions.assertEquals(1, LazyList.size(list));
        Assertions.assertNull(LazyList.get(list, 0));
        list = "a";
        list = LazyList.add(list, null);
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertNull(LazyList.get(list, 1));
        list = LazyList.add(list, null);
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertNull(LazyList.get(list, 1));
        Assertions.assertNull(LazyList.get(list, 2));
    }

    /**
     * Test for {@link LazyList#add(Object, int, Object)}
     */
    @Test
    public void testAddObjectIntObject_NullInput_NullItem() {
        Object list = LazyList.add(null, 0, null);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(1, LazyList.size(list));
    }

    /**
     * Test for {@link LazyList#add(Object, int, Object)}
     */
    @Test
    public void testAddObjectIntObject_NullInput_NonListItem() {
        String item = "a";
        Object list = LazyList.add(null, 0, item);
        Assertions.assertNotNull(list);
        if (LazyListTest.STRICT) {
            Assertions.assertTrue((list instanceof List));
        }
        Assertions.assertEquals(1, LazyList.size(list));
    }

    /**
     * Test for {@link LazyList#add(Object, int, Object)}
     */
    @Test
    public void testAddObjectIntObject_NullInput_NonListItem2() {
        Assumptions.assumeTrue(LazyListTest.STRICT);// Only run in STRICT mode.

        String item = "a";
        // Test branch of logic "index>0"
        Object list = LazyList.add(null, 1, item);// Always throws exception?

        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(1, LazyList.size(list));
    }

    /**
     * Test for {@link LazyList#add(Object, int, Object)}
     */
    @Test
    public void testAddObjectIntObject_NullInput_LazyListItem() {
        Object item = LazyList.add(null, "x");
        item = LazyList.add(item, "y");
        item = LazyList.add(item, "z");
        Object list = LazyList.add(null, 0, item);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(1, LazyList.size(list));
        Object val = LazyList.get(list, 0);
        Assertions.assertTrue((val instanceof List));
    }

    /**
     * Test for {@link LazyList#add(Object, int, Object)}
     */
    @Test
    public void testAddObjectIntObject_NullInput_GenericListItem() {
        List<String> item = new ArrayList<String>();
        item.add("a");
        Object list = LazyList.add(null, 0, item);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(1, LazyList.size(list));
    }

    /**
     * Test for {@link LazyList#add(Object, int, Object)}
     */
    @Test
    public void testAddObjectIntObject_NonListInput_NullItem() {
        String input = "a";
        Object list = LazyList.add(input, 0, null);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertNull(LazyList.get(list, 0));
        Assertions.assertEquals(LazyList.get(list, 1), "a");
    }

    /**
     * Test for {@link LazyList#add(Object, int, Object)}
     */
    @Test
    public void testAddObjectIntObject_NonListInput_NonListItem() {
        String input = "a";
        String item = "b";
        Object list = LazyList.add(input, 0, item);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "b");
        Assertions.assertEquals(LazyList.get(list, 1), "a");
    }

    /**
     * Test for {@link LazyList#add(Object, int, Object)}
     */
    @Test
    public void testAddObjectIntObject_LazyListInput() {
        Object list = LazyList.add(null, "c");// [c]

        list = LazyList.add(list, 0, "a");// [a, c]

        list = LazyList.add(list, 1, "b");// [a, b, c]

        list = LazyList.add(list, 3, "d");// [a, b, c, d]

        Assertions.assertEquals(4, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "c");
        Assertions.assertEquals(LazyList.get(list, 3), "d");
    }

    /**
     * Test for {@link LazyList#addCollection(Object, java.util.Collection)}
     */
    @Test
    public void testAddCollection_NullInput() {
        Collection<?> coll = Arrays.asList("a", "b", "c");
        Object list = LazyList.addCollection(null, coll);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "c");
    }

    /**
     * Test for {@link LazyList#addCollection(Object, java.util.Collection)}
     */
    @Test
    public void testAddCollection_NonListInput() {
        Collection<?> coll = Arrays.asList("a", "b", "c");
        String input = "z";
        Object list = LazyList.addCollection(input, coll);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(4, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "z");
        Assertions.assertEquals(LazyList.get(list, 1), "a");
        Assertions.assertEquals(LazyList.get(list, 2), "b");
        Assertions.assertEquals(LazyList.get(list, 3), "c");
    }

    /**
     * Test for {@link LazyList#addCollection(Object, java.util.Collection)}
     */
    @Test
    public void testAddCollection_LazyListInput() {
        Collection<?> coll = Arrays.asList("a", "b", "c");
        Object input = LazyList.add(null, "x");
        input = LazyList.add(input, "y");
        input = LazyList.add(input, "z");
        Object list = LazyList.addCollection(input, coll);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(6, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "x");
        Assertions.assertEquals(LazyList.get(list, 1), "y");
        Assertions.assertEquals(LazyList.get(list, 2), "z");
        Assertions.assertEquals(LazyList.get(list, 3), "a");
        Assertions.assertEquals(LazyList.get(list, 4), "b");
        Assertions.assertEquals(LazyList.get(list, 5), "c");
    }

    /**
     * Test for {@link LazyList#addCollection(Object, java.util.Collection)}
     */
    @Test
    public void testAddCollection_GenricListInput() {
        Collection<?> coll = Arrays.asList("a", "b", "c");
        List<String> input = new ArrayList<String>();
        input.add("x");
        input.add("y");
        input.add("z");
        Object list = LazyList.addCollection(input, coll);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(6, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "x");
        Assertions.assertEquals(LazyList.get(list, 1), "y");
        Assertions.assertEquals(LazyList.get(list, 2), "z");
        Assertions.assertEquals(LazyList.get(list, 3), "a");
        Assertions.assertEquals(LazyList.get(list, 4), "b");
        Assertions.assertEquals(LazyList.get(list, 5), "c");
    }

    /**
     * Test for {@link LazyList#addCollection(Object, java.util.Collection)}
     */
    @Test
    public void testAddCollection_Sequential() {
        Collection<?> coll = Arrays.asList("a", "b");
        Object list = null;
        list = LazyList.addCollection(list, coll);
        list = LazyList.addCollection(list, coll);
        Assertions.assertEquals(4, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "a");
        Assertions.assertEquals(LazyList.get(list, 3), "b");
    }

    /**
     * Test for {@link LazyList#addCollection(Object, java.util.Collection)}
     */
    @Test
    public void testAddCollection_GenericListInput() {
        List<String> l = new ArrayList<String>();
        l.add("a");
        l.add("b");
        Object list = null;
        list = LazyList.addCollection(list, l);
        list = LazyList.addCollection(list, l);
        Assertions.assertEquals(4, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "a");
        Assertions.assertEquals(LazyList.get(list, 3), "b");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_NullInput_NullArray() {
        String[] arr = null;
        Object list = LazyList.addArray(null, arr);
        Assertions.assertNull(list);
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_NullInput_EmptyArray() {
        String[] arr = new String[0];
        Object list = LazyList.addArray(null, arr);
        if (LazyListTest.STRICT) {
            Assertions.assertNotNull(list);
            Assertions.assertTrue((list instanceof List));
        }
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_NullInput_SingleArray() {
        String[] arr = new String[]{ "a" };
        Object list = LazyList.addArray(null, arr);
        Assertions.assertNotNull(list);
        if (LazyListTest.STRICT) {
            Assertions.assertTrue((list instanceof List));
        }
        Assertions.assertEquals(1, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_NullInput_Array() {
        String[] arr = new String[]{ "a", "b", "c" };
        Object list = LazyList.addArray(null, arr);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "c");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_NonListInput_NullArray() {
        String input = "z";
        String[] arr = null;
        Object list = LazyList.addArray(input, arr);
        Assertions.assertNotNull(list);
        if (LazyListTest.STRICT) {
            Assertions.assertTrue((list instanceof List));
        }
        Assertions.assertEquals(1, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "z");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_NonListInput_EmptyArray() {
        String input = "z";
        String[] arr = new String[0];
        Object list = LazyList.addArray(input, arr);
        Assertions.assertNotNull(list);
        if (LazyListTest.STRICT) {
            Assertions.assertTrue((list instanceof List));
        }
        Assertions.assertEquals(1, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "z");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_NonListInput_SingleArray() {
        String input = "z";
        String[] arr = new String[]{ "a" };
        Object list = LazyList.addArray(input, arr);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "z");
        Assertions.assertEquals(LazyList.get(list, 1), "a");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_NonListInput_Array() {
        String input = "z";
        String[] arr = new String[]{ "a", "b", "c" };
        Object list = LazyList.addArray(input, arr);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(4, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "z");
        Assertions.assertEquals(LazyList.get(list, 1), "a");
        Assertions.assertEquals(LazyList.get(list, 2), "b");
        Assertions.assertEquals(LazyList.get(list, 3), "c");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_LazyListInput_NullArray() {
        Object input = LazyList.add(null, "x");
        input = LazyList.add(input, "y");
        input = LazyList.add(input, "z");
        String[] arr = null;
        Object list = LazyList.addArray(input, arr);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "x");
        Assertions.assertEquals(LazyList.get(list, 1), "y");
        Assertions.assertEquals(LazyList.get(list, 2), "z");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_LazyListInput_EmptyArray() {
        Object input = LazyList.add(null, "x");
        input = LazyList.add(input, "y");
        input = LazyList.add(input, "z");
        String[] arr = new String[0];
        Object list = LazyList.addArray(input, arr);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "x");
        Assertions.assertEquals(LazyList.get(list, 1), "y");
        Assertions.assertEquals(LazyList.get(list, 2), "z");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_LazyListInput_SingleArray() {
        Object input = LazyList.add(null, "x");
        input = LazyList.add(input, "y");
        input = LazyList.add(input, "z");
        String[] arr = new String[]{ "a" };
        Object list = LazyList.addArray(input, arr);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(4, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "x");
        Assertions.assertEquals(LazyList.get(list, 1), "y");
        Assertions.assertEquals(LazyList.get(list, 2), "z");
        Assertions.assertEquals(LazyList.get(list, 3), "a");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_LazyListInput_Array() {
        Object input = LazyList.add(null, "x");
        input = LazyList.add(input, "y");
        input = LazyList.add(input, "z");
        String[] arr = new String[]{ "a", "b", "c" };
        Object list = LazyList.addArray(input, arr);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(6, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "x");
        Assertions.assertEquals(LazyList.get(list, 1), "y");
        Assertions.assertEquals(LazyList.get(list, 2), "z");
        Assertions.assertEquals(LazyList.get(list, 3), "a");
        Assertions.assertEquals(LazyList.get(list, 4), "b");
        Assertions.assertEquals(LazyList.get(list, 5), "c");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_GenericListInput_NullArray() {
        List<String> input = new ArrayList<String>();
        input.add("x");
        input.add("y");
        input.add("z");
        String[] arr = null;
        Object list = LazyList.addArray(input, arr);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "x");
        Assertions.assertEquals(LazyList.get(list, 1), "y");
        Assertions.assertEquals(LazyList.get(list, 2), "z");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_GenericListInput_EmptyArray() {
        List<String> input = new ArrayList<String>();
        input.add("x");
        input.add("y");
        input.add("z");
        String[] arr = new String[0];
        Object list = LazyList.addArray(input, arr);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "x");
        Assertions.assertEquals(LazyList.get(list, 1), "y");
        Assertions.assertEquals(LazyList.get(list, 2), "z");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_GenericListInput_SingleArray() {
        List<String> input = new ArrayList<String>();
        input.add("x");
        input.add("y");
        input.add("z");
        String[] arr = new String[]{ "a" };
        Object list = LazyList.addArray(input, arr);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(4, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "x");
        Assertions.assertEquals(LazyList.get(list, 1), "y");
        Assertions.assertEquals(LazyList.get(list, 2), "z");
        Assertions.assertEquals(LazyList.get(list, 3), "a");
    }

    /**
     * Tests for {@link LazyList#addArray(Object, Object[])}
     */
    @Test
    public void testAddArray_GenericListInput_Array() {
        List<String> input = new ArrayList<String>();
        input.add("x");
        input.add("y");
        input.add("z");
        String[] arr = new String[]{ "a", "b", "c" };
        Object list = LazyList.addArray(input, arr);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(6, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "x");
        Assertions.assertEquals(LazyList.get(list, 1), "y");
        Assertions.assertEquals(LazyList.get(list, 2), "z");
        Assertions.assertEquals(LazyList.get(list, 3), "a");
        Assertions.assertEquals(LazyList.get(list, 4), "b");
        Assertions.assertEquals(LazyList.get(list, 5), "c");
    }

    /**
     * Tests for {@link LazyList#ensureSize(Object, int)}
     */
    @Test
    public void testEnsureSize_NullInput() {
        Object list = LazyList.ensureSize(null, 10);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        // Not possible to test for List capacity value.
    }

    /**
     * Tests for {@link LazyList#ensureSize(Object, int)}
     */
    @Test
    public void testEnsureSize_NonListInput() {
        String input = "a";
        Object list = LazyList.ensureSize(input, 10);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        // Not possible to test for List capacity value.
        Assertions.assertEquals(1, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
    }

    /**
     * Tests for {@link LazyList#ensureSize(Object, int)}
     */
    @Test
    public void testEnsureSize_LazyListInput() {
        Object input = LazyList.add(null, "a");
        input = LazyList.add(input, "b");
        Object list = LazyList.ensureSize(input, 10);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        // Not possible to test for List capacity value.
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
    }

    /**
     * Tests for {@link LazyList#ensureSize(Object, int)}
     */
    @Test
    public void testEnsureSize_GenericListInput() {
        List<String> input = new ArrayList<String>();
        input.add("a");
        input.add("b");
        Object list = LazyList.ensureSize(input, 10);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        // Not possible to test for List capacity value.
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
    }

    /**
     * Tests for {@link LazyList#ensureSize(Object, int)}
     */
    @Test
    public void testEnsureSize_GenericListInput_LinkedList() {
        Assumptions.assumeTrue(LazyListTest.STRICT);// Only run in STRICT mode.

        // Using LinkedList concrete type as LazyList internal
        // implementation does not look for this specifically.
        List<String> input = new LinkedList<String>();
        input.add("a");
        input.add("b");
        Object list = LazyList.ensureSize(input, 10);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        // Not possible to test for List capacity value.
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
    }

    /**
     * Tests for {@link LazyList#ensureSize(Object, int)}
     */
    @Test
    public void testEnsureSize_Growth() {
        List<String> l = new ArrayList<String>();
        l.add("a");
        l.add("b");
        l.add("c");
        // NOTE: Testing for object equality might be viewed as
        // fragile by most developers, however, for this
        // specific implementation, we don't want the
        // provided list to change if the size requirements
        // have been met.
        // Trigger growth
        Object ret = LazyList.ensureSize(l, 10);
        Assertions.assertTrue((ret != l), "Should have returned a new list object");
        // Growth not neeed.
        ret = LazyList.ensureSize(l, 1);
        Assertions.assertTrue((ret == l), "Should have returned same list object");
    }

    /**
     * Tests for {@link LazyList#ensureSize(Object, int)}
     */
    @Test
    public void testEnsureSize_Growth_LinkedList() {
        Assumptions.assumeTrue(LazyListTest.STRICT);// Only run in STRICT mode.

        // Using LinkedList concrete type as LazyList internal
        // implementation has not historically looked for this
        // specifically.
        List<String> l = new LinkedList<String>();
        l.add("a");
        l.add("b");
        l.add("c");
        // NOTE: Testing for object equality might be viewed as
        // fragile by most developers, however, for this
        // specific implementation, we don't want the
        // provided list to change if the size requirements
        // have been met.
        // Trigger growth
        Object ret = LazyList.ensureSize(l, 10);
        Assertions.assertTrue((ret != l), "Should have returned a new list object");
        // Growth not neeed.
        ret = LazyList.ensureSize(l, 1);
        Assertions.assertTrue((ret == l), "Should have returned same list object");
    }

    /**
     * Test for {@link LazyList#remove(Object, Object)}
     */
    @Test
    public void testRemoveObjectObject_NullInput() {
        Object input = null;
        Assertions.assertNull(LazyList.remove(input, null));
        Assertions.assertNull(LazyList.remove(input, "a"));
        Assertions.assertNull(LazyList.remove(input, new ArrayList<Object>()));
        Assertions.assertNull(LazyList.remove(input, Integer.valueOf(42)));
    }

    /**
     * Test for {@link LazyList#remove(Object, Object)}
     */
    @Test
    public void testRemoveObjectObject_NonListInput() {
        String input = "a";
        // Remove null item
        Object list = LazyList.remove(input, null);
        Assertions.assertNotNull(list);
        if (LazyListTest.STRICT) {
            Assertions.assertTrue((list instanceof List));
        }
        Assertions.assertEquals(1, LazyList.size(list));
        // Remove item that doesn't exist
        list = LazyList.remove(input, "b");
        Assertions.assertNotNull(list);
        if (LazyListTest.STRICT) {
            Assertions.assertTrue((list instanceof List));
        }
        Assertions.assertEquals(1, LazyList.size(list));
        // Remove item that exists
        list = LazyList.remove(input, "a");
        // TODO: should this be null? or an empty list?
        Assertions.assertNull(list);// nothing left in list

        Assertions.assertEquals(0, LazyList.size(list));
    }

    /**
     * Test for {@link LazyList#remove(Object, Object)}
     */
    @Test
    public void testRemoveObjectObject_LazyListInput() {
        Object input = LazyList.add(null, "a");
        input = LazyList.add(input, "b");
        input = LazyList.add(input, "c");
        // Remove null item
        Object list = LazyList.remove(input, null);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(3, LazyList.size(list));
        // Attempt to remove something that doesn't exist
        list = LazyList.remove(input, "z");
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(3, LazyList.size(list));
        // Remove something that exists in input
        list = LazyList.remove(input, "b");
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "c");
    }

    /**
     * Test for {@link LazyList#remove(Object, Object)}
     */
    @Test
    public void testRemoveObjectObject_GenericListInput() {
        List<String> input = new ArrayList<String>();
        input.add("a");
        input.add("b");
        input.add("c");
        // Remove null item
        Object list = LazyList.remove(input, null);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertTrue((input == list), "Should not have recreated list obj");
        Assertions.assertEquals(3, LazyList.size(list));
        // Attempt to remove something that doesn't exist
        list = LazyList.remove(input, "z");
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertTrue((input == list), "Should not have recreated list obj");
        Assertions.assertEquals(3, LazyList.size(list));
        // Remove something that exists in input
        list = LazyList.remove(input, "b");
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertTrue((input == list), "Should not have recreated list obj");
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "c");
        // Try to remove the rest.
        list = LazyList.remove(list, "a");
        list = LazyList.remove(list, "c");
        Assertions.assertNull(list);
    }

    /**
     * Test for {@link LazyList#remove(Object, Object)}
     */
    @Test
    public void testRemoveObjectObject_LinkedListInput() {
        // Should be able to use any collection object.
        List<String> input = new LinkedList<String>();
        input.add("a");
        input.add("b");
        input.add("c");
        // Remove null item
        Object list = LazyList.remove(input, null);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertTrue((input == list), "Should not have recreated list obj");
        Assertions.assertEquals(3, LazyList.size(list));
        // Attempt to remove something that doesn't exist
        list = LazyList.remove(input, "z");
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertTrue((input == list), "Should not have recreated list obj");
        Assertions.assertEquals(3, LazyList.size(list));
        // Remove something that exists in input
        list = LazyList.remove(input, "b");
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertTrue((input == list), "Should not have recreated list obj");
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "c");
    }

    /**
     * Tests for {@link LazyList#remove(Object, int)}
     */
    @Test
    public void testRemoveObjectInt_NullInput() {
        Object input = null;
        Assertions.assertNull(LazyList.remove(input, 0));
        Assertions.assertNull(LazyList.remove(input, 2));
        Assertions.assertNull(LazyList.remove(input, (-2)));
    }

    /**
     * Tests for {@link LazyList#remove(Object, int)}
     */
    @Test
    public void testRemoveObjectInt_NonListInput() {
        String input = "a";
        // Invalid index
        Object list = LazyList.remove(input, 1);
        Assertions.assertNotNull(list);
        if (LazyListTest.STRICT) {
            Assertions.assertTrue((list instanceof List));
        }
        Assertions.assertEquals(1, LazyList.size(list));
        // Valid index
        list = LazyList.remove(input, 0);
        // TODO: should this be null? or an empty list?
        Assertions.assertNull(list);// nothing left in list

        Assertions.assertEquals(0, LazyList.size(list));
    }

    /**
     * Tests for {@link LazyList#remove(Object, int)}
     */
    @Test
    public void testRemoveObjectInt_LazyListInput() {
        Object input = LazyList.add(null, "a");
        input = LazyList.add(input, "b");
        input = LazyList.add(input, "c");
        Object list = null;
        if (LazyListTest.STRICT) {
            // Invalid index
            // Shouldn't cause a IndexOutOfBoundsException as this is not the
            // same behavior you experience in testRemoveObjectInt_NonListInput and
            // testRemoveObjectInt_NullInput when using invalid indexes.
            list = LazyList.remove(input, 5);
            Assertions.assertNotNull(list);
            Assertions.assertTrue((list instanceof List));
            Assertions.assertEquals(3, LazyList.size(list));
        }
        // Valid index
        list = LazyList.remove(input, 1);// remove the 'b'

        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "c");
    }

    /**
     * Tests for {@link LazyList#remove(Object, int)}
     */
    @Test
    public void testRemoveObjectInt_GenericListInput() {
        List<String> input = new ArrayList<String>();
        input.add("a");
        input.add("b");
        input.add("c");
        Object list = null;
        if (LazyListTest.STRICT) {
            // Invalid index
            // Shouldn't cause a IndexOutOfBoundsException as this is not the
            // same behavior you experience in testRemoveObjectInt_NonListInput and
            // testRemoveObjectInt_NullInput when using invalid indexes.
            list = LazyList.remove(input, 5);
            Assertions.assertNotNull(list);
            Assertions.assertTrue((list instanceof List));
            Assertions.assertEquals(3, LazyList.size(list));
        }
        // Valid index
        list = LazyList.remove(input, 1);// remove the 'b'

        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(2, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "c");
        // Remove the rest
        list = LazyList.remove(list, 0);// the 'a'

        list = LazyList.remove(list, 0);// the 'c'

        Assertions.assertNull(list);
    }

    /**
     * Test for {@link LazyList#getList(Object)}
     */
    @Test
    public void testGetListObject_NullInput() {
        Object input = null;
        Object list = LazyList.getList(input);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(0, LazyList.size(list));
    }

    /**
     * Test for {@link LazyList#getList(Object)}
     */
    @Test
    public void testGetListObject_NonListInput() {
        String input = "a";
        Object list = LazyList.getList(input);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(1, LazyList.size(list));
    }

    /**
     * Test for {@link LazyList#getList(Object)}
     */
    @Test
    public void testGetListObject_LazyListInput() {
        Object input = LazyList.add(null, "a");
        input = LazyList.add(input, "b");
        input = LazyList.add(input, "c");
        Object list = LazyList.getList(input);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "c");
    }

    /**
     * Test for {@link LazyList#getList(Object)}
     */
    @Test
    public void testGetListObject_GenericListInput() {
        List<String> input = new ArrayList<String>();
        input.add("a");
        input.add("b");
        input.add("c");
        Object list = LazyList.getList(input);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "c");
    }

    /**
     * Test for {@link LazyList#getList(Object)}
     */
    @Test
    public void testGetListObject_LinkedListInput() {
        List<String> input = new LinkedList<String>();
        input.add("a");
        input.add("b");
        input.add("c");
        Object list = LazyList.getList(input);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List));
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "c");
    }

    /**
     * Test for {@link LazyList#getList(Object)}
     */
    @Test
    public void testGetListObject_NullForEmpty() {
        Assertions.assertNull(LazyList.getList(null, true));
        Assertions.assertNotNull(LazyList.getList(null, false));
    }

    /**
     * Tests for {@link LazyList#toStringArray(Object)}
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testToStringArray() {
        Assertions.assertEquals(0, LazyList.toStringArray(null).length);
        Assertions.assertEquals(1, LazyList.toStringArray("a").length);
        Assertions.assertEquals("a", LazyList.toStringArray("a")[0]);
        @SuppressWarnings("rawtypes")
        ArrayList l = new ArrayList();
        l.add("a");
        l.add(null);
        l.add(new Integer(2));
        String[] a = LazyList.toStringArray(l);
        Assertions.assertEquals(3, a.length);
        Assertions.assertEquals("a", a[0]);
        Assertions.assertEquals(null, a[1]);
        Assertions.assertEquals("2", a[2]);
    }

    /**
     * Tests for {@link LazyList#toArray(Object, Class)}
     */
    @Test
    public void testToArray_NullInput_Object() {
        Object input = null;
        Object arr = LazyList.toArray(input, Object.class);
        Assertions.assertNotNull(arr);
        Assertions.assertTrue(arr.getClass().isArray());
    }

    /**
     * Tests for {@link LazyList#toArray(Object, Class)}
     */
    @Test
    public void testToArray_NullInput_String() {
        String input = null;
        Object arr = LazyList.toArray(input, String.class);
        Assertions.assertNotNull(arr);
        Assertions.assertTrue(arr.getClass().isArray());
        Assertions.assertTrue((arr instanceof String[]));
    }

    /**
     * Tests for {@link LazyList#toArray(Object, Class)}
     */
    @Test
    public void testToArray_NonListInput() {
        String input = "a";
        Object arr = LazyList.toArray(input, String.class);
        Assertions.assertNotNull(arr);
        Assertions.assertTrue(arr.getClass().isArray());
        Assertions.assertTrue((arr instanceof String[]));
        String[] strs = ((String[]) (arr));
        Assertions.assertEquals(1, strs.length);
        Assertions.assertEquals("a", strs[0]);
    }

    /**
     * Tests for {@link LazyList#toArray(Object, Class)}
     */
    @Test
    public void testToArray_LazyListInput() {
        Object input = LazyList.add(null, "a");
        input = LazyList.add(input, "b");
        input = LazyList.add(input, "c");
        Object arr = LazyList.toArray(input, String.class);
        Assertions.assertNotNull(arr);
        Assertions.assertTrue(arr.getClass().isArray());
        Assertions.assertTrue((arr instanceof String[]));
        String[] strs = ((String[]) (arr));
        Assertions.assertEquals(3, strs.length);
        Assertions.assertEquals("a", strs[0]);
        Assertions.assertEquals("b", strs[1]);
        Assertions.assertEquals("c", strs[2]);
    }

    /**
     * Tests for {@link LazyList#toArray(Object, Class)}
     */
    @Test
    public void testToArray_LazyListInput_Primitives() {
        Object input = LazyList.add(null, 22);
        input = LazyList.add(input, 333);
        input = LazyList.add(input, 4444);
        input = LazyList.add(input, 55555);
        Object arr = LazyList.toArray(input, int.class);
        Assertions.assertNotNull(arr);
        Assertions.assertTrue(arr.getClass().isArray());
        Assertions.assertTrue((arr instanceof int[]));
        int[] nums = ((int[]) (arr));
        Assertions.assertEquals(4, nums.length);
        Assertions.assertEquals(22, nums[0]);
        Assertions.assertEquals(333, nums[1]);
        Assertions.assertEquals(4444, nums[2]);
        Assertions.assertEquals(55555, nums[3]);
    }

    /**
     * Tests for {@link LazyList#toArray(Object, Class)}
     */
    @Test
    public void testToArray_GenericListInput() {
        List<String> input = new ArrayList<String>();
        input.add("a");
        input.add("b");
        input.add("c");
        Object arr = LazyList.toArray(input, String.class);
        Assertions.assertNotNull(arr);
        Assertions.assertTrue(arr.getClass().isArray());
        Assertions.assertTrue((arr instanceof String[]));
        String[] strs = ((String[]) (arr));
        Assertions.assertEquals(3, strs.length);
        Assertions.assertEquals("a", strs[0]);
        Assertions.assertEquals("b", strs[1]);
        Assertions.assertEquals("c", strs[2]);
    }

    /**
     * Tests for {@link LazyList#size(Object)}
     */
    @Test
    public void testSize_NullInput() {
        Assertions.assertEquals(0, LazyList.size(null));
    }

    /**
     * Tests for {@link LazyList#size(Object)}
     */
    @Test
    public void testSize_NonListInput() {
        String input = "a";
        Assertions.assertEquals(1, LazyList.size(input));
    }

    /**
     * Tests for {@link LazyList#size(Object)}
     */
    @Test
    public void testSize_LazyListInput() {
        Object input = LazyList.add(null, "a");
        input = LazyList.add(input, "b");
        Assertions.assertEquals(2, LazyList.size(input));
        input = LazyList.add(input, "c");
        Assertions.assertEquals(3, LazyList.size(input));
    }

    /**
     * Tests for {@link LazyList#size(Object)}
     */
    @Test
    public void testSize_GenericListInput() {
        List<String> input = new ArrayList<String>();
        Assertions.assertEquals(0, LazyList.size(input));
        input.add("a");
        input.add("b");
        Assertions.assertEquals(2, LazyList.size(input));
        input.add("c");
        Assertions.assertEquals(3, LazyList.size(input));
    }

    /**
     * Tests for bad input on {@link LazyList#get(Object, int)}
     */
    @Test
    public void testGet_OutOfBounds_NullInput() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            LazyList.get(null, 0);// Should Fail due to null input

        });
    }

    /**
     * Tests for bad input on {@link LazyList#get(Object, int)}
     */
    @Test
    public void testGet_OutOfBounds_NonListInput() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            String input = "a";
            LazyList.get(input, 1);// Should Fail

        });
    }

    /**
     * Tests for bad input on {@link LazyList#get(Object, int)}
     */
    @Test
    public void testGet_OutOfBounds_LazyListInput() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            Object input = LazyList.add(null, "a");
            LazyList.get(input, 1);// Should Fail

        });
    }

    /**
     * Tests for bad input on {@link LazyList#get(Object, int)}
     */
    @Test
    public void testGet_OutOfBounds_GenericListInput() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            List<String> input = new ArrayList<String>();
            input.add("a");
            LazyList.get(input, 1);// Should Fail

        });
    }

    /**
     * Tests for non-list input on {@link LazyList#get(Object, int)}
     */
    @Test
    public void testGet_NonListInput() {
        String input = "a";
        Assertions.assertEquals(LazyList.get(input, 0), "a");
    }

    /**
     * Tests for list input on {@link LazyList#get(Object, int)}
     */
    @Test
    public void testGet_LazyListInput() {
        Object input = LazyList.add(null, "a");
        Assertions.assertEquals(LazyList.get(input, 0), "a");
    }

    /**
     * Tests for list input on {@link LazyList#get(Object, int)}
     */
    @Test
    public void testGet_GenericListInput() {
        List<String> input = new ArrayList<String>();
        input.add("a");
        Assertions.assertEquals(LazyList.get(input, 0), "a");
        List<URI> uris = new ArrayList<URI>();
        uris.add(URI.create("http://www.mortbay.org/"));
        uris.add(URI.create("http://jetty.codehaus.org/jetty/"));
        uris.add(URI.create("http://www.intalio.com/jetty/"));
        uris.add(URI.create("http://www.eclipse.org/jetty/"));
        // Make sure that Generics pass through the 'get' routine safely.
        // We should be able to call this without casting the result to URI
        URI eclipseUri = LazyList.get(uris, 3);
        Assertions.assertEquals("http://www.eclipse.org/jetty/", eclipseUri.toASCIIString());
    }

    /**
     * Tests for {@link LazyList#contains(Object, Object)}
     */
    @Test
    public void testContains_NullInput() {
        Assertions.assertFalse(LazyList.contains(null, "z"));
    }

    /**
     * Tests for {@link LazyList#contains(Object, Object)}
     */
    @Test
    public void testContains_NonListInput() {
        String input = "a";
        Assertions.assertFalse(LazyList.contains(input, "z"));
        Assertions.assertTrue(LazyList.contains(input, "a"));
    }

    /**
     * Tests for {@link LazyList#contains(Object, Object)}
     */
    @Test
    public void testContains_LazyListInput() {
        Object input = LazyList.add(null, "a");
        input = LazyList.add(input, "b");
        input = LazyList.add(input, "c");
        Assertions.assertFalse(LazyList.contains(input, "z"));
        Assertions.assertTrue(LazyList.contains(input, "a"));
        Assertions.assertTrue(LazyList.contains(input, "b"));
    }

    /**
     * Tests for {@link LazyList#contains(Object, Object)}
     */
    @Test
    public void testContains_GenericListInput() {
        List<String> input = new ArrayList<String>();
        input.add("a");
        input.add("b");
        input.add("c");
        Assertions.assertFalse(LazyList.contains(input, "z"));
        Assertions.assertTrue(LazyList.contains(input, "a"));
        Assertions.assertTrue(LazyList.contains(input, "b"));
    }

    /**
     * Tests for {@link LazyList#clone(Object)}
     */
    @Test
    public void testClone_NullInput() {
        Object input = null;
        Object list = LazyList.clone(input);
        Assertions.assertNull(list);
    }

    /**
     * Tests for {@link LazyList#clone(Object)}
     */
    @Test
    public void testClone_NonListInput() {
        String input = "a";
        Object list = LazyList.clone(input);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((input == list), "Should be the same object");
    }

    /**
     * Tests for {@link LazyList#clone(Object)}
     */
    @Test
    public void testClone_LazyListInput() {
        Object input = LazyList.add(null, "a");
        input = LazyList.add(input, "b");
        input = LazyList.add(input, "c");
        Object list = LazyList.clone(input);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List), "Should be a List object");
        Assertions.assertFalse((input == list), "Should NOT be the same object");
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "c");
    }

    /**
     * Tests for {@link LazyList#clone(Object)}
     */
    @Test
    public void testClone_GenericListInput() {
        List<String> input = new ArrayList<String>();
        input.add("a");
        input.add("b");
        input.add("c");
        // TODO: decorate the .clone(Object) method to return
        // the same generic object element type
        Object list = LazyList.clone(input);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List), "Should be a List object");
        Assertions.assertFalse((input == list), "Should NOT be the same object");
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "c");
    }

    /**
     * Tests for {@link LazyList#toString(Object)}
     */
    @Test
    public void testToString_NullInput() {
        Object input = null;
        Assertions.assertEquals("[]", LazyList.toString(input));
    }

    /**
     * Tests for {@link LazyList#toString(Object)}
     */
    @Test
    public void testToString_NonListInput() {
        String input = "a";
        Assertions.assertEquals("[a]", LazyList.toString(input));
    }

    /**
     * Tests for {@link LazyList#toString(Object)}
     */
    @Test
    public void testToString_LazyListInput() {
        Object input = LazyList.add(null, "a");
        Assertions.assertEquals("[a]", LazyList.toString(input));
        input = LazyList.add(input, "b");
        input = LazyList.add(input, "c");
        Assertions.assertEquals("[a, b, c]", LazyList.toString(input));
    }

    /**
     * Tests for {@link LazyList#toString(Object)}
     */
    @Test
    public void testToString_GenericListInput() {
        List<String> input = new ArrayList<String>();
        input.add("a");
        Assertions.assertEquals("[a]", LazyList.toString(input));
        input.add("b");
        input.add("c");
        Assertions.assertEquals("[a, b, c]", LazyList.toString(input));
    }

    /**
     * Tests for {@link LazyList#iterator(Object)}
     */
    @Test
    public void testIterator_NullInput() {
        Iterator<?> iter = LazyList.iterator(null);
        Assertions.assertNotNull(iter);
        Assertions.assertFalse(iter.hasNext());
    }

    /**
     * Tests for {@link LazyList#iterator(Object)}
     */
    @Test
    public void testIterator_NonListInput() {
        String input = "a";
        Iterator<?> iter = LazyList.iterator(input);
        Assertions.assertNotNull(iter);
        Assertions.assertTrue(iter.hasNext());
        Assertions.assertEquals("a", iter.next());
        Assertions.assertFalse(iter.hasNext());
    }

    /**
     * Tests for {@link LazyList#iterator(Object)}
     */
    @Test
    public void testIterator_LazyListInput() {
        Object input = LazyList.add(null, "a");
        input = LazyList.add(input, "b");
        input = LazyList.add(input, "c");
        Iterator<?> iter = LazyList.iterator(input);
        Assertions.assertNotNull(iter);
        Assertions.assertTrue(iter.hasNext());
        Assertions.assertEquals("a", iter.next());
        Assertions.assertEquals("b", iter.next());
        Assertions.assertEquals("c", iter.next());
        Assertions.assertFalse(iter.hasNext());
    }

    /**
     * Tests for {@link LazyList#iterator(Object)}
     */
    @Test
    public void testIterator_GenericListInput() {
        List<String> input = new ArrayList<String>();
        input.add("a");
        input.add("b");
        input.add("c");
        Iterator<String> iter = LazyList.iterator(input);
        Assertions.assertNotNull(iter);
        Assertions.assertTrue(iter.hasNext());
        Assertions.assertEquals("a", iter.next());
        Assertions.assertEquals("b", iter.next());
        Assertions.assertEquals("c", iter.next());
        Assertions.assertFalse(iter.hasNext());
    }

    /**
     * Tests for {@link LazyList#listIterator(Object)}
     */
    @Test
    public void testListIterator_NullInput() {
        ListIterator<?> iter = LazyList.listIterator(null);
        Assertions.assertNotNull(iter);
        Assertions.assertFalse(iter.hasNext());
        Assertions.assertFalse(iter.hasPrevious());
    }

    /**
     * Tests for {@link LazyList#listIterator(Object)}
     */
    @Test
    public void testListIterator_NonListInput() {
        String input = "a";
        ListIterator<?> iter = LazyList.listIterator(input);
        Assertions.assertNotNull(iter);
        Assertions.assertTrue(iter.hasNext());
        Assertions.assertFalse(iter.hasPrevious());
        Assertions.assertEquals("a", iter.next());
        Assertions.assertFalse(iter.hasNext());
        Assertions.assertTrue(iter.hasPrevious());
    }

    /**
     * Tests for {@link LazyList#listIterator(Object)}
     */
    @Test
    public void testListIterator_LazyListInput() {
        Object input = LazyList.add(null, "a");
        input = LazyList.add(input, "b");
        input = LazyList.add(input, "c");
        ListIterator<?> iter = LazyList.listIterator(input);
        Assertions.assertNotNull(iter);
        Assertions.assertTrue(iter.hasNext());
        Assertions.assertFalse(iter.hasPrevious());
        Assertions.assertEquals("a", iter.next());
        Assertions.assertEquals("b", iter.next());
        Assertions.assertEquals("c", iter.next());
        Assertions.assertFalse(iter.hasNext());
        Assertions.assertTrue(iter.hasPrevious());
        Assertions.assertEquals("c", iter.previous());
        Assertions.assertEquals("b", iter.previous());
        Assertions.assertEquals("a", iter.previous());
        Assertions.assertFalse(iter.hasPrevious());
    }

    /**
     * Tests for {@link LazyList#listIterator(Object)}
     */
    @Test
    public void testListIterator_GenericListInput() {
        List<String> input = new ArrayList<String>();
        input.add("a");
        input.add("b");
        input.add("c");
        ListIterator<?> iter = LazyList.listIterator(input);
        Assertions.assertNotNull(iter);
        Assertions.assertTrue(iter.hasNext());
        Assertions.assertFalse(iter.hasPrevious());
        Assertions.assertEquals("a", iter.next());
        Assertions.assertEquals("b", iter.next());
        Assertions.assertEquals("c", iter.next());
        Assertions.assertFalse(iter.hasNext());
        Assertions.assertTrue(iter.hasPrevious());
        Assertions.assertEquals("c", iter.previous());
        Assertions.assertEquals("b", iter.previous());
        Assertions.assertEquals("a", iter.previous());
        Assertions.assertFalse(iter.hasPrevious());
    }

    /**
     * Tests for {@link ArrayUtil#asMutableList(Object[])}
     */
    @Test
    public void testArray2List_NullInput() {
        Object[] input = null;
        Object list = ArrayUtil.asMutableList(input);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List), "Should be a List object");
        Assertions.assertEquals(0, LazyList.size(list));
    }

    /**
     * Tests for {@link ArrayUtil#asMutableList(Object[])}
     */
    @Test
    public void testArray2List_EmptyInput() {
        String[] input = new String[0];
        Object list = ArrayUtil.asMutableList(input);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List), "Should be a List object");
        Assertions.assertEquals(0, LazyList.size(list));
    }

    /**
     * Tests for {@link ArrayUtil#asMutableList(Object[])}
     */
    @Test
    public void testArray2List_SingleInput() {
        String[] input = new String[]{ "a" };
        Object list = ArrayUtil.asMutableList(input);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List), "Should be a List object");
        Assertions.assertEquals(1, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
    }

    /**
     * Tests for {@link ArrayUtil#asMutableList(Object[])}
     */
    @Test
    public void testArray2List_MultiInput() {
        String[] input = new String[]{ "a", "b", "c" };
        Object list = ArrayUtil.asMutableList(input);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List), "Should be a List object");
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "c");
    }

    /**
     * Tests for {@link ArrayUtil#asMutableList(Object[])}
     */
    @Test
    public void testArray2List_GenericsInput() {
        String[] input = new String[]{ "a", "b", "c" };
        // Test the Generics definitions for array2List
        List<String> list = ArrayUtil.asMutableList(input);
        Assertions.assertNotNull(list);
        Assertions.assertTrue((list instanceof List), "Should be a List object");
        Assertions.assertEquals(3, LazyList.size(list));
        Assertions.assertEquals(LazyList.get(list, 0), "a");
        Assertions.assertEquals(LazyList.get(list, 1), "b");
        Assertions.assertEquals(LazyList.get(list, 2), "c");
    }

    /**
     * Tests for {@link ArrayUtil#addToArray(Object[], Object, Class)}
     */
    @Test
    public void testAddToArray_NullInput_NullItem() {
        Object[] input = null;
        Object[] arr = ArrayUtil.addToArray(input, null, Object.class);
        Assertions.assertNotNull(arr);
        if (LazyListTest.STRICT) {
            // Adding null item to array should result in nothing added?
            Assertions.assertEquals(0, arr.length);
        } else {
            Assertions.assertEquals(1, arr.length);
        }
    }

    /**
     * Tests for {@link ArrayUtil#addToArray(Object[], Object, Class)}
     */
    @Test
    public void testAddToArray_NullNullNull() {
        // NPE if item && type are both null.
        Assumptions.assumeTrue(LazyListTest.STRICT);
        // Harsh test case.
        Object[] input = null;
        Object[] arr = ArrayUtil.addToArray(input, null, null);
        Assertions.assertNotNull(arr);
        if (LazyListTest.STRICT) {
            // Adding null item to array should result in nothing added?
            Assertions.assertEquals(0, arr.length);
        } else {
            Assertions.assertEquals(1, arr.length);
        }
    }

    /**
     * Tests for {@link ArrayUtil#addToArray(Object[], Object, Class)}
     */
    @Test
    public void testAddToArray_NullInput_SimpleItem() {
        Object[] input = null;
        Object[] arr = ArrayUtil.addToArray(input, "a", String.class);
        Assertions.assertNotNull(arr);
        Assertions.assertEquals(1, arr.length);
        Assertions.assertEquals("a", arr[0]);
        // Same test, but with an undefined type
        arr = ArrayUtil.addToArray(input, "b", null);
        Assertions.assertNotNull(arr);
        Assertions.assertEquals(1, arr.length);
        Assertions.assertEquals("b", arr[0]);
    }

    /**
     * Tests for {@link ArrayUtil#addToArray(Object[], Object, Class)}
     */
    @Test
    public void testAddToArray_EmptyInput_NullItem() {
        String[] input = new String[0];
        String[] arr = ArrayUtil.addToArray(input, null, Object.class);
        Assertions.assertNotNull(arr);
        if (LazyListTest.STRICT) {
            // Adding null item to array should result in nothing added?
            Assertions.assertEquals(0, arr.length);
        } else {
            Assertions.assertEquals(1, arr.length);
        }
    }

    /**
     * Tests for {@link ArrayUtil#addToArray(Object[], Object, Class)}
     */
    @Test
    public void testAddToArray_EmptyInput_SimpleItem() {
        String[] input = new String[0];
        String[] arr = ArrayUtil.addToArray(input, "a", String.class);
        Assertions.assertNotNull(arr);
        Assertions.assertEquals(1, arr.length);
        Assertions.assertEquals("a", arr[0]);
    }

    /**
     * Tests for {@link ArrayUtil#addToArray(Object[], Object, Class)}
     */
    @Test
    public void testAddToArray_SingleInput_NullItem() {
        String[] input = new String[]{ "z" };
        String[] arr = ArrayUtil.addToArray(input, null, Object.class);
        Assertions.assertNotNull(arr);
        if (LazyListTest.STRICT) {
            // Should a null item be added to an array?
            Assertions.assertEquals(1, arr.length);
        } else {
            Assertions.assertEquals(2, arr.length);
            Assertions.assertEquals("z", arr[0]);
            Assertions.assertEquals(null, arr[1]);
        }
    }

    /**
     * Tests for {@link ArrayUtil#addToArray(Object[], Object, Class)}
     */
    @Test
    public void testAddToArray_SingleInput_SimpleItem() {
        String[] input = new String[]{ "z" };
        String[] arr = ArrayUtil.addToArray(input, "a", String.class);
        Assertions.assertNotNull(arr);
        Assertions.assertEquals(2, arr.length);
        Assertions.assertEquals("z", arr[0]);
        Assertions.assertEquals("a", arr[1]);
    }

    /**
     * Tests for {@link ArrayUtil#removeFromArray(Object[], Object)}
     */
    @Test
    public void testRemoveFromArray_NullInput_NullItem() {
        Object[] input = null;
        Object[] arr = ArrayUtil.removeFromArray(input, null);
        Assertions.assertNull(arr);
    }

    /**
     * Tests for {@link ArrayUtil#removeFromArray(Object[], Object)}
     */
    @Test
    public void testRemoveFromArray_NullInput_SimpleItem() {
        Object[] input = null;
        Object[] arr = ArrayUtil.removeFromArray(input, "a");
        Assertions.assertNull(arr);
    }

    /**
     * Tests for {@link ArrayUtil#removeFromArray(Object[], Object)}
     */
    @Test
    public void testRemoveFromArray_EmptyInput_NullItem() {
        String[] input = new String[0];
        String[] arr = ArrayUtil.removeFromArray(input, null);
        Assertions.assertNotNull(arr, "Should not be null");
        Assertions.assertEquals(0, arr.length);
    }

    /**
     * Tests for {@link ArrayUtil#removeFromArray(Object[], Object)}
     */
    @Test
    public void testRemoveFromArray_EmptyInput_SimpleItem() {
        String[] input = new String[0];
        String[] arr = ArrayUtil.removeFromArray(input, "a");
        Assertions.assertNotNull(arr, "Should not be null");
        Assertions.assertEquals(0, arr.length);
    }

    /**
     * Tests for {@link ArrayUtil#removeFromArray(Object[], Object)}
     */
    @Test
    public void testRemoveFromArray_SingleInput() {
        String[] input = new String[]{ "a" };
        String[] arr = ArrayUtil.removeFromArray(input, null);
        Assertions.assertNotNull(arr, "Should not be null");
        Assertions.assertEquals(1, arr.length);
        Assertions.assertEquals("a", arr[0]);
        // Remove actual item
        arr = ArrayUtil.removeFromArray(input, "a");
        Assertions.assertNotNull(arr, "Should not be null");
        Assertions.assertEquals(0, arr.length);
    }

    /**
     * Tests for {@link ArrayUtil#removeFromArray(Object[], Object)}
     */
    @Test
    public void testRemoveFromArray_MultiInput() {
        String[] input = new String[]{ "a", "b", "c" };
        String[] arr = ArrayUtil.removeFromArray(input, null);
        Assertions.assertNotNull(arr, "Should not be null");
        Assertions.assertEquals(3, arr.length);
        Assertions.assertEquals("a", arr[0]);
        Assertions.assertEquals("b", arr[1]);
        Assertions.assertEquals("c", arr[2]);
        // Remove an actual item
        arr = ArrayUtil.removeFromArray(input, "b");
        Assertions.assertNotNull(arr, "Should not be null");
        Assertions.assertEquals(2, arr.length);
        Assertions.assertEquals("a", arr[0]);
        Assertions.assertEquals("c", arr[1]);
    }
}

