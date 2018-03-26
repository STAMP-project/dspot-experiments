package com.squareup.javapoet;


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
    public void characterMappingInvalidStartButValidPart_sd46litString237() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", new Object()));
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
    public void characterMappingInvalidStartButValidPart_sd45litString211() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab"));
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
    public void characterMappingInvalidStartButValidPart_sd45_add223() throws Exception {
        String __DSPOT_suggestion_1 = "[{$QV5:Wz2[|+mr6#-Vt";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd45_add223__4 = nameAllocator.newName(__DSPOT_suggestion_1);
        Assert.assertEquals("__$QV5_Wz2___mr6__Vt", o_characterMappingInvalidStartButValidPart_sd45_add223__4);
        Assert.assertEquals("__$QV5_Wz2___mr6__Vt_", nameAllocator.newName(__DSPOT_suggestion_1));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46litString239() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd45litString208() throws Exception {
        Assert.assertEquals("__$QV5__Wz2___mr6__Vt", new NameAllocator().newName("[{$QV5@:Wz2[|+mr6#-Vt"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46litString230() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName("", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46litString231() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public_", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd247_add2117_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_11 = new Object();
            String __DSPOT_suggestion_10 = ">[Bob5_83OI`-k-a8(J8";
            Object __DSPOT_tag_3 = new Object();
            String __DSPOT_suggestion_2 = "X(r!Fs2l>UgIvC=TU&zg";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
            nameAllocator.newName(__DSPOT_suggestion_10, __DSPOT_tag_11);
            String o_characterMappingInvalidStartButValidPart_sd46_sd247__13 = nameAllocator.newName(__DSPOT_suggestion_10, __DSPOT_tag_11);
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_sd46_sd247_add2117 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
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
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd244_sd2022() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("N3u__h__LLuBuAr_pT_v", nameAllocator.clone().newName("N3u/#h+}LLuBuAr^pT%v", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd246litString2036() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("public_", nameAllocator.newName("public"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd246litString2046() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("foo_", new Object());
        Assert.assertEquals("foo_", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("_YSe__xHdm7__ToX_D7x", nameAllocator.newName(">YSe|%xHdm7#=ToX)D7x"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd246_sd2069() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
        String o_characterMappingInvalidStartButValidPart_sd46_sd246__11 = nameAllocator.newName(">YSe|%xHdm7#=ToX)D7x");
        Assert.assertEquals("_YSe__xHdm7__ToX_D7x", o_characterMappingInvalidStartButValidPart_sd46_sd246__11);
        Assert.assertEquals("_5__k5aw4Z_8__YbgCZ5G", nameAllocator.newName("5]|k5aw4Z#8{}YbgCZ5G", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd247_sd2113_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_293 = new Object();
            Object __DSPOT_tag_11 = new Object();
            String __DSPOT_suggestion_10 = ">[Bob5_83OI`-k-a8(J8";
            Object __DSPOT_tag_3 = new Object();
            String __DSPOT_suggestion_2 = "X(r!Fs2l>UgIvC=TU&zg";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
            String o_characterMappingInvalidStartButValidPart_sd46_sd247__13 = nameAllocator.newName(">[Bob5_83OI`-k-a8(J8", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_sd46_sd247_sd2113 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_add3993() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab", 1));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3989_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_758 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3989 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991() throws Exception {
        Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", new NameAllocator().newName("gAzGTF;K^aA[n|M]WL!p", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990() throws Exception {
        Assert.assertEquals("_8yrwa_pGW_Cd__LVcx9V", new NameAllocator().newName("8yrwa/pGW(Cd|#LVcx9V"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990litString4151() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990litString4162() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990litString4161() throws Exception {
        Assert.assertEquals("_8yrwa_pGW_Cdz_LVcx9V", new NameAllocator().newName("8yrwa/pGW(Cdz#LVcx9V"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991litString4175() throws Exception {
        Assert.assertEquals("foo__", new NameAllocator().newName("foo__", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990litString4155() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a\ud83c\udf7ab"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_add4168() throws Exception {
        String __DSPOT_suggestion_759 = "8yrwa/pGW(Cd|#LVcx9V";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3990_add4168__4 = nameAllocator.newName(__DSPOT_suggestion_759);
        Assert.assertEquals("_8yrwa_pGW_Cd__LVcx9V", o_characterMappingInvalidStartIsInvalidPart_sd3990_add4168__4);
        Assert.assertEquals("_8yrwa_pGW_Cd__LVcx9V_", nameAllocator.newName(__DSPOT_suggestion_759));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4190_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_766 = new Object();
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "gAzGTF;K^aA[n|M]WL!p";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName("gAzGTF;K^aA[n|M]WL!p", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3991_sd4190 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_add4193_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "gAzGTF;K^aA[n|M]WL!p";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3991_add4193 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4191litString5988() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName("foo", new Object());
        Assert.assertEquals("foo", o_characterMappingInvalidStartIsInvalidPart_sd3991__6);
        Assert.assertEquals("AJ_2f_OJ_sljavE08z_k", nameAllocator.newName("AJ/2f&OJ[sljavE08z|k"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4192_add6057_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_769 = new Object();
            String __DSPOT_suggestion_768 = "g_TZb7ENy8gU3C&lAudY";
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "gAzGTF;K^aA[n|M]WL!p";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            String o_characterMappingInvalidStartIsInvalidPart_sd3991_sd4192__13 = nameAllocator.newName("g_TZb7ENy8gU3C&lAudY", new Object());
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3991_sd4192_add6057 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_sd4167litString5324() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3990__4 = nameAllocator.newName("8yrwa/pGW(Cd|#LVcx9V");
        Assert.assertEquals("_8yrwa_pGW_Cd__LVcx9V", o_characterMappingInvalidStartIsInvalidPart_sd3990__4);
        Assert.assertEquals("od___Ngpl_b__RUV__X_L", nameAllocator.newName("od!::Ngpl)b[)RUV],X#L", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4192litString6050() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName("&ab", new Object());
        Assert.assertEquals("_ab", o_characterMappingInvalidStartIsInvalidPart_sd3991__6);
        Assert.assertEquals("g_TZb7ENy8gU3C_lAudY", nameAllocator.newName("g_TZb7ENy8gU3C&lAudY", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4190_failAssert1_sd7027() throws Exception {
        try {
            int __DSPOT_arg1_1298 = -783110609;
            String __DSPOT_arg0_1297 = "RB;TNZIocnnHqe}*(og3";
            Object __DSPOT_tag_766 = new Object();
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "gAzGTF;K^aA[n|M]WL!p";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName("gAzGTF;K^aA[n|M]WL!p", new Object());
            Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", nameAllocator.newName("gAzGTF;K^aA[n|M]WL!p", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3991_sd4190 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).split("RB;TNZIocnnHqe}*(og3", -783110609);
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_sd4167_add5357() throws Exception {
        String __DSPOT_suggestion_759 = "8yrwa/pGW(Cd|#LVcx9V";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3990_sd4167_add5357__7 = nameAllocator.newName(__DSPOT_suggestion_759);
        Assert.assertEquals("_8yrwa_pGW_Cd__LVcx9V", o_characterMappingInvalidStartIsInvalidPart_sd3990_sd4167_add5357__7);
        String o_characterMappingInvalidStartIsInvalidPart_sd3990__4 = nameAllocator.newName(__DSPOT_suggestion_759);
        Assert.assertEquals("_8yrwa_pGW_Cd__LVcx9V_", o_characterMappingInvalidStartIsInvalidPart_sd3990__4);
        Assert.assertEquals("od__Ngpl_b__RUV__X_L", nameAllocator.newName("od::Ngpl)b[)RUV],X#L", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4192litString6042() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName("public", new Object());
        Assert.assertEquals("public_", o_characterMappingInvalidStartIsInvalidPart_sd3991__6);
        Assert.assertEquals("g_TZb7ENy8gU3C_lAudY", nameAllocator.newName("g_TZb7ENy8gU3C&lAudY", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4189_sd5963() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName("gAzGTF;K^aA[n|M]WL!p", new Object());
        Assert.assertEquals("gAzGTF_K_aA_n_M_WL_p", o_characterMappingInvalidStartIsInvalidPart_sd3991__6);
        Assert.assertEquals("Nt_qB9_4PNg__p__5_f3", nameAllocator.clone().newName("Nt*qB9^4PNg#%p_}5^f3", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7931_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_1516 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("characterMappingSubstitute_sd7931 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933() throws Exception {
        Assert.assertEquals("_pP__g_WJu4_q8_3_lH_", new NameAllocator().newName("!pP}@g/WJu4%q8 3 lH#", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_add7935() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a-b", 1));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932() throws Exception {
        Assert.assertEquals("_e_1s0_B__3fMfjrRcpd", new NameAllocator().newName("*e`1s0!B`#3fMfjrRcpd"));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933litString8129() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName("", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932litString8089() throws Exception {
        Assert.assertEquals("_e_1s0_B__3ffjrRcpd", new NameAllocator().newName("*e`1s0!B`#3ffjrRcpd"));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8132_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_1524 = new Object();
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "!pP}@g/WJu4%q8 3 lH#";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName("!pP}@g/WJu4%q8 3 lH#", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSubstitute_sd7933_sd8132 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8131() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName("!pP}@g/WJu4%q8 3 lH#", new Object());
        Assert.assertEquals("_pP__g_WJu4_q8_3_lH_", o_characterMappingSubstitute_sd7933__6);
        nameAllocator.clone();
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933litString8127() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932_add8110() throws Exception {
        String __DSPOT_suggestion_1517 = "*e`1s0!B`#3fMfjrRcpd";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7932_add8110__4 = nameAllocator.newName(__DSPOT_suggestion_1517);
        Assert.assertEquals("_e_1s0_B__3fMfjrRcpd", o_characterMappingSubstitute_sd7932_add8110__4);
        Assert.assertEquals("_e_1s0_B__3fMfjrRcpd_", nameAllocator.newName(__DSPOT_suggestion_1517));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932litString8088() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_add8135_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "!pP}@g/WJu4%q8 3 lH#";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            org.junit.Assert.fail("characterMappingSubstitute_sd7933_add8135 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933litString8130() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("_ab", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933litString8120() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8131_sd9910() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName("!pP}@g/WJu4%q8 3 lH#", new Object());
        Assert.assertEquals("_pP__g_WJu4_q8_3_lH_", o_characterMappingSubstitute_sd7933__6);
        Assert.assertEquals("N_wMPh___W____rK_bpM", nameAllocator.clone().newName("N{wMPh;{:W:!%}rK?bpM", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8133litString9951() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName("public", new Object());
        Assert.assertEquals("public_", o_characterMappingSubstitute_sd7933__6);
        Assert.assertEquals("Le_m_OkD__8_V___r_4I", nameAllocator.newName("Le!m)OkD`_8;V@;]r(4I"));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8134litString9990() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName("1ab", new Object());
        Assert.assertEquals("_1ab", o_characterMappingSubstitute_sd7933__6);
        Assert.assertEquals("cEL3Y_iQpqI0efYA3Az_", nameAllocator.newName("cEL3Y+iQpqI0efYA3Az,", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8132_failAssert1_sd10970() throws Exception {
        try {
            char __DSPOT_arg1_2049 = '!';
            char __DSPOT_arg0_2048 = '!';
            Object __DSPOT_tag_1524 = new Object();
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "!pP}@g/WJu4%q8 3 lH#";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName("!pP}@g/WJu4%q8 3 lH#", new Object());
            Assert.assertEquals("_pP__g_WJu4_q8_3_lH_", nameAllocator.newName("!pP}@g/WJu4%q8 3 lH#", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSubstitute_sd7933_sd8132 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).replace('!', '!');
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8133() throws Exception {
        String __DSPOT_suggestion_1525 = "Le!m)OkD`_8;V@;]r(4I";
        String o_characterMappingSubstitute_sd7933__6 = new NameAllocator().newName("!pP}@g/WJu4%q8 3 lH#", new Object());
        Assert.assertEquals("_pP__g_WJu4_q8_3_lH_", o_characterMappingSubstitute_sd7933__6);
        Assert.assertEquals("c-jE5_-3c%@5p!yG18MP", "c-jE5_-3c%@5p!yG18MP");
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8133_add9959() throws Exception {
        String __DSPOT_suggestion_1525 = "Le!m)OkD`_8;V@;]r(4I";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName("!pP}@g/WJu4%q8 3 lH#", new Object());
        Assert.assertEquals("_pP__g_WJu4_q8_3_lH_", o_characterMappingSubstitute_sd7933__6);
        String o_characterMappingSubstitute_sd7933_sd8133_add9959__11 = nameAllocator.newName(__DSPOT_suggestion_1525);
        Assert.assertEquals("Le_m_OkD__8_V___r_4I", o_characterMappingSubstitute_sd7933_sd8133_add9959__11);
        Assert.assertEquals("Le_m_OkD__8_V___r_4I_", nameAllocator.newName(__DSPOT_suggestion_1525));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8133_add9958_failAssert6() throws Exception {
        try {
            String __DSPOT_suggestion_1525 = "Le!m)OkD`_8;V@;]r(4I";
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "!pP}@g/WJu4%q8 3 lH#";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            String o_characterMappingSubstitute_sd7933_sd8133__11 = nameAllocator.newName("Le!m)OkD`_8;V@;]r(4I");
            org.junit.Assert.fail("characterMappingSubstitute_sd7933_sd8133_add9958 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11878() throws Exception {
        Assert.assertEquals("Rf__UE_cBG_xBr$_ddWh", new NameAllocator().newName("Rf!-UE#cBG;xBr$/ddWh"));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11879() throws Exception {
        Assert.assertEquals("b__LU__M_ie_8fqhSwD_", new NameAllocator().newName("b]_LU _M-ie@8fqhSwD]", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11877_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_2274 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("characterMappingSurrogate_sd11877 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11879_sd12080() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11879__6 = nameAllocator.newName("b]_LU _M-ie@8fqhSwD]", new Object());
        Assert.assertEquals("b__LU__M_ie_8fqhSwD_", o_characterMappingSurrogate_sd11879__6);
        Assert.assertEquals("_F__0_p9____Z_DUxd__", nameAllocator.newName("=F^@0/p9#<&>Z(DUxd&&", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11879litString12072() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11879litString12064() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a_b", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11879_add12081_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "b]_LU _M-ie@8fqhSwD]";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            String o_characterMappingSurrogate_sd11879__6 = nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            org.junit.Assert.fail("characterMappingSurrogate_sd11879_add12081 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11878_sd12054() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11878__4 = nameAllocator.newName("Rf!-UE#cBG;xBr$/ddWh");
        Assert.assertEquals("Rf__UE_cBG_xBr$_ddWh", o_characterMappingSurrogate_sd11878__4);
        Assert.assertEquals("_7__Zhbf3___6Y_2Hs__0", nameAllocator.newName("7#<Zhbf3]<,6Y%2Hs*&0"));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11879_sd12078_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_2282 = new Object();
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "b]_LU _M-ie@8fqhSwD]";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_sd11879__6 = nameAllocator.newName("b]_LU _M-ie@8fqhSwD]", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSurrogate_sd11879_sd12078 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11878_add12056() throws Exception {
        String __DSPOT_suggestion_2275 = "Rf!-UE#cBG;xBr$/ddWh";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11878_add12056__4 = nameAllocator.newName(__DSPOT_suggestion_2275);
        Assert.assertEquals("Rf__UE_cBG_xBr$_ddWh", o_characterMappingSurrogate_sd11878_add12056__4);
        Assert.assertEquals("Rf__UE_cBG_xBr$_ddWh_", nameAllocator.newName(__DSPOT_suggestion_2275));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11879_sd12079_add13904() throws Exception {
        String __DSPOT_suggestion_2283 = "t FH0>D43#GgXrvS&iQJ";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11879__6 = nameAllocator.newName("b]_LU _M-ie@8fqhSwD]", new Object());
        Assert.assertEquals("b__LU__M_ie_8fqhSwD_", o_characterMappingSurrogate_sd11879__6);
        String o_characterMappingSurrogate_sd11879_sd12079_add13904__11 = nameAllocator.newName(__DSPOT_suggestion_2283);
        Assert.assertEquals("t_FH0_D43_GgXrvS_iQJ", o_characterMappingSurrogate_sd11879_sd12079_add13904__11);
        Assert.assertEquals("t_FH0_D43_GgXrvS_iQJ_", nameAllocator.newName(__DSPOT_suggestion_2283));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11879_sd12080litString13938() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11879__6 = nameAllocator.newName("public", new Object());
        Assert.assertEquals("public_", o_characterMappingSurrogate_sd11879__6);
        Assert.assertEquals("_F__0_p9____Z_DUxd__", nameAllocator.newName("=F^@0/p9#<&>Z(DUxd&&", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11879_add12081_failAssert2_sd14960() throws Exception {
        try {
            Object __DSPOT_tag_2828 = new Object();
            String __DSPOT_suggestion_2827 = "0DsOTkxdwW3Fs)]<D4j}";
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "b]_LU _M-ie@8fqhSwD]";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_sd11879_add12081_failAssert2_sd14960__11 = nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            Assert.assertEquals("b__LU__M_ie_8fqhSwD_", nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277));
            String o_characterMappingSurrogate_sd11879__6 = nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            org.junit.Assert.fail("characterMappingSurrogate_sd11879_add12081 should have thrown IllegalArgumentException");
            nameAllocator.newName("0DsOTkxdwW3Fs)]<D4j}", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11879_sd12080litString13923() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11879__6 = nameAllocator.newName("b]_LU _M-ie@8fqhSwD]", new Object());
        Assert.assertEquals("b__LU__M_ie_8fqhSwD_", o_characterMappingSurrogate_sd11879__6);
        Assert.assertEquals("_F__0_p9____Z_DUNxd__", nameAllocator.newName("=F^@0/p9#<&>Z(DUNxd&&", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11879_sd12077_sd13855() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11879__6 = nameAllocator.newName("b]_LU _M-ie@8fqhSwD]", new Object());
        Assert.assertEquals("b__LU__M_ie_8fqhSwD_", o_characterMappingSurrogate_sd11879__6);
        Assert.assertEquals("XaObrWo_d_A6__Y_6df_", nameAllocator.clone().newName("XaObrWo-d,A6]&Y`6df[", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11878_sd12055litString13234() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11878__4 = nameAllocator.newName("1ab");
        Assert.assertEquals("_1ab", o_characterMappingSurrogate_sd11878__4);
        Assert.assertEquals("mBXH7_5b_sgo__L_VAY_", nameAllocator.newName("mBXH7;5b=sgo(}L,VAY[", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11879_sd12078_failAssert1_sd14884() throws Exception {
        try {
            Object __DSPOT_tag_2759 = new Object();
            String __DSPOT_suggestion_2758 = "=8G?uxW#&JY,9,Ju_v2&";
            Object __DSPOT_tag_2282 = new Object();
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "b]_LU _M-ie@8fqhSwD]";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_sd11879__6 = nameAllocator.newName("b]_LU _M-ie@8fqhSwD]", new Object());
            Assert.assertEquals("b__LU__M_ie_8fqhSwD_", nameAllocator.newName("b]_LU _M-ie@8fqhSwD]", new Object()));
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSurrogate_sd11879_sd12078 should have thrown IllegalArgumentException");
            nameAllocator.newName("=8G?uxW#&JY,9,Ju_v2&", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11878_sd12055litString13240() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11878__4 = nameAllocator.newName("tag 1 cannot be used for both 'foo' and 'bar'");
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", o_characterMappingSurrogate_sd11878__4);
        Assert.assertEquals("mBXH7_5b_sgo__L_VAY_", nameAllocator.newName("mBXH7;5b=sgo(}L,VAY[", new Object()));
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString15793() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString15793__3 = outterAllocator.newName("public", 1);
        Assert.assertEquals("public_", o_cloneUsagelitString15793__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
    }

    @Test(timeout = 10000)
    public void cloneUsage_add16032_failAssert17() throws Exception {
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
    public void cloneUsagelitString15783() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString15783__3 = outterAllocator.newName("1ab", 1);
        Assert.assertEquals("_1ab", o_cloneUsagelitString15783__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
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
    public void cloneUsagelitString15784() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString15784__3 = outterAllocator.newName(".|Ad{^v2dZ", 1);
        Assert.assertEquals("__Ad__v2dZ", o_cloneUsagelitString15784__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15980() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15980__4 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15980__4);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        Assert.assertEquals("_T_X__K8o9j6gEQQ_Fd_", outterAllocator.clone().newName("]T`X!)K8o9j6gEQQ)Fd."));
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString15788() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString15788__3 = outterAllocator.newName("(oo", 1);
        Assert.assertEquals("_oo", o_cloneUsagelitString15788__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
    }

    @Test(timeout = 10000)
    public void cloneUsage_add16040() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_add16040__3 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_add16040__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        Assert.assertEquals("foo_", outterAllocator.clone().newName("foo", 2));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16001() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        int o_cloneUsage_sd16001__12 = outterAllocator.newName("foo", 1).indexOf(-384625037, -50044092);
        Assert.assertEquals(-1, ((int) (o_cloneUsage_sd16001__12)));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16026() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("foo", outterAllocator.newName("foo", 1).toLowerCase());
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15977_sd20354() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15977__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15977__6);
        String o_cloneUsage_sd15977__11 = outterAllocator.clone().newName("UVzU3:B2x0c0Mp;?d>Pd", new Object());
        Assert.assertEquals("UVzU3_B2x0c0Mp__d_Pd", o_cloneUsage_sd15977__11);
        Assert.assertEquals("___hck_usuDHL7pc_q_W", outterAllocator.clone().newName("!!]hck!usuDHL7pc!q%W"));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15977_sd20349_failAssert11() throws Exception {
        try {
            Object __DSPOT_tag_4285 = new Object();
            Object __DSPOT_tag_3039 = new Object();
            String __DSPOT_suggestion_3038 = "UVzU3:B2x0c0Mp;?d>Pd";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_sd15977__6 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_sd15977__11 = innerAllocator1.newName("UVzU3:B2x0c0Mp;?d>Pd", new Object());
            innerAllocator1.get(new Object());
            org.junit.Assert.fail("cloneUsage_sd15977_sd20349 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16013_sd22171() throws Exception {
        String __DSPOT_arg2_3083 = "k@!?BD@nbPziD<E{_;y=";
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        boolean o_cloneUsage_sd16013__15 = outterAllocator.newName("foo", 1).regionMatches(true, -998702579, __DSPOT_arg2_3083, -1713617671, -153139005);
        boolean o_cloneUsage_sd16013_sd22171__24 = __DSPOT_arg2_3083.regionMatches(true, 713157928, "2WVP#w-@(Qp5c#0Yd5Oa", 739202839, -507183855);
        Assert.assertFalse(o_cloneUsage_sd16013_sd22171__24);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16016_sd22571() throws Exception {
        String __DSPOT_arg0_3092 = "l}]?kr4.G+W2ev!M]pK8";
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_sd16016__12 = outterAllocator.newName("foo", 1).replaceAll(__DSPOT_arg0_3092, "%U<?:CpSi`t?j7$2%zDC");
        Assert.assertEquals("foo", o_cloneUsage_sd16016__12);
        boolean o_cloneUsage_sd16016_sd22571__21 = __DSPOT_arg0_3092.regionMatches(true, -1647231532, "wji*1.lf%O3kbsW]rkP|", 1928532873, 563821218);
        Assert.assertFalse(o_cloneUsage_sd16016_sd22571__21);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15977_sd20346() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15977__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15977__6);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_sd15977__11 = outterAllocator.clone().newName("UVzU3:B2x0c0Mp;?d>Pd", new Object());
        Assert.assertEquals("UVzU3_B2x0c0Mp__d_Pd", o_cloneUsage_sd15977__11);
        Assert.assertEquals("gP9QRX_S5sqip_K_FIqe", outterAllocator.newName("gP9QRX?S5sqip{K&FIqe"));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15981_add20522_failAssert17() throws Exception {
        try {
            Object __DSPOT_tag_3043 = new Object();
            String __DSPOT_suggestion_3042 = "4L 0q(i)qU8^f74o[;6q";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_sd15981__6 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            innerAllocator2.newName(__DSPOT_suggestion_3042, __DSPOT_tag_3043);
            String o_cloneUsage_sd15981__11 = innerAllocator2.newName(__DSPOT_suggestion_3042, __DSPOT_tag_3043);
            org.junit.Assert.fail("cloneUsage_sd15981_add20522 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_add16037_sd23609() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_add16037__3 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_add16037__3);
        String o_cloneUsage_add16037__6 = outterAllocator.clone().newName("foo", 3);
        Assert.assertEquals("foo_", o_cloneUsage_add16037__6);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("__d__0NDJy_L_b_Xp2__", outterAllocator.newName("`_d=^0NDJy{L%b@Xp2[(", new Object()));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15977_sd20347() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15977__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15977__6);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_sd15977__11 = outterAllocator.clone().newName("UVzU3:B2x0c0Mp;?d>Pd", new Object());
        Assert.assertEquals("UVzU3_B2x0c0Mp__d_Pd", o_cloneUsage_sd15977__11);
        Assert.assertEquals("_1_dR_j__9__Rl50X___l", outterAllocator.newName("1]dR!j#-9%|Rl50X;{_l", new Object()));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16013_sd22174_sd31325_failAssert36() throws Exception {
        try {
            Object __DSPOT_tag_10082 = new Object();
            String __DSPOT_arg1_5337 = "I `C)%1^#[3d&Ya@Us[d";
            String __DSPOT_arg0_5336 = "j%JC}?ifLoxnlGwe=ao|";
            int __DSPOT_arg4_3085 = -153139005;
            int __DSPOT_arg3_3084 = -1713617671;
            String __DSPOT_arg2_3083 = "k@!?BD@nbPziD<E{_;y=";
            int __DSPOT_arg1_3082 = -998702579;
            boolean __DSPOT_arg0_3081 = true;
            NameAllocator outterAllocator = new NameAllocator();
            String __DSPOT_invoc_3 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            boolean o_cloneUsage_sd16013__15 = outterAllocator.newName("foo", 1).regionMatches(true, -998702579, __DSPOT_arg2_3083, -1713617671, -153139005);
            String o_cloneUsage_sd16013_sd22174__21 = __DSPOT_arg2_3083.replaceAll("j%JC}?ifLoxnlGwe=ao|", "I `C)%1^#[3d&Ya@Us[d");
            outterAllocator.clone().get(new Object());
            org.junit.Assert.fail("cloneUsage_sd16013_sd22174_sd31325 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16013_sd22174_sd31327() throws Exception {
        String __DSPOT_arg2_3083 = "k@!?BD@nbPziD<E{_;y=";
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        boolean o_cloneUsage_sd16013__15 = outterAllocator.newName("foo", 1).regionMatches(true, -998702579, __DSPOT_arg2_3083, -1713617671, -153139005);
        String o_cloneUsage_sd16013_sd22174__21 = __DSPOT_arg2_3083.replaceAll("j%JC}?ifLoxnlGwe=ao|", "I `C)%1^#[3d&Ya@Us[d");
        Assert.assertEquals("I `C)%1^#[3d&Ya@Us[dkI `C)%1^#[3d&Ya@Us[d@I `C)%1^#[3d&Ya@Us[d!I `C)%1^#[3d&Ya@Us[d?I `C)%1^#[3d&Ya@Us[dBI `C)%1^#[3d&Ya@Us[dDI `C)%1^#[3d&Ya@Us[d@I `C)%1^#[3d&Ya@Us[dnI `C)%1^#[3d&Ya@Us[dbI `C)%1^#[3d&Ya@Us[dPI `C)%1^#[3d&Ya@Us[dzI `C)%1^#[3d&Ya@Us[diI `C)%1^#[3d&Ya@Us[dDI `C)%1^#[3d&Ya@Us[d<I `C)%1^#[3d&Ya@Us[dEI `C)%1^#[3d&Ya@Us[d{I `C)%1^#[3d&Ya@Us[d_I `C)%1^#[3d&Ya@Us[d;I `C)%1^#[3d&Ya@Us[dyI `C)%1^#[3d&Ya@Us[d=I `C)%1^#[3d&Ya@Us[d", o_cloneUsage_sd16013_sd22174__21);
        Assert.assertEquals("_rO_K6_EBZ_xY___KC99", outterAllocator.clone().newName("]rO=K6!EBZ[xY:/@KC99", new Object()));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16016_sd22522_sd28796() throws Exception {
        String __DSPOT_arg1_3093 = "%U<?:CpSi`t?j7$2%zDC";
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_sd16016__12 = outterAllocator.newName("foo", 1).replaceAll("l}]?kr4.G+W2ev!M]pK8", __DSPOT_arg1_3093);
        Assert.assertEquals("foo", o_cloneUsage_sd16016__12);
        boolean o_cloneUsage_sd16016_sd22522__21 = __DSPOT_arg1_3093.regionMatches(false, -1965840650, "j(mX?}t%TWcwfQPM^&LB", 459384341, 1275839087);
        boolean o_cloneUsage_sd16016_sd22522_sd28796__30 = o_cloneUsage_sd16016__12.regionMatches(false, 1013677436, "|d+0#nbgrT1UzQ#q-qv}", -1420391335, -479102380);
        Assert.assertFalse(o_cloneUsage_sd16016_sd22522_sd28796__30);
        Assert.assertEquals("foo", o_cloneUsage_sd16016__12);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16013_sd22174_sd31326() throws Exception {
        String __DSPOT_arg2_3083 = "k@!?BD@nbPziD<E{_;y=";
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        boolean o_cloneUsage_sd16013__15 = outterAllocator.newName("foo", 1).regionMatches(true, -998702579, __DSPOT_arg2_3083, -1713617671, -153139005);
        String o_cloneUsage_sd16013_sd22174__21 = __DSPOT_arg2_3083.replaceAll("j%JC}?ifLoxnlGwe=ao|", "I `C)%1^#[3d&Ya@Us[d");
        Assert.assertEquals("I `C)%1^#[3d&Ya@Us[dkI `C)%1^#[3d&Ya@Us[d@I `C)%1^#[3d&Ya@Us[d!I `C)%1^#[3d&Ya@Us[d?I `C)%1^#[3d&Ya@Us[dBI `C)%1^#[3d&Ya@Us[dDI `C)%1^#[3d&Ya@Us[d@I `C)%1^#[3d&Ya@Us[dnI `C)%1^#[3d&Ya@Us[dbI `C)%1^#[3d&Ya@Us[dPI `C)%1^#[3d&Ya@Us[dzI `C)%1^#[3d&Ya@Us[diI `C)%1^#[3d&Ya@Us[dDI `C)%1^#[3d&Ya@Us[d<I `C)%1^#[3d&Ya@Us[dEI `C)%1^#[3d&Ya@Us[d{I `C)%1^#[3d&Ya@Us[d_I `C)%1^#[3d&Ya@Us[d;I `C)%1^#[3d&Ya@Us[dyI `C)%1^#[3d&Ya@Us[d=I `C)%1^#[3d&Ya@Us[d", o_cloneUsage_sd16013_sd22174__21);
        Assert.assertEquals("zJCF___9Yl_Uaeg____o", outterAllocator.clone().newName("zJCF];&9Yl:Uaeg_: ]o"));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15981_sd20510_sd26764() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15981__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15981__6);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        String o_cloneUsage_sd15981__11 = outterAllocator.clone().newName("4L 0q(i)qU8^f74o[;6q", new Object());
        Assert.assertEquals("_4L_0q_i_qU8_f74o__6q", o_cloneUsage_sd15981__11);
        String o_cloneUsage_sd15981_sd20510__20 = outterAllocator.newName("ya*IJMY8)eQ&n]=`an0i", new Object());
        Assert.assertEquals("ya_IJMY8_eQ_n___an0i", o_cloneUsage_sd15981_sd20510__20);
        Assert.assertEquals("__d__axa_iuejl_r__zX", outterAllocator.newName("?=d#%axa-iuejl@r?_zX", new Object()));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16013_sd22174_sd31272() throws Exception {
        String __DSPOT_arg2_3083 = "k@!?BD@nbPziD<E{_;y=";
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        boolean o_cloneUsage_sd16013__15 = outterAllocator.newName("foo", 1).regionMatches(true, -998702579, __DSPOT_arg2_3083, -1713617671, -153139005);
        String o_cloneUsage_sd16013_sd22174__21 = __DSPOT_arg2_3083.replaceAll("j%JC}?ifLoxnlGwe=ao|", "I `C)%1^#[3d&Ya@Us[d");
        Assert.assertEquals("I `C)%1^#[3d&Ya@Us[dkI `C)%1^#[3d&Ya@Us[d@I `C)%1^#[3d&Ya@Us[d!I `C)%1^#[3d&Ya@Us[d?I `C)%1^#[3d&Ya@Us[dBI `C)%1^#[3d&Ya@Us[dDI `C)%1^#[3d&Ya@Us[d@I `C)%1^#[3d&Ya@Us[dnI `C)%1^#[3d&Ya@Us[dbI `C)%1^#[3d&Ya@Us[dPI `C)%1^#[3d&Ya@Us[dzI `C)%1^#[3d&Ya@Us[diI `C)%1^#[3d&Ya@Us[dDI `C)%1^#[3d&Ya@Us[d<I `C)%1^#[3d&Ya@Us[dEI `C)%1^#[3d&Ya@Us[d{I `C)%1^#[3d&Ya@Us[d_I `C)%1^#[3d&Ya@Us[d;I `C)%1^#[3d&Ya@Us[dyI `C)%1^#[3d&Ya@Us[d=I `C)%1^#[3d&Ya@Us[d", o_cloneUsage_sd16013_sd22174__21);
        int o_cloneUsage_sd16013_sd22174_sd31272__26 = __DSPOT_arg2_3083.compareTo("{wMd0U2*<w%x#LE/ZnZ&");
        Assert.assertEquals(-16, ((int) (o_cloneUsage_sd16013_sd22174_sd31272__26)));
    }

    @Test(timeout = 10000)
    public void javaKeyword_add40278_failAssert1() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(1);
            org.junit.Assert.fail("javaKeyword_add40278 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40273() throws Exception {
        Assert.assertEquals("P2Ned__bg6wlT76__seD", new NameAllocator().newName("P2Ned]}bg6wlT76/)seD"));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40274() throws Exception {
        Assert.assertEquals("_9IpH__g__C_65n___gR", new NameAllocator().newName("]9IpH#+g;-C 65n] |gR", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_add40276() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", 1));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40274_add40496_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_15612 = new Object();
            String __DSPOT_suggestion_15611 = "]9IpH#+g;-C 65n] |gR";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_15611, __DSPOT_tag_15612);
            String o_javaKeyword_sd40274__6 = nameAllocator.newName(__DSPOT_suggestion_15611, __DSPOT_tag_15612);
            org.junit.Assert.fail("javaKeyword_sd40274_add40496 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40273litString40460() throws Exception {
        Assert.assertEquals("P2Ned__bg6wyT76__seD", new NameAllocator().newName("P2Ned]}bg6wyT76/)seD"));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40273litString40465() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40274_sd40495() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40274__6 = nameAllocator.newName("]9IpH#+g;-C 65n] |gR", new Object());
        Assert.assertEquals("_9IpH__g__C_65n___gR", o_javaKeyword_sd40274__6);
        Assert.assertEquals("C4_ti40B_NOn___Z_JK_", nameAllocator.newName("C4]ti40B(NOn;[#Z>JK;", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40273_add40471() throws Exception {
        String __DSPOT_suggestion_15610 = "P2Ned]}bg6wlT76/)seD";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40273_add40471__4 = nameAllocator.newName(__DSPOT_suggestion_15610);
        Assert.assertEquals("P2Ned__bg6wlT76__seD", o_javaKeyword_sd40273_add40471__4);
        Assert.assertEquals("P2Ned__bg6wlT76__seD_", nameAllocator.newName(__DSPOT_suggestion_15610));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40273litString40458() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab"));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40274_sd40493_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_15617 = new Object();
            Object __DSPOT_tag_15612 = new Object();
            String __DSPOT_suggestion_15611 = "]9IpH#+g;-C 65n] |gR";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_sd40274__6 = nameAllocator.newName("]9IpH#+g;-C 65n] |gR", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("javaKeyword_sd40274_sd40493 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40274_sd40492_sd42500() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40274__6 = nameAllocator.newName("]9IpH#+g;-C 65n] |gR", new Object());
        Assert.assertEquals("_9IpH__g__C_65n___gR", o_javaKeyword_sd40274__6);
        Assert.assertEquals("N___eIfU__Gea_VG6qiH", nameAllocator.clone().newName("N )<eIfU<@Gea-VG6qiH", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40274_sd40495litString42559() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40274__6 = nameAllocator.newName("]9IpH#+g;-C 65n] |gR", new Object());
        Assert.assertEquals("_9IpH__g__C_65n___gR", o_javaKeyword_sd40274__6);
        Assert.assertEquals("public_", nameAllocator.newName("public", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40274_sd40493_failAssert1_sd43538() throws Exception {
        try {
            StringBuffer __DSPOT_arg0_16173 = new StringBuffer();
            Object __DSPOT_tag_15617 = new Object();
            Object __DSPOT_tag_15612 = new Object();
            String __DSPOT_suggestion_15611 = "]9IpH#+g;-C 65n] |gR";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_sd40274__6 = nameAllocator.newName("]9IpH#+g;-C 65n] |gR", new Object());
            Assert.assertEquals("_9IpH__g__C_65n___gR", nameAllocator.newName("]9IpH#+g;-C 65n] |gR", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("javaKeyword_sd40274_sd40493 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).contentEquals(new StringBuffer());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40274_sd40494_add42549() throws Exception {
        String __DSPOT_suggestion_15618 = "#&LapY5LkzyhP.O(6G#0";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40274__6 = nameAllocator.newName("]9IpH#+g;-C 65n] |gR", new Object());
        Assert.assertEquals("_9IpH__g__C_65n___gR", o_javaKeyword_sd40274__6);
        String o_javaKeyword_sd40274_sd40494_add42549__11 = nameAllocator.newName(__DSPOT_suggestion_15618);
        Assert.assertEquals("__LapY5LkzyhP_O_6G_0", o_javaKeyword_sd40274_sd40494_add42549__11);
        Assert.assertEquals("__LapY5LkzyhP_O_6G_0_", nameAllocator.newName(__DSPOT_suggestion_15618));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40274_sd40495litString42562() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40274__6 = nameAllocator.newName("]9IpH#+g;-C 65n] |gR", new Object());
        Assert.assertEquals("_9IpH__g__C_65n___gR", o_javaKeyword_sd40274__6);
        Assert.assertEquals("_1ab", nameAllocator.newName("1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40274_sd40494_add42548_failAssert6() throws Exception {
        try {
            String __DSPOT_suggestion_15618 = "#&LapY5LkzyhP.O(6G#0";
            Object __DSPOT_tag_15612 = new Object();
            String __DSPOT_suggestion_15611 = "]9IpH#+g;-C 65n] |gR";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_15611, __DSPOT_tag_15612);
            String o_javaKeyword_sd40274__6 = nameAllocator.newName(__DSPOT_suggestion_15611, __DSPOT_tag_15612);
            String o_javaKeyword_sd40274_sd40494__11 = nameAllocator.newName("#&LapY5LkzyhP.O(6G#0");
            org.junit.Assert.fail("javaKeyword_sd40274_sd40494_add42548 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40274_sd40494litString42513() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40274__6 = nameAllocator.newName("]9IpH#+g;-C 65n] |gR", new Object());
        Assert.assertEquals("_9IpH__g__C_65n___gR", o_javaKeyword_sd40274__6);
        Assert.assertEquals("a_b", nameAllocator.newName("a\ud83c\udf7ab"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44541() throws Exception {
        Assert.assertEquals("q_oV_o6KlF__WC_sxn_P", new NameAllocator().newName("q&oV[o6KlF. WC>sxn<P"));
    }

    @Test(timeout = 10000)
    public void nameCollision_add44546() throws Exception {
        Assert.assertEquals("foo", new NameAllocator().newName("foo"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44540_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_16436 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("nameCollision_sd44540 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_add44548litString44893() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a-b"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44542litString44817() throws Exception {
        Assert.assertEquals("foo", new NameAllocator().newName("foo", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_add44548litString44907() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44541_add44815() throws Exception {
        String __DSPOT_suggestion_16437 = "q&oV[o6KlF. WC>sxn<P";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd44541_add44815__4 = nameAllocator.newName(__DSPOT_suggestion_16437);
        Assert.assertEquals("q_oV_o6KlF__WC_sxn_P", o_nameCollision_sd44541_add44815__4);
        Assert.assertEquals("q_oV_o6KlF__WC_sxn_P_", nameAllocator.newName(__DSPOT_suggestion_16437));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44542_add44840_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_16439 = new Object();
            String __DSPOT_suggestion_16438 = "DTG_p *-=OlUVnLzmv&K";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_16438, __DSPOT_tag_16439);
            String o_nameCollision_sd44542__6 = nameAllocator.newName(__DSPOT_suggestion_16438, __DSPOT_tag_16439);
            org.junit.Assert.fail("nameCollision_sd44542_add44840 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_add44548litString44900() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44541_sd44812_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_16440 = new Object();
            String __DSPOT_suggestion_16437 = "q&oV[o6KlF. WC>sxn<P";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_sd44541__4 = nameAllocator.newName("q&oV[o6KlF. WC>sxn<P");
            nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollision_sd44541_sd44812 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_add44548litString44904() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab"));
    }

    @Test(timeout = 10000)
    public void nameCollision_add44548litString44903() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44542_sd44836_sd46801() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd44542__6 = nameAllocator.newName("DTG_p *-=OlUVnLzmv&K", new Object());
        Assert.assertEquals("DTG_p____OlUVnLzmv_K", o_nameCollision_sd44542__6);
        Assert.assertEquals("_6_j9S__9ddK____4g_MP", nameAllocator.clone().newName("6/j9S,#9ddK@(,+4g;MP", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44542_sd44839_add46895_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_16447 = new Object();
            String __DSPOT_suggestion_16446 = "L1x&hI[K1b,+4gNIZ]cm";
            Object __DSPOT_tag_16439 = new Object();
            String __DSPOT_suggestion_16438 = "DTG_p *-=OlUVnLzmv&K";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_16438, __DSPOT_tag_16439);
            String o_nameCollision_sd44542__6 = nameAllocator.newName(__DSPOT_suggestion_16438, __DSPOT_tag_16439);
            String o_nameCollision_sd44542_sd44839__13 = nameAllocator.newName("L1x&hI[K1b,+4gNIZ]cm", new Object());
            org.junit.Assert.fail("nameCollision_sd44542_sd44839_add46895 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44542_sd44838_add46850() throws Exception {
        String __DSPOT_suggestion_16445 = "nJIYNm`-a):|ewc9Htf|";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd44542__6 = nameAllocator.newName("DTG_p *-=OlUVnLzmv&K", new Object());
        Assert.assertEquals("DTG_p____OlUVnLzmv_K", o_nameCollision_sd44542__6);
        String o_nameCollision_sd44542_sd44838_add46850__11 = nameAllocator.newName(__DSPOT_suggestion_16445);
        Assert.assertEquals("nJIYNm__a___ewc9Htf_", o_nameCollision_sd44542_sd44838_add46850__11);
        Assert.assertEquals("nJIYNm__a___ewc9Htf__", nameAllocator.newName(__DSPOT_suggestion_16445));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44542_sd44839_sd46893() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd44542__6 = nameAllocator.newName("DTG_p *-=OlUVnLzmv&K", new Object());
        Assert.assertEquals("DTG_p____OlUVnLzmv_K", o_nameCollision_sd44542__6);
        String o_nameCollision_sd44542_sd44839__13 = nameAllocator.newName("L1x&hI[K1b,+4gNIZ]cm", new Object());
        Assert.assertEquals("L1x_hI_K1b__4gNIZ_cm", o_nameCollision_sd44542_sd44839__13);
        Assert.assertEquals("_D_s6_PRY_Z____UEsNg", nameAllocator.newName("}D[s6{PRY=Z_!|;UEsNg"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44541_sd44814litString46161() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd44541__4 = nameAllocator.newName("q&oV[o6KlF. WC>sxn<P");
        Assert.assertEquals("q_oV_o6KlF__WC_sxn_P", o_nameCollision_sd44541__4);
        Assert.assertEquals("public_", nameAllocator.newName("public", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44542_sd44838litString46825() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd44542__6 = nameAllocator.newName("foo_", new Object());
        Assert.assertEquals("foo_", o_nameCollision_sd44542__6);
        Assert.assertEquals("nJIYNm__a___ewc9Htf_", nameAllocator.newName("nJIYNm`-a):|ewc9Htf|"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44542_sd44837_failAssert1_sd49014() throws Exception {
        try {
            String __DSPOT_arg0_17136 = ":lA5w,6#E7p8 W%?,&d_";
            Object __DSPOT_tag_16444 = new Object();
            Object __DSPOT_tag_16439 = new Object();
            String __DSPOT_suggestion_16438 = "DTG_p *-=OlUVnLzmv&K";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_sd44542__6 = nameAllocator.newName("DTG_p *-=OlUVnLzmv&K", new Object());
            Assert.assertEquals("DTG_p____OlUVnLzmv_K", nameAllocator.newName("DTG_p *-=OlUVnLzmv&K", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollision_sd44542_sd44837 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).endsWith(":lA5w,6#E7p8 W%?,&d_");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_sd44542_sd44838litString46819() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd44542__6 = nameAllocator.newName("DTG_p *-=OlUVnLzmv&K", new Object());
        Assert.assertEquals("DTG_p____OlUVnLzmv_K", o_nameCollision_sd44542__6);
        Assert.assertEquals("_1ab", nameAllocator.newName("_1ab"));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50147() throws Exception {
        Assert.assertEquals("foo", new NameAllocator().newName("foo", 2));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50155_failAssert3() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(3);
            org.junit.Assert.fail("nameCollisionWithTag_add50155 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50143() throws Exception {
        Assert.assertEquals("_WR83ES_dg__gN0_NO_g", new NameAllocator().newName("+WR83ES}dg& gN0}NO?g", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50142() throws Exception {
        Assert.assertEquals("Kjk9Hl_h__b____O_Hyp", new NameAllocator().newName("Kjk9Hl&h-}b|]=}O Hyp"));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50149litString50612() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", 3));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50149litString50613() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab", 3));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50149litString50616() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", 3));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50149_add50626_failAssert2() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 3);
            String o_nameCollisionWithTag_add50149__3 = nameAllocator.newName("foo", 3);
            org.junit.Assert.fail("nameCollisionWithTag_add50149_add50626 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50143_sd50537_failAssert3() throws Exception {
        try {
            Object __DSPOT_tag_17475 = new Object();
            Object __DSPOT_tag_17470 = new Object();
            String __DSPOT_suggestion_17469 = "+WR83ES}dg& gN0}NO?g";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_sd50143__6 = nameAllocator.newName("+WR83ES}dg& gN0}NO?g", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollisionWithTag_sd50143_sd50537 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50149litString50607() throws Exception {
        Assert.assertEquals("unknown_tag__1", new NameAllocator().newName("unknown tag: 1", 3));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50143_sd50538() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50143__6 = nameAllocator.newName("+WR83ES}dg& gN0}NO?g", new Object());
        Assert.assertEquals("_WR83ES_dg__gN0_NO_g", o_nameCollisionWithTag_sd50143__6);
        Assert.assertEquals("___QJ_JeKw_x__b9__PU", nameAllocator.newName(".?|QJ>JeKw<x+^b9:*PU"));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50147litString50569() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", 2));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50149litString50599() throws Exception {
        Assert.assertEquals("foo_", new NameAllocator().newName("foo_", 3));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50142litString50505() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50143_sd50538litString53500() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50143__6 = nameAllocator.newName("public", new Object());
        Assert.assertEquals("public_", o_nameCollisionWithTag_sd50143__6);
        Assert.assertEquals("___QJ_JeKw_x__b9__PU", nameAllocator.newName(".?|QJ>JeKw<x+^b9:*PU"));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50143_sd50538litString53477() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50143__6 = nameAllocator.newName("+WR83ES}dg& gN0}NO?g", new Object());
        Assert.assertEquals("_WR83ES_dg__gN0_NO_g", o_nameCollisionWithTag_sd50143__6);
        Assert.assertEquals("foo_", nameAllocator.newName("foo_"));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50143_sd50539litString51472() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50143__6 = nameAllocator.newName("foo", new Object());
        Assert.assertEquals("foo", o_nameCollisionWithTag_sd50143__6);
        Assert.assertEquals("Q_S$Jnm_D__e__6c__g_", nameAllocator.newName("Q-S$Jnm|D*!e?>6c!*g[", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50143_sd50538_add53520() throws Exception {
        String __DSPOT_suggestion_17476 = ".?|QJ>JeKw<x+^b9:*PU";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50143__6 = nameAllocator.newName("+WR83ES}dg& gN0}NO?g", new Object());
        Assert.assertEquals("_WR83ES_dg__gN0_NO_g", o_nameCollisionWithTag_sd50143__6);
        String o_nameCollisionWithTag_sd50143_sd50538_add53520__11 = nameAllocator.newName(__DSPOT_suggestion_17476);
        Assert.assertEquals("___QJ_JeKw_x__b9__PU", o_nameCollisionWithTag_sd50143_sd50538_add53520__11);
        Assert.assertEquals("___QJ_JeKw_x__b9__PU_", nameAllocator.newName(__DSPOT_suggestion_17476));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50143_sd50538litString53479() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50143__6 = nameAllocator.newName("+WR83ES}dg& gN0}NO?g", new Object());
        Assert.assertEquals("_WR83ES_dg__gN0_NO_g", o_nameCollisionWithTag_sd50143__6);
        Assert.assertEquals("a_b", nameAllocator.newName("a-b"));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50143_sd50537_failAssert3_sd54340() throws Exception {
        try {
            String __DSPOT_arg0_18341 = " fviKK^sn([=PU<#nwos";
            Object __DSPOT_tag_17475 = new Object();
            Object __DSPOT_tag_17470 = new Object();
            String __DSPOT_suggestion_17469 = "+WR83ES}dg& gN0}NO?g";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_sd50143__6 = nameAllocator.newName("+WR83ES}dg& gN0}NO?g", new Object());
            Assert.assertEquals("_WR83ES_dg__gN0_NO_g", nameAllocator.newName("+WR83ES}dg& gN0}NO?g", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollisionWithTag_sd50143_sd50537 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).compareToIgnoreCase(" fviKK^sn([=PU<#nwos");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50143_sd50538litString53503() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50143__6 = nameAllocator.newName("4M+ s?=fy>", new Object());
        Assert.assertEquals("_4M__s__fy_", o_nameCollisionWithTag_sd50143__6);
        Assert.assertEquals("___QJ_JeKw_x__b9__PU", nameAllocator.newName(".?|QJ>JeKw<x+^b9:*PU"));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50143_sd50539litString51479() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50143__6 = nameAllocator.newName("+WR83ES}Cg& gN0}NO?g", new Object());
        Assert.assertEquals("_WR83ES_Cg__gN0_NO_g", o_nameCollisionWithTag_sd50143__6);
        Assert.assertEquals("Q_S$Jnm_D__e__6c__g_", nameAllocator.newName("Q-S$Jnm|D*!e?>6c!*g[", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50143_add50540_failAssert4_sd54408() throws Exception {
        try {
            Object __DSPOT_tag_18402 = new Object();
            String __DSPOT_suggestion_18401 = "nDMQTZ8,TBYR_zve#[+G";
            Object __DSPOT_tag_17470 = new Object();
            String __DSPOT_suggestion_17469 = "+WR83ES}dg& gN0}NO?g";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_sd50143_add50540_failAssert4_sd54408__11 = nameAllocator.newName(__DSPOT_suggestion_17469, __DSPOT_tag_17470);
            Assert.assertEquals("_WR83ES_dg__gN0_NO_g", nameAllocator.newName(__DSPOT_suggestion_17469, __DSPOT_tag_17470));
            String o_nameCollisionWithTag_sd50143__6 = nameAllocator.newName(__DSPOT_suggestion_17469, __DSPOT_tag_17470);
            org.junit.Assert.fail("nameCollisionWithTag_sd50143_add50540 should have thrown IllegalArgumentException");
            nameAllocator.newName("nDMQTZ8,TBYR_zve#[+G", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55118() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55118__4 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55118__4);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("AY___cPJ_0___HOW_1zn", nameAllocator.newName("AY![^cPJ 0*<>HOW?1zn"));
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55068() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55068__3 = nameAllocator.newName("public", 1);
        Assert.assertEquals("public_", o_tagReuseForbiddenlitString55068__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55186() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55186__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55186__3);
        try {
            int __DSPOT_arg3_18558 = 1905508054;
            char[] __DSPOT_arg2_18557 = new char[0];
            int __DSPOT_arg1_18556 = 1934827682;
            int __DSPOT_arg0_18555 = -2143694601;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).getChars(-2143694601, 1934827682, new char[0], 1905508054);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55053() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55053__3 = nameAllocator.newName("tag 1 cannot be used for both 'foo' and 'bar'", 1);
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", o_tagReuseForbiddenlitString55053__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55064() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55064__3 = nameAllocator.newName("1ab", 1);
        Assert.assertEquals("_1ab", o_tagReuseForbiddenlitString55064__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55117_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_18468 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.get(new Object());
            org.junit.Assert.fail("tagReuseForbidden_sd55117 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55051() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55051__3 = nameAllocator.newName("_1ab", 1);
        Assert.assertEquals("_1ab", o_tagReuseForbiddenlitString55051__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55052() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55052__3 = nameAllocator.newName("bar", 1);
        Assert.assertEquals("bar", o_tagReuseForbiddenlitString55052__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55061() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55061__3 = nameAllocator.newName("{oo", 1);
        Assert.assertEquals("_oo", o_tagReuseForbiddenlitString55061__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55204_sd70228() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55204__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55204__3);
        try {
            int __DSPOT_arg3_27917 = -1980596437;
            byte[] __DSPOT_arg2_27916 = new byte[0];
            int __DSPOT_arg1_27915 = -1203652822;
            int __DSPOT_arg0_27914 = -1805201430;
            String __DSPOT_arg1_18586 = "y,i=*X9NGLPH%MG+ (s@";
            String __DSPOT_arg0_18585 = "4)hm*tIV-NV QCM)SC1/";
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            String __DSPOT_invoc_15 = nameAllocator.newName("bar", 1).replaceAll("4)hm*tIV-NV QCM)SC1/", "y,i=*X9NGLPH%MG+ (s@");
            nameAllocator.newName("bar", 1).replaceAll("4)hm*tIV-NV QCM)SC1/", "y,i=*X9NGLPH%MG+ (s@").getBytes(-1805201430, -1203652822, new byte[0], -1980596437);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55119_sd62162() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55119__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55119__6);
        try {
            int __DSPOT_arg1_22802 = -1913338516;
            int __DSPOT_arg0_22801 = -588022799;
            String __DSPOT_invoc_13 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).offsetByCodePoints(-588022799, -1913338516);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("itpo_Z0x9fOUv_KsVo_9", nameAllocator.newName("itpo}Z0x9fOUv:KsVo%9", new Object()));
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55201_sd69751() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55201__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55201__3);
        try {
            int __DSPOT_arg1_27600 = 356227672;
            int __DSPOT_arg0_27599 = -1418589146;
            int __DSPOT_arg4_18578 = 309801623;
            int __DSPOT_arg3_18577 = -1488125976;
            String __DSPOT_arg2_18576 = ",R])C!rXzk+?l5C|#QN(";
            int __DSPOT_arg1_18575 = -58972954;
            boolean __DSPOT_arg0_18574 = true;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(true, -58972954, __DSPOT_arg2_18576, -1488125976, 309801623);
            __DSPOT_arg2_18576.offsetByCodePoints(-1418589146, 356227672);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55201_sd69720() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55201__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55201__3);
        try {
            int __DSPOT_arg4_18578 = 309801623;
            int __DSPOT_arg3_18577 = -1488125976;
            String __DSPOT_arg2_18576 = ",R])C!rXzk+?l5C|#QN(";
            int __DSPOT_arg1_18575 = -58972954;
            boolean __DSPOT_arg0_18574 = true;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(true, -58972954, ",R])C!rXzk+?l5C|#QN(", -1488125976, 309801623);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("kqU2IPD__JUp5a91_$IT", nameAllocator.newName("kqU2IPD}_JUp5a91!$IT"));
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55201_sd69721() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55201__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55201__3);
        try {
            int __DSPOT_arg4_18578 = 309801623;
            int __DSPOT_arg3_18577 = -1488125976;
            String __DSPOT_arg2_18576 = ",R])C!rXzk+?l5C|#QN(";
            int __DSPOT_arg1_18575 = -58972954;
            boolean __DSPOT_arg0_18574 = true;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(true, -58972954, ",R])C!rXzk+?l5C|#QN(", -1488125976, 309801623);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_q4h_z_E6JkZ3fVxQg1H", nameAllocator.newName("@q4h>z^E6JkZ3fVxQg1H", new Object()));
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55119_sd62131_sd79712() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55119__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55119__6);
        try {
            int __DSPOT_arg0_33455 = 1371837174;
            String __DSPOT_invoc_16 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).charAt(1371837174);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_sd55119__11 = nameAllocator.newName("itpo}Z0x9fOUv:KsVo%9", new Object());
        Assert.assertEquals("itpo_Z0x9fOUv_KsVo_9", o_tagReuseForbidden_sd55119__11);
        Assert.assertEquals("_5tW8wQxD_sBS_mrqVj1", nameAllocator.newName("[5tW8wQxD[sBS#mrqVj1", new Object()));
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55119_sd62131_sd79710() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55119__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55119__6);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_sd55119__11 = nameAllocator.newName("itpo}Z0x9fOUv:KsVo%9", new Object());
        Assert.assertEquals("itpo_Z0x9fOUv_KsVo_9", o_tagReuseForbidden_sd55119__11);
        String o_tagReuseForbidden_sd55119_sd62131__20 = nameAllocator.newName("[5tW8wQxD[sBS#mrqVj1", new Object());
        Assert.assertEquals("_5tW8wQxD_sBS_mrqVj1", o_tagReuseForbidden_sd55119_sd62131__20);
        Assert.assertEquals("_J_YxjA___at$5Ue0FJy", nameAllocator.newName("}J#YxjA;=%at$5Ue0FJy", new Object()));
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55202_sd69889_sd76544() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55202__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55202__3);
        try {
            int __DSPOT_arg4_31551 = -903911445;
            int __DSPOT_arg3_31550 = -653095518;
            String __DSPOT_arg2_31549 = ")EQXFe*r-|=>A*y{Kk&=";
            int __DSPOT_arg1_31548 = -260147986;
            boolean __DSPOT_arg0_31547 = true;
            int __DSPOT_arg4_27674 = 870953028;
            int __DSPOT_arg3_27673 = 361703392;
            String __DSPOT_arg2_27672 = "FM5nk!cn2z)>INEdIzWH";
            int __DSPOT_arg1_27671 = 347611764;
            boolean __DSPOT_arg0_27670 = false;
            int __DSPOT_arg3_18582 = 1710801704;
            int __DSPOT_arg2_18581 = 831491066;
            String __DSPOT_arg1_18580 = "h*9F{j0h3lSp/I%ut)7|";
            int __DSPOT_arg0_18579 = 1482268528;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(1482268528, __DSPOT_arg1_18580, 831491066, 1710801704);
            __DSPOT_arg1_18580.regionMatches(false, 347611764, __DSPOT_arg2_27672, 361703392, 870953028);
            __DSPOT_arg2_27672.regionMatches(true, -260147986, ")EQXFe*r-|=>A*y{Kk&=", -653095518, -903911445);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove55223litString95465() throws Exception {
        try {
            String o_tagReuseForbidden_remove55223__5 = new NameAllocator().newName("1ab", 1);
            Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", 1));
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55119_sd62131_sd79708_failAssert9() throws Exception {
        try {
            Object __DSPOT_tag_33451 = new Object();
            Object __DSPOT_tag_22765 = new Object();
            String __DSPOT_suggestion_22764 = "[5tW8wQxD[sBS#mrqVj1";
            Object __DSPOT_tag_18471 = new Object();
            String __DSPOT_suggestion_18470 = "itpo}Z0x9fOUv:KsVo%9";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_sd55119__6 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_sd55119__11 = nameAllocator.newName("itpo}Z0x9fOUv:KsVo%9", new Object());
            String o_tagReuseForbidden_sd55119_sd62131__20 = nameAllocator.newName("[5tW8wQxD[sBS#mrqVj1", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("tagReuseForbidden_sd55119_sd62131_sd79708 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55119_sd62131_sd79709() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55119__6 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55119__6);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_sd55119__11 = nameAllocator.newName("itpo}Z0x9fOUv:KsVo%9", new Object());
        Assert.assertEquals("itpo_Z0x9fOUv_KsVo_9", o_tagReuseForbidden_sd55119__11);
        String o_tagReuseForbidden_sd55119_sd62131__20 = nameAllocator.newName("[5tW8wQxD[sBS#mrqVj1", new Object());
        Assert.assertEquals("_5tW8wQxD_sBS_mrqVj1", o_tagReuseForbidden_sd55119_sd62131__20);
        Assert.assertEquals("_Cy_aWkk_7_J__2_0ALC", nameAllocator.newName(">Cy+aWkk&7@J=+2;0ALC"));
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove55223litString95472() throws Exception {
        try {
            String o_tagReuseForbidden_remove55223__5 = new NameAllocator().newName("public", 1);
            Assert.assertEquals("public_", new NameAllocator().newName("public", 1));
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void usage_sd99244() throws Exception {
        Assert.assertEquals("_4gGYLO6_5A9S8_fGO_z", new NameAllocator().newName("@4gGYLO6,5A9S8&fGO]z", new Object()));
    }

    @Test(timeout = 10000)
    public void usage_add99248() throws Exception {
        Assert.assertEquals("bar", new NameAllocator().newName("bar", 2));
    }

    @Test(timeout = 10000)
    public void usage_add99252_failAssert2() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(2);
            org.junit.Assert.fail("usage_add99252 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_add99248litString99570() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", 2));
    }

    @Test(timeout = 10000)
    public void usage_sd99243() throws Exception {
        Assert.assertEquals("O_V_B___R__eT_sv__x_", new NameAllocator().newName("O.V<B#&|R`%eT?sv#`x("));
    }

    @Test(timeout = 10000)
    public void usage_sd99243litString99495() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab"));
    }

    @Test(timeout = 10000)
    public void usage_sd99243litString99496() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab"));
    }

    @Test(timeout = 10000)
    public void usage_add99248litString99561() throws Exception {
        Assert.assertEquals("foo", new NameAllocator().newName("foo", 2));
    }

    @Test(timeout = 10000)
    public void usage_sd99243litString99498() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 10000)
    public void usage_add99248litString99578() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", 2));
    }

    @Test(timeout = 10000)
    public void usage_sd99244_add99531_failAssert5() throws Exception {
        try {
            Object __DSPOT_tag_45213 = new Object();
            String __DSPOT_suggestion_45212 = "@4gGYLO6,5A9S8&fGO]z";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_45212, __DSPOT_tag_45213);
            String o_usage_sd99244__6 = nameAllocator.newName(__DSPOT_suggestion_45212, __DSPOT_tag_45213);
            org.junit.Assert.fail("usage_sd99244_add99531 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_add99248_sd99585_failAssert3() throws Exception {
        try {
            Object __DSPOT_tag_45226 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_add99248__3 = nameAllocator.newName("bar", 2);
            nameAllocator.get(new Object());
            org.junit.Assert.fail("usage_add99248_sd99585 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_sd99243_sd99505litString101938() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99243__4 = nameAllocator.newName("a\ud83c\udf7ab");
        Assert.assertEquals("a_b", o_usage_sd99243__4);
        Assert.assertEquals("_8_uxTTZmb4ck8E___rB_", nameAllocator.newName("8[uxTTZmb4ck8E%@#rB`", new Object()));
    }

    @Test(timeout = 10000)
    public void usage_sd99244_sd99529_add102001() throws Exception {
        String __DSPOT_suggestion_45219 = "FTA ygxJ/m[O[dhSODcS";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99244__6 = nameAllocator.newName("@4gGYLO6,5A9S8&fGO]z", new Object());
        Assert.assertEquals("_4gGYLO6_5A9S8_fGO_z", o_usage_sd99244__6);
        String o_usage_sd99244_sd99529_add102001__11 = nameAllocator.newName(__DSPOT_suggestion_45219);
        Assert.assertEquals("FTA_ygxJ_m_O_dhSODcS", o_usage_sd99244_sd99529_add102001__11);
        Assert.assertEquals("FTA_ygxJ_m_O_dhSODcS_", nameAllocator.newName(__DSPOT_suggestion_45219));
    }

    @Test(timeout = 10000)
    public void usage_sd99243_sd99505litString101935() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99243__4 = nameAllocator.newName("a_b");
        Assert.assertEquals("a_b", o_usage_sd99243__4);
        Assert.assertEquals("_8_uxTTZmb4ck8E___rB_", nameAllocator.newName("8[uxTTZmb4ck8E%@#rB`", new Object()));
    }

    @Test(timeout = 10000)
    public void usage_sd99244_sd99528_failAssert2_sd102977() throws Exception {
        try {
            String __DSPOT_suggestion_45916 = "U]!M;]][Q$LlCu${dxk9";
            Object __DSPOT_tag_45218 = new Object();
            Object __DSPOT_tag_45213 = new Object();
            String __DSPOT_suggestion_45212 = "@4gGYLO6,5A9S8&fGO]z";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_sd99244__6 = nameAllocator.newName("@4gGYLO6,5A9S8&fGO]z", new Object());
            Assert.assertEquals("_4gGYLO6_5A9S8_fGO_z", nameAllocator.newName("@4gGYLO6,5A9S8&fGO]z", new Object()));
            nameAllocator.get(new Object());
            org.junit.Assert.fail("usage_sd99244_sd99528 should have thrown IllegalArgumentException");
            nameAllocator.newName("U]!M;]][Q$LlCu${dxk9");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_sd99244_sd99530litString100363() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99244__6 = nameAllocator.newName("@4gGYLO6,5A9S8&fGO]z", new Object());
        Assert.assertEquals("_4gGYLO6_5A9S8_fGO_z", o_usage_sd99244__6);
        Assert.assertEquals("public_", nameAllocator.newName("public", new Object()));
    }

    @Test(timeout = 10000)
    public void usage_sd99244_sd99529_add102000_failAssert6() throws Exception {
        try {
            String __DSPOT_suggestion_45219 = "FTA ygxJ/m[O[dhSODcS";
            Object __DSPOT_tag_45213 = new Object();
            String __DSPOT_suggestion_45212 = "@4gGYLO6,5A9S8&fGO]z";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_45212, __DSPOT_tag_45213);
            String o_usage_sd99244__6 = nameAllocator.newName(__DSPOT_suggestion_45212, __DSPOT_tag_45213);
            String o_usage_sd99244_sd99529__11 = nameAllocator.newName("FTA ygxJ/m[O[dhSODcS");
            org.junit.Assert.fail("usage_sd99244_sd99529_add102000 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103878() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("gHD_o_4eQgLn__x_zx4_", nameAllocator.newName("gHD!o}4eQgLn ,x<zx4(", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103877() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_G_UTK3Zm9_n_HziR__k", nameAllocator.newName("=G/UTK3Zm9.n@HziR_{k"));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103876_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_46191 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.get(new Object());
            org.junit.Assert.fail("useBeforeAllocateForbidden_sd103876 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103878_sd104281() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            StringBuffer __DSPOT_arg0_46342 = new StringBuffer("|Jpmi)S;g({9fM?L0,`v");
            String __DSPOT_invoc_8 = nameAllocator.get(1);
            nameAllocator.get(1).contentEquals(new StringBuffer("|Jpmi)S;g({9fM?L0,`v"));
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("gHD_o_4eQgLn__x_zx4_", nameAllocator.newName("gHD!o}4eQgLn ,x<zx4(", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103878_sd104270() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd103878__10 = nameAllocator.newName("gHD!o}4eQgLn ,x<zx4(", new Object());
        Assert.assertEquals("gHD_o_4eQgLn__x_zx4_", o_useBeforeAllocateForbidden_sd103878__10);
        Assert.assertEquals("__E___bSj5__ah_I__xt", nameAllocator.newName(";[E?_]bSj5_]ah_I,>xt"));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103877litString104175() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("", nameAllocator.newName(""));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103877_add104243() throws Exception {
        String __DSPOT_suggestion_46192 = "=G/UTK3Zm9.n@HziR_{k";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd103877_add104243__8 = nameAllocator.newName(__DSPOT_suggestion_46192);
        Assert.assertEquals("_G_UTK3Zm9_n_HziR__k", o_useBeforeAllocateForbidden_sd103877_add104243__8);
        Assert.assertEquals("_G_UTK3Zm9_n_HziR__k_", nameAllocator.newName(__DSPOT_suggestion_46192));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103878litString104256() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("public_", nameAllocator.newName("public", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103878litString104257() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_1ab", nameAllocator.newName("1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103877_add104243_sd109705() throws Exception {
        String __DSPOT_suggestion_46192 = "=G/UTK3Zm9.n@HziR_{k";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            int __DSPOT_arg3_49917 = -960981824;
            int __DSPOT_arg2_49916 = -1251261019;
            String __DSPOT_arg1_49915 = "{}y*kDeM6%ac_`yi)$}+";
            int __DSPOT_arg0_49914 = 2105811606;
            String __DSPOT_invoc_6 = nameAllocator.get(1);
            nameAllocator.get(1).regionMatches(2105811606, "{}y*kDeM6%ac_`yi)$}+", -1251261019, -960981824);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd103877_add104243__8 = nameAllocator.newName(__DSPOT_suggestion_46192);
        Assert.assertEquals("_G_UTK3Zm9_n_HziR__k", o_useBeforeAllocateForbidden_sd103877_add104243__8);
        Assert.assertEquals("_G_UTK3Zm9_n_HziR__k_", nameAllocator.newName(__DSPOT_suggestion_46192));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103878_sd104271_sd108469() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            int __DSPOT_arg0_49094 = 1899168078;
            String __DSPOT_invoc_11 = nameAllocator.get(1);
            nameAllocator.get(1).codePointAt(1899168078);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd103878__10 = nameAllocator.newName("gHD!o}4eQgLn ,x<zx4(", new Object());
        Assert.assertEquals("gHD_o_4eQgLn__x_zx4_", o_useBeforeAllocateForbidden_sd103878__10);
        Assert.assertEquals("G_l_a1$$afc__P_v__C_", nameAllocator.newName("G,l;a1$$afc*`P#v<{C&", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103878_sd104271_add108518_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_46332 = new Object();
            String __DSPOT_suggestion_46331 = "G,l;a1$$afc*`P#v<{C&";
            Object __DSPOT_tag_46194 = new Object();
            String __DSPOT_suggestion_46193 = "gHD!o}4eQgLn ,x<zx4(";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.newName(__DSPOT_suggestion_46193, __DSPOT_tag_46194);
            String o_useBeforeAllocateForbidden_sd103878__10 = nameAllocator.newName(__DSPOT_suggestion_46193, __DSPOT_tag_46194);
            String o_useBeforeAllocateForbidden_sd103878_sd104271__17 = nameAllocator.newName("G,l;a1$$afc*`P#v<{C&", new Object());
            org.junit.Assert.fail("useBeforeAllocateForbidden_sd103878_sd104271_add108518 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd103877_sd104191_sd114593() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            int __DSPOT_arg1_53608 = 1225974721;
            int __DSPOT_arg0_53607 = -1130566052;
            String __DSPOT_invoc_9 = nameAllocator.get(1);
            nameAllocator.get(1).subSequence(-1130566052, 1225974721);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd103877__8 = nameAllocator.newName("=G/UTK3Zm9.n@HziR_{k");
        Assert.assertEquals("_G_UTK3Zm9_n_HziR__k", o_useBeforeAllocateForbidden_sd103877__8);
        Assert.assertEquals("c0E2_zasE0qdWr5_9_1_", nameAllocator.newName("c0E2#zasE0qdWr5=9<1>", new Object()));
    }
}

