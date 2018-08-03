package com.squareup.javapoet;


import org.junit.Assert;
import org.junit.Test;


public final class AmplNameAllocatorTest {
    @Test(timeout = 10000)
    public void usage_mg39070() throws Exception {
        Object __DSPOT_tag_5595 = new Object();
        String __DSPOT_suggestion_5594 = "AtbrOT9m#C%Yg(^6]>u0";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg39070__6 = nameAllocator.newName(__DSPOT_suggestion_5594, __DSPOT_tag_5595);
        Assert.assertEquals("AtbrOT9m_C_Yg__6__u0", o_usage_mg39070__6);
    }

    @Test(timeout = 10000)
    public void usage_mg39070null39356_failAssert544() throws Exception {
        try {
            Object __DSPOT_tag_5595 = new Object();
            String __DSPOT_suggestion_5594 = "AtbrOT9m#C%Yg(^6]>u0";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_mg39070__6 = nameAllocator.newName(__DSPOT_suggestion_5594, null);
            org.junit.Assert.fail("usage_mg39070null39356 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void usage_mg39070null39357() throws Exception {
        Object __DSPOT_tag_5595 = new Object();
        String __DSPOT_suggestion_5594 = "AtbrOT9m#C%Yg(^6]>u0";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg39070__6 = nameAllocator.newName(__DSPOT_suggestion_5594, __DSPOT_tag_5595);
        Assert.assertEquals("AtbrOT9m_C_Yg__6__u0", o_usage_mg39070__6);
    }

    @Test(timeout = 10000)
    public void usage_mg39070null39347_failAssert541() throws Exception {
        try {
            Object __DSPOT_tag_5595 = new Object();
            String __DSPOT_suggestion_5594 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_mg39070__6 = nameAllocator.newName(__DSPOT_suggestion_5594, __DSPOT_tag_5595);
            org.junit.Assert.fail("usage_mg39070null39347 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void usage_mg39069null39350null40023() throws Exception {
        String __DSPOT_suggestion_5593 = "ZSbw%yQvB0y=mJ&<r#|+";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg39069__4 = nameAllocator.newName(__DSPOT_suggestion_5593);
        Assert.assertEquals("ZSbw_yQvB0y_mJ__r___", o_usage_mg39069__4);
    }

    @Test(timeout = 10000)
    public void usage_mg39070_mg39339null39955_failAssert577() throws Exception {
        try {
            String __DSPOT_suggestion_5599 = "B&v^eZ:f&XAe@?+)F](Z";
            Object __DSPOT_tag_5595 = null;
            String __DSPOT_suggestion_5594 = "AtbrOT9m#C%Yg(^6]>u0";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_mg39070__6 = nameAllocator.newName(__DSPOT_suggestion_5594, __DSPOT_tag_5595);
            String o_usage_mg39070_mg39339__10 = nameAllocator.newName(__DSPOT_suggestion_5599);
            org.junit.Assert.fail("usage_mg39070_mg39339null39955 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void usage_mg39070_mg39339null39960_failAssert569() throws Exception {
        try {
            String __DSPOT_suggestion_5599 = "B&v^eZ:f&XAe@?+)F](Z";
            Object __DSPOT_tag_5595 = new Object();
            String __DSPOT_suggestion_5594 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_mg39070__6 = nameAllocator.newName(__DSPOT_suggestion_5594, __DSPOT_tag_5595);
            String o_usage_mg39070_mg39339__10 = nameAllocator.newName(__DSPOT_suggestion_5599);
            org.junit.Assert.fail("usage_mg39070_mg39339null39960 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24451() throws Exception {
        Object __DSPOT_tag_4352 = new Object();
        String __DSPOT_suggestion_4351 = "GfD4S*Bd2xP??tp77F#B";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg24451__6 = nameAllocator.newName(__DSPOT_suggestion_4351, __DSPOT_tag_4352);
        Assert.assertEquals("GfD4S_Bd2xP__tp77F_B", o_nameCollision_mg24451__6);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24451null24660_failAssert363() throws Exception {
        try {
            Object __DSPOT_tag_4352 = new Object();
            String __DSPOT_suggestion_4351 = "GfD4S*Bd2xP??tp77F#B";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_mg24451__6 = nameAllocator.newName(__DSPOT_suggestion_4351, null);
            org.junit.Assert.fail("nameCollision_mg24451null24660 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24451null24661() throws Exception {
        Object __DSPOT_tag_4352 = new Object();
        String __DSPOT_suggestion_4351 = "GfD4S*Bd2xP??tp77F#B";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg24451__6 = nameAllocator.newName(__DSPOT_suggestion_4351, __DSPOT_tag_4352);
        Assert.assertEquals("GfD4S_Bd2xP__tp77F_B", o_nameCollision_mg24451__6);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24451null24659_failAssert362() throws Exception {
        try {
            Object __DSPOT_tag_4352 = new Object();
            String __DSPOT_suggestion_4351 = "GfD4S*Bd2xP??tp77F#B";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_mg24451__6 = nameAllocator.newName(null, __DSPOT_tag_4352);
            org.junit.Assert.fail("nameCollision_mg24451null24659 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24451_mg24643null25440_failAssert385() throws Exception {
        try {
            String __DSPOT_suggestion_4356 = "O$Kso7EO[G:0m9u]WJnl";
            Object __DSPOT_tag_4352 = new Object();
            String __DSPOT_suggestion_4351 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_mg24451__6 = nameAllocator.newName(__DSPOT_suggestion_4351, __DSPOT_tag_4352);
            String o_nameCollision_mg24451_mg24643__10 = nameAllocator.newName(__DSPOT_suggestion_4356);
            org.junit.Assert.fail("nameCollision_mg24451_mg24643null25440 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24450_mg24644null25476() throws Exception {
        Object __DSPOT_tag_4358 = new Object();
        String __DSPOT_suggestion_4357 = "F[T).Ydl,UT$_ wUoha ";
        String __DSPOT_suggestion_4350 = "A<bsVM6G[qNEYx@uX54,";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg24450__4 = nameAllocator.newName(__DSPOT_suggestion_4350);
        Assert.assertEquals("A_bsVM6G_qNEYx_uX54_", o_nameCollision_mg24450__4);
        String o_nameCollision_mg24450_mg24644__10 = nameAllocator.newName(__DSPOT_suggestion_4357, __DSPOT_tag_4358);
        Assert.assertEquals("F_T__Ydl_UT$__wUoha_", o_nameCollision_mg24450_mg24644__10);
        Assert.assertEquals("A_bsVM6G_qNEYx_uX54_", o_nameCollision_mg24450__4);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24451_mg24645null25430_failAssert372() throws Exception {
        try {
            Object __DSPOT_tag_4360 = new Object();
            String __DSPOT_suggestion_4359 = "&2:_>HZ]U]wPx}X62>WB";
            Object __DSPOT_tag_4352 = null;
            String __DSPOT_suggestion_4351 = "GfD4S*Bd2xP??tp77F#B";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_mg24451__6 = nameAllocator.newName(__DSPOT_suggestion_4351, __DSPOT_tag_4352);
            String o_nameCollision_mg24451_mg24645__12 = nameAllocator.newName(__DSPOT_suggestion_4359, __DSPOT_tag_4360);
            org.junit.Assert.fail("nameCollision_mg24451_mg24645null25430 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg26144() throws Exception {
        Object __DSPOT_tag_4524 = new Object();
        String __DSPOT_suggestion_4523 = "Zfwl%Gzj7?Rq-0FfIqkS";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_mg26144__6 = nameAllocator.newName(__DSPOT_suggestion_4523, __DSPOT_tag_4524);
        Assert.assertEquals("Zfwl_Gzj7_Rq_0FfIqkS", o_nameCollisionWithTag_mg26144__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg26144null26537_failAssert417() throws Exception {
        try {
            Object __DSPOT_tag_4524 = null;
            String __DSPOT_suggestion_4523 = "Zfwl%Gzj7?Rq-0FfIqkS";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_mg26144__6 = nameAllocator.newName(__DSPOT_suggestion_4523, __DSPOT_tag_4524);
            org.junit.Assert.fail("nameCollisionWithTag_mg26144null26537 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg26144null26539_failAssert418() throws Exception {
        try {
            Object __DSPOT_tag_4524 = new Object();
            String __DSPOT_suggestion_4523 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_mg26144__6 = nameAllocator.newName(__DSPOT_suggestion_4523, __DSPOT_tag_4524);
            org.junit.Assert.fail("nameCollisionWithTag_mg26144null26539 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg26144_add26524() throws Exception {
        Object __DSPOT_tag_4524 = new Object();
        String __DSPOT_suggestion_4523 = "Zfwl%Gzj7?Rq-0FfIqkS";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_mg26144__6 = nameAllocator.newName(__DSPOT_suggestion_4523, __DSPOT_tag_4524);
        Assert.assertEquals("Zfwl_Gzj7_Rq_0FfIqkS", o_nameCollisionWithTag_mg26144__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg26143_mg26532null27288() throws Exception {
        Object __DSPOT_tag_4530 = new Object();
        String __DSPOT_suggestion_4529 = "LT6Hm4CVh Svw_s&j*>o";
        String __DSPOT_suggestion_4522 = "ww*[I|&jqL])X5Ii#po]";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_mg26143__4 = nameAllocator.newName(__DSPOT_suggestion_4522);
        Assert.assertEquals("ww__I__jqL__X5Ii_po_", o_nameCollisionWithTag_mg26143__4);
        String o_nameCollisionWithTag_mg26143_mg26532__10 = nameAllocator.newName(__DSPOT_suggestion_4529, __DSPOT_tag_4530);
        Assert.assertEquals("LT6Hm4CVh_Svw_s_j__o", o_nameCollisionWithTag_mg26143_mg26532__10);
        Assert.assertEquals("ww__I__jqL__X5Ii_po_", o_nameCollisionWithTag_mg26143__4);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg26144_mg26531null27315_failAssert438() throws Exception {
        try {
            String __DSPOT_suggestion_4528 = "G*p(W0O{BSc0S,_4aoCv";
            Object __DSPOT_tag_4524 = new Object();
            String __DSPOT_suggestion_4523 = "Zfwl%Gzj7?Rq-0FfIqkS";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_mg26144__6 = nameAllocator.newName(__DSPOT_suggestion_4523, null);
            String o_nameCollisionWithTag_mg26144_mg26531__10 = nameAllocator.newName(__DSPOT_suggestion_4528);
            org.junit.Assert.fail("nameCollisionWithTag_mg26144_mg26531null27315 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg26144_mg26531null27311_failAssert448() throws Exception {
        try {
            String __DSPOT_suggestion_4528 = "G*p(W0O{BSc0S,_4aoCv";
            Object __DSPOT_tag_4524 = new Object();
            String __DSPOT_suggestion_4523 = "Zfwl%Gzj7?Rq-0FfIqkS";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_mg26144__6 = nameAllocator.newName(null, __DSPOT_tag_4524);
            String o_nameCollisionWithTag_mg26144_mg26531__10 = nameAllocator.newName(__DSPOT_suggestion_4528);
            org.junit.Assert.fail("nameCollisionWithTag_mg26144_mg26531null27311 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3142() throws Exception {
        Object __DSPOT_tag_347 = new Object();
        String __DSPOT_suggestion_346 = "Ph?<b+y!U3^[z=3TG?Pg";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg3142__6 = nameAllocator.newName(__DSPOT_suggestion_346, __DSPOT_tag_347);
        Assert.assertEquals("Ph__b_y_U3__z_3TG_Pg", o_characterMappingSubstitute_mg3142__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3142null3258_failAssert109() throws Exception {
        try {
            Object __DSPOT_tag_347 = new Object();
            String __DSPOT_suggestion_346 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_mg3142__6 = nameAllocator.newName(__DSPOT_suggestion_346, __DSPOT_tag_347);
            org.junit.Assert.fail("characterMappingSubstitute_mg3142null3258 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3141litString3236() throws Exception {
        String __DSPOT_suggestion_345 = "&r7Mez&0A)5f-JJZB2+%";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg3141__4 = nameAllocator.newName(__DSPOT_suggestion_345);
        Assert.assertEquals("_r7Mez_0A_5f_JJZB2__", o_characterMappingSubstitute_mg3141__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3142null3256_failAssert108() throws Exception {
        try {
            Object __DSPOT_tag_347 = null;
            String __DSPOT_suggestion_346 = "Ph?<b+y!U3^[z=3TG?Pg";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_mg3142__6 = nameAllocator.newName(__DSPOT_suggestion_346, __DSPOT_tag_347);
            org.junit.Assert.fail("characterMappingSubstitute_mg3142null3256 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3142_mg3251null4086_failAssert133() throws Exception {
        try {
            String __DSPOT_suggestion_351 = "GeAZBXWZ9Li]$FA^|2]&";
            Object __DSPOT_tag_347 = null;
            String __DSPOT_suggestion_346 = "Ph?<b+y!U3^[z=3TG?Pg";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_mg3142__6 = nameAllocator.newName(__DSPOT_suggestion_346, __DSPOT_tag_347);
            String o_characterMappingSubstitute_mg3142_mg3251__10 = nameAllocator.newName(__DSPOT_suggestion_351);
            org.junit.Assert.fail("characterMappingSubstitute_mg3142_mg3251null4086 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3141litString3230null3877() throws Exception {
        String __DSPOT_suggestion_345 = "T%vt_F}H[n6O(.TI(`VZ";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg3141__4 = nameAllocator.newName(__DSPOT_suggestion_345);
        Assert.assertEquals("T_vt_F_H_n6O__TI__VZ", o_characterMappingSubstitute_mg3141__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3142_mg3251null4093_failAssert129() throws Exception {
        try {
            String __DSPOT_suggestion_351 = "GeAZBXWZ9Li]$FA^|2]&";
            Object __DSPOT_tag_347 = new Object();
            String __DSPOT_suggestion_346 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_mg3142__6 = nameAllocator.newName(__DSPOT_suggestion_346, __DSPOT_tag_347);
            String o_characterMappingSubstitute_mg3142_mg3251__10 = nameAllocator.newName(__DSPOT_suggestion_351);
            org.junit.Assert.fail("characterMappingSubstitute_mg3142_mg3251null4093 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4702() throws Exception {
        Object __DSPOT_tag_519 = new Object();
        String __DSPOT_suggestion_518 = "WXDle}z;wuWR@,|:67iu";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg4702__6 = nameAllocator.newName(__DSPOT_suggestion_518, __DSPOT_tag_519);
        Assert.assertEquals("WXDle_z_wuWR____67iu", o_characterMappingSurrogate_mg4702__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4702null4827_failAssert165() throws Exception {
        try {
            Object __DSPOT_tag_519 = new Object();
            String __DSPOT_suggestion_518 = "WXDle}z;wuWR@,|:67iu";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_mg4702__6 = nameAllocator.newName(null, __DSPOT_tag_519);
            org.junit.Assert.fail("characterMappingSurrogate_mg4702null4827 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4702null4817_failAssert162() throws Exception {
        try {
            Object __DSPOT_tag_519 = null;
            String __DSPOT_suggestion_518 = "WXDle}z;wuWR@,|:67iu";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_mg4702__6 = nameAllocator.newName(__DSPOT_suggestion_518, __DSPOT_tag_519);
            org.junit.Assert.fail("characterMappingSurrogate_mg4702null4817 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4702null4830() throws Exception {
        Object __DSPOT_tag_519 = new Object();
        String __DSPOT_suggestion_518 = "WXDle}z;wuWR@,|:67iu";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg4702__6 = nameAllocator.newName(__DSPOT_suggestion_518, __DSPOT_tag_519);
        Assert.assertEquals("WXDle_z_wuWR____67iu", o_characterMappingSurrogate_mg4702__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4701litString4800null5538_failAssert200() throws Exception {
        try {
            String __DSPOT_suggestion_517 = "\n";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_mg4701__4 = nameAllocator.newName(null);
            org.junit.Assert.fail("characterMappingSurrogate_mg4701litString4800null5538 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4701_mg4812null5691() throws Exception {
        Object __DSPOT_tag_525 = new Object();
        String __DSPOT_suggestion_524 = ",kgWS%rG!{ 75Uj=:LlO";
        String __DSPOT_suggestion_517 = "qvHX/l[8=b-LYDSCxx!&";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg4701__4 = nameAllocator.newName(__DSPOT_suggestion_517);
        Assert.assertEquals("qvHX_l_8_b_LYDSCxx__", o_characterMappingSurrogate_mg4701__4);
        String o_characterMappingSurrogate_mg4701_mg4812__10 = nameAllocator.newName(__DSPOT_suggestion_524, __DSPOT_tag_525);
        Assert.assertEquals("_kgWS_rG___75Uj__LlO", o_characterMappingSurrogate_mg4701_mg4812__10);
        Assert.assertEquals("qvHX_l_8_b_LYDSCxx__", o_characterMappingSurrogate_mg4701__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4702_mg4813null5621_failAssert175() throws Exception {
        try {
            Object __DSPOT_tag_527 = new Object();
            String __DSPOT_suggestion_526 = "0m({q956x/WBjX1$DqGN";
            Object __DSPOT_tag_519 = null;
            String __DSPOT_suggestion_518 = "WXDle}z;wuWR@,|:67iu";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_mg4702__6 = nameAllocator.newName(__DSPOT_suggestion_518, __DSPOT_tag_519);
            String o_characterMappingSurrogate_mg4702_mg4813__12 = nameAllocator.newName(__DSPOT_suggestion_526, __DSPOT_tag_527);
            org.junit.Assert.fail("characterMappingSurrogate_mg4702_mg4813null5621 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg18() throws Exception {
        Object __DSPOT_tag_3 = new Object();
        String __DSPOT_suggestion_2 = "L[{$QV5:Wz2[|+mr6#-V";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
        Assert.assertEquals("L__$QV5_Wz2___mr6__V", o_characterMappingInvalidStartButValidPart_mg18__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg18null142_failAssert10() throws Exception {
        try {
            Object __DSPOT_tag_3 = new Object();
            String __DSPOT_suggestion_2 = "L[{$QV5:Wz2[|+mr6#-V";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(__DSPOT_suggestion_2, null);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_mg18null142 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg18litString109() throws Exception {
        Object __DSPOT_tag_3 = new Object();
        String __DSPOT_suggestion_2 = "L[{$QV5:Wz2[|+mr6=#-V";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
        Assert.assertEquals("L__$QV5_Wz2___mr6___V", o_characterMappingInvalidStartButValidPart_mg18__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg18null140_failAssert9() throws Exception {
        try {
            Object __DSPOT_tag_3 = new Object();
            String __DSPOT_suggestion_2 = "L[{$QV5:Wz2[|+mr6#-V";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(null, __DSPOT_tag_3);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_mg18null140 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg17_mg128litString553() throws Exception {
        Object __DSPOT_tag_9 = new Object();
        String __DSPOT_suggestion_8 = "R%h1,xavU[1Rvnj|}8wu";
        String __DSPOT_suggestion_1 = ":";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_mg17__4 = nameAllocator.newName(__DSPOT_suggestion_1);
        Assert.assertEquals("_", o_characterMappingInvalidStartButValidPart_mg17__4);
        String o_characterMappingInvalidStartButValidPart_mg17_mg128__10 = nameAllocator.newName(__DSPOT_suggestion_8, __DSPOT_tag_9);
        Assert.assertEquals("R_h1_xavU_1Rvnj__8wu", o_characterMappingInvalidStartButValidPart_mg17_mg128__10);
        Assert.assertEquals("_", o_characterMappingInvalidStartButValidPart_mg17__4);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg18_mg129null870_failAssert21() throws Exception {
        try {
            Object __DSPOT_tag_11 = new Object();
            String __DSPOT_suggestion_10 = "]&8(Dgh`l V!3a(!.#b{";
            Object __DSPOT_tag_3 = null;
            String __DSPOT_suggestion_2 = "L[{$QV5:Wz2[|+mr6#-V";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
            String o_characterMappingInvalidStartButValidPart_mg18_mg129__12 = nameAllocator.newName(__DSPOT_suggestion_10, __DSPOT_tag_11);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_mg18_mg129null870 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg18_mg126null957_failAssert32() throws Exception {
        try {
            String __DSPOT_suggestion_7 = "/6F-&k*201yCi*Odwpau";
            Object __DSPOT_tag_3 = new Object();
            String __DSPOT_suggestion_2 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
            String o_characterMappingInvalidStartButValidPart_mg18_mg126__10 = nameAllocator.newName(__DSPOT_suggestion_7);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_mg18_mg126null957 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1580() throws Exception {
        Object __DSPOT_tag_175 = new Object();
        String __DSPOT_suggestion_174 = "Y{S>>6Om:]zsR!qjgSF<";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_mg1580__6 = nameAllocator.newName(__DSPOT_suggestion_174, __DSPOT_tag_175);
        Assert.assertEquals("Y_S__6Om__zsR_qjgSF_", o_characterMappingInvalidStartIsInvalidPart_mg1580__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1580null1693_failAssert57() throws Exception {
        try {
            Object __DSPOT_tag_175 = null;
            String __DSPOT_suggestion_174 = "Y{S>>6Om:]zsR!qjgSF<";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_mg1580__6 = nameAllocator.newName(__DSPOT_suggestion_174, __DSPOT_tag_175);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_mg1580null1693 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1580_mg1691() throws Exception {
        Object __DSPOT_tag_183 = new Object();
        String __DSPOT_suggestion_182 = "tI4De0drHp,* B!1!0nS";
        Object __DSPOT_tag_175 = new Object();
        String __DSPOT_suggestion_174 = "Y{S>>6Om:]zsR!qjgSF<";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_mg1580__6 = nameAllocator.newName(__DSPOT_suggestion_174, __DSPOT_tag_175);
        Assert.assertEquals("Y_S__6Om__zsR_qjgSF_", o_characterMappingInvalidStartIsInvalidPart_mg1580__6);
        String o_characterMappingInvalidStartIsInvalidPart_mg1580_mg1691__12 = nameAllocator.newName(__DSPOT_suggestion_182, __DSPOT_tag_183);
        Assert.assertEquals("tI4De0drHp___B_1_0nS", o_characterMappingInvalidStartIsInvalidPart_mg1580_mg1691__12);
        Assert.assertEquals("Y_S__6Om__zsR_qjgSF_", o_characterMappingInvalidStartIsInvalidPart_mg1580__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1580null1696_failAssert58() throws Exception {
        try {
            Object __DSPOT_tag_175 = new Object();
            String __DSPOT_suggestion_174 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_mg1580__6 = nameAllocator.newName(__DSPOT_suggestion_174, __DSPOT_tag_175);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_mg1580null1696 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1580_mg1689null2519_failAssert82() throws Exception {
        try {
            String __DSPOT_suggestion_179 = "uza;+kVD6&G)ynZ< gd.";
            Object __DSPOT_tag_175 = new Object();
            String __DSPOT_suggestion_174 = "Y{S>>6Om:]zsR!qjgSF<";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_mg1580__6 = nameAllocator.newName(null, __DSPOT_tag_175);
            String o_characterMappingInvalidStartIsInvalidPart_mg1580_mg1689__10 = nameAllocator.newName(__DSPOT_suggestion_179);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_mg1580_mg1689null2519 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1580_mg1691null2403_failAssert70() throws Exception {
        try {
            Object __DSPOT_tag_183 = new Object();
            String __DSPOT_suggestion_182 = "tI4De0drHp,* B!1!0nS";
            Object __DSPOT_tag_175 = null;
            String __DSPOT_suggestion_174 = "Y{S>>6Om:]zsR!qjgSF<";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_mg1580__6 = nameAllocator.newName(__DSPOT_suggestion_174, __DSPOT_tag_175);
            String o_characterMappingInvalidStartIsInvalidPart_mg1580_mg1691__12 = nameAllocator.newName(__DSPOT_suggestion_182, __DSPOT_tag_183);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_mg1580_mg1691null2403 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1579_mg1690litString1984() throws Exception {
        Object __DSPOT_tag_181 = new Object();
        String __DSPOT_suggestion_180 = "usM]Jt}g`wki77.&<MPZ";
        String __DSPOT_suggestion_173 = "\n";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_mg1579__4 = nameAllocator.newName(__DSPOT_suggestion_173);
        Assert.assertEquals("_", o_characterMappingInvalidStartIsInvalidPart_mg1579__4);
        String o_characterMappingInvalidStartIsInvalidPart_mg1579_mg1690__10 = nameAllocator.newName(__DSPOT_suggestion_180, __DSPOT_tag_181);
        Assert.assertEquals("usM_Jt_g_wki77___MPZ", o_characterMappingInvalidStartIsInvalidPart_mg1579_mg1690__10);
        Assert.assertEquals("_", o_characterMappingInvalidStartIsInvalidPart_mg1579__4);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22837() throws Exception {
        String __DSPOT_suggestion_4178 = "ROs1nZGL?5!hSvY!V7E#";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg22837__4 = nameAllocator.newName(__DSPOT_suggestion_4178);
        Assert.assertEquals("ROs1nZGL_5_hSvY_V7E_", o_javaKeyword_mg22837__4);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22838null22996_failAssert301() throws Exception {
        try {
            Object __DSPOT_tag_4180 = new Object();
            String __DSPOT_suggestion_4179 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_mg22838__6 = nameAllocator.newName(__DSPOT_suggestion_4179, __DSPOT_tag_4180);
            org.junit.Assert.fail("javaKeyword_mg22838null22996 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22838null22994_failAssert300() throws Exception {
        try {
            Object __DSPOT_tag_4180 = null;
            String __DSPOT_suggestion_4179 = "_Sv=+Iim`s{yWC[>e_U#";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_mg22838__6 = nameAllocator.newName(__DSPOT_suggestion_4179, __DSPOT_tag_4180);
            org.junit.Assert.fail("javaKeyword_mg22838null22994 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22838_mg22991() throws Exception {
        Object __DSPOT_tag_4188 = new Object();
        String __DSPOT_suggestion_4187 = "=>GD#fnj5@[F{9]Wg9DE";
        Object __DSPOT_tag_4180 = new Object();
        String __DSPOT_suggestion_4179 = "_Sv=+Iim`s{yWC[>e_U#";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg22838__6 = nameAllocator.newName(__DSPOT_suggestion_4179, __DSPOT_tag_4180);
        Assert.assertEquals("_Sv__Iim_s_yWC__e_U_", o_javaKeyword_mg22838__6);
        String o_javaKeyword_mg22838_mg22991__12 = nameAllocator.newName(__DSPOT_suggestion_4187, __DSPOT_tag_4188);
        Assert.assertEquals("__GD_fnj5__F_9_Wg9DE", o_javaKeyword_mg22838_mg22991__12);
        Assert.assertEquals("_Sv__Iim_s_yWC__e_U_", o_javaKeyword_mg22838__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22838_mg22991null23790_failAssert312() throws Exception {
        try {
            Object __DSPOT_tag_4188 = null;
            String __DSPOT_suggestion_4187 = "=>GD#fnj5@[F{9]Wg9DE";
            Object __DSPOT_tag_4180 = new Object();
            String __DSPOT_suggestion_4179 = "_Sv=+Iim`s{yWC[>e_U#";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_mg22838__6 = nameAllocator.newName(__DSPOT_suggestion_4179, __DSPOT_tag_4180);
            String o_javaKeyword_mg22838_mg22991__12 = nameAllocator.newName(__DSPOT_suggestion_4187, __DSPOT_tag_4188);
            org.junit.Assert.fail("javaKeyword_mg22838_mg22991null23790 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22838_mg22989null23514_failAssert326() throws Exception {
        try {
            String __DSPOT_suggestion_4184 = ")M0.O]Wm0hk`m1Y[w]F5";
            Object __DSPOT_tag_4180 = new Object();
            String __DSPOT_suggestion_4179 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_mg22838__6 = nameAllocator.newName(__DSPOT_suggestion_4179, __DSPOT_tag_4180);
            String o_javaKeyword_mg22838_mg22989__10 = nameAllocator.newName(__DSPOT_suggestion_4184);
            org.junit.Assert.fail("javaKeyword_mg22838_mg22989null23514 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22838_mg22991litString23349() throws Exception {
        Object __DSPOT_tag_4188 = new Object();
        String __DSPOT_suggestion_4187 = "=>GD#fnj5@[FP{9]Wg9DE";
        Object __DSPOT_tag_4180 = new Object();
        String __DSPOT_suggestion_4179 = "_Sv=+Iim`s{yWC[>e_U#";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg22838__6 = nameAllocator.newName(__DSPOT_suggestion_4179, __DSPOT_tag_4180);
        Assert.assertEquals("_Sv__Iim_s_yWC__e_U_", o_javaKeyword_mg22838__6);
        String o_javaKeyword_mg22838_mg22991__12 = nameAllocator.newName(__DSPOT_suggestion_4187, __DSPOT_tag_4188);
        Assert.assertEquals("__GD_fnj5__FP_9_Wg9DE", o_javaKeyword_mg22838_mg22991__12);
        Assert.assertEquals("_Sv__Iim_s_yWC__e_U_", o_javaKeyword_mg22838__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString27970() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString27970__3 = nameAllocator.newName("g38", 1);
        Assert.assertEquals("g38", o_tagReuseForbiddenlitString27970__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("g38", o_tagReuseForbiddenlitString27970__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull28001_failAssert471() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(null, 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull28001 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull28002_failAssert472() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", null);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull28002 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg27998litNum29415() throws Exception {
        Object __DSPOT_tag_4695 = new Object();
        String __DSPOT_suggestion_4694 = "Dm:RP,8o{zYfg!$}?HU-";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg27998__6 = nameAllocator.newName("foo", -1840902226);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg27998__6);
        try {
            String o_tagReuseForbidden_mg27998litNum29415__11 = nameAllocator.newName("bar", 1);
            Assert.assertEquals("bar", o_tagReuseForbidden_mg27998litNum29415__11);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg27998__11 = nameAllocator.newName(__DSPOT_suggestion_4694, __DSPOT_tag_4695);
        Assert.assertEquals("Dm_RP_8o_zYfg_$__HU_", o_tagReuseForbidden_mg27998__11);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg27998__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg27998null29620_failAssert478() throws Exception {
        try {
            Object __DSPOT_tag_4695 = new Object();
            String __DSPOT_suggestion_4694 = "Dm:RP,8o{zYfg!$}?HU-";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_mg27998__6 = nameAllocator.newName("foo", null);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_mg27998__11 = nameAllocator.newName(__DSPOT_suggestion_4694, __DSPOT_tag_4695);
            org.junit.Assert.fail("tagReuseForbidden_mg27998null29620 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg27998null29617_failAssert480() throws Exception {
        try {
            Object __DSPOT_tag_4695 = new Object();
            String __DSPOT_suggestion_4694 = "Dm:RP,8o{zYfg!$}?HU-";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_mg27998__6 = nameAllocator.newName(null, 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_mg27998__11 = nameAllocator.newName(__DSPOT_suggestion_4694, __DSPOT_tag_4695);
            org.junit.Assert.fail("tagReuseForbidden_mg27998null29617 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove27994null28712litString30271() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            String o_tagReuseForbidden_remove27994__5 = nameAllocator.newName("M1[", 1);
            Assert.assertEquals("M1_", o_tagReuseForbidden_remove27994__5);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove27994litString28144null33551_failAssert527() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            try {
                String o_tagReuseForbidden_remove27994__5 = nameAllocator.newName("", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbidden_remove27994litString28144null33551 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove27994null28714null37351_failAssert523() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            try {
                String o_tagReuseForbidden_remove27994__5 = nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbidden_remove27994null28714null37351 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40779() throws Exception {
        Object __DSPOT_tag_5767 = new Object();
        String __DSPOT_suggestion_5766 = ">!PfKNb?I)wo|izk 7#)";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg40779__10 = nameAllocator.newName(__DSPOT_suggestion_5766, __DSPOT_tag_5767);
        Assert.assertEquals("__PfKNb_I_wo_izk_7__", o_useBeforeAllocateForbidden_mg40779__10);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40779null40915() throws Exception {
        Object __DSPOT_tag_5767 = new Object();
        String __DSPOT_suggestion_5766 = ">!PfKNb?I)wo|izk 7#)";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg40779__10 = nameAllocator.newName(__DSPOT_suggestion_5766, __DSPOT_tag_5767);
        Assert.assertEquals("__PfKNb_I_wo_izk_7__", o_useBeforeAllocateForbidden_mg40779__10);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40779null40914_failAssert608() throws Exception {
        try {
            Object __DSPOT_tag_5767 = new Object();
            String __DSPOT_suggestion_5766 = ">!PfKNb?I)wo|izk 7#)";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            String o_useBeforeAllocateForbidden_mg40779__10 = nameAllocator.newName(__DSPOT_suggestion_5766, null);
            org.junit.Assert.fail("useBeforeAllocateForbidden_mg40779null40914 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40779null40913_failAssert607() throws Exception {
        try {
            Object __DSPOT_tag_5767 = new Object();
            String __DSPOT_suggestion_5766 = ">!PfKNb?I)wo|izk 7#)";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            String o_useBeforeAllocateForbidden_mg40779__10 = nameAllocator.newName(null, __DSPOT_tag_5767);
            org.junit.Assert.fail("useBeforeAllocateForbidden_mg40779null40913 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40778_mg40894null42220_failAssert625() throws Exception {
        try {
            Object __DSPOT_tag_5776 = new Object();
            String __DSPOT_suggestion_5775 = "mIImNlK./Q,&@`9x2;m}";
            String __DSPOT_suggestion_5765 = "-Sx^ 2I]$n7:0,uD>t]r";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            String o_useBeforeAllocateForbidden_mg40778__8 = nameAllocator.newName(__DSPOT_suggestion_5765);
            String o_useBeforeAllocateForbidden_mg40778_mg40894__14 = nameAllocator.newName(__DSPOT_suggestion_5775, null);
            org.junit.Assert.fail("useBeforeAllocateForbidden_mg40778_mg40894null42220 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40778_mg40894null42156_failAssert632() throws Exception {
        try {
            Object __DSPOT_tag_5776 = new Object();
            String __DSPOT_suggestion_5775 = "mIImNlK./Q,&@`9x2;m}";
            String __DSPOT_suggestion_5765 = "-Sx^ 2I]$n7:0,uD>t]r";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            String o_useBeforeAllocateForbidden_mg40778__8 = nameAllocator.newName(null);
            String o_useBeforeAllocateForbidden_mg40778_mg40894__14 = nameAllocator.newName(__DSPOT_suggestion_5775, __DSPOT_tag_5776);
            org.junit.Assert.fail("useBeforeAllocateForbidden_mg40778_mg40894null42156 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40778_mg40894litString41376() throws Exception {
        Object __DSPOT_tag_5776 = new Object();
        String __DSPOT_suggestion_5775 = "\n";
        String __DSPOT_suggestion_5765 = "-Sx^ 2I]$n7:0,uD>t]r";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg40778__8 = nameAllocator.newName(__DSPOT_suggestion_5765);
        Assert.assertEquals("_Sx__2I_$n7_0_uD_t_r", o_useBeforeAllocateForbidden_mg40778__8);
        String o_useBeforeAllocateForbidden_mg40778_mg40894__14 = nameAllocator.newName(__DSPOT_suggestion_5775, __DSPOT_tag_5776);
        Assert.assertEquals("_", o_useBeforeAllocateForbidden_mg40778_mg40894__14);
        Assert.assertEquals("_Sx__2I_$n7_0_uD_t_r", o_useBeforeAllocateForbidden_mg40778__8);
    }

    @Test(timeout = 10000)
    public void cloneUsagenull6339_failAssert215() throws Exception {
        try {
            NameAllocator outterAllocator = new NameAllocator();
            outterAllocator.newName(null, 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            org.junit.Assert.fail("cloneUsagenull6339 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString6250() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString6250__3 = outterAllocator.newName("f6oo", 1);
        Assert.assertEquals("f6oo", o_cloneUsagelitString6250__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("f6oo", o_cloneUsagelitString6250__3);
    }

    @Test(timeout = 10000)
    public void cloneUsagenull6340_failAssert216() throws Exception {
        try {
            NameAllocator outterAllocator = new NameAllocator();
            outterAllocator.newName("foo", null);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            org.junit.Assert.fail("cloneUsagenull6340 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg6328null7938() throws Exception {
        Object __DSPOT_tag_691 = new Object();
        String __DSPOT_suggestion_690 = "y20Fs4d]2y]!j|ETYlfk";
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_mg6328__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_mg6328__6);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_mg6328__11 = outterAllocator.newName(__DSPOT_suggestion_690, __DSPOT_tag_691);
        Assert.assertEquals("y20Fs4d_2y__j_ETYlfk", o_cloneUsage_mg6328__11);
        Assert.assertEquals("foo", o_cloneUsage_mg6328__6);
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg6336null8272_failAssert238() throws Exception {
        try {
            Object __DSPOT_tag_699 = new Object();
            String __DSPOT_suggestion_698 = "r(mr3#$3FsBk+;.A%PzQ";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_mg6336__6 = outterAllocator.newName("foo", null);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_mg6336__11 = innerAllocator2.newName(__DSPOT_suggestion_698, __DSPOT_tag_699);
            org.junit.Assert.fail("cloneUsage_mg6336null8272 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg6336null8265_failAssert240() throws Exception {
        try {
            Object __DSPOT_tag_699 = new Object();
            String __DSPOT_suggestion_698 = "r(mr3#$3FsBk+;.A%PzQ";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_mg6336__6 = outterAllocator.newName(null, 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_mg6336__11 = innerAllocator2.newName(__DSPOT_suggestion_698, __DSPOT_tag_699);
            org.junit.Assert.fail("cloneUsage_mg6336null8265 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg6328null7978null15328_failAssert287() throws Exception {
        try {
            Object __DSPOT_tag_691 = new Object();
            String __DSPOT_suggestion_690 = "y20Fs4d]2y]!j|ETYlfk";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_mg6328__6 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = null;
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_mg6328__11 = outterAllocator.newName(null, __DSPOT_tag_691);
            org.junit.Assert.fail("cloneUsage_mg6328null7978null15328 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg6328null8018null19304_failAssert288() throws Exception {
        try {
            Object __DSPOT_tag_691 = null;
            String __DSPOT_suggestion_690 = "y20Fs4d]2y]!j|ETYlfk";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_mg6328__6 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = null;
            String o_cloneUsage_mg6328__11 = outterAllocator.newName(__DSPOT_suggestion_690, __DSPOT_tag_691);
            org.junit.Assert.fail("cloneUsage_mg6328null8018null19304 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg6328_mg7833_mg21186() throws Exception {
        Object __DSPOT_tag_3962 = new Object();
        String __DSPOT_suggestion_3961 = "v;Q/}]N6t)v*3Z q$6u9";
        Object __DSPOT_tag_1019 = new Object();
        String __DSPOT_suggestion_1017 = "N1fYk`-7OnlC$yi[c[$p";
        Object __DSPOT_tag_691 = new Object();
        String __DSPOT_suggestion_690 = "y20Fs4d]2y]!j|ETYlfk";
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_mg6328__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_mg6328__6);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_mg6328__11 = outterAllocator.newName(__DSPOT_suggestion_690, __DSPOT_tag_691);
        Assert.assertEquals("y20Fs4d_2y__j_ETYlfk", o_cloneUsage_mg6328__11);
        String o_cloneUsage_mg6328_mg7833__19 = innerAllocator2.newName(__DSPOT_suggestion_1017, __DSPOT_tag_1019);
        Assert.assertEquals("N1fYk__7OnlC$yi_c_$p", o_cloneUsage_mg6328_mg7833__19);
        String o_cloneUsage_mg6328_mg7833_mg21186__25 = innerAllocator2.newName(__DSPOT_suggestion_3961, __DSPOT_tag_3962);
        Assert.assertEquals("v_Q___N6t_v_3Z_q$6u9", o_cloneUsage_mg6328_mg7833_mg21186__25);
        Assert.assertEquals("foo", o_cloneUsage_mg6328__6);
        Assert.assertEquals("y20Fs4d_2y__j_ETYlfk", o_cloneUsage_mg6328__11);
        Assert.assertEquals("N1fYk__7OnlC$yi_c_$p", o_cloneUsage_mg6328_mg7833__19);
    }
}

