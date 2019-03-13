package org.roaringbitmap;


import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class KryoTest {
    @Test
    public void roaringTest() throws IOException {
        RoaringSerializer serializer = new RoaringSerializer();
        RoaringBitmap roaringDense = new RoaringBitmap();
        for (int i = 0; i < 100000; i++) {
            roaringDense.add(i);
        }
        File tmpfiledense = File.createTempFile("roaring_dense", "bin");
        tmpfiledense.deleteOnExit();
        KryoTest.writeRoaringToFile(tmpfiledense, roaringDense, serializer);
        RoaringBitmap denseRoaringFromFile = KryoTest.readRoaringFromFile(tmpfiledense, serializer);
        Assert.assertTrue(denseRoaringFromFile.equals(roaringDense));
        RoaringBitmap roaringSparse = new RoaringBitmap();
        for (int i = 0; i < 100000; i++) {
            if ((i % 11) == 0) {
                roaringSparse.add(i);
            }
        }
        File tmpfilesparse = File.createTempFile("roaring_sparse", "bin");
        KryoTest.writeRoaringToFile(tmpfilesparse, roaringSparse, serializer);
        RoaringBitmap sparseRoaringFromFile = KryoTest.readRoaringFromFile(tmpfilesparse, serializer);
        Assert.assertTrue(sparseRoaringFromFile.equals(roaringSparse));
    }
}

