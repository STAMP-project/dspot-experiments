package com.github.scribejava.core.oauth;


import Base64.Encoder;
import OAuthConstants.ACCESS_TOKEN;
import OAuthConstants.HEADER;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.java8.Base64;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuth2Authorization;
import com.github.scribejava.core.model.OAuthConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class OAuth20ServiceTest {
    private final Encoder base64Encoder = Base64.getEncoder();

    @Test
    public void shouldProduceCorrectRequestSync() throws IOException, InterruptedException, ExecutionException {
        final OAuth20Service service = new ServiceBuilder("your_api_key").apiSecret("your_api_secret").build(new OAuth20ApiUnit());
        final OAuth2AccessToken token = service.getAccessTokenPasswordGrant("user1", "password1");
        final Gson json = new Gson();
        Assert.assertNotNull(token);
        final Map<String, String> map = json.fromJson(token.getRawResponse(), new OAuth20ServiceTest.TypeTokenImpl().getType());
        Assert.assertEquals(OAuth20ServiceUnit.TOKEN, map.get(ACCESS_TOKEN));
        Assert.assertEquals(OAuth20ServiceUnit.EXPIRES, map.get("expires_in"));
        final String authorize = base64Encoder.encodeToString(String.format("%s:%s", service.getApiKey(), service.getApiSecret()).getBytes(Charset.forName("UTF-8")));
        Assert.assertEquals((((OAuthConstants.BASIC) + " ") + authorize), map.get(HEADER));
        Assert.assertEquals("user1", map.get("query-username"));
        Assert.assertEquals("password1", map.get("query-password"));
        Assert.assertEquals("password", map.get("query-grant_type"));
    }

    @Test
    public void shouldProduceCorrectRequestAsync() throws InterruptedException, ExecutionException {
        final OAuth20Service service = new ServiceBuilder("your_api_key").apiSecret("your_api_secret").build(new OAuth20ApiUnit());
        final OAuth2AccessToken token = service.getAccessTokenPasswordGrantAsync("user1", "password1", null).get();
        final Gson json = new Gson();
        Assert.assertNotNull(token);
        final Map<String, String> map = json.fromJson(token.getRawResponse(), new OAuth20ServiceTest.TypeTokenImpl().getType());
        Assert.assertEquals(OAuth20ServiceUnit.TOKEN, map.get(ACCESS_TOKEN));
        Assert.assertEquals(OAuth20ServiceUnit.EXPIRES, map.get("expires_in"));
        final String authorize = base64Encoder.encodeToString(String.format("%s:%s", service.getApiKey(), service.getApiSecret()).getBytes(Charset.forName("UTF-8")));
        Assert.assertEquals((((OAuthConstants.BASIC) + " ") + authorize), map.get(HEADER));
        Assert.assertEquals("user1", map.get("query-username"));
        Assert.assertEquals("password1", map.get("query-password"));
        Assert.assertEquals("password", map.get("query-grant_type"));
    }

    @Test
    public void testOAuthExtractAuthorization() {
        final OAuth20Service service = new ServiceBuilder("your_api_key").apiSecret("your_api_secret").build(new OAuth20ApiUnit());
        OAuth2Authorization authorization = service.extractAuthorization("https://cl.ex.com/cb?code=SplxlOB&state=xyz");
        Assert.assertEquals("SplxlOB", authorization.getCode());
        Assert.assertEquals("xyz", authorization.getState());
        authorization = service.extractAuthorization("https://cl.ex.com/cb?state=xyz&code=SplxlOB");
        Assert.assertEquals("SplxlOB", authorization.getCode());
        Assert.assertEquals("xyz", authorization.getState());
        authorization = service.extractAuthorization("https://cl.ex.com/cb?key=value&state=xyz&code=SplxlOB");
        Assert.assertEquals("SplxlOB", authorization.getCode());
        Assert.assertEquals("xyz", authorization.getState());
        authorization = service.extractAuthorization("https://cl.ex.com/cb?state=xyz&code=SplxlOB&key=value&");
        Assert.assertEquals("SplxlOB", authorization.getCode());
        Assert.assertEquals("xyz", authorization.getState());
        authorization = service.extractAuthorization("https://cl.ex.com/cb?code=SplxlOB&state=");
        Assert.assertEquals("SplxlOB", authorization.getCode());
        Assert.assertEquals(null, authorization.getState());
        authorization = service.extractAuthorization("https://cl.ex.com/cb?code=SplxlOB");
        Assert.assertEquals("SplxlOB", authorization.getCode());
        Assert.assertEquals(null, authorization.getState());
        authorization = service.extractAuthorization("https://cl.ex.com/cb?code=");
        Assert.assertEquals(null, authorization.getCode());
        Assert.assertEquals(null, authorization.getState());
        authorization = service.extractAuthorization("https://cl.ex.com/cb?code");
        Assert.assertEquals(null, authorization.getCode());
        Assert.assertEquals(null, authorization.getState());
        authorization = service.extractAuthorization("https://cl.ex.com/cb?");
        Assert.assertEquals(null, authorization.getCode());
        Assert.assertEquals(null, authorization.getState());
        authorization = service.extractAuthorization("https://cl.ex.com/cb");
        Assert.assertEquals(null, authorization.getCode());
        Assert.assertEquals(null, authorization.getState());
    }

    private static class TypeTokenImpl extends TypeToken<Map<String, String>> {
        private TypeTokenImpl() {
        }
    }
}

