/**
 * The MIT License
 *
 * Copyright (c) 2015, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.util;


import hudson.Util;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link Util.isOverridden} method.
 */
public class IsOverriddenTest {
    /**
     * Test that a method is found by isOverridden even when it is inherited from an intermediate class.
     */
    @Test
    public void isOverriddenTest() {
        Assert.assertTrue(Util.isOverridden(IsOverriddenTest.Base.class, IsOverriddenTest.Derived.class, "method"));
        Assert.assertTrue(Util.isOverridden(IsOverriddenTest.Base.class, IsOverriddenTest.Intermediate.class, "method"));
        Assert.assertFalse(Util.isOverridden(IsOverriddenTest.Base.class, IsOverriddenTest.Base.class, "method"));
        Assert.assertTrue(Util.isOverridden(IsOverriddenTest.Base.class, IsOverriddenTest.Intermediate.class, "setX", Object.class));
        Assert.assertTrue(Util.isOverridden(IsOverriddenTest.Base.class, IsOverriddenTest.Intermediate.class, "getX"));
    }

    /**
     * Negative test.
     * Trying to check for a method which does not exist in the hierarchy,
     */
    @Test(expected = IllegalArgumentException.class)
    public void isOverriddenNegativeTest() {
        Util.isOverridden(IsOverriddenTest.Base.class, IsOverriddenTest.Derived.class, "method2");
    }

    /**
     * Do not inspect private methods.
     */
    @Test(expected = IllegalArgumentException.class)
    public void avoidPrivateMethodsInspection() {
        Util.isOverridden(IsOverriddenTest.Base.class, IsOverriddenTest.Intermediate.class, "aPrivateMethod");
    }

    public abstract class Base<T> {
        protected abstract void method();

        private void aPrivateMethod() {
        }

        public void setX(T t) {
        }

        public T getX() {
            return null;
        }
    }

    public abstract class Intermediate extends IsOverriddenTest.Base<Integer> {
        protected void method() {
        }

        private void aPrivateMethod() {
        }

        public void setX(Integer i) {
        }

        public Integer getX() {
            return 0;
        }
    }

    public class Derived extends IsOverriddenTest.Intermediate {}
}

