package org.roaringbitmap.buffer;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;


@SuppressWarnings({ "static-method" })
public class TestMemoryMapping {
    static ArrayList<ImmutableRoaringBitmap> mappedbitmaps = new ArrayList<ImmutableRoaringBitmap>();

    static MappedByteBuffer out;

    static ArrayList<MutableRoaringBitmap> rambitmaps = new ArrayList<MutableRoaringBitmap>();

    static File tmpfile;

    @Test
    public void basic() {
        System.out.println("[TestMemoryMapping] basic tests");
        for (int k = 0; k < (TestMemoryMapping.mappedbitmaps.size()); ++k) {
            Assert.assertTrue(TestMemoryMapping.mappedbitmaps.get(k).equals(TestMemoryMapping.rambitmaps.get(k)));
        }
    }

    @Test
    public void complements() {
        System.out.println("[TestMemoryMapping] testing complements");
        for (int k = 0; k < ((TestMemoryMapping.mappedbitmaps.size()) - 1); k += 4) {
            final MutableRoaringBitmap rb = ImmutableRoaringBitmap.andNot(TestMemoryMapping.mappedbitmaps.get(k), TestMemoryMapping.mappedbitmaps.get((k + 1)));
            final MutableRoaringBitmap rbram = ImmutableRoaringBitmap.andNot(TestMemoryMapping.rambitmaps.get(k), TestMemoryMapping.rambitmaps.get((k + 1)));
            Assert.assertTrue(rb.equals(rbram));
        }
    }

    @Test
    public void intersections() {
        System.out.println("[TestMemoryMapping] testing intersections");
        for (int k = 0; (k + 1) < (TestMemoryMapping.mappedbitmaps.size()); k += 2) {
            final MutableRoaringBitmap rb = ImmutableRoaringBitmap.and(TestMemoryMapping.mappedbitmaps.get(k), TestMemoryMapping.mappedbitmaps.get((k + 1)));
            final MutableRoaringBitmap rbram = ImmutableRoaringBitmap.and(TestMemoryMapping.rambitmaps.get(k), TestMemoryMapping.rambitmaps.get((k + 1)));
            Assert.assertTrue(rb.equals(rbram));
        }
        for (int k = 0; k < ((TestMemoryMapping.mappedbitmaps.size()) - 4); k += 4) {
            final MutableRoaringBitmap rb = BufferFastAggregation.and(TestMemoryMapping.mappedbitmaps.get(k), TestMemoryMapping.mappedbitmaps.get((k + 1)), TestMemoryMapping.mappedbitmaps.get((k + 3)), TestMemoryMapping.mappedbitmaps.get((k + 4)));
            final MutableRoaringBitmap rbram = BufferFastAggregation.and(TestMemoryMapping.rambitmaps.get(k), TestMemoryMapping.rambitmaps.get((k + 1)), TestMemoryMapping.rambitmaps.get((k + 3)), TestMemoryMapping.rambitmaps.get((k + 4)));
            Assert.assertTrue(rb.equals(rbram));
        }
    }

    @Test
    public void multithreadingTest() throws IOException, InterruptedException {
        System.out.println("[TestMemoryMapping] multithreading test");
        final MutableRoaringBitmap rr1 = new MutableRoaringBitmap();
        final int numThreads = Runtime.getRuntime().availableProcessors();
        final Throwable[] errors = new Throwable[numThreads];
        for (int i = 0; i < numThreads; i++) {
            // each thread will check an integer from a different container
            rr1.add(((Short.MAX_VALUE) * i));
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        rr1.serialize(dos);
        dos.close();
        ByteBuffer bb = ByteBuffer.wrap(bos.toByteArray());
        final ImmutableRoaringBitmap rrback1 = new ImmutableRoaringBitmap(bb);
        final CountDownLatch ready = new CountDownLatch(numThreads);
        final CountDownLatch finished = new CountDownLatch(numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            final int ti = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    ready.countDown();
                    try {
                        ready.await();
                        final int elementToCheck = (Short.MAX_VALUE) * ti;
                        for (int j = 0; j < 10000000; j++) {
                            try {
                                Assert.assertTrue(rrback1.contains(elementToCheck));
                            } catch (Throwable t) {
                                errors[ti] = t;
                            }
                        }
                    } catch (Throwable e) {
                        errors[ti] = e;
                    }
                    finished.countDown();
                }
            });
        }
        finished.await(5, TimeUnit.SECONDS);
        for (int i = 0; i < numThreads; i++) {
            if ((errors[i]) != null) {
                errors[i].printStackTrace();
                Assert.fail((("The contains() for the element " + ((Short.MAX_VALUE) * i)) + " throw an exception"));
            }
        }
    }

    @Test
    public void containsTest() throws IOException {
        System.out.println("[containsTest]");
        for (int z = 0; z < 100; ++z) {
            final MutableRoaringBitmap rr1 = new MutableRoaringBitmap();
            for (int k = 0; k < 100; k += 10)
                rr1.add((k + z));

            for (int k = 100000; k < 200000; k += 2)
                rr1.add((k + z));

            for (int k = 400000; k < 500000; k++)
                rr1.add((k + z));

            rr1.runOptimize();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            rr1.serialize(dos);
            dos.close();
            ByteBuffer bb = ByteBuffer.wrap(bos.toByteArray());
            final ImmutableRoaringBitmap rrback1 = new ImmutableRoaringBitmap(bb);
            Assert.assertEquals(rrback1.getLongSizeInBytes(), rr1.getLongSizeInBytes());
            Assert.assertEquals(rrback1.serializedSizeInBytes(), rr1.serializedSizeInBytes());
            for (int k = 0; k < 1000000; k += 100) {
                Assert.assertEquals(rrback1.contains(k), rr1.contains(k));
            }
        }
    }

    @Test
    public void oneFormat() throws IOException {
        System.out.println("[TestMemoryMapping] testing format compatibility");
        final int ms = TestMemoryMapping.mappedbitmaps.size();
        for (int k = 0; k < ms; ++k) {
            System.out.println(((("[TestMemoryMapping] testing compat. bitmap " + k) + " out of ") + ms));
            ImmutableRoaringBitmap rr = TestMemoryMapping.mappedbitmaps.get(k);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(rr.serializedSizeInBytes());
            DataOutputStream dos = new DataOutputStream(bos);
            rr.serialize(dos);
            dos.close();
            byte[] arr = bos.toByteArray();
            bos = null;
            ByteArrayInputStream bis = new ByteArrayInputStream(arr);
            RoaringBitmap newr = new RoaringBitmap();
            newr.deserialize(new DataInputStream(bis));
            arr = null;
            RoaringBitmap rrasroaring = rr.toRoaringBitmap();
            Assert.assertEquals(newr, rrasroaring);
            System.out.println((((("[TestMemoryMapping] testing compat. bitmap " + k) + " out of ") + ms) + ". ok."));
        }
        System.out.println("[TestMemoryMapping] Format compatibility ok");
    }

    @Test
    public void reserialize() throws IOException {
        System.out.println("[TestMemoryMapping] testing reserialization");
        for (int k = 0; k < (TestMemoryMapping.mappedbitmaps.size()); ++k) {
            ImmutableRoaringBitmap rr = TestMemoryMapping.mappedbitmaps.get(k);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            rr.serialize(dos);
            dos.close();
            ByteBuffer bb = ByteBuffer.wrap(bos.toByteArray());
            ImmutableRoaringBitmap rrback = new ImmutableRoaringBitmap(bb);
            Assert.assertTrue(rr.equals(rrback));
            Assert.assertTrue(rr.equals(rrback.toMutableRoaringBitmap()));
            Assert.assertTrue(rr.toMutableRoaringBitmap().equals(rrback));
            Assert.assertTrue(rr.toMutableRoaringBitmap().equals(TestMemoryMapping.rambitmaps.get(k)));
        }
    }

    @Test
    public void standardTest() throws IOException {
        System.out.println("[TestMemoryMapping] standard test");
        MutableRoaringBitmap rr1 = MutableRoaringBitmap.bitmapOf(1, 2, 3, 1000);
        MutableRoaringBitmap rr2 = MutableRoaringBitmap.bitmapOf(2, 3, 1010);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        rr1.serialize(dos);
        rr2.serialize(dos);
        dos.close();
        ByteBuffer bb = ByteBuffer.wrap(bos.toByteArray());
        ImmutableRoaringBitmap rrback1 = new ImmutableRoaringBitmap(bb);
        Assert.assertTrue(rr1.equals(rrback1));
        bb.position(((bb.position()) + (rrback1.serializedSizeInBytes())));
        ImmutableRoaringBitmap rrback2 = new ImmutableRoaringBitmap(bb);
        Assert.assertTrue(rr1.equals(rrback1));
        Assert.assertTrue(rr2.equals(rrback2));
    }

    @Test
    public void standardTest1() throws IOException {
        System.out.println("[TestMemoryMapping] standard test 1");
        // use some run containers
        MutableRoaringBitmap rr1 = MutableRoaringBitmap.bitmapOf(1, 2, 3, 4, 5, 6, 1000);
        rr1.runOptimize();
        MutableRoaringBitmap rr2 = MutableRoaringBitmap.bitmapOf(2, 3, 1010);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        rr1.serialize(dos);
        rr2.serialize(dos);
        dos.close();
        ByteBuffer bb = ByteBuffer.wrap(bos.toByteArray());
        ImmutableRoaringBitmap rrback1 = new ImmutableRoaringBitmap(bb);
        Assert.assertTrue(rr1.equals(rrback1));
        bb.position(((bb.position()) + (rrback1.serializedSizeInBytes())));
        ImmutableRoaringBitmap rrback2 = new ImmutableRoaringBitmap(bb);
        Assert.assertTrue(rr1.equals(rrback1));
        Assert.assertTrue(rr2.equals(rrback2));
        ImmutableRoaringBitmap rrback1c = rrback1.clone();
        ImmutableRoaringBitmap rrback2c = rrback2.clone();
        Assert.assertTrue(rr1.equals(rrback1c));
        Assert.assertTrue(rr2.equals(rrback2c));
        Assert.assertTrue(rrback1.equals(rrback1c));
        Assert.assertTrue(rrback2.equals(rrback2c));
        Assert.assertEquals(rr1.hashCode(), rrback1.hashCode());
        Assert.assertEquals(rr1.hashCode(), rrback1c.hashCode());
        Assert.assertEquals(rr2.hashCode(), rrback2.hashCode());
        Assert.assertEquals(rr2.hashCode(), rrback2c.hashCode());
    }

    @Test
    public void testIterator() {
        System.out.println("[TestMemoryMapping] test iterators");
        final int ms = TestMemoryMapping.mappedbitmaps.size();
        System.out.println("We first test in-memory (RoaringBitmap) iterators.");
        for (int k = 0; k < ms; ++k) {
            System.out.println(((("[TestMemoryMapping] testing copy via iterators using RoaringBitmap copy " + k) + " out of ") + ms));
            final RoaringBitmap target = TestMemoryMapping.mappedbitmaps.get(k).toRoaringBitmap();
            final int truecard = target.getCardinality();
            System.out.println(("Cardinality = " + truecard));
            int card1 = 0;
            int oldvalue = -1;
            long t1 = System.nanoTime();
            for (int x : target) {
                Assert.assertTrue(target.contains(x));
                if (x > oldvalue) {
                    ++card1;
                }
                oldvalue = x;
            }
            long t2 = System.nanoTime();
            System.out.println((" iterator one ns/ops = " + (((t2 - t1) * 1.0) / truecard)));
            Assert.assertEquals(truecard, card1);
            long t3 = System.nanoTime();
            IntIterator i = target.getIntIterator();
            oldvalue = -1;
            int card2 = 0;
            while (i.hasNext()) {
                final int x = i.next();
                Assert.assertTrue(target.contains(x));
                if (x > oldvalue) {
                    ++card2;
                }
                oldvalue = x;
            } 
            long t4 = System.nanoTime();
            System.out.println((" iterator two ns/ops = " + (((t4 - t3) * 1.0) / truecard)));
            Assert.assertEquals(truecard, card2);
            System.out.println((((("[TestMemoryMapping] testing copy via iterators using RoaringBitmap copy " + k) + " out of ") + ms) + " ok"));
        }
        System.out.println("Next, we test mapped (ImmutableRoaringBitmap) iterators.");
        for (int k = 0; k < ms; ++k) {
            System.out.println(((("[TestMemoryMapping] testing copy via iterators " + k) + " out of ") + ms));
            final ImmutableRoaringBitmap target = TestMemoryMapping.mappedbitmaps.get(k);
            final ImmutableRoaringBitmap ramtarget = TestMemoryMapping.rambitmaps.get(k);
            Assert.assertEquals(target, ramtarget);
            final int truecard = target.getCardinality();
            System.out.println(("Cardinality = " + truecard));
            int card1 = 0;
            int oldvalue = -1;
            long t1 = System.nanoTime();
            for (int x : target) {
                Assert.assertTrue(ramtarget.contains(x));
                if (x > oldvalue) {
                    ++card1;
                }
                oldvalue = x;
            }
            long t2 = System.nanoTime();
            System.out.println((" iterator one ns/ops = " + (((t2 - t1) * 1.0) / truecard)));
            Assert.assertEquals(truecard, card1);
            long t3 = System.nanoTime();
            IntIterator i = target.getIntIterator();
            oldvalue = -1;
            int card2 = 0;
            while (i.hasNext()) {
                final int x = i.next();
                Assert.assertTrue(ramtarget.contains(x));
                if (x > oldvalue) {
                    ++card2;
                }
                oldvalue = x;
            } 
            long t4 = System.nanoTime();
            System.out.println((" iterator two ns/ops = " + (((t4 - t3) * 1.0) / truecard)));
            Assert.assertEquals(truecard, card2);
            System.out.println((((("[TestMemoryMapping] testing copy via iterators " + k) + " out of ") + ms) + " ok"));
        }
        System.out.println("[TestMemoryMapping] testing a custom iterator copy  ");
        MutableRoaringBitmap rb = new MutableRoaringBitmap();
        for (int k = 0; k < 4000; ++k) {
            rb.add(k);
        }
        for (int k = 0; k < 1000; ++k) {
            rb.add((k * 100));
        }
        MutableRoaringBitmap copy1 = new MutableRoaringBitmap();
        for (int x : rb) {
            copy1.add(x);
        }
        Assert.assertTrue(copy1.equals(rb));
        MutableRoaringBitmap copy2 = new MutableRoaringBitmap();
        IntIterator i = rb.getIntIterator();
        while (i.hasNext()) {
            copy2.add(i.next());
        } 
        Assert.assertTrue(copy2.equals(rb));
        System.out.println("[TestMemoryMapping] testing a custom iterator copy  ok");
    }

    @Test
    public void unions() {
        System.out.println("[TestMemoryMapping] testing Unions");
        for (int k = 0; k < ((TestMemoryMapping.mappedbitmaps.size()) - 4); k += 4) {
            final MutableRoaringBitmap rb = BufferFastAggregation.or(TestMemoryMapping.mappedbitmaps.get(k), TestMemoryMapping.mappedbitmaps.get((k + 1)), TestMemoryMapping.mappedbitmaps.get((k + 3)), TestMemoryMapping.mappedbitmaps.get((k + 4)));
            final MutableRoaringBitmap rbram = BufferFastAggregation.or(TestMemoryMapping.rambitmaps.get(k), TestMemoryMapping.rambitmaps.get((k + 1)), TestMemoryMapping.rambitmaps.get((k + 3)), TestMemoryMapping.rambitmaps.get((k + 4)));
            Assert.assertTrue(rb.equals(rbram));
        }
    }

    @Test
    public void XORs() {
        System.out.println("[TestMemoryMapping] testing XORs");
        for (int k = 0; k < ((TestMemoryMapping.mappedbitmaps.size()) - 4); k += 4) {
            final MutableRoaringBitmap rb = BufferFastAggregation.xor(TestMemoryMapping.mappedbitmaps.get(k), TestMemoryMapping.mappedbitmaps.get((k + 1)), TestMemoryMapping.mappedbitmaps.get((k + 3)), TestMemoryMapping.mappedbitmaps.get((k + 4)));
            final MutableRoaringBitmap rbram = BufferFastAggregation.xor(TestMemoryMapping.rambitmaps.get(k), TestMemoryMapping.rambitmaps.get((k + 1)), TestMemoryMapping.rambitmaps.get((k + 3)), TestMemoryMapping.rambitmaps.get((k + 4)));
            Assert.assertTrue(rb.equals(rbram));
        }
    }
}

