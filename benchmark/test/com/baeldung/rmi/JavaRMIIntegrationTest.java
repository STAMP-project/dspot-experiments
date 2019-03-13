package com.baeldung.rmi;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.junit.Assert;
import org.junit.Test;


public class JavaRMIIntegrationTest {
    @Test
    public void whenClientSendsMessageToServer_thenServerSendsResponseMessage() {
        try {
            Registry registry = LocateRegistry.getRegistry();
            MessengerService server = ((MessengerService) (registry.lookup("MessengerService")));
            String responseMessage = server.sendMessage("Client Message");
            String expectedMessage = "Server Message";
            Assert.assertEquals(responseMessage, expectedMessage);
        } catch (RemoteException e) {
            Assert.fail("Exception Occurred");
        } catch (NotBoundException nb) {
            Assert.fail("Exception Occurred");
        }
    }
}

