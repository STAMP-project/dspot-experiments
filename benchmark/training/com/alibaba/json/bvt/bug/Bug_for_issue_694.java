package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_694 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_694.Root root = JSON.parseObject("{\"entity\":{\"isBootomPluginClickable\":true,\"isMainPoi\":true}}", Bug_for_issue_694.Root.class);
        Assert.assertTrue(root.entity.isBootomPluginClickable);
        Assert.assertTrue(root.entity.isMainPoi);
    }

    public static class Root {
        public Bug_for_issue_694.Root.GSMapItemBIZEntity entity;

        class GSMapItemBIZEntity {
            protected boolean isBootomPluginClickable = false;// ???????????? ????????


            protected boolean isMainPoi = false;

            public boolean isBootomPluginClickable() {
                return isBootomPluginClickable;
            }

            public void setBootomPluginClickable(boolean bootomPluginClickable) {
                isBootomPluginClickable = bootomPluginClickable;
            }

            public boolean isMainPoi() {
                return isMainPoi;
            }

            public void setMainPoi(boolean mainPoi) {
                isMainPoi = mainPoi;
            }
        }
    }
}

