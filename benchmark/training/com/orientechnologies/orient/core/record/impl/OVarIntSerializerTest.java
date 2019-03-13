package com.orientechnologies.orient.core.record.impl;


import com.orientechnologies.orient.core.serialization.serializer.record.binary.BytesContainer;
import com.orientechnologies.orient.core.serialization.serializer.record.binary.OVarIntSerializer;
import org.junit.Assert;
import org.junit.Test;


public class OVarIntSerializerTest {
    @Test
    public void serializeZero() {
        BytesContainer bytes = new BytesContainer();
        OVarIntSerializer.write(bytes, 0);
        bytes.offset = 0;
        Assert.assertEquals(OVarIntSerializer.readAsLong(bytes), 0L);
    }

    @Test
    public void serializeNegative() {
        BytesContainer bytes = new BytesContainer();
        OVarIntSerializer.write(bytes, (-20432343));
        bytes.offset = 0;
        Assert.assertEquals(OVarIntSerializer.readAsLong(bytes), (-20432343L));
    }

    @Test
    public void serializePositive() {
        BytesContainer bytes = new BytesContainer();
        OVarIntSerializer.write(bytes, 20432343);
        bytes.offset = 0;
        Assert.assertEquals(OVarIntSerializer.readAsLong(bytes), 20432343L);
    }

    @Test
    public void serializeCrazyPositive() {
        BytesContainer bytes = new BytesContainer();
        OVarIntSerializer.write(bytes, 16238);
        bytes.offset = 0;
        Assert.assertEquals(OVarIntSerializer.readAsLong(bytes), 16238L);
    }

    @Test
    public void serializePosition() {
        BytesContainer bytes = new BytesContainer();
        bytes.offset = OVarIntSerializer.write(bytes, 16238);
        Assert.assertEquals(OVarIntSerializer.readAsLong(bytes), 16238L);
    }

    @Test
    public void serializeMaxLong() {
        BytesContainer bytes = new BytesContainer();
        bytes.offset = OVarIntSerializer.write(bytes, Long.MAX_VALUE);
        Assert.assertEquals(OVarIntSerializer.readAsLong(bytes), Long.MAX_VALUE);
    }

    @Test
    public void serializeMinLong() {
        BytesContainer bytes = new BytesContainer();
        bytes.offset = OVarIntSerializer.write(bytes, Long.MIN_VALUE);
        Assert.assertEquals(OVarIntSerializer.readAsLong(bytes), Long.MIN_VALUE);
    }
}

