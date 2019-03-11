package org.web3j.protocol.core;


import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.Test;
import org.mockito.Mockito;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;


public class JsonRpc2_0Web3jTest {
    private ScheduledExecutorService scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);

    private Web3jService service = Mockito.mock(Web3jService.class);

    private Web3j web3j = Web3j.build(service, 10, scheduledExecutorService);

    @Test
    public void testStopExecutorOnShutdown() throws Exception {
        web3j.shutdown();
        Mockito.verify(scheduledExecutorService).shutdown();
        Mockito.verify(service).close();
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionOnServiceClosure() throws Exception {
        Mockito.doThrow(new IOException("Failed to close")).when(service).close();
        web3j.shutdown();
    }
}

