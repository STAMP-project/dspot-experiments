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
        Reader reader = underTest.getReader("[KaS>u3?1!A.F+kvx#74#%K.nj*l9");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString28__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString28__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootAndResourceHasRelativePath_literalMutationString29() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootAndResourceHasRelativePath_literalMutationString29__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderNullRootAndResourceHasRelativePath_literalMutationString29__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString304() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString304__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString304__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString300() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("t[mplates");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString300__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString300__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString305() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("<PhH+!}#}");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString305__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString305__5)).toString());
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
    public void getReaderWithRootAndResourceHasRelativePath_literalMutationString307() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("8:Z(+51 i_+21VG94r7na}oml(06dGB");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasRelativePath_literalMutationString307__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasRelativePath_literalMutationString307__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString176() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString176__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString176__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString175() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("Otd];w#G0B");
        Reader reader = underTest.getReader("absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString175__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath_literalMutationString175__5)).toString());
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
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString240() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("#J*Pv)a8_");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString240__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString240__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString245() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("/absol[ute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString245__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString245__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString246() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString246__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString246__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString247() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("q{&.Mda@5g7r%>-o6t{H2hZtP*eS{abG");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString247__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString247__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString238() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString238__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString238__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString109() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("temp`lates/");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString109__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString109__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString108() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templ`tes/");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString108__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString108__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString113() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("P(fs7>NUOo2Quai:k2`}S(] .FAmSsAY");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString113__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString113__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString111() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString111__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString111__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString105() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString105__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString105__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString106() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("8s8m188>N1");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString106__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString106__5)).toString());
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
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString68() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("T_MOEx@NklY`]<@eTJLW:bN0]^GA*U");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString68__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString68__5)).toString());
        Assert.assertNull(reader);
    }
}

