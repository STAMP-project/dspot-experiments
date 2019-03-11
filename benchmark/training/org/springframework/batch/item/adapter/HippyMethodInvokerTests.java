/**
 * Copyright 2010-2012 the original author or authors.
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
package org.springframework.batch.item.adapter;


import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;


public class HippyMethodInvokerTests {
    @Test
    public void testVanillaMethodInvoker() throws Exception {
        HippyMethodInvokerTests.TestMethodAdapter adapter = new HippyMethodInvokerTests.TestMethodAdapter();
        setTargetMethod("handle");
        setTargetObject(new HippyMethodInvokerTests.PlainPojo());
        Assert.assertEquals("2.0.foo", adapter.getMessage(2, "foo"));
    }

    @Test
    public void testEmptyParameters() throws Exception {
        HippyMethodInvokerTests.TestMethodAdapter adapter = new HippyMethodInvokerTests.TestMethodAdapter();
        setTargetMethod("empty");
        setTargetObject(new HippyMethodInvokerTests.PlainPojo());
        Assert.assertEquals(".", adapter.getMessage(2, "foo"));
    }

    @Test
    public void testEmptyParametersEmptyArgs() throws Exception {
        HippyMethodInvokerTests.TestMethodAdapter adapter = new HippyMethodInvokerTests.TestMethodAdapter();
        setTargetMethod("empty");
        setTargetObject(new HippyMethodInvokerTests.PlainPojo());
        Assert.assertEquals(".", adapter.getMessage());
    }

    @Test
    public void testMissingArgument() throws Exception {
        HippyMethodInvokerTests.TestMethodAdapter adapter = new HippyMethodInvokerTests.TestMethodAdapter();
        setTargetMethod("missing");
        setTargetObject(new HippyMethodInvokerTests.PlainPojo());
        Assert.assertEquals("foo.foo", adapter.getMessage(2, "foo"));
    }

    @Test
    public void testWrongOrder() throws Exception {
        HippyMethodInvokerTests.TestMethodAdapter adapter = new HippyMethodInvokerTests.TestMethodAdapter();
        setTargetMethod("disorder");
        setTargetObject(new HippyMethodInvokerTests.PlainPojo());
        Assert.assertEquals("2.0.foo", adapter.getMessage(2, "foo"));
    }

    @Test
    public void testTwoArgsOfSameTypeWithInexactMatch() throws Exception {
        HippyMethodInvoker invoker = new HippyMethodInvoker();
        invoker.setTargetMethod("duplicate");
        invoker.setTargetObject(new HippyMethodInvokerTests.PlainPojo());
        invoker.setArguments(new Object[]{ "2", "foo" });
        invoker.prepare();
        Assert.assertEquals("foo.2", invoker.invoke());
    }

    @Test
    public void testOverloadedMethodUsingInputWithoutExactMatch() throws Exception {
        HippyMethodInvoker invoker = new HippyMethodInvoker();
        invoker.setTargetMethod("foo");
        @SuppressWarnings("unused")
        class OverloadingPojo {
            public Class<?> foo(List<?> arg) {
                return List.class;
            }

            public Class<?> foo(Set<?> arg) {
                return Set.class;
            }
        }
        TreeSet<Object> arg = new TreeSet<>();
        OverloadingPojo target = new OverloadingPojo();
        Assert.assertEquals(target.foo(arg), Set.class);
        invoker.setTargetObject(target);
        invoker.setArguments(new Object[]{ arg });
        invoker.prepare();
        Assert.assertEquals(invoker.invoke(), Set.class);
    }

    public static class PlainPojo {
        public String handle(double value, String input) {
            return (value + ".") + input;
        }

        public String disorder(String input, double value) {
            return (value + ".") + input;
        }

        public String duplicate(String input, Object value) {
            return (value + ".") + input;
        }

        public String missing(String input) {
            return (input + ".") + input;
        }

        public String empty() {
            return ".";
        }
    }

    public static interface Service {
        String getMessage(double value, String input);
    }

    public static class TestMethodAdapter extends AbstractMethodInvokingDelegator<String> implements HippyMethodInvokerTests.Service {
        @Override
        public String getMessage(double value, String input) {
            try {
                return invokeDelegateMethodWithArguments(new Object[]{ value, input });
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        public String getMessage() {
            try {
                return invokeDelegateMethodWithArguments(new Object[0]);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}

