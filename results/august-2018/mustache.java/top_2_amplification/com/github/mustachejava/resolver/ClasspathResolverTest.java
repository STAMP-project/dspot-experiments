package com.github.mustachejava.resolver;


import java.io.Reader;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class ClasspathResolverTest {
    @Test
    public void getReaderNullRootAndResourceHasRelativePath() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("nested_partials_template.html");
        Assert.assertThat(reader, Is.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void getReaderWithRootAndResourceHasRelativePath() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertThat(reader, Is.is(CoreMatchers.notNullValue()));
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_mg17901_failAssert239() throws Exception {
        try {
            String __DSPOT_resourceName_1825 = "B;^9|HPsQwsV7EE+ju|P";
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_1825);
            org.junit.Assert.fail("getReaderWithRootAndResourceHasRelativePath_mg17901 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 12: templates/B;^9|HPsQwsV7EE+ju|P", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_add17899litString17978_failAssert248() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver(":");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_add17899__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasRelativePath_add17899litString17978 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Expected scheme name at index 0: :/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertThat(reader, Is.is(CoreMatchers.notNullValue()));
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePathlitString22590_failAssert283() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("TjYIC1jBY]");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePathlitString22590 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 9: TjYIC1jBY]/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePathlitString22598_failAssert286() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("$t9IYS!0#p:d=f3ok!ywx1[? 8wik[Z");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePathlitString22598 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in fragment at index 34: templates/$t9IYS!0#p:d=f3ok!ywx1[? 8wik[Z", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add22602litString22701_failAssert295() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("{(#`PF8D[-6H(+fibcev<N ?ISH[;AK");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add22602__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add22602litString22701 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/{(#`PF8D[-6H(+fibcev<N ?ISH[;AK", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add22603litString22690_failAssert299() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("C]7WV0 NDf");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add22603__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add22603litString22690 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 1: C]7WV0 NDf/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add22604litString22670_failAssert305litString23432_failAssert314() throws Exception {
        try {
            try {
                ClasspathResolver underTest = new ClasspathResolver("tem:lates/");
                Reader reader = underTest.getReader("R3ZP(:eFe:.7CA1j*X`B^#;u_mMhfw&");
                Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add22604__5 = Is.is(CoreMatchers.notNullValue());
                org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add22604litString22670 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_add22604litString22670_failAssert305litString23432 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Illegal character in opaque part at index 28: tem:lates/R3ZP(:eFe:.7CA1j*X`B^#;u_mMhfw&", expected_1.getMessage());
        }
    }

    @Test
    public void getReaderWithRootAndResourceHasAbsolutePath() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertThat(reader, Is.is(CoreMatchers.notNullValue()));
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePathlitString10760_failAssert156() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("m=IDd#]DEJgRN$t8KZ2_4#ctmefc (Bi");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePathlitString10760 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in fragment at index 31: templates/m=IDd#]DEJgRN$t8KZ2_4#ctmefc (Bi", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePathlitString10752_failAssert153() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("DQ#[^oJhj");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePathlitString10752 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in fragment at index 4: DQ#[^oJhj/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePathlitString10748litString10841_failAssert159() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("Z7_#({H[&h2[9f*<q`qaDpW?6[Ew:[O#");
            Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePathlitString10748__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePathlitString10748litString10841 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in fragment at index 15: templates/Z7_#({H[&h2[9f*<q`qaDpW?6[Ew:[O#", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_add10766litString10917_failAssert177() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader(".T/Ag-uM;$U!(sd:kbrJl^(0o[6H4wQ*");
            Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_add10766__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePath_add10766litString10917 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 31: templates/.T/Ag-uM;$U!(sd:kbrJl^(0o[6H4wQ*", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_add10766litString10906_failAssert176() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver(":");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_add10766__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePath_add10766litString10906 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Expected scheme name at index 0: :/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertThat(reader, Is.is(CoreMatchers.notNullValue()));
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString20283_failAssert259() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("\n");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString20283 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: \n/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg20296_failAssert263() throws Exception {
        try {
            String __DSPOT_resourceName_1972 = "v}Yud=f$ott FH0>D43#";
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_1972);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg20296 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 11: templates/v}Yud=f$ott FH0>D43#", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg20296_failAssert263litString20445() throws Exception {
        try {
            String __DSPOT_resourceName_1972 = "v}Yud=f$ott FH0>D43#";
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg20296_failAssert263litString20445__8 = Is.is(CoreMatchers.notNullValue());
            Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg20296_failAssert263litString20445__8)).toString());
            underTest.getReader(__DSPOT_resourceName_1972);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg20296 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20295litString20366_failAssert277() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("FlV]o|xDn5");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20295__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20295litString20366 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 3: FlV]o|xDn5/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20294litString20419_failAssert275() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("\n");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20294__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_add20294litString20419 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/\n", expected.getMessage());
        }
    }

    @Test
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("/nested_partials_template.html");
        Assert.assertThat(reader, Is.is(CoreMatchers.nullValue()));
    }

    @Test(expected = NullPointerException.class)
    public void getReaderWithRootAndNullResource() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        underTest.getReader(null);
    }

    @Test(expected = NullPointerException.class)
    public void getReaderNullRootAndNullResourceThrowsNullPointer() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        underTest.getReader(null);
    }

    @Test
    public void getReaderWithRootAndResourceHasDoubleDotRelativePath() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("absolute/../absolute_partials_template.html");
        Assert.assertThat(reader, Is.is(CoreMatchers.notNullValue()));
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDoubleDotRelativePathlitString15527_failAssert207() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("\n");
            Reader reader = underTest.getReader("absolute/../absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDoubleDotRelativePathlitString15527 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: \n/absolute/../absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDoubleDotRelativePath_add15538litString15688_failAssert221() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("dsU_W`2,=");
            Reader reader = underTest.getReader("absolute/../absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasDoubleDotRelativePath_add15538__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDoubleDotRelativePath_add15538litString15688 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 5: dsU_W`2,=/absolute/../absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test
    public void getReaderWithRootAndResourceHasDotRelativePath() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("absolute/./nested_partials_sub.html");
        Assert.assertThat(reader, Is.is(CoreMatchers.notNullValue()));
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDotRelativePath_mg13188_failAssert184() throws Exception {
        try {
            String __DSPOT_resourceName_1527 = "|<cIr! %)94(c4bz!$W7";
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("absolute/./nested_partials_sub.html");
            Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_1527);
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDotRelativePath_mg13188 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/|<cIr! %)94(c4bz!$W7", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDotRelativePath_add13185litString13263_failAssert189() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver(":");
            Reader reader = underTest.getReader("absolute/./nested_partials_sub.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasDotRelativePath_add13185__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDotRelativePath_add13185litString13263 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Expected scheme name at index 0: :/absolute/./nested_partials_sub.html", expected.getMessage());
        }
    }
}

