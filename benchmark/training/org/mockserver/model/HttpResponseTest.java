package org.mockserver.model;


import CharsetUtil.ISO_8859_1;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsSame;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.serialization.Base64Converter;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpResponseTest {
    private final Base64Converter base64Converter = new Base64Converter();

    @Test
    public void shouldAlwaysCreateNewObject() {
        Assert.assertEquals(new HttpResponse().response(), HttpResponse.response());
        Assert.assertNotSame(HttpResponse.response(), HttpResponse.response());
    }

    @Test
    public void returnsResponseStatusCode() {
        Assert.assertEquals(new Integer(200), new HttpResponse().withStatusCode(200).getStatusCode());
    }

    @Test
    public void returnsResponseReasonPhrase() {
        Assert.assertEquals("reasonPhrase", new HttpResponse().withReasonPhrase("reasonPhrase").getReasonPhrase());
    }

    @Test
    public void returnsBody() {
        Assert.assertEquals(base64Converter.bytesToBase64String("somebody".getBytes(StandardCharsets.UTF_8)), new HttpResponse().withBody("somebody".getBytes(StandardCharsets.UTF_8)).getBodyAsString());
        Assert.assertEquals("somebody", new HttpResponse().withBody("somebody").getBodyAsString());
        TestCase.assertNull(new HttpResponse().withBody(((byte[]) (null))).getBodyAsString());
        Assert.assertEquals(null, new HttpResponse().withBody(((String) (null))).getBodyAsString());
    }

    @Test
    public void returnsHeaders() {
        Assert.assertEquals(new Header("name", "value"), new HttpResponse().withHeaders(new Header("name", "value")).getHeaderList().get(0));
        Assert.assertEquals(new Header("name", "value"), new HttpResponse().withHeaders(Arrays.asList(new Header("name", "value"))).getHeaderList().get(0));
        Assert.assertEquals(new Header("name", "value"), new HttpRequest().withHeader(new Header("name", "value")).getHeaderList().get(0));
        Assert.assertEquals(new Header("name", "value_one", "value_two"), new HttpRequest().withHeader(new Header("name", "value_one")).withHeader(new Header("name", "value_two")).getHeaderList().get(0));
    }

    @Test
    public void returnsFirstHeaders() {
        Assert.assertEquals("value1", new HttpResponse().withHeaders(new Header("name", "value1")).getFirstHeader("name"));
        Assert.assertEquals("value1", new HttpResponse().withHeaders(new Header("name", "value1", "value2")).getFirstHeader("name"));
        Assert.assertEquals("value1", new HttpResponse().withHeaders(new Header("name", "value1", "value2"), new Header("name", "value3")).getFirstHeader("name"));
    }

    @Test
    public void returnsFirstHeaderIgnoringCase() {
        Assert.assertEquals("value1", new HttpResponse().withHeaders(new Header("NAME", "value1")).getFirstHeader("name"));
        Assert.assertEquals("value1", new HttpResponse().withHeaders(new Header("name", "value1", "value2")).getFirstHeader("NAME"));
        Assert.assertEquals("value1", new HttpResponse().withHeaders(new Header("NAME", "value1", "value2"), new Header("NAME", "value3"), new Header("NAME", "value4")).getFirstHeader("NAME"));
        Assert.assertEquals("value1", new HttpResponse().withHeaders(new Header("name", "value1", "value2"), new Header("name", "value3"), new Header("name", "value4")).getFirstHeader("NAME"));
        Assert.assertEquals("value1", new HttpResponse().withHeaders(new Header("NAME", "value1", "value2"), new Header("name", "value3"), new Header("name", "value4")).getFirstHeader("name"));
    }

    @Test
    public void returnsHeaderByName() {
        MatcherAssert.assertThat(new HttpResponse().withHeaders(new Header("name", "value")).getHeader("name"), Matchers.containsInAnyOrder("value"));
        MatcherAssert.assertThat(new HttpResponse().withHeaders(new Header("name", "valueOne", "valueTwo")).getHeader("name"), Matchers.containsInAnyOrder("valueOne", "valueTwo"));
        MatcherAssert.assertThat(new HttpResponse().withHeader("name", "valueOne", "valueTwo").getHeader("name"), Matchers.containsInAnyOrder("valueOne", "valueTwo"));
        MatcherAssert.assertThat(new HttpResponse().withHeaders(new Header("name", "valueOne", "valueTwo")).getHeader("otherName"), Matchers.hasSize(0));
    }

    @Test
    public void containsHeaderIgnoringCase() {
        TestCase.assertTrue(new HttpResponse().withHeaders(new Header("name", "value")).containsHeader("name", "value"));
        TestCase.assertTrue(new HttpResponse().withHeaders(new Header("name", "value")).containsHeader("name", "VALUE"));
        TestCase.assertTrue(new HttpResponse().withHeaders(new Header("name", "value")).containsHeader("NAME", "value"));
        TestCase.assertTrue(new HttpResponse().withHeaders(new Header("name", "valueOne", "valueTwo")).containsHeader("name", "valueOne"));
        TestCase.assertTrue(new HttpResponse().withHeaders(new Header("name", "valueOne", "valueTwo")).containsHeader("name", "VALUEONE"));
        TestCase.assertTrue(new HttpResponse().withHeaders(new Header("name", "valueOne", "valueTwo")).containsHeader("NAME", "valueTwo"));
        TestCase.assertTrue(new HttpResponse().withHeader("name", "valueOne", "valueTwo").containsHeader("name", "ValueOne"));
        TestCase.assertTrue(new HttpResponse().withHeader("name", "valueOne", "valueTwo").containsHeader("name", "valueOne"));
        TestCase.assertTrue(new HttpResponse().withHeader("name", "valueOne", "valueTwo").containsHeader("NAME", "ValueOne"));
        TestCase.assertFalse(new HttpResponse().withHeaders(new Header("name", "valueOne", "valueTwo")).containsHeader("otherName", "valueOne"));
        TestCase.assertFalse(new HttpResponse().withHeaders(new Header("name", "valueOne", "valueTwo")).containsHeader("name", "value"));
    }

    @Test
    public void returnsHeaderByNameIgnoringCase() {
        MatcherAssert.assertThat(new HttpResponse().withHeaders(new Header("Name", "value")).getHeader("name"), Matchers.containsInAnyOrder("value"));
        MatcherAssert.assertThat(new HttpResponse().withHeaders(new Header("name", "valueOne", "valueTwo")).getHeader("Name"), Matchers.containsInAnyOrder("valueOne", "valueTwo"));
        MatcherAssert.assertThat(new HttpResponse().withHeader("NAME", "valueOne", "valueTwo").getHeader("name"), Matchers.containsInAnyOrder("valueOne", "valueTwo"));
        MatcherAssert.assertThat(new HttpResponse().withHeaders(new Header("name", "valueOne", "valueTwo")).getHeader("otherName"), Matchers.hasSize(0));
    }

    @Test
    public void addDuplicateHeader() {
        MatcherAssert.assertThat(new HttpResponse().withHeader(new Header("name", "valueOne")).withHeader(new Header("name", "valueTwo")).getHeaderList(), Matchers.containsInAnyOrder(new Header("name", "valueOne", "valueTwo")));
        MatcherAssert.assertThat(new HttpResponse().withHeader(new Header("name", "valueOne")).withHeader("name", "valueTwo").getHeaderList(), Matchers.containsInAnyOrder(new Header("name", "valueOne", "valueTwo")));
    }

    @Test
    public void updatesExistingHeader() {
        MatcherAssert.assertThat(new HttpResponse().withHeader(new Header("name", "valueOne")).replaceHeader(new Header("name", "valueTwo")).getHeaderList(), Matchers.containsInAnyOrder(new Header("name", "valueTwo")));
        MatcherAssert.assertThat(new HttpResponse().withHeader(new Header("name", "valueOne")).replaceHeader("name", "valueTwo").getHeaderList(), Matchers.containsInAnyOrder(new Header("name", "valueTwo")));
    }

    @Test
    public void returnsCookies() {
        Assert.assertEquals(new Cookie("name", "value"), new HttpResponse().withCookies(new Cookie("name", "value")).getCookieList().get(0));
        Assert.assertEquals(new Cookie("name", ""), new HttpResponse().withCookies(new Cookie("name", "")).getCookieList().get(0));
        Assert.assertEquals(new Cookie("name", null), new HttpResponse().withCookies(new Cookie("name", null)).getCookieList().get(0));
        Assert.assertEquals(new Cookie("name", "value"), new HttpResponse().withCookies(Arrays.asList(new Cookie("name", "value"))).getCookieList().get(0));
        Assert.assertEquals(new Cookie("name", "value"), new HttpResponse().withCookie(new Cookie("name", "value")).getCookieList().get(0));
        Assert.assertEquals(new Cookie("name", "value"), new HttpResponse().withCookie("name", "value").getCookieList().get(0));
        Assert.assertEquals(new Cookie("name", ""), new HttpResponse().withCookie(new Cookie("name", "")).getCookieList().get(0));
        Assert.assertEquals(new Cookie("name", null), new HttpResponse().withCookie(new Cookie("name", null)).getCookieList().get(0));
    }

    @Test
    public void setsDelay() {
        Assert.assertEquals(new Delay(TimeUnit.MILLISECONDS, 10), new HttpResponse().withDelay(new Delay(TimeUnit.MILLISECONDS, 10)).getDelay());
        Assert.assertEquals(new Delay(TimeUnit.MILLISECONDS, 10), new HttpResponse().withDelay(TimeUnit.MILLISECONDS, 10).getDelay());
    }

    @Test
    public void setsConnectionOptions() {
        Assert.assertEquals(new ConnectionOptions().withContentLengthHeaderOverride(10), new HttpResponse().withConnectionOptions(new ConnectionOptions().withContentLengthHeaderOverride(10)).getConnectionOptions());
    }

    @Test
    public void shouldReturnFormattedRequestInToString() {
        Assert.assertEquals((((((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"statusCode\" : 666,") + (NEW_LINE)) + "  \"reasonPhrase\" : \"randomPhrase\",") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"some_header\" : [ \"some_header_value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"some_cookie\" : \"some_cookie_value\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"STRING\",") + (NEW_LINE)) + "    \"string\" : \"some_body\",") + (NEW_LINE)) + "    \"contentType\" : \"text/plain; charset=iso-8859-1\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"delay\" : {") + (NEW_LINE)) + "    \"timeUnit\" : \"SECONDS\",") + (NEW_LINE)) + "    \"value\" : 15") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"connectionOptions\" : {") + (NEW_LINE)) + "    \"contentLengthHeaderOverride\" : 10,") + (NEW_LINE)) + "    \"keepAliveOverride\" : true") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"), HttpResponse.response().withBody("some_body", ISO_8859_1).withStatusCode(666).withReasonPhrase("randomPhrase").withHeaders(new Header("some_header", "some_header_value")).withCookies(new Cookie("some_cookie", "some_cookie_value")).withConnectionOptions(ConnectionOptions.connectionOptions().withContentLengthHeaderOverride(10).withKeepAliveOverride(true)).withDelay(TimeUnit.SECONDS, 15).toString());
    }

    @Test
    public void shouldClone() {
        // given
        HttpResponse responseOne = HttpResponse.response().withBody("some_body", StandardCharsets.UTF_8).withStatusCode(666).withReasonPhrase("someReasonPhrase").withHeader("some_header", "some_header_value").withCookie("some_cookie", "some_cookie_value").withConnectionOptions(ConnectionOptions.connectionOptions().withContentLengthHeaderOverride(10).withKeepAliveOverride(true)).withDelay(TimeUnit.SECONDS, 15);
        // when
        HttpResponse responseTwo = responseOne.clone();
        // then
        MatcherAssert.assertThat(responseOne, CoreMatchers.not(IsSame.sameInstance(responseTwo)));
        MatcherAssert.assertThat(responseOne, Is.is(responseTwo));
    }
}

