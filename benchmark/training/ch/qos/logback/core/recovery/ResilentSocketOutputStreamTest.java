package ch.qos.logback.core.recovery;


import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import org.junit.jupiter.api.Test;


public class ResilentSocketOutputStreamTest {
    private ResilentSocketOutputStream resilentSocketOutputStream;

    private Thread thread;

    private ServerSocket ss;

    private CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void testCreatesCleanOutputStream() throws Exception {
        assertThat(resilentSocketOutputStream.presumedClean).isTrue();
        assertThat(resilentSocketOutputStream.os).isNotNull();
    }

    @Test
    public void testThrowsExceptionIfCantCreateOutputStream() throws Exception {
        assertThatIllegalStateException().isThrownBy(() -> new ResilentSocketOutputStream("256.256.256.256", 1024, 100, 1024, SocketFactory.getDefault())).withMessage("Unable to create a TCP connection to 256.256.256.256:1024");
    }

    @Test
    public void testWriteMessage() throws Exception {
        resilentSocketOutputStream.write("Test message".getBytes(StandardCharsets.UTF_8));
        resilentSocketOutputStream.flush();
        latch.await(5, TimeUnit.SECONDS);
        assertThat(latch.getCount()).isEqualTo(0);
    }

    @Test
    public void testGetDescription() {
        assertThat(resilentSocketOutputStream.getDescription()).isEqualTo(String.format("tcp [localhost:%d]", ss.getLocalPort()));
    }
}

