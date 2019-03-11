package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 2016/11/13.
 */
public class Issue869_1 extends TestCase {
    public void test_for_issue() throws Exception {
        List<Issue869_1.DoublePoint> doublePointList = new ArrayList<Issue869_1.DoublePoint>();
        {
            Issue869_1.DoublePoint doublePoint = new Issue869_1.DoublePoint();
            doublePoint.startPoint = new Issue869_1.Point(22, 35);
            doublePoint.endPoint = doublePoint.startPoint;
            doublePointList.add(doublePoint);
        }
        {
            Issue869_1.DoublePoint doublePoint = new Issue869_1.DoublePoint();
            doublePoint.startPoint = new Issue869_1.Point(16, 18);
            doublePoint.endPoint = doublePoint.startPoint;
            doublePointList.add(doublePoint);
        }
        String json = JSON.toJSONString(doublePointList);
        TestCase.assertEquals("[{\"endPoint\":{\"x\":22,\"y\":35},\"startPoint\":{\"$ref\":\"$[0].endPoint\"}},{\"endPoint\":{\"x\":16,\"y\":18},\"startPoint\":{\"$ref\":\"$[1].endPoint\"}}]", json);
    }

    public void test_for_issue_parse() throws Exception {
        String text = "[{\"endPoint\":{\"x\":22,\"y\":35},\"startPoint\":{\"$ref\":\"$[0].endPoint\"}},{\"endPoint\":{\"$ref\":\"$[1].startPoint\"},\"startPoint\":{\"x\":16,\"y\":18}}]";
        List<Issue869.DoublePoint> doublePointList = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<List<Issue869.DoublePoint>>() {});
        TestCase.assertNotNull(doublePointList.get(0));
        TestCase.assertNotNull(doublePointList.get(1));
        TestCase.assertSame(doublePointList.get(0).startPoint, doublePointList.get(0).endPoint);
        TestCase.assertSame(doublePointList.get(1).startPoint, doublePointList.get(1).endPoint);
    }

    public static class DoublePoint {
        public Issue869_1.Point startPoint;

        public Issue869_1.Point endPoint;
    }

    public static class Point {
        public int x;

        public int y;

        public Issue869_1.Properties properties;

        public Point() {
        }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Properties {
        public String id;

        public String title;
    }
}

