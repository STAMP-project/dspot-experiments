package hudson;


import hudson.Launcher.RemoteLauncher;
import hudson.remoting.Pipe;
import hudson.remoting.VirtualChannel;
import hudson.util.IOUtils;
import hudson.util.StreamTaskListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import jenkins.security.MasterToSlaveCallable;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
@Issue("JENKINS-7809")
public class ProcTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Makes sure that the output flushing and {@link RemoteProc#join()} is synced.
     */
    @Test
    public void remoteProcOutputSync() throws Exception {
        Assume.assumeFalse("TODO: Implement this test for Windows", Functions.isWindows());
        VirtualChannel ch = createSlaveChannel();
        // keep the pipe fairly busy
        final Pipe p = Pipe.createRemoteToLocal();
        for (int i = 0; i < 10; i++)
            ch.callAsync(new ProcTest.ChannelFiller(p.getOut()));

        new Thread() {
            @Override
            public void run() {
                try {
                    IOUtils.drain(p.getIn());
                } catch (IOException e) {
                }
            }
        }.start();
        RemoteLauncher launcher = new RemoteLauncher(StreamTaskListener.NULL, ch, true);
        String str = "";
        for (int i = 0; i < 256; i++)
            str += "oxox";

        for (int i = 0; i < 1000; i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            launcher.launch().cmds("echo", str).stdout(baos).join();
            Assert.assertEquals(str, baos.toString().trim());
        }
        ch.close();
    }

    private static class ChannelFiller extends MasterToSlaveCallable<Void, IOException> {
        private final OutputStream o;

        private ChannelFiller(OutputStream o) {
            this.o = o;
        }

        public Void call() throws IOException {
            while (!(Thread.interrupted())) {
                o.write(new byte[256]);
            } 
            return null;
        }
    }

    @Test
    public void ioPumpingWithLocalLaunch() throws Exception {
        Assume.assumeFalse("TODO: Implement this test for Windows", Functions.isWindows());
        doIoPumpingTest(new hudson.Launcher.LocalLauncher(new StreamTaskListener(System.out, Charset.defaultCharset())));
    }

    @Test
    public void ioPumpingWithRemoteLaunch() throws Exception {
        Assume.assumeFalse("TODO: Implement this test for Windows", Functions.isWindows());
        doIoPumpingTest(new RemoteLauncher(new StreamTaskListener(System.out, Charset.defaultCharset()), createSlaveChannel(), true));
    }
}

