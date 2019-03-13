package com.alibaba.json.bvt.parser.deser;


import SerializerFeature.PrettyFormat;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created By maxiaoyao
 * Date: 2017/10/8
 * Time: ??10:52
 */
public class FieldSerializerTest4 {
    @Test
    public void testPattern() {
        FieldSerializerTest4.Result<List> listResult = new FieldSerializerTest4.Result<List>(Lists.newArrayList());
        FieldSerializerTest4.Result<Boolean> booleanResult = new FieldSerializerTest4.Result<Boolean>(null);
        String listJson = JSON.toJSONString(listResult, PrettyFormat);
        String booleanJson = JSON.toJSONString(booleanResult, PrettyFormat, WriteNullListAsEmpty);
        Assert.assertEquals("{\n\t\"data\":[]\n}", listJson);
        Assert.assertEquals("{\n\t\n}", booleanJson);
    }

    private static class Result<T> {
        private T data;

        public Result(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }
}

