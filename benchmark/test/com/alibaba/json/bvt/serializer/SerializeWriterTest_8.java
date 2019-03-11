package com.alibaba.json.bvt.serializer;


import SerializerFeature.BrowserCompatible;
import SerializerFeature.QuoteFieldNames;
import SerializerFeature.UseSingleQuotes;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.io.StringWriter;
import java.util.Collections;
import junit.framework.TestCase;


public class SerializeWriterTest_8 extends TestCase {
    public void test_BrowserCompatible() throws Exception {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 1024; ++i) {
            buf.append('a');
        }
        buf.append("??");
        buf.append("\u0000");
        JSON.toJSONString(buf.toString(), BrowserCompatible);
    }

    public void test_writer() throws Exception {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 1024; ++i) {
            buf.append('a');
        }
        buf.append("??");
        buf.append("\u0000");
        StringWriter out = new StringWriter();
        JSON.writeJSONStringTo(buf.toString(), out, BrowserCompatible);
    }

    public void test_singleQuote() throws Exception {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 1024; ++i) {
            buf.append('a');
        }
        buf.append("??");
        buf.append("\u0000");
        SerializeWriter out = new SerializeWriter(new StringWriter());
        try {
            JSONSerializer serializer = new JSONSerializer(out);
            serializer.config(QuoteFieldNames, false);
            serializer.config(UseSingleQuotes, true);
            serializer.write(Collections.singletonMap(buf.toString(), ""));
        } finally {
            out.close();
        }
    }

    public void test_singleQuote_writer() throws Exception {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 1024; ++i) {
            buf.append('a');
        }
        buf.append("??");
        buf.append("\u0000");
        StringWriter out = new StringWriter();
        JSON.writeJSONStringTo(Collections.singletonMap(buf.toString(), ""), out, UseSingleQuotes);
    }
}

