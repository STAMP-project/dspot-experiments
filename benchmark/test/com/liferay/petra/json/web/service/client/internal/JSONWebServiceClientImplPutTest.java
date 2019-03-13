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


import com.liferay.petra.json.web.service.client.JSONWebServiceInvocationException;
import com.liferay.petra.json.web.service.client.model.ResponseBody;
import com.liferay.petra.json.web.service.client.server.simulator.SimulatorConstants;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Igor Beslic
 */
public class JSONWebServiceClientImplPutTest extends JSONWebServiceClientBaseTest {
    @Test(expected = JSONWebServiceInvocationException.class)
    public void testBadRequestOnPut() throws Exception {
        JSONWebServiceClientImpl jsonWebServiceClientImpl = new JSONWebServiceClientImpl();
        Map<String, Object> properties = getBaseProperties();
        properties.put("protocol", "http");
        jsonWebServiceClientImpl.activate(properties);
        jsonWebServiceClientImpl.doPut("/", Collections.<String, String>emptyMap());
    }

    @Test
    public void testResponse200OnPut() throws Exception {
        JSONWebServiceClientImpl jsonWebServiceClientImpl = new JSONWebServiceClientImpl();
        Map<String, Object> properties = getBaseProperties();
        properties.put("headers", "headerKey1=headerValue1;Accept=application/json;");
        properties.put("protocol", "http");
        jsonWebServiceClientImpl.activate(properties);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS, "200");
        params.put(SimulatorConstants.HTTP_PARAMETER_RETURN_PARMS_IN_JSON, "true");
        String json = jsonWebServiceClientImpl.doPut("/testPut/", params);
        Assert.assertTrue(json, json.contains(SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS));
    }

    @Test
    public void testResponse200OnPutToObject() throws Exception {
        JSONWebServiceClientImpl jsonWebServiceClientImpl = new JSONWebServiceClientImpl();
        Map<String, Object> properties = getBaseProperties();
        properties.put("headers", "headerKey1=headerValue1;Accept=application/json;");
        properties.put("protocol", "http");
        jsonWebServiceClientImpl.activate(properties);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS, "200");
        params.put(SimulatorConstants.HTTP_PARAMETER_RETURN_PARMS_IN_JSON, "true");
        params.put("parameter1", "parameter1");
        params.put("parameter2", "parameter2");
        params.put("parameter3", "parameter3");
        ResponseBody responseBody = jsonWebServiceClientImpl.doPutToObject(ResponseBody.class, "/testPut/", params);
        Assert.assertEquals("parameter1", responseBody.getParameter1());
        Assert.assertEquals("parameter2", responseBody.getParameter2());
        Assert.assertEquals("parameter3", responseBody.getParameter3());
    }

    @Test
    public void testResponse200OnPutToObjectWithParmetersArray() throws Exception {
        JSONWebServiceClientImpl jsonWebServiceClientImpl = new JSONWebServiceClientImpl();
        Map<String, Object> properties = getBaseProperties();
        properties.put("headers", "headerKey1=headerValue1;Accept=application/json;");
        properties.put("protocol", "http");
        jsonWebServiceClientImpl.activate(properties);
        ResponseBody responseBody = jsonWebServiceClientImpl.doPutToObject(ResponseBody.class, "/testPut/", SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS, "200", SimulatorConstants.HTTP_PARAMETER_RETURN_PARMS_IN_JSON, "true", "parameter1", "parameter1", "parameter2", "parameter2", "parameter3", "parameter3");
        Assert.assertEquals("parameter1", responseBody.getParameter1());
        Assert.assertEquals("parameter2", responseBody.getParameter2());
        Assert.assertEquals("parameter3", responseBody.getParameter3());
    }

    @Test
    public void testResponse202OnPut() throws Exception {
        JSONWebServiceClientImpl jsonWebServiceClientImpl = new JSONWebServiceClientImpl();
        Map<String, Object> properties = getBaseProperties();
        properties.put("headers", "headerKey1=headerValue1;Accept=application/json;");
        properties.put("protocol", "http");
        jsonWebServiceClientImpl.activate(properties);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS, "202");
        params.put(SimulatorConstants.HTTP_PARAMETER_RETURN_PARMS_IN_JSON, "true");
        String json = jsonWebServiceClientImpl.doPost("/testPut/", params);
        Assert.assertEquals(SimulatorConstants.RESPONSE_SUCCESS_IN_JSON, json);
    }

    @Test
    public void testResponse204OnPut() throws Exception {
        JSONWebServiceClientImpl jsonWebServiceClientImpl = new JSONWebServiceClientImpl();
        Map<String, Object> properties = getBaseProperties();
        properties.put("headers", "Accept=application/json;headerKey1=headerValue1");
        properties.put("protocol", "http");
        jsonWebServiceClientImpl.activate(properties);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS, "204");
        params.put(SimulatorConstants.HTTP_PARAMETER_RETURN_PARMS_IN_JSON, "true");
        String json = jsonWebServiceClientImpl.doPost("/testPut/", params);
        Assert.assertNull(json);
    }
}

