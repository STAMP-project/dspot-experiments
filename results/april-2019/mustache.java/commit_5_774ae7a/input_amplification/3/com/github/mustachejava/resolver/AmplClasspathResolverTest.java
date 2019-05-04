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
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("oWh9I^`%4u");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822_add3487() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822_add3487__3 = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822_add3487__3);
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822__3 = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Reader reader = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822_add3487__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822__3 = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822__3);
        Reader reader = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1834_add3401() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1834__3 = underTest.getReader("/absolute_partials_template.html");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1834_add3401__8 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1834_add3401__8)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1834_add3401__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1846null3773_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1846__3 = underTest.getReader(" does not exist");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1846null3773 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1847_add3463() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1847_add3463__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1847_add3463__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1847__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1847_add3463__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822_add3490() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822__3 = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Reader reader = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822_add3490__8 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822_add3490__8)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822_add3490__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1834() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1834__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1834__3);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1834__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824_add3591() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824_add3591__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824_add3591__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824_add3591__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824_add3594() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824__5 = CoreMatchers.notNullValue();
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824_add3594__8 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824_add3594__8)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1824_add3594__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1848() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1848__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1848__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1848__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823_add3493() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823_add3493__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823_add3493__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823_add3493__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1847() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1847__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1847__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1847__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823_add3492() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823_add3492__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823_add3492__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823__5 = Is.is(CoreMatchers.notNullValue());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1823_add3492__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1846() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1846__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1846__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1846__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822null3779_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates/");
            Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822__3 = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584_add1822null3779 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577_add1852() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("oWh9I^`%4u");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577_add1852__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577_add1852__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577_add1852__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577_add1850() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("oWh9I^`%4u");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577_add1850__3 = underTest.getReader("/absolute_partials_template.html");
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577_add1850__3);
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertNull(o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577_add1850__3);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1836() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1836__5 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1836__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1836__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577_add1851() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("oWh9I^`%4u");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577_add1851__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577_add1851__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1577_add1851__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1835() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver(" does not exist");
        Reader reader = underTest.getReader("/absolute_partials_template.html");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1835__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1835__5)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1835__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1834null3760_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver(" does not exist");
            Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1834__3 = underTest.getReader("/absolute_partials_template.html");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576__5 = Is.is(CoreMatchers.notNullValue());
            org.junit.Assert.fail("getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1576_add1834null3760 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader(" does not exist");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader reader = underTest.getReader("G{c?T@eU0B9?Ebu |6]B4 UGSx`mrxr;");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("is not null", ((Is) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1584__5)).toString());
        Assert.assertNull(reader);
    }

    @Test(timeout = 10000)
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1846_add3460() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver("templates/");
        Reader o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1846__3 = underTest.getReader(" does not exist");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1846_add3460__8 = CoreMatchers.notNullValue();
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1846_add3460__8)).toString());
        Matcher<Object> o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582__5 = Is.is(CoreMatchers.notNullValue());
        Assert.assertEquals("not null", ((IsNot) (o_getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath_literalMutationString1582_add1846_add3460__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115_add900() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115__5 = CoreMatchers.nullValue();
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115_add900__8 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115_add900__8)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115_add900__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add101_add817() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add101__3 = underTest.getReader("]Oep%M9%SOW]oeX_RI93O9Et#zU/6]");
        Reader reader = underTest.getReader("]Oep%M9%SOW]oeX_RI93O9Et#zU/6]");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add101_add817__8 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add101_add817__8)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add101_add817__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("]Oep%M9%SOW]oeX_RI93O9Et#zU/6]");
        Assert.assertNull(reader);
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5__5)).toString());
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
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add103_add821() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("]Oep%M9%SOW]oeX_RI93O9Et#zU/6]");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add103__5 = CoreMatchers.nullValue();
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add103_add821__8 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add103_add821__8)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add103_add821__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("]Oep%M9%SOW]oeX_RI93O9Et#zU/6]");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add103() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("]Oep%M9%SOW]oeX_RI93O9Et#zU/6]");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add103__5 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add103__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add103__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add101() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add101__3 = underTest.getReader("]Oep%M9%SOW]oeX_RI93O9Et#zU/6]");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add101__3);
        Reader reader = underTest.getReader("]Oep%M9%SOW]oeX_RI93O9Et#zU/6]");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add101__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113null1044_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver();
            Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113__3 = underTest.getReader(" does not exist");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5 = Is.is(CoreMatchers.nullValue());
            org.junit.Assert.fail("getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113null1044 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add101null1024_failAssert0() throws Exception {
        try {
            ClasspathResolver underTest = new ClasspathResolver();
            Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add101__3 = underTest.getReader("]Oep%M9%SOW]oeX_RI93O9Et#zU/6]");
            Reader reader = underTest.getReader(null);
            Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5__5 = Is.is(CoreMatchers.nullValue());
            org.junit.Assert.fail("getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add101null1024 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115__5 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add114() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add114__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add114__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add114__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113_add911() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113__3 = underTest.getReader(" does not exist");
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113_add911__6 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113_add911__6);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113_add911__6);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113_add913() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113__3 = underTest.getReader(" does not exist");
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113_add913__8 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113_add913__8)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add113_add913__8)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115_add897() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115_add897__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115_add897__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115__5 = CoreMatchers.nullValue();
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add115_add897__3);
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102_add826() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("]Oep%M9%SOW]oeX_RI93O9Et#zU/6]");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102_add826__5 = CoreMatchers.nullValue();
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102_add826__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102__5 = Is.is(CoreMatchers.nullValue());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("null", ((IsNull) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102_add826__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102_add825() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader reader = underTest.getReader("]Oep%M9%SOW]oeX_RI93O9Et#zU/6]");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102_add825__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102_add825__5)).toString());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102__5 = Is.is(CoreMatchers.nullValue());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertEquals("is null", ((Is) (o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString5_add102_add825__5)).toString());
    }

    @Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add114_add903() throws Exception {
        ClasspathResolver underTest = new ClasspathResolver();
        Reader o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add114_add903__3 = underTest.getReader(" does not exist");
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add114_add903__3);
        Reader reader = underTest.getReader(" does not exist");
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add114__5 = Is.is(CoreMatchers.nullValue());
        Matcher<Object> o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3__5 = Is.is(CoreMatchers.nullValue());
        Assert.assertNull(o_getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString3_add114_add903__3);
    }
}

