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
}

