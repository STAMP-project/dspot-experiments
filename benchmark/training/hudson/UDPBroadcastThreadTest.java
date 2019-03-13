package hudson;


import UDPBroadcastThread.MULTICAST;
import UDPBroadcastThread.udpHandlingProblem;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class UDPBroadcastThreadTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Old unicast based clients should still be able to receive some reply,
     * as we haven't changed the port.
     */
    @Test
    public void legacy() throws Exception {
        DatagramSocket s = new DatagramSocket();
        sendQueryTo(s, InetAddress.getLocalHost());
        s.setSoTimeout(15000);// to prevent test hang

        try {
            receiveAndVerify(s);
        } catch (SocketTimeoutException x) {
            Assume.assumeFalse(udpHandlingProblem);
            throw x;
        }
    }

    /**
     * Multicast based clients should be able to receive multiple replies.
     */
    @Test
    public void multicast() throws Exception {
        UDPBroadcastThread second = new UDPBroadcastThread(j.jenkins);
        second.start();
        UDPBroadcastThread third = new UDPBroadcastThread(j.jenkins);
        third.start();
        second.ready.block();
        third.ready.block();
        try {
            DatagramSocket s = new DatagramSocket();
            sendQueryTo(s, MULTICAST);
            s.setSoTimeout(15000);// to prevent test hang

            // we should at least get two replies since we run two broadcasts
            try {
                receiveAndVerify(s);
                receiveAndVerify(s);
            } catch (SocketTimeoutException x) {
                Assume.assumeFalse(udpHandlingProblem);
                throw x;
            }
        } finally {
            third.interrupt();
            second.interrupt();
        }
    }
}

