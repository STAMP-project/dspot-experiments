/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clearspring.analytics.stream.quantile;


import TDigest.Group;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.math.jet.random.AbstractContinousDistribution;
import org.apache.mahout.math.jet.random.Gamma;
import org.apache.mahout.math.jet.random.Normal;
import org.apache.mahout.math.jet.random.Uniform;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class TDigestTest {
    private static PrintWriter sizeDump;

    private static PrintWriter errorDump;

    private static PrintWriter deviationDump;

    @Test
    public void testUniform() {
        Random gen = RandomUtils.getRandom();
        for (int i = 0; i < (repeats()); i++) {
            runTest(new Uniform(0, 1, gen), 100, new double[]{ 0.001, 0.01, 0.1, 0.5, 0.9, 0.99, 0.999 }, "uniform", true, gen);
        }
    }

    @Test
    public void testGamma() {
        // this Gamma distribution is very heavily skewed.  The 0.1%-ile is 6.07e-30 while
        // the median is 0.006 and the 99.9th %-ile is 33.6 while the mean is 1.
        // this severe skew means that we have to have positional accuracy that
        // varies by over 11 orders of magnitude.
        Random gen = RandomUtils.getRandom();
        for (int i = 0; i < (repeats()); i++) {
            // new double[]{6.0730483624079e-30, 6.0730483624079e-20, 6.0730483627432e-10, 5.9339110446023e-03,
            // 2.6615455373884e+00, 1.5884778179295e+01, 3.3636770117188e+01},
            runTest(new Gamma(0.1, 0.1, gen), 100, new double[]{ 0.001, 0.01, 0.1, 0.5, 0.9, 0.99, 0.999 }, "gamma", true, gen);
        }
    }

    @Test
    public void testNarrowNormal() {
        // this mixture of a uniform and normal distribution has a very narrow peak which is centered
        // near the median.  Our system should be scale invariant and work well regardless.
        final Random gen = RandomUtils.getRandom();
        AbstractContinousDistribution mix = new AbstractContinousDistribution() {
            AbstractContinousDistribution normal = new Normal(0, 1.0E-5, gen);

            AbstractContinousDistribution uniform = new Uniform((-1), 1, gen);

            @Override
            public double nextDouble() {
                double x;
                if ((gen.nextDouble()) < 0.5) {
                    x = uniform.nextDouble();
                } else {
                    x = normal.nextDouble();
                }
                return x;
            }
        };
        for (int i = 0; i < (repeats()); i++) {
            runTest(mix, 100, new double[]{ 0.001, 0.01, 0.1, 0.3, 0.5, 0.7, 0.9, 0.99, 0.999 }, "mixture", false, gen);
        }
    }

    @Test
    public void testRepeatedValues() {
        final Random gen = RandomUtils.getRandom();
        // 5% of samples will be 0 or 1.0.  10% for each of the values 0.1 through 0.9
        AbstractContinousDistribution mix = new AbstractContinousDistribution() {
            @Override
            public double nextDouble() {
                return (Math.rint(((gen.nextDouble()) * 10))) / 10.0;
            }
        };
        TDigest dist = new TDigest(((double) (1000)), gen);
        long t0 = System.nanoTime();
        List<Double> data = Lists.newArrayList();
        for (int i1 = 0; i1 < 100000; i1++) {
            double x = mix.nextDouble();
            data.add(x);
            dist.add(x);
        }
        System.out.printf("# %fus per point\n", ((((System.nanoTime()) - t0) * 0.001) / 100000));
        System.out.printf("# %d centroids\n", dist.centroidCount());
        // I would be happier with 5x compression, but repeated values make things kind of weird
        Assert.assertTrue("Summary is too large", ((dist.centroidCount()) < (10 * ((double) (1000)))));
        // all quantiles should round to nearest actual value
        for (int i = 0; i < 10; i++) {
            double z = i / 10.0;
            // we skip over troublesome points that are nearly halfway between
            for (double delta : new double[]{ 0.01, 0.02, 0.03, 0.07, 0.08, 0.09 }) {
                double q = z + delta;
                double cdf = dist.cdf(q);
                // we also relax the tolerances for repeated values
                Assert.assertEquals(String.format("z=%.1f, q = %.3f, cdf = %.3f", z, q, cdf), (z + 0.05), cdf, 0.005);
                double estimate = dist.quantile(q);
                Assert.assertEquals(String.format("z=%.1f, q = %.3f, cdf = %.3f, estimate = %.3f", z, q, cdf, estimate), ((Math.rint((q * 10))) / 10.0), estimate, 0.001);
            }
        }
    }

    @Test
    public void testSequentialPoints() {
        Random gen = RandomUtils.getRandom();
        for (int i = 0; i < (repeats()); i++) {
            runTest(new AbstractContinousDistribution() {
                double base = 0;

                @Override
                public double nextDouble() {
                    base += (Math.PI) * 1.0E-5;
                    return base;
                }
            }, 100, new double[]{ 0.001, 0.01, 0.1, 0.5, 0.9, 0.99, 0.999 }, "sequential", true, gen);
        }
    }

    @Test
    public void testSerialization() {
        Random gen = RandomUtils.getRandom();
        TDigest dist = new TDigest(100, gen);
        for (int i = 0; i < 100000; i++) {
            double x = gen.nextDouble();
            dist.add(x);
        }
        dist.compress();
        ByteBuffer buf = ByteBuffer.allocate(20000);
        dist.asBytes(buf);
        Assert.assertTrue(((buf.position()) < 11000));
        Assert.assertEquals(buf.position(), dist.byteSize());
        buf.clear();
        dist.asSmallBytes(buf);
        Assert.assertTrue(((buf.position()) < 6000));
        Assert.assertEquals(buf.position(), dist.smallByteSize());
        System.out.printf("# big %d bytes\n", buf.position());
        buf.flip();
        TDigest dist2 = TDigest.fromBytes(buf);
        Assert.assertEquals(dist.centroidCount(), dist2.centroidCount());
        Assert.assertEquals(dist.compression(), dist2.compression(), 0);
        Assert.assertEquals(dist.size(), dist2.size());
        for (double q = 0; q < 1; q += 0.01) {
            Assert.assertEquals(dist.quantile(q), dist2.quantile(q), 1.0E-8);
        }
        Iterator<? extends TDigest.Group> ix = dist2.centroids().iterator();
        for (TDigest.Group group : dist.centroids()) {
            Assert.assertTrue(ix.hasNext());
            Assert.assertEquals(group.count(), count());
        }
        Assert.assertFalse(ix.hasNext());
        buf.flip();
        dist.asSmallBytes(buf);
        Assert.assertTrue(((buf.position()) < 6000));
        System.out.printf("# small %d bytes\n", buf.position());
        buf.flip();
        dist2 = TDigest.fromBytes(buf);
        Assert.assertEquals(dist.centroidCount(), dist2.centroidCount());
        Assert.assertEquals(dist.compression(), dist2.compression(), 0);
        Assert.assertEquals(dist.size(), dist2.size());
        for (double q = 0; q < 1; q += 0.01) {
            Assert.assertEquals(dist.quantile(q), dist2.quantile(q), 1.0E-6);
        }
        ix = dist2.centroids().iterator();
        for (TDigest.Group group : dist.centroids()) {
            Assert.assertTrue(ix.hasNext());
            Assert.assertEquals(group.count(), count());
        }
        Assert.assertFalse(ix.hasNext());
    }

    @Test
    public void testIntEncoding() {
        Random gen = RandomUtils.getRandom();
        ByteBuffer buf = ByteBuffer.allocate(10000);
        List<Integer> ref = Lists.newArrayList();
        for (int i = 0; i < 3000; i++) {
            int n = gen.nextInt();
            n = n >>> (i / 100);
            ref.add(n);
            TDigest.encode(buf, n);
        }
        buf.flip();
        for (int i = 0; i < 3000; i++) {
            int n = TDigest.decode(buf);
            Assert.assertEquals(String.format("%d:", i), ref.get(i).intValue(), n);
        }
    }

    @Test
    public void compareToQDigest() {
        Random rand = RandomUtils.getRandom();
        for (int i = 0; i < (repeats()); i++) {
            compare(new Gamma(0.1, 0.1, rand), "gamma", (1L << 48), rand);
            compare(new Uniform(0, 1, rand), "uniform", (1L << 48), rand);
        }
    }

    @Test
    public void testSizeControl() throws IOException {
        // very slow running data generator.  Don't want to run this normally.  To run slow tests use
        // mvn test -DrunSlowTests=true
        Assume.assumeTrue(Boolean.parseBoolean(System.getProperty("runSlowTests")));
        Random gen = RandomUtils.getRandom();
        PrintWriter out = new PrintWriter(new FileOutputStream("scaling.tsv"));
        out.printf("k\tsamples\tcompression\tsize1\tsize2\n");
        for (int k = 0; k < 20; k++) {
            for (int size : new int[]{ 10, 100, 1000, 10000 }) {
                for (double compression : new double[]{ 2, 5, 10, 20, 50, 100, 200, 500, 1000 }) {
                    TDigest dist = new TDigest(compression, gen);
                    for (int i = 0; i < (size * 1000); i++) {
                        dist.add(gen.nextDouble());
                    }
                    out.printf("%d\t%d\t%.0f\t%d\t%d\n", k, size, compression, dist.smallByteSize(), dist.byteSize());
                    out.flush();
                }
            }
        }
        out.printf("\n");
        out.close();
    }

    @Test
    public void testScaling() throws FileNotFoundException {
        Random gen = RandomUtils.getRandom();
        PrintWriter out = new PrintWriter(new FileOutputStream("error-scaling.tsv"));
        try {
            out.printf("pass\tcompression\tq\terror\tsize\n");
            // change to 50 passes for better graphs
            int n = (repeats()) * (repeats());
            for (int k = 0; k < n; k++) {
                List<Double> data = Lists.newArrayList();
                for (int i = 0; i < 100000; i++) {
                    data.add(gen.nextDouble());
                }
                Collections.sort(data);
                for (double compression : new double[]{ 2, 5, 10, 20, 50, 100, 200, 500, 1000 }) {
                    TDigest dist = new TDigest(compression, gen);
                    for (Double x : data) {
                        dist.add(x);
                    }
                    dist.compress();
                    for (double q : new double[]{ 0.001, 0.01, 0.1, 0.5 }) {
                        double estimate = dist.quantile(q);
                        double actual = data.get(((int) (q * (data.size()))));
                        out.printf("%d\t%.0f\t%.3f\t%.9f\t%d\n", k, compression, q, (estimate - actual), dist.byteSize());
                        out.flush();
                    }
                }
            }
        } finally {
            out.close();
        }
    }

    @Test
    public void testMerge() {
        Random gen = RandomUtils.getRandom();
        for (int parts : new int[]{ 2, 5, 10, 20, 50, 100 }) {
            List<Double> data = Lists.newArrayList();
            TDigest dist = new TDigest(100, gen);
            dist.recordAllData();
            List<TDigest> many = Lists.newArrayList();
            for (int i = 0; i < 100; i++) {
                many.add(new TDigest(100, gen).recordAllData());
            }
            // we accumulate the data into multiple sub-digests
            List<TDigest> subs = Lists.newArrayList();
            for (int i = 0; i < parts; i++) {
                subs.add(new TDigest(50, gen).recordAllData());
            }
            for (int i = 0; i < 100000; i++) {
                double x = gen.nextDouble();
                data.add(x);
                dist.add(x);
                subs.get((i % parts)).add(x);
            }
            dist.compress();
            Collections.sort(data);
            // collect the raw data from the sub-digests
            List<Double> data2 = Lists.newArrayList();
            for (TDigest digest : subs) {
                for (TDigest.Group group : digest.centroids()) {
                    Iterables.addAll(data2, group.data());
                }
            }
            Collections.sort(data2);
            // verify that the raw data all got recorded
            Assert.assertEquals(data.size(), data2.size());
            Iterator<Double> ix = data.iterator();
            for (Double x : data2) {
                Assert.assertEquals(ix.next(), x);
            }
            // now merge the sub-digests
            TDigest dist2 = TDigest.merge(50, subs);
            for (double q : new double[]{ 0.001, 0.01, 0.1, 0.2, 0.3, 0.5 }) {
                double z = quantile(q, data);
                double e1 = (dist.quantile(q)) - z;
                double e2 = (dist2.quantile(q)) - z;
                System.out.printf("quantile\t%d\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\n", parts, q, (z - q), e1, e2, ((Math.abs(e2)) / q));
                Assert.assertTrue(String.format("parts=%d, q=%.4f, e1=%.5f, e2=%.5f, rel=%.4f", parts, q, e1, e2, ((Math.abs(e2)) / q)), (((Math.abs(e2)) / q) < 0.1));
                Assert.assertTrue(String.format("parts=%d, q=%.4f, e1=%.5f, e2=%.5f, rel=%.4f", parts, q, e1, e2, ((Math.abs(e2)) / q)), ((Math.abs(e2)) < 0.015));
            }
            for (double x : new double[]{ 0.001, 0.01, 0.1, 0.2, 0.3, 0.5 }) {
                double z = cdf(x, data);
                double e1 = (dist.cdf(x)) - z;
                double e2 = (dist2.cdf(x)) - z;
                System.out.printf("cdf\t%d\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\n", parts, x, (z - x), e1, e2, ((Math.abs(e2)) / x));
                Assert.assertTrue(String.format("parts=%d, x=%.4f, e1=%.5f, e2=%.5f", parts, x, e1, e2), ((Math.abs(e2)) < 0.015));
                Assert.assertTrue(String.format("parts=%d, x=%.4f, e1=%.5f, e2=%.5f", parts, x, e1, e2), (((Math.abs(e2)) / x) < 0.1));
            }
        }
    }
}

