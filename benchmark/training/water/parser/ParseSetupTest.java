package water.parser;


import org.junit.Assert;
import org.junit.Test;


public class ParseSetupTest {
    @Test
    public void isNA() throws Exception {
        ParseSetup p = new ParseSetup();
        p._na_strings = new String[][]{ new String[]{ "na" }, null, new String[]{ "NA", "null" } };
        Assert.assertTrue(p.isNA(0, new BufferedString("na")));
        Assert.assertFalse(p.isNA(0, new BufferedString("NA")));
        Assert.assertFalse(p.isNA(1, new BufferedString("na")));
        Assert.assertTrue(p.isNA(2, new BufferedString("NA")));
        Assert.assertTrue(p.isNA(2, new BufferedString("null")));
        Assert.assertFalse(p.isNA(2, new BufferedString("na")));
        Assert.assertFalse(p.isNA(3, new BufferedString("NA")));
    }
}

