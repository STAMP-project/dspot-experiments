package com.alibaba.json.bvt.issue_1300;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/07/2017.
 */
public class Issue1319 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1319.MyTest test = new Issue1319.MyTest(1, Issue1319.MyEnum.Test1);
        String result = JSON.toJSONString(test, WriteClassName);
        System.out.println(result);
        test = JSON.parseObject(result, Issue1319.MyTest.class);
        System.out.println(JSON.toJSONString(test));
        TestCase.assertEquals(Issue1319.MyEnum.Test1, test.getMyEnum());
        TestCase.assertEquals(1, test.value);
    }

    @JSONType(seeAlso = { Issue1319.OtherEnum.class, Issue1319.MyEnum.class })
    interface EnumInterface {}

    @JSONType(typeName = "myEnum")
    enum MyEnum implements Issue1319.EnumInterface {

        Test1,
        Test2;}

    @JSONType(typeName = "other")
    enum OtherEnum implements Issue1319.EnumInterface {

        Other;}

    static class MyTest {
        private int value;

        private Issue1319.EnumInterface myEnum;

        public MyTest() {
        }

        public MyTest(int property, Issue1319.MyEnum enumProperty) {
            this.value = property;
            this.myEnum = enumProperty;
        }

        public int getValue() {
            return value;
        }

        public Issue1319.EnumInterface getMyEnum() {
            return myEnum;
        }

        public void setMyEnum(Issue1319.EnumInterface myEnum) {
            this.myEnum = myEnum;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

