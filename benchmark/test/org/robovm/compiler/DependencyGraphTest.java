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
package org.robovm.compiler;


import org.junit.Assert;
import org.junit.Test;
import org.robovm.compiler.clazz.Clazz;
import org.robovm.compiler.config.Config;
import org.robovm.compiler.config.Config.TreeShakerMode;
import org.robovm.rt.annotation.StronglyLinked;
import org.robovm.rt.annotation.WeaklyLinked;


/**
 * Tests {@link DependencyGraph}.
 */
public class DependencyGraphTest {
    Config config;

    Clazz Root;

    Clazz A;

    Clazz B;

    Clazz C;

    public static class Root {}

    public static class A {
        public A(byte a) {
        }

        @StronglyLinked
        public A(short a) {
        }

        @WeaklyLinked
        public A(int a) {
        }

        public void a() {
        }

        @StronglyLinked
        public void b() {
        }

        @WeaklyLinked
        public void c() {
        }
    }

    @WeaklyLinked
    public static class B {
        public B(byte a) {
        }

        @StronglyLinked
        public B(short a) {
        }

        @WeaklyLinked
        public B(int a) {
        }

        public void a() {
        }

        @StronglyLinked
        public void b() {
        }

        @WeaklyLinked
        public void c() {
        }
    }

    @StronglyLinked
    public static class C {
        public C(byte a) {
        }

        @StronglyLinked
        public C(short a) {
        }

        @WeaklyLinked
        public C(int a) {
        }

        public void a() {
        }

        @StronglyLinked
        public void b() {
        }

        @WeaklyLinked
        public void c() {
        }
    }

    @Test
    public void testWeaklyStronglyLinkedMethodInfos() throws Exception {
        Assert.assertFalse(A.getClazzInfo().getMethod("<init>", "(B)V").isStronglyLinked());
        Assert.assertFalse(A.getClazzInfo().getMethod("<init>", "(B)V").isWeaklyLinked());
        Assert.assertTrue(A.getClazzInfo().getMethod("<init>", "(S)V").isStronglyLinked());
        Assert.assertFalse(A.getClazzInfo().getMethod("<init>", "(S)V").isWeaklyLinked());
        Assert.assertFalse(A.getClazzInfo().getMethod("<init>", "(I)V").isStronglyLinked());
        Assert.assertTrue(A.getClazzInfo().getMethod("<init>", "(I)V").isWeaklyLinked());
        Assert.assertFalse(A.getClazzInfo().getMethod("a", "()V").isStronglyLinked());
        Assert.assertFalse(A.getClazzInfo().getMethod("a", "()V").isWeaklyLinked());
        Assert.assertTrue(A.getClazzInfo().getMethod("b", "()V").isStronglyLinked());
        Assert.assertFalse(A.getClazzInfo().getMethod("b", "()V").isWeaklyLinked());
        Assert.assertFalse(A.getClazzInfo().getMethod("c", "()V").isStronglyLinked());
        Assert.assertTrue(A.getClazzInfo().getMethod("c", "()V").isWeaklyLinked());
        Assert.assertFalse(B.getClazzInfo().getMethod("<init>", "(B)V").isStronglyLinked());
        Assert.assertTrue(B.getClazzInfo().getMethod("<init>", "(B)V").isWeaklyLinked());
        Assert.assertTrue(B.getClazzInfo().getMethod("<init>", "(S)V").isStronglyLinked());
        Assert.assertFalse(B.getClazzInfo().getMethod("<init>", "(S)V").isWeaklyLinked());
        Assert.assertFalse(B.getClazzInfo().getMethod("<init>", "(I)V").isStronglyLinked());
        Assert.assertTrue(B.getClazzInfo().getMethod("<init>", "(I)V").isWeaklyLinked());
        Assert.assertFalse(B.getClazzInfo().getMethod("a", "()V").isStronglyLinked());
        Assert.assertTrue(B.getClazzInfo().getMethod("a", "()V").isWeaklyLinked());
        Assert.assertTrue(B.getClazzInfo().getMethod("b", "()V").isStronglyLinked());
        Assert.assertFalse(B.getClazzInfo().getMethod("b", "()V").isWeaklyLinked());
        Assert.assertFalse(B.getClazzInfo().getMethod("c", "()V").isStronglyLinked());
        Assert.assertTrue(B.getClazzInfo().getMethod("c", "()V").isWeaklyLinked());
        Assert.assertTrue(C.getClazzInfo().getMethod("<init>", "(B)V").isStronglyLinked());
        Assert.assertFalse(C.getClazzInfo().getMethod("<init>", "(B)V").isWeaklyLinked());
        Assert.assertTrue(C.getClazzInfo().getMethod("<init>", "(S)V").isStronglyLinked());
        Assert.assertFalse(C.getClazzInfo().getMethod("<init>", "(S)V").isWeaklyLinked());
        Assert.assertFalse(C.getClazzInfo().getMethod("<init>", "(I)V").isStronglyLinked());
        Assert.assertTrue(C.getClazzInfo().getMethod("<init>", "(I)V").isWeaklyLinked());
        Assert.assertTrue(C.getClazzInfo().getMethod("a", "()V").isStronglyLinked());
        Assert.assertFalse(C.getClazzInfo().getMethod("a", "()V").isWeaklyLinked());
        Assert.assertTrue(C.getClazzInfo().getMethod("b", "()V").isStronglyLinked());
        Assert.assertFalse(C.getClazzInfo().getMethod("b", "()V").isWeaklyLinked());
        Assert.assertFalse(C.getClazzInfo().getMethod("c", "()V").isStronglyLinked());
        Assert.assertTrue(C.getClazzInfo().getMethod("c", "()V").isWeaklyLinked());
    }

    @Test
    public void testFindReachableClassesNoTreeShaking() throws Exception {
        DependencyGraph graph = new DependencyGraph(TreeShakerMode.none);
        graph.add(Root, true);
        graph.add(A, false);
        graph.add(B, false);
        graph.add(C, false);
        Assert.assertTrue(graph.findReachableClasses().contains(Root.getInternalName()));
        Assert.assertTrue(graph.findReachableClasses().contains(A.getInternalName()));
        Assert.assertTrue(graph.findReachableClasses().contains(B.getInternalName()));
        Assert.assertTrue(graph.findReachableClasses().contains(C.getInternalName()));
    }

    @Test
    public void testFindReachableMethodsNoTreeShaking() throws Exception {
        DependencyGraph graph = new DependencyGraph(TreeShakerMode.none);
        graph.add(Root, true);
        graph.add(A, false);
        graph.add(B, false);
        graph.add(C, false);
        Assert.assertEquals(19, graph.findReachableMethods().size());
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(Root.getInternalName(), "<init>", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(A.getInternalName(), "<init>", "(B)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(A.getInternalName(), "<init>", "(S)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(A.getInternalName(), "<init>", "(I)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(A.getInternalName(), "a", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(A.getInternalName(), "b", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(A.getInternalName(), "c", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(B.getInternalName(), "<init>", "(B)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(B.getInternalName(), "<init>", "(S)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(B.getInternalName(), "<init>", "(I)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(B.getInternalName(), "a", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(B.getInternalName(), "b", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(B.getInternalName(), "c", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "<init>", "(B)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "<init>", "(S)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "<init>", "(I)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "a", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "b", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "c", "()V")));
    }

    @Test
    public void testFindReachableMethodsConservativeTreeShaking() throws Exception {
        DependencyGraph graph = new DependencyGraph(TreeShakerMode.conservative);
        graph.add(Root, true);
        graph.add(A, false);
        graph.add(B, false);
        graph.add(C, false);
        Assert.assertEquals(11, graph.findReachableMethods().size());
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(Root.getInternalName(), "<init>", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(A.getInternalName(), "<init>", "(B)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(A.getInternalName(), "<init>", "(S)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(A.getInternalName(), "a", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(A.getInternalName(), "b", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(B.getInternalName(), "<init>", "(S)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(B.getInternalName(), "b", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "<init>", "(B)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "<init>", "(S)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "a", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "b", "()V")));
    }

    @Test
    public void testFindReachableMethodsAggressiveTreeShaking() throws Exception {
        DependencyGraph graph = new DependencyGraph(TreeShakerMode.aggressive);
        graph.add(Root, true);
        graph.add(A, false);
        graph.add(B, false);
        graph.add(C, false);
        Assert.assertEquals(10, graph.findReachableMethods().size());
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(Root.getInternalName(), "<init>", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(A.getInternalName(), "<init>", "(B)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(A.getInternalName(), "<init>", "(S)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(A.getInternalName(), "b", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(B.getInternalName(), "<init>", "(S)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(B.getInternalName(), "b", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "<init>", "(B)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "<init>", "(S)V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "a", "()V")));
        Assert.assertTrue(graph.findReachableMethods().contains(new org.apache.commons.lang3.tuple.ImmutableTriple(C.getInternalName(), "b", "()V")));
    }
}

