/**
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util;


import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ConstantPoolTest {
    static final class TestConstant extends AbstractConstant<ConstantPoolTest.TestConstant> {
        TestConstant(int id, String name) {
            super(id, name);
        }
    }

    private static final ConstantPool<ConstantPoolTest.TestConstant> pool = new ConstantPool<ConstantPoolTest.TestConstant>() {
        @Override
        protected ConstantPoolTest.TestConstant newConstant(int id, String name) {
            return new ConstantPoolTest.TestConstant(id, name);
        }
    };

    @Test(expected = NullPointerException.class)
    public void testCannotProvideNullName() {
        ConstantPoolTest.pool.valueOf(null);
    }

    @Test
    @SuppressWarnings("RedundantStringConstructorCall")
    public void testUniqueness() {
        ConstantPoolTest.TestConstant a = ConstantPoolTest.pool.valueOf(new String("Leroy"));
        ConstantPoolTest.TestConstant b = ConstantPoolTest.pool.valueOf(new String("Leroy"));
        Assert.assertThat(a, CoreMatchers.is(CoreMatchers.sameInstance(b)));
    }

    @Test
    public void testIdUniqueness() {
        ConstantPoolTest.TestConstant one = ConstantPoolTest.pool.valueOf("one");
        ConstantPoolTest.TestConstant two = ConstantPoolTest.pool.valueOf("two");
        Assert.assertThat(id(), CoreMatchers.is(CoreMatchers.not(id())));
    }

    @Test
    public void testCompare() {
        ConstantPoolTest.TestConstant a = ConstantPoolTest.pool.valueOf("a_alpha");
        ConstantPoolTest.TestConstant b = ConstantPoolTest.pool.valueOf("b_beta");
        ConstantPoolTest.TestConstant c = ConstantPoolTest.pool.valueOf("c_gamma");
        ConstantPoolTest.TestConstant d = ConstantPoolTest.pool.valueOf("d_delta");
        ConstantPoolTest.TestConstant e = ConstantPoolTest.pool.valueOf("e_epsilon");
        Set<ConstantPoolTest.TestConstant> set = new TreeSet<ConstantPoolTest.TestConstant>();
        set.add(b);
        set.add(c);
        set.add(e);
        set.add(d);
        set.add(a);
        ConstantPoolTest.TestConstant[] array = set.toArray(new ConstantPoolTest.TestConstant[0]);
        Assert.assertThat(array.length, CoreMatchers.is(5));
        // Sort by name
        Arrays.sort(array, new Comparator<ConstantPoolTest.TestConstant>() {
            @Override
            public int compare(ConstantPoolTest.TestConstant o1, ConstantPoolTest.TestConstant o2) {
                return name().compareTo(name());
            }
        });
        Assert.assertThat(array[0], CoreMatchers.is(CoreMatchers.sameInstance(a)));
        Assert.assertThat(array[1], CoreMatchers.is(CoreMatchers.sameInstance(b)));
        Assert.assertThat(array[2], CoreMatchers.is(CoreMatchers.sameInstance(c)));
        Assert.assertThat(array[3], CoreMatchers.is(CoreMatchers.sameInstance(d)));
        Assert.assertThat(array[4], CoreMatchers.is(CoreMatchers.sameInstance(e)));
    }

    @Test
    public void testComposedName() {
        ConstantPoolTest.TestConstant a = ConstantPoolTest.pool.valueOf(Object.class, "A");
        Assert.assertThat(name(), CoreMatchers.is("java.lang.Object#A"));
    }
}

