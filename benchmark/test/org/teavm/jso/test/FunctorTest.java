/**
 * Copyright 2016 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.jso.test;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
@SkipJVM
public class FunctorTest {
    @Test
    public void functorPassed() {
        Assert.assertEquals("(5)", FunctorTest.testMethod(( a, b) -> a + b, 2, 3));
    }

    @Test
    public void functorParamsMarshaled() {
        Assert.assertEquals("(q,w)", FunctorTest.testMethod(( a, b) -> (a + ",") + b, "q", "w"));
    }

    @Test
    public void functorIdentityPreserved() {
        FunctorTest.JSBiFunction javaFunction = ( a, b) -> a + b;
        JSObject firstRef = FunctorTest.getFunction(javaFunction);
        JSObject secondRef = FunctorTest.getFunction(javaFunction);
        Assert.assertSame(firstRef, secondRef);
    }

    @Test
    public void functorWithDefaultMethodPassed() {
        Assert.assertEquals(123, FunctorTest.callFunctionWithDefaultMethod(( s) -> s + 100));
    }

    @Test
    public void functorWithStaticMethodPassed() {
        Assert.assertEquals(123, FunctorTest.callFunctionWithStaticMethod(( s) -> s + 100));
    }

    @Test
    public void propertyWithNonAlphabeticFirstChar() {
        FunctorTest.WithProperties wp = FunctorTest.getWithPropertiesInstance();
        Assert.assertEquals("foo_ok", wp.get_foo());
        Assert.assertEquals("bar_ok", wp.get$bar());
        Assert.assertEquals("baz_ok", wp.propbaz());
    }

    @Test
    public void functorPassedBack() {
        FunctorTest.JSBiFunction function = FunctorTest.getBiFunction();
        Assert.assertEquals(23042, function.foo(23, 42));
    }

    @Test
    public void functorParamsMarshaledBack() {
        FunctorTest.JSStringBiFunction function = FunctorTest.getStringBiFunction();
        Assert.assertEquals("q,w", function.foo("q", "w"));
    }

    @Test
    public void castToFunctor() {
        FunctorTest.JSBiFunction f = FunctorTest.getBiFunctionAsObject().cast();
        Assert.assertEquals(23042, f.foo(23, 42));
    }

    @JSFunctor
    interface JSBiFunction extends JSObject {
        int foo(int a, int b);
    }

    @JSFunctor
    interface JSStringBiFunction extends JSObject {
        String foo(String a, String b);
    }

    @JSFunctor
    interface JSFunctionWithDefaultMethod extends JSObject {
        int foo(int a);

        default String defaultMethod() {
            return "Content";
        }
    }

    @JSFunctor
    interface JSFunctionWithStaticMethod extends JSObject {
        int foo(int a);

        static String staticMethod() {
            return "Content";
        }
    }

    interface WithProperties extends JSObject {
        @JSProperty
        String get_foo();

        @JSProperty
        String get$bar();

        @JSProperty("baz")
        String propbaz();
    }
}

