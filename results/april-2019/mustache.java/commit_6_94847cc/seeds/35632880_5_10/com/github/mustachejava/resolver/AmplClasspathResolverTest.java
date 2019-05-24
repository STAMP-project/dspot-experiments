package com.github.mustachejava.resolver;


import java.io.Reader;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class AmplClasspathResolverTest {
    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString30() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("113Hq{+Up!E%sce!v-^e>#*zWJt&1");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString30__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString30__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString28() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString28__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString28__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString29() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("nested_partials_template<.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString29__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString29__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString302() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("n*Dy04u{C");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString302__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString302__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString304() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("temp`lates");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString304__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString304__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString305() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString305__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString305__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString307() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString307__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString307__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString311() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("F&zpy0jE>gu63|W[N[^_{L@eZ)EP{rH");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString311__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString311__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString184() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("|#wH@+Nrw_zZnl7!-[ahXE(<b>z.2cW");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString184__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString184__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString182() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString182__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString182__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString181() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("absolute_parti{ls_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString181__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString181__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString178() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("C^O5*paM^z");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString178__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString178__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString175() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString175__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString175__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString240() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString240__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString240__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString241() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("5*/Cu#Y{L");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString241__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString241__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString244() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString244__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString244__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString248() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("hzeN7Hy|)b%6Jja[SWPpG:go_i=!_gXI");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString248__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString248__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString107() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("template<s/");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString107__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString107__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString105() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("T(yD<t(k-[");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString105__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString105__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString106() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString106__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString106__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString112() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString112__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString112__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString113() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("/a[solute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString113__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString113__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString114() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("g=.m;q [c!iRb#k&]E2eksZ[A:|NayWx");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString114__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString114__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString67() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString67__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString67__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString69() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("a?I$i^vikH<|8FRT_#6)/lr)v/T3E=");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString69__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString69__5)).toString());
        Assert.assertNull(reader);
    }
}

