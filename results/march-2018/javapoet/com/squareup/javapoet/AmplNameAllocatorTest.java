package com.squareup.javapoet;


import org.junit.Assert;
import org.junit.Test;


public final class AmplNameAllocatorTest {
    @Test(timeout = 50000)
    public void tagReuseForbidden() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd44_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_0 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_sd44 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd46() throws Exception {
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", new NameAllocator().newName("X(r!Fs2l>UgIvC=TU&zg", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_add48() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", 1));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd45() throws Exception {
        Assert.assertEquals("__$QV5_Wz2___mr6__Vt", new NameAllocator().newName("[{$QV5:Wz2[|+mr6#-Vt"));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd46_add248_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_3 = new Object();
            String __DSPOT_suggestion_2 = "X(r!Fs2l>UgIvC=TU&zg";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
            String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_sd46_add248 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd45litString210() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a\ud83c\udf7ab"));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd46_sd245_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_8 = new Object();
            Object __DSPOT_tag_3 = new Object();
            String __DSPOT_suggestion_2 = "X(r!Fs2l>UgIvC=TU&zg";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_sd46_sd245 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd45_add223() throws Exception {
        String __DSPOT_suggestion_1 = "[{$QV5:Wz2[|+mr6#-Vt";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd45_add223__4 = nameAllocator.newName(__DSPOT_suggestion_1);
        Assert.assertEquals("__$QV5_Wz2___mr6__Vt", o_characterMappingInvalidStartButValidPart_sd45_add223__4);
        Assert.assertEquals("__$QV5_Wz2___mr6__Vt_", nameAllocator.newName(__DSPOT_suggestion_1));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd46litString239() throws Exception {
        Assert.assertEquals("_3a____b__I", new NameAllocator().newName("3a(!.#b{[I", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd45litString208() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a_b"));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd46litString232() throws Exception {
        Assert.assertEquals("unknown_tag__1", new NameAllocator().newName("unknown tag: 1", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd45litString204() throws Exception {
        Assert.assertEquals("__$QV5_Wz2_K_mr6__Vt", new NameAllocator().newName("[{$QV5:Wz2[K+mr6#-Vt"));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd46litString231() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a_b", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd45litString213() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd46_sd246_add2070() throws Exception {
        String __DSPOT_suggestion_9 = ">YSe|%xHdm7#=ToX)D7x";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
        String o_characterMappingInvalidStartButValidPart_sd46_sd246_add2070__11 = nameAllocator.newName(__DSPOT_suggestion_9);
        Assert.assertEquals("_YSe__xHdm7__ToX_D7x", o_characterMappingInvalidStartButValidPart_sd46_sd246_add2070__11);
        Assert.assertEquals("_YSe__xHdm7__ToX_D7x_", nameAllocator.newName(__DSPOT_suggestion_9));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd46_sd247_add2116_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_11 = new Object();
            String __DSPOT_suggestion_10 = ">[Bob5_83OI`-k-a8(J8";
            Object __DSPOT_tag_3 = new Object();
            String __DSPOT_suggestion_2 = "X(r!Fs2l>UgIvC=TU&zg";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
            nameAllocator.newName(__DSPOT_suggestion_10, __DSPOT_tag_11);
            String o_characterMappingInvalidStartButValidPart_sd46_sd247__13 = nameAllocator.newName(__DSPOT_suggestion_10, __DSPOT_tag_11);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_sd46_sd247_add2116 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd46_sd244_sd2021() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("__h__LLuBuAr_pT_vt_F", nameAllocator.clone().newName("/#h+}LLuBuAr^pT%vt_F", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd46_sd247litString2102() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("tag 1 cannot be used for both 'foo' and 'bar'", new Object());
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("__Bob5_83OI__k_a8_J8", nameAllocator.newName(">[Bob5_83OI`-k-a8(J8", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd46_sd246litString2035() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("public_", nameAllocator.newName("public"));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd46_sd245_failAssert1_sd3061() throws Exception {
        try {
            int __DSPOT_arg3_502 = -1366138306;
            byte[] __DSPOT_arg2_501 = new byte[]{ 34, -97, 112 };
            int __DSPOT_arg1_500 = 281226386;
            int __DSPOT_arg0_499 = 722414354;
            Object __DSPOT_tag_8 = new Object();
            Object __DSPOT_tag_3 = new Object();
            String __DSPOT_suggestion_2 = "X(r!Fs2l>UgIvC=TU&zg";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
            Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_sd46_sd245 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).getBytes(722414354, 281226386, new byte[]{ 34, -97, 112 }, -1366138306);
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartButValidPart_sd46_sd247litString2103() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("1ab", new Object());
        Assert.assertEquals("_1ab", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("__Bob5_83OI__k_a8_J8", nameAllocator.newName(">[Bob5_83OI`-k-a8(J8", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3987_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_758 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3987 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3988() throws Exception {
        Assert.assertEquals("E0YV_yXN_hMWO_xm_39_", new NameAllocator().newName("E0YV;yXN`hMWO=xm:39*"));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_add3991() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab", 1));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989() throws Exception {
        Assert.assertEquals("RU_4Ci__C__KA__P_SF_", new NameAllocator().newName("RU(4Ci|&C!/KA@(P(SF)", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3988litString4146() throws Exception {
        Assert.assertEquals("E0YV_yXN_hGWO_xm_39_", new NameAllocator().newName("E0YV;yXN`hGWO=xm:39*"));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_add3991litString4207() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", 1));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3988_add4166() throws Exception {
        String __DSPOT_suggestion_759 = "E0YV;yXN`hMWO=xm:39*";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3988_add4166__4 = nameAllocator.newName(__DSPOT_suggestion_759);
        Assert.assertEquals("E0YV_yXN_hMWO_xm_39_", o_characterMappingInvalidStartIsInvalidPart_sd3988_add4166__4);
        Assert.assertEquals("E0YV_yXN_hMWO_xm_39__", nameAllocator.newName(__DSPOT_suggestion_759));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989_add4191_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "RU(4Ci|&C!/KA@(P(SF)";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            String o_characterMappingInvalidStartIsInvalidPart_sd3989__6 = nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3989_add4191 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989_sd4190() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3989__6 = nameAllocator.newName("RU(4Ci|&C!/KA@(P(SF)", new Object());
        Assert.assertEquals("RU_4Ci__C__KA__P_SF_", o_characterMappingInvalidStartIsInvalidPart_sd3989__6);
        Assert.assertEquals("___kbpBO_TDkxVK__S__", nameAllocator.newName("]&.kbpBO=TDkxVK:+S:`", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989litString4183() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3988_sd4163_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_762 = new Object();
            String __DSPOT_suggestion_759 = "E0YV;yXN`hMWO=xm:39*";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_sd3988__4 = nameAllocator.newName("E0YV;yXN`hMWO=xm:39*");
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3988_sd4163 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3988litString4154() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab"));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989_sd4189litString6001() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3989__6 = nameAllocator.newName("&ab", new Object());
        Assert.assertEquals("_ab", o_characterMappingInvalidStartIsInvalidPart_sd3989__6);
        Assert.assertEquals("_0__zDDSlq__6b_7b_F0_", nameAllocator.newName("0[|zDDSlq};6b)7b[F0!"));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989_sd4190litString6029() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3989__6 = nameAllocator.newName("RU(4Ci|&C!/KA@(P(SF)", new Object());
        Assert.assertEquals("RU_4Ci__C__KA__P_SF_", o_characterMappingInvalidStartIsInvalidPart_sd3989__6);
        Assert.assertEquals("public_", nameAllocator.newName("public", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989_sd4189litString5974() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3989__6 = nameAllocator.newName("RU(4Ci|&C!/KA@(P(SF)", new Object());
        Assert.assertEquals("RU_4Ci__C__KA__P_SF_", o_characterMappingInvalidStartIsInvalidPart_sd3989__6);
        Assert.assertEquals("_0__zDDSlq__6r_7b_F0_", nameAllocator.newName("0[|zDDSlq};6r)7b[F0!"));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989_sd4189_add6008_failAssert6() throws Exception {
        try {
            String __DSPOT_suggestion_767 = "0[|zDDSlq};6b)7b[F0!";
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "RU(4Ci|&C!/KA@(P(SF)";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            String o_characterMappingInvalidStartIsInvalidPart_sd3989__6 = nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            String o_characterMappingInvalidStartIsInvalidPart_sd3989_sd4189__11 = nameAllocator.newName("0[|zDDSlq};6b)7b[F0!");
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3989_sd4189_add6008 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989_sd4188_failAssert1_sd7000() throws Exception {
        try {
            int __DSPOT_arg3_1260 = -1228442999;
            byte[] __DSPOT_arg2_1259 = new byte[]{ 119, 9, -110, 4 };
            int __DSPOT_arg1_1258 = -1975797263;
            int __DSPOT_arg0_1257 = -519500431;
            Object __DSPOT_tag_766 = new Object();
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "RU(4Ci|&C!/KA@(P(SF)";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_sd3989__6 = nameAllocator.newName("RU(4Ci|&C!/KA@(P(SF)", new Object());
            Assert.assertEquals("RU_4Ci__C__KA__P_SF_", nameAllocator.newName("RU(4Ci|&C!/KA@(P(SF)", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3989_sd4188 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).getBytes(-519500431, -1975797263, new byte[]{ 119, 9, -110, 4 }, -1228442999);
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3988_sd4165_add5353() throws Exception {
        String __DSPOT_suggestion_759 = "E0YV;yXN`hMWO=xm:39*";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3988_sd4165_add5353__7 = nameAllocator.newName(__DSPOT_suggestion_759);
        Assert.assertEquals("E0YV_yXN_hMWO_xm_39_", o_characterMappingInvalidStartIsInvalidPart_sd3988_sd4165_add5353__7);
        String o_characterMappingInvalidStartIsInvalidPart_sd3988__4 = nameAllocator.newName(__DSPOT_suggestion_759);
        Assert.assertEquals("E0YV_yXN_hMWO_xm_39__", o_characterMappingInvalidStartIsInvalidPart_sd3988__4);
        Assert.assertEquals("_v__EOShL$yz8yrwa_pG", nameAllocator.newName(")v;-EOShL$yz8yrwa/pG", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989_sd4187_sd5960() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3989__6 = nameAllocator.newName("RU(4Ci|&C!/KA@(P(SF)", new Object());
        Assert.assertEquals("RU_4Ci__C__KA__P_SF_", o_characterMappingInvalidStartIsInvalidPart_sd3989__6);
        Assert.assertEquals("__SD_dOAP_e__sOtGl2b", nameAllocator.clone().newName("/]SD=dOAP^e>*sOtGl2b", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7928() throws Exception {
        Assert.assertEquals("y1__cBv9F_d_C__mA___", new NameAllocator().newName("y1=+cBv9F#d.C@!mA *{"));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929() throws Exception {
        Assert.assertEquals("dN__X_uy7_iOt3b_uB_p", new NameAllocator().newName("dN}*X{uy7[iOt3b@uB.p", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7927_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_1516 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("characterMappingSubstitute_sd7927 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929litString8113() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a_b", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929_sd8128_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_1524 = new Object();
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "dN}*X{uy7[iOt3b@uB.p";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_sd7929__6 = nameAllocator.newName("dN}*X{uy7[iOt3b@uB.p", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSubstitute_sd7929_sd8128 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929_sd8127() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7929__6 = nameAllocator.newName("dN}*X{uy7[iOt3b@uB.p", new Object());
        Assert.assertEquals("dN__X_uy7_iOt3b_uB_p", o_characterMappingSubstitute_sd7929__6);
        nameAllocator.clone();
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7928_add8106() throws Exception {
        String __DSPOT_suggestion_1517 = "y1=+cBv9F#d.C@!mA *{";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7928_add8106__4 = nameAllocator.newName(__DSPOT_suggestion_1517);
        Assert.assertEquals("y1__cBv9F_d_C__mA___", o_characterMappingSubstitute_sd7928_add8106__4);
        Assert.assertEquals("y1__cBv9F_d_C__mA____", nameAllocator.newName(__DSPOT_suggestion_1517));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7928litString8099() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab"));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7928litString8089() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7928litString8101() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929_add8131_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "dN}*X{uy7[iOt3b@uB.p";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            String o_characterMappingSubstitute_sd7929__6 = nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            org.junit.Assert.fail("characterMappingSubstitute_sd7929_add8131 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929litString8121() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929litString8120() throws Exception {
        Assert.assertEquals("_7Ygs_d___k", new NameAllocator().newName("7Ygs|d/=?k", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929_sd8129_add9952() throws Exception {
        String __DSPOT_suggestion_1525 = "+f1NY?Z5:6h@`V(5mdsi";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7929__6 = nameAllocator.newName("dN}*X{uy7[iOt3b@uB.p", new Object());
        Assert.assertEquals("dN__X_uy7_iOt3b_uB_p", o_characterMappingSubstitute_sd7929__6);
        String o_characterMappingSubstitute_sd7929_sd8129_add9952__11 = nameAllocator.newName(__DSPOT_suggestion_1525);
        Assert.assertEquals("_f1NY_Z5_6h__V_5mdsi", o_characterMappingSubstitute_sd7929_sd8129_add9952__11);
        Assert.assertEquals("_f1NY_Z5_6h__V_5mdsi_", nameAllocator.newName(__DSPOT_suggestion_1525));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929_sd8129litString9941() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7929__6 = nameAllocator.newName("&ab", new Object());
        Assert.assertEquals("_ab", o_characterMappingSubstitute_sd7929__6);
        Assert.assertEquals("_f1NY_Z5_6h__V_5mdsi", nameAllocator.newName("+f1NY?Z5:6h@`V(5mdsi"));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929_sd8127_sd9903() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7929__6 = nameAllocator.newName("dN}*X{uy7[iOt3b@uB.p", new Object());
        Assert.assertEquals("dN__X_uy7_iOt3b_uB_p", o_characterMappingSubstitute_sd7929__6);
        Assert.assertEquals("_1t_X4jiG_M_I9hALQhZC", nameAllocator.clone().newName("1t&X4jiG&M[I9hALQhZC", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929_sd8130litString9991() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7929__6 = nameAllocator.newName("_ab", new Object());
        Assert.assertEquals("_ab", o_characterMappingSubstitute_sd7929__6);
        Assert.assertEquals("p___OJ05_7_N21___165", nameAllocator.newName("p!%;OJ05#7=N21-))165", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7928_sd8105litString9285() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7928__4 = nameAllocator.newName("y1=+cBv9m#d.C@!mA *{");
        Assert.assertEquals("y1__cBv9m_d_C__mA___", o_characterMappingSubstitute_sd7928__4);
        Assert.assertEquals("H___l6rhN4zbQ_R1A__C", nameAllocator.newName("H#<.l6rhN4zbQ-R1A:*C", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929_sd8128_failAssert1_sd10927() throws Exception {
        try {
            Object __DSPOT_tag_2001 = new Object();
            String __DSPOT_suggestion_2000 = ":qtC&k,,H-bfx,_aLYpQ";
            Object __DSPOT_tag_1524 = new Object();
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "dN}*X{uy7[iOt3b@uB.p";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_sd7929__6 = nameAllocator.newName("dN}*X{uy7[iOt3b@uB.p", new Object());
            Assert.assertEquals("dN__X_uy7_iOt3b_uB_p", nameAllocator.newName("dN}*X{uy7[iOt3b@uB.p", new Object()));
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSubstitute_sd7929_sd8128 should have thrown IllegalArgumentException");
            nameAllocator.newName(":qtC&k,,H-bfx,_aLYpQ", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929_sd8130litString9988() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7929__6 = nameAllocator.newName("public", new Object());
        Assert.assertEquals("public_", o_characterMappingSubstitute_sd7929__6);
        Assert.assertEquals("p___OJ05_7_N21___165", nameAllocator.newName("p!%;OJ05#7=N21-))165", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSubstitute_sd7929_add8131_failAssert2_sd11003() throws Exception {
        try {
            Object __DSPOT_tag_2070 = new Object();
            String __DSPOT_suggestion_2069 = ">l;)Y[glgf6Qb=^Ki.3[";
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "dN}*X{uy7[iOt3b@uB.p";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_sd7929_add8131_failAssert2_sd11003__11 = nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            Assert.assertEquals("dN__X_uy7_iOt3b_uB_p", nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519));
            String o_characterMappingSubstitute_sd7929__6 = nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            org.junit.Assert.fail("characterMappingSubstitute_sd7929_add8131 should have thrown IllegalArgumentException");
            nameAllocator.newName(">l;)Y[glgf6Qb=^Ki.3[", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11865() throws Exception {
        Assert.assertEquals("__u_FCV_50__7_4_32_H", new NameAllocator().newName("|%u-FCV!50+`7*4!32@H", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11864() throws Exception {
        Assert.assertEquals("p___Tg____hv_lU_0U__", new NameAllocator().newName("p]&[Tg[-+.hv@lU>0U()"));
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11863_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_2274 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("characterMappingSurrogate_sd11863 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11864_add12042() throws Exception {
        String __DSPOT_suggestion_2275 = "p]&[Tg[-+.hv@lU>0U()";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11864_add12042__4 = nameAllocator.newName(__DSPOT_suggestion_2275);
        Assert.assertEquals("p___Tg____hv_lU_0U__", o_characterMappingSurrogate_sd11864_add12042__4);
        Assert.assertEquals("p___Tg____hv_lU_0U___", nameAllocator.newName(__DSPOT_suggestion_2275));
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11864litString12028() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab"));
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11865litString12053() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11864litString12026() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a-b"));
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11865litString12054() throws Exception {
        Assert.assertEquals("__u_FCV50__7_4_32_H", new NameAllocator().newName("|%u-FCV50+`7*4!32@H", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11864litString12032() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11865_sd12064_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_2282 = new Object();
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "|%u-FCV!50+`7*4!32@H";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_sd11865__6 = nameAllocator.newName("|%u-FCV!50+`7*4!32@H", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSurrogate_sd11865_sd12064 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11865_add12067_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "|%u-FCV!50+`7*4!32@H";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            String o_characterMappingSurrogate_sd11865__6 = nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            org.junit.Assert.fail("characterMappingSurrogate_sd11865_add12067 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11865_add12067_failAssert2_sd14945() throws Exception {
        try {
            Object __DSPOT_tag_2828 = new Object();
            String __DSPOT_suggestion_2827 = "U<GwR/tXLbM|lNo(KW/P";
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "|%u-FCV!50+`7*4!32@H";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_sd11865_add12067_failAssert2_sd14945__11 = nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            Assert.assertEquals("__u_FCV_50__7_4_32_H", nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277));
            String o_characterMappingSurrogate_sd11865__6 = nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            org.junit.Assert.fail("characterMappingSurrogate_sd11865_add12067 should have thrown IllegalArgumentException");
            nameAllocator.newName("U<GwR/tXLbM|lNo(KW/P", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11865_sd12065_add13891() throws Exception {
        String __DSPOT_suggestion_2283 = "hbf3]<,6Y%2Hs*&0mBXH";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11865__6 = nameAllocator.newName("|%u-FCV!50+`7*4!32@H", new Object());
        Assert.assertEquals("__u_FCV_50__7_4_32_H", o_characterMappingSurrogate_sd11865__6);
        String o_characterMappingSurrogate_sd11865_sd12065_add13891__11 = nameAllocator.newName(__DSPOT_suggestion_2283);
        Assert.assertEquals("hbf3___6Y_2Hs__0mBXH", o_characterMappingSurrogate_sd11865_sd12065_add13891__11);
        Assert.assertEquals("hbf3___6Y_2Hs__0mBXH_", nameAllocator.newName(__DSPOT_suggestion_2283));
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11865_sd12066litString13893() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11865__6 = nameAllocator.newName("|%u-FCV!50+`7*4!32@H", new Object());
        Assert.assertEquals("__u_FCV_50__7_4_32_H", o_characterMappingSurrogate_sd11865__6);
        Assert.assertEquals("foo", nameAllocator.newName("foo", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11865_sd12066litString13909() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11865__6 = nameAllocator.newName("|%u-FCV!50+`7*4!32@H", new Object());
        Assert.assertEquals("__u_FCV_50__7_4_32_H", o_characterMappingSurrogate_sd11865__6);
        Assert.assertEquals("public_", nameAllocator.newName("public", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11865_sd12066litString13903() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11865__6 = nameAllocator.newName("|%u-FCV!50+`7*4!32@H", new Object());
        Assert.assertEquals("__u_FCV_50__7_4_32_H", o_characterMappingSurrogate_sd11865__6);
        Assert.assertEquals("_1ab", nameAllocator.newName("1ab", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11865_sd12064_failAssert1_sd14904() throws Exception {
        try {
            String __DSPOT_arg1_2809 = "glOgUa?#VpHGSH!5Uw[V";
            String __DSPOT_arg0_2808 = "@^SC wj[laX)Rfs=(Z 5";
            Object __DSPOT_tag_2282 = new Object();
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "|%u-FCV!50+`7*4!32@H";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_sd11865__6 = nameAllocator.newName("|%u-FCV!50+`7*4!32@H", new Object());
            Assert.assertEquals("__u_FCV_50__7_4_32_H", nameAllocator.newName("|%u-FCV!50+`7*4!32@H", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSurrogate_sd11865_sd12064 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).replaceAll("@^SC wj[laX)Rfs=(Z 5", "glOgUa?#VpHGSH!5Uw[V");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11865_sd12063_sd13842() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11865__6 = nameAllocator.newName("|%u-FCV!50+`7*4!32@H", new Object());
        Assert.assertEquals("__u_FCV_50__7_4_32_H", o_characterMappingSurrogate_sd11865__6);
        Assert.assertEquals("NI_h__c_UA_1_O_mx6Ix", nameAllocator.clone().newName("NI^h;!c/UA%1/O<mx6Ix", new Object()));
    }

    @Test(timeout = 50000)
    public void characterMappingSurrogate_sd11865_sd12065litString13863() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11865__6 = nameAllocator.newName("|%u-FCV!50+`7*4!32@H", new Object());
        Assert.assertEquals("__u_FCV_50__7_4_32_H", o_characterMappingSurrogate_sd11865__6);
        Assert.assertEquals("hbf3__6Y_2Hs__0mBXH", nameAllocator.newName("hbf3]<6Y%2Hs*&0mBXH"));
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd15966() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15966__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15966__6);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("_ZFjR3VfqH_PQpTYOLxT", outterAllocator.clone().newName("?ZFjR3VfqH>PQpTYOLxT", new Object()));
    }

    @Test(timeout = 50000)
    public void cloneUsage_add16026() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_add16026__3 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_add16026__3);
        String o_cloneUsage_add16026__6 = outterAllocator.clone().newName("foo", 3);
        Assert.assertEquals("foo_", o_cloneUsage_add16026__6);
        NameAllocator innerAllocator2 = outterAllocator.clone();
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd15965() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15965__4 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15965__4);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("_Nr_7Io__qgzj9_g8__c", outterAllocator.clone().newName("]Nr,7Io]]qgzj9[g8!*c"));
    }

    @Test(timeout = 50000)
    public void cloneUsagelitString15781() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString15781__3 = outterAllocator.newName("public", 1);
        Assert.assertEquals("public_", o_cloneUsagelitString15781__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd15964_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_3036 = new Object();
            NameAllocator outterAllocator = new NameAllocator();
            outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            outterAllocator.clone().get(new Object());
            org.junit.Assert.fail("cloneUsage_sd15964 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd15969() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15969__4 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15969__4);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        Assert.assertEquals("_3_Q__R_HhCnzd6_PxCHe", outterAllocator.clone().newName("3{Q.&R/HhCnzd6.PxCHe"));
    }

    @Test(timeout = 50000)
    public void cloneUsage_add16021_failAssert17() throws Exception {
        try {
            NameAllocator outterAllocator = new NameAllocator();
            outterAllocator.newName("foo", 1);
            outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            org.junit.Assert.fail("cloneUsage_add16021 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void cloneUsagelitString15767() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString15767__3 = outterAllocator.newName("a-b", 1);
        Assert.assertEquals("a_b", o_cloneUsagelitString15767__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd16017() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("foo", outterAllocator.newName("foo", 1).toString());
    }

    @Test(timeout = 50000)
    public void cloneUsage_add16026_sd23670() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_add16026__3 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_add16026__3);
        String o_cloneUsage_add16026__6 = outterAllocator.clone().newName("foo", 3);
        Assert.assertEquals("foo_", o_cloneUsage_add16026__6);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("M__D__Z_QL_3xkdT_HL5", outterAllocator.newName("M*[D+[Z^QL?3xkdT>HL5", new Object()));
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd15966_sd20345_failAssert6() throws Exception {
        try {
            Object __DSPOT_tag_4289 = new Object();
            Object __DSPOT_tag_3039 = new Object();
            String __DSPOT_suggestion_3038 = "?ZFjR3VfqH>PQpTYOLxT";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_sd15966__6 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_sd15966__11 = outterAllocator.clone().newName("?ZFjR3VfqH>PQpTYOLxT", new Object());
            outterAllocator.clone().get(new Object());
            org.junit.Assert.fail("cloneUsage_sd15966_sd20345 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd15970_add20514_failAssert17() throws Exception {
        try {
            Object __DSPOT_tag_3043 = new Object();
            String __DSPOT_suggestion_3042 = "/mJ!4;!mSOJ<Mvq)N>Z{";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_sd15970__6 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            innerAllocator2.newName(__DSPOT_suggestion_3042, __DSPOT_tag_3043);
            String o_cloneUsage_sd15970__11 = innerAllocator2.newName(__DSPOT_suggestion_3042, __DSPOT_tag_3043);
            org.junit.Assert.fail("cloneUsage_sd15970_add20514 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd15970_sd20510() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15970__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15970__6);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_sd15970__11 = innerAllocator2.newName("/mJ!4;!mSOJ<Mvq)N>Z{", new Object());
        Assert.assertEquals("_mJ_4__mSOJ_Mvq_N_Z_", o_cloneUsage_sd15970__11);
        Assert.assertEquals("a_IJMY8_eQ_n___an0i_", innerAllocator2.newName("a*IJMY8)eQ&n]=`an0i!", new Object()));
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd15969_sd20443() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15969__4 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15969__4);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        String o_cloneUsage_sd15969__9 = outterAllocator.clone().newName("3{Q.&R/HhCnzd6.PxCHe");
        Assert.assertEquals("_3_Q__R_HhCnzd6_PxCHe", o_cloneUsage_sd15969__9);
        Assert.assertEquals("mR0knpj8__A_lHp6_5_6", outterAllocator.newName("mR0knpj8>+A,lHp6#5!6", new Object()));
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd15966_sd20346() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15966__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15966__6);
        String o_cloneUsage_sd15966__11 = outterAllocator.clone().newName("?ZFjR3VfqH>PQpTYOLxT", new Object());
        Assert.assertEquals("_ZFjR3VfqH_PQpTYOLxT", o_cloneUsage_sd15966__11);
        Assert.assertEquals("P9QRX_S5sqip_K_FIqe1", outterAllocator.clone().newName("P9QRX?S5sqip{K&FIqe1"));
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd16002_sd22127_sd31170() throws Exception {
        String __DSPOT_arg1_5325 = "I `C)%1^#[3d&Ya@Us[d";
        String __DSPOT_arg2_3083 = "+]!5rp#^*aZzQL`wn^n*";
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        boolean o_cloneUsage_sd16002__15 = outterAllocator.newName("foo", 1).regionMatches(true, -8285344, __DSPOT_arg2_3083, 1857102388, 660956214);
        String o_cloneUsage_sd16002_sd22127__21 = __DSPOT_arg2_3083.replaceAll("j%JC}?ifLoxnlGwe=ao|", __DSPOT_arg1_5325);
        Assert.assertEquals("I `C)%1^#[3d&Ya@Us[d+I `C)%1^#[3d&Ya@Us[d]I `C)%1^#[3d&Ya@Us[d!I `C)%1^#[3d&Ya@Us[d5I `C)%1^#[3d&Ya@Us[drI `C)%1^#[3d&Ya@Us[dpI `C)%1^#[3d&Ya@Us[d#I `C)%1^#[3d&Ya@Us[d^I `C)%1^#[3d&Ya@Us[d*I `C)%1^#[3d&Ya@Us[daI `C)%1^#[3d&Ya@Us[dZI `C)%1^#[3d&Ya@Us[dzI `C)%1^#[3d&Ya@Us[dQI `C)%1^#[3d&Ya@Us[dLI `C)%1^#[3d&Ya@Us[d`I `C)%1^#[3d&Ya@Us[dwI `C)%1^#[3d&Ya@Us[dnI `C)%1^#[3d&Ya@Us[d^I `C)%1^#[3d&Ya@Us[dnI `C)%1^#[3d&Ya@Us[d*I `C)%1^#[3d&Ya@Us[d", o_cloneUsage_sd16002_sd22127__21);
        boolean o_cloneUsage_sd16002_sd22127_sd31170__27 = __DSPOT_arg1_5325.equals(new Object());
        Assert.assertFalse(o_cloneUsage_sd16002_sd22127_sd31170__27);
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd16002_sd22127_sd31314() throws Exception {
        String __DSPOT_arg2_3083 = "+]!5rp#^*aZzQL`wn^n*";
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        boolean o_cloneUsage_sd16002__15 = outterAllocator.newName("foo", 1).regionMatches(true, -8285344, __DSPOT_arg2_3083, 1857102388, 660956214);
        String o_cloneUsage_sd16002_sd22127__21 = __DSPOT_arg2_3083.replaceAll("j%JC}?ifLoxnlGwe=ao|", "I `C)%1^#[3d&Ya@Us[d");
        Assert.assertEquals("I `C)%1^#[3d&Ya@Us[d+I `C)%1^#[3d&Ya@Us[d]I `C)%1^#[3d&Ya@Us[d!I `C)%1^#[3d&Ya@Us[d5I `C)%1^#[3d&Ya@Us[drI `C)%1^#[3d&Ya@Us[dpI `C)%1^#[3d&Ya@Us[d#I `C)%1^#[3d&Ya@Us[d^I `C)%1^#[3d&Ya@Us[d*I `C)%1^#[3d&Ya@Us[daI `C)%1^#[3d&Ya@Us[dZI `C)%1^#[3d&Ya@Us[dzI `C)%1^#[3d&Ya@Us[dQI `C)%1^#[3d&Ya@Us[dLI `C)%1^#[3d&Ya@Us[d`I `C)%1^#[3d&Ya@Us[dwI `C)%1^#[3d&Ya@Us[dnI `C)%1^#[3d&Ya@Us[d^I `C)%1^#[3d&Ya@Us[dnI `C)%1^#[3d&Ya@Us[d*I `C)%1^#[3d&Ya@Us[d", o_cloneUsage_sd16002_sd22127__21);
        Assert.assertEquals("QBCL_cK_wMd0U2__w_x_", outterAllocator.clone().newName("QBCL]cK{wMd0U2*<w%x#", new Object()));
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd16002_sd22127_sd31317() throws Exception {
        String __DSPOT_arg2_3083 = "+]!5rp#^*aZzQL`wn^n*";
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        boolean o_cloneUsage_sd16002__15 = outterAllocator.newName("foo", 1).regionMatches(true, -8285344, __DSPOT_arg2_3083, 1857102388, 660956214);
        String o_cloneUsage_sd16002_sd22127__21 = __DSPOT_arg2_3083.replaceAll("j%JC}?ifLoxnlGwe=ao|", "I `C)%1^#[3d&Ya@Us[d");
        Assert.assertEquals("I `C)%1^#[3d&Ya@Us[d+I `C)%1^#[3d&Ya@Us[d]I `C)%1^#[3d&Ya@Us[d!I `C)%1^#[3d&Ya@Us[d5I `C)%1^#[3d&Ya@Us[drI `C)%1^#[3d&Ya@Us[dpI `C)%1^#[3d&Ya@Us[d#I `C)%1^#[3d&Ya@Us[d^I `C)%1^#[3d&Ya@Us[d*I `C)%1^#[3d&Ya@Us[daI `C)%1^#[3d&Ya@Us[dZI `C)%1^#[3d&Ya@Us[dzI `C)%1^#[3d&Ya@Us[dQI `C)%1^#[3d&Ya@Us[dLI `C)%1^#[3d&Ya@Us[d`I `C)%1^#[3d&Ya@Us[dwI `C)%1^#[3d&Ya@Us[dnI `C)%1^#[3d&Ya@Us[d^I `C)%1^#[3d&Ya@Us[dnI `C)%1^#[3d&Ya@Us[d*I `C)%1^#[3d&Ya@Us[d", o_cloneUsage_sd16002_sd22127__21);
        Assert.assertEquals("_ZnZ_5QbO_Rn__0_lROw", outterAllocator.clone().newName("/ZnZ&5QbO]Rn&{0@lROw"));
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd16002_sd22127_sd31308_failAssert36() throws Exception {
        try {
            Object __DSPOT_tag_10074 = new Object();
            String __DSPOT_arg1_5325 = "I `C)%1^#[3d&Ya@Us[d";
            String __DSPOT_arg0_5324 = "j%JC}?ifLoxnlGwe=ao|";
            int __DSPOT_arg4_3085 = 660956214;
            int __DSPOT_arg3_3084 = 1857102388;
            String __DSPOT_arg2_3083 = "+]!5rp#^*aZzQL`wn^n*";
            int __DSPOT_arg1_3082 = -8285344;
            boolean __DSPOT_arg0_3081 = true;
            NameAllocator outterAllocator = new NameAllocator();
            String __DSPOT_invoc_3 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            boolean o_cloneUsage_sd16002__15 = outterAllocator.newName("foo", 1).regionMatches(true, -8285344, __DSPOT_arg2_3083, 1857102388, 660956214);
            String o_cloneUsage_sd16002_sd22127__21 = __DSPOT_arg2_3083.replaceAll("j%JC}?ifLoxnlGwe=ao|", "I `C)%1^#[3d&Ya@Us[d");
            outterAllocator.get(new Object());
            org.junit.Assert.fail("cloneUsage_sd16002_sd22127_sd31308 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void cloneUsage_sd15970_sd20506_sd26839() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15970__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15970__6);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        String o_cloneUsage_sd15970__11 = outterAllocator.clone().newName("/mJ!4;!mSOJ<Mvq)N>Z{", new Object());
        Assert.assertEquals("_mJ_4__mSOJ_Mvq_N_Z_", o_cloneUsage_sd15970__11);
        String o_cloneUsage_sd15970_sd20506__20 = innerAllocator1.newName(">H0QfsxUab_erF,nHS,Q", new Object());
        Assert.assertEquals("_H0QfsxUab_erF_nHS_Q", o_cloneUsage_sd15970_sd20506__20);
        Assert.assertEquals("_2L1BtJxs__Try_t_hS_m", innerAllocator1.newName("2L1BtJxs_`Try-t)hS#m", new Object()));
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40448() throws Exception {
        Assert.assertEquals("__A_ah_M___a_BOqr_CG", new NameAllocator().newName("&/A;ah%M#_/a?BOqr^CG", new Object()));
    }

    @Test(timeout = 50000)
    public void javaKeyword_add40450() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", 1));
    }

    @Test(timeout = 50000)
    public void javaKeyword_add40452_failAssert1() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(1);
            org.junit.Assert.fail("javaKeyword_add40452 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40447() throws Exception {
        Assert.assertEquals("QMxD$mOp__O_vhw_Ux$2", new NameAllocator().newName("QMxD$mOp{)O vhw}Ux$2"));
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40448_add40673_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_15807 = new Object();
            String __DSPOT_suggestion_15806 = "&/A;ah%M#_/a?BOqr^CG";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_15806, __DSPOT_tag_15807);
            String o_javaKeyword_sd40448__6 = nameAllocator.newName(__DSPOT_suggestion_15806, __DSPOT_tag_15807);
            org.junit.Assert.fail("javaKeyword_sd40448_add40673 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40447_add40648() throws Exception {
        String __DSPOT_suggestion_15805 = "QMxD$mOp{)O vhw}Ux$2";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40447_add40648__4 = nameAllocator.newName(__DSPOT_suggestion_15805);
        Assert.assertEquals("QMxD$mOp__O_vhw_Ux$2", o_javaKeyword_sd40447_add40648__4);
        Assert.assertEquals("QMxD$mOp__O_vhw_Ux$2_", nameAllocator.newName(__DSPOT_suggestion_15805));
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40448_sd40669() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40448__6 = nameAllocator.newName("&/A;ah%M#_/a?BOqr^CG", new Object());
        Assert.assertEquals("__A_ah_M___a_BOqr_CG", o_javaKeyword_sd40448__6);
        nameAllocator.clone();
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40447litString40628() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40448_sd40670_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_15812 = new Object();
            Object __DSPOT_tag_15807 = new Object();
            String __DSPOT_suggestion_15806 = "&/A;ah%M#_/a?BOqr^CG";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_sd40448__6 = nameAllocator.newName("&/A;ah%M#_/a?BOqr^CG", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("javaKeyword_sd40448_sd40670 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40447litString40643() throws Exception {
        Assert.assertEquals("QxD$mOp__O_vhw_Ux$2", new NameAllocator().newName("QxD$mOp{)O vhw}Ux$2"));
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40448litString40650() throws Exception {
        Assert.assertEquals("foo", new NameAllocator().newName("foo", new Object()));
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40447litString40635() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab"));
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40447litString40641() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40448litString40666() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", new Object()));
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40448_sd40671_add42725() throws Exception {
        String __DSPOT_suggestion_15813 = "o.AH!O*DpoO9KAF?;xo5";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40448__6 = nameAllocator.newName("&/A;ah%M#_/a?BOqr^CG", new Object());
        Assert.assertEquals("__A_ah_M___a_BOqr_CG", o_javaKeyword_sd40448__6);
        String o_javaKeyword_sd40448_sd40671_add42725__11 = nameAllocator.newName(__DSPOT_suggestion_15813);
        Assert.assertEquals("o_AH_O_DpoO9KAF__xo5", o_javaKeyword_sd40448_sd40671_add42725__11);
        Assert.assertEquals("o_AH_O_DpoO9KAF__xo5_", nameAllocator.newName(__DSPOT_suggestion_15813));
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40448_add40673_failAssert2_sd43780() throws Exception {
        try {
            Object __DSPOT_tag_16427 = new Object();
            String __DSPOT_suggestion_16426 = "Rk@:qC^eKp=zh@_&qWS.";
            Object __DSPOT_tag_15807 = new Object();
            String __DSPOT_suggestion_15806 = "&/A;ah%M#_/a?BOqr^CG";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_sd40448_add40673_failAssert2_sd43780__11 = nameAllocator.newName(__DSPOT_suggestion_15806, __DSPOT_tag_15807);
            Assert.assertEquals("__A_ah_M___a_BOqr_CG", nameAllocator.newName(__DSPOT_suggestion_15806, __DSPOT_tag_15807));
            String o_javaKeyword_sd40448__6 = nameAllocator.newName(__DSPOT_suggestion_15806, __DSPOT_tag_15807);
            org.junit.Assert.fail("javaKeyword_sd40448_add40673 should have thrown IllegalArgumentException");
            nameAllocator.newName("Rk@:qC^eKp=zh@_&qWS.", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40448_sd40671litString42687() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40448__6 = nameAllocator.newName("&/A;ah%M#_/a?BOqr^CG", new Object());
        Assert.assertEquals("__A_ah_M___a_BOqr_CG", o_javaKeyword_sd40448__6);
        Assert.assertEquals("public_", nameAllocator.newName("public"));
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40448_sd40672_sd42769() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40448__6 = nameAllocator.newName("&/A;ah%M#_/a?BOqr^CG", new Object());
        Assert.assertEquals("__A_ah_M___a_BOqr_CG", o_javaKeyword_sd40448__6);
        String o_javaKeyword_sd40448_sd40672__13 = nameAllocator.newName("R@H.S=Ol&QGI-nFAscpd", new Object());
        Assert.assertEquals("R_H_S_Ol_QGI_nFAscpd", o_javaKeyword_sd40448_sd40672__13);
        Assert.assertEquals("_9_L_POa33_583zF_es_m", nameAllocator.newName("9[L/POa33]583zF?es|m", new Object()));
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40448_sd40669_sd42676() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40448__6 = nameAllocator.newName("&/A;ah%M#_/a?BOqr^CG", new Object());
        Assert.assertEquals("__A_ah_M___a_BOqr_CG", o_javaKeyword_sd40448__6);
        Assert.assertEquals("WEA7p_1v5Z_Kc_ZJqdnM", nameAllocator.clone().newName("WEA7p`1v5Z}Kc!ZJqdnM", new Object()));
    }

    @Test(timeout = 50000)
    public void javaKeyword_sd40448_sd40670_failAssert1_sd43709() throws Exception {
        try {
            int __DSPOT_arg1_16363 = 669867295;
            int __DSPOT_arg0_16362 = -4256529;
            Object __DSPOT_tag_15812 = new Object();
            Object __DSPOT_tag_15807 = new Object();
            String __DSPOT_suggestion_15806 = "&/A;ah%M#_/a?BOqr^CG";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_sd40448__6 = nameAllocator.newName("&/A;ah%M#_/a?BOqr^CG", new Object());
            Assert.assertEquals("__A_ah_M___a_BOqr_CG", nameAllocator.newName("&/A;ah%M#_/a?BOqr^CG", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("javaKeyword_sd40448_sd40670 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).codePointCount(-4256529, 669867295);
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void nameCollision_add44723() throws Exception {
        Assert.assertEquals("foo", new NameAllocator().newName("foo"));
    }

    @Test(timeout = 50000)
    public void nameCollision_sd44715_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_16631 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("nameCollision_sd44715 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void nameCollision_sd44716() throws Exception {
        Assert.assertEquals("_O1TvxqPfah4BJoDC9z_", new NameAllocator().newName("+O1TvxqPfah4BJoDC9z<"));
    }

    @Test(timeout = 50000)
    public void nameCollision_add44723litString45073() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab"));
    }

    @Test(timeout = 50000)
    public void nameCollision_sd44716_add44990() throws Exception {
        String __DSPOT_suggestion_16632 = "+O1TvxqPfah4BJoDC9z<";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd44716_add44990__4 = nameAllocator.newName(__DSPOT_suggestion_16632);
        Assert.assertEquals("_O1TvxqPfah4BJoDC9z_", o_nameCollision_sd44716_add44990__4);
        Assert.assertEquals("_O1TvxqPfah4BJoDC9z__", nameAllocator.newName(__DSPOT_suggestion_16632));
    }

    @Test(timeout = 50000)
    public void nameCollision_sd44717litString45006() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", new Object()));
    }

    @Test(timeout = 50000)
    public void nameCollision_add44721litString45052() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 50000)
    public void nameCollision_add44723_sd45084_failAssert5() throws Exception {
        try {
            Object __DSPOT_tag_16651 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_add44723__3 = nameAllocator.newName("foo");
            nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollision_add44723_sd45084 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void nameCollision_add44723litString45078() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab"));
    }

    @Test(timeout = 50000)
    public void nameCollision_add44723litString45068() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a-b"));
    }

    @Test(timeout = 50000)
    public void nameCollision_sd44717_add45015_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_16634 = new Object();
            String __DSPOT_suggestion_16633 = "/1xvQyIg)]Wb|}I e7b5";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_16633, __DSPOT_tag_16634);
            String o_nameCollision_sd44717__6 = nameAllocator.newName(__DSPOT_suggestion_16633, __DSPOT_tag_16634);
            org.junit.Assert.fail("nameCollision_sd44717_add45015 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void nameCollision_add44721litString45057() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 50000)
    public void nameCollision_sd44716_sd44989litString46355() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd44716__4 = nameAllocator.newName("+O1TvxqPfa}4BJoDC9z<");
        Assert.assertEquals("_O1TvxqPfa_4BJoDC9z_", o_nameCollision_sd44716__4);
        Assert.assertEquals("Q_3__B_Fr_cb9_g_Qegp", nameAllocator.newName("Q%3&#B:Fr)cb9!g=Qegp", new Object()));
    }

    @Test(timeout = 50000)
    public void nameCollision_sd44717_sd45013_add47025() throws Exception {
        String __DSPOT_suggestion_16640 = "_9}lVQMdAY[|cGK1]-1?";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd44717__6 = nameAllocator.newName("/1xvQyIg)]Wb|}I e7b5", new Object());
        Assert.assertEquals("_1xvQyIg__Wb__I_e7b5", o_nameCollision_sd44717__6);
        String o_nameCollision_sd44717_sd45013_add47025__11 = nameAllocator.newName(__DSPOT_suggestion_16640);
        Assert.assertEquals("_9_lVQMdAY__cGK1__1_", o_nameCollision_sd44717_sd45013_add47025__11);
        Assert.assertEquals("_9_lVQMdAY__cGK1__1__", nameAllocator.newName(__DSPOT_suggestion_16640));
    }

    @Test(timeout = 50000)
    public void nameCollision_sd44717_sd45014_add47070_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_16642 = new Object();
            String __DSPOT_suggestion_16641 = "<6ePa@GxB2[_4euy!::f";
            Object __DSPOT_tag_16634 = new Object();
            String __DSPOT_suggestion_16633 = "/1xvQyIg)]Wb|}I e7b5";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_16633, __DSPOT_tag_16634);
            String o_nameCollision_sd44717__6 = nameAllocator.newName(__DSPOT_suggestion_16633, __DSPOT_tag_16634);
            String o_nameCollision_sd44717_sd45014__13 = nameAllocator.newName("<6ePa@GxB2[_4euy!::f", new Object());
            org.junit.Assert.fail("nameCollision_sd44717_sd45014_add47070 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void nameCollision_sd44717_sd45011_sd46976() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd44717__6 = nameAllocator.newName("/1xvQyIg)]Wb|}I e7b5", new Object());
        Assert.assertEquals("_1xvQyIg__Wb__I_e7b5", o_nameCollision_sd44717__6);
        Assert.assertEquals("_oD4___s5_X_q_XF_h_Z", nameAllocator.clone().newName("=oD4+#-s5&X>q(XF?h[Z", new Object()));
    }

    @Test(timeout = 50000)
    public void nameCollision_sd44717_sd45013litString46995() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd44717__6 = nameAllocator.newName("/1xvQyIg)]Wb|}I e7b5", new Object());
        Assert.assertEquals("_1xvQyIg__Wb__I_e7b5", o_nameCollision_sd44717__6);
        Assert.assertEquals("_1ab", nameAllocator.newName("1ab"));
    }

    @Test(timeout = 50000)
    public void nameCollision_sd44717_sd45012_failAssert1_sd49181() throws Exception {
        try {
            int __DSPOT_arg1_17325 = -1021749732;
            int __DSPOT_arg0_17324 = -1573432051;
            Object __DSPOT_tag_16639 = new Object();
            Object __DSPOT_tag_16634 = new Object();
            String __DSPOT_suggestion_16633 = "/1xvQyIg)]Wb|}I e7b5";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_sd44717__6 = nameAllocator.newName("/1xvQyIg)]Wb|}I e7b5", new Object());
            Assert.assertEquals("_1xvQyIg__Wb__I_e7b5", nameAllocator.newName("/1xvQyIg)]Wb|}I e7b5", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollision_sd44717_sd45012 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).codePointCount(-1573432051, -1021749732);
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void nameCollision_sd44716_sd44989litString46359() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd44716__4 = nameAllocator.newName("public");
        Assert.assertEquals("public_", o_nameCollision_sd44716__4);
        Assert.assertEquals("Q_3__B_Fr_cb9_g_Qegp", nameAllocator.newName("Q%3&#B:Fr)cb9!g=Qegp", new Object()));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_add50325_failAssert2() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(2);
            org.junit.Assert.fail("nameCollisionWithTag_add50325 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_sd50314() throws Exception {
        Assert.assertEquals("KTVd__7__F_vH0_jsFyK", new NameAllocator().newName("KTVd<[7#;F^vH0 jsFyK"));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_sd50315() throws Exception {
        Assert.assertEquals("_8Qr_YaTCxC_f_NU96gA", new NameAllocator().newName(")8Qr+YaTCxC[f!NU96gA", new Object()));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_add50319() throws Exception {
        Assert.assertEquals("foo", new NameAllocator().newName("foo", 2));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_add50321_add50801_failAssert1() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 3);
            String o_nameCollisionWithTag_add50321__3 = nameAllocator.newName("foo", 3);
            org.junit.Assert.fail("nameCollisionWithTag_add50321_add50801 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_sd50315_sd50712_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_17670 = new Object();
            Object __DSPOT_tag_17665 = new Object();
            String __DSPOT_suggestion_17664 = ")8Qr+YaTCxC[f!NU96gA";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_sd50315__6 = nameAllocator.newName(")8Qr+YaTCxC[f!NU96gA", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollisionWithTag_sd50315_sd50712 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_sd50315_sd50713() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50315__6 = nameAllocator.newName(")8Qr+YaTCxC[f!NU96gA", new Object());
        Assert.assertEquals("_8Qr_YaTCxC_f_NU96gA", o_nameCollisionWithTag_sd50315__6);
        Assert.assertEquals("_O_0__31_4_T__E$8y_F", nameAllocator.newName("(O(0?@31(4?T`,E$8y?F"));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_add50321litString50780() throws Exception {
        Assert.assertEquals("_0Dc_0U3mGA", new NameAllocator().newName("0Dc>0U3mGA", 3));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_add50319litString50747() throws Exception {
        Assert.assertEquals("foo__", new NameAllocator().newName("foo__", 2));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_add50319litString50755() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab", 2));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_add50319litString50750() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a-b", 2));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_add50319litString50761() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", 2));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_sd50315_sd50714_add51667_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_17673 = new Object();
            String __DSPOT_suggestion_17672 = "X{v0<Dj(!hr{p]W*8l{O";
            Object __DSPOT_tag_17665 = new Object();
            String __DSPOT_suggestion_17664 = ")8Qr+YaTCxC[f!NU96gA";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_sd50315__6 = nameAllocator.newName(")8Qr+YaTCxC[f!NU96gA", new Object());
            nameAllocator.newName(__DSPOT_suggestion_17672, __DSPOT_tag_17673);
            String o_nameCollisionWithTag_sd50315_sd50714__13 = nameAllocator.newName(__DSPOT_suggestion_17672, __DSPOT_tag_17673);
            org.junit.Assert.fail("nameCollisionWithTag_sd50315_sd50714_add51667 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_sd50315_sd50713litString53672() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50315__6 = nameAllocator.newName("public", new Object());
        Assert.assertEquals("public_", o_nameCollisionWithTag_sd50315__6);
        Assert.assertEquals("_O_0__31_4_T__E$8y_F", nameAllocator.newName("(O(0?@31(4?T`,E$8y?F"));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_sd50315_sd50713_add53689() throws Exception {
        String __DSPOT_suggestion_17671 = "(O(0?@31(4?T`,E$8y?F";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50315__6 = nameAllocator.newName(")8Qr+YaTCxC[f!NU96gA", new Object());
        Assert.assertEquals("_8Qr_YaTCxC_f_NU96gA", o_nameCollisionWithTag_sd50315__6);
        String o_nameCollisionWithTag_sd50315_sd50713_add53689__11 = nameAllocator.newName(__DSPOT_suggestion_17671);
        Assert.assertEquals("_O_0__31_4_T__E$8y_F", o_nameCollisionWithTag_sd50315_sd50713_add53689__11);
        Assert.assertEquals("_O_0__31_4_T__E$8y_F_", nameAllocator.newName(__DSPOT_suggestion_17671));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_sd50315_sd50712_failAssert0_sd54155() throws Exception {
        try {
            int __DSPOT_arg3_18359 = -1428705107;
            int __DSPOT_arg2_18358 = -66674694;
            String __DSPOT_arg1_18357 = "W+5$Aj-ex}&>G?/X?qRy";
            int __DSPOT_arg0_18356 = 428784935;
            Object __DSPOT_tag_17670 = new Object();
            Object __DSPOT_tag_17665 = new Object();
            String __DSPOT_suggestion_17664 = ")8Qr+YaTCxC[f!NU96gA";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_sd50315__6 = nameAllocator.newName(")8Qr+YaTCxC[f!NU96gA", new Object());
            Assert.assertEquals("_8Qr_YaTCxC_f_NU96gA", nameAllocator.newName(")8Qr+YaTCxC[f!NU96gA", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollisionWithTag_sd50315_sd50712 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).regionMatches(428784935, "W+5$Aj-ex}&>G?/X?qRy", -66674694, -1428705107);
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_sd50315_sd50713litString53648() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50315__6 = nameAllocator.newName(")8Qr+YaTCxC[f!NU96gA", new Object());
        Assert.assertEquals("_8Qr_YaTCxC_f_NU96gA", o_nameCollisionWithTag_sd50315__6);
        Assert.assertEquals("_O_0_31_4_T__E$8y_F", nameAllocator.newName("(O(0@31(4?T`,E$8y?F"));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_sd50315_sd50714_sd51664() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50315__6 = nameAllocator.newName(")8Qr+YaTCxC[f!NU96gA", new Object());
        Assert.assertEquals("_8Qr_YaTCxC_f_NU96gA", o_nameCollisionWithTag_sd50315__6);
        String o_nameCollisionWithTag_sd50315_sd50714__13 = nameAllocator.newName("X{v0<Dj(!hr{p]W*8l{O", new Object());
        Assert.assertEquals("X_v0_Dj__hr_p_W_8l_O", o_nameCollisionWithTag_sd50315_sd50714__13);
        Assert.assertEquals("_6Sh_rY_Z__fI_Ftiuq__", nameAllocator.newName("6Sh>rY}Z|!fI{Ftiuq%="));
    }

    @Test(timeout = 50000)
    public void nameCollisionWithTag_sd50315_sd50713litString53645() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50315__6 = nameAllocator.newName(")8Qr+YaTCxC[f!NU96gA", new Object());
        Assert.assertEquals("_8Qr_YaTCxC_f_NU96gA", o_nameCollisionWithTag_sd50315__6);
        Assert.assertEquals("bar", nameAllocator.newName("bar"));
    }

    @Test(timeout = 50000)
    public void tagReuseForbiddenlitString55204() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55204__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString55204__3);
        try {
            nameAllocator.newName("public", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 50000)
    public void tagReuseForbiddenlitString55178() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55178__3 = nameAllocator.newName("1ab", 1);
        Assert.assertEquals("_1ab", o_tagReuseForbiddenlitString55178__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 50000)
    public void tagReuseForbidden_sd55233_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_18659 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.get(new Object());
            org.junit.Assert.fail("tagReuseForbidden_sd55233 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void tagReuseForbiddenlitString55174() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55174__3 = nameAllocator.newName("tag 1 cannot be used for both 'foo' and 'bar'", 1);
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", o_tagReuseForbiddenlitString55174__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 50000)
    public void tagReuseForbidden_sd55234() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55234__4 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55234__4);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("byZ_E9_b_v5_hn_C__2_", nameAllocator.newName("byZ)E9(b;v5+hn<C+]2("));
    }

    @Test(timeout = 50000)
    public void tagReuseForbiddenlitString55184() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55184__3 = nameAllocator.newName("public", 1);
        Assert.assertEquals("public_", o_tagReuseForbiddenlitString55184__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 50000)
    public void tagReuseForbidden_sd55300() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55300__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55300__3);
        try {
            int __DSPOT_arg3_18744 = 24245499;
            byte[] __DSPOT_arg2_18743 = new byte[]{ 88, -65, -90, 79 };
            int __DSPOT_arg1_18742 = 813012784;
            int __DSPOT_arg0_18741 = 1759253613;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).getBytes(1759253613, 813012784, new byte[]{ 88, -65, -90, 79 }, 24245499);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 50000)
    public void tagReuseForbiddenlitString55170() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55170__3 = nameAllocator.newName("a-b", 1);
        Assert.assertEquals("a_b", o_tagReuseForbiddenlitString55170__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 50000)
    public void tagReuseForbiddenlitString55182() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55182__3 = nameAllocator.newName("&ab", 1);
        Assert.assertEquals("_ab", o_tagReuseForbiddenlitString55182__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 50000)
    public void tagReuseForbiddenlitString55217() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55217__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbiddenlitString55217__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 50000)
    public void tagReuseForbidden_sd55300_sd68229() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55300__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55300__3);
        try {
            int __DSPOT_arg3_18744 = 24245499;
            byte[] __DSPOT_arg2_18743 = new byte[]{ 88, -65, -90, 79 };
            int __DSPOT_arg1_18742 = 813012784;
            int __DSPOT_arg0_18741 = 1759253613;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).getBytes(1759253613, 813012784, new byte[]{ 88, -65, -90, 79 }, 24245499);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_D_xh__LhF_CRQ__7__Y", nameAllocator.newName("@D<xh+@LhF)CRQ`%7. Y", new Object()));
    }

    @Test(timeout = 50000)
    public void tagReuseForbidden_sd55318_sd69837() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55318__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55318__3);
        try {
            int __DSPOT_arg3_18773 = 1809229157;
            int __DSPOT_arg2_18772 = 79271732;
            String __DSPOT_arg1_18771 = "hq*&W<|Vm<N1sM!CWw*}";
            int __DSPOT_arg0_18770 = 2003297378;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(2003297378, "hq*&W<|Vm<N1sM!CWw*}", 79271732, 1809229157);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_9__Tu5_UyBV1_S_lz__F", nameAllocator.newName("9?&Tu5?UyBV1%S:lz?-F", new Object()));
    }

    @Test(timeout = 50000)
    public void tagReuseForbidden_sd55321_sd70416() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55321__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55321__3);
        try {
            int __DSPOT_arg3_28137 = 900857182;
            int __DSPOT_arg2_28136 = 813286218;
            String __DSPOT_arg1_28135 = "=B/uet]hvhBjN, /i;4w";
            int __DSPOT_arg0_28134 = 306462358;
            String __DSPOT_arg1_18779 = "NBX$Z_orad:(HZs:qTzI";
            String __DSPOT_arg0_18778 = "<T4f!P=ND7|w^nv}d!v1";
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).replaceFirst(__DSPOT_arg0_18778, "NBX$Z_orad:(HZs:qTzI");
            __DSPOT_arg0_18778.regionMatches(306462358, "=B/uet]hvhBjN, /i;4w", 813286218, 900857182);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 50000)
    public void tagReuseForbidden_sd55317_sd69699() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55317__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55317__3);
        try {
            int __DSPOT_arg4_18769 = 2144397075;
            int __DSPOT_arg3_18768 = -441028693;
            String __DSPOT_arg2_18767 = "B!h[3l3rL`ABim#j8P*I";
            int __DSPOT_arg1_18766 = -242578012;
            boolean __DSPOT_arg0_18765 = false;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(false, -242578012, "B!h[3l3rL`ABim#j8P*I", -441028693, 2144397075);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("q_6oY__bKk_pA_JbXRGZ", nameAllocator.newName("q(6oY?[bKk-pA`JbXRGZ"));
    }

    @Test(timeout = 50000)
    public void tagReuseForbidden_remove55339litString94398() throws Exception {
        try {
            String o_tagReuseForbidden_remove55339__5 = new NameAllocator().newName("public", 1);
            Assert.assertEquals("public_", new NameAllocator().newName("public", 1));
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 50000)
    public void tagReuseForbidden_sd55235_sd62246_sd79683_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_33703 = new Object();
            Object __DSPOT_tag_22956 = new Object();
            String __DSPOT_suggestion_22955 = "F?bZ^{n%+hx0v8DCraxk";
            Object __DSPOT_tag_18662 = new Object();
            String __DSPOT_suggestion_18661 = "}>C]< cRHr7c,1|ajyT&";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_sd55235__6 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_sd55235__11 = nameAllocator.newName("}>C]< cRHr7c,1|ajyT&", new Object());
            String o_tagReuseForbidden_sd55235_sd62246__20 = nameAllocator.newName("F?bZ^{n%+hx0v8DCraxk", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("tagReuseForbidden_sd55235_sd62246_sd79683 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void tagReuseForbidden_sd55318_sd69837_sd88958() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55318__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55318__3);
        try {
            int __DSPOT_arg3_39506 = -509422346;
            char[] __DSPOT_arg2_39505 = new char[]{ ' ', '[' };
            int __DSPOT_arg1_39504 = -1021421230;
            int __DSPOT_arg0_39503 = 2113695977;
            int __DSPOT_arg3_18773 = 1809229157;
            int __DSPOT_arg2_18772 = 79271732;
            String __DSPOT_arg1_18771 = "hq*&W<|Vm<N1sM!CWw*}";
            int __DSPOT_arg0_18770 = 2003297378;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(2003297378, __DSPOT_arg1_18771, 79271732, 1809229157);
            __DSPOT_arg1_18771.getChars(2113695977, -1021421230, new char[]{ ' ', '[' }, -509422346);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_9__Tu5_UyBV1_S_lz__F", nameAllocator.newName("9?&Tu5?UyBV1%S:lz?-F", new Object()));
    }

    @Test(timeout = 50000)
    public void tagReuseForbidden_sd55235_sd62246_sd79684() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55235__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55235__6);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_sd55235__11 = nameAllocator.newName("}>C]< cRHr7c,1|ajyT&", new Object());
        Assert.assertEquals("__C___cRHr7c_1_ajyT_", o_tagReuseForbidden_sd55235__11);
        String o_tagReuseForbidden_sd55235_sd62246__20 = nameAllocator.newName("F?bZ^{n%+hx0v8DCraxk", new Object());
        Assert.assertEquals("F_bZ__n__hx0v8DCraxk", o_tagReuseForbidden_sd55235_sd62246__20);
        Assert.assertEquals("____tI9Jn_mJ_ii37Omo", nameAllocator.newName("{%?!tI9Jn mJ&ii37Omo"));
    }

    @Test(timeout = 50000)
    public void tagReuseForbidden_sd55317_sd69731_sd76169() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55317__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55317__3);
        try {
            int __DSPOT_arg3_31701 = -1334883901;
            byte[] __DSPOT_arg2_31700 = new byte[]{ -39, 11 };
            int __DSPOT_arg1_31699 = -761951450;
            int __DSPOT_arg0_31698 = -864752610;
            int __DSPOT_arg4_27662 = -1366886935;
            int __DSPOT_arg3_27661 = 258211510;
            String __DSPOT_arg2_27660 = "&Yt8?iv m1|<3Y-JbofO";
            int __DSPOT_arg1_27659 = -171048456;
            boolean __DSPOT_arg0_27658 = true;
            int __DSPOT_arg4_18769 = 2144397075;
            int __DSPOT_arg3_18768 = -441028693;
            String __DSPOT_arg2_18767 = "B!h[3l3rL`ABim#j8P*I";
            int __DSPOT_arg1_18766 = -242578012;
            boolean __DSPOT_arg0_18765 = false;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(false, -242578012, __DSPOT_arg2_18767, -441028693, 2144397075);
            __DSPOT_arg2_18767.regionMatches(true, -171048456, "&Yt8?iv m1|<3Y-JbofO", 258211510, -1366886935);
            __DSPOT_arg2_18767.getBytes(-864752610, -761951450, new byte[]{ -39, 11 }, -1334883901);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 50000)
    public void tagReuseForbidden_sd55317_sd69731_sd76138() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55317__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55317__3);
        try {
            int __DSPOT_arg3_31665 = 1272617107;
            int __DSPOT_arg2_31664 = -1351538452;
            String __DSPOT_arg1_31663 = "Tn2_EGL9pEzD$Y>U;ux;";
            int __DSPOT_arg0_31662 = -2142323676;
            int __DSPOT_arg4_27662 = -1366886935;
            int __DSPOT_arg3_27661 = 258211510;
            String __DSPOT_arg2_27660 = "&Yt8?iv m1|<3Y-JbofO";
            int __DSPOT_arg1_27659 = -171048456;
            boolean __DSPOT_arg0_27658 = true;
            int __DSPOT_arg4_18769 = 2144397075;
            int __DSPOT_arg3_18768 = -441028693;
            String __DSPOT_arg2_18767 = "B!h[3l3rL`ABim#j8P*I";
            int __DSPOT_arg1_18766 = -242578012;
            boolean __DSPOT_arg0_18765 = false;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(false, -242578012, __DSPOT_arg2_18767, -441028693, 2144397075);
            __DSPOT_arg2_18767.regionMatches(true, -171048456, __DSPOT_arg2_27660, 258211510, -1366886935);
            __DSPOT_arg2_27660.regionMatches(-2142323676, "Tn2_EGL9pEzD$Y>U;ux;", -1351538452, 1272617107);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 50000)
    public void usage_add97805_failAssert2() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(2);
            org.junit.Assert.fail("usage_add97805 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void usage_add97801() throws Exception {
        Assert.assertEquals("bar", new NameAllocator().newName("bar", 2));
    }

    @Test(timeout = 50000)
    public void usage_sd97797() throws Exception {
        Assert.assertEquals("_$61hVUea_i_P_2iDZ9_", new NameAllocator().newName("-$61hVUea/i|P[2iDZ9[", new Object()));
    }

    @Test(timeout = 50000)
    public void usage_add97801litString98118() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a-b", 2));
    }

    @Test(timeout = 50000)
    public void usage_add97801litString98129() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab", 2));
    }

    @Test(timeout = 50000)
    public void usage_add97801litString98131() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", 2));
    }

    @Test(timeout = 50000)
    public void usage_sd97796() throws Exception {
        Assert.assertEquals("b_b7PqkO0eL_AI_____I", new NameAllocator().newName("b}b7PqkO0eL<AI#}]^?I"));
    }

    @Test(timeout = 50000)
    public void usage_add97801litString98113() throws Exception {
        Assert.assertEquals("foo_", new NameAllocator().newName("foo_", 2));
    }

    @Test(timeout = 50000)
    public void usage_add97801litString98123() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", 2));
    }

    @Test(timeout = 50000)
    public void usage_sd97797_sd98081_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_44723 = new Object();
            Object __DSPOT_tag_44718 = new Object();
            String __DSPOT_suggestion_44717 = "-$61hVUea/i|P[2iDZ9[";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_sd97797__6 = nameAllocator.newName("-$61hVUea/i|P[2iDZ9[", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("usage_sd97797_sd98081 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void usage_sd97796litString98052() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 50000)
    public void usage_sd97797_add98084_failAssert3() throws Exception {
        try {
            Object __DSPOT_tag_44718 = new Object();
            String __DSPOT_suggestion_44717 = "-$61hVUea/i|P[2iDZ9[";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_44717, __DSPOT_tag_44718);
            String o_usage_sd97797__6 = nameAllocator.newName(__DSPOT_suggestion_44717, __DSPOT_tag_44718);
            org.junit.Assert.fail("usage_sd97797_add98084 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void usage_sd97796_sd98058litString100431() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd97796__4 = nameAllocator.newName("a_b");
        Assert.assertEquals("a_b", o_usage_sd97796__4);
        Assert.assertEquals("_wvF_l_aHbMt_V3LJD_z", nameAllocator.newName("#wvF_l_aHbMt/V3LJD-z", new Object()));
    }

    @Test(timeout = 50000)
    public void usage_sd97797_sd98081_failAssert2_sd101532() throws Exception {
        try {
            int __DSPOT_arg0_45425 = -136974548;
            Object __DSPOT_tag_44723 = new Object();
            Object __DSPOT_tag_44718 = new Object();
            String __DSPOT_suggestion_44717 = "-$61hVUea/i|P[2iDZ9[";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_sd97797__6 = nameAllocator.newName("-$61hVUea/i|P[2iDZ9[", new Object());
            Assert.assertEquals("_$61hVUea_i_P_2iDZ9_", nameAllocator.newName("-$61hVUea/i|P[2iDZ9[", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("usage_sd97797_sd98081 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).codePointAt(-136974548);
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void usage_sd97797_add98084_failAssert3_sd101605() throws Exception {
        try {
            Object __DSPOT_tag_45492 = new Object();
            String __DSPOT_suggestion_45491 = "wKkR<gvncfw>$<3TO/o-";
            Object __DSPOT_tag_44718 = new Object();
            String __DSPOT_suggestion_44717 = "-$61hVUea/i|P[2iDZ9[";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_sd97797_add98084_failAssert3_sd101605__11 = nameAllocator.newName(__DSPOT_suggestion_44717, __DSPOT_tag_44718);
            Assert.assertEquals("_$61hVUea_i_P_2iDZ9_", nameAllocator.newName(__DSPOT_suggestion_44717, __DSPOT_tag_44718));
            String o_usage_sd97797__6 = nameAllocator.newName(__DSPOT_suggestion_44717, __DSPOT_tag_44718);
            org.junit.Assert.fail("usage_sd97797_add98084 should have thrown IllegalArgumentException");
            nameAllocator.newName("wKkR<gvncfw>$<3TO/o-", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void usage_sd97797_sd98083litString98939() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd97797__6 = nameAllocator.newName("_ab", new Object());
        Assert.assertEquals("_ab", o_usage_sd97797__6);
        Assert.assertEquals("_rl1hc_gdpZS_fLX_gNH", nameAllocator.newName("?rl1hc(gdpZS(fLX!gNH", new Object()));
    }

    @Test(timeout = 50000)
    public void usage_sd97797_sd98083litString98925() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd97797__6 = nameAllocator.newName("-$61hVUea/]i|P[2iDZ9[", new Object());
        Assert.assertEquals("_$61hVUea__i_P_2iDZ9_", o_usage_sd97797__6);
        Assert.assertEquals("_rl1hc_gdpZS_fLX_gNH", nameAllocator.newName("?rl1hc(gdpZS(fLX!gNH", new Object()));
    }

    @Test(timeout = 50000)
    public void usage_sd97797_sd98083litString98914() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd97797__6 = nameAllocator.newName("-$61hVUea/i|P[2iDZ9[", new Object());
        Assert.assertEquals("_$61hVUea_i_P_2iDZ9_", o_usage_sd97797__6);
        Assert.assertEquals("_1ab", nameAllocator.newName("1ab", new Object()));
    }

    @Test(timeout = 50000)
    public void usage_sd97797_sd98082_sd100489() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd97797__6 = nameAllocator.newName("-$61hVUea/i|P[2iDZ9[", new Object());
        Assert.assertEquals("_$61hVUea_i_P_2iDZ9_", o_usage_sd97797__6);
        String o_usage_sd97797_sd98082__11 = nameAllocator.newName("ZSX2Czl6WqP%?QG/h*?h");
        Assert.assertEquals("ZSX2Czl6WqP__QG_h__h", o_usage_sd97797_sd98082__11);
        nameAllocator.clone();
    }

    @Test(timeout = 50000)
    public void usage_sd97797_sd98082_add100494() throws Exception {
        String __DSPOT_suggestion_44724 = "ZSX2Czl6WqP%?QG/h*?h";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd97797__6 = nameAllocator.newName("-$61hVUea/i|P[2iDZ9[", new Object());
        Assert.assertEquals("_$61hVUea_i_P_2iDZ9_", o_usage_sd97797__6);
        String o_usage_sd97797_sd98082_add100494__11 = nameAllocator.newName(__DSPOT_suggestion_44724);
        Assert.assertEquals("ZSX2Czl6WqP__QG_h__h", o_usage_sd97797_sd98082_add100494__11);
        Assert.assertEquals("ZSX2Czl6WqP__QG_h__h_", nameAllocator.newName(__DSPOT_suggestion_44724));
    }

    @Test(timeout = 50000)
    public void usage_sd97797_sd98082litString100458() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd97797__6 = nameAllocator.newName("-$61hVUea/i|P[2iDZ9[", new Object());
        Assert.assertEquals("_$61hVUea_i_P_2iDZ9_", o_usage_sd97797__6);
        Assert.assertEquals("public_", nameAllocator.newName("public"));
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102428() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("__J___w4_m_Pz_1Y5$Il", nameAllocator.newName("([J(_`w4&m*Pz`1Y5$Il"));
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102429() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_1i_C_DO__n31s_XurX3f", nameAllocator.newName("1i|C_DO_)n31s%XurX3f", new Object()));
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102427_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_45696 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.get(new Object());
            org.junit.Assert.fail("useBeforeAllocateForbidden_sd102427 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102429_sd102823() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd102429__10 = nameAllocator.newName("1i|C_DO_)n31s%XurX3f", new Object());
        Assert.assertEquals("_1i_C_DO__n31s_XurX3f", o_useBeforeAllocateForbidden_sd102429__10);
        Assert.assertEquals("XO_ZCzOJ_My_AwJ4jo__", nameAllocator.newName("XO.ZCzOJ:My^AwJ4jo][", new Object()));
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102428litString102727() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("", nameAllocator.newName(""));
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102428_add102795() throws Exception {
        String __DSPOT_suggestion_45697 = "([J(_`w4&m*Pz`1Y5$Il";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd102428_add102795__8 = nameAllocator.newName(__DSPOT_suggestion_45697);
        Assert.assertEquals("__J___w4_m_Pz_1Y5$Il", o_useBeforeAllocateForbidden_sd102428_add102795__8);
        Assert.assertEquals("__J___w4_m_Pz_1Y5$Il_", nameAllocator.newName(__DSPOT_suggestion_45697));
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102429_sd102835() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            Object __DSPOT_arg0_45849 = new Object();
            String __DSPOT_invoc_8 = nameAllocator.get(1);
            nameAllocator.get(1).equals(new Object());
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_1i_C_DO__n31s_XurX3f", nameAllocator.newName("1i|C_DO_)n31s%XurX3f", new Object()));
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102428_sd102791() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            String __DSPOT_invoc_6 = nameAllocator.get(1);
            nameAllocator.get(1).toUpperCase();
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("__J___w4_m_Pz_1Y5$Il", nameAllocator.newName("([J(_`w4&m*Pz`1Y5$Il"));
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102429litString102798() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("public_", nameAllocator.newName("public", new Object()));
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102428_add102795_sd107988() throws Exception {
        String __DSPOT_suggestion_45697 = "([J(_`w4&m*Pz`1Y5$Il";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            int __DSPOT_arg3_49280 = -1550907855;
            int __DSPOT_arg2_49279 = -2041919893;
            String __DSPOT_arg1_49278 = "=4/-]L-D&nA+ua}Ixi&x";
            int __DSPOT_arg0_49277 = 1919177547;
            String __DSPOT_invoc_6 = nameAllocator.get(1);
            nameAllocator.get(1).regionMatches(1919177547, "=4/-]L-D&nA+ua}Ixi&x", -2041919893, -1550907855);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd102428_add102795__8 = nameAllocator.newName(__DSPOT_suggestion_45697);
        Assert.assertEquals("__J___w4_m_Pz_1Y5$Il", o_useBeforeAllocateForbidden_sd102428_add102795__8);
        Assert.assertEquals("__J___w4_m_Pz_1Y5$Il_", nameAllocator.newName(__DSPOT_suggestion_45697));
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102429_sd102855_sd103649() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            int __DSPOT_arg3_46057 = -1545150281;
            byte[] __DSPOT_arg2_46056 = new byte[0];
            int __DSPOT_arg1_46055 = 1332975385;
            int __DSPOT_arg0_46054 = -558880186;
            int __DSPOT_arg4_45879 = -713726239;
            int __DSPOT_arg3_45878 = 755384199;
            String __DSPOT_arg2_45877 = "qT6M10? AJQ!&>us-d)(";
            int __DSPOT_arg1_45876 = 859891656;
            boolean __DSPOT_arg0_45875 = false;
            String __DSPOT_invoc_8 = nameAllocator.get(1);
            nameAllocator.get(1).regionMatches(false, 859891656, __DSPOT_arg2_45877, 755384199, -713726239);
            __DSPOT_arg2_45877.getBytes(-558880186, 1332975385, new byte[0], -1545150281);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_1i_C_DO__n31s_XurX3f", nameAllocator.newName("1i|C_DO_)n31s%XurX3f", new Object()));
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102428_sd102743_sd112867() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            String __DSPOT_arg0_52937 = "Al-=!Qh6jpnTChAIP=hg";
            String __DSPOT_invoc_9 = nameAllocator.get(1);
            nameAllocator.get(1).getBytes("Al-=!Qh6jpnTChAIP=hg");
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd102428__8 = nameAllocator.newName("([J(_`w4&m*Pz`1Y5$Il");
        Assert.assertEquals("__J___w4_m_Pz_1Y5$Il", o_useBeforeAllocateForbidden_sd102428__8);
        Assert.assertEquals("___5___WF5K_qE____PT", nameAllocator.newName("#*(5}>)WF5K>qE];).PT", new Object()));
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102429_sd102823_add106761_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_45837 = new Object();
            String __DSPOT_suggestion_45836 = "XO.ZCzOJ:My^AwJ4jo][";
            Object __DSPOT_tag_45699 = new Object();
            String __DSPOT_suggestion_45698 = "1i|C_DO_)n31s%XurX3f";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.newName(__DSPOT_suggestion_45698, __DSPOT_tag_45699);
            String o_useBeforeAllocateForbidden_sd102429__10 = nameAllocator.newName(__DSPOT_suggestion_45698, __DSPOT_tag_45699);
            String o_useBeforeAllocateForbidden_sd102429_sd102823__17 = nameAllocator.newName("XO.ZCzOJ:My^AwJ4jo][", new Object());
            org.junit.Assert.fail("useBeforeAllocateForbidden_sd102429_sd102823_add106761 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void useBeforeAllocateForbidden_sd102429_sd102822_sd112357() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            String __DSPOT_arg0_52642 = "July!rGE&8_E,4>Q|2$,";
            String __DSPOT_invoc_9 = nameAllocator.get(1);
            nameAllocator.get(1).endsWith("July!rGE&8_E,4>Q|2$,");
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd102429__10 = nameAllocator.newName("1i|C_DO_)n31s%XurX3f", new Object());
        Assert.assertEquals("_1i_C_DO__n31s_XurX3f", o_useBeforeAllocateForbidden_sd102429__10);
        Assert.assertEquals("Muw5aNDV__CRjQN228Av", nameAllocator.newName("Muw5aNDV;>CRjQN228Av"));
    }
}

