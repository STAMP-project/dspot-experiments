package com.alibaba.json.bvt.issue_1200;


import SerializerFeature.IgnoreErrorGetter;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 18/06/2017.
 */
public class Issue1272_IgnoreError extends TestCase {
    public void test_for_issue() throws Exception {
        String text = JSON.toJSONString(new Issue1272_IgnoreError.Point(), IgnoreErrorGetter);
        TestCase.assertEquals("{}", text);
    }

    public static class Point {
        private Long userId;

        public long getUserId() {
            return userId;
        }
    }
}

