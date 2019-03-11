package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.Locale;
import junit.framework.TestCase;


/**
 * Created by wenshao on 14/03/2017.
 */
public class LocaleFieldTest extends TestCase {
    public void test_local_str() throws Exception {
        LocaleFieldTest.Model model = new LocaleFieldTest.Model();
        model.locale = Locale.CHINA;
        String json = JSON.toJSONString(model);
        JSONObject jsonObject = JSON.parseObject(json);
        jsonObject.toJavaObject(LocaleFieldTest.Model.class);
    }

    public void test_local_obj() throws Exception {
        String json = "{\"locale\":{\"displayCountry\":\"China\",\"displayVariant\":\"\",\"displayLanguage\":\"Chinese\",\"language\":\"zh\",\"displayName\":\"Chinese (China)\",\"variant\":\"\",\"ISO3Language\":\"zho\",\"ISO3Country\":\"CHN\",\"country\":\"CN\"}}";
        JSONObject jsonObject = JSON.parseObject(json);
        LocaleFieldTest.Model model2 = jsonObject.toJavaObject(LocaleFieldTest.Model.class);
        TestCase.assertEquals("CN", model2.locale.getCountry());
        TestCase.assertEquals("zh", model2.locale.getLanguage());
        TestCase.assertEquals(Locale.CHINA.getDisplayCountry(), model2.locale.getDisplayCountry());
    }

    public static class Model {
        public Locale locale;
    }
}

