package org.mockserver.mock;


import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;


/**
 *
 *
 * @author jamesdbloom
 */
public class ExpectationTest {
    @Test
    public void shouldConstructAndGetFields() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpResponse httpResponse = new HttpResponse();
        HttpForward httpForward = new HttpForward();
        HttpError httpError = new HttpError();
        HttpClassCallback httpClassCallback = new HttpClassCallback();
        HttpObjectCallback httpObjectCallback = new HttpObjectCallback();
        Times times = Times.exactly(3);
        TimeToLive timeToLive = TimeToLive.exactly(TimeUnit.HOURS, 5L);
        // when
        Expectation expectationThatResponds = new Expectation(httpRequest, times, timeToLive).thenRespond(httpResponse);
        // then
        Assert.assertEquals(httpRequest, expectationThatResponds.getHttpRequest());
        Assert.assertEquals(httpResponse, expectationThatResponds.getHttpResponse());
        Assert.assertEquals(httpResponse, expectationThatResponds.getAction());
        Assert.assertNull(expectationThatResponds.getHttpForward());
        Assert.assertNull(expectationThatResponds.getHttpError());
        Assert.assertNull(expectationThatResponds.getHttpResponseClassCallback());
        Assert.assertNull(expectationThatResponds.getHttpResponseObjectCallback());
        Assert.assertEquals(times, expectationThatResponds.getTimes());
        Assert.assertEquals(timeToLive, expectationThatResponds.getTimeToLive());
        // when
        Expectation expectationThatForwards = new Expectation(httpRequest, times, timeToLive).thenForward(httpForward);
        // then
        Assert.assertEquals(httpRequest, expectationThatForwards.getHttpRequest());
        Assert.assertNull(expectationThatForwards.getHttpResponse());
        Assert.assertEquals(httpForward, expectationThatForwards.getHttpForward());
        Assert.assertEquals(httpForward, expectationThatForwards.getAction());
        Assert.assertNull(expectationThatForwards.getHttpError());
        Assert.assertNull(expectationThatForwards.getHttpResponseClassCallback());
        Assert.assertNull(expectationThatForwards.getHttpResponseObjectCallback());
        Assert.assertEquals(times, expectationThatForwards.getTimes());
        Assert.assertEquals(timeToLive, expectationThatForwards.getTimeToLive());
        // when
        Expectation expectationThatErrors = new Expectation(httpRequest, times, timeToLive).thenError(httpError);
        // then
        Assert.assertEquals(httpRequest, expectationThatErrors.getHttpRequest());
        Assert.assertNull(expectationThatErrors.getHttpResponse());
        Assert.assertNull(expectationThatErrors.getHttpForward());
        Assert.assertEquals(httpError, expectationThatErrors.getHttpError());
        Assert.assertEquals(httpError, expectationThatErrors.getAction());
        Assert.assertNull(expectationThatErrors.getHttpResponseClassCallback());
        Assert.assertNull(expectationThatErrors.getHttpResponseObjectCallback());
        Assert.assertEquals(times, expectationThatErrors.getTimes());
        Assert.assertEquals(timeToLive, expectationThatErrors.getTimeToLive());
        // when
        Expectation expectationThatCallsbacksClass = new Expectation(httpRequest, times, timeToLive).thenRespond(httpClassCallback);
        // then
        Assert.assertEquals(httpRequest, expectationThatForwards.getHttpRequest());
        Assert.assertNull(expectationThatCallsbacksClass.getHttpResponse());
        Assert.assertNull(expectationThatCallsbacksClass.getHttpForward());
        Assert.assertNull(expectationThatCallsbacksClass.getHttpError());
        Assert.assertEquals(httpClassCallback, expectationThatCallsbacksClass.getHttpResponseClassCallback());
        Assert.assertEquals(httpClassCallback, expectationThatCallsbacksClass.getAction());
        Assert.assertNull(expectationThatCallsbacksClass.getHttpResponseObjectCallback());
        Assert.assertEquals(times, expectationThatCallsbacksClass.getTimes());
        Assert.assertEquals(timeToLive, expectationThatCallsbacksClass.getTimeToLive());
        // when
        Expectation expectationThatCallsbackObject = new Expectation(httpRequest, times, timeToLive).thenRespond(httpObjectCallback);
        // then
        Assert.assertEquals(httpRequest, expectationThatForwards.getHttpRequest());
        Assert.assertNull(expectationThatCallsbackObject.getHttpResponse());
        Assert.assertNull(expectationThatCallsbackObject.getHttpForward());
        Assert.assertNull(expectationThatCallsbackObject.getHttpError());
        Assert.assertNull(expectationThatCallsbackObject.getHttpResponseClassCallback());
        Assert.assertEquals(httpObjectCallback, expectationThatCallsbackObject.getHttpResponseObjectCallback());
        Assert.assertEquals(httpObjectCallback, expectationThatCallsbackObject.getAction());
        Assert.assertEquals(times, expectationThatCallsbackObject.getTimes());
        Assert.assertEquals(timeToLive, expectationThatCallsbackObject.getTimeToLive());
    }

    @Test
    public void shouldAllowForNulls() {
        // when
        Expectation expectation = thenRespond(((HttpResponse) (null))).thenForward(((HttpForward) (null))).thenRespond(((HttpClassCallback) (null))).thenRespond(((HttpObjectCallback) (null)));
        // then
        Assert.assertTrue(expectation.isActive());
        Assert.assertFalse(expectation.contains(null));
        Assert.assertNull(expectation.getHttpRequest());
        Assert.assertNull(expectation.getHttpResponse());
        Assert.assertNull(expectation.getHttpForward());
        Assert.assertNull(expectation.getHttpResponseClassCallback());
        Assert.assertNull(expectation.getHttpResponseObjectCallback());
        Assert.assertNull(expectation.getTimes());
    }

    @Test
    public void shouldReturnAliveStatus() {
        // when no times left should return false
        Assert.assertFalse(isActive());
        Assert.assertFalse(isActive());
        Assert.assertFalse(isActive());
        // when ttl expired should return false
        Assert.assertFalse(isActive());
        Assert.assertFalse(isActive());
        Assert.assertFalse(isActive());
    }

    @Test
    public void shouldReduceRemainingMatches() {
        // given
        Expectation expectation = new Expectation(null, Times.once(), TimeToLive.unlimited());
        // when
        expectation.decrementRemainingMatches();
        // then
        Assert.assertThat(expectation.getTimes().getRemainingTimes(), Is.is(0));
    }

    @Test
    public void shouldCalculateRemainingMatches() {
        Assert.assertThat(isActive(), Is.is(true));
        Assert.assertThat(isActive(), Is.is(true));
        Assert.assertThat(isActive(), Is.is(true));
        Assert.assertThat(isActive(), Is.is(true));
        Assert.assertThat(isActive(), Is.is(false));
    }

    @Test
    public void shouldCalculateRemainingLife() {
        Assert.assertThat(isActive(), Is.is(true));
        Assert.assertThat(isActive(), Is.is(true));
        Assert.assertThat(isActive(), Is.is(true));
        Assert.assertThat(isActive(), Is.is(false));
    }

    @Test
    public void shouldNotThrowExceptionWithReducingNullRemainingMatches() {
        // given
        Expectation expectation = new Expectation(null, null, TimeToLive.unlimited());
        // when
        expectation.decrementRemainingMatches();
        // then
        Assert.assertThat(expectation.getTimes(), CoreMatchers.nullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventResponseAfterForward() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpResponse httpResponse = new HttpResponse();
        HttpForward httpForward = new HttpForward();
        // then
        thenRespond(httpResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventResponseAfterError() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpResponse httpResponse = new HttpResponse();
        HttpError httpError = new HttpError();
        // then
        thenRespond(httpResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventResponseAfterClassCallback() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpClassCallback httpClassCallback = new HttpClassCallback();
        HttpResponse httpResponse = new HttpResponse();
        // then
        thenRespond(httpResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventResponseAfterObjectCallback() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpObjectCallback httpObjectCallback = new HttpObjectCallback();
        HttpResponse httpResponse = new HttpResponse();
        // then
        thenRespond(httpResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventForwardAfterResponse() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpResponse httpResponse = new HttpResponse();
        HttpForward httpForward = new HttpForward();
        // then
        new Expectation(httpRequest, Times.once(), TimeToLive.unlimited()).thenRespond(httpResponse).thenForward(httpForward);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventForwardAfterError() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpError httpError = new HttpError();
        HttpForward httpForward = new HttpForward();
        // then
        new Expectation(httpRequest, Times.once(), TimeToLive.unlimited()).thenError(httpError).thenForward(httpForward);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventForwardAfterClassCallback() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpClassCallback httpClassCallback = new HttpClassCallback();
        HttpForward httpForward = new HttpForward();
        // then
        new Expectation(httpRequest, Times.once(), TimeToLive.unlimited()).thenRespond(httpClassCallback).thenForward(httpForward);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventForwardAfterObjectCallback() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpObjectCallback httpObjectCallback = new HttpObjectCallback();
        HttpForward httpForward = new HttpForward();
        // then
        new Expectation(httpRequest, Times.once(), TimeToLive.unlimited()).thenRespond(httpObjectCallback).thenForward(httpForward);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventClassCallbackAfterForward() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpForward httpForward = new HttpForward();
        HttpClassCallback httpClassCallback = new HttpClassCallback();
        // then
        new Expectation(httpRequest, Times.once(), TimeToLive.unlimited()).thenForward(httpForward).thenRespond(httpClassCallback);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventClassCallbackAfterError() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpError httpError = new HttpError();
        HttpClassCallback httpClassCallback = new HttpClassCallback();
        // then
        new Expectation(httpRequest, Times.once(), TimeToLive.unlimited()).thenError(httpError).thenRespond(httpClassCallback);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventClassCallbackAfterResponse() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpResponse httpResponse = new HttpResponse();
        HttpClassCallback httpClassCallback = new HttpClassCallback();
        // then
        new Expectation(httpRequest, Times.once(), TimeToLive.unlimited()).thenRespond(httpResponse).thenRespond(httpClassCallback);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventObjectCallbackAfterForward() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpForward httpForward = new HttpForward();
        HttpObjectCallback httpObjectCallback = new HttpObjectCallback();
        // then
        new Expectation(httpRequest, Times.once(), TimeToLive.unlimited()).thenForward(httpForward).thenRespond(httpObjectCallback);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventObjectCallbackAfterError() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpError httpError = new HttpError();
        HttpObjectCallback httpObjectCallback = new HttpObjectCallback();
        // then
        new Expectation(httpRequest, Times.once(), TimeToLive.unlimited()).thenError(httpError).thenRespond(httpObjectCallback);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventObjectCallbackAfterResponse() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        HttpResponse httpResponse = new HttpResponse();
        HttpObjectCallback httpObjectCallback = new HttpObjectCallback();
        // then
        new Expectation(httpRequest, Times.once(), TimeToLive.unlimited()).thenRespond(httpResponse).thenRespond(httpObjectCallback);
    }
}

