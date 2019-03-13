package io.socket.client;


import java.io.IOException;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ExecutionTest extends Connection {
    private static final Logger logger = Logger.getLogger(ExecutionTest.class.getName());

    static final int TIMEOUT = 100 * 1000;

    @Test(timeout = ExecutionTest.TIMEOUT)
    public void execConnection() throws IOException, InterruptedException {
        exec("io.socket.client.executions.Connection");
    }

    @Test(timeout = ExecutionTest.TIMEOUT)
    public void execConnectionFailure() throws IOException, InterruptedException {
        exec("io.socket.client.executions.ConnectionFailure");
    }

    @Test(timeout = ExecutionTest.TIMEOUT)
    public void execImmediateClose() throws IOException, InterruptedException {
        exec("io.socket.client.executions.ImmediateClose");
    }
}

