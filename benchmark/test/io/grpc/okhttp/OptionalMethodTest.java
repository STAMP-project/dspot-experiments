/**
 * Copyright 2017 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.okhttp;


import io.grpc.okhttp.internal.OptionalMethod;
import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for OptionalMethod.
 */
@RunWith(JUnit4.class)
public class OptionalMethodTest {
    public static class DefaultClass {
        public String testMethod(String arg) {
            return arg;
        }
    }

    public abstract static class PublicParent {
        public abstract String testMethod(String arg);
    }

    private static class PrivateImpl extends OptionalMethodTest.PublicParent {
        @Override
        public String testMethod(String arg) {
            return arg;
        }
    }

    private static class PrivateClass {
        public String testMethod(String arg) {
            return arg;
        }
    }

    @Test
    public void isSupported() {
        OptionalMethod<OptionalMethodTest.DefaultClass> defaultClassMethod = new OptionalMethod(String.class, "testMethod", String.class);
        Assert.assertTrue(defaultClassMethod.isSupported(new OptionalMethodTest.DefaultClass()));
        OptionalMethod<OptionalMethodTest.PublicParent> privateImpl = new OptionalMethod(String.class, "testMethod", String.class);
        Assert.assertTrue(privateImpl.isSupported(new OptionalMethodTest.PrivateImpl()));
        OptionalMethod<OptionalMethodTest.PrivateClass> privateClass = new OptionalMethod(String.class, "testMethod", String.class);
        Assert.assertFalse(privateClass.isSupported(new OptionalMethodTest.PrivateClass()));
    }

    @Test
    public void invokeOptional() throws InvocationTargetException {
        OptionalMethod<OptionalMethodTest.DefaultClass> defaultClassMethod = new OptionalMethod(String.class, "testMethod", String.class);
        Assert.assertEquals("testArg", defaultClassMethod.invokeOptional(new OptionalMethodTest.DefaultClass(), "testArg"));
        OptionalMethod<OptionalMethodTest.PublicParent> privateImpl = new OptionalMethod(String.class, "testMethod", String.class);
        Assert.assertEquals("testArg", privateImpl.invokeOptional(new OptionalMethodTest.PrivateImpl(), "testArg"));
        OptionalMethod<OptionalMethodTest.PrivateClass> privateClass = new OptionalMethod(String.class, "testMethod", String.class);
        Assert.assertEquals(null, privateClass.invokeOptional(new OptionalMethodTest.PrivateClass(), "testArg"));
    }
}

