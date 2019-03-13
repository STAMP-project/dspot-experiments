/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.datavec.spark.transform.analysis;


import DateTimeZone.UTC;
import com.tdunning.math.stats.TDigest;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.util.StatCounter;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.transform.analysis.DataAnalysis;
import org.datavec.api.transform.quality.DataQualityAnalysis;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.ui.HtmlAnalysis;
import org.datavec.local.transforms.AnalyzeLocal;
import org.datavec.spark.BaseSparkTest;
import org.datavec.spark.transform.AnalyzeSpark;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * Created by Alex on 23/06/2016.
 */
public class TestAnalysis extends BaseSparkTest {
    @Test
    public void testAnalysis() throws Exception {
        Schema schema = new Schema.Builder().addColumnInteger("intCol").addColumnDouble("doubleCol").addColumnTime("timeCol", UTC).addColumnCategorical("catCol", "A", "B").addColumnNDArray("ndarray", new long[]{ 1, 10 }).build();
        List<List<Writable>> data = new ArrayList<>();
        data.add(Arrays.asList(((Writable) (new IntWritable(0))), new DoubleWritable(1.0), new LongWritable(1000), new Text("A"), new NDArrayWritable(Nd4j.valueArrayOf(10, 100.0))));
        data.add(Arrays.asList(((Writable) (new IntWritable(5))), new DoubleWritable(0.0), new LongWritable(2000), new Text("A"), new NDArrayWritable(Nd4j.valueArrayOf(10, 200.0))));
        data.add(Arrays.asList(((Writable) (new IntWritable(3))), new DoubleWritable(10.0), new LongWritable(3000), new Text("A"), new NDArrayWritable(Nd4j.valueArrayOf(10, 300.0))));
        data.add(Arrays.asList(((Writable) (new IntWritable((-1)))), new DoubleWritable((-1.0)), new LongWritable(20000), new Text("B"), new NDArrayWritable(Nd4j.valueArrayOf(10, 400.0))));
        JavaRDD<List<Writable>> rdd = BaseSparkTest.sc.parallelize(data);
        DataAnalysis da = AnalyzeSpark.analyze(schema, rdd);
        String daString = da.toString();
        System.out.println(da);
        List<ColumnAnalysis> ca = da.getColumnAnalysis();
        Assert.assertEquals(5, ca.size());
        Assert.assertTrue(((ca.get(0)) instanceof IntegerAnalysis));
        Assert.assertTrue(((ca.get(1)) instanceof DoubleAnalysis));
        Assert.assertTrue(((ca.get(2)) instanceof TimeAnalysis));
        Assert.assertTrue(((ca.get(3)) instanceof CategoricalAnalysis));
        Assert.assertTrue(((ca.get(4)) instanceof NDArrayAnalysis));
        IntegerAnalysis ia = ((IntegerAnalysis) (ca.get(0)));
        Assert.assertEquals((-1), ia.getMin());
        Assert.assertEquals(5, ia.getMax());
        Assert.assertEquals(4, ia.getCountTotal());
        TDigest itd = ia.getDigest();
        Assert.assertEquals((-0.5), itd.quantile(0.25), 1.0E-9);// right-biased linear approximations w/ few points

        Assert.assertEquals(1.5, itd.quantile(0.5), 1.0E-9);
        Assert.assertEquals(4.0, itd.quantile(0.75), 1.0E-9);
        Assert.assertEquals(5.0, itd.quantile(1), 1.0E-9);
        DoubleAnalysis dba = ((DoubleAnalysis) (ca.get(1)));
        Assert.assertEquals((-1.0), dba.getMin(), 0.0);
        Assert.assertEquals(10.0, dba.getMax(), 0.0);
        Assert.assertEquals(4, dba.getCountTotal());
        TDigest dtd = dba.getDigest();
        Assert.assertEquals((-0.5), dtd.quantile(0.25), 1.0E-9);// right-biased linear approximations w/ few points

        Assert.assertEquals(0.5, dtd.quantile(0.5), 1.0E-9);
        Assert.assertEquals(5.5, dtd.quantile(0.75), 1.0E-9);
        Assert.assertEquals(10.0, dtd.quantile(1), 1.0E-9);
        TimeAnalysis ta = ((TimeAnalysis) (ca.get(2)));
        Assert.assertEquals(1000, ta.getMin());
        Assert.assertEquals(20000, ta.getMax());
        Assert.assertEquals(4, ta.getCountTotal());
        TDigest ttd = ta.getDigest();
        Assert.assertEquals(1500.0, ttd.quantile(0.25), 1.0E-9);// right-biased linear approximations w/ few points

        Assert.assertEquals(2500.0, ttd.quantile(0.5), 1.0E-9);
        Assert.assertEquals(11500.0, ttd.quantile(0.75), 1.0E-9);
        Assert.assertEquals(20000.0, ttd.quantile(1), 1.0E-9);
        CategoricalAnalysis cata = ((CategoricalAnalysis) (ca.get(3)));
        Map<String, Long> map = cata.getMapOfCounts();
        Assert.assertEquals(2, map.keySet().size());
        Assert.assertEquals(3L, ((long) (map.get("A"))));
        Assert.assertEquals(1L, ((long) (map.get("B"))));
        NDArrayAnalysis na = ((NDArrayAnalysis) (ca.get(4)));
        Assert.assertEquals(4, na.getCountTotal());
        Assert.assertEquals(0, na.getCountNull());
        Assert.assertEquals(10, na.getMinLength());
        Assert.assertEquals(10, na.getMaxLength());
        Assert.assertEquals((4 * 10), na.getTotalNDArrayValues());
        Assert.assertEquals(Collections.singletonMap(2, 4L), na.getCountsByRank());
        Assert.assertEquals(100.0, na.getMinValue(), 0.0);
        Assert.assertEquals(400.0, na.getMaxValue(), 0.0);
        Assert.assertNotNull(ia.getHistogramBuckets());
        Assert.assertNotNull(ia.getHistogramBucketCounts());
        Assert.assertNotNull(dba.getHistogramBuckets());
        Assert.assertNotNull(dba.getHistogramBucketCounts());
        Assert.assertNotNull(ta.getHistogramBuckets());
        Assert.assertNotNull(ta.getHistogramBucketCounts());
        Assert.assertNotNull(na.getHistogramBuckets());
        Assert.assertNotNull(na.getHistogramBucketCounts());
        double[] bucketsD = dba.getHistogramBuckets();
        long[] countD = dba.getHistogramBucketCounts();
        Assert.assertEquals((-1.0), bucketsD[0], 0.0);
        Assert.assertEquals(10.0, bucketsD[((bucketsD.length) - 1)], 0.0);
        Assert.assertEquals(1, countD[0]);
        Assert.assertEquals(1, countD[((countD.length) - 1)]);
        File f = Files.createTempFile("datavec_spark_analysis_UITest", ".html").toFile();
        System.out.println(f.getAbsolutePath());
        f.deleteOnExit();
        HtmlAnalysis.createHtmlAnalysisFile(da, f);
    }

    @Test
    public void testAnalysisStdev() {
        // Test stdev calculations compared to Spark's stats calculation
        Random r = new Random(12345);
        List<Double> l1 = new ArrayList<>();
        List<Integer> l2 = new ArrayList<>();
        List<Long> l3 = new ArrayList<>();
        int n = 10000;
        for (int i = 0; i < n; i++) {
            l1.add((10 * (r.nextDouble())));
            l2.add(((-1000) + (r.nextInt(2000))));
            l3.add(((-1000L) + (r.nextInt(2000))));
        }
        List<Double> l2d = new ArrayList<>();
        for (Integer i : l2)
            l2d.add(i.doubleValue());

        List<Double> l3d = new ArrayList<>();
        for (Long l : l3)
            l3d.add(l.doubleValue());

        StatCounter sc1 = BaseSparkTest.sc.parallelizeDoubles(l1).stats();
        StatCounter sc2 = BaseSparkTest.sc.parallelizeDoubles(l2d).stats();
        StatCounter sc3 = BaseSparkTest.sc.parallelizeDoubles(l3d).stats();
        org.datavec.api.transform.analysis.counter.StatCounter sc1new = new org.datavec.api.transform.analysis.counter.StatCounter();
        for (double d : l1) {
            sc1new.add(d);
        }
        Assert.assertEquals(sc1.sampleStdev(), sc1new.getStddev(false), 1.0E-6);
        List<StatCounter> sparkCounters = new ArrayList<>();
        List<org.datavec.api.transform.analysis.counter.StatCounter> counters = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            counters.add(new org.datavec.api.transform.analysis.counter.StatCounter());
            sparkCounters.add(new StatCounter());
        }
        for (int i = 0; i < (l1.size()); i++) {
            int idx = i % 10;
            counters.get(idx).add(l1.get(i));
            sparkCounters.get(idx).merge(l1.get(i));
        }
        org.datavec.api.transform.analysis.counter.StatCounter counter = counters.get(0);
        StatCounter sparkCounter = sparkCounters.get(0);
        for (int i = 1; i < 10; i++) {
            counter.merge(counters.get(i));
            sparkCounter.merge(sparkCounters.get(i));
            System.out.println();
        }
        Assert.assertEquals(sc1.sampleStdev(), counter.getStddev(false), 1.0E-6);
        Assert.assertEquals(sparkCounter.sampleStdev(), counter.getStddev(false), 1.0E-6);
        List<List<Writable>> data = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Writable> l = new ArrayList<>();
            l.add(new DoubleWritable(l1.get(i)));
            l.add(new IntWritable(l2.get(i)));
            l.add(new LongWritable(l3.get(i)));
            data.add(l);
        }
        Schema schema = new Schema.Builder().addColumnDouble("d").addColumnInteger("i").addColumnLong("l").build();
        JavaRDD<List<Writable>> rdd = BaseSparkTest.sc.parallelize(data);
        DataAnalysis da = AnalyzeSpark.analyze(schema, rdd);
        double stdev1 = sc1.sampleStdev();
        double stdev1a = getSampleStdev();
        double re1 = (Math.abs((stdev1 - stdev1a))) / ((Math.abs(stdev1)) + (Math.abs(stdev1a)));
        Assert.assertTrue((re1 < 1.0E-6));
        double stdev2 = sc2.sampleStdev();
        double stdev2a = getSampleStdev();
        double re2 = (Math.abs((stdev2 - stdev2a))) / ((Math.abs(stdev2)) + (Math.abs(stdev2a)));
        Assert.assertTrue((re2 < 1.0E-6));
        double stdev3 = sc3.sampleStdev();
        double stdev3a = getSampleStdev();
        double re3 = (Math.abs((stdev3 - stdev3a))) / ((Math.abs(stdev3)) + (Math.abs(stdev3a)));
        Assert.assertTrue((re3 < 1.0E-6));
    }

    @Test
    public void testSampleMostFrequent() {
        List<List<Writable>> toParallelize = new ArrayList<>();
        toParallelize.add(Arrays.<Writable>asList(new Text("a"), new Text("MostCommon")));
        toParallelize.add(Arrays.<Writable>asList(new Text("b"), new Text("SecondMostCommon")));
        toParallelize.add(Arrays.<Writable>asList(new Text("c"), new Text("SecondMostCommon")));
        toParallelize.add(Arrays.<Writable>asList(new Text("d"), new Text("0")));
        toParallelize.add(Arrays.<Writable>asList(new Text("e"), new Text("MostCommon")));
        toParallelize.add(Arrays.<Writable>asList(new Text("f"), new Text("ThirdMostCommon")));
        toParallelize.add(Arrays.<Writable>asList(new Text("c"), new Text("MostCommon")));
        toParallelize.add(Arrays.<Writable>asList(new Text("h"), new Text("1")));
        toParallelize.add(Arrays.<Writable>asList(new Text("i"), new Text("SecondMostCommon")));
        toParallelize.add(Arrays.<Writable>asList(new Text("j"), new Text("2")));
        toParallelize.add(Arrays.<Writable>asList(new Text("k"), new Text("ThirdMostCommon")));
        toParallelize.add(Arrays.<Writable>asList(new Text("l"), new Text("MostCommon")));
        toParallelize.add(Arrays.<Writable>asList(new Text("m"), new Text("3")));
        toParallelize.add(Arrays.<Writable>asList(new Text("n"), new Text("4")));
        toParallelize.add(Arrays.<Writable>asList(new Text("o"), new Text("5")));
        JavaRDD<List<Writable>> rdd = BaseSparkTest.sc.parallelize(toParallelize);
        Schema schema = new Schema.Builder().addColumnsString("irrelevant", "column").build();
        Map<Writable, Long> map = AnalyzeSpark.sampleMostFrequentFromColumn(3, "column", schema, rdd);
        // System.out.println(map);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals(4L, ((long) (map.get(new Text("MostCommon")))));
        Assert.assertEquals(3L, ((long) (map.get(new Text("SecondMostCommon")))));
        Assert.assertEquals(2L, ((long) (map.get(new Text("ThirdMostCommon")))));
    }

    @Test
    public void testAnalysisVsLocal() throws Exception {
        Schema s = new Schema.Builder().addColumnsDouble("%d", 0, 3).addColumnInteger("label").build();
        RecordReader rr = new CSVRecordReader();
        rr.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("iris.txt").getFile()));
        List<List<Writable>> toParallelize = new ArrayList<>();
        while (rr.hasNext()) {
            toParallelize.add(rr.next());
        } 
        JavaRDD<List<Writable>> rdd = BaseSparkTest.sc.parallelize(toParallelize).coalesce(1);
        rr.reset();
        DataAnalysis local = AnalyzeLocal.analyze(s, rr);
        DataAnalysis spark = AnalyzeSpark.analyze(s, rdd);
        // assertEquals(local.toJson(), spark.toJson());
        Assert.assertEquals(local, spark);
        // Also quality analysis:
        rr.reset();
        DataQualityAnalysis localQ = AnalyzeLocal.analyzeQuality(s, rr);
        DataQualityAnalysis sparkQ = AnalyzeSpark.analyzeQuality(s, rdd);
        Assert.assertEquals(localQ, sparkQ);
        // And, check unique etc:
        rr.reset();
        Map<String, Set<Writable>> mapLocal = AnalyzeLocal.getUnique(s.getColumnNames(), s, rr);
        Map<String, List<Writable>> mapSpark = AnalyzeSpark.getUnique(s.getColumnNames(), s, rdd);
        Assert.assertEquals(mapLocal.keySet(), mapSpark.keySet());
        for (String k : mapLocal.keySet()) {
            Assert.assertEquals(mapLocal.get(k), new HashSet<Writable>(mapSpark.get(k)));
        }
    }
}

