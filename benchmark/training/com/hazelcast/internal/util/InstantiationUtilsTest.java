/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.util;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InstantiationUtilsTest extends HazelcastTestSupport {
    @Test
    public void newInstanceOrNull_createInstanceWithNoArguments() {
        InstantiationUtilsTest.ClassWithNonArgConstructor instance = InstantiationUtils.newInstanceOrNull(InstantiationUtilsTest.ClassWithNonArgConstructor.class);
        Assert.assertNotNull(instance);
    }

    @Test
    public void newInstanceOrNull_createInstanceWithSingleArgument() {
        InstantiationUtilsTest.ClassWithStringConstructor instance = InstantiationUtils.newInstanceOrNull(InstantiationUtilsTest.ClassWithStringConstructor.class, "foo");
        Assert.assertNotNull(instance);
    }

    @Test
    public void newInstanceOrNull_createInstanceWithSubclass() {
        InstantiationUtilsTest.ClassWithObjectConstructor instance = InstantiationUtils.newInstanceOrNull(InstantiationUtilsTest.ClassWithObjectConstructor.class, "foo");
        Assert.assertNotNull(instance);
    }

    @Test
    public void newInstanceOrNull_noMatchingConstructorFound_different_length() {
        InstantiationUtilsTest.ClassWithNonArgConstructor instance = InstantiationUtils.newInstanceOrNull(InstantiationUtilsTest.ClassWithNonArgConstructor.class, "foo");
        Assert.assertNull(instance);
    }

    @Test
    public void newInstanceOrNull_noMatchingConstructorFound_different_types() {
        InstantiationUtilsTest.ClassWithStringConstructor instance = InstantiationUtils.newInstanceOrNull(InstantiationUtilsTest.ClassWithStringConstructor.class, 43);
        Assert.assertNull(instance);
    }

    @Test
    public void newInstanceOrNull_nullIsMatchingAllTypes() {
        InstantiationUtilsTest.ClassWithTwoArgConstructorConstructor instance = InstantiationUtils.newInstanceOrNull(InstantiationUtilsTest.ClassWithTwoArgConstructorConstructor.class, "foo", null);
        Assert.assertNotNull(instance);
    }

    @Test
    public void newInstanceOrNull_primitiveArgInConstructor() {
        InstantiationUtilsTest.ClassWithPrimitiveArgConstructor instance = InstantiationUtils.newInstanceOrNull(InstantiationUtilsTest.ClassWithPrimitiveArgConstructor.class, true, ((byte) (0)), 'c', 42.0, 42.0F, 42, ((long) (43)), ((short) (42)));
        Assert.assertNotNull(instance);
    }

    @Test
    public void newInstanceOrNull_primitiveArgInConstructorPassingNull() {
        InstantiationUtilsTest.ClassWithTwoConstructorsIncludingPrimitives instance = InstantiationUtils.newInstanceOrNull(InstantiationUtilsTest.ClassWithTwoConstructorsIncludingPrimitives.class, 42, null);
        Assert.assertNotNull(instance);
    }

    @Test(expected = AmbigiousInstantiationException.class)
    public void newInstanceOrNull_ambigiousConstructor() {
        InstantiationUtils.newInstanceOrNull(InstantiationUtilsTest.ClassWithTwoConstructors.class, "foo");
    }

    @Test
    public void newInstanceOrNull_primitiveArrayArgInConstructor() {
        InstantiationUtilsTest.ClassWithPrimitiveArrayInConstructor instance = InstantiationUtils.newInstanceOrNull(InstantiationUtilsTest.ClassWithPrimitiveArrayInConstructor.class, "foo", new int[]{ 42, 42 });
        Assert.assertNotNull(instance);
    }

    public static class ClassWithNonArgConstructor {}

    public static class ClassWithStringConstructor {
        public ClassWithStringConstructor(String ignored) {
        }
    }

    public static class ClassWithObjectConstructor {
        public ClassWithObjectConstructor(Object ignored) {
        }
    }

    public static class ClassWithTwoArgConstructorConstructor {
        public ClassWithTwoArgConstructorConstructor(String arg0, String arg1) {
        }
    }

    public static class ClassWithTwoConstructorsIncludingPrimitives {
        public ClassWithTwoConstructorsIncludingPrimitives(int arg0, int arg1) {
        }

        public ClassWithTwoConstructorsIncludingPrimitives(int arg0, Object arg1) {
        }
    }

    public static class ClassWithPrimitiveArgConstructor {
        public ClassWithPrimitiveArgConstructor(boolean arg0, byte arg1, char arg2, double arg3, float arg4, int arg5, long arg6, short arg7) {
        }
    }

    public static class ClassWithTwoConstructors {
        public ClassWithTwoConstructors(String ignored) {
        }

        public ClassWithTwoConstructors(Object ignored) {
        }
    }

    public static class ClassWithPrimitiveArrayInConstructor {
        public ClassWithPrimitiveArrayInConstructor(String ignored, int[] a) {
        }
    }
}

