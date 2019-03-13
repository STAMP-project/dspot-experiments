/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.io.file.tfile;


import Compression.Algorithm.GZ;
import Compression.Algorithm.LZO;
import SequenceFile.CompressionType.BLOCK;
import SequenceFile.CompressionType.NONE;
import TFile.Reader;
import TFile.Reader.Scanner;
import TFile.Writer;
import java.io.IOException;
import java.text.DateFormat;
import java.util.StringTokenizer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.file.tfile.TFile.Reader.Scanner.Entry;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;


public class TestTFileSeqFileComparison {
    TestTFileSeqFileComparison.MyOptions options;

    private FileSystem fs;

    private Configuration conf;

    private long startTimeEpoch;

    private long finishTimeEpoch;

    private DateFormat formatter;

    byte[][] dictionary;

    private interface KVAppendable {
        public void append(BytesWritable key, BytesWritable value) throws IOException;

        public void close() throws IOException;
    }

    private interface KVReadable {
        public byte[] getKey();

        public byte[] getValue();

        public int getKeyLength();

        public int getValueLength();

        public boolean next() throws IOException;

        public void close() throws IOException;
    }

    static class TFileAppendable implements TestTFileSeqFileComparison.KVAppendable {
        private FSDataOutputStream fsdos;

        private Writer writer;

        public TFileAppendable(FileSystem fs, Path path, String compress, int minBlkSize, int osBufferSize, Configuration conf) throws IOException {
            this.fsdos = fs.create(path, true, osBufferSize);
            this.writer = new TFile.Writer(fsdos, minBlkSize, compress, null, conf);
        }

        @Override
        public void append(BytesWritable key, BytesWritable value) throws IOException {
            writer.append(key.getBytes(), 0, key.getLength(), value.getBytes(), 0, value.getLength());
        }

        @Override
        public void close() throws IOException {
            writer.close();
            fsdos.close();
        }
    }

    static class TFileReadable implements TestTFileSeqFileComparison.KVReadable {
        private FSDataInputStream fsdis;

        private Reader reader;

        private Scanner scanner;

        private byte[] keyBuffer;

        private int keyLength;

        private byte[] valueBuffer;

        private int valueLength;

        public TFileReadable(FileSystem fs, Path path, int osBufferSize, Configuration conf) throws IOException {
            this.fsdis = fs.open(path, osBufferSize);
            this.reader = new TFile.Reader(fsdis, fs.getFileStatus(path).getLen(), conf);
            this.scanner = reader.createScanner();
            keyBuffer = new byte[32];
            valueBuffer = new byte[32];
        }

        private void checkKeyBuffer(int size) {
            if (size <= (keyBuffer.length)) {
                return;
            }
            keyBuffer = new byte[Math.max((2 * (keyBuffer.length)), ((2 * size) - (keyBuffer.length)))];
        }

        private void checkValueBuffer(int size) {
            if (size <= (valueBuffer.length)) {
                return;
            }
            valueBuffer = new byte[Math.max((2 * (valueBuffer.length)), ((2 * size) - (valueBuffer.length)))];
        }

        @Override
        public byte[] getKey() {
            return keyBuffer;
        }

        @Override
        public int getKeyLength() {
            return keyLength;
        }

        @Override
        public byte[] getValue() {
            return valueBuffer;
        }

        @Override
        public int getValueLength() {
            return valueLength;
        }

        @Override
        public boolean next() throws IOException {
            if (scanner.atEnd())
                return false;

            Entry entry = scanner.entry();
            keyLength = entry.getKeyLength();
            checkKeyBuffer(keyLength);
            entry.getKey(keyBuffer);
            valueLength = entry.getValueLength();
            checkValueBuffer(valueLength);
            entry.getValue(valueBuffer);
            scanner.advance();
            return true;
        }

        @Override
        public void close() throws IOException {
            scanner.close();
            reader.close();
            fsdis.close();
        }
    }

    static class SeqFileAppendable implements TestTFileSeqFileComparison.KVAppendable {
        private FSDataOutputStream fsdos;

        private SequenceFile.Writer writer;

        public SeqFileAppendable(FileSystem fs, Path path, int osBufferSize, String compress, int minBlkSize) throws IOException {
            Configuration conf = new Configuration();
            CompressionCodec codec = null;
            if ("lzo".equals(compress)) {
                codec = LZO.getCodec();
            } else
                if ("gz".equals(compress)) {
                    codec = GZ.getCodec();
                } else
                    if (!("none".equals(compress)))
                        throw new IOException("Codec not supported.");



            this.fsdos = fs.create(path, true, osBufferSize);
            if (!("none".equals(compress))) {
                writer = SequenceFile.createWriter(conf, fsdos, BytesWritable.class, BytesWritable.class, BLOCK, codec);
            } else {
                writer = SequenceFile.createWriter(conf, fsdos, BytesWritable.class, BytesWritable.class, NONE, null);
            }
        }

        @Override
        public void append(BytesWritable key, BytesWritable value) throws IOException {
            writer.append(key, value);
        }

        @Override
        public void close() throws IOException {
            writer.close();
            fsdos.close();
        }
    }

    static class SeqFileReadable implements TestTFileSeqFileComparison.KVReadable {
        private SequenceFile.Reader reader;

        private BytesWritable key;

        private BytesWritable value;

        public SeqFileReadable(FileSystem fs, Path path, int osBufferSize) throws IOException {
            Configuration conf = new Configuration();
            conf.setInt("io.file.buffer.size", osBufferSize);
            reader = new SequenceFile.Reader(fs, path, conf);
            key = new BytesWritable();
            value = new BytesWritable();
        }

        @Override
        public byte[] getKey() {
            return key.getBytes();
        }

        @Override
        public int getKeyLength() {
            return key.getLength();
        }

        @Override
        public byte[] getValue() {
            return value.getBytes();
        }

        @Override
        public int getValueLength() {
            return value.getLength();
        }

        @Override
        public boolean next() throws IOException {
            return reader.next(key, value);
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }

    @Test
    public void testRunComparisons() throws IOException {
        String[] compresses = new String[]{ "none", "lzo", "gz" };
        for (String compress : compresses) {
            if (compress.equals("none")) {
                conf.setInt("tfile.fs.input.buffer.size", options.fsInputBufferSizeNone);
                conf.setInt("tfile.fs.output.buffer.size", options.fsOutputBufferSizeNone);
            } else
                if (compress.equals("lzo")) {
                    conf.setInt("tfile.fs.input.buffer.size", options.fsInputBufferSizeLzo);
                    conf.setInt("tfile.fs.output.buffer.size", options.fsOutputBufferSizeLzo);
                } else {
                    conf.setInt("tfile.fs.input.buffer.size", options.fsInputBufferSizeGz);
                    conf.setInt("tfile.fs.output.buffer.size", options.fsOutputBufferSizeGz);
                }

            compareRun(compress);
        }
    }

    private static class MyOptions {
        String rootDir = GenericTestUtils.getTestDir().getAbsolutePath();

        String compress = "gz";

        String format = "tfile";

        int dictSize = 1000;

        int minWordLen = 5;

        int maxWordLen = 20;

        int keyLength = 50;

        int valueLength = 100;

        int minBlockSize = 256 * 1024;

        int fsOutputBufferSize = 1;

        int fsInputBufferSize = 0;

        // special variable only for unit testing.
        int fsInputBufferSizeNone = 0;

        int fsInputBufferSizeGz = 0;

        int fsInputBufferSizeLzo = 0;

        int fsOutputBufferSizeNone = 1;

        int fsOutputBufferSizeGz = 1;

        int fsOutputBufferSizeLzo = 1;

        // un-exposed parameters.
        int osInputBufferSize = 64 * 1024;

        int osOutputBufferSize = 64 * 1024;

        long fileSize = (3 * 1024) * 1024;

        long seed;

        static final int OP_CREATE = 1;

        static final int OP_READ = 2;

        int op = TestTFileSeqFileComparison.MyOptions.OP_READ;

        boolean proceed = false;

        public MyOptions(String[] args) {
            seed = System.nanoTime();
            try {
                Options opts = buildOptions();
                CommandLineParser parser = new GnuParser();
                CommandLine line = parser.parse(opts, args, true);
                processOptions(line, opts);
                validateOptions();
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                System.out.println("Try \"--help\" option for details.");
                setStopProceed();
            }
        }

        public boolean proceed() {
            return proceed;
        }

        private Options buildOptions() {
            Option compress = OptionBuilder.withLongOpt("compress").withArgName("[none|lzo|gz]").hasArg().withDescription("compression scheme").create('c');
            Option ditSize = OptionBuilder.withLongOpt("dict").withArgName("size").hasArg().withDescription("number of dictionary entries").create('d');
            Option fileSize = OptionBuilder.withLongOpt("file-size").withArgName("size-in-MB").hasArg().withDescription("target size of the file (in MB).").create('s');
            Option format = OptionBuilder.withLongOpt("format").withArgName("[tfile|seqfile]").hasArg().withDescription("choose TFile or SeqFile").create('f');
            Option fsInputBufferSz = OptionBuilder.withLongOpt("fs-input-buffer").withArgName("size").hasArg().withDescription("size of the file system input buffer (in bytes).").create('i');
            Option fsOutputBufferSize = OptionBuilder.withLongOpt("fs-output-buffer").withArgName("size").hasArg().withDescription("size of the file system output buffer (in bytes).").create('o');
            Option keyLen = OptionBuilder.withLongOpt("key-length").withArgName("length").hasArg().withDescription("base length of the key (in bytes), actual length varies in [base, 2*base)").create('k');
            Option valueLen = OptionBuilder.withLongOpt("value-length").withArgName("length").hasArg().withDescription("base length of the value (in bytes), actual length varies in [base, 2*base)").create('v');
            Option wordLen = OptionBuilder.withLongOpt("word-length").withArgName("min,max").hasArg().withDescription("range of dictionary word length (in bytes)").create('w');
            Option blockSz = OptionBuilder.withLongOpt("block").withArgName("size-in-KB").hasArg().withDescription("minimum block size (in KB)").create('b');
            Option seed = OptionBuilder.withLongOpt("seed").withArgName("long-int").hasArg().withDescription("specify the seed").create('S');
            Option operation = OptionBuilder.withLongOpt("operation").withArgName("r|w|rw").hasArg().withDescription("action: read-only, create-only, read-after-create").create('x');
            Option rootDir = OptionBuilder.withLongOpt("root-dir").withArgName("path").hasArg().withDescription("specify root directory where files will be created.").create('r');
            Option help = OptionBuilder.withLongOpt("help").hasArg(false).withDescription("show this screen").create("h");
            return new Options().addOption(compress).addOption(ditSize).addOption(fileSize).addOption(format).addOption(fsInputBufferSz).addOption(fsOutputBufferSize).addOption(keyLen).addOption(wordLen).addOption(blockSz).addOption(rootDir).addOption(valueLen).addOption(operation).addOption(help);
        }

        private void processOptions(CommandLine line, Options opts) throws ParseException {
            // --help -h and --version -V must be processed first.
            if (line.hasOption('h')) {
                HelpFormatter formatter = new HelpFormatter();
                System.out.println("TFile and SeqFile benchmark.");
                System.out.println();
                formatter.printHelp(100, "java ... TestTFileSeqFileComparison [options]", "\nSupported options:", opts, "");
                return;
            }
            if (line.hasOption('c')) {
                compress = line.getOptionValue('c');
            }
            if (line.hasOption('d')) {
                dictSize = Integer.parseInt(line.getOptionValue('d'));
            }
            if (line.hasOption('s')) {
                fileSize = ((Long.parseLong(line.getOptionValue('s'))) * 1024) * 1024;
            }
            if (line.hasOption('f')) {
                format = line.getOptionValue('f');
            }
            if (line.hasOption('i')) {
                fsInputBufferSize = Integer.parseInt(line.getOptionValue('i'));
            }
            if (line.hasOption('o')) {
                fsOutputBufferSize = Integer.parseInt(line.getOptionValue('o'));
            }
            if (line.hasOption('k')) {
                keyLength = Integer.parseInt(line.getOptionValue('k'));
            }
            if (line.hasOption('v')) {
                valueLength = Integer.parseInt(line.getOptionValue('v'));
            }
            if (line.hasOption('b')) {
                minBlockSize = (Integer.parseInt(line.getOptionValue('b'))) * 1024;
            }
            if (line.hasOption('r')) {
                rootDir = line.getOptionValue('r');
            }
            if (line.hasOption('S')) {
                seed = Long.parseLong(line.getOptionValue('S'));
            }
            if (line.hasOption('w')) {
                String min_max = line.getOptionValue('w');
                StringTokenizer st = new StringTokenizer(min_max, " \t,");
                if ((st.countTokens()) != 2) {
                    throw new ParseException(("Bad word length specification: " + min_max));
                }
                minWordLen = Integer.parseInt(st.nextToken());
                maxWordLen = Integer.parseInt(st.nextToken());
            }
            if (line.hasOption('x')) {
                String strOp = line.getOptionValue('x');
                if (strOp.equals("r")) {
                    op = TestTFileSeqFileComparison.MyOptions.OP_READ;
                } else
                    if (strOp.equals("w")) {
                        op = TestTFileSeqFileComparison.MyOptions.OP_CREATE;
                    } else
                        if (strOp.equals("rw")) {
                            op = (TestTFileSeqFileComparison.MyOptions.OP_CREATE) | (TestTFileSeqFileComparison.MyOptions.OP_READ);
                        } else {
                            throw new ParseException(("Unknown action specifier: " + strOp));
                        }


            }
            proceed = true;
        }

        private void validateOptions() throws ParseException {
            if (((!(compress.equals("none"))) && (!(compress.equals("lzo")))) && (!(compress.equals("gz")))) {
                throw new ParseException(("Unknown compression scheme: " + (compress)));
            }
            if ((!(format.equals("tfile"))) && (!(format.equals("seqfile")))) {
                throw new ParseException(("Unknown file format: " + (format)));
            }
            if ((minWordLen) >= (maxWordLen)) {
                throw new ParseException("Max word length must be greater than min word length.");
            }
            return;
        }

        private void setStopProceed() {
            proceed = false;
        }

        public boolean doCreate() {
            return ((op) & (TestTFileSeqFileComparison.MyOptions.OP_CREATE)) != 0;
        }

        public boolean doRead() {
            return ((op) & (TestTFileSeqFileComparison.MyOptions.OP_READ)) != 0;
        }
    }
}

