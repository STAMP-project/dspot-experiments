package com.baeldung.migration.junit4;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(JUnit4.class)
public class BeforeAndAfterAnnotationsUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(BeforeAndAfterAnnotationsUnitTest.class);

    private List<String> list;

    @Test
    public void whenCheckingListSize_ThenSizeEqualsToInit() {
        BeforeAndAfterAnnotationsUnitTest.LOG.info("executing test");
        Assert.assertEquals(2, list.size());
        list.add("another test");
    }

    @Test
    public void whenCheckingListSizeAgain_ThenSizeEqualsToInit() {
        BeforeAndAfterAnnotationsUnitTest.LOG.info("executing another test");
        Assert.assertEquals(2, list.size());
        list.add("yet another test");
    }
}

