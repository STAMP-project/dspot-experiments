/**
 * Copyright 2016 Red Hat, Inc.
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
package org.jboss.as.service;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


public class ReflectionUtilsTest {
    public static class Foo {
        public int getA() {
            return 0;
        }

        public boolean isB() {
            return false;
        }

        public int getC(int c) {
            return c;
        }
    }

    @Test
    public void findNonBooleanGetter() throws Exception {
        final Method getter = ReflectionUtils.getGetter(ReflectionUtilsTest.Foo.class, "a");
        Assert.assertNotNull(getter);
        Assert.assertEquals("getA", getter.getName());
    }

    @Test
    public void findBooleanGetter() throws Exception {
        final Method getter = ReflectionUtils.getGetter(ReflectionUtilsTest.Foo.class, "b");
        Assert.assertNotNull(getter);
        Assert.assertEquals("isB", getter.getName());
    }

    @Test(expected = IllegalStateException.class)
    public void doNotFindGetterWithArgument() throws Exception {
        ReflectionUtils.getGetter(ReflectionUtilsTest.Foo.class, "c");
        Assert.fail("Should have thrown exception - getC is not a getter");
    }
}

