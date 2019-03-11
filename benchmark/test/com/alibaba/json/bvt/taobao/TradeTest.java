package com.alibaba.json.bvt.taobao;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class TradeTest extends TestCase {
    public void test_cast() {
        String s = "{\"period\":{\"label\":\"\u6700\u8fd130\u5929\",\"value\":\"30d\"},\"data\":{\"gmv\":{\"min\":-2},\"id\":3712312925}}";
        TradeTest.Param param;
        param = JSON.parseObject(s, TradeTest.Param.class);// ?????????OK

        TestCase.assertNotNull(param);
        JSONObject jobj = JSON.parseObject(s);
        param = jobj.toJavaObject(TradeTest.Param.class);
    }

    public static class Param extends TradeTest.BaseObject {
        private static final long serialVersionUID = 5180807854744861824L;

        public TradeTest.TradeParam<Long, Double, Long, Long> data;

        public TradeTest.Pair<String> period;
    }

    public static class TradeParam<ID, G, O, C> extends TradeTest.BaseObject {
        private static final long serialVersionUID = 3201881995156974305L;

        public ID id;

        public TradeTest.Range<G> gmv;

        public TradeTest.Range<O> ordCnt;

        public TradeTest.Range<C> cspu;
    }

    public static class Range<T> extends TradeTest.BaseObject {
        private static final long serialVersionUID = 669395861117027110L;

        public T min;

        public T max;
    }

    public static class BaseObject {}

    public static class Pair<T> extends TradeTest.BaseObject {
        private static final long serialVersionUID = 2840564531670241284L;

        public String label;

        public T value;
    }
}

