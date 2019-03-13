package org.mockserver.serialization.model;


import java.nio.charset.StandardCharsets;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;

import static org.mockserver.model.HttpRequest.request;


/**
 *
 *
 * @author jamesdbloom
 */
public class ExpectationDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // given
        HttpRequest httpRequest = new HttpRequest().withBody("some_body");
        HttpResponse httpResponse = new HttpResponse().withBody("some_response_body");
        HttpTemplate httpResponseTemplate = withTemplate("some_repoonse_template");
        HttpClassCallback httpResponseClassCallback = new HttpClassCallback().withCallbackClass("some_response_class");
        HttpObjectCallback httpResponseObjectCallback = new HttpObjectCallback().withClientId("some_response_client_id");
        HttpForward httpForward = new HttpForward().withHost("some_host");
        HttpTemplate httpForwardTemplate = withTemplate("some_forward_template");
        HttpClassCallback httpForwardClassCallback = new HttpClassCallback().withCallbackClass("some_forward_class");
        HttpObjectCallback httpForwardObjectCallback = new HttpObjectCallback().withClientId("some_forward_client_id");
        HttpOverrideForwardedRequest httpOverrideForwardedRequest = new HttpOverrideForwardedRequest().withHttpRequest(httpRequest);
        HttpError httpError = new HttpError().withResponseBytes("some_bytes".getBytes(StandardCharsets.UTF_8));
        // when
        ExpectationDTO expectationWithResponse = new ExpectationDTO(new Expectation(httpRequest, Times.exactly(3), TimeToLive.unlimited()).thenRespond(httpResponse));
        // then
        MatcherAssert.assertThat(expectationWithResponse.getHttpRequest(), Is.is(new HttpRequestDTO(httpRequest)));
        MatcherAssert.assertThat(expectationWithResponse.getTimes(), Is.is(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))));
        MatcherAssert.assertThat(expectationWithResponse.getHttpResponse(), Is.is(new HttpResponseDTO(httpResponse)));
        Assert.assertNull(expectationWithResponse.getHttpResponseTemplate());
        Assert.assertNull(expectationWithResponse.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithResponse.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithResponse.getHttpForward());
        Assert.assertNull(expectationWithResponse.getHttpForwardTemplate());
        Assert.assertNull(expectationWithResponse.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithResponse.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithResponse.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithResponse.getHttpError());
        // when
        Expectation expectationWithResponseTemplate = buildObject();
        // then
        MatcherAssert.assertThat(expectationWithResponseTemplate.getHttpRequest(), Is.is(httpRequest));
        MatcherAssert.assertThat(expectationWithResponseTemplate.getTimes(), Is.is(Times.exactly(3)));
        Assert.assertNull(expectationWithResponseTemplate.getHttpResponse());
        MatcherAssert.assertThat(expectationWithResponseTemplate.getHttpResponseTemplate(), Is.is(httpResponseTemplate));
        Assert.assertNull(expectationWithResponseTemplate.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithResponseTemplate.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithResponseTemplate.getHttpForward());
        Assert.assertNull(expectationWithResponseTemplate.getHttpForwardTemplate());
        Assert.assertNull(expectationWithResponseTemplate.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithResponseTemplate.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithResponseTemplate.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithResponseTemplate.getHttpError());
        // when
        ExpectationDTO expectationWithResponseClassCallback = new ExpectationDTO(new Expectation(httpRequest, Times.exactly(3), TimeToLive.unlimited()).thenRespond(httpResponseClassCallback));
        // then
        MatcherAssert.assertThat(expectationWithResponseClassCallback.getHttpRequest(), Is.is(new HttpRequestDTO(httpRequest)));
        MatcherAssert.assertThat(expectationWithResponseClassCallback.getTimes(), Is.is(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))));
        Assert.assertNull(expectationWithResponseClassCallback.getHttpResponse());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpResponseTemplate());
        MatcherAssert.assertThat(expectationWithResponseClassCallback.getHttpResponseClassCallback(), Is.is(new HttpClassCallbackDTO(httpResponseClassCallback)));
        Assert.assertNull(expectationWithResponseClassCallback.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpForward());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpForwardTemplate());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpError());
        // when
        ExpectationDTO expectationWithResponseObjectCallback = new ExpectationDTO(new Expectation(httpRequest, Times.exactly(3), TimeToLive.unlimited()).thenRespond(httpResponseObjectCallback));
        // then
        MatcherAssert.assertThat(expectationWithResponseObjectCallback.getHttpRequest(), Is.is(new HttpRequestDTO(httpRequest)));
        MatcherAssert.assertThat(expectationWithResponseObjectCallback.getTimes(), Is.is(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))));
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpResponse());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpResponseTemplate());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpResponseClassCallback());
        MatcherAssert.assertThat(expectationWithResponseObjectCallback.getHttpResponseObjectCallback(), Is.is(new HttpObjectCallbackDTO(httpResponseObjectCallback)));
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpForward());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpForwardTemplate());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpError());
        // when
        ExpectationDTO expectationWithForward = new ExpectationDTO(new Expectation(httpRequest, Times.exactly(3), TimeToLive.unlimited()).thenForward(httpForward));
        // then
        MatcherAssert.assertThat(expectationWithForward.getHttpRequest(), Is.is(new HttpRequestDTO(httpRequest)));
        MatcherAssert.assertThat(expectationWithForward.getTimes(), Is.is(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))));
        Assert.assertNull(expectationWithForward.getHttpResponse());
        Assert.assertNull(expectationWithForward.getHttpResponseTemplate());
        Assert.assertNull(expectationWithForward.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithForward.getHttpResponseObjectCallback());
        MatcherAssert.assertThat(expectationWithForward.getHttpForward(), Is.is(new HttpForwardDTO(httpForward)));
        Assert.assertNull(expectationWithForward.getHttpForwardTemplate());
        Assert.assertNull(expectationWithForward.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithForward.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithForward.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithForward.getHttpError());
        // when
        Expectation expectationWithForwardTemplate = buildObject();
        // then
        MatcherAssert.assertThat(expectationWithForwardTemplate.getHttpRequest(), Is.is(httpRequest));
        MatcherAssert.assertThat(expectationWithForwardTemplate.getTimes(), Is.is(Times.exactly(3)));
        Assert.assertNull(expectationWithForwardTemplate.getHttpResponse());
        Assert.assertNull(expectationWithForwardTemplate.getHttpResponseTemplate());
        Assert.assertNull(expectationWithForwardTemplate.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithForwardTemplate.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithForwardTemplate.getHttpForward());
        MatcherAssert.assertThat(expectationWithForwardTemplate.getHttpForwardTemplate(), Is.is(httpForwardTemplate));
        Assert.assertNull(expectationWithForwardTemplate.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithForwardTemplate.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithForwardTemplate.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithForwardTemplate.getHttpError());
        // when
        ExpectationDTO expectationWithForwardClassCallback = new ExpectationDTO(new Expectation(httpRequest, Times.exactly(3), TimeToLive.unlimited()).thenForward(httpForwardClassCallback));
        // then
        MatcherAssert.assertThat(expectationWithForwardClassCallback.getHttpRequest(), Is.is(new HttpRequestDTO(httpRequest)));
        MatcherAssert.assertThat(expectationWithForwardClassCallback.getTimes(), Is.is(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))));
        Assert.assertNull(expectationWithForwardClassCallback.getHttpResponse());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpResponseTemplate());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpForward());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpForwardTemplate());
        MatcherAssert.assertThat(expectationWithForwardClassCallback.getHttpForwardClassCallback(), Is.is(new HttpClassCallbackDTO(httpForwardClassCallback)));
        Assert.assertNull(expectationWithForwardClassCallback.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpError());
        // when
        ExpectationDTO expectationWithForwardObjectCallback = new ExpectationDTO(new Expectation(httpRequest, Times.exactly(3), TimeToLive.unlimited()).thenForward(httpForwardObjectCallback));
        // then
        MatcherAssert.assertThat(expectationWithForwardObjectCallback.getHttpRequest(), Is.is(new HttpRequestDTO(httpRequest)));
        MatcherAssert.assertThat(expectationWithForwardObjectCallback.getTimes(), Is.is(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))));
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpResponse());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpResponseTemplate());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpForward());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpForwardTemplate());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpForwardClassCallback());
        MatcherAssert.assertThat(expectationWithForwardObjectCallback.getHttpForwardObjectCallback(), Is.is(new HttpObjectCallbackDTO(httpForwardObjectCallback)));
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpError());
        // when
        ExpectationDTO expectationWithOverrideForwardedRequest = new ExpectationDTO(new Expectation(httpRequest, Times.exactly(3), TimeToLive.unlimited()).thenForward(httpOverrideForwardedRequest));
        // then
        MatcherAssert.assertThat(expectationWithOverrideForwardedRequest.getHttpRequest(), Is.is(new HttpRequestDTO(httpRequest)));
        MatcherAssert.assertThat(expectationWithOverrideForwardedRequest.getTimes(), Is.is(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))));
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpResponse());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpResponseTemplate());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpForward());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpForwardTemplate());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpForwardObjectCallback());
        MatcherAssert.assertThat(expectationWithOverrideForwardedRequest.getHttpOverrideForwardedRequest(), Is.is(new HttpOverrideForwardedRequestDTO(httpOverrideForwardedRequest)));
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpError());
        // when
        ExpectationDTO expectationWithError = new ExpectationDTO(new Expectation(httpRequest, Times.exactly(3), TimeToLive.unlimited()).thenError(httpError));
        // then
        MatcherAssert.assertThat(expectationWithError.getHttpRequest(), Is.is(new HttpRequestDTO(httpRequest)));
        MatcherAssert.assertThat(expectationWithError.getTimes(), Is.is(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))));
        Assert.assertNull(expectationWithError.getHttpResponse());
        Assert.assertNull(expectationWithError.getHttpResponseTemplate());
        Assert.assertNull(expectationWithError.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithError.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithError.getHttpForward());
        Assert.assertNull(expectationWithError.getHttpForwardTemplate());
        Assert.assertNull(expectationWithError.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithError.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithError.getHttpOverrideForwardedRequest());
        MatcherAssert.assertThat(expectationWithError.getHttpError(), Is.is(new HttpErrorDTO(httpError)));
    }

    @Test
    public void shouldBuildObject() {
        // given
        HttpRequest httpRequest = new HttpRequest().withBody("some_body");
        HttpResponse httpResponse = new HttpResponse().withBody("some_response_body");
        HttpTemplate httpResponseTemplate = withTemplate("some_repoonse_template");
        HttpForward httpForward = new HttpForward().withHost("some_host");
        HttpTemplate httpForwardTemplate = withTemplate("some_forward_template");
        HttpError httpError = new HttpError().withResponseBytes("some_bytes".getBytes(StandardCharsets.UTF_8));
        HttpClassCallback httpClassCallback = new HttpClassCallback().withCallbackClass("some_class");
        HttpObjectCallback httpObjectCallback = new HttpObjectCallback().withClientId("some_client_id");
        HttpOverrideForwardedRequest httpOverrideForwardedRequest = new HttpOverrideForwardedRequest().withHttpRequest(httpRequest);
        // when
        Expectation expectationWithResponse = buildObject();
        // then
        MatcherAssert.assertThat(expectationWithResponse.getHttpRequest(), Is.is(httpRequest));
        MatcherAssert.assertThat(expectationWithResponse.getTimes(), Is.is(Times.exactly(3)));
        MatcherAssert.assertThat(expectationWithResponse.getHttpResponse(), Is.is(httpResponse));
        Assert.assertNull(expectationWithResponse.getHttpResponseTemplate());
        Assert.assertNull(expectationWithResponse.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithResponse.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithResponse.getHttpForward());
        Assert.assertNull(expectationWithResponse.getHttpForwardTemplate());
        Assert.assertNull(expectationWithResponse.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithResponse.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithResponse.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithResponse.getHttpError());
        // when
        Expectation expectationWithResponseTemplate = buildObject();
        // then
        MatcherAssert.assertThat(expectationWithResponseTemplate.getHttpRequest(), Is.is(httpRequest));
        MatcherAssert.assertThat(expectationWithResponseTemplate.getTimes(), Is.is(Times.exactly(3)));
        Assert.assertNull(expectationWithResponseTemplate.getHttpResponse());
        MatcherAssert.assertThat(expectationWithResponseTemplate.getHttpResponseTemplate(), Is.is(httpResponseTemplate));
        Assert.assertNull(expectationWithResponseTemplate.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithResponseTemplate.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithResponseTemplate.getHttpForward());
        Assert.assertNull(expectationWithResponseTemplate.getHttpForwardTemplate());
        Assert.assertNull(expectationWithResponseTemplate.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithResponseTemplate.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithResponseTemplate.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithResponseTemplate.getHttpError());
        // when
        Expectation expectationWithResponseClassCallback = buildObject();
        // then
        MatcherAssert.assertThat(expectationWithResponseClassCallback.getHttpRequest(), Is.is(httpRequest));
        MatcherAssert.assertThat(expectationWithResponseClassCallback.getTimes(), Is.is(Times.exactly(3)));
        Assert.assertNull(expectationWithResponseClassCallback.getHttpResponse());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpResponseTemplate());
        MatcherAssert.assertThat(expectationWithResponseClassCallback.getHttpResponseClassCallback(), Is.is(httpClassCallback));
        Assert.assertNull(expectationWithResponseClassCallback.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpForward());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpForwardTemplate());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithResponseClassCallback.getHttpError());
        // when
        Expectation expectationWithResponseObjectCallback = buildObject();
        // then
        MatcherAssert.assertThat(expectationWithResponseObjectCallback.getHttpRequest(), Is.is(httpRequest));
        MatcherAssert.assertThat(expectationWithResponseObjectCallback.getTimes(), Is.is(Times.exactly(3)));
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpResponse());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpResponseTemplate());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpResponseClassCallback());
        MatcherAssert.assertThat(expectationWithResponseObjectCallback.getHttpResponseObjectCallback(), Is.is(httpObjectCallback));
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpForward());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpForwardTemplate());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithResponseObjectCallback.getHttpError());
        // when
        Expectation expectationWithForward = buildObject();
        // then
        MatcherAssert.assertThat(expectationWithForward.getHttpRequest(), Is.is(httpRequest));
        MatcherAssert.assertThat(expectationWithForward.getTimes(), Is.is(Times.exactly(3)));
        Assert.assertNull(expectationWithForward.getHttpResponse());
        Assert.assertNull(expectationWithForward.getHttpResponseTemplate());
        Assert.assertNull(expectationWithForward.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithForward.getHttpResponseObjectCallback());
        MatcherAssert.assertThat(expectationWithForward.getHttpForward(), Is.is(httpForward));
        Assert.assertNull(expectationWithForward.getHttpForwardTemplate());
        Assert.assertNull(expectationWithForward.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithForward.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithForward.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithForward.getHttpError());
        // when
        Expectation expectationWithForwardTemplate = buildObject();
        // then
        MatcherAssert.assertThat(expectationWithForwardTemplate.getHttpRequest(), Is.is(httpRequest));
        MatcherAssert.assertThat(expectationWithForwardTemplate.getTimes(), Is.is(Times.exactly(3)));
        Assert.assertNull(expectationWithForwardTemplate.getHttpResponse());
        Assert.assertNull(expectationWithForwardTemplate.getHttpResponseTemplate());
        Assert.assertNull(expectationWithForwardTemplate.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithForwardTemplate.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithForwardTemplate.getHttpForward());
        MatcherAssert.assertThat(expectationWithForwardTemplate.getHttpForwardTemplate(), Is.is(httpForwardTemplate));
        Assert.assertNull(expectationWithForwardTemplate.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithForwardTemplate.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithForwardTemplate.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithForwardTemplate.getHttpError());
        // when
        Expectation expectationWithForwardClassCallback = buildObject();
        // then
        MatcherAssert.assertThat(expectationWithForwardClassCallback.getHttpRequest(), Is.is(httpRequest));
        MatcherAssert.assertThat(expectationWithForwardClassCallback.getTimes(), Is.is(Times.exactly(3)));
        Assert.assertNull(expectationWithForwardClassCallback.getHttpResponse());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpResponseTemplate());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpForward());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpForwardTemplate());
        MatcherAssert.assertThat(expectationWithForwardClassCallback.getHttpForwardClassCallback(), Is.is(httpClassCallback));
        Assert.assertNull(expectationWithForwardClassCallback.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithForwardClassCallback.getHttpError());
        // when
        Expectation expectationWithForwardObjectCallback = buildObject();
        // then
        MatcherAssert.assertThat(expectationWithForwardObjectCallback.getHttpRequest(), Is.is(httpRequest));
        MatcherAssert.assertThat(expectationWithForwardObjectCallback.getTimes(), Is.is(Times.exactly(3)));
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpResponse());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpResponseTemplate());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpForward());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpForwardTemplate());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpForwardClassCallback());
        MatcherAssert.assertThat(expectationWithForwardObjectCallback.getHttpForwardObjectCallback(), Is.is(httpObjectCallback));
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpOverrideForwardedRequest());
        Assert.assertNull(expectationWithForwardObjectCallback.getHttpError());
        // when
        Expectation expectationWithOverrideForwardedRequest = buildObject();
        // then
        MatcherAssert.assertThat(expectationWithOverrideForwardedRequest.getHttpRequest(), Is.is(httpRequest));
        MatcherAssert.assertThat(expectationWithOverrideForwardedRequest.getTimes(), Is.is(Times.exactly(3)));
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpResponse());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpResponseTemplate());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpForward());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpForwardTemplate());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpForwardObjectCallback());
        MatcherAssert.assertThat(expectationWithOverrideForwardedRequest.getHttpOverrideForwardedRequest(), Is.is(httpOverrideForwardedRequest));
        Assert.assertNull(expectationWithOverrideForwardedRequest.getHttpError());
        // when
        Expectation expectationWithError = buildObject();
        // then
        MatcherAssert.assertThat(expectationWithError.getHttpRequest(), Is.is(httpRequest));
        MatcherAssert.assertThat(expectationWithError.getTimes(), Is.is(Times.exactly(3)));
        Assert.assertNull(expectationWithError.getHttpResponse());
        Assert.assertNull(expectationWithError.getHttpResponseTemplate());
        Assert.assertNull(expectationWithError.getHttpResponseClassCallback());
        Assert.assertNull(expectationWithError.getHttpResponseObjectCallback());
        Assert.assertNull(expectationWithError.getHttpForward());
        Assert.assertNull(expectationWithError.getHttpForwardTemplate());
        Assert.assertNull(expectationWithError.getHttpForwardClassCallback());
        Assert.assertNull(expectationWithError.getHttpForwardObjectCallback());
        Assert.assertNull(expectationWithError.getHttpOverrideForwardedRequest());
        MatcherAssert.assertThat(expectationWithError.getHttpError(), Is.is(httpError));
    }

    @Test
    public void shouldBuildObjectWithNulls() {
        // when
        Expectation expectation = buildObject();
        // then
        MatcherAssert.assertThat(expectation.getHttpRequest(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectation.getTimes(), Is.is(Times.unlimited()));
        MatcherAssert.assertThat(expectation.getHttpResponse(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectation.getHttpResponseTemplate(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectation.getHttpResponseClassCallback(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectation.getHttpResponseObjectCallback(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectation.getHttpForward(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectation.getHttpForwardTemplate(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectation.getHttpForwardClassCallback(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectation.getHttpForwardObjectCallback(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectation.getHttpOverrideForwardedRequest(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectation.getHttpError(), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldReturnValuesSetInSetter() {
        // given
        HttpRequestDTO httpRequest = new HttpRequestDTO(new HttpRequest().withBody("some_body"));
        org.mockserver.serialization.model.TimesDTO times = new org.mockserver.serialization.model.TimesDTO(Times.exactly(3));
        HttpResponseDTO httpResponse = new HttpResponseDTO(new HttpResponse().withBody("some_response_body"));
        HttpTemplateDTO httpResponseTemplate = new HttpTemplateDTO(new HttpTemplate(HttpTemplate.TemplateType.JAVASCRIPT).withTemplate("some_repoonse_template"));
        HttpClassCallbackDTO httpResponseClassCallback = new HttpClassCallbackDTO(new HttpClassCallback().withCallbackClass("some_response_class"));
        HttpObjectCallbackDTO httpResponseObjectCallback = new HttpObjectCallbackDTO(new HttpObjectCallback().withClientId("some_response_client_id"));
        HttpForwardDTO httpForward = new HttpForwardDTO(new HttpForward().withHost("some_host"));
        HttpTemplateDTO httpForwardTemplate = new HttpTemplateDTO(new HttpTemplate(HttpTemplate.TemplateType.VELOCITY).withTemplate("some_forward_template"));
        HttpClassCallbackDTO httpForwardClassCallback = new HttpClassCallbackDTO(new HttpClassCallback().withCallbackClass("some_forward_class"));
        HttpObjectCallbackDTO httpForwardObjectCallback = new HttpObjectCallbackDTO(new HttpObjectCallback().withClientId("some_forward_client_id"));
        HttpOverrideForwardedRequestDTO httpOverrideForwardedRequest = new HttpOverrideForwardedRequestDTO(new HttpOverrideForwardedRequest().withHttpRequest(request("some_path")));
        HttpErrorDTO httpError = new HttpErrorDTO(new HttpError().withResponseBytes("some_bytes".getBytes(StandardCharsets.UTF_8)));
        // when
        ExpectationDTO expectation = new ExpectationDTO();
        expectation.setHttpRequest(httpRequest);
        expectation.setTimes(times);
        expectation.setHttpResponse(httpResponse);
        expectation.setHttpResponseTemplate(httpResponseTemplate);
        expectation.setHttpResponseClassCallback(httpResponseClassCallback);
        expectation.setHttpResponseObjectCallback(httpResponseObjectCallback);
        expectation.setHttpForward(httpForward);
        expectation.setHttpForwardTemplate(httpForwardTemplate);
        expectation.setHttpForwardClassCallback(httpForwardClassCallback);
        expectation.setHttpForwardObjectCallback(httpForwardObjectCallback);
        expectation.setHttpOverrideForwardedRequest(httpOverrideForwardedRequest);
        expectation.setHttpError(httpError);
        // then
        MatcherAssert.assertThat(expectation.getHttpRequest(), Is.is(httpRequest));
        MatcherAssert.assertThat(expectation.getTimes(), Is.is(times));
        MatcherAssert.assertThat(expectation.getHttpResponse(), Is.is(httpResponse));
        MatcherAssert.assertThat(expectation.getHttpResponseTemplate(), Is.is(httpResponseTemplate));
        MatcherAssert.assertThat(expectation.getHttpResponseClassCallback(), Is.is(httpResponseClassCallback));
        MatcherAssert.assertThat(expectation.getHttpResponseObjectCallback(), Is.is(httpResponseObjectCallback));
        MatcherAssert.assertThat(expectation.getHttpForward(), Is.is(httpForward));
        MatcherAssert.assertThat(expectation.getHttpForwardTemplate(), Is.is(httpForwardTemplate));
        MatcherAssert.assertThat(expectation.getHttpForwardClassCallback(), Is.is(httpForwardClassCallback));
        MatcherAssert.assertThat(expectation.getHttpForwardObjectCallback(), Is.is(httpForwardObjectCallback));
        MatcherAssert.assertThat(expectation.getHttpOverrideForwardedRequest(), Is.is(httpOverrideForwardedRequest));
        MatcherAssert.assertThat(expectation.getHttpError(), Is.is(httpError));
    }

    @Test
    public void shouldHandleNullObjectInput() {
        // when
        ExpectationDTO expectationDTO = new ExpectationDTO(null);
        // then
        MatcherAssert.assertThat(expectationDTO.getHttpRequest(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getTimes(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpResponse(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpResponseTemplate(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpResponseClassCallback(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpResponseObjectCallback(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpForward(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpForwardTemplate(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpForwardClassCallback(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpForwardObjectCallback(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpOverrideForwardedRequest(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpError(), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldHandleNullFieldInput() {
        // when
        ExpectationDTO expectationDTO = new ExpectationDTO(new Expectation(null, null, TimeToLive.unlimited()));
        // then
        MatcherAssert.assertThat(expectationDTO.getHttpRequest(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getTimes(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpResponse(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpResponseTemplate(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpResponseClassCallback(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpResponseObjectCallback(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpForward(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpForwardTemplate(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpForwardClassCallback(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpForwardObjectCallback(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpOverrideForwardedRequest(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(expectationDTO.getHttpError(), Is.is(CoreMatchers.nullValue()));
    }
}

