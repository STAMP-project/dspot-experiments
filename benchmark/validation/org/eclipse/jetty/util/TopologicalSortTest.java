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


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class TopologicalSortTest {
    @Test
    public void testNoDependencies() {
        String[] s = new String[]{ "D", "E", "C", "B", "A" };
        TopologicalSort<String> ts = new TopologicalSort();
        ts.sort(s);
        MatcherAssert.assertThat(s, Matchers.arrayContaining("D", "E", "C", "B", "A"));
    }

    @Test
    public void testSimpleLinear() {
        String[] s = new String[]{ "D", "E", "C", "B", "A" };
        TopologicalSort<String> ts = new TopologicalSort();
        ts.addDependency("B", "A");
        ts.addDependency("C", "B");
        ts.addDependency("D", "C");
        ts.addDependency("E", "D");
        ts.sort(s);
        MatcherAssert.assertThat(s, Matchers.arrayContaining("A", "B", "C", "D", "E"));
    }

    @Test
    public void testDisjoint() {
        String[] s = new String[]{ "A", "C", "B", "CC", "AA", "BB" };
        TopologicalSort<String> ts = new TopologicalSort();
        ts.addDependency("B", "A");
        ts.addDependency("C", "B");
        ts.addDependency("BB", "AA");
        ts.addDependency("CC", "BB");
        ts.sort(s);
        MatcherAssert.assertThat(s, Matchers.arrayContaining("A", "B", "C", "AA", "BB", "CC"));
    }

    @Test
    public void testDisjointReversed() {
        String[] s = new String[]{ "CC", "AA", "BB", "A", "C", "B" };
        TopologicalSort<String> ts = new TopologicalSort();
        ts.addDependency("B", "A");
        ts.addDependency("C", "B");
        ts.addDependency("BB", "AA");
        ts.addDependency("CC", "BB");
        ts.sort(s);
        MatcherAssert.assertThat(s, Matchers.arrayContaining("AA", "BB", "CC", "A", "B", "C"));
    }

    @Test
    public void testDisjointMixed() {
        String[] s = new String[]{ "CC", "A", "AA", "C", "BB", "B" };
        TopologicalSort<String> ts = new TopologicalSort();
        ts.addDependency("B", "A");
        ts.addDependency("C", "B");
        ts.addDependency("BB", "AA");
        ts.addDependency("CC", "BB");
        ts.sort(s);
        // Check direct ordering
        MatcherAssert.assertThat(indexOf(s, "A"), Matchers.lessThan(indexOf(s, "B")));
        MatcherAssert.assertThat(indexOf(s, "B"), Matchers.lessThan(indexOf(s, "C")));
        MatcherAssert.assertThat(indexOf(s, "AA"), Matchers.lessThan(indexOf(s, "BB")));
        MatcherAssert.assertThat(indexOf(s, "BB"), Matchers.lessThan(indexOf(s, "CC")));
    }

    @Test
    public void testTree() {
        String[] s = new String[]{ "LeafA0", "LeafB0", "LeafA1", "Root", "BranchA", "LeafB1", "BranchB" };
        TopologicalSort<String> ts = new TopologicalSort();
        ts.addDependency("BranchB", "Root");
        ts.addDependency("BranchA", "Root");
        ts.addDependency("LeafA1", "BranchA");
        ts.addDependency("LeafA0", "BranchA");
        ts.addDependency("LeafB0", "BranchB");
        ts.addDependency("LeafB1", "BranchB");
        ts.sort(s);
        // Check direct ordering
        MatcherAssert.assertThat(indexOf(s, "Root"), Matchers.lessThan(indexOf(s, "BranchA")));
        MatcherAssert.assertThat(indexOf(s, "Root"), Matchers.lessThan(indexOf(s, "BranchB")));
        MatcherAssert.assertThat(indexOf(s, "BranchA"), Matchers.lessThan(indexOf(s, "LeafA0")));
        MatcherAssert.assertThat(indexOf(s, "BranchA"), Matchers.lessThan(indexOf(s, "LeafA1")));
        MatcherAssert.assertThat(indexOf(s, "BranchB"), Matchers.lessThan(indexOf(s, "LeafB0")));
        MatcherAssert.assertThat(indexOf(s, "BranchB"), Matchers.lessThan(indexOf(s, "LeafB1")));
        // check remnant ordering of original list
        MatcherAssert.assertThat(indexOf(s, "BranchA"), Matchers.lessThan(indexOf(s, "BranchB")));
        MatcherAssert.assertThat(indexOf(s, "LeafA0"), Matchers.lessThan(indexOf(s, "LeafA1")));
        MatcherAssert.assertThat(indexOf(s, "LeafB0"), Matchers.lessThan(indexOf(s, "LeafB1")));
    }

    @Test
    public void testPreserveOrder() {
        String[] s = new String[]{ "Deep", "Foobar", "Wibble", "Bozo", "XXX", "12345", "Test" };
        TopologicalSort<String> ts = new TopologicalSort();
        ts.addDependency("Deep", "Test");
        ts.addDependency("Deep", "Wibble");
        ts.addDependency("Deep", "12345");
        ts.addDependency("Deep", "XXX");
        ts.addDependency("Deep", "Foobar");
        ts.addDependency("Deep", "Bozo");
        ts.sort(s);
        MatcherAssert.assertThat(s, Matchers.arrayContaining("Foobar", "Wibble", "Bozo", "XXX", "12345", "Test", "Deep"));
    }

    @Test
    public void testSimpleLoop() {
        String[] s = new String[]{ "A", "B", "C", "D", "E" };
        TopologicalSort<String> ts = new TopologicalSort();
        ts.addDependency("B", "A");
        ts.addDependency("A", "B");
        Assertions.assertThrows(IllegalStateException.class, () -> {
            ts.sort(s);
        });
    }

    @Test
    public void testDeepLoop() {
        String[] s = new String[]{ "A", "B", "C", "D", "E" };
        TopologicalSort<String> ts = new TopologicalSort();
        ts.addDependency("B", "A");
        ts.addDependency("C", "B");
        ts.addDependency("D", "C");
        ts.addDependency("E", "D");
        ts.addDependency("A", "E");
        Assertions.assertThrows(IllegalStateException.class, () -> {
            ts.sort(s);
        });
    }
}

