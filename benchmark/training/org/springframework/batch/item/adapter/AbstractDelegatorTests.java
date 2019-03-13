/**
 * Copyright 2008-2014 the original author or authors.
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


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.adapter.AbstractMethodInvokingDelegator.InvocationTargetThrowableWrapper;


/**
 * Tests for {@link AbstractMethodInvokingDelegator}
 *
 * @author Robert Kasanicky
 */
public class AbstractDelegatorTests {
    private static class ConcreteDelegator extends AbstractMethodInvokingDelegator<AbstractDelegatorTests.Foo> {}

    private AbstractMethodInvokingDelegator<AbstractDelegatorTests.Foo> delegator = new AbstractDelegatorTests.ConcreteDelegator();

    private AbstractDelegatorTests.Foo foo = new AbstractDelegatorTests.Foo("foo", 1);

    /**
     * Regular use - calling methods directly and via delegator leads to same
     * results
     */
    @Test
    public void testDelegation() throws Exception {
        delegator.setTargetMethod("getName");
        delegator.afterPropertiesSet();
        Assert.assertEquals(foo.getName(), delegator.invokeDelegateMethod());
    }

    /**
     * Regular use - calling methods directly and via delegator leads to same
     * results
     */
    @Test
    public void testDelegationWithArgument() throws Exception {
        delegator.setTargetMethod("setName");
        final String NEW_FOO_NAME = "newFooName";
        delegator.afterPropertiesSet();
        delegator.invokeDelegateMethodWithArgument(NEW_FOO_NAME);
        Assert.assertEquals(NEW_FOO_NAME, foo.getName());
        // using the arguments setter should work equally well
        foo.setName("foo");
        Assert.assertTrue((!(foo.getName().equals(NEW_FOO_NAME))));
        delegator.setArguments(new Object[]{ NEW_FOO_NAME });
        delegator.afterPropertiesSet();
        delegator.invokeDelegateMethod();
        Assert.assertEquals(NEW_FOO_NAME, foo.getName());
    }

    /**
     * Null argument value doesn't cause trouble when validating method
     * signature.
     */
    @Test
    public void testDelegationWithCheckedNullArgument() throws Exception {
        delegator.setTargetMethod("setName");
        delegator.setArguments(new Object[]{ null });
        delegator.afterPropertiesSet();
        delegator.invokeDelegateMethod();
        Assert.assertNull(foo.getName());
    }

    /**
     * Exception scenario - target method is not declared by target object.
     */
    @Test
    public void testInvalidMethodName() throws Exception {
        delegator.setTargetMethod("not-existing-method-name");
        try {
            delegator.afterPropertiesSet();
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
        try {
            delegator.invokeDelegateMethod();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Exception scenario - target method is called with invalid arguments.
     */
    @Test
    public void testInvalidArgumentsForExistingMethod() throws Exception {
        delegator.setTargetMethod("setName");
        delegator.afterPropertiesSet();
        try {
            delegator.invokeDelegateMethodWithArgument(new Object());
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Exception scenario - target method is called with incorrect number of
     * arguments.
     */
    @Test
    public void testTooFewArguments() throws Exception {
        delegator.setTargetMethod("setName");
        delegator.afterPropertiesSet();
        try {
            // single argument expected but none provided
            delegator.invokeDelegateMethod();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testTooManyArguments() throws Exception {
        delegator.setTargetMethod("setName");
        // single argument expected but two provided
        delegator.invokeDelegateMethodWithArguments(new Object[]{ "name", "anotherName" });
        Assert.assertEquals("name", foo.getName());
    }

    /**
     * Exception scenario - incorrect static arguments set.
     */
    @Test
    public void testIncorrectNumberOfStaticArguments() throws Exception {
        delegator.setTargetMethod("setName");
        // incorrect argument count
        delegator.setArguments(new Object[]{ "first", "second" });
        try {
            delegator.afterPropertiesSet();
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
        }
        // correct argument count, but invalid argument type
        delegator.setArguments(new Object[]{ new Object() });
        try {
            delegator.afterPropertiesSet();
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    /**
     * Exception scenario - target method is successfully invoked but throws
     * exception. Such 'business' exception should be re-thrown as is (without
     * wrapping).
     */
    @Test
    public void testDelegateException() throws Exception {
        delegator.setTargetMethod("fail");
        delegator.afterPropertiesSet();
        try {
            delegator.invokeDelegateMethod();
            Assert.fail();
        } catch (Exception expected) {
            Assert.assertEquals(AbstractDelegatorTests.Foo.FAILURE_MESSAGE, expected.getMessage());
        }
    }

    /**
     * Exception scenario - target method is successfully invoked but throws a
     * {@link Throwable} (not an {@link Exception}).
     */
    @Test
    public void testDelegateThrowable() throws Exception {
        delegator.setTargetMethod("failUgly");
        delegator.afterPropertiesSet();
        try {
            delegator.invokeDelegateMethod();
            Assert.fail();
        } catch (InvocationTargetThrowableWrapper expected) {
            Assert.assertEquals(AbstractDelegatorTests.Foo.UGLY_FAILURE_MESSAGE, expected.getCause().getMessage());
        }
    }

    @SuppressWarnings("unused")
    private static class Foo {
        public static final String FAILURE_MESSAGE = "Foo Failure!";

        public static final String UGLY_FAILURE_MESSAGE = "Ugly Foo Failure!";

        private String name;

        private int value;

        public Foo(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void fail() throws Exception {
            throw new Exception(AbstractDelegatorTests.Foo.FAILURE_MESSAGE);
        }

        public void failUgly() throws Throwable {
            throw new Throwable(AbstractDelegatorTests.Foo.UGLY_FAILURE_MESSAGE);
        }
    }

    private static class FooService {
        private List<AbstractDelegatorTests.Foo> processedFooNameValuePairs = new ArrayList<>();

        @SuppressWarnings("unused")
        public void processNameValuePair(String name, int value) {
            processedFooNameValuePairs.add(new AbstractDelegatorTests.Foo(name, value));
        }

        @SuppressWarnings("unused")
        public void processNameValuePair(String name, String value) {
            processedFooNameValuePairs.add(new AbstractDelegatorTests.Foo(name, new Integer(value)));
        }

        public List<AbstractDelegatorTests.Foo> getProcessedFooNameValuePairs() {
            return processedFooNameValuePairs;
        }
    }
}

