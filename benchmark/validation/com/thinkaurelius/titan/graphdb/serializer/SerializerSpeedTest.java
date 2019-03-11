package com.thinkaurelius.titan.graphdb.serializer;


import com.thinkaurelius.titan.graphdb.serializer.attributes.TClass1;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TClass1Serializer;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TClass2;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TClass2Serializer;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TEnum;
import com.thinkaurelius.titan.graphdb.serializer.attributes.TEnumSerializer;
import com.thinkaurelius.titan.testcategory.PerformanceTests;
import com.thinkaurelius.titan.testutil.JUnitBenchmarkProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;


@Category({ PerformanceTests.class })
public class SerializerSpeedTest extends SerializerTestCommon {
    @Rule
    public TestRule benchmark = JUnitBenchmarkProvider.get();

    @Test
    public void performanceTestStringSerialization() {
        int runs = 100000;
        for (int i = 0; i < runs; i++) {
            multipleStringWrite();
        }
    }

    @Test
    public void performanceTestObjectSerialization() {
        serialize.registerClass(2, TClass1.class, new TClass1Serializer());
        serialize.registerClass(80342, TClass2.class, new TClass2Serializer());
        serialize.registerClass(999, TEnum.class, new TEnumSerializer());
        int runs = 1000000;
        for (int i = 0; i < runs; i++) {
            objectWriteRead();
        }
    }
}

