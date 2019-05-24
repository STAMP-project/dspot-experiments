package com.github.mustachejava.resolver;


import java.io.Reader;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class AmplClasspathResolverTest {
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
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString27() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("PdG@a$IsaAs9%HB($^7r$?GzREmuc");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString27__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString27__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString301() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString301__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString301__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString304() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("tem{plates");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString304__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString304__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString311() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("9Bt^]hM*^kjpuZ2/k+%m &&m<)<$f!G");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString311__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString311__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString308() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString308__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString308__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString179() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("P.QQS3%w_?");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString179__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString179__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString177() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("template^s/");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString177__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString177__5)).toString());
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
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString184() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("Ee.{abcclvxmy`KtnN}{Y(<(6vaIWI4");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString184__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString184__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString181() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString181__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString181__5)).toString());
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
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString244() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("kAM_}UfYjD3ap@+?BK`S&}}BTUA7M(%3");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString244__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString244__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString245() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString245__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString245__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString239() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("sK=U+ `sf");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString239__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString239__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString107() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString107__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString107__5)).toString());
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
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString111() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("pKw-b7g(`HD#gCd>&%2:V {33Z%H>!9c");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString111__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString111__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString66() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("Uw]G!,5OB^]]jtl^;&?&:9yTL<h.g ");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString66__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString66__5)).toString());
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
}

