package com.alibaba.json.bvt.serializer;


import SerializerFeature.QuoteFieldNames;
import SerializerFeature.UseSingleQuotes;
import com.alibaba.fastjson.serializer.SerializeWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class SerializeWriterTest_6 extends TestCase {
    public void test_0() throws Exception {
        SerializeWriter out = new SerializeWriter(1);
        out.config(QuoteFieldNames, true);
        out.config(UseSingleQuotes, true);
        out.writeFieldValue(',', "name", ((Enum) (null)));
        Assert.assertEquals(",'name':null", out.toString());
    }
}

