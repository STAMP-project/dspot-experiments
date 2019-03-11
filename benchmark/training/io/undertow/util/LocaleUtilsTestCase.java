package io.undertow.util;


import io.undertow.testutils.category.UnitTest;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(UnitTest.class)
public class LocaleUtilsTestCase {
    @Test
    public void testGetLocaleFromInvalidString() throws Exception {
        Assert.assertEquals(LocaleUtils.getLocaleFromString("-"), new Locale(""));
    }
}

