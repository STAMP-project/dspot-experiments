package io.dropwizard.logging;


import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.util.Resources;
import io.dropwizard.validation.BaseValidator;
import java.io.File;
import java.net.DatagramSocket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UdpSocketAppenderFactoryTest {
    private static final int UDP_PORT = 32144;

    private Thread thread;

    private DatagramSocket datagramSocket;

    private int messagesCount = 100;

    private CountDownLatch countDownLatch = new CountDownLatch(messagesCount);

    @Test
    public void testSendLogsByTcp() throws Exception {
        ObjectMapper objectMapper = Jackson.newObjectMapper();
        objectMapper.getSubtypeResolver().registerSubtypes(UdpSocketAppenderFactory.class);
        DefaultLoggingFactory loggingFactory = new io.dropwizard.configuration.YamlConfigurationFactory(DefaultLoggingFactory.class, BaseValidator.newValidator(), objectMapper, "dw-udp").build(new File(Resources.getResource("yaml/logging-udp.yml").toURI()));
        loggingFactory.configure(new MetricRegistry(), "udp-test");
        Logger logger = LoggerFactory.getLogger("com.example.app");
        for (int i = 0; i < 100; i++) {
            logger.info("Application log {}", i);
        }
        countDownLatch.await(5, TimeUnit.SECONDS);
        assertThat(countDownLatch.getCount()).isEqualTo(0);
        loggingFactory.reset();
    }
}

