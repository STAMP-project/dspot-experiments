package water.util;


import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for StringUtils
 */
public class StringUtilsTest {
    @Test
    public void testJoinArray() throws Exception {
        Assert.assertEquals("null,a,4,null", join(",", new String[]{ null, "a", "4", null }));
        Assert.assertEquals("", join("::", new String[]{  }));
        Assert.assertEquals("xxxxx", join("x", new String[]{ "x", "xx", "" }));
    }

    @Test
    public void testJoinCollection() throws Exception {
        Assert.assertEquals("null,a,4,null", join(",", Arrays.asList(null, "a", "4", null)));
        Assert.assertEquals("", join("::", Collections.EMPTY_SET));
        Assert.assertEquals("xxxxx", join("x", Arrays.asList("x", "xx", "")));
    }
}

