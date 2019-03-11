package com.alibaba.json.bvt;


import SerializeConfig.globalInstance;
import SerializerFeature.QuoteFieldNames;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import java.util.Collections;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/01/2017.
 */
public class UnQuoteFieldNamesTest extends TestCase {
    public void test_for_issue() throws Exception {
        Map map = Collections.singletonMap("value", 123);
        String json = JSON.toJSONString(map, globalInstance, new SerializeFilter[0], null, ((JSON.DEFAULT_GENERATE_FEATURE) & (~(QuoteFieldNames.mask))));
        TestCase.assertEquals("{value:123}", json);
    }
}

