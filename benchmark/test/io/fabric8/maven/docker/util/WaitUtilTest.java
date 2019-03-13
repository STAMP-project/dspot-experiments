package io.fabric8.maven.docker.util;


import com.sun.net.httpserver.HttpServer;
import io.fabric8.maven.docker.config.WaitConfiguration;
import java.util.Collections;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author roland
 * @since 18.10.14
 */
@SuppressWarnings("restriction")
public class WaitUtilTest {
    static HttpServer server;

    static int port;

    static String httpPingUrl;

    private static String serverMethodToAssert;

    @Test(expected = TimeoutException.class)
    public void httpFail() throws PreconditionFailedException, TimeoutException {
        HttpPingChecker checker = new HttpPingChecker((("http://127.0.0.1:" + (WaitUtilTest.port)) + "/fake-context/"));
        WaitUtilTest.wait(500, checker);
    }

    @Test
    public void httpSuccess() throws PreconditionFailedException, TimeoutException {
        HttpPingChecker checker = new HttpPingChecker(WaitUtilTest.httpPingUrl);
        long waited = WaitUtilTest.wait(700, checker);
        Assert.assertTrue(("Waited less than 700ms: " + waited), (waited < 700));
    }

    @Test
    public void containerNotRunningButWaitConditionOk() throws PreconditionFailedException, TimeoutException {
        HttpPingChecker checker = new HttpPingChecker(WaitUtilTest.httpPingUrl);
        long waited = WaitUtilTest.wait(1, 700, checker);
        Assert.assertTrue(("Waited less than 700ms: " + waited), (waited < 700));
    }

    @Test(expected = PreconditionFailedException.class)
    public void containerNotRunningAndWaitConditionNok() throws PreconditionFailedException, TimeoutException {
        HttpPingChecker checker = new HttpPingChecker((("http://127.0.0.1:" + (WaitUtilTest.port)) + "/fake-context/"));
        WaitUtilTest.wait(0, 700, checker);
    }

    @Test
    public void httpSuccessWithStatus() throws PreconditionFailedException, TimeoutException {
        for (String status : new String[]{ "200", "200 ... 300", "200..250" }) {
            long waited = WaitUtilTest.wait(700, new HttpPingChecker(WaitUtilTest.httpPingUrl, WaitConfiguration.DEFAULT_HTTP_METHOD, status));
            Assert.assertTrue(("Waited less than  700ms: " + waited), (waited < 700));
        }
    }

    @Test(expected = TimeoutException.class)
    public void httpFailWithStatus() throws PreconditionFailedException, TimeoutException {
        WaitUtilTest.wait(700, new HttpPingChecker(WaitUtilTest.httpPingUrl, WaitConfiguration.DEFAULT_HTTP_METHOD, "500"));
    }

    @Test
    public void httpSuccessWithGetMethod() throws Exception {
        WaitUtilTest.serverMethodToAssert = "GET";
        try {
            HttpPingChecker checker = new HttpPingChecker(WaitUtilTest.httpPingUrl, "GET", WaitConfiguration.DEFAULT_STATUS_RANGE);
            long waited = WaitUtilTest.wait(700, checker);
            Assert.assertTrue(("Waited less than 500ms: " + waited), (waited < 700));
        } finally {
            WaitUtilTest.serverMethodToAssert = "HEAD";
        }
    }

    @Test
    public void tcpSuccess() throws PreconditionFailedException, TimeoutException {
        TcpPortChecker checker = new TcpPortChecker("localhost", Collections.singletonList(WaitUtilTest.port));
        long waited = WaitUtilTest.wait(700, checker);
        Assert.assertTrue(("Waited less than 700ms: " + waited), (waited < 700));
    }

    @Test
    public void cleanupShouldBeCalledAfterMatchedException() throws PreconditionFailedException, WaitTimeoutException {
        WaitUtilTest.StubWaitChecker checker = new WaitUtilTest.StubWaitChecker(true);
        WaitUtilTest.wait(0, checker);
        Assert.assertTrue(checker.isCleaned());
    }

    @Test
    public void cleanupShouldBeCalledAfterFailedException() throws PreconditionFailedException {
        WaitUtilTest.StubWaitChecker checker = new WaitUtilTest.StubWaitChecker(false);
        try {
            WaitUtilTest.wait(0, checker);
            Assert.fail("Failed expectation expected");
        } catch (WaitTimeoutException e) {
            Assert.assertTrue(checker.isCleaned());
        }
    }

    @Test
    public void waitOnCallable() throws Exception {
        long waited = waitOnCallable(1, 500);
        Assert.assertTrue((500 <= waited));
        Assert.assertTrue((1000 > waited));
    }

    @Test
    public void waitOnCallableFullWait() throws Exception {
        long waited = waitOnCallable(1, 1000);
        Assert.assertTrue((1000 <= waited));
    }

    private static class StubWaitChecker implements WaitChecker {
        private final boolean checkResult;

        private boolean cleaned = false;

        public StubWaitChecker(boolean checkResult) {
            this.checkResult = checkResult;
        }

        @Override
        public boolean check() {
            return checkResult;
        }

        @Override
        public void cleanUp() {
            cleaned = true;
        }

        @Override
        public String getLogLabel() {
            return "";
        }

        public boolean isCleaned() {
            return cleaned;
        }
    }

    private static class TestWaitPrecondition implements WaitUtil.Precondition {
        private int nrFailAfter;

        public TestWaitPrecondition(int nrFailAfter) {
            this.nrFailAfter = nrFailAfter;
        }

        @Override
        public boolean isOk() {
            return ((nrFailAfter) == (-1)) || (((nrFailAfter)--) > 0);
        }

        @Override
        public void cleanup() {
        }
    }
}

