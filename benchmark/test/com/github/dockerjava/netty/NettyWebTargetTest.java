package com.github.dockerjava.netty;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


/**
 *
 *
 * @author Alexander Koshevoy
 */
public class NettyWebTargetTest {
    @Mock
    private ChannelProvider channelProvider;

    @Test
    public void verifyImmutability() throws Exception {
        NettyWebTarget emptyWebTarget = new NettyWebTarget(channelProvider, "DUMMY");
        NettyWebTarget initWebTarget = emptyWebTarget.path("/containers/{id}/attach").resolveTemplate("id", "d03da378b592").queryParam("logs", "true");
        NettyWebTarget anotherWebTarget = emptyWebTarget.path("/containers/{id}/attach").resolveTemplate("id", "2cfada4e3c07").queryParam("stdin", "true");
        Assert.assertEquals(new NettyWebTarget(channelProvider, "DUMMY"), emptyWebTarget);
        Assert.assertEquals(path("/containers/d03da378b592/attach").queryParam("logs", "true"), initWebTarget);
        Assert.assertEquals(path("/containers/2cfada4e3c07/attach").queryParam("stdin", "true"), anotherWebTarget);
    }
}

