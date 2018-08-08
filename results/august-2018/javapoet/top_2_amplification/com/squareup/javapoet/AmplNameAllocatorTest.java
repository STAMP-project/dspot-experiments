package com.squareup.javapoet;


import org.junit.Assert;
import org.junit.Test;


public final class AmplNameAllocatorTest {
    @Test(timeout = 10000)
    public void usage_mg24211() throws Exception {
        Object __DSPOT_tag_5048 = new Object();
        String __DSPOT_suggestion_5047 = "s8{@Iys|72%T&7etJ&{+";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg24211__6 = nameAllocator.newName(__DSPOT_suggestion_5047, __DSPOT_tag_5048);
        Assert.assertEquals("s8__Iys_72_T_7etJ___", o_usage_mg24211__6);
    }

    @Test(timeout = 10000)
    public void usage_mg24211_add24365() throws Exception {
        Object __DSPOT_tag_5048 = new Object();
        String __DSPOT_suggestion_5047 = "s8{@Iys|72%T&7etJ&{+";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg24211__6 = nameAllocator.newName(__DSPOT_suggestion_5047, __DSPOT_tag_5048);
        Assert.assertEquals("s8__Iys_72_T_7etJ___", o_usage_mg24211__6);
    }

    @Test(timeout = 10000)
    public void usage_mg24210_mg24373litString24476() throws Exception {
        Object __DSPOT_tag_5054 = new Object();
        String __DSPOT_suggestion_5053 = ":";
        String __DSPOT_suggestion_5046 = "^&Tfyv]=:KLSh#y;:X9(";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_mg24210__4 = nameAllocator.newName(__DSPOT_suggestion_5046);
        Assert.assertEquals("__Tfyv___KLSh_y__X9_", o_usage_mg24210__4);
        String o_usage_mg24210_mg24373__10 = nameAllocator.newName(__DSPOT_suggestion_5053, __DSPOT_tag_5054);
        Assert.assertEquals("_", o_usage_mg24210_mg24373__10);
        Assert.assertEquals("__Tfyv___KLSh_y__X9_", o_usage_mg24210__4);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg15046() throws Exception {
        String __DSPOT_suggestion_3879 = "]d$rzM>t?^ZBRVlx.k5<";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg15046__4 = nameAllocator.newName(__DSPOT_suggestion_3879);
        Assert.assertEquals("_d$rzM_t__ZBRVlx_k5_", o_nameCollision_mg15046__4);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg15046litString15148() throws Exception {
        String __DSPOT_suggestion_3879 = "]d$rM>t?^ZBRVlx.k5<";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg15046__4 = nameAllocator.newName(__DSPOT_suggestion_3879);
        Assert.assertEquals("_d$rM_t__ZBRVlx_k5_", o_nameCollision_mg15046__4);
    }

    @Test(timeout = 10000)
    public void nameCollision_mg15046_mg15165litString15351() throws Exception {
        Object __DSPOT_tag_3889 = new Object();
        String __DSPOT_suggestion_3888 = "ac#Tp!<8T%kA6I$HH.Kt";
        String __DSPOT_suggestion_3879 = "]d$rzM>t?^ZBRVlx.5<";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_mg15046__4 = nameAllocator.newName(__DSPOT_suggestion_3879);
        Assert.assertEquals("_d$rzM_t__ZBRVlx_5_", o_nameCollision_mg15046__4);
        String o_nameCollision_mg15046_mg15165__10 = nameAllocator.newName(__DSPOT_suggestion_3888, __DSPOT_tag_3889);
        Assert.assertEquals("ac_Tp__8T_kA6I$HH_Kt", o_nameCollision_mg15046_mg15165__10);
        Assert.assertEquals("_d$rzM_t__ZBRVlx_5_", o_nameCollision_mg15046__4);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg16120() throws Exception {
        Object __DSPOT_tag_3997 = new Object();
        String __DSPOT_suggestion_3996 = "$^@8D(+@i[@C&Imc$Jjn";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_mg16120__6 = nameAllocator.newName(__DSPOT_suggestion_3996, __DSPOT_tag_3997);
        Assert.assertEquals("$__8D___i__C_Imc$Jjn", o_nameCollisionWithTag_mg16120__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg16120_add16340() throws Exception {
        Object __DSPOT_tag_3997 = new Object();
        String __DSPOT_suggestion_3996 = "$^@8D(+@i[@C&Imc$Jjn";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_mg16120__6 = nameAllocator.newName(__DSPOT_suggestion_3996, __DSPOT_tag_3997);
        Assert.assertEquals("$__8D___i__C_Imc$Jjn", o_nameCollisionWithTag_mg16120__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_mg16119_mg16348litString16523() throws Exception {
        Object __DSPOT_tag_4003 = new Object();
        String __DSPOT_suggestion_4002 = "\n";
        String __DSPOT_suggestion_3995 = "R!vZl:A9LM6]>zK`1GmW";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_mg16119__4 = nameAllocator.newName(__DSPOT_suggestion_3995);
        Assert.assertEquals("R_vZl_A9LM6__zK_1GmW", o_nameCollisionWithTag_mg16119__4);
        String o_nameCollisionWithTag_mg16119_mg16348__10 = nameAllocator.newName(__DSPOT_suggestion_4002, __DSPOT_tag_4003);
        Assert.assertEquals("_", o_nameCollisionWithTag_mg16119_mg16348__10);
        Assert.assertEquals("R_vZl_A9LM6__zK_1GmW", o_nameCollisionWithTag_mg16119__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg1986() throws Exception {
        Object __DSPOT_tag_233 = new Object();
        String __DSPOT_suggestion_232 = "4GTC}l]qvelpPW`h79`w";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg1986__6 = nameAllocator.newName(__DSPOT_suggestion_232, __DSPOT_tag_233);
        Assert.assertEquals("_4GTC_l_qvelpPW_h79_w", o_characterMappingSubstitute_mg1986__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg1986_mg2059() throws Exception {
        Object __DSPOT_tag_233 = new Object();
        String __DSPOT_suggestion_232 = "4GTC}l]qvelpPW`h79`w";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg1986__6 = nameAllocator.newName(__DSPOT_suggestion_232, __DSPOT_tag_233);
        Assert.assertEquals("_4GTC_l_qvelpPW_h79_w", o_characterMappingSubstitute_mg1986__6);
        nameAllocator.clone();
        Assert.assertEquals("_4GTC_l_qvelpPW_h79_w", o_characterMappingSubstitute_mg1986__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_mg1985_mg2064litString2177() throws Exception {
        Object __DSPOT_tag_239 = new Object();
        String __DSPOT_suggestion_238 = "\n";
        String __DSPOT_suggestion_231 = "iJy:v}SV5HR!Y({UBa;x";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_mg1985__4 = nameAllocator.newName(__DSPOT_suggestion_231);
        Assert.assertEquals("iJy_v_SV5HR_Y__UBa_x", o_characterMappingSubstitute_mg1985__4);
        String o_characterMappingSubstitute_mg1985_mg2064__10 = nameAllocator.newName(__DSPOT_suggestion_238, __DSPOT_tag_239);
        Assert.assertEquals("_", o_characterMappingSubstitute_mg1985_mg2064__10);
        Assert.assertEquals("iJy_v_SV5HR_Y__UBa_x", o_characterMappingSubstitute_mg1985__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg2970() throws Exception {
        Object __DSPOT_tag_349 = new Object();
        String __DSPOT_suggestion_348 = "`n#S4@x6*]1I5%}^%8%X";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg2970__6 = nameAllocator.newName(__DSPOT_suggestion_348, __DSPOT_tag_349);
        Assert.assertEquals("_n_S4_x6__1I5____8_X", o_characterMappingSurrogate_mg2970__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg2970_mg3043() throws Exception {
        Object __DSPOT_tag_349 = new Object();
        String __DSPOT_suggestion_348 = "`n#S4@x6*]1I5%}^%8%X";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg2970__6 = nameAllocator.newName(__DSPOT_suggestion_348, __DSPOT_tag_349);
        Assert.assertEquals("_n_S4_x6__1I5____8_X", o_characterMappingSurrogate_mg2970__6);
        nameAllocator.clone();
        Assert.assertEquals("_n_S4_x6__1I5____8_X", o_characterMappingSurrogate_mg2970__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_mg2970litString3037_mg3198() throws Exception {
        String __DSPOT_suggestion_375 = "&uM#lT(R@N-/+Q;*6-*}";
        Object __DSPOT_tag_349 = new Object();
        String __DSPOT_suggestion_348 = "\n";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_mg2970__6 = nameAllocator.newName(__DSPOT_suggestion_348, __DSPOT_tag_349);
        Assert.assertEquals("_", o_characterMappingSurrogate_mg2970__6);
        String o_characterMappingSurrogate_mg2970litString3037_mg3198__10 = nameAllocator.newName(__DSPOT_suggestion_375);
        Assert.assertEquals("_uM_lT_R_N___Q__6___", o_characterMappingSurrogate_mg2970litString3037_mg3198__10);
        Assert.assertEquals("_", o_characterMappingSurrogate_mg2970__6);
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
    public void characterMappingInvalidStartButValidPart_mg18_add87() throws Exception {
        Object __DSPOT_tag_3 = new Object();
        String __DSPOT_suggestion_2 = "L[{$QV5:Wz2[|+mr6#-V";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_mg18__6 = nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
        Assert.assertEquals("L__$QV5_Wz2___mr6__V", o_characterMappingInvalidStartButValidPart_mg18__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_mg17_mg95litString327() throws Exception {
        Object __DSPOT_tag_9 = new Object();
        String __DSPOT_suggestion_8 = "%h1,xavU[1Rvnj|}8wu]";
        String __DSPOT_suggestion_1 = "CS@!x*zb_,y(q2 5[gpb";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_mg17__4 = nameAllocator.newName(__DSPOT_suggestion_1);
        Assert.assertEquals("CS__x_zb__y_q2_5_gpb", o_characterMappingInvalidStartButValidPart_mg17__4);
        String o_characterMappingInvalidStartButValidPart_mg17_mg95__10 = nameAllocator.newName(__DSPOT_suggestion_8, __DSPOT_tag_9);
        Assert.assertEquals("_h1_xavU_1Rvnj__8wu_", o_characterMappingInvalidStartButValidPart_mg17_mg95__10);
        Assert.assertEquals("CS__x_zb__y_q2_5_gpb", o_characterMappingInvalidStartButValidPart_mg17__4);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1002() throws Exception {
        Object __DSPOT_tag_118 = new Object();
        String __DSPOT_suggestion_117 = "9)M4gfZk]Jpa[bR-2-=M";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_mg1002__6 = nameAllocator.newName(__DSPOT_suggestion_117, __DSPOT_tag_118);
        Assert.assertEquals("_9_M4gfZk_Jpa_bR_2__M", o_characterMappingInvalidStartIsInvalidPart_mg1002__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1002litString1061() throws Exception {
        Object __DSPOT_tag_118 = new Object();
        String __DSPOT_suggestion_117 = "9)M4gfZk]Jpa+[bR-2-=M";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_mg1002__6 = nameAllocator.newName(__DSPOT_suggestion_117, __DSPOT_tag_118);
        Assert.assertEquals("_9_M4gfZk_Jpa__bR_2__M", o_characterMappingInvalidStartIsInvalidPart_mg1002__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_mg1001_mg1080litString1352() throws Exception {
        Object __DSPOT_tag_124 = new Object();
        String __DSPOT_suggestion_123 = "FoAgu-u1_)f(dMmVFwF=";
        String __DSPOT_suggestion_116 = "(j*>vj<@X]YP!2!1tKs!";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_mg1001__4 = nameAllocator.newName(__DSPOT_suggestion_116);
        Assert.assertEquals("_j__vj__X_YP_2_1tKs_", o_characterMappingInvalidStartIsInvalidPart_mg1001__4);
        String o_characterMappingInvalidStartIsInvalidPart_mg1001_mg1080__10 = nameAllocator.newName(__DSPOT_suggestion_123, __DSPOT_tag_124);
        Assert.assertEquals("FoAgu_u1__f_dMmVFwF_", o_characterMappingInvalidStartIsInvalidPart_mg1001_mg1080__10);
        Assert.assertEquals("_j__vj__X_YP_2_1tKs_", o_characterMappingInvalidStartIsInvalidPart_mg1001__4);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg14032() throws Exception {
        Object __DSPOT_tag_3765 = new Object();
        String __DSPOT_suggestion_3764 = "aq$TQR<ZmpJT xtb1F|8";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg14032__6 = nameAllocator.newName(__DSPOT_suggestion_3764, __DSPOT_tag_3765);
        Assert.assertEquals("aq$TQR_ZmpJT_xtb1F_8", o_javaKeyword_mg14032__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg14032_add14120() throws Exception {
        Object __DSPOT_tag_3765 = new Object();
        String __DSPOT_suggestion_3764 = "aq$TQR<ZmpJT xtb1F|8";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg14032__6 = nameAllocator.newName(__DSPOT_suggestion_3764, __DSPOT_tag_3765);
        Assert.assertEquals("aq$TQR_ZmpJT_xtb1F_8", o_javaKeyword_mg14032__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_mg14031_mg14128litString14399() throws Exception {
        Object __DSPOT_tag_3771 = new Object();
        String __DSPOT_suggestion_3770 = "[d2#BuH``AKvJ$e=t%!b";
        String __DSPOT_suggestion_3763 = "";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_mg14031__4 = nameAllocator.newName(__DSPOT_suggestion_3763);
        Assert.assertEquals("", o_javaKeyword_mg14031__4);
        String o_javaKeyword_mg14031_mg14128__10 = nameAllocator.newName(__DSPOT_suggestion_3770, __DSPOT_tag_3771);
        Assert.assertEquals("_d2_BuH__AKvJ$e_t__b", o_javaKeyword_mg14031_mg14128__10);
        Assert.assertEquals("", o_javaKeyword_mg14031__4);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg17268() throws Exception {
        Object __DSPOT_tag_4113 = new Object();
        String __DSPOT_suggestion_4112 = "d9qOIu F0]<2^}Nan#,;";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg17268__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg17268__6);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg17268__11 = nameAllocator.newName(__DSPOT_suggestion_4112, __DSPOT_tag_4113);
        Assert.assertEquals("d9qOIu_F0__2__Nan___", o_tagReuseForbidden_mg17268__11);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg17268__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg17267litString17689() throws Exception {
        String __DSPOT_suggestion_4111 = "r6&Q4mkgM$?pE+i=JSd[";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg17267__4 = nameAllocator.newName("rdg", 1);
        Assert.assertEquals("rdg", o_tagReuseForbidden_mg17267__4);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg17267__9 = nameAllocator.newName(__DSPOT_suggestion_4111);
        Assert.assertEquals("r6_Q4mkgM$_pE_i_JSd_", o_tagReuseForbidden_mg17267__9);
        Assert.assertEquals("rdg", o_tagReuseForbidden_mg17267__4);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_mg17268_mg18156litString22442() throws Exception {
        Object __DSPOT_tag_4237 = new Object();
        String __DSPOT_suggestion_4236 = "&YI[ZtCe`@of.&v${WMY";
        Object __DSPOT_tag_4113 = new Object();
        String __DSPOT_suggestion_4112 = "d9qOIu F0]<2^}Nan#,;";
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_mg17268__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg17268__6);
        try {
            nameAllocator.newName("Oou", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_mg17268__11 = nameAllocator.newName(__DSPOT_suggestion_4112, __DSPOT_tag_4113);
        Assert.assertEquals("d9qOIu_F0__2__Nan___", o_tagReuseForbidden_mg17268__11);
        String o_tagReuseForbidden_mg17268_mg18156__19 = nameAllocator.newName(__DSPOT_suggestion_4236, __DSPOT_tag_4237);
        Assert.assertEquals("_YI_ZtCe__of__v$_WMY", o_tagReuseForbidden_mg17268_mg18156__19);
        Assert.assertEquals("foo", o_tagReuseForbidden_mg17268__6);
        Assert.assertEquals("d9qOIu_F0__2__Nan___", o_tagReuseForbidden_mg17268__11);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg25270() throws Exception {
        Object __DSPOT_tag_5164 = new Object();
        String __DSPOT_suggestion_5163 = "R_HcB(>vvzc%{K|2?qQT";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg25270__10 = nameAllocator.newName(__DSPOT_suggestion_5163, __DSPOT_tag_5164);
        Assert.assertEquals("R_HcB__vvzc__K_2_qQT", o_useBeforeAllocateForbidden_mg25270__10);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg25270_add25326() throws Exception {
        Object __DSPOT_tag_5164 = new Object();
        String __DSPOT_suggestion_5163 = "R_HcB(>vvzc%{K|2?qQT";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg25270__10 = nameAllocator.newName(__DSPOT_suggestion_5163, __DSPOT_tag_5164);
        Assert.assertEquals("R_HcB__vvzc__K_2_qQT", o_useBeforeAllocateForbidden_mg25270__10);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_mg25269_mg25337litString25769() throws Exception {
        Object __DSPOT_tag_5172 = new Object();
        String __DSPOT_suggestion_5171 = "t3yT>p|G,u`,<-mC+LE(";
        String __DSPOT_suggestion_5162 = "a_b";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_mg25269__8 = nameAllocator.newName(__DSPOT_suggestion_5162);
        Assert.assertEquals("a_b", o_useBeforeAllocateForbidden_mg25269__8);
        String o_useBeforeAllocateForbidden_mg25269_mg25337__14 = nameAllocator.newName(__DSPOT_suggestion_5171, __DSPOT_tag_5172);
        Assert.assertEquals("t3yT_p_G_u____mC_LE_", o_useBeforeAllocateForbidden_mg25269_mg25337__14);
        Assert.assertEquals("a_b", o_useBeforeAllocateForbidden_mg25269__8);
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString3937() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString3937__3 = outterAllocator.newName("unknown tag: 1", 1);
        Assert.assertEquals("unknown_tag__1", o_cloneUsagelitString3937__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("unknown_tag__1", o_cloneUsagelitString3937__3);
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg4017litNum5688() throws Exception {
        Object __DSPOT_tag_465 = new Object();
        String __DSPOT_suggestion_464 = "or+0F]JZNr 8m{vtctU`";
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_mg4017__6 = outterAllocator.newName("foo", -654261252);
        Assert.assertEquals("foo", o_cloneUsage_mg4017__6);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_mg4017__11 = outterAllocator.newName(__DSPOT_suggestion_464, __DSPOT_tag_465);
        Assert.assertEquals("or_0F_JZNr_8m_vtctU_", o_cloneUsage_mg4017__11);
        Assert.assertEquals("foo", o_cloneUsage_mg4017__6);
    }

    @Test(timeout = 10000)
    public void cloneUsage_mg4025_mg5146_mg13394() throws Exception {
        Object __DSPOT_tag_3723 = new Object();
        String __DSPOT_suggestion_3721 = "(lL!a&6&oY!lmLhM|@{i";
        Object __DSPOT_tag_747 = new Object();
        String __DSPOT_suggestion_746 = "(*4Grb2{]m,=py20Fs4d";
        Object __DSPOT_tag_473 = new Object();
        String __DSPOT_suggestion_472 = "`f6YlYxeR|f3mBUdZo;E";
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_mg4025__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_mg4025__6);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_mg4025__11 = innerAllocator2.newName(__DSPOT_suggestion_472, __DSPOT_tag_473);
        Assert.assertEquals("_f6YlYxeR_f3mBUdZo_E", o_cloneUsage_mg4025__11);
        String o_cloneUsage_mg4025_mg5146__19 = outterAllocator.newName(__DSPOT_suggestion_746, __DSPOT_tag_747);
        Assert.assertEquals("__4Grb2__m__py20Fs4d", o_cloneUsage_mg4025_mg5146__19);
        String o_cloneUsage_mg4025_mg5146_mg13394__25 = innerAllocator1.newName(__DSPOT_suggestion_3721, __DSPOT_tag_3723);
        Assert.assertEquals("_lL_a_6_oY_lmLhM___i", o_cloneUsage_mg4025_mg5146_mg13394__25);
        Assert.assertEquals("foo", o_cloneUsage_mg4025__6);
        Assert.assertEquals("_f6YlYxeR_f3mBUdZo_E", o_cloneUsage_mg4025__11);
        Assert.assertEquals("__4Grb2__m__py20Fs4d", o_cloneUsage_mg4025_mg5146__19);
    }
}

