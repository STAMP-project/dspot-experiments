package io.dropwizard.logging.socket;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;


public class DropwizardUdpSocketAppenderTest {
    private OutputStreamAppender<ILoggingEvent> udpStreamAppender;

    private DatagramSocket datagramSocket;

    private Thread thread;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    public void testSendMessage() throws Exception {
        udpStreamAppender.getOutputStream().write("Test message".getBytes(StandardCharsets.UTF_8));
        countDownLatch.await(5, TimeUnit.SECONDS);
        assertThat(countDownLatch.getCount()).isEqualTo(0);
    }
}

