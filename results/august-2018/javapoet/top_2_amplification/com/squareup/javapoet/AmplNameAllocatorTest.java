package com.squareup.javapoet;


import org.junit.Assert;
import org.junit.Test;


public final class AmplNameAllocatorTest {
    @Test(timeout = 10000)
    public void usage_mg30042() throws Exception {
        Object __DSPOT_tag_6687 = new Object();
        String __DSPOT_suggestion_6686 = "y(or|,16mh<6432#sl,E";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg30042__6 = nameAllocator.newName(__DSPOT_suggestion_6686, __DSPOT_tag_6687);
        Assert.assertEquals("y_or__16mh_6432_sl_E", o_usage_mg30042__6);
    }

    @Test(timeout = 10000)
    public void usage_add30036null30293_failAssert779() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_add30036__3 = nameAllocator.newName(null, 2);
            org.junit.Assert.fail("usage_add30036null30293 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void usage_mg30042null30299_failAssert783() throws Exception {
        try {
            Object __DSPOT_tag_6687 = new Object();
            String __DSPOT_suggestion_6686 = "y(or|,16mh<6432#sl,E";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_mg30042__6 = nameAllocator.newName(__DSPOT_suggestion_6686, null);
            org.junit.Assert.fail("usage_mg30042null30299 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void usage_mg30041litString30227() throws Exception {
        String __DSPOT_suggestion_6685 = "DT(7q8&hhwHD3r,D2[T|";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg30041__4 = nameAllocator.newName(__DSPOT_suggestion_6685);
        Assert.assertEquals("DT_7q8_hhwHD3r_D2_T_", o_usage_mg30041__4);
    }

    @Test(timeout = 10000)
    public void usage_mg30042_mg30274_failAssert777null31640_failAssert827() throws Exception {
        try {
            try {
                Object __DSPOT_tag_6696 = new Object();
                Object __DSPOT_tag_6687 = new Object();
                String __DSPOT_suggestion_6686 = null;
                NameAllocator nameAllocator = new NameAllocator();
                String o_usage_mg30042__6 = nameAllocator.newName(__DSPOT_suggestion_6686, __DSPOT_tag_6687);
                nameAllocator.get(__DSPOT_tag_6696);
                org.junit.Assert.fail("usage_mg30042_mg30274 should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("usage_mg30042_mg30274_failAssert777null31640 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void usage_mg30042litString30220null31539_failAssert808() throws Exception {
        try {
            Object __DSPOT_tag_6687 = new Object();
            String __DSPOT_suggestion_6686 = "y(or|,16Imh<6432#sl,E";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_mg30042__6 = nameAllocator.newName(__DSPOT_suggestion_6686, null);
            org.junit.Assert.fail("usage_mg30042litString30220null31539 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void usage_mg30041_mg30277_add31151() throws Exception {
        String __DSPOT_suggestion_6685 = "DO(7q8&hhwHD3r,D2[T|";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg30041_mg30277_add31151__4 = nameAllocator.newName(__DSPOT_suggestion_6685);
        Assert.assertEquals("DO_7q8_hhwHD3r_D2_T_", o_usage_mg30041_mg30277_add31151__4);
        String o_usage_mg30041__4 = nameAllocator.newName(__DSPOT_suggestion_6685);
        Assert.assertEquals("DO_7q8_hhwHD3r_D2_T__", o_usage_mg30041__4);
        nameAllocator.clone();
        Assert.assertEquals("DO_7q8_hhwHD3r_D2_T_", o_usage_mg30041_mg30277_add31151__4);
        Assert.assertEquals("DO_7q8_hhwHD3r_D2_T__", o_usage_mg30041__4);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg18423() throws Exception {
        Object __DSPOT_tag_4967 = new Object();
        String __DSPOT_suggestion_4966 = "I `C)%1^#[3d&Ya@Us[d";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg18423__6 = nameAllocator.newName(__DSPOT_suggestion_4966, __DSPOT_tag_4967);
        Assert.assertEquals("I__C__1___3d_Ya_Us_d", o_nameCollision_mg18423__6);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg18423null18605_failAssert521() throws Exception {
        try {
            Object __DSPOT_tag_4967 = new Object();
            String __DSPOT_suggestion_4966 = "I `C)%1^#[3d&Ya@Us[d";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_mg18423__6 = nameAllocator.newName(__DSPOT_suggestion_4966, null);
            org.junit.Assert.fail("nameCollision_mg18423null18605 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_mg18423_mg18592() throws Exception {
        String __DSPOT_suggestion_4985 = "Vb,:r;9h)/U)b_+xS|s#";
        Object __DSPOT_tag_4967 = new Object();
        String __DSPOT_suggestion_4966 = "I `C)%1^#[3d&Ya@Us[d";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg18423__6 = nameAllocator.newName(__DSPOT_suggestion_4966, __DSPOT_tag_4967);
        Assert.assertEquals("I__C__1___3d_Ya_Us_d", o_nameCollision_mg18423__6);
        String o_nameCollision_mg18423_mg18592__10 = nameAllocator.newName(__DSPOT_suggestion_4985);
        Assert.assertEquals("Vb__r_9h__U_b__xS_s_", o_nameCollision_mg18423_mg18592__10);
        Assert.assertEquals("I__C__1___3d_Ya_Us_d", o_nameCollision_mg18423__6);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg18422null18601_failAssert517() throws Exception {
        try {
            String __DSPOT_suggestion_4965 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_mg18422__4 = nameAllocator.newName(__DSPOT_suggestion_4965);
            org.junit.Assert.fail("nameCollision_mg18422null18601 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_add18419_mg18581null19599_failAssert538() throws Exception {
        try {
            Object __DSPOT_tag_4975 = new Object();
            String __DSPOT_suggestion_4974 = "8G[8#:^u&K+n5QOj/[1:";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_add18419__3 = nameAllocator.newName("foo");
            String o_nameCollision_add18419_mg18581__9 = nameAllocator.newName(__DSPOT_suggestion_4974, null);
            org.junit.Assert.fail("nameCollision_add18419_mg18581null19599 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_add18419_mg18580null19593_failAssert575() throws Exception {
        try {
            String __DSPOT_suggestion_4973 = ".rul;Fs*_N<P[}0H%yV|";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_add18419__3 = nameAllocator.newName(null);
            String o_nameCollision_add18419_mg18580__7 = nameAllocator.newName(__DSPOT_suggestion_4973);
            org.junit.Assert.fail("nameCollision_add18419_mg18580null19593 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_add18419litString18539_mg19274() throws Exception {
        String __DSPOT_suggestion_5013 = "v0@h[Uq9}9!,/P,i/Lki";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_add18419__3 = nameAllocator.newName("fo");
        Assert.assertEquals("fo", o_nameCollision_add18419__3);
        String o_nameCollision_add18419litString18539_mg19274__7 = nameAllocator.newName(__DSPOT_suggestion_5013);
        Assert.assertEquals("v0_h_Uq9_9___P_i_Lki", o_nameCollision_add18419litString18539_mg19274__7);
        Assert.assertEquals("fo", o_nameCollision_add18419__3);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg20291() throws Exception {
        Object __DSPOT_tag_5307 = new Object();
        String __DSPOT_suggestion_5306 = "i^l5^=f.2Yc(WWB3|v>:";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_mg20291__6 = nameAllocator.newName(__DSPOT_suggestion_5306, __DSPOT_tag_5307);
        Assert.assertEquals("i_l5__f_2Yc_WWB3_v__", o_nameCollisionWithTag_mg20291__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg20291null20660_failAssert587() throws Exception {
        try {
            Object __DSPOT_tag_5307 = new Object();
            String __DSPOT_suggestion_5306 = "i^l5^=f.2Yc(WWB3|v>:";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_mg20291__6 = nameAllocator.newName(__DSPOT_suggestion_5306, null);
            org.junit.Assert.fail("nameCollisionWithTag_mg20291null20660 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add20283litString20530() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_add20283__3 = nameAllocator.newName("f7oo", 2);
        Assert.assertEquals("f7oo", o_nameCollisionWithTag_add20283__3);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg20291null20658_failAssert600() throws Exception {
        try {
            Object __DSPOT_tag_5307 = new Object();
            String __DSPOT_suggestion_5306 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_mg20291__6 = nameAllocator.newName(__DSPOT_suggestion_5306, __DSPOT_tag_5307);
            org.junit.Assert.fail("nameCollisionWithTag_mg20291null20658 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add20284_mg20621null22413_failAssert624() throws Exception {
        try {
            Object __DSPOT_tag_5315 = new Object();
            String __DSPOT_suggestion_5314 = "(%i]e2:t5Iv3Ph?F0*{?";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_add20284__3 = nameAllocator.newName(null, 3);
            String o_nameCollisionWithTag_add20284_mg20621__9 = nameAllocator.newName(__DSPOT_suggestion_5314, __DSPOT_tag_5315);
            org.junit.Assert.fail("nameCollisionWithTag_add20284_mg20621null22413 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add20284litString20540null22383_failAssert653() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_add20284__3 = nameAllocator.newName("#hJ", null);
            org.junit.Assert.fail("nameCollisionWithTag_add20284litString20540null22383 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add20283litNum20570litString21297() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_add20283__3 = nameAllocator.newName("f7oo", Integer.MAX_VALUE);
        Assert.assertEquals("f7oo", o_nameCollisionWithTag_add20283__3);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3073() throws Exception {
        String __DSPOT_suggestion_473 = ";wuWR@,|:67iuTq!`+Z:";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg3073__4 = nameAllocator.newName(__DSPOT_suggestion_473);
        Assert.assertEquals("_wuWR____67iuTq___Z_", o_characterMappingSubstitute_mg3073__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_add3070null3186_failAssert175() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_add3070__3 = nameAllocator.newName("a-b", null);
            org.junit.Assert.fail("characterMappingSubstitute_add3070null3186 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_add3070null3185_failAssert176() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_add3070__3 = nameAllocator.newName(null, 1);
            org.junit.Assert.fail("characterMappingSubstitute_add3070null3185 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3073_mg3176() throws Exception {
        Object __DSPOT_tag_483 = new Object();
        String __DSPOT_suggestion_482 = "ULRzdEVQrABzs&sRi_+R";
        String __DSPOT_suggestion_473 = ";wuWR@,|:67iuTq!`+Z:";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg3073__4 = nameAllocator.newName(__DSPOT_suggestion_473);
        Assert.assertEquals("_wuWR____67iuTq___Z_", o_characterMappingSubstitute_mg3073__4);
        String o_characterMappingSubstitute_mg3073_mg3176__10 = nameAllocator.newName(__DSPOT_suggestion_482, __DSPOT_tag_483);
        Assert.assertEquals("ULRzdEVQrABzs_sRi__R", o_characterMappingSubstitute_mg3073_mg3176__10);
        Assert.assertEquals("_wuWR____67iuTq___Z_", o_characterMappingSubstitute_mg3073__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_add3070_mg3172null3997_failAssert225() throws Exception {
        try {
            Object __DSPOT_tag_479 = new Object();
            String __DSPOT_suggestion_478 = "eqoq0eDQ=abN3ud2>^fz";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_add3070__3 = nameAllocator.newName(null, 1);
            String o_characterMappingSubstitute_add3070_mg3172__9 = nameAllocator.newName(__DSPOT_suggestion_478, __DSPOT_tag_479);
            org.junit.Assert.fail("characterMappingSubstitute_add3070_mg3172null3997 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_add3070litNum3161_mg3806() throws Exception {
        String __DSPOT_suggestion_561 = "W/UN!. ^G]sYd+v>2!wB";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_add3070__3 = nameAllocator.newName("a-b", 0);
        Assert.assertEquals("a_b", o_characterMappingSubstitute_add3070__3);
        String o_characterMappingSubstitute_add3070litNum3161_mg3806__7 = nameAllocator.newName(__DSPOT_suggestion_561);
        Assert.assertEquals("W_UN____G_sYd_v_2_wB", o_characterMappingSubstitute_add3070litNum3161_mg3806__7);
        Assert.assertEquals("a_b", o_characterMappingSubstitute_add3070__3);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_add3070_mg3172null3998_failAssert197() throws Exception {
        try {
            Object __DSPOT_tag_479 = new Object();
            String __DSPOT_suggestion_478 = "eqoq0eDQ=abN3ud2>^fz";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_add3070__3 = nameAllocator.newName("a-b", null);
            String o_characterMappingSubstitute_add3070_mg3172__9 = nameAllocator.newName(__DSPOT_suggestion_478, __DSPOT_tag_479);
            org.junit.Assert.fail("characterMappingSubstitute_add3070_mg3172null3998 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4605() throws Exception {
        Object __DSPOT_tag_711 = new Object();
        String __DSPOT_suggestion_710 = "Xf5YY3>+ghqpI0ZbnJ#U";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg4605__6 = nameAllocator.newName(__DSPOT_suggestion_710, __DSPOT_tag_711);
        Assert.assertEquals("Xf5YY3__ghqpI0ZbnJ_U", o_characterMappingSurrogate_mg4605__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4604_mg4704() throws Exception {
        String __DSPOT_suggestion_709 = "Z* ol$g:g6:`C:AY=mEB";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg4604__4 = nameAllocator.newName(__DSPOT_suggestion_709);
        Assert.assertEquals("Z__ol$g_g6__C_AY_mEB", o_characterMappingSurrogate_mg4604__4);
        nameAllocator.clone();
        Assert.assertEquals("Z__ol$g_g6__C_AY_mEB", o_characterMappingSurrogate_mg4604__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4605null4720_failAssert255() throws Exception {
        try {
            Object __DSPOT_tag_711 = new Object();
            String __DSPOT_suggestion_710 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_mg4605__6 = nameAllocator.newName(__DSPOT_suggestion_710, __DSPOT_tag_711);
            org.junit.Assert.fail("characterMappingSurrogate_mg4605null4720 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_add4601null4717_failAssert254() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_add4601__3 = nameAllocator.newName("a\ud83c\udf7ab", null);
            org.junit.Assert.fail("characterMappingSurrogate_add4601null4717 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_add4601litNum4693litString4907() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_add4601__3 = nameAllocator.newName("tag 1 cannot be used for both \'foo\' and \'bar\'", Integer.MAX_VALUE);
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", o_characterMappingSurrogate_add4601__3);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4605litString4690null5493_failAssert306() throws Exception {
        try {
            Object __DSPOT_tag_711 = new Object();
            String __DSPOT_suggestion_710 = ":";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_mg4605__6 = nameAllocator.newName(null, __DSPOT_tag_711);
            org.junit.Assert.fail("characterMappingSurrogate_mg4605litString4690null5493 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4605litString4690null5494_failAssert280() throws Exception {
        try {
            Object __DSPOT_tag_711 = new Object();
            String __DSPOT_suggestion_710 = ":";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_mg4605__6 = nameAllocator.newName(__DSPOT_suggestion_710, null);
            org.junit.Assert.fail("characterMappingSurrogate_mg4605litString4690null5494 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg18() throws Exception {
        Object __DSPOT_tag_3 = new Object();
        String __DSPOT_suggestion_2 = "[|+mr6#-VtX(r!Fs2l>U";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
        Assert.assertEquals("___mr6__VtX_r_Fs2l_U", o_characterMappingInvalidStartButValidPart_mg18__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_add14null130_failAssert6() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_add14__3 = nameAllocator.newName("1ab", null);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_add14null130 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_add14null129_failAssert7() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_add14__3 = nameAllocator.newName(null, 1);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_add14null129 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg17_add110() throws Exception {
        String __DSPOT_suggestion_1 = "(q2 5[gpbL[{$QV5:Wz2";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_mg17_add110__4 = nameAllocator.newName(__DSPOT_suggestion_1);
        Assert.assertEquals("_q2_5_gpbL__$QV5_Wz2", o_characterMappingInvalidStartButValidPart_mg17_add110__4);
        String o_characterMappingInvalidStartButValidPart_mg17__4 = nameAllocator.newName(__DSPOT_suggestion_1);
        Assert.assertEquals("_q2_5_gpbL__$QV5_Wz2_", o_characterMappingInvalidStartButValidPart_mg17__4);
        Assert.assertEquals("_q2_5_gpbL__$QV5_Wz2", o_characterMappingInvalidStartButValidPart_mg17_add110__4);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_add14litNum106null919_failAssert38() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_add14__3 = nameAllocator.newName(null, Integer.MAX_VALUE);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_add14litNum106null919 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg17_mg118_failAssert4_add660() throws Exception {
        try {
            Object __DSPOT_tag_8 = new Object();
            String __DSPOT_suggestion_1 = "(q2 5[gpbL[{$QV5:Wz2";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_mg17__4 = nameAllocator.newName(__DSPOT_suggestion_1);
            Assert.assertEquals("_q2_5_gpbL__$QV5_Wz2", o_characterMappingInvalidStartButValidPart_mg17__4);
            nameAllocator.get(__DSPOT_tag_8);
            nameAllocator.get(__DSPOT_tag_8);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_mg17_mg118 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_add14litString80null903_failAssert29() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_add14__3 = nameAllocator.newName("foo", null);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_add14litString80null903 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1549() throws Exception {
        Object __DSPOT_tag_239 = new Object();
        String __DSPOT_suggestion_238 = "vMFfDqM[KPh3?+w?h+LZ";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_mg1549__6 = nameAllocator.newName(__DSPOT_suggestion_238, __DSPOT_tag_239);
        Assert.assertEquals("vMFfDqM_KPh3__w_h_LZ", o_characterMappingInvalidStartIsInvalidPart_mg1549__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_add1545null1661_failAssert90() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_add1545__3 = nameAllocator.newName("&ab", null);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_add1545null1661 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_add1545litString1615() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_add1545__3 = nameAllocator.newName("in5", 1);
        Assert.assertEquals("in5", o_characterMappingInvalidStartIsInvalidPart_add1545__3);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_add1545null1660_failAssert91() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_add1545__3 = nameAllocator.newName(null, 1);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_add1545null1660 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1549_mg1654null2465_failAssert116() throws Exception {
        try {
            String __DSPOT_suggestion_249 = "&a$)LIMmqw=Ma !VX)*-";
            Object __DSPOT_tag_239 = new Object();
            String __DSPOT_suggestion_238 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_mg1549__6 = nameAllocator.newName(__DSPOT_suggestion_238, __DSPOT_tag_239);
            String o_characterMappingInvalidStartIsInvalidPart_mg1549_mg1654__10 = nameAllocator.newName(__DSPOT_suggestion_249);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_mg1549_mg1654null2465 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1549_mg1653_failAssert82null2514_failAssert148() throws Exception {
        try {
            try {
                Object __DSPOT_tag_248 = new Object();
                Object __DSPOT_tag_239 = new Object();
                String __DSPOT_suggestion_238 = "vMFfDqM[KPh3?+w?h+LZ";
                NameAllocator nameAllocator = new NameAllocator();
                String o_characterMappingInvalidStartIsInvalidPart_mg1549__6 = nameAllocator.newName(__DSPOT_suggestion_238, null);
                nameAllocator.get(__DSPOT_tag_248);
                org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_mg1549_mg1653 should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_mg1549_mg1653_failAssert82null2514 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_add1545litString1615litNum2107() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_add1545__3 = nameAllocator.newName("in5", Integer.MIN_VALUE);
        Assert.assertEquals("in5", o_characterMappingInvalidStartIsInvalidPart_add1545__3);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg16803() throws Exception {
        Object __DSPOT_tag_4727 = new Object();
        String __DSPOT_suggestion_4726 = "{fEj>T&f3-CX`1y(s<%E";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg16803__6 = nameAllocator.newName(__DSPOT_suggestion_4726, __DSPOT_tag_4727);
        Assert.assertEquals("_fEj_T_f3_CX_1y_s__E", o_javaKeyword_mg16803__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_add16798null16947_failAssert442() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_add16798__3 = nameAllocator.newName("public", null);
            org.junit.Assert.fail("javaKeyword_add16798null16947 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_add16798null16946_failAssert441() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_add16798__3 = nameAllocator.newName(null, 1);
            org.junit.Assert.fail("javaKeyword_add16798null16946 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg16802_add16922() throws Exception {
        String __DSPOT_suggestion_4725 = "@iY8ZgJL#OKrd!Bvu&B3";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg16802_add16922__4 = nameAllocator.newName(__DSPOT_suggestion_4725);
        Assert.assertEquals("_iY8ZgJL_OKrd_Bvu_B3", o_javaKeyword_mg16802_add16922__4);
        String o_javaKeyword_mg16802__4 = nameAllocator.newName(__DSPOT_suggestion_4725);
        Assert.assertEquals("_iY8ZgJL_OKrd_Bvu_B3_", o_javaKeyword_mg16802__4);
        Assert.assertEquals("_iY8ZgJL_OKrd_Bvu_B3", o_javaKeyword_mg16802_add16922__4);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg16803_add16921_failAssert437null17831_failAssert502() throws Exception {
        try {
            try {
                Object __DSPOT_tag_4727 = new Object();
                String __DSPOT_suggestion_4726 = "{fEj>T&f3-CX`1y(s<%E";
                NameAllocator nameAllocator = new NameAllocator();
                nameAllocator.newName(null, __DSPOT_tag_4727);
                String o_javaKeyword_mg16803__6 = nameAllocator.newName(__DSPOT_suggestion_4726, __DSPOT_tag_4727);
                org.junit.Assert.fail("javaKeyword_mg16803_add16921 should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("javaKeyword_mg16803_add16921_failAssert437null17831 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_add16798litString16892null17810_failAssert474() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_add16798__3 = nameAllocator.newName("\n", null);
            org.junit.Assert.fail("javaKeyword_add16798litString16892null17810 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg16802_mg16934_mg17625() throws Exception {
        Object __DSPOT_tag_4839 = new Object();
        String __DSPOT_suggestion_4838 = "X$iG$B;wE0ZR.uV(sit3";
        String __DSPOT_suggestion_4725 = "@iY8ZgJL#OKrd!Bvu&B3";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg16802__4 = nameAllocator.newName(__DSPOT_suggestion_4725);
        Assert.assertEquals("_iY8ZgJL_OKrd_Bvu_B3", o_javaKeyword_mg16802__4);
        nameAllocator.clone();
        String o_javaKeyword_mg16802_mg16934_mg17625__11 = nameAllocator.newName(__DSPOT_suggestion_4838, __DSPOT_tag_4839);
        Assert.assertEquals("X$iG$B_wE0ZR_uV_sit3", o_javaKeyword_mg16802_mg16934_mg17625__11);
        Assert.assertEquals("_iY8ZgJL_OKrd_Bvu_B3", o_javaKeyword_mg16802__4);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull23051_failAssert662() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", null);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull23051 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg23049() throws Exception {
        Object __DSPOT_tag_5739 = new Object();
        String __DSPOT_suggestion_5738 = "Cd%>Uh>fHa|!_Fq@1*lI";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg23049__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg23049__6);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg23049__11 = nameAllocator.newName(__DSPOT_suggestion_5738, __DSPOT_tag_5739);
        Assert.assertEquals("Cd__Uh_fHa___Fq_1_lI", o_tagReuseForbidden_mg23049__11);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg23049__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull23050_failAssert664() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(null, 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull23050 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString23022null24114_failAssert710() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString23022__3 = nameAllocator.newName("", null);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString23022null24114 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString23029_mg23979() throws Exception {
        Object __DSPOT_tag_5775 = new Object();
        String __DSPOT_suggestion_5774 = "yXhzO#W39R/-r(N7c#:&";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString23029__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString23029__3);
        try {
            nameAllocator.newName("U+U", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitString23029_mg23979__13 = nameAllocator.newName(__DSPOT_suggestion_5774, __DSPOT_tag_5775);
        Assert.assertEquals("yXhzO_W39R__r_N7c___", o_tagReuseForbiddenlitString23029_mg23979__13);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString23029__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum23035null24143_failAssert718() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitNum23035__3 = nameAllocator.newName(null, Integer.MAX_VALUE);
            try {
                String o_tagReuseForbiddenlitNum23035__6 = nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitNum23035null24143 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString23022litString23183null29142_failAssert750() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitString23022__3 = nameAllocator.newName("", 1);
            try {
                nameAllocator.newName("foo_", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddenlitString23022litString23183null29142 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum23039_mg24043null29203_failAssert765() throws Exception {
        try {
            Object __DSPOT_tag_5839 = new Object();
            String __DSPOT_suggestion_5838 = "O&*71;#)H^R}#jZ#1f]:";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbiddenlitNum23039__3 = nameAllocator.newName("foo", 1);
            try {
                String o_tagReuseForbiddenlitNum23039__6 = nameAllocator.newName("bar", 0);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbiddenlitNum23039_mg24043__15 = nameAllocator.newName(null, __DSPOT_tag_5839);
            org.junit.Assert.fail("tagReuseForbiddenlitNum23039_mg24043null29203 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum23039_mg24042litString25133() throws Exception {
        String __DSPOT_suggestion_5837 = "<;spN?0nK_7R6m7X)T8o";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum23039__3 = nameAllocator.newName(":", 1);
        Assert.assertEquals("_", o_tagReuseForbiddenlitNum23039__3);
        try {
            String o_tagReuseForbiddenlitNum23039__6 = nameAllocator.newName("bar", 0);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum23039__6);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitNum23039_mg24042__13 = nameAllocator.newName(__DSPOT_suggestion_5837);
        Assert.assertEquals("__spN_0nK_7R6m7X_T8o", o_tagReuseForbiddenlitNum23039_mg24042__13);
        Assert.assertEquals("_", o_tagReuseForbiddenlitNum23039__3);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg32215() throws Exception {
        Object __DSPOT_tag_7023 = new Object();
        String __DSPOT_suggestion_7022 = ">aIAjXV:Xbev1kHSGRH3";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg32215__10 = nameAllocator.newName(__DSPOT_suggestion_7022, __DSPOT_tag_7023);
        Assert.assertEquals("_aIAjXV_Xbev1kHSGRH3", o_useBeforeAllocateForbidden_mg32215__10);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg32214null32300_failAssert850() throws Exception {
        try {
            String __DSPOT_suggestion_7021 = "[^XPj@{c36UA@#;/N.d[";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            String o_useBeforeAllocateForbidden_mg32214__8 = nameAllocator.newName(null);
            org.junit.Assert.fail("useBeforeAllocateForbidden_mg32214null32300 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg32215_mg32290() throws Exception {
        Object __DSPOT_tag_7023 = new Object();
        String __DSPOT_suggestion_7022 = ">aIAjXV:Xbev1kHSGRH3";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg32215__10 = nameAllocator.newName(__DSPOT_suggestion_7022, __DSPOT_tag_7023);
        Assert.assertEquals("_aIAjXV_Xbev1kHSGRH3", o_useBeforeAllocateForbidden_mg32215__10);
        nameAllocator.clone();
        Assert.assertEquals("_aIAjXV_Xbev1kHSGRH3", o_useBeforeAllocateForbidden_mg32215__10);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg32215null32304_failAssert849() throws Exception {
        try {
            Object __DSPOT_tag_7023 = new Object();
            String __DSPOT_suggestion_7022 = ">aIAjXV:Xbev1kHSGRH3";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            String o_useBeforeAllocateForbidden_mg32215__10 = nameAllocator.newName(__DSPOT_suggestion_7022, null);
            org.junit.Assert.fail("useBeforeAllocateForbidden_mg32215null32304 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg32214litNum32267null33231_failAssert865() throws Exception {
        try {
            String __DSPOT_suggestion_7021 = "[^XPj@{c36UA@#;/N.d[";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(Integer.MAX_VALUE);
            } catch (IllegalArgumentException expected) {
            }
            String o_useBeforeAllocateForbidden_mg32214__8 = nameAllocator.newName(null);
            org.junit.Assert.fail("useBeforeAllocateForbidden_mg32214litNum32267null33231 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg32215_mg32292null33250_failAssert897() throws Exception {
        try {
            String __DSPOT_suggestion_7029 = "<!d:&&na.!wATc6CZts_";
            Object __DSPOT_tag_7023 = new Object();
            String __DSPOT_suggestion_7022 = ">aIAjXV:Xbev1kHSGRH3";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            String o_useBeforeAllocateForbidden_mg32215__10 = nameAllocator.newName(__DSPOT_suggestion_7022, null);
            String o_useBeforeAllocateForbidden_mg32215_mg32292__14 = nameAllocator.newName(__DSPOT_suggestion_7029);
            org.junit.Assert.fail("useBeforeAllocateForbidden_mg32215_mg32292null33250 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg32215_mg32290litString32594() throws Exception {
        Object __DSPOT_tag_7023 = new Object();
        String __DSPOT_suggestion_7022 = ">aIAjXV:Xbey1kHSGRH3";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg32215__10 = nameAllocator.newName(__DSPOT_suggestion_7022, __DSPOT_tag_7023);
        Assert.assertEquals("_aIAjXV_Xbey1kHSGRH3", o_useBeforeAllocateForbidden_mg32215__10);
        nameAllocator.clone();
        Assert.assertEquals("_aIAjXV_Xbey1kHSGRH3", o_useBeforeAllocateForbidden_mg32215__10);
    }

    @Test(timeout = 10000)
    public void cloneUsagenull6213_failAssert328() throws Exception {
        try {
            NameAllocator outterAllocator = new NameAllocator();
            outterAllocator.newName("foo", null);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            org.junit.Assert.fail("cloneUsagenull6213 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsagenull6212_failAssert329() throws Exception {
        try {
            NameAllocator outterAllocator = new NameAllocator();
            outterAllocator.newName(null, 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            org.junit.Assert.fail("cloneUsagenull6212 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString6125() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString6125__3 = outterAllocator.newName("f3oo", 1);
        Assert.assertEquals("f3oo", o_cloneUsagelitString6125__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("f3oo", o_cloneUsagelitString6125__3);
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString6143_mg8221() throws Exception {
        Object __DSPOT_tag_1055 = new Object();
        String __DSPOT_suggestion_1054 = "wi,de73V{K/7#I-[b[[`";
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString6143__3 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsagelitString6143__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsagelitString6143_mg8221__13 = outterAllocator.newName(__DSPOT_suggestion_1054, __DSPOT_tag_1055);
        Assert.assertEquals("wi_de73V_K_7_I__b___", o_cloneUsagelitString6143_mg8221__13);
        Assert.assertEquals("foo", o_cloneUsagelitString6143__3);
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString6142null9467_failAssert340() throws Exception {
        try {
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsagelitString6142__3 = outterAllocator.newName(null, 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            org.junit.Assert.fail("cloneUsagelitString6142null9467 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString6142null9468_failAssert367() throws Exception {
        try {
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsagelitString6142__3 = outterAllocator.newName("foo", null);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            org.junit.Assert.fail("cloneUsagelitString6142null9468 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsagelitNum6172_mg8552null16058_failAssert428() throws Exception {
        try {
            String __DSPOT_suggestion_1385 = ":A=#/Z4SGxRU,7GegGAK";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsagelitNum6172__3 = outterAllocator.newName("foo", null);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsagelitNum6172_mg8552__11 = innerAllocator2.newName(__DSPOT_suggestion_1385);
            org.junit.Assert.fail("cloneUsagelitNum6172_mg8552null16058 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsagelitNum6172_mg8553_mg14044() throws Exception {
        Object __DSPOT_tag_1387 = new Object();
        String __DSPOT_suggestion_1386 = ".,hnV|K#q@+#RLJ6<5ds";
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitNum6172__3 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsagelitNum6172__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsagelitNum6172_mg8553__13 = innerAllocator2.newName(__DSPOT_suggestion_1386, __DSPOT_tag_1387);
        Assert.assertEquals("__hnV_K_q___RLJ6_5ds", o_cloneUsagelitNum6172_mg8553__13);
        outterAllocator.clone();
        Assert.assertEquals("foo", o_cloneUsagelitNum6172__3);
        Assert.assertEquals("__hnV_K_q___RLJ6_5ds", o_cloneUsagelitNum6172_mg8553__13);
    }

    @Test(timeout = 10000)
    public void cloneUsagelitNum6172_mg8553null15939_failAssert402() throws Exception {
        try {
            Object __DSPOT_tag_1387 = new Object();
            String __DSPOT_suggestion_1386 = ".,hnV|K#q@+#RLJ6<5ds";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsagelitNum6172__3 = outterAllocator.newName(null, 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsagelitNum6172_mg8553__13 = innerAllocator2.newName(__DSPOT_suggestion_1386, __DSPOT_tag_1387);
            org.junit.Assert.fail("cloneUsagelitNum6172_mg8553null15939 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }
}

