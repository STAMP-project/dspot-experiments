

package org.traccar.helper;


public class AmplBitBufferTest {
    @org.junit.Test
    public void test() {
        org.traccar.helper.BitBuffer buffer = new org.traccar.helper.BitBuffer();
        buffer.write(36);
        buffer.write(54);
        buffer.write(63);
        buffer.write(63);
        org.junit.Assert.assertEquals(4, buffer.readUnsigned(3));
        org.junit.Assert.assertEquals((-7), buffer.readSigned(4));
        org.junit.Assert.assertEquals(22, buffer.readUnsigned(5));
    }

    /* amplification of org.traccar.helper.BitBufferTest#test */
    @org.junit.Test(timeout = 1000)
    public void test_cf37_cf303() {
        org.traccar.helper.BitBuffer buffer = new org.traccar.helper.BitBuffer();
        buffer.write(36);
        buffer.write(54);
        buffer.write(63);
        buffer.write(63);
        org.junit.Assert.assertEquals(4, buffer.readUnsigned(3));
        org.junit.Assert.assertEquals((-7), buffer.readSigned(4));
        // StatementAdderOnAssert create random local variable
        int vc_8 = -1342873841;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_8, -1342873841);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_8, -1342873841);
        // StatementAdderOnAssert create random local variable
        org.traccar.helper.BitBuffer vc_7 = new org.traccar.helper.BitBuffer();
        // StatementAdderMethod cloned existing statement
        vc_7.write(vc_8);
        // AssertGenerator replace invocation
        int o_test_cf37_cf303__20 = // StatementAdderMethod cloned existing statement
buffer.readSigned(vc_8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_test_cf37_cf303__20, 0);
        org.junit.Assert.assertEquals(22, buffer.readUnsigned(5));
    }

    /* amplification of org.traccar.helper.BitBufferTest#test */
    @org.junit.Test(timeout = 1000)
    public void test_cf35_cf191_cf3113() {
        org.traccar.helper.BitBuffer buffer = new org.traccar.helper.BitBuffer();
        buffer.write(36);
        buffer.write(54);
        buffer.write(63);
        buffer.write(63);
        org.junit.Assert.assertEquals(4, buffer.readUnsigned(3));
        org.junit.Assert.assertEquals((-7), buffer.readSigned(4));
        // StatementAdderOnAssert create random local variable
        int vc_8 = -1342873841;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_8, -1342873841);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_8, -1342873841);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_8, -1342873841);
        // StatementAdderMethod cloned existing statement
        buffer.write(vc_8);
        // StatementAdderOnAssert create random local variable
        org.traccar.helper.BitBuffer vc_33 = new org.traccar.helper.BitBuffer();
        // StatementAdderMethod cloned existing statement
        vc_33.write(vc_8);
        // StatementAdderOnAssert create literal from method
        int int_vc_135 = -1342873841;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_135, -1342873841);
        // AssertGenerator replace invocation
        int o_test_cf35_cf191_cf3113__26 = // StatementAdderMethod cloned existing statement
buffer.readSigned(int_vc_135);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_test_cf35_cf191_cf3113__26, 0);
        org.junit.Assert.assertEquals(22, buffer.readUnsigned(5));
    }
}

