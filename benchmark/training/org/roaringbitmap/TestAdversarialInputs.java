package org.roaringbitmap;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class TestAdversarialInputs {
    @Test
    public void testInputGoodFile1() throws IOException {
        File file = copy("testdata/bitmapwithruns.bin");
        RoaringBitmap rb = new RoaringBitmap();
        // should not throw an exception
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        Assert.assertEquals(rb.getCardinality(), 200100);
        file.delete();
    }

    @Test
    public void testInputGoodFile2() throws IOException {
        File file = copy("testdata/bitmapwithoutruns.bin");
        RoaringBitmap rb = new RoaringBitmap();
        // should not throw an exception
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        Assert.assertEquals(rb.getCardinality(), 200100);
        file.delete();
    }

    @Test(expected = IOException.class)
    public void testInputBadFile1() throws IOException {
        File file = copy("testdata/crashproneinput1.bin");
        RoaringBitmap rb = new RoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IOException.class)
    public void testInputBadFile2() throws IOException {
        File file = copy("testdata/crashproneinput2.bin");
        RoaringBitmap rb = new RoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IOException.class)
    public void testInputBadFile3() throws IOException {
        File file = copy("testdata/crashproneinput3.bin");
        RoaringBitmap rb = new RoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IOException.class)
    public void testInputBadFile4() throws IOException {
        File file = copy("testdata/crashproneinput4.bin");
        RoaringBitmap rb = new RoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IOException.class)
    public void testInputBadFile5() throws IOException {
        File file = copy("testdata/crashproneinput5.bin");
        RoaringBitmap rb = new RoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IOException.class)
    public void testInputBadFile6() throws IOException {
        File file = copy("testdata/crashproneinput6.bin");
        RoaringBitmap rb = new RoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IOException.class)
    public void testInputBadFile7() throws IOException {
        File file = copy("testdata/crashproneinput7.bin");
        RoaringBitmap rb = new RoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }

    @Test(expected = IOException.class)
    public void testInputBadFile8() throws IOException {
        File file = copy("testdata/crashproneinput8.bin");
        RoaringBitmap rb = new RoaringBitmap();
        // should not work
        rb.deserialize(new DataInputStream(new FileInputStream(file)));
        file.delete();
    }
}

