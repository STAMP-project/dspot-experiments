package com.mongodb.hadoop;


import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.mapred.BSONFileInputFormat;
import com.mongodb.hadoop.testutils.BaseHadoopTest;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.junit.Assert;
import org.junit.Test;


public class BSONFileInputFormatTest {
    @Test
    public void enronEmails() throws IOException {
        BSONFileInputFormat inputFormat = new BSONFileInputFormat();
        JobConf job = new JobConf();
        String inputDirectory = new File(BaseHadoopTest.EXAMPLE_DATA_HOME, "/dump/enron_mail/messages.bson").getAbsoluteFile().toURI().toString();
        // Hadoop 2.X
        job.set("mapreduce.input.fileinputformat.inputdir", inputDirectory);
        // Hadoop 1.2.X
        job.set("mapred.input.dir", inputDirectory);
        FileSplit[] splits = inputFormat.getSplits(job, 5);
        int count = 0;
        BSONWritable writable = new BSONWritable();
        for (FileSplit split : splits) {
            RecordReader<NullWritable, BSONWritable> recordReader = inputFormat.getRecordReader(split, job, null);
            while (recordReader.next(null, writable)) {
                count++;
            } 
        }
        Assert.assertEquals("There are 501513 messages in the enron corpus", 501513, count);
    }
}

