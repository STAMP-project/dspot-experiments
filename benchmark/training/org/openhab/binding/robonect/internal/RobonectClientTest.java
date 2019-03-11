/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.robonect.internal;


import HttpMethod.GET;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.robonect.internal.model.ErrorList;
import org.openhab.binding.robonect.internal.model.MowerInfo;
import org.openhab.binding.robonect.internal.model.Name;
import org.openhab.binding.robonect.internal.model.VersionInfo;


/**
 * The goal of this class is to test the functionality of the RobonectClient,
 * by mocking the module responses.
 *
 * @author Marco Meyer - Initial contribution
 */
public class RobonectClientTest {
    private RobonectClient subject;

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private ContentResponse responseMock;

    @Mock
    private Request requestMock;

    @Test
    public void shouldCallStatusCommand() throws InterruptedException, ExecutionException, TimeoutException {
        Mockito.when(httpClientMock.newRequest("http://123.456.789.123/json?cmd=status")).thenReturn(requestMock);
        Mockito.when(requestMock.method(GET)).thenReturn(requestMock);
        Mockito.when(requestMock.timeout(30000L, TimeUnit.MILLISECONDS)).thenReturn(requestMock);
        Mockito.when(requestMock.send()).thenReturn(responseMock);
        Mockito.when(responseMock.getEncoding()).thenReturn("utf8");
        Mockito.when(responseMock.getContentAsString()).thenReturn("{\"successful\": true, \"name\": \"Mein Automower\", \"status\": {\"status\": 17, \"stopped\": false, \"duration\": 4359, \"mode\": 0, \"battery\": 100, \"hours\": 29}, \"timer\": {\"status\": 2, \"next\": {\"date\": \"01.05.2017\", \"time\": \"19:00:00\", \"unix\": 1493665200}}, \"wlan\": {\"signal\": -76}}");
        subject.getMowerInfo();
        Mockito.verify(httpClientMock, Mockito.times(1)).newRequest("http://123.456.789.123/json?cmd=status");
    }

    @Test
    public void shouldCallStartCommand() throws InterruptedException, ExecutionException, TimeoutException {
        Mockito.when(httpClientMock.newRequest("http://123.456.789.123/json?cmd=start")).thenReturn(requestMock);
        Mockito.when(requestMock.method(GET)).thenReturn(requestMock);
        Mockito.when(requestMock.timeout(30000L, TimeUnit.MILLISECONDS)).thenReturn(requestMock);
        Mockito.when(requestMock.send()).thenReturn(responseMock);
        Mockito.when(responseMock.getEncoding()).thenReturn("utf8");
        Mockito.when(responseMock.getContentAsString()).thenReturn("{\"successful\": true}");
        subject.start();
        Mockito.verify(httpClientMock, Mockito.times(1)).newRequest("http://123.456.789.123/json?cmd=start");
    }

    @Test
    public void shouldCallStopCommand() throws InterruptedException, ExecutionException, TimeoutException {
        Mockito.when(httpClientMock.newRequest("http://123.456.789.123/json?cmd=stop")).thenReturn(requestMock);
        Mockito.when(requestMock.method(GET)).thenReturn(requestMock);
        Mockito.when(requestMock.timeout(30000L, TimeUnit.MILLISECONDS)).thenReturn(requestMock);
        Mockito.when(requestMock.send()).thenReturn(responseMock);
        Mockito.when(responseMock.getEncoding()).thenReturn("utf8");
        Mockito.when(responseMock.getContentAsString()).thenReturn("{\"successful\": true}");
        subject.stop();
        Mockito.verify(httpClientMock, Mockito.times(1)).newRequest("http://123.456.789.123/json?cmd=stop");
    }

    @Test
    public void shouldResetErrors() throws InterruptedException, ExecutionException, TimeoutException {
        Mockito.when(httpClientMock.newRequest("http://123.456.789.123/json?cmd=error&reset")).thenReturn(requestMock);
        Mockito.when(requestMock.method(GET)).thenReturn(requestMock);
        Mockito.when(requestMock.timeout(30000L, TimeUnit.MILLISECONDS)).thenReturn(requestMock);
        Mockito.when(requestMock.send()).thenReturn(responseMock);
        Mockito.when(responseMock.getEncoding()).thenReturn("utf8");
        Mockito.when(responseMock.getContentAsString()).thenReturn("{\"successful\": true}");
        subject.resetErrors();
        Mockito.verify(httpClientMock, Mockito.times(1)).newRequest("http://123.456.789.123/json?cmd=error&reset");
    }

    @Test
    public void shouldRetrieveName() throws InterruptedException, ExecutionException, TimeoutException {
        Mockito.when(httpClientMock.newRequest("http://123.456.789.123/json?cmd=name")).thenReturn(requestMock);
        Mockito.when(requestMock.method(GET)).thenReturn(requestMock);
        Mockito.when(requestMock.timeout(30000L, TimeUnit.MILLISECONDS)).thenReturn(requestMock);
        Mockito.when(requestMock.send()).thenReturn(responseMock);
        Mockito.when(responseMock.getEncoding()).thenReturn("utf8");
        Mockito.when(responseMock.getContentAsString()).thenReturn("{\"successful\": true, \"name\": \"hugo\"}");
        Name name = subject.getName();
        Assert.assertEquals("hugo", name.getName());
        Mockito.verify(httpClientMock, Mockito.times(1)).newRequest("http://123.456.789.123/json?cmd=name");
    }

    @Test
    public void shouldSetNewName() throws InterruptedException, ExecutionException, TimeoutException {
        Mockito.when(httpClientMock.newRequest("http://123.456.789.123/json?cmd=name&name=MyRobo")).thenReturn(requestMock);
        Mockito.when(requestMock.method(GET)).thenReturn(requestMock);
        Mockito.when(requestMock.timeout(30000L, TimeUnit.MILLISECONDS)).thenReturn(requestMock);
        Mockito.when(requestMock.send()).thenReturn(responseMock);
        Mockito.when(responseMock.getEncoding()).thenReturn("utf8");
        Mockito.when(responseMock.getContentAsString()).thenReturn("{\"successful\": true, \"name\": \"MyRobo\"}");
        Name name = subject.setName("MyRobo");
        Assert.assertEquals("MyRobo", name.getName());
        Mockito.verify(httpClientMock, Mockito.times(1)).newRequest("http://123.456.789.123/json?cmd=name&name=MyRobo");
    }

    @Test
    public void shouldListErrors() throws InterruptedException, ExecutionException, TimeoutException {
        Mockito.when(httpClientMock.newRequest("http://123.456.789.123/json?cmd=error")).thenReturn(requestMock);
        Mockito.when(requestMock.method(GET)).thenReturn(requestMock);
        Mockito.when(requestMock.timeout(30000L, TimeUnit.MILLISECONDS)).thenReturn(requestMock);
        Mockito.when(requestMock.send()).thenReturn(responseMock);
        Mockito.when(responseMock.getEncoding()).thenReturn("utf8");
        Mockito.when(responseMock.getContentAsString()).thenReturn("{\"errors\": [{\"error_code\": 33, \"error_message\": \"Grasi ist gekippt\", \"date\": \"04.05.2017\", \"time\": \"22:22:17\", \"unix\": 1493936537}, {\"error_code\": 15, \"error_message\": \"Grasi ist angehoben\", \"date\": \"02.05.2017\", \"time\": \"20:36:43\", \"unix\": 1493757403}, {\"error_code\": 33, \"error_message\": \"Grasi ist gekippt\", \"date\": \"26.04.2017\", \"time\": \"21:31:18\", \"unix\": 1493242278}, {\"error_code\": 13, \"error_message\": \"Kein Antrieb\", \"date\": \"21.04.2017\", \"time\": \"20:17:22\", \"unix\": 1492805842}, {\"error_code\": 10, \"error_message\": \"Grasi ist umgedreht\", \"date\": \"20.04.2017\", \"time\": \"20:14:37\", \"unix\": 1492719277}, {\"error_code\": 1, \"error_message\": \"Grasi hat Arbeitsbereich \u00fcberschritten\", \"date\": \"12.04.2017\", \"time\": \"19:10:09\", \"unix\": 1492024209}, {\"error_code\": 33, \"error_message\": \"Grasi ist gekippt\", \"date\": \"10.04.2017\", \"time\": \"22:59:35\", \"unix\": 1491865175}, {\"error_code\": 1, \"error_message\": \"Grasi hat Arbeitsbereich \u00fcberschritten\", \"date\": \"10.04.2017\", \"time\": \"21:21:55\", \"unix\": 1491859315}, {\"error_code\": 33, \"error_message\": \"Grasi ist gekippt\", \"date\": \"10.04.2017\", \"time\": \"20:26:13\", \"unix\": 1491855973}, {\"error_code\": 1, \"error_message\": \"Grasi hat Arbeitsbereich \u00fcberschritten\", \"date\": \"09.04.2017\", \"time\": \"14:50:36\", \"unix\": 1491749436}, {\"error_code\": 33, \"error_message\": \"Grasi ist gekippt\", \"date\": \"09.04.2017\", \"time\": \"14:23:27\", \"unix\": 1491747807}], \"successful\": true}");
        ErrorList list = subject.errorList();
        Assert.assertEquals(11, list.getErrors().size());
        Mockito.verify(httpClientMock, Mockito.times(1)).newRequest("http://123.456.789.123/json?cmd=error");
    }

    @Test
    public void shouldRetrieveVersionInfo() throws InterruptedException, ExecutionException, TimeoutException {
        Mockito.when(httpClientMock.newRequest("http://123.456.789.123/json?cmd=version")).thenReturn(requestMock);
        Mockito.when(requestMock.method(GET)).thenReturn(requestMock);
        Mockito.when(requestMock.timeout(30000L, TimeUnit.MILLISECONDS)).thenReturn(requestMock);
        Mockito.when(requestMock.send()).thenReturn(responseMock);
        Mockito.when(responseMock.getEncoding()).thenReturn("utf8");
        Mockito.when(responseMock.getContentAsString()).thenReturn("{\"robonect\": {\"serial\": \"05D92D32-38355048-43203030\", \"version\": \"V0.9\", \"compiled\": \"2017-03-25 20:10:00\", \"comment\": \"V0.9c\"}, \"successful\": true}");
        VersionInfo info = subject.getVersionInfo();
        Assert.assertEquals("05D92D32-38355048-43203030", info.getRobonect().getSerial());
        Mockito.verify(httpClientMock, Mockito.times(1)).newRequest("http://123.456.789.123/json?cmd=version");
    }

    @Test
    public void shouldHandleProperEncoding() throws InterruptedException, ExecutionException, TimeoutException {
        byte[] responseBytesISO88591 = "{\"successful\": true, \"name\": \"Mein Automower\", \"status\": {\"status\": 7, \"stopped\": true, \"duration\": 192, \"mode\": 1, \"battery\": 95, \"hours\": 41}, \"timer\": {\"status\": 2}, \"error\" : {\"error_code\": 15, \"error_message\": \"Utanf\u00f6r arbetsomr\u00e5det\", \"date\": \"02.05.2017\", \"time\": \"20:36:43\", \"unix\": 1493757403}, \"wlan\": {\"signal\": -75}}".getBytes(StandardCharsets.ISO_8859_1);
        Mockito.when(httpClientMock.newRequest("http://123.456.789.123/json?cmd=status")).thenReturn(requestMock);
        Mockito.when(requestMock.method(GET)).thenReturn(requestMock);
        Mockito.when(requestMock.timeout(30000L, TimeUnit.MILLISECONDS)).thenReturn(requestMock);
        Mockito.when(requestMock.send()).thenReturn(responseMock);
        Mockito.when(responseMock.getEncoding()).thenReturn(null);
        Mockito.when(responseMock.getContent()).thenReturn(responseBytesISO88591);
        MowerInfo info = subject.getMowerInfo();
        Assert.assertEquals("Utanf?r arbetsomr?det", info.getError().getErrorMessage());
        Mockito.verify(httpClientMock, Mockito.times(1)).newRequest("http://123.456.789.123/json?cmd=status");
    }

    @Test(expected = RobonectCommunicationException.class)
    public void shouldReceiveErrorAnswerOnInterruptedException() throws InterruptedException, ExecutionException, TimeoutException {
        Mockito.when(httpClientMock.newRequest("http://123.456.789.123/json?cmd=status")).thenReturn(requestMock);
        Mockito.when(requestMock.method(GET)).thenReturn(requestMock);
        Mockito.when(requestMock.timeout(30000L, TimeUnit.MILLISECONDS)).thenReturn(requestMock);
        Mockito.when(requestMock.send()).thenThrow(new InterruptedException("Mock Interrupted Exception"));
        MowerInfo answer = subject.getMowerInfo();
    }

    @Test(expected = RobonectCommunicationException.class)
    public void shouldReceiveErrorAnswerOnExecutionException() throws InterruptedException, ExecutionException, TimeoutException {
        Mockito.when(httpClientMock.newRequest("http://123.456.789.123/json?cmd=status")).thenReturn(requestMock);
        Mockito.when(requestMock.method(GET)).thenReturn(requestMock);
        Mockito.when(requestMock.timeout(30000L, TimeUnit.MILLISECONDS)).thenReturn(requestMock);
        Mockito.when(requestMock.send()).thenThrow(new ExecutionException(new Exception("Mock Exception")));
        MowerInfo answer = subject.getMowerInfo();
    }

    @Test(expected = RobonectCommunicationException.class)
    public void shouldReceiveErrorAnswerOnTimeoutException() throws InterruptedException, ExecutionException, TimeoutException {
        Mockito.when(httpClientMock.newRequest("http://123.456.789.123/json?cmd=status")).thenReturn(requestMock);
        Mockito.when(requestMock.method(GET)).thenReturn(requestMock);
        Mockito.when(requestMock.timeout(30000L, TimeUnit.MILLISECONDS)).thenReturn(requestMock);
        Mockito.when(requestMock.send()).thenThrow(new TimeoutException("Mock Timeout Exception"));
        MowerInfo answer = subject.getMowerInfo();
    }
}

