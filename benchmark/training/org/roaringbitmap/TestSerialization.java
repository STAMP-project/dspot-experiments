package org.roaringbitmap;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;
import org.roaringbitmap.buffer.MutableRoaringBitmap;


public class TestSerialization {
    static RoaringBitmap bitmap_a;

    static RoaringBitmap bitmap_a1;

    static RoaringBitmap bitmap_empty = new RoaringBitmap();

    static RoaringBitmap bitmap_b = new RoaringBitmap();

    static MutableRoaringBitmap bitmap_ar;

    static MutableRoaringBitmap bitmap_br = new MutableRoaringBitmap();

    static MutableRoaringBitmap bitmap_emptyr = new MutableRoaringBitmap();

    static ByteBuffer outbb;

    static ByteBuffer presoutbb;

    @Test
    public void testDeserialize() throws IOException {
        TestSerialization.presoutbb.rewind();
        ByteBufferBackedInputStream in = new ByteBufferBackedInputStream(TestSerialization.presoutbb);
        DataInputStream dis = new DataInputStream(in);
        TestSerialization.bitmap_empty.deserialize(dis);
        TestSerialization.bitmap_b.deserialize(dis);
    }

    @Test
    public void testImmutableBuildingBySerialization() {
        TestSerialization.presoutbb.rewind();
        ImmutableRoaringBitmap imrempty = new ImmutableRoaringBitmap(TestSerialization.presoutbb);
        TestSerialization.presoutbb.position(((TestSerialization.presoutbb.position()) + (imrempty.serializedSizeInBytes())));
        Assert.assertEquals(imrempty.isEmpty(), true);
        ImmutableRoaringBitmap imrb = new ImmutableRoaringBitmap(TestSerialization.presoutbb);
        int cksum1 = 0;
        int cksum2 = 0;
        int count1 = 0;
        int count2 = 0;
        for (int x : TestSerialization.bitmap_a) {
            // or bitmap_a1 for a version without run
            cksum1 += x;
            ++count1;
        }
        for (int x : imrb) {
            cksum2 += x;
            ++count2;
        }
        Iterator<Integer> it1;
        Iterator<Integer> it2;
        it1 = TestSerialization.bitmap_a.iterator();
        // it1 = bitmap_a1.iterator();
        it2 = imrb.iterator();
        int blabcount = 0;
        int valcount = 0;
        while ((it1.hasNext()) && (it2.hasNext())) {
            ++valcount;
            int val1 = it1.next();
            int val2 = it2.next();
            if (val1 != val2) {
                if ((++blabcount) < 10) {
                    System.out.println(((((("disagree on " + valcount) + " nonmatching values are ") + val1) + " ") + val2));
                }
            }
        } 
        System.out.println((("there were " + blabcount) + " diffs"));
        if ((it1.hasNext()) != (it2.hasNext())) {
            System.out.println("one ran out earlier");
        }
        Assert.assertEquals(count1, count2);
        Assert.assertEquals(cksum1, cksum2);
    }

    @Test
    public void testImmutableBuildingBySerializationSimple() {
        System.out.println("testImmutableBuildingBySerializationSimple ");
        ByteBuffer bb1;
        MutableRoaringBitmap bm1 = new MutableRoaringBitmap();
        for (int k = 20; k < 30; ++k) {
            // runcontainer would be best
            bm1.add(k);
        }
        bm1.runOptimize();
        bb1 = ByteBuffer.allocate(TestSerialization.bitmap_a.serializedSizeInBytes());
        ByteBufferBackedOutputStream out = new ByteBufferBackedOutputStream(bb1);
        try {
            bm1.serialize(new DataOutputStream(out));
        } catch (Exception e) {
            e.printStackTrace();
        }
        bb1.flip();
        ImmutableRoaringBitmap imrb = new ImmutableRoaringBitmap(bb1);
        int cksum1 = 0;
        int cksum2 = 0;
        int count1 = 0;
        int count2 = 0;
        for (int x : bm1) {
            cksum1 += x;
            ++count1;
        }
        for (int x : imrb) {
            cksum2 += x;
            ++count2;
        }
        Assert.assertEquals(count1, count2);
        Assert.assertEquals(cksum1, cksum2);
    }

    @Test
    public void testMutableBuilding() {
        // did we build a mutable equal to the regular one?
        Assert.assertEquals(TestSerialization.bitmap_emptyr.isEmpty(), true);
        Assert.assertEquals(TestSerialization.bitmap_empty.isEmpty(), true);
        int cksum1 = 0;
        int cksum2 = 0;
        for (int x : TestSerialization.bitmap_a) {
            cksum1 += x;
        }
        for (int x : TestSerialization.bitmap_ar) {
            cksum2 += x;
        }
        Assert.assertEquals(cksum1, cksum2);
    }

    @Test
    public void testMutableBuildingBySerialization() throws IOException {
        TestSerialization.presoutbb.rewind();
        ByteBufferBackedInputStream in = new ByteBufferBackedInputStream(TestSerialization.presoutbb);
        MutableRoaringBitmap emptyt = new MutableRoaringBitmap();
        MutableRoaringBitmap mrb = new MutableRoaringBitmap();
        DataInputStream dis = new DataInputStream(in);
        emptyt.deserialize(dis);
        Assert.assertEquals(emptyt.isEmpty(), true);
        mrb.deserialize(dis);
        int cksum1 = 0;
        int cksum2 = 0;
        for (int x : TestSerialization.bitmap_a) {
            cksum1 += x;
        }
        for (int x : mrb) {
            cksum2 += x;
        }
        Assert.assertEquals(cksum1, cksum2);
    }

    @Test
    public void testMutableDeserializeMutable() throws IOException {
        TestSerialization.presoutbb.rewind();
        ByteBufferBackedInputStream in = new ByteBufferBackedInputStream(TestSerialization.presoutbb);
        DataInputStream dis = new DataInputStream(in);
        TestSerialization.bitmap_emptyr.deserialize(dis);
        TestSerialization.bitmap_br.deserialize(dis);
    }

    @Test
    public void testMutableRunSerializationBasicDeserialization() throws IOException {
        final int[] data = TestSerialization.takeSortedAndDistinct(new Random(4060), 100000);
        RoaringBitmap bitmap_a = RoaringBitmap.bitmapOf(data);
        RoaringBitmap bitmap_ar = RoaringBitmap.bitmapOf(data);
        MutableRoaringBitmap bitmap_am = MutableRoaringBitmap.bitmapOf(data);
        MutableRoaringBitmap bitmap_amr = MutableRoaringBitmap.bitmapOf(data);
        for (int k = 100000; k < 200000; ++k) {
            bitmap_a.add((3 * k));// bitmap density and too many little runs

            bitmap_ar.add((3 * k));
            bitmap_am.add((3 * k));
            bitmap_amr.add((3 * k));
        }
        for (int k = 700000; k < 800000; ++k) {
            // will choose a runcontainer on this
            bitmap_a.add(k);// bitmap density and too many little runs

            bitmap_ar.add(k);
            bitmap_am.add(k);
            bitmap_amr.add(k);
        }
        bitmap_ar.runOptimize();
        bitmap_amr.runOptimize();
        Assert.assertEquals(bitmap_a, bitmap_ar);
        Assert.assertEquals(bitmap_am, bitmap_amr);
        Assert.assertEquals(bitmap_am.serializedSizeInBytes(), bitmap_a.serializedSizeInBytes());
        Assert.assertEquals(bitmap_amr.serializedSizeInBytes(), bitmap_ar.serializedSizeInBytes());
        ByteBuffer outbuf = ByteBuffer.allocate((2 * ((bitmap_a.serializedSizeInBytes()) + (bitmap_ar.serializedSizeInBytes()))));
        DataOutputStream out = new DataOutputStream(new ByteBufferBackedOutputStream(outbuf));
        try {
            bitmap_a.serialize(out);
            bitmap_ar.serialize(out);
            bitmap_am.serialize(out);
            bitmap_amr.serialize(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        outbuf.flip();
        RoaringBitmap bitmap_c1 = new RoaringBitmap();
        RoaringBitmap bitmap_c2 = new RoaringBitmap();
        RoaringBitmap bitmap_c3 = new RoaringBitmap();
        RoaringBitmap bitmap_c4 = new RoaringBitmap();
        DataInputStream in = new DataInputStream(new ByteBufferBackedInputStream(outbuf));
        bitmap_c1.deserialize(in);
        bitmap_c2.deserialize(in);
        bitmap_c3.deserialize(in);
        bitmap_c4.deserialize(in);
        Assert.assertEquals(bitmap_a, bitmap_c1);
        Assert.assertEquals(bitmap_a, bitmap_c2);
        Assert.assertEquals(bitmap_a, bitmap_c3);
        Assert.assertEquals(bitmap_a, bitmap_c4);
        Assert.assertEquals(bitmap_ar, bitmap_c1);
        Assert.assertEquals(bitmap_ar, bitmap_c2);
        Assert.assertEquals(bitmap_ar, bitmap_c3);
        Assert.assertEquals(bitmap_ar, bitmap_c4);
    }

    @Test
    public void testMutableSerialize() throws IOException {
        System.out.println("testMutableSerialize");
        TestSerialization.outbb.rewind();
        ByteBufferBackedOutputStream out = new ByteBufferBackedOutputStream(TestSerialization.outbb);
        System.out.println(("bitmap_ar is " + (TestSerialization.bitmap_ar.getClass().getName())));
        DataOutputStream dos = new DataOutputStream(out);
        TestSerialization.bitmap_emptyr.serialize(dos);
        TestSerialization.bitmap_ar.serialize(dos);
    }

    @Test
    public void testRunSerializationDeserialization() throws IOException {
        final int[] data = TestSerialization.takeSortedAndDistinct(new Random(4060), 100000);
        RoaringBitmap bitmap_a = RoaringBitmap.bitmapOf(data);
        RoaringBitmap bitmap_ar = RoaringBitmap.bitmapOf(data);
        for (int k = 100000; k < 200000; ++k) {
            bitmap_a.add((3 * k));// bitmap density and too many little runs

            bitmap_ar.add((3 * k));
        }
        for (int k = 700000; k < 800000; ++k) {
            // will choose a runcontainer on this
            bitmap_a.add(k);
            bitmap_ar.add(k);
        }
        bitmap_a.runOptimize();// mix of all 3 container kinds

        ByteBuffer outbuf = ByteBuffer.allocate(bitmap_a.serializedSizeInBytes());
        ByteBufferBackedOutputStream out = new ByteBufferBackedOutputStream(outbuf);
        try {
            bitmap_a.serialize(new DataOutputStream(out));
        } catch (Exception e) {
            e.printStackTrace();
        }
        outbuf.flip();
        RoaringBitmap bitmap_c = new RoaringBitmap();
        ByteBufferBackedInputStream in = new ByteBufferBackedInputStream(outbuf);
        bitmap_c.deserialize(new DataInputStream(in));
        Assert.assertEquals(bitmap_a, bitmap_c);
    }

    @Test
    public void testSerialize() throws IOException {
        TestSerialization.outbb.rewind();
        ByteBufferBackedOutputStream out = new ByteBufferBackedOutputStream(TestSerialization.outbb);
        DataOutputStream dos = new DataOutputStream(out);
        TestSerialization.bitmap_empty.serialize(dos);
        TestSerialization.bitmap_a.serialize(dos);
    }
}

