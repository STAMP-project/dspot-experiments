/**
 * Copyright (C) 2012-2019 the original author or authors.
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
package ninja.utils;


import Kind.ANONYMOUS_METHOD_REFERENCE;
import Kind.ANY_INSTANCE_METHOD_REFERENCE;
import Kind.SPECIFIC_INSTANCE_METHOD_REFERENCE;
import Kind.STATIC_METHOD_REFERENCE;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.nio.charset.StandardCharsets;
import ninja.Context;
import ninja.ControllerMethods.ControllerMethod1;
import ninja.Result;
import ninja.Results;
import ninja.utils.Lambdas.LambdaInfo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LambdasTest {
    @FunctionalInterface
    public static interface Function1<T, R> extends Serializable {
        R apply(T t);
    }

    @FunctionalInterface
    public static interface Function2<C, T, R> extends Serializable {
        R apply(C c, T t);
    }

    @Test
    public void staticMethodReference() throws Exception {
        LambdasTest.Function1<Long, String> lambda = LambdasTest::longToString;
        LambdaInfo lambdaInfo = Lambdas.reflect(lambda);
        Assert.assertThat(lambdaInfo.getKind(), CoreMatchers.is(STATIC_METHOD_REFERENCE));
        SerializedLambda serializedLambda = lambdaInfo.getSerializedLambda();
        Assert.assertThat(serializedLambda.getFunctionalInterfaceMethodName(), CoreMatchers.is("apply"));
        Assert.assertThat(serializedLambda.getImplClass().replace('/', '.'), CoreMatchers.is(LambdasTest.class.getCanonicalName()));
        Assert.assertThat(serializedLambda.getImplMethodName(), CoreMatchers.is("longToString"));
        Assert.assertThat(serializedLambda.getImplMethodKind(), CoreMatchers.is(6));// 6 = static method

        Assert.assertThat(serializedLambda.getCapturedArgCount(), CoreMatchers.is(0));
        // verify it can be dynamically invoked
        String value = ((String) (lambdaInfo.getImplementationMethod().invoke(null, 1L)));
        Assert.assertThat(value, CoreMatchers.is("1"));
    }

    public static class Calculator {
        private final Long initial;

        public Calculator(Long initial) {
            this.initial = initial;
        }

        public String l2s(Long value) {
            long calculated = (initial) + value;
            return Long.toString(calculated);
        }
    }

    @Test
    public void specificInstanceMethodReference() throws Exception {
        LambdasTest.Calculator calc = new LambdasTest.Calculator(1L);
        LambdasTest.Function1<Long, String> lambda = calc::l2s;
        LambdaInfo lambdaInfo = Lambdas.reflect(lambda);
        Assert.assertThat(lambdaInfo.getKind(), CoreMatchers.is(SPECIFIC_INSTANCE_METHOD_REFERENCE));
        SerializedLambda serializedLambda = lambdaInfo.getSerializedLambda();
        Assert.assertThat(serializedLambda.getFunctionalInterfaceMethodName(), CoreMatchers.is("apply"));
        Assert.assertThat(serializedLambda.getImplClass().replace('/', '.').replace('$', '.'), CoreMatchers.is(LambdasTest.Calculator.class.getCanonicalName()));
        Assert.assertThat(serializedLambda.getImplMethodName(), CoreMatchers.is("l2s"));
        Assert.assertThat(serializedLambda.getImplMethodSignature(), CoreMatchers.is("(Ljava/lang/Long;)Ljava/lang/String;"));
        Assert.assertThat(serializedLambda.getCapturedArgCount(), CoreMatchers.is(1));// captured "this"

        Assert.assertThat(serializedLambda.getCapturedArg(0), CoreMatchers.is(calc));// captured "this"

        // verify it can be dynamically invoked
        String value = ((String) (lambdaInfo.getFunctionalMethod().invoke(lambda, 1L)));
        // String value = (String)lambda.getClass().getMethod("apply", Long.class).invoke(calc, 1L);
        Assert.assertThat(value, CoreMatchers.is("2"));
    }

    @Test
    public void anyInstanceMethodReference() throws Exception {
        ControllerMethod1<LambdasTest> lambda = LambdasTest::home;
        LambdaInfo lambdaInfo = Lambdas.reflect(lambda);
        Assert.assertThat(lambdaInfo.getKind(), CoreMatchers.is(ANY_INSTANCE_METHOD_REFERENCE));
        SerializedLambda serializedLambda = lambdaInfo.getSerializedLambda();
        Assert.assertThat(serializedLambda.getFunctionalInterfaceMethodName(), CoreMatchers.is("apply"));
        Assert.assertThat(serializedLambda.getImplClass().replace('/', '.'), CoreMatchers.is(LambdasTest.class.getCanonicalName()));
        Assert.assertThat(serializedLambda.getImplMethodName(), CoreMatchers.is("home"));
        Assert.assertThat(serializedLambda.getImplMethodSignature(), CoreMatchers.is("()Lninja/Result;"));
        Assert.assertThat(serializedLambda.getCapturedArgCount(), CoreMatchers.is(0));
    }

    @Test
    public void anonymousClassReference() throws Exception {
        @SuppressWarnings("Convert2Lambda")
        ControllerMethod1<Context> lambda = new ControllerMethod1<Context>() {
            @Override
            public Result apply(Context a) {
                return Results.html().renderRaw("".getBytes(StandardCharsets.UTF_8));
            }
        };
        try {
            LambdaInfo lambdaInfo = Lambdas.reflect(lambda);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void anonymousMethodReference() throws Exception {
        ControllerMethod1<Context> lambda = (Context context) -> Results.html().renderRaw("".getBytes(StandardCharsets.UTF_8));
        LambdaInfo lambdaInfo = Lambdas.reflect(lambda);
        Assert.assertThat(lambdaInfo.getKind(), CoreMatchers.is(ANONYMOUS_METHOD_REFERENCE));
        SerializedLambda serializedLambda = lambdaInfo.getSerializedLambda();
        Assert.assertThat(serializedLambda.getFunctionalInterfaceMethodName(), CoreMatchers.is("apply"));
        Assert.assertThat(serializedLambda.getImplClass().replace('/', '.'), CoreMatchers.is(LambdasTest.class.getCanonicalName()));
        Assert.assertThat(serializedLambda.getImplMethodName(), CoreMatchers.startsWith("lambda$"));
        Assert.assertThat(serializedLambda.getInstantiatedMethodType(), CoreMatchers.is("(Lninja/Context;)Lninja/Result;"));
        // includes captured args btw...
        Assert.assertThat(serializedLambda.getImplMethodSignature(), CoreMatchers.is("(Lninja/Context;)Lninja/Result;"));
        Assert.assertThat(serializedLambda.getImplMethodKind(), CoreMatchers.is(6));// 6 = REF_invokeStatic

        Assert.assertThat(serializedLambda.getCapturedArgCount(), CoreMatchers.is(0));
    }
}

