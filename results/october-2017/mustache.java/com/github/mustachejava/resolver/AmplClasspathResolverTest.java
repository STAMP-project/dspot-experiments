

package com.github.mustachejava.resolver;


public class AmplClasspathResolverTest {
    @org.junit.Test
    public void getReaderNullRootAndResourceHasRelativePath() throws java.lang.Exception {
        com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver();
        java.io.Reader reader = underTest.getReader("nested_partials_template.html");
        org.junit.Assert.assertThat(reader, org.hamcrest.core.Is.is(org.hamcrest.CoreMatchers.notNullValue()));
    }

    @org.junit.Test
    public void getReaderWithRootAndResourceHasRelativePath() throws java.lang.Exception {
        com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver("templates");
        java.io.Reader reader = underTest.getReader("absolute_partials_template.html");
        org.junit.Assert.assertThat(reader, org.hamcrest.core.Is.is(org.hamcrest.CoreMatchers.notNullValue()));
    }

    @org.junit.Test
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasRelativePath() throws java.lang.Exception {
        com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver("templates/");
        java.io.Reader reader = underTest.getReader("absolute_partials_template.html");
        org.junit.Assert.assertThat(reader, org.hamcrest.core.Is.is(org.hamcrest.CoreMatchers.notNullValue()));
    }

    @org.junit.Test
    public void getReaderWithRootAndResourceHasAbsolutePath() throws java.lang.Exception {
        com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver("templates");
        java.io.Reader reader = underTest.getReader("/absolute_partials_template.html");
        org.junit.Assert.assertThat(reader, org.hamcrest.core.Is.is(org.hamcrest.CoreMatchers.notNullValue()));
    }

    @org.junit.Test
    public void getReaderWithRootThatHasTrailingForwardSlashAndResourceHasAbsolutePath() throws java.lang.Exception {
        com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver("templates/");
        java.io.Reader reader = underTest.getReader("/absolute_partials_template.html");
        org.junit.Assert.assertThat(reader, org.hamcrest.core.Is.is(org.hamcrest.CoreMatchers.notNullValue()));
    }

    @org.junit.Test
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath() throws java.lang.Exception {
        com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver();
        java.io.Reader reader = underTest.getReader("/nested_partials_template.html");
        org.junit.Assert.assertThat(reader, org.hamcrest.core.Is.is(org.hamcrest.CoreMatchers.nullValue()));
    }

    @org.junit.Test(expected = java.lang.NullPointerException.class)
    public void getReaderWithRootAndNullResource() throws java.lang.Exception {
        com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver("templates");
        underTest.getReader(null);
    }

    @org.junit.Test(expected = java.lang.NullPointerException.class)
    public void getReaderNullRootAndNullResourceThrowsNullPointer() throws java.lang.Exception {
        com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver();
        underTest.getReader(null);
    }

    @org.junit.Test
    public void getReaderWithRootAndResourceHasDoubleDotRelativePath() throws java.lang.Exception {
        com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver("templates");
        java.io.Reader reader = underTest.getReader("absolute/../absolute_partials_template.html");
        org.junit.Assert.assertThat(reader, org.hamcrest.core.Is.is(org.hamcrest.CoreMatchers.notNullValue()));
    }

    @org.junit.Test
    public void getReaderWithRootAndResourceHasDotRelativePath() throws java.lang.Exception {
        com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver("templates");
        java.io.Reader reader = underTest.getReader("absolute/./nested_partials_sub.html");
        org.junit.Assert.assertThat(reader, org.hamcrest.core.Is.is(org.hamcrest.CoreMatchers.notNullValue()));
    }

    /* amplification of com.github.mustachejava.resolver.ClasspathResolverTest#getReaderNullRootAndNullResourceThrowsNullPointer */
    @org.junit.Test(timeout = 10000)
    public void getReaderNullRootAndNullResourceThrowsNullPointer_sd1_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_resourceName_0 = "-*k},GdhscbCS@!x*zH_";
            com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver();
            underTest.getReader(null);
            // StatementAdd: add invocation of a method
            underTest.getReader(__DSPOT_resourceName_0);
            org.junit.Assert.fail("getReaderNullRootAndNullResourceThrowsNullPointer_sd1 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.resolver.ClasspathResolverTest#getReaderNullRootDoesNotFindFileWithAbsolutePath */
    @org.junit.Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_sd234_failAssert2() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_resourceName_9 = ",y4JV)d4}^w[&oDAIOw?";
            com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver();
            java.io.Reader reader = underTest.getReader("/nested_partials_template.html");
            java.io.Reader Reader_6 = reader;
            org.hamcrest.core.Is.is(org.hamcrest.CoreMatchers.nullValue());
            // StatementAdd: add invocation of a method
            underTest.getReader(__DSPOT_resourceName_9);
            org.junit.Assert.fail("getReaderNullRootDoesNotFindFileWithAbsolutePath_sd234 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.github.mustachejava.resolver.ClasspathResolverTest#getReaderNullRootDoesNotFindFileWithAbsolutePath */
    @org.junit.Test(timeout = 10000)
    public void getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString230_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver();
            java.io.Reader reader = underTest.getReader("}7EGpwmm(EQndBdj-qEHp!#I]LDWP=");
            java.io.Reader Reader_2 = reader;
            org.hamcrest.core.Is.is(org.hamcrest.CoreMatchers.nullValue());
            org.junit.Assert.fail("getReaderNullRootDoesNotFindFileWithAbsolutePath_literalMutationString230 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.github.mustachejava.resolver.ClasspathResolverTest#getReaderWithRootAndNullResource */
    @org.junit.Test(timeout = 10000)
    public void getReaderWithRootAndNullResource_literalMutationString402_failAssert2() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.resolver.ClasspathResolver underTest = new com.github.mustachejava.resolver.ClasspathResolver("teplates");
            underTest.getReader(null);
            org.junit.Assert.fail("getReaderWithRootAndNullResource_literalMutationString402 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

