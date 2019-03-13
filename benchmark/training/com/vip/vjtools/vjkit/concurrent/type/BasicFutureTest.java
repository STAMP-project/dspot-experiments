package com.vip.vjtools.vjkit.concurrent.type;


import com.vip.vjtools.vjkit.base.ExceptionUtil;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Test;


public class BasicFutureTest {
    public static class MyFuture<T> extends BasicFuture<T> {
        @Override
        protected void onCompleted(T result) {
            System.out.println(("onCompleted:" + result));
        }

        @Override
        protected void onFailed(Exception ex) {
            System.out.println(("onFailed:" + (ex.getMessage())));
        }

        @Override
        protected void onCancelled() {
            System.out.println("onCancelled");
        }
    }

    private static class Tasks {
        public static void success(BasicFutureTest.MyFuture<String> future) {
            future.completed("haha");
        }

        public static void fail(BasicFutureTest.MyFuture<String> future) {
            future.failed(new RuntimeException("wuwu"));
        }

        public static void cancel(BasicFutureTest.MyFuture<String> future) {
            future.cancel(true);
        }
    }

    @Test
    public void test() throws InterruptedException, ExecutionException {
        BasicFutureTest.MyFuture<String> future = new BasicFutureTest.MyFuture<String>();
        BasicFutureTest.Tasks.success(future);
        String result = future.get();
        assertThat(result).isEqualTo("haha");
        // ???????
        try {
            BasicFutureTest.MyFuture<String> future2 = new BasicFutureTest.MyFuture<String>();
            future2.get(10, TimeUnit.MILLISECONDS);
            fail("should fail before");
        } catch (TimeoutException e) {
            assertThat(e).isInstanceOf(TimeoutException.class);
        }
        // ??
        try {
            BasicFutureTest.MyFuture<String> future3 = new BasicFutureTest.MyFuture<String>();
            BasicFutureTest.Tasks.fail(future3);
            future3.get();
            fail("should fail before");
        } catch (Throwable t) {
            assertThat(ExceptionUtil.unwrap(t)).hasMessage("wuwu");
        }
        // ??
        BasicFutureTest.MyFuture<String> future4 = new BasicFutureTest.MyFuture<String>();
        BasicFutureTest.Tasks.cancel(future4);
        assertThat(future4.isCancelled()).isTrue();
        try {
            String result4 = future4.get();
            fail("should fail here");
        } catch (CancellationException cae) {
        }
    }
}

