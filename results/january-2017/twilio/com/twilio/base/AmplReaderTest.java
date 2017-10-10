

package com.twilio.base;


public class AmplReaderTest {
    @org.junit.Test
    public void testNoPagingDefaults() {
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader();
        org.junit.Assert.assertNull(reader.getLimit());
        org.junit.Assert.assertNull(reader.getPageSize());
    }

    @org.junit.Test
    public void testSetPageSize() {
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(100);
        org.junit.Assert.assertEquals(100, reader.getPageSize().intValue());
        org.junit.Assert.assertNull(reader.getLimit());
    }

    @org.junit.Test
    public void testMaxPageSize() {
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(java.lang.Integer.MAX_VALUE);
        org.junit.Assert.assertEquals(1000, reader.getPageSize().intValue());
        org.junit.Assert.assertNull(reader.getLimit());
    }

    @org.junit.Test
    public void testSetLimit() {
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(100);
        org.junit.Assert.assertEquals(100, reader.getLimit().intValue());
        org.junit.Assert.assertEquals(100, reader.getPageSize().intValue());
    }

    @org.junit.Test
    public void testSetLimitMaxPageSize() {
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(java.lang.Integer.MAX_VALUE);
        org.junit.Assert.assertEquals(java.lang.Integer.MAX_VALUE, reader.getLimit().intValue());
        org.junit.Assert.assertEquals(1000, reader.getPageSize().intValue());
    }

    @org.junit.Test
    public void testSetPageSizeLimit() {
        com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(1000).pageSize(5);
        org.junit.Assert.assertEquals(1000, reader.getLimit().intValue());
        org.junit.Assert.assertEquals(5, reader.getPageSize().intValue());
    }

    /* amplification of com.twilio.base.ReaderTest#testMaxPageSize */
    @org.junit.Test(timeout = 10000)
    public void testMaxPageSize_cf11759_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(java.lang.Integer.MAX_VALUE);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getPageSize().intValue();
            // StatementAdderMethod cloned existing statement
            reader.read();
            org.junit.Assert.assertNull(reader.getLimit());
            org.junit.Assert.fail("testMaxPageSize_cf11759 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testMaxPageSize */
    @org.junit.Test(timeout = 10000)
    public void testMaxPageSize_cf11741_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(java.lang.Integer.MAX_VALUE);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getPageSize().intValue();
            // StatementAdderMethod cloned existing statement
            reader.firstPage();
            org.junit.Assert.assertNull(reader.getLimit());
            org.junit.Assert.fail("testMaxPageSize_cf11741 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testMaxPageSize */
    @org.junit.Test(timeout = 10000)
    public void testMaxPageSize_cf11732_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(java.lang.Integer.MAX_VALUE);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getPageSize().intValue();
            // StatementAdderMethod cloned existing statement
            reader.readAsync();
            org.junit.Assert.assertNull(reader.getLimit());
            org.junit.Assert.fail("testMaxPageSize_cf11732 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testMaxPageSize */
    @org.junit.Test(timeout = 10000)
    public void testMaxPageSize_cf11741_failAssert8_cf12075() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(java.lang.Integer.MAX_VALUE);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getPageSize().intValue();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, 1000);
            // StatementAdderMethod cloned existing statement
            reader.firstPage();
            // StatementAdderOnAssert create random local variable
            int vc_6137 = -2139619447;
            // StatementAdderMethod cloned existing statement
            reader.pageSize(vc_6137);
            org.junit.Assert.assertNull(reader.getLimit());
            org.junit.Assert.fail("testMaxPageSize_cf11741 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testMaxPageSize */
    @org.junit.Test(timeout = 10000)
    public void testMaxPageSize_cf11765_cf12447_failAssert3_cf14688() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(java.lang.Integer.MAX_VALUE);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getPageSize().intValue();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, 1000);
            // AssertGenerator replace invocation
            java.lang.Long o_testMaxPageSize_cf11765__7 = // StatementAdderMethod cloned existing statement
reader.getLimit();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testMaxPageSize_cf11765__7);
            // StatementAdderMethod cloned existing statement
            reader.read();
            // StatementAdderMethod cloned existing statement
            reader.getPageSize();
            org.junit.Assert.assertNull(reader.getLimit());
            org.junit.Assert.fail("testMaxPageSize_cf11765_cf12447 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testNoPagingDefaults */
    @org.junit.Test(timeout = 10000)
    public void testNoPagingDefaults_cf25741_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader();
            org.junit.Assert.assertNull(reader.getLimit());
            // StatementAdderMethod cloned existing statement
            reader.firstPage();
            org.junit.Assert.assertNull(reader.getPageSize());
            org.junit.Assert.fail("testNoPagingDefaults_cf25741 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testNoPagingDefaults */
    @org.junit.Test(timeout = 10000)
    public void testNoPagingDefaults_cf25732_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader();
            org.junit.Assert.assertNull(reader.getLimit());
            // StatementAdderMethod cloned existing statement
            reader.readAsync();
            org.junit.Assert.assertNull(reader.getPageSize());
            org.junit.Assert.fail("testNoPagingDefaults_cf25732 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testNoPagingDefaults */
    @org.junit.Test(timeout = 10000)
    public void testNoPagingDefaults_cf25762_cf26326_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader();
            org.junit.Assert.assertNull(reader.getLimit());
            // AssertGenerator replace invocation
            java.lang.Long o_testNoPagingDefaults_cf25762__5 = // StatementAdderMethod cloned existing statement
reader.getLimit();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testNoPagingDefaults_cf25762__5);
            // StatementAdderMethod cloned existing statement
            reader.firstPage();
            org.junit.Assert.assertNull(reader.getPageSize());
            org.junit.Assert.fail("testNoPagingDefaults_cf25762_cf26326 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testNoPagingDefaults */
    @org.junit.Test(timeout = 10000)
    public void testNoPagingDefaults_cf25762_cf26317_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader();
            org.junit.Assert.assertNull(reader.getLimit());
            // AssertGenerator replace invocation
            java.lang.Long o_testNoPagingDefaults_cf25762__5 = // StatementAdderMethod cloned existing statement
reader.getLimit();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testNoPagingDefaults_cf25762__5);
            // StatementAdderMethod cloned existing statement
            reader.readAsync();
            org.junit.Assert.assertNull(reader.getPageSize());
            org.junit.Assert.fail("testNoPagingDefaults_cf25762_cf26317 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetLimit */
    @org.junit.Test(timeout = 10000)
    public void testSetLimit_cf29150_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(100);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getLimit().intValue();
            // StatementAdderMethod cloned existing statement
            reader.firstPage();
            // MethodAssertGenerator build local variable
            Object o_9_0 = reader.getPageSize().intValue();
            org.junit.Assert.fail("testSetLimit_cf29150 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetLimit */
    @org.junit.Test(timeout = 10000)
    public void testSetLimit_cf29141_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(100);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getLimit().intValue();
            // StatementAdderMethod cloned existing statement
            reader.readAsync();
            // MethodAssertGenerator build local variable
            Object o_9_0 = reader.getPageSize().intValue();
            org.junit.Assert.fail("testSetLimit_cf29141 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetLimit */
    @org.junit.Test(timeout = 10000)
    public void testSetLimit_cf29168_failAssert18_add29201() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(100);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getLimit().intValue();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, 100);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            reader.read();
            // StatementAdderMethod cloned existing statement
            reader.read();
            // MethodAssertGenerator build local variable
            Object o_9_0 = reader.getPageSize().intValue();
            org.junit.Assert.fail("testSetLimit_cf29168 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetLimitMaxPageSize */
    @org.junit.Test(timeout = 10000)
    public void testSetLimitMaxPageSize_cf29305_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(java.lang.Integer.MAX_VALUE);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getLimit().intValue();
            // StatementAdderMethod cloned existing statement
            reader.firstPage();
            // MethodAssertGenerator build local variable
            Object o_9_0 = reader.getPageSize().intValue();
            org.junit.Assert.fail("testSetLimitMaxPageSize_cf29305 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetLimitMaxPageSize */
    @org.junit.Test(timeout = 10000)
    public void testSetLimitMaxPageSize_cf29296_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(java.lang.Integer.MAX_VALUE);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getLimit().intValue();
            // StatementAdderMethod cloned existing statement
            reader.readAsync();
            // MethodAssertGenerator build local variable
            Object o_9_0 = reader.getPageSize().intValue();
            org.junit.Assert.fail("testSetLimitMaxPageSize_cf29296 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetLimitMaxPageSize */
    @org.junit.Test(timeout = 10000)
    public void testSetLimitMaxPageSize_cf29323_failAssert14_add29356() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(java.lang.Integer.MAX_VALUE);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getLimit().intValue();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, 2147483647);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            reader.read();
            // StatementAdderMethod cloned existing statement
            reader.read();
            // MethodAssertGenerator build local variable
            Object o_9_0 = reader.getPageSize().intValue();
            org.junit.Assert.fail("testSetLimitMaxPageSize_cf29323 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetLimitMaxPageSize */
    @org.junit.Test(timeout = 10000)
    public void testSetLimitMaxPageSize_cf29296_failAssert5_add29332_add29362() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(java.lang.Integer.MAX_VALUE);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getLimit().intValue();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, 2147483647);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, 2147483647);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            reader.readAsync();
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            reader.readAsync();
            // StatementAdderMethod cloned existing statement
            reader.readAsync();
            // MethodAssertGenerator build local variable
            Object o_9_0 = reader.getPageSize().intValue();
            org.junit.Assert.fail("testSetLimitMaxPageSize_cf29296 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetPageSize */
    @org.junit.Test(timeout = 10000)
    public void testSetPageSize_cf29451_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(100);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getPageSize().intValue();
            // StatementAdderMethod cloned existing statement
            reader.readAsync();
            org.junit.Assert.assertNull(reader.getLimit());
            org.junit.Assert.fail("testSetPageSize_cf29451 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetPageSize */
    @org.junit.Test(timeout = 10000)
    public void testSetPageSize_cf29478_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(100);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getPageSize().intValue();
            // StatementAdderMethod cloned existing statement
            reader.read();
            org.junit.Assert.assertNull(reader.getLimit());
            org.junit.Assert.fail("testSetPageSize_cf29478 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetPageSize */
    @org.junit.Test(timeout = 10000)
    public void testSetPageSize_cf29460_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(100);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getPageSize().intValue();
            // StatementAdderMethod cloned existing statement
            reader.firstPage();
            org.junit.Assert.assertNull(reader.getLimit());
            org.junit.Assert.fail("testSetPageSize_cf29460 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetPageSize */
    @org.junit.Test(timeout = 10000)
    public void testSetPageSize_cf29451_failAssert5_cf29690() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(100);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getPageSize().intValue();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, 100);
            // StatementAdderMethod cloned existing statement
            reader.readAsync();
            // StatementAdderOnAssert create random local variable
            int vc_16217 = 712224871;
            // StatementAdderOnAssert create null value
            com.twilio.base.Reader vc_16215 = (com.twilio.base.Reader)null;
            // StatementAdderMethod cloned existing statement
            vc_16215.pageSize(vc_16217);
            org.junit.Assert.assertNull(reader.getLimit());
            org.junit.Assert.fail("testSetPageSize_cf29451 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetPageSize */
    @org.junit.Test(timeout = 10000)
    public void testSetPageSize_cf29460_failAssert8_cf29803_cf32411() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().pageSize(100);
            // MethodAssertGenerator build local variable
            Object o_4_0 = reader.getPageSize().intValue();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, 100);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, 100);
            // StatementAdderMethod cloned existing statement
            reader.firstPage();
            // StatementAdderOnAssert create literal from method
            int int_vc_780 = 10000;
            // StatementAdderOnAssert create null value
            com.twilio.base.Reader vc_16287 = (com.twilio.base.Reader)null;
            // StatementAdderMethod cloned existing statement
            vc_16287.pageSize(int_vc_780);
            // StatementAdderOnAssert create literal from method
            int int_vc_854 = 10000;
            // StatementAdderOnAssert create null value
            com.twilio.base.Reader vc_17799 = (com.twilio.base.Reader)null;
            // StatementAdderMethod cloned existing statement
            vc_17799.pageSize(int_vc_854);
            org.junit.Assert.assertNull(reader.getLimit());
            org.junit.Assert.fail("testSetPageSize_cf29460 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetPageSizeLimit */
    @org.junit.Test(timeout = 10000)
    public void testSetPageSizeLimit_cf33907_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(1000).pageSize(5);
            // MethodAssertGenerator build local variable
            Object o_5_0 = reader.getLimit().intValue();
            // StatementAdderMethod cloned existing statement
            reader.firstPage();
            // MethodAssertGenerator build local variable
            Object o_10_0 = reader.getPageSize().intValue();
            org.junit.Assert.fail("testSetPageSizeLimit_cf33907 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetPageSizeLimit */
    @org.junit.Test(timeout = 10000)
    public void testSetPageSizeLimit_cf33898_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(1000).pageSize(5);
            // MethodAssertGenerator build local variable
            Object o_5_0 = reader.getLimit().intValue();
            // StatementAdderMethod cloned existing statement
            reader.readAsync();
            // MethodAssertGenerator build local variable
            Object o_10_0 = reader.getPageSize().intValue();
            org.junit.Assert.fail("testSetPageSizeLimit_cf33898 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }

    /* amplification of com.twilio.base.ReaderTest#testSetPageSizeLimit */
    @org.junit.Test(timeout = 10000)
    public void testSetPageSizeLimit_cf33925_failAssert18_add33958() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.base.Reader<com.twilio.rest.api.v2010.account.Call> reader = new com.twilio.rest.api.v2010.account.CallReader().limit(1000).pageSize(5);
            // MethodAssertGenerator build local variable
            Object o_5_0 = reader.getLimit().intValue();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, 1000);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            reader.read();
            // StatementAdderMethod cloned existing statement
            reader.read();
            // MethodAssertGenerator build local variable
            Object o_10_0 = reader.getPageSize().intValue();
            org.junit.Assert.fail("testSetPageSizeLimit_cf33925 should have thrown AuthenticationException");
        } catch (com.twilio.exception.AuthenticationException eee) {
        }
    }
}

