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
    public void getReaderWithRootAndResourceHasRelativePathlitString25909_failAssert256() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("$[!:DItZ2");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasRelativePathlitString25909 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in scheme name at index 0: $[!:DItZ2/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_mg25924_failAssert261litString26121() throws Exception {
        try {
            String __DSPOT_resourceName_2184 = "z{@jsfXk>0ll2^YVKmK ";
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_mg25924_failAssert261litString26121__8 = Is.is(CoreMatchers.notNullValue());
            Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_mg25924_failAssert261litString26121__8)).toString());
            underTest.getReader(__DSPOT_resourceName_2184);
            org.junit.Assert.fail("getReaderWithRootAndResourceHasRelativePath_mg25924 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePathnull25930litString26044_failAssert283() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("\n");
            Matcher<Object> o_getReaderWithRootAndResourceHasRelativePathnull25930__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasRelativePathnull25930litString26044 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/\n", expected.getMessage());
        }
    }

    @Test
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertThat(reader, Is.is(CoreMatchers.notNullValue()));
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePathlitString32411_failAssert337() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("{ YRgre{!1");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePathlitString32411 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: { YRgre{!1/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_mg32426_failAssert342() throws Exception {
        try {
            String __DSPOT_resourceName_2483 = "p:>Sc^kiyW4UMe[sb5(;";
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_2483);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_mg32426 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 12: templates/p:>Sc^kiyW4UMe[sb5(;", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_mg32426_failAssert342_add32648() throws Exception {
        try {
            String __DSPOT_resourceName_2483 = "p:>Sc^kiyW4UMe[sb5(;";
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_mg32426_failAssert342_add32648__8 = Is.is(CoreMatchers.notNullValue());
            Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_mg32426_failAssert342_add32648__8)).toString());
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_mg32426_failAssert342_add32648__10 = Is.is(CoreMatchers.notNullValue());
            Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_mg32426_failAssert342_add32648__10)).toString());
            underTest.getReader(__DSPOT_resourceName_2483);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_mg32426 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePathlitString32415_mg32729_failAssert351() throws Exception {
        try {
            String __DSPOT_resourceName_2492 = "^v!E=&;ui45MYLi-YkPf";
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePathlitString32415__5 = Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_2492);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePathlitString32415_mg32729 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/^v!E=&;ui45MYLi-YkPf", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_mg32426_failAssert342litString32555() throws Exception {
        try {
            String __DSPOT_resourceName_2483 = "p:>Sc^kiyW4UMe[sb5(;";
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_mg32426_failAssert342litString32555__8 = Is.is(CoreMatchers.notNullValue());
            Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_mg32426_failAssert342litString32555__8)).toString());
            underTest.getReader(__DSPOT_resourceName_2483);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_mg32426 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void getReaderWithRootAndResourceHasAbsolutePath() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertThat(reader, Is.is(CoreMatchers.notNullValue()));
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePathlitString15848_failAssert140() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("tem plates");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePathlitString15848 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 3: tem plates/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePathlitString15858_failAssert144() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("wxqG[RqJK|<cIr! %)94(c4bz!$W7bX;");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePathlitString15858 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 14: templates/wxqG[RqJK|<cIr! %)94(c4bz!$W7bX;", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_add15864litString16194_failAssert167() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("\n");
            Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_add15864__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasAbsolutePath_add15864litString16194 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/\n", expected.getMessage());
        }
    }

    @Test
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertThat(reader, Is.is(CoreMatchers.notNullValue()));
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString29652_failAssert294() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("!{]{q!U;*K");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString29652 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 1: !{]{q!U;*K/absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString29658_failAssert297() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("/absolute_pa>rtials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString29658 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 21: templates/absolute_pa>rtials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString29662_failAssert299() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("\n");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString29662 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/\n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg29667_rv29999_failAssert302() throws Exception {
        try {
            char[] __DSPOT_arg0_2388 = new char[]{ 'f', 'N', '2', '8' };
            String __DSPOT_resourceName_2379 = "IOS5(cS638ZuPfM7o,qq";
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg29667__6 = Is.is(CoreMatchers.notNullValue());
            Reader __DSPOT_invoc_13 = underTest.getReader(__DSPOT_resourceName_2379);
            __DSPOT_invoc_13.read(__DSPOT_arg0_2388);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg29667_rv29999 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString29648_mg30140_failAssert310() throws Exception {
        try {
            String __DSPOT_resourceName_2396 = "!nJ)OQvx|W7(]H5<$0Fj";
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString29648__5 = Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_2396);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePathlitString29648_mg30140 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 18: templates/!nJ)OQvx|W7(]H5<$0Fj", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg29667litString29772_failAssert324() throws Exception {
        try {
            String __DSPOT_resourceName_2379 = "\n";
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg29667__6 = Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_2379);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg29667litString29772 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/\n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg29667litString29786_failAssert311() throws Exception {
        try {
            String __DSPOT_resourceName_2379 = "IOS5(cS638ZuPfM7o,qq";
            ClasspathResolver underTest = new ClasspathResolver("`s,((;u-z+");
            Reader reader = underTest.getReader("/absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg29667__6 = Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_2379);
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_mg29667litString29786 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: `s,((;u-z+/absolute_partials_template.html", expected.getMessage());
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
    public void getReaderWithRootAndResourceHasDoubleDotRelativePathlitString22224_failAssert221() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("\n");
            Reader reader = underTest.getReader("absolute/../absolute_partials_template.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDoubleDotRelativePathlitString22224 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: \n/absolute/../absolute_partials_template.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDoubleDotRelativePath_mg22237_failAssert225_rv22559() throws Exception {
        try {
            String __DSPOT_resourceName_1990 = "Ek5X;&. glTk7XY]h:+#";
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("absolute/../absolute_partials_template.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasDoubleDotRelativePath_mg22237_failAssert225_rv22559__8 = Is.is(CoreMatchers.notNullValue());
            Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasDoubleDotRelativePath_mg22237_failAssert225_rv22559__8)).toString());
            Reader __DSPOT_invoc_10 = underTest.getReader(__DSPOT_resourceName_1990);
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDoubleDotRelativePath_mg22237 should have thrown IllegalArgumentException");
            __DSPOT_invoc_10.markSupported();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void getReaderWithRootAndResourceHasDotRelativePath() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("absolute/./nested_partials_sub.html");
        Assert.assertThat(reader, Is.is(CoreMatchers.notNullValue()));
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDotRelativePathlitString19452_failAssert176() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("\n");
            Reader reader = underTest.getReader("absolute/./nested_partials_sub.html");
            Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDotRelativePathlitString19452 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 0: \n/absolute/./nested_partials_sub.html", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDotRelativePath_add19463litString19796_failAssert214() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("\n");
            Matcher<Object> o_getReaderWithRootAndResourceHasDotRelativePath_add19463__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDotRelativePath_add19463litString19796 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Illegal character in path at index 10: templates/\n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasDotRelativePath_mg19465litString19608_failAssert196() throws Exception {
        try {
            String __DSPOT_resourceName_1887 = "/)dZca%/?jMH6MmEQhqc";
            ClasspathResolver underTest = new ClasspathResolver("templates");
            Reader reader = underTest.getReader("absolute/./nested_partials_sub.html");
            Matcher<Object> o_getReaderWithRootAndResourceHasDotRelativePath_mg19465__6 = Is.is(CoreMatchers.notNullValue());
            underTest.getReader(__DSPOT_resourceName_1887);
            org.junit.Assert.fail("getReaderWithRootAndResourceHasDotRelativePath_mg19465litString19608 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Malformed escape pair at index 15: templates/)dZca%/?jMH6MmEQhqc", expected.getMessage());
        }
    }
}

