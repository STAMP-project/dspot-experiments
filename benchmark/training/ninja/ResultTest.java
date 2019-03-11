/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja;


import Result.APPLICATION_JSON;
import Result.LOCATION;
import Result.SC_200_OK;
import Result.SC_303_SEE_OTHER;
import Result.SC_307_TEMPORARY_REDIRECT;
import Result.SC_501_NOT_IMPLEMENTED;
import Result.TEXT_HTML;
import com.google.common.collect.Maps;
import java.io.StringWriter;
import java.io.Writer;
import java.util.AbstractMap;
import java.util.Map;
import ninja.utils.ResponseStreams;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static Result.SC_200_OK;
import static Result.SC_307_TEMPORARY_REDIRECT;


@RunWith(MockitoJUnitRunner.class)
public class ResultTest {
    @Mock
    Context context;

    @Mock
    ResponseStreams responseStreams;

    @Test
    public void testConstructor() {
        Result result = new Result(SC_307_TEMPORARY_REDIRECT);
        Assert.assertEquals(SC_307_TEMPORARY_REDIRECT, result.getStatusCode());
        Assert.assertEquals(0, result.getCookies().size());
        Assert.assertEquals(0, result.getHeaders().keySet().size());
    }

    @Test
    public void testGetRenderable() {
        ResultTest.TestObject testObject = new ResultTest.TestObject();
        Result result = new Result(200);
        result.render(testObject);
        Assert.assertEquals(testObject, result.getRenderable());
    }

    @Test
    public void testGetContentType() {
        Result result = new Result(200);
        result.contentType("text/my-funky-content-type");
        Assert.assertEquals("text/my-funky-content-type", result.getContentType());
    }

    @Test
    public void testAndAddHeaders() {
        Result result = new Result(200);
        result.addHeader("header1", "value1");
        result.addHeader("header2", "value2");
        Assert.assertEquals(2, result.getHeaders().size());
        Assert.assertEquals("value1", result.getHeaders().get("header1"));
        Assert.assertEquals("value2", result.getHeaders().get("header2"));
    }

    @Test
    public void testAndAddCookies() {
        Result result = new Result(200);
        result.addCookie(Cookie.builder("cookie1", "value1").build());
        result.addCookie(Cookie.builder("cookie2", "value2").build());
        Assert.assertEquals(2, result.getCookies().size());
        Assert.assertEquals("value1", result.getCookies().get(0).getValue());
        Assert.assertEquals("value2", result.getCookies().get(1).getValue());
    }

    @Test
    public void testUnsetCookie() {
        Result result = new Result(200);
        result.unsetCookie("Cookie-to-be-unset");
        Assert.assertEquals(1, result.getCookies().size());
        Assert.assertEquals("Cookie-to-be-unset", result.getCookies().get(0).getName());
        Assert.assertEquals("", result.getCookies().get(0).getValue());
    }

    @Test
    public void testSetAndGetStatus() {
        Result result = new Result(SC_200_OK);
        // set the status:
        result.status(SC_501_NOT_IMPLEMENTED);
        // and verify that we retrieve the correct one:
        Assert.assertEquals(SC_501_NOT_IMPLEMENTED, result.getStatusCode());
    }

    @Test
    public void testSetAndGetTemplate() {
        Result result = new Result(SC_200_OK);
        // set the status:
        result.template("/my/custom/template.ftl.html");
        // and verify that we retrieve the correct one:
        Assert.assertEquals("/my/custom/template.ftl.html", result.getTemplate());
        Assert.assertEquals(SC_200_OK, result.getStatusCode());
    }

    @Test
    public void testSetAndGetJsonView() {
        Result result = new Result(SC_200_OK);
        result.jsonView(ResultTest.TestObject.class);
        Assert.assertEquals(ResultTest.TestObject.class, result.getJsonView());
    }

    @Test
    public void testRedirect() {
        Result result = new Result(SC_200_OK);
        result.redirect("http://example.com");
        // assert that headers are set:
        Assert.assertEquals(1, result.getHeaders().size());
        Assert.assertEquals("http://example.com", result.getHeaders().get(LOCATION));
        Assert.assertEquals(SC_303_SEE_OTHER, result.getStatusCode());
    }

    @Test
    public void testRedirectTemporary() {
        Result result = new Result(SC_200_OK);
        result.redirectTemporary("http://example.com");
        // assert that headers are set:
        Assert.assertEquals(1, result.getHeaders().size());
        Assert.assertEquals("http://example.com", result.getHeaders().get(LOCATION));
        Assert.assertEquals(SC_307_TEMPORARY_REDIRECT, result.getStatusCode());
    }

    @Test
    public void testHtml() {
        Result result = new Result(SC_200_OK);
        result.html();
        Assert.assertEquals(TEXT_HTML, result.getContentType());
    }

    @Test
    public void testJson() {
        Result result = new Result(SC_200_OK);
        result.json();
        Assert.assertEquals(APPLICATION_JSON, result.getContentType());
    }

    @Test
    public void testUtf8IsUsedAsDefaultCharset() {
        Result result = new Result(SC_200_OK);
        Assert.assertEquals("utf-8", result.getCharset());
    }

    @Test
    public void testSettingOfCharsetWorks() {
        Result result = new Result(SC_200_OK);
        result.charset("iso-7777");
        Assert.assertEquals("iso-7777", result.getCharset());
    }

    @Test
    public void testRenderRenderable() {
        Renderable renderable = new Renderable() {
            @Override
            public void render(Context context, Result result) {
                // do nothing
            }
        };
        // step 1: normal operation
        Result result = new Result(200);
        result.render(renderable);
        Assert.assertEquals(renderable, result.getRenderable());
        // step 2: now we expect an illegal argument exception:
        boolean gotException = true;
        try {
            result.render(new ResultTest.TestObject());
        } catch (IllegalArgumentException e) {
            gotException = true;
        }
        Assert.assertTrue(gotException);
    }

    @Test
    public void testRenderSingleObject() {
        ResultTest.TestObject testObject = new ResultTest.TestObject();
        Result result = new Result(200);
        result.render(testObject);
        Assert.assertEquals(testObject, result.getRenderable());
    }

    @Test
    public void testRenderMultipleObjects() {
        ResultTest.TestObject testObject = new ResultTest.TestObject();
        // step 1: add one object.
        Result result = new Result(200);
        result.render(testObject);
        Assert.assertEquals(testObject, result.getRenderable());
        // step 2: add a second object (string is just a dummy)
        // => we expect to get a map from the result now...
        String string = new String("test");
        result.render(string);
        Assert.assertTrue(((result.getRenderable()) instanceof Map));
        Map<String, Object> resultMap = ((Map) (result.getRenderable()));
        Assert.assertEquals(string, resultMap.get("string"));
        Assert.assertEquals(testObject, resultMap.get("testObject"));
        // step 3: add same object => we expect an illegal argument exception as the map
        // cannot handle that case:
        ResultTest.TestObject anotherObject = new ResultTest.TestObject();
        boolean gotException = false;
        try {
            result.render(anotherObject);
        } catch (IllegalArgumentException e) {
            gotException = true;
        }
        Assert.assertTrue(gotException);
        // step 4: add an entry
        Map.Entry<String, Object> entry = new AbstractMap.SimpleImmutableEntry<String, Object>("anotherObject", anotherObject);
        result.render(entry);
        resultMap = ((Map) (result.getRenderable()));
        Assert.assertEquals(3, resultMap.size());
        Assert.assertEquals(anotherObject, resultMap.get("anotherObject"));
        // step 5: add another map and check that conversion works:
        Map<String, Object> mapToRender = Maps.newHashMap();
        String anotherString = new String("anotherString");
        ResultTest.TestObject anotherTestObject = new ResultTest.TestObject();
        mapToRender.put("anotherString", anotherString);
        mapToRender.put("anotherTestObject", anotherTestObject);
        result.render(mapToRender);
        resultMap = ((Map) (result.getRenderable()));
        Assert.assertEquals(2, resultMap.size());
        Assert.assertEquals(anotherString, resultMap.get("anotherString"));
        Assert.assertEquals(anotherTestObject, resultMap.get("anotherTestObject"));
    }

    @Test
    public void testRenderEntryAndMakeSureMapIsCreated() {
        String stringy = new String("stringy");
        // step 1: add one object.
        Result result = new Result(200);
        result.render("stringy", stringy);
        Map<String, Object> resultMap = ((Map) (result.getRenderable()));
        Assert.assertEquals(stringy, resultMap.get("stringy"));
    }

    @Test
    public void testRenderingOfStringObjectPairsWorks() {
        String object1 = new String("stringy1");
        String object2 = new String("stringy2");
        // step 1: add one object.
        Result result = new Result(200);
        result.render("object1", object1);
        result.render("object2", object2);
        Map<String, Object> resultMap = ((Map) (result.getRenderable()));
        Assert.assertEquals(object1, resultMap.get("object1"));
        Assert.assertEquals(object2, resultMap.get("object2"));
    }

    @Test
    public void testRenderRaw() throws Exception {
        String stringToRender = "{\"user\" : \"john@woo.com\"}";
        // Construct a new result via Results.
        Result result = Results.json().renderRaw(stringToRender);
        // Setup some stuff to catch the output that gets written to the
        // output stream.
        Writer writer = new StringWriter();
        Mockito.when(context.finalizeHeaders(result)).thenReturn(responseStreams);
        Mockito.when(responseStreams.getWriter()).thenReturn(writer);
        Renderable renderable = ((Renderable) (result.getRenderable()));
        // Now issue a "render":
        renderable.render(context, result);
        // make sure we called the finalizeHeaders
        Mockito.verify(context).finalizeHeaders(result);
        // make sure we did render the string to the OutputStream.
        Assert.assertEquals(writer.toString(), stringToRender);
        // also make sure the content type is set correctly.
        Assert.assertEquals(APPLICATION_JSON, result.getContentType());
    }

    /**
     * Simple helper to test if objects get copied to result.
     */
    public class TestObject {}
}

