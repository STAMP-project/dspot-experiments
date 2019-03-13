package com.weibo.api.motan.transport.netty4;


import URLParamType.maxServerConnection;
import URLParamType.minClientConnection;
import URLParamType.requestTimeout;
import com.weibo.api.motan.rpc.DefaultResponse;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.URL;
import com.weibo.api.motan.transport.Channel;
import com.weibo.api.motan.transport.MessageHandler;
import com.weibo.api.motan.util.StatsUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author sunnights
 */
public class NettyServerTest {
    private NettyServer nettyServer;

    private URL url;

    private String interfaceName = "com.weibo.api.motan.protocol.example.IHello";

    @Test
    public void testMaxServerConnection() throws InterruptedException {
        int minClientConnection = 5;
        int maxServerConnection = 7;
        url.addParameter(minClientConnection.getName(), String.valueOf(minClientConnection));
        url.addParameter(maxServerConnection.getName(), String.valueOf(maxServerConnection));
        url.addParameter(requestTimeout.getName(), "10000");
        nettyServer = new NettyServer(url, new MessageHandler() {
            @Override
            public Object handle(Channel channel, Object message) {
                Request request = ((Request) (message));
                DefaultResponse response = new DefaultResponse();
                response.setRequestId(request.getRequestId());
                response.setValue(((("method: " + (request.getMethodName())) + " requestId: ") + (request.getRequestId())));
                return response;
            }
        });
        nettyServer.open();
        Assert.assertEquals(0, nettyServer.channelManage.getChannels().size());
        NettyClient nettyClient = new NettyClient(url);
        nettyClient.open();
        Thread.sleep(100);
        Assert.assertEquals(minClientConnection, nettyServer.channelManage.getChannels().size());
        NettyClient nettyClient2 = new NettyClient(url);
        nettyClient2.open();
        Thread.sleep(100);
        Assert.assertEquals(maxServerConnection, nettyServer.channelManage.getChannels().size());
        nettyClient.close();
        nettyClient2.close();
        Thread.sleep(100);
        Assert.assertEquals(0, nettyServer.channelManage.getChannels().size());
    }

    @Test
    public void testCallbacks() throws InterruptedException {
        int minClientConnection = 2;
        int maxServerConnection = 2;
        url.addParameter(minClientConnection.getName(), String.valueOf(minClientConnection));
        url.addParameter(maxServerConnection.getName(), String.valueOf(maxServerConnection));
        url.addParameter(requestTimeout.getName(), "10000");
        nettyServer = new NettyServer(url, new MessageHandler() {
            @Override
            public Object handle(Channel channel, Object message) {
                Request request = ((Request) (message));
                DefaultResponse response = new DefaultResponse();
                response.setRequestId(request.getRequestId());
                response.setValue(((("method: " + (request.getMethodName())) + " requestId: ") + (request.getRequestId())));
                return response;
            }
        });
        nettyServer.open();
        Assert.assertEquals(0, nettyServer.channelManage.getChannels().size());
        NettyClient nettyClient = new NettyClient(url);
        nettyClient.open();
        Thread.sleep(100);
        nettyClient.close();
        Assert.assertEquals((minClientConnection + 1), StatsUtil.getStatisticCallbacks().size());
        nettyServer.close();
        Thread.sleep(100);
        Assert.assertEquals(0, StatsUtil.getStatisticCallbacks().size());
    }
}

