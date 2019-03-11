package com.intuit.karate.formats.postman;


import com.intuit.karate.FileUtils;
import java.io.InputStream;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by rkumar32 on 7/5/17.
 */
public class RequestBuilderTest {
    private static final Logger logger = LoggerFactory.getLogger(RequestBuilderTest.class);

    @Test
    public void testConverting() {
        InputStream is = getClass().getResourceAsStream("postman-echo-single.postman_collection");
        String json = FileUtils.toString(is);
        List<PostmanItem> items = PostmanUtils.readPostmanJson(json);
        RequestBuilderTest.logger.debug("list: {}", items);
        Assert.assertEquals(1, items.size());
        PostmanItem item = items.get(0);
        PostmanRequest request = item.getRequest();
        RequestBuilder builder = new RequestBuilder();
        Assert.assertEquals("OAuth1.0 Verify Signature", item.getName().trim());
        builder.addUrl(request.getUrl());
        Assert.assertEquals("'https://echo.getpostman.com/oauth1'", builder.getUrl().trim());
        builder.addHeaders(request.getHeaders());
        String authorizationValue = "OAuth oauth_consumer_key=\"RKCGzna7bv9YD57c\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"1442394747\",oauth_nonce=\"UIGipk\",oauth_version=\"1.0\",oauth_signature=\"CaeyGPr2mns1WCq4Cpm5aLvz6Gs=\"";
        Assert.assertEquals((("And header Authorization = '" + authorizationValue) + "'"), builder.getHeaders().trim());
        builder.addMethod(request.getMethod());
        Assert.assertEquals("GET", builder.getMethod().trim());
        builder.addBody(request.getBody());
        RequestBuilderTest.logger.debug(builder.getBody());
    }
}

