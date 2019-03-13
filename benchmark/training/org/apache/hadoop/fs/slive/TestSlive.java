/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.slive;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.fs.slive.Constants.OperationType.values;


/**
 * Junit 4 test for slive
 */
public class TestSlive {
    private static final Logger LOG = LoggerFactory.getLogger(TestSlive.class);

    private static final Random rnd = new Random(1L);

    private static final String TEST_DATA_PROP = "test.build.data";

    @Test
    public void testFinder() throws Exception {
        ConfigExtractor extractor = getTestConfig(false);
        PathFinder fr = new PathFinder(extractor, TestSlive.rnd);
        // should only be able to select 10 files
        // attempt for a given amount of iterations
        int maxIterations = 10000;
        Set<Path> files = new HashSet<Path>();
        for (int i = 0; i < maxIterations; i++) {
            files.add(fr.getFile());
        }
        Assert.assertTrue(((files.size()) == 10));
        Set<Path> dirs = new HashSet<Path>();
        for (int i = 0; i < maxIterations; i++) {
            dirs.add(fr.getDirectory());
        }
        Assert.assertTrue(((dirs.size()) == 10));
    }

    @Test
    public void testSelection() throws Exception {
        ConfigExtractor extractor = getTestConfig(false);
        WeightSelector selector = new WeightSelector(extractor, TestSlive.rnd);
        // should be 1 of each type - uniform
        int expected = values().length;
        Operation op = null;
        Set<String> types = new HashSet<String>();
        FileSystem fs = FileSystem.get(extractor.getConfig());
        while (true) {
            op = selector.select(1, 1);
            if (op == null) {
                break;
            }
            // doesn't matter if they work or not
            op.run(fs);
            types.add(op.getType());
        } 
        Assert.assertEquals(types.size(), expected);
    }

    @Test
    public void testArguments() throws Exception {
        ConfigExtractor extractor = getTestConfig(true);
        Assert.assertEquals(extractor.getOpCount().intValue(), values().length);
        Assert.assertEquals(extractor.getMapAmount().intValue(), 2);
        Assert.assertEquals(extractor.getReducerAmount().intValue(), 2);
        Range<Long> apRange = extractor.getAppendSize();
        Assert.assertEquals(apRange.getLower().intValue(), ((Constants.MEGABYTES) * 1));
        Assert.assertEquals(apRange.getUpper().intValue(), ((Constants.MEGABYTES) * 2));
        Range<Long> wRange = extractor.getWriteSize();
        Assert.assertEquals(wRange.getLower().intValue(), ((Constants.MEGABYTES) * 1));
        Assert.assertEquals(wRange.getUpper().intValue(), ((Constants.MEGABYTES) * 2));
        Range<Long> trRange = extractor.getTruncateSize();
        Assert.assertEquals(trRange.getLower().intValue(), 0);
        Assert.assertEquals(trRange.getUpper().intValue(), ((Constants.MEGABYTES) * 1));
        Range<Long> bRange = extractor.getBlockSize();
        Assert.assertEquals(bRange.getLower().intValue(), ((Constants.MEGABYTES) * 1));
        Assert.assertEquals(bRange.getUpper().intValue(), ((Constants.MEGABYTES) * 2));
        String resfile = extractor.getResultFile();
        Assert.assertEquals(resfile, TestSlive.getResultFile().toString());
        int durationMs = extractor.getDurationMilliseconds();
        Assert.assertEquals(durationMs, (10 * 1000));
    }

    @Test
    public void testDataWriting() throws Exception {
        long byteAm = 100;
        File fn = TestSlive.getTestFile();
        DataWriter writer = new DataWriter(TestSlive.rnd);
        FileOutputStream fs = new FileOutputStream(fn);
        DataWriter.GenerateOutput ostat = writer.writeSegment(byteAm, fs);
        TestSlive.LOG.info(ostat.toString());
        fs.close();
        Assert.assertTrue(((ostat.getBytesWritten()) == byteAm));
        DataVerifier vf = new DataVerifier();
        FileInputStream fin = new FileInputStream(fn);
        DataVerifier.VerifyOutput vfout = vf.verifyFile(byteAm, new DataInputStream(fin));
        TestSlive.LOG.info(vfout.toString());
        fin.close();
        Assert.assertEquals(vfout.getBytesRead(), byteAm);
        Assert.assertTrue(((vfout.getChunksDifferent()) == 0));
    }

    @Test
    public void testRange() {
        Range<Long> r = new Range<Long>(10L, 20L);
        Assert.assertEquals(r.getLower().longValue(), 10L);
        Assert.assertEquals(r.getUpper().longValue(), 20L);
    }

    @Test
    public void testCreateOp() throws Exception {
        // setup a valid config
        ConfigExtractor extractor = getTestConfig(false);
        final Path fn = new Path(TestSlive.getTestFile().getCanonicalPath());
        CreateOp op = new CreateOp(extractor, TestSlive.rnd) {
            protected Path getCreateFile() {
                return fn;
            }
        };
        runOperationOk(extractor, op, true);
    }

    @Test
    public void testOpFailures() throws Exception {
        ConfigExtractor extractor = getTestConfig(false);
        final Path fn = new Path(TestSlive.getImaginaryFile().getCanonicalPath());
        ReadOp rop = new ReadOp(extractor, TestSlive.rnd) {
            protected Path getReadFile() {
                return fn;
            }
        };
        runOperationBad(extractor, rop);
        DeleteOp dop = new DeleteOp(extractor, TestSlive.rnd) {
            protected Path getDeleteFile() {
                return fn;
            }
        };
        runOperationBad(extractor, dop);
        RenameOp reop = new RenameOp(extractor, TestSlive.rnd) {
            protected RenameOp.SrcTarget getRenames() {
                return new RenameOp.SrcTarget(fn, fn);
            }
        };
        runOperationBad(extractor, reop);
        AppendOp aop = new AppendOp(extractor, TestSlive.rnd) {
            protected Path getAppendFile() {
                return fn;
            }
        };
        runOperationBad(extractor, aop);
    }

    @Test
    public void testDelete() throws Exception {
        ConfigExtractor extractor = getTestConfig(false);
        final Path fn = new Path(TestSlive.getTestFile().getCanonicalPath());
        // ensure file created before delete
        CreateOp op = new CreateOp(extractor, TestSlive.rnd) {
            protected Path getCreateFile() {
                return fn;
            }
        };
        runOperationOk(extractor, op, true);
        // now delete
        DeleteOp dop = new DeleteOp(extractor, TestSlive.rnd) {
            protected Path getDeleteFile() {
                return fn;
            }
        };
        runOperationOk(extractor, dop, true);
    }

    @Test
    public void testRename() throws Exception {
        ConfigExtractor extractor = getTestConfig(false);
        final Path src = new Path(TestSlive.getTestFile().getCanonicalPath());
        final Path tgt = new Path(TestSlive.getTestRenameFile().getCanonicalPath());
        // ensure file created before rename
        CreateOp op = new CreateOp(extractor, TestSlive.rnd) {
            protected Path getCreateFile() {
                return src;
            }
        };
        runOperationOk(extractor, op, true);
        RenameOp rop = new RenameOp(extractor, TestSlive.rnd) {
            protected RenameOp.SrcTarget getRenames() {
                return new RenameOp.SrcTarget(src, tgt);
            }
        };
        runOperationOk(extractor, rop, true);
    }

    @Test
    public void testMRFlow() throws Exception {
        ConfigExtractor extractor = getTestConfig(false);
        SliveTest s = new SliveTest(TestSlive.getBaseConfig());
        int ec = ToolRunner.run(s, getTestArgs(false));
        Assert.assertTrue((ec == 0));
        String resFile = extractor.getResultFile();
        File fn = new File(resFile);
        Assert.assertTrue(fn.exists());
        // can't validate completely since operations may fail (mainly anyone but
        // create +mkdir) since they may not find there files
    }

    @Test
    public void testRead() throws Exception {
        ConfigExtractor extractor = getTestConfig(false);
        final Path fn = new Path(TestSlive.getTestFile().getCanonicalPath());
        // ensure file created before read
        CreateOp op = new CreateOp(extractor, TestSlive.rnd) {
            protected Path getCreateFile() {
                return fn;
            }
        };
        runOperationOk(extractor, op, true);
        ReadOp rop = new ReadOp(extractor, TestSlive.rnd) {
            protected Path getReadFile() {
                return fn;
            }
        };
        runOperationOk(extractor, rop, true);
    }

    @Test
    public void testSleep() throws Exception {
        ConfigExtractor extractor = getTestConfig(true);
        SleepOp op = new SleepOp(extractor, TestSlive.rnd);
        runOperationOk(extractor, op, true);
    }

    @Test
    public void testList() throws Exception {
        // ensure dir made
        ConfigExtractor extractor = getTestConfig(false);
        final Path dir = new Path(TestSlive.getTestDir().getCanonicalPath());
        MkdirOp op = new MkdirOp(extractor, TestSlive.rnd) {
            protected Path getDirectory() {
                return dir;
            }
        };
        runOperationOk(extractor, op, true);
        // list it
        ListOp lop = new ListOp(extractor, TestSlive.rnd) {
            protected Path getDirectory() {
                return dir;
            }
        };
        runOperationOk(extractor, lop, true);
    }

    @Test
    public void testBadChunks() throws Exception {
        File fn = TestSlive.getTestFile();
        int byteAm = 10000;
        FileOutputStream fout = new FileOutputStream(fn);
        byte[] bytes = new byte[byteAm];
        TestSlive.rnd.nextBytes(bytes);
        fout.write(bytes);
        fout.close();
        // attempt to read it
        DataVerifier vf = new DataVerifier();
        DataVerifier.VerifyOutput vout = new DataVerifier.VerifyOutput(0, 0, 0, 0);
        DataInputStream in = null;
        try {
            in = new DataInputStream(new FileInputStream(fn));
            vout = vf.verifyFile(byteAm, in);
        } catch (Exception e) {
        } finally {
            if (in != null)
                in.close();

        }
        Assert.assertTrue(((vout.getChunksSame()) == 0));
    }

    @Test
    public void testMkdir() throws Exception {
        ConfigExtractor extractor = getTestConfig(false);
        final Path dir = new Path(TestSlive.getTestDir().getCanonicalPath());
        MkdirOp op = new MkdirOp(extractor, TestSlive.rnd) {
            protected Path getDirectory() {
                return dir;
            }
        };
        runOperationOk(extractor, op, true);
    }

    @Test
    public void testSelector() throws Exception {
        ConfigExtractor extractor = getTestConfig(false);
        RouletteSelector selector = new RouletteSelector(TestSlive.rnd);
        List<OperationWeight> sList = new LinkedList<OperationWeight>();
        Operation op = selector.select(sList);
        Assert.assertTrue((op == null));
        CreateOp cop = new CreateOp(extractor, TestSlive.rnd);
        sList.add(new OperationWeight(cop, 1.0));
        AppendOp aop = new AppendOp(extractor, TestSlive.rnd);
        sList.add(new OperationWeight(aop, 0.01));
        op = selector.select(sList);
        Assert.assertTrue((op == cop));
    }

    @Test
    public void testAppendOp() throws Exception {
        // setup a valid config
        ConfigExtractor extractor = getTestConfig(false);
        // ensure file created before append
        final Path fn = new Path(TestSlive.getTestFile().getCanonicalPath());
        CreateOp op = new CreateOp(extractor, TestSlive.rnd) {
            protected Path getCreateFile() {
                return fn;
            }
        };
        runOperationOk(extractor, op, true);
        // local file system (ChecksumFileSystem) currently doesn't support append -
        // but we'll leave this test here anyways but can't check the results..
        AppendOp aop = new AppendOp(extractor, TestSlive.rnd) {
            protected Path getAppendFile() {
                return fn;
            }
        };
        runOperationOk(extractor, aop, false);
    }

    @Test
    public void testTruncateOp() throws Exception {
        // setup a valid config
        ConfigExtractor extractor = getTestConfig(false);
        // ensure file created before append
        final Path fn = new Path(TestSlive.getTestFile().getCanonicalPath());
        CreateOp op = new CreateOp(extractor, TestSlive.rnd) {
            protected Path getCreateFile() {
                return fn;
            }
        };
        runOperationOk(extractor, op, true);
        // local file system (ChecksumFileSystem) currently doesn't support truncate -
        // but we'll leave this test here anyways but can't check the results..
        TruncateOp top = new TruncateOp(extractor, TestSlive.rnd) {
            protected Path getTruncateFile() {
                return fn;
            }
        };
        runOperationOk(extractor, top, false);
    }
}

