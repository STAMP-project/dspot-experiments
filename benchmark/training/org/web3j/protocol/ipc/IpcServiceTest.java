package org.web3j.protocol.ipc;


import java.io.IOException;
import org.junit.Test;
import org.mockito.Mockito;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;


public class IpcServiceTest {
    private IpcService ipcService;

    private IOFacade ioFacade;

    @Test
    public void testSend() throws IOException {
        Mockito.when(ioFacade.read()).thenReturn(("{\"jsonrpc\":\"2.0\",\"id\":1," + "\"result\":\"Geth/v1.5.4-stable-b70acf3c/darwin/go1.7.3\"}\n"));
        ipcService.send(new Request(), Web3ClientVersion.class);
        Mockito.verify(ioFacade).write("{\"jsonrpc\":\"2.0\",\"method\":null,\"params\":null,\"id\":0}");
    }
}

