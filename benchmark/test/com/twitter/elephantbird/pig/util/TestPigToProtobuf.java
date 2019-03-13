package com.twitter.elephantbird.pig.util;


import DescriptorProtos.FieldDescriptorProto.Type;
import Descriptors.Descriptor;
import Descriptors.DescriptorValidationException;
import Descriptors.FieldDescriptor;
import Descriptors.FieldDescriptor.Type.BOOL;
import Descriptors.FieldDescriptor.Type.BYTES;
import Descriptors.FieldDescriptor.Type.DOUBLE;
import Descriptors.FieldDescriptor.Type.FLOAT;
import Descriptors.FieldDescriptor.Type.INT32;
import Descriptors.FieldDescriptor.Type.INT64;
import Descriptors.FieldDescriptor.Type.STRING;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.twitter.data.proto.tutorial.AddressBookProtos.Person;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.pig.data.DataType;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.apache.pig.impl.util.Pair;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verifies that <code>PigToProtobuf</code> converts from Pig schemas to Protobuf descriptors properly
 *
 * @author billg
 */
public class TestPigToProtobuf {
    @Test
    public void testConvertValidTypes() throws DescriptorValidationException {
        Schema schema = new Schema();
        schema.add(new Schema.FieldSchema("chararray", DataType.CHARARRAY));
        schema.add(new Schema.FieldSchema("bytearray", DataType.BYTEARRAY));
        schema.add(new Schema.FieldSchema("boolean", DataType.BOOLEAN));
        schema.add(new Schema.FieldSchema("integer", DataType.INTEGER));
        schema.add(new Schema.FieldSchema("long", DataType.LONG));
        schema.add(new Schema.FieldSchema("float", DataType.FLOAT));
        schema.add(new Schema.FieldSchema("double", DataType.DOUBLE));
        Descriptors.Descriptor descriptor = PigToProtobuf.schemaToProtoDescriptor(new org.apache.pig.ResourceSchema(schema));
        Assert.assertEquals("Incorrect data size", 7, descriptor.getFields().size());
        Iterator<Descriptors.FieldDescriptor> fieldIterator = descriptor.getFields().iterator();
        TestPigToProtobuf.assetFieldDescriptor(fieldIterator.next(), "chararray", STRING);
        TestPigToProtobuf.assetFieldDescriptor(fieldIterator.next(), "bytearray", BYTES);
        TestPigToProtobuf.assetFieldDescriptor(fieldIterator.next(), "boolean", BOOL);
        TestPigToProtobuf.assetFieldDescriptor(fieldIterator.next(), "integer", INT32);
        TestPigToProtobuf.assetFieldDescriptor(fieldIterator.next(), "long", INT64);
        TestPigToProtobuf.assetFieldDescriptor(fieldIterator.next(), "float", FLOAT);
        TestPigToProtobuf.assetFieldDescriptor(fieldIterator.next(), "double", DOUBLE);
    }

    @Test
    public void testConvertExtraFields() throws DescriptorValidationException {
        Schema schema = new Schema();
        schema.add(new Schema.FieldSchema("chararray", DataType.CHARARRAY));
        schema.add(new Schema.FieldSchema("bytearray", DataType.BYTEARRAY));
        List<Pair<String, DescriptorProtos.FieldDescriptorProto.Type>> extraFields = new ArrayList<Pair<String, DescriptorProtos.FieldDescriptorProto.Type>>();
        extraFields.add(new Pair<String, DescriptorProtos.FieldDescriptorProto.Type>("extra_string", Type.TYPE_STRING));
        extraFields.add(new Pair<String, DescriptorProtos.FieldDescriptorProto.Type>("extra_int", Type.TYPE_INT32));
        Descriptors.Descriptor descriptor = PigToProtobuf.schemaToProtoDescriptor(new org.apache.pig.ResourceSchema(schema), extraFields);
        Assert.assertEquals("Incorrect data size", 4, descriptor.getFields().size());
        Iterator<Descriptors.FieldDescriptor> fieldIterator = descriptor.getFields().iterator();
        TestPigToProtobuf.assetFieldDescriptor(fieldIterator.next(), "chararray", STRING);
        TestPigToProtobuf.assetFieldDescriptor(fieldIterator.next(), "bytearray", BYTES);
        TestPigToProtobuf.assetFieldDescriptor(fieldIterator.next(), "extra_string", STRING);
        TestPigToProtobuf.assetFieldDescriptor(fieldIterator.next(), "extra_int", INT32);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertInvalidTypeBag() throws DescriptorValidationException {
        Schema schema = new Schema();
        schema.add(new Schema.FieldSchema("bag", DataType.BAG));
        PigToProtobuf.schemaToProtoDescriptor(new org.apache.pig.ResourceSchema(schema));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertInvalidTypeMap() throws DescriptorValidationException {
        Schema schema = new Schema();
        schema.add(new Schema.FieldSchema("map", DataType.MAP));
        PigToProtobuf.schemaToProtoDescriptor(new org.apache.pig.ResourceSchema(schema));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertInvalidTypeTuple() throws DescriptorValidationException {
        Schema schema = new Schema();
        schema.add(new Schema.FieldSchema("tuple", DataType.TUPLE));
        PigToProtobuf.schemaToProtoDescriptor(new org.apache.pig.ResourceSchema(schema));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertInvalidSchemaEmpty() throws DescriptorValidationException {
        PigToProtobuf.schemaToProtoDescriptor(new org.apache.pig.ResourceSchema(new Schema()));
    }

    @Test
    public void testPerson() {
        Person expected = TestPigToProtobuf.personMessage("Joe", 1, null, "123-456-7890", "HOME");
        Person actual = PigToProtobuf.tupleToMessage(Person.class, TestPigToProtobuf.personTuple("Joe", 1, null, "123-456-7890", "HOME"));
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = RuntimeException.class)
    public void testPersonBadEnumValue() {
        PigToProtobuf.tupleToMessage(Person.class, TestPigToProtobuf.personTuple("Joe", 1, null, "123-456-7890", "ASDF"));
    }
}

