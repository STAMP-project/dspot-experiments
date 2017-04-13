/**
 * Copyright 2015-2017 Telefonica Investigaci?n y Desarrollo, S.A.U
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
 * this is required by "fail" like assertions
 */
/**
 * JsonResponseTest
 */


package com.telefonica.iot.cygnus.backends.http;


/**
 * @author frb
 */
@org.junit.runner.RunWith(value = org.mockito.runners.MockitoJUnitRunner.class)
public class AmplJsonResponseTest {
    // instance to be tested
    private com.telefonica.iot.cygnus.backends.http.JsonResponse response;

    /**
     * Sets up tests by creating a unique instance of the tested class, and by defining the behaviour of the mocked
     * classes.
     *
     * @throws Exception
     */
    @org.junit.Before
    public void setUp() throws java.lang.Exception {
        // set up the instance of the tested class
        org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
        obj.put("test", "test");
        response = new com.telefonica.iot.cygnus.backends.http.JsonResponse(obj, 200, "OK", null);
    }

    // setUp
    /**
     * Test of getJsonObject method, of class JsonResponse.
     */
    @org.junit.Test
    public void testGetJsonObject() {
        java.lang.System.out.println("Testing JsonResponseTest.getJsonObject");
        org.junit.Assert.assertTrue(response.getJsonObject().containsKey("test"));
    }

    // testIsCachedRes
    /**
     * Test of getStatusCode method, of class JsonResponse.
     */
    @org.junit.Test
    public void testGetStatusCode() {
        java.lang.System.out.println("Testing JsonResponseTest.getStatusCode");
        org.junit.Assert.assertEquals(200, response.getStatusCode());
    }

    // testGetStatusCode
    /**
     * Test of getReasonPhrase method, of class JsonResponse.
     */
    @org.junit.Test
    public void testGetReasonPhrase() {
        java.lang.System.out.println("Testing JsonResponseTest.getReasonPhrase");
        org.junit.Assert.assertEquals("OK", response.getReasonPhrase());
    }

    // testGetReasonPhrase
    /**
     * Test of getLocationHeader method, of class CKANCache.
     */
    // testGetLocationHeader
    @org.junit.Test
    public void testGetLocationHeader() {
        java.lang.System.out.println("Testing JsonResponseTest.getLocationHeader");
        org.junit.Assert.assertEquals(null, response.getLocationHeader());
    }
}

