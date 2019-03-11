package water;


import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tomas on 8/16/16.
 */
public class FuturesTest extends TestUtil {
    public static class TstFuture implements Future {
        private boolean _isDone;

        private boolean _isCancelled;

        public ExecutionException _exex;

        public RuntimeException _rex;

        @Override
        public synchronized boolean cancel(boolean mayInterruptIfRunning) {
            _isCancelled = true;
            notifyAll();
            return true;
        }

        public synchronized void complete() {
            _isDone = true;
            notifyAll();
        }

        public synchronized void complete(Throwable t) {
            if (t instanceof ExecutionException)
                _exex = ((ExecutionException) (t));
            else
                if (t instanceof RuntimeException)
                    _rex = ((RuntimeException) (t));
                else
                    throw new IllegalArgumentException();


            _isDone = true;
            notifyAll();
        }

        @Override
        public boolean isCancelled() {
            return _isCancelled;
        }

        @Override
        public boolean isDone() {
            return (_isDone) || (_isCancelled);
        }

        @Override
        public Object get() throws InterruptedException, ExecutionException {
            while (!(isDone())) {
                synchronized(this) {
                    wait();
                }
            } 
            if (_isCancelled)
                throw new CancellationException();

            if ((_exex) != null)
                throw _exex;

            if ((_rex) != null)
                throw _rex;

            return this;
        }

        @Override
        public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }

    private static class TstException extends RuntimeException {
        public TstException(String msg) {
            super(msg);
        }
    }

    // Test exceptions are correctly rethrown even if the future is eagerly removed
    @Test
    public void testExceptions() {
        // 0 test exception from pending task is thrown
        Futures fs = new Futures();
        FuturesTest.TstFuture cf = new FuturesTest.TstFuture();
        fs.add(cf);
        Assert.assertEquals(1, fs._pending_cnt);// task is already completed

        cf.complete(new ExecutionException(new FuturesTest.TstException("a")));
        try {
            fs.blockForPending();
            Assert.assertTrue("should've thrown", false);
        } catch (RuntimeException t) {
            Assert.assertTrue(((t.getCause()) instanceof FuturesTest.TstException));
        }
        // 1 test exception from already completed task is rethrown in blockForPending
        fs = new Futures();
        cf = new FuturesTest.TstFuture();
        cf.complete(new ExecutionException(new FuturesTest.TstException("a")));
        fs.add(cf);
        Assert.assertEquals(0, fs._pending_cnt);// task is already completed

        try {
            fs.blockForPending();
            Assert.assertTrue("should've thrown", false);
        } catch (RuntimeException t) {
            Assert.assertTrue(((t.getCause()) instanceof FuturesTest.TstException));
        }
        // 2 test exception is recorded and re-thrown if task is eagerly cleaned
        fs = new Futures();
        cf = new FuturesTest.TstFuture();
        fs.add(cf);
        Assert.assertEquals(1, fs._pending_cnt);
        cf.complete(new FuturesTest.TstException("eager cleanup"));
        for (int i = 0; i < 3; ++i) {
            fs.add((cf = new FuturesTest.TstFuture()));
            Assert.assertEquals(1, fs._pending_cnt);
            cf.complete();
        }
        try {
            fs.blockForPending();
            Assert.assertTrue("should've thrown", false);
        } catch (RuntimeException t) {
            Assert.assertTrue(((t.getCause()) instanceof FuturesTest.TstException));
            Assert.assertEquals("eager cleanup", t.getCause().getMessage());
        }
        // 3 test cancellation exceptions are ignored
        fs = new Futures();
        cf = new FuturesTest.TstFuture();
        cf.cancel(true);
        fs.add(cf);
        fs.blockForPending();
    }
}

