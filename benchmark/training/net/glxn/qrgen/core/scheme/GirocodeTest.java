package net.glxn.qrgen.core.scheme;


import Encoding.UTF_8;
import org.junit.Assert;
import org.junit.Test;


public class GirocodeTest {
    @Test
    public void parseStringLf() {
        checkParseString("\n");
    }

    @Test
    public void parseStringCrLf() {
        checkParseString("\r\n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseNull() {
        Girocode.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseEmptyString() {
        Girocode.parse("");
    }

    @Test
    public void testToString() {
        Girocode girocode = new Girocode();
        girocode.setEncoding(UTF_8);
        girocode.setBic("DAAABCDGGD");
        girocode.setName("Miss Marple");
        girocode.setIban("DE91300776014444814989");
        girocode.setAmount("EUR27.06");
        girocode.setPurposeCode("xyz");
        girocode.setText("for a good prupose");
        girocode.setHint("Watch this Girocode :-)");
        Assert.assertEquals(("BCD\n"// 
         + (((((((((("001\n"// 
         + "1\n")// 
         + "SCT\n")// 
         + "DAAABCDGGD\n")// 
         + "Miss Marple\n")// 
         + "DE91300776014444814989\n")// 
         + "EUR27.06\n")// 
         + "xyz\n")// 
         + "\n")// 
         + "for a good prupose\n")// 
         + "Watch this Girocode :-)\n")), girocode.toString());
    }

    @Test
    public void testToStringWithReference() {
        Girocode girocode = new Girocode();
        girocode.setEncoding(UTF_8);
        girocode.setBic("DAAABCDGGD");
        girocode.setName("Miss Marple");
        girocode.setIban("DE91300776014444814989");
        girocode.setAmount("EUR27.06");
        girocode.setPurposeCode("xyz");
        girocode.setReference("reference");
        girocode.setHint("Watch this Girocode :-)");
        Assert.assertEquals(("BCD\n"// 
         + (((((((((("001\n"// 
         + "1\n")// 
         + "SCT\n")// 
         + "DAAABCDGGD\n")// 
         + "Miss Marple\n")// 
         + "DE91300776014444814989\n")// 
         + "EUR27.06\n")// 
         + "xyz\n")// 
         + "reference\n")// 
         + "\n")// 
         + "Watch this Girocode :-)\n")), girocode.toString());
    }
}

