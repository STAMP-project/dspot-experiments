package org.hswebframework.web.organizational.authorization.simple;


import SerializerFeature.PrettyFormat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.hswebframework.web.Maps;
import org.hswebframework.web.bean.FastBeanCopier;
import org.junit.Test;


public class SimplePersonnelAuthenticationBuilderTest {
    @Test
    public void test() {
        JSONObject auth = new JSONObject();
        auth.put("personnel", Maps.buildMap().put("id", "1234").put("name", "234").get());
        auth.put("orgIds", JSON.parseArray("[{\"value\":\"123\",\"children\":[{\"value\":\"234\"}]}]"));
        auth.put("positions", JSON.parseArray(("[{\"id\":\"1234\"," + "\"department\":{\"id\":\"1234\",\"org\":{\"id\":\"234\",\"district\":{\"id\":\"test\"}}}}]")));
        SimplePersonnelAuthentication authorization = SimplePersonnelAuthorizationBuilder.fromJson(auth.toJSONString());
        Object json = JSON.toJSON(authorization);
        System.out.println(JSON.toJSONString(json, PrettyFormat));
        System.out.println(JSON.toJSONString(FastBeanCopier.copy(authorization, new JSONObject())).equals(json.toString()));
    }
}

