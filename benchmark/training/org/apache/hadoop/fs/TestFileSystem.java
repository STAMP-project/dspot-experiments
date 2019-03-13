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
package org.apache.hadoop.fs;


import FileSystem.CACHE;
import FileSystem.Cache.Key;
import HdfsClientConfigKeys.DFS_NAMENODE_RPC_PORT_DEFAULT;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.shell.CommandFormat;
import org.apache.hadoop.hdfs.client.HdfsClientConfigKeys;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSystem;
import org.apache.hadoop.mapred.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.mapred.RawLocalFileSystem.<init>;


public class TestFileSystem {
    private static final Logger LOG = LoggerFactory.getLogger(TestFileSystem.class);

    private static Configuration conf = new Configuration();

    private static int BUFFER_SIZE = TestFileSystem.conf.getInt("io.file.buffer.size", 4096);

    private static final long MEGA = 1024 * 1024;

    private static final int SEEKS_PER_FILE = 4;

    private static String ROOT = System.getProperty("test.build.data", "fs_test");

    private static Path CONTROL_DIR = new Path(TestFileSystem.ROOT, "fs_control");

    private static Path WRITE_DIR = new Path(TestFileSystem.ROOT, "fs_write");

    private static Path READ_DIR = new Path(TestFileSystem.ROOT, "fs_read");

    private static Path DATA_DIR = new Path(TestFileSystem.ROOT, "fs_data");

    @Test
    public void testFs() throws Exception {
        TestFileSystem.testFs((10 * (TestFileSystem.MEGA)), 100, 0);
    }

    @Test
    public void testCommandFormat() throws Exception {
        // This should go to TestFsShell.java when it is added.
        CommandFormat cf;
        cf = new CommandFormat("copyToLocal", 2, 2, "crc", "ignoreCrc");
        Assert.assertEquals(cf.parse(new String[]{ "-get", "file", "-" }, 1).get(1), "-");
        try {
            cf.parse(new String[]{ "-get", "file", "-ignoreCrc", "/foo" }, 1);
            Assert.fail("Expected parsing to fail as it should stop at first non-option");
        } catch (Exception e) {
            // Expected
        }
        cf = new CommandFormat("tail", 1, 1, "f");
        Assert.assertEquals(cf.parse(new String[]{ "-tail", "fileName" }, 1).get(0), "fileName");
        Assert.assertEquals(cf.parse(new String[]{ "-tail", "-f", "fileName" }, 1).get(0), "fileName");
        cf = new CommandFormat("setrep", 2, 2, "R", "w");
        Assert.assertEquals(cf.parse(new String[]{ "-setrep", "-R", "2", "/foo/bar" }, 1).get(1), "/foo/bar");
        cf = new CommandFormat("put", 2, 10000);
        Assert.assertEquals(cf.parse(new String[]{ "-put", "-", "dest" }, 1).get(1), "dest");
    }

    public static class WriteMapper extends Configured implements Mapper<Text, LongWritable, Text, LongWritable> {
        private Random random = new Random();

        private byte[] buffer = new byte[TestFileSystem.BUFFER_SIZE];

        private FileSystem fs;

        private boolean fastCheck;

        // a random suffix per task
        private String suffix = "-" + (random.nextLong());

        {
            try {
                fs = FileSystem.get(TestFileSystem.conf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public WriteMapper() {
            super(null);
        }

        public WriteMapper(Configuration conf) {
            super(conf);
        }

        public void configure(JobConf job) {
            setConf(job);
            fastCheck = job.getBoolean("fs.test.fastCheck", false);
        }

        public void map(Text key, LongWritable value, OutputCollector<Text, LongWritable> collector, Reporter reporter) throws IOException {
            String name = key.toString();
            long size = value.get();
            long seed = Long.parseLong(name);
            random.setSeed(seed);
            reporter.setStatus(("creating " + name));
            // write to temp file initially to permit parallel execution
            Path tempFile = new Path(TestFileSystem.DATA_DIR, (name + (suffix)));
            OutputStream out = fs.create(tempFile);
            long written = 0;
            try {
                while (written < size) {
                    if (fastCheck) {
                        Arrays.fill(buffer, ((byte) (random.nextInt(Byte.MAX_VALUE))));
                    } else {
                        random.nextBytes(buffer);
                    }
                    long remains = size - written;
                    int length = (remains <= (buffer.length)) ? ((int) (remains)) : buffer.length;
                    out.write(buffer, 0, length);
                    written += length;
                    reporter.setStatus(((((("writing " + name) + "@") + written) + "/") + size));
                } 
            } finally {
                out.close();
            }
            // rename to final location
            fs.rename(tempFile, new Path(TestFileSystem.DATA_DIR, name));
            collector.collect(new Text("bytes"), new LongWritable(written));
            reporter.setStatus(("wrote " + name));
        }

        public void close() {
        }
    }

    public static class ReadMapper extends Configured implements Mapper<Text, LongWritable, Text, LongWritable> {
        private Random random = new Random();

        private byte[] buffer = new byte[TestFileSystem.BUFFER_SIZE];

        private byte[] check = new byte[TestFileSystem.BUFFER_SIZE];

        private FileSystem fs;

        private boolean fastCheck;

        {
            try {
                fs = FileSystem.get(TestFileSystem.conf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public ReadMapper() {
            super(null);
        }

        public ReadMapper(Configuration conf) {
            super(conf);
        }

        public void configure(JobConf job) {
            setConf(job);
            fastCheck = job.getBoolean("fs.test.fastCheck", false);
        }

        public void map(Text key, LongWritable value, OutputCollector<Text, LongWritable> collector, Reporter reporter) throws IOException {
            String name = key.toString();
            long size = value.get();
            long seed = Long.parseLong(name);
            random.setSeed(seed);
            reporter.setStatus(("opening " + name));
            DataInputStream in = new DataInputStream(fs.open(new Path(TestFileSystem.DATA_DIR, name)));
            long read = 0;
            try {
                while (read < size) {
                    long remains = size - read;
                    int n = (remains <= (buffer.length)) ? ((int) (remains)) : buffer.length;
                    in.readFully(buffer, 0, n);
                    read += n;
                    if (fastCheck) {
                        Arrays.fill(check, ((byte) (random.nextInt(Byte.MAX_VALUE))));
                    } else {
                        random.nextBytes(check);
                    }
                    if (n != (buffer.length)) {
                        Arrays.fill(buffer, n, buffer.length, ((byte) (0)));
                        Arrays.fill(check, n, check.length, ((byte) (0)));
                    }
                    Assert.assertTrue(Arrays.equals(buffer, check));
                    reporter.setStatus(((((("reading " + name) + "@") + read) + "/") + size));
                } 
            } finally {
                in.close();
            }
            collector.collect(new Text("bytes"), new LongWritable(read));
            reporter.setStatus(("read " + name));
        }

        public void close() {
        }
    }

    public static class SeekMapper<K> extends Configured implements Mapper<Text, LongWritable, K, LongWritable> {
        private Random random = new Random();

        private byte[] check = new byte[TestFileSystem.BUFFER_SIZE];

        private FileSystem fs;

        private boolean fastCheck;

        {
            try {
                fs = FileSystem.get(TestFileSystem.conf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public SeekMapper() {
            super(null);
        }

        public SeekMapper(Configuration conf) {
            super(conf);
        }

        public void configure(JobConf job) {
            setConf(job);
            fastCheck = job.getBoolean("fs.test.fastCheck", false);
        }

        public void map(Text key, LongWritable value, OutputCollector<K, LongWritable> collector, Reporter reporter) throws IOException {
            String name = key.toString();
            long size = value.get();
            long seed = Long.parseLong(name);
            if (size == 0)
                return;

            reporter.setStatus(("opening " + name));
            FSDataInputStream in = fs.open(new Path(TestFileSystem.DATA_DIR, name));
            try {
                for (int i = 0; i < (TestFileSystem.SEEKS_PER_FILE); i++) {
                    // generate a random position
                    long position = (Math.abs(random.nextLong())) % size;
                    // seek file to that position
                    reporter.setStatus(("seeking " + name));
                    in.seek(position);
                    byte b = in.readByte();
                    // check that byte matches
                    byte checkByte = 0;
                    // advance random state to that position
                    random.setSeed(seed);
                    for (int p = 0; p <= position; p += check.length) {
                        reporter.setStatus(("generating data for " + name));
                        if (fastCheck) {
                            checkByte = ((byte) (random.nextInt(Byte.MAX_VALUE)));
                        } else {
                            random.nextBytes(check);
                            checkByte = check[((int) (position % (check.length)))];
                        }
                    }
                    Assert.assertEquals(b, checkByte);
                }
            } finally {
                in.close();
            }
        }

        public void close() {
        }
    }

    @Test
    public void testFsCache() throws Exception {
        {
            long now = System.currentTimeMillis();
            String[] users = new String[]{ "foo", "bar" };
            final Configuration conf = new Configuration();
            FileSystem[] fs = new FileSystem[users.length];
            for (int i = 0; i < (users.length); i++) {
                UserGroupInformation ugi = UserGroupInformation.createRemoteUser(users[i]);
                fs[i] = ugi.doAs(new PrivilegedExceptionAction<FileSystem>() {
                    public FileSystem run() throws IOException {
                        return FileSystem.get(conf);
                    }
                });
                for (int j = 0; j < i; j++) {
                    Assert.assertFalse(((fs[j]) == (fs[i])));
                }
            }
            FileSystem.closeAll();
        }
        {
            try {
                TestFileSystem.runTestCache(DFS_NAMENODE_RPC_PORT_DEFAULT);
            } catch (BindException be) {
                TestFileSystem.LOG.warn((("Cannot test HdfsClientConfigKeys.DFS_NAMENODE_RPC_PORT_DEFAULT (=" + (HdfsClientConfigKeys.DFS_NAMENODE_RPC_PORT_DEFAULT)) + ")"), be);
            }
            TestFileSystem.runTestCache(0);
        }
    }

    @Test
    public void testFsClose() throws Exception {
        {
            Configuration conf = new Configuration();
            new Path("file:///").getFileSystem(conf);
            FileSystem.closeAll();
        }
    }

    @Test
    public void testFsShutdownHook() throws Exception {
        final Set<FileSystem> closed = Collections.synchronizedSet(new HashSet<FileSystem>());
        Configuration conf = new Configuration();
        Configuration confNoAuto = new Configuration();
        conf.setClass("fs.test.impl", TestFileSystem.TestShutdownFileSystem.class, FileSystem.class);
        confNoAuto.setClass("fs.test.impl", TestFileSystem.TestShutdownFileSystem.class, FileSystem.class);
        confNoAuto.setBoolean("fs.automatic.close", false);
        TestFileSystem.TestShutdownFileSystem fsWithAuto = ((TestFileSystem.TestShutdownFileSystem) (new Path("test://a/").getFileSystem(conf)));
        TestFileSystem.TestShutdownFileSystem fsWithoutAuto = ((TestFileSystem.TestShutdownFileSystem) (new Path("test://b/").getFileSystem(confNoAuto)));
        fsWithAuto.setClosedSet(closed);
        fsWithoutAuto.setClosedSet(closed);
        // Different URIs should result in different FS instances
        Assert.assertNotSame(fsWithAuto, fsWithoutAuto);
        CACHE.closeAll(true);
        Assert.assertEquals(1, closed.size());
        Assert.assertTrue(closed.contains(fsWithAuto));
        closed.clear();
        FileSystem.closeAll();
        Assert.assertEquals(1, closed.size());
        Assert.assertTrue(closed.contains(fsWithoutAuto));
    }

    @Test
    public void testCacheKeysAreCaseInsensitive() throws Exception {
        Configuration conf = new Configuration();
        // check basic equality
        FileSystem.Cache.Key lowercaseCachekey1 = new FileSystem.Cache.Key(new URI("hdfs://localhost:12345/"), conf);
        FileSystem.Cache.Key lowercaseCachekey2 = new FileSystem.Cache.Key(new URI("hdfs://localhost:12345/"), conf);
        Assert.assertEquals(lowercaseCachekey1, lowercaseCachekey2);
        // check insensitive equality
        FileSystem.Cache.Key uppercaseCachekey = new FileSystem.Cache.Key(new URI("HDFS://Localhost:12345/"), conf);
        Assert.assertEquals(lowercaseCachekey2, uppercaseCachekey);
        // check behaviour with collections
        List<FileSystem.Cache.Key> list = new ArrayList<FileSystem.Cache.Key>();
        list.add(uppercaseCachekey);
        Assert.assertTrue(list.contains(uppercaseCachekey));
        Assert.assertTrue(list.contains(lowercaseCachekey2));
        Set<FileSystem.Cache.Key> set = new HashSet<FileSystem.Cache.Key>();
        set.add(uppercaseCachekey);
        Assert.assertTrue(set.contains(uppercaseCachekey));
        Assert.assertTrue(set.contains(lowercaseCachekey2));
        Map<FileSystem.Cache.Key, String> map = new HashMap<FileSystem.Cache.Key, String>();
        map.put(uppercaseCachekey, "");
        Assert.assertTrue(map.containsKey(uppercaseCachekey));
        Assert.assertTrue(map.containsKey(lowercaseCachekey2));
    }

    public static class TestShutdownFileSystem extends RawLocalFileSystem {
        private Set<FileSystem> closedSet;

        public void setClosedSet(Set<FileSystem> closedSet) {
            this.closedSet = closedSet;
        }

        public void close() throws IOException {
            if ((closedSet) != null) {
                closedSet.add(this);
            }
            super.close();
        }
    }
}

