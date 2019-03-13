package com.baeldung.migration.junit5;


import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(JUnitPlatform.class)
public class BeforeAllAndAfterAllAnnotationsUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(BeforeAllAndAfterAllAnnotationsUnitTest.class);

    @Test
    public void simpleTest() {
        BeforeAllAndAfterAllAnnotationsUnitTest.LOG.info("simple test");
    }

    @Test
    public void anotherSimpleTest() {
        BeforeAllAndAfterAllAnnotationsUnitTest.LOG.info("another simple test");
    }
}

