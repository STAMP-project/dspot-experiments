package com.alibaba.json.bvt.writeClassName;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class WriteClassNameTest_List3 extends TestCase {
    public void test_list() throws Exception {
        WriteClassNameTest_List3.Model model = new WriteClassNameTest_List3.Model();
        List tables = new ArrayList();
        tables.add(new WriteClassNameTest_List3.ExtTable(1001));
        tables.add(new WriteClassNameTest_List3.Table());
        model.setTables(tables);
        String json = JSON.toJSONString(model);
        TestCase.assertEquals("{\"tables\":[{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_List3$ExtTable\",\"id\":1001},{}]}", json);
        WriteClassNameTest_List3.Model model2 = JSON.parseObject(json, WriteClassNameTest_List3.Model.class);
        TestCase.assertEquals(WriteClassNameTest_List3.ExtTable.class, model2.getTables().iterator().next().getClass());
    }

    public static class Model {
        @JSONField(serialzeFeatures = SerializerFeature.WriteClassName)
        private List<? extends WriteClassNameTest_List3.Table> tables;

        public List<? extends WriteClassNameTest_List3.Table> getTables() {
            return tables;
        }

        public void setTables(List<? extends WriteClassNameTest_List3.Table> tables) {
            this.tables = tables;
        }
    }

    public static class Table {}

    public static class ExtTable extends WriteClassNameTest_List3.Table {
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

            WriteClassNameTest_List3.ExtTable extTable = ((WriteClassNameTest_List3.ExtTable) (o));
            return (id) == (extTable.id);
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}

