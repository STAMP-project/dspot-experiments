package org.mockserver.proxy.socks;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;


public class SocksDetectorTest {
    @Test
    public void successfullyParseSocks4ConnectRequest() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 1// command code
        , 0, 0// port
        , 6, 6, 6, 6// ip
        , 'f', 'o', 'o', 0// username
         });
        Assert.assertTrue(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void successfullyParseSocks4BindRequest() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 2// command code
        , 0, 0// port
        , 6, 6, 6, 6// ip
        , 'f', 'o', 'o', 0// username
         });
        Assert.assertTrue(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void successfullyParseSocks4RequestWithEmptyUsername() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 1// command code
        , 0, 0// port
        , 6, 6, 6, 6// ip
        , 0// username
         });
        Assert.assertTrue(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void successfullyParseSocks4RequestWithMaxLengthUsername() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 1// command code
        , 0, 0// port
        , 6, 6, 6, 6// ip
        , '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'// username
        , '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', 0 });
        Assert.assertTrue(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void failParsingSocks4RequestWithWrongProtocolVersion() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 5// protocol version
        , 1// command code
        , 0, 0// port
        , 6, 6, 6, 6// ip
        , 'f', 'o', 'o', 0// username
         });
        Assert.assertFalse(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void failParsingSocks4RequestWithUnsupportedCommandCode() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 3// command code
        , 0, 0// port
        , 6, 6, 6, 6// ip
        , 'f', 'o', 'o', 0// username
         });
        Assert.assertFalse(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void failParsingSocks4RequestWithTooLongUsername() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 1// command code
        , 0, 0// port
        , 6, 6, 6, 6// ip
        , '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'// username
        , '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', 0 });
        Assert.assertFalse(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void failParsingSocks4RequestWithAdditionalReadableBytes() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 1// command code
        , 0, 0// port
        , 6, 6, 6, 6// ip
        , 'f', 'o', 'o', 0// username
        , 0// additional byte
         });
        Assert.assertFalse(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void successfullyParseSocks4aRequest() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 1// command code
        , 0, 0// port
        , 0, 0, 0, 6// invalid ip
        , 'f', 'o', 'o', 0// username
        , 'f', 'o', 'o', 0// hostname
         });
        Assert.assertTrue(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void successfullyParseSocks4aRequestWithEmptyUsername() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 1// command code
        , 0, 0// port
        , 0, 0, 0, 6// invalid ip
        , 0// username
        , 'f', 'o', 'o', 0// hostname
         });
        Assert.assertTrue(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void successfullyParseSocks4aRequestWithMaxLengthHostname() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 1// command code
        , 0, 0// port
        , 0, 0, 0, 6// invalid ip
        , 'f', 'o', 'o', 0// username
        , '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'// hostname
        , '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', 0 });
        Assert.assertTrue(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void failParsingSocks4aRequestWithTooLongHostname() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 1// command code
        , 0, 0// port
        , 0, 0, 0, 6// invalid ip
        , 'f', 'o', 'o', 0// username
        , '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'// hostname
        , '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', 0 });
        Assert.assertFalse(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void failParsingSocks4aRequestWithEmptyHostname() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 1// command code
        , 0, 0// port
        , 0, 0, 0, 6// invalid ip
        , 'f', 'o', 'o', 0// username
        , 0// hostname
         });
        Assert.assertFalse(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void failParsingSocks4aRequestWithAdditionalReadableBytes() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 1// command code
        , 0, 0// port
        , 0, 0, 0, 6// invalid ip
        , 'f', 'o', 'o', 0// username
        , 'f', 'o', 'o', 0// hostname
        , 0// additional byte
         });
        Assert.assertFalse(SocksDetector.isSocks4(msg, msg.readableBytes()));
    }

    @Test
    public void successfullyParseSocks5Request() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 5// protocol version
        , 2// amount of authentication methods
        , 0, 2// authentication methods
         });
        Assert.assertTrue(SocksDetector.isSocks5(msg, msg.readableBytes()));
    }

    @Test
    public void successfullyParseSocks5RequestWithManyMethods() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 5// protocol version
        , 5// amount of authentication methods
        , 0, 1, 2, 0, 1// authentication methods
         });
        Assert.assertTrue(SocksDetector.isSocks5(msg, msg.readableBytes()));
    }

    @Test
    public void failParsingSocks5RequestWithWrongProtocolVersion() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 4// protocol version
        , 5// amount of authentication methods
        , 0, 1, 2, 0, 1// authentication methods
         });
        Assert.assertFalse(SocksDetector.isSocks5(msg, msg.readableBytes()));
    }

    @Test
    public void failParsingSocks5RequestWithUnsupportedAuthenticationMethod() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 5// protocol version
        , 1// amount of authentication methods
        , 5// authentication methods
         });
        Assert.assertFalse(SocksDetector.isSocks5(msg, msg.readableBytes()));
    }

    @Test
    public void failParsingSocks5RequestWithAdditionalReadableBytes() {
        ByteBuf msg = Unpooled.wrappedBuffer(new byte[]{ 5// protocol version
        , 1// amount of authentication methods
        , 0// authentication methods
        , 0// additional byte
         });
        Assert.assertFalse(SocksDetector.isSocks5(msg, msg.readableBytes()));
    }
}

