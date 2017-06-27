/**
 * Copyright 2017 Telefonica Investigaci?n y Desarrollo, S.A.U
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
 * CygnusHandlerTest
 */


package com.telefonica.iot.cygnus.handlers;


/**
 * @author frb
 */
public class AmplCygnusHandlerTest {
    /**
     * Constructor.
     */
    // CygnusHandlerTest
    public AmplCygnusHandlerTest() {
        org.apache.log4j.LogManager.getRootLogger().setLevel(org.apache.log4j.Level.FATAL);
    }

    /**
     * Dummy class for testing purposes.
     */
    private class CygnusHandlerImpl extends com.telefonica.iot.cygnus.handlers.CygnusHandler {    }

    // CygnusHandlerImpl
    /**
     * [CygnusHandler.getServiceMetrics] -------- Not null metrics are retrieved.
     */
    @org.junit.Test
    public void testGetServiceMetrics() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusHandler.getServiceMetrics]")) + " - Not null metrics are retrieved"));
        com.telefonica.iot.cygnus.handlers.AmplCygnusHandlerTest.CygnusHandlerImpl ch = new com.telefonica.iot.cygnus.handlers.AmplCygnusHandlerTest.CygnusHandlerImpl();
        com.telefonica.iot.cygnus.metrics.CygnusMetrics metrics = ch.getServiceMetrics();
        try {
            org.junit.Assert.assertTrue((metrics != null));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusMetrics.getServiceMetrics]")) + " -  OK  - Not null metrics were retrieved"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusMetrics.getServiceMetrics]")) + " - FAIL - Null metrics were retrieved"));
            throw e;
        }// try catch
        
    }

    // testGetServiceMetrics
    /**
     * [CygnusHandler.setServiceMetrics] -------- Given metrics are set.
     */
    // testGetServiceMetrics
    @org.junit.Test
    public void testSetServiceMetrics() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusHandler.setServiceMetrics]")) + " - Given metrics are set"));
        com.telefonica.iot.cygnus.handlers.AmplCygnusHandlerTest.CygnusHandlerImpl ch = new com.telefonica.iot.cygnus.handlers.AmplCygnusHandlerTest.CygnusHandlerImpl();
        com.telefonica.iot.cygnus.metrics.CygnusMetrics metrics = new com.telefonica.iot.cygnus.metrics.CygnusMetrics();
        ch.setServiceMetrics(metrics);
        try {
            org.junit.Assert.assertEquals(metrics, ch.getServiceMetrics());
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusMetrics.setServiceMetrics]")) + " -  OK  - Metrics were set"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusMetrics.setServiceMetrics]")) + " - FAIL - Metrics were not set"));
            throw e;
        }// try catch
        
    }
}

