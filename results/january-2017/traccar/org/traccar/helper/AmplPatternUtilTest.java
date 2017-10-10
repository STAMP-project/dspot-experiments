

package org.traccar.helper;


public class AmplPatternUtilTest {
    @org.junit.Test
    public void testCheckPattern() {
        org.junit.Assert.assertEquals("ab", org.traccar.helper.PatternUtil.checkPattern("abc", "abd").getPatternMatch());
    }
}

