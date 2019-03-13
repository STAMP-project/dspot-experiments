package com.twitter.elephantbird.thrift;


import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.transport.TTransport;
import org.junit.Test;


public class TestThriftBinaryProtocol {
    @Test
    public void testCheckContainerSizeValid() throws TException {
        // any non-negative value is considered valid when checkReadLength is not enabled
        TTransport transport;
        ThriftBinaryProtocol protocol;
        transport = getMockTransport(3);
        replay(transport);
        protocol = new ThriftBinaryProtocol(transport);
        protocol.readListBegin();
        verify(transport);
        transport = getMockTransport(3);
        replay(transport);
        protocol = new ThriftBinaryProtocol(transport);
        protocol.readSetBegin();
        verify(transport);
        transport = getMockMapTransport(3);
        replay(transport);
        protocol = new ThriftBinaryProtocol(transport);
        protocol.readMapBegin();
        verify(transport);
    }

    @Test(expected = TProtocolException.class)
    public void testCheckListContainerSizeInvalid() throws TException {
        // any negative value is considered invalid when checkReadLength is not enabled
        TTransport transport = getMockTransport((-1));
        replay(transport);
        ThriftBinaryProtocol protocol = new ThriftBinaryProtocol(transport);
        protocol.readListBegin();
        verify(transport);
    }

    @Test(expected = TProtocolException.class)
    public void testCheckSetContainerSizeInvalid() throws TException {
        TTransport transport = getMockTransport((-1));
        replay(transport);
        ThriftBinaryProtocol protocol = new ThriftBinaryProtocol(transport);
        protocol.readSetBegin();
        verify(transport);
    }

    @Test(expected = TProtocolException.class)
    public void testCheckMapContainerSizeInvalid() throws TException {
        TTransport transport = getMockMapTransport((-1));
        replay(transport);
        ThriftBinaryProtocol protocol = new ThriftBinaryProtocol(transport);
        protocol.readMapBegin();
        verify(transport);
    }
}

