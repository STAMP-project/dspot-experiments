/**
 * The MIT License
 *
 * Copyright (c) Red Hat, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.slaves;


import hudson.Functions;
import hudson.model.Computer;
import hudson.remoting.Channel;
import hudson.remoting.ChannelClosedException;
import hudson.remoting.PingThread;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;
import jenkins.security.MasterToSlaveCallable;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


/**
 *
 *
 * @author ogondza.
 */
public class PingThreadTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void failedPingThreadResetsComputerChannel() throws Exception {
        Assume.assumeFalse("We simulate hung agent by sending the SIGTSTP signal", Functions.isWindows());
        DumbSlave slave = j.createOnlineSlave();
        Computer computer = slave.toComputer();
        Channel channel = ((Channel) (slave.getChannel()));
        String pid = channel.call(new PingThreadTest.GetPid());
        PingThread pingThread = null;
        for (Thread it : Thread.getAllStackTraces().keySet()) {
            if ((it instanceof PingThread) && (it.getName().endsWith(channel.toString()))) {
                pingThread = ((PingThread) (it));
            }
        }
        Assert.assertNotNull(pingThread);
        // Simulate lost connection
        assert (new ProcessBuilder("kill", "-TSTP", pid).start().waitFor()) == 0;
        try {
            // ... do not wait for Ping Thread to notice
            Method onDead = PingThread.class.getDeclaredMethod("onDead", Throwable.class);
            onDead.setAccessible(true);
            onDead.invoke(pingThread, new TimeoutException("No ping"));
            try {
                channel.call(new PingThreadTest.GetPid());
                Assert.fail();
            } catch (ChannelClosedException ex) {
                // Expected
            }
            Assert.assertNull(slave.getComputer().getChannel());
            Assert.assertNull(computer.getChannel());
        } finally {
            assert (new ProcessBuilder("kill", "-CONT", pid).start().waitFor()) == 0;
        }
    }

    private static final class GetPid extends MasterToSlaveCallable<String, IOException> {
        @Override
        public String call() throws IOException {
            return ManagementFactory.getRuntimeMXBean().getName().replaceAll("@.*", "");
        }
    }
}

