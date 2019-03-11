package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.Set;
import junit.framework.TestCase;


/**
 * Created by wenshao on 15/02/2017.
 */
public class Issue_for_jiongxiong extends TestCase {
    public void test_for_jiongxiong() throws Exception {
        JSON.parseObject("{\"groupNames\":[\"a\"]}", Issue_for_jiongxiong.Model.class);
    }

    public static class Model {
        private Set<String> groupNames;

        public Set<String> getGroupNames() {
            return groupNames;
        }

        public void setGroupNames(Set<String> groupNames) {
            this.groupNames = groupNames;
        }
    }
}

