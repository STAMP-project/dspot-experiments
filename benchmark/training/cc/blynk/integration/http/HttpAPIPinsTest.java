package cc.blynk.integration.http;


import ContentType.APPLICATION_JSON;
import JsonParser.MAPPER;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.server.api.http.pojo.EmailPojo;
import cc.blynk.server.api.http.pojo.PushMessagePojo;
import java.util.List;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpAPIPinsTest extends SingleServerInstancePerTest {
    private static CloseableHttpClient httpclient;

    private static String httpsServerUrl;

    // ----------------------------GET METHODS SECTION
    @Test
    public void testGetWithFakeToken() throws Exception {
        HttpGet request = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "dsadasddasdasdasdasdasdas/get/d8"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Invalid token.", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testGetWithWrongPathToken() throws Exception {
        HttpGet request = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/w/d8"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testGetWithWrongPin() throws Exception {
        HttpGet request = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/x8"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Wrong pin format.", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testGetWithNonExistingPin() throws Exception {
        HttpGet request = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/v11"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Requested pin doesn't exist in the app.", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testGetWringPin() throws Exception {
        HttpGet request = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/v256"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Wrong pin format.", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testPutGetNonExistingPin() throws Exception {
        HttpPut put = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/v10"));
        put.setEntity(new StringEntity("[\"100\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(put)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
        HttpGet get = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/v10"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(get)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(1, values.size());
            Assert.assertEquals("100", values.get(0));
        }
    }

    @Test
    public void testMultiPutGetNonExistingPin() throws Exception {
        HttpPut put = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/v10"));
        put.setEntity(new StringEntity("[\"100\", \"101\", \"102\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(put)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
        HttpGet get = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/v10"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(get)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(3, values.size());
            Assert.assertEquals("100", values.get(0));
            Assert.assertEquals("101", values.get(1));
            Assert.assertEquals("102", values.get(2));
        }
    }

    @Test
    public void testMultiPutGetNonExistingPinWithNewMethod() throws Exception {
        HttpPut put = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/v10"));
        put.setEntity(new StringEntity("[\"100\", \"101\", \"102\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(put)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
        HttpGet get = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/v10"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(get)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(3, values.size());
            Assert.assertEquals("100", values.get(0));
            Assert.assertEquals("101", values.get(1));
            Assert.assertEquals("102", values.get(2));
        }
    }

    @Test
    public void testGetTimerExistingPin() throws Exception {
        HttpGet request = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/D0"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(1, values.size());
            Assert.assertEquals("1", values.get(0));
        }
    }

    @Test
    public void testGetWithExistingPin() throws Exception {
        HttpGet request = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/D8"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(1, values.size());
            Assert.assertEquals("0", values.get(0));
        }
        request = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/d1"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(1, values.size());
            Assert.assertEquals("1", values.get(0));
        }
        request = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/d3"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(1, values.size());
            Assert.assertEquals("87", values.get(0));
        }
    }

    @Test
    public void testGetWithExistingEmptyPin() throws Exception {
        HttpGet request = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/a14"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(0, values.size());
        }
    }

    @Test
    public void testGetWithExistingMultiPin() throws Exception {
        HttpGet request = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/a15"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(2, values.size());
            Assert.assertEquals("1", values.get(0));
            Assert.assertEquals("2", values.get(1));
        }
    }

    @Test
    public void testGetForRGBMerge() throws Exception {
        HttpGet request = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/v13"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(3, values.size());
            Assert.assertEquals("60", values.get(0));
            Assert.assertEquals("143", values.get(1));
            Assert.assertEquals("158", values.get(2));
        }
    }

    @Test
    public void testGetForJoystickMerge() throws Exception {
        HttpGet request = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/v14"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(2, values.size());
            Assert.assertEquals("128", values.get(0));
            Assert.assertEquals("129", values.get(1));
        }
    }

    // ----------------------------PUT METHODS SECTION
    @Test
    public void testPutNoContentType() throws Exception {
        HttpPut request = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/d8"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(500, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Unexpected content type. Expecting application/json.", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testPutFakeToken() throws Exception {
        HttpPut request = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "dsadasddasdasdasdasdasdas/update/d8"));
        request.setHeader("Content-Type", APPLICATION_JSON.toString());
        request.setEntity(new StringEntity("[\"100\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Invalid token.", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testPutWithWrongPin() throws Exception {
        HttpPut request = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/x8"));
        request.setHeader("Content-Type", APPLICATION_JSON.toString());
        request.setEntity(new StringEntity("[\"100\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Wrong pin format.", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testPutWithNoWidget() throws Exception {
        HttpPut request = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/v10"));
        request.setHeader("Content-Type", APPLICATION_JSON.toString());
        request.setEntity(new StringEntity("[\"100\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testPutWithNoWidgetNoPinData() throws Exception {
        HttpPut request = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/v10"));
        request.setHeader("Content-Type", APPLICATION_JSON.toString());
        request.setEntity(new StringEntity("[]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
            Assert.assertEquals("No pin for update provided.", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testPutWithNoWidgetMultivalue() throws Exception {
        HttpPut request = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/v10"));
        request.setHeader("Content-Type", APPLICATION_JSON.toString());
        request.setEntity(new StringEntity("[\"100\", \"101\", \"102\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testPutWithLargeValueNotAccepted() throws Exception {
        HttpPut request = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/pin/v10"));
        request.setHeader("Content-Type", APPLICATION_JSON.toString());
        StringBuilder val = new StringBuilder((512 * 1024));
        for (int i = 0; i < ((val.capacity()) / 10); i++) {
            val.append("1234567890");
        }
        val.append("1234567890");
        request.setEntity(new StringEntity((("[\"" + (val.toString())) + "\"]"), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(413, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testPutExtraWithNoWidget() throws Exception {
        HttpPut request = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/extra/pin/v10"));
        request.setHeader("Content-Type", APPLICATION_JSON.toString());
        request.setEntity(new StringEntity("[{\"timestamp\" : 123, \"value\":\"100\"}]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testPutWithExistingPin() throws Exception {
        HttpPut request = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/a14"));
        request.setEntity(new StringEntity("[\"100\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
        HttpGet getRequest = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/a14"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(getRequest)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = TestUtil.consumeJsonPinValues(response);
            Assert.assertEquals(1, values.size());
            Assert.assertEquals("100", values.get(0));
        }
    }

    @Test
    public void testPutWithExistingPinWrongBody() throws Exception {
        HttpPut request = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/a14"));
        request.setEntity(new StringEntity("\"100\"", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(500, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Error parsing body param. \"100\"", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testPutWithExistingPinWrongBody2() throws Exception {
        HttpPut request = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/a14"));
        request.setEntity(new StringEntity("", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(500, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Error parsing body param. ", TestUtil.consumeText(response));
        }
    }

    // ----------------------------NOTIFICATION POST METHODS SECTION
    // ----------------------------pushes
    @Test
    public void testPostNotifyNoContentType() throws Exception {
        HttpPost request = new HttpPost(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/notify"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(500, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Unexpected content type. Expecting application/json.", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testPostNotifyNoBody() throws Exception {
        HttpPost request = new HttpPost(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/notify"));
        request.setHeader("Content-Type", APPLICATION_JSON.toString());
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(500, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Error parsing body param. ", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testPostNotifyWithWrongBody() throws Exception {
        HttpPost request = new HttpPost(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/notify"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            sb.append(i);
        }
        request.setEntity(new StringEntity((("{\"body\":\"" + (sb.toString())) + "\"}"), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(400, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Body is empty or larger than 255 chars.", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testPostNotifyWithBody() throws Exception {
        HttpPost request = new HttpPost(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/notify"));
        String message = MAPPER.writeValueAsString(new PushMessagePojo("This is Push Message"));
        request.setEntity(new StringEntity(message, ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    // ----------------------------email
    @Test
    public void testPostEmailNoContentType() throws Exception {
        HttpPost request = new HttpPost(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/email"));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(500, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Unexpected content type. Expecting application/json.", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testPostEmailNoBody() throws Exception {
        HttpPost request = new HttpPost(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/email"));
        request.setHeader("Content-Type", APPLICATION_JSON.toString());
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(500, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Error parsing body param. ", TestUtil.consumeText(response));
        }
    }

    @Test
    public void testPostEmailWithBody() throws Exception {
        HttpPost request = new HttpPost(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/email"));
        String message = MAPPER.writeValueAsString(new EmailPojo("pupkin@gmail.com", "Title", "Subject"));
        request.setEntity(new StringEntity(message, ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    // ------------------------------ SYNC TEST
    @Test
    public void testSync() throws Exception {
        HttpPut request = new HttpPut(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/update/a14"));
        HttpGet getRequest = new HttpGet(((HttpAPIPinsTest.httpsServerUrl) + "4ae3851817194e2596cf1b7103603ef8/get/a14"));
        for (int i = 0; i < 100; i++) {
            request.setEntity(new StringEntity((("[\"" + i) + "\"]"), ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse response = HttpAPIPinsTest.httpclient.execute(request)) {
                Assert.assertEquals(200, response.getStatusLine().getStatusCode());
                EntityUtils.consume(response.getEntity());
            }
            try (CloseableHttpResponse response2 = HttpAPIPinsTest.httpclient.execute(getRequest)) {
                Assert.assertEquals(200, response2.getStatusLine().getStatusCode());
                List<String> values = TestUtil.consumeJsonPinValues(response2);
                Assert.assertEquals(1, values.size());
                Assert.assertEquals(String.valueOf(i), values.get(0));
            }
        }
    }
}

