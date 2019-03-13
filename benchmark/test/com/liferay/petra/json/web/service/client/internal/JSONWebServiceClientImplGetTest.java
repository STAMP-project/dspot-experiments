/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.petra.json.web.service.client.internal;


import JSONWebServiceTransportException.CommunicationFailure;
import com.liferay.petra.json.web.service.client.JSONWebServiceInvocationException;
import com.liferay.petra.json.web.service.client.server.simulator.SimulatorConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Igor Beslic
 */
public class JSONWebServiceClientImplGetTest extends JSONWebServiceClientBaseTest {
    @Test(expected = JSONWebServiceInvocationException.class)
    public void testBadRequestOnGet() throws Exception {
        JSONWebServiceClientImpl jsonWebServiceClientImpl = new JSONWebServiceClientImpl();
        Map<String, Object> properties = getBaseProperties();
        properties.put("headers", "headerKey1=headerValue1;Accept=application/json;");
        properties.put("protocol", "http");
        jsonWebServiceClientImpl.activate(properties);
        jsonWebServiceClientImpl.doGet("/", Collections.<String, String>emptyMap());
    }

    @Test(expected = CommunicationFailure.class)
    public void testCommunicationFailureOnGetWithRetryable() throws Exception {
        JSONWebServiceClientImpl jsonWebServiceClientImpl = new JSONWebServiceClientImpl();
        Map<String, Object> properties = getBaseProperties();
        properties.put("hostPort", 5555);
        properties.put("maxAttempts", 5);
        jsonWebServiceClientImpl.activate(properties);
        jsonWebServiceClientImpl.doGet("/testGet/", new HashMap<String, String>());
    }

    @Test
    public void testResponse200OnGet() throws Exception {
        JSONWebServiceClientImpl jsonWebServiceClientImpl = new JSONWebServiceClientImpl();
        Map<String, Object> properties = getBaseProperties();
        properties.put("headers", "headerKey1=headerValue1;Accept=application/json;");
        properties.put("protocol", "http");
        jsonWebServiceClientImpl.activate(properties);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS, "200");
        params.put(SimulatorConstants.HTTP_PARAMETER_RETURN_PARMS_IN_JSON, "true");
        String json = jsonWebServiceClientImpl.doGet("/testGet/", params);
        Assert.assertTrue(json, json.contains(SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS));
    }

    @Test
    public void testResponse200OnGetWithMultiNames() throws Exception {
        JSONWebServiceClientImpl jsonWebServiceClientImpl = new JSONWebServiceClientImpl();
        Map<String, Object> properties = getBaseProperties();
        properties.put("headers", "headerKey1=headerValue1;Accept=application/json;");
        properties.put("protocol", "http");
        jsonWebServiceClientImpl.activate(properties);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS, "200"));
        params.add(new BasicNameValuePair(SimulatorConstants.HTTP_PARAMETER_RETURN_PARMS_IN_JSON, "true"));
        params.add(new BasicNameValuePair("multi", "first"));
        params.add(new BasicNameValuePair("multi", "second"));
        params.add(new BasicNameValuePair("multi", "third"));
        params.add(new BasicNameValuePair("multi", "fourth"));
        String json = jsonWebServiceClientImpl.doGet("/testGet/", params);
        Assert.assertTrue(json, json.contains("first"));
        Assert.assertTrue(json, json.contains("second"));
        Assert.assertTrue(json, json.contains("third"));
        Assert.assertTrue(json, json.contains("fourth"));
    }

    @Test
    public void testResponse200OnGetWithRetryable() throws Exception {
        JSONWebServiceClientImpl jsonWebServiceClientImpl = new JSONWebServiceClientImpl();
        Map<String, Object> properties = getBaseProperties();
        properties.put("headers", "headerKey1=headerValue1;Accept=application/json;");
        properties.put("maxAttempts", 5);
        jsonWebServiceClientImpl.activate(properties);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS, "200");
        params.put(SimulatorConstants.HTTP_PARAMETER_RETURN_PARMS_IN_JSON, "true");
        String json = jsonWebServiceClientImpl.doGet("/testGet/", params);
        Assert.assertTrue(json, json.contains(SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS));
    }

    @Test
    public void testResponse202OnGet() throws Exception {
        JSONWebServiceClientImpl jsonWebServiceClientImpl = new JSONWebServiceClientImpl();
        Map<String, Object> properties = getBaseProperties();
        properties.put("headers", "headerKey1=headerValue1;Accept=application/json;");
        properties.put("protocol", "http");
        jsonWebServiceClientImpl.activate(properties);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS, "202");
        params.put(SimulatorConstants.HTTP_PARAMETER_RETURN_PARMS_IN_JSON, "true");
        String json = jsonWebServiceClientImpl.doGet("/testGet/", params);
        Assert.assertEquals(SimulatorConstants.RESPONSE_SUCCESS_IN_JSON, json);
    }

    @Test(expected = JSONWebServiceInvocationException.class)
    public void testResponse204OnGet() throws Exception {
        JSONWebServiceClientImpl jsonWebServiceClientImpl = new JSONWebServiceClientImpl();
        Map<String, Object> properties = getBaseProperties();
        properties.put("headers", "Accept=application/json;headerKey2=headerValue2");
        properties.put("protocol", "http");
        jsonWebServiceClientImpl.activate(properties);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS, "204");
        params.put(SimulatorConstants.HTTP_PARAMETER_RETURN_PARMS_IN_JSON, "true");
        jsonWebServiceClientImpl.doGet("/testGet/", params);
    }
}

