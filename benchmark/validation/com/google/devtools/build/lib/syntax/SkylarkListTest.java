/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.syntax;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.devtools.build.lib.syntax.SkylarkList.MutableList;
import com.google.devtools.build.lib.syntax.SkylarkList.Tuple;
import com.google.devtools.build.lib.syntax.util.EvaluationTestCase;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for SkylarkList.
 */
@RunWith(JUnit4.class)
public class SkylarkListTest extends EvaluationTestCase {
    @Test
    public void testIndex() throws Exception {
        eval("l = [1, '2', 3]");
        assertThat(eval("l[0]")).isEqualTo(1);
        assertThat(eval("l[1]")).isEqualTo("2");
        assertThat(eval("l[2]")).isEqualTo(3);
        eval("t = (1, '2', 3)");
        assertThat(eval("t[0]")).isEqualTo(1);
        assertThat(eval("t[1]")).isEqualTo("2");
        assertThat(eval("t[2]")).isEqualTo(3);
    }

    @Test
    public void testIndexOutOfBounds() throws Exception {
        checkEvalError("index out of range (index is 3, but sequence has 3 elements)", "['a', 'b', 'c'][3]");
        checkEvalError("index out of range (index is 10, but sequence has 3 elements)", "['a', 'b', 'c'][10]");
        checkEvalError("index out of range (index is 0, but sequence has 0 elements)", "[][0]");
    }

    @Test
    public void testNegativeIndices() throws Exception {
        eval("l = ['a', 'b', 'c']");
        assertThat(eval("l[0]")).isEqualTo("a");
        assertThat(eval("l[-1]")).isEqualTo("c");
        assertThat(eval("l[-2]")).isEqualTo("b");
        assertThat(eval("l[-3]")).isEqualTo("a");
        checkEvalError("index out of range (index is -4, but sequence has 3 elements)", "l[-4]");
        checkEvalError("index out of range (index is -1, but sequence has 0 elements)", "[][-1]");
    }

    @Test
    public void testSlice() throws Exception {
        eval("l = ['a', 'b', 'c']");
        assertThat(listEval("l[0:3]")).containsExactly("a", "b", "c").inOrder();
        assertThat(listEval("l[0:2]")).containsExactly("a", "b").inOrder();
        assertThat(listEval("l[0:1]")).containsExactly("a").inOrder();
        assertThat(listEval("l[0:0]")).isEmpty();
        assertThat(listEval("l[1:3]")).containsExactly("b", "c").inOrder();
        assertThat(listEval("l[2:3]")).containsExactly("c").inOrder();
        assertThat(listEval("l[3:3]")).isEmpty();
        assertThat(listEval("l[2:1]")).isEmpty();
        assertThat(listEval("l[3:0]")).isEmpty();
        eval("t = ('a', 'b', 'c')");
        assertThat(listEval("t[0:3]")).containsExactly("a", "b", "c").inOrder();
        assertThat(listEval("t[1:2]")).containsExactly("b").inOrder();
    }

    @Test
    public void testSliceDefault() throws Exception {
        eval("l = ['a', 'b', 'c']");
        assertThat(listEval("l[:]")).containsExactly("a", "b", "c").inOrder();
        assertThat(listEval("l[:2]")).containsExactly("a", "b").inOrder();
        assertThat(listEval("l[2:]")).containsExactly("c").inOrder();
    }

    @Test
    public void testSliceNegative() throws Exception {
        eval("l = ['a', 'b', 'c']");
        assertThat(listEval("l[-2:-1]")).containsExactly("b").inOrder();
        assertThat(listEval("l[-2:]")).containsExactly("b", "c").inOrder();
        assertThat(listEval("l[0:-1]")).containsExactly("a", "b").inOrder();
        assertThat(listEval("l[-1:1]")).isEmpty();
    }

    @Test
    public void testSliceBounds() throws Exception {
        eval("l = ['a', 'b', 'c']");
        assertThat(listEval("l[0:5]")).containsExactly("a", "b", "c").inOrder();
        assertThat(listEval("l[-10:2]")).containsExactly("a", "b").inOrder();
        assertThat(listEval("l[3:10]")).isEmpty();
        assertThat(listEval("l[-10:-9]")).isEmpty();
    }

    @Test
    public void testSliceSkip() throws Exception {
        eval("l = ['a', 'b', 'c', 'd', 'e', 'f', 'g']");
        assertThat(listEval("l[0:6:2]")).containsExactly("a", "c", "e").inOrder();
        assertThat(listEval("l[0:7:2]")).containsExactly("a", "c", "e", "g").inOrder();
        assertThat(listEval("l[0:10:2]")).containsExactly("a", "c", "e", "g").inOrder();
        assertThat(listEval("l[-6:10:2]")).containsExactly("b", "d", "f").inOrder();
        assertThat(listEval("l[1:5:3]")).containsExactly("b", "e").inOrder();
        assertThat(listEval("l[-10:3:2]")).containsExactly("a", "c").inOrder();
        assertThat(listEval("l[-10:10:1]")).containsExactly("a", "b", "c", "d", "e", "f", "g").inOrder();
    }

    @Test
    public void testSliceNegativeSkip() throws Exception {
        eval("l = ['a', 'b', 'c', 'd', 'e', 'f', 'g']");
        assertThat(listEval("l[5:2:-1]")).containsExactly("f", "e", "d").inOrder();
        assertThat(listEval("l[5:2:-2]")).containsExactly("f", "d").inOrder();
        assertThat(listEval("l[5:3:-2]")).containsExactly("f").inOrder();
        assertThat(listEval("l[6::-4]")).containsExactly("g", "c").inOrder();
        assertThat(listEval("l[7::-4]")).containsExactly("g", "c").inOrder();
        assertThat(listEval("l[-1::-4]")).containsExactly("g", "c").inOrder();
        assertThat(listEval("l[-1:-10:-4]")).containsExactly("g", "c").inOrder();
        assertThat(listEval("l[-1:-3:-4]")).containsExactly("g").inOrder();
        assertThat(listEval("l[2:5:-1]")).isEmpty();
        assertThat(listEval("l[-10:5:-1]")).isEmpty();
        assertThat(listEval("l[1:-8:-1]")).containsExactly("b", "a").inOrder();
        checkEvalError("slice step cannot be zero", "l[2:5:0]");
    }

    @Test
    public void testListSize() throws Exception {
        assertThat(eval("len([42, 'hello, world', []])")).isEqualTo(3);
    }

    @Test
    public void testListEmpty() throws Exception {
        assertThat(eval("8 if [1, 2, 3] else 9")).isEqualTo(8);
        assertThat(eval("8 if [] else 9")).isEqualTo(9);
    }

    @Test
    public void testListConcat() throws Exception {
        assertThat(eval("[1, 2] + [3, 4]")).isEqualTo(SkylarkList.createImmutable(Tuple.of(1, 2, 3, 4)));
    }

    @Test
    public void testConcatListIndex() throws Exception {
        eval("l = [1, 2] + [3, 4]", "e0 = l[0]", "e1 = l[1]", "e2 = l[2]", "e3 = l[3]");
        assertThat(lookup("e0")).isEqualTo(1);
        assertThat(lookup("e1")).isEqualTo(2);
        assertThat(lookup("e2")).isEqualTo(3);
        assertThat(lookup("e3")).isEqualTo(4);
    }

    @Test
    public void testConcatListHierarchicalIndex() throws Exception {
        eval("l = [1] + (([2] + [3, 4]) + [5])", "e0 = l[0]", "e1 = l[1]", "e2 = l[2]", "e3 = l[3]", "e4 = l[4]");
        assertThat(lookup("e0")).isEqualTo(1);
        assertThat(lookup("e1")).isEqualTo(2);
        assertThat(lookup("e2")).isEqualTo(3);
        assertThat(lookup("e3")).isEqualTo(4);
        assertThat(lookup("e4")).isEqualTo(5);
    }

    @Test
    public void testConcatListSize() throws Exception {
        assertThat(eval("len([1, 2] + [3, 4])")).isEqualTo(4);
    }

    @Test
    public void testAppend() throws Exception {
        eval("l = [1, 2]");
        assertThat(NONE).isEqualTo(eval("l.append([3, 4])"));
        assertThat(eval("[1, 2, [3, 4]]")).isEqualTo(lookup("l"));
    }

    @Test
    public void testExtend() throws Exception {
        eval("l = [1, 2]");
        assertThat(NONE).isEqualTo(eval("l.extend([3, 4])"));
        assertThat(eval("[1, 2, 3, 4]")).isEqualTo(lookup("l"));
    }

    @Test
    public void testConcatListToString() throws Exception {
        eval("l = [1, 2] + [3, 4]", "s = str(l)");
        assertThat(lookup("s")).isEqualTo("[1, 2, 3, 4]");
    }

    @Test
    public void testConcatListNotEmpty() throws Exception {
        eval("l = [1, 2] + [3, 4]", "v = 1 if l else 0");
        assertThat(lookup("v")).isEqualTo(1);
    }

    @Test
    public void testConcatListEmpty() throws Exception {
        eval("l = [] + []", "v = 1 if l else 0");
        assertThat(lookup("v")).isEqualTo(0);
    }

    @Test
    public void testListComparison() throws Exception {
        assertThat(eval("(1, 'two', [3, 4]) == (1, 'two', [3, 4])")).isEqualTo(true);
        assertThat(eval("[1, 2, 3, 4] == [1, 2] + [3, 4]")).isEqualTo(true);
        assertThat(eval("[1, 2, 3, 4] == (1, 2, 3, 4)")).isEqualTo(false);
        assertThat(eval("[1, 2] == [1, 2, 3]")).isEqualTo(false);
        assertThat(eval("[] == []")).isEqualTo(true);
        assertThat(eval("() == ()")).isEqualTo(true);
        assertThat(eval("() == (1,)")).isEqualTo(false);
        assertThat(eval("(1) == (1,)")).isEqualTo(false);
    }

    @Test
    public void testMutatorsCheckMutability() throws Exception {
        Mutability mutability = Mutability.create("test");
        MutableList<Object> list = MutableList.copyOf(mutability, ImmutableList.of(1, 2, 3));
        mutability.freeze();
        try {
            list.add(4, null, mutability);
            Assert.fail("expected exception");
        } catch (EvalException e) {
            assertThat(e).hasMessage("trying to mutate a frozen object");
        }
        try {
            list.add(0, 4, null, mutability);
            Assert.fail("expected exception");
        } catch (EvalException e) {
            assertThat(e).hasMessage("trying to mutate a frozen object");
        }
        try {
            list.addAll(ImmutableList.of(4, 5, 6), null, mutability);
            Assert.fail("expected exception");
        } catch (EvalException e) {
            assertThat(e).hasMessage("trying to mutate a frozen object");
        }
        try {
            list.remove(0, null, mutability);
            Assert.fail("expected exception");
        } catch (EvalException e) {
            assertThat(e).hasMessage("trying to mutate a frozen object");
        }
        try {
            list.set(0, 10, null, mutability);
            Assert.fail("expected exception");
        } catch (EvalException e) {
            assertThat(e).hasMessage("trying to mutate a frozen object");
        }
    }

    @Test
    public void testCannotMutateAfterShallowFreeze() throws Exception {
        Mutability mutability = Mutability.createAllowingShallowFreeze("test");
        MutableList<Object> list = MutableList.copyOf(mutability, ImmutableList.of(1, 2, 3));
        list.unsafeShallowFreeze();
        try {
            list.add(4, null, mutability);
            Assert.fail("expected exception");
        } catch (EvalException e) {
            assertThat(e).hasMessage("trying to mutate a frozen object");
        }
    }

    @Test
    public void testCopyOfTakesCopy() throws EvalException {
        ArrayList<String> copyFrom = Lists.newArrayList("hi");
        Mutability mutability = Mutability.create("test");
        MutableList<String> mutableList = MutableList.copyOf(mutability, copyFrom);
        copyFrom.add("added1");
        /* loc= */
        mutableList.add("added2", null, mutability);
        assertThat(copyFrom).containsExactly("hi", "added1").inOrder();
        assertThat(mutableList).containsExactly("hi", "added2").inOrder();
    }

    @Test
    public void testWrapUnsafeTakesOwnershipOfPassedArrayList() throws EvalException {
        ArrayList<String> wrapped = Lists.newArrayList("hi");
        Mutability mutability = Mutability.create("test");
        MutableList<String> mutableList = MutableList.wrapUnsafe(mutability, wrapped);
        // Big no-no, but we're proving a point.
        wrapped.add("added1");
        /* loc= */
        mutableList.add("added2", null, mutability);
        assertThat(wrapped).containsExactly("hi", "added1", "added2").inOrder();
        assertThat(mutableList).containsExactly("hi", "added1", "added2").inOrder();
    }

    @Test
    public void testGetSkylarkType_GivesExpectedClassesForListsAndTuples() throws Exception {
        Class<?> emptyTupleClass = Tuple.empty().getClass();
        Class<?> tupleClass = Tuple.of(1, "a", "b").getClass();
        Class<?> mutableListClass = MutableList.copyOf(env, Tuple.of(1, 2, 3)).getClass();
        assertThat(EvalUtils.getSkylarkType(mutableListClass)).isEqualTo(MutableList.class);
        assertThat(EvalUtils.getSkylarkType(emptyTupleClass)).isEqualTo(Tuple.class);
        assertThat(EvalUtils.getSkylarkType(tupleClass)).isEqualTo(Tuple.class);
    }
}

