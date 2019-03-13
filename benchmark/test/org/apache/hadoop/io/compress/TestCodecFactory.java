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
package org.apache.hadoop.io.compress;


import CommonConfigurationKeys.IO_COMPRESSION_CODECS_KEY;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


public class TestCodecFactory {
    private static class BaseCodec implements CompressionCodec {
        private Configuration conf;

        public void setConf(Configuration conf) {
            this.conf = conf;
        }

        public Configuration getConf() {
            return conf;
        }

        @Override
        public CompressionOutputStream createOutputStream(OutputStream out) throws IOException {
            return null;
        }

        @Override
        public Class<? extends Compressor> getCompressorType() {
            return null;
        }

        @Override
        public Compressor createCompressor() {
            return null;
        }

        @Override
        public CompressionInputStream createInputStream(InputStream in, Decompressor decompressor) throws IOException {
            return null;
        }

        @Override
        public CompressionInputStream createInputStream(InputStream in) throws IOException {
            return null;
        }

        @Override
        public CompressionOutputStream createOutputStream(OutputStream out, Compressor compressor) throws IOException {
            return null;
        }

        @Override
        public Class<? extends Decompressor> getDecompressorType() {
            return null;
        }

        @Override
        public Decompressor createDecompressor() {
            return null;
        }

        @Override
        public String getDefaultExtension() {
            return ".base";
        }
    }

    private static class BarCodec extends TestCodecFactory.BaseCodec {
        @Override
        public String getDefaultExtension() {
            return "bar";
        }
    }

    private static class FooBarCodec extends TestCodecFactory.BaseCodec {
        @Override
        public String getDefaultExtension() {
            return ".foo.bar";
        }
    }

    private static class FooCodec extends TestCodecFactory.BaseCodec {
        @Override
        public String getDefaultExtension() {
            return ".foo";
        }
    }

    private static class NewGzipCodec extends TestCodecFactory.BaseCodec {
        @Override
        public String getDefaultExtension() {
            return ".gz";
        }
    }

    @Test
    public void testFinding() {
        CompressionCodecFactory factory = new CompressionCodecFactory(new Configuration());
        CompressionCodec codec = factory.getCodec(new Path("/tmp/foo.bar"));
        Assert.assertEquals("default factory foo codec", null, codec);
        codec = factory.getCodecByClassName(TestCodecFactory.BarCodec.class.getCanonicalName());
        Assert.assertEquals("default factory foo codec", null, codec);
        codec = factory.getCodec(new Path("/tmp/foo.gz"));
        TestCodecFactory.checkCodec("default factory for .gz", GzipCodec.class, codec);
        codec = factory.getCodecByClassName(GzipCodec.class.getCanonicalName());
        TestCodecFactory.checkCodec("default factory for gzip codec", GzipCodec.class, codec);
        codec = factory.getCodecByName("gzip");
        TestCodecFactory.checkCodec("default factory for gzip codec", GzipCodec.class, codec);
        codec = factory.getCodecByName("GZIP");
        TestCodecFactory.checkCodec("default factory for gzip codec", GzipCodec.class, codec);
        codec = factory.getCodecByName("GZIPCodec");
        TestCodecFactory.checkCodec("default factory for gzip codec", GzipCodec.class, codec);
        codec = factory.getCodecByName("gzipcodec");
        TestCodecFactory.checkCodec("default factory for gzip codec", GzipCodec.class, codec);
        Class klass = factory.getCodecClassByName("gzipcodec");
        Assert.assertEquals(GzipCodec.class, klass);
        codec = factory.getCodec(new Path("/tmp/foo.bz2"));
        TestCodecFactory.checkCodec("default factory for .bz2", BZip2Codec.class, codec);
        codec = factory.getCodecByClassName(BZip2Codec.class.getCanonicalName());
        TestCodecFactory.checkCodec("default factory for bzip2 codec", BZip2Codec.class, codec);
        codec = factory.getCodecByName("bzip2");
        TestCodecFactory.checkCodec("default factory for bzip2 codec", BZip2Codec.class, codec);
        codec = factory.getCodecByName("bzip2codec");
        TestCodecFactory.checkCodec("default factory for bzip2 codec", BZip2Codec.class, codec);
        codec = factory.getCodecByName("BZIP2");
        TestCodecFactory.checkCodec("default factory for bzip2 codec", BZip2Codec.class, codec);
        codec = factory.getCodecByName("BZIP2CODEC");
        TestCodecFactory.checkCodec("default factory for bzip2 codec", BZip2Codec.class, codec);
        codec = factory.getCodecByClassName(DeflateCodec.class.getCanonicalName());
        TestCodecFactory.checkCodec("default factory for deflate codec", DeflateCodec.class, codec);
        codec = factory.getCodecByName("deflate");
        TestCodecFactory.checkCodec("default factory for deflate codec", DeflateCodec.class, codec);
        codec = factory.getCodecByName("deflatecodec");
        TestCodecFactory.checkCodec("default factory for deflate codec", DeflateCodec.class, codec);
        codec = factory.getCodecByName("DEFLATE");
        TestCodecFactory.checkCodec("default factory for deflate codec", DeflateCodec.class, codec);
        codec = factory.getCodecByName("DEFLATECODEC");
        TestCodecFactory.checkCodec("default factory for deflate codec", DeflateCodec.class, codec);
        factory = TestCodecFactory.setClasses(new Class[0]);
        // gz, bz2, snappy, lz4 are picked up by service loader, but bar isn't
        codec = factory.getCodec(new Path("/tmp/foo.bar"));
        Assert.assertEquals("empty factory bar codec", null, codec);
        codec = factory.getCodecByClassName(TestCodecFactory.BarCodec.class.getCanonicalName());
        Assert.assertEquals("empty factory bar codec", null, codec);
        codec = factory.getCodec(new Path("/tmp/foo.gz"));
        TestCodecFactory.checkCodec("empty factory gz codec", GzipCodec.class, codec);
        codec = factory.getCodecByClassName(GzipCodec.class.getCanonicalName());
        TestCodecFactory.checkCodec("empty factory gz codec", GzipCodec.class, codec);
        codec = factory.getCodec(new Path("/tmp/foo.bz2"));
        TestCodecFactory.checkCodec("empty factory for .bz2", BZip2Codec.class, codec);
        codec = factory.getCodecByClassName(BZip2Codec.class.getCanonicalName());
        TestCodecFactory.checkCodec("empty factory for bzip2 codec", BZip2Codec.class, codec);
        codec = factory.getCodec(new Path("/tmp/foo.snappy"));
        TestCodecFactory.checkCodec("empty factory snappy codec", SnappyCodec.class, codec);
        codec = factory.getCodecByClassName(SnappyCodec.class.getCanonicalName());
        TestCodecFactory.checkCodec("empty factory snappy codec", SnappyCodec.class, codec);
        codec = factory.getCodec(new Path("/tmp/foo.lz4"));
        TestCodecFactory.checkCodec("empty factory lz4 codec", Lz4Codec.class, codec);
        codec = factory.getCodecByClassName(Lz4Codec.class.getCanonicalName());
        TestCodecFactory.checkCodec("empty factory lz4 codec", Lz4Codec.class, codec);
        factory = TestCodecFactory.setClasses(new Class[]{ TestCodecFactory.BarCodec.class, TestCodecFactory.FooCodec.class, TestCodecFactory.FooBarCodec.class });
        codec = factory.getCodec(new Path("/tmp/.foo.bar.gz"));
        TestCodecFactory.checkCodec("full factory gz codec", GzipCodec.class, codec);
        codec = factory.getCodecByClassName(GzipCodec.class.getCanonicalName());
        TestCodecFactory.checkCodec("full codec gz codec", GzipCodec.class, codec);
        codec = factory.getCodec(new Path("/tmp/foo.bz2"));
        TestCodecFactory.checkCodec("full factory for .bz2", BZip2Codec.class, codec);
        codec = factory.getCodecByClassName(BZip2Codec.class.getCanonicalName());
        TestCodecFactory.checkCodec("full codec bzip2 codec", BZip2Codec.class, codec);
        codec = factory.getCodec(new Path("/tmp/foo.bar"));
        TestCodecFactory.checkCodec("full factory bar codec", TestCodecFactory.BarCodec.class, codec);
        codec = factory.getCodecByClassName(TestCodecFactory.BarCodec.class.getCanonicalName());
        TestCodecFactory.checkCodec("full factory bar codec", TestCodecFactory.BarCodec.class, codec);
        codec = factory.getCodecByName("bar");
        TestCodecFactory.checkCodec("full factory bar codec", TestCodecFactory.BarCodec.class, codec);
        codec = factory.getCodecByName("BAR");
        TestCodecFactory.checkCodec("full factory bar codec", TestCodecFactory.BarCodec.class, codec);
        codec = factory.getCodec(new Path("/tmp/foo/baz.foo.bar"));
        TestCodecFactory.checkCodec("full factory foo bar codec", TestCodecFactory.FooBarCodec.class, codec);
        codec = factory.getCodecByClassName(TestCodecFactory.FooBarCodec.class.getCanonicalName());
        TestCodecFactory.checkCodec("full factory foo bar codec", TestCodecFactory.FooBarCodec.class, codec);
        codec = factory.getCodecByName("foobar");
        TestCodecFactory.checkCodec("full factory foo bar codec", TestCodecFactory.FooBarCodec.class, codec);
        codec = factory.getCodecByName("FOOBAR");
        TestCodecFactory.checkCodec("full factory foo bar codec", TestCodecFactory.FooBarCodec.class, codec);
        codec = factory.getCodec(new Path("/tmp/foo.foo"));
        TestCodecFactory.checkCodec("full factory foo codec", TestCodecFactory.FooCodec.class, codec);
        codec = factory.getCodecByClassName(TestCodecFactory.FooCodec.class.getCanonicalName());
        TestCodecFactory.checkCodec("full factory foo codec", TestCodecFactory.FooCodec.class, codec);
        codec = factory.getCodecByName("foo");
        TestCodecFactory.checkCodec("full factory foo codec", TestCodecFactory.FooCodec.class, codec);
        codec = factory.getCodecByName("FOO");
        TestCodecFactory.checkCodec("full factory foo codec", TestCodecFactory.FooCodec.class, codec);
        factory = TestCodecFactory.setClasses(new Class[]{ TestCodecFactory.NewGzipCodec.class });
        codec = factory.getCodec(new Path("/tmp/foo.gz"));
        TestCodecFactory.checkCodec("overridden factory for .gz", TestCodecFactory.NewGzipCodec.class, codec);
        codec = factory.getCodecByClassName(TestCodecFactory.NewGzipCodec.class.getCanonicalName());
        TestCodecFactory.checkCodec("overridden factory for gzip codec", TestCodecFactory.NewGzipCodec.class, codec);
        Configuration conf = new Configuration();
        conf.set(IO_COMPRESSION_CODECS_KEY, ("   org.apache.hadoop.io.compress.GzipCodec   , " + ("    org.apache.hadoop.io.compress.DefaultCodec  , " + " org.apache.hadoop.io.compress.BZip2Codec   ")));
        try {
            CompressionCodecFactory.getCodecClasses(conf);
        } catch (IllegalArgumentException e) {
            Assert.fail("IllegalArgumentException is unexpected");
        }
    }
}

