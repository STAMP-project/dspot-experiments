package com.squareup.javapoet;


import org.junit.Assert;
import org.junit.Test;


public final class AmplNameAllocatorTest {
    @Test(timeout = 10000)
    public void usage_mg24190() throws Exception {
        String __DSPOT_suggestion_5052 = "(xrUr5x9Fo1n_sK!?|v9";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg24190__4 = nameAllocator.newName(__DSPOT_suggestion_5052);
        Assert.assertEquals("_xrUr5x9Fo1n_sK___v9", o_usage_mg24190__4);
    }

    @Test(timeout = 10000)
    public void usage_mg24190litString24333() throws Exception {
        String __DSPOT_suggestion_5052 = "(xrUr5x9Fo17n_sK!?|v9";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg24190__4 = nameAllocator.newName(__DSPOT_suggestion_5052);
        Assert.assertEquals("_xrUr5x9Fo17n_sK___v9", o_usage_mg24190__4);
    }

    @Test(timeout = 10000)
    public void usage_mg24190_mg24353litString24473() throws Exception {
        Object __DSPOT_tag_5060 = new Object();
        String __DSPOT_suggestion_5059 = "";
        String __DSPOT_suggestion_5052 = "(xrUr5x9Fo1n_sK!?|v9";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg24190__4 = nameAllocator.newName(__DSPOT_suggestion_5052);
        Assert.assertEquals("_xrUr5x9Fo1n_sK___v9", o_usage_mg24190__4);
        String o_usage_mg24190_mg24353__10 = nameAllocator.newName(__DSPOT_suggestion_5059, __DSPOT_tag_5060);
        Assert.assertEquals("", o_usage_mg24190_mg24353__10);
        Assert.assertEquals("_xrUr5x9Fo1n_sK___v9", o_usage_mg24190__4);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg15034() throws Exception {
        Object __DSPOT_tag_3887 = new Object();
        String __DSPOT_suggestion_3886 = "M>t?^ZBRVlx.k5<HxX^!";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg15034__6 = nameAllocator.newName(__DSPOT_suggestion_3886, __DSPOT_tag_3887);
        Assert.assertEquals("M_t__ZBRVlx_k5_HxX__", o_nameCollision_mg15034__6);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg15034_mg15150() throws Exception {
        String __DSPOT_suggestion_3891 = "/!PKJ +.n#=@>fZK/k0E";
        Object __DSPOT_tag_3887 = new Object();
        String __DSPOT_suggestion_3886 = "M>t?^ZBRVlx.k5<HxX^!";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg15034__6 = nameAllocator.newName(__DSPOT_suggestion_3886, __DSPOT_tag_3887);
        Assert.assertEquals("M_t__ZBRVlx_k5_HxX__", o_nameCollision_mg15034__6);
        String o_nameCollision_mg15034_mg15150__10 = nameAllocator.newName(__DSPOT_suggestion_3891);
        Assert.assertEquals("__PKJ___n____fZK_k0E", o_nameCollision_mg15034_mg15150__10);
        Assert.assertEquals("M_t__ZBRVlx_k5_HxX__", o_nameCollision_mg15034__6);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg15033litString15131_add15256() throws Exception {
        String __DSPOT_suggestion_3885 = "&gN(y1<)L:j4|NYF]d$rz";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg15033__4 = nameAllocator.newName(__DSPOT_suggestion_3885);
        Assert.assertEquals("_gN_y1__L_j4_NYF_d$rz", o_nameCollision_mg15033__4);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg16107() throws Exception {
        Object __DSPOT_tag_4002 = new Object();
        String __DSPOT_suggestion_4001 = "l:A9LM6]>zK`1GmW$^@8";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_mg16107__6 = nameAllocator.newName(__DSPOT_suggestion_4001, __DSPOT_tag_4002);
        Assert.assertEquals("l_A9LM6__zK_1GmW$__8", o_nameCollisionWithTag_mg16107__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg16107_add16328() throws Exception {
        Object __DSPOT_tag_4002 = new Object();
        String __DSPOT_suggestion_4001 = "l:A9LM6]>zK`1GmW$^@8";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_mg16107__6 = nameAllocator.newName(__DSPOT_suggestion_4001, __DSPOT_tag_4002);
        Assert.assertEquals("l_A9LM6__zK_1GmW$__8", o_nameCollisionWithTag_mg16107__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg16107_mg16336litString16464() throws Exception {
        Object __DSPOT_tag_4010 = new Object();
        String __DSPOT_suggestion_4009 = "V!^V#_nvu _fRP@{4$1{";
        Object __DSPOT_tag_4002 = new Object();
        String __DSPOT_suggestion_4001 = "l:_A9LM6]>zK`1GmW$^@8";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_mg16107__6 = nameAllocator.newName(__DSPOT_suggestion_4001, __DSPOT_tag_4002);
        Assert.assertEquals("l__A9LM6__zK_1GmW$__8", o_nameCollisionWithTag_mg16107__6);
        String o_nameCollisionWithTag_mg16107_mg16336__12 = nameAllocator.newName(__DSPOT_suggestion_4009, __DSPOT_tag_4010);
        Assert.assertEquals("V__V__nvu__fRP__4$1_", o_nameCollisionWithTag_mg16107_mg16336__12);
        Assert.assertEquals("l__A9LM6__zK_1GmW$__8", o_nameCollisionWithTag_mg16107__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg1985() throws Exception {
        Object __DSPOT_tag_235 = new Object();
        String __DSPOT_suggestion_234 = "4GTC}l]qvelpPW`h79`w";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg1985__6 = nameAllocator.newName(__DSPOT_suggestion_234, __DSPOT_tag_235);
        Assert.assertEquals("_4GTC_l_qvelpPW_h79_w", o_characterMappingSubstitute_mg1985__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg1985_add2055() throws Exception {
        Object __DSPOT_tag_235 = new Object();
        String __DSPOT_suggestion_234 = "4GTC}l]qvelpPW`h79`w";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg1985__6 = nameAllocator.newName(__DSPOT_suggestion_234, __DSPOT_tag_235);
        Assert.assertEquals("_4GTC_l_qvelpPW_h79_w", o_characterMappingSubstitute_mg1985__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg1985litString2042_add2328() throws Exception {
        Object __DSPOT_tag_235 = new Object();
        String __DSPOT_suggestion_234 = "4GeC}l]qvelpPW`h79`w";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg1985__6 = nameAllocator.newName(__DSPOT_suggestion_234, __DSPOT_tag_235);
        Assert.assertEquals("_4GeC_l_qvelpPW_h79_w", o_characterMappingSubstitute_mg1985__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg2969() throws Exception {
        Object __DSPOT_tag_351 = new Object();
        String __DSPOT_suggestion_350 = "#S4@x6*]1I5%}^%8%Xw`";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg2969__6 = nameAllocator.newName(__DSPOT_suggestion_350, __DSPOT_tag_351);
        Assert.assertEquals("_S4_x6__1I5____8_Xw_", o_characterMappingSurrogate_mg2969__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg2969_add3039() throws Exception {
        Object __DSPOT_tag_351 = new Object();
        String __DSPOT_suggestion_350 = "#S4@x6*]1I5%}^%8%Xw`";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg2969__6 = nameAllocator.newName(__DSPOT_suggestion_350, __DSPOT_tag_351);
        Assert.assertEquals("_S4_x6__1I5____8_Xw_", o_characterMappingSurrogate_mg2969__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg2968_mg3047litString3163() throws Exception {
        Object __DSPOT_tag_357 = new Object();
        String __DSPOT_suggestion_356 = "";
        String __DSPOT_suggestion_349 = "!{;g(#dnlZb%LN0=O7`n";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg2968__4 = nameAllocator.newName(__DSPOT_suggestion_349);
        Assert.assertEquals("___g__dnlZb_LN0_O7_n", o_characterMappingSurrogate_mg2968__4);
        String o_characterMappingSurrogate_mg2968_mg3047__10 = nameAllocator.newName(__DSPOT_suggestion_356, __DSPOT_tag_357);
        Assert.assertEquals("", o_characterMappingSurrogate_mg2968_mg3047__10);
        Assert.assertEquals("___g__dnlZb_LN0_O7_n", o_characterMappingSurrogate_mg2968__4);
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
    public void characterMappingInvalidStartButValidPart_mg18_add88() throws Exception {
        Object __DSPOT_tag_3 = new Object();
        String __DSPOT_suggestion_2 = "L[{$QV5:Wz2[|+mr6#-V";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
        Assert.assertEquals("L__$QV5_Wz2___mr6__V", o_characterMappingInvalidStartButValidPart_mg18__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg18litString77_mg210() throws Exception {
        Object __DSPOT_tag_3 = new Object();
        String __DSPOT_suggestion_2 = "L[{$QV5:Wz2[|+mr6=#-V";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
        Assert.assertEquals("L__$QV5_Wz2___mr6___V", o_characterMappingInvalidStartButValidPart_mg18__6);
        nameAllocator.clone();
        Assert.assertEquals("L__$QV5_Wz2___mr6___V", o_characterMappingInvalidStartButValidPart_mg18__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1001() throws Exception {
        Object __DSPOT_tag_119 = new Object();
        String __DSPOT_suggestion_118 = "9)M4gfZk]Jpa[bR-2-=M";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_mg1001__6 = nameAllocator.newName(__DSPOT_suggestion_118, __DSPOT_tag_119);
        Assert.assertEquals("_9_M4gfZk_Jpa_bR_2__M", o_characterMappingInvalidStartIsInvalidPart_mg1001__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1001litString1061() throws Exception {
        Object __DSPOT_tag_119 = new Object();
        String __DSPOT_suggestion_118 = "9)M4gfZk]Jpa[bR-2]-=M";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_mg1001__6 = nameAllocator.newName(__DSPOT_suggestion_118, __DSPOT_tag_119);
        Assert.assertEquals("_9_M4gfZk_Jpa_bR_2___M", o_characterMappingInvalidStartIsInvalidPart_mg1001__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1001_mg1075_mg1334() throws Exception {
        Object __DSPOT_tag_199 = new Object();
        String __DSPOT_suggestion_198 = "G]>@aWA(ki&%rufmpg#7";
        Object __DSPOT_tag_119 = new Object();
        String __DSPOT_suggestion_118 = "9)M4gfZk]Jpa[bR-2-=M";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_mg1001__6 = nameAllocator.newName(__DSPOT_suggestion_118, __DSPOT_tag_119);
        Assert.assertEquals("_9_M4gfZk_Jpa_bR_2__M", o_characterMappingInvalidStartIsInvalidPart_mg1001__6);
        nameAllocator.clone();
        String o_characterMappingInvalidStartIsInvalidPart_mg1001_mg1075_mg1334__13 = nameAllocator.newName(__DSPOT_suggestion_198, __DSPOT_tag_199);
        Assert.assertEquals("G___aWA_ki__rufmpg_7", o_characterMappingInvalidStartIsInvalidPart_mg1001_mg1075_mg1334__13);
        Assert.assertEquals("_9_M4gfZk_Jpa_bR_2__M", o_characterMappingInvalidStartIsInvalidPart_mg1001__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg14019() throws Exception {
        Object __DSPOT_tag_3771 = new Object();
        String __DSPOT_suggestion_3770 = "D3}Rz]TdV5Vb-kY/h>aq";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg14019__6 = nameAllocator.newName(__DSPOT_suggestion_3770, __DSPOT_tag_3771);
        Assert.assertEquals("D3_Rz_TdV5Vb_kY_h_aq", o_javaKeyword_mg14019__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg14019_mg14114() throws Exception {
        String __DSPOT_suggestion_3775 = "$|jp:%#G}HPy[w-zXN[C";
        Object __DSPOT_tag_3771 = new Object();
        String __DSPOT_suggestion_3770 = "D3}Rz]TdV5Vb-kY/h>aq";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg14019__6 = nameAllocator.newName(__DSPOT_suggestion_3770, __DSPOT_tag_3771);
        Assert.assertEquals("D3_Rz_TdV5Vb_kY_h_aq", o_javaKeyword_mg14019__6);
        String o_javaKeyword_mg14019_mg14114__10 = nameAllocator.newName(__DSPOT_suggestion_3775);
        Assert.assertEquals("$_jp___G_HPy_w_zXN_C", o_javaKeyword_mg14019_mg14114__10);
        Assert.assertEquals("D3_Rz_TdV5Vb_kY_h_aq", o_javaKeyword_mg14019__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg14019litString14100_mg14226() throws Exception {
        Object __DSPOT_tag_3771 = new Object();
        String __DSPOT_suggestion_3770 = "b1F|8]+8-dO9;&E|X%&G";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg14019__6 = nameAllocator.newName(__DSPOT_suggestion_3770, __DSPOT_tag_3771);
        Assert.assertEquals("b1F_8__8_dO9__E_X__G", o_javaKeyword_mg14019__6);
        nameAllocator.clone();
        Assert.assertEquals("b1F_8__8_dO9__E_X__G", o_javaKeyword_mg14019__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg17256() throws Exception {
        Object __DSPOT_tag_4118 = new Object();
        String __DSPOT_suggestion_4117 = "mkgM$?pE+i=JSd[d9qOI";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg17256__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg17256__6);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg17256__11 = nameAllocator.newName(__DSPOT_suggestion_4117, __DSPOT_tag_4118);
        Assert.assertEquals("mkgM$_pE_i_JSd_d9qOI", o_tagReuseForbidden_mg17256__11);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg17256__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg17256_add18119() throws Exception {
        Object __DSPOT_tag_4118 = new Object();
        String __DSPOT_suggestion_4117 = "mkgM$?pE+i=JSd[d9qOI";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg17256__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg17256__6);
        try {
            nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg17256__11 = nameAllocator.newName(__DSPOT_suggestion_4117, __DSPOT_tag_4118);
        Assert.assertEquals("mkgM$_pE_i_JSd_d9qOI", o_tagReuseForbidden_mg17256__11);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg17256__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitNum17243_mg17936_mg22650() throws Exception {
        Object __DSPOT_tag_4809 = new Object();
        String __DSPOT_suggestion_4808 = "8KGn{iI@f]lvw< |(S9|";
        Object __DSPOT_tag_4126 = new Object();
        String __DSPOT_suggestion_4125 = "efj|qYhXd#ZQ:K[q^OJf";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitNum17243__3 = nameAllocator.newName("foo", Integer.MIN_VALUE);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum17243__3);
        try {
            String o_tagReuseForbiddenlitNum17243__6 = nameAllocator.newName("bar", 1);
            Assert.assertEquals("bar", o_tagReuseForbiddenlitNum17243__6);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbiddenlitNum17243_mg17936__15 = nameAllocator.newName(__DSPOT_suggestion_4125, __DSPOT_tag_4126);
        Assert.assertEquals("efj_qYhXd_ZQ_K_q_OJf", o_tagReuseForbiddenlitNum17243_mg17936__15);
        String o_tagReuseForbiddenlitNum17243_mg17936_mg22650__21 = nameAllocator.newName(__DSPOT_suggestion_4808, __DSPOT_tag_4809);
        Assert.assertEquals("_8KGn_iI_f_lvw____S9_", o_tagReuseForbiddenlitNum17243_mg17936_mg22650__21);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitNum17243__3);
        Assert.assertEquals("efj_qYhXd_ZQ_K_q_OJf", o_tagReuseForbiddenlitNum17243_mg17936__15);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg25251() throws Exception {
        Object __DSPOT_tag_5169 = new Object();
        String __DSPOT_suggestion_5168 = "].@If{@0+XaH0F0Q+f#n";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg25251__10 = nameAllocator.newName(__DSPOT_suggestion_5168, __DSPOT_tag_5169);
        Assert.assertEquals("___If__0_XaH0F0Q_f_n", o_useBeforeAllocateForbidden_mg25251__10);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg25251litNum25306() throws Exception {
        Object __DSPOT_tag_5169 = new Object();
        String __DSPOT_suggestion_5168 = "].@If{@0+XaH0F0Q+f#n";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(886008273);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg25251__10 = nameAllocator.newName(__DSPOT_suggestion_5168, __DSPOT_tag_5169);
        Assert.assertEquals("___If__0_XaH0F0Q_f_n", o_useBeforeAllocateForbidden_mg25251__10);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg25251_mg25316_mg25998() throws Exception {
        Object __DSPOT_tag_5329 = new Object();
        String __DSPOT_suggestion_5328 = "XMw46idR:,ARcP `4FzH";
        String __DSPOT_suggestion_5173 = "g*BU7g^s7De`n7SYcV%[";
        Object __DSPOT_tag_5169 = new Object();
        String __DSPOT_suggestion_5168 = "].@If{@0+XaH0F0Q+f#n";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg25251__10 = nameAllocator.newName(__DSPOT_suggestion_5168, __DSPOT_tag_5169);
        Assert.assertEquals("___If__0_XaH0F0Q_f_n", o_useBeforeAllocateForbidden_mg25251__10);
        String o_useBeforeAllocateForbidden_mg25251_mg25316__14 = nameAllocator.newName(__DSPOT_suggestion_5173);
        Assert.assertEquals("g_BU7g_s7De_n7SYcV__", o_useBeforeAllocateForbidden_mg25251_mg25316__14);
        String o_useBeforeAllocateForbidden_mg25251_mg25316_mg25998__20 = nameAllocator.newName(__DSPOT_suggestion_5328, __DSPOT_tag_5329);
        Assert.assertEquals("XMw46idR__ARcP__4FzH", o_useBeforeAllocateForbidden_mg25251_mg25316_mg25998__20);
        Assert.assertEquals("___If__0_XaH0F0Q_f_n", o_useBeforeAllocateForbidden_mg25251__10);
        Assert.assertEquals("g_BU7g_s7De_n7SYcV__", o_useBeforeAllocateForbidden_mg25251_mg25316__14);
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString3936() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString3936__3 = outterAllocator.newName("unknown tag: 1", 1);
        Assert.assertEquals("unknown_tag__1", o_cloneUsagelitString3936__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("unknown_tag__1", o_cloneUsagelitString3936__3);
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg4016litNum5620() throws Exception {
        Object __DSPOT_tag_467 = new Object();
        String __DSPOT_suggestion_466 = "or+0F]JZNr 8m{vtctU`";
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_mg4016__6 = outterAllocator.newName("foo", -696702683);
        Assert.assertEquals("foo", o_cloneUsage_mg4016__6);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_mg4016__11 = outterAllocator.newName(__DSPOT_suggestion_466, __DSPOT_tag_467);
        Assert.assertEquals("or_0F_JZNr_8m_vtctU_", o_cloneUsage_mg4016__11);
        Assert.assertEquals("foo", o_cloneUsage_mg4016__6);
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg4019_mg5158_mg12409() throws Exception {
        Object __DSPOT_tag_3295 = new Object();
        String __DSPOT_suggestion_3294 = "Rgre{!1 uMVy4Rv>_,{f";
        Object __DSPOT_tag_745 = new Object();
        String __DSPOT_suggestion_742 = "h%PLOA_rz0Q. #<8R4 h";
        String __DSPOT_suggestion_469 = "ls({S&n-6[>6Jncm%^0X";
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_mg4019__4 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_mg4019__4);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_mg4019__9 = innerAllocator1.newName(__DSPOT_suggestion_469);
        Assert.assertEquals("ls__S_n_6__6Jncm__0X", o_cloneUsage_mg4019__9);
        String o_cloneUsage_mg4019_mg5158__17 = outterAllocator.newName(__DSPOT_suggestion_742, __DSPOT_tag_745);
        Assert.assertEquals("h_PLOA_rz0Q____8R4_h", o_cloneUsage_mg4019_mg5158__17);
        String o_cloneUsage_mg4019_mg5158_mg12409__23 = innerAllocator2.newName(__DSPOT_suggestion_3294, __DSPOT_tag_3295);
        Assert.assertEquals("Rgre__1_uMVy4Rv____f", o_cloneUsage_mg4019_mg5158_mg12409__23);
        Assert.assertEquals("foo", o_cloneUsage_mg4019__4);
        Assert.assertEquals("ls__S_n_6__6Jncm__0X", o_cloneUsage_mg4019__9);
        Assert.assertEquals("h_PLOA_rz0Q____8R4_h", o_cloneUsage_mg4019_mg5158__17);
    }
}

