package org.roaringbitmap.buffer;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;


public class TestAdversarialInputs {
    @Test
    public void testInputGoodFile1() throws IOException {
        File file = copy("testdata/bitmapwithruns.bin");
        MutableRoaringBitmap rb = new MutableRoaringBitmap();
        // should not throw an exception
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        Assert.assertEquals(rb.getCardinality(), 200100);
        file.delete();
    }

    @Test
    public void testInputGoodFile1Mapped() throws IOException {
        ByteBuffer bb = memoryMap("testdata/bitmapwithruns.bin");
        ImmutableRoaringBitmap rb = new ImmutableRoaringBitmap(bb);
        Assert.assertEquals(rb.getCardinality(), 200100);
    }

    @Test
    public void testInputGoodFile2() throws IOException {
        File file = copy("testdata/bitmapwithoutruns.bin");
        MutableRoaringBitmap rb = new MutableRoaringBitmap();
        // should not throw an exception
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        Assert.assertEquals(rb.getCardinality(), 200100);
        file.delete();
    }

    @Test
    public void testInputGoodFile2Mapped() throws IOException {
        ByteBuffer bb = memoryMap("testdata/bitmapwithoutruns.bin");
        ImmutableRoaringBitmap rb = new ImmutableRoaringBitmap(bb);
        Assert.assertEquals(rb.getCardinality(), 200100);
    }

    @Test(expected = IOException.class)
    public void testInputBadFile1() throws IOException {
        File file = copy("testdata/crashproneinput1.bin");
        MutableRoaringBitmap rb = new MutableRoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInputBadFile1Mapped() throws IOException {
        ByteBuffer bb = memoryMap("testdata/crashproneinput1.bin");
        ImmutableRoaringBitmap rb = new ImmutableRoaringBitmap(bb);
        System.out.println(rb.getCardinality());// won't get here

    }

    @Test(expected = IOException.class)
    public void testInputBadFile2() throws IOException {
        File file = copy("testdata/crashproneinput2.bin");
        MutableRoaringBitmap rb = new MutableRoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInputBadFile2Mapped() throws IOException {
        ByteBuffer bb = memoryMap("testdata/crashproneinput2.bin");
        ImmutableRoaringBitmap rb = new ImmutableRoaringBitmap(bb);
        System.out.println(rb.getCardinality());// won't get here

    }

    @Test(expected = IOException.class)
    public void testInputBadFile3() throws IOException {
        File file = copy("testdata/crashproneinput3.bin");
        MutableRoaringBitmap rb = new MutableRoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInputBadFile3Mapped() throws IOException {
        ByteBuffer bb = memoryMap("testdata/crashproneinput3.bin");
        ImmutableRoaringBitmap rb = new ImmutableRoaringBitmap(bb);
        System.out.println(rb.getCardinality());// won't get here

    }

    @Test(expected = IOException.class)
    public void testInputBadFile4() throws IOException {
        File file = copy("testdata/crashproneinput4.bin");
        MutableRoaringBitmap rb = new MutableRoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInputBadFile4Mapped() throws IOException {
        ByteBuffer bb = memoryMap("testdata/crashproneinput4.bin");
        ImmutableRoaringBitmap rb = new ImmutableRoaringBitmap(bb);
        System.out.println(rb.getCardinality());// won't get here

    }

    @Test(expected = IOException.class)
    public void testInputBadFile5() throws IOException {
        File file = copy("testdata/crashproneinput5.bin");
        MutableRoaringBitmap rb = new MutableRoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInputBadFile5Mapped() throws IOException {
        ByteBuffer bb = memoryMap("testdata/crashproneinput5.bin");
        ImmutableRoaringBitmap rb = new ImmutableRoaringBitmap(bb);
        System.out.println(rb.getCardinality());// won't get here

    }

    @Test(expected = IOException.class)
    public void testInputBadFile6() throws IOException {
        File file = copy("testdata/crashproneinput6.bin");
        MutableRoaringBitmap rb = new MutableRoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInputBadFile6Mapped() throws IOException {
        ByteBuffer bb = memoryMap("testdata/crashproneinput6.bin");
        ImmutableRoaringBitmap rb = new ImmutableRoaringBitmap(bb);
        System.out.println(rb.getCardinality());// won't get here

    }

    @Test(expected = IOException.class)
    public void testInputBadFile7() throws IOException {
        File file = copy("testdata/crashproneinput7.bin");
        MutableRoaringBitmap rb = new MutableRoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInputBadFile7Mapped() throws IOException {
        ByteBuffer bb = memoryMap("testdata/crashproneinput7.bin");
        ImmutableRoaringBitmap rb = new ImmutableRoaringBitmap(bb);
        System.out.println(rb.getCardinality());// won't get here

    }

    @Test(expected = IOException.class)
    public void testInputBadFile8() throws IOException {
        File file = copy("testdata/crashproneinput8.bin");
        MutableRoaringBitmap rb = new MutableRoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInputBadFile8Mapped() throws IOException {
        ByteBuffer bb = memoryMap("testdata/crashproneinput8.bin");
        ImmutableRoaringBitmap rb = new ImmutableRoaringBitmap(bb);
        System.out.println(rb.getCardinality());// won't get here

    }
}

