package com.twitter.elephantbird.pig.piggybank;


import com.google.protobuf.Message;
import com.twitter.data.proto.tutorial.AddressBookProtos.AddressBook;
import com.twitter.elephantbird.pig.util.PigToProtobuf;
import com.twitter.elephantbird.pig.util.ThriftToPig;
import com.twitter.elephantbird.util.ThriftToProto;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import thrift.test.OneOfEach;

import static org.apache.thrift.Fixtures.oneOfEach;


public class TestPigToProto {
    @Test
    public void testPigToProto() throws ExecException, TException {
        Tuple abTuple = Fixtures.buildAddressBookTuple();
        Message proto = PigToProtobuf.tupleToMessage(AddressBook.newBuilder(), abTuple);
        Assert.assertEquals(Fixtures.buildAddressBookProto(), proto);
        // test with OneOfEach.
        OneOfEach thrift_ooe = oneOfEach;
        com.twitter.elephantbird.examples.proto.ThriftFixtures.OneOfEach proto_ooe = ThriftToProto.newInstance(thrift_ooe, com.twitter.elephantbird.examples.proto.ThriftFixtures.OneOfEach.newBuilder().build()).convert(thrift_ooe);
        // tuple from Thrift ooe :
        Tuple tuple_ooe = ThriftToPig.newInstance(com.twitter.elephantbird.examples.proto.ThriftFixtures.OneOfEach.class).getPigTuple(thrift_ooe);
        Assert.assertEquals(proto_ooe, PigToProtobuf.tupleToMessage(com.twitter.elephantbird.examples.proto.ThriftFixtures.OneOfEach.class, tuple_ooe));
    }
}

