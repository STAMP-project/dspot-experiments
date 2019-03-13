package cn.hutool.json;


import JSONNull.NULL;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.test.bean.JSONBean;
import cn.hutool.json.test.bean.Seq;
import cn.hutool.json.test.bean.UserA;
import cn.hutool.json.test.bean.UserB;
import cn.hutool.json.test.bean.UserWithMap;
import cn.hutool.json.test.bean.report.CaseReport;
import cn.hutool.json.test.bean.report.StepReport;
import cn.hutool.json.test.bean.report.SuiteReport;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * JSONObject????
 *
 * @author Looly
 */
public class JSONObjectTest {
    @Test
    public void toStringTest2() {
        String str = "{\"test\":\"\u5173\u4e8e\u5f00\u5c552018\u5e74\u5ea6\u201c\u6587\u660e\u96c6\u4f53\u201d\u3001\u201c\u6587\u660e\u804c\u5de5\u201d\u8bc4\u9009\u8868\u5f70\u6d3b\u52a8\u7684\u901a\u77e5\"}";
        JSONObject json = new JSONObject(str);
        Assert.assertEquals(str, json.toString());
    }

    @Test
    public void putAllTest() {
        JSONObject json1 = JSONUtil.createObj();
        json1.put("a", "value1");
        json1.put("b", "value2");
        json1.put("c", "value3");
        json1.put("d", true);
        JSONObject json2 = JSONUtil.createObj();
        json2.put("a", "value21");
        json2.put("b", "value22");
        // putAll???????key?????a,b??key?????c????
        json1.putAll(json2);
        Assert.assertEquals(json1.get("a"), "value21");
        Assert.assertEquals(json1.get("b"), "value22");
        Assert.assertEquals(json1.get("c"), "value3");
    }

    @Test
    public void parseStringTest() {
        String jsonStr = "{\"b\":\"value2\",\"c\":\"value3\",\"a\":\"value1\", \"d\": true, \"e\": null}";
        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
        Assert.assertEquals(jsonObject.get("a"), "value1");
        Assert.assertEquals(jsonObject.get("b"), "value2");
        Assert.assertEquals(jsonObject.get("c"), "value3");
        Assert.assertEquals(jsonObject.get("d"), true);
        Assert.assertTrue(jsonObject.containsKey("e"));
        Assert.assertEquals(jsonObject.get("e"), NULL);
    }

    @Test
    public void parseStringTest2() {
        String jsonStr = "{\"file_name\":\"RMM20180127009_731.000\",\"error_data\":\"201121151350701001252500000032 18973908335 18973908335 13601893517 201711211700152017112115135420171121 6594000000010100000000000000000000000043190101701001910072 100001100 \",\"error_code\":\"F140\",\"error_info\":\"\u6700\u65e9\u53d1\u9001\u65f6\u95f4\u683c\u5f0f\u9519\u8bef\uff0c\u8be5\u5b57\u6bb5\u53ef\u4ee5\u4e3a\u7a7a\uff0c\u5f53\u4e0d\u4e3a\u7a7a\u65f6\u6b63\u786e\u586b\u5199\u683c\u5f0f\u4e3a\u201cYYYYMMDDHHMISS\u201d\",\"app_name\":\"inter-pre-check\"}";
        JSONObject json = new JSONObject(jsonStr);
        Assert.assertEquals("F140", json.getStr("error_code"));
        Assert.assertEquals("????????????????????????????????YYYYMMDDHHMISS?", json.getStr("error_info"));
    }

    @Test
    public void parseStringTest3() {
        String jsonStr = "{\"test\":\"\u4f53\u201d\u3001\u201c\u6587\"}";
        JSONObject json = new JSONObject(jsonStr);
        Assert.assertEquals("?????", json.getStr("test"));
    }

    @Test
    public void toBeanTest() {
        JSONObject subJson = JSONUtil.createObj().put("value1", "strValue1").put("value2", "234");
        JSONObject json = // ?????????
        JSONUtil.createObj().put("strValue", "strTest").put("intValue", 123).put("doubleValue", "").put("beanValue", subJson).put("list", JSONUtil.createArray().put("a").put("b")).put("testEnum", "TYPE_A");
        JSONObjectTest.TestBean bean = json.toBean(JSONObjectTest.TestBean.class);
        Assert.assertEquals("a", bean.getList().get(0));
        Assert.assertEquals("b", bean.getList().get(1));
        Assert.assertEquals("strValue1", bean.getBeanValue().getValue1());
        // BigDecimal????
        Assert.assertEquals(new BigDecimal("234"), bean.getBeanValue().getValue2());
        // ??????
        Assert.assertEquals(JSONObjectTest.TestEnum.TYPE_A, bean.getTestEnum());
    }

    @Test
    public void toBeanNullStrTest() {
        JSONObject json = JSONUtil.createObj().put("strValue", "null").put("intValue", 123).put("beanValue", "null").put("list", JSONUtil.createArray().put("a").put("b"));
        JSONObjectTest.TestBean bean = json.toBean(JSONObjectTest.TestBean.class);
        // ?JSON?????"null"??????????
        Assert.assertEquals("null", bean.getStrValue());
        // ?JSON?????"null"?Bean???????????ignoreError???????
        Assert.assertEquals(null, bean.getBeanValue());
    }

    @Test
    public void toBeanTest2() {
        UserA userA = new UserA();
        userA.setA("A user");
        userA.setName("{\n\t\"body\":{\n\t\t\"loginId\":\"id\",\n\t\t\"password\":\"pwd\"\n\t}\n}");
        userA.setDate(new Date());
        userA.setSqs(CollectionUtil.newArrayList(new Seq("seq1"), new Seq("seq2")));
        JSONObject json = JSONUtil.parseObj(userA);
        UserA userA2 = json.toBean(UserA.class);
        // ????
        Assert.assertEquals("seq1", userA2.getSqs().get(0).getSeq());
        // ?????????????????
        Assert.assertTrue(StrUtil.isNotBlank(userA2.getName()));
    }

    @Test
    public void toBeanTest3() {
        String jsonStr = "{'data':{'userName':'ak','password': null}}";
        UserWithMap user = JSONUtil.toBean(JSONUtil.parseObj(jsonStr), UserWithMap.class);
        String password = user.getData().get("password");
        Assert.assertTrue(user.getData().containsKey("password"));
        Assert.assertNull(password);
    }

    @Test
    public void toBeanTest4() {
        String json = "{\"data\":{\"b\": \"c\"}}";
        UserWithMap map = JSONUtil.toBean(json, UserWithMap.class);
        Assert.assertEquals("c", map.getData().get("b"));
    }

    @Test
    public void toBeanTest5() {
        String readUtf8Str = ResourceUtil.readUtf8Str("suiteReport.json");
        JSONObject json = JSONUtil.parseObj(readUtf8Str);
        SuiteReport bean = json.toBean(SuiteReport.class);
        // ???
        List<CaseReport> caseReports = bean.getCaseReports();
        CaseReport caseReport = caseReports.get(0);
        Assert.assertNotNull(caseReport);
        // ???
        List<StepReport> stepReports = caseReports.get(0).getStepReports();
        StepReport stepReport = stepReports.get(0);
        Assert.assertNotNull(stepReport);
    }

    @Test
    public void parseBeanTest() {
        UserA userA = new UserA();
        userA.setName("nameTest");
        userA.setDate(new Date());
        userA.setSqs(CollectionUtil.newArrayList(new Seq(null), new Seq("seq2")));
        JSONObject json = JSONUtil.parseObj(userA, false);
        Assert.assertTrue(json.containsKey("a"));
        Assert.assertTrue(json.getJSONArray("sqs").getJSONObject(0).containsKey("seq"));
    }

    @Test
    public void parseBeanTest2() {
        JSONObjectTest.TestBean bean = new JSONObjectTest.TestBean();
        bean.setDoubleValue(111.1);
        bean.setIntValue(123);
        bean.setList(CollUtil.newArrayList("a", "b", "c"));
        bean.setStrValue("strTest");
        bean.setTestEnum(JSONObjectTest.TestEnum.TYPE_B);
        JSONObject json = JSONUtil.parseObj(bean, false);
        // ??????
        Assert.assertEquals("TYPE_B", json.get("testEnum"));
        JSONObjectTest.TestBean bean2 = json.toBean(JSONObjectTest.TestBean.class);
        Assert.assertEquals(bean.toString(), bean2.toString());
    }

    @Test
    public void parseBeanTest3() {
        JSONObject json = JSONUtil.createObj().put("code", 22).put("data", "{\"jobId\": \"abc\", \"videoUrl\": \"http://a.com/a.mp4\"}");
        JSONBean bean = json.toBean(JSONBean.class);
        Assert.assertEquals(22, bean.getCode());
        Assert.assertEquals("abc", bean.getData().getObj("jobId"));
        Assert.assertEquals("http://a.com/a.mp4", bean.getData().getObj("videoUrl"));
    }

    @Test
    public void beanTransTest() {
        UserA userA = new UserA();
        userA.setA("A user");
        userA.setName("nameTest");
        userA.setDate(new Date());
        JSONObject userAJson = JSONUtil.parseObj(userA);
        UserB userB = JSONUtil.toBean(userAJson, UserB.class);
        Assert.assertEquals(userA.getName(), userB.getName());
        Assert.assertEquals(userA.getDate(), userB.getDate());
    }

    @Test
    public void beanTransTest2() {
        UserA userA = new UserA();
        userA.setA("A user");
        userA.setName("nameTest");
        userA.setDate(DateUtil.parse("2018-10-25"));
        JSONObject userAJson = JSONUtil.parseObj(userA);
        // ???????
        userAJson.setDateFormat("yyyy-MM-dd");
        UserA bean = JSONUtil.toBean(userAJson.toString(), UserA.class);
        Assert.assertEquals(DateUtil.parse("2018-10-25"), bean.getDate());
    }

    @Test
    public void beanTransTest3() {
        JSONObject userAJson = JSONUtil.createObj().put("a", "AValue").put("name", "nameValue").put("date", "08:00:00");
        UserA bean = JSONUtil.toBean(userAJson.toString(), UserA.class);
        Assert.assertEquals(((DateUtil.today()) + " 08:00:00"), DateUtil.date(bean.getDate()).toString());
    }

    @Test
    public void parseFromBeanTest() {
        UserA userA = new UserA();
        userA.setA(null);
        userA.setName("nameTest");
        userA.setDate(new Date());
        JSONObject userAJson = JSONUtil.parseObj(userA);
        Assert.assertFalse(userAJson.containsKey("a"));
        JSONObject userAJsonWithNullValue = JSONUtil.parseObj(userA, false);
        Assert.assertTrue(userAJsonWithNullValue.containsKey("a"));
        Assert.assertTrue(userAJsonWithNullValue.containsKey("sqs"));
    }

    @Test
    public void specialCharTest() {
        String json = "{\"pattern\": \"[abc]\b\u2001\", \"pattern2Json\": {\"patternText\": \"[ab]\\b\"}}";
        JSONObject obj = JSONUtil.parseObj(json);
        Assert.assertEquals("[abc]\\b\\u2001", obj.getStrEscaped("pattern"));
        Assert.assertEquals("{\"patternText\":\"[ab]\\b\"}", obj.getStrEscaped("pattern2Json"));
    }

    @Test
    public void getStrTest() {
        String json = "{\"name\": \"yyb\\nbbb\"}";
        JSONObject jsonObject = JSONUtil.parseObj(json);
        // ????????????
        Assert.assertEquals("yyb\nbbb", jsonObject.getStr("name"));
        // ?????????
        Assert.assertEquals("yyb\\nbbb", jsonObject.getStrEscaped("name"));
    }

    public static enum TestEnum {

        TYPE_A,
        TYPE_B;}

    /**
     * ??Bean
     *
     * @author Looly
     */
    public static class TestBean {
        private String strValue;

        private int intValue;

        private Double doubleValue;

        private JSONObjectTest.subBean beanValue;

        private List<String> list;

        private JSONObjectTest.TestEnum testEnum;

        public String getStrValue() {
            return strValue;
        }

        public void setStrValue(String strValue) {
            this.strValue = strValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public Double getDoubleValue() {
            return doubleValue;
        }

        public void setDoubleValue(Double doubleValue) {
            this.doubleValue = doubleValue;
        }

        public JSONObjectTest.subBean getBeanValue() {
            return beanValue;
        }

        public void setBeanValue(JSONObjectTest.subBean beanValue) {
            this.beanValue = beanValue;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public JSONObjectTest.TestEnum getTestEnum() {
            return testEnum;
        }

        public void setTestEnum(JSONObjectTest.TestEnum testEnum) {
            this.testEnum = testEnum;
        }

        @Override
        public String toString() {
            return ((((((((((("TestBean [strValue=" + (strValue)) + ", intValue=") + (intValue)) + ", doubleValue=") + (doubleValue)) + ", beanValue=") + (beanValue)) + ", list=") + (list)) + ", testEnum=") + (testEnum)) + "]";
        }
    }

    /**
     * ???Bean
     *
     * @author Looly
     */
    public static class subBean {
        private String value1;

        private BigDecimal value2;

        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public BigDecimal getValue2() {
            return value2;
        }

        public void setValue2(BigDecimal value2) {
            this.value2 = value2;
        }

        @Override
        public String toString() {
            return ((("subBean [value1=" + (value1)) + ", value2=") + (value2)) + "]";
        }
    }
}

