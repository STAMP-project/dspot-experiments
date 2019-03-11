package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class Bug_for_yunban extends TestCase {
    public void test_for_issue() throws Exception {
        List<Bug_for_yunban.RelationItem> relationItemList = new LinkedList<Bug_for_yunban.RelationItem>();
        Map<String, String> ext = new HashMap<String, String>();
        ext.put("a", "b");
        ext.put("c", "d");
        Bug_for_yunban.RelationItem relationItem = new Bug_for_yunban.RelationItem();
        relationItem.setExt(ext);
        relationItem.setSourceId("12");
        relationItemList.add(relationItem);
        relationItem = new Bug_for_yunban.RelationItem();
        relationItem.setExt(ext);
        relationItem.setSourceId("55");
        relationItemList.add(relationItem);
        // ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        // String a = JSON.toJSONString(relationItemList, SerializerFeature.WriteClassName);
        String a1 = JSON.toJSONString(relationItemList);
        System.out.println(a1);
        // ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        List<Bug_for_yunban.RelationItem> relationItemList1 = JSON.parseObject(a1, new com.alibaba.fastjson.TypeReference<List<Bug_for_yunban.RelationItem>>() {});
        System.out.print("fdafda");
    }

    public static class RelationItem {
        private String sourceId;

        private Map<String, String> ext;

        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }

        public Map<String, String> getExt() {
            return ext;
        }

        public void setExt(Map<String, String> ext) {
            this.ext = ext;
        }
    }
}

