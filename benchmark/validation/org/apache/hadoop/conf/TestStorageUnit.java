/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.conf;


import StorageUnit.BYTES;
import StorageUnit.EB;
import StorageUnit.GB;
import StorageUnit.KB;
import StorageUnit.MB;
import StorageUnit.PB;
import StorageUnit.TB;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * Tests that Storage Units work as expected.
 */
public class TestStorageUnit {
    static final double KB = 1024.0;

    static final double MB = (TestStorageUnit.KB) * 1024.0;

    static final double GB = (TestStorageUnit.MB) * 1024.0;

    static final double TB = (TestStorageUnit.GB) * 1024.0;

    static final double PB = (TestStorageUnit.TB) * 1024.0;

    static final double EB = (TestStorageUnit.PB) * 1024.0;

    @Test
    public void testByteToKiloBytes() {
        Map<Double, Double> results = new HashMap<>();
        results.put(1024.0, 1.0);
        results.put(2048.0, 2.0);
        results.put((-1024.0), (-1.0));
        results.put(34565.0, 33.7549);
        results.put(2.23344332E8, 218109.6992);
        results.put(1234983.0, 1206.0381);
        results.put(1234332.0, 1205.4023);
        results.put(0.0, 0.0);
        for (Map.Entry<Double, Double> entry : results.entrySet()) {
            MatcherAssert.assertThat(BYTES.toKBs(entry.getKey()), CoreMatchers.is(entry.getValue()));
        }
    }

    @Test
    public void testBytesToMegaBytes() {
        Map<Double, Double> results = new HashMap<>();
        results.put(1048576.0, 1.0);
        results.put(2.4117248E7, 23.0);
        results.put(4.59920023E8, 438.6139);
        results.put(2.34443233E8, 223.5825);
        results.put((-3.5651584E7), (-34.0));
        results.put(0.0, 0.0);
        for (Map.Entry<Double, Double> entry : results.entrySet()) {
            MatcherAssert.assertThat(BYTES.toMBs(entry.getKey()), CoreMatchers.is(entry.getValue()));
        }
    }

    @Test
    public void testBytesToGigaBytes() {
        Map<Double, Double> results = new HashMap<>();
        results.put(1.073741824E9, 1.0);
        results.put(2.4696061952E10, 23.0);
        results.put(4.59920023E8, 0.4283);
        results.put(2.34443233E8, 0.2183);
        results.put((-3.6507222016E10), (-34.0));
        results.put(0.0, 0.0);
        for (Map.Entry<Double, Double> entry : results.entrySet()) {
            MatcherAssert.assertThat(BYTES.toGBs(entry.getKey()), CoreMatchers.is(entry.getValue()));
        }
    }

    @Test
    public void testBytesToTerraBytes() {
        Map<Double, Double> results = new HashMap<>();
        results.put(1.09951E12, 1.0);
        results.put(2.52888E13, 23.0);
        results.put(4.59920023E8, 4.0E-4);
        results.put(2.34443233E8, 2.0E-4);
        results.put((-3.73834E13), (-34.0));
        results.put(0.0, 0.0);
        for (Map.Entry<Double, Double> entry : results.entrySet()) {
            MatcherAssert.assertThat(BYTES.toTBs(entry.getKey()), CoreMatchers.is(entry.getValue()));
        }
    }

    @Test
    public void testBytesToPetaBytes() {
        Map<Double, Double> results = new HashMap<>();
        results.put(1.1259E15, 1.0);
        results.put(2.58957E16, 23.0);
        results.put(4.70958E11, 4.0E-4);
        results.put(2.34443233E8, 0.0);// Out of precision window.

        results.put((-3.82806E16), (-34.0));
        results.put(0.0, 0.0);
        for (Map.Entry<Double, Double> entry : results.entrySet()) {
            MatcherAssert.assertThat(BYTES.toPBs(entry.getKey()), CoreMatchers.is(entry.getValue()));
        }
    }

    @Test
    public void testBytesToExaBytes() {
        Map<Double, Double> results = new HashMap<>();
        results.put(1.15292E18, 1.0);
        results.put(2.65172E19, 23.0);
        results.put(4.82261E14, 4.0E-4);
        results.put(2.34443233E8, 0.0);// Out of precision window.

        results.put((-3.91993E19), (-34.0));
        results.put(0.0, 0.0);
        for (Map.Entry<Double, Double> entry : results.entrySet()) {
            MatcherAssert.assertThat(BYTES.toEBs(entry.getKey()), CoreMatchers.is(entry.getValue()));
        }
    }

    @Test
    public void testByteConversions() {
        MatcherAssert.assertThat(BYTES.getShortName(), CoreMatchers.is("b"));
        MatcherAssert.assertThat(BYTES.getSuffixChar(), CoreMatchers.is("b"));
        MatcherAssert.assertThat(BYTES.getLongName(), CoreMatchers.is("bytes"));
        MatcherAssert.assertThat(BYTES.toString(), CoreMatchers.is("bytes"));
        MatcherAssert.assertThat(BYTES.toBytes(1), CoreMatchers.is(1.0));
        MatcherAssert.assertThat(BYTES.toBytes(1024), CoreMatchers.is(BYTES.getDefault(1024)));
        MatcherAssert.assertThat(BYTES.fromBytes(10), CoreMatchers.is(10.0));
    }

    @Test
    public void testKBConversions() {
        MatcherAssert.assertThat(StorageUnit.KB.getShortName(), CoreMatchers.is("kb"));
        MatcherAssert.assertThat(StorageUnit.KB.getSuffixChar(), CoreMatchers.is("k"));
        MatcherAssert.assertThat(StorageUnit.KB.getLongName(), CoreMatchers.is("kilobytes"));
        MatcherAssert.assertThat(StorageUnit.KB.toString(), CoreMatchers.is("kilobytes"));
        MatcherAssert.assertThat(StorageUnit.KB.toKBs(1024), CoreMatchers.is(StorageUnit.KB.getDefault(1024)));
        MatcherAssert.assertThat(StorageUnit.KB.toBytes(1), CoreMatchers.is(TestStorageUnit.KB));
        MatcherAssert.assertThat(StorageUnit.KB.fromBytes(TestStorageUnit.KB), CoreMatchers.is(1.0));
        MatcherAssert.assertThat(StorageUnit.KB.toKBs(10), CoreMatchers.is(10.0));
        MatcherAssert.assertThat(StorageUnit.KB.toMBs((3.0 * 1024.0)), CoreMatchers.is(3.0));
        MatcherAssert.assertThat(StorageUnit.KB.toGBs(1073741824), CoreMatchers.is(1024.0));
        MatcherAssert.assertThat(StorageUnit.KB.toTBs(1073741824), CoreMatchers.is(1.0));
        MatcherAssert.assertThat(StorageUnit.KB.toPBs(1.0995116E12), CoreMatchers.is(1.0));
        MatcherAssert.assertThat(StorageUnit.KB.toEBs(1.1258999E15), CoreMatchers.is(1.0));
    }

    @Test
    public void testMBConversions() {
        MatcherAssert.assertThat(StorageUnit.MB.getShortName(), CoreMatchers.is("mb"));
        MatcherAssert.assertThat(StorageUnit.MB.getSuffixChar(), CoreMatchers.is("m"));
        MatcherAssert.assertThat(StorageUnit.MB.getLongName(), CoreMatchers.is("megabytes"));
        MatcherAssert.assertThat(StorageUnit.MB.toString(), CoreMatchers.is("megabytes"));
        MatcherAssert.assertThat(StorageUnit.MB.toMBs(1024), CoreMatchers.is(StorageUnit.MB.getDefault(1024)));
        MatcherAssert.assertThat(StorageUnit.MB.toBytes(1), CoreMatchers.is(TestStorageUnit.MB));
        MatcherAssert.assertThat(StorageUnit.MB.fromBytes(TestStorageUnit.MB), CoreMatchers.is(1.0));
        MatcherAssert.assertThat(StorageUnit.MB.toKBs(1), CoreMatchers.is(1024.0));
        MatcherAssert.assertThat(StorageUnit.MB.toMBs(10), CoreMatchers.is(10.0));
        MatcherAssert.assertThat(StorageUnit.MB.toGBs(44040192), CoreMatchers.is(43008.0));
        MatcherAssert.assertThat(StorageUnit.MB.toTBs(1073741824), CoreMatchers.is(1024.0));
        MatcherAssert.assertThat(StorageUnit.MB.toPBs(1073741824), CoreMatchers.is(1.0));
        MatcherAssert.assertThat(StorageUnit.MB.toEBs((1 * ((TestStorageUnit.EB) / (TestStorageUnit.MB)))), CoreMatchers.is(1.0));
    }

    @Test
    public void testGBConversions() {
        MatcherAssert.assertThat(StorageUnit.GB.getShortName(), CoreMatchers.is("gb"));
        MatcherAssert.assertThat(StorageUnit.GB.getSuffixChar(), CoreMatchers.is("g"));
        MatcherAssert.assertThat(StorageUnit.GB.getLongName(), CoreMatchers.is("gigabytes"));
        MatcherAssert.assertThat(StorageUnit.GB.toString(), CoreMatchers.is("gigabytes"));
        MatcherAssert.assertThat(StorageUnit.GB.toGBs(1024), CoreMatchers.is(StorageUnit.GB.getDefault(1024)));
        MatcherAssert.assertThat(StorageUnit.GB.toBytes(1), CoreMatchers.is(TestStorageUnit.GB));
        MatcherAssert.assertThat(StorageUnit.GB.fromBytes(TestStorageUnit.GB), CoreMatchers.is(1.0));
        MatcherAssert.assertThat(StorageUnit.GB.toKBs(1), CoreMatchers.is((1024.0 * 1024)));
        MatcherAssert.assertThat(StorageUnit.GB.toMBs(10), CoreMatchers.is((10.0 * 1024)));
        MatcherAssert.assertThat(StorageUnit.GB.toGBs(4.4040192E7), CoreMatchers.is(4.4040192E7));
        MatcherAssert.assertThat(StorageUnit.GB.toTBs(1073741824), CoreMatchers.is(1048576.0));
        MatcherAssert.assertThat(StorageUnit.GB.toPBs(1.07375E9), CoreMatchers.is(1024.0078));
        MatcherAssert.assertThat(StorageUnit.GB.toEBs((1 * ((TestStorageUnit.EB) / (TestStorageUnit.GB)))), CoreMatchers.is(1.0));
    }

    @Test
    public void testTBConversions() {
        MatcherAssert.assertThat(StorageUnit.TB.getShortName(), CoreMatchers.is("tb"));
        MatcherAssert.assertThat(StorageUnit.TB.getSuffixChar(), CoreMatchers.is("t"));
        MatcherAssert.assertThat(StorageUnit.TB.getLongName(), CoreMatchers.is("terabytes"));
        MatcherAssert.assertThat(StorageUnit.TB.toString(), CoreMatchers.is("terabytes"));
        MatcherAssert.assertThat(StorageUnit.TB.toTBs(1024), CoreMatchers.is(StorageUnit.TB.getDefault(1024)));
        MatcherAssert.assertThat(StorageUnit.TB.toBytes(1), CoreMatchers.is(TestStorageUnit.TB));
        MatcherAssert.assertThat(StorageUnit.TB.fromBytes(TestStorageUnit.TB), CoreMatchers.is(1.0));
        MatcherAssert.assertThat(StorageUnit.TB.toKBs(1), CoreMatchers.is(((1024.0 * 1024) * 1024)));
        MatcherAssert.assertThat(StorageUnit.TB.toMBs(10), CoreMatchers.is(((10.0 * 1024) * 1024)));
        MatcherAssert.assertThat(StorageUnit.TB.toGBs(4.4040192E7), CoreMatchers.is(4.5097156608E10));
        MatcherAssert.assertThat(StorageUnit.TB.toTBs(1.073741824E9), CoreMatchers.is(1.073741824E9));
        MatcherAssert.assertThat(StorageUnit.TB.toPBs(1024), CoreMatchers.is(1.0));
        MatcherAssert.assertThat(StorageUnit.TB.toEBs((1 * ((TestStorageUnit.EB) / (TestStorageUnit.TB)))), CoreMatchers.is(1.0));
    }

    @Test
    public void testPBConversions() {
        MatcherAssert.assertThat(StorageUnit.PB.getShortName(), CoreMatchers.is("pb"));
        MatcherAssert.assertThat(StorageUnit.PB.getSuffixChar(), CoreMatchers.is("p"));
        MatcherAssert.assertThat(StorageUnit.PB.getLongName(), CoreMatchers.is("petabytes"));
        MatcherAssert.assertThat(StorageUnit.PB.toString(), CoreMatchers.is("petabytes"));
        MatcherAssert.assertThat(StorageUnit.PB.toPBs(1024), CoreMatchers.is(StorageUnit.PB.getDefault(1024)));
        MatcherAssert.assertThat(StorageUnit.PB.toBytes(1), CoreMatchers.is(TestStorageUnit.PB));
        MatcherAssert.assertThat(StorageUnit.PB.fromBytes(TestStorageUnit.PB), CoreMatchers.is(1.0));
        MatcherAssert.assertThat(StorageUnit.PB.toKBs(1), CoreMatchers.is(((TestStorageUnit.PB) / (TestStorageUnit.KB))));
        MatcherAssert.assertThat(StorageUnit.PB.toMBs(10), CoreMatchers.is((10.0 * ((TestStorageUnit.PB) / (TestStorageUnit.MB)))));
        MatcherAssert.assertThat(StorageUnit.PB.toGBs(4.4040192E7), CoreMatchers.is(((4.4040192E7 * (TestStorageUnit.PB)) / (TestStorageUnit.GB))));
        MatcherAssert.assertThat(StorageUnit.PB.toTBs(1.073741824E9), CoreMatchers.is((1.073741824E9 * ((TestStorageUnit.PB) / (TestStorageUnit.TB)))));
        MatcherAssert.assertThat(StorageUnit.PB.toPBs(1024.0), CoreMatchers.is(1024.0));
        MatcherAssert.assertThat(StorageUnit.PB.toEBs(1024.0), CoreMatchers.is(1.0));
    }

    @Test
    public void testEBConversions() {
        MatcherAssert.assertThat(StorageUnit.EB.getShortName(), CoreMatchers.is("eb"));
        MatcherAssert.assertThat(StorageUnit.EB.getSuffixChar(), CoreMatchers.is("e"));
        MatcherAssert.assertThat(StorageUnit.EB.getLongName(), CoreMatchers.is("exabytes"));
        MatcherAssert.assertThat(StorageUnit.EB.toString(), CoreMatchers.is("exabytes"));
        MatcherAssert.assertThat(StorageUnit.EB.toEBs(1024), CoreMatchers.is(StorageUnit.EB.getDefault(1024)));
        MatcherAssert.assertThat(StorageUnit.EB.toBytes(1), CoreMatchers.is(TestStorageUnit.EB));
        MatcherAssert.assertThat(StorageUnit.EB.fromBytes(TestStorageUnit.EB), CoreMatchers.is(1.0));
        MatcherAssert.assertThat(StorageUnit.EB.toKBs(1), CoreMatchers.is(((TestStorageUnit.EB) / (TestStorageUnit.KB))));
        MatcherAssert.assertThat(StorageUnit.EB.toMBs(10), CoreMatchers.is((10.0 * ((TestStorageUnit.EB) / (TestStorageUnit.MB)))));
        MatcherAssert.assertThat(StorageUnit.EB.toGBs(4.4040192E7), CoreMatchers.is(((4.4040192E7 * (TestStorageUnit.EB)) / (TestStorageUnit.GB))));
        MatcherAssert.assertThat(StorageUnit.EB.toTBs(1.073741824E9), CoreMatchers.is((1.073741824E9 * ((TestStorageUnit.EB) / (TestStorageUnit.TB)))));
        MatcherAssert.assertThat(StorageUnit.EB.toPBs(1.0), CoreMatchers.is(1024.0));
        MatcherAssert.assertThat(StorageUnit.EB.toEBs(42.0), CoreMatchers.is(42.0));
    }
}

