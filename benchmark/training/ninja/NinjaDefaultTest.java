/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja;


import Context.Impl;
import NinjaConstant.DIAGNOSTICS_KEY_NAME;
import NinjaConstant.I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_DEFAULT;
import NinjaConstant.I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_KEY;
import NinjaConstant.I18N_NINJA_SYSTEM_FORBIDDEN_REQUEST_TEXT_DEFAULT;
import NinjaConstant.I18N_NINJA_SYSTEM_FORBIDDEN_REQUEST_TEXT_KEY;
import NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT;
import NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_KEY;
import NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_DEFAULT;
import NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_KEY;
import NinjaConstant.LOCATION_VIEW_FTL_HTML_BAD_REQUEST;
import NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN;
import NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR;
import NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND;
import NinjaConstant.LOCATION_VIEW_HTML_BAD_REQUEST_KEY;
import NinjaConstant.LOCATION_VIEW_HTML_FORBIDDEN_KEY;
import NinjaConstant.LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY;
import NinjaConstant.LOCATION_VIEW_HTML_NOT_FOUND_KEY;
import Result.APPLICATION_JSON;
import Result.SC_400_BAD_REQUEST;
import Result.SC_403_FORBIDDEN;
import Result.SC_404_NOT_FOUND;
import Result.SC_500_INTERNAL_SERVER_ERROR;
import Result.TEXT_HTML;
import java.util.Optional;
import ninja.diagnostics.DiagnosticError;
import ninja.exceptions.BadRequestException;
import ninja.exceptions.ForbiddenRequestException;
import ninja.exceptions.InternalServerErrorException;
import ninja.exceptions.RequestNotFoundException;
import ninja.i18n.Messages;
import ninja.lifecycle.LifecycleService;
import ninja.utils.Message;
import ninja.utils.NinjaProperties;
import ninja.utils.ResultHandler;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class NinjaDefaultTest {
    @Mock
    LifecycleService lifecylceService;

    @Mock
    ResultHandler resultHandler;

    @Mock
    Router router;

    @Mock
    Impl contextImpl;

    @Mock
    Messages messages;

    @Mock
    Result result;

    @Mock
    NinjaProperties ninjaProperties;

    Route route;

    @Captor
    ArgumentCaptor<Result> resultCaptor;

    NinjaDefault ninjaDefault;

    @Test
    public void testOnRouteRequestWhenEverythingWorks() throws Exception {
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        Result result = Mockito.mock(Result.class);
        Mockito.when(filterChain.next(contextImpl)).thenReturn(result);
        ninjaDefault.onRouteRequest(contextImpl);
        Mockito.verify(contextImpl).setRoute(route);
        Mockito.verify(resultHandler).handleResult(result, contextImpl);
        Mockito.verify(ninjaDefault, Mockito.never()).getInternalServerErrorResult(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(Exception.class), ArgumentMatchers.any(Result.class));
        Mockito.verify(ninjaDefault, Mockito.never()).getBadRequestResult(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(BadRequestException.class));
        Mockito.verify(ninjaDefault, Mockito.never()).getForbiddenResult(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(ForbiddenRequestException.class));
        Mockito.verify(ninjaDefault, Mockito.never()).getNotFoundResult(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(RequestNotFoundException.class));
        Mockito.verify(ninjaDefault, Mockito.never()).getNotFoundResult(ArgumentMatchers.any(Context.class));
    }

    @Test
    public void testOnRouteRequestWhenException() throws Exception {
        Exception exception = new RuntimeException("That's a very generic exception that should be handled by onError!");
        Mockito.when(messages.getWithDefault(Matchers.eq(I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_KEY), Matchers.eq(I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT), ArgumentMatchers.any(Optional.class))).thenReturn(I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT);
        Mockito.when(messages.getWithDefault(Matchers.eq(exception.getMessage()), Matchers.eq(exception.getLocalizedMessage()), ArgumentMatchers.any(Optional.class))).thenReturn(exception.getLocalizedMessage());
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        Mockito.when(filterChain.next(contextImpl)).thenThrow(exception);
        ninjaDefault.onRouteRequest(contextImpl);
        Mockito.verify(ninjaDefault).getInternalServerErrorResult(contextImpl, exception, null);
    }

    @Test
    public void testOnRouteRequestWhenInternalServerErrorException() throws Exception {
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        InternalServerErrorException internalServerErrorException = new InternalServerErrorException("That's an InternalServerErrorException that should be handled by onError!");
        Mockito.when(filterChain.next(contextImpl)).thenThrow(internalServerErrorException);
        ninjaDefault.onRouteRequest(contextImpl);
        Mockito.verify(ninjaDefault).getInternalServerErrorResult(contextImpl, internalServerErrorException, null);
    }

    @Test
    public void testOnRouteRequestWhenInternalServerErrorExceptionInDiagnosticMode() throws Exception {
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        InternalServerErrorException internalServerErrorException = new InternalServerErrorException("That's an InternalServerErrorException that should be handled by onError!");
        Mockito.when(filterChain.next(contextImpl)).thenThrow(internalServerErrorException);
        Mockito.when(ninjaProperties.isDev()).thenReturn(true);
        Mockito.when(ninjaProperties.getBooleanWithDefault(DIAGNOSTICS_KEY_NAME, true)).thenReturn(true);
        ninjaDefault.onRouteRequest(contextImpl);
        Result localResult = ninjaDefault.getInternalServerErrorResult(contextImpl, internalServerErrorException, null);
        Assert.assertThat(localResult.getRenderable(), CoreMatchers.instanceOf(DiagnosticError.class));
    }

    @Test
    public void testOnRouteRequestWhenOnBadRequest() throws Exception {
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        BadRequestException badRequest = new BadRequestException("That's a BadRequest that should be handled by onBadRequest");
        Mockito.when(filterChain.next(contextImpl)).thenThrow(badRequest);
        ninjaDefault.onRouteRequest(contextImpl);
        Mockito.verify(ninjaDefault).getBadRequestResult(contextImpl, badRequest);
    }

    @Test
    public void testOnRouteRequestWhenOnBadRequestInDiagnosticMode() throws Exception {
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        BadRequestException badRequest = new BadRequestException("That's a BadRequest that should be handled by onBadRequest");
        Mockito.when(filterChain.next(contextImpl)).thenThrow(badRequest);
        Mockito.when(ninjaProperties.isDev()).thenReturn(true);
        Mockito.when(ninjaProperties.getBooleanWithDefault(DIAGNOSTICS_KEY_NAME, true)).thenReturn(true);
        ninjaDefault.onRouteRequest(contextImpl);
        Result localResult = ninjaDefault.getBadRequestResult(contextImpl, badRequest);
        Assert.assertThat(localResult.getRenderable(), CoreMatchers.instanceOf(DiagnosticError.class));
    }

    @Test
    public void testOnRouteRequestWhenForbiddenRequest() throws Exception {
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        ForbiddenRequestException forbiddenRequest = new ForbiddenRequestException("That's a ForbiddenRequest that should be handled by onForbiddenRequest");
        Mockito.when(filterChain.next(contextImpl)).thenThrow(forbiddenRequest);
        ninjaDefault.onRouteRequest(contextImpl);
        Mockito.verify(ninjaDefault).getForbiddenResult(contextImpl, forbiddenRequest);
    }

    @Test
    public void testOnRouteRequestWhenForbiddenRequestInDiagnosticMode() throws Exception {
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        ForbiddenRequestException forbiddenRequest = new ForbiddenRequestException("That's a ForbiddenRequest that should be handled by onForbiddenRequest");
        Mockito.when(filterChain.next(contextImpl)).thenThrow(forbiddenRequest);
        Mockito.when(ninjaProperties.isDev()).thenReturn(true);
        Mockito.when(ninjaProperties.getBooleanWithDefault(DIAGNOSTICS_KEY_NAME, true)).thenReturn(true);
        ninjaDefault.onRouteRequest(contextImpl);
        Result localResult = ninjaDefault.getForbiddenResult(contextImpl, forbiddenRequest);
        Assert.assertThat(localResult.getRenderable(), CoreMatchers.instanceOf(DiagnosticError.class));
    }

    @Test
    public void testOnRouteRequestWhenOnNotFound() throws Exception {
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        // This simulates that a route has not been found
        // subsequently the onNotFound method should be called.
        Mockito.when(router.getRouteFor(Matchers.anyString(), Matchers.anyString())).thenReturn(null);
        ninjaDefault.onRouteRequest(contextImpl);
        Mockito.verify(ninjaDefault).getNotFoundResult(contextImpl);
    }

    @Test
    public void testOnRouteRequestWhenOnNotFoundInDiagnosticMode() throws Exception {
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        // This simulates that a route has not been found
        // subsequently the onNotFound method should be called.
        Mockito.when(router.getRouteFor(Matchers.anyString(), Matchers.anyString())).thenReturn(null);
        Mockito.when(ninjaProperties.isDev()).thenReturn(true);
        Mockito.when(ninjaProperties.getBooleanWithDefault(DIAGNOSTICS_KEY_NAME, true)).thenReturn(true);
        ninjaDefault.onRouteRequest(contextImpl);
        Result localResult = ninjaDefault.getNotFoundResult(contextImpl);
        Assert.assertThat(localResult.getRenderable(), CoreMatchers.instanceOf(DiagnosticError.class));
    }

    @Test
    public void testOnRouteRequestWhenOnNotFoundException() throws Exception {
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        RequestNotFoundException notFoundRequest = new RequestNotFoundException("That's a RequestNotFoundException that should be handled by onNotFound");
        Mockito.when(filterChain.next(contextImpl)).thenThrow(notFoundRequest);
        ninjaDefault.onRouteRequest(contextImpl);
        Mockito.verify(ninjaDefault).getNotFoundResult(contextImpl, notFoundRequest);
    }

    @Test
    public void testOnRouteRequestWhenOnNotFoundExceptionInDiagnosticMode() throws Exception {
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        RequestNotFoundException notFoundRequest = new RequestNotFoundException("That's a RequestNotFoundException that should be handled by onNotFound");
        Mockito.when(filterChain.next(contextImpl)).thenThrow(notFoundRequest);
        Mockito.when(ninjaProperties.isDev()).thenReturn(true);
        Mockito.when(ninjaProperties.getBooleanWithDefault(DIAGNOSTICS_KEY_NAME, true)).thenReturn(true);
        ninjaDefault.onRouteRequest(contextImpl);
        Result localResult = ninjaDefault.getNotFoundResult(contextImpl, notFoundRequest);
        Assert.assertThat(localResult.getRenderable(), CoreMatchers.instanceOf(DiagnosticError.class));
    }

    @Test
    public void testOnExceptionBadRequest() {
        BadRequestException badRequestException = new BadRequestException();
        Result result = ninjaDefault.onException(contextImpl, badRequestException);
        Mockito.verify(ninjaDefault).getBadRequestResult(contextImpl, badRequestException);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.equalTo(SC_400_BAD_REQUEST));
    }

    @Test
    public void testOnExceptionForbiddenRequest() {
        ForbiddenRequestException forbiddenRequestException = new ForbiddenRequestException();
        Result result = ninjaDefault.onException(contextImpl, forbiddenRequestException);
        Mockito.verify(ninjaDefault).getForbiddenResult(contextImpl, forbiddenRequestException);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.equalTo(SC_403_FORBIDDEN));
    }

    @Test
    public void testOnExceptionRequestNotFound() {
        RequestNotFoundException notFoundRequestException = new RequestNotFoundException();
        Result result = ninjaDefault.onException(contextImpl, notFoundRequestException);
        Mockito.verify(ninjaDefault).getNotFoundResult(contextImpl, notFoundRequestException);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.equalTo(SC_404_NOT_FOUND));
    }

    @Test
    public void testOnExceptionCatchAll() {
        Exception anyException = new Exception();
        Result result = ninjaDefault.onException(contextImpl, anyException);
        Mockito.verify(ninjaDefault).getInternalServerErrorResult(contextImpl, anyException, null);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.equalTo(SC_500_INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testThatGetInternalServerErrorContentNegotiation() throws Exception {
        Mockito.when(contextImpl.getAcceptContentType()).thenReturn(APPLICATION_JSON);
        Result result = ninjaDefault.getInternalServerErrorResult(contextImpl, new Exception("not important"), null);
        Assert.assertThat(result.getContentType(), CoreMatchers.equalTo(null));
        Assert.assertThat(result.supportedContentTypes().size(), CoreMatchers.equalTo(3));
    }

    @Test
    public void testThatGetInternalServerErrorDoesFallsBackToHtml() throws Exception {
        Mockito.when(contextImpl.getAcceptContentType()).thenReturn("not_supported");
        Result result = ninjaDefault.getInternalServerErrorResult(contextImpl, new Exception("not important"), null);
        Assert.assertThat(result.fallbackContentType().get(), CoreMatchers.equalTo(TEXT_HTML));
    }

    @Test
    public void getInternalServerErrorResult() throws Exception {
        Mockito.when(ninjaProperties.getWithDefault(Matchers.eq(LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY), Matchers.eq(LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR))).thenReturn(LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR);
        Exception exception = new Exception("not important");
        // real test:
        Result result = ninjaDefault.getInternalServerErrorResult(contextImpl, exception, null);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.equalTo(SC_500_INTERNAL_SERVER_ERROR));
        Assert.assertThat(result.getTemplate(), CoreMatchers.equalTo(LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR));
        Assert.assertTrue(((result.getRenderable()) instanceof Message));
        Mockito.verify(messages).getWithDefault(Matchers.eq(I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_KEY), Matchers.eq(I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT), Matchers.eq(contextImpl), ArgumentMatchers.any(Optional.class));
        Mockito.verify(messages).getWithDefault(Matchers.eq(exception.getMessage()), Matchers.eq(exception.getLocalizedMessage()), Matchers.eq(contextImpl), ArgumentMatchers.any(Optional.class));
        Mockito.verify(ninjaProperties).getWithDefault(Matchers.eq(LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY), Matchers.eq(LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testThatGetBadRequestContentNegotiation() throws Exception {
        Mockito.when(contextImpl.getAcceptContentType()).thenReturn(APPLICATION_JSON);
        Result result = ninjaDefault.getBadRequestResult(contextImpl, new BadRequestException("not important"));
        Assert.assertThat(result.getContentType(), CoreMatchers.equalTo(null));
        Assert.assertThat(result.supportedContentTypes().size(), CoreMatchers.equalTo(3));
    }

    @Test
    public void testThatGetBadRequestDoesFallsBackToHtml() throws Exception {
        Mockito.when(contextImpl.getAcceptContentType()).thenReturn("not_supported");
        Result result = ninjaDefault.getBadRequestResult(contextImpl, new BadRequestException("not important"));
        Assert.assertThat(result.fallbackContentType().get(), CoreMatchers.equalTo(TEXT_HTML));
    }

    @Test
    public void testGetBadRequest() throws Exception {
        Mockito.when(ninjaProperties.getWithDefault(Matchers.eq(LOCATION_VIEW_HTML_BAD_REQUEST_KEY), Matchers.eq(LOCATION_VIEW_FTL_HTML_BAD_REQUEST))).thenReturn(LOCATION_VIEW_FTL_HTML_BAD_REQUEST);
        BadRequestException exception = new BadRequestException("not important");
        // real test:
        Result result = ninjaDefault.getBadRequestResult(contextImpl, exception);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.equalTo(SC_400_BAD_REQUEST));
        Assert.assertThat(result.getTemplate(), CoreMatchers.equalTo(LOCATION_VIEW_FTL_HTML_BAD_REQUEST));
        Assert.assertTrue(((result.getRenderable()) instanceof Message));
        Mockito.verify(messages).getWithDefault(Matchers.eq(I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_KEY), Matchers.eq(I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_DEFAULT), Matchers.eq(contextImpl), ArgumentMatchers.any(Optional.class));
        Mockito.verify(messages).getWithDefault(Matchers.eq(exception.getMessage()), Matchers.eq(exception.getLocalizedMessage()), Matchers.eq(contextImpl), ArgumentMatchers.any(Optional.class));
        Mockito.verify(ninjaProperties).getWithDefault(Matchers.eq(LOCATION_VIEW_HTML_BAD_REQUEST_KEY), Matchers.eq(LOCATION_VIEW_FTL_HTML_BAD_REQUEST));
    }

    @Test
    public void testThatGetForbiddenRequestContentNegotiation() throws Exception {
        Mockito.when(contextImpl.getAcceptContentType()).thenReturn(APPLICATION_JSON);
        Result result = ninjaDefault.getForbiddenResult(contextImpl, new ForbiddenRequestException("not important"));
        Assert.assertThat(result.getContentType(), CoreMatchers.equalTo(null));
        Assert.assertThat(result.supportedContentTypes().size(), CoreMatchers.equalTo(3));
    }

    @Test
    public void testThatGetForbiddenRequestDoesFallsBackToHtml() throws Exception {
        Mockito.when(contextImpl.getAcceptContentType()).thenReturn("not_supported");
        Result result = ninjaDefault.getForbiddenResult(contextImpl, new ForbiddenRequestException("not important"));
        Assert.assertThat(result.fallbackContentType().get(), CoreMatchers.equalTo(TEXT_HTML));
    }

    @Test
    public void testGetForbiddenRequest() throws Exception {
        Mockito.when(ninjaProperties.getWithDefault(Matchers.eq(LOCATION_VIEW_HTML_FORBIDDEN_KEY), Matchers.eq(LOCATION_VIEW_FTL_HTML_FORBIDDEN))).thenReturn(LOCATION_VIEW_FTL_HTML_FORBIDDEN);
        ForbiddenRequestException exception = new ForbiddenRequestException("not important");
        // real test:
        Result result = ninjaDefault.getForbiddenResult(contextImpl, exception);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.equalTo(SC_403_FORBIDDEN));
        Assert.assertThat(result.getTemplate(), CoreMatchers.equalTo(LOCATION_VIEW_FTL_HTML_FORBIDDEN));
        Assert.assertTrue(((result.getRenderable()) instanceof Message));
        Mockito.verify(messages).getWithDefault(Matchers.eq(I18N_NINJA_SYSTEM_FORBIDDEN_REQUEST_TEXT_KEY), Matchers.eq(I18N_NINJA_SYSTEM_FORBIDDEN_REQUEST_TEXT_DEFAULT), Matchers.eq(contextImpl), ArgumentMatchers.any(Optional.class));
        Mockito.verify(messages).getWithDefault(Matchers.eq(exception.getMessage()), Matchers.eq(exception.getLocalizedMessage()), Matchers.eq(contextImpl), ArgumentMatchers.any(Optional.class));
        Mockito.verify(ninjaProperties).getWithDefault(Matchers.eq(LOCATION_VIEW_HTML_FORBIDDEN_KEY), Matchers.eq(LOCATION_VIEW_FTL_HTML_FORBIDDEN));
    }

    @Test
    public void testThatGetOnNotFoundDoesContentNegotiation() throws Exception {
        Mockito.when(contextImpl.getAcceptContentType()).thenReturn(APPLICATION_JSON);
        Result result = ninjaDefault.getNotFoundResult(contextImpl);
        Assert.assertThat(result.getContentType(), CoreMatchers.equalTo(null));
        Assert.assertThat(result.supportedContentTypes().size(), CoreMatchers.equalTo(3));
    }

    @Test
    public void testThatGetOnNotFoundDoesFallsBackToHtml() throws Exception {
        Mockito.when(contextImpl.getAcceptContentType()).thenReturn("not_supported");
        Result result = ninjaDefault.getNotFoundResult(contextImpl);
        Assert.assertThat(result.fallbackContentType().get(), CoreMatchers.equalTo(TEXT_HTML));
    }

    @Test
    public void testGetOnNotFoundResultWorks() throws Exception {
        Mockito.when(ninjaProperties.getWithDefault(Matchers.eq(LOCATION_VIEW_HTML_NOT_FOUND_KEY), Matchers.eq(LOCATION_VIEW_FTL_HTML_NOT_FOUND))).thenReturn(LOCATION_VIEW_FTL_HTML_NOT_FOUND);
        Result result = ninjaDefault.getNotFoundResult(contextImpl);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.equalTo(SC_404_NOT_FOUND));
        Assert.assertThat(result.getTemplate(), CoreMatchers.equalTo(LOCATION_VIEW_FTL_HTML_NOT_FOUND));
        Assert.assertTrue(((result.getRenderable()) instanceof Message));
        Mockito.verify(messages).getWithDefault(Matchers.eq(I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_KEY), Matchers.eq(I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_DEFAULT), Matchers.eq(contextImpl), ArgumentMatchers.any(Optional.class));
        Mockito.verify(ninjaProperties).getWithDefault(Matchers.eq(LOCATION_VIEW_HTML_NOT_FOUND_KEY), Matchers.eq(LOCATION_VIEW_FTL_HTML_NOT_FOUND));
    }

    @Test
    public void testThatGetOnNotFoundExceptionDoesContentNegotiation() throws Exception {
        Mockito.when(contextImpl.getAcceptContentType()).thenReturn(APPLICATION_JSON);
        Result result = ninjaDefault.getNotFoundResult(contextImpl, new RequestNotFoundException("not important"));
        Assert.assertThat(result.getContentType(), CoreMatchers.equalTo(null));
        Assert.assertThat(result.supportedContentTypes().size(), CoreMatchers.equalTo(3));
    }

    @Test
    public void testThatGetOnNotFoundExceptionDoesFallsBackToHtml() throws Exception {
        Mockito.when(contextImpl.getAcceptContentType()).thenReturn("not_supported");
        Result result = ninjaDefault.getNotFoundResult(contextImpl, new RequestNotFoundException("not important"));
        Assert.assertThat(result.fallbackContentType().get(), CoreMatchers.equalTo(TEXT_HTML));
    }

    @Test
    public void testGetOnNotFoundExceptionResultWorks() throws Exception {
        Mockito.when(ninjaProperties.getWithDefault(Matchers.eq(LOCATION_VIEW_HTML_NOT_FOUND_KEY), Matchers.eq(LOCATION_VIEW_FTL_HTML_NOT_FOUND))).thenReturn(LOCATION_VIEW_FTL_HTML_NOT_FOUND);
        RequestNotFoundException exception = new RequestNotFoundException("not important");
        // real test:
        Result result = ninjaDefault.getNotFoundResult(contextImpl, exception);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.equalTo(SC_404_NOT_FOUND));
        Assert.assertThat(result.getTemplate(), CoreMatchers.equalTo(LOCATION_VIEW_FTL_HTML_NOT_FOUND));
        Assert.assertTrue(((result.getRenderable()) instanceof Message));
        Mockito.verify(messages).getWithDefault(Matchers.eq(I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_KEY), Matchers.eq(I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_DEFAULT), Matchers.eq(contextImpl), ArgumentMatchers.any(Optional.class));
        Mockito.verify(messages).getWithDefault(Matchers.eq(exception.getMessage()), Matchers.eq(exception.getLocalizedMessage()), Matchers.eq(contextImpl), ArgumentMatchers.any(Optional.class));
        Mockito.verify(ninjaProperties).getWithDefault(Matchers.eq(LOCATION_VIEW_HTML_NOT_FOUND_KEY), Matchers.eq(LOCATION_VIEW_FTL_HTML_NOT_FOUND));
    }

    @Test
    public void testOnFrameworkStart() {
        ninjaDefault.onFrameworkStart();
        Mockito.verify(lifecylceService).start();
    }

    @Test
    public void testOnFrameworkShutdown() {
        ninjaDefault.onFrameworkShutdown();
        Mockito.verify(lifecylceService).stop();
    }

    @Test
    public void testRenderErrorResultAndCatchAndLogExceptionsAsync() {
        Mockito.when(contextImpl.isAsync()).thenReturn(true);
        ninjaDefault.renderErrorResultAndCatchAndLogExceptions(result, contextImpl);
        Mockito.verify(contextImpl).isAsync();
        Mockito.verify(contextImpl).returnResultAsync(result);
    }

    @Test
    public void testRenderErrorResultAndCatchAndLogExceptionsSync() {
        Mockito.when(contextImpl.isAsync()).thenReturn(false);
        ninjaDefault.renderErrorResultAndCatchAndLogExceptions(result, contextImpl);
        Mockito.verify(resultHandler).handleResult(result, contextImpl);
    }

    @Test
    public void testIsDiagnosticsEnabled_TrueInDevAndWithProperNinjaPropertiesConfig() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(DIAGNOSTICS_KEY_NAME, Boolean.TRUE)).thenReturn(true);
        Mockito.when(ninjaProperties.isDev()).thenReturn(true);
        Assert.assertThat(ninjaDefault.isDiagnosticsEnabled(), CoreMatchers.equalTo(true));
        Mockito.verify(ninjaProperties).isDev();
        Mockito.verify(ninjaProperties).getBooleanWithDefault(DIAGNOSTICS_KEY_NAME, Boolean.TRUE);
    }

    @Test
    public void testIsDiagnosticsEnabled_FalseDisabledWhenNotInDev() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(DIAGNOSTICS_KEY_NAME, Boolean.TRUE)).thenReturn(true);
        Mockito.when(ninjaProperties.isDev()).thenReturn(false);
        Assert.assertThat(ninjaDefault.isDiagnosticsEnabled(), CoreMatchers.equalTo(false));
        Mockito.verify(ninjaProperties).isDev();
        Mockito.verify(ninjaProperties, Mockito.never()).getBooleanWithDefault(DIAGNOSTICS_KEY_NAME, Boolean.TRUE);
    }

    @Test
    public void testIsDiagnosticsEnabled_FalseWheInDevButDisabledInNinjaPropertiesConfig() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(DIAGNOSTICS_KEY_NAME, Boolean.TRUE)).thenReturn(false);
        Mockito.when(ninjaProperties.isDev()).thenReturn(true);
        Assert.assertThat(ninjaDefault.isDiagnosticsEnabled(), CoreMatchers.equalTo(false));
        Mockito.verify(ninjaProperties).isDev();
        Mockito.verify(ninjaProperties).getBooleanWithDefault(DIAGNOSTICS_KEY_NAME, Boolean.TRUE);
    }
}

