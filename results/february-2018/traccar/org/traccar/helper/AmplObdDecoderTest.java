package org.traccar.helper;


public class AmplObdDecoderTest {
    @org.junit.Test
    public void testDecode() {
        org.junit.Assert.assertEquals(83, org.traccar.helper.ObdDecoder.decode(1, "057b").getValue());
        org.junit.Assert.assertEquals(1225, org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue());
        org.junit.Assert.assertEquals(20, org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue());
        org.junit.Assert.assertEquals(64050, org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue());
        org.junit.Assert.assertEquals(25, org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue());
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    @org.junit.Test(timeout = 10000)
    public void testDecode_literalMutationNumber2_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.helper.ObdDecoder.decode(// TestDataMutator on numbers
            0, "057b").getValue();
            org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue();
            org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue();
            org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue();
            org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecode_literalMutationNumber2 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    @org.junit.Test(timeout = 10000)
    public void testDecode_literalMutationString49_failAssert30() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.helper.ObdDecoder.decode(1, "057b").getValue();
            org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue();
            org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue();
            org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue();
            org.traccar.helper.ObdDecoder.decode(1, "2241").getValue();
            org.junit.Assert.fail("testDecode_literalMutationString49 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    @org.junit.Test(timeout = 10000)
    public void testDecode_literalMutationNumber1() {
        // AssertGenerator create local variable with return value of invocation
        java.lang.Object o_testDecode_literalMutationNumber1__1 = org.traccar.helper.ObdDecoder.decode(// TestDataMutator on numbers
        2, "057b").getValue();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(83, ((int) (o_testDecode_literalMutationNumber1__1)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.Object o_testDecode_literalMutationNumber1__4 = org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1225, ((int) (o_testDecode_literalMutationNumber1__4)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.Object o_testDecode_literalMutationNumber1__6 = org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(20, ((int) (o_testDecode_literalMutationNumber1__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.Object o_testDecode_literalMutationNumber1__8 = org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue();
        // AssertGenerator create local variable with return value of invocation
        java.lang.Object o_testDecode_literalMutationNumber1__10 = org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(25, ((int) (o_testDecode_literalMutationNumber1__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(64050, ((int) (o_testDecode_literalMutationNumber1__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(83, ((int) (o_testDecode_literalMutationNumber1__1)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(20, ((int) (o_testDecode_literalMutationNumber1__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1225, ((int) (o_testDecode_literalMutationNumber1__4)));
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode_literalMutationString28 */
    @org.junit.Test(timeout = 10000)
    public void testDecode_literalMutationString28_failAssert16_literalMutationNumber3204() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_literalMutationString28_failAssert16_literalMutationNumber3204__3 = org.traccar.helper.ObdDecoder.decode(1, "057b").getValue();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(83, ((int) (o_testDecode_literalMutationString28_failAssert16_literalMutationNumber3204__3)));
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_literalMutationString28_failAssert16_literalMutationNumber3204__5 = org.traccar.helper.ObdDecoder.decode(// TestDataMutator on numbers
            2, "0C1324").getValue();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(1225, ((int) (o_testDecode_literalMutationString28_failAssert16_literalMutationNumber3204__5)));
            org.traccar.helper.ObdDecoder.decode(1, "0y14").getValue();
            org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue();
            org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecode_literalMutationString28 should have thrown NumberFormatException");
        } catch (java.lang.NumberFormatException eee) {
        }
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode_literalMutationString50 */
    @org.junit.Test(timeout = 10000)
    public void testDecode_literalMutationString50_literalMutationString671_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_literalMutationString50__1 = org.traccar.helper.ObdDecoder.decode(1, "07b").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_literalMutationString50__3 = org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_literalMutationString50__5 = org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_literalMutationString50__7 = org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_literalMutationString50__9 = org.traccar.helper.ObdDecoder.decode(1, "2F1").getValue();
            org.junit.Assert.fail("testDecode_literalMutationString50_literalMutationString671 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode_literalMutationString30 */
    @org.junit.Test(timeout = 10000)
    public void testDecode_literalMutationString30_failAssert18_literalMutationNumber3415_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.traccar.helper.ObdDecoder.decode(// TestDataMutator on numbers
                0, "057b").getValue();
                org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue();
                org.traccar.helper.ObdDecoder.decode(1, " 5[g").getValue();
                org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue();
                org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue();
                org.junit.Assert.fail("testDecode_literalMutationString30 should have thrown NumberFormatException");
            } catch (java.lang.NumberFormatException eee) {
            }
            org.junit.Assert.fail("testDecode_literalMutationString30_failAssert18_literalMutationNumber3415 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode_literalMutationNumber1 */
    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode_literalMutationNumber1_add231 */
    @org.junit.Test(timeout = 10000)
    public void testDecode_literalMutationNumber1_add231_literalMutationString13523_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_literalMutationNumber1_add231__1 = // MethodCallAdder
            org.traccar.helper.ObdDecoder.decode(// TestDataMutator on numbers
            2, "057b").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_literalMutationNumber1__1 = org.traccar.helper.ObdDecoder.decode(// TestDataMutator on numbers
            2, "057b").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_literalMutationNumber1__4 = org.traccar.helper.ObdDecoder.decode(1, "").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_literalMutationNumber1__6 = org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_literalMutationNumber1__8 = org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_literalMutationNumber1__10 = org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecode_literalMutationNumber1_add231_literalMutationString13523 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode_add51 */
    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode_add51_add791 */
    @org.junit.Test(timeout = 10000)
    public void testDecode_add51_add791_literalMutationString13588_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_add51__1 = // MethodCallAdder
            org.traccar.helper.ObdDecoder.decode(1, "057b").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_add51__4 = org.traccar.helper.ObdDecoder.decode(1, "07b").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_add51__6 = org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_add51_add791__14 = // MethodCallAdder
            org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_add51__8 = org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_add51__10 = org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue();
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testDecode_add51__12 = org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecode_add51_add791_literalMutationString13588 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode */
    /* amplification of org.traccar.helper.ObdDecoderTest#testDecode_literalMutationString16 */
    @org.junit.Test(timeout = 10000)
    public void testDecode_literalMutationString16_failAssert8_literalMutationNumber2356_literalMutationNumber11856_failAssert32() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator create local variable with return value of invocation
                java.lang.Object o_testDecode_literalMutationString16_failAssert8_literalMutationNumber2356__3 = org.traccar.helper.ObdDecoder.decode(// TestDataMutator on numbers
                0, "057b").getValue();
                org.traccar.helper.ObdDecoder.decode(1, "").getValue();
                org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue();
                org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue();
                org.traccar.helper.ObdDecoder.decode(// TestDataMutator on numbers
                2, "2F41").getValue();
                org.junit.Assert.fail("testDecode_literalMutationString16 should have thrown StringIndexOutOfBoundsException");
            } catch (java.lang.StringIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testDecode_literalMutationString16_failAssert8_literalMutationNumber2356_literalMutationNumber11856 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

