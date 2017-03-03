/**
 * Copyright (C) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.squareup.moshi;


public final class AmplLinkedHashTreeMapTest {
    @org.junit.Test
    public void iterationOrder() {
        com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
        map.put("a", "android");
        map.put("c", "cola");
        map.put("b", "bbq");
        org.assertj.core.api.Assertions.assertThat(map.keySet()).containsExactly("a", "c", "b");
        org.assertj.core.api.Assertions.assertThat(map.values()).containsExactly("android", "cola", "bbq");
    }

    @org.junit.Test
    public void removeRootDoesNotDoubleUnlink() {
        com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
        map.put("a", "android");
        map.put("c", "cola");
        map.put("b", "bbq");
        java.util.Iterator<java.util.Map.Entry<java.lang.String, java.lang.String>> it = map.entrySet().iterator();
        it.next();
        it.next();
        it.next();
        it.remove();
        org.assertj.core.api.Assertions.assertThat(map.keySet()).containsExactly("a", "c");
    }

    @org.junit.Test
    public void putNullKeyFails() {
        com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
        try {
            map.put(null, "android");
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException expected) {
        }
    }

    @org.junit.Test
    public void putNonComparableKeyFails() {
        com.squareup.moshi.LinkedHashTreeMap<java.lang.Object, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
        try {
            map.put(new java.lang.Object(), "android");
            org.junit.Assert.fail();
        } catch (java.lang.ClassCastException expected) {
        }
    }

    @org.junit.Test
    public void ContainsNonComparableKeyReturnsFalse() {
        com.squareup.moshi.LinkedHashTreeMap<java.lang.Object, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
        map.put("a", "android");
        org.assertj.core.api.Assertions.assertThat(map).doesNotContainKey(new java.lang.Object());
    }

    @org.junit.Test
    public void containsNullKeyIsAlwaysFalse() {
        com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
        map.put("a", "android");
        org.assertj.core.api.Assertions.assertThat(map).doesNotContainKey(null);
    }

    @org.junit.Test
    public void putOverrides() throws java.lang.Exception {
        com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
        org.assertj.core.api.Assertions.assertThat(map.put("d", "donut")).isNull();
        org.assertj.core.api.Assertions.assertThat(map.put("e", "eclair")).isNull();
        org.assertj.core.api.Assertions.assertThat(map.put("f", "froyo")).isNull();
        org.assertj.core.api.Assertions.assertThat(map.size()).isEqualTo(3);
        org.assertj.core.api.Assertions.assertThat(map.get("d")).isEqualTo("donut");
        org.assertj.core.api.Assertions.assertThat(map.put("d", "done")).isEqualTo("donut");
        org.assertj.core.api.Assertions.assertThat(map).hasSize(3);
    }

    @org.junit.Test
    public void emptyStringValues() {
        com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
        map.put("a", "");
        org.assertj.core.api.Assertions.assertThat(map.containsKey("a")).isTrue();
        org.assertj.core.api.Assertions.assertThat(map.get("a")).isEqualTo("");
    }

    // NOTE that this does not happen every time, but given the below predictable random,
    // this test will consistently fail (assuming the initial size is 16 and rehashing
    // size remains at 3/4)
    @org.junit.Test
    public void forceDoublingAndRehash() throws java.lang.Exception {
        java.util.Random random = new java.util.Random(1367593214724L);
        com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
        java.lang.String[] keys = new java.lang.String[1000];
        for (int i = 0; i < (keys.length); i++) {
            keys[i] = ((java.lang.Integer.toString(java.lang.Math.abs(random.nextInt()), 36)) + "-") + i;
            map.put(keys[i], ("" + i));
        }
        for (int i = 0; i < (keys.length); i++) {
            java.lang.String key = keys[i];
            org.assertj.core.api.Assertions.assertThat(map.containsKey(key)).isTrue();
            org.assertj.core.api.Assertions.assertThat(map.get(key)).isEqualTo(("" + i));
        }
    }

    @org.junit.Test
    public void clear() {
        com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
        map.put("a", "android");
        map.put("c", "cola");
        map.put("b", "bbq");
        map.clear();
        org.assertj.core.api.Assertions.assertThat(map.keySet()).containsExactly();
        org.assertj.core.api.Assertions.assertThat(map).isEmpty();
    }

    @org.junit.Test
    public void equalsAndHashCode() throws java.lang.Exception {
        com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.Integer> map1 = new com.squareup.moshi.LinkedHashTreeMap<>();
        map1.put("A", 1);
        map1.put("B", 2);
        map1.put("C", 3);
        map1.put("D", 4);
        com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.Integer> map2 = new com.squareup.moshi.LinkedHashTreeMap<>();
        map2.put("C", 3);
        map2.put("B", 2);
        map2.put("D", 4);
        map2.put("A", 1);
        org.assertj.core.api.Assertions.assertThat(map2).isEqualTo(map1);
        org.assertj.core.api.Assertions.assertThat(map2.hashCode()).isEqualTo(map1.hashCode());
    }

    @org.junit.Test
    public void avlWalker() {
        assertAvlWalker(node(node("a"), "b", node("c")), "a", "b", "c");
        assertAvlWalker(node(node(node("a"), "b", node("c")), "d", node(node("e"), "f", node("g"))), "a", "b", "c", "d", "e", "f", "g");
        assertAvlWalker(node(node(null, "a", node("b")), "c", node(node("d"), "e", null)), "a", "b", "c", "d", "e");
        assertAvlWalker(node(null, "a", node(null, "b", node(null, "c", node("d")))), "a", "b", "c", "d");
        assertAvlWalker(node(node(node(node("a"), "b", null), "c", null), "d", null), "a", "b", "c", "d");
    }

    private void assertAvlWalker(com.squareup.moshi.LinkedHashTreeMap.Node<java.lang.String, java.lang.String> root, java.lang.String... values) {
        com.squareup.moshi.LinkedHashTreeMap.AvlIterator<java.lang.String, java.lang.String> iterator = new com.squareup.moshi.LinkedHashTreeMap.AvlIterator<>();
        iterator.reset(root);
        for (java.lang.String value : values) {
            org.assertj.core.api.Assertions.assertThat(iterator.next().getKey()).isEqualTo(value);
        }
        org.assertj.core.api.Assertions.assertThat(iterator.next()).isNull();
    }

    @org.junit.Test
    public void avlBuilder() {
        assertAvlBuilder(1, "a");
        assertAvlBuilder(2, "(. a b)");
        assertAvlBuilder(3, "(a b c)");
        assertAvlBuilder(4, "(a b (. c d))");
        assertAvlBuilder(5, "(a b (c d e))");
        assertAvlBuilder(6, "((. a b) c (d e f))");
        assertAvlBuilder(7, "((a b c) d (e f g))");
        assertAvlBuilder(8, "((a b c) d (e f (. g h)))");
        assertAvlBuilder(9, "((a b c) d (e f (g h i)))");
        assertAvlBuilder(10, "((a b c) d ((. e f) g (h i j)))");
        assertAvlBuilder(11, "((a b c) d ((e f g) h (i j k)))");
        assertAvlBuilder(12, "((a b (. c d)) e ((f g h) i (j k l)))");
        assertAvlBuilder(13, "((a b (c d e)) f ((g h i) j (k l m)))");
        assertAvlBuilder(14, "(((. a b) c (d e f)) g ((h i j) k (l m n)))");
        assertAvlBuilder(15, "(((a b c) d (e f g)) h ((i j k) l (m n o)))");
        assertAvlBuilder(16, "(((a b c) d (e f g)) h ((i j k) l (m n (. o p))))");
        assertAvlBuilder(30, ("((((. a b) c (d e f)) g ((h i j) k (l m n))) o " + "(((p q r) s (t u v)) w ((x y z) A (B C D))))"));
        assertAvlBuilder(31, ("((((a b c) d (e f g)) h ((i j k) l (m n o))) p " + "(((q r s) t (u v w)) x ((y z A) B (C D E))))"));
    }

    private void assertAvlBuilder(int size, java.lang.String expected) {
        char[] values = "abcdefghijklmnopqrstuvwxyzABCDE".toCharArray();
        com.squareup.moshi.LinkedHashTreeMap.AvlBuilder<java.lang.String, java.lang.String> avlBuilder = new com.squareup.moshi.LinkedHashTreeMap.AvlBuilder<>();
        avlBuilder.reset(size);
        for (int i = 0; i < size; i++) {
            avlBuilder.add(node(java.lang.Character.toString(values[i])));
        }
        assertTree(expected, avlBuilder.root());
    }

    @org.junit.Test
    public void doubleCapacity() {
        // Arrays and generics don't get along.
        @java.lang.SuppressWarnings(value = "unchecked")
        com.squareup.moshi.LinkedHashTreeMap.Node<java.lang.String, java.lang.String>[] oldTable = new com.squareup.moshi.LinkedHashTreeMap.Node[1];
        oldTable[0] = node(node(node("a"), "b", node("c")), "d", node(node("e"), "f", node("g")));
        com.squareup.moshi.LinkedHashTreeMap.Node<java.lang.String, java.lang.String>[] newTable = com.squareup.moshi.LinkedHashTreeMap.doubleCapacity(oldTable);
        assertTree("(b d f)", newTable[0]);// Even hash codes!
        
        assertTree("(a c (. e g))", newTable[1]);// Odd hash codes!
        
    }

    @org.junit.Test
    public void doubleCapacityAllNodesOnLeft() {
        // Arrays and generics don't get along.
        @java.lang.SuppressWarnings(value = "unchecked")
        com.squareup.moshi.LinkedHashTreeMap.Node<java.lang.String, java.lang.String>[] oldTable = new com.squareup.moshi.LinkedHashTreeMap.Node[1];
        oldTable[0] = node(node("b"), "d", node("f"));
        com.squareup.moshi.LinkedHashTreeMap.Node<java.lang.String, java.lang.String>[] newTable = com.squareup.moshi.LinkedHashTreeMap.doubleCapacity(oldTable);
        assertTree("(b d f)", newTable[0]);// Even hash codes!
        
        org.assertj.core.api.Assertions.assertThat(newTable[1]).isNull();
        for (com.squareup.moshi.LinkedHashTreeMap.Node<?, ?> node : newTable) {
            if (node != null) {
                assertConsistent(node);
            }
        }
    }

    private static final com.squareup.moshi.LinkedHashTreeMap.Node<java.lang.String, java.lang.String> head = new com.squareup.moshi.LinkedHashTreeMap.Node<>();

    private com.squareup.moshi.LinkedHashTreeMap.Node<java.lang.String, java.lang.String> node(java.lang.String value) {
        return new com.squareup.moshi.LinkedHashTreeMap.Node<>(null, value, value.hashCode(), com.squareup.moshi.AmplLinkedHashTreeMapTest.head, com.squareup.moshi.AmplLinkedHashTreeMapTest.head);
    }

    private com.squareup.moshi.LinkedHashTreeMap.Node<java.lang.String, java.lang.String> node(com.squareup.moshi.LinkedHashTreeMap.Node<java.lang.String, java.lang.String> left, java.lang.String value, com.squareup.moshi.LinkedHashTreeMap.Node<java.lang.String, java.lang.String> right) {
        com.squareup.moshi.LinkedHashTreeMap.Node<java.lang.String, java.lang.String> result = node(value);
        if (left != null) {
            result.left = left;
            left.parent = result;
        }
        if (right != null) {
            result.right = right;
            right.parent = result;
        }
        return result;
    }

    private void assertTree(java.lang.String expected, com.squareup.moshi.LinkedHashTreeMap.Node<?, ?> root) {
        org.assertj.core.api.Assertions.assertThat(toString(root)).isEqualTo(expected);
        assertConsistent(root);
    }

    private void assertConsistent(com.squareup.moshi.LinkedHashTreeMap.Node<?, ?> node) {
        int leftHeight = 0;
        if ((node.left) != null) {
            assertConsistent(node.left);
            org.assertj.core.api.Assertions.assertThat(node.left.parent).isSameAs(node);
            leftHeight = node.left.height;
        }
        int rightHeight = 0;
        if ((node.right) != null) {
            assertConsistent(node.right);
            org.assertj.core.api.Assertions.assertThat(node.right.parent).isSameAs(node);
            rightHeight = node.right.height;
        }
        if ((node.parent) != null) {
            org.assertj.core.api.Assertions.assertThat((((node.parent.left) == node) || ((node.parent.right) == node))).isTrue();
        }
        if (((java.lang.Math.max(leftHeight, rightHeight)) + 1) != (node.height)) {
            org.junit.Assert.fail();
        }
    }

    private java.lang.String toString(com.squareup.moshi.LinkedHashTreeMap.Node<?, ?> root) {
        if (root == null) {
            return ".";
        }else
            if (((root.left) == null) && ((root.right) == null)) {
                return java.lang.String.valueOf(root.key);
            }else {
                return java.lang.String.format("(%s %s %s)", toString(root.left), root.key, toString(root.right));
            }
        
    }

    /* amplification of com.squareup.moshi.LinkedHashTreeMapTest#removeRootDoesNotDoubleUnlink */
    @org.junit.Test(timeout = 10000)
    public void removeRootDoesNotDoubleUnlink_add75402_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
            map.put("a", "android");
            map.put("c", "cola");
            map.put("b", "bbq");
            java.util.Iterator<java.util.Map.Entry<java.lang.String, java.lang.String>> it = map.entrySet().iterator();
            it.next();
            it.next();
            it.next();
            // MethodCallAdder
            it.remove();
            it.remove();
            org.assertj.core.api.Assertions.assertThat(map.keySet()).containsExactly("a", "c");
            org.junit.Assert.fail("removeRootDoesNotDoubleUnlink_add75402 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.LinkedHashTreeMapTest#removeRootDoesNotDoubleUnlink */
    @org.junit.Test(timeout = 10000)
    public void removeRootDoesNotDoubleUnlink_add75400_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
            map.put("a", "android");
            map.put("c", "cola");
            map.put("b", "bbq");
            java.util.Iterator<java.util.Map.Entry<java.lang.String, java.lang.String>> it = map.entrySet().iterator();
            it.next();
            // MethodCallAdder
            it.next();
            it.next();
            it.next();
            it.remove();
            org.assertj.core.api.Assertions.assertThat(map.keySet()).containsExactly("a", "c");
            org.junit.Assert.fail("removeRootDoesNotDoubleUnlink_add75400 should have thrown NoSuchElementException");
        } catch (java.util.NoSuchElementException eee) {
        }
    }

    /* amplification of com.squareup.moshi.LinkedHashTreeMapTest#removeRootDoesNotDoubleUnlink */
    @org.junit.Test(timeout = 10000)
    public void removeRootDoesNotDoubleUnlink_add75402_failAssert3_add75459_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
                map.put("a", "android");
                map.put("c", "cola");
                map.put("b", "bbq");
                java.util.Iterator<java.util.Map.Entry<java.lang.String, java.lang.String>> it = map.entrySet().iterator();
                it.next();
                // MethodCallAdder
                it.next();
                it.next();
                it.next();
                // MethodCallAdder
                it.remove();
                it.remove();
                org.assertj.core.api.Assertions.assertThat(map.keySet()).containsExactly("a", "c");
                org.junit.Assert.fail("removeRootDoesNotDoubleUnlink_add75402 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("removeRootDoesNotDoubleUnlink_add75402_failAssert3_add75459 should have thrown NoSuchElementException");
        } catch (java.util.NoSuchElementException eee) {
        }
    }

    /* amplification of com.squareup.moshi.LinkedHashTreeMapTest#removeRootDoesNotDoubleUnlink */
    @org.junit.Test(timeout = 10000)
    public void removeRootDoesNotDoubleUnlink_add75397_add75413_add75536_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
            map.put("a", "android");
            // AssertGenerator replace invocation
            java.lang.String o_removeRootDoesNotDoubleUnlink_add75397__4 = // MethodCallAdder
map.put("c", "cola");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_removeRootDoesNotDoubleUnlink_add75397__4);
            // AssertGenerator replace invocation
            java.lang.String o_removeRootDoesNotDoubleUnlink_add75397_add75413__8 = // MethodCallAdder
map.put("c", "cola");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_removeRootDoesNotDoubleUnlink_add75397_add75413__8, "cola");
            map.put("c", "cola");
            map.put("b", "bbq");
            java.util.Iterator<java.util.Map.Entry<java.lang.String, java.lang.String>> it = map.entrySet().iterator();
            it.next();
            // MethodCallAdder
            it.next();
            it.next();
            it.next();
            it.remove();
            org.assertj.core.api.Assertions.assertThat(map.keySet()).containsExactly("a", "c");
            org.junit.Assert.fail("removeRootDoesNotDoubleUnlink_add75397_add75413_add75536 should have thrown NoSuchElementException");
        } catch (java.util.NoSuchElementException eee) {
        }
    }

    /* amplification of com.squareup.moshi.LinkedHashTreeMapTest#removeRootDoesNotDoubleUnlink */
    @org.junit.Test(timeout = 10000)
    public void removeRootDoesNotDoubleUnlink_add75397_add75418_failAssert7_add75575() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.LinkedHashTreeMap<java.lang.String, java.lang.String> map = new com.squareup.moshi.LinkedHashTreeMap<>();
            // AssertGenerator replace invocation
            java.lang.String o_removeRootDoesNotDoubleUnlink_add75397_add75418_failAssert7_add75575__5 = // MethodCallAdder
map.put("a", "android");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_removeRootDoesNotDoubleUnlink_add75397_add75418_failAssert7_add75575__5);
            map.put("a", "android");
            // AssertGenerator replace invocation
            java.lang.String o_removeRootDoesNotDoubleUnlink_add75397__4 = // MethodCallAdder
map.put("c", "cola");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_removeRootDoesNotDoubleUnlink_add75397__4);
            map.put("c", "cola");
            map.put("b", "bbq");
            java.util.Iterator<java.util.Map.Entry<java.lang.String, java.lang.String>> it = map.entrySet().iterator();
            it.next();
            it.next();
            it.next();
            // MethodCallAdder
            it.remove();
            it.remove();
            org.assertj.core.api.Assertions.assertThat(map.keySet()).containsExactly("a", "c");
            org.junit.Assert.fail("removeRootDoesNotDoubleUnlink_add75397_add75418 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }
}

