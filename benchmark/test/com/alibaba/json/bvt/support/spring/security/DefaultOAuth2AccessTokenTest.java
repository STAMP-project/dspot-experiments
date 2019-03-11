package com.alibaba.json.bvt.support.spring.security;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;


public class DefaultOAuth2AccessTokenTest extends TestCase {
    public void test_0() throws Exception {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("123");
        token.setExpiration(new Date());
        String json = JSON.toJSONString(token, WriteClassName);
        DefaultOAuth2AccessToken token2 = ((DefaultOAuth2AccessToken) (JSON.parse(json)));
        TestCase.assertEquals(token.getValue(), token2.getValue());
        TestCase.assertEquals(token.getExpiration(), token2.getExpiration());
    }
}

