package com.baeldung.hexToAscii;


import org.junit.Assert;
import org.junit.Test;


public class HexToAsciiUnitTest {
    @Test
    public void whenHexToAscii() {
        String asciiString = "http://www.baeldung.com/jackson-serialize-dates";
        String hexEquivalent = "687474703a2f2f7777772e6261656c64756e672e636f6d2f6a61636b736f6e2d73657269616c697a652d6461746573";
        Assert.assertEquals(asciiString, HexToAsciiUnitTest.hexToAscii(hexEquivalent));
    }

    @Test
    public void whenAsciiToHex() {
        String asciiString = "http://www.baeldung.com/jackson-serialize-dates";
        String hexEquivalent = "687474703a2f2f7777772e6261656c64756e672e636f6d2f6a61636b736f6e2d73657269616c697a652d6461746573";
        Assert.assertEquals(hexEquivalent, HexToAsciiUnitTest.asciiToHex(asciiString));
    }
}

