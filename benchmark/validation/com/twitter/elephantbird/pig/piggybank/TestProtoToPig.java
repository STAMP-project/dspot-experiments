package com.twitter.elephantbird.pig.piggybank;


import com.google.protobuf.Descriptors.FieldDescriptor;
import com.twitter.data.proto.tutorial.AddressBookProtos.AddressBook;
import com.twitter.data.proto.tutorial.AddressBookProtos.Person;
import com.twitter.elephantbird.pig.util.PigUtil;
import com.twitter.elephantbird.util.TypeRef;
import java.io.IOException;
import java.util.List;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.junit.Assert;
import org.junit.Test;


public class TestProtoToPig {
    private static TupleFactory tf_ = TupleFactory.getInstance();

    @Test
    public void testProtoToPig() throws IOException {
        AddressBook abProto = Fixtures.buildAddressBookProto();
        Tuple abProtoTuple = TestProtoToPig.tf_.newTuple(new org.apache.pig.data.DataByteArray(abProto.toByteArray()));
        ProtobufBytesToTuple abProtoToPig = new ProtobufBytesToTuple(AddressBook.class.getCanonicalName());
        Tuple abTuple = abProtoToPig.exec(abProtoTuple);
        Assert.assertEquals("{(Elephant Bird,123,elephant@bird.com,{(415-999-9999,HOME),(415-666-6666,MOBILE),(415-333-3333,WORK)}),(Elephant Bird,123,elephant@bird.com,{(415-999-9999,HOME),(415-666-6666,MOBILE),(415-333-3333,WORK)})},", abTuple.toDelimitedString(","));
    }

    @Test
    public void testLazyProtoToPig() throws ExecException {
        Person personProto = Fixtures.buildPersonProto();
        Tuple protoTuple = new com.twitter.elephantbird.pig.util.ProtobufTuple(personProto);
        Tuple normalTuple = Fixtures.buildPersonTuple();
        List<FieldDescriptor> fieldDescs = personProto.getDescriptorForType().getFields();
        TypeRef<Person> typeRef = PigUtil.getProtobufTypeRef(Person.class.getName());
        Tuple projectedTuple = new com.twitter.elephantbird.pig.util.ProjectedProtobufTupleFactory<Person>(typeRef, TestProtoToPig.evenFields(fieldDescs)).newTuple(personProto);
        int idx = 0;
        for (FieldDescriptor fd : fieldDescs) {
            // Skipping data bags. Data bags actually work; it' just our fixture is not good for this,
            // since it tests "default value" functionality by leaving some elements as null, expecting
            // protobuf conversion to fill the nulls in. Which is what happens. But that means converting back
            // gives us non-null fields, which are not equal to the null fields...
            if ((normalTuple.get(fd.getIndex())) instanceof DataBag) {
                continue;
            }
            Assert.assertEquals(protoTuple.get(fd.getIndex()), normalTuple.get(fd.getIndex()));
            if ((idx % 2) == 0) {
                Assert.assertEquals(projectedTuple.get(((fd.getIndex()) / 2)), normalTuple.get(fd.getIndex()));
            }
            idx++;
        }
    }
}

