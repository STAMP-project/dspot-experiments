package com.twitter.elephantbird.util;


import com.twitter.elephantbird.examples.proto.ThriftFixtures;
import java.io.IOException;
import org.apache.thrift.Fixtures;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import thrift.test.OneOfEach;


public class TestThriftToProto {
    @Test
    public void testThriftToProto() throws IOException, TException {
        OneOfEach ooe = Fixtures.oneOfEach;
        ThriftToProto<OneOfEach, ThriftFixtures.OneOfEach> thriftToProto = ThriftToProto.newInstance(ooe, ThriftFixtures.OneOfEach.newBuilder().build());
        ThriftFixtures.OneOfEach proto = thriftToProto.convert(ooe);
        Assert.assertEquals(ooe.im_true, proto.getImTrue());
        Assert.assertEquals(ooe.im_false, proto.getImFalse());
        Assert.assertEquals(ooe.a_bite, proto.getABite());
        Assert.assertEquals(ooe.integer16, proto.getInteger16());
        Assert.assertEquals(ooe.integer32, proto.getInteger32());
        Assert.assertEquals(ooe.integer64, proto.getInteger64());
        Assert.assertEquals(ooe.double_precision, proto.getDoublePrecision(), 1.0E-5);
        Assert.assertEquals(ooe.some_characters, proto.getSomeCharacters());
        Assert.assertEquals(ooe.zomg_unicode, proto.getZomgUnicode());
        Assert.assertEquals(ooe.what_who, proto.getWhatWho());
        Assert.assertEquals(new String(ooe.getBase64(), "UTF-8"), proto.getBase64().toStringUtf8());
    }
}

