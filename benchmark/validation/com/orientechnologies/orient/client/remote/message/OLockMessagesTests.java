package com.orientechnologies.orient.client.remote.message;


import ORecordSerializerNetworkFactory.INSTANCE;
import OStorage.LOCKING_STRATEGY;
import com.orientechnologies.orient.core.id.ORecordId;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class OLockMessagesTests {
    @Test
    public void testReadWriteLockRequest() throws IOException {
        OLockRecordRequest request = new OLockRecordRequest(new ORecordId(10, 10), LOCKING_STRATEGY.EXCLUSIVE_LOCK, 10);
        MockChannel channel = new MockChannel();
        request.write(channel, null);
        channel.close();
        OLockRecordRequest other = new OLockRecordRequest();
        other.read(channel, (-1), INSTANCE.current());
        Assert.assertEquals(other.getIdentity(), request.getIdentity());
        Assert.assertEquals(other.getTimeout(), request.getTimeout());
        Assert.assertEquals(other.getLockingStrategy(), request.getLockingStrategy());
    }

    @Test
    public void testReadWriteLockResponse() throws IOException {
        OLockRecordResponse response = new OLockRecordResponse(((byte) (1)), 2, "value".getBytes());
        MockChannel channel = new MockChannel();
        response.write(channel, 0, null);
        channel.close();
        OLockRecordResponse other = new OLockRecordResponse();
        other.read(channel, null);
        Assert.assertEquals(other.getRecordType(), response.getRecordType());
        Assert.assertEquals(other.getVersion(), response.getVersion());
        Assert.assertArrayEquals(other.getRecord(), response.getRecord());
    }

    @Test
    public void testReadWriteUnlockRequest() throws IOException {
        OUnlockRecordRequest request = new OUnlockRecordRequest(new ORecordId(10, 10));
        MockChannel channel = new MockChannel();
        request.write(channel, null);
        channel.close();
        OUnlockRecordRequest other = new OUnlockRecordRequest();
        other.read(channel, (-1), INSTANCE.current());
        Assert.assertEquals(other.getIdentity(), request.getIdentity());
    }
}

