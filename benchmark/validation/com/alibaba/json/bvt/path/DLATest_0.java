package com.alibaba.json.bvt.path;


import Feature.IgnoreAutoType;
import Feature.OrderedField;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.util.IOUtils;
import java.io.InputStream;
import java.io.InputStreamReader;
import junit.framework.TestCase;


public class DLATest_0 extends TestCase {
    public void test_dla() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("json/dla_01.json");
        InputStreamReader reader = new InputStreamReader(is);
        String json = IOUtils.readAll(reader);
        Object object = JSON.parse(json, IgnoreAutoType, OrderedField);
        Object result = JSONPath.eval(object, "$..elapsedTime");
        TestCase.assertEquals("[\"1.48s\",1031,1031,1005,1005,1011,1011]", JSON.toJSONString(result));
        Object result2 = JSONPath.eval(object, "$..self");
        TestCase.assertEquals("[\"http://172.17.246.55:10001/v1/query/20181024_040507_3_f32vb\",\"http://172.17.246.55:10001/v1/stage/20181024_040507_3_f32vb.0\",\"http://172.17.246.56:14005/v1/task/20181024_040507_3_f32vb.0.0?shufferNettyServerPort=39524&commandNettyServerPort=37207\",\"http://172.17.246.55:10001/v1/stage/20181024_040507_3_f32vb.1\",\"http://172.17.246.55:14005/v1/task/20181024_040507_3_f32vb.1.0?shufferNettyServerPort=33921&commandNettyServerPort=45121\",\"http://172.17.246.56:14005/v1/task/20181024_040507_3_f32vb.1.1?shufferNettyServerPort=39524&commandNettyServerPort=37207\"]", JSON.toJSONString(result2));
    }

    public void test_dla_extract() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("json/dla_01.json");
        InputStreamReader reader = new InputStreamReader(is);
        String json = IOUtils.readAll(reader);
        Object result = JSONPath.extract(json, "$..elapsedTime");
        TestCase.assertEquals("[\"1.48s\",1031,1031,1005,1005,1011,1011]", JSON.toJSONString(result));
        Object result2 = JSONPath.extract(json, "$..self");
        TestCase.assertEquals("[\"http://172.17.246.55:10001/v1/query/20181024_040507_3_f32vb\",\"http://172.17.246.55:10001/v1/stage/20181024_040507_3_f32vb.0\",\"http://172.17.246.56:14005/v1/task/20181024_040507_3_f32vb.0.0?shufferNettyServerPort=39524&commandNettyServerPort=37207\",\"http://172.17.246.55:10001/v1/stage/20181024_040507_3_f32vb.1\",\"http://172.17.246.55:14005/v1/task/20181024_040507_3_f32vb.1.0?shufferNettyServerPort=33921&commandNettyServerPort=45121\",\"http://172.17.246.56:14005/v1/task/20181024_040507_3_f32vb.1.1?shufferNettyServerPort=39524&commandNettyServerPort=37207\"]", JSON.toJSONString(result2));
    }
}

