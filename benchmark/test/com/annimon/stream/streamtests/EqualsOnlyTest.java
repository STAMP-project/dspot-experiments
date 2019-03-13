package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public final class EqualsOnlyTest {
    @Test
    public void testEqualsOnly() {
        List<String> match = Stream.of("a", "b", "c").equalsOnly("b").toList();
        Assert.assertEquals(1, match.size());
        Assert.assertEquals("b", match.get(0));
    }
}

