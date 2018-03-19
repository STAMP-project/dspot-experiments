package com.squareup.javapoet;


import java.util.regex.PatternSyntaxException;
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
    public void characterMappingInvalidStartButValidPart_sd46litString225() throws Exception {
        Assert.assertEquals("bar", new NameAllocator().newName("bar", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd45litString201() throws Exception {
        Assert.assertEquals("bar", new NameAllocator().newName("bar"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd45litString212() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
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
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd45litString210() throws Exception {
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
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", new NameAllocator().newName("tag 1 cannot be used for both 'foo' and 'bar'", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46litString242() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab", new Object()));
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
    public void characterMappingInvalidStartButValidPart_sd45_sd222_sd1412_failAssert4() throws Exception {
        try {
            Object __DSPOT_tag_185 = new Object();
            Object __DSPOT_tag_7 = new Object();
            String __DSPOT_suggestion_6 = "k*201yCi*OdwpauR%h1,";
            String __DSPOT_suggestion_1 = "[{$QV5:Wz2[|+mr6#-Vt";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartButValidPart_sd45__4 = nameAllocator.newName("[{$QV5:Wz2[|+mr6#-Vt");
            String o_characterMappingInvalidStartButValidPart_sd45_sd222__11 = nameAllocator.newName("k*201yCi*OdwpauR%h1,", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartButValidPart_sd45_sd222_sd1412 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd244_sd2022() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("N3u__h__LLuBuAr_pT_v", nameAllocator.clone().newName("N3u/#h+}LLuBuAr^pT%v", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd246litString2041() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("_YSe__xHdm7__To_D7x", nameAllocator.newName(">YSe|%xHdm7#=To)D7x"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartButValidPart_sd46_sd247litString2101() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("1ab", new Object());
        Assert.assertEquals("_1ab", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("__Bob5_83OI__k_a8_J8", nameAllocator.newName(">[Bob5_83OI`-k-a8(J8", new Object()));
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
    public void characterMappingInvalidStartButValidPart_sd46_sd246litString2028() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartButValidPart_sd46__6 = nameAllocator.newName("X(r!Fs2l>UgIvC=TU&zg", new Object());
        Assert.assertEquals("X_r_Fs2l_UgIvC_TU_zg", o_characterMappingInvalidStartButValidPart_sd46__6);
        Assert.assertEquals("public_", nameAllocator.newName("public"));
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
        Assert.assertEquals("___Rs_TQeQ__7G_rHE0Y", new NameAllocator().newName("*=]Rs@TQeQ<=7G/rHE0Y", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990() throws Exception {
        Assert.assertEquals("_a_yvGr_Sm_fld0_x_qm", new NameAllocator().newName("{a=yvGr@Sm?fld0|x(qm"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990litString4151() throws Exception {
        Assert.assertEquals("_KA__P_SF_", new NameAllocator().newName("/KA@(P(SF)"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990litString4161() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990litString4152() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_add4168() throws Exception {
        String __DSPOT_suggestion_759 = "{a=yvGr@Sm?fld0|x(qm";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3990_add4168__4 = nameAllocator.newName(__DSPOT_suggestion_759);
        Assert.assertEquals("_a_yvGr_Sm_fld0_x_qm", o_characterMappingInvalidStartIsInvalidPart_sd3990_add4168__4);
        Assert.assertEquals("_a_yvGr_Sm_fld0_x_qm_", nameAllocator.newName(__DSPOT_suggestion_759));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990litString4157() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab"));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991litString4172() throws Exception {
        Assert.assertEquals("foo_", new NameAllocator().newName("foo_", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991litString4180() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4190_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_766 = new Object();
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "*=]Rs@TQeQ<=7G/rHE0Y";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName("*=]Rs@TQeQ<=7G/rHE0Y", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3991_sd4190 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_add4193_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "*=]Rs@TQeQ<=7G/rHE0Y";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3991_add4193 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4191litString5982() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName("*=]Rs@TQeQ<=7G/rHE0Y", new Object());
        Assert.assertEquals("___Rs_TQeQ__7G_rHE0Y", o_characterMappingInvalidStartIsInvalidPart_sd3991__6);
        Assert.assertEquals("", nameAllocator.newName(""));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3990_sd4167_sd5359() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3990__4 = nameAllocator.newName("{a=yvGr@Sm?fld0|x(qm");
        Assert.assertEquals("_a_yvGr_Sm_fld0_x_qm", o_characterMappingInvalidStartIsInvalidPart_sd3990__4);
        String o_characterMappingInvalidStartIsInvalidPart_sd3990_sd4167__11 = nameAllocator.newName("A`GH73;S{@T$husiKa(U", new Object());
        Assert.assertEquals("A_GH73_S__T$husiKa_U", o_characterMappingInvalidStartIsInvalidPart_sd3990_sd4167__11);
        Assert.assertEquals("B_VbF_itez__R_vs_zYU", nameAllocator.newName("B.VbF.itez#*R%vs/zYU", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4191_add6015_failAssert6() throws Exception {
        try {
            String __DSPOT_suggestion_767 = "(Cd|#LVcx9VgAzGTF;K^";
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "*=]Rs@TQeQ<=7G/rHE0Y";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName(__DSPOT_suggestion_760, __DSPOT_tag_761);
            String o_characterMappingInvalidStartIsInvalidPart_sd3991_sd4191__11 = nameAllocator.newName("(Cd|#LVcx9VgAzGTF;K^");
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3991_sd4191_add6015 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4190_failAssert1_sd6991() throws Exception {
        try {
            Object __DSPOT_tag_1243 = new Object();
            String __DSPOT_suggestion_1242 = "nf%+>F|HVSM[sCW8$DSC";
            Object __DSPOT_tag_766 = new Object();
            Object __DSPOT_tag_761 = new Object();
            String __DSPOT_suggestion_760 = "*=]Rs@TQeQ<=7G/rHE0Y";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName("*=]Rs@TQeQ<=7G/rHE0Y", new Object());
            Assert.assertEquals("___Rs_TQeQ__7G_rHE0Y", nameAllocator.newName("*=]Rs@TQeQ<=7G/rHE0Y", new Object()));
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingInvalidStartIsInvalidPart_sd3991_sd4190 should have thrown IllegalArgumentException");
            nameAllocator.newName("nf%+>F|HVSM[sCW8$DSC", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4192() throws Exception {
        Object __DSPOT_tag_761 = new Object();
        String __DSPOT_suggestion_760 = "*=]Rs@TQeQ<=7G/rHE0Y";
        String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = "g#%p_}5^f3RUL01$Q{+L";
        Assert.assertEquals("g#%p_}5^f3RUL01$Q{+L", o_characterMappingInvalidStartIsInvalidPart_sd3991__6);
        Assert.assertEquals("aA_n_M_WL_pi_TT_z0__", new NameAllocator().newName("aA[n|M]WL!pi%TT:z0[|", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4191_add6016() throws Exception {
        String __DSPOT_suggestion_767 = "(Cd|#LVcx9VgAzGTF;K^";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName("*=]Rs@TQeQ<=7G/rHE0Y", new Object());
        Assert.assertEquals("___Rs_TQeQ__7G_rHE0Y", o_characterMappingInvalidStartIsInvalidPart_sd3991__6);
        String o_characterMappingInvalidStartIsInvalidPart_sd3991_sd4191_add6016__11 = nameAllocator.newName(__DSPOT_suggestion_767);
        Assert.assertEquals("_Cd__LVcx9VgAzGTF_K_", o_characterMappingInvalidStartIsInvalidPart_sd3991_sd4191_add6016__11);
        Assert.assertEquals("_Cd__LVcx9VgAzGTF_K__", nameAllocator.newName(__DSPOT_suggestion_767));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4192litString6052() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName("public", new Object());
        Assert.assertEquals("public_", o_characterMappingInvalidStartIsInvalidPart_sd3991__6);
        Assert.assertEquals("aA_n_M_WL_pi_TT_z0__", nameAllocator.newName("aA[n|M]WL!pi%TT:z0[|", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4189_sd5967() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName("*=]Rs@TQeQ<=7G/rHE0Y", new Object());
        Assert.assertEquals("___Rs_TQeQ__7G_rHE0Y", o_characterMappingInvalidStartIsInvalidPart_sd3991__6);
        Assert.assertEquals("dOAP_e__sOtGl2bo___V", nameAllocator.clone().newName("dOAP^e>*sOtGl2bo%`@V", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingInvalidStartIsInvalidPart_sd3991_sd4192litString6046() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingInvalidStartIsInvalidPart_sd3991__6 = nameAllocator.newName("1ab", new Object());
        Assert.assertEquals("_1ab", o_characterMappingInvalidStartIsInvalidPart_sd3991__6);
        Assert.assertEquals("aA_n_M_WL_pi_TT_z0__", nameAllocator.newName("aA[n|M]WL!pi%TT:z0[|", new Object()));
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
        Assert.assertEquals("y1__cBv9F_d_C__mA___", new NameAllocator().newName("y1=+cBv9F#d.C@!mA *{", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932() throws Exception {
        Assert.assertEquals("_fuW2_Zz_PAfs_apO_L4", new NameAllocator().newName("]fuW2[Zz]PAfs[apO+L4"));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933litString8112() throws Exception {
        Assert.assertEquals("foo", new NameAllocator().newName("foo", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8132_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_1524 = new Object();
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "y1=+cBv9F#d.C@!mA *{";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName("y1=+cBv9F#d.C@!mA *{", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSubstitute_sd7933_sd8132 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932litString8098() throws Exception {
        Assert.assertEquals("_fuW2_Zz_PAfs_apO_WL4", new NameAllocator().newName("]fuW2[Zz]PAfs[apO+WL4"));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932_add8110() throws Exception {
        String __DSPOT_suggestion_1517 = "]fuW2[Zz]PAfs[apO+L4";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7932_add8110__4 = nameAllocator.newName(__DSPOT_suggestion_1517);
        Assert.assertEquals("_fuW2_Zz_PAfs_apO_L4", o_characterMappingSubstitute_sd7932_add8110__4);
        Assert.assertEquals("_fuW2_Zz_PAfs_apO_L4_", nameAllocator.newName(__DSPOT_suggestion_1517));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932litString8088() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public_"));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_add8135_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "y1=+cBv9F#d.C@!mA *{";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            org.junit.Assert.fail("characterMappingSubstitute_sd7933_add8135 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933litString8122() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933litString8130() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933litString8120() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8133_sd9954() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName("y1=+cBv9F#d.C@!mA *{", new Object());
        Assert.assertEquals("y1__cBv9F_d_C__mA___", o_characterMappingSubstitute_sd7933__6);
        String o_characterMappingSubstitute_sd7933_sd8133__11 = nameAllocator.newName("@{xXg(8=W7Ygs|d/=?k8");
        Assert.assertEquals("__xXg_8_W7Ygs_d___k8", o_characterMappingSubstitute_sd7933_sd8133__11);
        Assert.assertEquals("Vy__ycSQ75PG__H_f__d", nameAllocator.newName("Vy<=ycSQ75PG%<H(f{_d"));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8132_failAssert1_sd10973() throws Exception {
        try {
            int __DSPOT_arg1_2061 = 68923959;
            int __DSPOT_arg0_2060 = 1821317345;
            Object __DSPOT_tag_1524 = new Object();
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "y1=+cBv9F#d.C@!mA *{";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName("y1=+cBv9F#d.C@!mA *{", new Object());
            Assert.assertEquals("y1__cBv9F_d_C__mA___", nameAllocator.newName("y1=+cBv9F#d.C@!mA *{", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSubstitute_sd7933_sd8132 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).subSequence(1821317345, 68923959);
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8134litString9989() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName("5p!yG18MPH", new Object());
        Assert.assertEquals("_5p_yG18MPH", o_characterMappingSubstitute_sd7933__6);
        Assert.assertEquals("_f1NY_Z5_6h__V_5mdsi", nameAllocator.newName("+f1NY?Z5:6h@`V(5mdsi", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8131_sd9908() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName("y1=+cBv9F#d.C@!mA *{", new Object());
        Assert.assertEquals("y1__cBv9F_d_C__mA___", o_characterMappingSubstitute_sd7933__6);
        Assert.assertEquals("HT____Cr_u_DA_1$O1t_", nameAllocator.clone().newName("HT}[;&Cr@u%DA?1$O1t&", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8133_add9956_failAssert6() throws Exception {
        try {
            String __DSPOT_suggestion_1525 = "@{xXg(8=W7Ygs|d/=?k8";
            Object __DSPOT_tag_1519 = new Object();
            String __DSPOT_suggestion_1518 = "y1=+cBv9F#d.C@!mA *{";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName(__DSPOT_suggestion_1518, __DSPOT_tag_1519);
            String o_characterMappingSubstitute_sd7933_sd8133__11 = nameAllocator.newName("@{xXg(8=W7Ygs|d/=?k8");
            org.junit.Assert.fail("characterMappingSubstitute_sd7933_sd8133_add9956 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7932_sd8109litString9292() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7932__4 = nameAllocator.newName("public");
        Assert.assertEquals("public_", o_characterMappingSubstitute_sd7932__4);
        Assert.assertEquals("pd_pP__g_WJu4_q8_3_l", nameAllocator.newName("pd!pP}@g/WJu4%q8 3 l", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSubstitute_sd7933_sd8133_add9957() throws Exception {
        String __DSPOT_suggestion_1525 = "@{xXg(8=W7Ygs|d/=?k8";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSubstitute_sd7933__6 = nameAllocator.newName("y1=+cBv9F#d.C@!mA *{", new Object());
        Assert.assertEquals("y1__cBv9F_d_C__mA___", o_characterMappingSubstitute_sd7933__6);
        String o_characterMappingSubstitute_sd7933_sd8133_add9957__11 = nameAllocator.newName(__DSPOT_suggestion_1525);
        Assert.assertEquals("__xXg_8_W7Ygs_d___k8", o_characterMappingSubstitute_sd7933_sd8133_add9957__11);
        Assert.assertEquals("__xXg_8_W7Ygs_d___k8_", nameAllocator.newName(__DSPOT_suggestion_1525));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11874() throws Exception {
        Assert.assertEquals("__jAd__I_v___b8d_0i1", new NameAllocator().newName("/*jAd>@I*v{ (b8d<0i1"));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11875() throws Exception {
        Assert.assertEquals("sO9MsB1qsf0_zRf__UE_", new NameAllocator().newName("sO9MsB1qsf0%zRf!-UE#", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11873_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_2274 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("characterMappingSurrogate_sd11873 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11875litString12058() throws Exception {
        Assert.assertEquals("foo__", new NameAllocator().newName("foo__", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11875_sd12074_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_2282 = new Object();
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "sO9MsB1qsf0%zRf!-UE#";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_sd11875__6 = nameAllocator.newName("sO9MsB1qsf0%zRf!-UE#", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSurrogate_sd11875_sd12074 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11874litString12031() throws Exception {
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", new NameAllocator().newName("tag 1 cannot be used for both 'foo' and 'bar'"));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11875litString12067() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11875litString12061() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11874litString12044() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab"));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11874_add12052() throws Exception {
        String __DSPOT_suggestion_2275 = "/*jAd>@I*v{ (b8d<0i1";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11874_add12052__4 = nameAllocator.newName(__DSPOT_suggestion_2275);
        Assert.assertEquals("__jAd__I_v___b8d_0i1", o_characterMappingSurrogate_sd11874_add12052__4);
        Assert.assertEquals("__jAd__I_v___b8d_0i1_", nameAllocator.newName(__DSPOT_suggestion_2275));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11875_add12077_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "sO9MsB1qsf0%zRf!-UE#";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            String o_characterMappingSurrogate_sd11875__6 = nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            org.junit.Assert.fail("characterMappingSurrogate_sd11875_add12077 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11874litString12038() throws Exception {
        Assert.assertEquals("__jAd__I_v_8__b8d_0i1", new NameAllocator().newName("/*jAd>@I*v{8 (b8d<0i1"));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11874_sd12051litString13231() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11874__4 = nameAllocator.newName("&ab");
        Assert.assertEquals("_ab", o_characterMappingSurrogate_sd11874__4);
        Assert.assertEquals("_18_CMnAP__rhd7__Zhbf", nameAllocator.newName("18[CMnAP{@rhd7#<Zhbf", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11875_sd12074_failAssert1_sd14904() throws Exception {
        try {
            int __DSPOT_arg1_2793 = -2127151790;
            String __DSPOT_arg0_2792 = "qHy=i7jGmyq9/kzQWx;X";
            Object __DSPOT_tag_2282 = new Object();
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "sO9MsB1qsf0%zRf!-UE#";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_sd11875__6 = nameAllocator.newName("sO9MsB1qsf0%zRf!-UE#", new Object());
            Assert.assertEquals("sO9MsB1qsf0_zRf__UE_", nameAllocator.newName("sO9MsB1qsf0%zRf!-UE#", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("characterMappingSurrogate_sd11875_sd12074 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).lastIndexOf("qHy=i7jGmyq9/kzQWx;X", -2127151790);
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11875_sd12075_add13896() throws Exception {
        String __DSPOT_suggestion_2283 = "7Xig4;O7 ?M1>l%Ec/iT";
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11875__6 = nameAllocator.newName("sO9MsB1qsf0%zRf!-UE#", new Object());
        Assert.assertEquals("sO9MsB1qsf0_zRf__UE_", o_characterMappingSurrogate_sd11875__6);
        String o_characterMappingSurrogate_sd11875_sd12075_add13896__11 = nameAllocator.newName(__DSPOT_suggestion_2283);
        Assert.assertEquals("_7Xig4_O7__M1_l_Ec_iT", o_characterMappingSurrogate_sd11875_sd12075_add13896__11);
        Assert.assertEquals("_7Xig4_O7__M1_l_Ec_iT_", nameAllocator.newName(__DSPOT_suggestion_2283));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11875_sd12076litString13928() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11875__6 = nameAllocator.newName("public", new Object());
        Assert.assertEquals("public_", o_characterMappingSurrogate_sd11875__6);
        Assert.assertEquals("___v_Yud_f$ott_FH0_D", nameAllocator.newName(",!)v}Yud=f$ott FH0>D", new Object()));
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11875_add12077_failAssert2_sd14952() throws Exception {
        try {
            Object __DSPOT_tag_2828 = new Object();
            String __DSPOT_suggestion_2827 = "aqOG6xU]]yR5)TSN_/1/";
            Object __DSPOT_tag_2277 = new Object();
            String __DSPOT_suggestion_2276 = "sO9MsB1qsf0%zRf!-UE#";
            NameAllocator nameAllocator = new NameAllocator();
            String o_characterMappingSurrogate_sd11875_add12077_failAssert2_sd14952__11 = nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            Assert.assertEquals("sO9MsB1qsf0_zRf__UE_", nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277));
            String o_characterMappingSurrogate_sd11875__6 = nameAllocator.newName(__DSPOT_suggestion_2276, __DSPOT_tag_2277);
            org.junit.Assert.fail("characterMappingSurrogate_sd11875_add12077 should have thrown IllegalArgumentException");
            nameAllocator.newName("aqOG6xU]]yR5)TSN_/1/", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void characterMappingSurrogate_sd11875_sd12073_sd13847() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_characterMappingSurrogate_sd11875__6 = nameAllocator.newName("sO9MsB1qsf0%zRf!-UE#", new Object());
        Assert.assertEquals("sO9MsB1qsf0_zRf__UE_", o_characterMappingSurrogate_sd11875__6);
        Assert.assertEquals("Pk_gnNp2s_FBZzqDc___", nameAllocator.clone().newName("Pk+gnNp2s(FBZzqDc&?!", new Object()));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15986() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15986__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15986__6);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("etX_h__aZ___W_05j__R", outterAllocator.clone().newName("etX{h;/aZ]><W_05j]]R", new Object()));
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString15791() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString15791__3 = outterAllocator.newName("j9[g8!*c?Z", 1);
        Assert.assertEquals("j9_g8__c_Z", o_cloneUsagelitString15791__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
    }

    @Test(timeout = 10000)
    public void cloneUsage_add16049() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_add16049__3 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_add16049__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        Assert.assertEquals("foo_", outterAllocator.clone().newName("foo", 2));
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString15784() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString15784__3 = outterAllocator.newName("1ab", 1);
        Assert.assertEquals("_1ab", o_cloneUsagelitString15784__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString15795() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString15795__3 = outterAllocator.newName("&ab", 1);
        Assert.assertEquals("_ab", o_cloneUsagelitString15795__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16030() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        boolean o_cloneUsage_sd16030__12 = outterAllocator.newName("foo", 1).startsWith(",1ZiO&8F)cKP^+CnP!jd", -177063689);
        Assert.assertFalse(o_cloneUsage_sd16030__12);
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15984_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_3040 = new Object();
            NameAllocator outterAllocator = new NameAllocator();
            outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            outterAllocator.clone().get(new Object());
            org.junit.Assert.fail("cloneUsage_sd15984 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString15801() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString15801__3 = outterAllocator.newName("public", 1);
        Assert.assertEquals("public_", o_cloneUsagelitString15801__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
    }

    @Test(timeout = 10000)
    public void cloneUsagelitString15789() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsagelitString15789__3 = outterAllocator.newName("a-b", 1);
        Assert.assertEquals("a_b", o_cloneUsagelitString15789__3);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15985() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15985__4 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15985__4);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("_1LxK_0_h__p_y3zliv2", outterAllocator.clone().newName(",1LxK>0<h]+p+y3zliv2"));
    }

    @Test(timeout = 10000)
    public void cloneUsage_add16041_failAssert17() throws Exception {
        try {
            NameAllocator outterAllocator = new NameAllocator();
            outterAllocator.newName("foo", 1);
            outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            org.junit.Assert.fail("cloneUsage_add16041 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15983() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15983__3 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15983__3);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        outterAllocator.clone().clone();
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15986_sd20315() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15986__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15986__6);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_sd15986__11 = outterAllocator.clone().newName("etX{h;/aZ]><W_05j]]R", new Object());
        Assert.assertEquals("etX_h__aZ___W_05j__R", o_cloneUsage_sd15986__11);
        Assert.assertEquals("_62pPiA__3_dDjiI0_QS_", outterAllocator.newName("62pPiA`(3%dDjiI0<QS!"));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15986_sd20318_failAssert7() throws Exception {
        try {
            Object __DSPOT_tag_4277 = new Object();
            Object __DSPOT_tag_3043 = new Object();
            String __DSPOT_suggestion_3042 = "etX{h;/aZ]><W_05j]]R";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_sd15986__6 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_sd15986__11 = innerAllocator1.newName("etX{h;/aZ]><W_05j]]R", new Object());
            innerAllocator1.get(new Object());
            org.junit.Assert.fail("cloneUsage_sd15986_sd20318 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15986_sd20316() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15986__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15986__6);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_sd15986__11 = outterAllocator.clone().newName("etX{h;/aZ]><W_05j]]R", new Object());
        Assert.assertEquals("etX_h__aZ___W_05j__R", o_cloneUsage_sd15986__11);
        Assert.assertEquals("_p___n$_cz_0_awm_VZ_", outterAllocator.newName("<p {&n$#cz#0#awm[VZ;", new Object()));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15986_add20328_failAssert15() throws Exception {
        try {
            Object __DSPOT_tag_3043 = new Object();
            String __DSPOT_suggestion_3042 = "etX{h;/aZ]><W_05j]]R";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_sd15986__6 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            innerAllocator1.newName(__DSPOT_suggestion_3042, __DSPOT_tag_3043);
            String o_cloneUsage_sd15986__11 = innerAllocator1.newName(__DSPOT_suggestion_3042, __DSPOT_tag_3043);
            org.junit.Assert.fail("cloneUsage_sd15986_add20328 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16022_sd22257_failAssert2() throws Exception {
        try {
            String __DSPOT_arg1_5408 = "wg90aS=2dA%eVxX(6W:A";
            String __DSPOT_arg0_5407 = "b[LklXLJ@kQZ:!Fggrmn";
            int __DSPOT_arg4_3089 = -2073407488;
            int __DSPOT_arg3_3088 = 1553002281;
            String __DSPOT_arg2_3087 = "(>((sJo>eQ]k526@F8h%";
            int __DSPOT_arg1_3086 = 1629463781;
            boolean __DSPOT_arg0_3085 = false;
            NameAllocator outterAllocator = new NameAllocator();
            String __DSPOT_invoc_3 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            boolean o_cloneUsage_sd16022__15 = outterAllocator.newName("foo", 1).regionMatches(false, 1629463781, __DSPOT_arg2_3087, 1553002281, -2073407488);
            __DSPOT_arg2_3087.replaceFirst("b[LklXLJ@kQZ:!Fggrmn", "wg90aS=2dA%eVxX(6W:A");
            org.junit.Assert.fail("cloneUsage_sd16022_sd22257 should have thrown PatternSyntaxException");
        } catch (PatternSyntaxException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16022_sd22238_failAssert0() throws Exception {
        try {
            int __DSPOT_arg3_5378 = -1202224806;
            char[] __DSPOT_arg2_5377 = new char[]{ '`', '.' };
            int __DSPOT_arg1_5376 = -423165553;
            int __DSPOT_arg0_5375 = 1496541492;
            int __DSPOT_arg4_3089 = -2073407488;
            int __DSPOT_arg3_3088 = 1553002281;
            String __DSPOT_arg2_3087 = "(>((sJo>eQ]k526@F8h%";
            int __DSPOT_arg1_3086 = 1629463781;
            boolean __DSPOT_arg0_3085 = false;
            NameAllocator outterAllocator = new NameAllocator();
            String __DSPOT_invoc_3 = outterAllocator.newName("foo", 1);
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            boolean o_cloneUsage_sd16022__15 = outterAllocator.newName("foo", 1).regionMatches(false, 1629463781, __DSPOT_arg2_3087, 1553002281, -2073407488);
            __DSPOT_arg2_3087.getChars(1496541492, -423165553, new char[]{ '`', '.' }, -1202224806);
            org.junit.Assert.fail("cloneUsage_sd16022_sd22238 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16022_sd22279() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        boolean o_cloneUsage_sd16022__15 = outterAllocator.newName("foo", 1).regionMatches(false, 1629463781, "(>((sJo>eQ]k526@F8h%", 1553002281, -2073407488);
        Assert.assertEquals("DBI$h_f__6W__uCA_$7_", outterAllocator.clone().newName("DBI$h>f![6W]^uCA:$7%", new Object()));
    }

    @Test(timeout = 10000)
    public void cloneUsage_add16046_sd23819() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_add16046__3 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_add16046__3);
        String o_cloneUsage_add16046__6 = outterAllocator.clone().newName("foo", 3);
        Assert.assertEquals("foo_", o_cloneUsage_add16046__6);
        NameAllocator innerAllocator2 = outterAllocator.clone();
        Assert.assertEquals("tQ_p8_k0_dfG_sbmQ_GM", outterAllocator.newName("tQ&p8{k0`dfG=sbmQ{GM", new Object()));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd16022_sd22275() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        boolean o_cloneUsage_sd16022__15 = outterAllocator.newName("foo", 1).regionMatches(false, 1629463781, "(>((sJo>eQ]k526@F8h%", 1553002281, -2073407488);
        Assert.assertEquals("cidUqJ6bS_gJ$__Lq_9_", outterAllocator.newName("cidUqJ6bS]gJ$!(Lq#9=", new Object()));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15986_sd20320_sd27073() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15986__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15986__6);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_sd15986__11 = innerAllocator1.newName("etX{h;/aZ]><W_05j]]R", new Object());
        Assert.assertEquals("etX_h__aZ___W_05j__R", o_cloneUsage_sd15986__11);
        String o_cloneUsage_sd15986_sd20320__20 = innerAllocator1.newName("]dR!j#-9%|Rl50X;{_lE", new Object());
        Assert.assertEquals("_dR_j__9__Rl50X___lE", o_cloneUsage_sd15986_sd20320__20);
        Assert.assertEquals("_0_Z__Zg_x8sI__kcd7kw", innerAllocator1.newName("0}Z<@Zg+x8sI()kcd7kw"));
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15986_add20328_failAssert15litString38967() throws Exception {
        try {
            Object __DSPOT_tag_3043 = new Object();
            String __DSPOT_suggestion_3042 = "a_b";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_sd15986__6 = outterAllocator.newName("foo", 1);
            Assert.assertEquals("foo", outterAllocator.newName("foo", 1));
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_sd15986_add20328_failAssert15litString38967__15 = innerAllocator1.newName(__DSPOT_suggestion_3042, __DSPOT_tag_3043);
            Assert.assertEquals("a_b", innerAllocator1.newName(__DSPOT_suggestion_3042, __DSPOT_tag_3043));
            String o_cloneUsage_sd15986__11 = innerAllocator1.newName(__DSPOT_suggestion_3042, __DSPOT_tag_3043);
            org.junit.Assert.fail("cloneUsage_sd15986_add20328 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15986_sd20322_failAssert8() throws Exception {
        try {
            Object __DSPOT_tag_4281 = new Object();
            Object __DSPOT_tag_3043 = new Object();
            String __DSPOT_suggestion_3042 = "etX{h;/aZ]><W_05j]]R";
            NameAllocator outterAllocator = new NameAllocator();
            String o_cloneUsage_sd15986__6 = outterAllocator.newName("foo", 1);
            Assert.assertEquals("foo", outterAllocator.newName("foo", 1));
            NameAllocator innerAllocator1 = outterAllocator.clone();
            NameAllocator innerAllocator2 = outterAllocator.clone();
            String o_cloneUsage_sd15986__11 = "Xa1]-r)Qm1@e`yDYrE8Q";
            Assert.assertEquals("Xa1]-r)Qm1@e`yDYrE8Q", "Xa1]-r)Qm1@e`yDYrE8Q");
            outterAllocator.clone().get(new Object());
            org.junit.Assert.fail("cloneUsage_sd15986_sd20322 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void cloneUsage_sd15986_sd20320_sd27070() throws Exception {
        NameAllocator outterAllocator = new NameAllocator();
        String o_cloneUsage_sd15986__6 = outterAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_cloneUsage_sd15986__6);
        NameAllocator innerAllocator1 = outterAllocator.clone();
        NameAllocator innerAllocator2 = outterAllocator.clone();
        String o_cloneUsage_sd15986__11 = innerAllocator1.newName("etX{h;/aZ]><W_05j]]R", new Object());
        Assert.assertEquals("etX_h__aZ___W_05j__R", o_cloneUsage_sd15986__11);
        String o_cloneUsage_sd15986_sd20320__20 = innerAllocator1.newName("]dR!j#-9%|Rl50X;{_lE", new Object());
        Assert.assertEquals("_dR_j__9__Rl50X___lE", o_cloneUsage_sd15986_sd20320__20);
        Assert.assertEquals("c__bTwoL__ISa_M___0$", outterAllocator.newName("c] bTwoL#=ISa!M(|]0$", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_add40944() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", 1));
    }

    @Test(timeout = 10000)
    public void javaKeyword_add40946_failAssert1() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(1);
            org.junit.Assert.fail("javaKeyword_add40946 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40941() throws Exception {
        Assert.assertEquals("__E8l_NpngrnMS8BMBY_", new NameAllocator().newName("`}E8l[NpngrnMS8BMBY?"));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40942() throws Exception {
        Assert.assertEquals("Lx____4A6kQ_nZ_m_2lo", new NameAllocator().newName("Lx/)+#4A6kQ,nZ%m.2lo", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40941litString41130() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab"));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40942_add41164_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_16030 = new Object();
            String __DSPOT_suggestion_16029 = "Lx/)+#4A6kQ,nZ%m.2lo";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_16029, __DSPOT_tag_16030);
            String o_javaKeyword_sd40942__6 = nameAllocator.newName(__DSPOT_suggestion_16029, __DSPOT_tag_16030);
            org.junit.Assert.fail("javaKeyword_sd40942_add41164 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40941_add41139() throws Exception {
        String __DSPOT_suggestion_16028 = "`}E8l[NpngrnMS8BMBY?";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40941_add41139__4 = nameAllocator.newName(__DSPOT_suggestion_16028);
        Assert.assertEquals("__E8l_NpngrnMS8BMBY_", o_javaKeyword_sd40941_add41139__4);
        Assert.assertEquals("__E8l_NpngrnMS8BMBY__", nameAllocator.newName(__DSPOT_suggestion_16028));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40942litString41142() throws Exception {
        Assert.assertEquals("bar", new NameAllocator().newName("bar", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_add40944litString41182() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", 1));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40941litString41119() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40942_sd41161_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_16035 = new Object();
            Object __DSPOT_tag_16030 = new Object();
            String __DSPOT_suggestion_16029 = "Lx/)+#4A6kQ,nZ%m.2lo";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_sd40942__6 = nameAllocator.newName("Lx/)+#4A6kQ,nZ%m.2lo", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("javaKeyword_sd40942_sd41161 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40941litString41128() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40942_sd41161_failAssert1_sd44236() throws Exception {
        try {
            int __DSPOT_arg1_16639 = -1614334300;
            String __DSPOT_arg0_16638 = "!W3:VMmI-G&Sm$YXkCp]";
            Object __DSPOT_tag_16035 = new Object();
            Object __DSPOT_tag_16030 = new Object();
            String __DSPOT_suggestion_16029 = "Lx/)+#4A6kQ,nZ%m.2lo";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_sd40942__6 = nameAllocator.newName("Lx/)+#4A6kQ,nZ%m.2lo", new Object());
            Assert.assertEquals("Lx____4A6kQ_nZ_m_2lo", nameAllocator.newName("Lx/)+#4A6kQ,nZ%m.2lo", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("javaKeyword_sd40942_sd41161 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).startsWith("!W3:VMmI-G&Sm$YXkCp]", -1614334300);
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40942_sd41162() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40942__6 = nameAllocator.newName("Lx/)+#4A6kQ,nZ%m.2lo", new Object());
        Assert.assertEquals("Lx____4A6kQ_nZ_m_2lo", o_javaKeyword_sd40942__6);
        Assert.assertEquals("_oDy__xZ__aD8_s_2_Dv", nameAllocator.newName("!oDy^`xZ,{aD8-s:2{Dv"));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40942_sd41162litString43202() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40942__6 = nameAllocator.newName("public", new Object());
        Assert.assertEquals("public_", o_javaKeyword_sd40942__6);
        Assert.assertEquals("_oDy__xZ__aD8_s_2_Dv", nameAllocator.newName("!oDy^`xZ,{aD8-s:2{Dv"));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40942_sd41162_add43216() throws Exception {
        String __DSPOT_suggestion_16036 = "!oDy^`xZ,{aD8-s:2{Dv";
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40942__6 = nameAllocator.newName("Lx/)+#4A6kQ,nZ%m.2lo", new Object());
        Assert.assertEquals("Lx____4A6kQ_nZ_m_2lo", o_javaKeyword_sd40942__6);
        String o_javaKeyword_sd40942_sd41162_add43216__11 = nameAllocator.newName(__DSPOT_suggestion_16036);
        Assert.assertEquals("_oDy__xZ__aD8_s_2_Dv", o_javaKeyword_sd40942_sd41162_add43216__11);
        Assert.assertEquals("_oDy__xZ__aD8_s_2_Dv_", nameAllocator.newName(__DSPOT_suggestion_16036));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40942_sd41163litString43239() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40942__6 = nameAllocator.newName("foo_", new Object());
        Assert.assertEquals("foo_", o_javaKeyword_sd40942__6);
        Assert.assertEquals("___6__l0Sso__fuWxFs_", nameAllocator.newName("+){6#,l0Sso!?fuWxFs ", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40942_sd41162litString43200() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40942__6 = nameAllocator.newName("1ab", new Object());
        Assert.assertEquals("_1ab", o_javaKeyword_sd40942__6);
        Assert.assertEquals("_oDy__xZ__aD8_s_2_Dv", nameAllocator.newName("!oDy^`xZ,{aD8-s:2{Dv"));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40942_sd41163_add43262_failAssert1() throws Exception {
        try {
            Object __DSPOT_tag_16038 = new Object();
            String __DSPOT_suggestion_16037 = "+){6#,l0Sso!?fuWxFs ";
            Object __DSPOT_tag_16030 = new Object();
            String __DSPOT_suggestion_16029 = "Lx/)+#4A6kQ,nZ%m.2lo";
            NameAllocator nameAllocator = new NameAllocator();
            String o_javaKeyword_sd40942__6 = nameAllocator.newName("Lx/)+#4A6kQ,nZ%m.2lo", new Object());
            nameAllocator.newName(__DSPOT_suggestion_16037, __DSPOT_tag_16038);
            String o_javaKeyword_sd40942_sd41163__13 = nameAllocator.newName(__DSPOT_suggestion_16037, __DSPOT_tag_16038);
            org.junit.Assert.fail("javaKeyword_sd40942_sd41163_add43262 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40942_sd41160_sd43167() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40942__6 = nameAllocator.newName("Lx/)+#4A6kQ,nZ%m.2lo", new Object());
        Assert.assertEquals("Lx____4A6kQ_nZ_m_2lo", o_javaKeyword_sd40942__6);
        Assert.assertEquals("_5CgoK_8n$q_p_6_kev_r", nameAllocator.clone().newName("5CgoK{8n$q p*6%kev}r", new Object()));
    }

    @Test(timeout = 10000)
    public void javaKeyword_sd40942_sd41162litString43172() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_javaKeyword_sd40942__6 = nameAllocator.newName("Lx/)+#4A6kQ,nZ%m.2lo", new Object());
        Assert.assertEquals("Lx____4A6kQ_nZ_m_2lo", o_javaKeyword_sd40942__6);
        Assert.assertEquals("foo", nameAllocator.newName("foo"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45206() throws Exception {
        Assert.assertEquals("H3bz__Xe3_NJcE6hb_J_", new NameAllocator().newName("H3bz*>Xe3/NJcE6hb*J/"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45205_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_16854 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(new Object());
            org.junit.Assert.fail("nameCollision_sd45205 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45206_sd45474_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_16858 = new Object();
            String __DSPOT_suggestion_16855 = "H3bz*>Xe3/NJcE6hb*J/";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_sd45206__4 = nameAllocator.newName("H3bz*>Xe3/NJcE6hb*J/");
            nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollision_sd45206_sd45474 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_add45211litString45538() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab"));
    }

    @Test(timeout = 10000)
    public void nameCollision_add45213litString45561() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45207_sd45501() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45207__6 = nameAllocator.newName("v[70:34@]ubAEB<<Qm7F", new Object());
        Assert.assertEquals("v_70_34__ubAEB__Qm7F", o_nameCollision_sd45207__6);
        Assert.assertEquals("aisw_t_W_VmWU_hfgX_A", nameAllocator.newName("aisw!t)W#VmWU#hfgX#A", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45207_add45502_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_16857 = new Object();
            String __DSPOT_suggestion_16856 = "v[70:34@]ubAEB<<Qm7F";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_16856, __DSPOT_tag_16857);
            String o_nameCollision_sd45207__6 = nameAllocator.newName(__DSPOT_suggestion_16856, __DSPOT_tag_16857);
            org.junit.Assert.fail("nameCollision_sd45207_add45502 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45207litString45491() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45206_add45477() throws Exception {
        String __DSPOT_suggestion_16855 = "H3bz*>Xe3/NJcE6hb*J/";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45206_add45477__4 = nameAllocator.newName(__DSPOT_suggestion_16855);
        Assert.assertEquals("H3bz__Xe3_NJcE6hb_J_", o_nameCollision_sd45206_add45477__4);
        Assert.assertEquals("H3bz__Xe3_NJcE6hb_J__", nameAllocator.newName(__DSPOT_suggestion_16855));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45207litString45481() throws Exception {
        Assert.assertEquals("foo_", new NameAllocator().newName("foo_", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_add45209litString45521() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45206_sd45476litString46838() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45206__4 = nameAllocator.newName("a\ud83c\udf7ab");
        Assert.assertEquals("a_b", o_nameCollision_sd45206__4);
        Assert.assertEquals("tQsc2e___l_6___fB___", nameAllocator.newName("tQsc2e%|[l.6^&!fB-!_", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45207_sd45499_failAssert1_sd49677() throws Exception {
        try {
            String __DSPOT_arg0_17556 = "3p`zHFo>?Rg&O3No)jn]";
            Object __DSPOT_tag_16862 = new Object();
            Object __DSPOT_tag_16857 = new Object();
            String __DSPOT_suggestion_16856 = "v[70:34@]ubAEB<<Qm7F";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_sd45207__6 = nameAllocator.newName("v[70:34@]ubAEB<<Qm7F", new Object());
            Assert.assertEquals("v_70_34__ubAEB__Qm7F", nameAllocator.newName("v[70:34@]ubAEB<<Qm7F", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollision_sd45207_sd45499 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).equalsIgnoreCase("3p`zHFo>?Rg&O3No)jn]");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45207_sd45498_sd47462() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45207__6 = nameAllocator.newName("v[70:34@]ubAEB<<Qm7F", new Object());
        Assert.assertEquals("v_70_34__ubAEB__Qm7F", o_nameCollision_sd45207__6);
        Assert.assertEquals("_Ep2_9U__MuC__Z0g5_Y", nameAllocator.clone().newName(",Ep2<9U|[MuC+&Z0g5`Y", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45206_sd45476litString46839() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45206__4 = nameAllocator.newName("1ab");
        Assert.assertEquals("_1ab", o_nameCollision_sd45206__4);
        Assert.assertEquals("tQsc2e___l_6___fB___", nameAllocator.newName("tQsc2e%|[l.6^&!fB-!_", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45206_sd45476litString46844() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45206__4 = nameAllocator.newName("&ab");
        Assert.assertEquals("_ab", o_nameCollision_sd45206__4);
        Assert.assertEquals("tQsc2e___l_6___fB___", nameAllocator.newName("tQsc2e%|[l.6^&!fB-!_", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45206_sd45476litString46842() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45206__4 = nameAllocator.newName("public");
        Assert.assertEquals("public_", o_nameCollision_sd45206__4);
        Assert.assertEquals("tQsc2e___l_6___fB___", nameAllocator.newName("tQsc2e%|[l.6^&!fB-!_", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45207_sd45500_add47511() throws Exception {
        String __DSPOT_suggestion_16863 = "T5].[r7D|Suw@,?e4x.V";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollision_sd45207__6 = nameAllocator.newName("v[70:34@]ubAEB<<Qm7F", new Object());
        Assert.assertEquals("v_70_34__ubAEB__Qm7F", o_nameCollision_sd45207__6);
        String o_nameCollision_sd45207_sd45500_add47511__11 = nameAllocator.newName(__DSPOT_suggestion_16863);
        Assert.assertEquals("T5___r7D_Suw___e4x_V", o_nameCollision_sd45207_sd45500_add47511__11);
        Assert.assertEquals("T5___r7D_Suw___e4x_V_", nameAllocator.newName(__DSPOT_suggestion_16863));
    }

    @Test(timeout = 10000)
    public void nameCollision_sd45207_add45502_failAssert2_sd49740() throws Exception {
        try {
            Object __DSPOT_tag_17612 = new Object();
            String __DSPOT_suggestion_17611 = "r{p]W*8l{O,o.p3,Z1J/";
            Object __DSPOT_tag_16857 = new Object();
            String __DSPOT_suggestion_16856 = "v[70:34@]ubAEB<<Qm7F";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollision_sd45207_add45502_failAssert2_sd49740__11 = nameAllocator.newName(__DSPOT_suggestion_16856, __DSPOT_tag_16857);
            Assert.assertEquals("v_70_34__ubAEB__Qm7F", nameAllocator.newName(__DSPOT_suggestion_16856, __DSPOT_tag_16857));
            String o_nameCollision_sd45207__6 = nameAllocator.newName(__DSPOT_suggestion_16856, __DSPOT_tag_16857);
            org.junit.Assert.fail("nameCollision_sd45207_add45502 should have thrown IllegalArgumentException");
            nameAllocator.newName("r{p]W*8l{O,o.p3,Z1J/", new Object());
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50817_failAssert1() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(1);
            org.junit.Assert.fail("nameCollisionWithTag_add50817 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50809() throws Exception {
        Assert.assertEquals("uXXJY__in_I47fYeJ_$5", new NameAllocator().newName("uXXJY%&in:I47fYeJ>$5", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50815() throws Exception {
        Assert.assertEquals("foo", new NameAllocator().newName("foo", 3));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50808() throws Exception {
        Assert.assertEquals("TDWnj_Cx_S5_mpPHyv_B", new NameAllocator().newName("TDWnj#Cx^S5-mpPHyv`B"));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50813litString51254() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public", 2));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50808_sd51183() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50808__4 = nameAllocator.newName("TDWnj#Cx^S5-mpPHyv`B");
        Assert.assertEquals("TDWnj_Cx_S5_mpPHyv_B", o_nameCollisionWithTag_sd50808__4);
        Assert.assertEquals("o_gqUp__4D_v_Nw__u_h", nameAllocator.newName("o-gqUp,-4D v Nw*%u{h", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50815litNum51287() throws Exception {
        Assert.assertEquals("foo", new NameAllocator().newName("foo", 2));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50813litString51251() throws Exception {
        Assert.assertEquals("_oo", new NameAllocator().newName(">oo", 2));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50813litString51238() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", 2));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50809_sd51206_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_17893 = new Object();
            Object __DSPOT_tag_17888 = new Object();
            String __DSPOT_suggestion_17887 = "uXXJY%&in:I47fYeJ>$5";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_sd50809__6 = nameAllocator.newName("uXXJY%&in:I47fYeJ>$5", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollisionWithTag_sd50809_sd51206 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50815_add51295_failAssert3() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 3);
            String o_nameCollisionWithTag_add50815__3 = nameAllocator.newName("foo", 3);
            org.junit.Assert.fail("nameCollisionWithTag_add50815_add51295 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50813litString51247() throws Exception {
        Assert.assertEquals("a_b", new NameAllocator().newName("a\ud83c\udf7ab", 2));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_add50815litString51272() throws Exception {
        Assert.assertEquals("_9oo", new NameAllocator().newName("9oo", 3));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50809_sd51207_add53672_failAssert6() throws Exception {
        try {
            String __DSPOT_suggestion_17894 = "n6Qj#yvy}PILFHH%_||d";
            Object __DSPOT_tag_17888 = new Object();
            String __DSPOT_suggestion_17887 = "uXXJY%&in:I47fYeJ>$5";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_17887, __DSPOT_tag_17888);
            String o_nameCollisionWithTag_sd50809__6 = nameAllocator.newName(__DSPOT_suggestion_17887, __DSPOT_tag_17888);
            String o_nameCollisionWithTag_sd50809_sd51207__11 = nameAllocator.newName("n6Qj#yvy}PILFHH%_||d");
            org.junit.Assert.fail("nameCollisionWithTag_sd50809_sd51207_add53672 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50809_sd51207_add53673() throws Exception {
        String __DSPOT_suggestion_17894 = "n6Qj#yvy}PILFHH%_||d";
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50809__6 = nameAllocator.newName("uXXJY%&in:I47fYeJ>$5", new Object());
        Assert.assertEquals("uXXJY__in_I47fYeJ_$5", o_nameCollisionWithTag_sd50809__6);
        String o_nameCollisionWithTag_sd50809_sd51207_add53673__11 = nameAllocator.newName(__DSPOT_suggestion_17894);
        Assert.assertEquals("n6Qj_yvy_PILFHH____d", o_nameCollisionWithTag_sd50809_sd51207_add53673__11);
        Assert.assertEquals("n6Qj_yvy_PILFHH____d_", nameAllocator.newName(__DSPOT_suggestion_17894));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50809_sd51206_failAssert0_sd54686() throws Exception {
        try {
            int __DSPOT_arg1_18565 = 2135160085;
            int __DSPOT_arg0_18564 = -2086255474;
            Object __DSPOT_tag_17893 = new Object();
            Object __DSPOT_tag_17888 = new Object();
            String __DSPOT_suggestion_17887 = "uXXJY%&in:I47fYeJ>$5";
            NameAllocator nameAllocator = new NameAllocator();
            String o_nameCollisionWithTag_sd50809__6 = nameAllocator.newName("uXXJY%&in:I47fYeJ>$5", new Object());
            Assert.assertEquals("uXXJY__in_I47fYeJ_$5", nameAllocator.newName("uXXJY%&in:I47fYeJ>$5", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("nameCollisionWithTag_sd50809_sd51206 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).indexOf(-2086255474, 2135160085);
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50809_sd51208litString52148() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50809__6 = nameAllocator.newName("1ab", new Object());
        Assert.assertEquals("_1ab", o_nameCollisionWithTag_sd50809__6);
        Assert.assertEquals("_vT___kifgWn_vN__anU", nameAllocator.newName(":vT&@}kifgWn?vN.{anU", new Object()));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50809_sd51207litString53646() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50809__6 = nameAllocator.newName("uXXJY%&in:I47fYeJ>$5", new Object());
        Assert.assertEquals("uXXJY__in_I47fYeJ_$5", o_nameCollisionWithTag_sd50809__6);
        Assert.assertEquals("public_", nameAllocator.newName("public"));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50809_sd51207() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50809__6 = nameAllocator.newName("uXXJY%&in:I47fYeJ>$5", new Object());
        Assert.assertEquals("uXXJY__in_I47fYeJ_$5", o_nameCollisionWithTag_sd50809__6);
        Assert.assertEquals("n6Qj_yvy_PILFHH____d", nameAllocator.newName("n6Qj#yvy}PILFHH%_||d"));
    }

    @Test(timeout = 10000)
    public void nameCollisionWithTag_sd50809_sd51207litString53643() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_nameCollisionWithTag_sd50809__6 = nameAllocator.newName("uXXJY%&in:I47fYeJ>$5", new Object());
        Assert.assertEquals("uXXJY__in_I47fYeJ_$5", o_nameCollisionWithTag_sd50809__6);
        Assert.assertEquals("_ab", nameAllocator.newName("&ab"));
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55696() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55696__3 = nameAllocator.newName("bar", 1);
        Assert.assertEquals("bar", o_tagReuseForbiddenlitString55696__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55709() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55709__3 = nameAllocator.newName("&ab", 1);
        Assert.assertEquals("_ab", o_tagReuseForbiddenlitString55709__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55829() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55829__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55829__3);
        try {
            String __DSPOT_arg0_18968 = "p;8WCM2tJk,-)mxfj$}R";
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).getBytes("p;8WCM2tJk,-)mxfj$}R");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55708() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55708__3 = nameAllocator.newName("_1ab", 1);
        Assert.assertEquals("_1ab", o_tagReuseForbiddenlitString55708__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55761_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_18882 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.get(new Object());
            org.junit.Assert.fail("tagReuseForbidden_sd55761 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55705() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55705__3 = nameAllocator.newName("1ab", 1);
        Assert.assertEquals("_1ab", o_tagReuseForbiddenlitString55705__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55762() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55762__4 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55762__4);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("___3JA8_5_UBz_x_IeJ4", nameAllocator.newName(" #:3JA8`5]UBz(x`IeJ4"));
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55713() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55713__3 = nameAllocator.newName("public", 1);
        Assert.assertEquals("public_", o_tagReuseForbiddenlitString55713__3);
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbiddenlitString55711_sd58160() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbiddenlitString55711__3 = nameAllocator.newName("tag 1 cannot be used for both 'foo' and 'bar'", 1);
        Assert.assertEquals("tag_1_cannot_be_used_for_both__foo__and__bar_", o_tagReuseForbiddenlitString55711__3);
        try {
            int __DSPOT_arg3_20238 = 1347208564;
            int __DSPOT_arg2_20237 = -565487053;
            String __DSPOT_arg1_20236 = "i:i^*Z4Sk`^[*c5b_7C;";
            int __DSPOT_arg0_20235 = -1072087967;
            String __DSPOT_invoc_10 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(-1072087967, "i:i^*Z4Sk`^[*c5b_7C;", -565487053, 1347208564);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55841_sd70007() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55841__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55841__3);
        try {
            int __DSPOT_arg4_27873 = -2090795384;
            int __DSPOT_arg3_27872 = 268454632;
            String __DSPOT_arg2_27871 = "rr5`(aGtP9@0W2;-mn7t";
            int __DSPOT_arg1_27870 = -863261891;
            boolean __DSPOT_arg0_27869 = true;
            int __DSPOT_arg1_18984 = 149131901;
            String __DSPOT_arg0_18983 = "u6*8*FL$Q$>Fw%@Wn/80";
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).lastIndexOf(__DSPOT_arg0_18983, 149131901);
            __DSPOT_arg0_18983.regionMatches(true, -863261891, "rr5`(aGtP9@0W2;-mn7t", 268454632, -2090795384);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55845_sd70351() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55845__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55845__3);
        try {
            int __DSPOT_arg4_18992 = 1511445812;
            int __DSPOT_arg3_18991 = 312881541;
            String __DSPOT_arg2_18990 = "rH,?!*5_,wR`tyHWKz6x";
            int __DSPOT_arg1_18989 = -985176544;
            boolean __DSPOT_arg0_18988 = true;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(true, -985176544, "rH,?!*5_,wR`tyHWKz6x", 312881541, 1511445812);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("ww___oDpf_F_wO7_UC_G", nameAllocator.newName("ww)^<oDpf#F=wO7{UC:G"));
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55828_sd68881() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55828__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55828__3);
        try {
            int __DSPOT_arg3_18967 = 1531451557;
            byte[] __DSPOT_arg2_18966 = new byte[]{ 59, 33 };
            int __DSPOT_arg1_18965 = -1728407724;
            int __DSPOT_arg0_18964 = 285302294;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).getBytes(285302294, -1728407724, new byte[]{ 59, 33 }, 1531451557);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_QZ9Cl8xg____SL_i__U", nameAllocator.newName("|QZ9Cl8xg=#&|SL,i)/U", new Object()));
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55830_sd69076() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55830__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55830__3);
        try {
            int __DSPOT_arg3_18972 = 2028146662;
            char[] __DSPOT_arg2_18971 = new char[]{ 'H' };
            int __DSPOT_arg1_18970 = -452816317;
            int __DSPOT_arg0_18969 = -744252518;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).getChars(-744252518, -452816317, new char[]{ 'H' }, 2028146662);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_4teeeZ7__j$ubNCqi__u", nameAllocator.newName("4teeeZ7#)j$ubNCqi}@u", new Object()));
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55799_sd66422_sd96397_failAssert9() throws Exception {
        try {
            int __DSPOT_arg3_44155 = -383700388;
            char[] __DSPOT_arg2_44154 = new char[]{ ' ' };
            int __DSPOT_arg1_44153 = 426733112;
            int __DSPOT_arg0_44152 = -1451558282;
            int __DSPOT_arg4_25917 = -88677006;
            int __DSPOT_arg3_25916 = -1506268177;
            String __DSPOT_arg2_25915 = "TR2UV5g/6ilMCWes}aoE";
            int __DSPOT_arg1_25914 = 619292335;
            boolean __DSPOT_arg0_25913 = true;
            String __DSPOT_arg1_18937 = "RgH[pc#nJs&8,c[(|XF#";
            String __DSPOT_arg0_18936 = "w9lcS9m<5qQ:]#c,-qOf";
            NameAllocator nameAllocator = new NameAllocator();
            String __DSPOT_invoc_3 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_sd55799__12 = nameAllocator.newName("foo", 1).replaceFirst(__DSPOT_arg0_18936, "RgH[pc#nJs&8,c[(|XF#");
            boolean o_tagReuseForbidden_sd55799_sd66422__21 = nameAllocator.newName("foo", 1).replaceFirst(__DSPOT_arg0_18936, "RgH[pc#nJs&8,c[(|XF#").regionMatches(true, 619292335, "TR2UV5g/6ilMCWes}aoE", -1506268177, -88677006);
            __DSPOT_arg0_18936.getChars(-1451558282, 426733112, new char[]{ ' ' }, -383700388);
            org.junit.Assert.fail("tagReuseForbidden_sd55799_sd66422_sd96397 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55845_sd70351_sd95576() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55845__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55845__3);
        try {
            int __DSPOT_arg3_43647 = -688759182;
            int __DSPOT_arg2_43646 = -387032470;
            String __DSPOT_arg1_43645 = "=1?^Nwi`LMU@PFRTT0J;";
            int __DSPOT_arg0_43644 = 1740175159;
            int __DSPOT_arg4_18992 = 1511445812;
            int __DSPOT_arg3_18991 = 312881541;
            String __DSPOT_arg2_18990 = "rH,?!*5_,wR`tyHWKz6x";
            int __DSPOT_arg1_18989 = -985176544;
            boolean __DSPOT_arg0_18988 = true;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(true, -985176544, __DSPOT_arg2_18990, 312881541, 1511445812);
            __DSPOT_arg2_18990.regionMatches(1740175159, "=1?^Nwi`LMU@PFRTT0J;", -387032470, -688759182);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("ww___oDpf_F_wO7_UC_G", nameAllocator.newName("ww)^<oDpf#F=wO7{UC:G"));
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55845_sd70352_sd79568() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55845__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55845__3);
        try {
            int __DSPOT_arg3_33610 = -803383819;
            char[] __DSPOT_arg2_33609 = new char[]{ '#' };
            int __DSPOT_arg1_33608 = -104814863;
            int __DSPOT_arg0_33607 = -1113382256;
            int __DSPOT_arg4_18992 = 1511445812;
            int __DSPOT_arg3_18991 = 312881541;
            String __DSPOT_arg2_18990 = "rH,?!*5_,wR`tyHWKz6x";
            int __DSPOT_arg1_18989 = -985176544;
            boolean __DSPOT_arg0_18988 = true;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(true, -985176544, __DSPOT_arg2_18990, 312881541, 1511445812);
            __DSPOT_arg2_18990.getChars(-1113382256, -104814863, new char[]{ '#' }, -803383819);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("tLu__B_fMx9LmsrnMQ9_", nameAllocator.newName("tLu_:B(fMx9LmsrnMQ9[", new Object()));
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove55867litString94738() throws Exception {
        try {
            String o_tagReuseForbidden_remove55867__5 = new NameAllocator().newName("public", 1);
            Assert.assertEquals("public_", new NameAllocator().newName("public", 1));
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55799_sd66422_sd96413() throws Exception {
        String __DSPOT_arg0_18936 = "w9lcS9m<5qQ:]#c,-qOf";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.newName("bar", 1);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_sd55799__12 = nameAllocator.newName("foo", 1).replaceFirst(__DSPOT_arg0_18936, "RgH[pc#nJs&8,c[(|XF#");
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55799__12);
        boolean o_tagReuseForbidden_sd55799_sd66422__21 = o_tagReuseForbidden_sd55799__12.regionMatches(true, 619292335, "TR2UV5g/6ilMCWes}aoE", -1506268177, -88677006);
        boolean o_tagReuseForbidden_sd55799_sd66422_sd96413__29 = __DSPOT_arg0_18936.regionMatches(1372439331, "WLmqT(oLcePesj$R}ZPQ", 368521391, 159323428);
        Assert.assertFalse(o_tagReuseForbidden_sd55799_sd66422_sd96413__29);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55799__12);
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55845_sd70352_sd79552() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_tagReuseForbidden_sd55845__3 = nameAllocator.newName("foo", 1);
        Assert.assertEquals("foo", o_tagReuseForbidden_sd55845__3);
        try {
            int __DSPOT_arg4_18992 = 1511445812;
            int __DSPOT_arg3_18991 = 312881541;
            String __DSPOT_arg2_18990 = "rH,?!*5_,wR`tyHWKz6x";
            int __DSPOT_arg1_18989 = -985176544;
            boolean __DSPOT_arg0_18988 = true;
            String __DSPOT_invoc_6 = nameAllocator.newName("bar", 1);
            nameAllocator.newName("bar", 1).regionMatches(true, -985176544, "rH,?!*5_,wR`tyHWKz6x", 312881541, 1511445812);
        } catch (IllegalArgumentException expected) {
        }
        String o_tagReuseForbidden_sd55845_sd70352__22 = nameAllocator.newName("tLu_:B(fMx9LmsrnMQ9[", new Object());
        Assert.assertEquals("tLu__B_fMx9LmsrnMQ9_", o_tagReuseForbidden_sd55845_sd70352__22);
        Assert.assertEquals("_Cy_aWkk_7_J__2_0ALC", nameAllocator.newName(">Cy+aWkk&7@J=+2;0ALC", new Object()));
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_remove55867litString94734() throws Exception {
        try {
            String o_tagReuseForbidden_remove55867__5 = new NameAllocator().newName("1ab", 1);
            Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", 1));
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void tagReuseForbidden_sd55763_sd62772_sd79904_failAssert10() throws Exception {
        try {
            Object __DSPOT_tag_33662 = new Object();
            Object __DSPOT_tag_23179 = new Object();
            String __DSPOT_suggestion_23178 = "ll>*G6N>ZK7zp_UkEUjR";
            Object __DSPOT_tag_18885 = new Object();
            String __DSPOT_suggestion_18884 = "Ph?>aQlT]X,jXdJzE&AY";
            NameAllocator nameAllocator = new NameAllocator();
            String o_tagReuseForbidden_sd55763__6 = nameAllocator.newName("foo", 1);
            try {
                nameAllocator.newName("bar", 1);
            } catch (IllegalArgumentException expected) {
            }
            String o_tagReuseForbidden_sd55763__11 = nameAllocator.newName("Ph?>aQlT]X,jXdJzE&AY", new Object());
            String o_tagReuseForbidden_sd55763_sd62772__20 = nameAllocator.newName("ll>*G6N>ZK7zp_UkEUjR", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("tagReuseForbidden_sd55763_sd62772_sd79904 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_sd99709() throws Exception {
        Assert.assertEquals("oiON_ptGYtDp_z_L_Gib", new NameAllocator().newName("oiON)ptGYtDp&z#L;Gib"));
    }

    @Test(timeout = 10000)
    public void usage_add99714() throws Exception {
        Assert.assertEquals("bar", new NameAllocator().newName("bar", 2));
    }

    @Test(timeout = 10000)
    public void usage_add99716_failAssert1() throws Exception {
        try {
            NameAllocator nameAllocator = new NameAllocator();
            new NameAllocator().get(1);
            org.junit.Assert.fail("usage_add99716 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_sd99710() throws Exception {
        Assert.assertEquals("Oux6EHu_Um0jk___a__D", new NameAllocator().newName("Oux6EHu=Um0jk (]a@|D", new Object()));
    }

    @Test(timeout = 10000)
    public void usage_add99714litString100034() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("_1ab", 2));
    }

    @Test(timeout = 10000)
    public void usage_sd99709litString99967() throws Exception {
        Assert.assertEquals("public_", new NameAllocator().newName("public"));
    }

    @Test(timeout = 10000)
    public void usage_add99714litString100033() throws Exception {
        Assert.assertEquals("_1ab", new NameAllocator().newName("1ab", 2));
    }

    @Test(timeout = 10000)
    public void usage_sd99709litString99965() throws Exception {
        Assert.assertEquals("oiON_ptYtDp_z_L_Gib", new NameAllocator().newName("oiON)ptYtDp&z#L;Gib"));
    }

    @Test(timeout = 10000)
    public void usage_sd99709litString99955() throws Exception {
        Assert.assertEquals("", new NameAllocator().newName(""));
    }

    @Test(timeout = 10000)
    public void usage_add99714litString100036() throws Exception {
        Assert.assertEquals("_ab", new NameAllocator().newName("&ab", 2));
    }

    @Test(timeout = 10000)
    public void usage_sd99710_sd99994_failAssert2() throws Exception {
        try {
            Object __DSPOT_tag_45762 = new Object();
            Object __DSPOT_tag_45757 = new Object();
            String __DSPOT_suggestion_45756 = "Oux6EHu=Um0jk (]a@|D";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_sd99710__6 = nameAllocator.newName("Oux6EHu=Um0jk (]a@|D", new Object());
            nameAllocator.get(new Object());
            org.junit.Assert.fail("usage_sd99710_sd99994 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_sd99710_add99997_failAssert3() throws Exception {
        try {
            Object __DSPOT_tag_45757 = new Object();
            String __DSPOT_suggestion_45756 = "Oux6EHu=Um0jk (]a@|D";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_45756, __DSPOT_tag_45757);
            String o_usage_sd99710__6 = nameAllocator.newName(__DSPOT_suggestion_45756, __DSPOT_tag_45757);
            org.junit.Assert.fail("usage_sd99710_add99997 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_sd99710_sd99994_failAssert2_sd103453() throws Exception {
        try {
            String __DSPOT_arg0_46469 = "dSVjYF&45@NTvSO3gwh(";
            Object __DSPOT_tag_45762 = new Object();
            Object __DSPOT_tag_45757 = new Object();
            String __DSPOT_suggestion_45756 = "Oux6EHu=Um0jk (]a@|D";
            NameAllocator nameAllocator = new NameAllocator();
            String o_usage_sd99710__6 = nameAllocator.newName("Oux6EHu=Um0jk (]a@|D", new Object());
            Assert.assertEquals("Oux6EHu_Um0jk___a__D", nameAllocator.newName("Oux6EHu=Um0jk (]a@|D", new Object()));
            String __DSPOT_invoc_14 = nameAllocator.get(new Object());
            org.junit.Assert.fail("usage_sd99710_sd99994 should have thrown IllegalArgumentException");
            nameAllocator.get(new Object()).compareTo("dSVjYF&45@NTvSO3gwh(");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_sd99710_sd99996_sd100857() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99710__6 = nameAllocator.newName("Oux6EHu=Um0jk (]a@|D", new Object());
        Assert.assertEquals("Oux6EHu_Um0jk___a__D", o_usage_sd99710__6);
        String o_usage_sd99710_sd99996__13 = nameAllocator.newName("kpBi >iFD?+:[f7uY& O", new Object());
        Assert.assertEquals("kpBi__iFD____f7uY__O", o_usage_sd99710_sd99996__13);
        Assert.assertEquals("_Y_B_QnX_46_w__X3U_0", nameAllocator.newName("<Y.B;QnX@46<w#%X3U&0"));
    }

    @Test(timeout = 10000)
    public void usage_sd99710_sd99995_add102410_failAssert6() throws Exception {
        try {
            String __DSPOT_suggestion_45763 = "zd]&at,OfS?Q[X)d(#2O";
            Object __DSPOT_tag_45757 = new Object();
            String __DSPOT_suggestion_45756 = "Oux6EHu=Um0jk (]a@|D";
            NameAllocator nameAllocator = new NameAllocator();
            nameAllocator.newName(__DSPOT_suggestion_45756, __DSPOT_tag_45757);
            String o_usage_sd99710__6 = nameAllocator.newName(__DSPOT_suggestion_45756, __DSPOT_tag_45757);
            String o_usage_sd99710_sd99995__11 = nameAllocator.newName("zd]&at,OfS?Q[X)d(#2O");
            org.junit.Assert.fail("usage_sd99710_sd99995_add102410 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void usage_sd99710_sd99996litString100830() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99710__6 = nameAllocator.newName("Oux6EHu=Um0jk (]a@|D", new Object());
        Assert.assertEquals("Oux6EHu_Um0jk___a__D", o_usage_sd99710__6);
        Assert.assertEquals("public_", nameAllocator.newName("public", new Object()));
    }

    @Test(timeout = 10000)
    public void usage_sd99709_sd99971litString102353() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99709__4 = nameAllocator.newName("a\ud83c\udf7ab");
        Assert.assertEquals("a_b", o_usage_sd99709__4);
        Assert.assertEquals("_1_8MXE8PsBs______dbd", nameAllocator.newName("1 8MXE8PsBs) })/+dbd", new Object()));
    }

    @Test(timeout = 10000)
    public void usage_sd99710_sd99995_add102411() throws Exception {
        String __DSPOT_suggestion_45763 = "zd]&at,OfS?Q[X)d(#2O";
        NameAllocator nameAllocator = new NameAllocator();
        String o_usage_sd99710__6 = nameAllocator.newName("Oux6EHu=Um0jk (]a@|D", new Object());
        Assert.assertEquals("Oux6EHu_Um0jk___a__D", o_usage_sd99710__6);
        String o_usage_sd99710_sd99995_add102411__11 = nameAllocator.newName(__DSPOT_suggestion_45763);
        Assert.assertEquals("zd__at_OfS_Q_X_d__2O", o_usage_sd99710_sd99995_add102411__11);
        Assert.assertEquals("zd__at_OfS_Q_X_d__2O_", nameAllocator.newName(__DSPOT_suggestion_45763));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104344_failAssert0() throws Exception {
        try {
            Object __DSPOT_tag_46735 = new Object();
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.get(new Object());
            org.junit.Assert.fail("useBeforeAllocateForbidden_sd104344 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104346() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("CQyyaq1_5_b_y__F3_k_", nameAllocator.newName("CQyyaq1=5[b[y%&F3<k&", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104345() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("___sip_X_sr01L6nkNxM", nameAllocator.newName(".{|sip&X-sr01L6nkNxM"));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104346litString104725() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_ab", nameAllocator.newName("&ab", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104346_sd104740() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            int __DSPOT_arg0_46877 = 111086455;
            String __DSPOT_invoc_8 = nameAllocator.get(1);
            nameAllocator.get(1).charAt(111086455);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("CQyyaq1_5_b_y__F3_k_", nameAllocator.newName("CQyyaq1=5[b[y%&F3<k&", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104346litString104717() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("public_", nameAllocator.newName("public", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104346litString104721() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("_1ab", nameAllocator.newName("1ab", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104346_add104789() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("CQyyaq1_5_b_y__F3_k_", nameAllocator.newName("CQyyaq1=5[b[y%&F3<k&", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104345_add104710() throws Exception {
        String __DSPOT_suggestion_46736 = ".{|sip&X-sr01L6nkNxM";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd104345_add104710__8 = nameAllocator.newName(__DSPOT_suggestion_46736);
        Assert.assertEquals("___sip_X_sr01L6nkNxM", o_useBeforeAllocateForbidden_sd104345_add104710__8);
        Assert.assertEquals("___sip_X_sr01L6nkNxM_", nameAllocator.newName(__DSPOT_suggestion_46736));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104345_sd104694() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            String __DSPOT_arg1_46859 = "9H+2QZUA;lBxSYQOL3b#";
            String __DSPOT_arg0_46858 = "9sX6][WH}sRIo]l4{?|K";
            String __DSPOT_invoc_6 = nameAllocator.get(1);
            nameAllocator.get(1).replaceFirst("9sX6][WH}sRIo]l4{?|K", "9H+2QZUA;lBxSYQOL3b#");
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("___sip_X_sr01L6nkNxM", nameAllocator.newName(".{|sip&X-sr01L6nkNxM"));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104346litString104729() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            nameAllocator.get(1);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("", nameAllocator.newName("", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104346_sd104738_add109082_failAssert3() throws Exception {
        try {
            Object __DSPOT_tag_46876 = new Object();
            String __DSPOT_suggestion_46875 = "c9/=T#@B8({7GBG@G#/=";
            Object __DSPOT_tag_46738 = new Object();
            String __DSPOT_suggestion_46737 = "CQyyaq1=5[b[y%&F3<k&";
            NameAllocator nameAllocator = new NameAllocator();
            try {
                nameAllocator.get(1);
            } catch (IllegalArgumentException expected) {
            }
            nameAllocator.newName(__DSPOT_suggestion_46737, __DSPOT_tag_46738);
            String o_useBeforeAllocateForbidden_sd104346__10 = nameAllocator.newName(__DSPOT_suggestion_46737, __DSPOT_tag_46738);
            String o_useBeforeAllocateForbidden_sd104346_sd104738__17 = nameAllocator.newName("c9/=T#@B8({7GBG@G#/=", new Object());
            org.junit.Assert.fail("useBeforeAllocateForbidden_sd104346_sd104738_add109082 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104346_sd104778_sd108862() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            int __DSPOT_arg4_49605 = 1153709446;
            int __DSPOT_arg3_49604 = 395790604;
            String __DSPOT_arg2_49603 = "WFrQUTfD XJI$uOB+gX-";
            int __DSPOT_arg1_49602 = 1931848749;
            boolean __DSPOT_arg0_49601 = true;
            int __DSPOT_arg1_46934 = 537762892;
            String __DSPOT_arg0_46933 = "A[Gb$CamtVR849sHT}dd";
            String __DSPOT_invoc_8 = nameAllocator.get(1);
            nameAllocator.get(1).startsWith(__DSPOT_arg0_46933, 537762892);
            __DSPOT_arg0_46933.regionMatches(true, 1931848749, "WFrQUTfD XJI$uOB+gX-", 395790604, 1153709446);
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("CQyyaq1_5_b_y__F3_k_", nameAllocator.newName("CQyyaq1=5[b[y%&F3<k&", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104345_sd104658_sd114844() throws Exception {
        NameAllocator nameAllocator = new NameAllocator();
        try {
            int __DSPOT_arg1_54071 = 948130952;
            int __DSPOT_arg0_54070 = -319978058;
            String __DSPOT_invoc_9 = nameAllocator.get(1);
            nameAllocator.get(1).subSequence(-319978058, 948130952);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd104345__8 = nameAllocator.newName(".{|sip&X-sr01L6nkNxM");
        Assert.assertEquals("___sip_X_sr01L6nkNxM", o_useBeforeAllocateForbidden_sd104345__8);
        Assert.assertEquals("_8eZr_h_i__3_af___0S", nameAllocator.newName("-8eZr@h)i[:3?af%,!0S", new Object()));
    }

    @Test(timeout = 10000)
    public void useBeforeAllocateForbidden_sd104345_add104710_sd110191() throws Exception {
        String __DSPOT_suggestion_46736 = ".{|sip&X-sr01L6nkNxM";
        NameAllocator nameAllocator = new NameAllocator();
        try {
            int __DSPOT_arg3_50461 = 1475048519;
            int __DSPOT_arg2_50460 = -1569010185;
            String __DSPOT_arg1_50459 = "K|9CV/Dp_oWBv<tT]8#N";
            int __DSPOT_arg0_50458 = 111415113;
            String __DSPOT_invoc_6 = nameAllocator.get(1);
            nameAllocator.get(1).regionMatches(111415113, "K|9CV/Dp_oWBv<tT]8#N", -1569010185, 1475048519);
        } catch (IllegalArgumentException expected) {
        }
        String o_useBeforeAllocateForbidden_sd104345_add104710__8 = nameAllocator.newName(__DSPOT_suggestion_46736);
        Assert.assertEquals("___sip_X_sr01L6nkNxM", o_useBeforeAllocateForbidden_sd104345_add104710__8);
        Assert.assertEquals("___sip_X_sr01L6nkNxM_", nameAllocator.newName(__DSPOT_suggestion_46736));
    }
}

