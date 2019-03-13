/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.util.LambdaSafe.Filter;
import org.springframework.boot.util.LambdaSafe.InvocationResult;


/**
 * Tests for {@link LambdaSafe}.
 *
 * @author Phillip Webb
 */
public class LambdaSafeTests {
    @Test
    public void callbackWhenCallbackTypeIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> LambdaSafe.callback(null, new Object(), null)).withMessageContaining("CallbackType must not be null");
    }

    @Test
    public void callbackWhenCallbackInstanceIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> LambdaSafe.callback(.class, null, null)).withMessageContaining("CallbackInstance must not be null");
    }

    @Test
    public void callbackInvokeWhenNoGenericShouldInvokeCallback() {
        LambdaSafeTests.NonGenericCallback callbackInstance = Mockito.mock(LambdaSafeTests.NonGenericCallback.class);
        String argument = "foo";
        LambdaSafe.callback(LambdaSafeTests.NonGenericCallback.class, callbackInstance, argument).invoke(( c) -> c.handle(argument));
        Mockito.verify(callbackInstance).handle(argument);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbackInvokeWhenHasGenericShouldInvokeCallback() {
        LambdaSafeTests.StringCallback callbackInstance = Mockito.mock(LambdaSafeTests.StringCallback.class);
        String argument = "foo";
        LambdaSafe.callback(LambdaSafeTests.GenericCallback.class, callbackInstance, argument).invoke(( c) -> c.handle(argument));
        Mockito.verify(callbackInstance).handle(argument);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbackInvokeWhenHasResolvableGenericMatchShouldInvokeCallback() {
        LambdaSafeTests.StringBuilderCallback callbackInstance = Mockito.mock(LambdaSafeTests.StringBuilderCallback.class);
        StringBuilder argument = new StringBuilder("foo");
        LambdaSafe.callback(LambdaSafeTests.GenericCallback.class, callbackInstance, argument).invoke(( c) -> c.handle(argument));
        Mockito.verify(callbackInstance).handle(argument);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbackInvokeWhenHasResolvableGenericNonMatchShouldNotInvokeCallback() {
        LambdaSafeTests.GenericCallback<?> callbackInstance = Mockito.mock(LambdaSafeTests.StringBuilderCallback.class);
        String argument = "foo";
        LambdaSafe.callback(LambdaSafeTests.GenericCallback.class, callbackInstance, argument).invoke(( c) -> c.handle(argument));
        Mockito.verifyZeroInteractions(callbackInstance);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbackInvokeWhenLambdaMismatchShouldSwallowException() {
        LambdaSafeTests.GenericCallback<StringBuilder> callbackInstance = ( s) -> Assert.fail("Should not get here");
        String argument = "foo";
        LambdaSafe.callback(LambdaSafeTests.GenericCallback.class, callbackInstance, argument).invoke(( c) -> c.handle(argument));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbackInvokeWhenLambdaMismatchOnDifferentArgumentShouldSwallowException() {
        LambdaSafeTests.GenericMultiArgCallback<StringBuilder> callbackInstance = ( n, s, b) -> Assert.fail("Should not get here");
        String argument = "foo";
        LambdaSafe.callback(LambdaSafeTests.GenericMultiArgCallback.class, callbackInstance, argument).invoke(( c) -> c.handle(1, argument, false));
    }

    @Test
    public void callbackInvokeAndWhenNoGenericShouldReturnResult() {
        LambdaSafeTests.NonGenericFactory callbackInstance = Mockito.mock(LambdaSafeTests.NonGenericFactory.class);
        String argument = "foo";
        BDDMockito.given(callbackInstance.handle("foo")).willReturn(123);
        InvocationResult<Integer> result = LambdaSafe.callback(LambdaSafeTests.NonGenericFactory.class, callbackInstance, argument).invokeAnd(( c) -> c.handle(argument));
        assertThat(result.hasResult()).isTrue();
        assertThat(result.get()).isEqualTo(123);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbackInvokeAndWhenHasGenericShouldReturnResult() {
        LambdaSafeTests.StringFactory callbackInstance = Mockito.mock(LambdaSafeTests.StringFactory.class);
        String argument = "foo";
        BDDMockito.given(callbackInstance.handle("foo")).willReturn(123);
        InvocationResult<Integer> result = LambdaSafe.callback(LambdaSafeTests.GenericFactory.class, callbackInstance, argument).invokeAnd(( c) -> c.handle(argument));
        assertThat(result.hasResult()).isTrue();
        assertThat(result.get()).isEqualTo(123);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbackInvokeAndWhenReturnNullShouldReturnResult() {
        LambdaSafeTests.StringFactory callbackInstance = Mockito.mock(LambdaSafeTests.StringFactory.class);
        String argument = "foo";
        BDDMockito.given(callbackInstance.handle("foo")).willReturn(null);
        InvocationResult<Integer> result = LambdaSafe.callback(LambdaSafeTests.GenericFactory.class, callbackInstance, argument).invokeAnd(( c) -> c.handle(argument));
        assertThat(result.hasResult()).isTrue();
        assertThat(result.get()).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbackInvokeAndWhenHasResolvableGenericMatchShouldReturnResult() {
        LambdaSafeTests.StringBuilderFactory callbackInstance = Mockito.mock(LambdaSafeTests.StringBuilderFactory.class);
        StringBuilder argument = new StringBuilder("foo");
        BDDMockito.given(callbackInstance.handle(ArgumentMatchers.any(StringBuilder.class))).willReturn(123);
        InvocationResult<Integer> result = LambdaSafe.callback(LambdaSafeTests.GenericFactory.class, callbackInstance, argument).invokeAnd(( c) -> c.handle(argument));
        Mockito.verify(callbackInstance).handle(argument);
        assertThat(result.hasResult()).isTrue();
        assertThat(result.get()).isEqualTo(123);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbackInvokeAndWhenHasResolvableGenericNonMatchShouldReturnNoResult() {
        LambdaSafeTests.GenericFactory<?> callbackInstance = Mockito.mock(LambdaSafeTests.StringBuilderFactory.class);
        String argument = "foo";
        InvocationResult<Integer> result = LambdaSafe.callback(LambdaSafeTests.GenericFactory.class, callbackInstance, argument).invokeAnd(( c) -> c.handle(argument));
        assertThat(result.hasResult()).isFalse();
        Mockito.verifyZeroInteractions(callbackInstance);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbackInvokeAndWhenLambdaMismatchShouldSwallowException() {
        LambdaSafeTests.GenericFactory<StringBuilder> callbackInstance = ( s) -> {
            Assert.fail("Should not get here");
            return 123;
        };
        String argument = "foo";
        InvocationResult<Integer> result = LambdaSafe.callback(LambdaSafeTests.GenericFactory.class, callbackInstance, argument).invokeAnd(( c) -> c.handle(argument));
        assertThat(result.hasResult()).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbackInvokeAndWhenLambdaMismatchOnDifferentArgumentShouldSwallowException() {
        LambdaSafeTests.GenericMultiArgFactory<StringBuilder> callbackInstance = ( n, s, b) -> {
            Assert.fail("Should not get here");
            return 123;
        };
        String argument = "foo";
        InvocationResult<Integer> result = LambdaSafe.callback(LambdaSafeTests.GenericMultiArgFactory.class, callbackInstance, argument).invokeAnd(( c) -> c.handle(1, argument, false));
        assertThat(result.hasResult()).isFalse();
    }

    @Test
    public void callbacksInvokeWhenNoGenericShouldInvokeCallbacks() {
        LambdaSafeTests.NonGenericCallback callbackInstance = Mockito.mock(LambdaSafeTests.NonGenericCallback.class);
        String argument = "foo";
        LambdaSafe.callbacks(LambdaSafeTests.NonGenericCallback.class, Collections.singleton(callbackInstance), argument).invoke(( c) -> c.handle(argument));
        Mockito.verify(callbackInstance).handle(argument);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbacksInvokeWhenHasGenericShouldInvokeCallback() {
        LambdaSafeTests.StringCallback callbackInstance = Mockito.mock(LambdaSafeTests.StringCallback.class);
        String argument = "foo";
        LambdaSafe.callbacks(LambdaSafeTests.GenericCallback.class, Collections.singleton(callbackInstance), argument).invoke(( c) -> c.handle(argument));
        Mockito.verify(callbackInstance).handle(argument);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbacksInvokeWhenHasResolvableGenericMatchShouldInvokeCallback() {
        LambdaSafeTests.StringBuilderCallback callbackInstance = Mockito.mock(LambdaSafeTests.StringBuilderCallback.class);
        StringBuilder argument = new StringBuilder("foo");
        LambdaSafe.callbacks(LambdaSafeTests.GenericCallback.class, Collections.singleton(callbackInstance), argument).invoke(( c) -> c.handle(argument));
        Mockito.verify(callbackInstance).handle(argument);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbacksInvokeWhenHasResolvableGenericNonMatchShouldNotInvokeCallback() {
        LambdaSafeTests.GenericCallback<?> callbackInstance = Mockito.mock(LambdaSafeTests.StringBuilderCallback.class);
        String argument = "foo";
        LambdaSafe.callbacks(LambdaSafeTests.GenericCallback.class, Collections.singleton(callbackInstance), argument).invoke(( c) -> c.handle(null));
        Mockito.verifyZeroInteractions(callbackInstance);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbacksInvokeWhenLambdaMismatchShouldSwallowException() {
        LambdaSafeTests.GenericCallback<StringBuilder> callbackInstance = ( s) -> Assert.fail("Should not get here");
        String argument = "foo";
        LambdaSafe.callbacks(LambdaSafeTests.GenericCallback.class, Collections.singleton(callbackInstance), argument).invoke(( c) -> c.handle(argument));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbacksInvokeWhenLambdaMismatchOnDifferentArgumentShouldSwallowException() {
        LambdaSafeTests.GenericMultiArgCallback<StringBuilder> callbackInstance = ( n, s, b) -> Assert.fail("Should not get here");
        String argument = "foo";
        LambdaSafe.callbacks(LambdaSafeTests.GenericMultiArgCallback.class, Collections.singleton(callbackInstance), argument).invoke(( c) -> c.handle(1, argument, false));
    }

    @Test
    public void callbacksInvokeAndWhenNoGenericShouldReturnResult() {
        LambdaSafeTests.NonGenericFactory callbackInstance = Mockito.mock(LambdaSafeTests.NonGenericFactory.class);
        String argument = "foo";
        BDDMockito.given(callbackInstance.handle("foo")).willReturn(123);
        Stream<Integer> result = LambdaSafe.callbacks(LambdaSafeTests.NonGenericFactory.class, Collections.singleton(callbackInstance), argument).invokeAnd(( c) -> c.handle(argument));
        assertThat(result).containsExactly(123);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbacksInvokeAndWhenHasGenericShouldReturnResult() {
        LambdaSafeTests.StringFactory callbackInstance = Mockito.mock(LambdaSafeTests.StringFactory.class);
        String argument = "foo";
        BDDMockito.given(callbackInstance.handle("foo")).willReturn(123);
        Stream<Integer> result = LambdaSafe.callbacks(LambdaSafeTests.GenericFactory.class, Collections.singleton(callbackInstance), argument).invokeAnd(( c) -> c.handle(argument));
        assertThat(result).containsExactly(123);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbacksInvokeAndWhenReturnNullShouldReturnResult() {
        LambdaSafeTests.StringFactory callbackInstance = Mockito.mock(LambdaSafeTests.StringFactory.class);
        String argument = "foo";
        BDDMockito.given(callbackInstance.handle("foo")).willReturn(null);
        Stream<Integer> result = LambdaSafe.callbacks(LambdaSafeTests.GenericFactory.class, Collections.singleton(callbackInstance), argument).invokeAnd(( c) -> c.handle(argument));
        assertThat(result).containsExactly(((Integer) (null)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbacksInvokeAndWhenHasResolvableGenericMatchShouldReturnResult() {
        LambdaSafeTests.StringBuilderFactory callbackInstance = Mockito.mock(LambdaSafeTests.StringBuilderFactory.class);
        StringBuilder argument = new StringBuilder("foo");
        BDDMockito.given(callbackInstance.handle(ArgumentMatchers.any(StringBuilder.class))).willReturn(123);
        Stream<Integer> result = LambdaSafe.callbacks(LambdaSafeTests.GenericFactory.class, Collections.singleton(callbackInstance), argument).invokeAnd(( c) -> c.handle(argument));
        assertThat(result).containsExactly(123);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbacksInvokeAndWhenHasResolvableGenericNonMatchShouldReturnNoResult() {
        LambdaSafeTests.GenericFactory<?> callbackInstance = Mockito.mock(LambdaSafeTests.StringBuilderFactory.class);
        String argument = "foo";
        Stream<Integer> result = LambdaSafe.callbacks(LambdaSafeTests.GenericFactory.class, Collections.singleton(callbackInstance), argument).invokeAnd(( c) -> c.handle(argument));
        assertThat(result).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbacksInvokeAndWhenLambdaMismatchShouldSwallowException() {
        LambdaSafeTests.GenericFactory<StringBuilder> callbackInstance = ( s) -> {
            Assert.fail("Should not get here");
            return 123;
        };
        String argument = "foo";
        Stream<Integer> result = LambdaSafe.callbacks(LambdaSafeTests.GenericFactory.class, Collections.singleton(callbackInstance), argument).invokeAnd(( c) -> c.handle(argument));
        assertThat(result).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbacksInvokeAndWhenLambdaMismatchOnDifferentArgumentShouldSwallowException() {
        LambdaSafeTests.GenericMultiArgFactory<StringBuilder> callbackInstance = ( n, s, b) -> {
            Assert.fail("Should not get here");
            return 123;
        };
        String argument = "foo";
        Stream<Integer> result = LambdaSafe.callbacks(LambdaSafeTests.GenericMultiArgFactory.class, Collections.singleton(callbackInstance), argument).invokeAnd(( c) -> c.handle(1, argument, false));
        assertThat(result).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbacksInvokeWhenMultipleShouldInvokeSuitable() {
        List<LambdaSafeTests.GenericFactory<?>> callbackInstances = new ArrayList<>();
        LambdaSafeTests.GenericFactory<String> callback1 = ( s) -> 1;
        LambdaSafeTests.GenericFactory<CharSequence> callback2 = ( s) -> 2;
        LambdaSafeTests.GenericFactory<StringBuilder> callback3 = ( s) -> 3;
        LambdaSafeTests.StringFactory callback4 = Mockito.mock(LambdaSafeTests.StringFactory.class);
        BDDMockito.given(callback4.handle(ArgumentMatchers.any(String.class))).willReturn(4);
        LambdaSafeTests.StringBuilderFactory callback5 = Mockito.mock(LambdaSafeTests.StringBuilderFactory.class);
        BDDMockito.given(callback5.handle(ArgumentMatchers.any(StringBuilder.class))).willReturn(5);
        callbackInstances.add(callback1);
        callbackInstances.add(callback2);
        callbackInstances.add(callback3);
        callbackInstances.add(callback4);
        callbackInstances.add(callback5);
        String argument = "foo";
        Stream<Integer> result = LambdaSafe.callbacks(LambdaSafeTests.GenericFactory.class, callbackInstances, argument).invokeAnd(( c) -> c.handle(argument));
        assertThat(result).containsExactly(1, 2, 4);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbackWithFilterShouldUseFilter() {
        LambdaSafeTests.GenericCallback<?> callbackInstance = Mockito.mock(LambdaSafeTests.StringBuilderCallback.class);
        String argument = "foo";
        LambdaSafe.callback(LambdaSafeTests.GenericCallback.class, callbackInstance, argument).withFilter(Filter.allowAll()).invoke(( c) -> c.handle(null));
        Mockito.verify(callbackInstance).handle(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void callbackWithLoggerShouldUseLogger() {
        Log logger = Mockito.mock(Log.class);
        BDDMockito.given(logger.isDebugEnabled()).willReturn(true);
        LambdaSafeTests.GenericCallback<StringBuilder> callbackInstance = ( s) -> Assert.fail("Should not get here");
        String argument = "foo";
        LambdaSafe.callback(LambdaSafeTests.GenericCallback.class, callbackInstance, argument).withLogger(logger).invoke(( c) -> c.handle(argument));
        Mockito.verify(logger).debug(ArgumentMatchers.contains(("Non-matching CharSequence type for callback " + "LambdaSafeTests.GenericCallback")), ArgumentMatchers.any(Throwable.class));
    }

    interface NonGenericCallback {
        void handle(String argument);
    }

    interface GenericCallback<T extends CharSequence> {
        void handle(T argument);
    }

    interface StringCallback extends LambdaSafeTests.GenericCallback<String> {}

    interface StringBuilderCallback extends LambdaSafeTests.GenericCallback<StringBuilder> {}

    interface GenericMultiArgCallback<T extends CharSequence> {
        void handle(Integer number, T argument, Boolean bool);
    }

    interface NonGenericFactory {
        Integer handle(String argument);
    }

    interface GenericFactory<T extends CharSequence> {
        Integer handle(T argument);
    }

    interface StringFactory extends LambdaSafeTests.GenericFactory<String> {}

    interface StringBuilderFactory extends LambdaSafeTests.GenericFactory<StringBuilder> {}

    interface GenericMultiArgFactory<T extends CharSequence> {
        Integer handle(Integer number, T argument, Boolean bool);
    }
}

