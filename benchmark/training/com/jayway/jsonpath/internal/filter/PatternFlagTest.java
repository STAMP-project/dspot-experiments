package com.jayway.jsonpath.internal.filter;


import com.jayway.jsonpath.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class PatternFlagTest extends BaseTest {
    private final int flags;

    private final String expectedFlags;

    public PatternFlagTest(int flags, String expectedFlags) {
        this.flags = flags;
        this.expectedFlags = expectedFlags;
    }

    @Test
    public void testParseFlags() {
        Assert.assertEquals(expectedFlags, PatternFlag.parseFlags(flags));
    }
}

