package com.alibaba.json.bvt.bug.bug201806;


import SerializerFeature.WriteNullStringAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import junit.framework.TestCase;


public class Bug_for_weiqiang extends TestCase {
    public void test_for_bug() throws Exception {
        SerializeWriter sw = new SerializeWriter();
        sw.config(WriteNullStringAsEmpty, Boolean.TRUE);
        JSONSerializer js = new JSONSerializer(sw);
        js.write(JSON.parseObject("{'operator':null, 'status':1}"));
        System.out.println(js);
        String json2 = JSON.toJSONString(JSON.parseObject("{'operator':null, 'status':1}"), WriteNullStringAsEmpty);
        System.out.println(json2);
    }
}

