package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 2016/10/19.
 */
public class Issue869 extends TestCase {
    public void test_for_issue() throws Exception {
        List<Issue869.DoublePoint> doublePointList = new ArrayList<Issue869.DoublePoint>();
        {
            Issue869.DoublePoint doublePoint = new Issue869.DoublePoint();
            doublePoint.startPoint = new Point(22, 35);
            doublePoint.endPoint = doublePoint.startPoint;
            doublePointList.add(doublePoint);
        }
        {
            Issue869.DoublePoint doublePoint = new Issue869.DoublePoint();
            doublePoint.startPoint = new Point(16, 18);
            doublePoint.endPoint = doublePoint.startPoint;
            doublePointList.add(doublePoint);
        }
        String json = JSON.toJSONString(doublePointList);
        TestCase.assertEquals("[{\"endPoint\":{\"x\":22,\"y\":35},\"startPoint\":{\"x\":22,\"y\":35}},{\"endPoint\":{\"x\":16,\"y\":18},\"startPoint\":{\"x\":16,\"y\":18}}]", json);
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
        public Point startPoint;

        public Point endPoint;
    }
}

