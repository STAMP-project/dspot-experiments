package com.alibaba.json.bvt.writeClassName;


import Feature.IgnoreAutoType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.TestCase;


public class WriteClassNameTest_Map extends TestCase {
    public void test_list() throws Exception {
        WriteClassNameTest_Map.Model model = new WriteClassNameTest_Map.Model();
        Map tables = new LinkedHashMap();
        tables.put("1001", new WriteClassNameTest_Map.ExtTable(1001));
        tables.put("1002", new WriteClassNameTest_Map.Table());
        model.setTables(tables);
        String json = JSON.toJSONString(model);
        TestCase.assertEquals("{\"tables\":{\"1001\":{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_Map$ExtTable\",\"id\":1001},\"1002\":{}}}", json);
        JSONObject jsonObject = JSON.parseObject(json, IgnoreAutoType);
        TestCase.assertEquals("{\"tables\":{\"1002\":{},\"1001\":{\"id\":1001}}}", jsonObject.toJSONString());
        WriteClassNameTest_Map.Model model2 = JSON.parseObject(json, WriteClassNameTest_Map.Model.class);
        TestCase.assertEquals(WriteClassNameTest_Map.ExtTable.class, model2.getTables().get("1001").getClass());
    }

    public static class Model {
        @JSONField(serialzeFeatures = SerializerFeature.WriteClassName)
        private Map<String, ? extends WriteClassNameTest_Map.Table> tables;

        public Map<String, ? extends WriteClassNameTest_Map.Table> getTables() {
            return tables;
        }

        public void setTables(Map<String, ? extends WriteClassNameTest_Map.Table> tables) {
            this.tables = tables;
        }
    }

    public static class Table {}

    public static class ExtTable extends WriteClassNameTest_Map.Table {
        public int id;

        public ExtTable() {
        }

        public ExtTable(int id) {
            this.id = id;
        }
    }
}

