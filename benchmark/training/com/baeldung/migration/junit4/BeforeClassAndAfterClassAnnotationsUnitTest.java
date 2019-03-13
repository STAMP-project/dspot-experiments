package com.baeldung.migration.junit4;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(JUnit4.class)
public class BeforeClassAndAfterClassAnnotationsUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(BeforeClassAndAfterClassAnnotationsUnitTest.class);

    @Test
    public void simpleTest() {
        BeforeClassAndAfterClassAnnotationsUnitTest.LOG.info("simple test");
    }

    @Test
    public void anotherSimpleTest() {
        BeforeClassAndAfterClassAnnotationsUnitTest.LOG.info("another simple test");
    }
}

