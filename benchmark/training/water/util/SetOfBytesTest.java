package water.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for SetOfBytes
 *
 * Created by vpatryshev on 1/13/17.
 */
public class SetOfBytesTest {
    @Test
    public void testContains() throws Exception {
        SetOfBytes sut = new SetOfBytes(new byte[]{ 0, 2, ((byte) (128)), ((byte) (-1)) });
        Assert.assertTrue(sut.contains(0));
        Assert.assertTrue(sut.contains(2));
        Assert.assertTrue(sut.contains(128));
        Assert.assertTrue(sut.contains(255));
        Assert.assertFalse(sut.contains((-2)));
        Assert.assertFalse(sut.contains(1));
        Assert.assertFalse(sut.contains(3));
        Assert.assertFalse(sut.contains(256));
        Assert.assertFalse(sut.contains(65535));
        Assert.assertFalse(sut.contains((-129)));
        Assert.assertFalse(sut.contains(Integer.MIN_VALUE));
        Assert.assertFalse(sut.contains(Integer.MAX_VALUE));
        for (int i = 0; i < 256; i++)
            Assert.assertFalse(new SetOfBytes("").contains(i));

        SetOfBytes sut1 = new SetOfBytes("Hello World!");
        Assert.assertTrue(sut1.contains('!'));
        Assert.assertTrue(sut1.contains(' '));
        Assert.assertTrue(sut1.contains('o'));
        Assert.assertFalse(sut1.contains('O'));
        Assert.assertFalse(sut1.contains('0'));
        Assert.assertFalse(sut1.contains('h'));
    }

    @Test
    public void testEquals() throws Exception {
        SetOfBytes sut1 = new SetOfBytes("Hi");
        SetOfBytes sut2 = new SetOfBytes("High");
        Assert.assertTrue(sut1.equals(new SetOfBytes("iH")));
        Assert.assertFalse(sut1.equals(sut2));
        Assert.assertFalse(sut2.equals(sut1));
    }

    @Test
    public void testAsBytes() throws Exception {
        SetOfBytes sut = new SetOfBytes("Hello World!");
        Assert.assertEquals(sut, new SetOfBytes(sut.getBytes()));
        Assert.assertArrayEquals(" !HWdelor".getBytes(), sut.getBytes());
    }
}

