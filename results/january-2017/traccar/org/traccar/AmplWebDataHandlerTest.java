

package org.traccar;


public class AmplWebDataHandlerTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testFormatRequest() throws java.lang.Exception {
        org.traccar.model.Position p = position("2016-01-01 01:02:03.000", true, 20, 30);
        org.traccar.WebDataHandler handler = new org.traccar.WebDataHandler("http://localhost/?fixTime={fixTime}&gprmc={gprmc}&name={name}");
        org.junit.Assert.assertEquals("http://localhost/?fixTime=1451610123000&gprmc=$GPRMC,010203.000,A,2000.0000,N,03000.0000,E,0.00,0.00,010116,,*05&name=test", handler.formatRequest(p));
    }

    /* amplification of org.traccar.WebDataHandlerTest#testFormatRequest */
    @org.junit.Test(timeout = 1000)
    public void testFormatRequest_cf28() throws java.lang.Exception {
        org.traccar.model.Position p = position("2016-01-01 01:02:03.000", true, 20, 30);
        org.traccar.WebDataHandler handler = new org.traccar.WebDataHandler("http://localhost/?fixTime={fixTime}&gprmc={gprmc}&name={name}");
        // AssertGenerator replace invocation
        org.traccar.model.Position o_testFormatRequest_cf28__5 = // StatementAdderMethod cloned existing statement
handler.handlePosition(p);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).getHours(), 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).getDay(), 5);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).getDate(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).toInstant()).getEpochSecond(), 1451610123L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).getMinutes(), 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).toGMTString(), "1 Jan 2016 01:02:03 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).getSeconds(), 3);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getSpeed(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).getSeconds(), 3);
        // AssertGenerator add assertion
        java.util.LinkedHashMap map_1628012889 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_1628012889, ((org.traccar.model.Position)o_testFormatRequest_cf28__5).getAttributes());;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).getMinutes(), 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getOutdated());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).toLocaleString(), "Jan 1, 2016 2:02:03 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).getDay(), 5);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).getDate(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getAccuracy(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getNetwork());
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getType());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getLongitude(), 30.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).getYear(), 116);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testFormatRequest_cf28__5.equals(p));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).toInstant()).toEpochMilli(), 1451610123000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).getTime(), 1451610123000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).getHours(), 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).getYear(), 116);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getLatitude(), 20.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getProtocol());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).getTime(), 1451610123000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getServerTime());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).toLocaleString(), "Jan 1, 2016 2:02:03 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getAddress());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getCourse(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getAltitude(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getFixTime()).toInstant()).toEpochMilli(), 1451610123000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.model.Position)o_testFormatRequest_cf28__5).getValid());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).toInstant()).getEpochSecond(), 1451610123L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf28__5).getDeviceTime()).toGMTString(), "1 Jan 2016 01:02:03 GMT");
        org.junit.Assert.assertEquals("http://localhost/?fixTime=1451610123000&gprmc=$GPRMC,010203.000,A,2000.0000,N,03000.0000,E,0.00,0.00,010116,,*05&name=test", handler.formatRequest(p));
    }

    /* amplification of org.traccar.WebDataHandlerTest#testFormatRequest */
    @org.junit.Test(timeout = 1000)
    public void testFormatRequest_cf30_failAssert19() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position p = position("2016-01-01 01:02:03.000", true, 20, 30);
            org.traccar.WebDataHandler handler = new org.traccar.WebDataHandler("http://localhost/?fixTime={fixTime}&gprmc={gprmc}&name={name}");
            // StatementAdderOnAssert create random local variable
            org.traccar.model.Position vc_7 = new org.traccar.model.Position();
            // StatementAdderMethod cloned existing statement
            handler.handlePosition(vc_7);
            // MethodAssertGenerator build local variable
            Object o_9_0 = handler.formatRequest(p);
            org.junit.Assert.fail("testFormatRequest_cf30 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.WebDataHandlerTest#testFormatRequest */
    @org.junit.Test(timeout = 1000)
    public void testFormatRequest_cf26_failAssert17_add158() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position p = position("2016-01-01 01:02:03.000", true, 20, 30);
            org.traccar.WebDataHandler handler = new org.traccar.WebDataHandler("http://localhost/?fixTime={fixTime}&gprmc={gprmc}&name={name}");
            // StatementAdderOnAssert create random local variable
            org.traccar.model.Position vc_7 = new org.traccar.model.Position();
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getServerTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((org.traccar.model.Position)vc_7).getOutdated());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getProtocol());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getAddress());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getDeviceId(), 0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getAltitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getFixTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getNetwork());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((org.traccar.model.Position)vc_7).getValid());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getAccuracy(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getCourse(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getDeviceTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getId(), 0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getSpeed(), 0.0D);
            // AssertGenerator add assertion
            java.util.LinkedHashMap map_1869325281 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_1869325281, ((org.traccar.model.Position)vc_7).getAttributes());;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getType());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getLatitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getLongitude(), 0.0D);
            // StatementAdderOnAssert create null value
            org.traccar.WebDataHandler vc_4 = (org.traccar.WebDataHandler)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_4);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_4.handlePosition(vc_7);
            // StatementAdderMethod cloned existing statement
            vc_4.handlePosition(vc_7);
            // MethodAssertGenerator build local variable
            Object o_11_0 = handler.formatRequest(p);
            org.junit.Assert.fail("testFormatRequest_cf26 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.WebDataHandlerTest#testFormatRequest */
    @org.junit.Test(timeout = 1000)
    public void testFormatRequest_cf16_cf133_cf530_failAssert10() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position p = position("2016-01-01 01:02:03.000", true, 20, 30);
            org.traccar.WebDataHandler handler = new org.traccar.WebDataHandler("http://localhost/?fixTime={fixTime}&gprmc={gprmc}&name={name}");
            // AssertGenerator replace invocation
            java.lang.String o_testFormatRequest_cf16__5 = // StatementAdderMethod cloned existing statement
handler.formatRequest(p);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testFormatRequest_cf16__5, "http://localhost/?fixTime=1451610123000&gprmc=$GPRMC,010203.000,A,2000.0000,N,03000.0000,E,0.00,0.00,010116,,*05&name=test");
            // AssertGenerator replace invocation
            org.traccar.model.Position o_testFormatRequest_cf16_cf133__9 = // StatementAdderMethod cloned existing statement
handler.handlePosition(p);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getTimezoneOffset(), -60);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getSeconds(), 3);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).toInstant()).getNano(), 0);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getMinutes(), 2);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getTime(), 1451610123000L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getSpeed(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).toGMTString(), "1 Jan 2016 01:02:03 GMT");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getHours(), 2);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getServerTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getAccuracy(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).toLocaleString(), "Jan 1, 2016 2:02:03 AM");
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getNetwork());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getId(), 0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getType());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceId(), 0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).toInstant()).toEpochMilli(), 1451610123000L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getTimezoneOffset(), -60);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).toLocaleString(), "Jan 1, 2016 2:02:03 AM");
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getValid());
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(o_testFormatRequest_cf16_cf133__9.equals(p));
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getDate(), 1);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getSeconds(), 3);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getMinutes(), 2);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getDay(), 5);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getTime(), 1451610123000L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getDate(), 1);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).toInstant()).getNano(), 0);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getAddress());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getDay(), 5);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getLongitude(), 30.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).toGMTString(), "1 Jan 2016 01:02:03 GMT");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getYear(), 116);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).toInstant()).toEpochMilli(), 1451610123000L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getHours(), 2);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getOutdated());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getCourse(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getLatitude(), 20.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getMonth(), 0);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getMonth(), 0);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getYear(), 116);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getProtocol());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getAltitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).toInstant()).getEpochSecond(), 1451610123L);
            // AssertGenerator add assertion
            java.util.LinkedHashMap map_943058118 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_943058118, ((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getAttributes());;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).toInstant()).getEpochSecond(), 1451610123L);
            // StatementAdderOnAssert create null value
            org.traccar.model.Position vc_38 = (org.traccar.model.Position)null;
            // StatementAdderOnAssert create null value
            org.traccar.WebDataHandler vc_36 = (org.traccar.WebDataHandler)null;
            // StatementAdderMethod cloned existing statement
            vc_36.handlePosition(vc_38);
            // MethodAssertGenerator build local variable
            Object o_107_0 = handler.formatRequest(p);
            org.junit.Assert.fail("testFormatRequest_cf16_cf133_cf530 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.WebDataHandlerTest#testFormatRequest */
    @org.junit.Test(timeout = 1000)
    public void testFormatRequest_cf16_cf133_cf523() throws java.lang.Exception {
        org.traccar.model.Position p = position("2016-01-01 01:02:03.000", true, 20, 30);
        org.traccar.WebDataHandler handler = new org.traccar.WebDataHandler("http://localhost/?fixTime={fixTime}&gprmc={gprmc}&name={name}");
        // AssertGenerator replace invocation
        java.lang.String o_testFormatRequest_cf16__5 = // StatementAdderMethod cloned existing statement
handler.formatRequest(p);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testFormatRequest_cf16__5, "http://localhost/?fixTime=1451610123000&gprmc=$GPRMC,010203.000,A,2000.0000,N,03000.0000,E,0.00,0.00,010116,,*05&name=test");
        // AssertGenerator replace invocation
        org.traccar.model.Position o_testFormatRequest_cf16_cf133__9 = // StatementAdderMethod cloned existing statement
handler.handlePosition(p);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getSeconds(), 3);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getMinutes(), 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getTime(), 1451610123000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getSpeed(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).toGMTString(), "1 Jan 2016 01:02:03 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getHours(), 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getServerTime());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getAccuracy(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).toLocaleString(), "Jan 1, 2016 2:02:03 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getNetwork());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getType());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).toInstant()).toEpochMilli(), 1451610123000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).toLocaleString(), "Jan 1, 2016 2:02:03 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getValid());
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testFormatRequest_cf16_cf133__9.equals(p));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getDate(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getSeconds(), 3);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getMinutes(), 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getDay(), 5);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getTime(), 1451610123000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getDate(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getAddress());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getDay(), 5);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getLongitude(), 30.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).toGMTString(), "1 Jan 2016 01:02:03 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getYear(), 116);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).toInstant()).toEpochMilli(), 1451610123000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getHours(), 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getOutdated());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getCourse(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getLatitude(), 20.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).getYear(), 116);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getProtocol());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getAltitude(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getFixTime()).toInstant()).getEpochSecond(), 1451610123L);
        // AssertGenerator add assertion
        java.util.LinkedHashMap map_943058118 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_943058118, ((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getAttributes());;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.model.Position)o_testFormatRequest_cf16_cf133__9).getDeviceTime()).toInstant()).getEpochSecond(), 1451610123L);
        // AssertGenerator replace invocation
        java.lang.String o_testFormatRequest_cf16_cf133_cf523__101 = // StatementAdderMethod cloned existing statement
handler.formatRequest(p);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testFormatRequest_cf16_cf133_cf523__101, "http://localhost/?fixTime=1451610123000&gprmc=$GPRMC,010203.000,A,2000.0000,N,03000.0000,E,0.00,0.00,010116,,*05&name=test");
        org.junit.Assert.assertEquals("http://localhost/?fixTime=1451610123000&gprmc=$GPRMC,010203.000,A,2000.0000,N,03000.0000,E,0.00,0.00,010116,,*05&name=test", handler.formatRequest(p));
    }
}

