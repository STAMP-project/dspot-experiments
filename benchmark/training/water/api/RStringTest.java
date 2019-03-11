package water.api;


import org.junit.Assert;
import org.junit.Test;
import water.util.RString;


public class RStringTest {
    static final String s1 = "hello %all and %$all and %go";

    @Test
    public void testReplacement() {
        RString r1 = new RString(RStringTest.s1);
        r1.replace("all", " +");
        Assert.assertEquals("hello  + and +%2B and ", r1.toString());
        r1.replace("go", "why");
        Assert.assertEquals("hello  + and +%2B and why", r1.toString());
    }

    static final String s2 = "ReplacementIn%text{}Value";

    @Test
    public void testReplacement2() {
        RString r = new RString(RStringTest.s2);
        r.replace("text", "TEXT");
        Assert.assertEquals("ReplacementInTEXTValue", r.toString());
    }
}

