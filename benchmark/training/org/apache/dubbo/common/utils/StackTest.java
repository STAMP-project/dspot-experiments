/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.utils;


import java.util.EmptyStackException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class StackTest {
    @Test
    public void testOps() throws Exception {
        Stack<String> stack = new Stack<String>();
        stack.push("one");
        MatcherAssert.assertThat(stack.get(0), Matchers.equalTo("one"));
        MatcherAssert.assertThat(stack.peek(), Matchers.equalTo("one"));
        MatcherAssert.assertThat(stack.size(), Matchers.equalTo(1));
        stack.push("two");
        MatcherAssert.assertThat(stack.get(0), Matchers.equalTo("one"));
        MatcherAssert.assertThat(stack.peek(), Matchers.equalTo("two"));
        MatcherAssert.assertThat(stack.size(), Matchers.equalTo(2));
        MatcherAssert.assertThat(stack.set(0, "three"), Matchers.equalTo("one"));
        MatcherAssert.assertThat(stack.remove(0), Matchers.equalTo("three"));
        MatcherAssert.assertThat(stack.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(stack.isEmpty(), Matchers.is(false));
        MatcherAssert.assertThat(stack.get(0), Matchers.equalTo("two"));
        MatcherAssert.assertThat(stack.peek(), Matchers.equalTo("two"));
        MatcherAssert.assertThat(stack.pop(), Matchers.equalTo("two"));
        MatcherAssert.assertThat(stack.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testClear() throws Exception {
        Stack<String> stack = new Stack<String>();
        stack.push("one");
        stack.push("two");
        MatcherAssert.assertThat(stack.isEmpty(), Matchers.is(false));
        stack.clear();
        MatcherAssert.assertThat(stack.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testIllegalPop() throws Exception {
        Assertions.assertThrows(EmptyStackException.class, () -> {
            Stack<String> stack = new Stack<String>();
            stack.pop();
        });
    }

    @Test
    public void testIllegalPeek() throws Exception {
        Assertions.assertThrows(EmptyStackException.class, () -> {
            Stack<String> stack = new Stack<String>();
            stack.peek();
        });
    }

    @Test
    public void testIllegalGet() throws Exception {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            Stack<String> stack = new Stack<String>();
            stack.get(1);
        });
    }

    @Test
    public void testIllegalSet() throws Exception {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            Stack<String> stack = new Stack<String>();
            stack.set(1, "illegal");
        });
    }

    @Test
    public void testIllegalRemove() throws Exception {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            Stack<String> stack = new Stack<String>();
            stack.remove(1);
        });
    }
}

