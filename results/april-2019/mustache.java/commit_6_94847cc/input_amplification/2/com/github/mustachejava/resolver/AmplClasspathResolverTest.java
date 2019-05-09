package com.github.mustachejava.resolver;


import java.io.Reader;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


public class AmplClasspathResolverTest {
    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString190_add326() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("AW=]F(P!?]z@6()w}j4W@`J6 DxO8");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190_add326__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190_add326__5)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190_add326__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_add194_literalMutationString241() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_add194_literalMutationString241__3 = underTest.getReader("nes[ted_partials_template.html");
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_add194_literalMutationString241__3);
        Reader reader = underTest.getReader("nested_partials_template.html");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_add194__6 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_add194_literalMutationString241__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_add194_literalMutationString240() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_add194_literalMutationString240__3 = underTest.getReader("^7l(];lR`7KE$_qA_[YZ&!wa2.Zf9");
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_add194_literalMutationString240__3);
        Reader reader = underTest.getReader("nested_partials_template.html");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_add194__6 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_add194_literalMutationString240__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString190_add324() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190_add324__3 = underTest.getReader("AW=]F(P!?]z@6()w}j4W@`J6 DxO8");
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190_add324__3);
        Reader reader = underTest.getReader("AW=]F(P!?]z@6()w}j4W@`J6 DxO8");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190_add324__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString190_add325() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("AW=]F(P!?]z@6()w}j4W@`J6 DxO8");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190_add325__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190_add325__5)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190_add325__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString190() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("AW=]F(P!?]z@6()w}j4W@`J6 DxO8");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString190__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString189() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString189_add312() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189_add312__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189_add312__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189_add312__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString189_add313() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189_add313__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189_add313__5)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189_add313__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString189_add314() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189_add314__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189_add314__5)).toString());
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString189_add314__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString2944_add3237() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("Y)j7EzyWr<f<[#GenzF?AU@Ngr7g>(*");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2944_add3237__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2944_add3237__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2944__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2944_add3237__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString2936_add3212() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("u}hjuL?AX");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2936_add3212__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2936_add3212__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2936__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2936_add3212__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString2936_add3211() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("u}hjuL?AX");
        Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2936_add3211__3 = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2936_add3211__3);
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2936__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2936_add3211__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString2937() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString2936() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("u}hjuL?AX");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2936__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2936__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString2941() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2941__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2941__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString2937_add3249() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937_add3249__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937_add3249__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937_add3249__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString2937_add3248() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937_add3248__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937_add3248__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937_add3248__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString2937_add3247() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937_add3247__3 = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937_add3247__3);
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2937_add3247__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString2944() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("Y)j7EzyWr<f<[#GenzF?AU@Ngr7g>(*");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2944__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2944__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString2941_add3207() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2941_add3207__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2941_add3207__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2941__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2941_add3207__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString2941_add3208() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2941_add3208__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2941_add3208__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2941__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString2941_add3208__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1701() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1701__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1701__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1703_add1969() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("PR]/s:D6HD");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1703_add1969__3 = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1703_add1969__3);
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1703__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1703_add1969__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1708() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1708__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1708__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("z]UuMezDA_g;Jj;+$ f|.5,M!d_.6]O");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1703() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("PR]/s:D6HD");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1703__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1703__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1708_add1986() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1708_add1986__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1708_add1986__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1708__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1708_add1986__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1708_add1985() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1708_add1985__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1708_add1985__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1708__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1708_add1985__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1703_add1971() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("PR]/s:D6HD");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1703_add1971__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1703_add1971__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1703__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1703_add1971__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1701_add1997() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1701_add1997__3 = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1701_add1997__3);
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1701__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1701_add1997__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1701_add1998() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1701_add1998__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1701_add1998__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1701__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1701_add1998__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707_add2009() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707_add2009__3 = underTest.getReader("z]UuMezDA_g;Jj;+$ f|.5,M!d_.6]O");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707_add2009__3);
        Reader reader = underTest.getReader("z]UuMezDA_g;Jj;+$ f|.5,M!d_.6]O");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707_add2009__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707_add2011() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("z]UuMezDA_g;Jj;+$ f|.5,M!d_.6]O");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707_add2011__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707_add2011__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707_add2011__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707_add2010() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("z]UuMezDA_g;Jj;+$ f|.5,M!d_.6]O");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707_add2010__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707_add2010__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString1707_add2010__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("59`4]5J]sK5z..T m2vX#C/:}B9L?i_v");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325_add2602() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325_add2602__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325_add2602__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325_add2602__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325_add2604() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325_add2604__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325_add2604__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325_add2604__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325_add2603() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325_add2603__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325_add2603__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2325_add2603__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2320() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2320__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2320__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2322() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("H:gSSLq?k");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2322__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2322__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2320_add2616() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2320_add2616__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2320_add2616__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2320__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2320_add2616__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2320_add2614() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2320_add2614__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2320_add2614__3);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2320__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2320_add2614__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328_add2596() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("59`4]5J]sK5z..T m2vX#C/:}B9L?i_v");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328_add2596__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328_add2596__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328_add2596__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328_add2594() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328_add2594__3 = underTest.getReader("59`4]5J]sK5z..T m2vX#C/:}B9L?i_v");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328_add2594__3);
        Reader reader = underTest.getReader("59`4]5J]sK5z..T m2vX#C/:}B9L?i_v");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328_add2594__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328_add2595() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("59`4]5J]sK5z..T m2vX#C/:}B9L?i_v");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328_add2595__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328_add2595__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2328_add2595__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2322_add2620() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("H:gSSLq?k");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2322_add2620__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2322_add2620__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2322__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString2322_add2620__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1070_add1380() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1070_add1380__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1070_add1380__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1070__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1070_add1380__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1070_add1379() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1070_add1379__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1070_add1379__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1070__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1070_add1379__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1078_add1360() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1078_add1360__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1078_add1360__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1078__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1078_add1360__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1078_add1359() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1078_add1359__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1078_add1359__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1078__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1078_add1359__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1080_add1346() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1080_add1346__3 = underTest.getReader("#]-3b7Dp-^*OXou}Z}48wE`3hN(mKO&s");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1080_add1346__3);
        Reader reader = underTest.getReader("#]-3b7Dp-^*OXou}Z}48wE`3hN(mKO&s");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1080__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1080_add1346__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1078() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1078__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1078__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1080() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("#]-3b7Dp-^*OXou}Z}48wE`3hN(mKO&s");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1080__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1080__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1070() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1070__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1070__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634_add751() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("#pl?(hX%}8E5OoU5XXrB^/=HMJ(?nz");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634_add751__5 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634_add751__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634_add751__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634_add750() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("#pl?(hX%}8E5OoU5XXrB^/=HMJ(?nz");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634_add750__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634_add750__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634_add750__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634_add749() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634_add749__3 = underTest.getReader("#pl?(hX%}8E5OoU5XXrB^/=HMJ(?nz");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634_add749__3);
        Reader reader = underTest.getReader("#pl?(hX%}8E5OoU5XXrB^/=HMJ(?nz");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634_add749__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630_add745() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630_add745__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630_add745__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630_add745__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630_add746() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630_add746__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630_add746__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630_add746__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630_add747() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630_add747__5 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630_add747__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630_add747__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("#pl?(hX%}8E5OoU5XXrB^/=HMJ(?nz");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString634__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString630__5)).toString());
        Assert.assertNull(reader);
    }
}

