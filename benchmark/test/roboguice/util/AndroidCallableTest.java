package roboguice.util;


import Log.DEBUG;
import android.os.Looper;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;


@RunWith(RobolectricTestRunner.class)
public class AndroidCallableTest {
    @Test
    public void shouldCallMethodsUsingProperThreads() throws Exception {
        final Thread fgThread = Thread.currentThread();
        final Thread[] bgThread = new Thread[]{ null };
        final Thread[] answers = new Thread[5];
        final ShadowLooper looper = Robolectric.shadowOf(Looper.getMainLooper());
        Executors.newSingleThreadExecutor(new AndroidCallableTest.MyThreadFactory(bgThread)).submit(new AndroidCallableTest.StringAndroidCallable(answers, false));
        // Run all the pending tasks on the ui thread
        while ((answers[((answers.length) - 1)]) == null)
            looper.runToEndOfTasks();

        final Thread[] correctAnswer = new Thread[]{ fgThread, bgThread[0], null, fgThread, fgThread };
        Assert.assertThat(answers, CoreMatchers.equalTo(correctAnswer));
    }

    @Test
    public void shouldCallMethodsUsingProperThreadsWithException() throws Exception {
        final Thread fgThread = Thread.currentThread();
        final Thread[] bgThread = new Thread[]{ null };
        final Thread[] answers = new Thread[5];
        final ShadowLooper looper = Robolectric.shadowOf(Looper.getMainLooper());
        Executors.newSingleThreadExecutor(new AndroidCallableTest.MyThreadFactory(bgThread)).submit(new AndroidCallableTest.StringAndroidCallable(answers, true));
        // Run all the pending tasks on the ui thread
        while ((answers[((answers.length) - 1)]) == null)
            looper.runToEndOfTasks();

        final Thread[] correctAnswer = new Thread[]{ fgThread, bgThread[0], fgThread, null, fgThread };
        Assert.assertThat(answers, CoreMatchers.equalTo(correctAnswer));
    }

    @Test
    public void shouldHaveCorrectStackTrace() {
        final Exception[] exception = new Exception[]{ null };
        final StackTraceElement[] here;
        final StackTraceElement[][] there = new StackTraceElement[][]{ null };
        final ShadowLooper looper = Robolectric.shadowOf(Looper.getMainLooper());
        Ln.setLoggingLevel(DEBUG);
        try {
            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException e) {
            here = e.getStackTrace();
        }
        Executors.newSingleThreadExecutor(new AndroidCallableTest.MyThreadFactory(new Thread[]{ null })).submit(new AndroidCallable<String>() {
            @Override
            public String doInBackground() throws Exception {
                try {
                    throw new NullPointerException();
                } catch (NullPointerException e) {
                    there[0] = e.getStackTrace();
                    throw e;
                }
            }

            @Override
            public void onException(Exception e) {
                exception[0] = e;
            }

            @Override
            public void onSuccess(String result) {
            }
        }, false);
        // Run all the pending tasks on the ui thread
        while ((exception[0]) == null)
            looper.runToEndOfTasks();

        Assert.assertThat(exception[0].getStackTrace().length, CoreMatchers.equalTo((((here.length) + (there[0].length)) + 3)));
        int i = 0;
        while (i < (there[0].length)) {
            Assert.assertThat(exception[0].getStackTrace()[i], CoreMatchers.equalTo(there[0][i]));
            ++i;
        } 
        // skip 3 frames due to differences in where we got our stacktrace from
        for (int j = i + 3, k = 0; k < (here.length); ++k , ++j)
            Assert.assertThat(exception[0].getStackTrace()[j].getFileName(), CoreMatchers.equalTo(here[k].getFileName()));
        // line numbers may be off

    }

    @Test(expected = RuntimeException.class)
    public void shouldCrashAppForExceptionInOnSuccess() {
        final ShadowLooper looper = Robolectric.shadowOf(Looper.getMainLooper());
        final boolean[] callRecord = new boolean[]{ false };
        Executors.newSingleThreadExecutor().submit(new AndroidCallable<String>() {
            @Override
            public String doInBackground() throws Exception {
                return "";
            }

            @Override
            public void onSuccess(String result) {
                throw new RuntimeException();
            }

            @Override
            public void onException(Exception e) {
            }

            @Override
            public void onFinally() {
                callRecord[0] = true;
            }
        });
        // Run all the pending tasks on the ui thread
        while (!(callRecord[((callRecord.length) - 1)]))
            looper.runToEndOfTasks();

    }

    @Test(expected = RuntimeException.class)
    public void shouldCrashAppForExceptionInOnException() {
        final ShadowLooper looper = Robolectric.shadowOf(Looper.getMainLooper());
        final boolean[] callRecord = new boolean[]{ false };
        Executors.newSingleThreadExecutor().submit(new AndroidCallable<String>() {
            @Override
            public String doInBackground() throws Exception {
                throw new Exception();
            }

            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onException(Exception e) {
                throw new RuntimeException();
            }

            @Override
            public void onFinally() {
                callRecord[0] = true;
            }
        });
        // Run all the pending tasks on the ui thread
        while (!(callRecord[((callRecord.length) - 1)]))
            looper.runToEndOfTasks();

    }

    @Test(expected = RuntimeException.class)
    public void shouldCrashAppForExceptionInOnFinally() {
        final ShadowLooper looper = Robolectric.shadowOf(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().submit(new AndroidCallable<String>() {
            @Override
            public String doInBackground() throws Exception {
                return "";
            }

            @Override
            public void onSuccess(String result) {
                throw new RuntimeException();
            }

            @Override
            public void onException(Exception e) {
            }

            @Override
            public void onFinally() {
                throw new RuntimeException();
            }
        });
        // Run all the pending tasks on the ui thread
        // noinspection InfiniteLoopStatement
        while (true)
            looper.runToEndOfTasks();

    }

    @Test
    public void shouldCallOnExceptionForExceptionInDoInBackground() {
        final ShadowLooper looper = Robolectric.shadowOf(Looper.getMainLooper());
        final boolean[] callRecord = new boolean[]{ false, false, false, false };
        final boolean[] correctAnswer = new boolean[]{ true, false, true, true };
        Executors.newSingleThreadExecutor().submit(new AndroidCallable<String>() {
            @Override
            public void onPreCall() {
                callRecord[0] = true;
            }

            @Override
            public String doInBackground() throws Exception {
                throw new Exception();
            }

            @Override
            public void onSuccess(String result) {
                callRecord[1] = true;
            }

            @Override
            public void onException(Exception e) {
                callRecord[2] = true;
            }

            @Override
            public void onFinally() {
                callRecord[3] = true;
            }
        });
        // Run all the pending tasks on the ui thread
        while (!(callRecord[((callRecord.length) - 1)]))
            looper.runToEndOfTasks();

        Assert.assertThat(callRecord, CoreMatchers.equalTo(correctAnswer));
    }

    @Test
    public void shouldCallOnExceptionForExceptionInOnPreExecute() {
        final ShadowLooper looper = Robolectric.shadowOf(Looper.getMainLooper());
        final boolean[] callRecord = new boolean[]{ false, false, false };
        final boolean[] correctAnswer = new boolean[]{ false, true, true };
        Executors.newSingleThreadExecutor().submit(new AndroidCallable<String>() {
            @Override
            public void onPreCall() {
                throw new RuntimeException();
            }

            @Override
            public String doInBackground() throws Exception {
                return "";
            }

            @Override
            public void onSuccess(String result) {
                callRecord[0] = true;
            }

            @Override
            public void onException(Exception e) {
                callRecord[1] = true;
            }

            @Override
            public void onFinally() {
                callRecord[2] = true;
            }
        });
        // Run all the pending tasks on the ui thread
        while (!(callRecord[((callRecord.length) - 1)]))
            looper.runToEndOfTasks();

        Assert.assertThat(callRecord, CoreMatchers.equalTo(correctAnswer));
    }

    private static class StringAndroidCallable extends AndroidCallable<String> {
        Thread[] answers;

        boolean shouldThrowException;

        public StringAndroidCallable(Thread[] answers, boolean shouldThrowException) {
            this.answers = answers;
            this.shouldThrowException = shouldThrowException;
        }

        @Override
        public void onPreCall() {
            answers[0] = Thread.currentThread();
        }

        @Override
        public String doInBackground() throws Exception {
            answers[1] = Thread.currentThread();
            if (shouldThrowException)
                throw new UnsupportedOperationException();

            return "12345";
        }

        @Override
        public void onException(Exception e) {
            answers[2] = Thread.currentThread();
        }

        @Override
        public void onSuccess(String result) {
            answers[3] = Thread.currentThread();
        }

        @Override
        public void onFinally() {
            answers[4] = Thread.currentThread();
        }
    }

    private static class MyThreadFactory implements ThreadFactory {
        private final Thread[] bgThread;

        public MyThreadFactory(Thread[] bgThread) {
            this.bgThread = bgThread;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            bgThread[0] = new Thread(runnable);
            bgThread[0].setName("bgThread");
            return bgThread[0];
        }
    }
}

