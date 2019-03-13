package com.pushtorefresh.storio3.impl;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.Interceptor.Chain;
import com.pushtorefresh.storio3.operations.PreparedOperation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ChainImplTest {
    @Test
    public void proceed_shouldThrowIfIteratorEmpty() {
        try {
            final List<Interceptor> empty = Collections.emptyList();
            final Chain chain = new ChainImpl(empty.listIterator());
            chain.proceed(Mockito.mock(PreparedOperation.class));
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("proceed was called on empty iterator");
        }
    }

    @Test
    public void proceed_shouldThrowIfCalledMultipleTimes() {
        final List<Interceptor> interceptors = Arrays.asList(Mockito.mock(Interceptor.class), Mockito.mock(Interceptor.class));
        try {
            final Chain chain = new ChainImpl(interceptors.listIterator());
            final PreparedOperation operation = Mockito.mock(PreparedOperation.class);
            chain.proceed(operation);
            chain.proceed(operation);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage((("nextInterceptor " + (interceptors.get(0))) + " must call proceed() exactly once"));
        }
    }

    @Test
    public void buildChain_shouldThrowIfRegisteredInterceptorNull() {
        try {
            final List<Interceptor> interceptors = Arrays.asList(null, Mockito.mock(Interceptor.class));
            final Chain chain = ChainImpl.buildChain(interceptors, Mockito.mock(Interceptor.class));
            chain.proceed(Mockito.mock(PreparedOperation.class));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Interceptor should not be null");
        }
    }

    @Test
    public void buildChain_shouldThrowIfRealInterceptorNull() {
        try {
            final List<Interceptor> interceptors = Collections.singletonList(Mockito.mock(Interceptor.class));
            // noinspection ConstantConditions
            final Chain chain = ChainImpl.buildChain(interceptors, null);
            chain.proceed(Mockito.mock(PreparedOperation.class));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Interceptor should not be null");
        }
    }

    @Test
    public void buildChain_placesRealInterceptorAfterRegistered() {
        Interceptor registered1 = Mockito.spy(new ChainImplTest.IntermediateInterceptor());
        Interceptor registered2 = Mockito.spy(new ChainImplTest.IntermediateInterceptor());
        final List<Interceptor> interceptors = Arrays.asList(registered1, registered2);
        Interceptor real = Mockito.mock(Interceptor.class);
        final Chain chain = ChainImpl.buildChain(interceptors, real);
        InOrder inOrder = Mockito.inOrder(registered1, registered2, real);
        PreparedOperation operation = Mockito.mock(PreparedOperation.class);
        chain.proceed(operation);
        inOrder.verify(registered1).intercept(ArgumentMatchers.eq(operation), ArgumentMatchers.any(Chain.class));
        inOrder.verify(registered2).intercept(ArgumentMatchers.eq(operation), ArgumentMatchers.any(Chain.class));
        inOrder.verify(real).intercept(ArgumentMatchers.eq(operation), ArgumentMatchers.any(Chain.class));
    }

    private static class IntermediateInterceptor implements Interceptor {
        @Override
        @Nullable
        public <Result, WrappedResult, Data> Result intercept(@NonNull
        PreparedOperation<Result, WrappedResult, Data> operation, @NonNull
        Chain chain) {
            return chain.proceed(operation);
        }
    }
}

