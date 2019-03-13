package com.alibaba.json.bvt.writeClassName;


import Feature.IgnoreAutoType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.LinkedHashSet;
import junit.framework.TestCase;


public class WriteClassNameTest_Set5 extends TestCase {
    public void test_list() throws Exception {
        WriteClassNameTest_Set5.Model model = new WriteClassNameTest_Set5.Model();
        LinkedHashSet tables = new LinkedHashSet();
        tables.add(new WriteClassNameTest_Set5.ExtTable(1001));
        tables.add(new WriteClassNameTest_Set5.Table());
        model.setTables(tables);
        String json = JSON.toJSONString(model);
        TestCase.assertEquals("{\"tables\":[{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_Set5$ExtTable\",\"id\":1001},{}]}", json);
        WriteClassNameTest_Set5.Model model2 = JSON.parseObject(json, WriteClassNameTest_Set5.Model.class);
        TestCase.assertEquals(WriteClassNameTest_Set5.ExtTable.class, model2.getTables().iterator().next().getClass());
        JSONObject jsonObject = JSON.parseObject(json, IgnoreAutoType);
        TestCase.assertEquals("{\"tables\":[{\"id\":1001},{}]}", jsonObject.toJSONString());
    }

    public static class Model {
        @JSONField(serialzeFeatures = SerializerFeature.WriteClassName)
        private LinkedHashSet<? extends WriteClassNameTest_Set5.Table> tables;

        public LinkedHashSet<? extends WriteClassNameTest_Set5.Table> getTables() {
            return tables;
        }

        public void setTables(LinkedHashSet<? extends WriteClassNameTest_Set5.Table> tables) {
            this.tables = tables;
        }
    }

    public static class Table {}

    public static class ExtTable extends WriteClassNameTest_Set5.Table {
        public int id;

        public ExtTable() {
        }

        public ExtTable(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            WriteClassNameTest_Set5.ExtTable extTable = ((WriteClassNameTest_Set5.ExtTable) (o));
            return (id) == (extTable.id);
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}

