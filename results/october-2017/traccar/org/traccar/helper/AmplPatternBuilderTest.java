

package org.traccar.helper;


public class AmplPatternBuilderTest {
    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder() {
        java.lang.String o_testPatternBuilder__1 = new org.traccar.helper.PatternBuilder().text("$GPRMC").toString();
        java.lang.String o_testPatternBuilder__4 = new org.traccar.helper.PatternBuilder().number("(dd.x+)").toString();
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder__4);
        java.lang.String o_testPatternBuilder__7 = new org.traccar.helper.PatternBuilder().text("a").text("b").text("c").optional(2).toString();
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder__7);
        java.lang.String o_testPatternBuilder__13 = new org.traccar.helper.PatternBuilder().expression("a|b").toString();
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder__13);
        java.lang.String o_testPatternBuilder__16 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder__16);
        java.lang.String o_testPatternBuilder__19 = new org.traccar.helper.PatternBuilder().or().toString();
        org.junit.Assert.assertEquals("|", o_testPatternBuilder__19);
        java.lang.String o_testPatternBuilder__22 = new org.traccar.helper.PatternBuilder().number("|d|d|").toString();
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", o_testPatternBuilder__22);
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder__13);
        org.junit.Assert.assertEquals("\\$GPRMC", o_testPatternBuilder__1);
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder__7);
        org.junit.Assert.assertEquals("|", o_testPatternBuilder__19);
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder__4);
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder__16);
    }

    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder_literalMutationString14() {
        java.lang.String o_testPatternBuilder_literalMutationString14__1 = new org.traccar.helper.PatternBuilder().text("$GPRMC").toString();
        org.junit.Assert.assertEquals("\\$GPRMC", o_testPatternBuilder_literalMutationString14__1);
        java.lang.String o_testPatternBuilder_literalMutationString14__4 = new org.traccar.helper.PatternBuilder().number("(dd.x+)").toString();
        java.lang.String o_testPatternBuilder_literalMutationString14__7 = new org.traccar.helper.PatternBuilder().text("q").text("b").text("c").optional(2).toString();
        org.junit.Assert.assertEquals("q(?:bc)?", o_testPatternBuilder_literalMutationString14__7);
        java.lang.String o_testPatternBuilder_literalMutationString14__13 = new org.traccar.helper.PatternBuilder().expression("a|b").toString();
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder_literalMutationString14__13);
        java.lang.String o_testPatternBuilder_literalMutationString14__16 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString14__16);
        java.lang.String o_testPatternBuilder_literalMutationString14__19 = new org.traccar.helper.PatternBuilder().or().toString();
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString14__19);
        java.lang.String o_testPatternBuilder_literalMutationString14__22 = new org.traccar.helper.PatternBuilder().number("|d|d|").toString();
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", o_testPatternBuilder_literalMutationString14__22);
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder_literalMutationString14__4);
        org.junit.Assert.assertEquals("q(?:bc)?", o_testPatternBuilder_literalMutationString14__7);
        org.junit.Assert.assertEquals("\\$GPRMC", o_testPatternBuilder_literalMutationString14__1);
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder_literalMutationString14__13);
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString14__19);
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString14__16);
    }

    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder_literalMutationString9_literalMutationString559() {
        java.lang.String o_testPatternBuilder_literalMutationString9__1 = new org.traccar.helper.PatternBuilder().text("$GPRMC").toString();
        org.junit.Assert.assertEquals("\\$GPRMC", o_testPatternBuilder_literalMutationString9__1);
        java.lang.String String_1143 = o_testPatternBuilder_literalMutationString9__1;
        org.junit.Assert.assertEquals("\\$GPRMC", String_1143);
        java.lang.String o_testPatternBuilder_literalMutationString9__4 = new org.traccar.helper.PatternBuilder().number("(!dd.x+)").toString();
        org.junit.Assert.assertEquals("(!\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder_literalMutationString9__4);
        java.lang.String String_1144 = o_testPatternBuilder_literalMutationString9__4;
        org.junit.Assert.assertEquals("(!\\d{2}\\.[0-9a-fA-F]+)", String_1144);
        java.lang.String o_testPatternBuilder_literalMutationString9__7 = new org.traccar.helper.PatternBuilder().text("a").text("b").text("c").optional(2).toString();
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder_literalMutationString9__7);
        java.lang.String String_1145 = o_testPatternBuilder_literalMutationString9__7;
        org.junit.Assert.assertEquals("a(?:bc)?", String_1145);
        java.lang.String o_testPatternBuilder_literalMutationString9__13 = new org.traccar.helper.PatternBuilder().expression("a|b").toString();
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder_literalMutationString9__13);
        java.lang.String String_1146 = o_testPatternBuilder_literalMutationString9__13;
        org.junit.Assert.assertEquals("a|b", String_1146);
        java.lang.String o_testPatternBuilder_literalMutationString9__16 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString9__16);
        java.lang.String String_1147 = o_testPatternBuilder_literalMutationString9__16;
        org.junit.Assert.assertEquals("ab\\|", String_1147);
        java.lang.String o_testPatternBuilder_literalMutationString9__19 = new org.traccar.helper.PatternBuilder().or().toString();
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString9__19);
        java.lang.String String_1148 = o_testPatternBuilder_literalMutationString9__19;
        org.junit.Assert.assertEquals("|", String_1148);
        java.lang.String o_testPatternBuilder_literalMutationString9__22 = new org.traccar.helper.PatternBuilder().number("|dr|d|").toString();
        org.junit.Assert.assertEquals("\\|\\dr|\\d\\|", o_testPatternBuilder_literalMutationString9__22);
        java.lang.String String_1149 = o_testPatternBuilder_literalMutationString9__22;
        org.junit.Assert.assertEquals("\\|\\dr|\\d\\|", String_1149);
        java.lang.String String_1150 = o_testPatternBuilder_literalMutationString9__4;
        org.junit.Assert.assertEquals("(!\\d{2}\\.[0-9a-fA-F]+)", String_1150);
        java.lang.String String_1151 = o_testPatternBuilder_literalMutationString9__19;
        org.junit.Assert.assertEquals("|", String_1151);
        java.lang.String String_1152 = o_testPatternBuilder_literalMutationString9__13;
        org.junit.Assert.assertEquals("a|b", String_1152);
        java.lang.String String_1153 = o_testPatternBuilder_literalMutationString9__7;
        org.junit.Assert.assertEquals("a(?:bc)?", String_1153);
        java.lang.String String_1154 = o_testPatternBuilder_literalMutationString9__16;
        org.junit.Assert.assertEquals("ab\\|", String_1154);
        java.lang.String String_1155 = o_testPatternBuilder_literalMutationString9__1;
        org.junit.Assert.assertEquals("\\$GPRMC", String_1155);
        org.junit.Assert.assertEquals("\\|\\dr|\\d\\|", o_testPatternBuilder_literalMutationString9__22);
        org.junit.Assert.assertEquals("(!\\d{2}\\.[0-9a-fA-F]+)", String_1144);
        org.junit.Assert.assertEquals("a(?:bc)?", String_1145);
        org.junit.Assert.assertEquals("ab\\|", String_1147);
        org.junit.Assert.assertEquals("a|b", String_1152);
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString9__19);
        org.junit.Assert.assertEquals("(!\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder_literalMutationString9__4);
        org.junit.Assert.assertEquals("a(?:bc)?", String_1153);
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder_literalMutationString9__13);
        org.junit.Assert.assertEquals("\\|\\dr|\\d\\|", String_1149);
        org.junit.Assert.assertEquals("\\$GPRMC", String_1143);
        org.junit.Assert.assertEquals("(!\\d{2}\\.[0-9a-fA-F]+)", String_1150);
        org.junit.Assert.assertEquals("a|b", String_1146);
        org.junit.Assert.assertEquals("|", String_1148);
        org.junit.Assert.assertEquals("|", String_1151);
        org.junit.Assert.assertEquals("\\$GPRMC", o_testPatternBuilder_literalMutationString9__1);
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString9__16);
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder_literalMutationString9__7);
        org.junit.Assert.assertEquals("ab\\|", String_1154);
    }

    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder_literalMutationString34_literalMutationString1558_literalMutationString9415() {
        java.lang.String o_testPatternBuilder_literalMutationString34__1 = new org.traccar.helper.PatternBuilder().text("$GPRMC").toString();
        org.junit.Assert.assertEquals("\\$GPRMC", o_testPatternBuilder_literalMutationString34__1);
        java.lang.String String_7856 = o_testPatternBuilder_literalMutationString34__1;
        org.junit.Assert.assertEquals("\\$GPRMC", String_7856);
        java.lang.String String_1897 = o_testPatternBuilder_literalMutationString34__1;
        org.junit.Assert.assertEquals("\\$GPRMC", String_1897);
        java.lang.String String_7857 = String_1897;
        org.junit.Assert.assertEquals("\\$GPRMC", String_7857);
        java.lang.String o_testPatternBuilder_literalMutationString34__4 = new org.traccar.helper.PatternBuilder().number("(dd.x+)").toString();
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder_literalMutationString34__4);
        java.lang.String String_7858 = o_testPatternBuilder_literalMutationString34__4;
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_7858);
        java.lang.String String_1898 = o_testPatternBuilder_literalMutationString34__4;
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_1898);
        java.lang.String String_7859 = String_1898;
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_7859);
        java.lang.String o_testPatternBuilder_literalMutationString34__7 = new org.traccar.helper.PatternBuilder().text("a").text("b").text("c").optional(2).toString();
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder_literalMutationString34__7);
        java.lang.String String_7860 = o_testPatternBuilder_literalMutationString34__7;
        org.junit.Assert.assertEquals("a(?:bc)?", String_7860);
        java.lang.String String_1899 = o_testPatternBuilder_literalMutationString34__7;
        org.junit.Assert.assertEquals("a(?:bc)?", String_1899);
        java.lang.String String_7861 = String_1899;
        org.junit.Assert.assertEquals("a(?:bc)?", String_7861);
        java.lang.String o_testPatternBuilder_literalMutationString34__13 = new org.traccar.helper.PatternBuilder().expression("ab").toString();
        org.junit.Assert.assertEquals("ab", o_testPatternBuilder_literalMutationString34__13);
        java.lang.String String_7862 = o_testPatternBuilder_literalMutationString34__13;
        org.junit.Assert.assertEquals("ab", String_7862);
        java.lang.String String_1900 = o_testPatternBuilder_literalMutationString34__13;
        org.junit.Assert.assertEquals("ab", String_1900);
        java.lang.String String_7863 = String_1900;
        org.junit.Assert.assertEquals("ab", String_7863);
        java.lang.String o_testPatternBuilder_literalMutationString34__16 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString34__16);
        java.lang.String String_7864 = o_testPatternBuilder_literalMutationString34__16;
        org.junit.Assert.assertEquals("ab\\|", String_7864);
        java.lang.String String_1901 = o_testPatternBuilder_literalMutationString34__16;
        org.junit.Assert.assertEquals("ab\\|", String_1901);
        java.lang.String String_7865 = String_1901;
        org.junit.Assert.assertEquals("ab\\|", String_7865);
        java.lang.String o_testPatternBuilder_literalMutationString34__19 = new org.traccar.helper.PatternBuilder().or().toString();
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString34__19);
        java.lang.String String_7866 = o_testPatternBuilder_literalMutationString34__19;
        org.junit.Assert.assertEquals("|", String_7866);
        java.lang.String o_testPatternBuilder_literalMutationString34__22 = new org.traccar.helper.PatternBuilder().number("|d|d|").toString();
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", o_testPatternBuilder_literalMutationString34__22);
        java.lang.String String_1902 = o_testPatternBuilder_literalMutationString34__22;
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_1902);
        java.lang.String String_7867 = String_1902;
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_7867);
        java.lang.String String_1903 = o_testPatternBuilder_literalMutationString34__1;
        org.junit.Assert.assertEquals("\\$GPRMC", String_1903);
        java.lang.String String_7868 = String_1903;
        org.junit.Assert.assertEquals("\\$GPRMC", String_7868);
        java.lang.String String_1904 = o_testPatternBuilder_literalMutationString34__13;
        org.junit.Assert.assertEquals("ab", String_1904);
        java.lang.String String_7869 = String_1904;
        org.junit.Assert.assertEquals("ab", String_7869);
        java.lang.String String_1905 = o_testPatternBuilder_literalMutationString34__19;
        org.junit.Assert.assertEquals("|", String_1905);
        java.lang.String String_7870 = String_1905;
        org.junit.Assert.assertEquals("|", String_7870);
        java.lang.String String_1906 = o_testPatternBuilder_literalMutationString34__4;
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_1906);
        java.lang.String String_7871 = String_1906;
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_7871);
        java.lang.String String_1907 = o_testPatternBuilder_literalMutationString34__7;
        org.junit.Assert.assertEquals("a(?:bc)?", String_1907);
        java.lang.String String_7872 = String_1907;
        org.junit.Assert.assertEquals("a(?:bc)?", String_7872);
        java.lang.String String_1908 = o_testPatternBuilder_literalMutationString34__16;
        org.junit.Assert.assertEquals("ab\\|", String_1908);
        java.lang.String String_7873 = String_1908;
        org.junit.Assert.assertEquals("ab\\|", String_7873);
        java.lang.String String_7874 = String_1901;
        java.lang.String String_7875 = String_1902;
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_7875);
        java.lang.String String_7876 = o_testPatternBuilder_literalMutationString34__4;
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_7876);
        java.lang.String String_7877 = String_1898;
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_7877);
        java.lang.String String_7878 = String_1903;
        org.junit.Assert.assertEquals("\\$GPRMC", String_7878);
        java.lang.String String_7879 = o_testPatternBuilder_literalMutationString34__13;
        org.junit.Assert.assertEquals("ab", String_7879);
        java.lang.String String_7880 = String_1900;
        org.junit.Assert.assertEquals("ab", String_7880);
        java.lang.String String_7881 = String_1907;
        org.junit.Assert.assertEquals("a(?:bc)?", String_7881);
        java.lang.String String_7882 = String_1897;
        org.junit.Assert.assertEquals("\\$GPRMC", String_7882);
        java.lang.String String_7883 = o_testPatternBuilder_literalMutationString34__19;
        org.junit.Assert.assertEquals("|", String_7883);
        java.lang.String String_7884 = String_1905;
        org.junit.Assert.assertEquals("|", String_7884);
        java.lang.String String_7885 = String_1906;
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_7885);
        java.lang.String String_7886 = o_testPatternBuilder_literalMutationString34__16;
        org.junit.Assert.assertEquals("ab\\|", String_7886);
        java.lang.String String_7887 = String_1904;
        org.junit.Assert.assertEquals("ab", String_7887);
        java.lang.String String_7888 = o_testPatternBuilder_literalMutationString34__1;
        org.junit.Assert.assertEquals("\\$GPRMC", String_7888);
        java.lang.String String_7889 = o_testPatternBuilder_literalMutationString34__22;
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_7889);
        java.lang.String String_7890 = String_1899;
        org.junit.Assert.assertEquals("a(?:bc)?", String_7890);
        java.lang.String String_7891 = o_testPatternBuilder_literalMutationString34__7;
        org.junit.Assert.assertEquals("a(?:bc)?", String_7891);
        org.junit.Assert.assertEquals("ab", String_7862);
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_7858);
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_7875);
        org.junit.Assert.assertEquals("ab", String_1900);
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_7859);
        org.junit.Assert.assertEquals("ab", String_7863);
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder_literalMutationString34__7);
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString34__16);
        org.junit.Assert.assertEquals("\\$GPRMC", String_7878);
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_7877);
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", o_testPatternBuilder_literalMutationString34__22);
        org.junit.Assert.assertEquals("ab\\|", String_7874);
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_7889);
        org.junit.Assert.assertEquals("a(?:bc)?", String_1907);
        org.junit.Assert.assertEquals("ab", String_7879);
        org.junit.Assert.assertEquals("ab", String_7880);
        org.junit.Assert.assertEquals("a(?:bc)?", String_7881);
        org.junit.Assert.assertEquals("a(?:bc)?", String_7890);
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_7871);
        org.junit.Assert.assertEquals("a(?:bc)?", String_7861);
        org.junit.Assert.assertEquals("a(?:bc)?", String_1899);
        org.junit.Assert.assertEquals("|", String_7866);
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_1906);
        org.junit.Assert.assertEquals("ab\\|", String_7864);
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_1898);
        org.junit.Assert.assertEquals("ab\\|", String_1901);
        org.junit.Assert.assertEquals("ab", String_7869);
        org.junit.Assert.assertEquals("|", String_7884);
        org.junit.Assert.assertEquals("\\$GPRMC", String_7856);
        org.junit.Assert.assertEquals("|", String_7870);
        org.junit.Assert.assertEquals("ab", String_1904);
        org.junit.Assert.assertEquals("a(?:bc)?", String_7860);
        org.junit.Assert.assertEquals("\\$GPRMC", String_7882);
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder_literalMutationString34__4);
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_7885);
        org.junit.Assert.assertEquals("ab\\|", String_7865);
        org.junit.Assert.assertEquals("|", String_1905);
        org.junit.Assert.assertEquals("ab", o_testPatternBuilder_literalMutationString34__13);
        org.junit.Assert.assertEquals("\\$GPRMC", String_7868);
        org.junit.Assert.assertEquals("a(?:bc)?", String_7872);
        org.junit.Assert.assertEquals("\\$GPRMC", o_testPatternBuilder_literalMutationString34__1);
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_1902);
        org.junit.Assert.assertEquals("\\$GPRMC", String_7888);
        org.junit.Assert.assertEquals("\\$GPRMC", String_1903);
        org.junit.Assert.assertEquals("ab\\|", String_7873);
        org.junit.Assert.assertEquals("ab\\|", String_7886);
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString34__19);
        org.junit.Assert.assertEquals("|", String_7883);
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", String_7876);
        org.junit.Assert.assertEquals("\\$GPRMC", String_7857);
        org.junit.Assert.assertEquals("\\$GPRMC", String_1897);
        org.junit.Assert.assertEquals("ab\\|", String_1908);
        org.junit.Assert.assertEquals("ab", String_7887);
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_7867);
    }

    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder */
    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder_literalMutationString17() {
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString17__1 = new org.traccar.helper.PatternBuilder().text("$GPRMC").toString();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString17__4 = new org.traccar.helper.PatternBuilder().number("(dd.x+)").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder_literalMutationString17__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString17__7 = new org.traccar.helper.PatternBuilder().text("a").text("2").text("c").optional(2).toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a(?:2c)?", o_testPatternBuilder_literalMutationString17__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString17__13 = new org.traccar.helper.PatternBuilder().expression("a|b").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder_literalMutationString17__13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString17__16 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString17__16);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString17__19 = new org.traccar.helper.PatternBuilder().or().toString();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString17__22 = new org.traccar.helper.PatternBuilder().number("|d|d|").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", o_testPatternBuilder_literalMutationString17__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString17__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder_literalMutationString17__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", o_testPatternBuilder_literalMutationString17__1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a(?:2c)?", o_testPatternBuilder_literalMutationString17__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.[0-9a-fA-F]+)", o_testPatternBuilder_literalMutationString17__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString17__19);
    }

    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder */
    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder_literalMutationString6 */
    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder_literalMutationString6_literalMutationString397() {
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString6__1 = new org.traccar.helper.PatternBuilder().text("Gdhscb").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdhscb", o_testPatternBuilder_literalMutationString6__1);
        java.lang.String String_246 = o_testPatternBuilder_literalMutationString6__1;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdhscb", String_246);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString6__4 = new org.traccar.helper.PatternBuilder().number("(dd.+)").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.+)", o_testPatternBuilder_literalMutationString6__4);
        java.lang.String String_247 = o_testPatternBuilder_literalMutationString6__4;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.+)", String_247);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString6__7 = new org.traccar.helper.PatternBuilder().text("a").text("b").text("c").optional(2).toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder_literalMutationString6__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString6__13 = new org.traccar.helper.PatternBuilder().expression("a|b").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder_literalMutationString6__13);
        java.lang.String String_248 = o_testPatternBuilder_literalMutationString6__13;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_248);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString6__16 = new org.traccar.helper.PatternBuilder().expression("ab|").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString6__16);
        java.lang.String String_249 = o_testPatternBuilder_literalMutationString6__16;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", String_249);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString6__19 = new org.traccar.helper.PatternBuilder().or().toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString6__19);
        java.lang.String String_250 = o_testPatternBuilder_literalMutationString6__19;
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString6__22 = new org.traccar.helper.PatternBuilder().number("|d|d|").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", o_testPatternBuilder_literalMutationString6__22);
        java.lang.String String_251 = o_testPatternBuilder_literalMutationString6__22;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_251);
        java.lang.String String_252 = o_testPatternBuilder_literalMutationString6__16;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", String_252);
        java.lang.String String_253 = o_testPatternBuilder_literalMutationString6__1;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdhscb", String_253);
        java.lang.String String_254 = o_testPatternBuilder_literalMutationString6__13;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_254);
        java.lang.String String_255 = o_testPatternBuilder_literalMutationString6__4;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.+)", String_255);
        java.lang.String String_256 = o_testPatternBuilder_literalMutationString6__7;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a(?:bc)?", String_256);
        java.lang.String String_257 = o_testPatternBuilder_literalMutationString6__19;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", String_257);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdhscb", o_testPatternBuilder_literalMutationString6__1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder_literalMutationString6__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a(?:bc)?", String_256);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", String_249);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a(?:bc)?", o_testPatternBuilder_literalMutationString6__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString6__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_251);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.+)", o_testPatternBuilder_literalMutationString6__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.+)", String_255);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdhscb", String_246);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", o_testPatternBuilder_literalMutationString6__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_248);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ab\\|", String_252);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_254);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", o_testPatternBuilder_literalMutationString6__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", String_250);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(\\d{2}\\.+)", String_247);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Gdhscb", String_253);
    }

    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder */
    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder_literalMutationString8 */
    /* amplification of org.traccar.helper.PatternBuilderTest#testPatternBuilder_literalMutationString8_literalMutationString507 */
    @org.junit.Test(timeout = 10000)
    public void testPatternBuilder_literalMutationString8_literalMutationString507_literalMutationString2876() {
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString8__1 = new org.traccar.helper.PatternBuilder().text("$GPRMC").toString();
        java.lang.String String_4832 = o_testPatternBuilder_literalMutationString8__1;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_4832);
        java.lang.String String_62 = o_testPatternBuilder_literalMutationString8__1;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_62);
        java.lang.String String_4833 = String_62;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_4833);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString8__4 = new org.traccar.helper.PatternBuilder().number("*zH_,y(").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", o_testPatternBuilder_literalMutationString8__4);
        java.lang.String String_4834 = o_testPatternBuilder_literalMutationString8__4;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_4834);
        java.lang.String String_63 = o_testPatternBuilder_literalMutationString8__4;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_63);
        java.lang.String String_4835 = String_63;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_4835);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString8__7 = new org.traccar.helper.PatternBuilder().text("").text("b").text("c").optional(2).toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", o_testPatternBuilder_literalMutationString8__7);
        java.lang.String String_4836 = o_testPatternBuilder_literalMutationString8__7;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_4836);
        java.lang.String String_64 = o_testPatternBuilder_literalMutationString8__7;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_64);
        java.lang.String String_4837 = String_64;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_4837);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString8__13 = new org.traccar.helper.PatternBuilder().expression("a|b").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder_literalMutationString8__13);
        java.lang.String String_4838 = o_testPatternBuilder_literalMutationString8__13;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_4838);
        java.lang.String String_65 = o_testPatternBuilder_literalMutationString8__13;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_65);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString8__16 = new org.traccar.helper.PatternBuilder().expression("").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_testPatternBuilder_literalMutationString8__16);
        java.lang.String String_4839 = o_testPatternBuilder_literalMutationString8__16;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_4839);
        java.lang.String String_66 = o_testPatternBuilder_literalMutationString8__16;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_66);
        java.lang.String String_4840 = String_66;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_4840);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString8__19 = new org.traccar.helper.PatternBuilder().or().toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString8__19);
        java.lang.String String_4841 = o_testPatternBuilder_literalMutationString8__19;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", String_4841);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testPatternBuilder_literalMutationString8__22 = new org.traccar.helper.PatternBuilder().number("|d|d|").toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", o_testPatternBuilder_literalMutationString8__22);
        java.lang.String String_4842 = o_testPatternBuilder_literalMutationString8__22;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_4842);
        java.lang.String String_67 = o_testPatternBuilder_literalMutationString8__22;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_67);
        java.lang.String String_4843 = String_67;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_4843);
        java.lang.String String_68 = o_testPatternBuilder_literalMutationString8__16;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_68);
        java.lang.String String_4844 = String_68;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_4844);
        java.lang.String String_69 = o_testPatternBuilder_literalMutationString8__7;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_69);
        java.lang.String String_4845 = String_69;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_4845);
        java.lang.String String_70 = o_testPatternBuilder_literalMutationString8__13;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_70);
        java.lang.String String_4846 = String_70;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_4846);
        java.lang.String String_71 = o_testPatternBuilder_literalMutationString8__19;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", String_71);
        java.lang.String String_4847 = String_71;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", String_4847);
        java.lang.String String_72 = o_testPatternBuilder_literalMutationString8__1;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_72);
        java.lang.String String_4848 = String_72;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_4848);
        java.lang.String String_73 = o_testPatternBuilder_literalMutationString8__4;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_73);
        java.lang.String String_4849 = String_73;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_4849);
        java.lang.String String_4850 = String_62;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_4850);
        java.lang.String String_4851 = o_testPatternBuilder_literalMutationString8__19;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", String_4851);
        java.lang.String String_4852 = String_66;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_4852);
        java.lang.String String_4853 = String_67;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_4853);
        java.lang.String String_4854 = o_testPatternBuilder_literalMutationString8__7;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_4854);
        java.lang.String String_4855 = String_69;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_4855);
        java.lang.String String_4856 = String_70;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_4856);
        java.lang.String String_4857 = o_testPatternBuilder_literalMutationString8__16;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_4857);
        java.lang.String String_4858 = o_testPatternBuilder_literalMutationString8__22;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_4858);
        java.lang.String String_4859 = o_testPatternBuilder_literalMutationString8__1;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_4859);
        java.lang.String String_4860 = String_71;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", String_4860);
        java.lang.String String_4861 = String_64;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_4861);
        java.lang.String String_4862 = String_63;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_4862);
        java.lang.String String_4863 = o_testPatternBuilder_literalMutationString8__13;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_4863);
        java.lang.String String_4864 = String_65;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_4864);
        java.lang.String String_4865 = o_testPatternBuilder_literalMutationString8__4;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_4865);
        java.lang.String String_4866 = String_68;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_4866);
        java.lang.String String_4867 = String_72;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_4867);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_4859);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_4839);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_69);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_4840);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_68);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_4857);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", o_testPatternBuilder_literalMutationString8__1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_4845);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", String_4860);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_66);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_4856);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_4837);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", String_4847);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_4842);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", o_testPatternBuilder_literalMutationString8__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_67);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_4863);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_4862);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_4843);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_72);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", o_testPatternBuilder_literalMutationString8__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", o_testPatternBuilder_literalMutationString8__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_4861);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_4858);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_4848);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_4850);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_62);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_4833);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_4834);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_73);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_4838);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", String_4851);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_63);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_4835);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\$GPRMC", String_4832);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", o_testPatternBuilder_literalMutationString8__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", o_testPatternBuilder_literalMutationString8__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_4852);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_4849);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_65);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_4854);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_4864);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_testPatternBuilder_literalMutationString8__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("\\|\\d|\\d\\|", String_4853);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_4855);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_64);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", String_71);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("|", String_4841);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_70);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("(?:bc)?", String_4836);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_4844);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("*zH_,y(", String_4865);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_4866);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("a|b", String_4846);
    }
}

