package com.pushtorefresh.storio3;


import Interceptor.Chain;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.pushtorefresh.storio3.operations.PreparedOperation;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;


@RunWith(RobolectricTestRunner.class)
public class LoggingInterceptorTest {
    // @Before
    @SuppressWarnings("NullableProblems")
    @NonNull
    private LoggingInterceptor loggingInterceptor;

    // @Before
    @SuppressWarnings("NullableProblems")
    @NonNull
    private StringBuilder resultBuilder;

    // @Before
    @SuppressWarnings("NullableProblems")
    @NonNull
    private static StringBuilder androidLogBuilder;

    @Test
    public void interceptShouldLogToLogger() {
        final String result = "some result";
        final String data = "some data";
        final Interceptor.Chain chain = new LoggingInterceptorTest.TestChain(result);
        final PreparedOperation operation = new LoggingInterceptorTest.TestOperation(data);
        loggingInterceptor.intercept(operation, chain);
        // TODO how to test timings?
        assertThat(resultBuilder.toString()).startsWith("TestOperation\n=> data: some data\n<= result: some result\ntook ");
    }

    @Config(shadows = { LoggingInterceptorTest.ShadowLog.class })
    @Test
    public void defaultLoggerShouldLogToAndroidLog() {
        loggingInterceptor = LoggingInterceptor.defaultLogger();
        final String result = "some result";
        final String data = "some data";
        final Interceptor.Chain chain = new LoggingInterceptorTest.TestChain(result);
        final PreparedOperation operation = new LoggingInterceptorTest.TestOperation(data);
        loggingInterceptor.intercept(operation, chain);
        // TODO how to test timings?
        assertThat(LoggingInterceptorTest.androidLogBuilder.toString()).startsWith("StorIO:TestOperation\n=> data: some data\n<= result: some result\ntook ");
    }

    @Implements(Log.class)
    public static class ShadowLog {
        @Implementation
        public static int d(@NonNull
        String tag, @NonNull
        String msg) {
            LoggingInterceptorTest.androidLogBuilder.append(tag).append(":").append(msg);
            return 0;
        }
    }

    private static class TestChain implements Interceptor.Chain {
        @NonNull
        private final String result;

        private TestChain(@NonNull
        String result) {
            this.result = result;
        }

        @Nullable
        @Override
        public <Result, WrappedResult, Data> Result proceed(@NonNull
        PreparedOperation<Result, WrappedResult, Data> operation) {
            // noinspection unchecked
            return ((Result) (result));
        }
    }

    private static class TestOperation implements PreparedOperation<String, String, String> {
        @NonNull
        private final String data;

        private TestOperation(@NonNull
        String data) {
            this.data = data;
        }

        @Nullable
        @Override
        public String executeAsBlocking() {
            throw new IllegalStateException("Not implemented yet");
        }

        @NonNull
        @Override
        public Flowable<String> asRxFlowable(@NonNull
        BackpressureStrategy backpressureStrategy) {
            throw new IllegalStateException("Not implemented yet");
        }

        @NonNull
        @Override
        public Single<String> asRxSingle() {
            throw new IllegalStateException("Not implemented yet");
        }

        @NonNull
        @Override
        public String getData() {
            return data;
        }
    }
}

