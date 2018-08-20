package com.github.mustachejava.resolver;


import java.io.Reader;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class AmplClasspathResolverTest {
    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePathlitString17572_failAssert466() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("\n");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasRelativePathlitString17572 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: \n/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_add17583litString17657_failAssert505() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("\n");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_add17583__5 = Is.is(CoreMatchers.notNullValue());
            Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_add17583__7 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasRelativePath_add17583litString17657 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: \n/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_add17582litString17679_failAssert498() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            underTest.getReader("/n6nafz9U$xX9TWh*/V,!o}XE!d<S!%");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_add17582__6 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasRelativePath_add17582litString17679 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 31: templates/n6nafz9U$xX9TWh*/V,!o}XE!d<S!%", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_add17583_add17767_mg19570_failAssert545() throws Exception {
        try {
            String __DSPOT_resourceName_1207 = "XS<3HR{0aKX+9$[}4NnM";
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_add17583__5 = Is.is(CoreMatchers.notNullValue());
            Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_add17583_add17767__9 = Is.is(CoreMatchers.notNullValue());
            Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_add17583__7 = Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_1207);
            org.junit.Assert.fail("getReaderWithRootAndResourceHasRelativePath_add17583_add17767_mg19570 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 12: templates/XS<3HR{0aKX+9$[}4NnM", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePathlitString23831_failAssert641() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("<&7-)?keC!");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePathlitString23831 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: <&7-)?keC!/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePathlitString23839_failAssert645() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("]cpa(5n(Hm0*Gx9F0}z|[Mv`FGXOYo9");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePathlitString23839 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/]cpa(5n(Hm0*Gx9F0}z|[Mv`FGXOYo9", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23844litString23916_failAssert676() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("4|YKBHl]&?");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23844__5 = Is.is(CoreMatchers.notNullValue());
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23844__7 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23844litString23916 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 1: 4|YKBHl]&?/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23843litString23942_failAssert683() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            underTest.getReader("\n");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23843__6 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23843litString23942 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/\n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23845_add24042_mg26164_failAssert708() throws Exception {
        try {
            String __DSPOT_resourceName_1574 = "/-.&qH`[ =I*Fipqw>8)";
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23845_add24042__5 = CoreMatchers.notNullValue();
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23845__5 = CoreMatchers.notNullValue();
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23845__6 = Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_1574);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23845_add24042_mg26164 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 15: templates/-.&qH`[ =I*Fipqw>8)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23845_add24045_mg26181_failAssert710() throws Exception {
        try {
            String __DSPOT_resourceName_1591 = "[i%bsN5/#N:zAdz`?X+e";
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23845__5 = CoreMatchers.notNullValue();
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23845_add24045__8 = CoreMatchers.notNullValue();
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23845__6 = Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_1591);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23845_add24045_mg26181 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/[i%bsN5/#N:zAdz`?X+e", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23843_rv24098litString24852_failAssert703() throws Exception {
        try {
            long __DSPOT_arg0_1567 = 331251973L;
            ClasspathResolver underTest = new ClasspathResolver("\n");
            Reader __DSPOT_invoc_3 = underTest.getReader("absolute_partials_template.html");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23843__6 = Is.is(CoreMatchers.notNullValue());
            long o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23843_rv24098__13 = __DSPOT_invoc_3.skip(__DSPOT_arg0_1567);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add23843_rv24098litString24852 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: \n/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePathlitString7602_failAssert204() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("MF%&a%r^,B-LGF}>oXfZQ)#lU1E7.ppZ");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePathlitString7602 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Malformed escape pair at index 12: templates/MF%&a%r^,B-LGF}>oXfZQ)#lU1E7.ppZ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePathlitString7594_failAssert205() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("R?&,0.F%Q");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePathlitString7594 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Malformed escape pair at index 7: R?&,0.F%Q/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_add7606litString7717_failAssert248() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            underTest.getReader("/absolute_partials_t>emplate.html");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_add7606__6 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePath_add7606litString7717 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 29: templates/absolute_partials_t>emplate.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_add7606litString7719_failAssert253() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            underTest.getReader("$9[7L1G=%Cl1t^iP.v=JJv#]Or;=b$]&");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_add7606__6 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePath_add7606litString7719 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 12: templates/$9[7L1G=%Cl1t^iP.v=JJv#]Or;=b$]&", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_add7608_add7804litString8746_failAssert294() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver(":");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_add7608_add7804__5 = CoreMatchers.notNullValue();
            Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_add7608__5 = CoreMatchers.notNullValue();
            Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_add7608__6 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePath_add7608_add7804litString8746 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Expected scheme name at index 0: :/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_add7606_rv7870litString8463_failAssert293() throws Exception {
        try {
            long __DSPOT_arg0_566 = 2118914411L;
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader __DSPOT_invoc_3 = underTest.getReader("/absolute_partials_template.html");
            Reader reader = underTest.getReader("m67_uZ>BTwODW,v;(UHQX8z!=:5Qv[wG");
            Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_add7606__6 = Is.is(CoreMatchers.notNullValue());
            long o_getReaderWithRootAndResourceHasAbsolutePath_add7606_rv7870__13 = __DSPOT_invoc_3.skip(__DSPOT_arg0_566);
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePath_add7606_rv7870litString8463 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 16: templates/m67_uZ>BTwODW,v;(UHQX8z!=:5Qv[wG", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString20470_failAssert551() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("\n");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString20470 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: \n/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg20483_failAssert554() throws Exception {
        try {
            String __DSPOT_resourceName_1327 = "ohipAx]EaQ7$TI-1Q8Hb";
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_1327);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg20483 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 16: templates/ohipAx]EaQ7$TI-1Q8Hb", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString20473_failAssert552() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("/`bsolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString20473 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/`bsolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20481litString20567_failAssert575() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("/absolute_partials_]template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20481__5 = Is.is(CoreMatchers.notNullValue());
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20481__7 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20481litString20567 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 28: templates/absolute_partials_]template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20481litString20563_failAssert581() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("\n");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20481__5 = Is.is(CoreMatchers.notNullValue());
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20481__7 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20481litString20563 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: \n/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480_rv20705() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader __DSPOT_invoc_3 = underTest.getReader("/absolute_partials_template.html");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480__6 = Is.is(CoreMatchers.notNullValue());
        int o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480_rv20705__12 = __DSPOT_invoc_3.read();
        Assert.assertEquals(123, ((int) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480_rv20705__12)));
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg20483_failAssert554litString20621() throws Exception {
        try {
            String __DSPOT_resourceName_1327 = "ohipAx]EaQ7$TI-1Q8Hb";
            ClasspathResolver underTest = new ClasspathResolver("absolute_partials_template.html");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg20483_failAssert554litString20621__8 = Is.is(CoreMatchers.notNullValue());
            Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg20483_failAssert554litString20621__8)).toString());
            underTest.getReader(__DSPOT_resourceName_1327);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg20483 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480litString20587_failAssert578() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            underTest.getReader("\n");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480__6 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480litString20587 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/\n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20482_add20642_mg22765_failAssert608() throws Exception {
        try {
            String __DSPOT_resourceName_1380 = "/|c*GY:sycin>y6_nKI*";
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20482_add20642__5 = CoreMatchers.notNullValue();
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20482__5 = CoreMatchers.notNullValue();
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20482__6 = Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_1380);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20482_add20642_mg22765 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/|c*GY:sycin>y6_nKI*", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480_add20659_rv22842() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader __DSPOT_invoc_3 = underTest.getReader("/absolute_partials_template.html");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480_add20659__6 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480__6 = Is.is(CoreMatchers.notNullValue());
        int o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480_add20659_rv22842__15 = __DSPOT_invoc_3.read();
        Assert.assertEquals(123, ((int) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480_add20659_rv22842__15)));
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480_rv20709litString21305_failAssert607() throws Exception {
        try {
            long __DSPOT_arg0_1341 = 1447449182L;
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader __DSPOT_invoc_3 = underTest.getReader("\n");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480__6 = Is.is(CoreMatchers.notNullValue());
            long o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480_rv20709__13 = __DSPOT_invoc_3.skip(__DSPOT_arg0_1341);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20480_rv20709litString21305 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/\n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20482_add20642litString21895_failAssert632() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("]$4&Jf%Lxz");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20482_add20642__5 = CoreMatchers.notNullValue();
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20482__5 = CoreMatchers.notNullValue();
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20482__6 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20482_add20642litString21895 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: ]$4&Jf%Lxz/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndNullResourcelitString5747_failAssert168() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            underTest.getReader("\n");
            org.junit.Assert.fail("getReaderWithRootAndNullResourcelitString5747 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/\n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndNullResourcelitString5737_failAssert176litString5922_failAssert196() throws Exception {
        try {
            try {
                ClasspathResolver underTest = new ClasspathResolver("nested_partials_template.html");
                underTest.getReader("\n");
                org.junit.Assert.fail("getReaderWithRootAndNullResourcelitString5737 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("getReaderWithRootAndNullResourcelitString5737_failAssert176litString5922 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Illegal character in path at index 30: nested_partials_template.html/\n", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDoubleDotRelativePathlitString14086_failAssert393() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("\n");
            Reader reader = underTest.getReader("absolute/../absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDoubleDotRelativePathlitString14086 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: \n/absolute/../absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDoubleDotRelativePath_add14096litString14180_failAssert422() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver(":");
            underTest.getReader("absolute/../absolute_partials_template.html");
            Reader reader = underTest.getReader("absolute/../absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasDoubleDotRelativePath_add14096__6 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDoubleDotRelativePath_add14096litString14180 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Expected scheme name at index 0: :/absolute/../absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDoubleDotRelativePath_mg14099_failAssert395litString14248_add16065() throws Exception {
        try {
            String __DSPOT_resourceName_938 = "<lL2 qFGM(`F>11g@_XW";
            ClasspathResolver underTest = new ClasspathResolver("tmplates");
            Reader reader = underTest.getReader("absolute/../absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasDoubleDotRelativePath_mg14099_failAssert395litString14248_add16065__8 = Is.is(CoreMatchers.notNullValue());
            Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasDoubleDotRelativePath_mg14099_failAssert395litString14248_add16065__8)).toString());
            Matcher<Object> o_getReaderWithRootAndResourceHasDoubleDotRelativePath_mg14099_failAssert395litString14248__8 = Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_938);
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDoubleDotRelativePath_mg14099 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDotRelativePathlitString10666_failAssert300() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("temp{ates");
            Reader reader = underTest.getReader("absolute/./nested_partials_sub.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDotRelativePathlitString10666 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 4: temp{ates/absolute/./nested_partials_sub.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDotRelativePath_add10682litString10772_failAssert335() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver(":");
            Reader reader = underTest.getReader("absolute/./nested_partials_sub.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasDotRelativePath_add10682__5 = Is.is(CoreMatchers.notNullValue());
            Matcher<Object> o_getReaderWithRootAndResourceHasDotRelativePath_add10682__7 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDotRelativePath_add10682litString10772 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Expected scheme name at index 0: :/absolute/./nested_partials_sub.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDotRelativePath_add10683_add10871litString12092_failAssert353() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("1oRp2=`!G");
            Reader reader = underTest.getReader("absolute/./nested_partials_sub.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasDotRelativePath_add10683_add10871__5 = CoreMatchers.notNullValue();
            Matcher<Object> o_getReaderWithRootAndResourceHasDotRelativePath_add10683__5 = CoreMatchers.notNullValue();
            Matcher<Object> o_getReaderWithRootAndResourceHasDotRelativePath_add10683__6 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDotRelativePath_add10683_add10871litString12092 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 6: 1oRp2=`!G/absolute/./nested_partials_sub.html", expected.getMessage());
        }
    }
}

