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
package com.liferay.jenkins.results.parser;


import java.io.File;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 *
 *
 * @author Peter Yoo
 */
public class JenkinsResultsParserUtilTest extends Test {
    @org.junit.Test
    public void testExpandSlaveRange() {
        testEquals(("cloud-10-50-0-151,cloud-10-50-0-152,cloud-10-50-0-153," + "cloud-10-50-0-154,cloud-10-50-0-155,cloud-10-50-0-156"), JenkinsResultsParserUtil.expandSlaveRange("cloud-10-50-0-151..156"));
        testEquals(("cloud-10-50-0-47,cloud-10-50-0-0,cloud-10-50-0-1," + "cloud-10-50-0-2,cloud-10-50-0-49,cloud-10-50-0-50"), JenkinsResultsParserUtil.expandSlaveRange("cloud-10-50-0-47, cloud-10-50-0-0..2, cloud-10-50-0-49..50"));
    }

    @org.junit.Test
    public void testFixJSON() {
        testEquals("ABC&#09;123", JenkinsResultsParserUtil.fixJSON("ABC\t123"));
        testEquals("ABC&#34;123", JenkinsResultsParserUtil.fixJSON("ABC\"123"));
        testEquals("ABC&#39;123", JenkinsResultsParserUtil.fixJSON("ABC'123"));
        testEquals("ABC&#40;123", JenkinsResultsParserUtil.fixJSON("ABC(123"));
        testEquals("ABC&#41;123", JenkinsResultsParserUtil.fixJSON("ABC)123"));
        testEquals("ABC&#60;123", JenkinsResultsParserUtil.fixJSON("ABC<123"));
        testEquals("ABC&#62;123", JenkinsResultsParserUtil.fixJSON("ABC>123"));
        testEquals("ABC&#91;123", JenkinsResultsParserUtil.fixJSON("ABC[123"));
        testEquals("ABC&#92;123", JenkinsResultsParserUtil.fixJSON("ABC\\123"));
        testEquals("ABC&#93;123", JenkinsResultsParserUtil.fixJSON("ABC]123"));
        testEquals("ABC&#123;123", JenkinsResultsParserUtil.fixJSON("ABC{123"));
        testEquals("ABC&#125;123", JenkinsResultsParserUtil.fixJSON("ABC}123"));
        testEquals("ABC<br />123", JenkinsResultsParserUtil.fixJSON("ABC\n123"));
    }

    @org.junit.Test
    public void testFixURL() {
        testEquals("ABC%28123", JenkinsResultsParserUtil.fixURL("ABC(123"));
        testEquals("ABC%29123", JenkinsResultsParserUtil.fixURL("ABC)123"));
        testEquals("ABC%5B123", JenkinsResultsParserUtil.fixURL("ABC[123"));
        testEquals("ABC%5D123", JenkinsResultsParserUtil.fixURL("ABC]123"));
    }

    @org.junit.Test
    public void testGetJobVariant() throws Exception {
        TestSample testSample = testSamples.get("axis-integration-db2-1");
        testEquals("integration-db2", JenkinsResultsParserUtil.getJobVariant(read(testSample.getSampleDir(), "/api/json")));
        testSample = testSamples.get("axis-plugin-1");
        testEquals("plugins", JenkinsResultsParserUtil.getJobVariant(read(testSample.getSampleDir(), "/api/json")));
        testSample = testSamples.get("job-1");
        testEquals("", JenkinsResultsParserUtil.getJobVariant(read(testSample.getSampleDir(), "/api/json")));
    }

    @org.junit.Test
    public void testGetLocalURL() {
        testEquals("http://test-8/8/ABC?123=456&xyz=abc", JenkinsResultsParserUtil.getLocalURL("https://test.liferay.com/8/ABC?123=456&xyz=abc"));
        testEquals("http://test-1-20/ABC?123=456&xyz=abc", JenkinsResultsParserUtil.getLocalURL("https://test-1-20.liferay.com/ABC?123=456&xyz=abc"));
        testEquals("http://test-4-1/ABC?123=456&xyz=abc", JenkinsResultsParserUtil.getLocalURL("http://test-4-1/ABC?123=456&xyz=abc"));
    }

    @org.junit.Test
    public void testIsJSONArrayEqual() {
        JSONArray expectedJSONArray = new JSONArray();
        expectedJSONArray.put(true);
        expectedJSONArray.put(1.1);
        expectedJSONArray.put(1);
        expectedJSONArray.put("value");
        JSONArray actualJSONArray = new JSONArray();
        actualJSONArray.put(true);
        actualJSONArray.put(1.1);
        actualJSONArray.put(1);
        actualJSONArray.put("value");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("boolean", true);
        jsonObject.put("double", 1.1);
        jsonObject.put("int", 1);
        jsonObject.put("string", "value");
        expectedJSONArray.put(jsonObject);
        actualJSONArray.put(jsonObject);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(true);
        jsonArray.put(1.1);
        jsonArray.put(1);
        jsonArray.put("value");
        expectedJSONArray.put(jsonArray);
        actualJSONArray.put(jsonArray);
        if (!(JenkinsResultsParserUtil.isJSONArrayEqual(expectedJSONArray, actualJSONArray))) {
            errorCollector.addError(new Throwable(JenkinsResultsParserUtil.combine("Expected does not match actual\nExpected: ", expectedJSONArray.toString(), "\nActual:   ", actualJSONArray.toString())));
        }
        actualJSONArray.put("string2");
        if (JenkinsResultsParserUtil.isJSONArrayEqual(expectedJSONArray, actualJSONArray)) {
            errorCollector.addError(new Throwable(JenkinsResultsParserUtil.combine("Expected should not match actual\nExpected: ", expectedJSONArray.toString(), "\nActual:   ", actualJSONArray.toString())));
        }
    }

    @org.junit.Test
    public void testIsJSONObjectEqual() {
        JSONObject expectedJSONObject = new JSONObject();
        expectedJSONObject.put("boolean", true);
        expectedJSONObject.put("double", 1.1);
        expectedJSONObject.put("int", 1);
        expectedJSONObject.put("string", "value");
        JSONObject actualJSONObject = new JSONObject();
        actualJSONObject.put("boolean", true);
        actualJSONObject.put("double", 1.1);
        actualJSONObject.put("int", 1);
        actualJSONObject.put("string", "value");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("boolean", true);
        jsonObject.put("double", 1.1);
        jsonObject.put("int", 1);
        jsonObject.put("string", "value");
        expectedJSONObject.put("json_object", jsonObject);
        actualJSONObject.put("json_object", jsonObject);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(true);
        jsonArray.put(1.1);
        jsonArray.put(1);
        jsonArray.put("value");
        expectedJSONObject.put("json_array", jsonArray);
        actualJSONObject.put("json_array", jsonArray);
        if (!(JenkinsResultsParserUtil.isJSONObjectEqual(expectedJSONObject, actualJSONObject))) {
            errorCollector.addError(new Throwable(JenkinsResultsParserUtil.combine("Expected does not match actual\nExpected: ", expectedJSONObject.toString(), "\nActual:   ", actualJSONObject.toString())));
        }
        actualJSONObject.put("string", "value2");
        if (JenkinsResultsParserUtil.isJSONObjectEqual(expectedJSONObject, actualJSONObject)) {
            errorCollector.addError(new Throwable(JenkinsResultsParserUtil.combine("Expected should not match actual\nExpected: ", expectedJSONObject.toString(), "\nActual:   ", actualJSONObject.toString())));
        }
    }

    @org.junit.Test
    public void testToJSONObject() throws Exception {
        for (TestSample testSample : testSamples.values()) {
            testToJSONObject(new File(testSample.getSampleDir(), "api/json"));
        }
    }

    @org.junit.Test
    public void testToString() throws Exception {
        for (TestSample testSample : testSamples.values()) {
            testToString(new File(testSample.getSampleDir(), "api/json"));
        }
    }
}

