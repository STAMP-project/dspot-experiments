package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import junit.framework.TestCase;
import org.junit.Assert;


public class StackTraceElementTest extends TestCase {
    public void test_stackTrace() throws Exception {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String text = JSON.toJSONString(stackTrace, WriteClassName);
        JSONArray array = ((JSONArray) (JSON.parse(text)));
        for (int i = 0; i < (array.size()); ++i) {
            StackTraceElement element = ((StackTraceElement) (array.get(i)));
            Assert.assertEquals(stackTrace[i].getFileName(), element.getFileName());
            Assert.assertEquals(stackTrace[i].getLineNumber(), element.getLineNumber());
            Assert.assertEquals(stackTrace[i].getClassName(), element.getClassName());
            Assert.assertEquals(stackTrace[i].getMethodName(), element.getMethodName());
        }
    }
}

