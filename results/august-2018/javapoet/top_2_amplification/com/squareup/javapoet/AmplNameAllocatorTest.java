package com.squareup.javapoet;


import org.junit.Assert;
import org.junit.Test;


public final class AmplNameAllocatorTest {
    @Test(timeout = 10000)
    public void usage_mg38743() throws Exception {
        Object __DSPOT_tag_5578 = new Object();
        String __DSPOT_suggestion_5577 = "t(KtNI*9f|cPYs^c[rg{";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg38743__6 = nameAllocator.newName(__DSPOT_suggestion_5577, __DSPOT_tag_5578);
        Assert.assertEquals("t_KtNI_9f_cPYs_c_rg_", o_usage_mg38743__6);
    }

    @Test(timeout = 10000)
    public void usage_mg38743null39027_failAssert643() throws Exception {
        try {
            Object __DSPOT_tag_5578 = new Object();
            String __DSPOT_suggestion_5577 = "t(KtNI*9f|cPYs^c[rg{";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_mg38743__6 = nameAllocator.newName(null, __DSPOT_tag_5578);
            org.junit.Assert.fail("usage_mg38743null39027 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void usage_mg38743null39017_failAssert640() throws Exception {
        try {
            Object __DSPOT_tag_5578 = null;
            String __DSPOT_suggestion_5577 = "t(KtNI*9f|cPYs^c[rg{";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_mg38743__6 = nameAllocator.newName(__DSPOT_suggestion_5577, __DSPOT_tag_5578);
            org.junit.Assert.fail("usage_mg38743null39017 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void usage_mg38743litString38992() throws Exception {
        Object __DSPOT_tag_5578 = new Object();
        String __DSPOT_suggestion_5577 = "t(1tNI*9f|cPYs^c[rg{";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg38743__6 = nameAllocator.newName(__DSPOT_suggestion_5577, __DSPOT_tag_5578);
        Assert.assertEquals("t_1tNI_9f_cPYs_c_rg_", o_usage_mg38743__6);
    }

    @Test(timeout = 10000)
    public void usage_mg38743_mg39011null39794_failAssert680() throws Exception {
        try {
            String __DSPOT_suggestion_5582 = "h_6qGOlSh-_oN9GT3y 7";
            Object __DSPOT_tag_5578 = new Object();
            String __DSPOT_suggestion_5577 = "t(KtNI*9f|cPYs^c[rg{";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_mg38743__6 = nameAllocator.newName(__DSPOT_suggestion_5577, null);
            String o_usage_mg38743_mg39011__10 = nameAllocator.newName(__DSPOT_suggestion_5582);
            org.junit.Assert.fail("usage_mg38743_mg39011null39794 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void usage_mg38743_mg39013null39842_failAssert655() throws Exception {
        try {
            Object __DSPOT_tag_5586 = new Object();
            String __DSPOT_suggestion_5585 = "s&};w$ 0o79 6b]w3j(,";
            Object __DSPOT_tag_5578 = new Object();
            String __DSPOT_suggestion_5577 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_mg38743__6 = nameAllocator.newName(__DSPOT_suggestion_5577, __DSPOT_tag_5578);
            String o_usage_mg38743_mg39013__12 = nameAllocator.newName(__DSPOT_suggestion_5585, __DSPOT_tag_5586);
            org.junit.Assert.fail("usage_mg38743_mg39013null39842 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void usage_mg38742null39022null39438() throws Exception {
        String __DSPOT_suggestion_5576 = "0Uz>YIl}Z]j>-,V1*_?e";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg38742__4 = nameAllocator.newName(__DSPOT_suggestion_5576);
        Assert.assertEquals("_0Uz_YIl_Z_j___V1___e", o_usage_mg38742__4);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24175() throws Exception {
        Object __DSPOT_tag_4349 = new Object();
        String __DSPOT_suggestion_4348 = "VMdab#?[+R4>tZJ.ReQg";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg24175__6 = nameAllocator.newName(__DSPOT_suggestion_4348, __DSPOT_tag_4349);
        Assert.assertEquals("VMdab____R4_tZJ_ReQg", o_nameCollision_mg24175__6);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24175null24384_failAssert442() throws Exception {
        try {
            Object __DSPOT_tag_4349 = new Object();
            String __DSPOT_suggestion_4348 = "VMdab#?[+R4>tZJ.ReQg";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_mg24175__6 = nameAllocator.newName(__DSPOT_suggestion_4348, null);
            org.junit.Assert.fail("nameCollision_mg24175null24384 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24175null24383_failAssert441() throws Exception {
        try {
            Object __DSPOT_tag_4349 = new Object();
            String __DSPOT_suggestion_4348 = "VMdab#?[+R4>tZJ.ReQg";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_mg24175__6 = nameAllocator.newName(null, __DSPOT_tag_4349);
            org.junit.Assert.fail("nameCollision_mg24175null24383 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24175litString24349() throws Exception {
        Object __DSPOT_tag_4349 = new Object();
        String __DSPOT_suggestion_4348 = "VMdab#?[+R4>tZJ.RxeQg";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg24175__6 = nameAllocator.newName(__DSPOT_suggestion_4348, __DSPOT_tag_4349);
        Assert.assertEquals("VMdab____R4_tZJ_RxeQg", o_nameCollision_mg24175__6);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24175_mg24367null25189_failAssert485() throws Exception {
        try {
            String __DSPOT_suggestion_4353 = "3U<xo_XUQ3l^+sv)$C*=";
            Object __DSPOT_tag_4349 = new Object();
            String __DSPOT_suggestion_4348 = "VMdab#?[+R4>tZJ.ReQg";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_mg24175__6 = nameAllocator.newName(null, __DSPOT_tag_4349);
            String o_nameCollision_mg24175_mg24367__10 = nameAllocator.newName(__DSPOT_suggestion_4353);
            org.junit.Assert.fail("nameCollision_mg24175_mg24367null25189 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24175_mg24367null25154_failAssert484() throws Exception {
        try {
            String __DSPOT_suggestion_4353 = "3U<xo_XUQ3l^+sv)$C*=";
            Object __DSPOT_tag_4349 = null;
            String __DSPOT_suggestion_4348 = "VMdab#?[+R4>tZJ.ReQg";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_mg24175__6 = nameAllocator.newName(__DSPOT_suggestion_4348, __DSPOT_tag_4349);
            String o_nameCollision_mg24175_mg24367__10 = nameAllocator.newName(__DSPOT_suggestion_4353);
            org.junit.Assert.fail("nameCollision_mg24175_mg24367null25154 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_mg24174_mg24368litString24568() throws Exception {
        Object __DSPOT_tag_4355 = new Object();
        String __DSPOT_suggestion_4354 = "{sP&w:!8h)R_[i%bsN5/";
        String __DSPOT_suggestion_4347 = "\n";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg24174__4 = nameAllocator.newName(__DSPOT_suggestion_4347);
        Assert.assertEquals("_", o_nameCollision_mg24174__4);
        String o_nameCollision_mg24174_mg24368__10 = nameAllocator.newName(__DSPOT_suggestion_4354, __DSPOT_tag_4355);
        Assert.assertEquals("_sP_w__8h_R__i_bsN5_", o_nameCollision_mg24174_mg24368__10);
        Assert.assertEquals("_", o_nameCollision_mg24174__4);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg25847() throws Exception {
        Object __DSPOT_tag_4517 = new Object();
        String __DSPOT_suggestion_4516 = "x`ddvrKjq6l$(tZ*ED!f";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_mg25847__6 = nameAllocator.newName(__DSPOT_suggestion_4516, __DSPOT_tag_4517);
        Assert.assertEquals("x_ddvrKjq6l$_tZ_ED_f", o_nameCollisionWithTag_mg25847__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg25847null26239_failAssert503() throws Exception {
        try {
            Object __DSPOT_tag_4517 = null;
            String __DSPOT_suggestion_4516 = "x`ddvrKjq6l$(tZ*ED!f";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_mg25847__6 = nameAllocator.newName(__DSPOT_suggestion_4516, __DSPOT_tag_4517);
            org.junit.Assert.fail("nameCollisionWithTag_mg25847null26239 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg25847null26242_failAssert504() throws Exception {
        try {
            Object __DSPOT_tag_4517 = new Object();
            String __DSPOT_suggestion_4516 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_mg25847__6 = nameAllocator.newName(__DSPOT_suggestion_4516, __DSPOT_tag_4517);
            org.junit.Assert.fail("nameCollisionWithTag_mg25847null26242 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg25846litString26215() throws Exception {
        String __DSPOT_suggestion_4515 = "$0t$nmN0>W`bOGlY#IlL6";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_mg25846__4 = nameAllocator.newName(__DSPOT_suggestion_4515);
        Assert.assertEquals("$0t$nmN0_W_bOGlY_IlL6", o_nameCollisionWithTag_mg25846__4);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg25847_mg26234null27050_failAssert538() throws Exception {
        try {
            String __DSPOT_suggestion_4521 = "UXpWM1crZKZ>+<H4m!Q>";
            Object __DSPOT_tag_4517 = new Object();
            String __DSPOT_suggestion_4516 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_mg25847__6 = nameAllocator.newName(__DSPOT_suggestion_4516, __DSPOT_tag_4517);
            String o_nameCollisionWithTag_mg25847_mg26234__10 = nameAllocator.newName(__DSPOT_suggestion_4521);
            org.junit.Assert.fail("nameCollisionWithTag_mg25847_mg26234null27050 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg25847_mg26236null27081() throws Exception {
        Object __DSPOT_tag_4525 = new Object();
        String __DSPOT_suggestion_4524 = "^T5=#a1 EeDrZJ[)g[^<";
        Object __DSPOT_tag_4517 = new Object();
        String __DSPOT_suggestion_4516 = "x`ddvrKjq6l$(tZ*ED!f";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_mg25847__6 = nameAllocator.newName(__DSPOT_suggestion_4516, __DSPOT_tag_4517);
        Assert.assertEquals("x_ddvrKjq6l$_tZ_ED_f", o_nameCollisionWithTag_mg25847__6);
        String o_nameCollisionWithTag_mg25847_mg26236__12 = nameAllocator.newName(__DSPOT_suggestion_4524, __DSPOT_tag_4525);
        Assert.assertEquals("_T5__a1_EeDrZJ__g___", o_nameCollisionWithTag_mg25847_mg26236__12);
        Assert.assertEquals("x_ddvrKjq6l$_tZ_ED_f", o_nameCollisionWithTag_mg25847__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg25847_mg26234null27045_failAssert543() throws Exception {
        try {
            String __DSPOT_suggestion_4521 = "UXpWM1crZKZ>+<H4m!Q>";
            Object __DSPOT_tag_4517 = null;
            String __DSPOT_suggestion_4516 = "x`ddvrKjq6l$(tZ*ED!f";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_mg25847__6 = nameAllocator.newName(__DSPOT_suggestion_4516, __DSPOT_tag_4517);
            String o_nameCollisionWithTag_mg25847_mg26234__10 = nameAllocator.newName(__DSPOT_suggestion_4521);
            org.junit.Assert.fail("nameCollisionWithTag_mg25847_mg26234null27045 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3077() throws Exception {
        Object __DSPOT_tag_338 = new Object();
        String __DSPOT_suggestion_337 = "n5_N]Rma)Zp`f_]Wh$_8";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg3077__6 = nameAllocator.newName(__DSPOT_suggestion_337, __DSPOT_tag_338);
        Assert.assertEquals("n5_N_Rma_Zp_f__Wh$_8", o_characterMappingSubstitute_mg3077__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3077null3191_failAssert135() throws Exception {
        try {
            Object __DSPOT_tag_338 = null;
            String __DSPOT_suggestion_337 = "n5_N]Rma)Zp`f_]Wh$_8";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_mg3077__6 = nameAllocator.newName(__DSPOT_suggestion_337, __DSPOT_tag_338);
            org.junit.Assert.fail("characterMappingSubstitute_mg3077null3191 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3076null3189_failAssert130() throws Exception {
        try {
            String __DSPOT_suggestion_336 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_mg3076__4 = nameAllocator.newName(__DSPOT_suggestion_336);
            org.junit.Assert.fail("characterMappingSubstitute_mg3076null3189 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3077litString3170() throws Exception {
        Object __DSPOT_tag_338 = new Object();
        String __DSPOT_suggestion_337 = "n5_N]Rma)Z`f_]Wh$_8";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg3077__6 = nameAllocator.newName(__DSPOT_suggestion_337, __DSPOT_tag_338);
        Assert.assertEquals("n5_N_Rma_Z_f__Wh$_8", o_characterMappingSubstitute_mg3077__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3077_mg3186null4027_failAssert172() throws Exception {
        try {
            String __DSPOT_suggestion_342 = "Nqa:(I-2sN3u/#h+}LLu";
            Object __DSPOT_tag_338 = new Object();
            String __DSPOT_suggestion_337 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_mg3077__6 = nameAllocator.newName(__DSPOT_suggestion_337, __DSPOT_tag_338);
            String o_characterMappingSubstitute_mg3077_mg3186__10 = nameAllocator.newName(__DSPOT_suggestion_342);
            org.junit.Assert.fail("characterMappingSubstitute_mg3077_mg3186null4027 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3077_mg3186null4034_failAssert165() throws Exception {
        try {
            String __DSPOT_suggestion_342 = "Nqa:(I-2sN3u/#h+}LLu";
            Object __DSPOT_tag_338 = new Object();
            String __DSPOT_suggestion_337 = "n5_N]Rma)Zp`f_]Wh$_8";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_mg3077__6 = nameAllocator.newName(__DSPOT_suggestion_337, null);
            String o_characterMappingSubstitute_mg3077_mg3186__10 = nameAllocator.newName(__DSPOT_suggestion_342);
            org.junit.Assert.fail("characterMappingSubstitute_mg3077_mg3186null4034 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg3077_mg3186litString3872() throws Exception {
        String __DSPOT_suggestion_342 = ":";
        Object __DSPOT_tag_338 = new Object();
        String __DSPOT_suggestion_337 = "n5_N]Rma)Zp`f_]Wh$_8";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg3077__6 = nameAllocator.newName(__DSPOT_suggestion_337, __DSPOT_tag_338);
        Assert.assertEquals("n5_N_Rma_Zp_f__Wh$_8", o_characterMappingSubstitute_mg3077__6);
        String o_characterMappingSubstitute_mg3077_mg3186__10 = nameAllocator.newName(__DSPOT_suggestion_342);
        Assert.assertEquals("_", o_characterMappingSubstitute_mg3077_mg3186__10);
        Assert.assertEquals("n5_N_Rma_Zp_f__Wh$_8", o_characterMappingSubstitute_mg3077__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4613() throws Exception {
        Object __DSPOT_tag_506 = new Object();
        String __DSPOT_suggestion_505 = "`@7?|Zf!3jh[SvM:l-|+";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg4613__6 = nameAllocator.newName(__DSPOT_suggestion_505, __DSPOT_tag_506);
        Assert.assertEquals("__7__Zf_3jh_SvM_l___", o_characterMappingSurrogate_mg4613__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4613null4726_failAssert202() throws Exception {
        try {
            Object __DSPOT_tag_506 = new Object();
            String __DSPOT_suggestion_505 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_mg4613__6 = nameAllocator.newName(__DSPOT_suggestion_505, __DSPOT_tag_506);
            org.junit.Assert.fail("characterMappingSurrogate_mg4613null4726 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4613null4725_failAssert201() throws Exception {
        try {
            Object __DSPOT_tag_506 = null;
            String __DSPOT_suggestion_505 = "`@7?|Zf!3jh[SvM:l-|+";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_mg4613__6 = nameAllocator.newName(__DSPOT_suggestion_505, __DSPOT_tag_506);
            org.junit.Assert.fail("characterMappingSurrogate_mg4613null4725 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4613litString4708() throws Exception {
        Object __DSPOT_tag_506 = new Object();
        String __DSPOT_suggestion_505 = "LxY[s=ns5,U.Y[?;Ytm)";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg4613__6 = nameAllocator.newName(__DSPOT_suggestion_505, __DSPOT_tag_506);
        Assert.assertEquals("LxY_s_ns5_U_Y___Ytm_", o_characterMappingSurrogate_mg4613__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4613_mg4723null5405_failAssert215() throws Exception {
        try {
            Object __DSPOT_tag_512 = new Object();
            String __DSPOT_suggestion_511 = "xk@!dxt{Zp}:EhwD4*{;";
            Object __DSPOT_tag_506 = new Object();
            String __DSPOT_suggestion_505 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_mg4613__6 = nameAllocator.newName(__DSPOT_suggestion_505, __DSPOT_tag_506);
            String o_characterMappingSurrogate_mg4613_mg4723__12 = nameAllocator.newName(__DSPOT_suggestion_511, __DSPOT_tag_512);
            org.junit.Assert.fail("characterMappingSurrogate_mg4613_mg4723null5405 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4613_mg4723null5537() throws Exception {
        Object __DSPOT_tag_512 = new Object();
        String __DSPOT_suggestion_511 = "xk@!dxt{Zp}:EhwD4*{;";
        Object __DSPOT_tag_506 = new Object();
        String __DSPOT_suggestion_505 = "`@7?|Zf!3jh[SvM:l-|+";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg4613__6 = nameAllocator.newName(__DSPOT_suggestion_505, __DSPOT_tag_506);
        Assert.assertEquals("__7__Zf_3jh_SvM_l___", o_characterMappingSurrogate_mg4613__6);
        String o_characterMappingSurrogate_mg4613_mg4723__12 = nameAllocator.newName(__DSPOT_suggestion_511, __DSPOT_tag_512);
        Assert.assertEquals("xk__dxt_Zp__EhwD4___", o_characterMappingSurrogate_mg4613_mg4723__12);
        Assert.assertEquals("__7__Zf_3jh_SvM_l___", o_characterMappingSurrogate_mg4613__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg4613_mg4723null5387_failAssert217() throws Exception {
        try {
            Object __DSPOT_tag_512 = new Object();
            String __DSPOT_suggestion_511 = "xk@!dxt{Zp}:EhwD4*{;";
            Object __DSPOT_tag_506 = null;
            String __DSPOT_suggestion_505 = "`@7?|Zf!3jh[SvM:l-|+";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_mg4613__6 = nameAllocator.newName(__DSPOT_suggestion_505, __DSPOT_tag_506);
            String o_characterMappingSurrogate_mg4613_mg4723__12 = nameAllocator.newName(__DSPOT_suggestion_511, __DSPOT_tag_512);
            org.junit.Assert.fail("characterMappingSurrogate_mg4613_mg4723null5387 should have thrown NullPointerException");
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
    public void characterMappingInvalidStartButValidPart_mg18null130_failAssert7() throws Exception {
        try {
            Object __DSPOT_tag_3 = null;
            String __DSPOT_suggestion_2 = "L[{$QV5:Wz2[|+mr6#-V";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_mg18null130 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg17null143() throws Exception {
        String __DSPOT_suggestion_1 = "CS@!x*zH_,y(q2 5[gpb";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_mg17__4 = nameAllocator.newName(__DSPOT_suggestion_1);
        Assert.assertEquals("CS__x_zH__y_q2_5_gpb", o_characterMappingInvalidStartButValidPart_mg17__4);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg18null140_failAssert10() throws Exception {
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
    public void characterMappingInvalidStartButValidPart_mg18_mg126null816_failAssert33() throws Exception {
        try {
            String __DSPOT_suggestion_6 = "Bcvg[?i!rb0/|]6^FT)-";
            Object __DSPOT_tag_3 = new Object();
            String __DSPOT_suggestion_2 = "L[{$QV5:Wz2[|+mr6#-V";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(__DSPOT_suggestion_2, null);
            String o_characterMappingInvalidStartButValidPart_mg18_mg126__10 = nameAllocator.newName(__DSPOT_suggestion_6);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_mg18_mg126null816 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg18_mg126null771_failAssert37() throws Exception {
        try {
            String __DSPOT_suggestion_6 = "Bcvg[?i!rb0/|]6^FT)-";
            Object __DSPOT_tag_3 = new Object();
            String __DSPOT_suggestion_2 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
            String o_characterMappingInvalidStartButValidPart_mg18_mg126__10 = nameAllocator.newName(__DSPOT_suggestion_6);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_mg18_mg126null771 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg18_mg128null946() throws Exception {
        Object __DSPOT_tag_9 = new Object();
        String __DSPOT_suggestion_8 = "%h1,xavU[1Rvnj|}8wu]";
        Object __DSPOT_tag_3 = new Object();
        String __DSPOT_suggestion_2 = "L[{$QV5:Wz2[|+mr6#-V";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
        Assert.assertEquals("L__$QV5_Wz2___mr6__V", o_characterMappingInvalidStartButValidPart_mg18__6);
        String o_characterMappingInvalidStartButValidPart_mg18_mg128__12 = nameAllocator.newName(__DSPOT_suggestion_8, __DSPOT_tag_9);
        Assert.assertEquals("_h1_xavU_1Rvnj__8wu_", o_characterMappingInvalidStartButValidPart_mg18_mg128__12);
        Assert.assertEquals("L__$QV5_Wz2___mr6__V", o_characterMappingInvalidStartButValidPart_mg18__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1539() throws Exception {
        Object __DSPOT_tag_171 = new Object();
        String __DSPOT_suggestion_170 = "_^tFCqr,tX[gb 2PqgP;";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_mg1539__6 = nameAllocator.newName(__DSPOT_suggestion_170, __DSPOT_tag_171);
        Assert.assertEquals("__tFCqr_tX_gb_2PqgP_", o_characterMappingInvalidStartIsInvalidPart_mg1539__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1539null1654_failAssert70() throws Exception {
        try {
            Object __DSPOT_tag_171 = new Object();
            String __DSPOT_suggestion_170 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_mg1539__6 = nameAllocator.newName(__DSPOT_suggestion_170, __DSPOT_tag_171);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_mg1539null1654 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1539null1664_failAssert73() throws Exception {
        try {
            Object __DSPOT_tag_171 = new Object();
            String __DSPOT_suggestion_170 = "_^tFCqr,tX[gb 2PqgP;";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_mg1539__6 = nameAllocator.newName(__DSPOT_suggestion_170, null);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_mg1539null1664 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1538null1663() throws Exception {
        String __DSPOT_suggestion_169 = "^!%Y]_E]i.}JC]TrhX0]";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_mg1538__4 = nameAllocator.newName(__DSPOT_suggestion_169);
        Assert.assertEquals("___Y__E_i__JC_TrhX0_", o_characterMappingInvalidStartIsInvalidPart_mg1538__4);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1539_mg1650null2406_failAssert79() throws Exception {
        try {
            Object __DSPOT_tag_179 = new Object();
            String __DSPOT_suggestion_178 = "4 Iz;of%p.X)v_mKH*(,";
            Object __DSPOT_tag_171 = new Object();
            String __DSPOT_suggestion_170 = "_^tFCqr,tX[gb 2PqgP;";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_mg1539__6 = nameAllocator.newName(__DSPOT_suggestion_170, null);
            String o_characterMappingInvalidStartIsInvalidPart_mg1539_mg1650__12 = nameAllocator.newName(__DSPOT_suggestion_178, __DSPOT_tag_179);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_mg1539_mg1650null2406 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1539_mg1650null2397_failAssert87() throws Exception {
        try {
            Object __DSPOT_tag_179 = new Object();
            String __DSPOT_suggestion_178 = "4 Iz;of%p.X)v_mKH*(,";
            Object __DSPOT_tag_171 = new Object();
            String __DSPOT_suggestion_170 = "_^tFCqr,tX[gb 2PqgP;";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_mg1539__6 = nameAllocator.newName(null, __DSPOT_tag_171);
            String o_characterMappingInvalidStartIsInvalidPart_mg1539_mg1650__12 = nameAllocator.newName(__DSPOT_suggestion_178, __DSPOT_tag_179);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_mg1539_mg1650null2397 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1538litString1629null1991() throws Exception {
        String __DSPOT_suggestion_169 = "^!%Y]_E]i.}JC]Trh+X0]";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_mg1538__4 = nameAllocator.newName(__DSPOT_suggestion_169);
        Assert.assertEquals("___Y__E_i__JC_Trh_X0_", o_characterMappingInvalidStartIsInvalidPart_mg1538__4);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22583() throws Exception {
        Object __DSPOT_tag_4181 = new Object();
        String __DSPOT_suggestion_4180 = "QUM#7aj^BRdw.!I2l]<:";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg22583__6 = nameAllocator.newName(__DSPOT_suggestion_4180, __DSPOT_tag_4181);
        Assert.assertEquals("QUM_7aj_BRdw__I2l___", o_javaKeyword_mg22583__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22582null22737_failAssert368() throws Exception {
        try {
            String __DSPOT_suggestion_4179 = null;
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_mg22582__4 = nameAllocator.newName(__DSPOT_suggestion_4179);
            org.junit.Assert.fail("javaKeyword_mg22582null22737 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22583null22751_failAssert377() throws Exception {
        try {
            Object __DSPOT_tag_4181 = new Object();
            String __DSPOT_suggestion_4180 = "QUM#7aj^BRdw.!I2l]<:";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_mg22583__6 = nameAllocator.newName(__DSPOT_suggestion_4180, null);
            org.junit.Assert.fail("javaKeyword_mg22583null22751 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22583_mg22734() throws Exception {
        String __DSPOT_suggestion_4185 = "Z5BD8hGe#|if|rr-)yzn";
        Object __DSPOT_tag_4181 = new Object();
        String __DSPOT_suggestion_4180 = "QUM#7aj^BRdw.!I2l]<:";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg22583__6 = nameAllocator.newName(__DSPOT_suggestion_4180, __DSPOT_tag_4181);
        Assert.assertEquals("QUM_7aj_BRdw__I2l___", o_javaKeyword_mg22583__6);
        String o_javaKeyword_mg22583_mg22734__10 = nameAllocator.newName(__DSPOT_suggestion_4185);
        Assert.assertEquals("Z5BD8hGe__if_rr__yzn", o_javaKeyword_mg22583_mg22734__10);
        Assert.assertEquals("QUM_7aj_BRdw__I2l___", o_javaKeyword_mg22583__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22583_mg22734null23567_failAssert415() throws Exception {
        try {
            String __DSPOT_suggestion_4185 = "Z5BD8hGe#|if|rr-)yzn";
            Object __DSPOT_tag_4181 = new Object();
            String __DSPOT_suggestion_4180 = "QUM#7aj^BRdw.!I2l]<:";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_mg22583__6 = nameAllocator.newName(null, __DSPOT_tag_4181);
            String o_javaKeyword_mg22583_mg22734__10 = nameAllocator.newName(__DSPOT_suggestion_4185);
            org.junit.Assert.fail("javaKeyword_mg22583_mg22734null23567 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22583_mg22734null23549_failAssert414() throws Exception {
        try {
            String __DSPOT_suggestion_4185 = "Z5BD8hGe#|if|rr-)yzn";
            Object __DSPOT_tag_4181 = null;
            String __DSPOT_suggestion_4180 = "QUM#7aj^BRdw.!I2l]<:";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_mg22583__6 = nameAllocator.newName(__DSPOT_suggestion_4180, __DSPOT_tag_4181);
            String o_javaKeyword_mg22583_mg22734__10 = nameAllocator.newName(__DSPOT_suggestion_4185);
            org.junit.Assert.fail("javaKeyword_mg22583_mg22734null23549 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg22583_mg22736null23545() throws Exception {
        Object __DSPOT_tag_4189 = new Object();
        String __DSPOT_suggestion_4188 = "#QToJVG=HCb]q!WdOb^s";
        Object __DSPOT_tag_4181 = new Object();
        String __DSPOT_suggestion_4180 = "QUM#7aj^BRdw.!I2l]<:";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg22583__6 = nameAllocator.newName(__DSPOT_suggestion_4180, __DSPOT_tag_4181);
        Assert.assertEquals("QUM_7aj_BRdw__I2l___", o_javaKeyword_mg22583__6);
        String o_javaKeyword_mg22583_mg22736__12 = nameAllocator.newName(__DSPOT_suggestion_4188, __DSPOT_tag_4189);
        Assert.assertEquals("_QToJVG_HCb_q_WdOb_s", o_javaKeyword_mg22583_mg22736__12);
        Assert.assertEquals("QUM_7aj_BRdw__I2l___", o_javaKeyword_mg22583__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg27681() throws Exception {
        String __DSPOT_suggestion_4683 = "f[eV`wu}0=]V@:)U8#r<";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg27681__4 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg27681__4);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg27681__9 = nameAllocator.newName(__DSPOT_suggestion_4683);
        Assert.assertEquals("f_eV_wu_0__V___U8_r_", o_tagReuseForbidden_mg27681__9);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg27681__4);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull27686_failAssert563() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", null);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull27686 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddennull27685_failAssert562() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(null, 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbiddennull27685 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg27681null29109_failAssert587() throws Exception {
        try {
            String __DSPOT_suggestion_4683 = "f[eV`wu}0=]V@:)U8#r<";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_mg27681__4 = nameAllocator.newName("foo", null);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_mg27681__9 = nameAllocator.newName(__DSPOT_suggestion_4683);
            org.junit.Assert.fail("tagReuseForbidden_mg27681null29109 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg27682null29035_failAssert573() throws Exception {
        try {
            Object __DSPOT_tag_4685 = new Object();
            String __DSPOT_suggestion_4684 = "sStnUh^F{IfCyhKIXrA@";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_mg27682__6 = nameAllocator.newName(null, 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_mg27682__11 = nameAllocator.newName(__DSPOT_suggestion_4684, __DSPOT_tag_4685);
            org.junit.Assert.fail("tagReuseForbidden_mg27682null29035 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum27674_mg28565() throws Exception {
        Object __DSPOT_tag_4703 = new Object();
        String __DSPOT_suggestion_4702 = ">`kM;fCG*<GJ![ySpS4m";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum27674__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum27674__3);
        try {
            String o_tagReuseForbiddenlitNum27674__6 = nameAllocator.newName("bar", Integer.MIN_VALUE);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum27674__6);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitNum27674_mg28565__15 = nameAllocator.newName(__DSPOT_suggestion_4702, __DSPOT_tag_4703);
        Assert.assertEquals("__kM_fCG__GJ__ySpS4m", o_tagReuseForbiddenlitNum27674_mg28565__15);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum27674__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg27682_mg28951litString33355() throws Exception {
        Object __DSPOT_tag_4794 = new Object();
        String __DSPOT_suggestion_4793 = ",:f*wuI7#;K2DJ&8r |_";
        Object __DSPOT_tag_4685 = new Object();
        String __DSPOT_suggestion_4684 = "sStnUh^F{IfCyhKIXrA@";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg27682__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg27682__6);
        try {
            nameAllocator.newName("\n", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg27682__11 = nameAllocator.newName(__DSPOT_suggestion_4684, __DSPOT_tag_4685);
        Assert.assertEquals("sStnUh_F_IfCyhKIXrA_", o_tagReuseForbidden_mg27682__11);
        String o_tagReuseForbidden_mg27682_mg28951__19 = nameAllocator.newName(__DSPOT_suggestion_4793, __DSPOT_tag_4794);
        Assert.assertEquals("__f_wuI7__K2DJ_8r___", o_tagReuseForbidden_mg27682_mg28951__19);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg27682__6);
        Assert.assertEquals("sStnUh_F_IfCyhKIXrA_", o_tagReuseForbidden_mg27682__11);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove27678null28415null36818_failAssert621() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            try {
                String o_tagReuseForbidden_remove27678__5 = nameAllocator.newName("bar", null);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbidden_remove27678null28415null36818 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove27678null28415null36813_failAssert616() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            try {
                String o_tagReuseForbidden_remove27678__5 = nameAllocator.newName(null, 1);
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("tagReuseForbidden_remove27678null28415null36813 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40433() throws Exception {
        Object __DSPOT_tag_5745 = new Object();
        String __DSPOT_suggestion_5744 = "H3C}&61%xfN*{901?4gb";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg40433__10 = nameAllocator.newName(__DSPOT_suggestion_5744, __DSPOT_tag_5745);
        Assert.assertEquals("H3C__61_xfN__901_4gb", o_useBeforeAllocateForbidden_mg40433__10);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40433null40568_failAssert705() throws Exception {
        try {
            Object __DSPOT_tag_5745 = new Object();
            String __DSPOT_suggestion_5744 = "H3C}&61%xfN*{901?4gb";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            String o_useBeforeAllocateForbidden_mg40433__10 = nameAllocator.newName(__DSPOT_suggestion_5744, null);
            org.junit.Assert.fail("useBeforeAllocateForbidden_mg40433null40568 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40432null40565() throws Exception {
        String __DSPOT_suggestion_5743 = " S`jnPq@E}JYJX9&%|fz";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg40432__8 = nameAllocator.newName(__DSPOT_suggestion_5743);
        Assert.assertEquals("_S_jnPq_E_JYJX9___fz", o_useBeforeAllocateForbidden_mg40432__8);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40433null40556_failAssert702() throws Exception {
        try {
            Object __DSPOT_tag_5745 = new Object();
            String __DSPOT_suggestion_5744 = null;
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            String o_useBeforeAllocateForbidden_mg40433__10 = nameAllocator.newName(__DSPOT_suggestion_5744, __DSPOT_tag_5745);
            org.junit.Assert.fail("useBeforeAllocateForbidden_mg40433null40556 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40432_mg40547null42026_failAssert725() throws Exception {
        try {
            Object __DSPOT_tag_5754 = new Object();
            String __DSPOT_suggestion_5753 = "-,w.|ZACecYouH/WS&y&";
            String __DSPOT_suggestion_5743 = null;
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            String o_useBeforeAllocateForbidden_mg40432__8 = nameAllocator.newName(__DSPOT_suggestion_5743);
            String o_useBeforeAllocateForbidden_mg40432_mg40547__14 = nameAllocator.newName(__DSPOT_suggestion_5753, __DSPOT_tag_5754);
            org.junit.Assert.fail("useBeforeAllocateForbidden_mg40432_mg40547null42026 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("suggestion", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40432_mg40547null42108() throws Exception {
        Object __DSPOT_tag_5754 = new Object();
        String __DSPOT_suggestion_5753 = "-,w.|ZACecYouH/WS&y&";
        String __DSPOT_suggestion_5743 = " S`jnPq@E}JYJX9&%|fz";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg40432__8 = nameAllocator.newName(__DSPOT_suggestion_5743);
        Assert.assertEquals("_S_jnPq_E_JYJX9___fz", o_useBeforeAllocateForbidden_mg40432__8);
        String o_useBeforeAllocateForbidden_mg40432_mg40547__14 = nameAllocator.newName(__DSPOT_suggestion_5753, __DSPOT_tag_5754);
        Assert.assertEquals("__w__ZACecYouH_WS_y_", o_useBeforeAllocateForbidden_mg40432_mg40547__14);
        Assert.assertEquals("_S_jnPq_E_JYJX9___fz", o_useBeforeAllocateForbidden_mg40432__8);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg40433_mg40548null41504_failAssert732() throws Exception {
        try {
            String __DSPOT_suggestion_5755 = "}U|ehl5*v3Fre!1c{D@c";
            Object __DSPOT_tag_5745 = null;
            String __DSPOT_suggestion_5744 = "H3C}&61%xfN*{901?4gb";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            String o_useBeforeAllocateForbidden_mg40433__10 = nameAllocator.newName(__DSPOT_suggestion_5744, __DSPOT_tag_5745);
            String o_useBeforeAllocateForbidden_mg40433_mg40548__14 = nameAllocator.newName(__DSPOT_suggestion_5755);
            org.junit.Assert.fail("useBeforeAllocateForbidden_mg40433_mg40548null41504 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals("tag", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsagenull6225_failAssert266() throws Exception {
        try {
            NameAllocator outterAllocator = new NameAllocator();
            outterAllocator.newName(null, 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            org.junit.Assert.fail("cloneUsagenull6225 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsagenull6226_failAssert267() throws Exception {
        try {
            NameAllocator outterAllocator = new NameAllocator();
            outterAllocator.newName("foo", null);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            org.junit.Assert.fail("cloneUsagenull6226 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString6134() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString6134__3 = outterAllocator.newName("tag 1 cannot be used for both \'foo\' and \'bar\'", 1);
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", o_cloneUsagelitString6134__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", o_cloneUsagelitString6134__3);
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg6218_mg10594() throws Exception {
        Object __DSPOT_tag_678 = new Object();
        String __DSPOT_suggestion_677 = "97!n@[jri.o56p(&&`#J";
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_mg6218__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_mg6218__6);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_mg6218__11 = innerAllocator1.newName(__DSPOT_suggestion_677, __DSPOT_tag_678);
        Assert.assertEquals("_97_n__jri_o56p_____J", o_cloneUsage_mg6218__11);
        outterAllocator.clone();
        Assert.assertEquals("foo", o_cloneUsage_mg6218__6);
        Assert.assertEquals("_97_n__jri_o56p_____J", o_cloneUsage_mg6218__11);
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg6222null9580_failAssert297() throws Exception {
        try {
            Object __DSPOT_tag_682 = new Object();
            String __DSPOT_suggestion_681 = "w[qX-.C!W/4=(weR>z8Z";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_mg6222__6 = outterAllocator.newName(null, 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_mg6222__11 = innerAllocator2.newName(__DSPOT_suggestion_681, __DSPOT_tag_682);
            org.junit.Assert.fail("cloneUsage_mg6222null9580 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg6214null7715_failAssert292() throws Exception {
        try {
            Object __DSPOT_tag_674 = new Object();
            String __DSPOT_suggestion_673 = "kx7?ezalTwt&Wp6w:cKr";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_mg6214__6 = outterAllocator.newName("foo", null);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_mg6214__11 = outterAllocator.newName(__DSPOT_suggestion_673, __DSPOT_tag_674);
            org.junit.Assert.fail("cloneUsage_mg6214null7715 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg6214_mg7355_mg17015() throws Exception {
        String __DSPOT_suggestion_3057 = "32&LAv*,:+kd4_Ow-np.";
        Object __DSPOT_tag_884 = new Object();
        String __DSPOT_suggestion_883 = "ia_[wGs]/6B$sn%]M7!F";
        Object __DSPOT_tag_674 = new Object();
        String __DSPOT_suggestion_673 = "kx7?ezalTwt&Wp6w:cKr";
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_mg6214__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_mg6214__6);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_mg6214__11 = outterAllocator.newName(__DSPOT_suggestion_673, __DSPOT_tag_674);
        Assert.assertEquals("kx7_ezalTwt_Wp6w_cKr", o_cloneUsage_mg6214__11);
        String o_cloneUsage_mg6214_mg7355__19 = innerAllocator1.newName(__DSPOT_suggestion_883, __DSPOT_tag_884);
        Assert.assertEquals("ia__wGs__6B$sn__M7_F", o_cloneUsage_mg6214_mg7355__19);
        String o_cloneUsage_mg6214_mg7355_mg17015__23 = outterAllocator.newName(__DSPOT_suggestion_3057);
        Assert.assertEquals("_32_LAv____kd4_Ow_np_", o_cloneUsage_mg6214_mg7355_mg17015__23);
        Assert.assertEquals("foo", o_cloneUsage_mg6214__6);
        Assert.assertEquals("kx7_ezalTwt_Wp6w_cKr", o_cloneUsage_mg6214__11);
        Assert.assertEquals("ia__wGs__6B$sn__M7_F", o_cloneUsage_mg6214_mg7355__19);
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg6214null7770null19286_failAssert362() throws Exception {
        try {
            Object __DSPOT_tag_674 = new Object();
            String __DSPOT_suggestion_673 = "kx7?ezalTwt&Wp6w:cKr";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_mg6214__6 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = null;
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_mg6214__11 = outterAllocator.newName(__DSPOT_suggestion_673, null);
            org.junit.Assert.fail("cloneUsage_mg6214null7770null19286 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("tag", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg6214null7770null19178_failAssert364() throws Exception {
        try {
            Object __DSPOT_tag_674 = new Object();
            String __DSPOT_suggestion_673 = "kx7?ezalTwt&Wp6w:cKr";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_mg6214__6 = outterAllocator.newName(null, 1);
            NameAllocator innerAllocator1 = null;
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_mg6214__11 = outterAllocator.newName(__DSPOT_suggestion_673, __DSPOT_tag_674);
            org.junit.Assert.fail("cloneUsage_mg6214null7770null19178 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("suggestion", expected.getMessage());
        }
    }
}

