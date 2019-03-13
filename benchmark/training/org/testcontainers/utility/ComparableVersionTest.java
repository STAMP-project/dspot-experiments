package org.testcontainers.utility;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ComparableVersionTest {
    private final int[] expected;

    private final String given;

    public ComparableVersionTest(final String given, final int[] expected) {
        this.given = given;
        this.expected = expected;
    }

    @Test
    public void shouldParseVersions() {
        Assert.assertArrayEquals(expected, ComparableVersion.parseVersion(given));
    }
}

