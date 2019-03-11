package com.mongodb.hadoop;


import GridFSInputFormat.GridFSBinaryRecordReader;
import GridFSInputFormat.GridFSTextRecordReader;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.hadoop.testutils.BaseHadoopTest;
import com.mongodb.hadoop.util.MongoConfigUtil;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.junit.Assert;
import org.junit.Test;


public class GridFSInputFormatTest extends BaseHadoopTest {
    private static MongoClient client = new MongoClient();

    private static GridFSInputFormat inputFormat = new GridFSInputFormat();

    private static String[] readmeSections;

    private static GridFSBucket bucket = GridFSBuckets.create(GridFSInputFormatTest.client.getDatabase("mongo_hadoop"));

    private static StringBuilder fileContents;

    private static GridFSFile readme;

    private static GridFSFile bson;

    @Test
    public void testGetSplits() throws IOException, InterruptedException {
        Assert.assertEquals(((int) (Math.ceil(((GridFSInputFormatTest.readme.getLength()) / ((float) (GridFSInputFormatTest.readme.getChunkSize())))))), getSplits().size());
    }

    @Test
    public void testRecordReader() throws IOException, InterruptedException {
        List<InputSplit> splits = getSplits();
        Configuration conf = GridFSInputFormatTest.getConfiguration();
        // Split README by sections in Markdown.
        MongoConfigUtil.setGridFSDelimiterPattern(conf, "#+");
        TaskAttemptContext context = GridFSInputFormatTest.mockTaskAttemptContext(conf);
        List<String> sections = new ArrayList<String>();
        for (InputSplit split : splits) {
            RecordReader reader = new GridFSInputFormat.GridFSTextRecordReader();
            reader.initialize(split, context);
            while (reader.nextKeyValue()) {
                sections.add(reader.getCurrentValue().toString());
            } 
        }
        Assert.assertEquals(Arrays.asList(GridFSInputFormatTest.readmeSections), sections);
    }

    @Test
    public void testRecordReaderNoDelimiter() throws IOException, InterruptedException {
        List<InputSplit> splits = getSplits();
        Configuration conf = GridFSInputFormatTest.getConfiguration();
        // Empty delimiter == no delimiter.
        MongoConfigUtil.setGridFSDelimiterPattern(conf, "");
        TaskAttemptContext context = GridFSInputFormatTest.mockTaskAttemptContext(conf);
        StringBuilder fileText = new StringBuilder();
        for (InputSplit split : splits) {
            GridFSInputFormat.GridFSTextRecordReader reader = new GridFSInputFormat.GridFSTextRecordReader();
            reader.initialize(split, context);
            while (reader.nextKeyValue()) {
                fileText.append(reader.getCurrentValue().toString());
            } 
        }
        Assert.assertEquals(GridFSInputFormatTest.fileContents.toString(), fileText.toString());
    }

    @Test
    public void testReadWholeFile() throws IOException, InterruptedException {
        Configuration conf = GridFSInputFormatTest.getConfiguration();
        MongoConfigUtil.setGridFSWholeFileSplit(conf, true);
        JobContext jobContext = GridFSInputFormatTest.mockJobContext(conf);
        List<InputSplit> splits = GridFSInputFormatTest.inputFormat.getSplits(jobContext);
        // Empty delimiter == no delimiter.
        MongoConfigUtil.setGridFSDelimiterPattern(conf, "#+");
        TaskAttemptContext context = GridFSInputFormatTest.mockTaskAttemptContext(conf);
        Assert.assertEquals(1, splits.size());
        List<String> sections = new ArrayList<String>();
        for (InputSplit split : splits) {
            GridFSInputFormat.GridFSTextRecordReader reader = new GridFSInputFormat.GridFSTextRecordReader();
            reader.initialize(split, context);
            int i;
            for (i = 0; reader.nextKeyValue(); ++i) {
                sections.add(reader.getCurrentValue().toString());
            }
        }
        Assert.assertEquals(Arrays.asList(GridFSInputFormatTest.readmeSections), sections);
    }

    @Test
    public void testReadWholeFileNoDelimiter() throws IOException, InterruptedException {
        Configuration conf = GridFSInputFormatTest.getConfiguration();
        MongoConfigUtil.setGridFSWholeFileSplit(conf, true);
        JobContext jobContext = GridFSInputFormatTest.mockJobContext(conf);
        List<InputSplit> splits = GridFSInputFormatTest.inputFormat.getSplits(jobContext);
        // Empty delimiter == no delimiter.
        MongoConfigUtil.setGridFSDelimiterPattern(conf, "");
        TaskAttemptContext context = GridFSInputFormatTest.mockTaskAttemptContext(conf);
        Assert.assertEquals(1, splits.size());
        String fileText = null;
        for (InputSplit split : splits) {
            GridFSInputFormat.GridFSTextRecordReader reader = new GridFSInputFormat.GridFSTextRecordReader();
            reader.initialize(split, context);
            int i;
            for (i = 0; reader.nextKeyValue(); ++i) {
                fileText = reader.getCurrentValue().toString();
            }
            Assert.assertEquals(1, i);
        }
        Assert.assertEquals(GridFSInputFormatTest.fileContents.toString(), fileText);
    }

    @Test
    public void testReadBinaryFiles() throws IOException, InterruptedException, URISyntaxException {
        Configuration conf = GridFSInputFormatTest.getConfiguration();
        MongoConfigUtil.setQuery(conf, new BasicDBObject("filename", "orders.bson"));
        MongoConfigUtil.setGridFSWholeFileSplit(conf, true);
        MongoConfigUtil.setGridFSReadBinary(conf, true);
        JobContext context = GridFSInputFormatTest.mockJobContext(conf);
        TaskAttemptContext taskContext = GridFSInputFormatTest.mockTaskAttemptContext(conf);
        List<InputSplit> splits = GridFSInputFormatTest.inputFormat.getSplits(context);
        Assert.assertEquals(1, splits.size());
        int i = 0;
        byte[] buff = null;
        for (InputSplit split : splits) {
            GridFSInputFormat.GridFSBinaryRecordReader reader = new GridFSInputFormat.GridFSBinaryRecordReader();
            reader.initialize(split, taskContext);
            for (; reader.nextKeyValue(); ++i) {
                buff = new byte[reader.getCurrentValue().getLength()];
                // BytesWritable.copyBytes does not exist in Hadoop 1.2
                System.arraycopy(reader.getCurrentValue().getBytes(), 0, buff, 0, buff.length);
            }
        }
        // Only one record to read on the split.
        Assert.assertEquals(1, i);
        Assert.assertNotNull(buff);
        Assert.assertEquals(GridFSInputFormatTest.bson.getLength(), buff.length);
    }
}

