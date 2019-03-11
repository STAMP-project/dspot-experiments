package com.rarchives.ripme.tst;


import com.rarchives.ripme.utils.Base64;
import junit.framework.TestCase;


public class Base64Test extends TestCase {
    public void testDecode() {
        TestCase.assertEquals("test", new String(Base64.decode("dGVzdA==")));
    }
}

