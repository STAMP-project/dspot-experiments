package water;


import H2O.SELF;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class RemoteRunnableTest extends TestUtil {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testRunOnH2ONode_local() {
        RemoteRunnableTest.DummyLocalRunnable runnable = new RemoteRunnableTest.DummyLocalRunnable("NOT OK");
        RemoteRunnableTest.DummyLocalRunnable result = H2O.runOnH2ONode(SELF, runnable);
        Assert.assertSame(runnable, result);
        Assert.assertSame(Thread.currentThread(), result._thread);
        Assert.assertEquals("OK", result._result);
    }

    @Test
    public void testRunOnH2ONode_remote() {
        H2ONode node = findRemoteNode();
        Assert.assertNotNull(node);
        RemoteRunnableTest.DummyRemoteRunnable runnable = new RemoteRunnableTest.DummyRemoteRunnable("NOT OK");
        RemoteRunnableTest.DummyRemoteRunnable result = H2O.runOnH2ONode(node, runnable);
        Assert.assertNotSame(runnable, result);
        Assert.assertEquals("OK", result._result);
    }

    @Test
    public void testRunOnH2ONode_remoteFailing() {
        H2ONode node = findRemoteNode();
        Assert.assertNotNull(node);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Failing intentionally");
        RemoteRunnableTest.FailingRemoteRunnable runnable = new RemoteRunnableTest.FailingRemoteRunnable();
        H2O.runOnH2ONode(node, runnable);
    }

    private static class DummyLocalRunnable extends H2O.RemoteRunnable<RemoteRunnableTest.DummyLocalRunnable> {
        private String _result;

        private transient Thread _thread;

        public DummyLocalRunnable(String result) {
            _result = result;
        }

        @Override
        public void setupOnRemote() {
            throw new IllegalStateException("Shouldn't be called");
        }

        @Override
        public void run() {
            _thread = Thread.currentThread();
            _result = "OK";
        }
    }

    private static class DummyRemoteRunnable extends H2O.RemoteRunnable<RemoteRunnableTest.DummyRemoteRunnable> {
        private String _result;

        public DummyRemoteRunnable(String result) {
            _result = result;
        }

        @Override
        public void setupOnRemote() {
            _result = "O";
        }

        @Override
        public void run() {
            _result += "K";
        }
    }

    private static class FailingRemoteRunnable extends H2O.RemoteRunnable<RemoteRunnableTest.FailingRemoteRunnable> {
        @Override
        public void run() {
            throw new IllegalStateException("Failing intentionally");
        }
    }
}

