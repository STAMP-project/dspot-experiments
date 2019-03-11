package com.alibaba.json.bvt.serializer;


import SerializerFeature.QuoteFieldNames;
import SerializerFeature.UseSingleQuotes;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.math.BigDecimal;
import junit.framework.TestCase;
import org.junit.Assert;


public class SerializeWriterTest_5 extends TestCase {
    public void test_0() throws Exception {
        SerializeWriter out = new SerializeWriter(1);
        out.config(QuoteFieldNames, true);
        out.writeFieldValue(',', "name", ((Enum) (null)));
        Assert.assertEquals(",\"name\":null", out.toString());
    }

    public void test_1() throws Exception {
        SerializeWriter out = new SerializeWriter(1);
        out.config(QuoteFieldNames, true);
        out.writeFieldValue(',', "name", ((BigDecimal) (null)));
        Assert.assertEquals(",\"name\":null", out.toString());
    }

    public void test_2() throws Exception {
        SerializeWriter out = new SerializeWriter(1);
        out.config(QuoteFieldNames, true);
        out.writeFieldValue(',', "name", ((String) (null)));
        Assert.assertEquals(",\"name\":null", out.toString());
    }

    public void test_3() throws Exception {
        SerializeWriter out = new SerializeWriter(1);
        out.config(QuoteFieldNames, true);
        out.config(UseSingleQuotes, true);
        out.writeFieldValue(',', "name", ((String) (null)));
        Assert.assertEquals(",'name':null", out.toString());
    }

    public void test_4() throws Exception {
        SerializeWriter out = new SerializeWriter(1);
        out.config(QuoteFieldNames, true);
        out.config(UseSingleQuotes, true);
        out.writeFieldValue(',', "name", ((String) (null)));
        Assert.assertEquals(",'name':null", out.toString());
    }
}

