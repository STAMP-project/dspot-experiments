package com.alibaba.json.bvt.util;


import com.alibaba.fastjson.util.IOUtils;
import java.io.StringReader;
import junit.framework.TestCase;


public class IOUtilsTest extends TestCase {
    public void test_readAll() throws Exception {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < (1024 * 64); ++i) {
            buf.append("a");
        }
        StringReader reader = new StringReader(buf.toString());
        IOUtils.readAll(reader);
    }
}

