package com.baeldung.nativekeyword;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DateTimeUtilsManualTest {
    private static final Logger LOG = LoggerFactory.getLogger(DateTimeUtilsManualTest.class);

    @Test
    public void givenNativeLibsLoaded_thenNativeMethodIsAccessible() {
        DateTimeUtils dateTimeUtils = new DateTimeUtils();
        DateTimeUtilsManualTest.LOG.info(("System time is : " + (dateTimeUtils.getSystemTime())));
        Assert.assertNotNull(dateTimeUtils.getSystemTime());
    }
}

