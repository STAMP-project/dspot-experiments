package com.squareup.javapoet;


import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public final class AmplNameAllocatorTest {
    @Test(timeout = 10000)
    public void tagReuseForbidden() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbidden__3);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd44_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_0 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_sd44 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46() throws Exception {
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", new NameAllocator().newName("X(r!Fs2l>UgIvC=TU&zg", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_add48() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", 1));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd45() throws Exception {
        Assert.assertEquals("__$QV5_Wz2___mr6__Vt", new NameAllocator().newName("[{$QV5:Wz2[|+mr6#-Vt"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46litString225() throws Exception {
        Assert.assertEquals("bar", new NameAllocator().newName("bar", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46litString236() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd45litString211() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 10000)
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

    @Test(timeout = 10000)
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

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd247() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("__Bob5_83OI__k_a8_J8", nameAllocator.newName(">[Bob5_83OI`-k-a8(J8", new Object()));
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46litString235() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd45_add223() throws Exception {
        String __DSPOT_suggestion_1 = "[{$QV5:Wz2[|+mr6#-Vt";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd45_add223__4 = nameAllocator.newName(__DSPOT_suggestion_1);
        Assert.assertEquals("__$QV5_Wz2___mr6__Vt", o_characterMappingInvalidStartButValidPart_sd45_add223__4);
        Assert.assertEquals("__$QV5_Wz2___mr6__Vt_", nameAllocator.newName(__DSPOT_suggestion_1));
        Assert.assertEquals("__$QV5_Wz2___mr6__Vt", o_characterMappingInvalidStartButValidPart_sd45_add223__4);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd45_sd219() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd45__4 = nameAllocator.newName("[{$QV5:Wz2[|+mr6#-Vt");
        Assert.assertEquals("__$QV5_Wz2___mr6__Vt", o_characterMappingInvalidStartButValidPart_sd45__4);
        nameAllocator.clone();
        Assert.assertEquals("__$QV5_Wz2___mr6__Vt", o_characterMappingInvalidStartButValidPart_sd45__4);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd45litString214() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd45_sd222litString1404() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd45__4 = nameAllocator.newName("public");
        Assert.assertEquals("public_", o_characterMappingInvalidStartButValidPart_sd45__4);
        Assert.assertEquals("k_201yCi_OdwpauR_h1_", nameAllocator.newName("k*201yCi*OdwpauR%h1,", new Object()));
        Assert.assertEquals("public_", o_characterMappingInvalidStartButValidPart_sd45__4);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd246_add2071() throws Exception {
        String __DSPOT_suggestion_9 = ">YSe|%xHdm7#=ToX)D7x";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
        String o_characterMappingInvalidStartButValidPart_sd46_sd246_add2071__11 = nameAllocator.newName(__DSPOT_suggestion_9);
        Assert.assertEquals("_YSe__xHdm7__ToX_D7x", o_characterMappingInvalidStartButValidPart_sd46_sd246_add2071__11);
        Assert.assertEquals("_YSe__xHdm7__ToX_D7x_", nameAllocator.newName(__DSPOT_suggestion_9));
        Assert.assertEquals("_YSe__xHdm7__ToX_D7x", o_characterMappingInvalidStartButValidPart_sd46_sd246_add2071__11);
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd244_sd2022() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("I_2sN3u__h__LLuBuAr_", nameAllocator.clone().newName("I-2sN3u/#h+}LLuBuAr^", new Object()));
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd45_sd222litString1374() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd45__4 = nameAllocator.newName("[{$QV5:Wz2[|+mr6#-Vt");
        Assert.assertEquals("__$QV5_Wz2___mr6__Vt", o_characterMappingInvalidStartButValidPart_sd45__4);
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", nameAllocator.newName("tag 1 cannot be used for both 'foo' and 'bar'", new Object()));
        Assert.assertEquals("__$QV5_Wz2___mr6__Vt", o_characterMappingInvalidStartButValidPart_sd45__4);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd245_failAssert1_sd3061() throws Exception {
        try {
            int __DSPOT_arg3_502 = 836151974;
            byte[] __DSPOT_arg2_501 = new byte[]{ 70 };
            int __DSPOT_arg1_500 = 264504726;
            int __DSPOT_arg0_499 = 322386401;
            Object __DSPOT_tag_8 = new Object();
            Object __DSPOT_tag_3 = new Object();
            String __DSPOT_suggestion_2 = "X(r!Fs2l>UgIvC=TU&zg";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
            Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_sd46_sd245 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).getBytes(322386401, 264504726, new byte[]{ 70 }, 836151974);
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd247litString2106() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("1ab", new Object());
        Assert.assertEquals("_1ab", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("__Bob5_83OI__k_a8_J8", nameAllocator.newName(">[Bob5_83OI`-k-a8(J8", new Object()));
        Assert.assertEquals("_1ab", o_characterMappingInvalidStartButValidPart_sd46__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd247litString2105() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("", new Object());
        Assert.assertEquals("", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("__Bob5_83OI__k_a8_J8", nameAllocator.newName(">[Bob5_83OI`-k-a8(J8", new Object()));
        Assert.assertEquals("", o_characterMappingInvalidStartButValidPart_sd46__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd246_add2070_failAssert5() throws Exception {
        try {
            String __DSPOT_suggestion_9 = ">YSe|%xHdm7#=ToX)D7x";
            Object __DSPOT_tag_3 = new Object();
            String __DSPOT_suggestion_2 = "X(r!Fs2l>UgIvC=TU&zg";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
            String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName(__DSPOT_suggestion_2, __DSPOT_tag_3);
            String o_characterMappingInvalidStartButValidPart_sd46_sd246__11 = nameAllocator.newName(">YSe|%xHdm7#=ToX)D7x");
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_sd46_sd246_add2070 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd246litString2038() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("a_b", nameAllocator.newName("a_b"));
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd247litString2107() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("_1ab", new Object());
        Assert.assertEquals("_1ab", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("__Bob5_83OI__k_a8_J8", nameAllocator.newName(">[Bob5_83OI`-k-a8(J8", new Object()));
        Assert.assertEquals("_1ab", o_characterMappingInvalidStartButValidPart_sd46__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3988_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_758 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3988 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_add3992() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab", 1));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990() throws Exception {
        Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", new NameAllocator().newName("gAzGTF;K^aA[n|M]WL!p", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989() throws Exception {
        Assert.assertEquals("_8yrwa_pGW_Cd__LVcx9V", new NameAllocator().newName("8yrwa/pGW(Cd|#LVcx9V"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989litString4148() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a-b"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990litString4173() throws Exception {
        Assert.assertEquals("foo_", new NameAllocator().newName("foo_", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990litString4183() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_sd4189_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_766 = new Object();
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "gAzGTF;K^aA[n|M]WL!p";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_sd3990__6 = nameAllocator.newName("gAzGTF;K^aA[n|M]WL!p", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3990_sd4189 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990litString4175() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990litString4186() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989litString4160() throws Exception {
        Assert.assertEquals("_8yrwa_pGW_Cdz_LVcx9V", new NameAllocator().newName("8yrwa/pGW(Cdz#LVcx9V"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_add4192_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "gAzGTF;K^aA[n|M]WL!p";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            String o_characterMappingInvalidStartIsInvalidPart_sd3990__6 = nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3990_add4192 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989_add4167() throws Exception {
        String __DSPOT_suggestion_759 = "8yrwa/pGW(Cd|#LVcx9V";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3989_add4167__4 = nameAllocator.newName(__DSPOT_suggestion_759);
        Assert.assertEquals("_8yrwa_pGW_Cd__LVcx9V", o_characterMappingInvalidStartIsInvalidPart_sd3989_add4167__4);
        Assert.assertEquals("_8yrwa_pGW_Cd__LVcx9V_", nameAllocator.newName(__DSPOT_suggestion_759));
        Assert.assertEquals("_8yrwa_pGW_Cd__LVcx9V", o_characterMappingInvalidStartIsInvalidPart_sd3989_add4167__4);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_sd4189_failAssert1_sd6989() throws Exception {
        try {
            Object __DSPOT_tag_1243 = new Object();
            String __DSPOT_suggestion_1242 = "w=ic8:KL-L74r1@oKmx5";
            Object __DSPOT_tag_766 = new Object();
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "gAzGTF;K^aA[n|M]WL!p";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_sd3990__6 = nameAllocator.newName("gAzGTF;K^aA[n|M]WL!p", new Object());
            Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", nameAllocator.newName("gAzGTF;K^aA[n|M]WL!p", new Object()));
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3990_sd4189 should have thrown IllegalArgumentException");
            nameAllocator.newName("w=ic8:KL-L74r1@oKmx5", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_sd4188_sd5963() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3990__6 = nameAllocator.newName("gAzGTF;K^aA[n|M]WL!p", new Object());
        Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", o_characterMappingInvalidStartIsInvalidPart_sd3990__6);
        Assert.assertEquals("_8ENt_qB9_4PNg__p__5_", nameAllocator.clone().newName("8ENt*qB9^4PNg#%p_}5^", new Object()));
        Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", o_characterMappingInvalidStartIsInvalidPart_sd3990__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_sd4191litString6024() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3990__6 = nameAllocator.newName("gAzGTF;K^aA[n|M]WL!p", new Object());
        Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", o_characterMappingInvalidStartIsInvalidPart_sd3990__6);
        Assert.assertEquals("_1ab", nameAllocator.newName("1ab", new Object()));
        Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", o_characterMappingInvalidStartIsInvalidPart_sd3990__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989_sd4166litString5330() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3989__4 = nameAllocator.newName("8yrwa/pGW(Cd|#LVcx9V");
        Assert.assertEquals("_8yrwa_pGW_Cd__LVcx9V", o_characterMappingInvalidStartIsInvalidPart_sd3989__4);
        Assert.assertEquals("_ab", nameAllocator.newName("&ab", new Object()));
        Assert.assertEquals("_8yrwa_pGW_Cd__LVcx9V", o_characterMappingInvalidStartIsInvalidPart_sd3989__4);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_sd4190litString5998() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3990__6 = nameAllocator.newName("public_", new Object());
        Assert.assertEquals("public_", o_characterMappingInvalidStartIsInvalidPart_sd3990__6);
        Assert.assertEquals("AJ_2f_OJ_sljavE08z_k", nameAllocator.newName("AJ/2f&OJ[sljavE08z|k"));
        Assert.assertEquals("public_", o_characterMappingInvalidStartIsInvalidPart_sd3990__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_sd4190litString5982() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3990__6 = nameAllocator.newName("gAzGTF;K^aA[n|M]WL!p", new Object());
        Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", o_characterMappingInvalidStartIsInvalidPart_sd3990__6);
        Assert.assertEquals("public_", nameAllocator.newName("public_"));
        Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", o_characterMappingInvalidStartIsInvalidPart_sd3990__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_sd4190litString5985() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3990__6 = nameAllocator.newName("gAzGTF;K^aA[n|M]WL!p", new Object());
        Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", o_characterMappingInvalidStartIsInvalidPart_sd3990__6);
        Assert.assertEquals("public_", nameAllocator.newName("public"));
        Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", o_characterMappingInvalidStartIsInvalidPart_sd3990__6);
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_sd4190_add6011_failAssert6() throws Exception {
        try {
            String __DSPOT_suggestion_767 = "AJ/2f&OJ[sljavE08z|k";
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "gAzGTF;K^aA[n|M]WL!p";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            String o_characterMappingInvalidStartIsInvalidPart_sd3990__6 = nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            String o_characterMappingInvalidStartIsInvalidPart_sd3990_sd4190__11 = nameAllocator.newName("AJ/2f&OJ[sljavE08z|k");
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3990_sd4190_add6011 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_sd4190_add6012() throws Exception {
        String __DSPOT_suggestion_767 = "AJ/2f&OJ[sljavE08z|k";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3990__6 = nameAllocator.newName("gAzGTF;K^aA[n|M]WL!p", new Object());
        Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", o_characterMappingInvalidStartIsInvalidPart_sd3990__6);
        String o_characterMappingInvalidStartIsInvalidPart_sd3990_sd4190_add6012__11 = nameAllocator.newName(__DSPOT_suggestion_767);
        Assert.assertEquals("AJ_2f_OJ_sljavE08z_k", o_characterMappingInvalidStartIsInvalidPart_sd3990_sd4190_add6012__11);
        Assert.assertEquals("AJ_2f_OJ_sljavE08z_k_", nameAllocator.newName(__DSPOT_suggestion_767));
        Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", o_characterMappingInvalidStartIsInvalidPart_sd3990__6);
        Assert.assertEquals("AJ_2f_OJ_sljavE08z_k", o_characterMappingInvalidStartIsInvalidPart_sd3990_sd4190_add6012__11);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7930_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_1516 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("characterMappingSubstitute_sd7930 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7931() throws Exception {
        Assert.assertEquals("_Zz_PAfs_apO_L4y1__c", new NameAllocator().newName("[Zz]PAfs[apO+L4y1=+c"));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932() throws Exception {
        Assert.assertEquals("Bv9F_d_C__mA___dN__X", new NameAllocator().newName("Bv9F#d.C@!mA *{dN}*X", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932_add8134_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "Bv9F#d.C@!mA *{dN}*X";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            String o_characterMappingSubstitute_sd7932__6 = nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            org.junit.Assert.fail("characterMappingSubstitute_sd7932_add8134 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7931_add8109() throws Exception {
        String __DSPOT_suggestion_1517 = "[Zz]PAfs[apO+L4y1=+c";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7931_add8109__4 = nameAllocator.newName(__DSPOT_suggestion_1517);
        Assert.assertEquals("_Zz_PAfs_apO_L4y1__c", o_characterMappingSubstitute_sd7931_add8109__4);
        Assert.assertEquals("_Zz_PAfs_apO_L4y1__c_", nameAllocator.newName(__DSPOT_suggestion_1517));
        Assert.assertEquals("_Zz_PAfs_apO_L4y1__c", o_characterMappingSubstitute_sd7931_add8109__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7931litString8088() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932_sd8131_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_1524 = new Object();
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "Bv9F#d.C@!mA *{dN}*X";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_sd7932__6 = nameAllocator.newName("Bv9F#d.C@!mA *{dN}*X", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSubstitute_sd7932_sd8131 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7931litString8098() throws Exception {
        Assert.assertEquals("_Zz_PAfs_apO_4y1__c", new NameAllocator().newName("[Zz]PAfs[apO+4y1=+c"));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932litString8120() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_add7934litString8151() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("_ab", 1));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_add7934litString8152() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", 1));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932_sd8133litString9988() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7932__6 = nameAllocator.newName("public", new Object());
        Assert.assertEquals("public_", o_characterMappingSubstitute_sd7932__6);
        Assert.assertEquals("_Z5_6h__V_5mdsip___O", nameAllocator.newName("?Z5:6h@`V(5mdsip!%;O", new Object()));
        Assert.assertEquals("public_", o_characterMappingSubstitute_sd7932__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932_sd8132litString9932() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7932__6 = nameAllocator.newName("bar", new Object());
        Assert.assertEquals("bar", o_characterMappingSubstitute_sd7932__6);
        Assert.assertEquals("_8_W7Ygs_d___k8_f1NY", nameAllocator.newName("(8=W7Ygs|d/=?k8+f1NY"));
        Assert.assertEquals("bar", o_characterMappingSubstitute_sd7932__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932_sd8131_failAssert1_sd10967() throws Exception {
        try {
            char __DSPOT_arg1_2049 = ']';
            char __DSPOT_arg0_2048 = 'R';
            Object __DSPOT_tag_1524 = new Object();
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "Bv9F#d.C@!mA *{dN}*X";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_sd7932__6 = nameAllocator.newName("Bv9F#d.C@!mA *{dN}*X", new Object());
            Assert.assertEquals("Bv9F_d_C__mA___dN__X", nameAllocator.newName("Bv9F#d.C@!mA *{dN}*X", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSubstitute_sd7932_sd8131 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).replace('R', ']');
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932_add8134_failAssert2_sd11009() throws Exception {
        try {
            Object __DSPOT_tag_2070 = new Object();
            String __DSPOT_suggestion_2069 = "<ls2,tD++1V]c}>l;)Y[";
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "Bv9F#d.C@!mA *{dN}*X";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_sd7932_add8134_failAssert2_sd11009__11 = nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            Assert.assertEquals("Bv9F_d_C__mA___dN__X", nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519));
            String o_characterMappingSubstitute_sd7932__6 = nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            org.junit.Assert.fail("characterMappingSubstitute_sd7932_add8134 should have thrown IllegalArgumentException");
            nameAllocator.newName("<ls2,tD++1V]c}>l;)Y[", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7931_sd8108_add9299() throws Exception {
        String __DSPOT_suggestion_1517 = "[Zz]PAfs[apO+L4y1=+c";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7931_sd8108_add9299__7 = nameAllocator.newName(__DSPOT_suggestion_1517);
        Assert.assertEquals("_Zz_PAfs_apO_L4y1__c", o_characterMappingSubstitute_sd7931_sd8108_add9299__7);
        String o_characterMappingSubstitute_sd7931__4 = nameAllocator.newName(__DSPOT_suggestion_1517);
        Assert.assertEquals("_Zz_PAfs_apO_L4y1__c_", o_characterMappingSubstitute_sd7931__4);
        Assert.assertEquals("__g_WJu4_q8_3_lH___l", nameAllocator.newName("}@g/WJu4%q8 3 lH#<.l", new Object()));
        Assert.assertEquals("_Zz_PAfs_apO_L4y1__c", o_characterMappingSubstitute_sd7931_sd8108_add9299__7);
        Assert.assertEquals("_Zz_PAfs_apO_L4y1__c_", o_characterMappingSubstitute_sd7931__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932_sd8130_sd9907() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7932__6 = nameAllocator.newName("Bv9F#d.C@!mA *{dN}*X", new Object());
        Assert.assertEquals("Bv9F_d_C__mA___dN__X", o_characterMappingSubstitute_sd7932__6);
        Assert.assertEquals("HT____Cr_u_DA_1$O1t_", nameAllocator.clone().newName("HT}[;&Cr@u%DA?1$O1t&", new Object()));
        Assert.assertEquals("Bv9F_d_C__mA___dN__X", o_characterMappingSubstitute_sd7932__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932_sd8133litString9990() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7932__6 = nameAllocator.newName("1ab", new Object());
        Assert.assertEquals("_1ab", o_characterMappingSubstitute_sd7932__6);
        Assert.assertEquals("_Z5_6h__V_5mdsip___O", nameAllocator.newName("?Z5:6h@`V(5mdsip!%;O", new Object()));
        Assert.assertEquals("_1ab", o_characterMappingSubstitute_sd7932__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11876() throws Exception {
        Assert.assertEquals("_FCV_50__7_4_32_H___", new NameAllocator().newName("-FCV!50+`7*4!32@H:/*"));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11877() throws Exception {
        Assert.assertEquals("jAd__I_v___b8d_0i1sO", new NameAllocator().newName("jAd>@I*v{ (b8d<0i1sO", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11875_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_2274 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("characterMappingSurrogate_sd11875 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11877_sd12076_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_2282 = new Object();
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "jAd>@I*v{ (b8d<0i1sO";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_sd11877__6 = nameAllocator.newName("jAd>@I*v{ (b8d<0i1sO", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSurrogate_sd11877_sd12076 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11876litString12040() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11876litString12038() throws Exception {
        Assert.assertEquals("_FCV_50__7_4_32____", new NameAllocator().newName("-FCV!50+`7*4!32@:/*"));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11876_add12054() throws Exception {
        String __DSPOT_suggestion_2275 = "-FCV!50+`7*4!32@H:/*";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11876_add12054__4 = nameAllocator.newName(__DSPOT_suggestion_2275);
        Assert.assertEquals("_FCV_50__7_4_32_H___", o_characterMappingSurrogate_sd11876_add12054__4);
        Assert.assertEquals("_FCV_50__7_4_32_H____", nameAllocator.newName(__DSPOT_suggestion_2275));
        Assert.assertEquals("_FCV_50__7_4_32_H___", o_characterMappingSurrogate_sd11876_add12054__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11877litString12056() throws Exception {
        Assert.assertEquals("foo", new NameAllocator().newName("foo", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11877litString12067() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11877_add12079_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "jAd>@I*v{ (b8d<0i1sO";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            String o_characterMappingSurrogate_sd11877__6 = nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            org.junit.Assert.fail("characterMappingSurrogate_sd11877_add12079 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11877_sd12076_failAssert1_sd14879() throws Exception {
        try {
            Object __DSPOT_tag_2759 = new Object();
            String __DSPOT_suggestion_2758 = "C#p/6wfQTp@b6xt<S6}7";
            Object __DSPOT_tag_2282 = new Object();
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "jAd>@I*v{ (b8d<0i1sO";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_sd11877__6 = nameAllocator.newName("jAd>@I*v{ (b8d<0i1sO", new Object());
            Assert.assertEquals("jAd__I_v___b8d_0i1sO", nameAllocator.newName("jAd>@I*v{ (b8d<0i1sO", new Object()));
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSurrogate_sd11877_sd12076 should have thrown IllegalArgumentException");
            nameAllocator.newName("C#p/6wfQTp@b6xt<S6}7", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11876_sd12053litString13215() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11876__4 = nameAllocator.newName("-FCV!50+`7*4!32@H:/*");
        Assert.assertEquals("_FCV_50__7_4_32_H___", o_characterMappingSurrogate_sd11876__4);
        Assert.assertEquals("_1ab", nameAllocator.newName("1ab", new Object()));
        Assert.assertEquals("_FCV_50__7_4_32_H___", o_characterMappingSurrogate_sd11876__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11876_sd12053litString13238() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11876__4 = nameAllocator.newName("public");
        Assert.assertEquals("public_", o_characterMappingSurrogate_sd11876__4);
        Assert.assertEquals("A_VeQ9$_KmJB_wq__Y18", nameAllocator.newName("A|VeQ9$(KmJB[wq([Y18", new Object()));
        Assert.assertEquals("public_", o_characterMappingSurrogate_sd11876__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11877_add12079_failAssert2_sd14955() throws Exception {
        try {
            Object __DSPOT_tag_2828 = new Object();
            String __DSPOT_suggestion_2827 = "Th.,2+9]ReH$h|0U<GwR";
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "jAd>@I*v{ (b8d<0i1sO";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_sd11877_add12079_failAssert2_sd14955__11 = nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            Assert.assertEquals("jAd__I_v___b8d_0i1sO", nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277));
            String o_characterMappingSurrogate_sd11877__6 = nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            org.junit.Assert.fail("characterMappingSurrogate_sd11877_add12079 should have thrown IllegalArgumentException");
            nameAllocator.newName("Th.,2+9]ReH$h|0U<GwR", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11877_sd12075_sd13850() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11877__6 = nameAllocator.newName("jAd>@I*v{ (b8d<0i1sO", new Object());
        Assert.assertEquals("jAd__I_v___b8d_0i1sO", o_characterMappingSurrogate_sd11877__6);
        Assert.assertEquals("__qmlRn0G_v_7YpIKVY_", nameAllocator.clone().newName("_[qmlRn0G-v]7YpIKVY>", new Object()));
        Assert.assertEquals("jAd__I_v___b8d_0i1sO", o_characterMappingSurrogate_sd11877__6);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11876_sd12053litString13236() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11876__4 = nameAllocator.newName("-CV!50+`7*4!32@H:/*");
        Assert.assertEquals("_CV_50__7_4_32_H___", o_characterMappingSurrogate_sd11876__4);
        Assert.assertEquals("A_VeQ9$_KmJB_wq__Y18", nameAllocator.newName("A|VeQ9$(KmJB[wq([Y18", new Object()));
        Assert.assertEquals("_CV_50__7_4_32_H___", o_characterMappingSurrogate_sd11876__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11876_sd12053litString13231() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11876__4 = nameAllocator.newName("_1ab");
        Assert.assertEquals("_1ab", o_characterMappingSurrogate_sd11876__4);
        Assert.assertEquals("A_VeQ9$_KmJB_wq__Y18", nameAllocator.newName("A|VeQ9$(KmJB[wq([Y18", new Object()));
        Assert.assertEquals("_1ab", o_characterMappingSurrogate_sd11876__4);
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11877_sd12077_add13899() throws Exception {
        String __DSPOT_suggestion_2283 = "b=sgo(}L,VAY[6a{4}7X";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11877__6 = nameAllocator.newName("jAd>@I*v{ (b8d<0i1sO", new Object());
        Assert.assertEquals("jAd__I_v___b8d_0i1sO", o_characterMappingSurrogate_sd11877__6);
        String o_characterMappingSurrogate_sd11877_sd12077_add13899__11 = nameAllocator.newName(__DSPOT_suggestion_2283);
        Assert.assertEquals("b_sgo__L_VAY_6a_4_7X", o_characterMappingSurrogate_sd11877_sd12077_add13899__11);
        Assert.assertEquals("b_sgo__L_VAY_6a_4_7X_", nameAllocator.newName(__DSPOT_suggestion_2283));
        Assert.assertEquals("b_sgo__L_VAY_6a_4_7X", o_characterMappingSurrogate_sd11877_sd12077_add13899__11);
        Assert.assertEquals("jAd__I_v___b8d_0i1sO", o_characterMappingSurrogate_sd11877__6);
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString15793() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString15793__3 = outterAllocator.newName("public", 1);
        Assert.assertEquals("public_", o_cloneUsagelitString15793__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("public_", o_cloneUsagelitString15793__3);
    }

    @Test(timeout = 10000)
    public void cloneUsage_add16032_failAssert18() throws Exception {
        try {
            NameAllocator outterAllocator = new NameAllocator();
            outterAllocator.newName("foo", 1);
            outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            org.junit.Assert.fail("cloneUsage_add16032 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15977() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15977__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15977__6);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("_N__AA_F_1G8_l$UVzU3", outterAllocator.clone().newName("|N(<AA[F*1G8:l$UVzU3", new Object()));
        Assert.assertEquals("foo", o_cloneUsage_sd15977__6);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15976() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15976__4 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15976__4);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("q_N_Z___2G_X_n4G__ai", outterAllocator.clone().newName("q)N>Z{@(2G[X+n4G*-ai"));
        Assert.assertEquals("foo", o_cloneUsage_sd15976__4);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15971_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_3032 = new Object();
            NameAllocator outterAllocator = new NameAllocator();
            outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            outterAllocator.get(new Object());
            org.junit.Assert.fail("cloneUsage_sd15971 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15980() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15980__4 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15980__4);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        Assert.assertEquals("_2x0c0Mp__d_Pdoi_T_X_", outterAllocator.clone().newName("2x0c0Mp;?d>Pdoi]T`X!"));
        Assert.assertEquals("foo", o_cloneUsage_sd15980__4);
    }

    @Test(timeout = 10000)
    public void cloneUsage_add16040() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_add16040__3 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_add16040__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        Assert.assertEquals("foo_", outterAllocator.clone().newName("foo", 2));
        Assert.assertEquals("foo", o_cloneUsage_add16040__3);
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString15778() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString15778__3 = outterAllocator.newName("bkPYJ.I Lc", 1);
        Assert.assertEquals("bkPYJ_I_Lc", o_cloneUsagelitString15778__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("bkPYJ_I_Lc", o_cloneUsagelitString15778__3);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16026() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("foo", outterAllocator.newName("foo", 1).toLowerCase());
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15981_add20523_failAssert17() throws Exception {
        try {
            Object __DSPOT_tag_3043 = new Object();
            String __DSPOT_suggestion_3042 = ")K8o9j6gEQQ)Fd.4L 0q";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_sd15981__6 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            innerAllocator2.newName(__DSPOT_suggestion_3042, __DSPOT_tag_3043);
            String o_cloneUsage_sd15981__11 = innerAllocator2.newName(__DSPOT_suggestion_3042, __DSPOT_tag_3043);
            org.junit.Assert.fail("cloneUsage_sd15981_add20523 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15980_sd20452() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15980__4 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15980__4);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        String o_cloneUsage_sd15980__9 = outterAllocator.clone().newName("2x0c0Mp;?d>Pdoi]T`X!");
        Assert.assertEquals("_2x0c0Mp__d_Pdoi_T_X_", o_cloneUsage_sd15980__9);
        Assert.assertEquals("b__k__wi_iSMZ__nim_f", outterAllocator.newName("b=`k.,wi<iSMZ[)nim/f", new Object()));
        Assert.assertEquals("_2x0c0Mp__d_Pdoi_T_X_", o_cloneUsage_sd15980__9);
        Assert.assertEquals("foo", o_cloneUsage_sd15980__4);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15979_failAssert2_sd24565() throws Exception {
        try {
            int __DSPOT_arg3_6936 = 1473657708;
            int __DSPOT_arg2_6935 = 1369986624;
            String __DSPOT_arg1_6934 = "Hc?A63w)>pBRt!}7nud<";
            int __DSPOT_arg0_6933 = -2134222680;
            Object __DSPOT_tag_3040 = new Object();
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_sd15979_failAssert2_sd24565__11 = outterAllocator.newName("foo", 1);
            Assert.assertEquals("foo", outterAllocator.newName("foo", 1));
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String __DSPOT_invoc_12 = outterAllocator.clone().get(new Object());
            org.junit.Assert.fail("cloneUsage_sd15979 should have thrown IllegalArgumentException");
            outterAllocator.clone().get(new Object()).regionMatches(-2134222680, "Hc?A63w)>pBRt!}7nud<", 1369986624, 1473657708);
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16014_sd22337() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        boolean o_cloneUsage_sd16014__14 = outterAllocator.newName("foo", 1).regionMatches(-1740842537, "-b62[$MuoQ9Jel}]?kr4", 545801629, -537965066);
        Assert.assertEquals("ZH_e_aE_S_A__E__Lfju", outterAllocator.newName("ZH(e@aE]S.A_(E%`Lfju", new Object()));
    }

    @Test(timeout = 10000)
    public void cloneUsage_add16037_sd23766() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_add16037__3 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_add16037__3);
        String o_cloneUsage_add16037__6 = outterAllocator.clone().newName("foo", 3);
        Assert.assertEquals("foo_", o_cloneUsage_add16037__6);
        Assert.assertEquals("woJd__uu_3_LeZ_n_p_1", outterAllocator.clone().newName("woJd%/uu[3!LeZ#n`p+1", new Object()));
        Assert.assertEquals("foo_", o_cloneUsage_add16037__6);
        Assert.assertEquals("foo", o_cloneUsage_add16037__3);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15977_sd20346_failAssert8() throws Exception {
        try {
            Object __DSPOT_tag_4281 = new Object();
            Object __DSPOT_tag_3039 = new Object();
            String __DSPOT_suggestion_3038 = "|N(<AA[F*1G8:l$UVzU3";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_sd15977__6 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_sd15977__11 = outterAllocator.clone().newName("|N(<AA[F*1G8:l$UVzU3", new Object());
            outterAllocator.get(new Object());
            org.junit.Assert.fail("cloneUsage_sd15977_sd20346 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16003_sd21588() throws Exception {
        String __DSPOT_arg0_3070 = "ZzQL`wn^n*2Sdr@IKX2Z";
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        int o_cloneUsage_sd16003__12 = outterAllocator.newName("foo", 1).indexOf(__DSPOT_arg0_3070, -1403789164);
        boolean o_cloneUsage_sd16003_sd21588__21 = __DSPOT_arg0_3070.regionMatches(false, -1531518647, "Q/9$L?lg ;(5},[q ;Vd", 370642262, -920199663);
        Assert.assertFalse(o_cloneUsage_sd16003_sd21588__21);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15977_sd20347() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15977__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15977__6);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_sd15977__11 = outterAllocator.clone().newName("|N(<AA[F*1G8:l$UVzU3", new Object());
        Assert.assertEquals("_N__AA_F_1G8_l$UVzU3", o_cloneUsage_sd15977__11);
        Assert.assertEquals("_9__Rl50X___lE_V6$_w", outterAllocator.newName("-9%|Rl50X;{_lE{V6$^w"));
        Assert.assertEquals("_N__AA_F_1G8_l$UVzU3", o_cloneUsage_sd15977__11);
        Assert.assertEquals("foo", o_cloneUsage_sd15977__6);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15973_sd20193_sd27208() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15973__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15973__6);
        String o_cloneUsage_sd15973__11 = outterAllocator.newName("d6.PxCHe/mJ!4;!mSOJ<", new Object());
        Assert.assertEquals("d6_PxCHe_mJ_4__mSOJ_", o_cloneUsage_sd15973__11);
        String o_cloneUsage_sd15973_sd20193__20 = outterAllocator.clone().newName("9:k&c1^LRdP5C[?MkXrV", new Object());
        Assert.assertEquals("_9_k_c1_LRdP5C__MkXrV", o_cloneUsage_sd15973_sd20193__20);
        Assert.assertEquals("m_xjhzp__LvWs_q3i_DR", outterAllocator.clone().newName("m`xjhzp &LvWs)q3i&DR", new Object()));
        Assert.assertEquals("_9_k_c1_LRdP5C__MkXrV", o_cloneUsage_sd15973_sd20193__20);
        Assert.assertEquals("foo", o_cloneUsage_sd15973__6);
        Assert.assertEquals("d6_PxCHe_mJ_4__mSOJ_", o_cloneUsage_sd15973__11);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15976_sd20289_sd30631() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15976__4 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15976__4);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_sd15976__9 = outterAllocator.clone().newName("q)N>Z{@(2G[X+n4G*-ai");
        Assert.assertEquals("q_N_Z___2G_X_n4G__ai", o_cloneUsage_sd15976__9);
        String o_cloneUsage_sd15976_sd20289__18 = outterAllocator.newName("x&;&R(INe=cV>i!W`]W6", new Object());
        Assert.assertEquals("x___R_INe_cV_i_W__W6", o_cloneUsage_sd15976_sd20289__18);
        Assert.assertEquals("_1K_W__vrs_RD_xZ_P__", outterAllocator.newName(",1K[W=!vrs(RD&xZ+P*&", new Object()));
        Assert.assertEquals("x___R_INe_cV_i_W__W6", o_cloneUsage_sd15976_sd20289__18);
        Assert.assertEquals("foo", o_cloneUsage_sd15976__4);
        Assert.assertEquals("q_N_Z___2G_X_n4G__ai", o_cloneUsage_sd15976__9);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15977_add20360_failAssert16litString39311() throws Exception {
        try {
            Object __DSPOT_tag_3039 = new Object();
            String __DSPOT_suggestion_3038 = "a-b";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_sd15977__6 = outterAllocator.newName("foo", 1);
            Assert.assertEquals("foo", outterAllocator.newName("foo", 1));
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_sd15977_add20360_failAssert16litString39311__15 = innerAllocator1.newName(__DSPOT_suggestion_3038, __DSPOT_tag_3039);
            Assert.assertEquals("a_b", innerAllocator1.newName(__DSPOT_suggestion_3038, __DSPOT_tag_3039));
            String o_cloneUsage_sd15977__11 = innerAllocator1.newName(__DSPOT_suggestion_3038, __DSPOT_tag_3039);
            org.junit.Assert.fail("cloneUsage_sd15977_add20360 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16016_sd22543_sd30480() throws Exception {
        String __DSPOT_arg1_3093 = "?j7$2%zDC6CPY#8v*eeR";
        String __DSPOT_arg0_3092 = "2ev!M]pK8%U<?:CpSi`t";
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_sd16016__12 = outterAllocator.newName("foo", 1).replaceAll(__DSPOT_arg0_3092, __DSPOT_arg1_3093);
        Assert.assertEquals("foo", o_cloneUsage_sd16016__12);
        boolean o_cloneUsage_sd16016_sd22543__21 = __DSPOT_arg1_3093.regionMatches(true, 932294162, "VAO2Cf%l 1<Vtu;)7a N", -1628150656, -323762951);
        boolean o_cloneUsage_sd16016_sd22543_sd30480__30 = __DSPOT_arg0_3092.regionMatches(true, 1863788455, "w0]nG)7!p)D+gCsAQNHH", 1180616583, 1445997338);
        Assert.assertFalse(o_cloneUsage_sd16016_sd22543_sd30480__30);
        Assert.assertEquals("foo", o_cloneUsage_sd16016__12);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15973_sd20185_sd26892() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15973__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15973__6);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        String o_cloneUsage_sd15973__11 = outterAllocator.newName("d6.PxCHe/mJ!4;!mSOJ<", new Object());
        Assert.assertEquals("d6_PxCHe_mJ_4__mSOJ_", o_cloneUsage_sd15973__11);
        String o_cloneUsage_sd15973_sd20185__20 = outterAllocator.newName("XX5EoW-`H{W^#A.O}d#Q", new Object());
        Assert.assertEquals("XX5EoW__H_W__A_O_d_Q", o_cloneUsage_sd15973_sd20185__20);
        Assert.assertEquals("_J4XKsCViK_VJ6_NU__a", outterAllocator.clone().newName(".J4XKsCViK<VJ6-NU(-a", new Object()));
        Assert.assertEquals("foo", o_cloneUsage_sd15973__6);
        Assert.assertEquals("d6_PxCHe_mJ_4__mSOJ_", o_cloneUsage_sd15973__11);
        Assert.assertEquals("XX5EoW__H_W__A_O_d_Q", o_cloneUsage_sd15973_sd20185__20);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15977_sd20350_failAssert11() throws Exception {
        try {
            Object __DSPOT_tag_4285 = new Object();
            Object __DSPOT_tag_3039 = new Object();
            String __DSPOT_suggestion_3038 = "|N(<AA[F*1G8:l$UVzU3";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_sd15977__6 = outterAllocator.newName("foo", 1);
            Assert.assertEquals("foo", outterAllocator.newName("foo", 1));
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_sd15977__11 = "+2^GLt38[%V:97_dqr;z";
            Assert.assertEquals("+2^GLt38[%V:97_dqr;z", "+2^GLt38[%V:97_dqr;z");
            outterAllocator.clone().get(new Object());
            org.junit.Assert.fail("cloneUsage_sd15977_sd20350 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40757_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_15828 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("javaKeyword_sd40757 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40758() throws Exception {
        Assert.assertEquals("xHG__Lt_oH_NAI_VWT_m", new NameAllocator().newName("xHG))Lt_oH}NAI(VWT<m"));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759() throws Exception {
        Assert.assertEquals("_zj_E5nO___Fk_hExKTT", new NameAllocator().newName("^zj?E5nO-/@Fk/hExKTT", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_add40761() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", 1));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40758litString40951() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759litString40959() throws Exception {
        Assert.assertEquals("_zZ_p_zH5_", new NameAllocator().newName("/zZ!p`zH5)", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759_add40981_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_15831 = new Object();
            String __DSPOT_suggestion_15830 = "^zj?E5nO-/@Fk/hExKTT";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_15830, __DSPOT_tag_15831);
            String o_javaKeyword_sd40759__6 = nameAllocator.newName(__DSPOT_suggestion_15830, __DSPOT_tag_15831);
            org.junit.Assert.fail("javaKeyword_sd40759_add40981 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759litString40965() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a_b", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759_sd40978_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_15836 = new Object();
            Object __DSPOT_tag_15831 = new Object();
            String __DSPOT_suggestion_15830 = "^zj?E5nO-/@Fk/hExKTT";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_sd40759__6 = nameAllocator.newName("^zj?E5nO-/@Fk/hExKTT", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("javaKeyword_sd40759_sd40978 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40758_add40956() throws Exception {
        String __DSPOT_suggestion_15829 = "xHG))Lt_oH}NAI(VWT<m";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40758_add40956__4 = nameAllocator.newName(__DSPOT_suggestion_15829);
        Assert.assertEquals("xHG__Lt_oH_NAI_VWT_m", o_javaKeyword_sd40758_add40956__4);
        Assert.assertEquals("xHG__Lt_oH_NAI_VWT_m_", nameAllocator.newName(__DSPOT_suggestion_15829));
        Assert.assertEquals("xHG__Lt_oH_NAI_VWT_m", o_javaKeyword_sd40758_add40956__4);
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759litString40969() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40758litString40943() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a\ud83c\udf7ab"));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759litString40957() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759_sd40980litString43071() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40759__6 = nameAllocator.newName("azj?E5nO-/@Fk/hExKTT", new Object());
        Assert.assertEquals("azj_E5nO___Fk_hExKTT", o_javaKeyword_sd40759__6);
        Assert.assertEquals("_p5q_Y_KF__Y3Ym__i__", nameAllocator.newName("=p5q@Y]KF^?Y3Ym;}i+(", new Object()));
        Assert.assertEquals("azj_E5nO___Fk_hExKTT", o_javaKeyword_sd40759__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759_sd40979litString43007() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40759__6 = nameAllocator.newName("^zj?E5nO-/@Fk/hExKTT", new Object());
        Assert.assertEquals("_zj_E5nO___Fk_hExKTT", o_javaKeyword_sd40759__6);
        Assert.assertEquals("public_", nameAllocator.newName("public_"));
        Assert.assertEquals("_zj_E5nO___Fk_hExKTT", o_javaKeyword_sd40759__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759_sd40977_sd42984() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40759__6 = nameAllocator.newName("^zj?E5nO-/@Fk/hExKTT", new Object());
        Assert.assertEquals("_zj_E5nO___Fk_hExKTT", o_javaKeyword_sd40759__6);
        Assert.assertEquals("_$A______NqCO__sFFB_", nameAllocator.clone().newName("`$A;*!_)>NqCO/}sFFB-", new Object()));
        Assert.assertEquals("_zj_E5nO___Fk_hExKTT", o_javaKeyword_sd40759__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759_sd40980litString43058() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40759__6 = nameAllocator.newName("public", new Object());
        Assert.assertEquals("public_", o_javaKeyword_sd40759__6);
        Assert.assertEquals("_p5q_Y_KF__Y3Ym__i__", nameAllocator.newName("=p5q@Y]KF^?Y3Ym;}i+(", new Object()));
        Assert.assertEquals("public_", o_javaKeyword_sd40759__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759_sd40979litString43016() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40759__6 = nameAllocator.newName("public_", new Object());
        Assert.assertEquals("public_", o_javaKeyword_sd40759__6);
        Assert.assertEquals("Z_gHtmedr_p__4N_r_T_", nameAllocator.newName("Z`gHtmedr?p@_4N|r!T-"));
        Assert.assertEquals("public_", o_javaKeyword_sd40759__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759_sd40979_add43033() throws Exception {
        String __DSPOT_suggestion_15837 = "Z`gHtmedr?p@_4N|r!T-";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40759__6 = nameAllocator.newName("^zj?E5nO-/@Fk/hExKTT", new Object());
        Assert.assertEquals("_zj_E5nO___Fk_hExKTT", o_javaKeyword_sd40759__6);
        String o_javaKeyword_sd40759_sd40979_add43033__11 = nameAllocator.newName(__DSPOT_suggestion_15837);
        Assert.assertEquals("Z_gHtmedr_p__4N_r_T_", o_javaKeyword_sd40759_sd40979_add43033__11);
        Assert.assertEquals("Z_gHtmedr_p__4N_r_T__", nameAllocator.newName(__DSPOT_suggestion_15837));
        Assert.assertEquals("Z_gHtmedr_p__4N_r_T_", o_javaKeyword_sd40759_sd40979_add43033__11);
        Assert.assertEquals("_zj_E5nO___Fk_hExKTT", o_javaKeyword_sd40759__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759_sd40980_add43078_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_15839 = new Object();
            String __DSPOT_suggestion_15838 = "=p5q@Y]KF^?Y3Ym;}i+(";
            Object __DSPOT_tag_15831 = new Object();
            String __DSPOT_suggestion_15830 = "^zj?E5nO-/@Fk/hExKTT";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_15830, __DSPOT_tag_15831);
            String o_javaKeyword_sd40759__6 = nameAllocator.newName(__DSPOT_suggestion_15830, __DSPOT_tag_15831);
            String o_javaKeyword_sd40759_sd40980__13 = nameAllocator.newName("=p5q@Y]KF^?Y3Ym;}i+(", new Object());
            org.junit.Assert.fail("javaKeyword_sd40759_sd40980_add43078 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759_sd40980litString43064() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40759__6 = nameAllocator.newName("1ab", new Object());
        Assert.assertEquals("_1ab", o_javaKeyword_sd40759__6);
        Assert.assertEquals("_p5q_Y_KF__Y3Ym__i__", nameAllocator.newName("=p5q@Y]KF^?Y3Ym;}i+(", new Object()));
        Assert.assertEquals("_1ab", o_javaKeyword_sd40759__6);
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40759_sd40978_failAssert1_sd44020() throws Exception {
        try {
            String __DSPOT_arg0_16389 = "):|ewc9Htf|L1x&hI[K1";
            Object __DSPOT_tag_15836 = new Object();
            Object __DSPOT_tag_15831 = new Object();
            String __DSPOT_suggestion_15830 = "^zj?E5nO-/@Fk/hExKTT";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_sd40759__6 = nameAllocator.newName("^zj?E5nO-/@Fk/hExKTT", new Object());
            Assert.assertEquals("_zj_E5nO___Fk_hExKTT", nameAllocator.newName("^zj?E5nO-/@Fk/hExKTT", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("javaKeyword_sd40759_sd40978 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).compareTo("):|ewc9Htf|L1x&hI[K1");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45025() throws Exception {
        Assert.assertEquals("zGR7uQ_A__r_0DE__E8_", new NameAllocator().newName("zGR7uQ`A(+r<0DE +E8 "));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45024_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_16655 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("nameCollision_sd45024 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45026() throws Exception {
        Assert.assertEquals("______l__b___HJd__$h", new NameAllocator().newName("&}}[!}l^!b^`@HJd[>$h", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45025_sd45296_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_16659 = new Object();
            String __DSPOT_suggestion_16656 = "zGR7uQ`A(+r<0DE +E8 ";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_sd45025__4 = nameAllocator.newName("zGR7uQ`A(+r<0DE +E8 ");
            nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollision_sd45025_sd45296 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_add45032litString45388() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab"));
    }

    @Test(timeout = 10000)
    public void nameCollision_add45032litString45385() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab"));
    }

    @Test(timeout = 10000)
    public void nameCollision_add45032litString45384() throws Exception {
        Assert.assertEquals("eoo", new NameAllocator().newName("eoo"));
    }

    @Test(timeout = 10000)
    public void nameCollision_add45032litString45387() throws Exception {
        Assert.assertEquals("f_oo", new NameAllocator().newName("f!oo"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45026litString45300() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a_b", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45025_add45299() throws Exception {
        String __DSPOT_suggestion_16656 = "zGR7uQ`A(+r<0DE +E8 ";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45025_add45299__4 = nameAllocator.newName(__DSPOT_suggestion_16656);
        Assert.assertEquals("zGR7uQ_A__r_0DE__E8_", o_nameCollision_sd45025_add45299__4);
        Assert.assertEquals("zGR7uQ_A__r_0DE__E8__", nameAllocator.newName(__DSPOT_suggestion_16656));
        Assert.assertEquals("zGR7uQ_A__r_0DE__E8_", o_nameCollision_sd45025_add45299__4);
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45026litString45311() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_add45032litString45391() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45026_add45324_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_16658 = new Object();
            String __DSPOT_suggestion_16657 = "&}}[!}l^!b^`@HJd[>$h";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_16657, __DSPOT_tag_16658);
            String o_nameCollision_sd45026__6 = nameAllocator.newName(__DSPOT_suggestion_16657, __DSPOT_tag_16658);
            org.junit.Assert.fail("nameCollision_sd45026_add45324 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45026_sd45323litString47356() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45026__6 = nameAllocator.newName("&}}[!}l^!b^`@HJd[>$h", new Object());
        Assert.assertEquals("______l__b___HJd__$h", o_nameCollision_sd45026__6);
        Assert.assertEquals("public_", nameAllocator.newName("public", new Object()));
        Assert.assertEquals("______l__b___HJd__$h", o_nameCollision_sd45026__6);
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45026_sd45321_failAssert1_sd49493() throws Exception {
        try {
            String __DSPOT_arg0_17351 = "3kW!67l5Y0.]pp<75@U#";
            Object __DSPOT_tag_16663 = new Object();
            Object __DSPOT_tag_16658 = new Object();
            String __DSPOT_suggestion_16657 = "&}}[!}l^!b^`@HJd[>$h";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_sd45026__6 = nameAllocator.newName("&}}[!}l^!b^`@HJd[>$h", new Object());
            Assert.assertEquals("______l__b___HJd__$h", nameAllocator.newName("&}}[!}l^!b^`@HJd[>$h", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollision_sd45026_sd45321 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).compareTo("3kW!67l5Y0.]pp<75@U#");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45026_sd45322_add47336() throws Exception {
        String __DSPOT_suggestion_16664 = "FRVKYU^-01J!cxfHkZMM";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45026__6 = nameAllocator.newName("&}}[!}l^!b^`@HJd[>$h", new Object());
        Assert.assertEquals("______l__b___HJd__$h", o_nameCollision_sd45026__6);
        String o_nameCollision_sd45026_sd45322_add47336__11 = nameAllocator.newName(__DSPOT_suggestion_16664);
        Assert.assertEquals("FRVKYU__01J_cxfHkZMM", o_nameCollision_sd45026_sd45322_add47336__11);
        Assert.assertEquals("FRVKYU__01J_cxfHkZMM_", nameAllocator.newName(__DSPOT_suggestion_16664));
        Assert.assertEquals("______l__b___HJd__$h", o_nameCollision_sd45026__6);
        Assert.assertEquals("FRVKYU__01J_cxfHkZMM", o_nameCollision_sd45026_sd45322_add47336__11);
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45026_sd45323_add47381_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_16666 = new Object();
            String __DSPOT_suggestion_16665 = "=v[c0Jq,0<%c9(S+Gm<C";
            Object __DSPOT_tag_16658 = new Object();
            String __DSPOT_suggestion_16657 = "&}}[!}l^!b^`@HJd[>$h";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_16657, __DSPOT_tag_16658);
            String o_nameCollision_sd45026__6 = nameAllocator.newName(__DSPOT_suggestion_16657, __DSPOT_tag_16658);
            String o_nameCollision_sd45026_sd45323__13 = nameAllocator.newName("=v[c0Jq,0<%c9(S+Gm<C", new Object());
            org.junit.Assert.fail("nameCollision_sd45026_sd45323_add47381 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45026_sd45322litString47321() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45026__6 = nameAllocator.newName("1ab", new Object());
        Assert.assertEquals("_1ab", o_nameCollision_sd45026__6);
        Assert.assertEquals("FRVKYU__01J_cxfHkZMM", nameAllocator.newName("FRVKYU^-01J!cxfHkZMM"));
        Assert.assertEquals("_1ab", o_nameCollision_sd45026__6);
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45025_sd45298litString46674() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45025__4 = nameAllocator.newName("tag 1 cannot be used for both 'foo' and 'bar'");
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", o_nameCollision_sd45025__4);
        Assert.assertEquals("qaftBc_1D_MD_SW_K_fZ", nameAllocator.newName("qaftBc#1D(MD*SW}K#fZ", new Object()));
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", o_nameCollision_sd45025__4);
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45026_sd45320_sd47287() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45026__6 = nameAllocator.newName("&}}[!}l^!b^`@HJd[>$h", new Object());
        Assert.assertEquals("______l__b___HJd__$h", o_nameCollision_sd45026__6);
        Assert.assertEquals("_mQ_zNZCQS_P__BB1X6M", nameAllocator.clone().newName("-mQ_zNZCQS?P=>BB1X6M", new Object()));
        Assert.assertEquals("______l__b___HJd__$h", o_nameCollision_sd45026__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50628() throws Exception {
        Assert.assertEquals("_8__Nv_0_NZ_8__kY__9b", new NameAllocator().newName("8&}Nv 0#NZ=8%`kY{*9b", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50627() throws Exception {
        Assert.assertEquals("_a_n__3_g2UGB__UY55G", new NameAllocator().newName("[a[n*^3-g2UGB /UY55G"));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50632() throws Exception {
        Assert.assertEquals("foo", new NameAllocator().newName("foo", 2));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50638_failAssert2() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(2);
            org.junit.Assert.fail("nameCollisionWithTag_add50638 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50627litString50982() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50634litString51100() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", 3));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50628_sd51023() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50628__6 = nameAllocator.newName("8&}Nv 0#NZ=8%`kY{*9b", new Object());
        Assert.assertEquals("_8__Nv_0_NZ_8__kY__9b", o_nameCollisionWithTag_sd50628__6);
        Assert.assertEquals("i_Z_DT__AA__1_mVL_Fg", nameAllocator.newName("i.Z-DT&*AA @1_mVL]Fg"));
        Assert.assertEquals("_8__Nv_0_NZ_8__kY__9b", o_nameCollisionWithTag_sd50628__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50632litString51064() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", 2));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50632litString51055() throws Exception {
        Assert.assertEquals("foo__", new NameAllocator().newName("foo__", 2));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50632litString51058() throws Exception {
        Assert.assertEquals("_q0I7_NXM3", new NameAllocator().newName("-q0I7=NXM3", 2));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50628_sd51022_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_17694 = new Object();
            Object __DSPOT_tag_17689 = new Object();
            String __DSPOT_suggestion_17688 = "8&}Nv 0#NZ=8%`kY{*9b";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_sd50628__6 = nameAllocator.newName("8&}Nv 0#NZ=8%`kY{*9b", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollisionWithTag_sd50628_sd51022 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50628_add51025_failAssert4() throws Exception {
        try {
            Object __DSPOT_tag_17689 = new Object();
            String __DSPOT_suggestion_17688 = "8&}Nv 0#NZ=8%`kY{*9b";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_17688, __DSPOT_tag_17689);
            String o_nameCollisionWithTag_sd50628__6 = nameAllocator.newName(__DSPOT_suggestion_17688, __DSPOT_tag_17689);
            org.junit.Assert.fail("nameCollisionWithTag_sd50628_add51025 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50628_sd51023litString53927() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50628__6 = nameAllocator.newName("8&}Nv 0#NZ=8%`kY{*9b", new Object());
        Assert.assertEquals("_8__Nv_0_NZ_8__kY__9b", o_nameCollisionWithTag_sd50628__6);
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", nameAllocator.newName("tag 1 cannot be used for both 'foo' and 'bar'"));
        Assert.assertEquals("_8__Nv_0_NZ_8__kY__9b", o_nameCollisionWithTag_sd50628__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50628_sd51023_add53954() throws Exception {
        String __DSPOT_suggestion_17695 = "i.Z-DT&*AA @1_mVL]Fg";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50628__6 = nameAllocator.newName("8&}Nv 0#NZ=8%`kY{*9b", new Object());
        Assert.assertEquals("_8__Nv_0_NZ_8__kY__9b", o_nameCollisionWithTag_sd50628__6);
        String o_nameCollisionWithTag_sd50628_sd51023_add53954__11 = nameAllocator.newName(__DSPOT_suggestion_17695);
        Assert.assertEquals("i_Z_DT__AA__1_mVL_Fg", o_nameCollisionWithTag_sd50628_sd51023_add53954__11);
        Assert.assertEquals("i_Z_DT__AA__1_mVL_Fg_", nameAllocator.newName(__DSPOT_suggestion_17695));
        Assert.assertEquals("i_Z_DT__AA__1_mVL_Fg", o_nameCollisionWithTag_sd50628_sd51023_add53954__11);
        Assert.assertEquals("_8__Nv_0_NZ_8__kY__9b", o_nameCollisionWithTag_sd50628__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50628_sd51023litString53935() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50628__6 = nameAllocator.newName("/|FW/tZ]]s", new Object());
        Assert.assertEquals("__FW_tZ__s", o_nameCollisionWithTag_sd50628__6);
        Assert.assertEquals("i_Z_DT__AA__1_mVL_Fg", nameAllocator.newName("i.Z-DT&*AA @1_mVL]Fg"));
        Assert.assertEquals("__FW_tZ__s", o_nameCollisionWithTag_sd50628__6);
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50628_add51025_failAssert4_sd54842() throws Exception {
        try {
            Object __DSPOT_tag_18617 = new Object();
            String __DSPOT_suggestion_18616 = "ZHb![9r@JhIN86@!_^nV";
            Object __DSPOT_tag_17689 = new Object();
            String __DSPOT_suggestion_17688 = "8&}Nv 0#NZ=8%`kY{*9b";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_sd50628_add51025_failAssert4_sd54842__11 = nameAllocator.newName(__DSPOT_suggestion_17688, __DSPOT_tag_17689);
            Assert.assertEquals("_8__Nv_0_NZ_8__kY__9b", nameAllocator.newName(__DSPOT_suggestion_17688, __DSPOT_tag_17689));
            String o_nameCollisionWithTag_sd50628__6 = nameAllocator.newName(__DSPOT_suggestion_17688, __DSPOT_tag_17689);
            org.junit.Assert.fail("nameCollisionWithTag_sd50628_add51025 should have thrown IllegalArgumentException");
            nameAllocator.newName("ZHb![9r@JhIN86@!_^nV", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50628_sd51022_failAssert0_sd54469() throws Exception {
        try {
            String __DSPOT_arg0_18354 = "@2/:rRs[=;D;:)?Mk<n>";
            Object __DSPOT_tag_17694 = new Object();
            Object __DSPOT_tag_17689 = new Object();
            String __DSPOT_suggestion_17688 = "8&}Nv 0#NZ=8%`kY{*9b";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_sd50628__6 = nameAllocator.newName("8&}Nv 0#NZ=8%`kY{*9b", new Object());
            Assert.assertEquals("_8__Nv_0_NZ_8__kY__9b", nameAllocator.newName("8&}Nv 0#NZ=8%`kY{*9b", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollisionWithTag_sd50628_sd51022 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).equalsIgnoreCase("@2/:rRs[=;D;:)?Mk<n>");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50628_sd51023litString53932() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50628__6 = nameAllocator.newName("public", new Object());
        Assert.assertEquals("public_", o_nameCollisionWithTag_sd50628__6);
        Assert.assertEquals("i_Z_DT__AA__1_mVL_Fg", nameAllocator.newName("i.Z-DT&*AA @1_mVL]Fg"));
        Assert.assertEquals("public_", o_nameCollisionWithTag_sd50628__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55501() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55501__3 = nameAllocator.newName("bar", 1);
        Assert.assertEquals("bar", o_tagReuseForbiddenlitString55501__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("bar", o_tagReuseForbiddenlitString55501__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55512() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55512__3 = nameAllocator.newName("&ab", 1);
        Assert.assertEquals("_ab", o_tagReuseForbiddenlitString55512__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_ab", o_tagReuseForbiddenlitString55512__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55511() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55511__3 = nameAllocator.newName("1ab", 1);
        Assert.assertEquals("_1ab", o_tagReuseForbiddenlitString55511__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_1ab", o_tagReuseForbiddenlitString55511__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55506() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55506__3 = nameAllocator.newName("q26X_i]-+}", 1);
        Assert.assertEquals("q26X_i____", o_tagReuseForbiddenlitString55506__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("q26X_i____", o_tagReuseForbiddenlitString55506__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55517() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55517__3 = nameAllocator.newName("public", 1);
        Assert.assertEquals("public_", o_tagReuseForbiddenlitString55517__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("public_", o_tagReuseForbiddenlitString55517__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55567() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55567__4 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55567__4);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("G__OD_E_CScbWN__62p_", nameAllocator.newName("G)+OD]E_CScbWN%[62p."));
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55567__4);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55634() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55634__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55634__3);
        try {
            String __DSPOT_arg0_18773 = "QCNdiEF`D%$[?kh[m#v(";
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).getBytes("QCNdiEF`D%$[?kh[m#v(");
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55634__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55566_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_18687 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.get(new Object());
            org.junit.Assert.fail("tagReuseForbidden_sd55566 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55650_sd70309() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55650__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55650__3);
        try {
            int __DSPOT_arg3_27929 = 1231455670;
            byte[] __DSPOT_arg2_27928 = new byte[]{ 55, 118, 72, -116 };
            int __DSPOT_arg1_27927 = 2099847855;
            int __DSPOT_arg0_27926 = -1435500609;
            int __DSPOT_arg4_18797 = -226733947;
            int __DSPOT_arg3_18796 = -604874593;
            String __DSPOT_arg2_18795 = "?s(<M?n(o{q1e?PrR/6L";
            int __DSPOT_arg1_18794 = 1627776971;
            boolean __DSPOT_arg0_18793 = false;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(false, 1627776971, __DSPOT_arg2_18795, -604874593, -226733947);
            __DSPOT_arg2_18795.getBytes(-1435500609, 2099847855, new byte[]{ 55, 118, 72, -116 }, 1231455670);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55650__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55635_sd69019() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55635__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55635__3);
        try {
            int __DSPOT_arg3_18777 = -564906123;
            char[] __DSPOT_arg2_18776 = new char[]{ 'N', 'L', '#' };
            int __DSPOT_arg1_18775 = -1071321231;
            int __DSPOT_arg0_18774 = 1830519605;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).getChars(1830519605, -1071321231, new char[]{ 'N', 'L', '#' }, -564906123);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_fU_bvEUCiUpnxHPni__", nameAllocator.newName("}fU(bvEUCiUpnxHPni-{", new Object()));
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55635__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55650_sd70294() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55650__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55650__3);
        try {
            int __DSPOT_arg4_18797 = -226733947;
            int __DSPOT_arg3_18796 = -604874593;
            String __DSPOT_arg2_18795 = "?s(<M?n(o{q1e?PrR/6L";
            int __DSPOT_arg1_18794 = 1627776971;
            boolean __DSPOT_arg0_18793 = false;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(false, 1627776971, "?s(<M?n(o{q1e?PrR/6L", -604874593, -226733947);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("L_I__0_BxOYx__woR15L", nameAllocator.newName("L[I^ 0 BxOYx#(woR15L"));
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55650__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55633_sd68811() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55633__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55633__3);
        try {
            int __DSPOT_arg3_18772 = 232181063;
            byte[] __DSPOT_arg2_18771 = new byte[]{ 103, -92, -124, -5 };
            int __DSPOT_arg1_18770 = 945164346;
            int __DSPOT_arg0_18769 = -99512292;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).getBytes(-99512292, 945164346, new byte[]{ 103, -92, -124, -5 }, 232181063);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_00k__l_YFG_k2caCuL__", nameAllocator.newName("00k}?l?YFG+k2caCuL;?", new Object()));
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55633__3);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55568_sd62615() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55568__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55568__6);
        try {
            String __DSPOT_arg0_23037 = "qLps?%ezL1=lN^#X43Rd";
            String __DSPOT_invoc_13 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).split("qLps?%ezL1=lN^#X43Rd");
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("p_P_G_8mRk5yIWsRc_k_", nameAllocator.newName("p]P_G:8mRk5yIWsRc&k;", new Object()));
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55568__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55568_sd62578_sd79701() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55568__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55568__6);
        try {
            String __DSPOT_arg0_33604 = "@R0z]39VL^*aAc-ryjgO";
            String __DSPOT_invoc_16 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).compareToIgnoreCase("@R0z]39VL^*aAc-ryjgO");
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_sd55568__11 = nameAllocator.newName("p]P_G:8mRk5yIWsRc&k;", new Object());
        Assert.assertEquals("p_P_G_8mRk5yIWsRc_k_", o_tagReuseForbidden_sd55568__11);
        Assert.assertEquals("r_4G5e_meCW___lm2_e_", nameAllocator.newName("r<4G5e)meCW}}}lm2#e;", new Object()));
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55568__6);
        Assert.assertEquals("p_P_G_8mRk5yIWsRc_k_", o_tagReuseForbidden_sd55568__11);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55568_sd62578_sd79734() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55568__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55568__6);
        try {
            int __DSPOT_arg1_33656 = 1986261400;
            int __DSPOT_arg0_33655 = -457453300;
            String __DSPOT_invoc_16 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).subSequence(-457453300, 1986261400);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_sd55568__11 = nameAllocator.newName("p]P_G:8mRk5yIWsRc&k;", new Object());
        Assert.assertEquals("p_P_G_8mRk5yIWsRc_k_", o_tagReuseForbidden_sd55568__11);
        Assert.assertEquals("r_4G5e_meCW___lm2_e_", nameAllocator.newName("r<4G5e)meCW}}}lm2#e;", new Object()));
        Assert.assertEquals("p_P_G_8mRk5yIWsRc_k_", o_tagReuseForbidden_sd55568__11);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55568__6);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove55672litNum72753_sd98010() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            String o_tagReuseForbidden_remove55672__5 = nameAllocator.newName("bar", 0);
            Assert.assertEquals("bar", nameAllocator.newName("bar", 0));
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_Qzrs_DqdNuGAPQT_M_Q", nameAllocator.newName(")Qzrs#DqdNuGAPQT>M?Q"));
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove55672litNum72752litString98031() throws Exception {
        try {
            String o_tagReuseForbidden_remove55672__5 = new NameAllocator().newName("public", 2);
            Assert.assertEquals("public_", new NameAllocator().newName("public", 2));
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove55672litNum72752litString98030() throws Exception {
        try {
            String o_tagReuseForbidden_remove55672__5 = new NameAllocator().newName("unknown tag: 1", 2);
            Assert.assertEquals("unknown_tag__1", new NameAllocator().newName("unknown tag: 1", 2));
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove55672litNum72753_sd98009_failAssert3() throws Exception {
        try {
            Object __DSPOT_tag_45019 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            try {
                String o_tagReuseForbidden_remove55672__5 = nameAllocator.newName("bar", 0);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.get(new Object());
            org.junit.Assert.fail("tagReuseForbidden_remove55672litNum72753_sd98009 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove55672litNum72752litString98017() throws Exception {
        try {
            String o_tagReuseForbidden_remove55672__5 = new NameAllocator().newName("", 2);
            Assert.assertEquals("", new NameAllocator().newName("", 2));
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove55672litNum72752litString98028() throws Exception {
        try {
            String o_tagReuseForbidden_remove55672__5 = new NameAllocator().newName("&ab", 2);
            Assert.assertEquals("_ab", new NameAllocator().newName("&ab", 2));
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove55672litNum72752litString98013() throws Exception {
        try {
            String o_tagReuseForbidden_remove55672__5 = new NameAllocator().newName("a-b", 2);
            Assert.assertEquals("a_b", new NameAllocator().newName("a-b", 2));
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove55672litNum72752litString98023() throws Exception {
        try {
            String o_tagReuseForbidden_remove55672__5 = new NameAllocator().newName("1ab", 2);
            Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", 2));
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void usage_add99221() throws Exception {
        Assert.assertEquals("bar", new NameAllocator().newName("bar", 2));
    }

    @Test(timeout = 10000)
    public void usage_add99225_failAssert2() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(2);
            org.junit.Assert.fail("usage_add99225 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_sd99217() throws Exception {
        Assert.assertEquals("q____IuN__VyO__e__ml", new NameAllocator().newName("q|>]|IuN!&VyO%.e[,ml", new Object()));
    }

    @Test(timeout = 10000)
    public void usage_sd99216() throws Exception {
        Assert.assertEquals("xQJZpt__q_r_Yz_g_Y_D", new NameAllocator().newName("xQJZpt[#q r`Yz-g[Y.D"));
    }

    @Test(timeout = 10000)
    public void usage_sd99216litString99468() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 10000)
    public void usage_sd99217_sd99501_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_45303 = new Object();
            Object __DSPOT_tag_45298 = new Object();
            String __DSPOT_suggestion_45297 = "q|>]|IuN!&VyO%.e[,ml";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_sd99217__6 = nameAllocator.newName("q|>]|IuN!&VyO%.e[,ml", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("usage_sd99217_sd99501 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_add99221litString99533() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a\ud83c\udf7ab", 2));
    }

    @Test(timeout = 10000)
    public void usage_add99221litString99534() throws Exception {
        Assert.assertEquals("foo", new NameAllocator().newName("foo", 2));
    }

    @Test(timeout = 10000)
    public void usage_sd99217_add99504_failAssert3() throws Exception {
        try {
            Object __DSPOT_tag_45298 = new Object();
            String __DSPOT_suggestion_45297 = "q|>]|IuN!&VyO%.e[,ml";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_45297, __DSPOT_tag_45298);
            String o_usage_sd99217__6 = nameAllocator.newName(__DSPOT_suggestion_45297, __DSPOT_tag_45298);
            org.junit.Assert.fail("usage_sd99217_add99504 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_add99221litString99542() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", 2));
    }

    @Test(timeout = 10000)
    public void usage_add99221litString99543() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", 2));
    }

    @Test(timeout = 10000)
    public void usage_add99221litString99551() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", 2));
    }

    @Test(timeout = 10000)
    public void usage_sd99217_sd99503() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99217__6 = nameAllocator.newName("q|>]|IuN!&VyO%.e[,ml", new Object());
        Assert.assertEquals("q____IuN__VyO__e__ml", o_usage_sd99217__6);
        Assert.assertEquals("_tyh_KA_XPtXLmOj__t_", nameAllocator.newName(">tyh KA(XPtXLmOj/^t%", new Object()));
        Assert.assertEquals("q____IuN__VyO__e__ml", o_usage_sd99217__6);
    }

    @Test(timeout = 10000)
    public void usage_sd99217_sd99502litString101863() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99217__6 = nameAllocator.newName("1ab", new Object());
        Assert.assertEquals("_1ab", o_usage_sd99217__6);
        Assert.assertEquals("tG_XvfeH__tYiZQUr_jb", nameAllocator.newName("tG)XvfeH^{tYiZQUr.jb"));
        Assert.assertEquals("_1ab", o_usage_sd99217__6);
    }

    @Test(timeout = 10000)
    public void usage_sd99217_sd99502litString101865() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99217__6 = nameAllocator.newName("public", new Object());
        Assert.assertEquals("public_", o_usage_sd99217__6);
        Assert.assertEquals("tG_XvfeH__tYiZQUr_jb", nameAllocator.newName("tG)XvfeH^{tYiZQUr.jb"));
        Assert.assertEquals("public_", o_usage_sd99217__6);
    }

    @Test(timeout = 10000)
    public void usage_sd99217_add99504_failAssert3_sd103056() throws Exception {
        try {
            Object __DSPOT_tag_46076 = new Object();
            String __DSPOT_suggestion_46075 = "?9%}lDFe^JbiV_Wvt]9C";
            Object __DSPOT_tag_45298 = new Object();
            String __DSPOT_suggestion_45297 = "q|>]|IuN!&VyO%.e[,ml";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_sd99217_add99504_failAssert3_sd103056__11 = nameAllocator.newName(__DSPOT_suggestion_45297, __DSPOT_tag_45298);
            Assert.assertEquals("q____IuN__VyO__e__ml", nameAllocator.newName(__DSPOT_suggestion_45297, __DSPOT_tag_45298));
            String o_usage_sd99217__6 = nameAllocator.newName(__DSPOT_suggestion_45297, __DSPOT_tag_45298);
            org.junit.Assert.fail("usage_sd99217_add99504 should have thrown IllegalArgumentException");
            nameAllocator.newName("?9%}lDFe^JbiV_Wvt]9C", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_sd99217_sd99502litString101867() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99217__6 = nameAllocator.newName("&ab", new Object());
        Assert.assertEquals("_ab", o_usage_sd99217__6);
        Assert.assertEquals("tG_XvfeH__tYiZQUr_jb", nameAllocator.newName("tG)XvfeH^{tYiZQUr.jb"));
        Assert.assertEquals("_ab", o_usage_sd99217__6);
    }

    @Test(timeout = 10000)
    public void usage_sd99217_sd99502litString101848() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99217__6 = nameAllocator.newName("q|>]|IuN!&VyO%.e[,ml", new Object());
        Assert.assertEquals("q____IuN__VyO__e__ml", o_usage_sd99217__6);
        Assert.assertEquals("unknown_tag__1", nameAllocator.newName("unknown tag: 1"));
        Assert.assertEquals("q____IuN__VyO__e__ml", o_usage_sd99217__6);
    }

    @Test(timeout = 10000)
    public void usage_sd99217_sd99501_failAssert2_sd102979() throws Exception {
        try {
            String __DSPOT_suggestion_46005 = "1FwSC[b=O*_-)(j!#e&U";
            Object __DSPOT_tag_45303 = new Object();
            Object __DSPOT_tag_45298 = new Object();
            String __DSPOT_suggestion_45297 = "q|>]|IuN!&VyO%.e[,ml";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_sd99217__6 = nameAllocator.newName("q|>]|IuN!&VyO%.e[,ml", new Object());
            Assert.assertEquals("q____IuN__VyO__e__ml", nameAllocator.newName("q|>]|IuN!&VyO%.e[,ml", new Object()));
            nameAllocator.get(new Object());
            org.junit.Assert.fail("usage_sd99217_sd99501 should have thrown IllegalArgumentException");
            nameAllocator.newName("1FwSC[b=O*_-)(j!#e&U");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_sd99217_sd99502_add101874() throws Exception {
        String __DSPOT_suggestion_45304 = "tG)XvfeH^{tYiZQUr.jb";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99217__6 = nameAllocator.newName("q|>]|IuN!&VyO%.e[,ml", new Object());
        Assert.assertEquals("q____IuN__VyO__e__ml", o_usage_sd99217__6);
        String o_usage_sd99217_sd99502_add101874__11 = nameAllocator.newName(__DSPOT_suggestion_45304);
        Assert.assertEquals("tG_XvfeH__tYiZQUr_jb", o_usage_sd99217_sd99502_add101874__11);
        Assert.assertEquals("tG_XvfeH__tYiZQUr_jb_", nameAllocator.newName(__DSPOT_suggestion_45304));
        Assert.assertEquals("tG_XvfeH__tYiZQUr_jb", o_usage_sd99217_sd99502_add101874__11);
        Assert.assertEquals("q____IuN__VyO__e__ml", o_usage_sd99217__6);
    }

    @Test(timeout = 10000)
    public void usage_sd99217_sd99502() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99217__6 = nameAllocator.newName("q|>]|IuN!&VyO%.e[,ml", new Object());
        Assert.assertEquals("q____IuN__VyO__e__ml", o_usage_sd99217__6);
        Assert.assertEquals("tG_XvfeH__tYiZQUr_jb", nameAllocator.newName("tG)XvfeH^{tYiZQUr.jb"));
        Assert.assertEquals("q____IuN__VyO__e__ml", o_usage_sd99217__6);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103878() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("__3ENOr_O8k$Lt_____$", nameAllocator.newName("-&3ENOr[O8k$Lt%)![,$"));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103879() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("m9W_F_A_rq___HDlD_K_", nameAllocator.newName("m9W`F%A.rq{.|HDlD@K_", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103877_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_46280 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.get(new Object());
            org.junit.Assert.fail("useBeforeAllocateForbidden_sd103877 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103878litString104164() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("bar", nameAllocator.newName("bar"));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103879_sd104305() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            char __DSPOT_arg1_46469 = 'y';
            char __DSPOT_arg0_46468 = 'V';
            String __DSPOT_invoc_8 = nameAllocator.get(1);
            nameAllocator.get(1).replace('V', 'y');
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("m9W_F_A_rq___HDlD_K_", nameAllocator.newName("m9W`F%A.rq{.|HDlD@K_", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103879litString104254() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_1ab", nameAllocator.newName("1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103878_add104243() throws Exception {
        String __DSPOT_suggestion_46281 = "-&3ENOr[O8k$Lt%)![,$";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd103878_add104243__8 = nameAllocator.newName(__DSPOT_suggestion_46281);
        Assert.assertEquals("__3ENOr_O8k$Lt_____$", o_useBeforeAllocateForbidden_sd103878_add104243__8);
        Assert.assertEquals("__3ENOr_O8k$Lt_____$_", nameAllocator.newName(__DSPOT_suggestion_46281));
        Assert.assertEquals("__3ENOr_O8k$Lt_____$", o_useBeforeAllocateForbidden_sd103878_add104243__8);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103878litString104181() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_ab", nameAllocator.newName("&ab"));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103879litString104247() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("public_", nameAllocator.newName("public", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103879_sd104271_add108202_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_46421 = new Object();
            String __DSPOT_suggestion_46420 = "l){m7M9^VTekky-G-3!0";
            Object __DSPOT_tag_46283 = new Object();
            String __DSPOT_suggestion_46282 = "m9W`F%A.rq{.|HDlD@K_";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.newName(__DSPOT_suggestion_46282, __DSPOT_tag_46283);
            String o_useBeforeAllocateForbidden_sd103879__10 = nameAllocator.newName(__DSPOT_suggestion_46282, __DSPOT_tag_46283);
            String o_useBeforeAllocateForbidden_sd103879_sd104271__17 = nameAllocator.newName("l){m7M9^VTekky-G-3!0", new Object());
            org.junit.Assert.fail("useBeforeAllocateForbidden_sd103879_sd104271_add108202 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103879_sd104320_sd109102() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            int __DSPOT_arg4_49702 = -1363374264;
            int __DSPOT_arg3_49701 = 1440868891;
            String __DSPOT_arg2_49700 = "j=E;1tVco`aDV[g<W_$i";
            int __DSPOT_arg1_49699 = -818196743;
            boolean __DSPOT_arg0_49698 = true;
            Locale __DSPOT_arg0_46486 = new Locale("%Z8_ExZg78mFHG;>otDG", "=&j(j8!G^.#_Wyh)ypaF");
            String __DSPOT_invoc_8 = nameAllocator.get(1);
            String __DSPOT_invoc_13 = nameAllocator.get(1).toUpperCase(new Locale("%Z8_ExZg78mFHG;>otDG", "=&j(j8!G^.#_Wyh)ypaF"));
            nameAllocator.get(1).toUpperCase(new Locale("%Z8_ExZg78mFHG;>otDG", "=&j(j8!G^.#_Wyh)ypaF")).regionMatches(true, -818196743, "j=E;1tVco`aDV[g<W_$i", 1440868891, -1363374264);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("m9W_F_A_rq___HDlD_K_", nameAllocator.newName("m9W`F%A.rq{.|HDlD@K_", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103878_sd104191_sd114164() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            int __DSPOT_arg4_53468 = -227986694;
            int __DSPOT_arg3_53467 = -1365913542;
            String __DSPOT_arg2_53466 = "%]%&xfc!7;{M_Fh2{_/y";
            int __DSPOT_arg1_53465 = 1323566709;
            boolean __DSPOT_arg0_53464 = false;
            String __DSPOT_invoc_9 = nameAllocator.get(1);
            nameAllocator.get(1).regionMatches(false, 1323566709, "%]%&xfc!7;{M_Fh2{_/y", -1365913542, -227986694);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd103878__8 = nameAllocator.newName("-&3ENOr[O8k$Lt%)![,$");
        Assert.assertEquals("__3ENOr_O8k$Lt_____$", o_useBeforeAllocateForbidden_sd103878__8);
        Assert.assertEquals("__wx__akLU9_iyfdM_r2", nameAllocator.newName("?^wx};akLU9(iyfdM#r2", new Object()));
        Assert.assertEquals("__3ENOr_O8k$Lt_____$", o_useBeforeAllocateForbidden_sd103878__8);
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103878_add104243_sd109345() throws Exception {
        String __DSPOT_suggestion_46281 = "-&3ENOr[O8k$Lt%)![,$";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            int __DSPOT_arg3_49852 = -95061789;
            int __DSPOT_arg2_49851 = -1540553215;
            String __DSPOT_arg1_49850 = "kDeM6%ac_`yi)$}+ :dz";
            int __DSPOT_arg0_49849 = 499052412;
            String __DSPOT_invoc_6 = nameAllocator.get(1);
            nameAllocator.get(1).regionMatches(499052412, "kDeM6%ac_`yi)$}+ :dz", -1540553215, -95061789);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd103878_add104243__8 = nameAllocator.newName(__DSPOT_suggestion_46281);
        Assert.assertEquals("__3ENOr_O8k$Lt_____$", o_useBeforeAllocateForbidden_sd103878_add104243__8);
        Assert.assertEquals("__3ENOr_O8k$Lt_____$_", nameAllocator.newName(__DSPOT_suggestion_46281));
        Assert.assertEquals("__3ENOr_O8k$Lt_____$", o_useBeforeAllocateForbidden_sd103878_add104243__8);
    }
}

