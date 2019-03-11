package cucumber.runner;


import org.junit.Assert;
import org.junit.Test;


public class TimeServiceTest {
    private final TimeService stopWatch = TimeService.SYSTEM;

    private Throwable exception;

    @Test
    public void should_be_thread_safe() {
        try {
            Thread timerThreadOne = new TimeServiceTest.TimerThread(500L);
            Thread timerThreadTwo = new TimeServiceTest.TimerThread(750L);
            timerThreadOne.start();
            timerThreadTwo.start();
            timerThreadOne.join();
            timerThreadTwo.join();
            Assert.assertNull("null_pointer_exception", exception);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    class TimerThread extends Thread {
        private final long timeoutMillis;

        public TimerThread(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
        }

        @Override
        public void run() {
            try {
                stopWatch.time();
                Thread.sleep(timeoutMillis);
                stopWatch.time();
            } catch (NullPointerException e) {
                exception = e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

