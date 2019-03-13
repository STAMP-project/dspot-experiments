package water;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import water.util.StringUtils;


public class KeyToString extends TestUtil {
    @Test
    public void testKeyToString() {
        byte[] b = StringUtils.bytesOf("XXXHelloAll");
        Assert.assertTrue(Key.make(b).toString().equals("XXXHelloAll"));
        Assert.assertTrue(Arrays.equals(Key.make(b)._kb, b));
        b[0] = 16;
        b[1] = 20;
        Key k = Key.make("$202020$");
        Assert.assertEquals(k._kb.length, 3);
        Assert.assertEquals(k._kb[0], 32);
        Assert.assertEquals(k._kb[1], 32);
        Assert.assertEquals(k._kb[2], 32);
        k = Key.make("$fffe85$Azaz09-.");
        Assert.assertTrue(k.toString().equals("$fffe85$Azaz09-."));
        k = Key.make("Hi There");
        Assert.assertTrue(k.toString().equals("Hi There"));
    }
}

