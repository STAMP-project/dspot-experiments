package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class Bug_for_issue_256 extends TestCase {
    public void test_for_issue() throws Exception {
        List<Bug_for_issue_256.AisleDeployInfo> list3 = new ArrayList<Bug_for_issue_256.AisleDeployInfo>();
        Bug_for_issue_256.AisleDeployInfo aisleDeployInfo = new Bug_for_issue_256.AisleDeployInfo();
        aisleDeployInfo.setId(1L);
        aisleDeployInfo.setProvinceArea("3,4,5");
        list3.add(aisleDeployInfo);
        Bug_for_issue_256.AisleDeployInfo aisleDeployInfo1 = new Bug_for_issue_256.AisleDeployInfo();
        aisleDeployInfo1.setId(2L);
        aisleDeployInfo1.setProvinceArea("3,4,5");
        list3.add(aisleDeployInfo1);
        List<Bug_for_issue_256.AisleDeployInfo> list4 = new ArrayList<Bug_for_issue_256.AisleDeployInfo>();
        list4.add(aisleDeployInfo);
        Map<String, List<Bug_for_issue_256.AisleDeployInfo>> map3 = new HashMap<String, List<Bug_for_issue_256.AisleDeployInfo>>();
        map3.put("1", list3);
        map3.put("2", list4);
        String str = JSON.toJSONString(map3);
        Map<String, List<Bug_for_issue_256.AisleDeployInfo>> map1 = JSON.parseObject(str, new com.alibaba.fastjson.TypeReference<Map<String, List<Bug_for_issue_256.AisleDeployInfo>>>() {});
        List<Bug_for_issue_256.AisleDeployInfo> aList = map1.get("1");
        if ((aList != null) && ((aList.size()) > 0)) {
            for (int i = 0; i < (aList.size()); i++) {
                System.out.println(aList.get(i).getId());
            }
        }
    }

    public static class AisleDeployInfo {
        private long id;

        private String provinceArea;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getProvinceArea() {
            return provinceArea;
        }

        public void setProvinceArea(String provinceArea) {
            this.provinceArea = provinceArea;
        }
    }

    public static class Model extends HashMap {}
}

