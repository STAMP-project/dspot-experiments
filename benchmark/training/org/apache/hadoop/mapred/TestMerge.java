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
package org.apache.hadoop.mapred;


import IFile.EOF_MARKER;
import MapOutputCollector.Context;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.io.serializer.SerializationFactory;
import org.apache.hadoop.io.serializer.Serializer;
import org.apache.hadoop.mapred.Task.TaskReporter;
import org.junit.Test;


/**
 * This test tests the support for a merge operation in Hadoop.  The input files
 * are already sorted on the key.  This test implements an external
 * MapOutputCollector implementation that just copies the records to different
 * partitions while maintaining the sort order in each partition.  The Hadoop
 * framework's merge on the reduce side will merge the partitions created to
 * generate the final output which is sorted on the key.
 */
@SuppressWarnings({ "unchecked", "deprecation" })
public class TestMerge {
    private static final int NUM_HADOOP_DATA_NODES = 2;

    // Number of input files is same as the number of mappers.
    private static final int NUM_MAPPERS = 10;

    // Number of reducers.
    private static final int NUM_REDUCERS = 4;

    // Number of lines per input file.
    private static final int NUM_LINES = 1000;

    // Where MR job's input will reside.
    private static final Path INPUT_DIR = new Path("/testplugin/input");

    // Where output goes.
    private static final Path OUTPUT = new Path("/testplugin/output");

    @Test
    public void testMerge() throws Exception {
        MiniDFSCluster dfsCluster = null;
        MiniMRClientCluster mrCluster = null;
        FileSystem fileSystem = null;
        try {
            Configuration conf = new Configuration();
            // Start the mini-MR and mini-DFS clusters
            dfsCluster = numDataNodes(TestMerge.NUM_HADOOP_DATA_NODES).build();
            fileSystem = dfsCluster.getFileSystem();
            mrCluster = MiniMRClientClusterFactory.create(this.getClass(), TestMerge.NUM_HADOOP_DATA_NODES, conf);
            // Generate input.
            createInput(fileSystem);
            // Run the test.
            runMergeTest(new JobConf(mrCluster.getConfig()), fileSystem);
        } finally {
            if (dfsCluster != null) {
                dfsCluster.shutdown();
            }
            if (mrCluster != null) {
                mrCluster.stop();
            }
        }
    }

    /**
     * A mapper implementation that assumes that key text contains valid integers
     * in displayable form.
     */
    public static class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
        private Text keyText;

        private Text valueText;

        public MyMapper() {
            keyText = new Text();
            valueText = new Text();
        }

        @Override
        public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
            String record = value.toString();
            int blankPos = record.indexOf(" ");
            keyText.set(record.substring(0, blankPos));
            valueText.set(record.substring((blankPos + 1)));
            output.collect(keyText, valueText);
        }

        public void close() throws IOException {
        }
    }

    /**
     * Partitioner implementation to make sure that output is in total sorted
     * order.  We basically route key ranges to different reducers such that
     * key values monotonically increase with the partition number.  For example,
     * in this test, the keys are numbers from 1 to 1000 in the form "000000001"
     * to "000001000" in each input file.  The keys "000000001" to "000000250" are
     * routed to partition 0, "000000251" to "000000500" are routed to partition 1
     * and so on since we have 4 reducers.
     */
    static class MyPartitioner implements Partitioner<Text, Text> {
        public MyPartitioner() {
        }

        public void configure(JobConf job) {
        }

        public int getPartition(Text key, Text value, int numPartitions) {
            int keyValue = 0;
            try {
                keyValue = Integer.parseInt(key.toString());
            } catch (NumberFormatException nfe) {
                keyValue = 0;
            }
            int partitionNumber = (numPartitions * (Math.max(0, (keyValue - 1)))) / (TestMerge.NUM_LINES);
            return partitionNumber;
        }
    }

    /**
     * Implementation of map output copier(that avoids sorting) on the map side.
     * It maintains keys in the input order within each partition created for
     * reducers.
     */
    static class MapOutputCopier<K, V> implements MapOutputCollector<K, V> {
        private static final int BUF_SIZE = 128 * 1024;

        private MapTask mapTask;

        private JobConf jobConf;

        private TaskReporter reporter;

        private int numberOfPartitions;

        private Class<K> keyClass;

        private Class<V> valueClass;

        private TestMerge.KeyValueWriter<K, V>[] recordWriters;

        private ByteArrayOutputStream[] outStreams;

        public MapOutputCopier() {
        }

        @SuppressWarnings("unchecked")
        public void init(MapOutputCollector.Context context) throws IOException, ClassNotFoundException {
            this.mapTask = context.getMapTask();
            this.jobConf = context.getJobConf();
            this.reporter = context.getReporter();
            numberOfPartitions = jobConf.getNumReduceTasks();
            keyClass = ((Class<K>) (jobConf.getMapOutputKeyClass()));
            valueClass = ((Class<V>) (jobConf.getMapOutputValueClass()));
            recordWriters = new TestMerge.KeyValueWriter[numberOfPartitions];
            outStreams = new ByteArrayOutputStream[numberOfPartitions];
            // Create output streams for partitions.
            for (int i = 0; i < (numberOfPartitions); i++) {
                outStreams[i] = new ByteArrayOutputStream();
                recordWriters[i] = new TestMerge.KeyValueWriter<K, V>(jobConf, outStreams[i], keyClass, valueClass);
            }
        }

        public synchronized void collect(K key, V value, int partitionNumber) throws IOException, InterruptedException {
            if ((partitionNumber >= 0) && (partitionNumber < (numberOfPartitions))) {
                recordWriters[partitionNumber].write(key, value);
            } else {
                throw new IOException(("Invalid partition number: " + partitionNumber));
            }
            reporter.progress();
        }

        public void close() throws IOException, InterruptedException {
            long totalSize = 0;
            for (int i = 0; i < (numberOfPartitions); i++) {
                recordWriters[i].close();
                outStreams[i].close();
                totalSize += outStreams[i].size();
            }
            MapOutputFile mapOutputFile = mapTask.getMapOutputFile();
            Path finalOutput = mapOutputFile.getOutputFileForWrite(totalSize);
            Path indexPath = mapOutputFile.getOutputIndexFileForWrite(((numberOfPartitions) * (mapTask.MAP_OUTPUT_INDEX_RECORD_LENGTH)));
            // Copy partitions to final map output.
            copyPartitions(finalOutput, indexPath);
        }

        public void flush() throws IOException, ClassNotFoundException, InterruptedException {
        }

        private void copyPartitions(Path mapOutputPath, Path indexPath) throws IOException {
            FileSystem localFs = FileSystem.getLocal(jobConf);
            FileSystem rfs = getRaw();
            FSDataOutputStream rawOutput = rfs.create(mapOutputPath, true, TestMerge.MapOutputCopier.BUF_SIZE);
            SpillRecord spillRecord = new SpillRecord(numberOfPartitions);
            IndexRecord indexRecord = new IndexRecord();
            for (int i = 0; i < (numberOfPartitions); i++) {
                indexRecord.startOffset = rawOutput.getPos();
                byte[] buffer = outStreams[i].toByteArray();
                IFileOutputStream checksumOutput = new IFileOutputStream(rawOutput);
                checksumOutput.write(buffer);
                // Write checksum.
                checksumOutput.finish();
                // Write index record
                indexRecord.rawLength = ((long) (buffer.length));
                indexRecord.partLength = (rawOutput.getPos()) - (indexRecord.startOffset);
                spillRecord.putIndex(indexRecord, i);
                reporter.progress();
            }
            rawOutput.close();
            spillRecord.writeToFile(indexPath, jobConf);
        }
    }

    static class KeyValueWriter<K, V> {
        private Class<K> keyClass;

        private Class<V> valueClass;

        private DataOutputBuffer dataBuffer;

        private Serializer<K> keySerializer;

        private Serializer<V> valueSerializer;

        private DataOutputStream outputStream;

        public KeyValueWriter(Configuration conf, OutputStream output, Class<K> kyClass, Class<V> valClass) throws IOException {
            keyClass = kyClass;
            valueClass = valClass;
            dataBuffer = new DataOutputBuffer();
            SerializationFactory serializationFactory = new SerializationFactory(conf);
            keySerializer = ((Serializer<K>) (serializationFactory.getSerializer(keyClass)));
            keySerializer.open(dataBuffer);
            valueSerializer = ((Serializer<V>) (serializationFactory.getSerializer(valueClass)));
            valueSerializer.open(dataBuffer);
            outputStream = new DataOutputStream(output);
        }

        public void write(K key, V value) throws IOException {
            if ((key.getClass()) != (keyClass)) {
                throw new IOException(((("wrong key class: " + (key.getClass())) + " is not ") + (keyClass)));
            }
            if ((value.getClass()) != (valueClass)) {
                throw new IOException(((("wrong value class: " + (value.getClass())) + " is not ") + (valueClass)));
            }
            // Append the 'key'
            keySerializer.serialize(key);
            int keyLength = dataBuffer.getLength();
            if (keyLength < 0) {
                throw new IOException(((("Negative key-length not allowed: " + keyLength) + " for ") + key));
            }
            // Append the 'value'
            valueSerializer.serialize(value);
            int valueLength = (dataBuffer.getLength()) - keyLength;
            if (valueLength < 0) {
                throw new IOException(((("Negative value-length not allowed: " + valueLength) + " for ") + value));
            }
            // Write the record out
            WritableUtils.writeVInt(outputStream, keyLength);
            WritableUtils.writeVInt(outputStream, valueLength);
            outputStream.write(dataBuffer.getData(), 0, dataBuffer.getLength());
            // Reset
            dataBuffer.reset();
        }

        public void close() throws IOException {
            keySerializer.close();
            valueSerializer.close();
            WritableUtils.writeVInt(outputStream, EOF_MARKER);
            WritableUtils.writeVInt(outputStream, EOF_MARKER);
            outputStream.close();
        }
    }
}

