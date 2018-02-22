package com.github.mustachejava.resolver;


import org.junit.Test;


public class AmplClasspathResolverTest {
    @Test(timeout = 10000)
    public void getReaderWithRootAndNullResource_failAssert1() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ClasspathResolver underTest = new ClasspathResolver("templates");
            underTest.getReader(null);
            org.junit.Assert.fail("getReaderWithRootAndNullResource should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }
}

