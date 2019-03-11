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


import java.io.IOException;
import java.util.Random;
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
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;


/**
 * test the performance for seek.
 */
public class TestTFileSeek {
    private TestTFileSeek.MyOptions options;

    private Configuration conf;

    private Path path;

    private FileSystem fs;

    private NanoTimer timer;

    private Random rng;

    private RandomDistribution.DiscreteRNG keyLenGen;

    private KVGenerator kvGen;

    @Test
    public void testSeeks() throws IOException {
        String[] supported = TFile.getSupportedCompressionAlgorithms();
        boolean proceed = false;
        for (String c : supported) {
            if (c.equals(options.compress)) {
                proceed = true;
                break;
            }
        }
        if (!proceed) {
            System.out.println(("Skipped for " + (options.compress)));
            return;
        }
        if (options.doCreate()) {
            createTFile();
        }
        if (options.doRead()) {
            seekTFile();
        }
    }

    private static class IntegerRange {
        private final int from;

        private final int to;

        public IntegerRange(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public static TestTFileSeek.IntegerRange parse(String s) throws ParseException {
            StringTokenizer st = new StringTokenizer(s, " \t,");
            if ((st.countTokens()) != 2) {
                throw new ParseException(("Bad integer specification: " + s));
            }
            int from = Integer.parseInt(st.nextToken());
            int to = Integer.parseInt(st.nextToken());
            return new TestTFileSeek.IntegerRange(from, to);
        }

        public int from() {
            return from;
        }

        public int to() {
            return to;
        }
    }

    private static class MyOptions {
        // hard coded constants
        int dictSize = 1000;

        int minWordLen = 5;

        int maxWordLen = 20;

        int osInputBufferSize = 64 * 1024;

        int osOutputBufferSize = 64 * 1024;

        int fsInputBufferSizeNone = 0;

        int fsInputBufferSizeLzo = 0;

        int fsInputBufferSizeGz = 0;

        int fsOutputBufferSizeNone = 1;

        int fsOutputBufferSizeLzo = 1;

        int fsOutputBufferSizeGz = 1;

        String rootDir = GenericTestUtils.getTestDir().getAbsolutePath();

        String file = "TestTFileSeek";

        String compress = "gz";

        int minKeyLen = 10;

        int maxKeyLen = 50;

        int minValLength = 100;

        int maxValLength = 200;

        int minBlockSize = 64 * 1024;

        int fsOutputBufferSize = 1;

        int fsInputBufferSize = 0;

        long fileSize = (3 * 1024) * 1024;

        long seekCount = 1000;

        long seed;

        static final int OP_CREATE = 1;

        static final int OP_READ = 2;

        int op = (TestTFileSeek.MyOptions.OP_CREATE) | (TestTFileSeek.MyOptions.OP_READ);

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
            Option fileSize = OptionBuilder.withLongOpt("file-size").withArgName("size-in-MB").hasArg().withDescription("target size of the file (in MB).").create('s');
            Option fsInputBufferSz = OptionBuilder.withLongOpt("fs-input-buffer").withArgName("size").hasArg().withDescription("size of the file system input buffer (in bytes).").create('i');
            Option fsOutputBufferSize = OptionBuilder.withLongOpt("fs-output-buffer").withArgName("size").hasArg().withDescription("size of the file system output buffer (in bytes).").create('o');
            Option keyLen = OptionBuilder.withLongOpt("key-length").withArgName("min,max").hasArg().withDescription("the length range of the key (in bytes)").create('k');
            Option valueLen = OptionBuilder.withLongOpt("value-length").withArgName("min,max").hasArg().withDescription("the length range of the value (in bytes)").create('v');
            Option blockSz = OptionBuilder.withLongOpt("block").withArgName("size-in-KB").hasArg().withDescription("minimum block size (in KB)").create('b');
            Option seed = OptionBuilder.withLongOpt("seed").withArgName("long-int").hasArg().withDescription("specify the seed").create('S');
            Option operation = OptionBuilder.withLongOpt("operation").withArgName("r|w|rw").hasArg().withDescription("action: seek-only, create-only, seek-after-create").create('x');
            Option rootDir = OptionBuilder.withLongOpt("root-dir").withArgName("path").hasArg().withDescription("specify root directory where files will be created.").create('r');
            Option file = OptionBuilder.withLongOpt("file").withArgName("name").hasArg().withDescription("specify the file name to be created or read.").create('f');
            Option seekCount = OptionBuilder.withLongOpt("seek").withArgName("count").hasArg().withDescription("specify how many seek operations we perform (requires -x r or -x rw.").create('n');
            Option help = OptionBuilder.withLongOpt("help").hasArg(false).withDescription("show this screen").create("h");
            return new Options().addOption(compress).addOption(fileSize).addOption(fsInputBufferSz).addOption(fsOutputBufferSize).addOption(keyLen).addOption(blockSz).addOption(rootDir).addOption(valueLen).addOption(operation).addOption(seekCount).addOption(file).addOption(help);
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
            if (line.hasOption('i')) {
                fsInputBufferSize = Integer.parseInt(line.getOptionValue('i'));
            }
            if (line.hasOption('o')) {
                fsOutputBufferSize = Integer.parseInt(line.getOptionValue('o'));
            }
            if (line.hasOption('n')) {
                seekCount = Integer.parseInt(line.getOptionValue('n'));
            }
            if (line.hasOption('k')) {
                TestTFileSeek.IntegerRange ir = TestTFileSeek.IntegerRange.parse(line.getOptionValue('k'));
                minKeyLen = ir.from();
                maxKeyLen = ir.to();
            }
            if (line.hasOption('v')) {
                TestTFileSeek.IntegerRange ir = TestTFileSeek.IntegerRange.parse(line.getOptionValue('v'));
                minValLength = ir.from();
                maxValLength = ir.to();
            }
            if (line.hasOption('b')) {
                minBlockSize = (Integer.parseInt(line.getOptionValue('b'))) * 1024;
            }
            if (line.hasOption('r')) {
                rootDir = line.getOptionValue('r');
            }
            if (line.hasOption('f')) {
                file = line.getOptionValue('f');
            }
            if (line.hasOption('S')) {
                seed = Long.parseLong(line.getOptionValue('S'));
            }
            if (line.hasOption('x')) {
                String strOp = line.getOptionValue('x');
                if (strOp.equals("r")) {
                    op = TestTFileSeek.MyOptions.OP_READ;
                } else
                    if (strOp.equals("w")) {
                        op = TestTFileSeek.MyOptions.OP_CREATE;
                    } else
                        if (strOp.equals("rw")) {
                            op = (TestTFileSeek.MyOptions.OP_CREATE) | (TestTFileSeek.MyOptions.OP_READ);
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
            if ((minKeyLen) >= (maxKeyLen)) {
                throw new ParseException("Max key length must be greater than min key length.");
            }
            if ((minValLength) >= (maxValLength)) {
                throw new ParseException("Max value length must be greater than min value length.");
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
            return ((op) & (TestTFileSeek.MyOptions.OP_CREATE)) != 0;
        }

        public boolean doRead() {
            return ((op) & (TestTFileSeek.MyOptions.OP_READ)) != 0;
        }
    }
}

