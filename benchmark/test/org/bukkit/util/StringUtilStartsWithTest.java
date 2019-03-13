package org.bukkit.util;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class StringUtilStartsWithTest {
    @Parameterized.Parameter(0)
    public String base;

    @Parameterized.Parameter(1)
    public String prefix;

    @Parameterized.Parameter(2)
    public boolean result;

    @Test
    public void testFor() {
        Assert.assertThat((((((base) + " starts with ") + (prefix)) + ": ") + (result)), StringUtil.startsWithIgnoreCase(base, prefix), is(result));
    }
}

