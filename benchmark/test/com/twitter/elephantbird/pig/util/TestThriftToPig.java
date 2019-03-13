package com.twitter.elephantbird.pig.util;


import DataType.CHARARRAY;
import DataType.INTEGER;
import PhoneType.HOME;
import ThriftToPig.USE_ENUM_ID_CONF_KEY;
import com.twitter.elephantbird.thrift.test.PhoneNumber;
import com.twitter.elephantbird.thrift.test.TestMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.pig.impl.logicalLayer.FrontendException;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.junit.Assert;
import org.junit.Test;


public class TestThriftToPig {
    private static final TupleFactory tupleFactory = TupleFactory.getInstance();

    private static enum TestType {

        THRIFT_TO_PIG,
        BYTES_TO_TUPLE,
        TUPLE_TO_THRIFT;}

    @Test
    public void test() throws Exception {
        tupleTest(TestThriftToPig.TestType.THRIFT_TO_PIG);
        tupleTest(TestThriftToPig.TestType.BYTES_TO_TUPLE);
        tupleTest(TestThriftToPig.TestType.TUPLE_TO_THRIFT);
    }

    // test a list of a struct
    @Test
    public void nestedStructInListTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestRecipe", "name:chararray,ingredients:bag{t:tuple(name:chararray,color:chararray)}");
    }

    // test a set of a struct
    @Test
    public void nestedStructInSetTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestUniqueRecipe", "name:chararray,ingredients:bag{t:tuple(name:chararray,color:chararray)}");
    }

    @Test
    public void stringsInSetTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestNameList", "name:chararray,names:bag{t:tuple(names_tuple:chararray)}");
    }

    @Test
    public void stringsInListTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestNameSet", "name:chararray,names:bag{t:tuple(names_tuple:chararray)}");
    }

    @Test
    public void listInListTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestListInList", "name:chararray,names:bag{t:tuple(names_bag:bag{t:tuple(names_tuple:chararray)})}");
    }

    @Test
    public void setInListTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestSetInList", "name:chararray,names:bag{t:tuple(names_bag:bag{t:tuple(names_tuple:chararray)})}");
    }

    @Test
    public void listInSetTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestListInSet", "name:chararray,names:bag{t:tuple(names_bag:bag{t:tuple(names_tuple:chararray)})})");
    }

    @Test
    public void setInSetTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestSetInSet", "name:chararray,names:bag{t:tuple(names_bag:bag{t:tuple(names_tuple:chararray)})}");
    }

    @Test
    public void testUnionSchema() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestUnion", "stringType:chararray,i32:int,bufferType:bytearray,structType:tuple(first_name:chararray,last_name:chararray),boolType: int");
    }

    @Test
    public void basicMapTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestMap", "name:chararray,names:map[chararray]");
    }

    @Test
    public void listInMapTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestListInMap", "name:chararray,names:map[{(names_tuple:chararray)}]");
    }

    @Test
    public void setInMapTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestSetInMap", "name:chararray,names:map[{(names_tuple:chararray)}]");
    }

    @Test
    public void structInMapTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestStructInMap", "name:chararray,names:map[(name: (first_name: chararray,last_name: chararray),phones: map[chararray])],name_to_id: map[int]");
    }

    @Test
    public void mapInListTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestMapInList", "name:chararray,names:bag{t:tuple(names_tuple:map[chararray])}");
    }

    @Test
    public void mapInSetTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestMapInSet", "name:chararray,names:bag{t:tuple(names_tuple:map[chararray])}");
    }

    @Test
    public void binaryInListMap() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestBinaryInListMap", "count:int,binaryBlobs:bag{t:tuple(binaryBlobs_tuple:map[bytearray])}");
    }

    @Test
    public void exceptionInMapTest() throws FrontendException {
        nestedInListTestHelper("com.twitter.elephantbird.thrift.test.TestExceptionInMap", "name:chararray,names:map[(descreption: chararray)]");
    }

    /**
     * Tests that thrift map field value has no field schema alias.
     *
     * @throws FrontendException
     * 		
     */
    @Test
    public void testMapValueFieldAlias() throws FrontendException {
        ThriftToPig<TestMap> thriftToPig = new ThriftToPig<TestMap>(TestMap.class);
        Schema schema = thriftToPig.toSchema();
        Assert.assertEquals("{name: chararray,names: map[chararray]}", schema.toString());
        Assert.assertNull(schema.getField(1).schema.getField(0).alias);
        schema = ThriftToPig.toSchema(TestMap.class);
        Assert.assertEquals("{name: chararray,names: map[chararray]}", schema.toString());
        Assert.assertNull(schema.getField(1).schema.getField(0).alias);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testSetConversionProperties() throws ExecException {
        PhoneNumber pn = new PhoneNumber();
        pn.setNumber("1234");
        pn.setType(HOME);
        ThriftToPig ttp = ThriftToPig.newInstance(PhoneNumber.class);
        Tuple tuple = ttp.getPigTuple(pn);
        Assert.assertEquals(CHARARRAY, tuple.getType(1));
        Assert.assertEquals(HOME.toString(), tuple.get(1));
        Configuration conf = new Configuration();
        conf.setBoolean(USE_ENUM_ID_CONF_KEY, true);
        ThriftToPig.setConversionProperties(conf);
        tuple = ttp.getPigTuple(pn);
        Assert.assertEquals(INTEGER, tuple.getType(1));
        Assert.assertEquals(HOME.getValue(), tuple.get(1));
    }
}

