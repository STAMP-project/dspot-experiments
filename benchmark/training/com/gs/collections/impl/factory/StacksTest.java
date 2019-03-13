/**
 * Copyright 2011 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.factory;


import Stacks.immutable;
import Stacks.mutable;
import com.gs.collections.api.factory.stack.ImmutableStackFactory;
import com.gs.collections.api.stack.ImmutableStack;
import com.gs.collections.impl.stack.mutable.ArrayStack;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

import static Stacks.immutable;


public class StacksTest {
    @Test
    public void immutables() {
        ImmutableStackFactory stackFactory = immutable;
        Assert.assertEquals(ArrayStack.newStack(), stackFactory.of());
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of());
        Assert.assertEquals(ArrayStack.newStackWith(1), stackFactory.of(1));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1));
        Assert.assertEquals(ArrayStack.newStackWith(1, 2), stackFactory.of(1, 2));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2));
        Assert.assertEquals(ArrayStack.newStackWith(1, 2, 3), stackFactory.of(1, 2, 3));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3));
        Assert.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4), stackFactory.of(1, 2, 3, 4));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4));
        Assert.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4, 5), stackFactory.of(1, 2, 3, 4, 5));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4, 5));
        Assert.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4, 5, 6), stackFactory.of(1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4, 5, 6));
        Assert.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4, 5, 6, 7), stackFactory.of(1, 2, 3, 4, 5, 6, 7));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4, 5, 6, 7));
        Assert.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4, 5, 6, 7, 8), stackFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Assert.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4, 5, 6, 7, 8, 9), stackFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Assert.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), stackFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Assert.assertEquals(ArrayStack.newStackWith(3, 2, 1), stackFactory.ofAll(ArrayStack.newStackWith(1, 2, 3)));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.ofAll(ArrayStack.newStackWith(1, 2, 3)));
    }

    @Test
    public void emptyStack() {
        Assert.assertTrue(immutable.of().isEmpty());
    }

    @Test
    public void newStackWith() {
        ImmutableStack<String> stack = immutable.of();
        Assert.assertEquals(stack, immutable.of(stack.toArray()));
        Assert.assertEquals((stack = stack.push("1")), immutable.of("1"));
        Assert.assertEquals((stack = stack.push("2")), immutable.of("1", "2"));
        Assert.assertEquals((stack = stack.push("3")), immutable.of("1", "2", "3"));
        Assert.assertEquals((stack = stack.push("4")), immutable.of("1", "2", "3", "4"));
        Assert.assertEquals((stack = stack.push("5")), immutable.of("1", "2", "3", "4", "5"));
        Assert.assertEquals((stack = stack.push("6")), immutable.of("1", "2", "3", "4", "5", "6"));
        Assert.assertEquals((stack = stack.push("7")), immutable.of("1", "2", "3", "4", "5", "6", "7"));
        Assert.assertEquals((stack = stack.push("8")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8"));
        Assert.assertEquals((stack = stack.push("9")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        Assert.assertEquals((stack = stack.push("10")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        Assert.assertEquals((stack = stack.push("11")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        Assert.assertEquals((stack = stack.push("12")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
    }

    @SuppressWarnings("RedundantArrayCreation")
    @Test
    public void newStackWithArray() {
        ImmutableStack<String> stack = immutable.of();
        Assert.assertEquals((stack = stack.push("1")), immutable.of(new String[]{ "1" }));
        Assert.assertEquals((stack = stack.push("2")), immutable.of(new String[]{ "1", "2" }));
        Assert.assertEquals((stack = stack.push("3")), immutable.of(new String[]{ "1", "2", "3" }));
        Assert.assertEquals((stack = stack.push("4")), immutable.of(new String[]{ "1", "2", "3", "4" }));
        Assert.assertEquals((stack = stack.push("5")), immutable.of(new String[]{ "1", "2", "3", "4", "5" }));
        Assert.assertEquals((stack = stack.push("6")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6" }));
        Assert.assertEquals((stack = stack.push("7")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6", "7" }));
        Assert.assertEquals((stack = stack.push("8")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6", "7", "8" }));
        Assert.assertEquals((stack = stack.push("9")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9" }));
        Assert.assertEquals((stack = stack.push("10")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        Assert.assertEquals((stack = stack.push("11")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
    }

    @Test
    public void newStackWithStack() {
        ImmutableStack<String> stack = immutable.of();
        ArrayStack<String> arrayStack = ArrayStack.newStackWith("1");
        Assert.assertEquals((stack = stack.push("1")), arrayStack.toImmutable());
        arrayStack.push("2");
        Assert.assertEquals((stack = stack.push("2")), arrayStack.toImmutable());
        arrayStack.push("3");
        Assert.assertEquals((stack = stack.push("3")), arrayStack.toImmutable());
        arrayStack.push("4");
        Assert.assertEquals((stack = stack.push("4")), arrayStack.toImmutable());
        arrayStack.push("5");
        Assert.assertEquals((stack = stack.push("5")), arrayStack.toImmutable());
        arrayStack.push("6");
        Assert.assertEquals((stack = stack.push("6")), arrayStack.toImmutable());
        arrayStack.push("7");
        Assert.assertEquals((stack = stack.push("7")), arrayStack.toImmutable());
        arrayStack.push("8");
        Assert.assertEquals((stack = stack.push("8")), arrayStack.toImmutable());
        arrayStack.push("9");
        Assert.assertEquals((stack = stack.push("9")), arrayStack.toImmutable());
        arrayStack.push("10");
        Assert.assertEquals((stack = stack.push("10")), arrayStack.toImmutable());
        arrayStack.push("11");
        Assert.assertEquals((stack = stack.push("11")), arrayStack.toImmutable());
    }

    @Test
    public void newStackWithWithStack() {
        ArrayStack<Object> expected = ArrayStack.newStack();
        Assert.assertEquals(expected, mutable.ofAll(ArrayStack.newStack()));
        expected.push(1);
        Assert.assertEquals(ArrayStack.newStackWith(1), mutable.ofAll(expected));
        expected.push(2);
        Assert.assertEquals(ArrayStack.newStackWith(2, 1), mutable.ofAll(expected));
    }

    @Test
    public void classIsNonInstantiable() {
        Verify.assertClassNonInstantiable(Stacks.class);
    }
}

