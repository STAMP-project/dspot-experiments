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


import javax.annotation.CheckForNull;
import org.junit.Test;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.StaplerProxy;


public class PreventRoutingTest extends StaplerAbstractTest {
    @TestExtension
    public static class TargetNull extends StaplerAbstractTest.AbstractUnprotectedRootAction implements StaplerProxy {
        @Override
        @CheckForNull
        public String getUrlName() {
            return "target-null";
        }

        @Override
        public Object getTarget() {
            // in case of null, it's "this" that is considered
            return null;
        }

        public StaplerAbstractTest.Renderable getLegitRoutable() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @TestExtension
    public static class TargetNewObject extends StaplerAbstractTest.AbstractUnprotectedRootAction implements StaplerProxy {
        @Override
        @CheckForNull
        public String getUrlName() {
            return "target-new-object";
        }

        @Override
        public Object getTarget() {
            // Object is not routable
            return new Object();
        }

        public StaplerAbstractTest.Renderable getLegitRoutable() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void getTargetNewObject_isNotRoutable() throws Exception {
        assertNotReachable("target-new-object/legitRoutable");
    }

    @TestExtension
    public static class NotARequest extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        @Override
        @CheckForNull
        public String getUrlName() {
            return "not-a-request";
        }

        public StaplerAbstractTest.Renderable getLegitRoutable() {
            PreventRoutingTest.notStaplerGetter(this);
            return new StaplerAbstractTest.Renderable();
        }

        // just to validate it's ok
        public StaplerAbstractTest.Renderable getLegitRoutable2() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void regularGetter_notARequest() throws Exception {
        assertReachable("not-a-request/legitRoutable2");
        assertNotReachable("not-a-request/legitRoutable");
    }
}

