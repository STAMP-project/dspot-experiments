package com.baeldung.migration.junit5;


import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(JUnitPlatform.class)
public class BeforeEachAndAfterEachAnnotationsUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(BeforeEachAndAfterEachAnnotationsUnitTest.class);

    private List<String> list;

    @Test
    public void whenCheckingListSize_ThenSizeEqualsToInit() {
        BeforeEachAndAfterEachAnnotationsUnitTest.LOG.info("executing test");
        Assert.assertEquals(2, list.size());
        list.add("another test");
    }

    @Test
    public void whenCheckingListSizeAgain_ThenSizeEqualsToInit() {
        BeforeEachAndAfterEachAnnotationsUnitTest.LOG.info("executing another test");
        Assert.assertEquals(2, list.size());
        list.add("yet another test");
    }
}

