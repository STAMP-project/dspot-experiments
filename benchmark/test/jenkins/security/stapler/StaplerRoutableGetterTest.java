/**
 * The MIT License
 *
 * Copyright (c) 2018, CloudBees, Inc.
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
package jenkins.security.stapler;


import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.TestExtension;


@Issue("SECURITY-400")
@For({ StaplerDispatchable.class, StaplerNotDispatchable.class, TypedFilter.class })
public class StaplerRoutableGetterTest extends StaplerAbstractTest {
    @TestExtension
    public static class TestRootAction extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        @Override
        public String getUrlName() {
            return "test";
        }

        public Object getFalseWithoutAnnotation() {
            return new StaplerAbstractTest.Renderable();
        }

        @StaplerDispatchable
        public Object getFalseWithAnnotation() {
            return new StaplerAbstractTest.Renderable();
        }

        public StaplerAbstractTest.Renderable getTrueWithoutAnnotation() {
            return new StaplerAbstractTest.Renderable();
        }

        @StaplerNotDispatchable
        public StaplerAbstractTest.Renderable getTrueWithAnnotation() {
            return new StaplerAbstractTest.Renderable();
        }

        @StaplerDispatchable
        @StaplerNotDispatchable
        public StaplerAbstractTest.Renderable getPriorityToNegative() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void testForceGetterMethod() throws Exception {
        assertNotReachable("test/falseWithoutAnnotation/");
        assertNotReachable("test/falseWithoutAnnotation/valid/");
        StaplerAbstractTest.filteredGetMethodTriggered = false;
        assertReachable("test/falseWithAnnotation/");
        assertReachable("test/falseWithAnnotation/valid/");
    }

    @Test
    public void testForceNotGetterMethod() throws Exception {
        assertReachable("test/trueWithoutAnnotation/");
        assertReachable("test/trueWithoutAnnotation/valid/");
        assertNotReachable("test/trueWithAnnotation/");
        assertNotReachable("test/trueWithAnnotation/valid/");
    }

    @Test
    public void testPriorityIsNegative() throws Exception {
        assertNotReachable("test/priorityToNegative/");
    }

    public static class TestRootActionParent extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        @StaplerNotDispatchable
        public StaplerAbstractTest.Renderable getParentKoButChildOk() {
            return new StaplerAbstractTest.Renderable();
        }

        @StaplerNotDispatchable
        public StaplerAbstractTest.Renderable getParentKoButChildNone() {
            return new StaplerAbstractTest.Renderable();
        }

        public StaplerAbstractTest.Renderable getParentNoneButChildOk() {
            return new StaplerAbstractTest.Renderable();
        }

        public StaplerAbstractTest.Renderable getParentNoneButChildKo() {
            return new StaplerAbstractTest.Renderable();
        }

        @StaplerDispatchable
        public StaplerAbstractTest.Renderable getParentOkButChildKo() {
            return new StaplerAbstractTest.Renderable();
        }

        @StaplerDispatchable
        public StaplerAbstractTest.Renderable getParentOkButChildNone() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @TestExtension
    public static class TestRootActionChild extends StaplerRoutableGetterTest.TestRootActionParent {
        @Override
        public String getUrlName() {
            return "test-child";
        }

        @StaplerDispatchable
        public StaplerAbstractTest.Renderable getParentKoButChildOk() {
            return new StaplerAbstractTest.Renderable();
        }

        public StaplerAbstractTest.Renderable getParentKoButChildNone() {
            return new StaplerAbstractTest.Renderable();
        }

        @StaplerDispatchable
        public StaplerAbstractTest.Renderable getParentNoneButChildOk() {
            return new StaplerAbstractTest.Renderable();
        }

        @StaplerNotDispatchable
        public StaplerAbstractTest.Renderable getParentNoneButChildKo() {
            return new StaplerAbstractTest.Renderable();
        }

        @StaplerNotDispatchable
        public StaplerAbstractTest.Renderable getParentOkButChildKo() {
            return new StaplerAbstractTest.Renderable();
        }

        public StaplerAbstractTest.Renderable getParentOkButChildNone() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void testInheritanceOfAnnotation_childHasLastWord() throws Exception {
        assertNotReachable("test-child/parentKoButChildOk/");
        assertNotReachable("test-child/parentKoButChildNone/");
        StaplerAbstractTest.filteredGetMethodTriggered = false;
        assertReachable("test-child/parentNoneButChildOk/");
        assertNotReachable("test-child/parentNoneButChildKo/");
        assertNotReachable("test-child/parentOkButChildKo/");
        StaplerAbstractTest.filteredGetMethodTriggered = false;
        assertReachable("test-child/parentOkButChildNone/");
    }
}

