package org.mockserver.socket;


import java.io.IOException;
import java.net.ServerSocket;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class PortFactoryTest {
    @Test
    public void shouldFindFreePort() throws IOException {
        // when
        int freePort = new PortFactory().findFreePort();
        // then
        Assert.assertTrue(new ServerSocket(freePort).isBound());
    }
}

