/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ReadableDurationTest {
    private Integer value;

    private String expected;

    public ReadableDurationTest(String expected, Integer value) {
        this.value = value;
        this.expected = expected;
    }

    @Test
    public void test() {
        Assert.assertEquals(expected, new Report.ReadableDuration(value).getTime());
    }
}

