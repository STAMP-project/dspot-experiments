package com.rarchives.ripme.tst;


import com.rarchives.ripme.ripper.AbstractRipper;
import java.io.IOException;
import java.net.URL;
import junit.framework.TestCase;


public class AbstractRipperTest extends TestCase {
    public void testGetFileName() throws IOException {
        String fileName = AbstractRipper.getFileName(new URL("http://www.tsumino.com/Image/Object?name=U1EieteEGwm6N1dGszqCpA%3D%3D"), "test", "test");
        TestCase.assertEquals("test.test", fileName);
        fileName = AbstractRipper.getFileName(new URL("http://www.tsumino.com/Image/Object?name=U1EieteEGwm6N1dGszqCpA%3D%3D"), "test", null);
        TestCase.assertEquals("test", fileName);
        fileName = AbstractRipper.getFileName(new URL("http://www.tsumino.com/Image/Object?name=U1EieteEGwm6N1dGszqCpA%3D%3D"), null, null);
        TestCase.assertEquals("Object", fileName);
        fileName = AbstractRipper.getFileName(new URL("http://www.test.com/file.png"), null, null);
        TestCase.assertEquals("file.png", fileName);
        fileName = AbstractRipper.getFileName(new URL("http://www.test.com/file."), null, null);
        TestCase.assertEquals("file.", fileName);
    }
}

