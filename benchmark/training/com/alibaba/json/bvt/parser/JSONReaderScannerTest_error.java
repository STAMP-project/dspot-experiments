package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSONReader;
import java.io.IOException;
import java.io.Reader;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderScannerTest_error extends TestCase {
    public void test_e() throws Exception {
        Exception error = null;
        try {
            new JSONReader(new JSONReaderScannerTest_error.MyReader());
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class MyReader extends Reader {
        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            throw new IOException();
        }

        @Override
        public void close() throws IOException {
            // TODO Auto-generated method stub
        }
    }
}

