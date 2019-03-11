package com.alibaba.json.bvt.writeAsArray;


import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.parser.Feature;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_long_stream_public extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_long_stream_public.VO vo = new WriteAsArray_long_stream_public.VO();
        vo.setId(123);
        vo.setName("wenshao");
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[123,\"wenshao\"]", text);
        JSONReader reader = new JSONReader(new StringReader(text), Feature.SupportArrayToBean);
        WriteAsArray_long_stream_public.VO vo2 = reader.readObject(WriteAsArray_long_stream_public.VO.class);
        Assert.assertEquals(vo.getId(), vo2.getId());
        Assert.assertEquals(vo.getName(), vo2.getName());
        reader.close();
    }

    public static class VO {
        private long id;

        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

