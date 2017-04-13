/**
 * Copyright 2016-2017 Telefonica Investigaci?n y Desarrollo, S.A.U
 *
 * This file is part of fiware-cygnus (FIWARE project).
 *
 * fiware-cygnus is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * fiware-cygnus is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with fiware-cygnus. If not, see
 * http://www.gnu.org/licenses/.
 *
 * For those usages not covered by the GNU Affero General Public License please contact with iot_support at tid dot es
 */
/**
 * NGSIUtilsTest
 */


package com.telefonica.iot.cygnus.utils;


/**
 * @author frb
 */
@org.junit.runner.RunWith(value = org.mockito.runners.MockitoJUnitRunner.class)
public class AmplCommonUtilsTest {
    /**
     * Constructor.
     */
    public AmplCommonUtilsTest() {
        org.apache.log4j.LogManager.getRootLogger().setLevel(org.apache.log4j.Level.FATAL);
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    @org.junit.Test
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        metadataJson.put("name", "TimeInstant");
        metadataJson.put("type", "SQL timestamp");
        metadataJson.put("value", "2017-01-01T00:00:01Z");
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        metadatasJson.add(metadataJson);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    @org.junit.Test
    public void testGetTimeInstantISO8601TimestampWithMiliseconds() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        metadataJson.put("name", "TimeInstant");
        metadataJson.put("type", "SQL timestamp");
        metadataJson.put("value", "2017-01-01T00:00:01.123Z");
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        metadatasJson.add(metadataJson);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    @org.junit.Test
    public void testGetTimeInstantISO8601TimestampWithMicroseconds() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        metadataJson.put("name", "TimeInstant");
        metadataJson.put("type", "SQL timestamp");
        metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        metadatasJson.add(metadataJson);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    @org.junit.Test
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        metadataJson.put("name", "TimeInstant");
        metadataJson.put("type", "SQL timestamp");
        metadataJson.put("value", "2017-01-01 00:00:01");
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        metadatasJson.add(metadataJson);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    @org.junit.Test
    public void testGetTimeInstantSQLTimestampWithMiliseconds() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        metadataJson.put("name", "TimeInstant");
        metadataJson.put("type", "SQL timestamp");
        metadataJson.put("value", "2017-01-01 00:00:01.123");
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        metadatasJson.add(metadataJson);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    @org.junit.Test
    public void testGetTimeInstantSQLTimestampWithMicroseconds() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        metadataJson.put("name", "TimeInstant");
        metadataJson.put("type", "SQL timestamp");
        metadataJson.put("value", "2017-01-01 00:00:01.123456");
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        metadatasJson.add(metadataJson);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    @org.junit.Test
    public void testGetMilliseconds() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
        java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
        java.lang.Long milliseconds;
        try {
            milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
        } catch (java.text.ParseException e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
            throw new java.lang.AssertionError(e.getMessage());
        }// try catch
        
        try {
            org.junit.Assert.assertEquals(timestamp, com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf94() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
        java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
        java.lang.Long milliseconds;
        try {
            milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
        } catch (java.text.ParseException e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
            throw new java.lang.AssertionError(e.getMessage());
        }// try catch
        
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_41 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // StatementAdderMethod cloned existing statement
            vc_41.printLoadedJars();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_41);
            org.junit.Assert.assertEquals(timestamp, com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf83() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
        java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
        java.lang.Long milliseconds;
        try {
            milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
        } catch (java.text.ParseException e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
            throw new java.lang.AssertionError(e.getMessage());
        }// try catch
        
        try {
            // StatementAdderOnAssert create random local variable
            java.util.ArrayList vc_36 = new java.util.ArrayList();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_33 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetMilliseconds_cf83__22 = // StatementAdderMethod cloned existing statement
vc_33.toString(vc_36);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetMilliseconds_cf83__22, "");
            org.junit.Assert.assertEquals(timestamp, com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf30_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
            java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
            java.lang.Long milliseconds;
            try {
                milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
            } catch (java.text.ParseException e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
                throw new java.lang.AssertionError(e.getMessage());
            }// try catch
            
            try {
                // StatementAdderOnAssert create null value
                java.lang.String vc_2 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_0 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_0.isANumber(vc_2);
                // MethodAssertGenerator build local variable
                Object o_24_0 = com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true);
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMilliseconds_cf30 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf40() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
        java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
        java.lang.Long milliseconds;
        try {
            milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
        } catch (java.text.ParseException e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
            throw new java.lang.AssertionError(e.getMessage());
        }// try catch
        
        try {
            // StatementAdderOnAssert create random local variable
            int vc_8 = 661689706;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1 = "[CommonUtils.getMilliseconds]";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_4 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetMilliseconds_cf40__24 = // StatementAdderMethod cloned existing statement
vc_4.isANumber(String_vc_1, vc_8);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetMilliseconds_cf40__24);
            org.junit.Assert.assertEquals(timestamp, com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf56() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
        java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
        java.lang.Long milliseconds;
        try {
            milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
        } catch (java.text.ParseException e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
            throw new java.lang.AssertionError(e.getMessage());
        }// try catch
        
        try {
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_3 = "[CommonUtils.getMilliseconds]";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_13 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.Long o_testGetMilliseconds_cf56__22 = // StatementAdderMethod cloned existing statement
vc_13.getTimeInstant(String_vc_3);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testGetMilliseconds_cf56__22);
            org.junit.Assert.assertEquals(timestamp, com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf76() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
        java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
        java.lang.Long milliseconds;
        try {
            milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
        } catch (java.text.ParseException e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
            throw new java.lang.AssertionError(e.getMessage());
        }// try catch
        
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_27 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetMilliseconds_cf76__20 = // StatementAdderMethod cloned existing statement
vc_27.getLastCommit();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetMilliseconds_cf76__20, "UNKNOWN");
            org.junit.Assert.assertEquals(timestamp, com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf33() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
        java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
        java.lang.Long milliseconds;
        try {
            milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
        } catch (java.text.ParseException e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
            throw new java.lang.AssertionError(e.getMessage());
        }// try catch
        
        try {
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_3 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_0 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetMilliseconds_cf33__22 = // StatementAdderMethod cloned existing statement
vc_0.isANumber(vc_3);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetMilliseconds_cf33__22);
            org.junit.Assert.assertEquals(timestamp, com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf82_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
            java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
            java.lang.Long milliseconds;
            try {
                milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
            } catch (java.text.ParseException e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
                throw new java.lang.AssertionError(e.getMessage());
            }// try catch
            
            try {
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_35 = (java.util.ArrayList)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_33 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_33.toString(vc_35);
                // MethodAssertGenerator build local variable
                Object o_24_0 = com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true);
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMilliseconds_cf82 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf49() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
        java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
        java.lang.Long milliseconds;
        try {
            milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
        } catch (java.text.ParseException e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
            throw new java.lang.AssertionError(e.getMessage());
        }// try catch
        
        try {
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_12 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_9 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetMilliseconds_cf49__22 = // StatementAdderMethod cloned existing statement
vc_9.isMAdeOfAlphaNumericsOrUnderscores(vc_12);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testGetMilliseconds_cf49__22);
            org.junit.Assert.assertEquals(timestamp, com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf48() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
        java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
        java.lang.Long milliseconds;
        try {
            milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
        } catch (java.text.ParseException e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
            throw new java.lang.AssertionError(e.getMessage());
        }// try catch
        
        try {
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_2 = "[CommonUtils.getMilliseconds]";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_9 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetMilliseconds_cf48__22 = // StatementAdderMethod cloned existing statement
vc_9.isMAdeOfAlphaNumericsOrUnderscores(String_vc_2);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetMilliseconds_cf48__22);
            org.junit.Assert.assertEquals(timestamp, com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf70() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
        java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
        java.lang.Long milliseconds;
        try {
            milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
        } catch (java.text.ParseException e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
            throw new java.lang.AssertionError(e.getMessage());
        }// try catch
        
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_21 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetMilliseconds_cf70__20 = // StatementAdderMethod cloned existing statement
vc_21.getCygnusVersion();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetMilliseconds_cf70__20, "1.7.0");
            org.junit.Assert.assertEquals(timestamp, com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds_cf47 */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf47_cf1113_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
            java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
            java.lang.Long milliseconds;
            try {
                milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
            } catch (java.text.ParseException e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
                throw new java.lang.AssertionError(e.getMessage());
            }// try catch
            
            try {
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_9 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // AssertGenerator replace invocation
                boolean o_testGetMilliseconds_cf47__20 = // StatementAdderMethod cloned existing statement
vc_9.isMAdeOfAlphaNumericsOrUnderscores(timestamp);
                // MethodAssertGenerator build local variable
                Object o_22_0 = o_testGetMilliseconds_cf47__20;
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_336 = (java.util.ArrayList)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_334 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_334.toString(vc_336);
                // MethodAssertGenerator build local variable
                Object o_30_0 = com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true);
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMilliseconds_cf47_cf1113 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds_cf33 */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf33_cf410_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
            java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
            java.lang.Long milliseconds;
            try {
                milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
            } catch (java.text.ParseException e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
                throw new java.lang.AssertionError(e.getMessage());
            }// try catch
            
            try {
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_3 = new java.lang.String();
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_0 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // AssertGenerator replace invocation
                boolean o_testGetMilliseconds_cf33__22 = // StatementAdderMethod cloned existing statement
vc_0.isANumber(vc_3);
                // MethodAssertGenerator build local variable
                Object o_24_0 = o_testGetMilliseconds_cf33__22;
                // StatementAdderOnAssert create null value
                java.lang.String vc_131 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_129 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_129.isANumber(vc_131);
                // MethodAssertGenerator build local variable
                Object o_32_0 = com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true);
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMilliseconds_cf33_cf410 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds_cf57 */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf57_cf1733_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
            java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
            java.lang.Long milliseconds;
            try {
                milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
            } catch (java.text.ParseException e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
                throw new java.lang.AssertionError(e.getMessage());
            }// try catch
            
            try {
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_16 = new java.lang.String();
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_13 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // AssertGenerator replace invocation
                java.lang.Long o_testGetMilliseconds_cf57__22 = // StatementAdderMethod cloned existing statement
vc_13.getTimeInstant(vc_16);
                // MethodAssertGenerator build local variable
                Object o_24_0 = o_testGetMilliseconds_cf57__22;
                // StatementAdderOnAssert create null value
                java.lang.String vc_518 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_516 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_516.isANumber(vc_518);
                // MethodAssertGenerator build local variable
                Object o_32_0 = com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true);
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMilliseconds_cf57_cf1733 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds_literalMutation13 */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_literalMutation13_failAssert3_literalMutation2877_cf3327_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
                java.lang.String timestamp = "2017-0F-10T1c7:08:00.0Z";
                // StatementAdderOnAssert create null value
                java.lang.String vc_905 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_903 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_903.isANumber(vc_905);
                // MethodAssertGenerator build local variable
                Object o_12_0 = timestamp;
                java.lang.Long milliseconds;
                try {
                    milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
                } catch (java.text.ParseException e) {
                    java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
                    throw new java.lang.AssertionError(e.getMessage());
                }// try catch
                
                try {
                    // MethodAssertGenerator build local variable
                    Object o_18_0 = com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true);
                    java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
                } catch (java.lang.AssertionError e) {
                    java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
                    throw e;
                }// try catch
                
                org.junit.Assert.fail("testGetMilliseconds_literalMutation13 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("testGetMilliseconds_literalMutation13_failAssert3_literalMutation2877_cf3327 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds_cf72 */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_cf72_cf2063_failAssert15_literalMutation4006() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
            java.lang.String timestamp = "2017-01-10T17:08:00.0Z";
            java.lang.Long milliseconds;
            try {
                milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
            } catch (java.text.ParseException e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
                throw new java.lang.AssertionError(e.getMessage());
            }// try catch
            
            try {
                // StatementAdderOnAssert create literal from method
                boolean boolean_vc_5 = false;
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(boolean_vc_5);
                // StatementAdderOnAssert create random local variable
                long vc_25 = -9125137839912864011L;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_23 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // AssertGenerator replace invocation
                java.lang.String o_testGetMilliseconds_cf72__24 = // StatementAdderMethod cloned existing statement
vc_23.getHumanReadable(vc_25, boolean_vc_5);
                // MethodAssertGenerator build local variable
                Object o_26_0 = o_testGetMilliseconds_cf72__24;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_26_0, "289156201-10-11T17:05:35.989");
                // StatementAdderOnAssert create null value
                java.lang.String vc_613 = (java.lang.String)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_613);
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_611 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_611);
                // StatementAdderMethod cloned existing statement
                vc_611.isMAdeOfAlphaNumericsOrUnderscores(vc_613);
                // MethodAssertGenerator build local variable
                Object o_34_0 = com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true);
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMilliseconds_cf72_cf2063 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithMicroseconds
    /**
     * [CommonUtils.getMilliseconds] -------- Milliseconds are obtained when passing a valid timestamp.
     */
    // testGetMilliseconds
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetMilliseconds_literalMutation13 */
    @org.junit.Test(timeout = 10000)
    public void testGetMilliseconds_literalMutation13_failAssert3_literalMutation2878_cf3279_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-------- Milliseconds are obtained when passing a valid timestamp"));
                java.lang.String timestamp = "@xl+Y)]GWMwTIPF26|q@>4l";
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_895 = (java.util.ArrayList)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_893 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_893.toString(vc_895);
                // MethodAssertGenerator build local variable
                Object o_12_0 = timestamp;
                java.lang.Long milliseconds;
                try {
                    milliseconds = com.telefonica.iot.cygnus.utils.CommonUtils.getMilliseconds(timestamp);
                } catch (java.text.ParseException e) {
                    java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - There was some problem while getting the milliseconds"));
                    throw new java.lang.AssertionError(e.getMessage());
                }// try catch
                
                try {
                    // MethodAssertGenerator build local variable
                    Object o_18_0 = com.telefonica.iot.cygnus.utils.CommonUtils.getHumanReadable(milliseconds, true);
                    java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "-  OK  - Milliseconds obtained"));
                } catch (java.lang.AssertionError e) {
                    java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getMilliseconds]")) + "- FAIL - Milliseconds were not obtained"));
                    throw e;
                }// try catch
                
                org.junit.Assert.fail("testGetMilliseconds_literalMutation13 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("testGetMilliseconds_literalMutation13_failAssert3_literalMutation2878_cf3279 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_cf4305() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4305__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4305__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4305__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4305__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4305__7 = metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4305__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4305__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4305__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_998 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4305__19 = // StatementAdderMethod cloned existing statement
vc_998.isMAdeOfAlphaNumericsOrUnderscores(metadatasStr);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4305__19);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_cf4328() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4328__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4328__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4328__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4328__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4328__7 = metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4328__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4328__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4328__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_1010 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4328__19 = // StatementAdderMethod cloned existing statement
vc_1010.getCygnusVersion();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4328__19, "1.7.0");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_cf4290() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4290__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4290__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4290__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4290__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4290__7 = metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4290__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4290__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4290__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_182 = "name";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_989 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4290__21 = // StatementAdderMethod cloned existing statement
vc_989.isANumber(String_vc_182);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4290__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_cf4306() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4306__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4306__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4306__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4306__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4306__7 = metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4306__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4306__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4306__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_184 = "name";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_998 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4306__21 = // StatementAdderMethod cloned existing statement
vc_998.isMAdeOfAlphaNumericsOrUnderscores(String_vc_184);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4306__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_cf4339() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4339__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4339__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4339__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4339__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4339__7 = metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4339__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4339__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4339__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.util.ArrayList vc_1025 = new java.util.ArrayList();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_1022 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4339__21 = // StatementAdderMethod cloned existing statement
vc_1022.toString(vc_1025);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4339__21, "");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__7 = metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_992 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_989 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__21 = // StatementAdderMethod cloned existing statement
vc_989.isANumber(vc_992);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_cf4314() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4314__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4314__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4314__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4314__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4314__7 = metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4314__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4314__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4314__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_185 = "[CommonUtils.getTimeInstant]";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_1002 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.Long o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4314__21 = // StatementAdderMethod cloned existing statement
vc_1002.getTimeInstant(String_vc_185);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4314__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_cf4288_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            metadataJson.put("name", "TimeInstant");
            metadataJson.put("type", "SQL timestamp");
            metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            metadatasJson.add(metadataJson);
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.lang.String vc_991 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_989 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_989.isANumber(vc_991);
                // MethodAssertGenerator build local variable
                Object o_23_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantISO8601TimestampWithMicroseconds_cf4288 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_cf4338_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            metadataJson.put("name", "TimeInstant");
            metadataJson.put("type", "SQL timestamp");
            metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            metadatasJson.add(metadataJson);
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_1024 = (java.util.ArrayList)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_1022 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_1022.toString(vc_1024);
                // MethodAssertGenerator build local variable
                Object o_23_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantISO8601TimestampWithMicroseconds_cf4338 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_cf4330() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4330__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4330__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4330__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4330__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4330__7 = metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4330__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4330__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4330__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            boolean vc_1015 = false;
            // StatementAdderOnAssert create random local variable
            long vc_1014 = 4688712608187965345L;
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_1012 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4330__23 = // StatementAdderMethod cloned existing statement
vc_1012.getHumanReadable(vc_1014, vc_1015);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4330__23, "148581357-10-10T05:06:05.345");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_cf4332() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4332__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4332__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4332__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4332__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4332__7 = metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4332__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4332__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4332__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_1016 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4332__19 = // StatementAdderMethod cloned existing statement
vc_1016.getLastCommit();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4332__19, "UNKNOWN");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_cf4350() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4350__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4350__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4350__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4350__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4350__7 = metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4350__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4350__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4350__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_1030 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // StatementAdderMethod cloned existing statement
            vc_1030.printLoadedJars();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1030);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291 */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291_cf8032_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with microseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__5 = metadataJson.put("name", "TimeInstant");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__5;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__6 = metadataJson.put("type", "SQL timestamp");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__6;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__7 = metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__7;
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__10 = metadatasJson.add(metadataJson);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__10;
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_992 = new java.lang.String();
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_989 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // AssertGenerator replace invocation
                boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__21 = // StatementAdderMethod cloned existing statement
vc_989.isANumber(vc_992);
                // MethodAssertGenerator build local variable
                Object o_35_0 = o_testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291__21;
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_2615 = (java.util.ArrayList)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_2613 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_2613.toString(vc_2615);
                // MethodAssertGenerator build local variable
                Object o_43_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantISO8601TimestampWithMicroseconds_cf4291_cf8032 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantISO8601TimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid ISO 8601 timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMicroseconds_literalMutation4266 */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMicroseconds_literalMutation4266_cf5605_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + ""));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_literalMutation4266__5 = metadataJson.put("name", "TimeInstant");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetTimeInstantISO8601TimestampWithMicroseconds_literalMutation4266__5;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_literalMutation4266__6 = metadataJson.put("type", "SQL timestamp");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetTimeInstantISO8601TimestampWithMicroseconds_literalMutation4266__6;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithMicroseconds_literalMutation4266__7 = metadataJson.put("value", "2017-01-01T00:00:01.123456Z");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetTimeInstantISO8601TimestampWithMicroseconds_literalMutation4266__7;
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithMicroseconds_literalMutation4266__10 = metadatasJson.add(metadataJson);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testGetTimeInstantISO8601TimestampWithMicroseconds_literalMutation4266__10;
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.lang.String vc_1550 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_1548 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_1548.isANumber(vc_1550);
                // MethodAssertGenerator build local variable
                Object o_35_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantISO8601TimestampWithMicroseconds_literalMutation4266_cf5605 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_cf10737() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10737__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10737__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10737__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10737__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10737__7 = metadataJson.put("value", "2017-01-01T00:00:01.123Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10737__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10737__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10737__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_3277 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10737__19 = // StatementAdderMethod cloned existing statement
vc_3277.isMAdeOfAlphaNumericsOrUnderscores(metadatasStr);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10737__19);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_cf10739() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10739__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10739__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10739__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10739__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10739__7 = metadataJson.put("value", "2017-01-01T00:00:01.123Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10739__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10739__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10739__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_3280 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_3277 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10739__21 = // StatementAdderMethod cloned existing statement
vc_3277.isMAdeOfAlphaNumericsOrUnderscores(vc_3280);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10739__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_cf10770_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            metadataJson.put("name", "TimeInstant");
            metadataJson.put("type", "SQL timestamp");
            metadataJson.put("value", "2017-01-01T00:00:01.123Z");
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            metadatasJson.add(metadataJson);
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_3303 = (java.util.ArrayList)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_3301 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_3301.toString(vc_3303);
                // MethodAssertGenerator build local variable
                Object o_23_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantISO8601TimestampWithMiliseconds_cf10770 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_cf10760() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10760__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10760__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10760__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10760__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10760__7 = metadataJson.put("value", "2017-01-01T00:00:01.123Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10760__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10760__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10760__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_3289 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10760__19 = // StatementAdderMethod cloned existing statement
vc_3289.getCygnusVersion();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10760__19, "1.7.0");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_cf10782() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10782__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10782__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10782__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10782__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10782__7 = metadataJson.put("value", "2017-01-01T00:00:01.123Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10782__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10782__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10782__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_3309 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // StatementAdderMethod cloned existing statement
            vc_3309.printLoadedJars();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3309);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_cf10771() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10771__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10771__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10771__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10771__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10771__7 = metadataJson.put("value", "2017-01-01T00:00:01.123Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10771__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10771__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10771__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.util.ArrayList vc_3304 = new java.util.ArrayList();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_3301 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10771__21 = // StatementAdderMethod cloned existing statement
vc_3301.toString(vc_3304);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10771__21, "");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_cf10720_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            metadataJson.put("name", "TimeInstant");
            metadataJson.put("type", "SQL timestamp");
            metadataJson.put("value", "2017-01-01T00:00:01.123Z");
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            metadatasJson.add(metadataJson);
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.lang.String vc_3270 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_3268 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_3268.isANumber(vc_3270);
                // MethodAssertGenerator build local variable
                Object o_23_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantISO8601TimestampWithMiliseconds_cf10720 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_cf10762() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10762__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10762__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10762__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10762__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10762__7 = metadataJson.put("value", "2017-01-01T00:00:01.123Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10762__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10762__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10762__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            boolean vc_3294 = false;
            // StatementAdderOnAssert create random local variable
            long vc_3293 = 7954590295770724698L;
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_3291 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10762__23 = // StatementAdderMethod cloned existing statement
vc_3291.getHumanReadable(vc_3293, vc_3294);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10762__23, "252072901-10-24T03:52:04.698");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_cf10764() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10764__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10764__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10764__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10764__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10764__7 = metadataJson.put("value", "2017-01-01T00:00:01.123Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10764__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10764__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10764__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_3295 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10764__19 = // StatementAdderMethod cloned existing statement
vc_3295.getLastCommit();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10764__19, "UNKNOWN");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_cf10722() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10722__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10722__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10722__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10722__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10722__7 = metadataJson.put("value", "2017-01-01T00:00:01.123Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10722__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10722__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10722__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_527 = "-------- When getting a time instant, it is properly obtained when passing a valid ";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_3268 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10722__21 = // StatementAdderMethod cloned existing statement
vc_3268.isANumber(String_vc_527);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10722__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_cf10746() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10746__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10746__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10746__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10746__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10746__7 = metadataJson.put("value", "2017-01-01T00:00:01.123Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10746__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10746__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10746__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_530 = "[CommonUtils.getTimeInstant]";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_3281 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.Long o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10746__21 = // StatementAdderMethod cloned existing statement
vc_3281.getTimeInstant(String_vc_530);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10746__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_cf10723() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10723__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10723__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10723__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10723__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10723__7 = metadataJson.put("value", "2017-01-01T00:00:01.123Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10723__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10723__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10723__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_3271 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_3268 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10723__21 = // StatementAdderMethod cloned existing statement
vc_3268.isANumber(vc_3271);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantISO8601TimestampWithMiliseconds_cf10723__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10707 */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10707_cf12880_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp with miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10707__5 = metadataJson.put("name", "TimeInstant");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10707__5;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10707__6 = metadataJson.put("type", "SQL timestamp");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10707__6;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10707__7 = metadataJson.put("value", "2017-01-01T00:00:01.123Z");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10707__7;
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10707__10 = metadatasJson.add(metadataJson);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10707__10;
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.lang.String vc_4216 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_4214 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_4214.isANumber(vc_4216);
                // MethodAssertGenerator build local variable
                Object o_35_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time i+stant obtained for \'") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10707_cf12880 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantISO8601TimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10698 */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10698_cf12087_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + ""));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10698__5 = metadataJson.put("name", "TimeInstant");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10698__5;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10698__6 = metadataJson.put("type", "SQL timestamp");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10698__6;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10698__7 = metadataJson.put("value", "2017-01-01T00:00:01.123Z");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10698__7;
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10698__10 = metadatasJson.add(metadataJson);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10698__10;
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_3862 = (java.util.ArrayList)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_3860 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_3860.toString(vc_3862);
                // MethodAssertGenerator build local variable
                Object o_35_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantISO8601TimestampWithMiliseconds_literalMutation10698_cf12087 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17051() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17051__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17051__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17051__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17051__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17051__7 = metadataJson.put("value", "2017-01-01T00:00:01Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17051__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17051__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17051__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_5513 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17051__19 = // StatementAdderMethod cloned existing statement
vc_5513.isMAdeOfAlphaNumericsOrUnderscores(metadatasStr);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17051__19);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17074() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17074__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17074__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17074__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17074__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17074__7 = metadataJson.put("value", "2017-01-01T00:00:01Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17074__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17074__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17074__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_5525 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17074__19 = // StatementAdderMethod cloned existing statement
vc_5525.getCygnusVersion();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17074__19, "1.7.0");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__7 = metadataJson.put("value", "2017-01-01T00:00:01Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_867 = "TimeInstant";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_5513 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__21 = // StatementAdderMethod cloned existing statement
vc_5513.isMAdeOfAlphaNumericsOrUnderscores(String_vc_867);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17096() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17096__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17096__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17096__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17096__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17096__7 = metadataJson.put("value", "2017-01-01T00:00:01Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17096__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17096__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17096__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_5545 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // StatementAdderMethod cloned existing statement
            vc_5545.printLoadedJars();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5545);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17085() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17085__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17085__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17085__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17085__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17085__7 = metadataJson.put("value", "2017-01-01T00:00:01Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17085__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17085__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17085__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.util.ArrayList vc_5540 = new java.util.ArrayList();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_5537 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17085__21 = // StatementAdderMethod cloned existing statement
vc_5537.toString(vc_5540);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17085__21, "");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17084_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            metadataJson.put("name", "TimeInstant");
            metadataJson.put("type", "SQL timestamp");
            metadataJson.put("value", "2017-01-01T00:00:01Z");
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            metadatasJson.add(metadataJson);
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_5539 = (java.util.ArrayList)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_5537 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_5537.toString(vc_5539);
                // MethodAssertGenerator build local variable
                Object o_23_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17084 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17060() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17060__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17060__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17060__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17060__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17060__7 = metadataJson.put("value", "2017-01-01T00:00:01Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17060__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17060__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17060__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_868 = "SQL timestamp";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_5517 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.Long o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17060__21 = // StatementAdderMethod cloned existing statement
vc_5517.getTimeInstant(String_vc_868);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17060__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17036() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17036__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17036__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17036__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17036__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17036__7 = metadataJson.put("value", "2017-01-01T00:00:01Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17036__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17036__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17036__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_865 = "type";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_5504 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17036__21 = // StatementAdderMethod cloned existing statement
vc_5504.isANumber(String_vc_865);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17036__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17034_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            metadataJson.put("name", "TimeInstant");
            metadataJson.put("type", "SQL timestamp");
            metadataJson.put("value", "2017-01-01T00:00:01Z");
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            metadatasJson.add(metadataJson);
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.lang.String vc_5506 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_5504 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_5504.isANumber(vc_5506);
                // MethodAssertGenerator build local variable
                Object o_23_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17034 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17037() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17037__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17037__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17037__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17037__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17037__7 = metadataJson.put("value", "2017-01-01T00:00:01Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17037__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17037__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17037__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_5507 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_5504 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17037__21 = // StatementAdderMethod cloned existing statement
vc_5504.isANumber(vc_5507);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17037__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17078() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17078__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17078__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17078__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17078__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17078__7 = metadataJson.put("value", "2017-01-01T00:00:01Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17078__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17078__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17078__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_5531 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17078__19 = // StatementAdderMethod cloned existing statement
vc_5531.getLastCommit();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17078__19, "UNKNOWN");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052 */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052_cf21443_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__5 = metadataJson.put("name", "TimeInstant");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__5;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__6 = metadataJson.put("type", "SQL timestamp");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__6;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__7 = metadataJson.put("value", "2017-01-01T00:00:01Z");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__7;
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__10 = metadatasJson.add(metadataJson);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__10;
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_867 = "TimeInstant";
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_5513 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // AssertGenerator replace invocation
                boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__21 = // StatementAdderMethod cloned existing statement
vc_5513.isMAdeOfAlphaNumericsOrUnderscores(String_vc_867);
                // MethodAssertGenerator build local variable
                Object o_35_0 = o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052__21;
                // StatementAdderOnAssert create null value
                java.lang.String vc_7312 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                vc_5513.isANumber(vc_7312);
                // MethodAssertGenerator build local variable
                Object o_41_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17052_cf21443 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // CommonUtilsTest
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when passing
     * a valid ISO 8601 timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17053 */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17053_cf21664_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "ISO 8601 timestamp without miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17053__5 = metadataJson.put("name", "TimeInstant");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17053__5;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17053__6 = metadataJson.put("type", "SQL timestamp");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17053__6;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17053__7 = metadataJson.put("value", "2017-01-01T00:00:01Z");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17053__7;
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17053__10 = metadatasJson.add(metadataJson);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17053__10;
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_5516 = new java.lang.String();
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_5513 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // AssertGenerator replace invocation
                boolean o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17053__21 = // StatementAdderMethod cloned existing statement
vc_5513.isMAdeOfAlphaNumericsOrUnderscores(vc_5516);
                // MethodAssertGenerator build local variable
                Object o_35_0 = o_testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17053__21;
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_7388 = (java.util.ArrayList)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_7386 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_7386.toString(vc_7388);
                // MethodAssertGenerator build local variable
                Object o_43_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantISO8601TimestampWithoutMiliseconds_cf17053_cf21664 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_cf23461() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__7 = metadataJson.put("value", "2017-01-01 00:00:01.123456");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_7756 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_7753 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.Long o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__21 = // StatementAdderMethod cloned existing statement
vc_7753.getTimeInstant(vc_7756);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_cf23451() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23451__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23451__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23451__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23451__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23451__7 = metadataJson.put("value", "2017-01-01 00:00:01.123456");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23451__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23451__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23451__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_7749 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23451__19 = // StatementAdderMethod cloned existing statement
vc_7749.isMAdeOfAlphaNumericsOrUnderscores(metadatasStr);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23451__19);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_cf23484_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            metadataJson.put("name", "TimeInstant");
            metadataJson.put("type", "SQL timestamp");
            metadataJson.put("value", "2017-01-01 00:00:01.123456");
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            metadatasJson.add(metadataJson);
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_7775 = (java.util.ArrayList)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_7773 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_7773.toString(vc_7775);
                // MethodAssertGenerator build local variable
                Object o_23_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantSQLTimestampWithMicroseconds_cf23484 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_cf23478() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23478__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23478__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23478__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23478__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23478__7 = metadataJson.put("value", "2017-01-01 00:00:01.123456");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23478__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23478__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23478__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_7767 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23478__19 = // StatementAdderMethod cloned existing statement
vc_7767.getLastCommit();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23478__19, "UNKNOWN");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_cf23437() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23437__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23437__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23437__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23437__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23437__7 = metadataJson.put("value", "2017-01-01 00:00:01.123456");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23437__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23437__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23437__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_7743 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_7740 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23437__21 = // StatementAdderMethod cloned existing statement
vc_7740.isANumber(vc_7743);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23437__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_cf23474() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23474__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23474__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23474__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23474__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23474__7 = metadataJson.put("value", "2017-01-01 00:00:01.123456");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23474__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23474__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23474__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_7761 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23474__19 = // StatementAdderMethod cloned existing statement
vc_7761.getCygnusVersion();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23474__19, "1.7.0");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_cf23496() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23496__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23496__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23496__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23496__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23496__7 = metadataJson.put("value", "2017-01-01 00:00:01.123456");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23496__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23496__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23496__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_7781 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // StatementAdderMethod cloned existing statement
            vc_7781.printLoadedJars();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7781);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_cf23485() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23485__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23485__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23485__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23485__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23485__7 = metadataJson.put("value", "2017-01-01 00:00:01.123456");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23485__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23485__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23485__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.util.ArrayList vc_7776 = new java.util.ArrayList();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_7773 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23485__21 = // StatementAdderMethod cloned existing statement
vc_7773.toString(vc_7776);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23485__21, "");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_cf23453() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23453__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23453__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23453__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23453__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23453__7 = metadataJson.put("value", "2017-01-01 00:00:01.123456");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23453__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23453__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23453__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_7752 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_7749 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23453__21 = // StatementAdderMethod cloned existing statement
vc_7749.isMAdeOfAlphaNumericsOrUnderscores(vc_7752);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23453__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_cf23476() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23476__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23476__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23476__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23476__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23476__7 = metadataJson.put("value", "2017-01-01 00:00:01.123456");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23476__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23476__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23476__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            boolean vc_7766 = false;
            // StatementAdderOnAssert create random local variable
            long vc_7765 = 1618490568887021873L;
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_7763 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23476__23 = // StatementAdderMethod cloned existing statement
vc_7763.getHumanReadable(vc_7765, vc_7766);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23476__23, "51289894-05-31T11:43:41.873");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_cf23443() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23443__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23443__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23443__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23443__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23443__7 = metadataJson.put("value", "2017-01-01 00:00:01.123456");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23443__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23443__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23443__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            int vc_7748 = -1887182064;
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_7744 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23443__21 = // StatementAdderMethod cloned existing statement
vc_7744.isANumber(metadatasStr, vc_7748);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23443__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_cf23434_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            metadataJson.put("name", "TimeInstant");
            metadataJson.put("type", "SQL timestamp");
            metadataJson.put("value", "2017-01-01 00:00:01.123456");
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            metadatasJson.add(metadataJson);
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.lang.String vc_7742 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_7740 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_7740.isANumber(vc_7742);
                // MethodAssertGenerator build local variable
                Object o_23_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantSQLTimestampWithMicroseconds_cf23434 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds_cf23461 */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_cf23461_cf28347_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__5 = metadataJson.put("name", "TimeInstant");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__5;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__6 = metadataJson.put("type", "SQL timestamp");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__6;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__7 = metadataJson.put("value", "2017-01-01 00:00:01.123456");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__7;
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__10 = metadatasJson.add(metadataJson);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__10;
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_7756 = new java.lang.String();
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_7753 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // AssertGenerator replace invocation
                java.lang.Long o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__21 = // StatementAdderMethod cloned existing statement
vc_7753.getTimeInstant(vc_7756);
                // MethodAssertGenerator build local variable
                Object o_35_0 = o_testGetTimeInstantSQLTimestampWithMicroseconds_cf23461__21;
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_9710 = (java.util.ArrayList)null;
                // StatementAdderMethod cloned existing statement
                vc_7753.toString(vc_9710);
                // MethodAssertGenerator build local variable
                Object o_41_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantSQLTimestampWithMicroseconds_cf23461_cf28347 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with microseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMicroseconds_literalMutation23423 */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMicroseconds_literalMutation23423_cf25779_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with microseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_literalMutation23423__5 = metadataJson.put("name", "TimeInstant");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetTimeInstantSQLTimestampWithMicroseconds_literalMutation23423__5;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_literalMutation23423__6 = metadataJson.put("type", "SQL timestamp");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetTimeInstantSQLTimestampWithMicroseconds_literalMutation23423__6;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithMicroseconds_literalMutation23423__7 = metadataJson.put("value", "2017-01-01 00:00:01.123456");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetTimeInstantSQLTimestampWithMicroseconds_literalMutation23423__7;
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithMicroseconds_literalMutation23423__10 = metadatasJson.add(metadataJson);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testGetTimeInstantSQLTimestampWithMicroseconds_literalMutation23423__10;
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.lang.String vc_8774 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_8772 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_8772.isANumber(vc_8774);
                // MethodAssertGenerator build local variable
                Object o_35_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "\' is\'") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantSQLTimestampWithMicroseconds_literalMutation23423_cf25779 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMiliseconds_cf30103() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30103__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30103__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30103__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30103__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30103__7 = metadataJson.put("value", "2017-01-01 00:00:01.123");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30103__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30103__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30103__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_10031 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_10028 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30103__21 = // StatementAdderMethod cloned existing statement
vc_10028.isMAdeOfAlphaNumericsOrUnderscores(vc_10031);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30103__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMiliseconds_cf30124() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30124__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30124__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30124__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30124__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30124__7 = metadataJson.put("value", "2017-01-01 00:00:01.123");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30124__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30124__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30124__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_10040 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30124__19 = // StatementAdderMethod cloned existing statement
vc_10040.getCygnusVersion();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30124__19, "1.7.0");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMiliseconds_cf30146() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30146__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30146__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30146__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30146__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30146__7 = metadataJson.put("value", "2017-01-01 00:00:01.123");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30146__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30146__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30146__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_10060 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // StatementAdderMethod cloned existing statement
            vc_10060.printLoadedJars();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_10060);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMiliseconds_cf30135() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30135__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30135__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30135__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30135__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30135__7 = metadataJson.put("value", "2017-01-01 00:00:01.123");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30135__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30135__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30135__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.util.ArrayList vc_10055 = new java.util.ArrayList();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_10052 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30135__21 = // StatementAdderMethod cloned existing statement
vc_10052.toString(vc_10055);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30135__21, "");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMiliseconds_cf30101() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30101__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30101__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30101__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30101__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30101__7 = metadataJson.put("value", "2017-01-01 00:00:01.123");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30101__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30101__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30101__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_10028 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30101__19 = // StatementAdderMethod cloned existing statement
vc_10028.isMAdeOfAlphaNumericsOrUnderscores(metadatasStr);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30101__19);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMiliseconds_cf30111() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30111__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30111__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30111__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30111__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30111__7 = metadataJson.put("value", "2017-01-01 00:00:01.123");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30111__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30111__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30111__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_10035 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_10032 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.Long o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30111__21 = // StatementAdderMethod cloned existing statement
vc_10032.getTimeInstant(vc_10035);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30111__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMiliseconds_cf30084_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            metadataJson.put("name", "TimeInstant");
            metadataJson.put("type", "SQL timestamp");
            metadataJson.put("value", "2017-01-01 00:00:01.123");
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            metadatasJson.add(metadataJson);
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.lang.String vc_10021 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_10019 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_10019.isANumber(vc_10021);
                // MethodAssertGenerator build local variable
                Object o_23_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantSQLTimestampWithMiliseconds_cf30084 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMiliseconds_cf30134_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            metadataJson.put("name", "TimeInstant");
            metadataJson.put("type", "SQL timestamp");
            metadataJson.put("value", "2017-01-01 00:00:01.123");
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            metadatasJson.add(metadataJson);
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_10054 = (java.util.ArrayList)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_10052 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_10052.toString(vc_10054);
                // MethodAssertGenerator build local variable
                Object o_23_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantSQLTimestampWithMiliseconds_cf30134 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMiliseconds_cf30086() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30086__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30086__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30086__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30086__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30086__7 = metadataJson.put("value", "2017-01-01 00:00:01.123");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30086__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30086__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30086__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1548 = "SQL timestamp";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_10019 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30086__21 = // StatementAdderMethod cloned existing statement
vc_10019.isANumber(String_vc_1548);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30086__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMiliseconds_cf30095() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30095__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30095__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30095__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30095__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30095__7 = metadataJson.put("value", "2017-01-01 00:00:01.123");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30095__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30095__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30095__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            int vc_10027 = 174128184;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_10026 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_10023 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30095__23 = // StatementAdderMethod cloned existing statement
vc_10023.isANumber(vc_10026, vc_10027);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30095__23);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMiliseconds_cf30128() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30128__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30128__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30128__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30128__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30128__7 = metadataJson.put("value", "2017-01-01 00:00:01.123");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30128__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30128__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30128__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_10046 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30128__19 = // StatementAdderMethod cloned existing statement
vc_10046.getLastCommit();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30128__19, "UNKNOWN");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds_literalMutation30076 */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMiliseconds_literalMutation30076_cf32711_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_literalMutation30076__5 = metadataJson.put("name", "TimeInstant");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetTimeInstantSQLTimestampWithMiliseconds_literalMutation30076__5;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_literalMutation30076__6 = metadataJson.put("type", "SQL timestamp");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetTimeInstantSQLTimestampWithMiliseconds_literalMutation30076__6;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_literalMutation30076__7 = metadataJson.put("value", "2017-01-01 00:00:01.123");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetTimeInstantSQLTimestampWithMiliseconds_literalMutation30076__7;
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_literalMutation30076__10 = metadatasJson.add(metadataJson);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testGetTimeInstantSQLTimestampWithMiliseconds_literalMutation30076__10;
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.lang.String vc_11182 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_11180 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_11180.isANumber(vc_11182);
                // MethodAssertGenerator build local variable
                Object o_35_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "\'is \'") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantSQLTimestampWithMiliseconds_literalMutation30076_cf32711 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantSQLTimestampWithoutMiliseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp with miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithMiliseconds_cf30110 */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithMiliseconds_cf30110_cf34856_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp with miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30110__5 = metadataJson.put("name", "TimeInstant");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30110__5;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30110__6 = metadataJson.put("type", "SQL timestamp");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30110__6;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30110__7 = metadataJson.put("value", "2017-01-01 00:00:01.123");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30110__7;
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30110__10 = metadatasJson.add(metadataJson);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30110__10;
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_1551 = "value";
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_10032 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // AssertGenerator replace invocation
                java.lang.Long o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30110__21 = // StatementAdderMethod cloned existing statement
vc_10032.getTimeInstant(String_vc_1551);
                // MethodAssertGenerator build local variable
                Object o_35_0 = o_testGetTimeInstantSQLTimestampWithMiliseconds_cf30110__21;
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_11946 = (java.util.ArrayList)null;
                // StatementAdderMethod cloned existing statement
                vc_10032.toString(vc_11946);
                // MethodAssertGenerator build local variable
                Object o_41_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantSQLTimestampWithMiliseconds_cf30110_cf34856 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36448() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36448__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36448__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36448__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36448__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36448__7 = metadataJson.put("value", "2017-01-01 00:00:01");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36448__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36448__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36448__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            int vc_12220 = 1328093009;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_12219 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_12216 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36448__23 = // StatementAdderMethod cloned existing statement
vc_12216.isANumber(vc_12219, vc_12220);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36448__23);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36479() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36479__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36479__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36479__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36479__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36479__7 = metadataJson.put("value", "2017-01-01 00:00:01");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36479__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36479__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36479__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            boolean vc_12238 = false;
            // StatementAdderOnAssert create random local variable
            long vc_12237 = 2942447114560562277L;
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_12235 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36479__23 = // StatementAdderMethod cloned existing statement
vc_12235.getHumanReadable(vc_12237, vc_12238);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36479__23, "93244407-03-08T23:16:02.277");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__7 = metadataJson.put("value", "2017-01-01 00:00:01");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            int vc_12220 = 1328093009;
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_12216 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__21 = // StatementAdderMethod cloned existing statement
vc_12216.isANumber(metadatasStr, vc_12220);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36481() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36481__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36481__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36481__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36481__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36481__7 = metadataJson.put("value", "2017-01-01 00:00:01");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36481__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36481__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36481__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_12239 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36481__19 = // StatementAdderMethod cloned existing statement
vc_12239.getLastCommit();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36481__19, "UNKNOWN");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36463() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36463__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36463__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36463__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36463__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36463__7 = metadataJson.put("value", "2017-01-01 00:00:01");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36463__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36463__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36463__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1882 = "[CommonUtils.getTimeInstant]";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_12225 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.Long o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36463__21 = // StatementAdderMethod cloned existing statement
vc_12225.getTimeInstant(String_vc_1882);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36463__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36487_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            metadataJson.put("name", "TimeInstant");
            metadataJson.put("type", "SQL timestamp");
            metadataJson.put("value", "2017-01-01 00:00:01");
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            metadatasJson.add(metadataJson);
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_12247 = (java.util.ArrayList)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_12245 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_12245.toString(vc_12247);
                // MethodAssertGenerator build local variable
                Object o_23_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36487 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36477() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36477__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36477__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36477__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36477__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36477__7 = metadataJson.put("value", "2017-01-01 00:00:01");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36477__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36477__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36477__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_12233 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36477__19 = // StatementAdderMethod cloned existing statement
vc_12233.getCygnusVersion();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36477__19, "1.7.0");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36455() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36455__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36455__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36455__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36455__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36455__7 = metadataJson.put("value", "2017-01-01 00:00:01");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36455__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36455__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36455__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1881 = "type";
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_12221 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36455__21 = // StatementAdderMethod cloned existing statement
vc_12221.isMAdeOfAlphaNumericsOrUnderscores(String_vc_1881);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36455__21);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36499() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36499__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36499__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36499__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36499__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36499__7 = metadataJson.put("value", "2017-01-01 00:00:01");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36499__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36499__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36499__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_12253 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // StatementAdderMethod cloned existing statement
            vc_12253.printLoadedJars();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_12253);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36488() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36488__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36488__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36488__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36488__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36488__7 = metadataJson.put("value", "2017-01-01 00:00:01");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36488__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36488__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36488__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create random local variable
            java.util.ArrayList vc_12248 = new java.util.ArrayList();
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_12245 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            java.lang.String o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36488__21 = // StatementAdderMethod cloned existing statement
vc_12245.toString(vc_12248);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36488__21, "");
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36437_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            metadataJson.put("name", "TimeInstant");
            metadataJson.put("type", "SQL timestamp");
            metadataJson.put("value", "2017-01-01 00:00:01");
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            metadatasJson.add(metadataJson);
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create null value
                java.lang.String vc_12214 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_12212 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // StatementAdderMethod cloned existing statement
                vc_12212.isANumber(vc_12214);
                // MethodAssertGenerator build local variable
                Object o_23_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36437 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36454() {
        java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
        org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36454__5 = metadataJson.put("name", "TimeInstant");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36454__5);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36454__6 = metadataJson.put("type", "SQL timestamp");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36454__6);
        // AssertGenerator replace invocation
        java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36454__7 = metadataJson.put("value", "2017-01-01 00:00:01");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36454__7);
        org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
        // AssertGenerator replace invocation
        boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36454__10 = metadatasJson.add(metadataJson);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36454__10);
        java.lang.String metadatasStr = metadatasJson.toJSONString();
        java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
        try {
            // StatementAdderOnAssert create null value
            com.telefonica.iot.cygnus.utils.CommonUtils vc_12221 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36454__19 = // StatementAdderMethod cloned existing statement
vc_12221.isMAdeOfAlphaNumericsOrUnderscores(metadatasStr);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36454__19);
            org.junit.Assert.assertTrue((timeInstant != null));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
            throw e;
        }// try catch
        
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446 */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446_cf40240_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__5 = metadataJson.put("name", "TimeInstant");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__5;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__6 = metadataJson.put("type", "SQL timestamp");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__6;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__7 = metadataJson.put("value", "2017-01-01 00:00:01");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__7;
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__10 = metadatasJson.add(metadataJson);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__10;
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create random local variable
                int vc_12220 = 1328093009;
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_12216 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // AssertGenerator replace invocation
                boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__21 = // StatementAdderMethod cloned existing statement
vc_12216.isANumber(metadatasStr, vc_12220);
                // MethodAssertGenerator build local variable
                Object o_35_0 = o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446__21;
                // StatementAdderOnAssert create null value
                java.lang.String vc_13848 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                vc_12216.isANumber(vc_13848);
                // MethodAssertGenerator build local variable
                Object o_41_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36446_cf40240 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetTimeInstantISO8601TimestampWithMicroseconds
    /**
     * [CommonUtils.getTimeInstant] -------- When getting a time instant, it is properly obtained when
     * passing a valid SQL timestamp without miliseconds.
     */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds */
    /* amplification of com.telefonica.iot.cygnus.utils.CommonUtilsTest#testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36456 */
    @org.junit.Test(timeout = 10000)
    public void testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36456_cf41069_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-------- When getting a time instant, it is properly obtained when passing a valid ") + "SQL timestamp without miliseconds"));
            org.json.simple.JSONObject metadataJson = new org.json.simple.JSONObject();
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36456__5 = metadataJson.put("name", "TimeInstant");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36456__5;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36456__6 = metadataJson.put("type", "SQL timestamp");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36456__6;
            // AssertGenerator replace invocation
            java.lang.Object o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36456__7 = metadataJson.put("value", "2017-01-01 00:00:01");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36456__7;
            org.json.simple.JSONArray metadatasJson = new org.json.simple.JSONArray();
            // AssertGenerator replace invocation
            boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36456__10 = metadatasJson.add(metadataJson);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36456__10;
            java.lang.String metadatasStr = metadatasJson.toJSONString();
            java.lang.Long timeInstant = com.telefonica.iot.cygnus.utils.CommonUtils.getTimeInstant(metadatasStr);
            try {
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_12224 = new java.lang.String();
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.utils.CommonUtils vc_12221 = (com.telefonica.iot.cygnus.utils.CommonUtils)null;
                // AssertGenerator replace invocation
                boolean o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36456__21 = // StatementAdderMethod cloned existing statement
vc_12221.isMAdeOfAlphaNumericsOrUnderscores(vc_12224);
                // MethodAssertGenerator build local variable
                Object o_35_0 = o_testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36456__21;
                // StatementAdderOnAssert create null value
                java.util.ArrayList vc_14096 = (java.util.ArrayList)null;
                // StatementAdderMethod cloned existing statement
                vc_12221.toString(vc_14096);
                // MethodAssertGenerator build local variable
                Object o_41_0 = timeInstant != null;
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "-  OK  - Time instant obtained for '") + (metadatasJson.toJSONString())) + "' is '") + timeInstant) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CommonUtils.getTimeInstant]")) + "- FAIL - Time instant obtained is 'null'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetTimeInstantSQLTimestampWithoutMiliseconds_cf36456_cf41069 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

