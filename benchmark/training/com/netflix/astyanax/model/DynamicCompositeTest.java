package com.netflix.astyanax.model;


import com.netflix.astyanax.serializers.AsciiSerializer;
import com.netflix.astyanax.serializers.BytesArraySerializer;
import com.netflix.astyanax.serializers.IntegerSerializer;
import com.netflix.astyanax.serializers.LongSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.UUIDSerializer;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class DynamicCompositeTest {
    @Test
    public void testComposite() {
        DynamicComposite dc = new DynamicComposite();
        for (char ch = 'A'; ch < 'Z'; ch++) {
            dc.addComponent(Character.toString(ch), StringSerializer.get());
        }
    }

    @Test
    public void testReversedSerialization() {
        AsciiSerializer asciiSerializer = AsciiSerializer.get();
        BytesArraySerializer bytesArraySerializer = BytesArraySerializer.get();
        IntegerSerializer integerSerializer = IntegerSerializer.get();
        LongSerializer longSerializer = LongSerializer.get();
        StringSerializer stringSerializer = StringSerializer.get();
        UUIDSerializer uuidSerializer = UUIDSerializer.get();
        DynamicComposite dc = new DynamicComposite();
        final String string = "test";
        final byte[] bytes = new byte[]{ 0 };
        final int intValue = 1;
        final long longValue = 1L;
        final UUID uuid = UUID.randomUUID();
        dc.addComponent(string, asciiSerializer, getReversed(asciiSerializer));
        dc.addComponent(bytes, bytesArraySerializer, getReversed(bytesArraySerializer));
        dc.addComponent(intValue, integerSerializer, getReversed(integerSerializer));
        dc.addComponent(longValue, longSerializer, getReversed(longSerializer));
        dc.addComponent(string, stringSerializer, getReversed(stringSerializer));
        dc.addComponent(uuid, uuidSerializer, getReversed(uuidSerializer));
        // serialize to bytes
        ByteBuffer buff = dc.serialize();
        // de-serialize
        DynamicComposite read = DynamicComposite.fromByteBuffer(buff);
        Assert.assertEquals(6, read.size());
        Assert.assertEquals(string, read.getComponent(0).getValue(asciiSerializer));
        Assert.assertArrayEquals(bytes, ((byte[]) (read.getComponent(1).getValue(bytesArraySerializer))));
        Assert.assertEquals(intValue, read.getComponent(2).getValue(integerSerializer));
        Assert.assertEquals(longValue, read.getComponent(3).getValue(longSerializer));
        Assert.assertEquals(string, read.getComponent(4).getValue(stringSerializer));
        Assert.assertEquals(uuid, read.getComponent(5).getValue(uuidSerializer));
    }
}

