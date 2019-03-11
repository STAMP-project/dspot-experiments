package org.testcontainers.junit;


import java.util.function.Consumer;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.output.WaitingConsumer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;


/**
 * Created by rnorth on 26/07/2016.
 */
public class WorkingDirectoryTest {
    private static WaitingConsumer waitingConsumer = new WaitingConsumer();

    private static ToStringConsumer toStringConsumer = new ToStringConsumer();

    private static Consumer<OutputFrame> compositeConsumer = WorkingDirectoryTest.waitingConsumer.andThen(WorkingDirectoryTest.toStringConsumer);

    @ClassRule
    public static GenericContainer container = new GenericContainer("alpine:3.2").withWorkingDirectory("/etc").withStartupCheckStrategy(new OneShotStartupCheckStrategy()).withCommand("ls", "-al").withLogConsumer(WorkingDirectoryTest.compositeConsumer);

    @Test
    public void checkOutput() {
        String listing = WorkingDirectoryTest.toStringConsumer.toUtf8String();
        assertTrue("Directory listing contains expected /etc content", listing.contains("hostname"));
        assertTrue("Directory listing contains expected /etc content", listing.contains("init.d"));
        assertTrue("Directory listing contains expected /etc content", listing.contains("passwd"));
    }
}

