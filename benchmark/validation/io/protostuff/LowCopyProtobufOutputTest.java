package io.protostuff;


import java.nio.ByteBuffer;
import junit.framework.TestCase;

import static io.protostuff.Bar.Status.COMPLETED;


/**
 *
 *
 * @author Ryan Rawson
 */
public class LowCopyProtobufOutputTest extends TestCase {
    public void testCompareVsOther() throws Exception {
        Baz aBaz = new Baz(1, "hello world", 1238372479L);
        ByteBuffer serForm1 = testObj(aBaz, aBaz);
        deserTest(aBaz, aBaz, serForm1);
        Bar testBar = new Bar(22, "some String", aBaz, COMPLETED, ByteString.wrap("fuck yo test".getBytes()), false, 3.14F, 2.7182818284, 599L);
        ByteBuffer serForm2 = testObj(testBar, testBar);
        deserTest(testBar, testBar, serForm2);
    }
}

