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
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1252_add1542() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1252_add1542__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1252_add1542__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1252__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1252_add1542__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1252_add1541() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1252_add1541__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1252_add1541__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1252__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1252_add1541__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1246_add1525() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1246_add1525__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1246_add1525__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1246__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1246_add1525__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1246_add1526() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1246_add1526__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1246_add1526__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1246__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1246_add1526__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1251_add1548() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1251_add1548__3 = underTest.getReader("32`]$93`oFMKEt`TD+{c>y>p)Zgd!ucB");
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1251_add1548__3);
        Reader reader = underTest.getReader("32`]$93`oFMKEt`TD+{c>y>p)Zgd!ucB");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1251__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1251_add1548__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1251_add1549() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("32`]$93`oFMKEt`TD+{c>y>p)Zgd!ucB");
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1251_add1549__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1251_add1549__5)).toString());
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1251__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1251_add1549__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1246() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1246__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1246__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1251() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader("32`]$93`oFMKEt`TD+{c>y>p)Zgd!ucB");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1251__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1251__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1252() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1252__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootAndResourceHasAbsolutePath_literalMutationString1252__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("{%d8Uv.o7eHV/PNJZohDviF71-+!c5E?");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString635() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString635__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString635__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString635_add902() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString635_add902__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString635_add902__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString635__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString635_add902__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString635_add903() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString635_add903__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString635_add903__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString635__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString635_add903__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString629() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString629__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString629__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637_add918() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("{%d8Uv.o7eHV/PNJZohDviF71-+!c5E?");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637_add918__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637_add918__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637_add918__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637_add919() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("{%d8Uv.o7eHV/PNJZohDviF71-+!c5E?");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637_add919__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637_add919__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637_add919__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637_add917() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637_add917__3 = underTest.getReader("{%d8Uv.o7eHV/PNJZohDviF71-+!c5E?");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637_add917__3);
        Reader reader = underTest.getReader("{%d8Uv.o7eHV/PNJZohDviF71-+!c5E?");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString637_add917__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString629_add905() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString629_add905__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString629_add905__3);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString629__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString629_add905__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString629_add907() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString629_add907__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString629_add907__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString629__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString629_add907__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("019H}$M3_f@%J8-d;4]`6G_>-cOC%*");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add127() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add127__5 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add127__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add127__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add126() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add126__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add126__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add126__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add125() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add125__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add125__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add125__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4_add118() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("019H}$M3_f@%J8-d;4]`6G_>-cOC%*");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4_add118__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4_add118__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4_add118__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4_add119() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("019H}$M3_f@%J8-d;4]`6G_>-cOC%*");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4_add119__5 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4_add119__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4_add119__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4_add117() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4_add117__3 = underTest.getReader("019H}$M3_f@%J8-d;4]`6G_>-cOC%*");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4_add117__3);
        Reader reader = underTest.getReader("019H}$M3_f@%J8-d;4]`6G_>-cOC%*");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString4_add117__3);
    }
}

