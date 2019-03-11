package com.thinkaurelius.titan.util;


import com.thinkaurelius.titan.util.encoding.StringEncoding;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class StringEncodingTest {
    @Test
    public void testAsciiStringEncoding() {
        String[] str = new String[]{ "asdf3", "", "f232rdfjdhjkhfafb-38`138", "8947(*&#$80124n" };
        for (String s : str) {
            Assert.assertTrue(StringEncoding.isAsciiString(s));
            Assert.assertEquals(Math.max(1, s.length()), StringEncoding.getAsciiByteLength(s));
            byte[] data = new byte[StringEncoding.getAsciiByteLength(s)];
            StringEncoding.writeAsciiString(data, 0, s);
            Assert.assertEquals(s, StringEncoding.readAsciiString(data, 0));
        }
        byte[] data = new byte[6];
        StringEncoding.writeAsciiString(data, 0, "abc");
        StringEncoding.writeAsciiString(data, 3, "xyz");
        Assert.assertEquals("abc", StringEncoding.readAsciiString(data, 0));
        Assert.assertEquals("xyz", StringEncoding.readAsciiString(data, 3));
        String[] str2 = new String[]{ null, "?sdf30sn?+p" };
        for (String s : str2) {
            try {
                StringEncoding.getAsciiByteLength(s);
                Assert.fail();
            } catch (IllegalArgumentException e) {
            } catch (NullPointerException e) {
            }
        }
    }
}

