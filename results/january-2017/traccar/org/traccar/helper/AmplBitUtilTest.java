

package org.traccar.helper;


public class AmplBitUtilTest {
    @org.junit.Test
    public void testCheck() {
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(0, 0));
        org.junit.Assert.assertTrue(org.traccar.helper.BitUtil.check(1, 0));
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(2, 0));
    }

    @org.junit.Test
    public void testBetween() {
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.between(0, 0, 0));
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.between(1, 0, 1));
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(2, 0, 2));
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(6, 0, 2));
    }

    @org.junit.Test
    public void testFrom() {
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.from(1, 0));
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.from(1, 1));
    }

    @org.junit.Test
    public void testTo() {
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.to(2, 2));
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.to(2, 1));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testBetween */
    @org.junit.Test(timeout = 1000)
    public void testBetween_cf37() {
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.between(0, 0, 0));
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.between(1, 0, 1));
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(2, 0, 2));
        // StatementAdderOnAssert create random local variable
        int vc_20 = 109393251;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_20, 109393251);
        // StatementAdderOnAssert create random local variable
        long vc_19 = 1278970058164205098L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_19, 1278970058164205098L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_17 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_17);
        // AssertGenerator replace invocation
        long o_testBetween_cf37__13 = // StatementAdderMethod cloned existing statement
vc_17.from(vc_19, vc_20);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testBetween_cf37__13, 37222927L);
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(6, 0, 2));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testBetween */
    @org.junit.Test(timeout = 1000)
    public void testBetween_cf41() {
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.between(0, 0, 0));
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.between(1, 0, 1));
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(2, 0, 2));
        // StatementAdderOnAssert create random local variable
        int vc_24 = 307886798;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_24, 307886798);
        // StatementAdderOnAssert create random local variable
        long vc_23 = -3495687074601681896L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_23, -3495687074601681896L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_21 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_21);
        // AssertGenerator replace invocation
        long o_testBetween_cf41__13 = // StatementAdderMethod cloned existing statement
vc_21.to(vc_23, vc_24);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testBetween_cf41__13, 15384L);
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(6, 0, 2));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testBetween */
    @org.junit.Test(timeout = 1000)
    public void testBetween_cf40() {
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.between(0, 0, 0));
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.between(1, 0, 1));
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(2, 0, 2));
        // StatementAdderOnAssert create literal from method
        int int_vc_6 = 2;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_6, 2);
        // StatementAdderOnAssert create random local variable
        long vc_23 = -3495687074601681896L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_23, -3495687074601681896L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_21 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_21);
        // AssertGenerator replace invocation
        long o_testBetween_cf40__13 = // StatementAdderMethod cloned existing statement
vc_21.to(vc_23, int_vc_6);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testBetween_cf40__13, 0L);
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(6, 0, 2));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testBetween */
    @org.junit.Test(timeout = 1000)
    public void testBetween_cf17_cf469() {
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.between(0, 0, 0));
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.between(1, 0, 1));
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(2, 0, 2));
        // StatementAdderOnAssert create random local variable
        int vc_3 = 2086707665;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_3, 2086707665);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_3, 2086707665);
        // StatementAdderOnAssert create random local variable
        long vc_2 = -2662824784957808284L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, -2662824784957808284L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, -2662824784957808284L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_0 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator replace invocation
        boolean o_testBetween_cf17__13 = // StatementAdderMethod cloned existing statement
vc_0.check(vc_2, vc_3);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testBetween_cf17__13);
        // StatementAdderOnAssert create literal from method
        int int_vc_26 = 0;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_26, 0);
        // AssertGenerator replace invocation
        long o_testBetween_cf17_cf469__25 = // StatementAdderMethod cloned existing statement
vc_0.from(vc_2, int_vc_26);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testBetween_cf17_cf469__25, -2662824784957808284L);
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(6, 0, 2));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testBetween */
    @org.junit.Test(timeout = 1000)
    public void testBetween_cf36_cf777() {
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.between(0, 0, 0));
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.between(1, 0, 1));
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(2, 0, 2));
        // StatementAdderOnAssert create literal from method
        int int_vc_5 = 0;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_5, 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_5, 0);
        // StatementAdderOnAssert create random local variable
        long vc_19 = 1278970058164205098L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_19, 1278970058164205098L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_19, 1278970058164205098L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_17 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_17);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_17);
        // AssertGenerator replace invocation
        long o_testBetween_cf36__13 = // StatementAdderMethod cloned existing statement
vc_17.from(vc_19, int_vc_5);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testBetween_cf36__13, 1278970058164205098L);
        // StatementAdderOnAssert create random local variable
        int vc_99 = -1140420648;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_99, -1140420648);
        // StatementAdderOnAssert create literal from method
        long long_vc_38 = 1278970058164205098L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_38, 1278970058164205098L);
        // AssertGenerator replace invocation
        long o_testBetween_cf36_cf777__27 = // StatementAdderMethod cloned existing statement
vc_17.to(long_vc_38, vc_99);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testBetween_cf36_cf777__27, 3995178L);
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(6, 0, 2));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testBetween */
    @org.junit.Test(timeout = 1000)
    public void testBetween_cf17_cf509() {
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.between(0, 0, 0));
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.between(1, 0, 1));
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(2, 0, 2));
        // StatementAdderOnAssert create random local variable
        int vc_3 = 2086707665;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_3, 2086707665);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_3, 2086707665);
        // StatementAdderOnAssert create random local variable
        long vc_2 = -2662824784957808284L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, -2662824784957808284L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, -2662824784957808284L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_0 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator replace invocation
        boolean o_testBetween_cf17__13 = // StatementAdderMethod cloned existing statement
vc_0.check(vc_2, vc_3);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testBetween_cf17__13);
        // StatementAdderOnAssert create literal from method
        int int_vc_28 = 0;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_28, 0);
        // StatementAdderOnAssert create literal from method
        long long_vc_27 = -2662824784957808284L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_27, -2662824784957808284L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_71 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_71);
        // AssertGenerator replace invocation
        long o_testBetween_cf17_cf509__29 = // StatementAdderMethod cloned existing statement
vc_71.to(long_vc_27, int_vc_28);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testBetween_cf17_cf509__29, 0L);
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(6, 0, 2));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testBetween */
    @org.junit.Test(timeout = 0)
    public void testBetween_cf17_cf509_literalMutation1571() {
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.between(0, 0, 0));
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.between(1, 0, 1));
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(2, 0, 2));
        // StatementAdderOnAssert create random local variable
        int vc_3 = 2086707665;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_3, 2086707665);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_3, 2086707665);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_3, 2086707665);
        // StatementAdderOnAssert create random local variable
        long vc_2 = -2662824784957808284L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, -2662824784957808284L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, -2662824784957808284L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2, -2662824784957808284L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_0 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator replace invocation
        boolean o_testBetween_cf17__13 = // StatementAdderMethod cloned existing statement
vc_0.check(vc_2, vc_3);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testBetween_cf17__13);
        // StatementAdderOnAssert create literal from method
        int int_vc_28 = 0;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_28, 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_28, 0);
        // StatementAdderOnAssert create literal from method
        long long_vc_27 = -2662824784957808284L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_27, -2662824784957808284L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_27, -2662824784957808284L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_71 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_71);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_71);
        // AssertGenerator replace invocation
        long o_testBetween_cf17_cf509__29 = // StatementAdderMethod cloned existing statement
vc_71.to(long_vc_27, int_vc_28);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testBetween_cf17_cf509__29, 0L);
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(6, 0, 2));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testBetween */
    @org.junit.Test(timeout = 1000)
    public void testBetween_cf36_cf588_literalMutation6217() {
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.between(0, 0, 0));
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.between(1, 0, 1));
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(2, 0, 2));
        // StatementAdderOnAssert create literal from method
        int int_vc_5 = 0;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_5, 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_5, 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_5, 0);
        // StatementAdderOnAssert create random local variable
        long vc_19 = 1278970058164205098L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_19, 1278970058164205098L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_19, 1278970058164205098L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_19, 1278970058164205098L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_17 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_17);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_17);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_17);
        // AssertGenerator replace invocation
        long o_testBetween_cf36__13 = // StatementAdderMethod cloned existing statement
vc_17.from(vc_19, int_vc_5);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testBetween_cf36__13, 1278970058164205098L);
        // StatementAdderOnAssert create literal from method
        int int_vc_30 = 6;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_30, 6);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_30, 6);
        // AssertGenerator replace invocation
        boolean o_testBetween_cf36_cf588__25 = // StatementAdderMethod cloned existing statement
vc_17.check(vc_19, int_vc_30);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_testBetween_cf36_cf588__25);
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.between(6, 0, 2));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testCheck */
    @org.junit.Test(timeout = 1000)
    public void testCheck_cf15482() {
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(0, 0));
        org.junit.Assert.assertTrue(org.traccar.helper.BitUtil.check(1, 0));
        // StatementAdderOnAssert create random local variable
        int vc_1549 = -865441718;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1549, -865441718);
        // StatementAdderOnAssert create random local variable
        long vc_1548 = 7162778826012923506L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1548, 7162778826012923506L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_1546 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1546);
        // AssertGenerator replace invocation
        long o_testCheck_cf15482__11 = // StatementAdderMethod cloned existing statement
vc_1546.to(vc_1548, vc_1549);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCheck_cf15482__11, 626L);
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(2, 0));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testCheck */
    @org.junit.Test(timeout = 1000)
    public void testCheck_cf15481() {
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(0, 0));
        org.junit.Assert.assertTrue(org.traccar.helper.BitUtil.check(1, 0));
        // StatementAdderOnAssert create literal from method
        int int_vc_673 = 0;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_673, 0);
        // StatementAdderOnAssert create random local variable
        long vc_1548 = 7162778826012923506L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1548, 7162778826012923506L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_1546 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1546);
        // AssertGenerator replace invocation
        long o_testCheck_cf15481__11 = // StatementAdderMethod cloned existing statement
vc_1546.to(vc_1548, int_vc_673);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCheck_cf15481__11, 0L);
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(2, 0));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testCheck */
    @org.junit.Test(timeout = 1000)
    public void testCheck_cf15477() {
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(0, 0));
        org.junit.Assert.assertTrue(org.traccar.helper.BitUtil.check(1, 0));
        // StatementAdderOnAssert create literal from method
        int int_vc_672 = 0;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_672, 0);
        // StatementAdderOnAssert create random local variable
        long vc_1544 = 6424934348016978246L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1544, 6424934348016978246L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_1542 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1542);
        // AssertGenerator replace invocation
        long o_testCheck_cf15477__11 = // StatementAdderMethod cloned existing statement
vc_1542.from(vc_1544, int_vc_672);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCheck_cf15477__11, 6424934348016978246L);
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(2, 0));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testCheck */
    @org.junit.Test(timeout = 1000)
    public void testCheck_cf15477_cf16160() {
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(0, 0));
        org.junit.Assert.assertTrue(org.traccar.helper.BitUtil.check(1, 0));
        // StatementAdderOnAssert create literal from method
        int int_vc_672 = 0;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_672, 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_672, 0);
        // StatementAdderOnAssert create random local variable
        long vc_1544 = 6424934348016978246L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1544, 6424934348016978246L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1544, 6424934348016978246L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_1542 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1542);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1542);
        // AssertGenerator replace invocation
        long o_testCheck_cf15477__11 = // StatementAdderMethod cloned existing statement
vc_1542.from(vc_1544, int_vc_672);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCheck_cf15477__11, 6424934348016978246L);
        // StatementAdderOnAssert create literal from method
        int int_vc_706 = 0;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_706, 0);
        // StatementAdderOnAssert create random local variable
        long vc_1623 = -3012000433012135684L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1623, -3012000433012135684L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_1621 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1621);
        // AssertGenerator replace invocation
        long o_testCheck_cf15477_cf16160__27 = // StatementAdderMethod cloned existing statement
vc_1621.to(vc_1623, int_vc_706);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCheck_cf15477_cf16160__27, 0L);
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(2, 0));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testCheck */
    @org.junit.Test(timeout = 1000)
    public void testCheck_cf15478_cf16383() {
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(0, 0));
        org.junit.Assert.assertTrue(org.traccar.helper.BitUtil.check(1, 0));
        // StatementAdderOnAssert create random local variable
        int vc_1545 = 1039837911;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1545, 1039837911);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1545, 1039837911);
        // StatementAdderOnAssert create random local variable
        long vc_1544 = 6424934348016978246L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1544, 6424934348016978246L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1544, 6424934348016978246L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_1542 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1542);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1542);
        // AssertGenerator replace invocation
        long o_testCheck_cf15478__11 = // StatementAdderMethod cloned existing statement
vc_1542.from(vc_1544, vc_1545);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCheck_cf15478__11, 765911859037L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_1646 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1646);
        // AssertGenerator replace invocation
        long o_testCheck_cf15478_cf16383__23 = // StatementAdderMethod cloned existing statement
vc_1646.to(vc_1544, vc_1545);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCheck_cf15478_cf16383__23, 4327750L);
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(2, 0));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testCheck */
    @org.junit.Test(timeout = 1000)
    public void testCheck_cf15458_cf15918_cf20072() {
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(0, 0));
        org.junit.Assert.assertTrue(org.traccar.helper.BitUtil.check(1, 0));
        // StatementAdderOnAssert create random local variable
        int vc_1528 = 1310564523;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1528, 1310564523);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1528, 1310564523);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1528, 1310564523);
        // StatementAdderOnAssert create random local variable
        long vc_1527 = -5868754517739847445L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1527, -5868754517739847445L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1527, -5868754517739847445L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1527, -5868754517739847445L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_1525 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1525);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1525);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1525);
        // AssertGenerator replace invocation
        boolean o_testCheck_cf15458__11 = // StatementAdderMethod cloned existing statement
vc_1525.check(vc_1527, vc_1528);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_testCheck_cf15458__11);
        // StatementAdderOnAssert create literal from method
        long long_vc_694 = -5868754517739847445L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_694, -5868754517739847445L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_694, -5868754517739847445L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_1596 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1596);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1596);
        // AssertGenerator replace invocation
        long o_testCheck_cf15458_cf15918__25 = // StatementAdderMethod cloned existing statement
vc_1596.to(long_vc_694, vc_1528);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCheck_cf15458_cf15918__25, 7542770352363L);
        // StatementAdderOnAssert create random local variable
        int vc_2028 = 1085686872;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2028, 1085686872);
        // StatementAdderOnAssert create literal from method
        long long_vc_883 = -5868754517739847445L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_883, -5868754517739847445L);
        // AssertGenerator replace invocation
        boolean o_testCheck_cf15458_cf15918_cf20072__43 = // StatementAdderMethod cloned existing statement
vc_1596.check(long_vc_883, vc_2028);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_testCheck_cf15458_cf15918_cf20072__43);
        org.junit.Assert.assertFalse(org.traccar.helper.BitUtil.check(2, 0));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testFrom */
    @org.junit.Test(timeout = 1000)
    public void testFrom_cf28314() {
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.from(1, 0));
        // StatementAdderOnAssert create random local variable
        int vc_2895 = -1423530248;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2895, -1423530248);
        // StatementAdderOnAssert create random local variable
        long vc_2894 = -4037896843945700936L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2894, -4037896843945700936L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_2892 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2892);
        // AssertGenerator replace invocation
        long o_testFrom_cf28314__9 = // StatementAdderMethod cloned existing statement
vc_2892.from(vc_2894, vc_2895);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testFrom_cf28314__9, -57L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.from(1, 1));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testFrom */
    @org.junit.Test(timeout = 1000)
    public void testFrom_cf28318() {
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.from(1, 0));
        // StatementAdderOnAssert create random local variable
        int vc_2899 = 2122593254;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2899, 2122593254);
        // StatementAdderOnAssert create random local variable
        long vc_2898 = 6336135873179601770L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2898, 6336135873179601770L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_2896 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2896);
        // AssertGenerator replace invocation
        long o_testFrom_cf28318__9 = // StatementAdderMethod cloned existing statement
vc_2896.to(vc_2898, vc_2899);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testFrom_cf28318__9, 7004146538L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.from(1, 1));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testFrom */
    @org.junit.Test(timeout = 1000)
    public void testFrom_cf28313_literalMutation28815() {
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.from(1, 0));
        // StatementAdderOnAssert create literal from method
        int int_vc_1262 = 0;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_1262, 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_1262, 0);
        // StatementAdderOnAssert create random local variable
        long vc_2894 = -4037896843945700936L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2894, -4037896843945700936L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2894, -4037896843945700936L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_2892 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2892);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2892);
        // AssertGenerator replace invocation
        long o_testFrom_cf28313__9 = // StatementAdderMethod cloned existing statement
vc_2892.from(vc_2894, int_vc_1262);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testFrom_cf28313__9, -4037896843945700936L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.from(1, 1));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testFrom */
    @org.junit.Test(timeout = 1000)
    public void testFrom_cf28313_cf29006_cf40240() {
        org.junit.Assert.assertEquals(1, org.traccar.helper.BitUtil.from(1, 0));
        // StatementAdderOnAssert create literal from method
        int int_vc_1262 = 0;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_1262, 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_1262, 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_1262, 0);
        // StatementAdderOnAssert create random local variable
        long vc_2894 = -4037896843945700936L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2894, -4037896843945700936L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2894, -4037896843945700936L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2894, -4037896843945700936L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_2892 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2892);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2892);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2892);
        // AssertGenerator replace invocation
        long o_testFrom_cf28313__9 = // StatementAdderMethod cloned existing statement
vc_2892.from(vc_2894, int_vc_1262);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testFrom_cf28313__9, -4037896843945700936L);
        // StatementAdderOnAssert create random local variable
        int vc_2974 = 1083291199;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2974, 1083291199);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2974, 1083291199);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_2971 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2971);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2971);
        // AssertGenerator replace invocation
        long o_testFrom_cf28313_cf29006__23 = // StatementAdderMethod cloned existing statement
vc_2971.to(vc_2894, vc_2974);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testFrom_cf28313_cf29006__23, 5185475192909074872L);
        // StatementAdderOnAssert create random local variable
        int vc_4095 = -1125957342;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4095, -1125957342);
        // StatementAdderOnAssert create literal from method
        long long_vc_1788 = -4037896843945700936L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_1788, -4037896843945700936L);
        // AssertGenerator replace invocation
        long o_testFrom_cf28313_cf29006_cf40240__41 = // StatementAdderMethod cloned existing statement
vc_2971.from(long_vc_1788, vc_4095);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testFrom_cf28313_cf29006_cf40240__41, -235036531L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.from(1, 1));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testTo */
    @org.junit.Test(timeout = 1000)
    public void testTo_cf40338() {
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.to(2, 2));
        // StatementAdderOnAssert create random local variable
        int vc_4120 = -346707480;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4120, -346707480);
        // StatementAdderOnAssert create random local variable
        long vc_4119 = -6839883687918437116L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4119, -6839883687918437116L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_4117 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4117);
        // AssertGenerator replace invocation
        long o_testTo_cf40338__9 = // StatementAdderMethod cloned existing statement
vc_4117.from(vc_4119, vc_4120);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testTo_cf40338__9, -6220838L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.to(2, 1));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testTo */
    @org.junit.Test(timeout = 1000)
    public void testTo_cf40341() {
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.to(2, 2));
        // StatementAdderOnAssert create literal from method
        int int_vc_1798 = 1;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_1798, 1);
        // StatementAdderOnAssert create random local variable
        long vc_4123 = 245110206892171160L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4123, 245110206892171160L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_4121 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4121);
        // AssertGenerator replace invocation
        long o_testTo_cf40341__9 = // StatementAdderMethod cloned existing statement
vc_4121.to(vc_4123, int_vc_1798);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testTo_cf40341__9, 0L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.to(2, 1));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testTo */
    @org.junit.Test(timeout = 1000)
    public void testTo_cf40342() {
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.to(2, 2));
        // StatementAdderOnAssert create random local variable
        int vc_4124 = 1403781326;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4124, 1403781326);
        // StatementAdderOnAssert create random local variable
        long vc_4123 = 245110206892171160L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4123, 245110206892171160L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_4121 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4121);
        // AssertGenerator replace invocation
        long o_testTo_cf40342__9 = // StatementAdderMethod cloned existing statement
vc_4121.to(vc_4123, vc_4124);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testTo_cf40342__9, 8088L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.to(2, 1));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testTo */
    @org.junit.Test(timeout = 1000)
    public void testTo_cf40318_cf40753() {
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.to(2, 2));
        // StatementAdderOnAssert create random local variable
        int vc_4103 = -1296063715;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4103, -1296063715);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4103, -1296063715);
        // StatementAdderOnAssert create random local variable
        long vc_4102 = -249068779597350628L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4102, -249068779597350628L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4102, -249068779597350628L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_4100 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4100);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4100);
        // AssertGenerator replace invocation
        boolean o_testTo_cf40318__9 = // StatementAdderMethod cloned existing statement
vc_4100.check(vc_4102, vc_4103);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testTo_cf40318__9);
        // StatementAdderOnAssert create random local variable
        int vc_4170 = 1980087374;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4170, 1980087374);
        // StatementAdderOnAssert create random local variable
        long vc_4169 = -7360342839708123923L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4169, -7360342839708123923L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_4167 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4167);
        // AssertGenerator replace invocation
        long o_testTo_cf40318_cf40753__25 = // StatementAdderMethod cloned existing statement
vc_4167.from(vc_4169, vc_4170);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testTo_cf40318_cf40753__25, -449239675275155L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.to(2, 1));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testTo */
    @org.junit.Test(timeout = 1000)
    public void testTo_cf40338_cf41283() {
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.to(2, 2));
        // StatementAdderOnAssert create random local variable
        int vc_4120 = -346707480;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4120, -346707480);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4120, -346707480);
        // StatementAdderOnAssert create random local variable
        long vc_4119 = -6839883687918437116L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4119, -6839883687918437116L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4119, -6839883687918437116L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_4117 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4117);
        // AssertGenerator replace invocation
        long o_testTo_cf40338__9 = // StatementAdderMethod cloned existing statement
vc_4117.from(vc_4119, vc_4120);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testTo_cf40338__9, -6220838L);
        // StatementAdderOnAssert create literal from method
        long long_vc_1841 = -6839883687918437116L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_1841, -6839883687918437116L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_4221 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4221);
        // AssertGenerator replace invocation
        long o_testTo_cf40338_cf41283__23 = // StatementAdderMethod cloned existing statement
vc_4221.to(long_vc_1841, vc_4120);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testTo_cf40338_cf41283__23, 27592359172L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.to(2, 1));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testTo */
    @org.junit.Test(timeout = 1000)
    public void testTo_cf40337_cf40853_cf51560() {
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.to(2, 2));
        // StatementAdderOnAssert create literal from method
        int int_vc_1797 = 2;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_1797, 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_1797, 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_1797, 2);
        // StatementAdderOnAssert create random local variable
        long vc_4119 = -6839883687918437116L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4119, -6839883687918437116L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4119, -6839883687918437116L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4119, -6839883687918437116L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_4117 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4117);
        // AssertGenerator replace invocation
        long o_testTo_cf40337__9 = // StatementAdderMethod cloned existing statement
vc_4117.from(vc_4119, int_vc_1797);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testTo_cf40337__9, -1709970921979609279L);
        // StatementAdderOnAssert create random local variable
        int vc_4178 = -373947824;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4178, -373947824);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4178, -373947824);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_4175 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4175);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4175);
        // AssertGenerator replace invocation
        boolean o_testTo_cf40337_cf40853__23 = // StatementAdderMethod cloned existing statement
vc_4175.check(vc_4119, vc_4178);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testTo_cf40337_cf40853__23);
        // StatementAdderOnAssert create random local variable
        int vc_5245 = -440635209;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_5245, -440635209);
        // AssertGenerator replace invocation
        long o_testTo_cf40337_cf40853_cf51560__39 = // StatementAdderMethod cloned existing statement
vc_4175.from(vc_4119, vc_5245);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testTo_cf40337_cf40853_cf51560__39, -190L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.to(2, 1));
    }

    /* amplification of org.traccar.helper.BitUtilTest#testTo */
    @org.junit.Test(timeout = 1000)
    public void testTo_cf40337_literalMutation40834_cf42257() {
        org.junit.Assert.assertEquals(2, org.traccar.helper.BitUtil.to(2, 2));
        // StatementAdderOnAssert create literal from method
        int int_vc_1797 = 2;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_1797, 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_1797, 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_1797, 2);
        // StatementAdderOnAssert create random local variable
        long vc_4119 = -6839883687918437116L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4119, -6839883687918437116L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4119, -6839883687918437116L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4119, -6839883687918437116L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_4117 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4117);
        // AssertGenerator replace invocation
        long o_testTo_cf40337__9 = // StatementAdderMethod cloned existing statement
vc_4117.from(vc_4119, int_vc_1797);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testTo_cf40337__9, -1709970921979609279L);
        // StatementAdderOnAssert create random local variable
        int vc_4324 = -1374543760;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_4324, -1374543760);
        // StatementAdderOnAssert create null value
        org.traccar.helper.BitUtil vc_4321 = (org.traccar.helper.BitUtil)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4321);
        // AssertGenerator replace invocation
        long o_testTo_cf40337_literalMutation40834_cf42257__29 = // StatementAdderMethod cloned existing statement
vc_4321.to(vc_4119, vc_4324);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testTo_cf40337_literalMutation40834_cf42257__29, 239721127214340L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.BitUtil.to(2, 1));
    }
}

