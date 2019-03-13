package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.json.bvtVO.wuqi.InstanceSchema;
import com.alibaba.json.bvtVO.wuqi.Result;
import com.alibaba.json.bvtVO.wuqi.SchemaResult;
import java.util.Arrays;
import junit.framework.TestCase;


/**
 * Created by wenshao on 01/04/2017.
 */
public class WuqiTest extends TestCase {
    public void test_for_wuqi() throws Exception {
        SchemaResult schemaResult = new SchemaResult();
        schemaResult.setCode(1001);
        schemaResult.setMassage("success");
        InstanceSchema instanceSchema = new InstanceSchema();
        instanceSchema.setCreated(1466692258L);
        instanceSchema.setCycleType(0);
        instanceSchema.setDefaultValue("-1");
        instanceSchema.setFieldBaseType("string");
        instanceSchema.setFieldComment("?????????");
        instanceSchema.setFieldIndexed(1);
        instanceSchema.setFieldName("NormalPriceTag_ws");
        instanceSchema.setFieldStored(1);
        instanceSchema.setFieldTag(0);
        instanceSchema.setFieldType("text_ws");
        instanceSchema.setId(1317);
        instanceSchema.setInstanceName("xitem");
        instanceSchema.setIsDeleted(0);
        instanceSchema.setIsTagField(1);
        instanceSchema.setUpdated(1466692258L);
        schemaResult.setData(Arrays.asList(instanceSchema));
        Result result = new Result();
        result.setData(schemaResult);
        String jsonStr = JSON.toJSONString(result, WriteClassName);
        TestCase.assertEquals("{\"@type\":\"com.alibaba.json.bvtVO.wuqi.Result\",\"data\":{\"@type\":\"com.alibaba.json.bvtVO.wuqi.SchemaResult\",\"code\":1001,\"data\":[{\"created\":1466692258,\"cycleType\":0,\"defaultValue\":\"-1\",\"fieldBaseType\":\"string\",\"fieldComment\":\"\u666e\u901a\u5546\u54c1\u4ef7\u683c\u5e26\u6807\u7b7e\",\"fieldIndexed\":1,\"fieldName\":\"NormalPriceTag_ws\",\"fieldStored\":1,\"fieldTag\":0,\"fieldType\":\"text_ws\",\"id\":1317,\"instanceName\":\"xitem\",\"isDeleted\":0,\"isTagField\":1,\"updated\":1466692258}],\"extra\":[],\"massage\":\"success\"}}", jsonStr);
    }
}

